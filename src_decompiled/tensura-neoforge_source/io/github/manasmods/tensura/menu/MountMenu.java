package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.util.MenuHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class MountMenu extends AbstractContainerMenu {
   private final Container container;
   private final TensuraMountEntity mount;
   private final int page;

   public MountMenu(int counter, Inventory playerInventory, Container container, TensuraMountEntity mount, int page) {
      this(
         counter,
         playerInventory,
         container,
         mount,
         page,
         mount.hasSaddleSlot() && mount.isSaddleRequired(),
         mount.hasArmorSlot(),
         mount.hasWeaponSlot(),
         mount.isChested() ? Math.max(0, Math.min(15, mount.getTotalChestSlots() - 15 * page)) : 0
      );
   }

   public MountMenu(
      int counter,
      Inventory playerInventory,
      Container container,
      TensuraMountEntity mount,
      int page,
      boolean saddle,
      boolean armor,
      boolean weapon,
      int chestSlots
   ) {
      super(null, counter);
      this.container = container;
      this.mount = mount;
      this.page = page;
      container.startOpen(playerInventory.player);
      int slotIndex = this.addMiscSlots(0, saddle, armor, weapon);
      this.addChestSlots(slotIndex, chestSlots);
      this.addPlayerSlots(playerInventory);
   }

   public boolean stillValid(Player player) {
      return !this.mount.hasInventoryChanged(this.container)
         && this.container.stillValid(player)
         && this.mount.isAlive()
         && this.mount.distanceTo(player) < 8.0F;
   }

   public int addMiscSlots(int slotIndex, boolean saddle, boolean armor, boolean weapon) {
      if (saddle) {
         this.addSlot(new Slot(this.container, slotIndex, 8, 17) {
            public boolean mayPlace(@NotNull ItemStack stack) {
               return MountMenu.this.mount.isMountSaddle(stack) && MountMenu.this.mount.isSaddleable();
            }

            public boolean mayPickup(Player player) {
               return TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.BINDING_CURSE, this.getItem()) <= 0;
            }
         });
         slotIndex++;
      }

      if (armor) {
         this.addSlot(new Slot(this.container, slotIndex, 8, 35) {
            public boolean mayPlace(ItemStack stack) {
               return MountMenu.this.mount.isMountArmor(stack);
            }

            public boolean mayPickup(Player player) {
               return TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.BINDING_CURSE, this.getItem()) <= 0;
            }
         });
         slotIndex++;
      }

      if (weapon) {
         this.addSlot(new Slot(this.container, slotIndex, 8, 53) {
            public boolean mayPlace(ItemStack stack) {
               return MountMenu.this.mount.isMountWeapon(stack);
            }
         });
         slotIndex++;
      }

      return slotIndex;
   }

   public void addChestSlots(int slotIndex, int chestSlots) {
      if (chestSlots > 0) {
         int size = chestSlots;
         int containerIndex = this.mount.getMiscSlots() + 15 * this.page;

         for (int i = 0; i < 3 && size > 0; i++) {
            for (int j = 0; j < 5 && size > 0; j++) {
               this.addSlot(new Slot(this.container, containerIndex, 81 + j * 18, 17 + i * 18));
               containerIndex++;
               size--;
            }
         }
      }
   }

   public void addPlayerSlots(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 9 + j * 18, 102 + i * 18 - 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 142));
      }
   }

   public boolean clickMenuButton(Player player, int i) {
      if (this.mount.isChested()) {
         if (i == 0 && this.page > 0) {
            MenuHelper.dropDraggingItem(this, player);
            this.mount.openMountInventory(player, this.page - 1);
            return true;
         }

         if (i == 1 && this.page < (this.mount.getTotalChestSlots() - 1) / 15) {
            MenuHelper.dropDraggingItem(this, player);
            this.mount.openMountInventory(player, this.page + 1);
            return true;
         }
      }

      return false;
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
         } else if (this.mount.hasWeaponSlot()
            && this.getSlot(this.mount.getWeaponSlotId()).mayPlace(stackInSlot)
            && !this.getSlot(this.mount.getWeaponSlotId()).hasItem()) {
            if (!this.moveItemStackTo(stackInSlot, this.mount.getWeaponSlotId(), this.mount.getWeaponSlotId() + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.mount.hasArmorSlot()
            && this.getSlot(this.mount.getArmorSlotId()).mayPlace(stackInSlot)
            && !this.getSlot(this.mount.getArmorSlotId()).hasItem()) {
            if (!this.moveItemStackTo(stackInSlot, this.mount.getArmorSlotId(), this.mount.getArmorSlotId() + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.mount.hasSaddleSlot() && this.mount.isSaddleRequired() && this.getSlot(0).mayPlace(stackInSlot) && !this.getSlot(0).hasItem()) {
            if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (size <= this.mount.getMiscSlots() || !this.moveItemStackTo(stackInSlot, this.mount.getMiscSlots(), size, false)) {
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
