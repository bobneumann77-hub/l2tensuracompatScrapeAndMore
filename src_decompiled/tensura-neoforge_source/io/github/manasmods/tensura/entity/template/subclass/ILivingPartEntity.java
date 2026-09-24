package io.github.manasmods.tensura.entity.template.subclass;

import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface ILivingPartEntity {
   default UUID getHeadId() {
      return null;
   }

   default Entity getHead() {
      return null;
   }

   void onServerHurt(LivingEntity var1);

   default Vec3 getOffsetVec(float offsetZ, float xRot, float yRot) {
      return new Vec3(0.0, 0.0, offsetZ).xRot(xRot * (float) (Math.PI / 180.0)).yRot(-yRot * (float) (Math.PI / 180.0));
   }

   default float getLimitAngle(float sourceAngle, float targetAngle, float maximumChange) {
      float f = Mth.wrapDegrees(targetAngle - sourceAngle);
      if (f > maximumChange) {
         f = maximumChange;
      }

      if (f < -maximumChange) {
         f = -maximumChange;
      }

      float f1 = sourceAngle + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   static LivingEntity checkForHead(LivingEntity entity) {
      return !entity.level().isClientSide() && entity instanceof ILivingPartEntity part && part.getHead() instanceof LivingEntity head ? head : entity;
   }
}
