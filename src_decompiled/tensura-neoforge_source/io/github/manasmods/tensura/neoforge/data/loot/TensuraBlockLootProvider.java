package io.github.manasmods.tensura.neoforge.data.loot;

import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

public class TensuraBlockLootProvider extends BlockLootSubProvider {
   public TensuraBlockLootProvider(Provider lookup) {
      super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
   }

   protected void generate() {
      RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      this.dropSelf((Block)TensuraBlocks.PALM_SAPLING.get());
      this.add(
         (Block)TensuraBlocks.PALM_LEAVES.get(), block -> this.createLeavesDrops(block, (Block)TensuraBlocks.PALM_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES)
      );
      this.dropSelf((Block)TensuraBlocks.PALM_LOG.get());
      this.dropSelf((Block)TensuraBlocks.PALM_WOOD.get());
      this.dropSelf((Block)TensuraBlocks.STRIPPED_PALM_LOG.get());
      this.dropSelf((Block)TensuraBlocks.STRIPPED_PALM_WOOD.get());
      this.dropSelf((Block)TensuraBlocks.PALM_PLANKS.get());
      this.dropSelf((Block)TensuraBlocks.PALM_STAIRS.get());
      this.add((Block)TensuraBlocks.PALM_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)TensuraBlocks.PALM_DOOR.get(), x$0 -> this.createDoorTable(x$0));
      this.dropSelf((Block)TensuraBlocks.PALM_TRAPDOOR.get());
      this.dropSelf((Block)TensuraBlocks.PALM_FENCE.get());
      this.dropSelf((Block)TensuraBlocks.PALM_FENCE_GATE.get());
      this.dropSelf((Block)TensuraBlocks.PALM_BUTTON.get());
      this.dropSelf((Block)TensuraBlocks.PALM_PRESSURE_PLATE.get());
      this.dropSelf((Block)TensuraBlocks.PALM_STANDING_SIGN.get());
      this.dropSelf((Block)TensuraBlocks.PALM_WALL_SIGN.get());
      this.dropSelf((Block)TensuraBlocks.PALM_HANGING_SIGN.get());
      this.dropSelf((Block)TensuraBlocks.PALM_WALL_HANGING_SIGN.get());
      this.dropSelf((Block)TensuraBlocks.THATCH_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.THATCH_STAIRS.get());
      this.add((Block)TensuraBlocks.THATCH_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.THATCH_WALL.get());
      this.dropSelf((Block)TensuraBlocks.THATCH_BED.get());
      this.dropSelf((Block)TensuraBlocks.TRAINING_DUMMY.get());
      this.dropSelf((Block)TensuraBlocks.TATAMI_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.TATAMI_CARPET.get());
      this.dropSelf((Block)TensuraBlocks.SINGLE_TATAMI_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.SINGLE_TATAMI_CARPET.get());
      this.dropSelf((Block)TensuraBlocks.KILN.get());
      this.dropSelf((Block)TensuraBlocks.KILN_MITHRIL.get());
      this.dropSelf((Block)TensuraBlocks.KILN_ORICHALCUM.get());
      this.dropSelf((Block)TensuraBlocks.MINING_STATION.get());
      this.dropSelf((Block)TensuraBlocks.SMITHING_BENCH.get());
      this.dropSelf((Block)TensuraBlocks.SPELLBINDING_TABLE.get());
      this.dropSelf((Block)TensuraBlocks.WOODCUTTER.get());
      this.dropSelf((Block)TensuraBlocks.OAK_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.SPRUCE_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.BIRCH_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.JUNGLE_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.ACACIA_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.DARK_OAK_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.MANGROVE_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.CHERRY_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.PALM_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.BAMBOO_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.CRIMSON_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.WARPED_TOOL_RACK.get());
      this.dropSelf((Block)TensuraBlocks.STONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.GRANITE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.DIORITE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.ANDESITE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.CALCITE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.TUFF_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.DEEPSLATE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.BRICK_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.SANDSTONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.RED_SANDSTONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.SARASA_SANDSTONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.PACKED_MUD_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.PRISMARINE_BRICK_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.NETHER_BRICK_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.RED_NETHER_BRICK_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.BLACKSTONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.BASALT_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.QUARTZ_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.END_STONE_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.PURPUR_WARP_PAD.get());
      this.dropSelf((Block)TensuraBlocks.BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get());
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get());
      this.add((Block)TensuraBlocks.MAGIC_ORE.get(), block -> this.createOreDrop(block, (Item)TensuraMaterialItems.MAGIC_ORE.get()));
      this.add((Block)TensuraBlocks.DEEPSLATE_MAGIC_ORE.get(), block -> this.createOreDrop(block, (Item)TensuraMaterialItems.MAGIC_ORE.get()));
      this.add((Block)TensuraBlocks.SILVER_ORE.get(), block -> this.createOreDrop(block, (Item)TensuraMaterialItems.RAW_SILVER.get()));
      this.add((Block)TensuraBlocks.DEEPSLATE_SILVER_ORE.get(), block -> this.createOreDrop(block, (Item)TensuraMaterialItems.RAW_SILVER.get()));
      this.dropSelf((Block)TensuraBlocks.RAW_SILVER_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.SILVER_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.MAGIC_ORE_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.LOW_MAGISTEEL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.HIGH_MAGISTEEL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.PURE_MAGISTEEL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.MITHRIL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.ORICHALCUM_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.ADAMANTITE_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.HIHIIROKANE_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS.get());
      this.add((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get());
      this.add((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get());
      this.dropSelf((Block)TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS.get());
      this.add((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get());
      this.add((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get());
      this.dropSelf((Block)TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS.get());
      this.add((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get());
      this.add((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get());
      this.dropSelf((Block)TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.SARASA_SAND.get());
      this.dropSelf((Block)TensuraBlocks.SARASA_SANDSTONE.get());
      this.dropSelf((Block)TensuraBlocks.SARASA_SANDSTONE_STAIRS.get());
      this.add((Block)TensuraBlocks.SARASA_SANDSTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.SARASA_SANDSTONE_WALL.get());
      this.dropSelf((Block)TensuraBlocks.CHISELED_SARASA_SANDSTONE.get());
      this.dropSelf((Block)TensuraBlocks.CUT_SARASA_SANDSTONE.get());
      this.add((Block)TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.SMOOTH_SARASA_SANDSTONE.get());
      this.dropSelf((Block)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS.get());
      this.add((Block)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.SLIME_CHUNK_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.CHILLED_SLIME_BLOCK.get());
      this.add(
         (Block)TensuraBlocks.CHARYBDIS_CORE.get(),
         block -> LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(block)
                        .apply(CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY).include((DataComponentType)TensuraDataComponents.EP_DURABILITY.get()))
                        .apply(CopyBlockState.copyState(block).copy(CharybdisCoreBlock.MODE))
                  )
            )
      );
      this.add(
         (Block)TensuraBlocks.SPIDER_EGG.get(),
         block -> this.createSilkTouchDispatchTable(
            block,
            (Builder)this.applyExplosionDecay(
               block,
               LootItem.lootTableItem((ItemLike)TensuraMobDropItems.STICKY_THREAD.get())
                  .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                  .apply(LimitCount.limitCount(IntRange.range(1, 3)))
            )
         )
      );
      this.add(
         (Block)TensuraBlocks.MOTH_EGG.get(),
         block -> this.createSilkTouchDispatchTable(
            block,
            (Builder)this.applyExplosionDecay(
               block,
               LootItem.lootTableItem((ItemLike)TensuraMobDropItems.HELL_MOTH_SILK.get())
                  .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  .apply(LimitCount.limitCount(IntRange.range(0, 3)))
            )
         )
      );
      this.dropSelf((Block)TensuraBlocks.WEB_BLOCK.get());
      this.dropSelf((Block)TensuraBlocks.WEB_STAIRS.get());
      this.add((Block)TensuraBlocks.WEB_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.WEBBED_COBBLESTONE.get());
      this.dropSelf((Block)TensuraBlocks.WEBBED_COBBLESTONE_STAIRS.get());
      this.add((Block)TensuraBlocks.WEBBED_COBBLESTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.WEBBED_COBBLESTONE_WALL.get());
      this.dropSelf((Block)TensuraBlocks.WEBBED_STONE_BRICKS.get());
      this.dropSelf((Block)TensuraBlocks.WEBBED_STONE_BRICK_STAIRS.get());
      this.add((Block)TensuraBlocks.WEBBED_STONE_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)TensuraBlocks.WEBBED_STONE_BRICK_WALL.get());
      this.dropSelf((Block)TensuraBlocks.LOOSE_DIRT.get());
      this.dropSelf((Block)TensuraBlocks.LOOSE_GRAVEL.get());
      this.dropSelf((Block)TensuraBlocks.QUICKMUD.get());
      this.dropSelf((Block)TensuraBlocks.QUICKSAND.get());
      this.dropSelf((Block)TensuraBlocks.RED_QUICKSAND.get());
      this.dropSelf((Block)TensuraBlocks.SARASA_QUICKSAND.get());
      this.dropPottedContents((Block)TensuraBlocks.POTTED_PALM_SAPLING.get());
      this.dropPottedContents((Block)TensuraBlocks.POTTED_HIPOKUTE_FLOWER.get());
      this.dropPottedContents((Block)TensuraBlocks.POTTED_BAFFLEDIL.get());
      this.dropSelf((Block)TensuraBlocks.BAFFLEDIL.get());
      this.add(
         (Block)TensuraBlocks.HIPOKUTE_GRASS.get(),
         block -> (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
            (ItemLike)TensuraBlocks.HIPOKUTE_GRASS.get(),
            LootTable.lootTable()
               .withPool(
                  LootPool.lootPool()
                     .add(
                        ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                 (ItemLike)TensuraMaterialItems.HIPOKUTE_SEEDS.get()
                              )
                              .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 5.0F)))
                              .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)TensuraBlocks.HIPOKUTE_GRASS.get())
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 3)
                                    )
                              ))
                           .otherwise(
                              ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                       (ItemLike)TensuraMaterialItems.HIPOKUTE_SEEDS.get()
                                    )
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                                    .when(
                                       LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)TensuraBlocks.HIPOKUTE_GRASS.get())
                                          .setProperties(
                                             net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 2)
                                          )
                                    ))
                                 .otherwise(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.HIPOKUTE_SEEDS.get()))
                           )
                     )
               )
               .withPool(
                  LootPool.lootPool()
                     .add(
                        LootItem.lootTableItem((ItemLike)TensuraMaterialItems.HIPOKUTE_GRASS.get())
                           .when(
                              LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)TensuraBlocks.HIPOKUTE_GRASS.get())
                                 .setProperties(
                                    net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 2)
                                 )
                           )
                     )
               )
               .withPool(
                  LootPool.lootPool()
                     .add(
                        LootItem.lootTableItem((ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get())
                           .when(
                              LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)TensuraBlocks.HIPOKUTE_GRASS.get())
                                 .setProperties(
                                    net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 3)
                                 )
                           )
                     )
               )
         )
      );
      this.add(
         (Block)TensuraBlocks.ORC_DISASTER_HEAD.get(),
         block -> LootTable.lootTable()
            .withPool(
               LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ORC_DISASTER_HEAD.get()))
            )
      );
   }

   @NotNull
   protected Iterable<Block> getKnownBlocks() {
      return BuiltInRegistries.BLOCK
         .stream()
         .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("tensura"))
         .collect(Collectors.toList());
   }
}
