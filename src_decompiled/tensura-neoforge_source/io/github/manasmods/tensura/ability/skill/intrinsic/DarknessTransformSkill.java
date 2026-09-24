package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DarknessTransformSkill extends ElementalTransformSkill {
   public DarknessTransformSkill() {
      super(Skill.SkillType.INTRINSIC, Element.DARKNESS);
   }

   @Override
   protected void applyVisualEffect(LivingEntity entity) {
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get(), 1.0);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 3, 0.01);
   }

   @Override
   protected void onDamageEntity(ManasSkillInstance instance, LivingEntity target, DamageSource source) {
      target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, CONFIG.effectDuration, 0, false, false, false));
      target.hurt(source, CONFIG.damage);
   }
}
