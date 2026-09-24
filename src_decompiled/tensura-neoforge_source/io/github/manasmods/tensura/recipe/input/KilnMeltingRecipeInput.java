package io.github.manasmods.tensura.recipe.input;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record KilnMeltingRecipeInput(
   ItemStack item, Optional<ResourceLocation> left, Optional<ResourceLocation> right, int moltenAmount, int magicAmount, int maximumMolten
) implements RecipeInput {
   public ItemStack getItem(int i) {
      return this.item;
   }

   public int size() {
      return 1;
   }

   public boolean isEmpty() {
      return this.item.isEmpty();
   }
}
