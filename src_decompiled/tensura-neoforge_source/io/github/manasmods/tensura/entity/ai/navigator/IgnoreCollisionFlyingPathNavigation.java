package io.github.manasmods.tensura.entity.ai.navigator;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IgnoreCollisionFlyingPathNavigation extends FlyingPathNavigation {
   public IgnoreCollisionFlyingPathNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   @NotNull
   protected PathFinder createPathFinder(int i) {
      this.nodeEvaluator = new IgnoreCollisionFlyingPathNavigation.IgnoreCollisionFlyNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      this.nodeEvaluator.setCanFloat(true);
      return new PathFinder(this.nodeEvaluator, i);
   }

   protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec32) {
      return true;
   }

   public void tick() {
      this.tick++;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            Vec3 vec3 = this.path.getNextEntityPos(this.mob);
            if (this.mob.getBlockX() == Mth.floor(vec3.x) && this.mob.getBlockY() == Mth.floor(vec3.y) && this.mob.getBlockZ() == Mth.floor(vec3.z)) {
               this.path.advance();
            }
         }

         if (!this.isDone() && this.path != null) {
            Vec3 vec3 = this.path.getNextEntityPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, this.speedModifier);
         }
      }
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return true;
   }

   public static class IgnoreCollisionFlyNodeEvaluator extends FlyNodeEvaluator {
      public void prepare(PathNavigationRegion pathNavigationRegion, Mob mob) {
         super.prepare(pathNavigationRegion, mob);
         this.entityWidth = 1;
         this.entityHeight = 1;
         this.entityDepth = 1;
      }

      private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob mob) {
         return List.of(BlockPos.containing(mob.getBlockX(), mob.getBlockY(), mob.getBlockZ()));
      }

      public Node getStart() {
         int i;
         if (this.canFloat() && this.mob.isInWater()) {
            i = this.mob.getBlockY();
            MutableBlockPos mutableBlockPos = new MutableBlockPos(this.mob.getX(), i, this.mob.getZ());

            for (BlockState blockState = this.currentContext.getBlockState(mutableBlockPos);
               blockState.is(Blocks.WATER);
               blockState = this.currentContext.getBlockState(mutableBlockPos)
            ) {
               mutableBlockPos.set(this.mob.getX(), ++i, this.mob.getZ());
            }
         } else {
            i = Mth.floor(this.mob.getY() + 0.5);
         }

         BlockPos blockPos = BlockPos.containing(this.mob.getX(), i, this.mob.getZ());
         if (!this.canStartAt(blockPos)) {
            for (BlockPos blockPos2 : this.iteratePathfindingStartNodeCandidatePositions(this.mob)) {
               if (this.canStartAt(blockPos2)) {
                  return super.getStartNode(blockPos2);
               }
            }
         }

         return super.getStartNode(blockPos);
      }

      @Nullable
      protected Node findAcceptedNode(int i, int j, int k, int l, double d, Direction direction, PathType pathType) {
         Node node = null;
         PathType pathType2 = this.getCachedPathType(i, j, k);
         float f = this.mob.getPathfindingMalus(pathType2);
         if (f >= 0.0F) {
            node = this.getNodeAndUpdateCostToMax(i, j, k, pathType2, f);
         }

         return node;
      }

      private Node getNodeAndUpdateCostToMax(int i, int j, int k, PathType pathType, float f) {
         Node node = this.getNode(i, j, k);
         node.type = pathType;
         node.costMalus = Math.max(node.costMalus, f);
         return node;
      }
   }
}
