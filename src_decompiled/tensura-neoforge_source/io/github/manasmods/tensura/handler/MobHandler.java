package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.EntityEvent.Add;
import dev.architectury.event.events.common.EntityEvent.LivingCheckSpawn;
import dev.architectury.event.events.common.LootEvent.ModifyLootTable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingChangeTargetEvent;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.template.critereon.ExistencePointPredicate;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.subclass.IMultipartLevel;
import net.minecraft.advancements.critereon.EntityPredicate.Builder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class MobHandler {
   public static void init() {
      EntityEvent.ADD.register((Add)(entity, level) -> {
         if (level instanceof IMultipartLevel multipartLevel && entity instanceof IMultipart parent) {
            for (TensuraPartEntity part : parent.getParts()) {
               if (part != null) {
                  multipartLevel.tensura$registerPart(part);
               }
            }
         }

         return EventResult.pass();
      });
      EntityEvent.LIVING_CHECK_SPAWN.register((LivingCheckSpawn)(entity, world, x, y, z, type, spawner) -> {
         if (type == MobSpawnType.NATURAL) {
            double areaMP = AreaMagiculeHelper.getMagicule(entity, true);
            if (areaMP < ChunkStorage.CONFIG.minimalMagiculeSpawn) {
               return EventResult.interruptTrue();
            }
         }

         return EventResult.pass();
      });
      EntityEvents.LIVING_CHANGE_TARGET.register((LivingChangeTargetEvent)(entity, changeableTarget) -> {
         if (changeableTarget.isPresent() && !entity.level().isClientSide()) {
            LivingEntity target = (LivingEntity)changeableTarget.get();
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            if (existence.isTargetNeutral(target.getUUID())) {
               return EventResult.interruptFalse();
            }

            if (!entity.getActiveEffects().isEmpty()) {
               MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
               if (instance != null && instance.getAmplifier() >= 4 && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
                  return EventResult.interruptFalse();
               }
            }

            if (entity.getLastHurtByMob() != target && entity.getLastHurtMob() != target && entity.getLastAttacker() != target) {
               double threshold = ChunkStorage.CONFIG.minimalMagiculeSpawn;
               if (AreaMagiculeHelper.getMagicule(target, true) < threshold && AreaMagiculeHelper.getMagicule(entity, true) >= threshold) {
                  return EventResult.interruptFalse();
               }
            }

            return EventResult.pass();
         } else {
            return EventResult.pass();
         }
      });
      LootEvent.MODIFY_LOOT_TABLE
         .register(
            (ModifyLootTable)(key, context, builtin) -> {
               if (builtin) {
                  if (key.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.PIERROT_MASK.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.KUNAI.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.BASTION_HOGLIN_STABLE)
                     || key.equals(BuiltInLootTables.STRONGHOLD_CROSSING)
                     || key.equals(BuiltInLootTables.DESERT_PYRAMID)
                     || key.equals(BuiltInLootTables.IGLOO_CHEST)
                     || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD)
                     || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS)
                     || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.RUINED_PORTAL)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()).when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ZANE_BLOOD.get()).when(LootItemRandomChanceCondition.randomChance(0.3F)))
                     );
                  } else if (key.equals(BuiltInLootTables.WOODLAND_MANSION)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.5F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.JAPANESE_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.ANCIENT_CITY)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.JAPANESE_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MARIONETTE_HEART.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.05F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.ANCIENT_CITY_ICE_BOX)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.JAPANESE_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.BASTION_TREASURE)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()).when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MARIONETTE_HEART.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.END_CITY_TREASURE)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MARIONETTE_HEART.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.BURIED_TREASURE)
                     || key.equals(BuiltInLootTables.JUNGLE_TEMPLE)
                     || key.equals(BuiltInLootTables.STRONGHOLD_LIBRARY)
                     || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_UNIQUE)
                     || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ZANE_BLOOD.get()).when(LootItemRandomChanceCondition.randomChance(0.3F)))
                     );
                  } else if (key.equals(BuiltInLootTables.NETHER_BRIDGE) || key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()).when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()).when(LootItemRandomChanceCondition.randomChance(0.1F))
                           )
                     );
                     context.addPool(
                        LootPool.lootPool()
                           .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ZANE_BLOOD.get()).when(LootItemRandomChanceCondition.randomChance(0.3F)))
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_ARMORER)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SHIELD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_WEAPONSMITH)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.GREAT_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_TOOLSMITH)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.LONG_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_TANNERY)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_BUTCHER)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.HUNTING_KNIFE.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  } else if (key.equals(BuiltInLootTables.VILLAGE_FLETCHER) || key.equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
                     context.addPool(
                        LootPool.lootPool()
                           .add(
                              LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.BASIC_BOWS.get())
                                 .when(LootItemRandomChanceCondition.randomChance(0.2F))
                           )
                     );
                  }

                  if (key.location().getPath().startsWith("entities/")) {
                     if (key.equals(EntityType.ENDER_DRAGON.getDefaultLootTable())) {
                        context.addPool(
                           LootPool.lootPool()
                              .add(
                                 LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get())
                                    .when(LootItemRandomChanceCondition.randomChance(0.5F))
                              )
                        );
                     }

                     Builder highPredicate = Builder.entity().of(TensuraEntityTags.DROP_CRYSTAL).subPredicate(ExistencePointPredicate.getDefault(9000.0));
                     Builder mediumPredicate = Builder.entity()
                        .of(TensuraEntityTags.DROP_CRYSTAL)
                        .subPredicate(ExistencePointPredicate.getDefault(3000.0, 8999.0));
                     Builder lowPredicate = Builder.entity().of(TensuraEntityTags.DROP_CRYSTAL).subPredicate(ExistencePointPredicate.getDefault(1.0));
                     net.minecraft.world.level.storage.loot.LootPool.Builder pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                           ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                    (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()
                                 )
                                 .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, mediumPredicate)))
                              .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                              .otherwise(
                                 ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                          (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()
                                       )
                                       .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, highPredicate)))
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                    .otherwise(
                                       ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                                (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()
                                             )
                                             .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, lowPredicate)))
                                          .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                    )
                              )
                        );
                     context.addPool(pool);
                  }
               }
            }
         );
   }
}
