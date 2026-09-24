package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class AirFlightArt extends Battlewill {
   private static final BattlewillConfig.AirFlight CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).AirFlight;

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.isMastered(entity);
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
         entity.sendSystemMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED));
      } else {
         entity.setNoGravity(true);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (entity.onGround() || entity.isShiftKeyDown()) {
         entity.setNoGravity(false);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0) {
         entity.setNoGravity(false);
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
         entity.sendSystemMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED));
         entity.setNoGravity(false);
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         entity.setNoGravity(false);
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      pushForwardAtStaticSpeed(entity, instance.isMastered(entity) ? CONFIG.forwardBoostMastered : CONFIG.forwardBoost);
      entity.resetFallDistance();
      if (heldTicks >= 10 && entity.onGround()) {
         entity.setNoGravity(false);
      }

      return true;
   }

   public static void pushForwardAtStaticSpeed(LivingEntity entity, float riptideLevel) {
      float f7 = entity.getYRot();
      float f = entity.getXRot();
      float f1 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
      float f2 = -Mth.sin(f * (float) (Math.PI / 180.0));
      float f3 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
      float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
      float f5 = 3.0F * ((1.0F + riptideLevel) / 4.0F);
      f1 *= f5 / f4;
      f2 *= f5 / f4;
      f3 *= f5 / f4;
      entity.setDeltaMovement(f1, f2, f3);
      entity.hasImpulse = true;
      entity.hurtMarked = true;
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      Vec3 movement = entity.getDeltaMovement();
      double dy = delta * 1.5;
      entity.setDeltaMovement(movement.x(), movement.y() + dy, movement.z());
      entity.hurtMarked = true;
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      entity.setNoGravity(false);
   }
}
