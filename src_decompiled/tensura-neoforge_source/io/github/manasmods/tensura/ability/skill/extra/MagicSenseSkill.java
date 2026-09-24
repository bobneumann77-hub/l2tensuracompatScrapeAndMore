package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MagicSenseSkill extends Skill {
   private static final ExtraSkillConfig.MagicSense CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MagicSense;

   public MagicSenseSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
      return race.isPresent() && race.get().is(TensuraRaceTags.SLIME) ? true : entity.hasEffect(MobEffects.DARKNESS) || entity.hasEffect(MobEffects.BLINDNESS);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         if (SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.DANGER_SENSE.get())) {
            SkillHelper.learnSkill(entity, ((SenseSoundwaveSkill)ExtraSkills.SENSE_SOUNDWAVE.get()).createLearningInstance(entity));
         }
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled()) {
         this.onToggleOn(instance, entity);
      }

      if (!instance.isSubInstance()) {
         if (SkillUtils.hasSkillFully(entity, (ManasSkill)ExtraSkills.SENSE_HEAT_SOURCE.get())) {
            if (SkillUtils.hasSkillFully(entity, (ManasSkill)ExtraSkills.SENSE_SOUNDWAVE.get())) {
               SkillHelper.learnSkill(entity, ((UniversalPerceptionSkill)ExtraSkills.UNIVERSAL_PERCEPTION.get()).createLearningInstance(entity));
            }
         }
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0F)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, instance.isMastered(entity) ? CONFIG.presenceSenseMastered : CONFIG.presenceSensePress);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.presenceSenseMastered);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.isToggled()) {
         instance.setToggled(false);
         AttributeHelper.removePresenceSense(entity, instance.isMastered(entity) ? CONFIG.presenceSenseMastered : CONFIG.presenceSensePress);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      } else {
         instance.setToggled(true);
         AttributeHelper.addPresenceSense(entity, instance.isMastered(entity) ? CONFIG.presenceSenseMastered : CONFIG.presenceSensePress);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }
}
