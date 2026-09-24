package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.util.MenuHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HumanoidInventoryMenu extends AbstractContainerMenu {
   private final Container container;
   private final TensuraHumanoidEntity humanoid;
   private final int page;

   public HumanoidInventoryMenu(int counter, Inventory playerInventory, Container container, TensuraHumanoidEntity mount, int page) {
      this(counter, playerInventory, container, mount, page, Math.max(0, Math.min(36, mount.getChestSlots() - 36 * page)));
   }

   public HumanoidInventoryMenu(int counter, Inventory playerInventory, Container container, TensuraHumanoidEntity mount, int page, int chestSlots) {
      super(null, counter);
      this.container = container;
      this.humanoid = mount;
      this.page = page;
      container.startOpen(playerInventory.player);
      this.addChestSlots(chestSlots);
      this.addPlayerSlots(playerInventory);
   }

   public boolean stillValid(Player player) {
      return !this.humanoid.hasInventoryChanged(this.container)
         && this.container.stillValid(player)
         && this.humanoid.isAlive()
         && this.humanoid.distanceTo(player) < 8.0F;
   }

   public void addChestSlots(int chestSlots) {
      int containerIndex = this.humanoid.getMiscSlots() + 36 * this.page;
      int size = chestSlots;

      for (int i = 0; i < 4 && size > 0; i++) {
         for (int j = 0; j < 9 && size > 0; j++) {
            this.addSlot(new Slot(this.container, containerIndex, 8 + j * 18, 8 + i * 18));
            containerIndex++;
            size--;
         }
      }
   }

   public void addPlayerSlots(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 111 + i * 18 - 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 151));
      }
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == 0) {
         MenuHelper.dropDraggingItem(this, player);
         if (this.page > 0) {
            this.humanoid.openSideInventory(player, this.page - 1);
         } else {
            this.humanoid.openMainInventory(player);
         }

         return true;
      } else if (i == 1 && this.page < (this.humanoid.getChestSlots() - 1) / 36) {
         MenuHelper.dropDraggingItem(this, player);
         this.humanoid.openSideInventory(player, this.page + 1);
         return true;
      } else {
         return false;
      }
   }

   public ItemStack quickMoveStack(Player player, int slotNumber) {
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(slotNumber);
      if (slot.hasItem()) {
         ItemStack stackInSlot = slot.getItem();
         stack = stackInSlot.copy();
         int size = this.slots.size() - 36;
         if (slotNumber < size) {
            if (!this.moveItemStackTo(stackInSlot, size, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stackInSlot, 0, size, false)) {
            int i = size + 27;
            int i1 = i + 9;
            if (slotNumber >= i && slotNumber < i1) {
               if (!this.moveItemStackTo(stackInSlot, size, i, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (slotNumber < i) {
               if (!this.moveItemStackTo(stackInSlot, i, i1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(stackInSlot, i, i, false)) {
               return ItemStack.EMPTY;
            }

            return ItemStack.EMPTY;
         }

         if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return stack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);
   }
}
