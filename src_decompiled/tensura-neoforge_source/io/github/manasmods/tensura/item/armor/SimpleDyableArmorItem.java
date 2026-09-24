package io.github.manasmods.tensura.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SimpleDyableArmorItem extends SimpleArmorItem {
   public SimpleDyableArmorItem(Holder<ArmorMaterial> pMaterial, Type type, Properties properties, int durabilityMultiplier) {
      super(pMaterial, type, properties, durabilityMultiplier);
   }
}
