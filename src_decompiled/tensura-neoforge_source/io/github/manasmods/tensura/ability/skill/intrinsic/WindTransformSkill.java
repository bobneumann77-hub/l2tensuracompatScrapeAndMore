package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class WindTransformSkill extends ElementalTransformSkill {
   public WindTransformSkill() {
      super(Skill.SkillType.INTRINSIC, Element.WIND);
   }

   @Override
   protected void applyVisualEffect(LivingEntity entity) {
      if (entity.tickCount % 5 == 0) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getGreenGust(), 3.0);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, new SimpleAuraParticleOptions(0.77F, 0.95F, 0.8F, 1.0F, (float)size, -0.3F), 5, 0.01);
   }

   @Override
   protected void onDamageEntity(ManasSkillInstance instance, LivingEntity target, DamageSource source) {
      target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), CONFIG.effectDuration, 0, true, false, true));
      target.hurt(source, CONFIG.damage);
   }
}
