package io.github.manasmods.tensura.menu.slot;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.menu.UncraftingMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingInput.Positioned;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

public class DegenerateSlot extends Slot {
   private final UncraftingMenu menu;
   private final Player player;
   private int removeCount;

   public DegenerateSlot(Player pPlayer, UncraftingMenu menu, Container pContainer, int pSlot, int xPosition, int yPosition) {
      super(pContainer, pSlot, xPosition, yPosition);
      this.player = pPlayer;
      this.menu = menu;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return !this.menu.craftSlots.isEmpty()
         ? false
         : this.menu.decraftSlots.isEmpty() && !EnchantmentHelper.hasTag(stack, TensuraTags.Enchantments.SEALING_CURSE);
   }

   public void set(ItemStack pStack) {
      super.set(pStack);
      if (this.menu.craftSlots.isEmpty()) {
         this.menu.craftSlots.setCanPlace(false);
         this.menu.decraftSlots.updateSlots(pStack, this.player.level());
      }
   }

   public void onTake(Player player, ItemStack itemStack) {
      if (!this.menu.decraftSlots.isEmpty()) {
         this.menu.decraftSlots.clearContent();
         this.menu.craftSlots.setCanPlace(true);
      } else {
         this.checkTakeAchievements(itemStack);
         Positioned positioned = this.menu.craftSlots.asPositionedCraftInput();
         CraftingInput craftingInput = positioned.input();
         int i = positioned.left();
         int j = positioned.top();
         NonNullList<ItemStack> nonNullList = player.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, craftingInput, player.level());

         for (int k = 0; k < craftingInput.height(); k++) {
            for (int l = 0; l < craftingInput.width(); l++) {
               int m = l + i + (k + j) * this.menu.craftSlots.getWidth();
               ItemStack itemStack2 = this.menu.craftSlots.getItem(m);
               ItemStack itemStack3 = (ItemStack)nonNullList.get(l + k * craftingInput.width());
               if (!itemStack2.isEmpty()) {
                  this.menu.craftSlots.removeItem(m, 1);
                  itemStack2 = this.menu.craftSlots.getItem(m);
               }

               if (!itemStack3.isEmpty()) {
                  if (itemStack2.isEmpty()) {
                     this.menu.craftSlots.setItem(m, itemStack3);
                  } else if (ItemStack.isSameItemSameComponents(itemStack2, itemStack3)) {
                     itemStack3.grow(itemStack2.getCount());
                     this.menu.craftSlots.setItem(m, itemStack3);
                  } else if (!this.player.getInventory().add(itemStack3)) {
                     this.player.drop(itemStack3, false);
                  }
               }
            }
         }
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
