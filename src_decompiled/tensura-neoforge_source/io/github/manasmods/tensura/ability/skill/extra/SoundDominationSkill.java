package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SoundDominationSkill extends Skill {
   public SoundDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.SOUND_MANIPULATION.get())
         ? false
         : newEP > SoundManipulationSkill.CONFIG.dominationEpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SOUND_BOOST, SoundManipulationSkill.CONFIG.dominationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SOUND_BOOST, SoundManipulationSkill.CONFIG.dominationBoost);
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
}
