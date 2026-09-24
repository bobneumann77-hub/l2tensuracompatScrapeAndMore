package io.github.manasmods.tensura.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SimpleLeggingsItem extends SimpleArmorItem {
   public SimpleLeggingsItem(Holder<ArmorMaterial> pMaterial, Properties properties, int durabilityMultiplier) {
      super(pMaterial, Type.LEGGINGS, properties, durabilityMultiplier);
   }
}
