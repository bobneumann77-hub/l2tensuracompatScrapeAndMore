package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class EngorgerSkill extends Skill {
   public static final UniqueSkillConfig.Engorger CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Engorger;

   public EngorgerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENGORGEMENT), 240, 0, false, false, false));
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENGORGEMENT));
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!instance.isToggled()) {
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.mode.need_toggle_on", new Object[]{this.getName()}).withStyle(ChatFormatting.RED), true
            );
         }

         if (!entity.isShiftKeyDown()) {
            return;
         }

         instance.setToggled(true);
         instance.onToggleOn(entity);
      }

      MobEffectInstance effectInstance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENGORGEMENT));
      if (effectInstance != null && effectInstance.getAmplifier() != 0) {
         if (!entity.onGround() && !entity.isInWaterOrBubble()) {
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, mode, 0.5F)) {
            return;
         }

         instance.addMasteryPoint(entity);
         entity.resetFallDistance();
         float bonus = instance.isMastered(entity) ? CONFIG.dashAttackBonusMastered : CONFIG.dashAttackBonus;
         float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * CONFIG.dashAttackMultiplier + bonus;
         if (entity instanceof Player player) {
            player.startAutoSpinAttack(CONFIG.dashDuration, damage, entity.getMainHandItem());
         }

         SkillHelper.riptidePush(entity, CONFIG.dashLevel);
         entity.hurtMarked = true;
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      } else {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         instance.addMasteryPoint(entity);
         double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENGORGEMENT), 2400, 1, false, false, false), entity);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }
}
