package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.SimpleScytheItem;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class BladeTigerScytheItem extends SimpleScytheItem {
   public BladeTigerScytheItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         7,
         -3.0F,
         2.0,
         0.75,
         20.0,
         0.0,
         6,
         -3.2F,
         2.0,
         0.5,
         50.0,
         0.0,
         new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(2000)
      );
   }

   public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
      return pRepair.is((Item)TensuraMobDropItems.BLADE_TIGER_TAIL.get());
   }
}
