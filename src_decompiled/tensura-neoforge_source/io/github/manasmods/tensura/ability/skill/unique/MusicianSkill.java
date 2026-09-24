package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.SenseSoundwaveSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MusicianSkill extends Skill {
   private static final UniqueSkillConfig.Musician CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Musician;

   public MusicianSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 2 : 1;
            case 1 -> 0;
            case 2 -> 1;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> instance.isMastered(entity) ? 2 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "musician.sonic_blast";
         case 1 -> "musician.sound_wave";
         case 2 -> "musician.mind_requiem";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostWave;
         case 2 -> CONFIG.magiculeCostRequiem;
         default -> CONFIG.magiculeCostBlast;
      };
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, 1.0, Operation.ADD_VALUE);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, 1.0);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Level level = entity.level();
         switch (mode) {
            case 0:
               this.sonicBlast(instance, entity, level, mode);
               break;
            case 1:
               this.soundWave(instance, entity, level, mode);
               break;
            case 2:
               this.mindRequiem(instance, entity, level, mode);
         }
      }
   }

   public void sonicBlast(ManasSkillInstance instance, LivingEntity entity, Level level, int mode) {
      instance.addMasteryPoint(entity);
      instance.setCoolDown(CONFIG.blastCooldown, mode);
      double range = instance.isMastered(entity) ? CONFIG.blastRangeMastered : CONFIG.blastRange;
      Vec3 target = entity.position().add(entity.getLookAngle().scale(range));
      Vec3 source = entity.position().add(0.0, 1.6F, 0.0);
      Vec3 offSetToTarget = target.subtract(source);
      Vec3 normalizes = offSetToTarget.normalize();
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.MUSIC_BLAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      float damage = instance.isMastered(entity) ? CONFIG.blastDamageMastered : CONFIG.blastDamage;

      for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
         Vec3 particlePos = source.add(normalizes.scale(particleIndex));
         TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessSonic(0.75F, 1.0F), particlePos.x, particlePos.y, particlePos.z);
         AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(2.0);
         List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(entity) && !livingx.isAlliedTo(entity));
         if (!list.isEmpty()) {
            for (LivingEntity living : list) {
               if (!RaceUtils.isSpiritual(living)) {
                  DamageSource damagesource = this.createSource(instance, entity, TensuraDamageTypes.SOUND_BLAST, mode);
                  if (living.hurt(damagesource, damage)) {
                     EffectStorage.setCameraShake(living, 0.02F, 10);
                  }
               }
            }
         }
      }
   }

   public void soundWave(ManasSkillInstance instance, LivingEntity entity, Level level, int mode) {
      entity.level()
         .playSound(
            null,
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            (SoundEvent)TensuraSoundEvents.MUSIC_BLAST.get(),
            TensuraSkill.ABILITY_SOUND,
            1.0F,
            1.0F
         );
      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.waveRadius),
            living -> !living.is(entity) && !living.isAlliedTo(entity) && living.isAlive()
         );
      if (!list.isEmpty()) {
         float damage = instance.isMastered(entity) ? CONFIG.waveDamageMastered : CONFIG.waveDamage;
         instance.addMasteryPoint(entity);

         for (LivingEntity target : list) {
            if (!RaceUtils.isSpiritual(target)) {
               DamageSource damagesource = this.createSource(instance, entity, TensuraDamageTypes.SOUND_BLAST, mode).tensura$setDodgeBypass();
               if (target.hurt(damagesource, damage)) {
                  EffectStorage.setCameraShake(target, 0.01F, 10);
               }
            }
         }
      }

      instance.setCoolDown(CONFIG.waveCooldown, mode);
      float size = Math.max(1.0F, entity.getBbWidth() / 1.8F);
      TensuraParticleHelper.spawnServerParticles(
         level, TensuraParticleUtils.getColorlessWave(0.3F, 2.0F * size), entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         level, TensuraParticleUtils.getColorlessWave(0.3F, 3.73F * size), entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         level, TensuraParticleUtils.getColorlessWave(0.3F, 4.0F * size), entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         level, TensuraParticleUtils.getColorlessWave(0.3F, 3.73F * size), entity.getX(), entity.getY() + entity.getBbHeight() * 0.25, entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessWave(0.3F, 2.0F * size), entity.getX(), entity.getY(), entity.getZ());
   }

   public void mindRequiem(ManasSkillInstance instance, LivingEntity entity, Level level, int mode) {
      instance.addMasteryPoint(entity);
      instance.setCoolDown(CONFIG.requiemCooldown, mode);
      Vec3 target = entity.position().add(entity.getLookAngle().scale(CONFIG.requiemRange));
      Vec3 source = entity.position().add(0.0, 1.6F, 0.0);
      Vec3 offSetToTarget = target.subtract(source);
      Vec3 normalizes = offSetToTarget.normalize();
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.MUSIC_REQUIEM.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );

      for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
         Vec3 particlePos = source.add(normalizes.scale(particleIndex));
         TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getMindRequiemSonic(2.0F), particlePos.x, particlePos.y, particlePos.z);
         AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(3.0);
         List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(entity) && !livingx.isAlliedTo(entity));
         if (!list.isEmpty()) {
            for (LivingEntity living : list) {
               DamageSource damagesource = this.createSource(instance, entity, TensuraDamageTypes.MIND_REQUIEM, mode).tensura$setDodgeBypass();
               if (living.hurt(damagesource, CONFIG.requiemDamage)) {
                  TensuraDamageHelper.directSpiritualHurt(living, entity, damagesource, CONFIG.requiemSpiritualDamage);
                  EffectStorage.setCameraShake(living, 0.03F, 10);
               }
            }
         }
      }
   }
}
