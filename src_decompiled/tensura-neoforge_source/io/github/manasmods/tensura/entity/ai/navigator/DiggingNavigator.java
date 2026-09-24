package io.github.manasmods.tensura.entity.ai.navigator;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.template.subclass.IDigging;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DiggingNavigator<E extends Mob & IDigging<E>> extends FlyingPathNavigation {
   private final E mob;

   public DiggingNavigator(E mob, Level world) {
      super(mob, world);
      this.mob = mob;
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return !this.level.isEmptyBlock(blockPos) && this.mob.isSafeDig(this.mob, this.level, blockPos);
   }

   @NotNull
   protected PathFinder createPathFinder(int i) {
      this.nodeEvaluator = new DiggingNavigator.DiggingNodeEvaluator();
      return new PathFinder(this.nodeEvaluator, i);
   }

   protected double getGroundY(Vec3 vec3) {
      return vec3.y;
   }

   protected boolean canUpdatePath() {
      return true;
   }

   protected void followThePath() {
      Vec3 vector3d = this.getTempMobPos();
      this.maxDistanceToWaypoint = this.mob.getBbWidth();
      Vec3i vector3i = this.path.getNextNodePos();
      double d0 = Math.abs(this.mob.getX() - (vector3i.getX() + 0.5));
      double d1 = Math.abs(this.mob.getY() - vector3i.getY());
      double d2 = Math.abs(this.mob.getZ() - (vector3i.getZ() + 0.5));
      boolean flag = d0 < this.maxDistanceToWaypoint && d2 < this.maxDistanceToWaypoint && d1 <= 1.0;
      if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
         this.path.advance();
      }

      this.doStuckDetection(vector3d);
   }

   protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec31) {
      Vec3 vector3d = new Vec3(vec31.x, vec31.y + this.mob.getBbHeight() * 0.5, vec31.z);
      BlockHitResult result = this.level.clip(new ClipContext(vec3, vector3d, Block.COLLIDER, Fluid.NONE, this.mob));
      return this.mob.isSafeDig(this.mob, this.level, result.getBlockPos());
   }

   private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
      if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
         return false;
      }

      Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
      if (!currentPosition.closerThan(vector3d, 2.0)) {
         return false;
      }

      Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
      Vec3 vector3d2 = vector3d1.subtract(vector3d);
      Vec3 vector3d3 = currentPosition.subtract(vector3d);
      return vector3d2.dot(vector3d3) > 0.0;
   }

   static class DiggingNodeEvaluator<E extends Mob & IDigging<E>> extends FlyNodeEvaluator {
      public DiggingNodeEvaluator() {
         this.canFloat = true;
      }

      public PathType getPathTypeOfMob(PathfindingContext pathfindingContext, int i, int j, int k, Mob mob) {
         Set<PathType> set = this.getPathTypeWithinMobBB((E)mob, pathfindingContext, i, j, k);
         if (set.contains(PathType.FENCE)) {
            return PathType.FENCE;
         }

         if (set.contains(PathType.UNPASSABLE_RAIL)) {
            return PathType.UNPASSABLE_RAIL;
         }

         PathType pathType = PathType.BLOCKED;

         for (PathType pathType2 : set) {
            if (mob.getPathfindingMalus(pathType2) < 0.0F) {
               return pathType2;
            }

            if (mob.getPathfindingMalus(pathType2) >= mob.getPathfindingMalus(pathType)) {
               pathType = pathType2;
            }
         }

         return this.entityWidth <= 1
               && pathType != PathType.OPEN
               && mob.getPathfindingMalus(pathType) == 0.0F
               && this.getPathType(pathfindingContext, i, j, k) == PathType.OPEN
            ? PathType.OPEN
            : pathType;
      }

      public Set<PathType> getPathTypeWithinMobBB(E mob, PathfindingContext pathfindingContext, int i, int j, int k) {
         EnumSet<PathType> enumSet = EnumSet.noneOf((Class<E>)PathType.class);

         for (int l = 0; l < this.entityWidth; l++) {
            for (int m = 0; m < this.entityHeight; m++) {
               for (int n = 0; n < this.entityDepth; n++) {
                  int o = l + i;
                  int p = m + j;
                  int q = n + k;
                  PathType pathType = this.getPathType(mob, pathfindingContext, o, p, q);
                  BlockPos blockPos = this.mob.blockPosition();
                  boolean bl = this.canPassDoors();
                  if (pathType == PathType.DOOR_WOOD_CLOSED && this.canOpenDoors() && bl) {
                     pathType = PathType.WALKABLE_DOOR;
                  }

                  if (pathType == PathType.DOOR_OPEN && !bl) {
                     pathType = PathType.BLOCKED;
                  }

                  if (pathType == PathType.RAIL
                     && this.getPathType(pathfindingContext, blockPos.getX(), blockPos.getY(), blockPos.getZ()) != PathType.RAIL
                     && this.getPathType(pathfindingContext, blockPos.getX(), blockPos.getY() - 1, blockPos.getZ()) != PathType.RAIL) {
                     pathType = PathType.UNPASSABLE_RAIL;
                  }

                  enumSet.add((E)pathType);
               }
            }
         }

         return enumSet;
      }

      public PathType getPathType(PathfindingContext pathfindingContext, int i, int j, int k) {
         PathType def = pathfindingContext.getPathTypeFromState(i, j, k);
         if (def != PathType.LAVA
            && def != PathType.OPEN
            && def != PathType.WATER
            && def != PathType.WATER_BORDER
            && def != PathType.DANGER_OTHER
            && def != PathType.DAMAGE_FIRE
            && def != PathType.DANGER_POWDER_SNOW) {
            BlockState state = pathfindingContext.level().getBlockState(new BlockPos(i, j, k));
            return state.is(TensuraBlockTags.DIGGABLE_BY_MONSTER)
                  && state.getFluidState().isEmpty()
                  && state.canOcclude()
                  && j > pathfindingContext.level().getMinBuildHeight()
               ? PathType.WALKABLE
               : PathType.BLOCKED;
         } else {
            return PathType.BLOCKED;
         }
      }

      public PathType getPathType(E mob, PathfindingContext pathfindingContext, int i, int j, int k) {
         PathType def = pathfindingContext.getPathTypeFromState(i, j, k);
         if (def != PathType.LAVA
            && def != PathType.OPEN
            && def != PathType.WATER
            && def != PathType.WATER_BORDER
            && def != PathType.DANGER_OTHER
            && def != PathType.DAMAGE_FIRE
            && def != PathType.DANGER_POWDER_SNOW) {
            return mob.isSafeDig(mob, pathfindingContext.level(), new BlockPos(i, j, k)) && j > pathfindingContext.level().getMinBuildHeight()
               ? PathType.WALKABLE
               : PathType.BLOCKED;
         } else {
            return PathType.BLOCKED;
         }
      }
   }
}
