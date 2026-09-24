package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.menu.slot.LavaStorageInputSlot;
import io.github.manasmods.tensura.menu.slot.SpatialSlot;
import io.github.manasmods.tensura.menu.slot.WaterStorageInputSlot;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SpatialStorageMenu extends AbstractContainerMenu {
   @Generated
   private static final Logger log = LogManager.getLogger(SpatialStorageMenu.class);
   private final ManasSkill skill;
   private final Player player;
   private final LivingEntity storageOwner;
   private final int page;
   private final SimpleContainer container;
   private final Container liquidInputSlots = new SimpleContainer(2);
   public final ResultContainer waterStorageOutput = new ResultContainer();
   public final ResultContainer lavaStorageOutput = new ResultContainer();

   public SpatialStorageMenu(int id, Inventory inv, LivingEntity storageOwner, SimpleContainer container, ManasSkill skill, int page) {
      super(null, id);
      this.skill = skill;
      this.player = inv.player;
      this.storageOwner = storageOwner;
      this.page = page;
      this.container = container;
      container.startOpen(inv.player);
      this.addPlayerInventory(inv);
      this.addPlayerHotBar(inv);
      this.addLiquidStorageSlots();
      this.addSpatialSlots(container);
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   private void addPlayerInventory(Inventory inventory) {
      for (int row = 0; row < 3; row++) {
         for (int slot = 0; slot < 9; slot++) {
            this.addSlot(new Slot(inventory, slot + row * 9 + 9, 37 + slot * 18, 102 + row * 18));
         }
      }
   }

   private void addPlayerHotBar(Inventory inventory) {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(inventory, i, 37 + i * 18, 160));
      }
   }

   private void addSpatialSlots(SimpleContainer container) {
      int slotIndex = 27 * this.page;
      int size = container.getContainerSize() - 27 * this.page;

      for (int i = 0; i < 3 && size > 0; i++) {
         for (int j = 0; j < 9 && size > 0; j++) {
            this.addSlot(new SpatialSlot(container, slotIndex, 37 + j * 18, 25 + i * 18));
            slotIndex++;
            size--;
         }
      }
   }

   private void addLiquidStorageSlots() {
      this.addSlot(new Slot(this.waterStorageOutput, 0, 7, 161) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }
      });
      this.addSlot(new Slot(this.lavaStorageOutput, 0, 211, 161) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }
      });
      this.addSlot(new WaterStorageInputSlot(this, this.liquidInputSlots, 7, 7));
      this.addSlot(new LavaStorageInputSlot(this, this.liquidInputSlots, 211, 7));
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == 0 && this.page > 0) {
         ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
         if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               spatialStorage.openSpatialStoragePage(serverPlayer, this.getStorageOwner(), instance, this.page - 1);
            }
         }

         return true;
      } else if (i == 1 && this.page < (this.container.getContainerSize() - 1) / 27) {
         ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
         if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               spatialStorage.openSpatialStoragePage(serverPlayer, this.getStorageOwner(), instance, this.page + 1);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private ManasSkillInstance getSkillInstance(LivingEntity owner) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(owner).getSkill(this.skill);
      return optional.orElse(null);
   }

   public void removed(Player pPlayer) {
      ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
      if (instance != null
         && this.container instanceof SpatialStorageContainer storageContainer
         && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
         spatialStorage.saveContainer(instance, this.getStorageOwner(), storageContainer);
      }

      this.container.stopOpen(pPlayer);
      if (this.getStorageOwner().equals(this.getPlayer())) {
         this.clearContainer(pPlayer, this.liquidInputSlots);
         this.clearContainer(pPlayer, this.waterStorageOutput);
         this.clearContainer(pPlayer, this.lavaStorageOutput);
      }

      super.removed(pPlayer);
   }

   public int getSpatialSize() {
      return Math.min(this.container.getContainerSize() - this.page * 27, 27);
   }

   public ItemStack quickMoveStack(Player pPlayer, int i) {
      ItemStack copy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (!slot.hasItem()) {
         return copy;
      }

      ItemStack stack = slot.getItem();
      copy = stack.copy();
      if (i >= 0 && i < 36) {
         if (i < 27) {
            if (!this.moveItemStackTo(stack, 40, 40 + this.getSpatialSize(), false) && !this.moveItemStackTo(stack, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stack, 40, 40 + this.getSpatialSize(), false) && !this.moveItemStackTo(stack, 0, 27, false)) {
            return ItemStack.EMPTY;
         }
      } else if (i >= 40 && i <= 40 + this.getSpatialSize()) {
         if (!this.moveItemStackTo(stack, 0, 36, false)) {
            return ItemStack.EMPTY;
         }
      } else if (!this.moveItemStackTo(stack, 0, 27, false) && !this.moveItemStackTo(stack, 40, 40 + this.getSpatialSize(), false)) {
         return ItemStack.EMPTY;
      }

      if (stack.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      if (stack.getCount() == copy.getCount()) {
         return ItemStack.EMPTY;
      }

      slot.onTake(this.player, stack);
      return copy;
   }

   protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
      boolean flag = false;
      int i = pStartIndex;
      if (pReverseDirection) {
         i = pEndIndex - 1;
      }

      if (pStack.isStackable()) {
         while (!pStack.isEmpty() && (pReverseDirection ? i >= pStartIndex : i < pEndIndex)) {
            Slot slot = (Slot)this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && slot.mayPlace(pStack) && ItemStack.isSameItemSameComponents(pStack, itemstack)) {
               int j = itemstack.getCount() + pStack.getCount();
               int maxSize = i >= 40 ? this.container.getMaxStackSize(pStack) : Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize());
               if (j <= maxSize) {
                  pStack.setCount(0);
                  itemstack.setCount(j);
                  slot.setChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  pStack.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot.setChanged();
                  flag = true;
               }
            }

            if (pReverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      if (!pStack.isEmpty()) {
         if (pReverseDirection) {
            i = pEndIndex - 1;
         } else {
            i = pStartIndex;
         }

         while (pReverseDirection ? i >= pStartIndex : i < pEndIndex) {
            Slot pSlot = (Slot)this.slots.get(i);
            ItemStack stack = pSlot.getItem();
            if (stack.isEmpty() && pSlot.mayPlace(pStack)) {
               if (pStack.getCount() > pSlot.getMaxStackSize()) {
                  pSlot.set(pStack.split(pSlot.getMaxStackSize()));
               } else {
                  pSlot.set(pStack.split(pStack.getCount()));
               }

               pSlot.setChanged();
               flag = true;
               break;
            }

            if (pReverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      return flag;
   }

   @Generated
   public ManasSkill getSkill() {
      return this.skill;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public LivingEntity getStorageOwner() {
      return this.storageOwner;
   }
}
