package io.github.manasmods.tensura.entity.ai.controller;

import io.github.manasmods.tensura.entity.template.subclass.IDigging;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.phys.Vec3;

public class DiggingMoveController<E extends Mob & IDigging<E>> extends MoveControl {
   private final E mob;

   public DiggingMoveController(E mob) {
      super(mob);
      this.mob = mob;
   }

   public void tick() {
      if (this.operation == Operation.MOVE_TO) {
         Vec3 vector3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
         double length = vector3d.length();
         float speed = this.mob.getDiggingTick() < 40 ? 0.0F : 1.0F;
         Vec3 vector3d1 = vector3d.scale(this.speedModifier * speed * 0.025 / length);
         if (this.mob.isSafeDig(this.mob, this.mob.level(), BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ))) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vector3d1).scale(0.9F));
         } else {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.3, 0.0).scale(0.7F));
            this.operation = Operation.WAIT;
            this.mob.getNavigation().stop();
         }

         double width = this.mob.getBoundingBox().getSize();
         if (length < width * 0.15F) {
            this.operation = Operation.WAIT;
         } else if (length >= width) {
            this.mob.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * (180.0F / (float)Math.PI));
         }
      }
   }
}
