package io.github.manasmods.tensura.item.tool;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class SimplePickaxeItem extends PickaxeItem {
   public SimplePickaxeItem(Tier pTier, Properties properties) {
      this(pTier, 1, -2.8F, properties);
   }

   public SimplePickaxeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties properties) {
      this(pTier, properties, PickaxeItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier));
   }

   public SimplePickaxeItem(Tier pTier, Properties properties, ItemAttributeModifiers modifiers) {
      super(pTier, properties.attributes(modifiers));
   }
}
