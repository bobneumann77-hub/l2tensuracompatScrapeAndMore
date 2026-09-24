package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import io.github.manasmods.tensura.entity.template.subclass.ISwimming;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class SwimToWalkTarget<E extends PathfinderMob & ISwimming> extends MoveToWalkTarget<E> {
   public SwimToWalkTarget() {
      this.runFor(entity -> entity.getRandom().nextInt(200) + 200);
   }

   protected boolean attemptNewPath(E entity, WalkTarget walkTarget, boolean reachedCurrentTarget) {
      Brain<?> brain = entity.getBrain();
      BlockPos pos = walkTarget.getTarget().currentBlockPosition();
      this.path = entity.getNavigation().createPath(pos, 0);
      this.speedModifier = walkTarget.getSpeedModifier();
      if (reachedCurrentTarget) {
         BrainUtils.clearMemory(brain, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         return false;
      }

      if (this.path != null && this.path.canReach()) {
         BrainUtils.clearMemory(brain, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      } else {
         BrainUtils.setMemory(brain, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, entity.level().getGameTime());
      }

      if (this.path != null) {
         return true;
      } else {
         Vec3 vec3 = getPosTowardsInWater(entity, pos.getBottomCenter(), 20.0, 1.0);
         if (vec3 != null) {
            this.path = entity.getNavigation().createPath(vec3.x(), vec3.y() - entity.getFluidJumpThreshold(), vec3.z(), 0);
            return this.path != null;
         } else {
            return false;
         }
      }
   }

   @Nullable
   static Vec3 getPosTowardsInWater(PathfinderMob mob, Vec3 target, double maxDistance, double stepSize) {
      Vec3 startPos = mob.position();
      Vec3 directionXZ = new Vec3(target.x - startPos.x, 0.0, target.z - startPos.z).normalize();
      LevelAccessor level = mob.level();
      double dist = 0.0;

      while (dist <= maxDistance) {
         double currentX = startPos.x + directionXZ.x * dist;
         double currentZ = startPos.z + directionXZ.z * dist;
         int waterY = findWaterLevelAt(level, (int)currentX, (int)currentZ, target.y);
         if (waterY != Integer.MIN_VALUE) {
            return new Vec3(currentX, waterY - mob.getFluidJumpThreshold(), currentZ);
         }

         dist += stepSize;
      }

      return null;
   }

   private static int findWaterLevelAt(LevelAccessor level, int x, int z, double startY) {
      for (int y = (int)Math.floor(startY); y >= 0; y--) {
         if (level.getBlockState(new BlockPos(x, y, z)).isPathfindable(PathComputationType.WATER)) {
            return y;
         }
      }

      return Integer.MIN_VALUE;
   }
}
