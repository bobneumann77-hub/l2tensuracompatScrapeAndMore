package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MercilessSkill extends Skill {
   private static final UniqueSkillConfig.Merciless CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Merciless;

   public MercilessSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
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
         case 0 -> "merciless.steal";
         case 1 -> "merciless.consume";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostSteal;
         case 1 -> CONFIG.magiculeCostConsume;
         default -> 0.0;
      };
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance, 1)) {
         return true;
      }

      if (!EnergyHelper.isOutOfEnergy(owner, instance, 1)) {
         MobEffectInstance soulDrain = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.SOUL_DRAIN), CONFIG.drainDuration, CONFIG.drainLevel - 1, false, false, false
         );
         TensuraMobEffect.addEffect(target, soulDrain, owner, this, 1);
      }

      return true;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SOUL_ESCAPE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0);
      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.stealRadius),
            targetx -> !targetx.is(entity) && targetx.isAlive() && !targetx.isAlliedTo(entity) && !targetx.getType().is(TensuraEntityTags.NO_SPIRITUAL_DAMAGE)
         );
      if (!list.isEmpty()) {
         double ownerEP = EnergyHelper.getMaxEP(entity) * CONFIG.stealEP;
         double fearLevel = CONFIG.stealFear - 1.0;

         for (LivingEntity target : list) {
            if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
               MobEffectInstance fear = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
               boolean shouldConsume = target.getHealth() < target.getMaxHealth() * CONFIG.stealHP
                  || EnergyHelper.getMaxEP(target) < ownerEP
                  || fear != null && fear.getAmplifier() >= fearLevel;
               if (shouldConsume) {
                  DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.SOUL_CONSUMED, mode).tensura$setDodgeBypass();
                  target.hurt(source, target.getMaxHealth() * 10.0F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0);
               }
            }
         }
      }

      return true;
   }
}
