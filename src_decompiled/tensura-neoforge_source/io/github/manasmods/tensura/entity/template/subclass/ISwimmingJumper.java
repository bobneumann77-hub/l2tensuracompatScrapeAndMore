package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.ai.navigator.SwimmingJumpNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult.Type;

public interface ISwimmingJumper extends ISwimming {
   @Override
   default void initSwimming(PathfinderMob entity) {
      entity.setPathfindingMalus(PathType.WATER, 0.0F);
      this.switchLookControl(new SmoothSwimmingLookControl(entity, 10));
      this.switchMoveControl(new SmoothSwimmingMoveControl(entity, 85, 10, 1.0F, 0.5F, false));
      this.switchNavigation(new SwimmingJumpNavigation(entity, entity.level()));
   }

   default boolean shouldUseJumpAttack(LivingEntity entity, LivingEntity target, boolean waterTarget) {
      int dist = (int)Math.sqrt(entity.distanceToSqr(target));
      return this.canJumpOutOfWater(entity, 7, dist, waterTarget);
   }

   default boolean canJumpOutOfWater(LivingEntity entity, int height, int dist, boolean waterTarget) {
      Direction direction = entity.getMotionDirection();
      int i = direction.getStepX();
      int j = direction.getStepZ();
      BlockPos pos = entity.blockPosition();
      return waterTarget && !this.isWaterClear(entity, pos, i, j, dist) ? false : this.isSurfaceClear(entity, pos, 3, height, i, j, dist);
   }

   default boolean isWaterClear(LivingEntity entity, BlockPos blockPos, int stepX, int stepY, int dist) {
      BlockPos blockPos2 = blockPos.offset(stepX * dist, 0, stepY * dist);
      return entity.level().getFluidState(blockPos2).is(FluidTags.WATER) && !entity.level().getBlockState(blockPos2).blocksMotion();
   }

   default boolean isSurfaceClear(LivingEntity entity, BlockPos pos, int startHeight, int height, int stepX, int stepZ, int dist) {
      return entity.level()
         .clip(
            new ClipContext(
               pos.offset(stepX * dist, startHeight, stepZ * dist).getCenter(),
               pos.offset(stepX * dist, height, stepZ * dist).getCenter(),
               Block.COLLIDER,
               Fluid.SOURCE_ONLY,
               entity
            )
         )
         .getType()
         .equals(Type.MISS);
   }
}
