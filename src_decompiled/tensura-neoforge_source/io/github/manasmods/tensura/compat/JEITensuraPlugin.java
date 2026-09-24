package io.github.manasmods.tensura.compat;

import io.github.manasmods.tensura.client.screen.KilnScreen;
import io.github.manasmods.tensura.client.screen.MiningStationScreen;
import io.github.manasmods.tensura.client.screen.RefiningScreen;
import io.github.manasmods.tensura.client.screen.RepeatCraftingScreen;
import io.github.manasmods.tensura.client.screen.ResearcherStorageScreen;
import io.github.manasmods.tensura.client.screen.SmithingBenchScreen;
import io.github.manasmods.tensura.client.screen.SynthesisSeparationScreen;
import io.github.manasmods.tensura.client.screen.UncraftingScreen;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.KilnMixingRecipe;
import io.github.manasmods.tensura.recipe.MiningStationRecipe;
import io.github.manasmods.tensura.recipe.RefiningRecipe;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.recipe.WoodcutterRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEITensuraPlugin implements IModPlugin {
   @NotNull
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "jei_plugin");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new KilnMeltingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new KilnMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new MiningStationRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new RefiningRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new SmithingBenchRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new WoodCuttingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
      List<KilnMeltingRecipe> meltingRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.KILN_MELTING_TYPE.get())
         .stream()
         .<KilnMeltingRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(KilnMeltingRecipeCategory.KILN_MELTING_RECIPE_TYPE, meltingRecipes);
      List<KilnMixingRecipe> mixingRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.KILN_MIXING_TYPE.get())
         .stream()
         .<KilnMixingRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE, mixingRecipes);
      List<MiningStationRecipe> miningStationRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.MINING_STATION_TYPE.get())
         .stream()
         .<MiningStationRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(MiningStationRecipeCategory.MINING_STATION_RECIPE_TYPE, miningStationRecipes);
      List<RefiningRecipe> refiningRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.REFINING_TYPE.get())
         .stream()
         .<RefiningRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(RefiningRecipeCategory.REFINING_RECIPE_TYPE, refiningRecipes);
      List<SmithingBenchRecipe> smithingBenchRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.SMITHING_BENCH_TYPE.get())
         .stream()
         .<SmithingBenchRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(SmithingBenchRecipeCategory.SMITHING_BENCH_RECIPE_TYPE, smithingBenchRecipes);
      List<WoodcutterRecipe> woodcuttingRecipes = recipeManager.getAllRecipesFor((RecipeType)TensuraRecipes.WOOD_CUTTER_TYPE.get())
         .stream()
         .<WoodcutterRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(WoodCuttingRecipeCategory.WOOD_CUTTING_RECIPE_TYPE, woodcuttingRecipes);
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addRecipeClickArea(
         KilnScreen.class, 204, 77, 13, 13, new mezz.jei.api.recipe.RecipeType[]{KilnMeltingRecipeCategory.KILN_MELTING_RECIPE_TYPE}
      );
      registration.addRecipeClickArea(KilnScreen.class, 54, 22, 11, 46, new mezz.jei.api.recipe.RecipeType[]{KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE});
      registration.addRecipeClickArea(KilnScreen.class, 111, 22, 11, 46, new mezz.jei.api.recipe.RecipeType[]{KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE});
      registration.addRecipeClickArea(
         MiningStationScreen.class, 58, 26, 60, 22, new mezz.jei.api.recipe.RecipeType[]{MiningStationRecipeCategory.MINING_STATION_RECIPE_TYPE}
      );
      registration.addRecipeClickArea(RefiningScreen.class, 197, 55, 14, 18, new mezz.jei.api.recipe.RecipeType[]{RefiningRecipeCategory.REFINING_RECIPE_TYPE});
      registration.addRecipeClickArea(
         SmithingBenchScreen.class, 107, 80, 18, 18, new mezz.jei.api.recipe.RecipeType[]{SmithingBenchRecipeCategory.SMITHING_BENCH_RECIPE_TYPE}
      );
      registration.addRecipeClickArea(RepeatCraftingScreen.class, 141, 53, 22, 15, new mezz.jei.api.recipe.RecipeType[]{RecipeTypes.CRAFTING});
      registration.addRecipeClickArea(ResearcherStorageScreen.class, 205, 91, 12, 11, new mezz.jei.api.recipe.RecipeType[]{RecipeTypes.SMELTING});
      registration.addRecipeClickArea(SynthesisSeparationScreen.class, 144, 31, 22, 15, new mezz.jei.api.recipe.RecipeType[]{RecipeTypes.ANVIL});
      registration.addRecipeClickArea(UncraftingScreen.class, 75, 67, 12, 11, new mezz.jei.api.recipe.RecipeType[]{RecipeTypes.CRAFTING});
      registration.addRecipeClickArea(UncraftingScreen.class, 123, 67, 12, 11, new mezz.jei.api.recipe.RecipeType[]{RecipeTypes.CRAFTING});
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN.get()), new mezz.jei.api.recipe.RecipeType[]{KilnMeltingRecipeCategory.KILN_MELTING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get()),
         new mezz.jei.api.recipe.RecipeType[]{KilnMeltingRecipeCategory.KILN_MELTING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN_ORICHALCUM.get()),
         new mezz.jei.api.recipe.RecipeType[]{KilnMeltingRecipeCategory.KILN_MELTING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN.get()), new mezz.jei.api.recipe.RecipeType[]{KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get()),
         new mezz.jei.api.recipe.RecipeType[]{KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.KILN_ORICHALCUM.get()),
         new mezz.jei.api.recipe.RecipeType[]{KilnMixingRecipeCategory.KILN_MIXING_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.MINING_STATION.get()),
         new mezz.jei.api.recipe.RecipeType[]{MiningStationRecipeCategory.MINING_STATION_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.SMITHING_BENCH.get()),
         new mezz.jei.api.recipe.RecipeType[]{SmithingBenchRecipeCategory.SMITHING_BENCH_RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)TensuraBlocks.Items.WOODCUTTER.get()),
         new mezz.jei.api.recipe.RecipeType[]{WoodCuttingRecipeCategory.WOOD_CUTTING_RECIPE_TYPE}
      );
   }
}
