package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbnormalConditionResistance extends ResistSkill {
   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return TensuraDamageHelper.isAbnormal(damageSource);
   }

   @NotNull
   @Override
   public List<Holder<MobEffect>> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
      return List.of(
         MobEffects.HUNGER,
         MobEffects.POISON,
         MobEffects.BLINDNESS,
         MobEffects.CONFUSION,
         MobEffects.DARKNESS,
         MobEffects.DIG_SLOWDOWN,
         MobEffects.MOVEMENT_SLOWDOWN,
         MobEffects.WEAKNESS,
         TensuraMobEffects.getReference(TensuraMobEffects.BURDEN),
         TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY),
         TensuraMobEffects.getReference(TensuraMobEffects.CURSE)
      );
   }

   @Nullable
   @Override
   protected ManasSkill getNullificationForm() {
      return (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get();
   }
}
