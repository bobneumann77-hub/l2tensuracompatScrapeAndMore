package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DarknessAttackNullification extends ResistSkill {
   public DarknessAttackNullification() {
      super(ResistSkill.ResistType.NULLIFICATION);
   }

   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return TensuraDamageHelper.isDarkDamage(damageSource);
   }
}
