package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.block.KilnBlock;
import io.github.manasmods.tensura.block.entity.KilnBlockEntity;
import io.github.manasmods.tensura.menu.slot.TensuraFuelSlot;
import io.github.manasmods.tensura.menu.slot.TensuraMixingSlot;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.input.KilnMeltingRecipeInput;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KilnMenu extends RecipeBookMenu<KilnMeltingRecipeInput, KilnMeltingRecipe> {
   @Generated
   private static final Logger log = LogManager.getLogger(KilnMenu.class);
   public final KilnBlockEntity kiln;
   public final Level level;

   public KilnMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
      this(id, inv, (KilnBlockEntity)inv.player.level().getBlockEntity(extraData.readBlockPos()));
   }

   public KilnMenu(int id, Inventory inv, KilnBlockEntity container) {
      super((MenuType)TensuraMenuTypes.KILN.get(), id);
      checkContainerSize(container, 3);
      this.kiln = container;
      this.level = inv.player.level();
      this.addSlot(new Slot(container, 1, 203, 52));
      this.addSlot(new TensuraFuelSlot(container, 0, 203, 98));
      this.addSlot(new TensuraMixingSlot(container, 2, 80, 36, this));
      this.addPlayerInventory(inv);
      this.addPlayerHotbar(inv);
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      if (this.kiln instanceof StackedContentsCompatible compatible) {
         compatible.fillStackedContents(stackedContents);
      }
   }

   public void clearCraftingContent() {
      this.getSlot(0).set(ItemStack.EMPTY);
   }

   public boolean recipeMatches(RecipeHolder<KilnMeltingRecipe> recipeHolder) {
      KilnMeltingRecipeInput recipeInput = new KilnMeltingRecipeInput(
         this.kiln.getItem(0),
         this.kiln.getLeftBarId(),
         this.kiln.getRightBarId(),
         this.kiln.getMoltenAmount(),
         this.kiln.getMagicMaterialAmount(),
         this.kiln.getMaxMoltenAmount()
      );
      return ((KilnMeltingRecipe)recipeHolder.value()).matches(recipeInput, this.level);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return 1;
   }

   public int getGridHeight() {
      return 1;
   }

   public int getSize() {
      return 2;
   }

   public RecipeBookType getRecipeBookType() {
      return null;
   }

   public boolean shouldMoveToInventory(int i) {
      return i != 2;
   }

   public boolean isSmelting() {
      return this.kiln.getMeltingProgress() > 0;
   }

   public boolean hasFuel() {
      return this.kiln.getFuelTime() > 0;
   }

   public int getMoltenProgress() {
      int progress = this.kiln.getMoltenAmount();
      int progressArrowSize = 74;
      return progress != 0 ? progress * progressArrowSize / this.kiln.getMaxMoltenAmount() : 0;
   }

   public int getMagisteelProgress() {
      int progress = this.kiln.getMagicMaterialAmount();
      int progressArrowSize = 74;
      return progress != 0 ? progress * progressArrowSize / this.kiln.getMaxMoltenAmount() : 0;
   }

   public int getScaledProgress() {
      int maxProgress = this.kiln.getMaxMeltingProgress();
      int progress = Math.min(this.kiln.getMeltingProgress(), maxProgress);
      int progressArrowSize = 25;
      return progress != 0 ? progress * progressArrowSize / maxProgress : 0;
   }

   public int getScaledFuelProgress() {
      int fuelProgress = this.kiln.getFuelTime();
      int maxFuelProgress = this.kiln.getMaxFuelTime();
      int fuelProgressSize = 13;
      return maxFuelProgress != 0 ? (int)((float)fuelProgress / maxFuelProgress * fuelProgressSize) : 0;
   }

   public boolean stillValid(Player player) {
      return (Boolean)ContainerLevelAccess.create(this.level, this.kiln.getBlockPos())
         .evaluate((level, blockPos) -> level.getBlockState(blockPos).getBlock() instanceof KilnBlock && player.canInteractWithBlock(blockPos, 4.0), true);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
      }
   }

   public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
      return pSlot.index == 2 ? false : super.canTakeItemForPickAll(pStack, pSlot);
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == 0) {
         if (this.kiln.hasNextMixingRecipe()) {
            this.kiln.mixingNextRecipe();
            return true;
         }
      } else if (i == 1 && this.kiln.hasPrevMixingRecipe()) {
         this.kiln.mixingPrevRecipe();
         return true;
      }

      return false;
   }

   public ItemStack quickMoveStack(Player player, int i) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         itemStack = itemStack2.copy();
         if (i == 2) {
            if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (i >= 3 && i < 40) {
            if (!this.moveItemStackTo(itemStack2, 0, 2, false)) {
               if (i < 31) {
                  if (!this.moveItemStackTo(itemStack2, 31, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(itemStack2, 3, 31, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(itemStack2, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemStack2);
      }

      return itemStack;
   }
}
