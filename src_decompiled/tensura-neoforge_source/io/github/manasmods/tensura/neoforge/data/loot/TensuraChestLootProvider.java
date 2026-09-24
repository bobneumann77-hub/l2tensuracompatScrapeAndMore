package io.github.manasmods.tensura.neoforge.data.loot;

import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.data.loot.TensuraChestLoot;
import io.github.manasmods.tensura.data.template.function.ApplySkillDataFunction;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction.Target;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TensuraChestLootProvider implements LootTableSubProvider {
   private final Provider registries;

   public TensuraChestLootProvider(Provider lookup) {
      this.registries = lookup;
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
      RandomSource randomSource = RandomSource.create();
      consumer.accept(
         TensuraChestLoot.DWARF_GUARD,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 10))
                  .add(this.item(Items.STICK).setWeight(35))
                  .add(this.item(Items.LEATHER).setWeight(35))
                  .add(this.item(Items.IRON_INGOT).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.PAPER).setWeight(50))
                  .add(this.item((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.IRON_SWORD).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SPEAR.get()).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_MARKET,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 10))
                  .add(this.item(Items.STICK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WHITE_WOOL).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.LEATHER).setWeight(50))
                  .add(this.item(Items.FLINT).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.IRON_INGOT).setWeight(50))
                  .add(this.item(Items.GOLD_INGOT).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 5))
                  .add(this.item(Items.BREAD).setWeight(50))
                  .add(this.item(Items.COOKIE).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_HOME,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 10))
                  .add(this.item(Items.BEETROOT).setWeight(35))
                  .add(this.item(Items.CARROT).setWeight(35))
                  .add(this.item(Items.POTATO).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.EMERALD).setWeight(50))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 5))
                  .add(this.item(Items.BREAD).setWeight(50))
                  .add(this.item(Items.COOKIE).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_LUMBERJACK,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(6, 16))
                  .add(this.item(Items.OAK_LOG).setWeight(35))
                  .add(this.item(Items.STRIPPED_OAK_WOOD).setWeight(35))
                  .add(this.item(Items.DARK_OAK_LOG).setWeight(35))
                  .add(this.item(Items.STRIPPED_DARK_OAK_WOOD).setWeight(35))
                  .add(this.item(Items.SPRUCE_LOG).setWeight(35))
                  .add(this.item(Items.STRIPPED_SPRUCE_WOOD).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item(Items.OAK_SAPLING).setWeight(50))
                  .add(this.item(Items.DARK_OAK_SAPLING).setWeight(50))
                  .add(this.item(Items.SPRUCE_SAPLING).setWeight(50))
                  .add(this.item(Items.APPLE).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.IRON_AXE).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.SILVER_AXE.get()).setWeight(50))
                  .add(this.item(Items.IRON_NUGGET).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_FISHERMAN,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 16))
                  .add(this.item(Items.SALMON).setWeight(35))
                  .add(this.item(Items.COD).setWeight(35))
                  .add(this.item(Items.PUFFERFISH).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 5))
                  .add(this.item(Items.COD_BUCKET).setWeight(50))
                  .add(this.item(Items.SALMON_BUCKET).setWeight(50))
                  .add(this.item(Items.PUFFERFISH_BUCKET).setWeight(50))
                  .add(this.item(Items.SEAGRASS).setWeight(50))
                  .add(this.item(Items.SEA_PICKLE).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 5))
                  .add(this.item(Items.FISHING_ROD).setWeight(50))
                  .add(this.item(Items.STRING).setWeight(50))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_FARM,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 16))
                  .add(this.item(Items.CARROT).setWeight(35))
                  .add(this.item(Items.POTATO).setWeight(35))
                  .add(this.item(Items.BEETROOT).setWeight(35))
                  .add(this.item(Items.WHEAT).setWeight(35))
                  .add(this.item(Items.WHEAT_SEEDS).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 5))
                  .add(this.item(Items.MELON).setWeight(50))
                  .add(this.item(Items.PUMPKIN).setWeight(50))
                  .add(this.item(Items.MELON_SEEDS).setWeight(50))
                  .add(this.item(Items.PUMPKIN_SEEDS).setWeight(50))
                  .add(this.item(Items.SHORT_GRASS).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 5))
                  .add(this.item(Items.IRON_NUGGET).setWeight(50))
                  .add(this.item(Items.IRON_HOE).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.SILVER_HOE.get()).setWeight(50))
                  .add(this.item(Items.TALL_GRASS).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_LIBRARY,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(35))
                  .add(this.item(Items.INK_SAC).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get()).setWeight(10))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
                  .add(this.item(Items.AIR).setWeight(75))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.CHISELED_BOOKSHELF).setWeight(50))
                  .add(this.item(Items.BOOKSHELF).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_SMITHY,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(6, 16))
                  .add(this.item(Items.IRON_INGOT).setWeight(35))
                  .add(this.item(Items.COPPER_INGOT).setWeight(35))
                  .add(this.item(Items.GOLD_INGOT).setWeight(35))
                  .add(this.item((ItemLike)TensuraMaterialItems.SILVER_INGOT.get()).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(25))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 3))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.IRON_GEAR.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.GOLD_GEAR.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SILVER_GEAR.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.LONG_SWORD.get()).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(75))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 6))
                  .add(this.item(Items.IRON_SWORD).setWeight(50))
                  .add(this.item(Items.IRON_AXE).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_LONG_SWORD.get()).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get()).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SPEAR.get()).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_GREAT_SWORD.get()).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_MAGIC_TRAINER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(35))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(10.0F)))
                  )
                  .add(this.item(Items.AIR).setWeight(15))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(40)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.LOW_BASIC_TOME_DWARF_TRADE))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(20)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.LOW_UPGRADED_TOME_DWARF_TRADE))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(5)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.LOW_RARE_TOME_TRADE))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(5))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(1))
                  .add(this.item(Items.AIR).setWeight(60))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.RED_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_DOJO,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item(Items.LEATHER).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(35))
                  .add(this.item(Items.AIR).setWeight(15))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 2))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                        .setWeight(20)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.LOW_MANUAL_DWARF_TRADE))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.MEDIUM_MANUAL_DWARF_TRADE))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                        .setWeight(5)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.HIGH_MANUAL_DWARF_TRADE))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get())
                        .setWeight(1)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_MANUAL_DWARF_TRADE))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get()).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(60))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.OAK_PLANKS).setWeight(50))
                  .add(this.item(Items.FEATHER).setWeight(25))
                  .add(this.item(Items.FLINT).setWeight(25))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(50))
                  .add(this.item(Items.WHEAT).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_MERCHANT,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 16))
                  .add(this.item(Items.LEATHER).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(35))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(25)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(5.0F)))
                  )
                  .add(this.item(Items.AIR).setWeight(15))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 8))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BRONZE_COIN.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.SILVER_COIN.get()).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(60))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 20))
                  .add(this.item(Items.COPPER_INGOT).setWeight(50))
                  .add(this.item(Items.IRON_NUGGET).setWeight(25))
                  .add(this.item(Items.FLINT).setWeight(25))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(50))
                  .add(this.item(Items.WHEAT).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(25))
            )
      );
      consumer.accept(
         TensuraChestLoot.DWARF_ROYAL_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 10))
                  .add(this.item(Items.STICK).setWeight(35))
                  .add(this.item(Items.LEATHER).setWeight(35))
                  .add(this.item(Items.IRON_INGOT).setWeight(35))
                  .add(this.item(Items.GOLD_INGOT).setWeight(15))
                  .add(this.item(Items.AIR).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.PAPER).setWeight(50))
                  .add(this.item((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraMaterialItems.DWARGON_BANNER_PATTERN.get()).setWeight(5))
                  .add(this.item(Items.AIR).setWeight(15))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.IRON_SWORD).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SPEAR.get()).setWeight(50))
                  .add(this.item(Items.GOLDEN_SWORD).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.GOLDEN_SPEAR.get()).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(15))
            )
      );
      consumer.accept(
         TensuraChestLoot.GOBLIN_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 10))
                  .add(this.item(Items.STICK).setWeight(35))
                  .add(this.item(Items.FLINT).setWeight(35))
                  .add(this.item(Items.ARROW).setWeight(35))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.FEATHER).setWeight(50))
                  .add(this.item(Items.LEATHER).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(50))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.BASIC_BOWS.get()).setWeight(10))
                  .add(this.item(Items.BOW).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.SHORT_BOW.get()).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.ACACIA_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 3)).add(this.item(Items.LEATHER).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.FEATHER).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.APPLE).setWeight(30)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item(Items.ACACIA_PLANKS).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.BIRCH_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 3)).add(this.item(Items.BEETROOT).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.SHORT_GRASS).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.APPLE).setWeight(30)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item(Items.BIRCH_PLANKS).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.JUNGLE_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.VINE).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.COCOA_BEANS).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item(Items.JUNGLE_PLANKS).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.OAK_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.WHEAT_SEEDS).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.WHEAT).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.APPLE).setWeight(30)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item(Items.OAK_PLANKS).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.PALM_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.SAND).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.FLINT).setWeight(30)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item((ItemLike)TensuraBlocks.Items.PALM_PLANKS.get()).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.SPRUCE_GOBLIN_VILLAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.SWEET_BERRIES).setWeight(50)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(25)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.FERN).setWeight(30)))
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.STICK).setWeight(35)))
            .withPool(LootPool.lootPool().setRolls(this.between(4, 10)).add(this.item(Items.SPRUCE_PLANKS).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.exactly(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_THRONE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(2, 5)).add(this.item(Items.MANGROVE_ROOTS)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get())))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LILY_PAD).setWeight(50))
                  .add(this.item(Items.MUDDY_MANGROVE_ROOTS).setWeight(30))
            )
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.COD).setWeight(35)).add(this.item(Items.SALMON).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item((ItemLike)TensuraMaterialItems.GOLD_COIN.get()).setWeight(1))
                  .add(this.item((ItemLike)TensuraMaterialItems.SILVER_COIN.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraMaterialItems.BRONZE_COIN.get()).setWeight(30))
                  .add(this.item(Items.DIAMOND).setWeight(40))
                  .add(this.item(Items.EMERALD).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(75))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get()).setWeight(10))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SPEAR.get()).setWeight(25))
                  .add(this.item(Items.TRIDENT).setWeight(30))
                  .add(this.item(Items.GOLDEN_HELMET).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_BEDROOM,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(2, 5)).add(this.item(Items.MANGROVE_ROOTS)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get())))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LILY_PAD).setWeight(50))
                  .add(this.item(Items.MUDDY_MANGROVE_ROOTS).setWeight(30))
            )
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.COD).setWeight(35)).add(this.item(Items.SALMON).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LEATHER_CHESTPLATE).setWeight(30))
                  .add(this.item(Items.LEATHER_LEGGINGS).setWeight(40))
                  .add(this.item(Items.LEATHER_HELMET).setWeight(50))
                  .add(this.item(Items.LEATHER_BOOTS).setWeight(60))
                  .add(this.item(Items.AIR).setWeight(75))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 2))
                  .add(this.item((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get()).setWeight(5))
                  .add(this.item(Items.BOOK).setWeight(50))
                  .add(this.item(Items.AIR).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_JAIL,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.LILY_PAD).setWeight(50)).add(this.item(Items.MUD).setWeight(30)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item(Items.COD).setWeight(35))
                  .add(this.item(Items.SALMON).setWeight(35))
                  .add(this.item(Items.BOWL).setWeight(50))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 2))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item(Items.IRON_SWORD).setWeight(40))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SPEAR.get()).setWeight(25))
                  .add(this.item(Items.AIR).setWeight(75))
            )
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.BOOK).setWeight(50)).add(this.item(Items.AIR).setWeight(75)))
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_MESS_HALL,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.POTATO).setWeight(50))
                  .add(this.item(Items.CARROT).setWeight(50))
                  .add(this.item(Items.POISONOUS_POTATO).setWeight(20))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item(Items.COOKED_COD).setWeight(35))
                  .add(this.item(Items.COOKED_SALMON).setWeight(35))
                  .add(this.item(Items.BOWL).setWeight(50))
            )
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.STICK).setWeight(50)).add(this.item(Items.AIR).setWeight(75)))
            .withPool(LootPool.lootPool().setRolls(this.between(1, 2)).add(this.item(Items.GOLDEN_APPLE).setWeight(1)).add(this.item(Items.AIR).setWeight(99)))
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_SMITHY,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get())))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 4))
                  .add(this.item((ItemLike)TensuraToolItems.SILVER_SPEAR.get()))
                  .add(this.item((ItemLike)TensuraToolItems.SILVER_SWORD.get()))
                  .add(this.item((ItemLike)TensuraToolItems.SILVER_SHORT_SWORD.get()))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SPEAR.get()))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get()))
                  .add(this.item(Items.IRON_SWORD))
            )
            .withPool(
               LootPool.lootPool().setRolls(this.between(1, 5)).add(this.item(Items.STICK).setWeight(35)).add(this.item(Items.COBBLESTONE).setWeight(35))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.SILVER_INGOT.get()))
                  .add(this.item((ItemLike)TensuraMaterialItems.RAW_SILVER.get()))
                  .add(this.item(Items.IRON_INGOT))
                  .add(this.item(Items.RAW_IRON))
                  .add(this.item(Items.GOLD_INGOT))
                  .add(this.item(Items.RAW_GOLD))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SPEAR.get()).setWeight(25))
                  .add(this.item(Items.TRIDENT).setWeight(30))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_STORAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item((ItemLike)TensuraMaterialItems.THATCH.get())))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LILY_PAD).setWeight(50))
                  .add(this.item(Items.MUDDY_MANGROVE_ROOTS).setWeight(30))
            )
            .withPool(LootPool.lootPool().setRolls(this.between(3, 8)).add(this.item(Items.APPLE).setWeight(35)).add(this.item(Items.OAK_LOG).setWeight(35)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get()).setWeight(1))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SPEAR.get()).setWeight(10))
                  .add(this.item(Items.LANTERN).setWeight(50))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item(Items.TORCH).setWeight(75))
            )
      );
      consumer.accept(
         TensuraChestLoot.LIZARDMAN_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.SPYGLASS).setWeight(50))
                  .add(this.item((ItemLike)TensuraMaterialItems.THATCH.get()).setWeight(75))
                  .add(
                     LootItem.lootTableItem(Items.MAP)
                        .setWeight(10)
                        .apply(
                           ExplorationMapFunction.makeExplorationMap()
                              .setDestination(TensuraTags.Structures.ON_LIZARDMAN_VILLAGE_MAPS)
                              .setMapDecoration(MapDecorationTypes.SWAMP_HUT)
                              .setZoom((byte)2)
                              .setSkipKnownStructures(false)
                        )
                        .apply(SetNameFunction.setName(Component.translatable("tensura.map.lizardman_village"), Target.ITEM_NAME))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.COOKED_COD).setWeight(35))
                  .add(this.item(Items.COOKED_SALMON).setWeight(35))
                  .add(this.item(Items.ARROW).setWeight(35))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item(Items.IRON_SWORD).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get()).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(50))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.BASIC_BOWS.get()).setWeight(10))
                  .add(this.item(Items.BOW).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.SHORT_BOW.get()).setWeight(50))
                  .add(this.item((ItemLike)TensuraToolItems.LONG_BOW.get()).setWeight(30))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.ORC_TENT,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.SAND).setWeight(50)).add(this.item(Items.SANDSTONE).setWeight(20)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item(Items.DEAD_BUSH).setWeight(20))
                  .add(this.item(Items.CACTUS).setWeight(20))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.SPEAR.get()).setWeight(2))
                  .add(this.item(Items.LEATHER).setWeight(50))
                  .add(this.item((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.ORC_STORAGE,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(1, 4)).add(this.item(Items.SAND).setWeight(50)).add(this.item(Items.SANDSTONE).setWeight(20)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.STICK).setWeight(50))
                  .add(this.item(Items.DEAD_BUSH).setWeight(20))
                  .add(this.item(Items.CACTUS).setWeight(20))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LEATHER).setWeight(50))
                  .add(this.item((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(50))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 4))
                  .add(this.item(Items.LANTERN).setWeight(60))
                  .add(this.item(Items.SADDLE).setWeight(30))
                  .add(this.item(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(5))
                  .add(this.item(Items.SNORT_POTTERY_SHERD).setWeight(10))
                  .add(this.item(Items.AIR).setWeight(70))
            )
      );
      consumer.accept(
         TensuraChestLoot.HELL_RUINS,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(5, 15))
                  .add(this.item(Items.BONE).setWeight(70))
                  .add(this.item(Items.BONE_BLOCK).setWeight(20))
                  .add(this.item(Items.SKELETON_SKULL).setWeight(10))
                  .add(this.item(Items.SPRUCE_LOG).setWeight(30))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 2))
                  .add(this.item(Items.NETHERITE_SCRAP).setWeight(2))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_ORE.get()).setWeight(1))
                  .add(this.item((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()).setWeight(1))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.HELL_TREASURE_TOME))
                        .setWeight(1)
                  )
                  .add(
                     LootItem.lootTableItem(Items.MAP)
                        .setWeight(1)
                        .apply(
                           ExplorationMapFunction.makeExplorationMap()
                              .setDestination(TensuraTags.Structures.ON_HELL_GATE_EXPLORER_MAPS)
                              .setMapDecoration(MapDecorationTypes.RED_X)
                              .setZoom((byte)2)
                              .setSkipKnownStructures(false)
                        )
                        .apply(SetNameFunction.setName(Component.translatable("tensura.map.hell_gate"), Target.ITEM_NAME))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 4))
                  .add(this.item(Items.SKULL_POTTERY_SHERD).setWeight(20))
                  .add(this.item(Items.DANGER_POTTERY_SHERD).setWeight(20))
                  .add(this.item(Items.COPPER_INGOT).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(40))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 6))
                  .add(
                     LootItem.lootTableItem(Items.SUSPICIOUS_STEW)
                        .setWeight(30)
                        .apply(
                           SetStewEffectFunction.stewEffect()
                              .withEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), UniformGenerator.between(7.0F, 10.0F))
                        )
                  )
                  .add(this.item(Items.FEATHER).setWeight(30))
                  .add(this.item(Items.RED_SAND).setWeight(30))
                  .add(this.item(Items.RED_SANDSTONE).setWeight(30))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.SPIDER_NEST,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(this.between(2, 5)).add(this.item(Items.COAL).setWeight(70)).add(this.item(Items.FLINT).setWeight(30)))
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraBlocks.SPIDER_EGG.get()).setWeight(30))
                  .add(this.item((ItemLike)TensuraBlocks.STICKY_COBWEB.get()).setWeight(30))
                  .add(this.item((ItemLike)TensuraBlocks.STICKY_STEEL_COBWEB.get()).setWeight(30))
                  .add(this.item(Items.AIR).setWeight(10))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(this.item(Items.NAME_TAG).setWeight(25))
                  .add(this.item((ItemLike)TensuraToolItems.SHORT_SPIDER_BOW.get(), randomSource.nextInt(1, 100)).setWeight(10))
                  .add(this.item((ItemLike)TensuraToolItems.SPIDER_BOW.get(), randomSource.nextInt(1, 100)).setWeight(10))
                  .add(this.item((ItemLike)TensuraToolItems.LONG_SPIDER_BOW.get(), randomSource.nextInt(1, 100)).setWeight(5))
                  .add(this.item(Items.AIR).setWeight(50))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(2, 4))
                  .add(this.item(Items.RAW_IRON).setWeight(20))
                  .add(this.item(Items.RAW_GOLD).setWeight(20))
                  .add(this.item((ItemLike)TensuraMaterialItems.RAW_SILVER.get()).setWeight(20))
                  .add(this.item(Items.AIR).setWeight(40))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(this.item((ItemLike)TensuraSmithingSchematicItems.WEB_GUN.get()).setWeight(20))
                  .add(this.item(Items.STRING).setWeight(26))
                  .add(this.item((ItemLike)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get()).setWeight(2))
                  .add(this.item(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
                  .add(this.item(Items.AIR).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.BURIED_WIZARD_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(15))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(70)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.COMMON_TOME_BURIED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(30)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.UNCOMMON_TOME_BURIED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_TOME_BURIED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(3)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.EPIC_TOME_TOWER))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(70))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_C.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_B.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_A.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.BROWN_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.BURNT_WIZARD_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(15))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(70)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.COMMON_TOME_BURNT))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(30)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.UNCOMMON_TOME_BURNT))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_TOME_BURNT))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(3)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.EPIC_TOME_TOWER))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(70))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_C.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_B.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_A.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.RED_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.FROZEN_WIZARD_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(15))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(70)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.COMMON_TOME_FROZEN))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(30)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.UNCOMMON_TOME_FROZEN))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_TOME_FROZEN))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(3)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.EPIC_TOME_TOWER))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(70))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_C.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_B.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_A.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.BLACK_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.ROTTED_WIZARD_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(15))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(70)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.COMMON_TOME_ROTTED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(30)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.UNCOMMON_TOME_ROTTED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_TOME_ROTTED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(3)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.EPIC_TOME_TOWER))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(70))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_C.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_B.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_A.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.PURPLE_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
            )
      );
      consumer.accept(
         TensuraChestLoot.RUINED_WIZARD_TOWER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(4, 8))
                  .add(this.item((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).setWeight(35))
                  .add(this.item(Items.BOOK).setWeight(35))
                  .add(this.item(Items.PAPER).setWeight(35))
                  .add(this.item(Items.WRITABLE_BOOK).setWeight(15))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .setWeight(30)
                        .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(1, 3))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(70)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.COMMON_TOME_RUINED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(30)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.UNCOMMON_TOME_RUINED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(10)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.RARE_TOME_RUINED))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .setWeight(3)
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.EPIC_TOME_TOWER))
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(5))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(0, 1))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.UNBOUND_TOME.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_D.get()).setWeight(70))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_C.get()).setWeight(30))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_B.get()).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)TensuraToolItems.GRIMOIRE_A.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(this.between(3, 7))
                  .add(this.item(Items.AMETHYST_SHARD).setWeight(50))
                  .add(this.item(Items.GREEN_CANDLE).setWeight(25))
                  .add(this.item(Items.WHITE_CANDLE).setWeight(25))
                  .add(this.item(Items.LAPIS_LAZULI).setWeight(50))
                  .add(this.item(Items.REDSTONE).setWeight(50))
                  .add(this.item(Items.GLOWSTONE).setWeight(50))
            )
      );
   }

   private NumberProvider exactly(int number) {
      if (number <= 0) {
         throw new IllegalArgumentException("Number has to be greater than zero.");
      } else {
         return ConstantValue.exactly(number);
      }
   }

   private NumberProvider between(int min, int max) {
      if (min > max) {
         throw new IllegalArgumentException("Min cannot be greater than Max.");
      } else {
         return UniformGenerator.between(min, max);
      }
   }

   private net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder<?> item(ItemLike item) {
      return LootItem.lootTableItem(item);
   }

   private net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder<?> item(ItemLike item, int durability) {
      if (item.asItem().getDefaultInstance().getMaxDamage() >= durability && durability > 0) {
         Item item1 = item.asItem();
         item1.setDamage(item.asItem().getDefaultInstance(), durability);
         return LootItem.lootTableItem(item1);
      } else {
         return LootItem.lootTableItem(item);
      }
   }
}
