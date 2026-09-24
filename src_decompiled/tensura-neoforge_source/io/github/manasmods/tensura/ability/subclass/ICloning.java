package io.github.manasmods.tensura.ability.subclass;

import io.github.manasmods.tensura.entity.human.CloneEntity;
import net.minecraft.world.entity.LivingEntity;

public interface ICloning {
   void onCloneTick(CloneEntity var1, LivingEntity var2);

   static double getConvertedMovementSpeed(double speed, boolean fromPlayer) {
      return fromPlayer ? speed / 0.1 * 0.2 : speed / 0.2 * 0.1;
   }

   static double getConvertedJumpStrength(double strength, boolean fromPlayer) {
      return fromPlayer ? strength / 0.42 * 0.7 : Math.max(strength / 0.7 * 0.42, 0.42);
   }
}
