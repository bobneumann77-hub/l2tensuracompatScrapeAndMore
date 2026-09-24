package io.github.manasmods.tensura.item.consumable;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;

public class SimpleFoodItem extends Item {
   private final boolean liquid;
   private final boolean alwaysFoil;

   public SimpleFoodItem(Properties properties, boolean liquid, boolean alwaysFoil) {
      super(properties);
      this.liquid = liquid;
      this.alwaysFoil = alwaysFoil;
   }

   public SimpleFoodItem(Properties properties, boolean alwaysFoil) {
      this(properties, false, alwaysFoil);
   }

   public SimpleFoodItem(RegistrySupplier<CreativeModeTab> tab, FoodProperties food, boolean liquid, boolean alwaysFoil) {
      this(new Properties().arch$tab(tab).food(food), liquid, alwaysFoil);
   }

   public SimpleFoodItem(RegistrySupplier<CreativeModeTab> tab, FoodProperties pFood, boolean alwaysFoil) {
      this(tab, pFood, false, alwaysFoil);
   }

   public SimpleFoodItem(RegistrySupplier<CreativeModeTab> tab, FoodProperties pFood) {
      this(tab, pFood, false, false);
   }

   public SimpleFoodItem(FoodProperties pFood, boolean alwaysFoil) {
      this(TensuraCreativeTabs.CONSUMABLES, pFood, alwaysFoil);
   }

   public SimpleFoodItem(FoodProperties pFood) {
      this(pFood, false);
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return this.isLiquid() ? UseAnim.DRINK : super.getUseAnimation(pStack);
   }

   public boolean isFoil(ItemStack pStack) {
      return this.isAlwaysFoil() ? true : super.isFoil(pStack);
   }

   @Generated
   public boolean isLiquid() {
      return this.liquid;
   }

   @Generated
   public boolean isAlwaysFoil() {
      return this.alwaysFoil;
   }
}
