package io.github.manasmods.tensura.item.tool;

import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleAxeItem extends AxeItem {
   public SimpleAxeItem(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties properties) {
      super(pTier, properties.attributes(AxeItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
   }

   public SimpleAxeItem(Tier pTier, SimpleAxeItem.AxeModifier axeModifier) {
      this(pTier, axeModifier.getAttackDamageModifier(), axeModifier.getAttackSpeedModifier(), axeModifier.getProperties());
   }

   public enum AxeModifier {
      WOOD(6.0F, -3.2F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      FLINT(6.0F, -3.2F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      STONE(7.0F, -3.2F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      IRON(6.0F, -3.1F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      SILVER(6.0F, -3.1F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      GOLD(6.0F, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      DIAMOND(5.0F, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      LOW_MAGISTEEL(5.0F, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      NETHERITE(5.0F, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS)),
      HIGH_MAGISTEEL(5.0F, -2.9F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      MITHRIL(5.0F, -2.9F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      ORICHALCUM(5.0F, -2.9F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      PURE_MAGISTEEL(5.0F, -2.8F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      ADAMANTITE(5.0F, -2.8F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()),
      HIHIIROKANE(5.0F, -2.7F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant());

      private final float attackDamageModifier;
      private final float attackSpeedModifier;
      private final Properties properties;

      @Generated
      AxeModifier(final float attackDamageModifier, final float attackSpeedModifier, final Properties properties) {
         this.attackDamageModifier = attackDamageModifier;
         this.attackSpeedModifier = attackSpeedModifier;
         this.properties = properties;
      }

      @Generated
      public float getAttackDamageModifier() {
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
