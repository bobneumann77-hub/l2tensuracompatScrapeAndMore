package io.github.manasmods.tensura.menu.container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SimpleLimitedContainer extends SimpleContainer {
   private final int limitedSlots;

   public SimpleLimitedContainer(int size, int limited) {
      super(size);
      this.limitedSlots = limited;
   }

   public SimpleLimitedContainer(int limited, ItemStack... stacks) {
      super(stacks);
      this.limitedSlots = limited;
   }

   public ItemStack addItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      ItemStack stackCopy = stack.copy();
      this.moveItemToOccupiedSlotsWithSameType(stackCopy);
      if (stackCopy.isEmpty()) {
         return ItemStack.EMPTY;
      }

      this.moveItemToEmptySlots(stackCopy);
      return stackCopy.isEmpty() ? ItemStack.EMPTY : stackCopy;
   }

   private void moveItemToEmptySlots(ItemStack stack) {
      for (int i = this.limitedSlots; i < this.getContainerSize(); i++) {
         ItemStack itemInSlot = this.getItem(i);
         if (itemInSlot.isEmpty()) {
            this.setItem(i, stack.copyAndClear());
            return;
         }
      }
   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack stack) {
      for (int i = this.limitedSlots; i < this.getContainerSize(); i++) {
         ItemStack stackInSlot = this.getItem(i);
         if (ItemStack.isSameItemSameComponents(stackInSlot, stack)) {
            this.moveItemsBetweenStacks(stack, stackInSlot);
            if (stack.isEmpty()) {
               return;
            }
         }
      }
   }

   public void moveItemsBetweenStacks(ItemStack stackInFirstSlot, ItemStack stackInSecondSlot) {
      int i = Math.min(this.getMaxStackSize(), stackInSecondSlot.getMaxStackSize());
      int j = Math.min(stackInFirstSlot.getCount(), i - stackInSecondSlot.getCount());
      if (j > 0) {
         stackInSecondSlot.grow(j);
         stackInFirstSlot.shrink(j);
         this.setChanged();
      }
   }
}
