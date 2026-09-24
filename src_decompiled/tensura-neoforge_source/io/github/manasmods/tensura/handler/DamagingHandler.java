package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent.LivingHurt;
import io.github.manasmods.manascore.attribute.api.AttributeEvents;
import io.github.manasmods.manascore.attribute.api.AttributeEvents.CriticalAttackChanceEvent;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingDamageEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillDamageCalculationEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.aspectual.barrier.AntiShockAreaMagic;
import io.github.manasmods.tensura.ability.skill.extra.MultilayerBarrierSkill;
import io.github.manasmods.tensura.ability.skill.unique.GuardianSkill;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.enchantment.EngravingHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.human.golem.TrainingDummyEntity;
import io.github.manasmods.tensura.entity.magic.barrier.RangedBarrierEntity;
import io.github.manasmods.tensura.entity.template.subclass.IOtherworlder;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.weapon.custom.VortexSpearItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public class DamagingHandler {
   private static final Holder<Attribute>[] ELEMENT_BOOST = elementMap(
      Element.DARKNESS,
      TensuraAttributes.DARKNESS_BOOST,
      Element.EARTH,
      TensuraAttributes.EARTH_BOOST,
      Element.FLAME,
      TensuraAttributes.FLAME_BOOST,
      Element.LIGHT,
      TensuraAttributes.LIGHT_BOOST,
      Element.SPACE,
      TensuraAttributes.SPACE_BOOST,
      Element.WATER,
      TensuraAttributes.WATER_BOOST,
      Element.WIND,
      TensuraAttributes.WIND_BOOST
   );
   private static final Holder<Attribute>[] ELEMENT_RESIST = elementMap(
      Element.DARKNESS,
      TensuraAttributes.DARKNESS_RESISTANCE,
      Element.EARTH,
      TensuraAttributes.EARTH_RESISTANCE,
      Element.FLAME,
      TensuraAttributes.FLAME_RESISTANCE,
      Element.LIGHT,
      TensuraAttributes.LIGHT_RESISTANCE,
      Element.SPACE,
      TensuraAttributes.SPACE_RESISTANCE,
      Element.WATER,
      TensuraAttributes.WATER_RESISTANCE,
      Element.WIND,
      TensuraAttributes.WIND_RESISTANCE
   );

   private static Holder<Attribute>[] elementMap(Object... pairs) {
      Holder<Attribute>[] arr = new Holder[Element.values().length];

      for (int i = 0; i < pairs.length; i += 2) {
         arr[((Element)pairs[i]).ordinal()] = (Holder<Attribute>)pairs[i + 1];
      }

      return arr;
   }

   private static boolean hasDamageActionEffect(LivingEntity entity) {
      for (MobEffectInstance instance : entity.getActiveEffects()) {
         if (instance.getEffect().value() instanceof DamageAction) {
            return true;
         }
      }

      return false;
   }

   public static void init() {
      EntityEvents.LIVING_ON_BEING_DAMAGED.register((LivingHurt)(target, source, amount) -> {
         if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (SkillUtils.shouldCancelInteraction(target, true)) {
               return EventResult.interruptFalse();
            }

            if (source.getDirectEntity() instanceof LivingEntity entity && SkillUtils.shouldCancelInteraction(entity)) {
               return EventResult.interruptFalse();
            }
         }

         IExistence existence = TensuraStorages.getExistenceFrom(target);
         if (existence.isSpiritualForm() && !SkillUtils.inSpiritualWorld(target.level().dimension()) && TensuraDamageHelper.isPhysicalAttack(source)) {
            return EventResult.interruptFalse();
         }

         if (TensuraGameRules.isLabyrinthPvpOff(target.level())) {
            if (source.is(DamageTypeTags.IS_FIRE)) {
               return EventResult.interruptFalse();
            }

            if (TensuraGameRules.isLabyrinthPvpOff(target.level(), target, source.getEntity())) {
               return EventResult.interruptFalse();
            }
         }

         if ((source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypes.FLY_INTO_WALL)) && VortexSpearItem.onHit(target, null, target.position())) {
            return EventResult.interruptFalse();
         }

         if (triggerDodge(target, source)) {
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SWEEP_ATTACK, 1.0);
            return EventResult.interruptFalse();
         }

         if (source.getEntity() instanceof LivingEntity attacker) {
            IExistence attackerExistence = TensuraStorages.getExistenceFrom(attacker);
            if (!source.isCreativePlayer()) {
               if (attackerExistence.isTargetNeutral(target.getUUID())) {
                  return EventResult.interruptFalse();
               }

               if (SubordinateHelper.isSubordinate(target, attacker, attackerExistence)) {
                  return EventResult.interruptFalse();
               }

               LivingEntity targetOwner = SubordinateHelper.getSubordinateOwner(target);
               if (targetOwner != null && SubordinateHelper.isSubordinate(targetOwner, attacker, attackerExistence)) {
                  return EventResult.interruptFalse();
               }
            }

            if (existence.isTargetNeutral(attacker.getUUID())) {
               existence.removeNeutralTarget(attacker.getUUID());
               existence.markDirty();
            }
         }

         return EventResult.pass();
      });
      SkillEvents.SKILL_DAMAGE_PRE_CALCULATION.register((SkillDamageCalculationEvent)(storage, entity, source, amount) -> {
         applyElementalBoost(source, amount);
         if (!GuardianSkill.onSubordinateHurt(entity, source, amount)) {
            return EventResult.interruptFalse();
         }

         if (hasDamageActionEffect(entity)) {
            for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
               if (instance.getEffect().value() instanceof DamageAction effect && !effect.onBeingDamaged(entity, source, amount)) {
                  return EventResult.interruptFalse();
               }
            }
         }

         if (source.getEntity() instanceof LivingEntity attacker) {
            if (hasDamageActionEffect(attacker)) {
               for (MobEffectInstance instance : List.copyOf(attacker.getActiveEffects())) {
                  if (instance.getEffect().value() instanceof DamageAction effect && !effect.onDamagingEntity(attacker, entity, source, amount)) {
                     return EventResult.interruptFalse();
                  }
               }
            }

            if (attacker.isFallFlying() && VortexSpearItem.onHit(attacker, entity, entity.position())) {
               double d7 = attacker.getDeltaMovement().horizontalDistance();
               amount.set((Float)amount.get() + (float)(d7 * 10.0));
            }
         }

         applyResistanceAttributes(entity, source, amount);
         applyEnervation(entity, source, amount);
         applyMagicInterference(entity, source, amount);
         if (TensuraDamageHelper.isUndeadPurifying(source) && RaceUtils.isUndead(entity)) {
            amount.set((Float)amount.get() * 3.0F);
         }

         return EventResult.pass();
      });
      SkillEvents.SKILL_DAMAGE_CALCULATION
         .register(
            (SkillDamageCalculationEvent)(storage, entity, source, amount) -> {
               if (source.is(TensuraTags.DamageTypes.BYPASS_BARRIER)) {
                  return EventResult.pass();
               }

               AttributeInstance instance = entity.getAttribute(TensuraAttributes.MULTILAYER_BARRIER);
               if (instance == null) {
                  return EventResult.pass();
               }

               double barrier = instance.getValue();
               if (barrier > 0.0) {
                  if (source.getEntity() instanceof LivingEntity attacker && RangedBarrierEntity.shouldInstaBreak(attacker, entity)) {
                     instance.removeModifiers();
                     entity.level()
                        .playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     return EventResult.pass();
                  }

                  float ignoredDamage = Math.min((Float)amount.get() * source.tensura$getBarrierBypassLevel(), (Float)amount.get());
                  float bypassDamage = (float)Math.max((Float)amount.get() - ignoredDamage - barrier, 0.0);
                  if (bypassDamage > 0.0F) {
                     instance.removeModifiers();
                     entity.level()
                        .playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  }

                  float takenDamage = bypassDamage + ignoredDamage;
                  double mpCost = ((Float)amount.get() - takenDamage) * MultilayerBarrierSkill.CONFIG.magiculeCost;
                  double lackedMP = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)mpCost);
                  if (lackedMP > 0.0) {
                     takenDamage = (float)(takenDamage + lackedMP / MultilayerBarrierSkill.CONFIG.magiculeCost);
                     instance.removeModifiers();
                     entity.level()
                        .playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  }

                  amount.set(takenDamage);
               }

               return EventResult.pass();
            }
         );
      EntityEvents.LIVING_DAMAGE
         .register(
            (LivingDamageEvent)(entity, source, amount) -> {
               if (!entity.getActiveEffects().isEmpty()
                  && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SHOCK))
                  && TensuraDamageHelper.isPhysicalAttack(source)) {
                  amount.set(Math.min(AntiShockAreaMagic.CONFIG.antiShockDamage, (Float)amount.get()));
               }

               return EventResult.pass();
            }
         );
      TensuraEntityEvents.LIVING_POST_DAMAGE
         .register(
            (TensuraEntityEvents.LivingPostDamageEvent)(entity, source, amount) -> {
               if (TensuraDamageHelper.isSeveranceDamage(source, entity, false)) {
                  AbilityConfig.Misc misc = TensuraSkill.BASE_CONFIG.Misc;
                  IEffect effect = TensuraStorages.getEffectFrom(entity);
                  int removeSecond = misc.severanceRemoveSec;
                  if (removeSecond != 0) {
                     effect.setSeveranceRemoveTime(removeSecond);
                     effect.increaseSeveranceAmount(amount * misc.severanceMultiplier);
                     effect.markDirty();
                  }
               }

               if (hasDamageActionEffect(entity)) {
                  for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
                     if (instance.getEffect().value() instanceof DamageAction effect) {
                        effect.onPostBeingDamaged(entity, source, amount);
                     }
                  }
               }

               if (!entity.getType().is(TensuraEntityTags.EP_DROP_EXCLUDED)) {
                  ManasSkillInstance sourceSkill = source.tensura$getAbilityInstance();
                  if (sourceSkill != null && !sourceSkill.isTemporarySkill()) {
                     if (TensuraStorages.getExistenceFrom(entity).getSpawnType() != MobSpawnType.MOB_SUMMONED) {
                        if (source.getEntity() instanceof LivingEntity attacker) {
                           Skills skills = SkillAPI.getSkillsFrom(attacker);
                           Optional<ManasSkillInstance> optional = skills.getSkill(sourceSkill.getSkill());
                           if (!optional.isEmpty() && !optional.get().isMastered(attacker)) {
                              optional.get()
                                 .addMasteryPoint(
                                    attacker,
                                    attacker.getAttributeValue(TensuraAttributes.ABILITY_MASTERY_GAIN) * TensuraSkill.BASE_CONFIG.Mastery.masteryHitMultiplier
                                 );
                              skills.markDirty();
                           }
                        }
                     }
                  }
               }
            }
         );
      AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.register((CriticalAttackChanceEvent)(attacker, target, originalMultiplier, multiplier, chance) -> {
         if (!((Double)chance.get() <= 0.0) && target instanceof LivingEntity entity) {
            return SkillUtils.shouldCancelCriticalChance(entity) ? EventResult.interruptFalse() : EventResult.pass();
         } else {
            return EventResult.pass();
         }
      });
      EntityEvents.PROJECTILE_HIT.register((ProjectileHitEvent)(result, projectile, deflectionChangeable, hitResultChangeable) -> {
         if (result instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof LivingEntity target) {
            if (SkillUtils.shouldCancelInteraction(target, true)) {
               hitResultChangeable.set(ProjectileHitResult.PASS);
               return;
            }

            if (target instanceof IOtherworlder entity) {
               entity.onProjectileImpact(hitResult, projectile, deflectionChangeable, hitResultChangeable);
            }
         }
      });
      TensuraEntityEvents.SPIRITUAL_HURT_EVENT
         .register(
            (TensuraEntityEvents.SpiritualHurtEvent)(target, attacker, originalAmount, resistPercentage, amount, source) -> {
               MobEffectInstance instance = target.getActiveEffects().isEmpty()
                  ? null
                  : target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
               if (instance != null && instance.getAmplifier() >= 1) {
                  DamageSource damageSource = (DamageSource)source.get();
                  if (damageSource == null) {
                     damageSource = TensuraDamageTypes.getEntityDamageSource(target.level(), TensuraDamageTypes.SOUL_SCATTER, attacker);
                  }

                  if (target.hurt(damageSource.tensura$setPhysicalConverted(), originalAmount)) {
                     target.invulnerableTime = 0;
                  }

                  return EventResult.interruptFalse();
               } else if (target instanceof TrainingDummyEntity dummy) {
                  Predicate<ServerPlayer> predicate = player -> player.distanceToSqr(dummy) <= 256.0 || player == attacker;
                  TensuraParticleHelper.spawnServerParticles(
                     dummy.level(),
                     dummy.getDamageParticle((DamageSource)source.get(), (Float)amount.get(), 1),
                     dummy.getX(),
                     dummy.getY() + dummy.getBbHeight() * 1.625F,
                     dummy.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true,
                     predicate
                  );
                  target.level().playSound(null, target, SoundEvents.ARMOR_STAND_HIT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  TensuraDamageHelper.markHurt(target, attacker);
                  return EventResult.interruptFalse();
               } else {
                  return EventResult.pass();
               }
            }
         );
   }

   private static boolean triggerDodge(LivingEntity target, DamageSource source) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(target);
      if (data == null) {
         return false;
      }

      if (data.getDodgeInvulnerability() <= 0) {
         return false;
      }

      if (source.tensura$isDodgeBypass()) {
         return false;
      }

      if (source.is(TensuraTags.DamageTypes.BYPASS_DODGE)) {
         return false;
      }

      if (source.getDirectEntity() != null && source.getDirectEntity() != target) {
         if (source.getEntity() instanceof LivingEntity attacker) {
            double negate = attacker.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
            return attacker.getRandom().nextFloat() * 100.0F >= negate;
         } else {
            data.setDodgeCooldown(0);
            data.markDirty();
            return true;
         }
      } else {
         return false;
      }
   }

   private static void applyElementalBoost(DamageSource source, Changeable<Float> amount) {
      if (source.getEntity() instanceof LivingEntity attacker) {
         if (TensuraDamageHelper.isSoundDamage(source)) {
            amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.SOUND_BOOST));
         } else if (!TensuraDamageHelper.isTensuraMagic(source)) {
            Element element = source.tensura$getElement();
            if (element != null) {
               Holder<Attribute> boost = ELEMENT_BOOST[element.ordinal()];
               if (boost != null) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(boost));
               }
            } else {
               if (TensuraDamageHelper.isGravityDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.GRAVITY_BOOST));
               } else if (TensuraDamageHelper.isLightningDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.LIGHTNING_BOOST));
               } else if (TensuraDamageHelper.isEarthDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.EARTH_BOOST));
               } else if (TensuraDamageHelper.isDarkDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.DARKNESS_BOOST));
               } else if (TensuraDamageHelper.isFireDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.FLAME_BOOST));
               } else if (TensuraDamageHelper.isLightDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.LIGHT_BOOST));
               } else if (TensuraDamageHelper.isSpatialDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.SPACE_BOOST));
               } else if (TensuraDamageHelper.isWaterDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.WATER_BOOST));
               } else if (TensuraDamageHelper.isWindDamage(source)) {
                  amount.set((Float)amount.get() * (float)attacker.getAttributeValue(TensuraAttributes.WIND_BOOST));
               }
            }
         }
      }
   }

   private static void applyResistanceAttributes(LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (source.tensura$getAbilityInstance() != null) {
         double interference = target.getAttributeValue(TensuraAttributes.MAGIC_INTERFERENCE);
         if (source.getEntity() instanceof LivingEntity attacker) {
            if (interference > 0.0 && attacker.getAttributeValue(TensuraAttributes.RESISTANCE_DEGRADATION) < 1.0) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)interference);
            }
         } else if (interference > 0.0) {
            amount.set((Float)amount.get() - (Float)amount.get() * (float)interference);
         }
      }

      if (TensuraDamageHelper.isTensuraMagic(source)) {
         amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.MAGIC_RESISTANCE));
      } else {
         Element element = source.tensura$getElement();
         if (element != null) {
            Holder<Attribute> resist = ELEMENT_RESIST[element.ordinal()];
            if (resist != null) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(resist));
            }
         } else {
            if (TensuraDamageHelper.isEarthDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.EARTH_RESISTANCE));
            } else if (TensuraDamageHelper.isDarkDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.DARKNESS_RESISTANCE));
            } else if (TensuraDamageHelper.isFireDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.FLAME_RESISTANCE));
            } else if (TensuraDamageHelper.isLightDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.LIGHT_RESISTANCE));
            } else if (TensuraDamageHelper.isSpatialDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.SPACE_RESISTANCE));
            } else if (TensuraDamageHelper.isWaterDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.WATER_RESISTANCE));
            } else if (TensuraDamageHelper.isWindDamage(source)) {
               amount.set((Float)amount.get() - (Float)amount.get() * (float)target.getAttributeValue(TensuraAttributes.WIND_RESISTANCE));
            }
         }
      }
   }

   private static void applyEnervation(LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (TensuraDamageHelper.isPhysicalAttack(source) || TensuraDamageHelper.isTensuraMagic(source)) {
         float barrierLevel = 0.0F;

         for (ItemStack slot : target.getArmorSlots()) {
            int level = TensuraEnchantmentHelper.getEnchantmentLevel(target.level(), TensuraEnchantments.ENERVATION, slot);
            if (level > 0) {
               barrierLevel += level;
            } else {
               barrierLevel += TensuraEnchantmentHelper.getEnchantmentLevel(target.level(), TensuraEnchantments.VITALITY, slot);
            }
         }

         if (barrierLevel <= 0.0F) {
            return;
         }

         amount.set((Float)amount.get() - barrierLevel * EngravingHelper.CONFIG.enervationBarrier);
      }
   }

   private static void applyMagicInterference(LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         if (TensuraDamageHelper.isTensuraMagic(source)) {
            if (!(source.getEntity() instanceof LivingEntity attacker && attacker.getAttributeValue(TensuraAttributes.RESISTANCE_DEGRADATION) >= 1.0)) {
               float interferenceLevel = TensuraEnchantmentHelper.getArmorEnchantmentLevel(TensuraEnchantments.MAGIC_INTERFERENCE, target);
               if (target.isBlocking()
                  && TensuraEnchantmentHelper.getEnchantmentLevel(target.level(), TensuraEnchantments.MAGIC_INTERFERENCE, target.getUseItem()) > 0) {
                  interferenceLevel++;
               }

               interferenceLevel = Math.max(
                  EngravingHelper.CONFIG.magicInterferenceMitigation * interferenceLevel, (float)target.getAttributeValue(TensuraAttributes.MAGIC_INTERFERENCE)
               );
               if (interferenceLevel > 0.0F) {
                  if (source.getEntity() instanceof LivingEntity attacker
                     && TensuraStorages.getExistenceFrom(attacker).getMagicule() >= TensuraStorages.getExistenceFrom(target).getMagicule() * 1.5) {
                     return;
                  }

                  amount.set(Math.max(0.0F, (Float)amount.get() * (1.0F - interferenceLevel)));
               }
            }
         }
      }
   }
}
