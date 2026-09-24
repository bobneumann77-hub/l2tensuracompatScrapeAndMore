package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.EntityEvent.Add;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.block.entity.PrayingPathBlockEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.goal.FollowSwarmLeaderGoal;
import io.github.manasmods.tensura.entity.monster.GiantCodEntity;
import io.github.manasmods.tensura.entity.monster.GiantSalmonEntity;
import io.github.manasmods.tensura.entity.monster.HoundDogEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.variant.HoundDogVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import java.util.Objects;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;

public class BehaviourHandler {
   public static void init() {
      EntityEvent.ADD.register((Add)(entity, world) -> {
         if (entity instanceof Cod cod) {
            cod.goalSelector.addGoal(3, new FollowSwarmLeaderGoal(cod, GiantCodEntity.class));
         }

         if (entity instanceof Salmon salmon) {
            salmon.goalSelector.addGoal(3, new FollowSwarmLeaderGoal(salmon, GiantSalmonEntity.class));
         }

         return EventResult.pass();
      });
      TensuraEntityEvents.NAMING_EVENT
         .register(
            (TensuraEntityEvents.NamingEvent)(entity, namer, originalCost, actualCost, namingType, name) -> !TensuraBehaviourHelper.CONFIG.Boss.bossAreaName
                  && entity.level().dimension().equals(TensuraDimensions.BOSS_AREA)
               ? EventResult.interruptFalse()
               : EventResult.pass()
         );
      TensuraEntityEvents.FORCE_TAME_EVENT
         .register(
            (TensuraEntityEvents.ForceTameEvent)(entity, owner, temp) -> !TensuraBehaviourHelper.CONFIG.Boss.bossAreaMindControl
                  && entity.level().dimension().equals(TensuraDimensions.BOSS_AREA)
               ? EventResult.interruptFalse()
               : EventResult.pass()
         );
      TensuraEntityEvents.POSSESSION_EVENT
         .register(
            (TensuraEntityEvents.PossessionEvent)(entity, owner) -> !TensuraBehaviourHelper.CONFIG.Boss.bossAreaPossess
                  && entity.level().dimension().equals(TensuraDimensions.BOSS_AREA)
               ? EventResult.interruptFalse()
               : EventResult.pass()
         );
      TensuraEntityEvents.POST_TAME_EVENT
         .register(
            (TensuraEntityEvents.PostTameEvent)(animal, player) -> {
               if (!animal.level().isClientSide()) {
                  IExistence existence = TensuraStorages.getExistenceFrom(animal);
                  if (!Objects.equals(existence.getTemporaryOwner(), player.getUUID())) {
                     existence.setPermanentOwner(player.getUUID());
                     existence.markDirty();
                     if (animal instanceof IElementalSpirit spirit) {
                        ISpiritWielder spiritData = TensuraStorages.getSpiritFrom(player);
                        int currentLevel = spiritData.getSpiritLevelId(spirit.getElemental());
                        if (spirit.getSpiritLevel().getId() <= currentLevel) {
                           return;
                        }

                        float chance = player.getRandom().nextFloat() * 100.0F;
                        if (spirit.getSpiritLevel().equals(SpiritualMagic.SpiritLevel.MEDIUM)) {
                           if (chance >= TensuraRace.BASE_CONFIG.Spirit.mediumSpiritTamePercentage) {
                              return;
                           }
                        } else if (spirit.getSpiritLevel().equals(SpiritualMagic.SpiritLevel.GREATER)
                           && chance >= TensuraRace.BASE_CONFIG.Spirit.greaterSpiritTamePercentage) {
                           return;
                        }

                        if (spiritData.setSpiritLevel(spirit.getElemental(), spirit.getSpiritLevel())) {
                           spiritData.markDirty();
                           PrayingPathBlockEntity.grantSpiritMagic(player, spirit.getElemental(), spirit.getSpiritLevel());
                           PrayingPathBlockEntity.grantManipulation(player, spirit.getElemental());
                           player.awardStat(Stats.CUSTOM.get(TensuraStats.SPIRIT_CONTRACTED_TIME));
                           ((PlayerTrigger)TensuraCriteriaTriggers.SPIRIT_CONTRACTED.get()).trigger((ServerPlayer)player);
                        }
                     } else if (animal instanceof HoundDogEntity dog && dog.getVariant().equals(HoundDogVariant.EVOLVED)) {
                        SkillHelper.learnSkill(player, (ManasSkill)SummoningMagics.SUMMON_HOUND_DOG.get());
                     } else if (animal.getType().equals(MonsterEntityTypes.BASILISK.get())) {
                        SkillHelper.learnSkill(player, (ManasSkill)SummoningMagics.SUMMON_BASILISK.get());
                     }
                  }
               }
            }
         );
   }
}
