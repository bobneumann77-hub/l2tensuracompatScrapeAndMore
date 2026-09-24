package io.github.manasmods.tensura.entity.ai.controller;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlightMoveController extends MoveControl {
   private final Mob entity;
   private final boolean shouldLookAtTarget;
   private final boolean needsYSupport;
   private final boolean ignoreCollision;

   public FlightMoveController(Mob mob, boolean shouldLookAtTarget, boolean needsYSupport, boolean ignoreCollision) {
      super(mob);
      this.entity = mob;
      this.shouldLookAtTarget = shouldLookAtTarget;
      this.needsYSupport = needsYSupport;
      this.ignoreCollision = ignoreCollision;
   }

   public FlightMoveController(Mob mob, boolean shouldLookAtTarget, boolean ignoreCollision) {
      this(mob, shouldLookAtTarget, false, ignoreCollision);
   }

   public FlightMoveController(Mob mob, boolean shouldLookAtTarget) {
      this(mob, shouldLookAtTarget, false);
   }

   public void tick() {
      if (this.operation == Operation.MOVE_TO) {
         Vec3 vector3d = new Vec3(this.wantedX - this.entity.getX(), this.wantedY - this.entity.getY(), this.wantedZ - this.entity.getZ());
         double distance = vector3d.length();
         if (!this.canReach(vector3d.normalize(), Mth.ceil(distance))) {
            this.operation = Operation.WAIT;
         } else {
            this.entity
               .setDeltaMovement(
                  this.entity
                     .getDeltaMovement()
                     .add(vector3d.scale(this.speedModifier * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.05 / distance))
               );
            if (this.needsYSupport) {
               double d1 = this.wantedY - this.entity.getY();
               this.entity
                  .setDeltaMovement(
                     this.entity
                        .getDeltaMovement()
                        .add(0.0, this.entity.getSpeed() * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED) * Mth.clamp(d1, -1.0, 1.0) * 0.6F, 0.0)
                  );
            }

            if (this.entity.getTarget() != null && this.shouldLookAtTarget) {
               double d2 = this.entity.getTarget().getX() - this.entity.getX();
               double d1 = this.entity.getTarget().getZ() - this.entity.getZ();
               this.entity.setYRot(-((float)Mth.atan2(d2, d1)) * (180.0F / (float)Math.PI));
            } else {
               Vec3 vector3d1 = this.entity.getDeltaMovement();
               this.entity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * (180.0F / (float)Math.PI));
            }

            this.entity.yBodyRot = this.entity.getYRot();
         }
      } else if (this.operation == Operation.STRAFE) {
         this.operation = Operation.WAIT;
      }
   }

   private boolean canReach(Vec3 direction, int distance) {
      if (this.ignoreCollision) {
         return true;
      }

      AABB box = this.mob.getBoundingBox();

      for (int i = 1; i < distance; i++) {
         box = box.move(direction);
         if (!this.mob.level().noCollision(this.mob, box)) {
            return false;
         }
      }

      return true;
   }
}
