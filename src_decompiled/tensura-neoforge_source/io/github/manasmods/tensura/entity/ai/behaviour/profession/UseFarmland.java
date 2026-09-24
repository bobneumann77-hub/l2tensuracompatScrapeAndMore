package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.block.HipokuteGrass;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.menu.container.SimpleLimitedContainer;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.FluidState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class UseFarmland extends ExtendedBehaviour<PlayerLikeEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemory(MemoryModuleType.WALK_TARGET)
      .usesMemory(MemoryModuleType.LOOK_TARGET);
   private Function<PlayerLikeEntity, Integer> harvestDuration = entity -> 300;
   private Function<PlayerLikeEntity, Float> speedMod = entity -> 0.8F;
   private Function<PlayerLikeEntity, Integer> scanRadius = entity -> 2;
   private Function<PlayerLikeEntity, Long> startCooldown = entity -> 10L;
   private Function<PlayerLikeEntity, Integer> interactionRange = entity -> 2;
   private Function<PlayerLikeEntity, Long> reselectionDelay = entity -> 20L;
   private BiPredicate<PlayerLikeEntity, ItemStack> seedPlantable = (entity, stack) -> stack.is(TensuraItemTags.MOB_SEED_PLANTABLE);
   private Predicate<PlayerLikeEntity> griefingAllowed = entity -> entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_GRIEF);
   private Predicate<PlayerLikeEntity> shouldStartFarming = PlayerLikeEntity::shouldDoFarming;
   @Nullable
   private BlockPos target;
   private final List<BlockPos> candidates = new ObjectArrayList();
   private long nextOkStartTime = 0L;
   private int workedTicks = 0;

   public UseFarmland duration(Function<PlayerLikeEntity, Integer> ticks) {
      this.harvestDuration = ticks;
      return this;
   }

   public UseFarmland speed(Function<PlayerLikeEntity, Float> f) {
      this.speedMod = f;
      return this;
   }

   public UseFarmland scanRadius(Function<PlayerLikeEntity, Integer> r) {
      this.scanRadius = r;
      return this;
   }

   public UseFarmland cooldown(Function<PlayerLikeEntity, Long> t) {
      this.startCooldown = t;
      return this;
   }

   public UseFarmland interactionRange(Function<PlayerLikeEntity, Integer> r) {
      this.interactionRange = r;
      return this;
   }

   public UseFarmland reselectionDelay(Function<PlayerLikeEntity, Long> t) {
      this.reselectionDelay = t;
      return this;
   }

   public UseFarmland isSeedPlantable(BiPredicate<PlayerLikeEntity, ItemStack> p) {
      this.seedPlantable = p;
      return this;
   }

   public UseFarmland griefing(Predicate<PlayerLikeEntity> p) {
      this.griefingAllowed = p;
      return this;
   }

   public UseFarmland shouldStartFarming(Predicate<PlayerLikeEntity> p) {
      this.shouldStartFarming = p;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, PlayerLikeEntity merchant) {
      long now = level.getGameTime();
      if (now < this.nextOkStartTime) {
         return false;
      }

      if (!this.griefingAllowed.test(merchant)) {
         return false;
      }

      if (!this.shouldStartFarming.test(merchant)) {
         return false;
      }

      this.candidates.clear();
      int r = Math.max(0, this.scanRadius.apply(merchant));
      MutableBlockPos pos = merchant.blockPosition().mutable();

      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            for (int dz = -r; dz <= r; dz++) {
               pos.set(merchant.getX() + dx, merchant.getY() + dy, merchant.getZ() + dz);
               if (isValidSpot(level, pos, merchant)) {
                  this.candidates.add(pos.immutable());
               }
            }
         }
      }

      this.target = this.pick(level);
      if (this.target == null) {
         this.nextOkStartTime = now + 20L;
      }

      return this.target != null;
   }

   protected void start(ServerLevel level, PlayerLikeEntity v, long now) {
      if (this.target != null) {
         BrainUtils.setMemory(v, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target));
         BrainUtils.setMemory(v, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.target), this.speedMod.apply(v), 1));
      }
   }

   protected void stop(ServerLevel level, PlayerLikeEntity v, long now) {
      BrainUtils.clearMemory(v, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(v, MemoryModuleType.WALK_TARGET);
      this.workedTicks = 0;
      this.nextOkStartTime = now + this.startCooldown.apply(v);
      this.target = null;
      this.candidates.clear();
   }

   protected void tick(ServerLevel level, PlayerLikeEntity entity, long now) {
      if (this.target == null) {
         this.target = this.pick(level);
         if (this.target != null) {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target));
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.target), this.speedMod.apply(entity), 1));
         }

         this.workedTicks++;
      } else {
         boolean atTarget = this.target.closerToCenterThan(entity.position(), this.interactionRange.apply(entity).intValue());
         if (atTarget) {
            BlockState state = level.getBlockState(this.target);
            Block block = state.getBlock();
            boolean acted = false;
            if (now > this.nextOkStartTime) {
               if (block instanceof CropBlock crop && crop.isMaxAge(state)) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  if (destroyBlockWithTool(level, this.target, true, entity, entity.getMainHandItem(), 512)) {
                     entity.getMainHandItem().hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
                     acted = true;
                  }
               } else if (block instanceof HipokuteGrass hipokute && hipokute.getAge(state) >= 2) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  if (destroyBlockWithTool(level, this.target, true, entity, entity.getMainHandItem(), 512)) {
                     entity.getMainHandItem().hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
                     acted = true;
                  }
               }

               if (level.getBlockState(this.target).isAir() && level.getBlockState(this.target.below()).getBlock() instanceof FarmBlock) {
                  SimpleLimitedContainer inventory = entity.inventory;
                  if (inventory.hasAnyMatching(stackx -> this.seedPlantable.test(entity, stackx))) {
                     for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack stack = inventory.getItem(i);
                        if (!stack.isEmpty() && this.seedPlantable.test(entity, stack) && stack.getItem() instanceof BlockItem blockItem) {
                           BlockState plant = blockItem.getBlock().defaultBlockState();
                           level.setBlockAndUpdate(this.target, plant);
                           level.gameEvent(GameEvent.BLOCK_PLACE, this.target, Context.of(entity, plant));
                           level.playSound(
                              null, this.target.getX(), this.target.getY(), this.target.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F
                           );
                           stack.shrink(1);
                           entity.swing(InteractionHand.MAIN_HAND, true);
                           if (stack.isEmpty()) {
                              inventory.setItem(i, ItemStack.EMPTY);
                           }

                           acted = true;
                           break;
                        }
                     }
                  }
               }

               if (!acted && block instanceof CropBlock crop && !crop.isMaxAge(state)) {
                  SimpleLimitedContainer inventory = entity.inventory;
                  int size = inventory.getContainerSize();
                  ItemStack boneMeal = ItemStack.EMPTY;

                  for (int j = 0; j < size; j++) {
                     ItemStack stack = inventory.getItem(j);
                     if (stack.is(Items.BONE_MEAL)) {
                        boneMeal = stack;
                        break;
                     }
                  }

                  if (!boneMeal.isEmpty() && BoneMealItem.growCrop(boneMeal, level, this.target)) {
                     level.levelEvent(1505, this.target, 15);
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     acted = true;
                  }
               }

               if (acted) {
                  this.nextOkStartTime = now + this.startCooldown.apply(entity);
                  this.candidates.remove(this.target);
                  this.target = this.pick(level);
                  if (this.target != null) {
                     BrainUtils.setMemory(
                        entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.target), this.speedMod.apply(entity), 1)
                     );
                     BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target));
                  }

                  this.workedTicks++;
                  return;
               }
            }

            this.candidates.remove(this.target);
            this.target = this.pick(level);
            if (this.target != null) {
               this.nextOkStartTime = Math.max(this.nextOkStartTime, now + this.reselectionDelay.apply(entity));
               BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.target), this.speedMod.apply(entity), 1));
               BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target));
            }
         }

         this.workedTicks++;
      }
   }

   protected boolean canStillUse(ServerLevel level, PlayerLikeEntity v, long now) {
      return this.workedTicks < this.harvestDuration.apply(v);
   }

   public static boolean isValidSpot(ServerLevel level, BlockPos pos, PlayerLikeEntity entity) {
      BlockState state = level.getBlockState(pos);
      return isValidCrop(state, entity) || state.isAir() && level.getBlockState(pos.below()).getBlock() instanceof FarmBlock;
   }

   public static boolean isValidCrop(BlockState state) {
      return state.getBlock() instanceof CropBlock || state.getBlock() instanceof HipokuteGrass hipokute && hipokute.getAge(state) >= 2;
   }

   public static boolean isValidCrop(BlockState state, PlayerLikeEntity entity) {
      if (state.getBlock() instanceof HipokuteGrass hipokute) {
         return hipokute.getAge(state) >= 2;
      } else if (state.getBlock() instanceof CropBlock crop) {
         return crop.isMaxAge(state) ? true : entity.inventory.hasAnyMatching(stack -> stack.is(Items.BONE_MEAL));
      } else {
         return false;
      }
   }

   private BlockPos pick(ServerLevel level) {
      return this.candidates.isEmpty() ? null : this.candidates.get(level.getRandom().nextInt(this.candidates.size()));
   }

   public static boolean destroyBlockWithTool(Level level, BlockPos blockPos, boolean drop, @Nullable Entity entity, ItemStack stack, int event) {
      BlockState blockState = level.getBlockState(blockPos);
      if (blockState.isAir()) {
         return false;
      }

      FluidState fluidState = level.getFluidState(blockPos);
      if (!(blockState.getBlock() instanceof BaseFireBlock)) {
         level.levelEvent(2001, blockPos, Block.getId(blockState));
      }

      if (drop) {
         BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
         Block.dropResources(blockState, level, blockPos, blockEntity, entity, stack);
      }

      boolean setBlock = level.setBlock(blockPos, fluidState.createLegacyBlock(), 3, event);
      if (setBlock) {
         level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, Context.of(entity, blockState));
      }

      return setBlock;
   }
}
