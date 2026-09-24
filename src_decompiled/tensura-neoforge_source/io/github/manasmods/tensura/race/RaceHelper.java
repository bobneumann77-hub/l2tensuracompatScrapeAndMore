package io.github.manasmods.tensura.race;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.intrinsic.PossessionSkill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.ability.skill.unique.VillainSkill;
import io.github.manasmods.tensura.data.existence.EntityExistenceData;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.handler.AttributeHandler;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RaceHelper {
   public static boolean evolveRace(LivingEntity entity) {
      return evolveRace(entity, true);
   }

   public static boolean evolveRace(LivingEntity entity, boolean triggerRewards) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
      return optional.filter(instance -> evolveRace(entity, instance.getDefaultEvolution(entity), triggerRewards)).isPresent();
   }

   public static boolean evolveRace(LivingEntity entity, ManasRace race, boolean triggerRewards) {
      return evolveRace(entity, race, triggerRewards, false);
   }

   public static boolean evolveRace(LivingEntity entity, ManasRace race, boolean triggerRewards, boolean skipNextEvoCheck) {
      if (entity.level().isClientSide()) {
         return false;
      }

      Races races = RaceAPI.getRaceFrom(entity);
      Optional<ManasRaceInstance> optional = races.getRace();
      if (optional.isEmpty()) {
         return false;
      }

      ManasRaceInstance originalRace = optional.get();
      if (!skipNextEvoCheck && !originalRace.getNextEvolutions(entity).contains(race)) {
         return false;
      }

      if (entity instanceof ServerPlayer serverPlayer) {
         ((PlayerTrigger)TensuraCriteriaTriggers.EVOLVE_RACE.get()).trigger(serverPlayer);
      }

      Map<Holder<Attribute>, AttributeModifier> map = new HashMap<>();

      for (Holder<Attribute> attribute : PossessionSkill.getStatList()) {
         AttributeInstance instance = entity.getAttribute(attribute);
         if (instance != null && instance.hasModifier(TensuraRace.DEFAULT_RACE_ID)) {
            map.put(attribute, instance.getModifier(TensuraRace.DEFAULT_RACE_ID));
         }
      }

      ManasRaceInstance evolution = race.createDefaultInstance();
      if (races.evolveRace(evolution) && race instanceof TensuraRace tensuraRace) {
         for (Entry<Holder<Attribute>, AttributeModifier> entry : map.entrySet()) {
            AttributeInstance instance = entity.getAttribute(entry.getKey());
            if (instance != null) {
               AttributeModifier modifier = instance.getModifier(entry.getValue().id());
               if (modifier != null && (modifier.operation() != entry.getValue().operation() || !(modifier.amount() >= entry.getValue().amount()))) {
                  instance.removeModifier(entry.getValue().id());
                  instance.addPermanentModifier(entry.getValue());
               }
            }
         }

         if (triggerRewards) {
            tensuraRace.triggerEvolutionRewards(evolution, entity);
         }

         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getOriginalAlignment().equals(Alignment.DEFAULT)) {
            existence.setOriginalAlignment(tensuraRace.getAlignment());
            existence.markDirty();
         }

         Alignment alignment = existence.getAlignment();
         if (alignment.equals(Alignment.DEFAULT)) {
            existence.setAlignment(tensuraRace.getAlignment());
            existence.markDirty();
         }

         if (existence.isHeroEgg() && existence.getAlignment().equals(Alignment.MAJIN)) {
            entity.sendSystemMessage(Component.translatable("tensura.evolve.hero.egg_lost.evolution").withStyle(ChatFormatting.RED));
            existence.setHeroEgg(false);
            existence.markDirty();
         }
      }

      ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(entity);
      if (playerData != null) {
         playerData.setTrackedEvolution(null);
         playerData.markDirty();
      }

      return originalRace.getRace() != race;
   }

   public static void awakening(LivingEntity entity, boolean isHero) {
      Level level = entity.level();
      Changeable<Boolean> hero = Changeable.of(isHero);
      if (!((TensuraEntityEvents.AwakeningEvent)TensuraEntityEvents.AWAKENING_EVENT.invoker()).awaken(entity, hero).isFalse()) {
         if (entity instanceof ServerPlayer serverPlayer) {
            ((PlayerTrigger)TensuraCriteriaTriggers.AWAKEN_RACE.get()).trigger(serverPlayer);
         }

         EnergyHelper.removeSpiritualEPLimit(entity);
         float multiplier = hero.get() ? TensuraRace.BASE_CONFIG.Hero.epMultiplierHero : TensuraRace.BASE_CONFIG.DemonLord.epMultiplierDemonLord;
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
         if (aura != null) {
            aura.setBaseValue(aura.getBaseValue() * multiplier);
            existence.setAura(Math.max(existence.getAura(), aura.getValue()));
         }

         AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
         if (magicule != null) {
            magicule.setBaseValue(magicule.getBaseValue() * multiplier);
            existence.setMagicule(Math.max(existence.getMagicule(), magicule.getValue()));
         }

         Skills storage = SkillAPI.getSkillsFrom(entity);

         for (ManasSkillInstance instance : List.copyOf(storage.getLearnedSkills())) {
            if (instance.getSkill() instanceof ResistSkill resistSkill) {
               storage.getSkill(resistSkill).ifPresent(skillInstance -> resistSkill.evolveToNullification(skillInstance, entity));
            }
         }

         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         if (optional.isPresent() && optional.get().getRace() instanceof TensuraRace race) {
            ManasRace evolution = race.getAwakeningEvolution(optional.get(), entity);
            if (evolution != null) {
               evolveRace(entity, evolution, true, true);
            }
         }

         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
         if ((Boolean)hero.get()) {
            for (Player everyone : level.players()) {
               everyone.displayClientMessage(
                  Component.translatable("tensura.evolve.hero.success", new Object[]{entity.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                  false
               );
            }

            if (SkillHelper.learnSkill(entity, (ManasSkill)IntrinsicSkills.UNPREDICTABILITY.get()) && optional.isPresent()) {
               optional.get().addIntrinsicSkill((ManasSkill)IntrinsicSkills.UNPREDICTABILITY.get());
               optional.get().markDirty();
               RaceAPI.getRaceFrom(entity).markDirty();
            }

            existence.setTrueHero(true);
            existence.markDirty();
         } else {
            for (Player sub : level.players()) {
               sub.displayClientMessage(
                  Component.translatable("tensura.evolve.demon_lord.success", new Object[]{entity.getName()})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                  false
               );
            }

            existence.setTrueDemonLord(true);
            existence.markDirty();
         }
      }
   }

   public static void applyHarvestFestivalGift(IExistence existence, LivingEntity entity) {
      if (!existence.hasHarvestGift()) {
         if (entity instanceof Player sub) {
            if (!((TensuraEntityEvents.HarvestFestivalRewardEvent)TensuraEntityEvents.HARVEST_FESTIVAL_REWARD_EVENT.invoker()).reward(entity).isFalse()) {
               existence.setHarvestGift(true);
               Optional<ManasRaceInstance> subOptional = RaceAPI.getRaceFrom(sub).getRace();
               if (subOptional.isPresent() && subOptional.get().getRace() instanceof TensuraRace race) {
                  ManasRace harvest = race.getHarvestFestivalEvolution(subOptional.get(), sub);
                  if (harvest != null) {
                     sub.displayClientMessage(
                        Component.translatable("tensura.evolve.demon_lord.subordinate_evolve").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false
                     );
                     evolveRace(sub, harvest, true);
                  }
               }
            }
         } else if (!((TensuraEntityEvents.HarvestFestivalRewardEvent)TensuraEntityEvents.HARVEST_FESTIVAL_REWARD_EVENT.invoker()).reward(entity).isFalse()) {
            float multiplier = TensuraRace.BASE_CONFIG.DemonLord.epMultiplierDemonLord;
            existence.setHarvestGift(true);
            existence.markDirty();
            evolveMobs(entity);
            AttributeInstance subAura = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (subAura != null) {
               subAura.setBaseValue(subAura.getBaseValue() * multiplier);
               existence.setAura(subAura.getValue());
            }

            AttributeInstance subMagicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (subMagicule != null) {
               subMagicule.setBaseValue(subMagicule.getBaseValue() * multiplier);
               existence.setMagicule(subMagicule.getValue());
            }
         }
      }
   }

   public static void awakenHeroDuringFight(LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.isHeroEgg()) {
            if (!RaceUtils.isAlreadyAwakened(entity, existence)) {
               if (!(entity.getHealth() >= entity.getMaxHealth() * TensuraRace.BASE_CONFIG.Hero.bossHPMultiplier)) {
                  if (RaceUtils.isFightingBossForHero(entity)) {
                     existence.setTrueHero(true);
                     awakening(entity, true);
                     existence.markDirty();
                  }
               }
            }
         }
      }
   }

   public static void applyMajinChance(LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (existence.getAlignment().equals(Alignment.DEFAULT)) {
         double chance = SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.VILLAIN.get())
            ? VillainSkill.CONFIG.majinPercentage
            : TensuraRace.BASE_CONFIG.majinPercentage;
         if (entity.getRandom().nextFloat() <= chance / 100.0) {
            existence.setAlignment(Alignment.MAJIN);
            existence.markDirty();
         }
      }
   }

   public static void applyBaseAttribute(EntityType<? extends LivingEntity> type, LivingEntity entity) {
      applyBaseAttribute(DefaultAttributes.getSupplier(type), entity);
   }

   public static void applyBaseAttribute(AttributeSupplier supplier, LivingEntity entity) {
      applyBaseAttribute(supplier, entity, false);
   }

   public static void applyBaseAttribute(AttributeSupplier supplier, LivingEntity entity, boolean totalReset) {
      for (AttributeInstance attribute : supplier.instances.values()) {
         AttributeInstance attributeInstance = entity.getAttribute(attribute.getAttribute());
         if (attributeInstance != null) {
            double base = attribute.getBaseValue();
            if (totalReset || attributeInstance.getBaseValue() < base) {
               attributeInstance.setBaseValue(base);
            }
         }
      }
   }

   public static void evolveMobs(LivingEntity sub) {
      if (sub.level() instanceof ServerLevel level) {
         if (sub instanceof INameEvolution ranking) {
            ranking.evolve();
         }

         Registry<EntityExistenceData> registry = level.registryAccess().registryOrThrow(TensuraCustomData.ENTITY_EXISTENCE);
         registry.stream()
            .filter(entityExistenceData -> entityExistenceData.entity().equals(sub.getType().arch$registryName()))
            .findFirst()
            .ifPresent(data -> {
               if (!data.evolution().isEmpty()) {
                  CompoundTag tag = sub.saveWithoutId(new CompoundTag());
                  sub.discard();
                  Optional<EntityType<?>> optional = EntityType.byString(data.evolution().get().toString());
                  if (!optional.isEmpty()) {
                     Entity entity = optional.get().create(level);
                     if (entity instanceof LivingEntity evolution) {
                        entity.load(tag);
                        if (entity instanceof Mob mob) {
                           mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CONVERSION, null);
                        }

                        level.addFreshEntity(entity);
                        applyBaseAttribute((EntityType<? extends LivingEntity>)optional.get(), evolution);
                        evolution.setHealth(evolution.getMaxHealth());
                        AttributeHandler.updateEntityExistence(evolution, level, TensuraStorages.getExistenceFrom(evolution));
                     }
                  }
               }
            });
      }
   }
}
