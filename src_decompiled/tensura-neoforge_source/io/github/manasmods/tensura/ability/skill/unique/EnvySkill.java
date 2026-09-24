package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnvySkill extends Skill {
   private static final UniqueSkillConfig.Envy CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Envy;
   protected static final ResourceLocation ENVY = ResourceLocation.fromNamespaceAndPath("tensura", "envy");

   public EnvySkill() {
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
         case 0 -> "envy.absorb";
         case 1 -> "envy.strength_sap";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostSap : 0.0;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 240, CONFIG.luckLevel - 1, false, false, false));
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
      if (!instance.isToggled()) {
         return true;
      } else {
         return ((MobEffectInstance)effect.get()).is(MobEffects.WEAKNESS) ? false : !((MobEffectInstance)effect.get()).is(MobEffects.MOVEMENT_SLOWDOWN);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(MobEffects.WEAKNESS);
      entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(ENVY, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(ENVY, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(ENVY);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(ENVY);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 1) {
         return false;
      }

      if (heldTicks > 0 && heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0) {
         instance.addMasteryPoint(entity);
      }

      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 5, 0.01, 3.0F);
      if (heldTicks % 20 == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % 200 == 0 && entity.getRandom().nextFloat() <= CONFIG.insanityChance / 100.0F) {
            int level = CONFIG.insanityLevel;
            MobEffectInstance insanity = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY));
            if (insanity != null) {
               level = insanity.getAmplifier() + CONFIG.insanityLevel;
            }

            entity.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), CONFIG.insanityDuration, level, true, false, true)
            );
         }

         List<LivingEntity> list = entity.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               entity.getBoundingBox().inflate(CONFIG.sapRadius),
               living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
            );
         if (!list.isEmpty()) {
            double ownerEP = EnergyHelper.getMaxEP(entity);

            for (LivingEntity target : list) {
               if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
                  double targetEP = EnergyHelper.getMaxEP(target);
                  double difference = (instance.isMastered(entity) ? CONFIG.sapEPMastered : CONFIG.sapEP) - targetEP / ownerEP;
                  if (!(difference < 0.0)) {
                     int level = 1 + (int)(difference / CONFIG.epDifferenceMultiplier);
                     target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CONFIG.sapDuration, level));
                     target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, CONFIG.sapDuration, level));
                     EnergyHelper.drainEnergy(target, entity, CONFIG.epDrain, true, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NONE);
                  }
               }
            }
         }
      }

      return true;
   }

   private boolean failedAbsorb(LivingEntity entity, double EP, double targetEP) {
      if (targetEP > EP * 10.0) {
         return entity.getRandom().nextFloat() >= 0.1;
      } else {
         return targetEP >= EP * 5.0 ? entity.getRandom().nextFloat() >= 0.2 : entity.getRandom().nextFloat() >= 0.3;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 1) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            Level level = entity.level();
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            if (target != null && target.isAlive()) {
               if (target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return;
               }

               double EP = EnergyHelper.getMaxEP(entity);
               double targetEP = EnergyHelper.getEPGain(target, entity);
               if (targetEP < EP) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  return;
               }

               instance.addMasteryPoint(entity);
               if (this.failedAbsorb(entity, EP, targetEP)) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                  }

                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  return;
               }

               if (!TensuraGameRules.canEpSteal(level) && target instanceof Player) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  return;
               }

               double difference = Math.min(target.getRandom().nextFloat() >= 0.4 ? (targetEP - EP) / 2.0 : targetEP - EP, EnergyHelper.CONFIG.maximumEPSteal);
               boolean success = EnergyHelper.drainEnergy(target, entity, difference / 2.0, false, EnergyHelper.DrainType.MAX_AURA, EnergyHelper.GainType.NONE);
               success = EnergyHelper.drainEnergy(target, entity, difference / 2.0, false, EnergyHelper.DrainType.MAX_MAGICULE, EnergyHelper.GainType.MAX)
                  || success;
               if (success) {
                  EnergyHelper.gainAura(entity, difference / 4.0, EnergyHelper.GainType.NORMAL);
                  EnergyHelper.gainMagicule(entity, difference / 4.0, EnergyHelper.GainType.NORMAL);
                  instance.setCoolDown(instance.isMastered(entity) ? 10 : 30, mode);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 1.0);
               } else {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            } else {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            }
         }
      }
   }
}
