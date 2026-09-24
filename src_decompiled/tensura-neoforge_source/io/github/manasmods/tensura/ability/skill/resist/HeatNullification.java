package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class HeatNullification extends ResistSkill {
   public HeatNullification() {
      super(ResistSkill.ResistType.NULLIFICATION);
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         if (SkillUtils.hasSkill(entity, (ManasSkill)ResistanceSkills.COLD_RESISTANCE.get())
            || SkillUtils.hasSkill(entity, (ManasSkill)ResistanceSkills.COLD_NULLIFICATION.get())) {
            SkillHelper.learnSkill(entity, (ManasSkill)ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get());
         }
      }
   }

   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return TensuraDamageHelper.isHeat(damageSource);
   }
}
