package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.LustSkill;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.awt.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class LustDrainEffect extends TensuraMobEffect implements DamageAction {
   public LustDrainEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(45, 6, 63).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
      return pDuration % 10 == 0;
   }

   @Override
   public boolean onPlayerAttack(Player source, Entity entity) {
      if (entity instanceof LivingEntity target) {
         MobEffectInstance instance = source.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_DRAIN));
         if (instance == null) {
            return true;
         }

         if (!EnergyHelper.drainEnergy(target, source, LustSkill.CONFIG.drainEP, false, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL_EXCEED_MAX)) {
            return true;
         }

         if (instance.getAmplifier() > 0) {
            EnergyHelper.drainEnergy(
               target,
               source,
               LustSkill.CONFIG.drainEPMastered / 2.0 * instance.getAmplifier(),
               false,
               EnergyHelper.DrainType.EP,
               EnergyHelper.GainType.NORMAL_EXCEED_MAX
            );
         }

         source.level()
            .playSound(
               null, source.getX(), source.getY(), source.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
         return true;
      } else {
         return true;
      }
   }
}
