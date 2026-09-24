package io.github.manasmods.tensura.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record MiningStationRecipeInput(ItemStack item) implements RecipeInput {
   @NotNull
   public ItemStack getItem(int i) {
      return this.item;
   }

   public int size() {
      return 1;
   }
}
