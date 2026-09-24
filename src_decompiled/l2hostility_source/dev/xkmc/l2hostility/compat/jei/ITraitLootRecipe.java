package dev.xkmc.l2hostility.compat.jei;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ITraitLootRecipe {
   List<ItemStack> getResults();

   List<ItemStack> getCurioRequired();

   List<ItemStack> getInputs();

   void addTooltip(Consumer<Component> var1);

   default boolean isValid() {
      return true;
   }
}
