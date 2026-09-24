package io.github.manasmods.tensura.menu.container;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiResultContainer implements Container, RecipeCraftingHolder {
   private final int maxStackSize;
   private final NonNullList<ItemStack> itemStacks;
   @Nullable
   private RecipeHolder<?> recipeUsed;

   public MultiResultContainer(int size) {
      this(size, 64);
   }

   public MultiResultContainer(int size, int maxStackSize) {
      this.maxStackSize = maxStackSize;
      this.itemStacks = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public int getMaxStackSize(ItemStack itemStack) {
      return this.getMaxStackSize();
   }

   public boolean isEmpty() {
      return this.itemStacks.stream().allMatch(ItemStack::isEmpty);
   }

   @NotNull
   public ItemStack getItem(int slot) {
      return (ItemStack)this.itemStacks.get(slot);
   }

   @NotNull
   public ItemStack removeItem(int slot, int count) {
      return ContainerHelper.takeItem(this.itemStacks, slot);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      return ContainerHelper.takeItem(this.itemStacks, slot);
   }

   public void setItem(int slot, ItemStack itemStack) {
      this.itemStacks.set(slot, itemStack);
   }

   public void setChanged() {
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return this.recipeUsed;
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> recipeUsed) {
      this.recipeUsed = recipeUsed;
   }

   public int getEmptySlot() {
      for (int slot = 0; slot < this.itemStacks.size(); slot++) {
         if (((ItemStack)this.itemStacks.get(slot)).isEmpty()) {
            return slot;
         }
      }

      return -1;
   }

   public boolean hasEnoughSpaceFor(ItemStack... item) {
      return this.hasEnoughSpaceFor(Arrays.stream(item).toList());
   }

   public boolean hasEnoughSpaceFor(List<ItemStack> items) {
      int maxStackSize = this.getMaxStackSize();
      List<ItemStack> pseudoInventory = this.itemStacks.stream().<ItemStack>map(ItemStack::copy).collect(Collectors.toList());

      for (ItemStack item : items) {
         int remaining = item.getCount();

         for (int index = 0; index < pseudoInventory.size(); index++) {
            ItemStack stack = pseudoInventory.get(index);
            if (stack.isEmpty()) {
               if (item.item == null || item.item == Items.AIR) {
                  remaining = 0;
                  break;
               }

               int insert = Math.min(maxStackSize, remaining);
               remaining -= insert;
               stack = new ItemStack(item.item, insert, item.components.copy());
               stack.setPopTime(item.getPopTime());
               pseudoInventory.set(index, stack);
               if (remaining == 0) {
                  break;
               }
            } else if (ItemStack.isSameItemSameComponents(item, stack)) {
               int insert = Math.min(maxStackSize - stack.getCount(), remaining);
               remaining -= insert;
               stack.grow(insert);
               if (remaining == 0) {
                  break;
               }
            }
         }

         if (remaining > 0) {
            return false;
         }
      }

      return true;
   }

   public boolean setItem(ItemStack item) {
      int slot = this.getEmptySlot();
      if (slot != -1) {
         this.setItem(slot, item);
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   public boolean addItem(ItemStack item) {
      for (ItemStack itemStack : this.itemStacks) {
         if (ItemStack.isSameItemSameComponents(itemStack, item)) {
            int count1 = itemStack.getCount();
            if (count1 != this.maxStackSize) {
               int count2 = item.getCount();
               int insert = Math.min(count2, this.maxStackSize - count1);
               item.shrink(insert);
               itemStack.grow(insert);
               this.setChanged();
               if (item.getCount() == 0) {
                  break;
               }
            }
         }
      }

      return item.getCount() == 0;
   }

   public boolean addOrSetItem(ItemStack item) {
      return this.addItem(item) || this.setItem(item);
   }

   public boolean addOrSetItems(NonNullList<ItemStack> items) {
      return this.addOrSetItems(items, true);
   }

   public boolean addOrSetItems(NonNullList<ItemStack> items, boolean ensureEnoughSpace) {
      if (ensureEnoughSpace && !this.hasEnoughSpaceFor(items)) {
         return false;
      }

      items.forEach(this::addOrSetItem);
      return true;
   }

   public void setItems(NonNullList<ItemStack> items) {
      for (int index = 0; index < items.size(); index++) {
         this.itemStacks.set(index, (ItemStack)items.get(index));
      }
   }

   @Generated
   public int getMaxStackSize() {
      return this.maxStackSize;
   }

   @Generated
   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }
}
