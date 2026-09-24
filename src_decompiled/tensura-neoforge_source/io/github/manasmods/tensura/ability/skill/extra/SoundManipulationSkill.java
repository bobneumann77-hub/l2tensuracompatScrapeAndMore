package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class SoundManipulationSkill extends Skill {
   public static final ExtraSkillConfig.SoundManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SoundManipulation;

   public SoundManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((SoundDominationSkill)ExtraSkills.SOUND_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SOUND_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SOUND_BOOST, CONFIG.manipulationBoost);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (TensuraDamageHelper.isSoundDamage(source)) {
         instance.addMasteryPoint(owner);
      }

      return true;
   }

   public static void learnSoundManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.SOUND_MANIPULATION.get())) {
         int skills = !instance.is(TensuraSkillTags.WIND_SKILLS) && !instance.is(TensuraSkillTags.SOUND_SKILLS) ? 0 : 1;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && (skill.is(TensuraSkillTags.WIND_SKILLS) || skill.is(TensuraSkillTags.SOUND_SKILLS))) {
               skills++;
            }
         }

         if (!(skills < CONFIG.soundMasteredSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((SoundManipulationSkill)ExtraSkills.SOUND_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
