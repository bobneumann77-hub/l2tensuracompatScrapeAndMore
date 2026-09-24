package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.util.MenuHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class HumanoidMainMenu extends AbstractContainerMenu {
   private final Container container;
   private final TensuraHumanoidEntity humanoid;

   public HumanoidMainMenu(int counter, Inventory playerInventory, Container container, TensuraHumanoidEntity mount) {
      super(null, counter);
      this.container = container;
      this.humanoid = mount;
      container.startOpen(playerInventory.player);
      this.addMiscSlots();
      this.addPlayerSlots(playerInventory);
   }

   public boolean stillValid(Player player) {
      return !this.humanoid.hasInventoryChanged(this.container)
         && this.container.stillValid(player)
         && this.humanoid.isAlive()
         && this.humanoid.distanceTo(player) < 8.0F;
   }

   public void addMiscSlots() {
      for (final EquipmentSlot slot : this.humanoid.getAvailableSlots()) {
         int id = this.humanoid.getSlotId(slot);
         if (id != -1) {
            int y = switch (slot) {
               case CHEST -> 26;
               case LEGS, OFFHAND -> 44;
               case FEET, MAINHAND -> 62;
               default -> 8;
            };
            if (slot.isArmor()) {
               int x = 8;
               this.addSlot(new Slot(this.container, id, x, y) {
                  public boolean mayPlace(@NotNull ItemStack stack) {
                     return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == slot;
                  }

                  public boolean mayPickup(Player player) {
                     return TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.BINDING_CURSE, this.getItem()) <= 0;
                  }
               });
            } else {
               int x = 77;
               this.addSlot(new Slot(this.container, id, x, y) {
                  public boolean mayPlace(@NotNull ItemStack stack) {
                     return true;
                  }
               });
            }
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
      if (i == 1 && this.humanoid.getChestSlots() > 0) {
         MenuHelper.dropDraggingItem(this, player);
         this.humanoid.openSideInventory(player, 0);
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
