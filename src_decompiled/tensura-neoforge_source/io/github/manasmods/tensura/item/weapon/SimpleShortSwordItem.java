package io.github.manasmods.tensura.item.weapon;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleShortSwordItem extends TensuraSwordItem {
   public SimpleShortSwordItem(Tier pTier, Properties properties) {
      super(pTier, 1, -2.0F, -0.75, -1.0, 0.0, 0.5, properties);
   }

   public SimpleShortSwordItem(
      Tier pTier, int damage, float speed, double range, double sweepRatio, double critChance, double critMultiplier, Properties pProperties
   ) {
      super(pTier, damage, speed, range, sweepRatio, critChance, critMultiplier, pProperties);
   }
}
