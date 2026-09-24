package io.github.manasmods.tensura.item.weapon;

import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleSwordItem extends SwordItem {
   public SimpleSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties properties) {
      super(pTier, properties.attributes(SwordItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
   }

   public SimpleSwordItem(Tier pTier, SimpleSwordItem.SwordModifier swordModifier) {
      this(pTier, swordModifier.getAttackDamageModifier(), swordModifier.getAttackSpeedModifier(), swordModifier.getProperties());
   }

   public SimpleSwordItem(Tier pTier) {
      this(pTier, SimpleSwordItem.SwordModifier.NORMAL);
   }

   public enum SwordModifier {
      NORMAL(3, -2.4F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      FIRE_RESISTED(3, -2.4F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant());

      private final int attackDamageModifier;
      private final float attackSpeedModifier;
      private final Properties properties;

      @Generated
      SwordModifier(final int attackDamageModifier, final float attackSpeedModifier, final Properties properties) {
         this.attackDamageModifier = attackDamageModifier;
         this.attackSpeedModifier = attackSpeedModifier;
         this.properties = properties;
      }

      @Generated
      public int getAttackDamageModifier() {
         return this.attackDamageModifier;
      }

      @Generated
      public float getAttackSpeedModifier() {
         return this.attackSpeedModifier;
      }

      @Generated
      public Properties getProperties() {
         return this.properties;
      }
   }
}
