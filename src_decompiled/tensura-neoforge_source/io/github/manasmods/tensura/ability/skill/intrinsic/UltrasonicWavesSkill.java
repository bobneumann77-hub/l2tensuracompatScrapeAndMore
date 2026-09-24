package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class UltrasonicWavesSkill extends Skill {
   private static final IntrinsicSkillConfig.UltrasonicWaves CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).UltrasonicWaves;

   public UltrasonicWavesSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "ultrasonic_waves.sonic_boom";
         case 1 -> "ultrasonic_waves.auditory_sense";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (mode == 1) {
         if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.AUDITORY_SENSE))) {
            entity.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.AUDITORY_SENSE), CONFIG.auditoryDuration, 0, false, false, false)
            );
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
         }
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.sonicCooldown / 2 : CONFIG.sonicCooldown, mode);
         Vec3 target = entity.position().add(entity.getLookAngle().scale(CONFIG.sonicRange));
         Vec3 source = entity.position().add(0.0, entity.getEyeHeight(), 0.0);
         Vec3 sourceToTarget = target.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);

         for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            ((ServerLevel)level).sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            AABB aabb = new AABB(new BlockPos((int)particlePos.x, (int)particlePos.y, (int)particlePos.z)).inflate(2.0);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, entityData -> !entityData.is(entity) && !entityData.isAlliedTo(entity));
            if (!list.isEmpty()) {
               for (LivingEntity living : list) {
                  if (!RaceUtils.isSpiritual(living)) {
                     DamageSource damageSource = this.createSource(instance, entity, DamageTypes.SONIC_BOOM, mode);
                     living.hurt(damageSource, CONFIG.sonicDamage);
                  }
               }
            }
         }
      }
   }
}
