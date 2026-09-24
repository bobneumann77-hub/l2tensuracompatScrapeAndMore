package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.MagicElementalTransformSkill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

public class MagicSpaceTransformSkill extends MagicElementalTransformSkill {
   @Override
   protected Element getMagicElement() {
      return Element.SPACE;
   }

   @Override
   protected ManasSkill getElementalTransform() {
      return (ManasSkill)IntrinsicSkills.SPACE_TRANSFORM.get();
   }

   @Override
   protected void doVisualEffect(LivingEntity entity) {
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         ParticleTypes.REVERSE_PORTAL,
         entity.getX(),
         entity.getY() + entity.getBbHeight() / 2.0F,
         entity.getZ(),
         55,
         0.08,
         0.08,
         0.08,
         2.0,
         true
      );
   }
}
