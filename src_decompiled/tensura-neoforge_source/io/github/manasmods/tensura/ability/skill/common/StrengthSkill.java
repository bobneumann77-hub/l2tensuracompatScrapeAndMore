package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
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
import net.minecraft.world.entity.player.Player;

public class StrengthSkill extends Skill {
   private static final CommonSkillConfig.Strength CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Strength;

   public StrengthSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0 ? false : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() ? true : instance.onCoolDown(0) && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isToggled()) {
         instance.addMasteryPoint(entity);
      } else if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      } else {
         entity.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN), 240, CONFIG.strengthenLevel - 1, true, false, true)
         );
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!instance.isToggled()) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN))) {
            instance.setCoolDown(CONFIG.cooldown, mode);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            int duration = instance.isMastered(entity) ? CONFIG.strengthenDurationMastered : CONFIG.strengthenDuration;
            instance.setCoolDown(CONFIG.cooldown + duration / 20, mode);
            entity.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN),
                  duration,
                  instance.isMastered(entity) ? CONFIG.strengthenLevelMastered - 1 : CONFIG.strengthenLevel - 1,
                  true,
                  false,
                  true
               )
            );
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.33,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.66,
               entity.getZ()
            );
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.33,
         entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.66,
         entity.getZ()
      );
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance effect = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
      if (effect != null && effect.getAmplifier() <= 0) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
      }
   }
}
