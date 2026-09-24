package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class DepositPickedItems<E extends PlayerLikeEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemory(MemoryModuleType.WALK_TARGET)
      .usesMemory(MemoryModuleType.LOOK_TARGET);
   private Function<E, Integer> scanRadius = entity -> 2;
   private Function<E, Float> speedMod = entity -> 1.0F;
   private BiPredicate<E, BlockPos> shouldOpen = DepositPickedItems::canUseContainer;
   private Predicate<E> shouldDeposit = PlayerLikeEntity::shouldDepositPickedItems;
   @Nullable
   private BlockPos target;

   public DepositPickedItems<E> scanRadius(Function<E, Integer> f) {
      this.scanRadius = f;
      return this;
   }

   public DepositPickedItems<E> speed(Function<E, Float> f) {
      this.speedMod = f;
      return this;
   }

   public DepositPickedItems<E> shouldOpen(BiPredicate<E, BlockPos> pred) {
      this.shouldOpen = pred;
      return this;
   }

   public DepositPickedItems<E> shouldDeposit(Predicate<E> pred) {
      this.shouldDeposit = pred;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!this.shouldDeposit.test(entity)) {
         return false;
      }

      this.target = this.findContainer(entity);
      return this.target != null;
   }

   protected void start(ServerLevel level, E entity, long gameTime) {
      if (this.target != null) {
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.target));
         BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.target), this.speedMod.apply(entity), 1));
      }
   }

   protected void tick(ServerLevel level, E entity, long gameTime) {
      if (this.target != null) {
         if (this.target.closerToCenterThan(entity.position(), 1.5)) {
            depositItem(level, entity, this.target);
         }
      }
   }

   protected void stop(ServerLevel level, E entity, long gameTime) {
      BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
      this.target = null;
   }

   protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
      return this.target != null && this.shouldDeposit.test(entity);
   }

   @Nullable
   private BlockPos findContainer(E entity) {
      int radius = this.scanRadius.apply(entity);
      BlockPos origin = entity.blockPosition();
      MutableBlockPos pos = new MutableBlockPos();

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -radius; dz <= radius; dz++) {
               pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
               if (this.shouldOpen.test(entity, pos)) {
                  return pos.immutable();
               }
            }
         }
      }

      return null;
   }

   public static boolean canUseContainer(PlayerLikeEntity entity, BlockPos pos) {
      BlockState state = entity.level().getBlockState(pos);
      if (!state.is(TensuraBlockTags.OPENABLE_BY_NPC)) {
         return false;
      }

      if (entity.level().getBlockEntity(pos) instanceof Container container) {
         for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemstack = container.getItem(i);
            if (itemstack.isEmpty()) {
               return true;
            }

            if (itemstack.getCount() < itemstack.getMaxStackSize()
               && entity.getPickedItems().hasAnyMatching(stack -> ItemStack.isSameItemSameComponents(itemstack, stack))) {
               return true;
            }
         }
      }

      return false;
   }

   public static void depositItem(ServerLevel level, PlayerLikeEntity entity, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof Container container) {
         SimpleContainer pickedItems = entity.getPickedItems();

         for (int i = 0; i < pickedItems.getContainerSize(); i++) {
            ItemStack picked = pickedItems.getItem(i);
            if (!picked.isEmpty()) {
               int inInventory = countMatching(entity.inventory, picked);
               int inPicked = countMatching(pickedItems, picked);
               if (inInventory < inPicked) {
                  removeMatching(pickedItems, picked, inPicked - inInventory);
               } else {
                  int toKeep = entity.getRemainingPickedItems(picked);
                  int toDeposit = Math.min(inPicked - toKeep, container.getMaxStackSize(picked));
                  if (toDeposit > 0) {
                     ItemStack toInsert = picked.copyWithCount(toDeposit);
                     ItemStack remaining = HopperBlockEntity.addItem(pickedItems, container, toInsert, null);
                     int inserted = toDeposit - remaining.getCount();
                     if (inserted > 0) {
                        removeMatching(entity.inventory, picked, inserted);
                        removeMatching(pickedItems, picked, inserted);
                        container.setChanged();
                     }
                  }
               }
            }
         }

         level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.8F, 1.0F);
      }
   }

   private static int countMatching(Container inventory, ItemStack template) {
      int count = 0;

      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (ItemStack.isSameItemSameComponents(stack, template)) {
            count += stack.getCount();
         }
      }

      return count;
   }

   private static void removeMatching(Container inventory, ItemStack template, int amount) {
      int remaining = amount;

      for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
         ItemStack stack = inventory.getItem(i);
         if (ItemStack.isSameItemSameComponents(stack, template)) {
            int toRemove = Math.min(stack.getCount(), remaining);
            stack.shrink(toRemove);
            remaining -= toRemove;
         }
      }
   }
}
