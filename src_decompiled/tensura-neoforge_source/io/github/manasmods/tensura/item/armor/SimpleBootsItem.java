package io.github.manasmods.tensura.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SimpleBootsItem extends SimpleArmorItem {
   public SimpleBootsItem(Holder<ArmorMaterial> pMaterial, Properties properties, int durabilityMultiplier) {
      super(pMaterial, Type.BOOTS, properties, durabilityMultiplier);
   }
}
