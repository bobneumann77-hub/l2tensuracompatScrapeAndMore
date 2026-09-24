package io.github.manasmods.tensura.recipe.input;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record RefiningRecipeInput(Container inputContainer) implements RecipeInput {
   @NotNull
   public ItemStack getItem(int i) {
      return i >= 0 && i < this.inputContainer.getContainerSize() ? this.inputContainer.getItem(i) : ItemStack.EMPTY;
   }

   public int size() {
      return this.inputContainer.getContainerSize();
   }
}
