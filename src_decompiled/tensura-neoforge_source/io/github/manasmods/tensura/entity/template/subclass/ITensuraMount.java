package io.github.manasmods.tensura.entity.template.subclass;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface ITensuraMount {
   default boolean canActivateMountAbility(LivingEntity rider) {
      return true;
   }

   void mountAbility(Player var1);

   default void mountAbilityRelease(LivingEntity rider) {
   }

   default boolean hasScrollAbility(Player rider) {
      return false;
   }

   default void mountScrollAbility(Player rider, double scrollChange) {
   }

   default void descending(Entity mount, LivingEntity rider) {
      mount.setDeltaMovement(mount.getDeltaMovement().add(0.0, -0.07, 0.0));
   }

   default void onShadowStorageSpawned() {
   }
}
