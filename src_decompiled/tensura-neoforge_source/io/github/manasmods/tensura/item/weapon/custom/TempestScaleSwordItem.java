package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TwoHandedSwordItem;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class TempestScaleSwordItem extends TwoHandedSwordItem {
   public TempestScaleSwordItem(Properties pProperties) {
      super(TensuraToolTiers.PURE_MAGISTEEL, 3, -2.6F, 1.0, 0.25, 0.0, 0.0, 2, -2.8F, 1.0, 0.0, 0.0, 0.0, pProperties);
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return TensuraMobDropItems.CHARYBDIS_SCALE.get() == repair.getItem() || super.isValidRepairItem(toRepair, repair);
   }
}
