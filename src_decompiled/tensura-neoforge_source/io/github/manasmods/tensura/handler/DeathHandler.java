package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent.LivingDeath;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.block.LabyrinthPortal;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.effect.ability.InstantRegenerationEffect;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.enchantment.EngravingHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.entity.projectile.SpearProjectile;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DeathHandler {
   public static void init() {
      EntityEvents.DEATH_EVENT_FIRST
         .register(
            (LivingDeath)(entity, source) -> {
               Level level = entity.level();
               if (Float.isNaN(entity.getHealth()) || entity.getHealth() < 0.0F) {
                  entity.setHealth(0.0F);
                  if (!(entity instanceof Player)) {
                     entity.discard();
                  }

                  return EventResult.interruptFalse();
               } else {
                  if (InstantRegenerationEffect.canStopDeath(source, entity)) {
                     entity.setHealth(0.01F);
                     return EventResult.interruptFalse();
                  }

                  if (entity.getType().is(TensuraEntityTags.SPIRIT_PROTECTOR)) {
                     if (source.getEntity() instanceof LivingEntity living && living != entity) {
                        ElementalColossusEntity.markAsPassed(entity, living, false, true);
                     } else if (entity.getLastHurtByMob() != null && entity != entity.getLastHurtByMob()) {
                        ElementalColossusEntity.markAsPassed(entity, entity.getLastHurtByMob(), false, true);
                     }
                  } else if (level.dimension().equals(TensuraDimensions.LABYRINTH)) {
                     if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                        && !entity.getType().is(TensuraEntityTags.CAN_DIE_IN_LABYRINTH)
                        && !level.getGameRules().getBoolean(TensuraGameRules.LABYRINTH_DEATH)
                        && !ExistenceStorage.isSummon(entity)) {
                        entity.setHealth(0.01F);
                        if (level instanceof ServerLevel serverLevel) {
                           entity.invulnerableTime = 100;
                           entity.removeAllEffects();
                           if (source.getEntity() instanceof LivingEntity protector && protector.getType().is(TensuraEntityTags.SPIRIT_PROTECTOR)) {
                              ElementalColossusEntity.markAsPassed(protector, entity, true, false);
                           } else if (entity.getLastHurtByMob() != null && entity.getLastHurtByMob().getType().is(TensuraEntityTags.SPIRIT_PROTECTOR)) {
                              ElementalColossusEntity.markAsPassed(entity.getLastHurtByMob(), entity, true, false);
                           } else {
                              entity.changeDimension(LabyrinthPortal.getOverworldTransition(entity, serverLevel.getServer().overworld()));
                           }
                        }

                        return EventResult.interruptFalse();
                     }

                     LabyrinthPortal.switchGameMode(entity, true);
                  }

                  if (source.getEntity() instanceof LivingEntity attacker) {
                     for (MobEffectInstance effectInstance : attacker.getActiveEffects()) {
                        if (effectInstance.getEffect().value() instanceof DamageAction effect && !effect.onKillEntity(attacker, entity, source)) {
                           return EventResult.interruptFalse();
                        }
                     }

                     IExistence existence = TensuraStorages.getExistenceFrom(entity);
                     if (Objects.equals(existence.getPermanentOwner(), attacker.getUUID())) {
                        existence.setPermanentOwner(null);
                        existence.markDirty();
                     }
                  }

                  return EventResult.pass();
               }
            }
         );
      EntityEvents.DEATH_EVENT_NORMAL.register((LivingDeath)(entity, source) -> {
         if (entity.isAlive()) {
            return EventResult.pass();
         }

         TensuraStorages.resetEffect(entity);
         if (entity.getType().is(TensuraEntityTags.EP_DROP_EXCLUDED)) {
            return EventResult.pass();
         }

         LivingEntity attacker = getEntityGainingEP(entity, source);
         if (attacker == null) {
            return EventResult.pass();
         }

         double EP = EnergyHelper.getEPGain(entity, attacker, false);
         if (EP <= 0.0) {
            return EventResult.pass();
         }

         if (TensuraStorages.getExistenceFrom(entity).isSkippingEPDrop()) {
            return EventResult.pass();
         }

         float multiplier = entity.level().getGameRules().getInt(TensuraGameRules.EP_GAIN_MULTIPLIER);
         double epGain = EnergyHelper.getEPGain(entity, attacker, true);
         gainEPEntity(attacker, entity, epGain * multiplier);
         if (attacker.getType().is(TensuraEntityTags.CLONES)) {
            return EventResult.pass();
         }

         for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            gearGetEP(attacker, equipmentSlot, EP * multiplier);
         }

         if (source.getDirectEntity() instanceof AbstractArrow arrow) {
            gearGetEP(attacker, arrow, EP * multiplier);
         }

         return EventResult.pass();
      });
      EntityEvents.DEATH_EVENT_LAST
         .register(
            (LivingDeath)(entity, source) -> {
               if (entity.level().isClientSide()) {
                  return EventResult.pass();
               }

               if (entity.isAlive()) {
                  return EventResult.pass();
               }

               LivingEntity owner = SubordinateHelper.getSubordinateOwner(entity);
               if (owner != null) {
                  onSubordinateDeath(owner, entity, source);
               }

               if (source.is(TensuraDamageTypes.MAGICULE_POISON)) {
                  RaceHelper.applyMajinChance(entity);
               }

               if (entity instanceof Player player) {
                  RaceAPI.getRaceFrom(player).getRace().ifPresent(raceInstance -> BoneGolemVariant.removeBoneGolemFromRace(player, raceInstance));
               }

               if (entity instanceof Mob mob
                  && mob.getTarget() != null
                  && mob.getTarget() instanceof DwarfEntity dwarf
                  && !dwarf.isTame()
                  && source.getEntity() instanceof Player attacker
                  && !Objects.equals(SubordinateHelper.getSubordinateOwnerUUID(mob), attacker.getUUID())) {
                  ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(attacker);
                  double reputation = data.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
                  if (reputation > DwarfEntity.REPUTATION_CONFIG.minReputation) {
                     data.setReputation(
                        (EntityType<?>)HumanEntityTypes.DWARF.get(),
                        Math.min(reputation + DwarfEntity.REPUTATION_CONFIG.saveHelpPoint, DwarfEntity.REPUTATION_CONFIG.maxReputation)
                     );
                     data.markDirty();
                  }
               }

               ManasSkillInstance instance = source.tensura$getAbilityInstance();
               if (instance != null) {
                  if (source.is(TensuraDamageTypes.FEAR) && instance.is(TensuraSkillTags.HAS_MAGICULE_RICH_HAKI)) {
                     RaceHelper.applyMajinChance(entity);
                  }

                  if (source.getEntity() instanceof LivingEntity attacker) {
                     addStatistic(entity, attacker, source);
                     if (entity.getType().is(TensuraEntityTags.EP_DROP_EXCLUDED)) {
                        return EventResult.pass();
                     }

                     if (instance.isTemporarySkill()) {
                        return EventResult.pass();
                     }

                     if (TensuraStorages.getExistenceFrom(entity).getSpawnType() == MobSpawnType.MOB_SUMMONED) {
                        return EventResult.pass();
                     }

                     Skills skills = SkillAPI.getSkillsFrom(attacker);
                     Optional<ManasSkillInstance> optional = skills.getSkill(instance.getSkill());
                     if (optional.isEmpty() || optional.get().isMastered(attacker)) {
                        return EventResult.pass();
                     }

                     optional.get()
                        .addMasteryPoint(
                           attacker,
                           attacker.getAttributeValue(TensuraAttributes.ABILITY_MASTERY_GAIN) * TensuraSkill.BASE_CONFIG.Mastery.masteryKillMultiplier
                        );
                     skills.markDirty();
                  }
               } else if (source.getEntity() instanceof LivingEntity attacker) {
                  addStatistic(entity, attacker, source);
               }

               return EventResult.pass();
            }
         );
   }

   @Nullable
   private static LivingEntity getEntityGainingEP(LivingEntity target, DamageSource source) {
      Entity pAttacker = source.getEntity();
      if (pAttacker == target) {
         return null;
      }

      if (pAttacker instanceof CloneEntity clone) {
         pAttacker = clone.getOwner();
      }

      if (pAttacker instanceof LivingEntity living) {
         if (living instanceof CloneEntity clone) {
            return clone.getOwner();
         } else {
            if (living.getType().equals(EntityType.PLAYER)) {
               return living;
            }

            IExistence existence = TensuraStorages.getExistenceFrom(living);
            UUID summoner = existence.getSummoner();
            return summoner != null && living.level() instanceof ServerLevel level && level.getEntity(summoner) instanceof LivingEntity summonerEntity
               ? summonerEntity
               : living;
         }
      } else {
         return target.getLastAttacker();
      }
   }

   public static void gainEPEntity(LivingEntity entity, LivingEntity target, double totalEP) {
      double maxMP = entity.level().getGameRules().getInt(TensuraGameRules.MAX_MP_GAIN);
      double maxAP = entity.level().getGameRules().getInt(TensuraGameRules.MAX_AP_GAIN);
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      Alignment alignment = existence.getAlignment();
      if (!target.getType().is(TensuraEntityTags.SOUL_DROP_EXCLUDED)) {
         if (SubordinateHelper.getSubordinateOwner(entity) instanceof Player owner && entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
            IExistence ownerExistence = TensuraStorages.getExistenceFrom(owner);
            if (ownerExistence.isDemonLordSeed()) {
               double souls = EnergyHelper.getEPGain(target, owner, false) * TensuraRace.BASE_CONFIG.epToSoulRate / 100.0;
               souls = Math.min(souls, maxMP + maxAP);
               ownerExistence.setSoulPoints((int)Math.min(ownerExistence.getSoulPoints() + souls, 2.147483647E9));
               if (owner.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE) && ownerExistence.getSoulPoints() >= 20000000) {
                  SkillHelper.learnSkill(owner, (ManasSkill)UniqueSkills.MERCILESS.get());
               }

               int soulReq = Math.max(
                  entity.level().getGameRules().getInt(TensuraGameRules.FORCE_HARVEST_FESTIVAL),
                  entity.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN)
               );
               if (ownerExistence.getSoulPoints() / 1000 >= soulReq && !RaceUtils.isAlreadyAwakened(entity, ownerExistence)) {
                  ExistenceStorage.enterHarvestFestival(ownerExistence, owner);
               }

               ownerExistence.markDirty();
            }
         } else if (existence.isDemonLordSeed()) {
            double souls = EnergyHelper.getEPGain(target, entity, false) * TensuraRace.BASE_CONFIG.epToSoulRate / 100.0;
            souls = Math.min(souls, maxMP + maxAP);
            existence.setSoulPoints((int)Math.min(existence.getSoulPoints() + souls, 2.147483647E9));
            if (entity.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE) && existence.getSoulPoints() >= 20000000) {
               SkillHelper.learnSkill(entity, (ManasSkill)UniqueSkills.MERCILESS.get());
            }

            int soulReq = Math.max(
               entity.level().getGameRules().getInt(TensuraGameRules.FORCE_HARVEST_FESTIVAL),
               entity.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN)
            );
            if (existence.getSoulPoints() / 1000 >= soulReq && !RaceUtils.isAlreadyAwakened(entity, existence)) {
               ExistenceStorage.enterHarvestFestival(existence, entity);
            }

            existence.markDirty();
         }
      }

      double auraGain = totalEP * entity.getAttributeValue(TensuraAttributes.AURA_GAIN) / 100.0 * alignment.getAuraGainMultiplier();
      double magiculeGain = totalEP * entity.getAttributeValue(TensuraAttributes.MAGICULE_GAIN) / 100.0 * alignment.getMagiculeGainMultiplier();
      if (target instanceof Player player && EnergyHelper.CONFIG.penaltyLimitGain) {
         double epGain = auraGain + magiculeGain;
         double penalty = EnergyHelper.getBaseMaxEP(player) * entity.level().getGameRules().getInt(TensuraGameRules.EP_DEATH_PENALTY) / 100.0;
         if (epGain > penalty) {
            auraGain = penalty * auraGain / epGain;
            magiculeGain = penalty * magiculeGain / epGain;
         }
      }

      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         aura.setBaseValue(aura.getBaseValue() + Math.min(auraGain, maxAP));
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         magicule.setBaseValue(magicule.getBaseValue() + Math.min(magiculeGain, maxMP));
      }
   }

   private static void gearGetEP(LivingEntity entity, EquipmentSlot slot, double totalEP) {
      ItemStack stack = entity.getItemBySlot(slot);
      if (stack.has((DataComponentType)TensuraDataComponents.EP.get())) {
         double maxEP = (Double)stack.get((DataComponentType)TensuraDataComponents.MAX_EP.get());
         double currentEP = (Double)stack.get((DataComponentType)TensuraDataComponents.EP.get());
         double newEP = currentEP + getGearEPGain(entity, stack, totalEP);
         if (newEP < maxEP) {
            stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
            EngravingHelper.grantRandomEngraving(entity, stack, newEP);
            GearHandler.applyUniqueGearEvolution(stack, newEP);
         } else {
            ResourceLocation location = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.EVOLUTION.get());
            if (location != null && TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.STAGNATION, stack) <= 0) {
               newEP = Math.min(newEP, maxEP);
               stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
               EngravingHelper.grantRandomEngraving(entity, stack, newEP);
               ItemStack evolution = new ItemStack(((Item)BuiltInRegistries.ITEM.get(location)).arch$holder(), stack.getCount(), stack.getComponentsPatch());
               GearHandler.initiateGearEvolution(entity.level(), evolution);
               if (entity instanceof TensuraHumanoidEntity humanoid) {
                  humanoid.inventory.setItem(humanoid.getSlotId(slot), evolution);
                  humanoid.updateContainerEquipment();
               } else {
                  entity.setItemSlot(slot, evolution);
               }
            } else {
               if (currentEP != maxEP) {
                  newEP = Math.min(newEP, maxEP);
                  stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
                  EngravingHelper.grantRandomEngraving(entity, stack, newEP);
                  GearHandler.applyUniqueGearEvolution(stack, newEP);
               }
            }
         }
      }
   }

   private static void gearGetEP(LivingEntity entity, AbstractArrow arrow, double totalEP) {
      ItemStack stack = arrow.getPickupItemStackOrigin();
      if (stack.has((DataComponentType)TensuraDataComponents.EP.get())) {
         double maxEP = (Double)stack.get((DataComponentType)TensuraDataComponents.MAX_EP.get());
         double currentEP = (Double)stack.get((DataComponentType)TensuraDataComponents.EP.get());
         double newEP = currentEP + getGearEPGain(entity, stack, totalEP);
         if (newEP < maxEP) {
            stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
            EngravingHelper.grantRandomEngraving(entity, stack, newEP);
            GearHandler.applyUniqueGearEvolution(stack, newEP);
         } else {
            ResourceLocation location = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.EVOLUTION.get());
            if (location != null && TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.STAGNATION, stack) <= 0) {
               newEP = Math.min(newEP, maxEP);
               stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
               EngravingHelper.grantRandomEngraving(entity, stack, newEP);
               ItemStack evolution = new ItemStack(((Item)BuiltInRegistries.ITEM.get(location)).arch$holder(), stack.getCount(), stack.getComponentsPatch());
               GearHandler.initiateGearEvolution(entity.level(), evolution);
               if (arrow instanceof SpearProjectile spear) {
                  spear.setSourceItem(evolution);
               } else if (arrow instanceof KunaiProjectile kunai) {
                  kunai.setSourceItem(evolution);
               }
            } else {
               if (currentEP != maxEP) {
                  newEP = Math.min(newEP, maxEP);
                  stack.set((DataComponentType)TensuraDataComponents.EP.get(), newEP);
                  EngravingHelper.grantRandomEngraving(entity, stack, newEP);
                  GearHandler.applyUniqueGearEvolution(stack, newEP);
               }
            }
         }
      }
   }

   private static double getGearEPGain(LivingEntity entity, ItemStack stack, double totalEP) {
      float lethargy = TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.LETHARGY, stack) * 0.2F;
      float vigor = TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.VIGOR, stack) * 0.2F;
      float growth = TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.GROWTH, stack);
      double boost = 1.0F + vigor + growth - lethargy;
      return Math.round(totalEP * (Double)stack.get((DataComponentType)TensuraDataComponents.EP_GAIN.get()) * boost);
   }

   private static boolean isHumanLike(LivingEntity target) {
      if (target.getType().is(TensuraEntityTags.HUMAN_LIKE)) {
         return true;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(target).getRace();
      return optional.<Boolean>map(instance -> instance.is(TensuraRaceTags.HUMAN_LIKE)).orElse(false);
   }

   private static void addStatistic(LivingEntity target, LivingEntity attacker, DamageSource source) {
      LivingEntity owner = attacker;
      LivingEntity subOwner = SubordinateHelper.getSubordinateOwner(attacker);
      if (subOwner != null && !(attacker instanceof Player)) {
         owner = subOwner;
      }

      EntityType<?> type = target.getType();
      if (isHumanLike(target)) {
         IExistence existence = TensuraStorages.getExistenceFrom(owner);
         existence.setHumanKill(existence.getHumanKill() + 1);
         existence.markDirty();
      }

      if (owner instanceof ServerPlayer player) {
         boolean heroBoss = type.is(TensuraEntityTags.HERO_BOSS);
         if ((heroBoss || ReincarnationMenu.PLAYER_CONFIG.ResetScroll.bossesCounter.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()))
            && SubordinateHelper.getSubordinateOwnerUUID(target) == null) {
            if (heroBoss && player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get(type)) <= 0) {
               player.awardStat(Stats.CUSTOM.get(TensuraStats.BOSS_DEFEATED));
            }

            player.awardStat(((StatType)TensuraStats.BOSS_KILLED.get()).get(type));
         }

         if (attacker instanceof CloneEntity) {
            CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(player, target, source);
            player.awardStat(Stats.ENTITY_KILLED.get(type));
            player.awardStat(Stats.MOB_KILLS);
         }
      }
   }

   private static void onSubordinateDeath(LivingEntity owner, LivingEntity target, DamageSource source) {
      Skills skills = SkillAPI.getSkillsFrom(owner);
      List<ManasSkillInstance> list = List.copyOf(skills.getLearnedSkills());
      if (!list.isEmpty()) {
         for (ManasSkillInstance copy : list) {
            Optional<ManasSkillInstance> optional = skills.getSkill(copy.getSkill());
            if (!optional.isEmpty()) {
               ManasSkillInstance instance = optional.get();
               if (instance.canInteractSkill(owner) && instance.getSkill() instanceof TensuraSkill skill) {
                  skill.onSubordinateDeath(instance, owner, target, source);
               }
            }
         }

         skills.markDirty();
      }
   }
}
