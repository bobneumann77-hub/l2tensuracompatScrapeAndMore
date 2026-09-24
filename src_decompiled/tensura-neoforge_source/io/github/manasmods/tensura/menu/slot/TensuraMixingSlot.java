package io.github.manasmods.tensura.menu.slot;

import io.github.manasmods.tensura.menu.KilnMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TensuraMixingSlot extends Slot {
   private final KilnMenu menu;

   public TensuraMixingSlot(Container container, int index, int x, int y, KilnMenu menu) {
      super(container, index, x, y);
      this.menu = menu;
   }

   public boolean mayPlace(ItemStack stack) {
      return false;
   }

   public void onTake(Player pPlayer, ItemStack pStack) {
      this.menu.kiln.performMixing(pPlayer.registryAccess());
   }
}
