package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LightTransformSkill extends ElementalTransformSkill {
   public LightTransformSkill() {
      super(Skill.SkillType.INTRINSIC, Element.LIGHT);
   }

   @Override
   protected void applyVisualEffect(LivingEntity entity) {
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getGoldAura(0.8F, (float)size, -0.3F), 5, 0.01);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getYellowGust());
   }

   @Override
   protected void onDamageEntity(ManasSkillInstance instance, LivingEntity target, DamageSource source) {
      target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, CONFIG.effectDuration, 0, false, false, false));
      target.hurt(source, CONFIG.damage);
   }
}
