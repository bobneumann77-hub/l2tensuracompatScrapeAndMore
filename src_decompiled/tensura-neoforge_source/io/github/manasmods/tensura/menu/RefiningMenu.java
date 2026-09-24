package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.ability.subclass.IRepeatCrafting;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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

public class RefiningMenu<S extends ManasSkill & IRefining<S>> extends AbstractContainerMenu {
   @Generated
   private static final Logger log = LogManager.getLogger(RefiningMenu.class);
   private final S skill;
   private final Player player;
   private final LivingEntity storageOwner;
   public final SpatialStorageContainer brewingContainer;
   public final ResultContainer resultContainer;

   public RefiningMenu(int id, Inventory inv, LivingEntity storageOwner, SimpleContainer container, S skill) {
      super(null, id);
      this.skill = skill;
      this.player = inv.player;
      this.storageOwner = storageOwner;
      this.resultContainer = new ResultContainer();
      this.brewingContainer = new SpatialStorageContainer(8, 128);
      this.addCraftingSlots();
      this.addPlayerInventory(inv);
      if (this.skill.isAutoRefiningAllowed()) {
         for (int i = 0; i < 8; i++) {
            this.brewingContainer.setItem(i, container.getItem(i + skill.getSpatialStorageIdOffset()));
         }

         this.resultContainer.setItem(0, container.getItem(skill.getSpatialStorageIdOffset() + 8));
      }
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
      return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
   }

   @Nullable
   public ManasSkillInstance getSkillInstance(LivingEntity owner) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(owner).getSkill(this.skill);
      return optional.orElse(null);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 45 + l * 18, 110 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 45 + i * 18, 168));
      }
   }

   private void addCraftingSlots() {
      this.addSlot(new Slot(this.brewingContainer, 0, 10, 38));
      this.addSlot(new Slot(this.brewingContainer, 1, 10, 56));
      this.addSlot(new Slot(this.brewingContainer, 2, 10, 74));
      this.addSlot(new Slot(this.brewingContainer, 3, 41, 56));
      this.addSlot(new Slot(this.brewingContainer, 4, 69, 56));
      this.addSlot(new Slot(this.brewingContainer, 5, 97, 56));
      this.addSlot(new Slot(this.brewingContainer, 6, 125, 56));
      this.addSlot(new Slot(this.brewingContainer, 7, 153, 56));
      this.addSlot(new Slot(this.resultContainer, 0, 219, 56) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }
      });
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      if (pId == 1 && this.skill.isAutoRefiningAllowed()) {
         ManasSkillInstance instance = this.getSkillInstance(pPlayer);
         if (instance != null) {
            CompoundTag tag = instance.getOrCreateTag();
            boolean repeating = tag.getBoolean("RepeatBrewing");
            if (!repeating && this.brewingContainer.isEmpty()) {
               return false;
            }

            tag.putBoolean("RepeatBrewing", !repeating);
            tag.putBoolean("Brewing", !repeating);
            instance.markDirty();
            SkillAPI.getSkillsFrom(pPlayer).markDirty();
         }

         return true;
      } else if (pId == 2) {
         ManasSkillInstance instance = this.getSkillInstance(pPlayer);
         if (instance != null) {
            CompoundTag tag = instance.getOrCreateTag();
            boolean brewing = tag.getBoolean("Brewing");
            if (!brewing && this.brewingContainer.isEmpty()) {
               return false;
            }

            tag.putBoolean("Brewing", !brewing);
            instance.markDirty();
            SkillAPI.getSkillsFrom(pPlayer).markDirty();
         }

         return true;
      } else {
         if (pId == 3 && this.skill.hasAutoCraftingTab() && this.skill instanceof IRepeatCrafting<?> repeatCrafting) {
            ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
            if (instance != null) {
               this.removed(this.player);
               if (pPlayer instanceof ServerPlayer serverPlayer) {
                  repeatCrafting.openRepeatCraftingMenu(serverPlayer, this.getStorageOwner(), instance);
               }

               return true;
            }
         }

         return false;
      }
   }

   public void removed(Player pPlayer) {
      if (this.skill.isAutoRefiningAllowed()) {
         ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
         if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
            for (int i = 0; i < 8; i++) {
               spatialStorage.setItemInSpatialStorage(
                  instance, this.getStorageOwner(), this.brewingContainer.getItem(i), i + this.skill.getSpatialStorageIdOffset()
               );
            }

            spatialStorage.setItemInSpatialStorage(
               instance, this.getStorageOwner(), this.resultContainer.getItem(0), this.skill.getSpatialStorageIdOffset() + 8
            );
         }
      } else {
         this.clearContainer(pPlayer, this.brewingContainer);
         this.clearContainer(pPlayer, this.resultContainer);
      }

      super.removed(pPlayer);
   }

   public ItemStack quickMoveStack(Player pPlayer, int i) {
      ItemStack copy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (!slot.hasItem()) {
         return copy;
      }

      ItemStack stack = slot.getItem();
      copy = stack.copy();
      if (i == 8) {
         stack.getItem().onCraftedBy(stack, this.player.level(), this.player);
         if (!this.moveItemStackTo(stack, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         slot.onQuickCraft(stack, copy);
      } else if (i >= 9 && i < 45) {
         if (!this.moveItemStackTo(stack, 0, 8, false)) {
            if (i < 36) {
               if (!this.moveItemStackTo(stack, 36, 45, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(stack, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         }
      } else if (!this.moveItemStackTo(stack, 9, 45, false)) {
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

   @Generated
   public S getSkill() {
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
