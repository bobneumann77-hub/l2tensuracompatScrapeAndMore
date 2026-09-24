package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class TempestScaleKnifeItem extends TensuraSwordItem {
   public TempestScaleKnifeItem(Properties pProperties) {
      super(TensuraToolTiers.PURE_MAGISTEEL, 1, -2.2F, -0.5, -1.0, 0.0, 0.2, pProperties);
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return TensuraMobDropItems.CHARYBDIS_SCALE.get() == repair.getItem() || super.isValidRepairItem(toRepair, repair);
   }
}
