package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class AuraSwordArt extends Battlewill {
   private static final BattlewillConfig.AuraSword CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).AuraSword;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() ? true : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isToggled()) {
         instance.addMasteryPoint(entity);
      } else if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_aura.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
      } else {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD), 240, 0, true, false, true));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!instance.isToggled()) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD))) {
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
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD));
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD), CONFIG.effectTime, 0, true, false, true));
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance effect = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD));
      if (effect != null && effect.getAmplifier() <= 0) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD));
      }
   }
}
