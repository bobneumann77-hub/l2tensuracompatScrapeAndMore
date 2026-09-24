package io.github.manasmods.tensura.entity.ai.behaviour.path;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

public class SetRandomWalkTargetAroundCenter<E extends PathfinderMob> extends SetRandomWalkTarget<E> {
   private Function<E, Vec3> centerProvider = Entity::position;
   private Function<E, Double> maxDistance = entity -> 10.0;
   private TriFunction<E, Vec3, Double, Boolean> goToCenterWhen = (entity, center, radius) -> entity.distanceToSqr(center) > radius;
   private int attempts = 18;
   private int verticalProbe = 8;
   private boolean requirePath = true;

   public SetRandomWalkTargetAroundCenter<E> center(BlockPos pos) {
      this.centerProvider = entity -> Vec3.atCenterOf(pos);
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> center(Vec3 pos) {
      this.centerProvider = entity -> pos;
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> center(Function<E, Vec3> provider) {
      this.centerProvider = provider;
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> maxDistance(double r) {
      return this.maxDistance(entity -> r);
   }

   public SetRandomWalkTargetAroundCenter<E> maxDistance(Function<E, Double> provider) {
      this.maxDistance = provider;
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> attempts(int attempts) {
      this.attempts = Math.max(1, attempts);
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> verticalProbe(int yRadius) {
      this.verticalProbe = Math.max(0, yRadius);
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> requirePath(boolean require) {
      this.requirePath = require;
      return this;
   }

   public SetRandomWalkTargetAroundCenter<E> goToCenterWhen(TriFunction<E, Vec3, Double, Boolean> function) {
      this.goToCenterWhen = function;
      return this;
   }

   @Nullable
   protected Vec3 getTargetPos(E entity) {
      Vec3 center = this.centerProvider.apply(entity);
      double rMax = Math.max(0.0, this.maxDistance.apply(entity));
      if (rMax == 0.0) {
         return center;
      }

      if ((Boolean)this.goToCenterWhen.apply(entity, center, rMax * rMax)) {
         return center;
      }

      Level level = entity.level();
      RandomSource rand = entity.getRandom();
      BlockPos base = BlockPos.containing(center);
      int yProbe = this.verticalProbe;
      int i = 0;

      Vec3 candidateCenter;
      while (true) {
         if (i >= this.attempts) {
            return null;
         }

         double u = rand.nextDouble();
         double theta = rand.nextDouble() * (Math.PI * 2);
         double r = Math.sqrt(u) * rMax;
         double dx = r * Math.cos(theta);
         double dz = r * Math.sin(theta);
         int x = Mth.floor(center.x + dx);
         int z = Mth.floor(center.z + dz);
         int y0 = base.getY();
         BlockPos candidate = findWalkableNear(level, new BlockPos(x, y0, z), yProbe, this.avoidWaterPredicate.test(entity));
         if (candidate != null) {
            candidateCenter = Vec3.atCenterOf(candidate);
            if (this.positionPredicate.test(entity, candidateCenter)) {
               if (!this.requirePath) {
                  break;
               }

               Path path = entity.getNavigation().createPath(candidate, 0);
               if (path != null && path.canReach()) {
                  break;
               }
            }
         }

         i++;
      }

      return candidateCenter;
   }

   @Nullable
   private static BlockPos findWalkableNear(Level level, BlockPos around, int yRadius, boolean avoidWater) {
      int yMin = Math.max(level.getMinBuildHeight() + 1, around.getY() - yRadius);
      int yMax = Math.min(level.getMaxBuildHeight() - 2, around.getY() + yRadius);
      int x = around.getX();
      int z = around.getZ();
      MutableBlockPos cursor = new MutableBlockPos();
      MutableBlockPos below = new MutableBlockPos();

      for (int dy = 0; dy <= yMax - yMin; dy++) {
         int yA = around.getY() + dy;
         if (yA <= yMax) {
            cursor.set(x, yA, z);
            if (isGoodStandPos(level, cursor, below, avoidWater)) {
               return cursor.immutable();
            }
         }

         if (dy != 0) {
            int yB = around.getY() - dy;
            if (yB >= yMin) {
               cursor.set(x, yB, z);
               if (isGoodStandPos(level, cursor, below, avoidWater)) {
                  return cursor.immutable();
               }
            }
         }
      }

      return null;
   }

   private static boolean isGoodStandPos(Level level, BlockPos pos, MutableBlockPos below, boolean avoidWater) {
      BlockState stateAt = level.getBlockState(pos);
      if (!stateAt.getCollisionShape(level, pos).isEmpty()) {
         return false;
      } else {
         below.set(pos.getX(), pos.getY() - 1, pos.getZ());
         BlockState belowState = level.getBlockState(below);
         if (belowState.getCollisionShape(level, below).isEmpty()) {
            return false;
         } else if (avoidWater) {
            FluidState fluid = level.getFluidState(pos);
            return fluid.isEmpty();
         } else {
            return true;
         }
      }
   }
}
