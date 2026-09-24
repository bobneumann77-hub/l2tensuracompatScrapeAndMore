package io.github.manasmods.tensura.menu.container;

import lombok.Generated;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class TensuraCraftingContainer extends TransientCraftingContainer {
   private boolean canPlace = true;

   public TensuraCraftingContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight) {
      super(pMenu, pWidth, pHeight);
   }

   public void setItem(int pIndex, ItemStack pStack) {
      if (this.isCanPlace()) {
         super.setItem(pIndex, pStack);
      }
   }

   @Generated
   public boolean isCanPlace() {
      return this.canPlace;
   }

   @Generated
   public void setCanPlace(boolean canPlace) {
      this.canPlace = canPlace;
   }
}
