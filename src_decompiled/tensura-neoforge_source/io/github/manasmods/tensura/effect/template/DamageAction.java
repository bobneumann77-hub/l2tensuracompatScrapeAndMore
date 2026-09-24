package io.github.manasmods.tensura.effect.template;

import io.github.manasmods.manascore.network.api.util.Changeable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface DamageAction {
   default boolean onBeingDamaged(LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      return true;
   }

   default boolean onDamagingEntity(LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      return true;
   }

   default boolean onPlayerAttack(Player player, Entity target) {
      return true;
   }

   default void onPostBeingDamaged(LivingEntity entity, DamageSource source, float amount) {
   }

   default boolean onKillEntity(LivingEntity attacker, LivingEntity entity, DamageSource source) {
      return true;
   }
}
