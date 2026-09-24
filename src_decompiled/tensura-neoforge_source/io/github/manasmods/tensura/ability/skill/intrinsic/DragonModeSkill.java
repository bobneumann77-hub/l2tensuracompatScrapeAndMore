package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DragonModeSkill extends Skill implements ITransformation {
   private static final IntrinsicSkillConfig.DragonMode CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).DragonMode;

   public DragonModeSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return this.canTick(instance, entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE));
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
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE))) {
         if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE))) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            instance.addMasteryPoint(entity);
            instance.setCoolDown(CONFIG.cooldown, mode);
            entity.setHealth(entity.getHealth() * 2.0F);
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setMagicule(existence.getMagicule() * 2.0);
            existence.setAura(existence.getAura() * 2.0);
            existence.markDirty();
            EffectStorage.setCameraShake(entity, 10.0, 0.1F, 10);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.TRANSFORM_DRAGON.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            entity.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE),
                  instance.isMastered(entity) ? CONFIG.transformationDurationMastered : CONFIG.transformationDuration,
                  0,
                  false,
                  false,
                  false
               )
            );
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 3.0);
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getGoldWave(1.0F, entity.getBbWidth() * 7.0F, -0.5F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() / 2.0F,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(),
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
         } else {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE));
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
         }
      }
   }
}
