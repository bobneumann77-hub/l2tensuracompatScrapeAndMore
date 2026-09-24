package io.github.manasmods.tensura.item.weapon;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleScytheItem extends TwoHandedSwordItem {
   public SimpleScytheItem(Tier pTier, Properties properties) {
      super(pTier, 5, -3.2F, 2.0, 0.75, 50.0, 0.0, 4, -3.4F, 2.0, 0.5, 50.0, 0.0, properties);
   }

   public SimpleScytheItem(
      Tier pTier,
      int damage,
      float speed,
      double range,
      double sweepRatio,
      double critChance,
      double critMultiplier,
      int oneHandedDamage,
      float oneHandedSpeed,
      double oneHandedRange,
      double oneHandedSweepRatio,
      double oneHandedCritChance,
      double oneHandedCritMultiplier,
      Properties pProperties
   ) {
      super(
         pTier,
         damage,
         speed,
         range,
         sweepRatio,
         critChance,
         critMultiplier,
         oneHandedDamage,
         oneHandedSpeed,
         oneHandedRange,
         oneHandedSweepRatio,
         oneHandedCritChance,
         oneHandedCritMultiplier,
         pProperties
      );
   }
}
