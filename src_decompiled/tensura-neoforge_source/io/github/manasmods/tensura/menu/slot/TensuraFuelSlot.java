package io.github.manasmods.tensura.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class TensuraFuelSlot extends Slot {
   public TensuraFuelSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
   }

   public boolean mayPlace(ItemStack stack) {
      return AbstractFurnaceBlockEntity.isFuel(stack) || isBucket(stack);
   }

   public int getMaxStackSize(ItemStack pStack) {
      return isBucket(pStack) ? 1 : super.getMaxStackSize(pStack);
   }

   public static boolean isBucket(ItemStack stack) {
      return stack.is(Items.BUCKET);
   }
}
