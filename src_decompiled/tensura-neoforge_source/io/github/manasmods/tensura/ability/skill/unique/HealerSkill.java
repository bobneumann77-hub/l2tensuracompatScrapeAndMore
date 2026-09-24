package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HealerSkill extends Skill {
   private static final UniqueSkillConfig.Healer CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Healer;

   public HealerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
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
         case 0 -> "healer.heal";
         case 1 -> "healer.virus";
         case 2 -> "healer.plague";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostInfection;
         case 2 -> 300.0;
         default -> 0.0;
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            LivingEntity targetx = entity.isShiftKeyDown() ? ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false) : null;
            entity.swing(InteractionHand.MAIN_HAND, true);
            if (targetx != null) {
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
               float healingHP = targetx.getMaxHealth() - targetx.getHealth();
               double lackedMana = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(healingHP * cost));
               if (lackedMana > 0.0) {
                  healingHP = (float)(healingHP - lackedMana / cost);
               }

               targetx.heal(healingHP);
               if (instance.isMastered(entity)) {
                  IExistence existence = TensuraStorages.getExistenceFrom(targetx);
                  double healingSpiritual = existence.getSpiritualHealth();
                  double lackedSpiritual = targetx.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) - healingSpiritual;
                  double lackedMP = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedSpiritual * CONFIG.magiculeCostSHP));
                  if (lackedMP > 0.0) {
                     lackedSpiritual -= lackedMP / CONFIG.magiculeCostSHP;
                  }

                  existence.setSpiritualHealth(healingSpiritual + lackedSpiritual);
                  existence.markDirty();
               }

               TensuraParticleHelper.addServerParticlesAroundSelf(targetx, ParticleTypes.COMPOSTER, 1.0);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getGreenWave(0.9F, targetx.getBbWidth() * 3.0F, -0.5F, true),
                  targetx.getX(),
                  targetx.getY() + targetx.getBbHeight() * 0.33,
                  targetx.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getGreenWave(0.9F, targetx.getBbWidth() * 3.0F, -0.5F, true),
                  targetx.getX(),
                  targetx.getY() + targetx.getBbHeight() * 0.66,
                  targetx.getZ()
               );
            } else {
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
               double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
               float healingHP = entity.getMaxHealth() - entity.getHealth();
               double lackedMana = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(healingHP * cost));
               if (lackedMana > 0.0) {
                  healingHP = (float)(healingHP - lackedMana / cost);
               }

               entity.heal(healingHP);
               if (instance.isMastered(entity)) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  double healingSpiritual = existence.getSpiritualHealth();
                  double lackedSpiritual = entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) - healingSpiritual;
                  double lackedMP = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedSpiritual * CONFIG.magiculeCostSHP));
                  if (lackedMP > 0.0) {
                     lackedSpiritual -= lackedMP / CONFIG.magiculeCostSHP;
                  }

                  existence.setSpiritualHealth(healingSpiritual + lackedSpiritual);
                  existence.markDirty();
               }

               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 1.0);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getGreenWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.33,
                  entity.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getGreenWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.66,
                  entity.getZ()
               );
            }
            break;
         case 1:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            entity.swing(InteractionHand.MAIN_HAND, true);
            if (target != null) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION))) {
                  target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER, 1.0);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               } else {
                  if (target instanceof Player player && player.getAbilities().invulnerable) {
                     return;
                  }

                  instance.addMasteryPoint(entity);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownInfectionMastered : CONFIG.cooldownInfection, mode);
                  MobEffectInstance infection = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), CONFIG.infectionDuration, 0, true, false, true
                  );
                  TensuraMobEffect.addEffect(target, infection, entity, this, mode);
                  TensuraDamageHelper.markHurt(target, entity);
                  double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                  TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getCrimsonAura(0.2F, (float)size, -0.3F), 5, 0.01);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     10.0F,
                     1.0F
                  );
               }
            }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 2) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      Level level = entity.level();
      level.playSound(
         null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 1.0F
      );
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getCrimsonAura(0.2F, (float)size, -0.3F), 5, 0.01, 5.0F);
      List<LivingEntity> list = level.getEntitiesOfClass(
         LivingEntity.class,
         entity.getBoundingBox().inflate(CONFIG.plagueRadius),
         targetx -> !targetx.is(entity) && targetx.isAlive() && !targetx.isAlliedTo(entity)
      );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
               MobEffectInstance infection = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), CONFIG.infectionDuration, 0, true, false, true
               );
               TensuraMobEffect.addEffect(target, infection, entity, this, mode);
            }
         }
      }

      return true;
   }
}
