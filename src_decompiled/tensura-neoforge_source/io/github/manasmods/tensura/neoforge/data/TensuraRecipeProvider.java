package io.github.manasmods.tensura.neoforge.data;

import com.google.common.collect.ImmutableList;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.recipe.TensuraKilnMoltenMaterials;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.KilnMixingRecipe;
import io.github.manasmods.tensura.recipe.MiningStationRecipe;
import io.github.manasmods.tensura.recipe.RefiningRecipe;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.recipe.WoodcutterRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraPotions;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraRecipeProvider extends RecipeProvider {
   public TensuraRecipeProvider(PackOutput output, CompletableFuture<Provider> completableFuture) {
      super(output, completableFuture);
   }

   protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
      this.blocksAndItems(recipeOutput);
      this.gear(recipeOutput);
      this.kilnMelting(recipeOutput);
      this.kilnMixing(recipeOutput);
      this.refining(recipeOutput);
      this.smeltingRecipes(recipeOutput);
      this.smithingRecipes(recipeOutput);
      this.smithingUpgrades(recipeOutput);
      this.stoneCutter(recipeOutput);
      this.woodCutter(recipeOutput);
      this.miningStation(recipeOutput);
   }

   public static ResourceLocation getRecipeName(ItemLike itemLike, String postFix) {
      ResourceLocation location = RecipeBuilder.getDefaultRecipeId(itemLike.asItem());
      return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + postFix);
   }

   private void kilnMelting(RecipeOutput recipeOutput) {
      kilnMeltingOres(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_COPPER,
         "copper",
         Items.COPPER_INGOT,
         Items.RAW_COPPER,
         ItemTags.COPPER_ORES,
         Items.COPPER_BLOCK,
         Items.RAW_COPPER_BLOCK
      );
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 54)
         .requires(Ingredient.of(new ItemLike[]{Items.COPPER_DOOR}))
         .smeltTick(400)
         .build(recipeOutput, "copper_door");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 27)
         .requires(Ingredient.of(new ItemLike[]{Items.COPPER_TRAPDOOR}))
         .smeltTick(200)
         .build(recipeOutput, "copper_trapdoor");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 60)
         .requires(Ingredient.of(new ItemLike[]{Items.COPPER_BULB}))
         .smeltTick(450)
         .build(recipeOutput, "copper_bulb");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 27)
         .requires(Ingredient.of(new ItemLike[]{Items.LIGHTNING_ROD}))
         .smeltTick(200)
         .build(recipeOutput, "lightning_rod");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 81)
         .requires(Ingredient.of(new ItemLike[]{Items.CHISELED_COPPER}))
         .smeltTick(600)
         .build(recipeOutput, "chiseled_copper_block");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 81)
         .requires(Ingredient.of(new ItemLike[]{Items.COPPER_GRATE}))
         .smeltTick(600)
         .build(recipeOutput, "copper_grate");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 81)
         .requires(Ingredient.of(new ItemLike[]{Items.CUT_COPPER}))
         .smeltTick(600)
         .build(recipeOutput, "cut_copper_block");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 54)
         .requires(Ingredient.of(new ItemLike[]{Items.CUT_COPPER_STAIRS}))
         .smeltTick(400)
         .build(recipeOutput, "cut_copper_stairs");
      KilnMeltingRecipe.Builder.of(TensuraKilnMoltenMaterials.MOLTEN_COPPER, 40)
         .requires(Ingredient.of(new ItemLike[]{Items.CUT_COPPER_SLAB}))
         .smeltTick(300)
         .build(recipeOutput, "cut_copper_slab");
      kilnMeltingOres(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_GOLD,
         "gold",
         Items.GOLD_NUGGET,
         Items.GOLD_INGOT,
         Items.RAW_GOLD,
         ItemTags.GOLD_ORES,
         Items.GOLD_BLOCK,
         Items.RAW_GOLD_BLOCK
      );
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_GOLD,
         "gold",
         Items.GOLDEN_HELMET,
         Items.GOLDEN_CHESTPLATE,
         Items.GOLDEN_LEGGINGS,
         Items.GOLDEN_BOOTS,
         Items.GOLDEN_PICKAXE,
         Items.GOLDEN_AXE,
         Items.GOLDEN_SHOVEL,
         Items.GOLDEN_HOE,
         (ItemLike)TensuraToolItems.GOLDEN_SICKLE.get(),
         Items.GOLDEN_SWORD,
         (ItemLike)TensuraToolItems.GOLDEN_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.GOLDEN_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.GOLDEN_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.GOLDEN_KATANA.get(),
         (ItemLike)TensuraToolItems.GOLDEN_KODACHI.get(),
         (ItemLike)TensuraToolItems.GOLDEN_TACHI.get(),
         (ItemLike)TensuraToolItems.GOLDEN_ODACHI.get(),
         (ItemLike)TensuraToolItems.GOLDEN_SPEAR.get(),
         (ItemLike)TensuraToolItems.GOLDEN_SCYTHE.get(),
         2,
         1
      );
      kilnMeltingOres(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         "iron",
         Items.IRON_NUGGET,
         Items.IRON_INGOT,
         Items.RAW_IRON,
         ItemTags.IRON_ORES,
         Items.IRON_BLOCK,
         Items.RAW_IRON_BLOCK
      );
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         "iron",
         Items.IRON_HELMET,
         Items.IRON_CHESTPLATE,
         Items.IRON_LEGGINGS,
         Items.IRON_BOOTS,
         Items.IRON_PICKAXE,
         Items.IRON_AXE,
         Items.IRON_SHOVEL,
         Items.IRON_HOE,
         (ItemLike)TensuraToolItems.IRON_SICKLE.get(),
         Items.IRON_SWORD,
         (ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.IRON_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.IRON_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.IRON_KATANA.get(),
         (ItemLike)TensuraToolItems.IRON_KODACHI.get(),
         (ItemLike)TensuraToolItems.IRON_TACHI.get(),
         (ItemLike)TensuraToolItems.IRON_ODACHI.get(),
         (ItemLike)TensuraToolItems.IRON_SPEAR.get(),
         (ItemLike)TensuraToolItems.IRON_SCYTHE.get(),
         2,
         1
      );
      kilnMeltingOres(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_SILVER,
         "silver",
         (ItemLike)TensuraMaterialItems.SILVER_NUGGET.get(),
         (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(),
         (ItemLike)TensuraMaterialItems.RAW_SILVER.get(),
         TensuraItemTags.SILVER_ORES,
         (ItemLike)TensuraBlocks.Items.SILVER_BLOCK.get(),
         (ItemLike)TensuraBlocks.Items.RAW_SILVER_BLOCK.get()
      );
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_SILVER,
         "silver",
         (ItemLike)TensuraArmorItems.SILVER_HELMET.get(),
         (ItemLike)TensuraArmorItems.SILVER_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.SILVER_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.SILVER_BOOTS.get(),
         (ItemLike)TensuraToolItems.SILVER_PICKAXE.get(),
         (ItemLike)TensuraToolItems.SILVER_AXE.get(),
         (ItemLike)TensuraToolItems.SILVER_SHOVEL.get(),
         (ItemLike)TensuraToolItems.SILVER_HOE.get(),
         (ItemLike)TensuraToolItems.SILVER_SICKLE.get(),
         (ItemLike)TensuraToolItems.SILVER_SWORD.get(),
         (ItemLike)TensuraToolItems.SILVER_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.SILVER_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.SILVER_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.SILVER_KATANA.get(),
         (ItemLike)TensuraToolItems.SILVER_KODACHI.get(),
         (ItemLike)TensuraToolItems.SILVER_TACHI.get(),
         (ItemLike)TensuraToolItems.SILVER_ODACHI.get(),
         (ItemLike)TensuraToolItems.SILVER_SPEAR.get(),
         (ItemLike)TensuraToolItems.SILVER_SCYTHE.get(),
         2,
         1
      );
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 4, Items.NETHERITE_SCRAP, 50);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 4, Items.ANCIENT_DEBRIS, 75);
      this.kilnDoubleMelting(
         recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 16, TensuraKilnMoltenMaterials.MOLTEN_GOLD, 36, Items.NETHERITE_INGOT, 100
      );
      this.kilnDoubleMelting(
         recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 144, TensuraKilnMoltenMaterials.MOLTEN_GOLD, 324, Items.NETHERITE_BLOCK, 500
      );
      kilnDoubleMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_NETHERITE,
         TensuraKilnMoltenMaterials.MOLTEN_GOLD,
         "netherite",
         Items.NETHERITE_HELMET,
         Items.NETHERITE_CHESTPLATE,
         Items.NETHERITE_LEGGINGS,
         Items.NETHERITE_BOOTS,
         Items.NETHERITE_PICKAXE,
         Items.NETHERITE_AXE,
         Items.NETHERITE_SHOVEL,
         Items.NETHERITE_HOE,
         (ItemLike)TensuraToolItems.NETHERITE_SICKLE.get(),
         Items.NETHERITE_SWORD,
         (ItemLike)TensuraToolItems.NETHERITE_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.NETHERITE_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.NETHERITE_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.NETHERITE_KATANA.get(),
         (ItemLike)TensuraToolItems.NETHERITE_KODACHI.get(),
         (ItemLike)TensuraToolItems.NETHERITE_TACHI.get(),
         (ItemLike)TensuraToolItems.NETHERITE_ODACHI.get(),
         (ItemLike)TensuraToolItems.NETHERITE_SPEAR.get(),
         (ItemLike)TensuraToolItems.NETHERITE_SCYTHE.get(),
         4,
         1,
         8,
         4
      );
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, ((Block)TensuraBlocks.MAGIC_ORE.get()).asItem(), 120);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, ((Block)TensuraBlocks.DEEPSLATE_MAGIC_ORE.get()).asItem(), 120);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, (Item)TensuraMaterialItems.MAGIC_ORE.get(), 100);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 9, ((Block)TensuraBlocks.MAGIC_ORE_BLOCK.get()).asItem(), 600);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, (Item)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(), 40);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 2, (Item)TensuraMaterialItems.MITHRIL_NUGGET.get(), 60);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 2, (Item)TensuraMaterialItems.ORICHALCUM_NUGGET.get(), 60);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 4, (Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(), 60);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 36, (Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), 300);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 324, (Item)TensuraBlocks.Items.PURE_MAGISTEEL_BLOCK.get(), 1500);
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         "pure_magisteel",
         (ItemLike)TensuraArmorItems.PURE_MAGISTEEL_HELMET.get(),
         (ItemLike)TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.PURE_MAGISTEEL_BOOTS.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_PICKAXE.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_AXE.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SHOVEL.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_HOE.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SICKLE.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SWORD.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_KATANA.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_KODACHI.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_TACHI.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_ODACHI.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SPEAR.get(),
         (ItemLike)TensuraToolItems.PURE_MAGISTEEL_SCYTHE.get(),
         8,
         4
      );
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, (Item)TensuraToolItems.KANABO.get(), 75);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 1, (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(), 100);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 4, (Item)TensuraToolItems.DRAGON_KNUCKLE.get(), 300);
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         4,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         8,
         (Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
         200
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         36,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         72,
         (Item)TensuraBlocks.Items.LOW_MAGISTEEL_BLOCK.get(),
         1000
      );
      kilnDoubleMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         "low_magisteel",
         (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_HELMET.get(),
         (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_BOOTS.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_PICKAXE.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_AXE.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SHOVEL.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_HOE.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SICKLE.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SWORD.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_KATANA.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_KODACHI.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_TACHI.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_ODACHI.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SPEAR.get(),
         (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SCYTHE.get(),
         1,
         0,
         2,
         1
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         16,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         5,
         (Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
         250
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         144,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         45,
         (Item)TensuraBlocks.Items.HIGH_MAGISTEEL_BLOCK.get(),
         1250
      );
      kilnDoubleMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         TensuraKilnMoltenMaterials.MOLTEN_IRON,
         "high_magisteel",
         (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_HELMET.get(),
         (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_PICKAXE.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_AXE.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SHOVEL.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_HOE.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SICKLE.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SWORD.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_KATANA.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_KODACHI.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_TACHI.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_ODACHI.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SPEAR.get(),
         (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SCYTHE.get(),
         4,
         2,
         1,
         0
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         20,
         TensuraKilnMoltenMaterials.MOLTEN_SILVER,
         4,
         (Item)TensuraMaterialItems.MITHRIL_INGOT.get(),
         300
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         180,
         TensuraKilnMoltenMaterials.MOLTEN_SILVER,
         36,
         (Item)TensuraBlocks.Items.MITHRIL_BLOCK.get(),
         1500
      );
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         "mithril",
         (ItemLike)TensuraArmorItems.MITHRIL_HELMET.get(),
         (ItemLike)TensuraArmorItems.MITHRIL_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.MITHRIL_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.MITHRIL_BOOTS.get(),
         (ItemLike)TensuraToolItems.MITHRIL_PICKAXE.get(),
         (ItemLike)TensuraToolItems.MITHRIL_AXE.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SHOVEL.get(),
         (ItemLike)TensuraToolItems.MITHRIL_HOE.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SICKLE.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SWORD.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.MITHRIL_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.MITHRIL_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.MITHRIL_KATANA.get(),
         (ItemLike)TensuraToolItems.MITHRIL_KODACHI.get(),
         (ItemLike)TensuraToolItems.MITHRIL_TACHI.get(),
         (ItemLike)TensuraToolItems.MITHRIL_ODACHI.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SPEAR.get(),
         (ItemLike)TensuraToolItems.MITHRIL_SCYTHE.get(),
         4,
         2
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         20,
         TensuraKilnMoltenMaterials.MOLTEN_GOLD,
         4,
         (Item)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
         300
      );
      this.kilnDoubleMelting(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         180,
         TensuraKilnMoltenMaterials.MOLTEN_GOLD,
         36,
         (Item)TensuraBlocks.Items.ORICHALCUM_BLOCK.get(),
         1500
      );
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         "orichalcum",
         (ItemLike)TensuraArmorItems.ORICHALCUM_HELMET.get(),
         (ItemLike)TensuraArmorItems.ORICHALCUM_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.ORICHALCUM_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.ORICHALCUM_BOOTS.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_PICKAXE.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_AXE.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SHOVEL.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_HOE.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SICKLE.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SWORD.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_KATANA.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_KODACHI.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_TACHI.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_ODACHI.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SPEAR.get(),
         (ItemLike)TensuraToolItems.ORICHALCUM_SCYTHE.get(),
         4,
         2
      );
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 8, (Item)TensuraMaterialItems.ADAMANTITE_NUGGET.get(), 100);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 72, (Item)TensuraMaterialItems.ADAMANTITE_INGOT.get(), 400);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 648, (Item)TensuraBlocks.Items.ADAMANTITE_BLOCK.get(), 2000);
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         "adamantite",
         (ItemLike)TensuraArmorItems.ADAMANTITE_HELMET.get(),
         (ItemLike)TensuraArmorItems.ADAMANTITE_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.ADAMANTITE_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.ADAMANTITE_BOOTS.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_PICKAXE.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_AXE.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SHOVEL.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_HOE.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SICKLE.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SWORD.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_KATANA.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_KODACHI.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_TACHI.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_ODACHI.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SPEAR.get(),
         (ItemLike)TensuraToolItems.ADAMANTITE_SCYTHE.get(),
         16,
         8
      );
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 12, (Item)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(), 150);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 108, (Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get(), 500);
      this.kilnMelting(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 972, (Item)TensuraBlocks.Items.HIHIIROKANE_BLOCK.get(), 2500);
      kilnMeltingGears(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL,
         "hihiirokane",
         (ItemLike)TensuraArmorItems.HIHIIROKANE_HELMET.get(),
         (ItemLike)TensuraArmorItems.HIHIIROKANE_CHESTPLATE.get(),
         (ItemLike)TensuraArmorItems.HIHIIROKANE_LEGGINGS.get(),
         (ItemLike)TensuraArmorItems.HIHIIROKANE_BOOTS.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_PICKAXE.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_AXE.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SHOVEL.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_HOE.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SICKLE.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SWORD.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SHORT_SWORD.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_LONG_SWORD.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_GREAT_SWORD.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_KATANA.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_KODACHI.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_TACHI.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_ODACHI.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SPEAR.get(),
         (ItemLike)TensuraToolItems.HIHIIROKANE_SCYTHE.get(),
         24,
         12
      );
   }

   protected void kilnMelting(RecipeOutput recipeOutput, ResourceLocation moltenType, int amount, TagKey<Item> input, int smeltTick, String path) {
      KilnMeltingRecipe.Builder.of(moltenType, amount).requires(Ingredient.of(input)).smeltTick(smeltTick).build(recipeOutput, path);
   }

   protected void kilnMelting(RecipeOutput recipeOutput, ResourceLocation moltenType, int amount, Item input, int smeltTick) {
      KilnMeltingRecipe.Builder.of(moltenType, amount)
         .requires(Ingredient.of(new ItemLike[]{input}))
         .smeltTick(smeltTick)
         .build(recipeOutput, BuiltInRegistries.ITEM.getKey(input).getPath());
   }

   protected static void kilnMeltingOres(
      RecipeOutput recipeOutput,
      ResourceLocation moltenType,
      String filePrefix,
      ItemLike nugget,
      ItemLike ingot,
      ItemLike raw,
      TagKey<Item> ore,
      ItemLike block,
      ItemLike rawBlock
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, 1).requires(Ingredient.of(new ItemLike[]{nugget})).smeltTick(20).build(recipeOutput, filePrefix + "_nuggets");
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(new ItemLike[]{ingot})).smeltTick(100).build(recipeOutput, filePrefix + "_ingots");
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(new ItemLike[]{raw})).smeltTick(120).build(recipeOutput, filePrefix + "_raw");
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(ore)).smeltTick(200).build(recipeOutput, filePrefix + "_ores");
      KilnMeltingRecipe.Builder.of(moltenType, 81).requires(Ingredient.of(new ItemLike[]{block})).smeltTick(600).build(recipeOutput, filePrefix + "_blocks");
      KilnMeltingRecipe.Builder.of(moltenType, 81)
         .requires(Ingredient.of(new ItemLike[]{rawBlock}))
         .smeltTick(900)
         .build(recipeOutput, filePrefix + "_raw_blocks");
   }

   protected static void kilnMeltingOres(
      RecipeOutput recipeOutput,
      ResourceLocation moltenType,
      String filePrefix,
      ItemLike ingot,
      ItemLike raw,
      TagKey<Item> ore,
      ItemLike block,
      ItemLike rawBlock
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(new ItemLike[]{ingot})).smeltTick(100).build(recipeOutput, filePrefix + "_ingots");
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(new ItemLike[]{raw})).smeltTick(120).build(recipeOutput, filePrefix + "_raw");
      KilnMeltingRecipe.Builder.of(moltenType, 9).requires(Ingredient.of(ore)).smeltTick(200).build(recipeOutput, filePrefix + "_ores");
      KilnMeltingRecipe.Builder.of(moltenType, 81).requires(Ingredient.of(new ItemLike[]{block})).smeltTick(600).build(recipeOutput, filePrefix + "_blocks");
      KilnMeltingRecipe.Builder.of(moltenType, 81)
         .requires(Ingredient.of(new ItemLike[]{rawBlock}))
         .smeltTick(900)
         .build(recipeOutput, filePrefix + "_raw_blocks");
   }

   protected static void kilnMeltingGears(
      RecipeOutput recipeOutput,
      ResourceLocation moltenType,
      String filePrefix,
      ItemLike helmet,
      ItemLike chestplate,
      ItemLike leggings,
      ItemLike boots,
      ItemLike pickaxe,
      ItemLike axe,
      ItemLike shovel,
      ItemLike hoe,
      ItemLike sickle,
      ItemLike sword,
      ItemLike shortSword,
      ItemLike longSword,
      ItemLike greatSword,
      ItemLike katana,
      ItemLike kodachi,
      ItemLike tachi,
      ItemLike odachi,
      ItemLike spear,
      ItemLike scythe,
      int moltenArmor,
      int moltenTools
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{helmet}))
         .smeltTick(125)
         .build(recipeOutput, filePrefix + "_helmet");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{chestplate}))
         .smeltTick(225)
         .build(recipeOutput, filePrefix + "_chestplate");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{leggings}))
         .smeltTick(175)
         .build(recipeOutput, filePrefix + "_leggings");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{boots}))
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_boots");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{pickaxe}))
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_pickaxe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools).requires(Ingredient.of(new ItemLike[]{axe})).smeltTick(75).build(recipeOutput, filePrefix + "_axe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{shovel}))
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_shovel");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools).requires(Ingredient.of(new ItemLike[]{hoe})).smeltTick(50).build(recipeOutput, filePrefix + "_hoe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{sickle}))
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_sickle");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{sword}))
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{shortSword}))
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_short_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{longSword}))
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_long_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{greatSword}))
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_great_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{katana}))
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_katana");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{kodachi}))
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_kodachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{tachi}))
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_tachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{odachi}))
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_odachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{spear}))
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_spear");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{spear}))
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_scythe");
   }

   protected void kilnDoubleMelting(
      RecipeOutput recipeOutput, ResourceLocation moltenType, int amount, ResourceLocation secondaryType, int secondaryAmount, Item input, int smeltTick
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, amount)
         .requires(Ingredient.of(new ItemLike[]{input}))
         .smeltTick(smeltTick)
         .inputSecondary(secondaryType, secondaryAmount)
         .build(recipeOutput, BuiltInRegistries.ITEM.getKey(input).getPath());
   }

   protected void kilnDoubleMelting(
      RecipeOutput recipeOutput,
      ResourceLocation moltenType,
      int amount,
      ResourceLocation secondaryType,
      int secondaryAmount,
      TagKey<Item> input,
      int smeltTick,
      String path
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, amount)
         .requires(Ingredient.of(input))
         .smeltTick(smeltTick)
         .inputSecondary(secondaryType, secondaryAmount)
         .build(recipeOutput, path);
   }

   protected static void kilnDoubleMeltingGears(
      RecipeOutput recipeOutput,
      ResourceLocation moltenType,
      ResourceLocation secondType,
      String filePrefix,
      ItemLike helmet,
      ItemLike chestplate,
      ItemLike leggings,
      ItemLike boots,
      ItemLike pickaxe,
      ItemLike axe,
      ItemLike shovel,
      ItemLike hoe,
      ItemLike sickle,
      ItemLike sword,
      ItemLike shortSword,
      ItemLike longSword,
      ItemLike greatSword,
      ItemLike katana,
      ItemLike kodachi,
      ItemLike tachi,
      ItemLike odachi,
      ItemLike spear,
      ItemLike scythe,
      int moltenArmor,
      int moltenTools,
      int secondMoltenArmor,
      int secondMoltenTools
   ) {
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{helmet}))
         .inputSecondary(secondType, secondMoltenArmor)
         .smeltTick(125)
         .build(recipeOutput, filePrefix + "_helmet");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{chestplate}))
         .inputSecondary(secondType, secondMoltenArmor)
         .smeltTick(225)
         .build(recipeOutput, filePrefix + "_chestplate");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{leggings}))
         .inputSecondary(secondType, secondMoltenArmor)
         .smeltTick(175)
         .build(recipeOutput, filePrefix + "_leggings");
      KilnMeltingRecipe.Builder.of(moltenType, moltenArmor)
         .requires(Ingredient.of(new ItemLike[]{boots}))
         .smeltTick(100)
         .inputSecondary(secondType, secondMoltenArmor)
         .build(recipeOutput, filePrefix + "_boots");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{pickaxe}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_pickaxe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{axe}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_axe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{shovel}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_shovel");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{hoe}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_hoe");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{sickle}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_sickle");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{sword}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{shortSword}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_short_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{longSword}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_long_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{greatSword}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_great_sword");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{katana}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_katana");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{kodachi}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(50)
         .build(recipeOutput, filePrefix + "_kodachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{tachi}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_tachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{odachi}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_odachi");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{spear}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(75)
         .build(recipeOutput, filePrefix + "_spear");
      KilnMeltingRecipe.Builder.of(moltenType, moltenTools)
         .requires(Ingredient.of(new ItemLike[]{scythe}))
         .inputSecondary(secondType, secondMoltenTools)
         .smeltTick(100)
         .build(recipeOutput, filePrefix + "_scythe");
   }

   private void kilnMixing(RecipeOutput recipeOutput) {
      kilnMixingLeft(recipeOutput, Items.COPPER_INGOT.getDefaultInstance(), TensuraKilnMoltenMaterials.MOLTEN_COPPER, 9);
      kilnMixingLeft(recipeOutput, Items.COPPER_BLOCK.getDefaultInstance(), TensuraKilnMoltenMaterials.MOLTEN_COPPER, 81);
      kilnMixingLeftOres(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_GOLD, Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.GOLD_BLOCK);
      kilnMixingLeftOres(recipeOutput, TensuraKilnMoltenMaterials.MOLTEN_IRON, Items.IRON_NUGGET, Items.IRON_INGOT, Items.IRON_BLOCK);
      kilnMixingLeftOres(
         recipeOutput,
         TensuraKilnMoltenMaterials.MOLTEN_SILVER,
         (Item)TensuraMaterialItems.SILVER_NUGGET.get(),
         (Item)TensuraMaterialItems.SILVER_INGOT.get(),
         ((Block)TensuraBlocks.SILVER_BLOCK.get()).asItem()
      );
      kilnMixingRight(recipeOutput, (Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(), TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 4);
      kilnMixingRight(recipeOutput, (Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 36);
      kilnMixingRight(recipeOutput, (Item)TensuraBlocks.Items.PURE_MAGISTEEL_BLOCK.get(), TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 324);
      kilnMixingRight(recipeOutput, Items.NETHERITE_SCRAP, TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 4);
      KilnMixingRecipe.Builder.of(TensuraMaterialItems.LOW_MAGISTEEL_INGOT)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_IRON, 8)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 4)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(TensuraBlocks.Items.LOW_MAGISTEEL_BLOCK)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_IRON, 72)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 36)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_IRON, 5)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 16)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(TensuraBlocks.Items.HIGH_MAGISTEEL_BLOCK)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_IRON, 45)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 144)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of((Item)TensuraMaterialItems.MITHRIL_INGOT.get())
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_SILVER, 4)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 20)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(TensuraBlocks.Items.MITHRIL_BLOCK)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_SILVER, 36)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 180)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of((Item)TensuraMaterialItems.ORICHALCUM_INGOT.get())
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_GOLD, 4)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 20)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(TensuraBlocks.Items.ORICHALCUM_BLOCK)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_GOLD, 36)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_MAGISTEEL, 180)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(Items.NETHERITE_INGOT)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_GOLD, 36)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 16)
         .build(recipeOutput);
      KilnMixingRecipe.Builder.of(Items.NETHERITE_BLOCK)
         .leftInput(TensuraKilnMoltenMaterials.MOLTEN_GOLD, 324)
         .rightInput(TensuraKilnMoltenMaterials.MOLTEN_NETHERITE, 144)
         .build(recipeOutput);
   }

   protected static void kilnMixingLeftOres(RecipeOutput recipeOutput, ResourceLocation moltenType, Item nugget, Item ingot, Item block) {
      kilnMixingLeft(recipeOutput, nugget.getDefaultInstance(), moltenType, 1);
      kilnMixingLeft(recipeOutput, ingot.getDefaultInstance(), moltenType, 9);
      kilnMixingLeft(recipeOutput, block.getDefaultInstance(), moltenType, 81);
   }

   protected static void kilnMixingLeft(RecipeOutput recipeOutput, ItemStack output, ResourceLocation moltenType, int amount) {
      KilnMixingRecipe.Builder.of(output).leftInput(moltenType, amount).build(recipeOutput);
   }

   protected static void kilnMixingRight(RecipeOutput recipeOutput, Item output, ResourceLocation moltenType, int amount) {
      kilnMixingRight(recipeOutput, output.getDefaultInstance(), moltenType, amount);
   }

   protected static void kilnMixingRight(RecipeOutput recipeOutput, ItemStack output, ResourceLocation moltenType, int amount) {
      KilnMixingRecipe.Builder.of(output).rightInput(moltenType, amount).build(recipeOutput);
   }

   public void refining(RecipeOutput output) {
      RefiningRecipe.Builder.of((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), 2)
         .addInput((Item)TensuraBlocks.Items.MAGIC_ORE_BLOCK.get())
         .build(output);
      RefiningRecipe.Builder.of((Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(), 2).addInput((Item)TensuraMaterialItems.MAGIC_ORE.get()).build(output);
      RefiningRecipe.Builder.of((Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(), 2).addInput((Item)TensuraBlocks.Items.MAGIC_ORE.get()).build(output);
      RefiningRecipe.Builder.of((Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(), 2)
         .addInput((Item)TensuraBlocks.Items.DEEPSLATE_MAGIC_ORE.get())
         .build(output);

      for (Item bottle : List.of(
         Items.GLASS_BOTTLE,
         Items.EXPERIENCE_BOTTLE,
         Items.POTION,
         (Item)TensuraConsumableItems.MAGIC_BOTTLE.get(),
         (Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(),
         (Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get()
      )) {
         RefiningRecipe.Builder.of((Item)TensuraConsumableItems.HIGH_POTION.get())
            .addInput(bottle)
            .addIngredient((Item)TensuraMaterialItems.HIPOKUTE_GRASS.get())
            .build(output);
         RefiningRecipe.Builder.of((Item)TensuraConsumableItems.FULL_POTION.get())
            .addInput(bottle)
            .addIngredient((Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get())
            .build(output);
         RefiningRecipe.Builder.of((Item)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get())
            .addInput(bottle)
            .addIngredient((Item)TensuraMaterialItems.HIPOKUTE_GRASS.get())
            .addIngredient((Item)TensuraMobDropItems.DAEMON_ESSENCE.get())
            .build(output);
         RefiningRecipe.Builder.of((Item)TensuraConsumableItems.HIGH_ARCANE_POTION.get())
            .addInput(bottle)
            .addIngredient((Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get())
            .addIngredient((Item)TensuraMobDropItems.DAEMON_ESSENCE.get())
            .build(output);
      }

      RefiningRecipe.Builder.of((Item)TensuraConsumableItems.LOW_ARCANE_POTION.get())
         .addInput((Item)TensuraConsumableItems.LOW_POTION.get())
         .addIngredient((Item)TensuraMobDropItems.DAEMON_ESSENCE.get())
         .build(output);
      RefiningRecipe.Builder.of((Item)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get())
         .addInput((Item)TensuraConsumableItems.HIGH_POTION.get())
         .addIngredient((Item)TensuraMobDropItems.DAEMON_ESSENCE.get())
         .build(output);
      RefiningRecipe.Builder.of((Item)TensuraConsumableItems.HIGH_ARCANE_POTION.get())
         .addInput((Item)TensuraConsumableItems.FULL_POTION.get())
         .addIngredient((Item)TensuraMobDropItems.DAEMON_ESSENCE.get())
         .build(output);
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.CHILL),
         TensuraPotions.getReference(TensuraPotions.LONG_CHILL),
         TensuraPotions.getReference(TensuraPotions.STRONG_CHILL),
         (Item)TensuraConsumableItems.CHILLED_SLIME.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.CORROSION),
         TensuraPotions.getReference(TensuraPotions.LONG_CORROSION),
         TensuraPotions.getReference(TensuraPotions.STRONG_CORROSION),
         (Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.FATAL_POISON),
         TensuraPotions.getReference(TensuraPotions.LONG_FATAL_POISON),
         TensuraPotions.getReference(TensuraPotions.STRONG_FATAL_POISON),
         (Item)TensuraMobDropItems.SPIDER_FANG.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.FRAGILITY),
         TensuraPotions.getReference(TensuraPotions.LONG_FRAGILITY),
         TensuraPotions.getReference(TensuraPotions.STRONG_FRAGILITY),
         (Item)TensuraConsumableItems.GIANT_ANT_LEG.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.HYPNOSIS),
         TensuraPotions.getReference(TensuraPotions.LONG_HYPNOSIS),
         TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOSIS),
         TensuraPotions.getReference(TensuraPotions.HYPNOTIC_EFFICIENCY),
         TensuraPotions.getReference(TensuraPotions.LONG_HYPNOTIC_EFFICIENCY),
         TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOTIC_EFFICIENCY),
         (Item)TensuraMaterialItems.BAFFLEDIL.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.PARALYSIS),
         TensuraPotions.getReference(TensuraPotions.LONG_PARALYSIS),
         TensuraPotions.getReference(TensuraPotions.STRONG_PARALYSIS),
         (Item)TensuraMobDropItems.CENTIPEDE_STINGER.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.GLOWING),
         TensuraPotions.getReference(TensuraPotions.LONG_GLOWING),
         null,
         (Item)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get()
      );
      this.potionRefiningRecipes(
         output,
         TensuraPotions.getReference(TensuraPotions.NIGHT_OWL),
         TensuraPotions.getReference(TensuraPotions.LONG_NIGHT_OWL),
         null,
         (Item)TensuraMobDropItems.INVISIBLE_FEATHER.get()
      );
      this.potionRefiningRecipes(
         output,
         Potions.SWIFTNESS,
         Potions.LONG_SWIFTNESS,
         Potions.STRONG_SWIFTNESS,
         Potions.SLOWNESS,
         Potions.LONG_SLOWNESS,
         Potions.STRONG_SLOWNESS,
         Items.SUGAR
      );
      this.potionRefiningRecipes(
         output,
         Potions.LEAPING,
         Potions.LONG_LEAPING,
         Potions.STRONG_LEAPING,
         Potions.SLOWNESS,
         Potions.LONG_SLOWNESS,
         Potions.STRONG_SLOWNESS,
         Items.RABBIT_FOOT
      );
      this.potionRefiningRecipes(output, Potions.STRENGTH, Potions.LONG_STRENGTH, Potions.STRONG_STRENGTH, Items.BLAZE_POWDER);
      this.potionRefiningRecipes(
         output, Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON, Potions.HARMING, null, Potions.STRONG_HARMING, Items.SPIDER_EYE
      );
      this.potionRefiningRecipes(output, Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION, Items.GHAST_TEAR);
      this.potionRefiningRecipes(output, Potions.TURTLE_MASTER, Potions.LONG_TURTLE_MASTER, Potions.STRONG_TURTLE_MASTER, Items.TURTLE_HELMET);
      this.potionRefiningRecipes(
         output, Potions.HEALING, null, Potions.STRONG_HEALING, Potions.HARMING, null, Potions.STRONG_HARMING, Items.GLISTERING_MELON_SLICE
      );
      this.potionRefiningRecipes(output, Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE, null, Items.MAGMA_CREAM);
      this.potionRefiningRecipes(output, Potions.WATER_BREATHING, Potions.LONG_WATER_BREATHING, null, Items.PUFFERFISH);
      this.potionRefiningRecipes(output, Potions.SLOW_FALLING, Potions.LONG_SLOW_FALLING, null, Items.PHANTOM_MEMBRANE);
      this.potionRefiningRecipes(
         output, Potions.NIGHT_VISION, Potions.LONG_NIGHT_VISION, null, Potions.INVISIBILITY, Potions.LONG_INVISIBILITY, null, Items.GOLDEN_CARROT
      );
      this.potionRefiningRecipes(output, Potions.WIND_CHARGED, null, null, Items.BREEZE_ROD);
      this.potionRefiningRecipes(output, Potions.WEAVING, null, null, Items.COBWEB);
      this.potionRefiningRecipes(output, Potions.OOZING, null, null, Items.SLIME_BLOCK);
      this.potionRefiningRecipes(output, Potions.INFESTED, null, null, Items.STONE);
      RefiningRecipe.Builder.of(Potions.WATER, Items.SPLASH_POTION).addInput(Potions.WATER).addIngredient(Items.GUNPOWDER).build(output);
      RefiningRecipe.Builder.of(Potions.WATER, Items.LINGERING_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.WATER, Items.LINGERING_POTION)
         .addInput(Potions.WATER, Items.SPLASH_POTION)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD).addInput(Potions.WATER).addIngredient(Items.NETHER_WART).build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD, Items.SPLASH_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.NETHER_WART)
         .addIngredient(Items.GUNPOWDER)
         .build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD, Items.LINGERING_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.NETHER_WART)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD, Items.SPLASH_POTION)
         .addInput(Potions.WATER, Items.SPLASH_POTION)
         .addIngredient(Items.NETHER_WART)
         .build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD, Items.LINGERING_POTION)
         .addInput(Potions.WATER, Items.SPLASH_POTION)
         .addIngredient(Items.NETHER_WART)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.AWKWARD, Items.LINGERING_POTION)
         .addInput(Potions.WATER, Items.LINGERING_POTION)
         .addIngredient(Items.NETHER_WART)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS).addInput(Potions.WATER).addIngredient(Items.FERMENTED_SPIDER_EYE).build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS, Items.SPLASH_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.FERMENTED_SPIDER_EYE)
         .addIngredient(Items.GUNPOWDER)
         .build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.FERMENTED_SPIDER_EYE)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS, Items.SPLASH_POTION).addInput(Potions.WEAKNESS).addIngredient(Items.GUNPOWDER).build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WEAKNESS)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WEAKNESS, Items.SPLASH_POTION)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS)
         .addInput(Potions.WATER)
         .addIngredient(Items.FERMENTED_SPIDER_EYE)
         .addIngredient(Items.REDSTONE)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.SPLASH_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.FERMENTED_SPIDER_EYE)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WATER)
         .addIngredient(Items.FERMENTED_SPIDER_EYE)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.SPLASH_POTION)
         .addInput(Potions.LONG_WEAKNESS)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.LONG_WEAKNESS)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.LONG_WEAKNESS, Items.SPLASH_POTION)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS).addInput(Potions.WEAKNESS).addIngredient(Items.REDSTONE).build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.SPLASH_POTION)
         .addInput(Potions.WEAKNESS)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WEAKNESS)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.GUNPOWDER)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.SPLASH_POTION)
         .addInput(Potions.WEAKNESS, Items.SPLASH_POTION)
         .addIngredient(Items.REDSTONE)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WEAKNESS, Items.SPLASH_POTION)
         .addIngredient(Items.REDSTONE)
         .addIngredient(Items.DRAGON_BREATH)
         .build(output);
      RefiningRecipe.Builder.of(Potions.LONG_WEAKNESS, Items.LINGERING_POTION)
         .addInput(Potions.WEAKNESS, Items.LINGERING_POTION)
         .addIngredient(Items.REDSTONE)
         .build(output);
   }

   protected void potionRefiningRecipes(
      RecipeOutput output, Holder<Potion> potion, @Nullable Holder<Potion> longPotion, @Nullable Holder<Potion> strongPotion, Item... toAdd
   ) {
      this.potionRefiningRecipes(output, potion, longPotion, strongPotion, null, null, null, toAdd);
   }

   protected void potionRefiningRecipes(
      RecipeOutput output,
      Holder<Potion> potion,
      @Nullable Holder<Potion> longPotion,
      @Nullable Holder<Potion> strongPotion,
      @Nullable Holder<Potion> reversedPotion,
      @Nullable Holder<Potion> reversedLongPotion,
      @Nullable Holder<Potion> reversedStrongPotion,
      Item... toAdd
   ) {
      this.generateFromBase(output, potion, longPotion, strongPotion, reversedPotion, reversedLongPotion, reversedStrongPotion, toAdd);
      if (reversedPotion != null) {
         this.generatePotionInputInversions(output, potion, longPotion, strongPotion, reversedPotion, reversedLongPotion, reversedStrongPotion);
      }
   }

   protected void potionRefiningRecipes(
      RecipeOutput output,
      Holder<Potion> potion,
      @Nullable Holder<Potion> longPotion,
      @Nullable Holder<Potion> strongPotion,
      Item toAdd,
      Holder<Potion> reversedPotion,
      @Nullable Holder<Potion> reversedLongPotion,
      @Nullable Holder<Potion> reversedStrongPotion
   ) {
      this.potionRefiningRecipes(output, potion, longPotion, strongPotion, reversedPotion, reversedLongPotion, reversedStrongPotion, toAdd);
   }

   private void generateFromBase(
      RecipeOutput output,
      Holder<Potion> potion,
      @Nullable Holder<Potion> longPotion,
      @Nullable Holder<Potion> strongPotion,
      @Nullable Holder<Potion> reversedPotion,
      @Nullable Holder<Potion> reversedLongPotion,
      @Nullable Holder<Potion> reversedStrongPotion,
      Item[] toAdd
   ) {
      this.generateFromOneBase(output, Potions.WATER, true, potion, longPotion, strongPotion, reversedPotion, reversedLongPotion, reversedStrongPotion, toAdd);
      this.generateFromOneBase(
         output, Potions.AWKWARD, false, potion, longPotion, strongPotion, reversedPotion, reversedLongPotion, reversedStrongPotion, toAdd
      );
   }

   private void generateFromOneBase(
      RecipeOutput output,
      Holder<Potion> baseInputPotion,
      boolean needsNetherWart,
      Holder<Potion> potion,
      @Nullable Holder<Potion> longPotion,
      @Nullable Holder<Potion> strongPotion,
      @Nullable Holder<Potion> reversedPotion,
      @Nullable Holder<Potion> reversedLongPotion,
      @Nullable Holder<Potion> reversedStrongPotion,
      Item[] toAdd
   ) {
      for (TensuraRecipeProvider.BottleType inBottle : TensuraRecipeProvider.BottleType.values()) {
         for (TensuraRecipeProvider.BottleType outBottle : TensuraRecipeProvider.BottleType.values()) {
            if (this.isAllowedBottleTransform(inBottle, outBottle)) {
               boolean addGunpowder = outBottle != TensuraRecipeProvider.BottleType.NORMAL && inBottle == TensuraRecipeProvider.BottleType.NORMAL;
               boolean addDragonBreath = outBottle == TensuraRecipeProvider.BottleType.LINGERING && inBottle != TensuraRecipeProvider.BottleType.LINGERING;
               this.build(output, potion, outBottle, baseInputPotion, inBottle, toAdd, needsNetherWart, null, addGunpowder, addDragonBreath, false);
               this.build(output, reversedPotion, outBottle, baseInputPotion, inBottle, toAdd, needsNetherWart, null, addGunpowder, addDragonBreath, true);
               this.build(
                  output, longPotion, outBottle, baseInputPotion, inBottle, toAdd, needsNetherWart, Items.REDSTONE, addGunpowder, addDragonBreath, false
               );
               this.build(
                  output, reversedLongPotion, outBottle, baseInputPotion, inBottle, toAdd, needsNetherWart, Items.REDSTONE, addGunpowder, addDragonBreath, true
               );
               this.build(
                  output,
                  strongPotion,
                  outBottle,
                  baseInputPotion,
                  inBottle,
                  toAdd,
                  needsNetherWart,
                  Items.GLOWSTONE_DUST,
                  addGunpowder,
                  addDragonBreath,
                  false
               );
               this.build(
                  output,
                  reversedStrongPotion,
                  outBottle,
                  baseInputPotion,
                  inBottle,
                  toAdd,
                  needsNetherWart,
                  Items.GLOWSTONE_DUST,
                  addGunpowder,
                  addDragonBreath,
                  true
               );
            }
         }
      }
   }

   private boolean isAllowedBottleTransform(TensuraRecipeProvider.BottleType inBottle, TensuraRecipeProvider.BottleType outBottle) {
      if (outBottle == TensuraRecipeProvider.BottleType.NORMAL) {
         return inBottle == TensuraRecipeProvider.BottleType.NORMAL;
      } else {
         return outBottle != TensuraRecipeProvider.BottleType.SPLASH
            ? true
            : inBottle == TensuraRecipeProvider.BottleType.NORMAL || inBottle == TensuraRecipeProvider.BottleType.SPLASH;
      }
   }

   private void generatePotionInputInversions(
      RecipeOutput output,
      Holder<Potion> potion,
      @Nullable Holder<Potion> longPotion,
      @Nullable Holder<Potion> strongPotion,
      Holder<Potion> reversedPotion,
      @Nullable Holder<Potion> reversedLongPotion,
      @Nullable Holder<Potion> reversedStrongPotion
   ) {
      for (TensuraRecipeProvider.BottleType inBottle : TensuraRecipeProvider.BottleType.values()) {
         for (TensuraRecipeProvider.BottleType outBottle : TensuraRecipeProvider.BottleType.values()) {
            if (this.isAllowedBottleTransform(inBottle, outBottle)) {
               boolean addGunpowder = outBottle != TensuraRecipeProvider.BottleType.NORMAL && inBottle == TensuraRecipeProvider.BottleType.NORMAL;
               boolean addDragonBreath = outBottle == TensuraRecipeProvider.BottleType.LINGERING && inBottle != TensuraRecipeProvider.BottleType.LINGERING;
               this.build(output, reversedPotion, outBottle, potion, inBottle, new Item[0], false, null, addGunpowder, addDragonBreath, true);
               this.build(output, reversedLongPotion, outBottle, potion, inBottle, new Item[0], false, Items.REDSTONE, addGunpowder, addDragonBreath, true);
               this.build(
                  output, reversedStrongPotion, outBottle, potion, inBottle, new Item[0], false, Items.GLOWSTONE_DUST, addGunpowder, addDragonBreath, true
               );
               if (longPotion != null && reversedLongPotion != null) {
                  this.build(output, reversedLongPotion, outBottle, longPotion, inBottle, new Item[0], false, null, addGunpowder, addDragonBreath, true);
               }

               if (strongPotion != null && reversedStrongPotion != null) {
                  this.build(output, reversedStrongPotion, outBottle, strongPotion, inBottle, new Item[0], false, null, addGunpowder, addDragonBreath, true);
               }
            }
         }
      }
   }

   private void build(
      RecipeOutput output,
      @Nullable Holder<Potion> resultPotion,
      TensuraRecipeProvider.BottleType outBottle,
      Holder<Potion> inputPotion,
      TensuraRecipeProvider.BottleType inBottle,
      Item[] toAdd,
      boolean needsNetherWart,
      @Nullable Item modifier,
      boolean addGunpowder,
      boolean addDragonBreath,
      boolean addFermentedEye
   ) {
      if (resultPotion != null) {
         int ingredientCount = 0;
         ingredientCount += toAdd != null ? toAdd.length : 0;
         ingredientCount += needsNetherWart ? 1 : 0;
         ingredientCount += modifier != null ? 1 : 0;
         ingredientCount += addGunpowder ? 1 : 0;
         ingredientCount += addDragonBreath ? 1 : 0;
         ingredientCount += addFermentedEye ? 1 : 0;
         if (ingredientCount <= 5) {
            RefiningRecipe.Builder builder = outBottle == TensuraRecipeProvider.BottleType.NORMAL
               ? RefiningRecipe.Builder.of(resultPotion)
               : RefiningRecipe.Builder.of(resultPotion, outBottle.bottleItem);
            if (inBottle == TensuraRecipeProvider.BottleType.NORMAL) {
               builder.addInput(inputPotion);
            } else if (inBottle.bottleItem != null) {
               builder.addInput(inputPotion, inBottle.bottleItem);
            }

            if (toAdd != null && toAdd.length > 0) {
               builder.addIngredient(toAdd);
            }

            if (needsNetherWart) {
               builder.addIngredient(Items.NETHER_WART);
            }

            if (modifier != null) {
               builder.addIngredient(modifier);
            }

            if (addGunpowder) {
               builder.addIngredient(Items.GUNPOWDER);
            }

            if (addDragonBreath) {
               builder.addIngredient(Items.DRAGON_BREATH);
            }

            if (addFermentedEye) {
               builder.addIngredient(Items.FERMENTED_SPIDER_EYE);
            }

            builder.build(output);
         }
      }
   }

   private void smeltingRecipes(RecipeOutput output) {
      ImmutableList<ItemLike> SILVER_SMELTABLES = ImmutableList.of(
         (ItemLike)TensuraBlocks.SILVER_ORE.get(), (ItemLike)TensuraBlocks.DEEPSLATE_SILVER_ORE.get(), (ItemLike)TensuraMaterialItems.RAW_SILVER.get()
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.CATTLEDEER_BEEF.get(),
         (ItemLike)TensuraConsumableItems.CATTLEDEER_STEAK.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.GIANT_ANT_LEG.get(),
         (ItemLike)TensuraConsumableItems.COOKED_GIANT_ANT_LEG.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
         (ItemLike)TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get(),
         (ItemLike)TensuraConsumableItems.BLADE_TIGER_STEAK.get(),
         0.5F,
         300,
         700,
         200
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_SERPENT_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_SERPENT_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_SISSIE_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.SISSIE_FIN.get(),
         (ItemLike)TensuraConsumableItems.COOKED_SISSIE_FIN.get(),
         0.5F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
         (ItemLike)TensuraConsumableItems.COOKED_SPEAR_TORO_FIN.get(),
         0.5F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
         (ItemLike)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get(),
         0.35F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output, RecipeCategory.MISC, (ItemLike)TensuraConsumableItems.CHILLED_SLIME.get(), (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(), 0.2F, 100, 300, 50
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.CHILLED_SLIME_BLOCK.get(),
         (ItemLike)TensuraBlocks.Items.SLIME_CHUNK_BLOCK.get(),
         0.2F,
         200,
         600,
         100
      );
      this.allSmeltingRecipes(
         output,
         RecipeCategory.FOOD,
         (ItemLike)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(),
         (ItemLike)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get(),
         0.0F,
         60,
         180,
         60
      );
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.Items.SMOOTH_SARASA_SANDSTONE.get(),
            0.1F,
            200
         )
         .unlockedBy("has_sarasa_sandstone", has((ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE.get()))
         .save(output);
      oreSmelting(output, SILVER_SMELTABLES, RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(), 0.7F, 200, "silver_ingot");
      oreBlasting(output, SILVER_SMELTABLES, RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(), 0.7F, 100, "silver_ingot");
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.LOW_MAGISTEEL_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         0.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         0.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         0.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.LOW_MAGISTEEL_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         0.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         1.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         1.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         1.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         1.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.MITHRIL_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.MITHRIL_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.MITHRIL_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.MITHRIL_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ORICHALCUM_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ORICHALCUM_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ORICHALCUM_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ORICHALCUM_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         1.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.PURE_MAGISTEEL_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         2.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         2.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         2.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.PURE_MAGISTEEL_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         2.0F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ADAMANTITE_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ADAMANTITE_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ADAMANTITE_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.ADAMANTITE_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIHIIROKANE_HELMET.get()}),
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIHIIROKANE_CHESTPLATE.get()}),
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIHIIROKANE_LEGGINGS.get()}),
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         2.5F,
         200
      );
      this.smeltBlastRecipes(
         output,
         RecipeCategory.MISC,
         Ingredient.of(new ItemLike[]{(ItemLike)TensuraArmorItems.HIHIIROKANE_BOOTS.get()}),
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         2.5F,
         200
      );
   }

   protected void allSmeltingRecipes(
      RecipeOutput recipe, RecipeCategory category, ItemLike input, ItemLike result, float exp, int smeltingTicks, int campfireTicks, int smokingTicks
   ) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{input}), category, result, exp, smeltingTicks)
         .unlockedBy(RecipeProvider.getHasName(input), has(input))
         .save(recipe, getSmeltingRecipeName(result));
      SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(new ItemLike[]{input}), category, result, exp, campfireTicks)
         .unlockedBy(RecipeProvider.getHasName(input), has(input))
         .save(recipe, getRecipeName(result, "_from_campfire_cooking"));
      SimpleCookingRecipeBuilder.smoking(Ingredient.of(new ItemLike[]{input}), category, result, exp, smokingTicks)
         .unlockedBy(RecipeProvider.getHasName(input), has(input))
         .save(recipe, getRecipeName(result, "_from_smoking"));
   }

   protected void smeltBlastRecipes(RecipeOutput output, RecipeCategory category, Ingredient ingredient, ItemLike result, float exp, int smeltingTicks) {
      ItemStack[] items = ingredient.getItems();

      for (ItemStack itemStack : items) {
         SimpleCookingRecipeBuilder.smelting(ingredient, category, result, exp, smeltingTicks)
            .unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), has(itemStack.getItem()))
            .save(output, getSmeltingRecipeName(result) + "_" + getItemName(itemStack.getItem()));
         SimpleCookingRecipeBuilder.blasting(ingredient, category, result, exp, smeltingTicks / 2)
            .unlockedBy(RecipeProvider.getHasName(itemStack.getItem()), has(itemStack.getItem()))
            .save(output, getBlastingRecipeName(result) + "_" + getItemName(itemStack.getItem()));
      }
   }

   private void smithingRecipes(RecipeOutput recipeOutput) {
      SmithingBenchRecipe.Builder.of(TensuraToolItems.KANABO)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraToolItems.GOBLIN_CLUB, 1)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SHORT_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.STICK, 2)
         .addIngredient(Items.STRING, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.STICK, 3)
         .addIngredient(Items.STRING, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LONG_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.STICK, 4)
         .addIngredient(Items.STRING, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WAR_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.STICK, 5)
         .addIngredient(Items.STRING, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SHORT_SPIDER_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.SPIDER_BOWS)
         .addIngredient(TensuraConsumableItems.KNIGHT_SPIDER_LEG, 2)
         .addIngredient(TensuraItemTags.STRONG_THREAD, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SPIDER_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.SPIDER_BOWS)
         .addIngredient(TensuraConsumableItems.KNIGHT_SPIDER_LEG, 3)
         .addIngredient(TensuraItemTags.STRONG_THREAD, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LONG_SPIDER_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.SPIDER_BOWS)
         .addIngredient(TensuraConsumableItems.KNIGHT_SPIDER_LEG, 4)
         .addIngredient(TensuraItemTags.STRONG_THREAD, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WAR_SPIDER_BOW)
         .requiresSchematic(TensuraSmithingSchematicItems.SPIDER_BOWS)
         .addIngredient(TensuraConsumableItems.KNIGHT_SPIDER_LEG, 5)
         .addIngredient(TensuraItemTags.STRONG_THREAD, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.ARROW, 6)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.FLINT, 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(Items.FEATHER, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.INVISIBLE_ARROW.get(), 6)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.FLINT, 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(TensuraMobDropItems.INVISIBLE_FEATHER, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.SPEARED_FIN_ARROW.get(), 6)
         .requiresSchematic(TensuraSmithingSchematicItems.BASIC_BOWS)
         .addIngredient(Items.FLINT, 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(TensuraConsumableItems.SPEAR_TORO_FIN, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ICE_BLADE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(TensuraMaterialItems.ELEMENT_CORE_WATER, 2)
         .addIngredient(Items.ENDER_EYE, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.TEMPEST_SCALE_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 3)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.TEMPEST_SCALE_KNIFE)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 2)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.TEMPEST_SCALE_SHIELD)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHIELD)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 6)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SISSIE_TOOTH_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient((Item)TensuraMobDropItems.SISSIE_TOOTH.get(), 2)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(), 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.CENTIPEDE_DAGGER)
         .requiresSchematic(TensuraSmithingSchematicItems.HUNTING_KNIFE)
         .addIngredient((Item)TensuraMobDropItems.CENTIPEDE_STINGER.get(), 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(Items.STRING, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SPIDER_DAGGER)
         .requiresSchematic(TensuraSmithingSchematicItems.HUNTING_KNIFE)
         .addIngredient((Item)TensuraMobDropItems.SPIDER_FANG.get(), 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(Items.STRING, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.BEAST_HORN_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient((Item)TensuraMobDropItems.BEAST_HORN.get(), 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.UNICORN_HORN_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient((Item)TensuraMobDropItems.UNICORN_HORN.get(), 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.BLADE_TIGER_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient((Item)TensuraMobDropItems.BLADE_TIGER_TAIL.get(), 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGIC_STAFF)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.MAGIC_STAFF)
         .addIngredient((Item)TensuraMaterialItems.MAGIC_STONE.get(), 1)
         .addIngredient((Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(), 2)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MEDIUM_MAGIC_STAFF)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.MAGIC_STAFF)
         .addIngredient((Item)TensuraMaterialItems.MAGIC_STONE.get(), 1)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(), 2)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGIC_STAFF)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.MAGIC_STAFF)
         .addIngredient((Item)TensuraMaterialItems.MAGIC_STONE.get(), 1)
         .addIngredient((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), 2)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SLIME_STAFF)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.MAGIC_STAFF)
         .addIngredient(TensuraMobDropItems.SLIME_CORE, 1)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(ItemTags.PLANKS, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(ItemTags.PLANKS, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(ItemTags.PLANKS, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(ItemTags.PLANKS, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(ItemTags.PLANKS, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.PLANKS, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.PLANKS, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.PLANKS, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WOODEN_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.PLANKS, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.LEATHER_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.LEATHER_GEAR)
         .addIngredient(Items.LEATHER, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.LEATHER_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.LEATHER_GEAR)
         .addIngredient(Items.LEATHER, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.LEATHER_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.LEATHER_GEAR)
         .addIngredient(Items.LEATHER, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.LEATHER_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.LEATHER_GEAR)
         .addIngredient(Items.LEATHER, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.STONE_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(ItemTags.STONE_TOOL_MATERIALS, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.SADDLE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 3)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.STRING, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraMaterialItems.MONSTER_SADDLE.get())
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 5)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_NUGGET, 8)
         .addIngredient(TensuraMobDropItems.STEEL_THREAD, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.LEATHER_HORSE_ARMOR)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .addIngredient(Items.LEATHER, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_HORSE_ARMOR)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .addIngredient(Items.IRON_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_HORSE_ARMOR)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 2)
         .addIngredient(Items.GOLD_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_HORSE_ARMOR)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 2)
         .addIngredient(Items.DIAMOND, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.WINGED_SHOES)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 4)
         .addIngredient(TensuraMobDropItems.DRAGON_PEACOCK_FEATHER, 4)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.BAT_GLIDER)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.GIANT_BAT_WING, 2)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 2)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_D_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_D_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_C_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_C_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_B_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_B_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_A_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_A_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(Items.GOLD_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.GOLD_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.GOLD_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.GOLD_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.GOLDEN_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.GOLD_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.GOLDEN_APPLE)
         .requiresSchematic(TensuraSmithingSchematicItems.GOLD_GEAR)
         .addIngredient(Items.APPLE, 1)
         .addIngredient(Items.GOLD_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SILVER_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SILVER_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SILVER_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SILVER_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SILVER_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraConsumableItems.SILVER_APPLE)
         .requiresSchematic(TensuraSmithingSchematicItems.SILVER_GEAR)
         .addIngredient(Items.APPLE, 1)
         .addIngredient(TensuraMaterialItems.SILVER_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(Items.IRON_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(Items.IRON_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(Items.IRON_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.IRON_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.IRON_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.IRON_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.IRON_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.IRON_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.IRON_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.IRON_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.IRON_GEAR)
         .addIngredient(Items.IRON_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ANT_CROSSBOW)
         .requiresSchematic(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR)
         .addIngredient(TensuraConsumableItems.GIANT_ANT_LEG, 3)
         .addIngredient(TensuraMobDropItems.STEEL_THREAD, 2)
         .addIngredient(Items.TRIPWIRE_HOOK, 1)
         .addIngredient(Items.IRON_INGOT, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANT_CARAPACE_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.GIANT_ANT_CARAPACE, 5)
         .addIngredient(Items.GOLD_INGOT, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANT_CARAPACE_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.GIANT_ANT_CARAPACE, 8)
         .addIngredient(Items.GOLD_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANT_CARAPACE_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.GIANT_ANT_CARAPACE, 7)
         .addIngredient(Items.GOLD_INGOT, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANT_CARAPACE_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.GIANT_ANT_CARAPACE, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SERPENT_SCALEMAIL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.SERPENT_SCALE, 5)
         .addIngredient((Item)TensuraMaterialItems.SILVER_INGOT.get(), 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.SERPENT_SCALE, 8)
         .addIngredient((Item)TensuraMaterialItems.SILVER_INGOT.get(), 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.SERPENT_SCALE, 7)
         .addIngredient((Item)TensuraMaterialItems.SILVER_INGOT.get(), 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.SERPENT_SCALE, 4)
         .addIngredient((Item)TensuraMaterialItems.SILVER_INGOT.get(), 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(Items.DIAMOND, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(Items.DIAMOND, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(Items.DIAMOND, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.DIAMOND, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(Items.DIAMOND, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.DIAMOND, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.DIAMOND, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.DIAMOND, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(Items.DIAMOND, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DIAMOND_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.DIAMOND_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.DIAMOND_GEAR)
         .addIngredient(Items.DIAMOND, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE, 5)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE, 8)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE, 7)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR)
         .addIngredient(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE, 4)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.MAGIC_STONE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.WARP_CORE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 4)
         .addIngredient(TensuraMaterialItems.ELEMENT_CORE_SPACE, 2)
         .addIngredient(Items.ENDER_PEARL, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.DAEMON_CORE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMobDropItems.DAEMON_ESSENCE, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.ELEMENT_CORE_EMPTY)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(), 2)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(), 2)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.LOW_MAGISTEEL_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.LOW_MAGISTEEL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 8)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 7)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.LOW_MAGISTEEL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ARMORSAURUS_SHIELD)
         .requiresSchematic(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHIELD)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SHELL, 4)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SCALE, 3)
         .addIngredient(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SCALE, 5)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SCALE, 8)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SCALE, 7)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.ARMORSAURUS_SCALE, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIGH_MAGISTEEL_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIGH_MAGISTEEL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 8)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 7)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_C, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIGH_MAGISTEEL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.CHARYBDIS_SCALEMAIL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.CHARYBDIS_SCALEMAIL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.CHARYBDIS_SCALEMAIL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.CHARYBDIS_SCALEMAIL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR)
         .addIngredient(TensuraMobDropItems.CHARYBDIS_SCALE, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.MITHRIL_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MITHRIL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 5)
         .addIngredient(Items.FEATHER, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MITHRIL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 8)
         .addIngredient(Items.GOLD_INGOT, 4)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MITHRIL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 7)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.MITHRIL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraConsumableItems.ENCHANTED_SILVER_APPLE)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraConsumableItems.SILVER_APPLE, 1)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.BOOK)
         .requiresSchematic(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR)
         .addIngredient(Items.PAPER, 3)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_D, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.RACE_RESET_SCROLL)
         .requiresSchematic(TensuraSmithingSchematicItems.MITHRIL_GEAR)
         .addIngredient(TensuraMaterialItems.MITHRIL_INGOT, 2)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .addIngredient(Items.PAPER, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.SKILL_RESET_SCROLL)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 2)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 1)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .addIngredient(Items.PAPER, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraMaterialItems.CHARACTER_RESET_SCROLL)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(TensuraMaterialItems.MAGIC_STONE, 1)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .addIngredient(Items.PAPER, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(Items.ENCHANTED_GOLDEN_APPLE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(Items.GOLDEN_APPLE, 1)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ORICHALCUM_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ORICHALCUM_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 5)
         .addIngredient(Items.GOLD_INGOT, 2)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ORICHALCUM_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 8)
         .addIngredient(Items.GOLD_INGOT, 4)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ORICHALCUM_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 7)
         .addIngredient(Items.GOLD_INGOT, 3)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ORICHALCUM_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.ORICHALCUM_GEAR)
         .addIngredient(TensuraMaterialItems.ORICHALCUM_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 4)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 3)
         .addIngredient(Items.GOLD_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.PURE_MAGISTEEL_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 8)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 7)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_A, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.PURE_MAGISTEEL_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.ADAMANTITE_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ADAMANTITE_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ADAMANTITE_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ADAMANTITE_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ADAMANTITE_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.ADAMANTITE_GEAR)
         .addIngredient(TensuraMaterialItems.ADAMANTITE_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SPEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 1)
         .addIngredient(Items.STICK, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SCYTHE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.SPEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 4)
         .addIngredient(Items.STICK, 3)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_KATANA)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 2)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_KODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.SHORT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_TACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.LONG_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 3)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_ODACHI)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .requiresSchematic(TensuraSmithingSchematicItems.GREAT_SWORD)
         .requiresSchematic(TensuraSmithingSchematicItems.JAPANESE_SWORD)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 4)
         .addIngredient(Items.STICK, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_PICKAXE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_AXE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SHOVEL)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 1)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_HOE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 2)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.HIHIIROKANE_SICKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 3)
         .addIngredient(Items.STICK, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIHIIROKANE_HELMET)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIHIIROKANE_CHESTPLATE)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIHIIROKANE_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.HIHIIROKANE_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR)
         .addIngredient(TensuraMaterialItems.HIHIIROKANE_INGOT, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANTI_MAGIC_MASK)
         .requiresSchematic(TensuraSmithingSchematicItems.ANTI_MAGIC_MASK)
         .addIngredient((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), 3)
         .addIngredient(Items.CLAY_BALL, 2)
         .addIngredient(Items.RED_DYE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.DARK_JACKET)
         .requiresSchematic(TensuraSmithingSchematicItems.DARK_SET)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 8)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.DARK_LEGGINGS)
         .requiresSchematic(TensuraSmithingSchematicItems.DARK_SET)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 7)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.DARK_BOOTS)
         .requiresSchematic(TensuraSmithingSchematicItems.DARK_SET)
         .addIngredient(TensuraMobDropItems.MONSTER_LEATHER_B, 4)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.CRAZY_PIERROT_MASK)
         .requiresSchematic(TensuraSmithingSchematicItems.PIERROT_MASK)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get())
         .addIngredient(Items.CLAY_BALL, 3)
         .addIngredient(Items.YELLOW_DYE, 1)
         .addIngredient(Items.BLACK_DYE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.ANGRY_PIERROT_MASK)
         .requiresSchematic(TensuraSmithingSchematicItems.PIERROT_MASK)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get())
         .addIngredient(Items.CLAY_BALL, 3)
         .addIngredient(Items.RED_DYE, 1)
         .addIngredient(Items.ORANGE_DYE, 1)
         .addIngredient(Items.BLUE_DYE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.WONDER_PIERROT_MASK)
         .requiresSchematic(TensuraSmithingSchematicItems.PIERROT_MASK)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get())
         .addIngredient(Items.CLAY_BALL, 3)
         .addIngredient(Items.PURPLE_DYE, 1)
         .addIngredient(Items.RED_DYE, 1)
         .addIngredient(Items.PINK_DYE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraArmorItems.TEARY_PIERROT_MASK)
         .requiresSchematic(TensuraSmithingSchematicItems.PIERROT_MASK)
         .addIngredient((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get())
         .addIngredient(Items.CLAY_BALL, 3)
         .addIngredient(Items.PINK_DYE, 1)
         .addIngredient(Items.YELLOW_DYE, 1)
         .addIngredient(Items.RED_DYE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.SEVERER_BLADE.get(), 3)
         .requiresSchematic(TensuraSmithingSchematicItems.SPATIAL_BLADE)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.ENDER_PEARL, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.SPATIAL_BLADE)
         .requiresSchematic(TensuraSmithingSchematicItems.SPATIAL_BLADE)
         .addIngredient(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, 1)
         .addIngredient(Items.STICK, 1)
         .addIngredient(TensuraToolItems.SEVERER_BLADE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.STICKY_STEEL_WEB_CARTRIDGE.get())
         .requiresSchematic(TensuraSmithingSchematicItems.WEB_GUN)
         .addIngredient(TensuraToolItems.COPPER_SHELL, 1)
         .addIngredient(TensuraMobDropItems.STICKY_THREAD, 4)
         .addIngredient(TensuraMobDropItems.STEEL_THREAD, 4)
         .addIngredient(Items.GUNPOWDER, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.STICKY_WEB_CARTRIDGE.get())
         .requiresSchematic(TensuraSmithingSchematicItems.WEB_GUN)
         .addIngredient(TensuraToolItems.COPPER_SHELL, 1)
         .addIngredient(TensuraMobDropItems.STICKY_THREAD, 4)
         .addIngredient(Items.GUNPOWDER, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.WEB_CARTRIDGE.get())
         .requiresSchematic(TensuraSmithingSchematicItems.WEB_GUN)
         .addIngredient(TensuraToolItems.COPPER_SHELL, 1)
         .addIngredient(Items.STRING, 8)
         .addIngredient(Items.GUNPOWDER, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of((Item)TensuraToolItems.COPPER_SHELL.get(), 4)
         .requiresSchematic(TensuraSmithingSchematicItems.WEB_GUN)
         .addIngredient(Items.COPPER_INGOT, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.WEB_GUN)
         .requiresSchematic(TensuraSmithingSchematicItems.WEB_GUN)
         .addIngredient(Items.IRON_INGOT, 2)
         .addIngredient(Items.REDSTONE, 1)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.KUNAI)
         .requiresSchematic(TensuraSmithingSchematicItems.KUNAI)
         .addIngredient(Items.IRON_NUGGET, 5)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.PURE_MAGISTEEL_KUNAI)
         .requiresSchematic(TensuraSmithingSchematicItems.KUNAI)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_NUGGET, 5)
         .addIngredient(Items.GOLD_NUGGET, 2)
         .build(recipeOutput);
      SmithingBenchRecipe.Builder.of(TensuraToolItems.DRAGON_KNUCKLE)
         .requiresSchematic(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR)
         .addIngredient(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, 2)
         .addIngredient(Items.PINK_DYE, 3)
         .build(recipeOutput);
   }

   private void smithingUpgrades(RecipeOutput recipeOutput) {
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_SICKLE.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_SICKLE.get());
      netheriteSmithing(
         recipeOutput, (Item)TensuraToolItems.DIAMOND_SHORT_SWORD.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_SHORT_SWORD.get()
      );
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_LONG_SWORD.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_LONG_SWORD.get());
      netheriteSmithing(
         recipeOutput, (Item)TensuraToolItems.DIAMOND_GREAT_SWORD.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_GREAT_SWORD.get()
      );
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_SPEAR.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_SPEAR.get());
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_SCYTHE.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_SCYTHE.get());
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_KATANA.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_KATANA.get());
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_KODACHI.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_KODACHI.get());
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_TACHI.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_TACHI.get());
      netheriteSmithing(recipeOutput, (Item)TensuraToolItems.DIAMOND_ODACHI.get(), RecipeCategory.TOOLS, (Item)TensuraToolItems.NETHERITE_ODACHI.get());
   }

   private void stoneCutter(RecipeOutput output) {
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE_SLAB.get(), (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE.get(), 2
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE_STAIRS.get(), (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.WEBBED_COBBLESTONE.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE_WALL.get()
         )
         .unlockedBy("has_webbed_cobblestone", has((ItemLike)TensuraBlocks.WEBBED_COBBLESTONE.get()))
         .save(output, "webbed_cobblestone_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.WEBBED_STONE_BRICKS.get(), (ItemLike)TensuraBlocks.WEBBED_COBBLESTONE.get()
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.WEBBED_STONE_BRICK_SLAB.get(), (ItemLike)TensuraBlocks.WEBBED_STONE_BRICKS.get(), 2
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.WEBBED_STONE_BRICK_STAIRS.get(), (ItemLike)TensuraBlocks.WEBBED_STONE_BRICKS.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.WEBBED_STONE_BRICKS.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.WEBBED_STONE_BRICK_WALL.get()
         )
         .unlockedBy("has_webbed_bricks", has((ItemLike)TensuraBlocks.WEBBED_STONE_BRICKS.get()))
         .save(output, "webbed_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get()
         )
         .unlockedBy("has_magic_crystal_bricks", has((ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
         .save(output, "low_magic_crystal_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get()
         )
         .unlockedBy("has_magic_crystal_bricks", has((ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
         .save(output, "medium_magic_crystal_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get()
         )
         .unlockedBy("has_magic_crystal_bricks", has((ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
         .save(output, "high_magic_crystal_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get(),
         (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.SARASA_SANDSTONE_SLAB.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get(), 2
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.SARASA_SANDSTONE_STAIRS.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get()
      );
      SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{(ItemLike)TensuraBlocks.SARASA_SANDSTONE.get()}),
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)TensuraBlocks.SARASA_SANDSTONE_WALL.get()
         )
         .unlockedBy("has_sarasa_sandstone", has((ItemLike)TensuraBlocks.SARASA_SANDSTONE.get()))
         .save(output, "sarasa_sandstone_walls_from_stone_stonecutting");
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.CUT_SARASA_SANDSTONE.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get()
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get(), 2
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.CHISELED_SARASA_SANDSTONE.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get()
      );
      stonecutterResultFromBase(
         output, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB.get(), (ItemLike)TensuraBlocks.CUT_SARASA_SANDSTONE.get(), 2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB.get(),
         (ItemLike)TensuraBlocks.SMOOTH_SARASA_SANDSTONE.get(),
         2
      );
      stonecutterResultFromBase(
         output,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS.get(),
         (ItemLike)TensuraBlocks.SMOOTH_SARASA_SANDSTONE.get()
      );
   }

   private void woodCutter(RecipeOutput output) {
      this.addWoodFamily(
         output,
         Blocks.OAK_LOG,
         Blocks.OAK_WOOD,
         Blocks.STRIPPED_OAK_LOG,
         Blocks.STRIPPED_OAK_WOOD,
         Blocks.OAK_PLANKS,
         Blocks.OAK_STAIRS,
         Blocks.OAK_SLAB,
         Blocks.OAK_FENCE,
         Items.OAK_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.SPRUCE_LOG,
         Blocks.SPRUCE_WOOD,
         Blocks.STRIPPED_SPRUCE_LOG,
         Blocks.STRIPPED_SPRUCE_WOOD,
         Blocks.SPRUCE_PLANKS,
         Blocks.SPRUCE_STAIRS,
         Blocks.SPRUCE_SLAB,
         Blocks.SPRUCE_FENCE,
         Items.SPRUCE_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.BIRCH_LOG,
         Blocks.BIRCH_WOOD,
         Blocks.STRIPPED_BIRCH_LOG,
         Blocks.STRIPPED_BIRCH_WOOD,
         Blocks.BIRCH_PLANKS,
         Blocks.BIRCH_STAIRS,
         Blocks.BIRCH_SLAB,
         Blocks.BIRCH_FENCE,
         Items.BIRCH_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.JUNGLE_LOG,
         Blocks.JUNGLE_WOOD,
         Blocks.STRIPPED_JUNGLE_LOG,
         Blocks.STRIPPED_JUNGLE_WOOD,
         Blocks.JUNGLE_PLANKS,
         Blocks.JUNGLE_STAIRS,
         Blocks.JUNGLE_SLAB,
         Blocks.JUNGLE_FENCE,
         Items.JUNGLE_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.ACACIA_LOG,
         Blocks.ACACIA_WOOD,
         Blocks.STRIPPED_ACACIA_LOG,
         Blocks.STRIPPED_ACACIA_WOOD,
         Blocks.ACACIA_PLANKS,
         Blocks.ACACIA_STAIRS,
         Blocks.ACACIA_SLAB,
         Blocks.ACACIA_FENCE,
         Items.ACACIA_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.DARK_OAK_LOG,
         Blocks.DARK_OAK_WOOD,
         Blocks.STRIPPED_DARK_OAK_LOG,
         Blocks.STRIPPED_DARK_OAK_WOOD,
         Blocks.DARK_OAK_PLANKS,
         Blocks.DARK_OAK_STAIRS,
         Blocks.DARK_OAK_SLAB,
         Blocks.DARK_OAK_FENCE,
         Items.DARK_OAK_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.MANGROVE_LOG,
         Blocks.MANGROVE_WOOD,
         Blocks.STRIPPED_MANGROVE_LOG,
         Blocks.STRIPPED_MANGROVE_WOOD,
         Blocks.MANGROVE_PLANKS,
         Blocks.MANGROVE_STAIRS,
         Blocks.MANGROVE_SLAB,
         Blocks.MANGROVE_FENCE,
         Items.MANGROVE_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.CHERRY_LOG,
         Blocks.CHERRY_WOOD,
         Blocks.STRIPPED_CHERRY_LOG,
         Blocks.STRIPPED_CHERRY_WOOD,
         Blocks.CHERRY_PLANKS,
         Blocks.CHERRY_STAIRS,
         Blocks.CHERRY_SLAB,
         Blocks.CHERRY_FENCE,
         Items.CHERRY_BUTTON
      );
      this.addWoodFamily(
         output,
         (Block)TensuraBlocks.PALM_LOG.get(),
         (Block)TensuraBlocks.PALM_WOOD.get(),
         (Block)TensuraBlocks.STRIPPED_PALM_LOG.get(),
         (Block)TensuraBlocks.STRIPPED_PALM_WOOD.get(),
         (Block)TensuraBlocks.PALM_PLANKS.get(),
         (Block)TensuraBlocks.PALM_STAIRS.get(),
         (Block)TensuraBlocks.PALM_SLAB.get(),
         (Block)TensuraBlocks.PALM_FENCE.get(),
         (Item)TensuraBlocks.Items.PALM_BUTTON.get()
      );
      this.addWoodFamily(
         output,
         Blocks.CRIMSON_STEM,
         Blocks.CRIMSON_HYPHAE,
         Blocks.STRIPPED_CRIMSON_STEM,
         Blocks.STRIPPED_CRIMSON_HYPHAE,
         Blocks.CRIMSON_PLANKS,
         Blocks.CRIMSON_STAIRS,
         Blocks.CRIMSON_SLAB,
         Blocks.CRIMSON_FENCE,
         Items.CRIMSON_BUTTON
      );
      this.addWoodFamily(
         output,
         Blocks.WARPED_STEM,
         Blocks.WARPED_HYPHAE,
         Blocks.STRIPPED_WARPED_STEM,
         Blocks.STRIPPED_WARPED_HYPHAE,
         Blocks.WARPED_PLANKS,
         Blocks.WARPED_STAIRS,
         Blocks.WARPED_SLAB,
         Blocks.WARPED_FENCE,
         Items.WARPED_BUTTON
      );
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_STAIRS, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_STAIRS, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_SLAB, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_SLAB, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_FENCE, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_FENCE, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_BUTTON, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_BUTTON, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.STICK, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Items.STICK, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.LADDER, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Items.LADDER, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BOWL, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.STRIPPED_BAMBOO_BLOCK, Items.BOWL, 2);
      this.addWoodFamily(output, Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_SLAB, Blocks.BAMBOO_FENCE, Items.BAMBOO_BUTTON);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_MOSAIC);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_STAIRS);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_SLAB, 2);
   }

   private void addWoodFamily(
      RecipeOutput output, Block log, Block wood, Block strippedLog, Block strippedWood, Block planks, Block stairs, Block slab, Block fence, Item button
   ) {
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, strippedLog, 1);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, strippedWood, 1);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, planks, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, planks, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, planks, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, planks, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, stairs, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, stairs, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, stairs, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, stairs, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, slab, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, slab, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, slab, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, slab, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, fence, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, fence, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, fence, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, fence, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, button, 16);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, button, 16);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, button, 16);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, button, 16);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, Items.STICK, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, Items.STICK, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, Items.STICK, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, Items.STICK, 8);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, Items.LADDER, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, Items.LADDER, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, Items.LADDER, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, Items.LADDER, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, log, Items.BOWL, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wood, Items.BOWL, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedLog, Items.BOWL, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, strippedWood, Items.BOWL, 4);
      this.addWoodFamily(output, planks, stairs, slab, fence, button);
   }

   private void addWoodFamily(RecipeOutput output, Block planks, Block stairs, Block slab, Block fence, Item button) {
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, stairs);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, slab, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, fence);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, button, 4);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, Items.STICK, 2);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, Items.LADDER);
      this.woodcutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, planks, Items.BOWL);
   }

   private void miningStation(RecipeOutput recipeOutput) {
      MiningStationRecipe.Builder.of(Items.STONE, Items.ANDESITE).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.COBBLESTONE)
         .addResult(Items.GRAVEL)
         .addResult(Items.IRON_NUGGET, 0.001F)
         .addResult(Items.GOLD_NUGGET, 0.001F)
         .build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.GRAVEL).addResult(Items.SAND).addResult(Items.FLINT, 0.1F).addResult(Items.CLAY, 0.01F).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.GRANITE).addResult(Items.RED_SAND, 1, 4).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.COAL_ORE).addResult(Items.COAL, 2, 4).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_COAL_ORE).addResult(Items.COAL, 2, 4).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.IRON_ORE).addResult(Items.RAW_IRON, 1, 4).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_IRON_ORE).addResult(Items.RAW_IRON, 1, 4).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.COPPER_ORE).addResult(Items.RAW_COPPER, 2, 7).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_COPPER_ORE).addResult(Items.RAW_COPPER, 2, 7).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.GOLD_ORE).addResult(Items.RAW_GOLD, 1, 4).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_GOLD_ORE).addResult(Items.RAW_GOLD, 1, 4).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.REDSTONE_ORE).addResult(Items.REDSTONE, 4, 8).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_REDSTONE_ORE).addResult(Items.REDSTONE, 4, 8).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.LAPIS_ORE).addResult(Items.LAPIS_LAZULI, 4, 8).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_LAPIS_ORE).addResult(Items.LAPIS_LAZULI, 4, 8).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DIAMOND_ORE).addResult(Items.DIAMOND, 1, 2).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_DIAMOND_ORE).addResult(Items.DIAMOND, 1, 2).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.EMERALD_ORE).addResult(Items.EMERALD, 1, 2).addResult(Items.COBBLESTONE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.DEEPSLATE_EMERALD_ORE).addResult(Items.EMERALD, 1, 2).addResult(Items.DEEPSLATE).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.NETHER_GOLD_ORE).addResult(Items.GOLD_NUGGET, 2, 8).addResult(Items.NETHERRACK).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.NETHER_QUARTZ_ORE).addResult(Items.QUARTZ, 1, 3).addResult(Items.NETHERRACK).build(recipeOutput);
      MiningStationRecipe.Builder.of(Items.ANCIENT_DEBRIS).addResult(Items.NETHERITE_SCRAP, 1).build(recipeOutput);
      MiningStationRecipe.Builder.of((ItemLike)TensuraBlocks.SILVER_ORE.get())
         .addResult(TensuraMaterialItems.RAW_SILVER, 1, 4)
         .addResult(Items.COBBLESTONE)
         .build(recipeOutput);
      MiningStationRecipe.Builder.of((ItemLike)TensuraBlocks.DEEPSLATE_SILVER_ORE.get())
         .addResult(TensuraMaterialItems.RAW_SILVER, 1, 4)
         .addResult(Items.DEEPSLATE)
         .build(recipeOutput);
      MiningStationRecipe.Builder.of((ItemLike)TensuraBlocks.MAGIC_ORE.get())
         .addResult(TensuraMaterialItems.MAGIC_ORE, 1, 2)
         .addResult(Items.COBBLESTONE)
         .build(recipeOutput);
      MiningStationRecipe.Builder.of((ItemLike)TensuraBlocks.DEEPSLATE_MAGIC_ORE.get())
         .addResult(TensuraMaterialItems.MAGIC_ORE, 1, 2)
         .addResult(Items.DEEPSLATE)
         .build(recipeOutput);
   }

   private void gear(RecipeOutput recipeOutput) {
      this.sickle(recipeOutput, ItemTags.PLANKS, (ItemLike)TensuraToolItems.WOODEN_SICKLE.get());
      this.sickle(recipeOutput, ItemTags.STONE_TOOL_MATERIALS, (ItemLike)TensuraToolItems.STONE_SICKLE.get());
      this.sickle(recipeOutput, Items.IRON_INGOT, (ItemLike)TensuraToolItems.IRON_SICKLE.get());
      this.toolsAndSickle(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(),
         TensuraToolItems.SILVER_AXE,
         TensuraToolItems.SILVER_HOE,
         TensuraToolItems.SILVER_PICKAXE,
         TensuraToolItems.SILVER_SHOVEL,
         TensuraToolItems.SILVER_SWORD,
         TensuraToolItems.SILVER_SICKLE
      );
      this.sickle(recipeOutput, Items.GOLD_INGOT, (ItemLike)TensuraToolItems.GOLDEN_SICKLE.get());
      this.sickle(recipeOutput, Items.DIAMOND, (ItemLike)TensuraToolItems.DIAMOND_SICKLE.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)TensuraArmorItems.SILVER_HELMET.get())
         .group("silver_helmet")
         .define('S', (ItemLike)TensuraMaterialItems.SILVER_INGOT.get())
         .pattern("SSS")
         .pattern("S S")
         .unlockedBy(
            "has_silver_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)TensuraArmorItems.SILVER_CHESTPLATE.get())
         .group("silver_chestplate")
         .define('S', (ItemLike)TensuraMaterialItems.SILVER_INGOT.get())
         .pattern("S S")
         .pattern("SSS")
         .pattern("SSS")
         .unlockedBy(
            "has_silver_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)TensuraArmorItems.SILVER_LEGGINGS.get())
         .group("silver_leggings")
         .define('S', (ItemLike)TensuraMaterialItems.SILVER_INGOT.get())
         .pattern("SSS")
         .pattern("S S")
         .pattern("S S")
         .unlockedBy(
            "has_silver_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)TensuraArmorItems.SILVER_BOOTS.get())
         .group("silver_boots")
         .define('S', (ItemLike)TensuraMaterialItems.SILVER_INGOT.get())
         .pattern("S S")
         .pattern("S S")
         .unlockedBy(
            "has_silver_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}).build()})
         )
         .save(recipeOutput);
   }

   private void blocksAndItems(RecipeOutput recipeOutput) {
      this.planksFromLogs(recipeOutput, TensuraBlocks.PALM_PLANKS, TensuraItemTags.PALM_LOGS);
      woodFromLogs(recipeOutput, (ItemLike)TensuraBlocks.PALM_WOOD.get(), (ItemLike)TensuraBlocks.PALM_LOG.get());
      woodFromLogs(recipeOutput, (ItemLike)TensuraBlocks.STRIPPED_PALM_WOOD.get(), (ItemLike)TensuraBlocks.STRIPPED_PALM_LOG.get());
      this.stairs(recipeOutput, TensuraBlocks.PALM_STAIRS, TensuraBlocks.PALM_PLANKS);
      this.slab(recipeOutput, TensuraBlocks.PALM_SLAB, TensuraBlocks.PALM_PLANKS);
      this.door(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.PALM_DOOR.get());
      this.trapdoor(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.PALM_TRAPDOOR.get());
      this.button(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.PALM_BUTTON.get());
      pressurePlate(recipeOutput, (ItemLike)TensuraBlocks.PALM_PRESSURE_PLATE.get(), (ItemLike)TensuraBlocks.PALM_PLANKS.get());
      this.fence(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.PALM_FENCE.get());
      this.fenceGate(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.PALM_FENCE_GATE.get());
      this.sign(recipeOutput, (ItemLike)TensuraBlocks.PALM_PLANKS.get(), (ItemLike)TensuraBlocks.Items.PALM_SIGN.get());
      woodenBoat(recipeOutput, (ItemLike)TensuraBlocks.Items.PALM_BOAT.get(), (ItemLike)TensuraBlocks.PALM_PLANKS.get());
      this.chestboat(recipeOutput, (ItemLike)TensuraBlocks.Items.PALM_BOAT.get(), (ItemLike)TensuraBlocks.Items.PALM_CHEST_BOAT.get());
      this.nineStorage(
         recipeOutput, (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(), (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      this.nineStorage(
         recipeOutput, (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(), (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      this.nineStorage(
         recipeOutput, (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get(), (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()
      );
      this.stairs(recipeOutput, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.stairs(recipeOutput, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.stairs(recipeOutput, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab(recipeOutput, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab(recipeOutput, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab(recipeOutput, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.bricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.bricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.bricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
         (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.stairs(recipeOutput, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.stairs(recipeOutput, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.stairs(recipeOutput, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab(recipeOutput, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab(recipeOutput, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab(recipeOutput, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      wall(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(),
         (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      wall(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(),
         (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      wall(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(),
         (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.chiseledBricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.Items.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.chiseledBricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.Items.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.chiseledBricks(
         recipeOutput,
         (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(),
         (ItemLike)TensuraBlocks.Items.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()
      );
      this.stairs(recipeOutput, TensuraBlocks.WEB_STAIRS, TensuraBlocks.WEB_BLOCK);
      this.slab(recipeOutput, TensuraBlocks.WEB_SLAB, TensuraBlocks.WEB_BLOCK);
      this.stairs(recipeOutput, TensuraBlocks.WEBBED_COBBLESTONE_STAIRS, TensuraBlocks.WEBBED_COBBLESTONE);
      this.slab(recipeOutput, TensuraBlocks.WEBBED_COBBLESTONE_SLAB, TensuraBlocks.WEBBED_COBBLESTONE);
      wall(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.WEBBED_COBBLESTONE_WALL.get(),
         (ItemLike)TensuraBlocks.Items.WEBBED_COBBLESTONE.get()
      );
      this.bricks(recipeOutput, (ItemLike)TensuraBlocks.Items.WEBBED_STONE_BRICKS.get(), (ItemLike)TensuraBlocks.Items.WEBBED_COBBLESTONE.get());
      this.stairs(recipeOutput, TensuraBlocks.WEBBED_STONE_BRICK_STAIRS, TensuraBlocks.WEBBED_STONE_BRICKS);
      this.slab(recipeOutput, TensuraBlocks.WEBBED_STONE_BRICK_SLAB, TensuraBlocks.WEBBED_STONE_BRICKS);
      wall(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraBlocks.Items.WEBBED_STONE_BRICK_WALL.get(),
         (ItemLike)TensuraBlocks.Items.WEBBED_STONE_BRICKS.get()
      );
      this.bricks(recipeOutput, (ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE.get(), (ItemLike)TensuraBlocks.Items.SARASA_SAND.get());
      this.stairs(recipeOutput, TensuraBlocks.SARASA_SANDSTONE_STAIRS, TensuraBlocks.SARASA_SANDSTONE);
      this.slab(recipeOutput, TensuraBlocks.SARASA_SANDSTONE_SLAB, TensuraBlocks.SARASA_SANDSTONE);
      wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.SARASA_SANDSTONE_WALL.get(), (ItemLike)TensuraBlocks.SARASA_SANDSTONE.get());
      this.bricks(recipeOutput, (ItemLike)TensuraBlocks.Items.CUT_SARASA_SANDSTONE.get(), (ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE.get());
      this.slab(recipeOutput, TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB, TensuraBlocks.CUT_SARASA_SANDSTONE);
      this.stairs(recipeOutput, TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS, TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      this.slab(recipeOutput, TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB, TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      this.chiseledBricks(
         recipeOutput, (ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE_SLAB.get(), (ItemLike)TensuraBlocks.Items.CHISELED_SARASA_SANDSTONE.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.RAW_SILVER.get(), (ItemLike)TensuraBlocks.RAW_SILVER_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.SILVER_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.SILVER_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(), (ItemLike)TensuraBlocks.SILVER_BLOCK.get());
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(), (ItemLike)TensuraBlocks.MAGIC_ORE_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(), (ItemLike)TensuraBlocks.LOW_MAGISTEEL_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(), (ItemLike)TensuraBlocks.HIGH_MAGISTEEL_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get(), (ItemLike)TensuraBlocks.MITHRIL_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get(), (ItemLike)TensuraBlocks.ORICHALCUM_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(), (ItemLike)TensuraBlocks.PURE_MAGISTEEL_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.ADAMANTITE_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.ADAMANTITE_INGOT.get(), (ItemLike)TensuraBlocks.ADAMANTITE_BLOCK.get());
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_INGOT.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.HIHIIROKANE_INGOT.get(), (ItemLike)TensuraBlocks.HIHIIROKANE_BLOCK.get());
      this.magicBottles(
         recipeOutput, (ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get(), 3, Items.GLASS, (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()
      );
      this.magicBottles(
         recipeOutput, (ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get(), 6, Items.GLASS, (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()
      );
      this.magicBottles(
         recipeOutput, (ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get(), 9, Items.GLASS, (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get(),
         (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
         (ItemLike)TensuraMaterialItems.MAGIC_STONE.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EARTH.get(),
         (ItemLike)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get(),
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_FIRE.get(),
         (ItemLike)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get(),
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_SPACE.get(),
         (ItemLike)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get(),
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_WATER.get(),
         (ItemLike)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get(),
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()
      );
      this.elementalCores(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_WIND.get(),
         (ItemLike)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get(),
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()
      );
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLUE_DYE)
         .requires((ItemLike)TensuraMaterialItems.BAFFLEDIL.get())
         .unlockedBy(
            "has_baffledil", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.BAFFLEDIL.get()}).build()})
         )
         .save(recipeOutput, "blue_dye_from_baffledil");
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WHITE_DYE)
         .requires((ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get())
         .unlockedBy(
            "has_hipokute_flower",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get()}).build()})
         )
         .save(recipeOutput, "white_dye_from_hipokute_flower");
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.NAME_TAG)
         .requires((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get())
         .requires(Items.PAPER)
         .requires(Items.STRING)
         .unlockedBy(
            "has_monster_leather_d",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()}).build()})
         )
         .save(recipeOutput, "name_tag_from_monster_leather");
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SADDLE)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get())
         .define('I', Items.IRON_INGOT)
         .define('S', Items.STRING)
         .pattern("LLL")
         .pattern("I I")
         .pattern("S S")
         .unlockedBy(
            "has_monster_leather_d",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()}).build()})
         )
         .save(recipeOutput, "saddle_crafting");
      this.nineStorage(recipeOutput, (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(), (ItemLike)TensuraBlocks.SLIME_CHUNK_BLOCK.get());
      this.nineStorage(recipeOutput, (ItemLike)TensuraConsumableItems.CHILLED_SLIME.get(), (ItemLike)TensuraBlocks.CHILLED_SLIME_BLOCK.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SLIME_BALL)
         .requires((ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(), 4)
         .unlockedBy(
            "has_slime_chunks",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.SLIME_CHUNK.get()}).build()})
         )
         .save(recipeOutput, "slime_ball_from_chunks");
      this.enchantedApple(recipeOutput, (ItemLike)TensuraConsumableItems.CHILLED_SLIME.get(), Items.SNOWBALL, (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get());
      this.enchantedApple(
         recipeOutput, (ItemLike)TensuraBlocks.Items.CHILLED_SLIME_BLOCK.get(), Items.SNOW_BLOCK, (ItemLike)TensuraBlocks.Items.SLIME_CHUNK_BLOCK.get()
      );
      this.enchantedApple(
         recipeOutput, Items.ENCHANTED_GOLDEN_APPLE, (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get(), Items.GOLDEN_APPLE, "orichalcum_ingot"
      );
      this.enchantedApple(
         recipeOutput, (ItemLike)TensuraConsumableItems.SILVER_APPLE.get(), (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(), Items.APPLE, "silver_ingot"
      );
      this.enchantedApple(
         recipeOutput,
         (ItemLike)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get(),
         (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get(),
         (ItemLike)TensuraConsumableItems.SILVER_APPLE.get(),
         "mithril_ingot"
      );
      this.raceResetScroll(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.RACE_RESET_SCROLL.get(),
         Items.PAPER,
         (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get(),
         (ItemLike)TensuraMaterialItems.MAGIC_STONE.get(),
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get()
      );
      this.skillResetScroll(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.SKILL_RESET_SCROLL.get(),
         Items.PAPER,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
         (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
         (ItemLike)TensuraMaterialItems.MAGIC_STONE.get(),
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get()
      );
      this.skillResetScroll(
         recipeOutput,
         (ItemLike)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get(),
         Items.PAPER,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
         (ItemLike)TensuraMaterialItems.MAGIC_STONE.get(),
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get()
      );
      this.nineStorage(recipeOutput, (ItemLike)TensuraMaterialItems.THATCH.get(), (ItemLike)TensuraBlocks.THATCH_BLOCK.get());
      this.stairs(recipeOutput, TensuraBlocks.THATCH_STAIRS, TensuraBlocks.THATCH_BLOCK);
      this.slab(recipeOutput, TensuraBlocks.THATCH_SLAB, TensuraBlocks.THATCH_BLOCK);
      wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.THATCH_WALL.get(), (ItemLike)TensuraBlocks.Items.THATCH_BLOCK.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.TATAMI_BLOCK.get(), 4)
         .group("tatami_block")
         .define('#', (ItemLike)TensuraMaterialItems.THATCH.get())
         .define('B', (ItemLike)TensuraBlocks.Items.THATCH_BLOCK.get())
         .pattern("##")
         .pattern("BB")
         .pattern("##")
         .unlockedBy(
            "has_thatch", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.THATCH.get()}).build()})
         )
         .unlockedBy(
            "has_thatch_block",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.THATCH_BLOCK.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.TATAMI_CARPET.get(), 3)
         .group("tatami_carpet")
         .define('B', (ItemLike)TensuraBlocks.Items.TATAMI_BLOCK.get())
         .pattern("BB")
         .unlockedBy(
            "has_tatami_block",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.TATAMI_BLOCK.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SINGLE_TATAMI_BLOCK.get(), 4)
         .group("single_tatami_block")
         .define('#', (ItemLike)TensuraBlocks.Items.TATAMI_BLOCK.get())
         .pattern("##")
         .pattern("##")
         .unlockedBy(
            "has_tatami_block",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.TATAMI_BLOCK.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SINGLE_TATAMI_CARPET.get(), 3)
         .group("single_tatami_carpet")
         .define('B', (ItemLike)TensuraBlocks.Items.SINGLE_TATAMI_BLOCK.get())
         .pattern("BB")
         .unlockedBy(
            "has_single_tatami_block",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.SINGLE_TATAMI_BLOCK.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.MINING_STATION.get(), 1)
         .group("mining_station")
         .define('#', ItemTags.PLANKS)
         .define('I', Items.IRON_INGOT)
         .define('S', ItemTags.STONE_CRAFTING_MATERIALS)
         .pattern("II")
         .pattern("SS")
         .pattern("##")
         .unlockedBy("has_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(ItemTags.PLANKS).build()}))
         .unlockedBy("has_iron_ingot", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.IRON_INGOT}).build()}))
         .unlockedBy("has_stones", inventoryTrigger(new ItemPredicate[]{Builder.item().of(ItemTags.STONE_CRAFTING_MATERIALS).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SMITHING_BENCH.get(), 1)
         .group("smithing_bench")
         .define('P', Items.PAPER)
         .define('C', Items.CRAFTING_TABLE)
         .define('S', Items.SMITHING_TABLE)
         .define('L', ItemTags.PLANKS)
         .pattern("PP")
         .pattern("CS")
         .pattern("LL")
         .unlockedBy("has_paper", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PAPER}).build()}))
         .unlockedBy("has_crafting_table", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CRAFTING_TABLE}).build()}))
         .unlockedBy("has_smithing_table", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SMITHING_TABLE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SPELLBINDING_TABLE.get(), 1)
         .group("spellbinding_table")
         .define('#', Items.CRYING_OBSIDIAN)
         .define('N', (ItemLike)TensuraMaterialItems.MAGIC_STONE.get())
         .define('S', (ItemLike)TensuraMaterialItems.SILVER_INGOT.get())
         .pattern(" N ")
         .pattern("S#S")
         .pattern("###")
         .unlockedBy("has_crying_obsidian", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CRYING_OBSIDIAN}).build()}))
         .unlockedBy(
            "has_magic_stone",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.MAGIC_STONE.get()}).build()})
         )
         .unlockedBy(
            "has_silver", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.WOODCUTTER.get(), 1)
         .group("woodcutter")
         .define('#', ItemTags.PLANKS)
         .define('I', Items.IRON_INGOT)
         .pattern(" I ")
         .pattern("###")
         .unlockedBy("has_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(ItemTags.PLANKS).build()}))
         .unlockedBy("has_iron_ingot", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.IRON_INGOT}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.OAK_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.OAK_SLAB)
         .define('F', Blocks.OAK_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_oak_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.OAK_SLAB}).build()}))
         .unlockedBy("has_oak_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.OAK_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SPRUCE_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.SPRUCE_SLAB)
         .define('F', Blocks.SPRUCE_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_spruce_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.SPRUCE_SLAB}).build()}))
         .unlockedBy("has_spruce_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.SPRUCE_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BIRCH_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.BIRCH_SLAB)
         .define('F', Blocks.BIRCH_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_birch_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.BIRCH_SLAB}).build()}))
         .unlockedBy("has_birch_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.BIRCH_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.JUNGLE_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.JUNGLE_SLAB)
         .define('F', Blocks.JUNGLE_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_jungle_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.JUNGLE_SLAB}).build()}))
         .unlockedBy("has_jungle_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.JUNGLE_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.ACACIA_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.ACACIA_SLAB)
         .define('F', Blocks.ACACIA_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_acacia_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.ACACIA_SLAB}).build()}))
         .unlockedBy("has_acacia_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.ACACIA_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.DARK_OAK_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.DARK_OAK_SLAB)
         .define('F', Blocks.DARK_OAK_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_dark_oak_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.DARK_OAK_SLAB}).build()}))
         .unlockedBy("has_dark_oak_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.DARK_OAK_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.MANGROVE_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.MANGROVE_SLAB)
         .define('F', Blocks.MANGROVE_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_mangrove_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.MANGROVE_SLAB}).build()}))
         .unlockedBy("has_mangrove_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.MANGROVE_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.CHERRY_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.CHERRY_SLAB)
         .define('F', Blocks.CHERRY_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_cherry_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.CHERRY_SLAB}).build()}))
         .unlockedBy("has_cherry_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.CHERRY_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PALM_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', (ItemLike)TensuraBlocks.PALM_SLAB.get())
         .define('F', (ItemLike)TensuraBlocks.PALM_FENCE.get())
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_palm_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.PALM_SLAB.get()}).build()}))
         .unlockedBy(
            "has_palm_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.PALM_FENCE.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BAMBOO_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.BAMBOO_SLAB)
         .define('F', Blocks.BAMBOO_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_bamboo_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.BAMBOO_SLAB}).build()}))
         .unlockedBy("has_bamboo_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.BAMBOO_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.CRIMSON_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.CRIMSON_SLAB)
         .define('F', Blocks.CRIMSON_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_crimson_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.CRIMSON_SLAB}).build()}))
         .unlockedBy("has_crimson_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.CRIMSON_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.WARPED_TOOL_RACK.get(), 1)
         .group("tool_rack")
         .define('#', Blocks.WARPED_SLAB)
         .define('F', Blocks.WARPED_FENCE)
         .define('S', Items.STICK)
         .pattern("SSS")
         .pattern("F F")
         .pattern("###")
         .unlockedBy("has_warped_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.WARPED_SLAB}).build()}))
         .unlockedBy("has_warped_fence", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.WARPED_FENCE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BRICKS_MAGIC_ENGINE.get(), 1)
         .group("bricks_magic_engine")
         .define('#', Items.BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.STONE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("stone_bricks_magic_engine")
         .define('#', Items.STONE_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_stone_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STONE_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.TUFF_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("tuff_bricks_magic_engine")
         .define('#', Items.TUFF_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_tuff_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.TUFF_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.DEEPSLATE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("deepslate_bricks_magic_engine")
         .define('#', Items.DEEPSLATE_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_deepslate_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.DEEPSLATE_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.MUD_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("mud_bricks_magic_engine")
         .define('#', Items.MUD_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_mud_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.MUD_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PRISMARINE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("prismarine_bricks_magic_engine")
         .define('#', Items.PRISMARINE_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_prismarine_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PRISMARINE_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("nether_bricks_magic_engine")
         .define('#', Items.NETHER_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_nether_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.NETHER_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("red_nether_bricks_magic_engine")
         .define('#', Items.RED_NETHER_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_red_nether_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.RED_NETHER_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("polished_blackstone_bricks_magic_engine")
         .define('#', Items.POLISHED_BLACKSTONE_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy(
            "has_polished_blackstone_bricks",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_BLACKSTONE_BRICKS}).build()})
         )
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.QUARTZ_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("quartz_bricks_magic_engine")
         .define('#', Items.QUARTZ_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_quartz_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.QUARTZ_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.END_STONE_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("end_stone_bricks_magic_engine")
         .define('#', Items.END_STONE_BRICKS)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_end_stone_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.END_STONE_BRICKS}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PURPUR_MAGIC_ENGINE.get(), 1)
         .group("purpur_bricks_magic_engine")
         .define('#', Items.PURPUR_BLOCK)
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy("has_purpur_block", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PURPUR_BLOCK}).build()}))
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("low_quality_magic_crystal_bricks_magic_engine")
         .define('#', (ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get())
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy(
            "has_low_quality_magic_crystal_bricks",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}).build()})
         )
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("medium_quality_magic_crystal_bricks_magic_engine")
         .define('#', (ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get())
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy(
            "has_medium_quality_magic_crystal_bricks",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}).build()})
         )
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), 1)
         .group("high_quality_magic_crystal_bricks_magic_engine")
         .define('#', (ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get())
         .define('I', (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())
         .define('S', (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get())
         .pattern("#S#")
         .pattern("#I#")
         .pattern("###")
         .unlockedBy(
            "has_high_quality_magic_crystal_bricks",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()}).build()})
         )
         .unlockedBy(
            "has_magisteel_ingot",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_magic_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.WARP_CORE.get(), 1)
         .group("warp_core")
         .define('S', (ItemLike)TensuraMaterialItems.MAGIC_STONE.get())
         .define('L', (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get())
         .define('C', (ItemLike)TensuraMaterialItems.ELEMENT_CORE_SPACE.get())
         .define('E', Items.ENDER_PEARL)
         .pattern("ELC")
         .pattern("LSL")
         .pattern("CLE")
         .unlockedBy(
            "has_magic_stone",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.MAGIC_STONE.get()}).build()})
         )
         .unlockedBy(
            "has_low_magisteel",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get()}).build()})
         )
         .unlockedBy(
            "has_space_core",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.ELEMENT_CORE_SPACE.get()}).build()})
         )
         .unlockedBy("has_ender_pearl", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.ENDER_PEARL}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.STONE_WARP_PAD.get(), 1)
         .group("stone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.STONE)
         .define('M', Items.SMOOTH_STONE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_stone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STONE}).build()}))
         .unlockedBy("has_smooth_stone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SMOOTH_STONE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.GRANITE_WARP_PAD.get(), 1)
         .group("granite_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.GRANITE)
         .define('M', Items.POLISHED_GRANITE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_granite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.GRANITE}).build()}))
         .unlockedBy("has_polished_granite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_GRANITE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.DIORITE_WARP_PAD.get(), 1)
         .group("diorite_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.DIORITE)
         .define('M', Items.POLISHED_DIORITE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_diorite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.DIORITE}).build()}))
         .unlockedBy("has_polished_diorite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_DIORITE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.ANDESITE_WARP_PAD.get(), 1)
         .group("andesite_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.ANDESITE)
         .define('M', Items.POLISHED_ANDESITE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_andesite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.ANDESITE}).build()}))
         .unlockedBy("has_polished_andesite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_ANDESITE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.CALCITE_WARP_PAD.get(), 1)
         .group("calcite_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.CALCITE)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_calcite", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CALCITE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.TUFF_WARP_PAD.get(), 1)
         .group("tuff_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.TUFF)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_tuff", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.TUFF}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.DEEPSLATE_WARP_PAD.get(), 1)
         .group("deepslate_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.DEEPSLATE)
         .define('M', Items.POLISHED_DEEPSLATE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_deepslate", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.DEEPSLATE}).build()}))
         .unlockedBy("has_polished_deepslate", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_DEEPSLATE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BRICK_WARP_PAD.get(), 1)
         .group("brick_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.BRICKS)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SANDSTONE_WARP_PAD.get(), 1)
         .group("sandstone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.SMOOTH_SANDSTONE)
         .define('M', Items.CUT_SANDSTONE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_cut_sandstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CUT_SANDSTONE}).build()}))
         .unlockedBy("has_smooth_sandstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SMOOTH_SANDSTONE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.RED_SANDSTONE_WARP_PAD.get(), 1)
         .group("red_sandstone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.SMOOTH_RED_SANDSTONE)
         .define('M', Items.CUT_RED_SANDSTONE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_cut_red_sandstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CUT_RED_SANDSTONE}).build()}))
         .unlockedBy("has_smooth_red_sandstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SMOOTH_RED_SANDSTONE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.SARASA_SANDSTONE_WARP_PAD.get(), 1)
         .group("sarasa_sandstone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', (ItemLike)TensuraBlocks.Items.SMOOTH_SARASA_SANDSTONE.get())
         .define('M', (ItemLike)TensuraBlocks.Items.CUT_SARASA_SANDSTONE.get())
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy(
            "has_cut_sarasa_sandstone",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.CUT_SARASA_SANDSTONE.get()}).build()})
         )
         .unlockedBy(
            "has_smooth_sarasa_sandstone",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.SMOOTH_SARASA_SANDSTONE.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PACKED_MUD_WARP_PAD.get(), 1)
         .group("packed_mud_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.PACKED_MUD)
         .define('M', Items.MUD_BRICKS)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_packed_mud", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PACKED_MUD}).build()}))
         .unlockedBy("has_mud_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.MUD_BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PRISMARINE_BRICK_WARP_PAD.get(), 1)
         .group("prismarine_brick_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.PRISMARINE_BRICKS)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_prismarine_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PRISMARINE_BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.NETHER_BRICK_WARP_PAD.get(), 1)
         .group("nether_brick_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.NETHER_BRICKS)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_nether_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.NETHER_BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.RED_NETHER_BRICK_WARP_PAD.get(), 1)
         .group("red_nether_brick_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('M', Items.RED_NETHER_BRICKS)
         .pattern("MMM")
         .pattern("MCM")
         .pattern("MMM")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_red_nether_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.RED_NETHER_BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BASALT_WARP_PAD.get(), 1)
         .group("basalt_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.SMOOTH_BASALT)
         .define('M', Items.BASALT)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_basalt", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BASALT}).build()}))
         .unlockedBy("has_smooth_basalt", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SMOOTH_BASALT}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.BLACKSTONE_WARP_PAD.get(), 1)
         .group("blackstone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.BLACKSTONE)
         .define('M', Items.POLISHED_BLACKSTONE)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_blackstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BLACKSTONE}).build()}))
         .unlockedBy("has_polished_blackstone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.POLISHED_BLACKSTONE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.QUARTZ_WARP_PAD.get(), 1)
         .group("quartz_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.QUARTZ_BLOCK)
         .define('M', Items.QUARTZ_PILLAR)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_quartz_block", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.QUARTZ_BLOCK}).build()}))
         .unlockedBy("has_quartz_pillar", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.QUARTZ_PILLAR}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.END_STONE_WARP_PAD.get(), 1)
         .group("end_stone_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.END_STONE)
         .define('M', Items.END_STONE_BRICKS)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_end_stone", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.END_STONE}).build()}))
         .unlockedBy("has_end_stone_bricks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.END_STONE_BRICKS}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.PURPUR_WARP_PAD.get(), 1)
         .group("purpur_warp_pad")
         .define('C', (ItemLike)TensuraMaterialItems.WARP_CORE.get())
         .define('S', Items.PURPUR_BLOCK)
         .define('M', Items.PURPUR_PILLAR)
         .pattern("SMS")
         .pattern("MCM")
         .pattern("SMS")
         .unlockedBy(
            "has_warp_core", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.WARP_CORE.get()}).build()})
         )
         .unlockedBy("has_purpur_block", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PURPUR_BLOCK}).build()}))
         .unlockedBy("has_purpur_pillar", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.PURPUR_PILLAR}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.KILN.get(), 1)
         .group("kiln")
         .define('#', Items.OBSIDIAN)
         .define('N', Items.NETHERITE_INGOT)
         .define('C', Items.CAULDRON)
         .define('B', Items.BLAST_FURNACE)
         .pattern("#N#")
         .pattern("CBC")
         .pattern("###")
         .unlockedBy("has_obsidian", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.OBSIDIAN}).build()}))
         .unlockedBy("has_netherite_ingot", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.NETHERITE_INGOT}).build()}))
         .unlockedBy("has_blast_furnace", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BLAST_FURNACE}).build()}))
         .unlockedBy("has_cauldron", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CAULDRON}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get(), 1)
         .group("kiln")
         .define('#', (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get())
         .define('N', Items.NETHERITE_INGOT)
         .define('C', Items.CAULDRON)
         .define('K', (ItemLike)TensuraBlocks.Items.KILN.get())
         .pattern("#N#")
         .pattern("CKC")
         .pattern("#N#")
         .unlockedBy("has_obsidian", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.OBSIDIAN}).build()}))
         .unlockedBy("has_netherite_ingot", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.NETHERITE_INGOT}).build()}))
         .unlockedBy("has_kiln", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.KILN.get()}).build()}))
         .unlockedBy("has_cauldron", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CAULDRON}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.Items.KILN_ORICHALCUM.get(), 1)
         .group("kiln")
         .define('#', (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get())
         .define('N', Items.NETHERITE_INGOT)
         .define('C', Items.CAULDRON)
         .define('K', (ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get())
         .pattern("#N#")
         .pattern("CKC")
         .pattern("#N#")
         .unlockedBy("has_obsidian", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.OBSIDIAN}).build()}))
         .unlockedBy("has_netherite_ingot", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.NETHERITE_INGOT}).build()}))
         .unlockedBy(
            "has_kiln_mithril",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get()}).build()})
         )
         .unlockedBy("has_cauldron", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CAULDRON}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)TensuraBlocks.THATCH_BED.get(), 1)
         .group("beds")
         .define('#', (ItemLike)TensuraBlocks.THATCH_BLOCK.get())
         .define('I', (ItemLike)TensuraMaterialItems.THATCH.get())
         .pattern("   ")
         .pattern("  I")
         .pattern("###")
         .unlockedBy(
            "has_thatch_block", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.THATCH_BLOCK.get()}).build()})
         )
         .unlockedBy(
            "has_thatch_item", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.THATCH.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraBlocks.Items.TRAINING_DUMMY.get(), 1)
         .define('B', (ItemLike)TensuraBlocks.THATCH_BLOCK.get())
         .define('T', (ItemLike)TensuraMaterialItems.THATCH.get())
         .define('F', ItemTags.WOODEN_FENCES)
         .define('S', Items.SMOOTH_STONE_SLAB)
         .pattern("TBT")
         .pattern("TFT")
         .pattern(" S ")
         .unlockedBy(
            "has_thatch_block", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraBlocks.THATCH_BLOCK.get()}).build()})
         )
         .unlockedBy(
            "has_thatch_item", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.THATCH.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)TensuraConsumableItems.DUBIOUS_FOOD.get())
         .requires(TensuraItemTags.DUBIOUS_POISON_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_MAGIC_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_RAW_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_EFFECT_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_CRYSTAL_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_BREWING_INGREDIENT)
         .requires(TensuraItemTags.DUBIOUS_MUSHROOM_INGREDIENT)
         .requires(TensuraItemTags.RAW_MONSTER_CONSUMABLES)
         .requires(ItemTags.SMALL_FLOWERS)
         .unlockedBy("has_dubious_ingredient", inventoryTrigger(new ItemPredicate[]{Builder.item().of(TensuraItemTags.DUBIOUS_EFFECT_INGREDIENT).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.POUCH_D.get(), 1)
         .group("pouches")
         .define('S', Items.STRING)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get())
         .pattern(" S ")
         .pattern("L L")
         .pattern(" L ")
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .unlockedBy(
            "has_monster_leather_d",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.POUCH_C.get(), 1)
         .group("pouches")
         .define('S', Items.STRING)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
         .pattern(" S ")
         .pattern("L L")
         .pattern(" L ")
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .unlockedBy(
            "has_monster_leather_c",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.POUCH_B.get(), 1)
         .group("pouches")
         .define('S', Items.STRING)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get())
         .pattern(" S ")
         .pattern("L L")
         .pattern(" L ")
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .unlockedBy(
            "has_monster_leather_b",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.POUCH_A.get(), 1)
         .group("pouches")
         .define('S', Items.STRING)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get())
         .pattern(" S ")
         .pattern("L L")
         .pattern(" L ")
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .unlockedBy(
            "has_monster_leather_a",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.POUCH_SPECIAL_A.get(), 1)
         .group("pouches")
         .define('S', Items.STRING)
         .define('L', (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get())
         .pattern(" S ")
         .pattern("L L")
         .pattern(" L ")
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .unlockedBy(
            "has_monster_leather_special_a",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)TensuraMaterialItems.DAEMON_CORE.get(), 1)
         .group("daemon_core")
         .define('S', (ItemLike)TensuraMaterialItems.MAGIC_STONE.get())
         .define('D', (ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get())
         .pattern("DDD")
         .pattern("DSD")
         .pattern("DDD")
         .unlockedBy(
            "has_magic_stone",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.MAGIC_STONE.get()}).build()})
         )
         .unlockedBy(
            "has_daemon_essence",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()}).build()})
         )
         .save(recipeOutput);
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM,
         TensuraMaterialItems.LOW_MAGISTEEL_INGOT,
         TensuraArmorItems.LOW_MAGISTEEL_HELMET,
         TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE,
         TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS,
         TensuraArmorItems.LOW_MAGISTEEL_BOOTS,
         "has_low_magisteel_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.HIGH_MAGISTEEL_BONE_GOLEM,
         TensuraMaterialItems.HIGH_MAGISTEEL_INGOT,
         TensuraArmorItems.HIGH_MAGISTEEL_HELMET,
         TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE,
         TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS,
         TensuraArmorItems.HIGH_MAGISTEEL_BOOTS,
         "has_high_magisteel_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.MITHRIL_BONE_GOLEM,
         TensuraMaterialItems.PURE_MAGISTEEL_INGOT,
         TensuraArmorItems.MITHRIL_HELMET,
         TensuraArmorItems.MITHRIL_CHESTPLATE,
         TensuraArmorItems.MITHRIL_LEGGINGS,
         TensuraArmorItems.MITHRIL_BOOTS,
         "has_mithril_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.PURE_MAGISTEEL_BONE_GOLEM,
         TensuraMaterialItems.PURE_MAGISTEEL_INGOT,
         TensuraArmorItems.PURE_MAGISTEEL_HELMET,
         TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE,
         TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS,
         TensuraArmorItems.PURE_MAGISTEEL_BOOTS,
         "has_pure_magisteel_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.ORICHALCUM_BONE_GOLEM,
         TensuraMaterialItems.PURE_MAGISTEEL_INGOT,
         TensuraArmorItems.ORICHALCUM_HELMET,
         TensuraArmorItems.ORICHALCUM_CHESTPLATE,
         TensuraArmorItems.ORICHALCUM_LEGGINGS,
         TensuraArmorItems.ORICHALCUM_BOOTS,
         "has_orichalcum_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.ADAMANTITE_BONE_GOLEM,
         TensuraMaterialItems.PURE_MAGISTEEL_INGOT,
         TensuraArmorItems.ADAMANTITE_HELMET,
         TensuraArmorItems.ADAMANTITE_CHESTPLATE,
         TensuraArmorItems.ADAMANTITE_LEGGINGS,
         TensuraArmorItems.ADAMANTITE_BOOTS,
         "has_adamantite_ingot"
      );
      this.boneGolem(
         recipeOutput,
         TensuraMaterialItems.HIHIIROKANE_BONE_GOLEM,
         TensuraMaterialItems.PURE_MAGISTEEL_INGOT,
         TensuraArmorItems.HIHIIROKANE_HELMET,
         TensuraArmorItems.HIHIIROKANE_CHESTPLATE,
         TensuraArmorItems.HIHIIROKANE_LEGGINGS,
         TensuraArmorItems.HIHIIROKANE_BOOTS,
         "has_hihiirokane_ingot"
      );
   }

   private void nineStorage(RecipeOutput output, ItemLike material, ItemLike block) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material, 9)
         .requires(block)
         .unlockedBy(getHasName(block), has(block))
         .save(output, getRecipeName(material, "from_" + getItemName(block)));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
         .define('#', material)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .unlockedBy(getHasName(material), has(material))
         .save(output, getRecipeName(block, "from_" + getItemName(material)));
   }

   private void slab(RecipeOutput recipeOutput, RegistrySupplier<SlabBlock> slab, RegistrySupplier<? extends Block> planks) {
      slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, (ItemLike)slab.get(), (ItemLike)planks.get());
   }

   private void stairs(RecipeOutput recipeOutput, Supplier<? extends StairBlock> stairs, RegistrySupplier<? extends Block> planks) {
      stairBuilder((ItemLike)stairs.get(), Ingredient.of(new ItemLike[]{(ItemLike)planks.get()}))
         .unlockedBy(RecipeProvider.getHasName((ItemLike)planks.get()), has((ItemLike)planks.get()))
         .save(recipeOutput);
   }

   private void planksFromLogs(RecipeOutput recipeOutput, RegistrySupplier<? extends ItemLike> palmPlanks, TagKey<Item> palmLogs) {
      planksFromLogs(recipeOutput, (ItemLike)palmPlanks.get(), palmLogs, 4);
   }

   protected void toolsAndSickle(
      RecipeOutput recipeOutput,
      TagKey<Item> tag,
      RegistrySupplier<? extends Item> axe,
      RegistrySupplier<? extends Item> hoe,
      RegistrySupplier<? extends Item> pickaxe,
      RegistrySupplier<? extends Item> shovel,
      RegistrySupplier<? extends Item> sword,
      RegistrySupplier<? extends Item> sickle
   ) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)axe.get())
         .define('#', Items.STICK)
         .define('X', tag)
         .pattern("XX")
         .pattern("X#")
         .pattern(" #")
         .unlockedBy("has_diamond", has(tag))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)hoe.get())
         .define('#', Items.STICK)
         .define('X', tag)
         .pattern("XX")
         .pattern(" #")
         .pattern(" #")
         .unlockedBy("has_diamond", has(tag))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)pickaxe.get())
         .define('#', Items.STICK)
         .define('X', tag)
         .pattern("XXX")
         .pattern(" # ")
         .pattern(" # ")
         .unlockedBy("has_diamond", has(tag))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)shovel.get())
         .define('#', Items.STICK)
         .define('X', tag)
         .pattern("X")
         .pattern("#")
         .pattern("#")
         .unlockedBy("has_diamond", has(tag))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)sword.get())
         .define('#', Items.STICK)
         .define('X', tag)
         .pattern("X")
         .pattern("X")
         .pattern("#")
         .unlockedBy("has_diamond", has(tag))
         .save(recipeOutput);
      this.sickle(recipeOutput, tag, (ItemLike)sickle.get());
   }

   protected void toolsAndSickle(
      RecipeOutput recipeOutput,
      ItemLike itemLike,
      RegistrySupplier<? extends Item> axe,
      RegistrySupplier<? extends Item> hoe,
      RegistrySupplier<? extends Item> pickaxe,
      RegistrySupplier<? extends Item> shovel,
      RegistrySupplier<? extends Item> sword,
      RegistrySupplier<? extends Item> sickle
   ) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)axe.get())
         .define('#', Items.STICK)
         .define('X', itemLike)
         .pattern("XX")
         .pattern("X#")
         .pattern(" #")
         .unlockedBy("has_diamond", has(itemLike))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)hoe.get())
         .define('#', Items.STICK)
         .define('X', itemLike)
         .pattern("XX")
         .pattern(" #")
         .pattern(" #")
         .unlockedBy("has_diamond", has(itemLike))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)pickaxe.get())
         .define('#', Items.STICK)
         .define('X', itemLike)
         .pattern("XXX")
         .pattern(" # ")
         .pattern(" # ")
         .unlockedBy("has_diamond", has(itemLike))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)shovel.get())
         .define('#', Items.STICK)
         .define('X', itemLike)
         .pattern("X")
         .pattern("#")
         .pattern("#")
         .unlockedBy("has_diamond", has(itemLike))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)sword.get())
         .define('#', Items.STICK)
         .define('X', itemLike)
         .pattern("X")
         .pattern("X")
         .pattern("#")
         .unlockedBy("has_diamond", has(itemLike))
         .save(recipeOutput);
      this.sickle(recipeOutput, itemLike, (ItemLike)sickle.get());
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike sickle) {
      this.sickle(finishedReciperecipeOutput, Ingredient.of(new ItemLike[]{material}), sickle);
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, TagKey<Item> material, ItemLike sickle) {
      this.sickle(finishedReciperecipeOutput, Ingredient.of(material), sickle);
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, Ingredient material, ItemLike sickle) {
      this.sickle(finishedReciperecipeOutput, material, Ingredient.of(new ItemLike[]{Items.STICK}), sickle);
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike stick, ItemLike sickle) {
      this.sickle(finishedReciperecipeOutput, Ingredient.of(new ItemLike[]{material}), Ingredient.of(new ItemLike[]{stick}), sickle);
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, TagKey<Item> material, TagKey<Item> stick, ItemLike sickle) {
      this.sickle(finishedReciperecipeOutput, Ingredient.of(material), Ingredient.of(stick), sickle);
   }

   protected void sickle(RecipeOutput finishedReciperecipeOutput, Ingredient material, Ingredient stick, ItemLike sickle) {
      Criterion<?> unlockCriterion = inventoryTrigger(
         new ItemPredicate[]{Builder.item().of(Arrays.stream(material.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new)).build()}
      );
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, sickle)
         .pattern("XXX")
         .pattern("  S")
         .pattern("  S")
         .define('X', material)
         .define('S', stick)
         .unlockedBy("has_material_for_" + RecipeBuilder.getDefaultRecipeId(sickle).getPath(), unlockCriterion)
         .save(finishedReciperecipeOutput);
   }

   protected void door(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike door) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, door, 3)
         .define('#', material)
         .pattern("##")
         .pattern("##")
         .pattern("##")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void trapdoor(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike trapdoor) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, trapdoor, 2)
         .define('#', material)
         .pattern("###")
         .pattern("###")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void button(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike button) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, button)
         .requires(material)
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void fence(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike fence) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fence, 3)
         .define('W', material)
         .define('#', Items.STICK)
         .pattern("W#W")
         .pattern("W#W")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void fenceGate(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike fenceGate) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fenceGate)
         .define('W', material)
         .define('#', Items.STICK)
         .pattern("#W#")
         .pattern("#W#")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void sign(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike sign) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, sign, 3)
         .group("sign")
         .define('#', material)
         .define('X', Items.STICK)
         .pattern("###")
         .pattern("###")
         .pattern(" X ")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void chestboat(RecipeOutput finishedReciperecipeOutput, ItemLike boat, ItemLike chestBoat) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, chestBoat)
         .requires(Blocks.CHEST)
         .requires(boat)
         .group("chest_boat")
         .unlockedBy("has_boat", has(ItemTags.BOATS))
         .save(finishedReciperecipeOutput);
   }

   protected void bricks(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike bricks) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bricks, 4)
         .define('#', material)
         .pattern("##")
         .pattern("##")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void chiseledBricks(RecipeOutput finishedReciperecipeOutput, ItemLike material, ItemLike bricks) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bricks, 1)
         .define('#', material)
         .pattern("#")
         .pattern("#")
         .unlockedBy(getHasName(material), has(material))
         .save(finishedReciperecipeOutput);
   }

   protected void magicBottles(RecipeOutput finishedReciperecipeOutput, ItemLike bottle, int count, ItemLike glass, ItemLike crystal) {
      String unlock = count <= 3 ? "low_crystal" : (count <= 6 ? "medium_crystal" : "high_crystal");
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bottle, count)
         .define('#', glass)
         .define('X', crystal)
         .pattern("#X#")
         .pattern(" # ")
         .group("magic_bottles")
         .unlockedBy("has_" + unlock, has(crystal))
         .save(finishedReciperecipeOutput, "bottles_of_" + unlock);
   }

   protected void enchantedApple(RecipeOutput finishedReciperecipeOutput, ItemLike core, ItemLike material, ItemLike apple) {
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, core)
         .define('#', material)
         .define('X', apple)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .unlockedBy("has_material", has(apple))
         .save(finishedReciperecipeOutput, getItemName(apple) + "_from_" + getItemName(material));
   }

   protected void enchantedApple(RecipeOutput finishedReciperecipeOutput, ItemLike core, ItemLike material, ItemLike apple, String string) {
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, core)
         .define('#', material)
         .define('X', apple)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .unlockedBy("has_material", has(apple))
         .save(finishedReciperecipeOutput, getItemName(apple) + "_from_" + string);
   }

   protected void elementalCores(RecipeOutput finishedReciperecipeOutput, ItemLike core, ItemLike shard, ItemLike emptyCore) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, core)
         .define('#', shard)
         .define('X', emptyCore)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .unlockedBy("has_empty_core", has((ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get()))
         .save(finishedReciperecipeOutput);
   }

   protected void raceResetScroll(RecipeOutput finishedReciperecipeOutput, ItemLike scroll, ItemLike paper, ItemLike mithril, ItemLike stone, ItemLike leather) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, scroll)
         .define('P', paper)
         .define('M', mithril)
         .define('S', stone)
         .define('L', leather)
         .pattern("PMP")
         .pattern("LSL")
         .pattern("PMP")
         .unlockedBy("has_mithril", has((ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get()))
         .save(finishedReciperecipeOutput);
   }

   protected void skillResetScroll(
      RecipeOutput finishedReciperecipeOutput, ItemLike scroll, ItemLike paper, ItemLike pure, ItemLike orichalcum, ItemLike stone, ItemLike leather
   ) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, scroll)
         .define('P', paper)
         .define('#', pure)
         .define('O', orichalcum)
         .define('S', stone)
         .define('L', leather)
         .pattern("P#P")
         .pattern("OSO")
         .pattern("LPL")
         .unlockedBy("has_orichalcum", has((ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get()))
         .save(finishedReciperecipeOutput);
   }

   private void boneGolem(
      RecipeOutput recipeOutput,
      RegistrySupplier<Item> result,
      RegistrySupplier<Item> ingot,
      RegistrySupplier<Item> helmet,
      RegistrySupplier<Item> chestplate,
      RegistrySupplier<Item> leggings,
      RegistrySupplier<Item> boots,
      String unlockName
   ) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)result.get(), 1)
         .group("bone_golem")
         .define('D', (ItemLike)TensuraMaterialItems.DAEMON_CORE.get())
         .define('I', (ItemLike)ingot.get())
         .define('H', (ItemLike)helmet.get())
         .define('C', (ItemLike)chestplate.get())
         .define('L', (ItemLike)leggings.get())
         .define('B', (ItemLike)boots.get())
         .pattern("IHI")
         .pattern("CDL")
         .pattern("IBI")
         .unlockedBy(
            "has_daemon_core",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.DAEMON_CORE.get()}).build()})
         )
         .unlockedBy(unlockName, inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ingot.get()}).build()}))
         .save(recipeOutput);
   }

   private void woodcutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike ingredient) {
      this.woodcutterResultFromBase(output, category, result, ingredient, 1);
   }

   private void woodcutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike from, ItemLike into, int count) {
      SingleItemRecipeBuilder builder = this.woodcutting(Ingredient.of(new ItemLike[]{from}), category, into, count).unlockedBy(getHasName(from), has(from));
      String name = getConversionRecipeName(into, from);
      builder.save(output, name + "_woodcutting");
   }

   private SingleItemRecipeBuilder woodcutting(Ingredient ingredient, RecipeCategory category, ItemLike result) {
      return new SingleItemRecipeBuilder(category, WoodcutterRecipe::new, ingredient, result, 1);
   }

   private SingleItemRecipeBuilder woodcutting(Ingredient ingredient, RecipeCategory category, ItemLike result, int count) {
      return new SingleItemRecipeBuilder(category, WoodcutterRecipe::new, ingredient, result, count);
   }

   private enum BottleType {
      NORMAL(null),
      SPLASH(Items.SPLASH_POTION),
      LINGERING(Items.LINGERING_POTION);

      @Nullable
      final Item bottleItem;

      BottleType(@Nullable Item bottleItem) {
         this.bottleItem = bottleItem;
      }
   }
}
