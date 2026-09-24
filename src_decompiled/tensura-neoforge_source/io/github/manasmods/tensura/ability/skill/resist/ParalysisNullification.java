package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ParalysisNullification extends ResistSkill {
   public ParalysisNullification() {
      super(ResistSkill.ResistType.NULLIFICATION);
   }

   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return damageSource.is(TensuraDamageTypes.PARALYZING);
   }

   @NotNull
   @Override
   public List<Holder<MobEffect>> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
      return List.of(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), MobEffects.DIG_SLOWDOWN, MobEffects.MOVEMENT_SLOWDOWN);
   }
}
