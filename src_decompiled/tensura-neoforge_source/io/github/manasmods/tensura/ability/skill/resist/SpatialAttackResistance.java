package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SpatialAttackResistance extends ResistSkill {
   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return TensuraDamageHelper.isSpatialDamage(damageSource);
   }

   @Nullable
   @Override
   protected ManasSkill getNullificationForm() {
      return (ManasSkill)ResistanceSkills.SPATIAL_ATTACK_NULLIFICATION.get();
   }
}
