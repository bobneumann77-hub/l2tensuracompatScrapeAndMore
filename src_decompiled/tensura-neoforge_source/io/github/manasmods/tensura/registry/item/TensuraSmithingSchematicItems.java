package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.item.misc.SmithingSchematicItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class TensuraSmithingSchematicItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<SmithingSchematicItem> BASIC_BOWS = ITEMS.register("basic_bows_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SPIDER_BOWS = ITEMS.register("spider_bows_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> JAPANESE_SWORD = ITEMS.register("japanese_sword_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> HUNTING_KNIFE = ITEMS.register("hunting_knife_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SHORT_SWORD = ITEMS.register("short_sword_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> LONG_SWORD = ITEMS.register("long_sword_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> GREAT_SWORD = ITEMS.register("great_sword_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SPEAR = ITEMS.register("spear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> KUNAI = ITEMS.register("kunai_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SHIELD = ITEMS.register("shield_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> MAGIC_STAFF = ITEMS.register("magic_staff_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> LEATHER_GEAR = ITEMS.register("leather_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> MONSTER_LEATHER_GEAR = ITEMS.register(
      "monster_leather_gear_schematic", SmithingSchematicItem::new
   );
   public static final RegistrySupplier<SmithingSchematicItem> GOLD_GEAR = ITEMS.register("gold_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> IRON_GEAR = ITEMS.register("iron_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SILVER_GEAR = ITEMS.register("silver_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> ANT_CARAPACE_GEAR = ITEMS.register("ant_carapace_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SERPENT_SCALEMAIL_GEAR = ITEMS.register(
      "serpent_scalemail_gear_schematic", SmithingSchematicItem::new
   );
   public static final RegistrySupplier<SmithingSchematicItem> DIAMOND_GEAR = ITEMS.register("diamond_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> KNIGHT_SPIDER_CARAPACE_GEAR = ITEMS.register(
      "knight_spider_carapace_gear_schematic", SmithingSchematicItem::new
   );
   public static final RegistrySupplier<SmithingSchematicItem> LOW_MAGISTEEL_GEAR = ITEMS.register("low_magisteel_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> ARMORSAURUS_SCALEMAIL_GEAR = ITEMS.register(
      "armorsaurus_scalemail_gear_schematic", SmithingSchematicItem::new
   );
   public static final RegistrySupplier<SmithingSchematicItem> HIGH_MAGISTEEL_GEAR = ITEMS.register("high_magisteel_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> CHARYBDIS_SCALEMAIL_GEAR = ITEMS.register(
      "charybdis_scalemail_gear_schematic", SmithingSchematicItem::new
   );
   public static final RegistrySupplier<SmithingSchematicItem> MITHRIL_GEAR = ITEMS.register("mithril_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> ORICHALCUM_GEAR = ITEMS.register("orichalcum_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> PURE_MAGISTEEL_GEAR = ITEMS.register("pure_magisteel_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> ADAMANTITE_GEAR = ITEMS.register("adamantite_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> HIHIIROKANE_GEAR = ITEMS.register("hihiirokane_gear_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> ANTI_MAGIC_MASK = ITEMS.register("anti_magic_mask_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> DARK_SET = ITEMS.register("dark_set_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> PIERROT_MASK = ITEMS.register("pierrot_mask_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> SPATIAL_BLADE = ITEMS.register("spatial_blade_schematic", SmithingSchematicItem::new);
   public static final RegistrySupplier<SmithingSchematicItem> WEB_GUN = ITEMS.register("web_gun_schematic", SmithingSchematicItem::new);

   public static void init() {
      ITEMS.register();
   }
}
