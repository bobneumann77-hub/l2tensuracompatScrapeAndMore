package io.github.manasmods.tensura.item.tool;

import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleShovelItem extends ShovelItem {
   public SimpleShovelItem(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties properties) {
      super(pTier, properties.attributes(ShovelItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
   }

   public SimpleShovelItem(Tier pTier, Properties properties) {
      this(pTier, 1.5F, -3.0F, properties);
   }

   public SimpleShovelItem(Tier pTier, DeferredSupplier<CreativeModeTab> tab) {
      this(pTier, 1.5F, -3.0F, new Properties().arch$tab(tab));
   }
}
