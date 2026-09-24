package io.github.manasmods.tensura.compat;

import io.github.manasmods.tensura.recipe.MiningStationRecipe;
import io.github.manasmods.tensura.recipe.misc.RecipeResult;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import java.util.ArrayList;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class MiningStationRecipeCategory implements IRecipeCategory<MiningStationRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "mining_station");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/mining_station/jei_mining_station.png");
   public static final RecipeType<MiningStationRecipe> MINING_STATION_RECIPE_TYPE = new RecipeType(UID, MiningStationRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public MiningStationRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 78);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)TensuraBlocks.MINING_STATION.get()));
   }

   public RecipeType<MiningStationRecipe> getRecipeType() {
      return MINING_STATION_RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("block.tensura.mining_station");
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, MiningStationRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 80, 7).addIngredients(recipe.recipeIngredient().getIngredient());

      for (int i = 0; i < recipe.recipeResults().size() && i < 9; i++) {
         RecipeResult recipeResult = recipe.recipeResults().get(i);
         ItemStack stack = recipe.recipeResults().get(i).getItem();
         List<ItemStack> ingredient = new ArrayList<>();
         if (recipeResult.getMax() <= recipeResult.getMin()) {
            ingredient.add(stack.copyWithCount(recipeResult.getMin()));
         } else {
            for (int count = recipeResult.getMin(); count <= recipeResult.getMax(); count++) {
               ingredient.add(stack.copyWithCount(count));
            }
         }

         if (recipeResult.getChance() < 1.0F) {
            ItemStack empty = Items.BARRIER.getDefaultInstance();
            empty.set(DataComponents.ITEM_NAME, stack.getHoverName());
            ingredient.add(empty);
         }

         builder.addSlot(RecipeIngredientRole.OUTPUT, 8 + 18 * i, 55).addItemStacks(ingredient);
      }
   }

   @Generated
   public IDrawable getBackground() {
      return this.background;
   }
}
