package io.github.manasmods.tensura.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SimpleHelmetItem extends SimpleArmorItem {
   public SimpleHelmetItem(Holder<ArmorMaterial> pMaterial, Properties properties, int durabilityMultiplier) {
      super(pMaterial, Type.HELMET, properties, durabilityMultiplier);
   }
}
