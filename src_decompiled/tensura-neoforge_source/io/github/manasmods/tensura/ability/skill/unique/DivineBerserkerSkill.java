package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DivineBerserkerSkill extends Skill implements ITransformation {
   private static final UniqueSkillConfig.DivineBerserker CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).DivineBerserker;

   public DivineBerserkerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return this.canTick(instance, entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance ogre = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
      return ogre != null && ogre.getAmplifier() >= 1;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (this.canTick(instance, entity)) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER))) {
         return true;
      }

      if (!TensuraDamageHelper.isBattlewill(source, entity)) {
         return true;
      }

      float multiplier = instance.isMastered(entity) ? CONFIG.battlewillMultiplierMastered : CONFIG.battlewillMultiplier;
      amount.set((Float)amount.get() * multiplier);
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER))) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER))) {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            instance.setCoolDown(CONFIG.cooldown, mode);
         } else {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            int duration = instance.isMastered(entity) ? CONFIG.transformationDurationMastered : CONFIG.transformationDuration;
            instance.setCoolDown(duration / 20 + CONFIG.cooldown, mode);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.TRANSFORM_OGRE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            EffectStorage.setCameraShake(entity, 10.0, 0.1F, 10);
            if (instance.isMastered(entity)) {
               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER), duration, 3, false, false, false));
            } else {
               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER), duration, 1, false, false, false));
            }

            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(), 3.0);
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getPurpleWave(1.0F, entity.getBbWidth() * 7.0F, -0.5F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() / 2.0F,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(),
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               25,
               0.08,
               0.08,
               0.08,
               0.5,
               true
            );
         }
      }
   }
}
