package io.github.manasmods.tensura.menu.container;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpatialStorageContainer extends SimpleContainer {
   private final int maxStackSize;

   public SpatialStorageContainer(int size, int maxStackSize) {
      super(size);
      this.maxStackSize = maxStackSize;
   }

   public boolean stillValid(Player pPlayer) {
      return pPlayer.isAlive();
   }

   public int getMaxStackSize() {
      return this.maxStackSize;
   }

   public int getMaxStackSize(ItemStack itemStack) {
      return itemStack.getMaxStackSize() == 1 ? 1 : this.getMaxStackSize();
   }

   public void setItem(int i, ItemStack itemStack) {
      this.items.set(i, itemStack);
      this.setChanged();
      if (itemStack.is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
         && (Boolean)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
         itemStack.setCount(0);
      }
   }

   public void fromTag(ListTag nbt, Provider provide) {
      for (int i = 0; i < nbt.size(); i++) {
         CompoundTag tag = nbt.getCompound(i);
         int slot = tag.getInt("Slot");
         if (slot < this.getContainerSize()) {
            Optional<ItemStack> stack = ItemStack.parse(provide, tag.get("Stack"));
            this.setItem(slot, stack.orElse(ItemStack.EMPTY));
         }
      }
   }

   @NotNull
   public ListTag createTag(Provider provider) {
      ListTag listtag = new ListTag();

      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack itemstack = this.getItem(i);
         if (!itemstack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Slot", i);
            tag.put("Stack", itemstack.save(provider, tag));
            listtag.add(tag);
         }
      }

      return listtag;
   }

   public boolean canAddItem(ItemStack pStack) {
      boolean flag = false;

      for (ItemStack itemstack : this.items) {
         if (itemstack.isEmpty()) {
            flag = true;
            break;
         }

         if (ItemStack.isSameItemSameComponents(itemstack, pStack)
            && (itemstack.getMaxStackSize() != 1 || itemstack.getCount() < 1)
            && itemstack.getCount() < this.getMaxStackSize()) {
            flag = true;
            break;
         }
      }

      return flag;
   }

   public void moveItemsBetweenStacks(ItemStack pStack, ItemStack pOther) {
      int i = pOther.getMaxStackSize() == 1 ? 1 : this.getMaxStackSize();
      int j = Math.min(pStack.getCount(), i - pOther.getCount());
      if (j > 0) {
         pOther.grow(j);
         pStack.shrink(j);
         this.setChanged();
      }
   }
}
