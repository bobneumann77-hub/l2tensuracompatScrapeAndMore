package io.github.manasmods.tensura.item.tool;

import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleHoeItem extends HoeItem {
   public SimpleHoeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties properties) {
      super(pTier, properties.attributes(HoeItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
   }

   public SimpleHoeItem(Tier pTier, SimpleHoeItem.HoeModifier hoeModifier) {
      this(pTier, hoeModifier.getAttackDamageModifier(), hoeModifier.getAttackSpeedModifier(), hoeModifier.getProperties());
   }

   public enum HoeModifier {
      WOOD(0, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      FLINT(-1, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      STONE(-1, -2.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      IRON(-2, -1.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      SILVER(-2, -1.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      GOLD(0, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      DIAMOND(-3, -0.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      LOW_MAGISTEEL(1, -2.7F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      NETHERITE(-4, -2.8F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      HIGH_MAGISTEEL(-5, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      MITHRIL(-6, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      ORICHALCUM(-7, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      PURE_MAGISTEEL(-8, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      ADAMANTITE(-9, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      HIHIIROKANE(-25, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant());

      private final int attackDamageModifier;
      private final float attackSpeedModifier;
      private final Properties properties;

      @Generated
      HoeModifier(final int attackDamageModifier, final float attackSpeedModifier, final Properties properties) {
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
