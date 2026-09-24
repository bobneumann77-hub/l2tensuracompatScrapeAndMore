package io.github.manasmods.tensura.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TensuraItemTags {
   public static final TagKey<Item> EMPTY_HAND_POSE = modTag("empty_hand_pose");
   public static final TagKey<Item> NO_SLOWNESS_ON_USE = modTag("no_slowness_on_use");
   public static final TagKey<Item> STOP_ON_USE = modTag("stop_on_use");
   public static final TagKey<Item> RESET_SCROLLS = modTag("reset_scrolls");
   public static final TagKey<Item> HANDHELD_ENCHANTABLE = modTag("handheld_enchantable");
   public static final TagKey<Item> WEAPON_AND_TOOL_ENCHANTABLE = modTag("weapon_and_tool_enchantable");
   public static final TagKey<Item> RANGED_ENCHANTABLE = modTag("ranged_enchantable");
   public static final TagKey<Item> SPELL_CAST_WEAPONS = modTag("spell_cast_weapons");
   public static final TagKey<Item> MAGIC_GRIMOIRES = modTag("magic_grimoires");
   public static final TagKey<Item> MAGIC_STAVES = modTag("magic_staves");
   public static final TagKey<Item> MULTITOOLS = modTag("multitools");
   public static final TagKey<Item> FIST_WEAPONS = modTag("fist_weapons");
   public static final TagKey<Item> DAGGERS = modTag("daggers");
   public static final TagKey<Item> SHORT_SWORDS = modTag("short_swords");
   public static final TagKey<Item> LONG_SWORDS = modTag("long_swords");
   public static final TagKey<Item> GREAT_SWORDS = modTag("great_swords");
   public static final TagKey<Item> KATANAS = modTag("katanas");
   public static final TagKey<Item> KODACHIS = modTag("kodachis");
   public static final TagKey<Item> TACHIS = modTag("tachis");
   public static final TagKey<Item> ODACHIS = modTag("odachis");
   public static final TagKey<Item> TRIDENTS = modTag("tridents");
   public static final TagKey<Item> SPEARS = modTag("spears");
   public static final TagKey<Item> SCYTHES = modTag("scythes");
   public static final TagKey<Item> TOOL_RACK_EXCLUDED = modTag("tool_rack_excluded");
   public static final TagKey<Item> FISHING_RODS = modTag("fishing_rods");
   public static final TagKey<Item> POUCHES = modTag("pouches");
   public static final TagKey<Item> SICKLES = modTag("sickles");
   public static final TagKey<Item> SHIELDS = modTag("shields");
   public static final TagKey<Item> SCHEMATICS = modTag("schematics");
   public static final TagKey<Item> SPELL_BINDABLE = modTag("spell_bindable");
   public static TagKey<Item> BODY_ARMOR_ITEMS = modTag("body_armor_items");
   public static TagKey<Item> ADAMANTITE_ITEMS = modTag("adamantite_items");
   public static TagKey<Item> HIHIIROKANE_ITEMS = modTag("hihiirokane_items");
   public static TagKey<Item> HOLY_ARMAMENTS_ITEMS = modTag("holy_armaments_items");
   public static TagKey<Item> PIERROT_MASKS = modTag("pierrot_masks");
   public static TagKey<Item> PEACOCK_SITTING_HELMETS = modTag("peacock_sitting_helmets");
   public static TagKey<Item> SLOTTING_CAST_EXCLUDED = modTag("slotting_cast_excluded");
   public static TagKey<Item> INFINITY_ELEMENTAL_CORES = modTag("infinity_elemental_cores");
   public static TagKey<Item> ELEMENTAL_CORES = modTag("elemental_cores");
   public static TagKey<Item> COINS = modTag("coins");
   public static TagKey<Item> CAN_LOOK_AT_ENDERMAN = vanillaTag("can_look_at_enderman");
   public static TagKey<Item> CAN_TOUCH_DRAGON_EGG = vanillaTag("can_interact_with_dragon_egg");
   public static TagKey<Item> CAN_WALK_ON_POWDER_SNOW = vanillaTag("can_walk_on_powder_snow");
   public static TagKey<Item> INDESTRUCTIBLE_BY_ENVIRONMENTAL_CAUSE = vanillaTag("indestructible_by_environmental_cause");
   public static TagKey<Item> DUMMY_REMOVE_ON_LEAVING_HAND = modTag("dummy_remove_on_leaving_hand");
   public static TagKey<Item> STRONG_THREAD = modTag("strong_thread");
   public static TagKey<Item> WARP_PADS = modTag("warp_pads");
   public static TagKey<Item> BONE_GOLEMS = modTag("bone_golems");
   public static final TagKey<Item> MONSTER_LEATHERS = modTag("monster_leathers");
   public static final TagKey<Item> MAGIC_ORES = modTag("magic_ores");
   public static final TagKey<Item> SILVER_ORES = modTag("silver_ores");
   public static TagKey<Item> PALM_LOGS = modTag("palm_logs");
   public static TagKey<Item> MOB_SEED_PLANTABLE = modTag("mob_seed_plantable");
   public static TagKey<Item> STONE_FOR_TRADES = vanillaTag("stone_for_trades");
   public static TagKey<Item> GLAZED_TERRACOTTA = vanillaTag("glazed_terracotta");
   public static TagKey<Item> CONCRETE_POWDER = vanillaTag("concrete_powder");
   public static TagKey<Item> CONCRETE = vanillaTag("concrete");
   public static TagKey<Item> DYE = vanillaTag("dye");
   public static TagKey<Item> NO_CURSE = modTag("ability/no_curse");
   public static TagKey<Item> NO_DECRAFT = modTag("ability/no_decraft");
   public static TagKey<Item> NPC_KEEP = modTag("npc_keep");
   public static TagKey<Item> NPC_STORABLE = modTag("npc_storable");
   public static TagKey<Item> BUTCHER_STORABLE = modTag("butcher_storable");
   public static TagKey<Item> FARMER_STORABLE = modTag("farmer_storable");
   public static TagKey<Item> FISHERMAN_STORABLE = modTag("fisherman_storable");
   public static TagKey<Item> GUARD_STORABLE = modTag("guard_storable");
   public static TagKey<Item> LUMBERJACK_STORABLE = modTag("lumberjack_storable");
   public static TagKey<Item> SHEPHERD_STORABLE = modTag("shepherd_storable");
   public static TagKey<Item> EVOLUTION_ESSENCES = modTag("evolution_essences");
   public static TagKey<Item> MONSTER_CONSUMABLES = modTag("monster_consumables");
   public static TagKey<Item> RAW_MONSTER_CONSUMABLES = modTag("raw_monster_consumables");
   public static TagKey<Item> COOKED_MONSTER_CONSUMABLES = modTag("cooked_monster_consumables");
   public static TagKey<Item> HIPOKUTE_POTION_CONTAINERS = modTag("hipokute_potion_containers");
   public static TagKey<Item> HIPOKUTE_POTIONS = modTag("hipokute_potions");
   public static TagKey<Item> ARCANE_POTIONS = modTag("arcane_potions");
   public static TagKey<Item> DUBIOUS_POISON_INGREDIENT = modTag("dubious/ingredient_poison");
   public static TagKey<Item> DUBIOUS_MAGIC_INGREDIENT = modTag("dubious/ingredient_magic");
   public static TagKey<Item> DUBIOUS_RAW_INGREDIENT = modTag("dubious/ingredient_raw");
   public static TagKey<Item> DUBIOUS_EFFECT_INGREDIENT = modTag("dubious/ingredient_effect");
   public static TagKey<Item> DUBIOUS_CRYSTAL_INGREDIENT = modTag("dubious/ingredient_crystal");
   public static TagKey<Item> DUBIOUS_BREWING_INGREDIENT = modTag("dubious/ingredient_brewing");
   public static TagKey<Item> DUBIOUS_MUSHROOM_INGREDIENT = modTag("dubious/ingredient_mushroom");
   public static TagKey<Item> ANT_FOOD = modTag("ant_food");
   public static TagKey<Item> BEAR_FOOD = modTag("bear_food");
   public static TagKey<Item> CATERPILLAR_FOOD = modTag("caterpillar_food");
   public static TagKey<Item> CATTLE_FOOD = modTag("cattledeer_food");
   public static TagKey<Item> PEACOCK_FOOD = modTag("peacock_food");
   public static TagKey<Item> PEACOCK_TAMING_FOOD = modTag("peacock_taming_food");
   public static TagKey<Item> RABBIT_TAMING_FOOD = modTag("rabbit_taming_food");
   public static TagKey<Item> SLIME_FOOD = modTag("slime_food");
   public static TagKey<Item> SLIME_TAMING_FOOD = modTag("slime_taming_food");
   public static TagKey<Item> METAL_SLIME_TAMING_FOOD = modTag("metal_slime_taming_food");
   public static TagKey<Item> SPIRIT_FOOD = modTag("spirit_food");
   public static TagKey<Item> MOTH_TEMPT_ITEMS = modTag("moth_tempt_items");
   public static TagKey<Item> RESET_BARGHEST_FLAME = modTag("barghest/flame_reset");
   public static TagKey<Item> BARGHEST_FLAME_ORANGE = modTag("barghest/flame_orange");
   public static TagKey<Item> BARGHEST_FLAME_TEAL = modTag("barghest/flame_teal");
   public static TagKey<Item> BARGHEST_FLAME_YELLOW = modTag("barghest/flame_yellow");
   public static TagKey<Item> BARGHEST_FLAME_RED = modTag("barghest/flame_red");
   public static TagKey<Item> BARGHEST_FLAME_GREEN = modTag("barghest/flame_green");
   public static TagKey<Item> BARGHEST_FLAME_PURPLE = modTag("barghest/flame_purple");
   public static TagKey<Item> BARGHEST_FLAME_WHITE = modTag("barghest/flame_white");

   static TagKey<Item> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<Item> neoforgeTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("neoforge", name));
   }

   static TagKey<Item> vanillaTag(String name) {
      return create(ResourceLocation.withDefaultNamespace(name));
   }

   static TagKey<Item> create(ResourceLocation name) {
      return TagKey.create(Registries.ITEM, name);
   }
}
