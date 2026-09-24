package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColdResistance extends ResistSkill {
   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return TensuraDamageHelper.isCold(damageSource);
   }

   @NotNull
   @Override
   public List<Holder<MobEffect>> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
      return List.of(TensuraMobEffects.getReference(TensuraMobEffects.CHILL));
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         if (SkillUtils.hasSkill(entity, (ManasSkill)ResistanceSkills.HEAT_RESISTANCE.get())
            || SkillUtils.hasSkill(entity, (ManasSkill)ResistanceSkills.HEAT_NULLIFICATION.get())) {
            SkillHelper.learnSkill(entity, (ManasSkill)ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get());
         }
      }
   }

   @Nullable
   @Override
   protected ManasSkill getNullificationForm() {
      return (ManasSkill)ResistanceSkills.COLD_NULLIFICATION.get();
   }
}
