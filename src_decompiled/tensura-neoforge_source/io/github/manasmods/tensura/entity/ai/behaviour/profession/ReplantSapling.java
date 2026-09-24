package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.registry.entity.ai.TensuraMemoryModules;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ReplantSapling<E extends PlayerLikeEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).usesMemory((MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
   private Function<E, Double> saplingDistance = entity -> 3.0;

   public ReplantSapling<E> setSaplingDistance(double radius) {
      this.saplingDistance = entity -> radius;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      List<BlockPos> trees = (List<BlockPos>)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
      if (trees != null && !trees.isEmpty()) {
         if (!entity.inventory.hasAnyMatching(stack -> stack.is(ItemTags.SAPLINGS))) {
            return false;
         }

         double distance = this.saplingDistance.apply(entity);
         return entity.distanceToSqr(Vec3.atCenterOf((Vec3i)trees.getFirst())) <= distance * distance;
      } else {
         return false;
      }
   }

   protected void start(E entity) {
      List<BlockPos> trees = (List<BlockPos>)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
      if (trees != null && !trees.isEmpty()) {
         BlockPos lastTree = trees.getFirst();
         if (entity.level().isEmptyBlock(lastTree)) {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(lastTree));
            if (entity.level().getBlockState(lastTree.below()).is(BlockTags.DIRT)) {
               ItemStack sapling = this.getSapling(entity);
               if (sapling.isEmpty()) {
                  return;
               }

               if (sapling.getItem() instanceof BlockItem saplingBlockItem) {
                  entity.level().setBlockAndUpdate(lastTree, saplingBlockItem.getBlock().defaultBlockState());
                  BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
                  entity.playSound(SoundEvents.GRASS_PLACE, 1.0F, 1.0F);
                  sapling.shrink(1);
               }
            }
         }

         trees.removeFirst();
         if (trees.isEmpty()) {
            BrainUtils.clearMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
         } else {
            BrainUtils.setMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get(), trees);
         }
      }
   }

   private ItemStack getSapling(E entity) {
      for (int j = 0; j < entity.inventory.getContainerSize(); j++) {
         ItemStack itemStack = entity.inventory.getItem(j);
         if (itemStack.is(ItemTags.SAPLINGS)) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static class SetWalkTarget<E extends PlayerLikeEntity> extends ExtendedBehaviour<E> {
      private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
         .usesMemories(new MemoryModuleType[]{MemoryModuleType.WALK_TARGET})
         .usesMemory(MemoryModuleType.LOOK_TARGET)
         .hasMemory((MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
      private Function<E, Double> saplingDistance = entity -> 3.0;

      public ReplantSapling.SetWalkTarget<E> setSaplingDistance(double radius) {
         this.saplingDistance = entity -> radius;
         return this;
      }

      protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
         return entity.inventory.hasAnyMatching(stack -> stack.is(ItemTags.SAPLINGS));
      }

      protected void start(E entity) {
         List<BlockPos> trees = (List<BlockPos>)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
         if (trees != null && !trees.isEmpty()) {
            BlockPos lastTree = trees.getFirst();
            if (entity.distanceToSqr(Vec3.atCenterOf(lastTree)) < this.saplingDistance.apply(entity)) {
               BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
            } else {
               BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(lastTree));
               BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(lastTree, 1.0F, 0));
            }
         }
      }

      protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
         return MEMORY_REQUIREMENTS;
      }
   }
}
