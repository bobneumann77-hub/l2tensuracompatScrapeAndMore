package io.github.manasmods.tensura.recipe.input;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record SmithingBenchRecipeInput(Container container, List<ResourceLocation> learntSchematics, boolean creative) implements RecipeInput {
   @NotNull
   public ItemStack getItem(int i) {
      return i >= 0 && i < this.container.getContainerSize() ? this.container.getItem(i) : ItemStack.EMPTY;
   }

   public int size() {
      return 5;
   }
}
