package io.github.manasmods.tensura.compat;

import io.github.manasmods.tensura.recipe.WoodcutterRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

public class WoodCuttingRecipeCategory extends AbstractRecipeCategory<WoodcutterRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "wood_cutting");
   public static final RecipeType<WoodcutterRecipe> WOOD_CUTTING_RECIPE_TYPE = new RecipeType(UID, WoodcutterRecipe.class);
   public static final int width = 82;
   public static final int height = 34;

   public WoodCuttingRecipeCategory(IGuiHelper guiHelper) {
      super(
         WOOD_CUTTING_RECIPE_TYPE,
         Component.translatable("tensura.jei.woodcutting.title"),
         guiHelper.createDrawableItemLike((ItemLike)TensuraBlocks.WOODCUTTER.get()),
         82,
         34
      );
   }

   public void setRecipe(IRecipeLayoutBuilder builder, WoodcutterRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 9).setStandardSlotBackground().addIngredients((Ingredient)recipe.getIngredients().getFirst());
      builder.addOutputSlot(61, 9).setOutputSlotBackground().addItemStack(getResultItem(recipe));
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, WoodcutterRecipe recipe, IFocusGroup focuses) {
      builder.addRecipeArrow().setPosition(26, 9);
   }

   public boolean isHandled(WoodcutterRecipe recipe) {
      return !recipe.isSpecial();
   }

   public ResourceLocation getRegistryName(RecipeHolder<WoodcutterRecipe> recipe) {
      return recipe.id();
   }

   public static ItemStack getResultItem(Recipe<?> recipe) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level == null) {
         throw new NullPointerException("level must not be null.");
      }

      RegistryAccess registryAccess = level.registryAccess();
      return recipe.getResultItem(registryAccess);
   }
}
