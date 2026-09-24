package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.item.TensuraFoodProperties;
import io.github.manasmods.tensura.item.consumable.HealingPotionItem;
import io.github.manasmods.tensura.item.consumable.MagicBottleItem;
import io.github.manasmods.tensura.item.consumable.ManaPotionItem;
import io.github.manasmods.tensura.item.consumable.SimpleFoodItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class TensuraConsumableItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<Item> DUBIOUS_FOOD = ITEMS.register("dubious_food", () -> new SimpleFoodItem(TensuraFoodProperties.DUBIOUS_FOOD));
   public static final RegistrySupplier<Item> RAW_BLADE_TIGER_MEAT = ITEMS.register(
      "raw_blade_tiger_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_BLADE_TIGER_MEAT)
   );
   public static final RegistrySupplier<Item> BLADE_TIGER_STEAK = ITEMS.register(
      "blade_tiger_steak", () -> new SimpleFoodItem(TensuraFoodProperties.BLADE_TIGER_STEAK)
   );
   public static final RegistrySupplier<Item> RAW_ARMORSAURUS_MEAT = ITEMS.register(
      "raw_armorsaurus_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_ARMORSAURUS_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_ARMORSAURUS_MEAT = ITEMS.register(
      "cooked_armorsaurus_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_ARMORSAURUS_MEAT)
   );
   public static final RegistrySupplier<Item> CHILLED_SLIME = ITEMS.register("chilled_slime", () -> new SimpleFoodItem(TensuraFoodProperties.CHILLED_SLIME));
   public static final RegistrySupplier<Item> BUCKET_OF_CATTLEDEER_MILK = ITEMS.register(
      "bucket_of_cattledeer_milk", () -> new MilkBucketItem(new Properties().arch$tab(TensuraCreativeTabs.CONSUMABLES).stacksTo(1))
   );
   public static final RegistrySupplier<Item> CATTLEDEER_BEEF = ITEMS.register(
      "cattledeer_beef", () -> new SimpleFoodItem(TensuraFoodProperties.CATTLEDEER_BEEF)
   );
   public static final RegistrySupplier<Item> CATTLEDEER_STEAK = ITEMS.register(
      "cattledeer_steak", () -> new SimpleFoodItem(TensuraFoodProperties.CATTLEDEER_STEAK)
   );
   public static final RegistrySupplier<Item> RAW_CHARYBDIS_MEAT = ITEMS.register(
      "raw_charybdis_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_CHARYBDIS_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_CHARYBDIS_MEAT = ITEMS.register(
      "cooked_charybdis_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_CHARYBDIS_MEAT)
   );
   public static final RegistrySupplier<Item> GIANT_ANT_LEG = ITEMS.register("giant_ant_leg", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_GIANT_ANT_LEG));
   public static final RegistrySupplier<Item> COOKED_GIANT_ANT_LEG = ITEMS.register(
      "cooked_giant_ant_leg", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_GIANT_ANT_LEG)
   );
   public static final RegistrySupplier<Item> RAW_GIANT_BAT_MEAT = ITEMS.register(
      "raw_giant_bat_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_GIANT_BAT_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_GIANT_BAT_MEAT = ITEMS.register(
      "cooked_giant_bat_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_GIANT_BAT_MEAT)
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_LEG = ITEMS.register(
      "knight_spider_leg", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_KNIGHT_SPIDER_LEG)
   );
   public static final RegistrySupplier<Item> COOKED_KNIGHT_SPIDER_LEG = ITEMS.register(
      "cooked_knight_spider_leg", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_KNIGHT_SPIDER_LEG)
   );
   public static final RegistrySupplier<Item> RAW_MEGALODON_MEAT = ITEMS.register(
      "raw_megalodon_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_MEGALODON_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_MEGALODON_MEAT = ITEMS.register(
      "cooked_megalodon_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_MEGALODON_MEAT)
   );
   public static final RegistrySupplier<Item> RAW_SERPENT_MEAT = ITEMS.register(
      "raw_serpent_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_SERPENT_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_SERPENT_MEAT = ITEMS.register(
      "cooked_serpent_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_SERPENT_MEAT)
   );
   public static final RegistrySupplier<Item> RAW_SPEAR_TORO_MEAT = ITEMS.register(
      "raw_spear_toro_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_SPEAR_TORO_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_SPEAR_TORO_MEAT = ITEMS.register(
      "cooked_spear_toro_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_SPEAR_TORO_MEAT)
   );
   public static final RegistrySupplier<Item> SPEAR_TORO_FIN = ITEMS.register("spear_toro_fin", () -> new SimpleFoodItem(TensuraFoodProperties.SPEAR_TORO_FIN));
   public static final RegistrySupplier<Item> COOKED_SPEAR_TORO_FIN = ITEMS.register(
      "cooked_spear_toro_fin", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_SPEAR_TORO_FIN)
   );
   public static final RegistrySupplier<Item> RAW_SISSIE_MEAT = ITEMS.register(
      "raw_sissie_meat", () -> new SimpleFoodItem(TensuraFoodProperties.RAW_SISSIE_MEAT)
   );
   public static final RegistrySupplier<Item> COOKED_SISSIE_MEAT = ITEMS.register(
      "cooked_sissie_meat", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_SISSIE_MEAT)
   );
   public static final RegistrySupplier<Item> SISSIE_FIN = ITEMS.register("sissie_fin", () -> new SimpleFoodItem(TensuraFoodProperties.SISSIE_FIN));
   public static final RegistrySupplier<Item> COOKED_SISSIE_FIN = ITEMS.register(
      "cooked_sissie_fin", () -> new SimpleFoodItem(TensuraFoodProperties.COOKED_SISSIE_FIN)
   );
   public static final RegistrySupplier<Item> SILVER_APPLE = ITEMS.register(
      "silver_apple",
      () -> new SimpleFoodItem(new Properties().arch$tab(TensuraCreativeTabs.CONSUMABLES).rarity(Rarity.RARE).food(TensuraFoodProperties.SILVER_APPLE), false)
   );
   public static final RegistrySupplier<Item> ENCHANTED_SILVER_APPLE = ITEMS.register(
      "enchanted_silver_apple",
      () -> new SimpleFoodItem(
         new Properties().arch$tab(TensuraCreativeTabs.CONSUMABLES).rarity(Rarity.EPIC).food(TensuraFoodProperties.ENCHANTED_SILVER_APPLE), true
      )
   );
   public static final RegistrySupplier<Item> MAGIC_BOTTLE = ITEMS.register(
      "magic_bottle", () -> new MagicBottleItem(new Properties().arch$tab(TensuraCreativeTabs.CONSUMABLES))
   );
   public static final RegistrySupplier<Item> WATER_MAGIC_BOTTLE = ITEMS.register("magic_bottle_of_water", () -> new ManaPotionItem(0, 0.0F, 0.0F));
   public static final RegistrySupplier<Item> VACUUMED_WATER_MAGIC_BOTTLE = ITEMS.register(
      "vacuumed_magic_bottle_of_water", () -> new ManaPotionItem(0, 0.0F, 10.0F)
   );
   public static final RegistrySupplier<Item> LOW_POTION = ITEMS.register("low_potion", () -> new HealingPotionItem(1, 1.0F, 0.33F, 100.0F).setHealPercentage());
   public static final RegistrySupplier<Item> HIGH_POTION = ITEMS.register(
      "high_potion", () -> new HealingPotionItem(1, 1.0F, 0.66F, 1000.0F).setHealPercentage()
   );
   public static final RegistrySupplier<Item> FULL_POTION = ITEMS.register(
      "full_potion", () -> new HealingPotionItem(1, 1.0F, 0.99F, 10000.0F).setHealPercentage()
   );
   public static final RegistrySupplier<Item> REVIVAL_ELIXIR = ITEMS.register(
      "revival_elixir", () -> new HealingPotionItem(1, 1.0F, 1.0F, 20000.0F).setHealPercentage()
   );
   public static final RegistrySupplier<Item> LOW_ARCANE_POTION = ITEMS.register(
      "low_arcane_potion", () -> new ManaPotionItem(0, 0.0F, 0.05F).setMagiculePercentage()
   );
   public static final RegistrySupplier<Item> MEDIUM_ARCANE_POTION = ITEMS.register(
      "medium_arcane_potion", () -> new ManaPotionItem(0, 0.0F, 0.1F).setMagiculePercentage()
   );
   public static final RegistrySupplier<Item> HIGH_ARCANE_POTION = ITEMS.register(
      "high_arcane_potion", () -> new ManaPotionItem(0, 0.0F, 0.2F).setMagiculePercentage()
   );

   public static void init() {
      ITEMS.register();
   }
}
