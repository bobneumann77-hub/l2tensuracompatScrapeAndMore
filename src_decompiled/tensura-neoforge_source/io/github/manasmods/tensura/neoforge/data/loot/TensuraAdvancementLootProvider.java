package io.github.manasmods.tensura.neoforge.data.loot;

import io.github.manasmods.tensura.data.loot.TensuraAdvancementLoot;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class TensuraAdvancementLootProvider implements LootTableSubProvider {
   public TensuraAdvancementLootProvider(Provider lookup) {
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
      consumer.accept(
         TensuraAdvancementLoot.GETCHA_LEATHERS,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.LEATHER_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.GETCHA_BETTER_LEATHERS,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.SMELT_IRON,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.IRON_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.MINE_DIAMOND,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.DIAMOND_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.GOLD_RUSH,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.GOLD_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.ACQUIRE_SILVERWARE,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SILVER_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.LOW_MAGISTEEL,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.HIGH_MAGISTEEL,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.MITHRIL,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.MITHRIL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.ORICHALCUM,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.ORICHALCUM_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.PURE_MAGISTEEL,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.ADAMANTITE,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.ADAMANTITE_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.HIHIIROKANE,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.HIHIIROKANE_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.VIGILANT,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.SHELL_LIZARD,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.HISS_TORY,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.ARACHNOPHOBIC,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.WEB_GUN.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.GOODNIGHT_SPIDER,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR.get())))
            .withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SPIDER_BOWS.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.RULER_OF_THE_SKIES,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR.get())))
      );
      consumer.accept(
         TensuraAdvancementLoot.RULER_OF_MONSTERS,
         LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.DARK_SET.get())))
      );
   }
}
