package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.registry.entity.ai.TensuraMemoryModules;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ChopTree<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory((MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
   private Function<E, Double> treeDistance = entity -> 3.5;
   private BiFunction<E, BlockPos, Boolean> isLog = (entity, pos) -> entity.level().getBlockState(pos).is(BlockTags.LOGS);
   private Function<E, Integer> choppingTick;
   protected int breakingTime;
   protected int previousBreakProgress;

   public ChopTree(Function<E, Integer> choppingTick) {
      this.runtimeProvider = choppingTick;
      this.choppingTick = choppingTick;
   }

   public ChopTree<E> setTreeDistance(double radius) {
      this.treeDistance = entity -> radius;
      return this;
   }

   public ChopTree<E> setLog(BiFunction<E, BlockPos, Boolean> function) {
      this.isLog = function;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      BlockPos treePos = (BlockPos)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
      if (treePos == null) {
         return false;
      }

      double distance = this.treeDistance.apply(entity);
      return entity.distanceToSqr(Vec3.atCenterOf(treePos)) <= distance * distance;
   }

   protected boolean shouldKeepRunning(E entity) {
      return BrainUtils.hasMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
   }

   protected void tick(E entity) {
      BlockPos treePos = (BlockPos)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
      if (treePos != null && this.isLog.apply(entity, treePos)) {
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(treePos));
         BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
         entity.swing(InteractionHand.MAIN_HAND, true);
         this.breakingTime++;
         int time = this.choppingTick.apply(entity);
         int i = (int)((float)this.breakingTime / time * 10.0F);
         if (this.breakingTime % 20 == 0) {
            entity.playSound(SoundEvents.WOOD_HIT, 1.0F, 1.0F);
         }

         if (i != this.previousBreakProgress) {
            entity.level().destroyBlockProgress(entity.getId(), treePos, i);
            this.previousBreakProgress = i;
         }

         if (this.breakingTime == time) {
            entity.playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);
            List<BlockPos> stump = new ArrayList<>(List.of());
            this.addTreeChopped(treePos, entity.level(), stump);
            this.chopTree(entity, treePos);
            List<BlockPos> chopped = (List<BlockPos>)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get());
            if (chopped != null) {
               chopped.addAll(stump);
            } else {
               chopped = stump;
            }

            if (!chopped.contains(treePos)) {
               chopped.add(treePos);
            }

            BrainUtils.setMemory(entity, (MemoryModuleType)TensuraMemoryModules.LAST_TREES_CHOPPED.get(), chopped);
            this.stop((ServerLevel)entity.level(), entity, entity.level().getGameTime());
         }
      } else {
         this.stop((ServerLevel)entity.level(), entity, entity.level().getGameTime());
      }
   }

   protected void start(E entity) {
      this.breakingTime = 0;
      this.previousBreakProgress = -1;
   }

   protected void stop(E entity) {
      BrainUtils.clearMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
   }

   private void chopTree(E entity, BlockPos tree) {
      Level level = entity.level();
      Queue<BlockPos> blocks = new ArrayDeque<>();
      Set<BlockPos> visited = new HashSet<>();
      blocks.add(tree);

      while (!blocks.isEmpty()) {
         BlockPos pos = blocks.remove();
         if (visited.add(pos) && this.isLog.apply(entity, pos)) {
            for (Direction facing : Plane.HORIZONTAL) {
               BlockPos pos2 = pos.relative(facing);
               if (!visited.contains(pos2)) {
                  blocks.add(pos2);
               }
            }

            for (int x = 0; x < 3; x++) {
               for (int z = 0; z < 3; z++) {
                  BlockPos pos2 = pos.offset(-1 + x, 1, -1 + z);
                  if (!visited.contains(pos2)) {
                     blocks.add(pos2);
                  }
               }
            }

            level.destroyBlock(pos, true);
         }
      }
   }

   private void addTreeChopped(BlockPos pos, BlockGetter level, List<BlockPos> list) {
      for (Direction facing : Plane.HORIZONTAL) {
         BlockPos posR = pos.relative(facing);
         if (!list.contains(posR)) {
            BlockState state = level.getBlockState(posR);
            if (state.is(BlockTags.LOGS)) {
               BlockState belowState = level.getBlockState(posR.below());
               if (belowState.is(BlockTags.DIRT)) {
                  list.add(posR);
                  this.addTreeChopped(posR, level, list);
               }
            }
         }
      }
   }

   public static class SetWalkTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
      private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
         .usesMemories(new MemoryModuleType[]{MemoryModuleType.WALK_TARGET})
         .usesMemory(MemoryModuleType.LOOK_TARGET)
         .hasMemory((MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());

      protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
         return MEMORY_REQUIREMENTS;
      }

      protected void start(E entity) {
         BlockPos tree = (BlockPos)BrainUtils.getMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
         if (tree != null) {
            BlockPos target = null;

            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos pos = tree.relative(direction);
               if (entity.level().isEmptyBlock(pos)) {
                  target = pos;
                  break;
               }
            }

            if (target != null) {
               BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target));
               BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(target, 1.0F, 1));
            } else {
               BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
               BrainUtils.clearMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
            }
         }
      }
   }
}
