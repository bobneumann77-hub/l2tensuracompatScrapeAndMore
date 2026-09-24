package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class IceBlizzardMagic extends AspectualMagic {
   public static final AspectualMagicConfig.IceBlizzard CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).IceBlizzard;

   public IceBlizzardMagic() {
      super(AspectualMagic.AspectualType.ICE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("BarrierID", 0);
      instance.markDirty();
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      float radius = CONFIG.blizzardRadius;
      MagicCircle.castMagicCircle(
         radius / 2.0F,
         25,
         MagicCircleVariant.ICE,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         int holdDuration = CONFIG.castTimeHold;
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BarrierEntity blizzard = BarrierEntity.getLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.BLIZZARD.get(),
            0.0F,
            radius,
            0.0F,
            60,
            20.0F,
            entity.position().add(0.0, -radius, 0.0),
            entity,
            instance,
            mode,
            cost,
            Pair.of(0.0, 0.0),
            heldTicks
         );
         if (blizzard != null) {
            if (heldTicks == castTime) {
               blizzard.setAge(-20);
               blizzard.setTickEachHit(holdDuration);
               blizzard.setSecondaryDamage(CONFIG.blizzardDamage);
               MobEffectInstance chill = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.CHILL), CONFIG.chillLevel, CONFIG.chillDuration, true, false, true
               );
               blizzard.setMobEffect(chill);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), radius / 3.0F);
            }

            if (heldTicks > castTime && (heldTicks - castTime) % holdDuration == 0) {
               if (this.isOutOfEnergy(entity, instance, (double)0.0, (double)CONFIG.magiculeCostHold)) {
                  return false;
               }

               if (heldTicks - castTime == holdDuration) {
                  blizzard.setEffectStack(true);
                  blizzard.setSecondaryDamage(CONFIG.blizzardDamageHold);
                  MobEffectInstance chillHold = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.CHILL), CONFIG.chillLevelHold, CONFIG.chillDurationHold, true, false, true
                  );
                  blizzard.setMobEffect(chillHold);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), radius);
               }
            }
         }

         int tick = heldTicks - castTime;
         int holdTime = holdDuration * ((instance.isMastered(entity) ? CONFIG.maxHoldMastered : CONFIG.maxHold) - 1);
         this.renderRemainingTime(entity, tick, holdTime);
         return tick <= holdTime;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      int cast = this.getCastingTime(instance, entity);
      if (cast <= 1 && heldTicks >= cast) {
         instance.setCoolDown(60, mode);
      }
   }
}
