package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.EntityEvent.Add;
import dev.architectury.event.events.common.EntityEvent.LivingHurt;
import io.github.manasmods.manascore.attribute.api.AttributeEvents;
import io.github.manasmods.manascore.attribute.api.AttributeEvents.GlideEvent;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.advancement.ExistenceGainTrigger;
import io.github.manasmods.tensura.advancement.TensuraAdvancements;
import io.github.manasmods.tensura.config.entity.AttributeConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.data.existence.EntityExistenceData;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.goal.AvoidFearedEntityGoal;
import io.github.manasmods.tensura.entity.ai.goal.PassiveMeleeAttackGoal;
import io.github.manasmods.tensura.entity.ai.goal.TensuraOwnerHurtByTargetGoal;
import io.github.manasmods.tensura.entity.ai.goal.TensuraOwnerHurtGoal;
import io.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.menu.NamingMenu;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.tslat.smartbrainlib.api.SmartBrainOwner;

public class AttributeHandler {
   private static WeakReference<Registry<EntityExistenceData>> cachedEntityRegistryRef = new WeakReference<>(null);
   private static Map<ResourceLocation, EntityExistenceData> entityById = Map.of();
   private static List<TensuraSkill> cachedTensuraSkills = null;

   private static Map<ResourceLocation, EntityExistenceData> getEntityById(Registry<EntityExistenceData> registry) {
      Registry<EntityExistenceData> cached = cachedEntityRegistryRef.get();
      if (cached == registry) {
         return entityById;
      }

      Map<ResourceLocation, EntityExistenceData> map = new HashMap<>();

      for (EntityExistenceData data : registry) {
         map.putIfAbsent(data.entity(), data);
      }

      entityById = map;
      cachedEntityRegistryRef = new WeakReference<>(registry);
      return map;
   }

