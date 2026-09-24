package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface INameEvolution {
   default boolean canBeNamed(Player player) {
      return true;
   }

   default void onPreNamed(
      IExistence existence,
      @Nullable Player owner,
      Changeable<Double> epGain,
      Changeable<Double> cost,
      RequestNamingMenuPacket.NamingType namingType,
      String name
   ) {
   }

   default int getMaxEvolutionState() {
      return 1;
   }

   default int getCurrentEvolutionState() {
      return 0;
   }

   default void setCurrentEvolutionState(int state) {
   }

   default void evolve() {
      int current = this.getCurrentEvolutionState();
      if (current < this.getMaxEvolutionState()) {
         this.setCurrentEvolutionState(current + 1);
      }
   }

   default void gainMaxHealth(LivingEntity entity, double amount) {
      AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         health.setBaseValue(health.getBaseValue() + amount);
      }

      entity.heal(entity.getMaxHealth());
   }

   default void gainAttackDamage(LivingEntity entity, double amount) {
      AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
         damage.setBaseValue(damage.getBaseValue() + amount);
      }
   }

   default void gainMovementSpeed(LivingEntity entity, double amount) {
      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.setBaseValue(speed.getBaseValue() + amount);
      }
   }

   default void gainSwimSpeed(LivingEntity entity, double amount) {
      AttributeInstance swimSpeed = entity.getAttribute(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      if (swimSpeed != null) {
         swimSpeed.setBaseValue(swimSpeed.getBaseValue() + amount);
      }
   }

   default void gainLavaSpeed(LivingEntity entity, double amount) {
      AttributeInstance lavaSpeed = entity.getAttribute(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER);
      if (lavaSpeed != null) {
         lavaSpeed.setBaseValue(lavaSpeed.getBaseValue() + amount);
      }
   }

   default void gainJumpStrength(LivingEntity entity, double amount) {
      AttributeInstance jump = entity.getAttribute(Attributes.JUMP_STRENGTH);
      if (jump != null) {
         jump.setBaseValue(jump.getBaseValue() + amount);
      }

      AttributeInstance fall = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
      if (fall != null) {
         fall.setBaseValue(fall.getBaseValue() + amount * 10.0);
      }
   }

   default boolean shouldDropCrystal() {
      return true;
   }
}
