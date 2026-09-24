package io.github.manasmods.tensura.compat;

import io.github.manasmods.tensura.recipe.RefiningRecipe;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class RefiningRecipeCategory implements IRecipeCategory<RefiningRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "refining");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/refining/jei_refining.png");
   public static final RecipeType<RefiningRecipe> REFINING_RECIPE_TYPE = new RecipeType(UID, RefiningRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public RefiningRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 249, 80);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)TensuraConsumableItems.FULL_POTION.get()));
   }

   public RecipeType<RefiningRecipe> getRecipeType() {
      return REFINING_RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("tooltip.tensura.great_sage_menu.refining");
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RefiningRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 219, 32).addItemStack(recipe.getOutput());
      builder.addSlot(RecipeIngredientRole.INPUT, 10, 32).addItemStack(recipe.getInput());
      List<ItemStack> ingredient1 = new ArrayList<>(Arrays.stream(((Ingredient)recipe.getIngredients().get(0)).getItems()).toList());
      builder.addSlot(RecipeIngredientRole.INPUT, 41, 32).addItemStacks(ingredient1);
      List<ItemStack> ingredient2 = new ArrayList<>(Arrays.stream(((Ingredient)recipe.getIngredients().get(1)).getItems()).toList());
      builder.addSlot(RecipeIngredientRole.INPUT, 69, 32).addItemStacks(ingredient2);
      List<ItemStack> ingredient3 = new ArrayList<>(Arrays.stream(((Ingredient)recipe.getIngredients().get(2)).getItems()).toList());
      builder.addSlot(RecipeIngredientRole.INPUT, 97, 32).addItemStacks(ingredient3);
      List<ItemStack> ingredient4 = new ArrayList<>(Arrays.stream(((Ingredient)recipe.getIngredients().get(3)).getItems()).toList());
      builder.addSlot(RecipeIngredientRole.INPUT, 125, 32).addItemStacks(ingredient4);
      List<ItemStack> ingredient5 = new ArrayList<>(Arrays.stream(((Ingredient)recipe.getIngredients().get(4)).getItems()).toList());
      builder.addSlot(RecipeIngredientRole.INPUT, 153, 32).addItemStacks(ingredient5);
   }

   @Generated
   public IDrawable getBackground() {
      return this.background;
   }
}
