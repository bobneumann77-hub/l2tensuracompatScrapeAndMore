package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.battlewill.UtilityArts;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HazeArt extends Battlewill {
   private static final BattlewillConfig.Haze CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).Haze;

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)UtilityArts.FORMHIDE.get());
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0F)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.lack_aura.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED),
               false
            );
         }

         instance.setToggled(false);
         instance.onToggleOff(entity);
      } else {
         entity.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 200, CONFIG.concealment - 1, false, false, false)
         );
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.isMastered(entity)) {
         if (instance.isToggled()) {
            instance.setToggled(false);
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else {
            instance.setToggled(true);
            entity.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 200, CONFIG.concealment - 1, false, false, false)
            );
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.PRESENCE_CONCEALMENT.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.isMastered(entity)) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 5, CONFIG.concealment - 1, false, false, false)
      );
      if (heldTicks == 0) {
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      this.onTick(instance, entity);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance concealment = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
      if (concealment != null && concealment.getAmplifier() == CONFIG.concealment - 1) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
      }
   }
}
