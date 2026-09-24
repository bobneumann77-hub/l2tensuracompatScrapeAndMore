package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.magic.misc.NonPlayerFishingHook;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class FishAtWater<E extends PlayerLikeEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemory(MemoryModuleType.WALK_TARGET)
      .usesMemory(MemoryModuleType.LOOK_TARGET);
   private Predicate<E> canFish = PlayerLikeEntity::shouldDoFishing;
   private BiPredicate<E, NonPlayerFishingHook> shouldRetrieve = (entity, hook) -> hook.isBiting() || hook.getOutOfWaterTime() >= 40;
   private BiFunction<E, ItemStack, Integer> fishingLuck = (entity, stack) -> EnchantmentHelper.getFishingLuckBonus((ServerLevel)entity.level(), stack, entity);
   private BiFunction<E, ItemStack, Integer> fishingLure = (entity, stack) -> (int)(
      EnchantmentHelper.getFishingTimeReduction((ServerLevel)entity.level(), stack, entity) * 20.0F
   );
   private Function<E, Integer> scanRadius = entity -> 5;
   private Function<E, Float> speedMod = entity -> 0.8F;
   private Function<E, Integer> closeEnough = entity -> 1;
   private Function<E, Long> recastTimeFallback = entity -> 400L;
   private Function<E, Long> cooldown = entity -> 60L;
   private BiPredicate<E, BlockPos> isValidWater = getValidWater();
   private BiPredicate<E, BlockPos> isValidBank = getValidBank();
   @Nullable
   private BlockPos waterPos = null;
   @Nullable
   private BlockPos bankPos = null;
   private final List<BlockPos> candidateWater = new ObjectArrayList();
   private long nextCooldownEnd = 0L;
   private long castedTime = 0L;
   private long nextOkStartTime = 0L;

   public FishAtWater<E> canFish(Predicate<E> predicate) {
      this.canFish = predicate;
      return this;
   }

   public FishAtWater<E> shouldRetrieve(BiPredicate<E, NonPlayerFishingHook> predicate) {
      this.shouldRetrieve = predicate;
      return this;
   }

   public FishAtWater<E> fishingLuck(BiFunction<E, ItemStack, Integer> predicate) {
      this.fishingLuck = predicate;
      return this;
   }

   public FishAtWater<E> fishingLure(BiFunction<E, ItemStack, Integer> predicate) {
      this.fishingLure = predicate;
      return this;
   }

   public FishAtWater<E> scanRadius(Function<E, Integer> radius) {
      this.scanRadius = radius;
      return this;
   }

   public FishAtWater<E> speedMod(Function<E, Float> speed) {
      this.speedMod = speed;
      return this;
   }

   public FishAtWater<E> closeEnough(Function<E, Integer> radius) {
      this.closeEnough = radius;
      return this;
   }

   public FishAtWater<E> recastTimeFallback(Function<E, Long> time) {
      this.recastTimeFallback = time;
      return this;
   }

   public FishAtWater<E> cooldown(Function<E, Long> cooldown) {
      this.cooldown = cooldown;
      return this;
   }

   public FishAtWater<E> waterPredicate(BiPredicate<E, BlockPos> predicate) {
      this.isValidWater = predicate;
      return this;
   }

   public FishAtWater<E> bankPredicate(BiPredicate<E, BlockPos> predicate) {
      this.isValidBank = predicate;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E e) {
      long now = level.getGameTime();
      if (now < this.nextOkStartTime) {
         return false;
      }

      if (!this.canFish.test(e)) {
         return false;
      }

      this.candidateWater.clear();
      int radius = Math.max(1, this.scanRadius.apply(e));
      BlockPos origin = e.blockPosition();
      MutableBlockPos cursor = new MutableBlockPos();

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dy = -2; dy <= 2; dy++) {
            for (int dz = -radius; dz <= radius; dz++) {
               cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
               if (this.isValidWater.test(e, cursor)) {
                  BlockPos bp = this.findBankAdjacent(e, cursor);
                  if (bp != null) {
                     this.candidateWater.add(cursor.immutable());
                  }
               }
            }
         }
      }

      BlockPos chosenWater = pickWater(level.getRandom(), this.candidateWater);
      if (chosenWater == null) {
         this.nextOkStartTime = now + 20L;
         return false;
      } else {
         this.waterPos = chosenWater;
         this.bankPos = this.findBankAdjacent(e, chosenWater);
         if (this.bankPos == null) {
            this.nextOkStartTime = now + 20L;
            return false;
         } else {
            return true;
         }
      }
   }

   protected boolean shouldKeepRunning(E entity) {
      return entity.isAlive() && this.canFish.test(entity);
   }

   protected void start(ServerLevel level, E e, long now) {
      if (this.bankPos != null && this.waterPos != null) {
         BrainUtils.setMemory(e, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.waterPos));
         BrainUtils.setMemory(
            e, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.bankPos), this.speedMod.apply(e), this.closeEnough.apply(e))
         );
      }

      this.castedTime = 0L;
   }

   protected void tick(ServerLevel level, E entity, long now) {
      if (this.bankPos == null || this.waterPos == null) {
         this.chooseNextSpot(level, entity);
      } else if (entity.fishing == null) {
         BrainUtils.setMemory(
            entity,
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(new BlockPosTracker(this.bankPos), this.speedMod.apply(entity), this.closeEnough.apply(entity))
         );
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.waterPos));
         if (this.bankPos.closerToCenterThan(entity.position(), Math.max(1.0, this.closeEnough.apply(entity).intValue()))) {
            if (now < this.nextCooldownEnd) {
               return;
            }

            if (this.castedTime == 0L) {
               this.bankPos = entity.blockPosition();
               this.castedTime = now;
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.8F, 1.0F);
               ItemStack stack = getFishingRod(entity);
               int luck = this.fishingLuck.apply(entity, stack);
               int lure = this.fishingLure.apply(entity, stack);
               NonPlayerFishingHook hook = new NonPlayerFishingHook(entity, level, this.waterPos, luck, lure);
               if (entity.fishing != null) {
                  entity.fishing.discard();
               }

               entity.fishing = hook;
               level.addFreshEntity(hook);
            }
         }
      } else {
         BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(entity.blockPosition()), 1.0F, 1));
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.waterPos));
         if (this.shouldRetrieve.test(entity, entity.fishing)
            || !this.startCondition.test(entity)
            || !this.canFish.test(entity)
            || this.castedTime > 0L && now - this.castedTime >= this.recastTimeFallback.apply(entity)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            ItemStack rod = getFishingRod(entity);
            int i = entity.fishing.retrieve(rod);
            rod.hurtAndBreak(i, entity, rod == entity.getMainHandItem() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            entity.fishing = null;
            this.castedTime = 0L;
            this.nextCooldownEnd = now + this.cooldown.apply(entity);
            this.chooseNextSpot(level, entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 0.8F, 1.0F);
         }
      }
   }

   protected void stop(ServerLevel level, E e, long now) {
      BrainUtils.clearMemory(e, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(e, MemoryModuleType.WALK_TARGET);
      this.candidateWater.clear();
   }

   private void chooseNextSpot(ServerLevel level, E e) {
      if (this.candidateWater.isEmpty()) {
         if (this.checkExtraStartConditions(level, e) && this.bankPos != null && this.waterPos != null) {
            BrainUtils.setMemory(e, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.waterPos));
            BrainUtils.setMemory(
               e, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.bankPos), this.speedMod.apply(e), this.closeEnough.apply(e))
            );
         }
      } else {
         this.waterPos = pickWater(level.getRandom(), this.candidateWater);
         this.bankPos = this.waterPos == null ? null : this.findBankAdjacent(e, this.waterPos);
         if (this.bankPos != null && this.waterPos != null) {
            BrainUtils.setMemory(e, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.waterPos));
            BrainUtils.setMemory(
               e, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.bankPos), this.speedMod.apply(e), this.closeEnough.apply(e))
            );
         }
      }
   }

   @Nullable
   private BlockPos findBankAdjacent(E entity, BlockPos water) {
      for (BlockPos bank : List.of(water.west(), water.east(), water.north(), water.south(), water.west(2), water.east(2), water.north(2), water.south(2))) {
         if (this.isValidBank.test(entity, bank) && entity.getNavigation().createPath(bank, 0) != null) {
            return bank.immutable();
         }
      }

      return null;
   }

   @Nullable
   private static BlockPos pickWater(RandomSource rand, List<BlockPos> candidates) {
      return candidates.isEmpty() ? null : candidates.get(rand.nextInt(candidates.size()));
   }

   private static ItemStack getFishingRod(LivingEntity e) {
      ItemStack main = e.getMainHandItem();
      if (main.is(TensuraItemTags.FISHING_RODS)) {
         return main;
      }

      ItemStack off = e.getOffhandItem();
      if (off.is(TensuraItemTags.FISHING_RODS)) {
         return off;
      }

      ItemStack head = e.getItemBySlot(EquipmentSlot.MAINHAND);
      return head.is(TensuraItemTags.FISHING_RODS) ? head : ItemStack.EMPTY;
   }

   public static <E extends LivingEntity> BiPredicate<E, BlockPos> getValidWater() {
      return (entity, waterPos) -> {
         Level level = entity.level();
         if (!level.getBlockState(waterPos).is(Blocks.WATER)) {
            return false;
         }

         if (!level.getBlockState(waterPos.above()).isAir()) {
            return false;
         }

         for (int depth = 1; depth <= 2; depth++) {
            if (!level.getBlockState(waterPos.below(depth)).is(Blocks.WATER)) {
               return false;
            }
         }

         boolean hasBank = false;

         for (int dx = -3; dx <= 3 && !hasBank; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
               if (getValidBank().test(entity, waterPos.offset(dx, 0, dz))) {
                  hasBank = true;
                  break;
               }
            }
         }

         if (!hasBank) {
            return false;
         }

         int nearbyWater = 0;

         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               if ((dx != 0 || dz != 0) && level.getBlockState(waterPos.offset(dx, 0, dz)).is(Blocks.WATER)) {
                  nearbyWater++;
               }
            }
         }

         return nearbyWater >= 8;
      };
   }

   public static <E extends LivingEntity> BiPredicate<E, BlockPos> getValidBank() {
      return (entity, bankPos) -> {
         BlockState ground = entity.level().getBlockState(bankPos);
         BlockState above = entity.level().getBlockState(bankPos.above());
         if (ground.isSolid() && above.isAir()) {
            for (int dx = -3; dx <= 3; dx++) {
               for (int dz = -3; dz <= 3; dz++) {
                  if (Math.abs(dx) + Math.abs(dz) == 1) {
                     BlockPos side = bankPos.offset(dx, 0, dz);
                     BlockState sideState = entity.level().getBlockState(side);
                     if (sideState.is(Blocks.WATER)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      };
   }
}
