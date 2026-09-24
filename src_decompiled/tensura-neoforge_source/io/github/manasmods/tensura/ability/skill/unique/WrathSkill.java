package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WrathSkill extends Skill {
   public static final UniqueSkillConfig.Wrath CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Wrath;
   public static final ResourceLocation WRATH = ResourceLocation.fromNamespaceAndPath("tensura", "wrath_rampage");

   public WrathSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public int getMaxMastery() {
      return SKILL_CONFIG.Mastery.masteryUniqueSin;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.isMastered(living);
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
         case 0 -> "wrath.breader";
         case 1 -> "wrath.enrage";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostEnrage : 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return !instance.isMastered(entity) ? false : instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      this.activateBreederReactor(instance, entity);
      this.summonWrathParticles(entity);
   }

   private void summonWrathParticles(LivingEntity entity) {
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getRedAura(1.0F, (float)size, -0.3F), 3, 0.03);
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      switch (mode) {
         case 0:
            if (instance.isToggled() && instance.isMastered(entity)) {
               return false;
            }

            if (heldTicks % 100 == 0 && heldTicks > 0) {
               this.activateBreederReactor(instance, entity);
            }

            if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
               instance.addMasteryPoint(entity);
            }

            this.summonWrathParticles(entity);
            if (entity instanceof Player player) {
               player.playNotifySound(SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }

            return true;
         case 1:
            if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return false;
            } else {
               Level level = entity.level();
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_ANGRY, TensuraSkill.ABILITY_SOUND, 10.0F, 1.0F);
               double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
               if (heldTicks % 3 == 0) {
                  TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getRedAura(0.2F, (float)size, -0.3F), 10, 0.01, 3.0F);
               }

               List<LivingEntity> list = level.getEntitiesOfClass(
                  LivingEntity.class,
                  entity.getBoundingBox().inflate(CONFIG.enrageRadius),
                  living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
               );
               if (!list.isEmpty()) {
                  if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
                     instance.addMasteryPoint(entity);
                  }

                  for (LivingEntity target : list) {
                     if (!target.isSpectator()
                        && !(target instanceof Player player && player.isCreative())
                        && !SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())
                        && (
                           !SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                              || entity.getRandom().nextInt(3) == 1
                        )) {
                        int effectLevel = 0;
                        MobEffectInstance rampage = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
                        int duration;
                        if (rampage != null && heldTicks > 0) {
                           duration = rampage.getDuration() + 2;
                           effectLevel = (duration - CONFIG.enrageDuration) / CONFIG.enrageIncreaseTick;
                        } else {
                           duration = CONFIG.enrageDuration;
                        }

                        target.addEffect(
                           new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), duration, effectLevel + 1, true, false, true),
                           entity
                        );
                        if (heldTicks % 10 == 0) {
                           TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 2.0);
                        }
                     }
                  }
               }

               return true;
            }
         default:
            return false;
      }
   }

   private void activateBreederReactor(ManasSkillInstance instance, LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      double maxMP = EnergyHelper.getMaxMagicule(entity);
      existence.setMagicule(existence.getMagicule() + maxMP * CONFIG.breederMultiplier);
      existence.markDirty();
      double rampageChance = existence.getMagicule() > maxMP ? CONFIG.breederAboveChance : CONFIG.breederUnderChance;
      if (instance.isMastered(entity)) {
         rampageChance *= 2.0;
      }

      if (entity.getRandom().nextFloat() < rampageChance / 100.0) {
         int max = instance.isMastered(entity) ? CONFIG.maxRampageMastered - 1 : CONFIG.maxRampage - 1;
         MobEffectInstance effectInstance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
         int level = effectInstance != null ? Math.min(effectInstance.getAmplifier() + 1, max) : 0;
         MobEffectInstance rampage = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), CONFIG.breederDuration, level, true, false, true
         );
         TensuraMobEffect.addEffect(entity, rampage, entity, this, 0);
      }
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer owner, boolean conqueredEnd) {
      if (!conqueredEnd) {
         if (instance.isToggled()) {
            instance.setToggled(false);
            instance.onToggleOff(owner);
         }
      }
   }
}
