package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SoulDrainEffect extends TensuraMobEffect {
   public SoulDrainEffect() {
      super(MobEffectCategory.HARMFUL, new Color(40, 4, 75).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.SOUL_DRAIN));
         if (instance == null) {
            return true;
         }

         Entity source = instance.tensura$hasSource() ? level.getEntity(instance.tensura$getSource()) : null;
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
            TensuraDamageTypes.SOUL_CONSUMED, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
         );
         TensuraDamageHelper.directSpiritualHurt(entity, source, damageSource, 10 * (pAmplifier + 1));
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 10 == 0;
   }
}
