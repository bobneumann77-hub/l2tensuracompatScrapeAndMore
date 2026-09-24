package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.block.entity.MiningStationBlockEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class MiningStationMenu extends AbstractContainerMenu {
   public final MiningStationBlockEntity miningStation;
   private final ContainerLevelAccess access;

   public MiningStationMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
      this(pContainerId, inventory, (MiningStationBlockEntity)inventory.player.level().getBlockEntity(buf.readBlockPos()));
   }

   public MiningStationMenu(int pContainerId, Inventory inventory, MiningStationBlockEntity blockEntity) {
      super((MenuType)TensuraMenuTypes.MINING_STATION_MENU.get(), pContainerId);
      this.miningStation = blockEntity;
      this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
      this.addSlot(new Slot(blockEntity, 0, 80, 7));

      for (int i = 1; i < 10; i++) {
         this.addSlot(new Slot(blockEntity, i, 8 + 18 * (i - 1), 55) {
            public boolean mayPlace(ItemStack itemStack) {
               return false;
            }
         });
      }

      this.addPlayerInventory(inventory);
      this.addPlayerHotbar(inventory);
      blockEntity.startOpen(inventory.player);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 87 + row * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int column = 0; column < 9; column++) {
         this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 145));
      }
   }

   @NotNull
   public ItemStack quickMoveStack(Player player, int slotIndex) {
      ItemStack copyStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(slotIndex);
      if (!slot.hasItem()) {
         return copyStack;
      }

      ItemStack slotStack = slot.getItem();
      copyStack = slotStack.copy();
      if (slotIndex == 0 && !this.moveItemStackTo(slotStack, 10, 46, true)) {
         return ItemStack.EMPTY;
      }

      if (slotIndex > 0 && slotIndex < 10 && !this.moveItemStackTo(slotStack, 10, 46, true)) {
         return ItemStack.EMPTY;
      }

      if (slotIndex > 9 && !this.moveItemStackTo(slotStack, 0, 1, false)) {
         return ItemStack.EMPTY;
      }

      if (slotStack.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      if (slotStack.getCount() == copyStack.getCount()) {
         return ItemStack.EMPTY;
      }

      slot.onTake(player, slotStack);
      return copyStack;
   }

   public boolean stillValid(Player player) {
      return AbstractContainerMenu.stillValid(this.access, player, (Block)TensuraBlocks.MINING_STATION.get());
   }

   public void removed(Player player) {
      super.removed(player);
      this.access.execute((level, pos) -> this.miningStation.stopOpen(player));
   }

   @Generated
   public MiningStationBlockEntity getMiningStation() {
      return this.miningStation;
   }
}
