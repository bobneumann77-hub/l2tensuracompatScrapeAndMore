package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.item.misc.BattlewillManualItem;
import io.github.manasmods.tensura.item.misc.BoneGolemItem;
import io.github.manasmods.tensura.item.misc.ElementCoreItem;
import io.github.manasmods.tensura.item.misc.HipokuteFlowerItem;
import io.github.manasmods.tensura.item.misc.MagicTomeItem;
import io.github.manasmods.tensura.item.misc.MarionetteHeartItem;
import io.github.manasmods.tensura.item.misc.PouchItem;
import io.github.manasmods.tensura.item.misc.ResetScrollItem;
import io.github.manasmods.tensura.item.misc.ShadowStorageItem;
import io.github.manasmods.tensura.item.misc.SimpleBlockItem;
import io.github.manasmods.tensura.item.misc.SlimeBucketItem;
import io.github.manasmods.tensura.item.misc.SpatialBagItem;
import io.github.manasmods.tensura.item.misc.TensuraFireChargeItem;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.sound.TensuraJukeboxSongs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class TensuraMaterialItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<Item> MUSIC_DISC_NANODA = ITEMS.register(
      "music_disc_nanoda",
      () -> new Item(
         new Properties()
            .stacksTo(1)
            .arch$tab(TensuraCreativeTabs.MISCELLANEOUS)
            .rarity(Rarity.EPIC)
            .jukeboxPlayable(TensuraJukeboxSongs.NANODA)
            .fireResistant()
      )
   );
   public static final RegistrySupplier<Item> DWARGON_BANNER_PATTERN = ITEMS.register(
      "dwargon_banner_pattern",
      () -> new BannerPatternItem(TensuraTags.BannerPattens.DWARGON, new Properties().stacksTo(1).arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> SLIME_IN_A_BUCKET = ITEMS.register(
      "slime_in_a_bucket", () -> new SlimeBucketItem(new Properties().stacksTo(1).arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> SHADOW_STORAGE = ITEMS.register(
      "shadow_storage", () -> new ShadowStorageItem(new Properties().stacksTo(1).fireResistant())
   );
   public static final RegistrySupplier<Item> THATCH = ITEMS.register("thatch", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS)));
   public static final RegistrySupplier<Item> HIPOKUTE_SEEDS = ITEMS.register(
      "hipokute_seeds", () -> new ItemNameBlockItem((Block)TensuraBlocks.HIPOKUTE_GRASS.get(), new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> HIPOKUTE_GRASS = ITEMS.register(
      "hipokute_grass", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> HIPOKUTE_FLOWER = ITEMS.register(
      "hipokute_flower", () -> new HipokuteFlowerItem(TensuraBlocks.POTTED_HIPOKUTE_FLOWER, new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> BAFFLEDIL = ITEMS.register(
      "baffledil", () -> new SimpleBlockItem((Block)TensuraBlocks.BAFFLEDIL.get(), new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> MONSTER_SADDLE = ITEMS.register(
      "monster_saddle", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(1))
   );
   public static final RegistrySupplier<Item> RAW_SILVER = ITEMS.register(
      "raw_silver", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> SILVER_INGOT = ITEMS.register(
      "silver_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> SILVER_NUGGET = ITEMS.register(
      "silver_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> MAGIC_ORE = ITEMS.register(
      "magic_ore_shard", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_INGOT = ITEMS.register(
      "low_magisteel_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_INGOT = ITEMS.register(
      "high_magisteel_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_INGOT = ITEMS.register(
      "mithril_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_INGOT = ITEMS.register(
      "orichalcum_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_INGOT = ITEMS.register(
      "pure_magisteel_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_INGOT = ITEMS.register(
      "adamantite_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_INGOT = ITEMS.register(
      "hihiirokane_ingot", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_NUGGET = ITEMS.register(
      "low_magisteel_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_NUGGET = ITEMS.register(
      "high_magisteel_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_NUGGET = ITEMS.register(
      "mithril_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_NUGGET = ITEMS.register(
      "orichalcum_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_NUGGET = ITEMS.register(
      "pure_magisteel_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_NUGGET = ITEMS.register(
      "adamantite_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_NUGGET = ITEMS.register(
      "hihiirokane_nugget", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> MAGIC_STONE = ITEMS.register(
      "magic_stone", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> ELEMENT_CORE_EMPTY = ITEMS.register("element_core_empty", ElementCoreItem::new);
   public static final RegistrySupplier<Item> EARTH_ELEMENTAL_SHARD = ITEMS.register(
      "earth_elemental_shard",
      () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final RegistrySupplier<ElementCoreItem> ELEMENT_CORE_EARTH = ITEMS.register("element_core_earth", () -> new ElementCoreItem(Element.EARTH));
   public static final RegistrySupplier<Item> FIRE_ELEMENTAL_SHARD = ITEMS.register(
      "fire_elemental_shard", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final RegistrySupplier<ElementCoreItem> ELEMENT_CORE_FIRE = ITEMS.register("element_core_fire", () -> new ElementCoreItem(Element.FLAME));
   public static final RegistrySupplier<Item> SPACE_ELEMENTAL_SHARD = ITEMS.register(
      "space_elemental_shard",
      () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final RegistrySupplier<ElementCoreItem> ELEMENT_CORE_SPACE = ITEMS.register("element_core_space", () -> new ElementCoreItem(Element.SPACE));
   public static final RegistrySupplier<Item> WATER_ELEMENTAL_SHARD = ITEMS.register(
      "water_elemental_shard",
      () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final RegistrySupplier<ElementCoreItem> ELEMENT_CORE_WATER = ITEMS.register("element_core_water", () -> new ElementCoreItem(Element.WATER));
   public static final RegistrySupplier<Item> WIND_ELEMENTAL_SHARD = ITEMS.register(
      "wind_elemental_shard", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final RegistrySupplier<ElementCoreItem> ELEMENT_CORE_WIND = ITEMS.register("element_core_wind", () -> new ElementCoreItem(Element.WIND));
   public static final RegistrySupplier<Item> WARP_CORE = ITEMS.register(
      "warp_core", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant().rarity(Rarity.RARE))
   );
   public static final RegistrySupplier<Item> DAEMON_CORE = ITEMS.register(
      "daemon_core", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant().rarity(Rarity.RARE))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_BONE_GOLEM = ITEMS.register(
      "low_magisteel_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.LOW_MAGISTEEL)
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_BONE_GOLEM = ITEMS.register(
      "high_magisteel_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.HIGH_MAGISTEEL)
   );
   public static final RegistrySupplier<Item> MITHRIL_BONE_GOLEM = ITEMS.register("mithril_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.MITHRIL));
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_BONE_GOLEM = ITEMS.register(
      "pure_magisteel_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.PURE_MAGISTEEL)
   );
   public static final RegistrySupplier<Item> ORICHALCUM_BONE_GOLEM = ITEMS.register(
      "orichalcum_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.ORICHALCUM)
   );
   public static final RegistrySupplier<Item> ADAMANTITE_BONE_GOLEM = ITEMS.register(
      "adamantite_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.ADAMANTITE)
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_BONE_GOLEM = ITEMS.register(
      "hihiirokane_bone_golem", () -> new BoneGolemItem(BoneGolemVariant.HIHIIROKANE)
   );
   public static final RegistrySupplier<Item> BLACK_FIRE_CHARGE = ITEMS.register(
      "black_fire_charge",
      () -> new TensuraFireChargeItem(TensuraBlocks.BLACK_FIRE, new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> BRONZE_COIN = ITEMS.register(
      "bronze_coin", () -> new Item(new Properties().stacksTo(100).arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> SILVER_COIN = ITEMS.register(
      "silver_coin", () -> new Item(new Properties().stacksTo(100).arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> GOLD_COIN = ITEMS.register(
      "gold_coin", () -> new Item(new Properties().stacksTo(100).arch$tab(TensuraCreativeTabs.MISCELLANEOUS))
   );
   public static final RegistrySupplier<Item> STELLAR_GOLD_COIN = ITEMS.register(
      "stellar_gold_coin", () -> new Item(new Properties().stacksTo(100).arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant())
   );
   public static final RegistrySupplier<Item> POUCH_D = ITEMS.register("pouch_d", () -> new PouchItem(4));
   public static final RegistrySupplier<Item> POUCH_C = ITEMS.register("pouch_c", () -> new PouchItem(8));
   public static final RegistrySupplier<Item> POUCH_B = ITEMS.register("pouch_b", () -> new PouchItem(12));
   public static final RegistrySupplier<Item> POUCH_A = ITEMS.register("pouch_a", () -> new PouchItem(16));
   public static final RegistrySupplier<Item> POUCH_SPECIAL_A = ITEMS.register("pouch_special_a", () -> new PouchItem(20));
   public static final RegistrySupplier<Item> SPATIAL_BAG = ITEMS.register("spatial_bag", SpatialBagItem::new);
   public static final RegistrySupplier<Item> MARIONETTE_HEART = ITEMS.register("marionette_heart", MarionetteHeartItem::new);
   public static final RegistrySupplier<Item> RACE_RESET_SCROLL = ITEMS.register(
      "race_reset_scroll", () -> new ResetScrollItem(ResetScrollItem.ResetType.RESET_RACE)
   );
   public static final RegistrySupplier<Item> SKILL_RESET_SCROLL = ITEMS.register(
      "skill_reset_scroll", () -> new ResetScrollItem(ResetScrollItem.ResetType.RESET_SKILL)
   );
   public static final RegistrySupplier<Item> CHARACTER_RESET_SCROLL = ITEMS.register(
      "character_reset_scroll", () -> new ResetScrollItem(ResetScrollItem.ResetType.RESET_ALL)
   );
   public static final RegistrySupplier<Item> MAGIC_TOME = ITEMS.register("magic_tome", MagicTomeItem::new);
   public static final RegistrySupplier<Item> UNBOUND_TOME = ITEMS.register("unbound_tome", () -> new Item(new Properties().rarity(Rarity.RARE).stacksTo(16)));
   public static final RegistrySupplier<Item> BATTLEWILL_MANUAL = ITEMS.register("battlewill_manual", BattlewillManualItem::new);

   public static void init() {
      ITEMS.register();
   }
}
