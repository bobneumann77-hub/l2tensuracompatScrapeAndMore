package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.utility.AirFlightArt;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FlightMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Flight CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Flight;

   public FlightMagic() {
      super(AspectualMagic.AspectualType.MISC);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      if (entity.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0) {
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
         entity.sendSystemMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED));
         return false;
      }

      if (heldTicks % 10 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      if (heldTicks >= castTime) {
         if (heldTicks == castTime) {
            instance.addMasteryPoint(entity);
         }

         AirFlightArt.pushForwardAtStaticSpeed(entity, CONFIG.pushForce);
         entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, true, false, true));
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
         int tick = heldTicks - castTime;
         int holdDuration = instance.isMastered(entity) ? CONFIG.flightDurationMastered : CONFIG.flightDuration;
         this.renderRemainingTime(entity, tick, holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }
}
