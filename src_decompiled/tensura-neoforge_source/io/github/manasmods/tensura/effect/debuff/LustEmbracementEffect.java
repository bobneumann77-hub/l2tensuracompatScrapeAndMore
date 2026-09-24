package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.LustSkill;
import io.github.manasmods.tensura.effect.WebbedEffect;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.awt.Color;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class LustEmbracementEffect extends TensuraMobEffect {
   private static final Predicate<LivingEntity> LUST_FILTER = entity -> entity.isAlive()
      && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT));
   public static final ResourceLocation EMBRACEMENT = ResourceLocation.fromNamespaceAndPath("tensura", "embracement");

   public LustEmbracementEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(255, 249, 0).getRGB());
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, EMBRACEMENT, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, EMBRACEMENT, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, EMBRACEMENT, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FLYING_SPEED, EMBRACEMENT, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, EMBRACEMENT, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, EMBRACEMENT, 1.0, Operation.ADD_VALUE);
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setLockedXRot(entity.getXRot());
      effect.setLockedYRot(entity.getYHeadRot());
      effect.markDirty();
   }

   public boolean applyEffectTick(LivingEntity target, int pAmplifier) {
      WebbedEffect.lockRotation(target);
      MobEffectInstance instance = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT));
      if (instance == null) {
         return true;
      }

      if (target.level() instanceof ServerLevel level) {
         if (instance.getDuration() % 5 == 0) {
            TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
         }

         if (instance.getDuration() % 20 != 0) {
            return true;
         }

         if (instance.tensura$hasSource()) {
            Entity source = level.getEntity(instance.tensura$getSource());
            if (source == target) {
               double distance = target.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
               List<LivingEntity> list = target.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(distance), LUST_FILTER);
               return !list.isEmpty();
            }

            if (source != null) {
               if (!source.isAlive()) {
                  return false;
               }

               IEffect effect = TensuraStorages.getEffectFrom(target);
               if (!effect.isIgnorePainNull()) {
                  effect.setIgnorePainNull(true);
                  effect.markDirty();
               }

               if (pAmplifier >= 1
                  && !EnergyHelper.drainEnergy(
                     target, source, LustSkill.CONFIG.embraceEPMastered, true, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL_EXCEED_MAX
                  )) {
                  return true;
               }

               if (!EnergyHelper.drainEnergy(
                  target, source, LustSkill.CONFIG.embraceEP, false, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL_EXCEED_MAX
               )) {
                  return true;
               }

               level.playSound(
                  null, source.getX(), source.getY(), source.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               TensuraParticleHelper.spawnServerParticles(
                  level,
                  TensuraParticleUtils.getGoldWave(0.9F, source.getBbWidth() * 3.0F, -0.5F, true),
                  source.getX(),
                  source.getY() + source.getBbHeight() * 0.5,
                  source.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  level,
                  TensuraParticleUtils.getGoldWave(0.9F, target.getBbWidth() * 3.0F, 0.3F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setIgnorePainNull(false);
      effect.markDirty();
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
      return pDuration > 0;
   }

   public static boolean isEmbraced(LivingEntity entity) {
      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return speed != null && speed.hasModifier(EMBRACEMENT);
   }
}
