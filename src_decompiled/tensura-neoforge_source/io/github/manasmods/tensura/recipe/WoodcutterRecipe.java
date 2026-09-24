package io.github.manasmods.tensura.recipe;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WoodcutterRecipe extends SingleItemRecipe {
   public WoodcutterRecipe(String string, Ingredient ingredient, ItemStack itemStack) {
      super((RecipeType)TensuraRecipes.WOOD_CUTTER_TYPE.get(), (RecipeSerializer)TensuraRecipes.WOOD_CUTTER_SERIALIZER.get(), string, ingredient, itemStack);
   }

   public boolean matches(SingleRecipeInput singleRecipeInput, Level level) {
      return this.ingredient.test(singleRecipeInput.item());
   }

   @NotNull
   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)TensuraBlocks.WOODCUTTER.get());
   }

   public static class Type implements RecipeType<WoodcutterRecipe> {
   }
}
