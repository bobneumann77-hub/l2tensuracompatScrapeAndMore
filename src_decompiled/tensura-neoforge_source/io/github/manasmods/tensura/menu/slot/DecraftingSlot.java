package io.github.manasmods.tensura.menu.slot;

import io.github.manasmods.tensura.menu.UncraftingMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DecraftingSlot extends Slot {
   private final UncraftingMenu menu;
   private final Player player;
   private int removeCount;

   public DecraftingSlot(Player pPlayer, UncraftingMenu menu, Container pContainer, int pSlot, int xPosition, int yPosition) {
      super(pContainer, pSlot, xPosition, yPosition);
      this.player = pPlayer;
      this.menu = menu;
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return false;
   }

   public void onTake(Player pPlayer, ItemStack pStack) {
      this.checkTakeAchievements(pStack);
      this.menu.craftResultSlots.removeItem(0, 1);
      this.menu.craftResultSlots.setChanged();
      if (!this.player.getInventory().add(this.getItem())) {
         this.player.drop(this.getItem(), false);
      }

      if (this.menu.decraftSlots.isEmpty()) {
         this.menu.craftSlots.setCanPlace(true);
      }
   }

   @NotNull
   public ItemStack remove(int pAmount) {
      if (this.hasItem()) {
         this.removeCount = this.removeCount + Math.min(pAmount, this.getItem().getCount());
      }

      return super.remove(pAmount);
   }

   protected void onQuickCraft(ItemStack pStack, int pAmount) {
      this.removeCount += pAmount;
      this.checkTakeAchievements(pStack);
   }

   protected void onSwapCraft(int pNumItemsCrafted) {
      this.removeCount += pNumItemsCrafted;
   }

   protected void checkTakeAchievements(ItemStack pStack) {
      if (this.removeCount > 0) {
         pStack.onCraftedBy(this.player.level(), this.player, this.removeCount);
      }

      this.removeCount = 0;
   }
}
