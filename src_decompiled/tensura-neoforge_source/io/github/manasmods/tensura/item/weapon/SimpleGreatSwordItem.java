package io.github.manasmods.tensura.item.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleGreatSwordItem extends TwoHandedSwordItem {
   private static final MutableComponent TOOLTIP = Component.translatable("tooltip.tensura.great_sword.tooltip");

   public SimpleGreatSwordItem(Tier pTier, Properties properties) {
      super(pTier, 6, -3.2F, 2.0, 0.5, 0.0, 0.0, (int)(-1.0F - pTier.getAttackDamageBonus()), -4.0F, 2.0, 0.0, 0.0, 0.0, properties);
   }

   @Override
   protected MutableComponent getTwoHandedTooltip() {
      return TOOLTIP;
   }
}
