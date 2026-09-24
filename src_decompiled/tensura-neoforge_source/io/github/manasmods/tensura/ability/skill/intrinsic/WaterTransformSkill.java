package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class WaterTransformSkill extends ElementalTransformSkill {
   public WaterTransformSkill() {
      super(Skill.SkillType.INTRINSIC, Element.WATER);
   }

   @Override
   protected void applyVisualEffect(LivingEntity entity) {
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getWaterBubble(), 1.0);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, new SimpleAuraParticleOptions(0.64F, 0.92F, 0.95F, 1.0F, (float)size, -0.3F), 5, 0.01);
   }

   @Override
   protected void onDamageEntity(ManasSkillInstance instance, LivingEntity target, DamageSource source) {
      target.addEffect(new MobEffectInstance(MobEffects.POISON, CONFIG.effectDuration, 0, false, false, false));
      target.hurt(source, CONFIG.damage);
   }
}