   public static void init() {
      EntityEvent.ADD.register((Add)(entity, level) -> {
         if (((TensuraEntityEvents.AddFreshEvent)TensuraEntityEvents.ADD_FRESH.invoker()).add(entity, level).isFalse()) {
            return EventResult.interruptFalse();
         }

         if (entity instanceof Mob mob && !(mob instanceof SmartBrainOwner)) {
            if (mob instanceof PathfinderMob attacker) {
               if (mob.getAttribute(Attributes.ATTACK_DAMAGE) == null) {
                  boolean addMelee = true;

                  for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) {
                     if (goal.getGoal() instanceof TargetGoal) {
                        addMelee = false;
                        break;
                     }
                  }

                  if (addMelee) {
                     float speed = attacker instanceof Villager ? 0.75F : 1.5F;
                     mob.goalSelector.addGoal(2, new PassiveMeleeAttackGoal(attacker, speed, false));
                  }
               }

               if (!mob.getType().is(TensuraEntityTags.NO_FEAR)) {
                  mob.goalSelector.addGoal(0, new AvoidFearedEntityGoal(attacker, 30.0F, 1.5, 2.0));
               }

               if (mob.getType().is(TensuraEntityTags.MONSTER)) {
                  mob.goalSelector.addGoal(1, new AvoidEntityGoal(attacker, HinataSakaguchiEntity.class, 40.0F, 1.0, 3.0));
               }
            }

            mob.goalSelector.addGoal(1, new TensuraOwnerHurtByTargetGoal(mob));
            mob.goalSelector.addGoal(1, new TensuraOwnerHurtGoal(mob));
         }

         return EventResult.pass();
      });
      TensuraEntityEvents.ADD_FRESH.register((TensuraEntityEvents.AddFreshEvent)(target, level) -> {
         if (level.isClientSide()) {
            return EventResult.pass();
         }

         IExistence existence = (IExistence)target.manasCore$getStorage(ExistenceStorage.getKey());
         if (existence == null) {
            return EventResult.pass();
         }

         if (target.getType().is(TensuraEntityTags.EP_INITIATE_EXCLUDED)) {
            existence.setSkippingEPDrop(false);
            existence.markDirty();
            return EventResult.pass();
         }

         if (target instanceof LivingEntity entity) {
            if (!existence.isSkippingEPDrop()) {
               existence.setAura(Math.min(existence.getAura(), entity.getAttributeValue(TensuraAttributes.MAX_AURA)));
               existence.setMagicule(Math.min(existence.getMagicule(), entity.getAttributeValue(TensuraAttributes.MAX_MAGICULE)));
               existence.markDirty();
               return EventResult.pass();
            }

            updateEntityExistence(entity, level, existence);
            if (existence.isSkippingEPDrop()) {
               double energy = 100.0F * entity.getMaxHealth();
               AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule != null) {
                  magicule.setBaseValue(energy / 2.0);
               }

               AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura != null) {
                  aura.setBaseValue(energy / 2.0);
               }

               AttributeInstance shp = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
               if (shp != null) {
                  shp.setBaseValue(entity.getMaxHealth() * 2.0F);
                  existence.setSpiritualHealth(shp.getBaseValue());
               }

               existence.setSkippingEPDrop(false);
               existence.markDirty();
            }

            return EventResult.pass();
         } else {
            return EventResult.pass();
         }
      });
      TensuraEntityEvents.ATTRIBUTE_BASE_CHANGE_EVENT
         .register(
            (TensuraEntityEvents.AttributeBaseValueChangedEvent)(entity, instance, oldValue, newValue) -> {
               if (oldValue >= newValue) {
                  return EventResult.pass();
               } else if (instance.getAttribute().value() instanceof RangedAttribute attribute && oldValue == attribute.getDefaultValue()) {
                  return EventResult.pass();
               } else {
                  Level level = entity.level();
                  if (level.isClientSide()) {
                     return EventResult.pass();
                  }

                  if (!instance.getAttribute().equals(TensuraAttributes.MAX_AURA) && !instance.getAttribute().equals(TensuraAttributes.MAX_MAGICULE)) {
                     return EventResult.pass();
                  }

                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  int humanKill = TensuraRace.BASE_CONFIG.massNamingHuman;
                  if (entity instanceof ServerPlayer player) {
                     if (!instance.getAttribute().equals(TensuraAttributes.MAX_MAGICULE)) {
                        return EventResult.pass();
                     }

                     if (existence.isNameable()
                        && (
                           player.getStats().getValue(Stats.CUSTOM.get(Stats.RAID_WIN)) >= TensuraRace.BASE_CONFIG.massNamingRaid
                              || existence.getHumanKill() >= humanKill
                        )) {
                        TensuraAdvancements.grant(player, TensuraAdvancements.Basic.INFAMY_FAMOUS);
                        if (existence.getName() == null) {
                           RequestNamingMenuPacket.name(player, null, RequestNamingMenuPacket.NamingType.LOW, player.getName().getString());
                           ((ExistenceGainTrigger)TensuraCriteriaTriggers.EXISTENCE_GAIN.get()).trigger(player);
                           return EventResult.interruptTrue();
                        }
                     }

                     applyDemonLordSeedAndSkills(player, existence, newValue);
                     ((ExistenceGainTrigger)TensuraCriteriaTriggers.EXISTENCE_GAIN.get()).trigger(player);
                     return EventResult.pass();
                  } else {
                     if (oldValue != 0.0) {
                        addStatOnEPGain(entity, existence, newValue - oldValue);
                     }

                     if (existence.getHumanKill() >= humanKill && existence.getName() == null) {
                        RequestNamingMenuPacket.name(entity, null, RequestNamingMenuPacket.NamingType.LOW, NamingMenu.getRandomName(level.getRandom()));
                     }

                     return EventResult.pass();
                  }
               }
            }
         );
      EntityEvents.LIVING_PRE_DAMAGED
         .register(
            (LivingHurt)(entity, source, amount) -> {
               LivingEntity attackerCached = source.getEntity() instanceof LivingEntity la ? la : null;
               if (source.tensura$getResistanceBypassLevel() < 1.0F
                  && attackerCached != null
                  && attackerCached.getAttributeValue(TensuraAttributes.RESISTANCE_DEGRADATION) >= 1.0) {
                  source.tensura$setResistanceBypassLevel(1.0F);
               }

               boolean isPhysical = TensuraDamageHelper.isPhysicalAttack(source);
               if (!isPhysical && !entity.getActiveEffects().isEmpty()) {
                  MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
                  if (instance != null && instance.getAmplifier() >= 1) {
                     source.tensura$setPhysicalConverted();
                     isPhysical = true;
                  }
               }

               if (isPhysical) {
                  if (source.tensura$getResistanceBypassLevel() < 1.0F
                     && attackerCached != null
                     && attackerCached.getAttributeValue(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION) >= 1.0) {
                     source.tensura$setResistanceBypassLevel(1.0F);
                  }

                  if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                     return EventResult.pass();
                  }

                  if (!source.tensura$isDodgeBypass() && !source.is(TensuraTags.DamageTypes.BYPASS_DODGE)) {
                     Entity direct = source.getDirectEntity();
                     if (direct == null || direct != source.getEntity()) {
                        return EventResult.pass();
                     }

                     if (direct.getType().is(TensuraEntityTags.CANNOT_DODGE)) {
                        return EventResult.pass();
                     }

                     double dodge = entity.getAttributeValue(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
                     if (entity.getRandom().nextFloat() * 100.0F <= dodge) {
                        if (direct instanceof LivingEntity attacker) {
                           double negate = attacker.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
                           if (attacker.getRandom().nextFloat() * 100.0F < negate) {
                              return EventResult.pass();
                           }
                        }

                        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 1.0);
                        entity.level()
                           .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
                        return EventResult.interruptFalse();
                     }
                  }
               }

               TensuraDamageHelper.applyElementalResistanceDegradation(source);
               return EventResult.pass();
            }
         );
      EntityEvents.PROJECTILE_HIT
         .register(
            (ProjectileHitEvent)(result, projectile, deflection, hitResultChangeable) -> {
               if (!projectile.getType().is(TensuraEntityTags.CANNOT_DODGE)) {
                  if (result instanceof EntityHitResult hitResult) {
                     if (hitResult.getEntity() instanceof LivingEntity entity) {
                        double var12 = entity.getAttributeValue(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
                        if (entity.getRandom().nextFloat() * 100.0F <= var12) {
                           if (projectile.getOwner() instanceof LivingEntity attacker) {
                              double negate = attacker.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
                              if (attacker.getRandom().nextFloat() * 100.0F < negate) {
                                 return;
                              }
                           }

                           TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 1.0);
                           entity.level()
                              .playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
                              );
                           deflection.set(ProjectileDeflection.NONE);
                           hitResultChangeable.set(ProjectileHitResult.PASS);
                        } else {
                           ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
                           if (data != null && data.getDodgeInvulnerability() > 0) {
                              if (projectile.getOwner() instanceof LivingEntity attacker) {
                                 double negate = attacker.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
                                 if (attacker.getRandom().nextFloat() * 100.0F < negate) {
                                    return;
                                 }
                              }

                              data.setDodgeCooldown(0);
                              data.markDirty();
                              deflection.set(ProjectileDeflection.NONE);
                              hitResultChangeable.set(ProjectileHitResult.PASS);
                              TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 1.0);
                           }
                        }
                     }
                  }
               }
            }
         );
      AttributeEvents.CONTINUE_GLIDE_EVENT.register((GlideEvent)(glider, canGlide) -> {
         if (!(Boolean)canGlide.get() && glider.isFallFlying()) {
            Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(glider).getRace();
            if (optional.isPresent() && optional.get().is(TensuraRaceTags.CAN_GLIDE)) {
               canGlide.set(true);
            }

            return EventResult.pass();
         } else {
            return EventResult.pass();
         }
      });
   }

   public static void updateEntityExistence(LivingEntity entity, LevelAccessor level, IExistence existence) {
      if (entity.getType().is(TensuraEntityTags.MONSTER)) {
         existence.setOriginalAlignment(Alignment.MAJIN);
         existence.setAlignment(Alignment.MAJIN);
         existence.markDirty();
      }

      if (entity.getType().is(TensuraEntityTags.SPIRITUAL) && SkillUtils.inSpiritualWorld(entity.level().dimension()) && existence.getSummonedSecond() <= 0) {
         existence.setSpiritualForm(true);
         existence.markDirty();
      }

      Registry<EntityExistenceData> registry = level.registryAccess().registryOrThrow(TensuraCustomData.ENTITY_EXISTENCE);
      ResourceLocation entityId = entity.getType().arch$registryName();
      EntityExistenceData data = getEntityById(registry).get(entityId);
      if (data != null) {
         initEntityExistence(entity, level, existence, data);
      }
   }

   public static void initEntityExistence(LivingEntity entity, LevelAccessor level, IExistence existence, EntityExistenceData data) {
      boolean reset = existence.getSpawnType() != MobSpawnType.CONVERSION;
      AttributeInstance shp = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
      if (shp != null) {
         if (reset || shp.getBaseValue() < data.spiritualHP()) {
            shp.setBaseValue(data.spiritualHP());
         }

         existence.setSpiritualHealth(shp.getBaseValue());
      }

      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         double maxAura = data.maxAura() <= data.minAura() ? data.minAura() : level.getRandom().nextInt(data.minAura(), data.maxAura());
         if (reset || aura.getBaseValue() < maxAura) {
            aura.setBaseValue(maxAura);
         }

         existence.setAura(maxAura);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         double maxMagicule = data.maxMagicule() <= data.minMagicule() ? data.minMagicule() : level.getRandom().nextInt(data.minMagicule(), data.maxMagicule());
         if (reset || magicule.getBaseValue() < maxMagicule) {
            magicule.setBaseValue(maxMagicule);
         }

         existence.setMagicule(maxMagicule);
      }

      if (data.abilities().isPresent()) {
         for (ResourceLocation ability : data.abilities().get()) {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ability);
            if (skill != null) {
               ManasSkillInstance instance = new TensuraSkillInstance(skill);
               instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
               SkillHelper.learnSkill(entity, instance);
               if (instance.canBeToggled(entity)) {
                  instance.setToggled(true);
                  instance.onToggleOn(entity);
               }
            }
         }
      }

      if (data.abilitiesRandom().isPresent()) {
         for (Entry<ResourceLocation, Double> entry : data.abilitiesRandom().get().entrySet()) {
            if (!(level.getRandom().nextFloat() > entry.getValue())) {
               ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(entry.getKey());
               if (skill != null) {
                  ManasSkillInstance instance = new TensuraSkillInstance(skill);
                  instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                  SkillHelper.learnSkill(entity, instance);
                  if (instance.canBeToggled(entity)) {
                     instance.setToggled(true);
                     instance.onToggleOn(entity);
                  }
               }
            }
         }
      }

      if (existence.isSkippingEPDrop() && ReincarnationMenu.CONFIG.Skills.nonPlayerResistance) {
         ReincarnationMenu.grantLearningResistance(entity);
      }

      existence.setSkippingEPDrop(false);
      existence.markDirty();
   }

   private static void addStatOnEPGain(LivingEntity entity, IExistence existence, double difference) {
      double gainedEP = existence.getGainedEP() + difference;
      double points = (int)gainedEP / 10000;
      if (points > 0.0 && !entity.getType().is(TensuraEntityTags.EP_INITIATE_EXCLUDED)) {
         AttributeConfig config = (AttributeConfig)ConfigRegistry.getConfig(AttributeConfig.class);
         existence.setGainedEP(gainedEP - points * 10000.0);
         AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
         if (armor != null) {
            armor.setBaseValue(armor.getBaseValue() + points * config.armorGain);
         }

         AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
         if (attack != null) {
            attack.setBaseValue(attack.getBaseValue() + points * config.attackGain);
         }

         AttributeInstance HP = entity.getAttribute(Attributes.MAX_HEALTH);
         if (HP != null) {
            HP.setBaseValue(HP.getBaseValue() + points * config.HPGain);
         }

         AttributeInstance SHP = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
         if (SHP != null) {
            SHP.setBaseValue(SHP.getBaseValue() + points * config.SHPGain);
         }

         entity.heal((float)(points * config.HPGain));
      } else {
         existence.setGainedEP(gainedEP);
         entity.heal(TensuraBehaviourHelper.CONFIG.successKillHeal);
      }
   }

   public static void restoreMagiculeOnWakingUp(Player player) {
      if (!player.level().isClientSide()) {
         if (player.level().dimension() == Level.OVERWORLD) {
            if (player.level().isThundering()) {
               IEffect effect = TensuraStorages.getEffectFrom(player);
               if (!effect.isSleptAtThunderNight()) {
                  return;
               }

               effect.setSleptAtThunderNight(false);
            }

            if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
               player.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
            }

            IExistence existence = TensuraStorages.getExistenceFrom(player);
            existence.setSpiritualHealth(player.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
            double maxAP = EnergyHelper.getMaxAura(player);
            if (existence.getAura() < maxAP) {
               existence.setAura(Math.min(existence.getAura() + Math.round(maxAP * EnergyHelper.CONFIG.wakeUpAura), maxAP));
            }

            double maxMP = EnergyHelper.getMaxMagicule(player);
            if (existence.getMagicule() < maxMP) {
               existence.setMagicule(Math.min(existence.getMagicule() + Math.round(maxMP * EnergyHelper.CONFIG.wakeUpMagicule), maxMP));
            }

            existence.markDirty();
         }
      }
   }

   public static void applyDemonLordSeedAndSkills(Player player, IExistence existence, double newValue) {
      double newEP = newValue + player.getAttributeBaseValue(TensuraAttributes.MAX_AURA);
      if (existence.getAlignment().isCanBecomeDemonLord()
         && !existence.isTrueHero()
         && !existence.isTrueDemonLord()
         && !existence.isDemonLordSeed()
         && newEP >= player.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_SEED)) {
         existence.setDemonLordSeed(true);
         existence.markDirty();
         player.sendSystemMessage(Component.translatable("tensura.evolve.demon_lord.seed").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      Skills skills = SkillAPI.getSkillsFrom(player);

      for (TensuraSkill skill : getTensuraSkillsCached()) {
         if (skill.checkAcquiringRequirement(player, newEP) && skills.getSkill(skill).isEmpty()) {
            SkillHelper.learnSkill(player, skill.createLearningInstance(player));
         }
      }
   }

   private static List<TensuraSkill> getTensuraSkillsCached() {
      if (cachedTensuraSkills == null) {
         List<TensuraSkill> list = new ArrayList<>();

         for (ManasSkill manasSkill : SkillAPI.getSkillRegistry()) {
            if (manasSkill instanceof TensuraSkill skill) {
               list.add(skill);
            }
         }

         cachedTensuraSkills = Collections.unmodifiableList(list);
      }

      return cachedTensuraSkills;
   }
}
