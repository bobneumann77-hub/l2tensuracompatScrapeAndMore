package io.github.manasmods.tensura.entity.template.subclass;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public interface IFollower {
   boolean shouldFollow();

   default void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      tameable.getNavigation().moveTo(owner, followSpeed);
   }

   default boolean isOverWater(LivingEntity living) {
      BlockPos position = living.blockPosition();

      while (position.getY() > 0 && living.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !living.level().getFluidState(position).isEmpty() || position.getY() <= 0;
   }

   default BlockPos getGround(LivingEntity living, BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)living.getY(), in.getZ());

      while (position.getY() > -64 && !living.level().getBlockState(position).isSolid() && living.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   default boolean targetNotBlocked(LivingEntity living, Vec3 target) {
      Vec3 Vector3d = new Vec3(living.getX(), living.getEyeY(), living.getZ());
      return living.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, living)).getType() == Type.MISS;
   }

   @Nullable
   default Vec3 getBlockInViewAway(LivingEntity living, Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - living.getRandom().nextInt(24) - radiusAdd;
      float neg = living.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = living.yBodyRot;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + living.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getGround(living, radialPos);
      int distFromGround = (int)living.getY() - ground.getY();
      int flightHeight = 4 + living.getRandom().nextInt(10);
      BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : living.getRandom().nextInt(6) + 1);
      return this.targetNotBlocked(living, Vec3.atCenterOf(newPos)) && living.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   @Nullable
   default Vec3 getBlockGrounding(LivingEntity living, Vec3 fleePos) {
      float radius = -9.45F - living.getRandom().nextInt(24);
      float neg = living.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = living.yBodyRot;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + living.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), (int)living.getY(), (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getGround(living, radialPos);
      if (ground.getY() == -64) {
         return living.position();
      }

      ground = living.blockPosition();

      while (ground.getY() > -62 && !living.level().getBlockState(ground).isSolid()) {
         ground = ground.below();
      }

      return this.targetNotBlocked(living, Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
   }
}
