package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.field.DeathBlessingField;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.MenuHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LustSkill extends Skill {
   public static final UniqueSkillConfig.Lust CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Lust;

   public LustSkill() {
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
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 4 : 3;
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> instance.isMastered(entity) ? 4 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(3, 4);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "lust.drain";
         case 1 -> "lust.invigorate";
         case 2 -> "lust.rebirth";
         case 3 -> "lust.embracing_drain";
         case 4 -> "lust.death_blessing";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostDrain;
         default -> 0.0;
         case 2 -> CONFIG.magiculeCostRebirth;
         case 3 -> CONFIG.magiculeCostEmbrace;
         case 4 -> CONFIG.magiculeCostBless;
      };
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 4) {
         return false;
      }

      if (instance.getOrCreateTag().getDouble(this.getModeId(instance, mode)) < BASE_CONFIG.Learning.learningPointRequirement) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      DeathBlessingField.getBlessing(CONFIG.blessRadius, CONFIG.blessTime, CONFIG.blessRange, entity, instance, this, mode);
      if (heldTicks > CONFIG.blessTime) {
         instance.setCoolDown(CONFIG.cooldownBless, mode);
         return false;
      }

      if (entity instanceof Player player) {
         int second = CONFIG.blessTime / 20;
         player.displayClientMessage(
            Component.translatable("tensura.skill.time_held.max", new Object[]{heldTicks / 20, second}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
            true
         );
      }

      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 4 && !instance.onCoolDown(mode)) {
         if (TickingSkill.isTickingSkill(entity, this, 4)) {
            instance.setCoolDown(CONFIG.cooldownBless, mode);
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            if (target != null) {
               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return;
               }

               if (EnergyHelper.drainEnergy(target, entity, CONFIG.drainEP, false, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL_EXCEED_MAX)) {
                  if (instance.isMastered(entity)) {
                     EnergyHelper.drainEnergy(target, entity, CONFIG.drainEPMastered, true, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL_EXCEED_MAX);
                  }

                  instance.setCoolDown(CONFIG.cooldownDrain, mode);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
               }
            } else if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_DRAIN))) {
               instance.setCoolDown(CONFIG.cooldownDrain, mode);
               entity.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.LUST_DRAIN),
                     CONFIG.drainDuration,
                     instance.isMastered(entity) ? 1 : 0,
                     false,
                     false,
                     false
                  )
               );
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
            break;
         case 1:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            boolean success;
            if (target != null && entity.isShiftKeyDown()) {
               if (target instanceof Animal animal && entity instanceof Player player && animal.getHealth() == animal.getMaxHealth() && animal.getAge() >= 0) {
                  animal.setAge(0);
                  animal.setInLove(player);
               }

               Predicate<Holder<MobEffect>> predicate = effect -> !effect.is(TensuraTags.MobEffects.SKILL_EFFECT);
               success = TensuraMobEffect.removePredicateEffect(target, predicate, this.getMagiculeCost(entity, instance, mode));
               double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
               float lackedHealth = target.getMaxHealth() - target.getHealth();
               double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedHealth * cost));
               if (lackedMagicule > 0.0) {
                  lackedHealth = (float)(lackedHealth - lackedMagicule / cost);
               }

               target.heal(lackedHealth);
               success = success || lackedHealth > 0.0F;
               if (success) {
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER, 1.0);
               }
            } else {
               Predicate<Holder<MobEffect>> predicate = effect -> !effect.is(TensuraTags.MobEffects.SKILL_EFFECT);
               success = TensuraMobEffect.removePredicateEffect(entity, predicate, this.getMagiculeCost(entity, instance, mode));
               double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
               float lackedHealth = entity.getMaxHealth() - entity.getHealth();
               double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedHealth * cost));
               if (lackedMagicule > 0.0) {
                  lackedHealth = (float)(lackedHealth - lackedMagicule / cost);
               }

               entity.heal(lackedHealth);
               success = success || lackedHealth > 0.0F;
               if (success) {
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 1.0);
               }
            }

            if (success) {
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownInvigorateMastered : CONFIG.cooldownInvigorate, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
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
            }
            break;
         case 2:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            MenuHelper.sendComingSoonMessage(entity);
            break;
         case 3:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT))) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
               return;
            }

            double distance = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false);
            if (target == null) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED), true);
               }

               return;
            }

            if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT)) && !target.hasInfiniteMaterials()) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               instance.addMasteryPoint(entity);
               int embrace = instance.isMastered(entity) ? 1 : 0;
               TensuraMobEffect.addEffect(
                  target,
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT), CONFIG.embraceDuration, embrace, false, false, false
                  ),
                  entity,
                  this,
                  mode
               );
               TensuraMobEffect.addEffect(
                  entity,
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT), CONFIG.embraceDuration, embrace, false, false, false
                  ),
                  entity,
                  this,
                  mode
               );
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               break;
            }

            if (entity instanceof Player owner) {
               owner.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED), true);
            }

            return;
         case 4:
            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            instance.getOrCreateTag().putInt("BlessingID", 0);
            instance.markDirty();
      }
   }
}
