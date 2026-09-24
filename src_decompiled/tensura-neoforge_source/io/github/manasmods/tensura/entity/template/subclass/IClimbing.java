package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IClimbing {
   boolean isClimbing();

   void setClimbing(boolean var1);

   default float getClimbSpeedMultiplier() {
      return 1.0F;
   }

   default boolean collidingWall(Entity entity) {
      return !entity.horizontalCollision ? false : entity.level().getEntitiesOfClass(BarrierPart.class, entity.getBoundingBox().inflate(0.5)).isEmpty();
   }

   default void handleClimbing(LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         this.setClimbing(this.collidingWall(entity));
      }

      if (entity.onClimbable()) {
         entity.resetFallDistance();
         if (this.collidingWall(entity)) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getDeltaMovement().y * this.getClimbSpeedMultiplier(), entity.getDeltaMovement().z);
         }
      }
   }

   default void handleRideableClimbing(LivingEntity entity, Vec3 vec3) {
      double yawRad = Math.toRadians(entity.getYRot());
      Vec3 rotatedVec = this.rotateVector(vec3, -Math.toDegrees(Math.atan2(-Math.sin(yawRad), Math.cos(yawRad))));
      Vec3 collide = entity.collide(rotatedVec);
      double dot = rotatedVec.x * collide.x + rotatedVec.z * collide.z;
      double originalLengthSq = rotatedVec.x * rotatedVec.x + rotatedVec.z * rotatedVec.z;
      entity.horizontalCollision = dot < originalLengthSq - 1.0E-5;
      this.setClimbing(this.collidingWall(entity));
      if (this.collidingWall(entity) && entity.onClimbable()) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.1 * this.getClimbSpeedMultiplier(), 0.0));
      }
   }

   default Vec3 rotateVector(Vec3 vec, double degrees) {
      double rad = Math.toRadians(degrees);
      double cos = Math.cos(rad);
      double sin = Math.sin(rad);
      double x = vec.x * cos - vec.z * sin;
      double z = vec.x * sin + vec.z * cos;
      return new Vec3(x, vec.y, z);
   }
}
