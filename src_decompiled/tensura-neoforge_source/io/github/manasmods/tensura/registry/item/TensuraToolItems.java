package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.misc.OrbOfDominationItem;
import io.github.manasmods.tensura.item.tool.SimpleAxeItem;
import io.github.manasmods.tensura.item.tool.SimpleHoeItem;
import io.github.manasmods.tensura.item.tool.SimplePickaxeItem;
import io.github.manasmods.tensura.item.tool.SimpleShovelItem;
import io.github.manasmods.tensura.item.tool.custom.ArmorsaurusGauntletItem;
import io.github.manasmods.tensura.item.tool.custom.ArmorsaurusShieldItem;
import io.github.manasmods.tensura.item.tool.custom.DragonKnuckleItem;
import io.github.manasmods.tensura.item.tool.custom.TempestScaleShieldItem;
import io.github.manasmods.tensura.item.weapon.SimpleGreatSwordItem;
import io.github.manasmods.tensura.item.weapon.SimpleKatanaItem;
import io.github.manasmods.tensura.item.weapon.SimpleKodachiItem;
import io.github.manasmods.tensura.item.weapon.SimpleLongSwordItem;
import io.github.manasmods.tensura.item.weapon.SimpleOdachiItem;
import io.github.manasmods.tensura.item.weapon.SimpleScytheItem;
import io.github.manasmods.tensura.item.weapon.SimpleShortSwordItem;
import io.github.manasmods.tensura.item.weapon.SimpleSickleItem;
import io.github.manasmods.tensura.item.weapon.SimpleSpearItem;
import io.github.manasmods.tensura.item.weapon.SimpleSwordItem;
import io.github.manasmods.tensura.item.weapon.SimpleTachiItem;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.item.weapon.custom.BladeTigerScytheItem;
import io.github.manasmods.tensura.item.weapon.custom.CentipedeDaggerItem;
import io.github.manasmods.tensura.item.weapon.custom.IceBladeItem;
import io.github.manasmods.tensura.item.weapon.custom.MeatCrusherItem;
import io.github.manasmods.tensura.item.weapon.custom.SissieToothPickaxe;
import io.github.manasmods.tensura.item.weapon.custom.SpatialBladeItem;
import io.github.manasmods.tensura.item.weapon.custom.SpiderDaggerItem;
import io.github.manasmods.tensura.item.weapon.custom.TempestScaleKnifeItem;
import io.github.manasmods.tensura.item.weapon.custom.TempestScaleSwordItem;
import io.github.manasmods.tensura.item.weapon.custom.VortexSpearItem;
import io.github.manasmods.tensura.item.weapon.ranged.InvisibleArrowItem;
import io.github.manasmods.tensura.item.weapon.ranged.KunaiItem;
import io.github.manasmods.tensura.item.weapon.ranged.MagisteelKunaiItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleBowItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleCrossbowItem;
import io.github.manasmods.tensura.item.weapon.ranged.SpearedFinArrowItem;
import io.github.manasmods.tensura.item.weapon.ranged.WaltherP99Item;
import io.github.manasmods.tensura.item.weapon.ranged.WebCartridgeItem;
import io.github.manasmods.tensura.item.weapon.ranged.WebGunItem;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.item.weapon.spell.SlimeStaffItem;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TensuraToolItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<TensuraSwordItem> GOBLIN_CLUB = ITEMS.register(
      "goblin_club",
      () -> new TensuraSwordItem(Tiers.WOOD, 2, -2.5F, 0.0, 0.0, 0.0, 0.5, 0.5, new Properties().arch$tab(TensuraCreativeTabs.GEARS).rarity(Rarity.RARE))
   );
   public static final RegistrySupplier<TensuraSwordItem> KANABO = ITEMS.register(
      "kanabo",
      () -> new TensuraSwordItem(
         Tiers.WOOD, 9, -3.0F, 1.0, 0.0, 0.0, 0.5, 1.0, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(350).rarity(Rarity.EPIC)
      )
   );
   public static final RegistrySupplier<Item> SHORT_BOW = ITEMS.register(
      "short_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(231), 10, 10, 2.0, 0.5F)
   );
   public static final RegistrySupplier<Item> LONG_BOW = ITEMS.register(
      "long_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(500), 30, 30, 2.5, 1.2F)
   );
   public static final RegistrySupplier<Item> WAR_BOW = ITEMS.register(
      "war_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(615), 60, 60, 7.0, 1.4F, 1.5F)
   );
   public static final RegistrySupplier<Item> SHORT_SPIDER_BOW = ITEMS.register(
      "short_spider_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(578), 15, 15, 3.0, 0.2F)
   );
   public static final RegistrySupplier<Item> SPIDER_BOW = ITEMS.register(
      "spider_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(980), 25, 25, 3.5, 0.5F)
   );
   public static final RegistrySupplier<Item> LONG_SPIDER_BOW = ITEMS.register(
      "long_spider_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(1250), 45, 45, 5.5, 0.7F, 1.5F)
   );
   public static final RegistrySupplier<Item> WAR_SPIDER_BOW = ITEMS.register(
      "war_spider_bow", () -> new SimpleBowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(1538), 80, 80, 14.0, 1.0F, 1.5F)
   );
   public static final RegistrySupplier<SimpleCrossbowItem> ANT_CROSSBOW = ITEMS.register(
      "ant_crossbow", () -> new SimpleCrossbowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(960), 20, 4.0F, 2.0F, 0.6F)
   );
   public static final RegistrySupplier<InvisibleArrowItem> INVISIBLE_ARROW = ITEMS.register(
      "invisible_arrow", () -> new InvisibleArrowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SpearedFinArrowItem> SPEARED_FIN_ARROW = ITEMS.register(
      "speared_fin_arrow", () -> new SpearedFinArrowItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<KunaiItem> KUNAI = ITEMS.register(
      "kunai", () -> new KunaiItem(Tiers.IRON, 7.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).stacksTo(16))
   );
   public static final RegistrySupplier<MagisteelKunaiItem> PURE_MAGISTEEL_KUNAI = ITEMS.register(
      "pure_magisteel_kunai", () -> new MagisteelKunaiItem(TensuraToolTiers.LOW_MAGISTEEL, 10.0F, 1000)
   );
   public static final RegistrySupplier<ArmorsaurusShieldItem> ARMORSAURUS_SHIELD = ITEMS.register("armorsaurus_shield", ArmorsaurusShieldItem::new);
   public static final RegistrySupplier<TempestScaleShieldItem> TEMPEST_SCALE_SHIELD = ITEMS.register("tempest_scale_shield", TempestScaleShieldItem::new);
   public static final RegistrySupplier<Item> TEMPEST_SCALE_SWORD = ITEMS.register(
      "tempest_scale_sword", () -> new TempestScaleSwordItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(3964))
   );
   public static final RegistrySupplier<Item> TEMPEST_SCALE_KNIFE = ITEMS.register(
      "tempest_scale_knife", () -> new TempestScaleKnifeItem(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(3964))
   );
   public static final RegistrySupplier<Item> CENTIPEDE_DAGGER = ITEMS.register("centipede_dagger", CentipedeDaggerItem::new);
   public static final RegistrySupplier<Item> SPIDER_DAGGER = ITEMS.register("spider_dagger", SpiderDaggerItem::new);
   public static final RegistrySupplier<Item> BEAST_HORN_SPEAR = ITEMS.register(
      "beast_horn_spear", () -> new SimpleSpearItem(Tiers.IRON, 7, -2.6F, 0.1, 0.0, 2, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> UNICORN_HORN_SPEAR = ITEMS.register(
      "unicorn_horn_spear",
      () -> new SimpleSpearItem(
         TensuraToolTiers.LOW_MAGISTEEL, 7, -2.6F, 0.2, 0.0, 4, -3.0F, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(500)
      )
   );
   public static final RegistrySupplier<Item> BLADE_TIGER_SCYTHE = ITEMS.register("blade_tiger_scythe", BladeTigerScytheItem::new);
   public static final RegistrySupplier<Item> SISSIE_TOOTH_PICKAXE = ITEMS.register("sissie_tooth_pickaxe", SissieToothPickaxe::new);
   public static final RegistrySupplier<SimpleSpellCastItem> LOW_MAGIC_STAFF = ITEMS.register(
      "low_magic_staff",
      () -> new SimpleSpellCastItem(20, 3, TensuraToolTiers.LOW_MAGISTEEL, -3, 0.05, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(100))
   );
   public static final RegistrySupplier<SimpleSpellCastItem> MEDIUM_MAGIC_STAFF = ITEMS.register(
      "medium_magic_staff",
      () -> new SimpleSpellCastItem(
         10, 4, TensuraToolTiers.HIGH_MAGISTEEL, -7, 0.1, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(300).rarity(Rarity.UNCOMMON)
      )
   );
   public static final RegistrySupplier<SimpleSpellCastItem> HIGH_MAGIC_STAFF = ITEMS.register(
      "high_magic_staff",
      () -> new SimpleSpellCastItem(
         5, 5, TensuraToolTiers.PURE_MAGISTEEL, -14, 0.2, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(500).rarity(Rarity.RARE)
      )
   );
   public static final RegistrySupplier<Item> SLIME_STAFF = ITEMS.register("slime_staff", SlimeStaffItem::new);
   public static final RegistrySupplier<SimpleSpellCastItem> GRIMOIRE_D = ITEMS.register(
      "grimoire_d", () -> new SimpleSpellCastItem(40, 3, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(100))
   );
   public static final RegistrySupplier<SimpleSpellCastItem> GRIMOIRE_C = ITEMS.register(
      "grimoire_c", () -> new SimpleSpellCastItem(30, 4, 0.05, new Properties().arch$tab(TensuraCreativeTabs.GEARS).rarity(Rarity.UNCOMMON).durability(200))
   );
   public static final RegistrySupplier<SimpleSpellCastItem> GRIMOIRE_B = ITEMS.register(
      "grimoire_b", () -> new SimpleSpellCastItem(20, 5, 0.1, new Properties().arch$tab(TensuraCreativeTabs.GEARS).rarity(Rarity.UNCOMMON).durability(300))
   );
   public static final RegistrySupplier<SimpleSpellCastItem> GRIMOIRE_A = ITEMS.register(
      "grimoire_a", () -> new SimpleSpellCastItem(15, 6, 0.15, new Properties().arch$tab(TensuraCreativeTabs.GEARS).rarity(Rarity.RARE).durability(400))
   );
   public static final RegistrySupplier<SimpleSpellCastItem> GRIMOIRE_SPECIAL_A = ITEMS.register(
      "grimoire_special_a", () -> new SimpleSpellCastItem(10, 7, 0.2, new Properties().arch$tab(TensuraCreativeTabs.GEARS).rarity(Rarity.RARE).durability(500))
   );
   public static final RegistrySupplier<Item> WOODEN_SHORT_SWORD = ITEMS.register(
      "wooden_short_sword", () -> new SimpleShortSwordItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_LONG_SWORD = ITEMS.register(
      "wooden_long_sword", () -> new SimpleLongSwordItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_GREAT_SWORD = ITEMS.register(
      "wooden_great_sword", () -> new SimpleGreatSwordItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_KATANA = ITEMS.register(
      "wooden_katana", () -> new SimpleKatanaItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_KODACHI = ITEMS.register(
      "wooden_kodachi", () -> new SimpleKodachiItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_TACHI = ITEMS.register(
      "wooden_tachi", () -> new SimpleTachiItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_ODACHI = ITEMS.register(
      "wooden_odachi", () -> new SimpleOdachiItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_SPEAR = ITEMS.register(
      "wooden_spear", () -> new SimpleSpearItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> WOODEN_SCYTHE = ITEMS.register(
      "wooden_scythe", () -> new SimpleScytheItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> WOODEN_SICKLE = ITEMS.register(
      "wooden_sickle", () -> new SimpleSickleItem(Tiers.WOOD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_SHORT_SWORD = ITEMS.register(
      "stone_short_sword", () -> new SimpleShortSwordItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_LONG_SWORD = ITEMS.register(
      "stone_long_sword", () -> new SimpleLongSwordItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_GREAT_SWORD = ITEMS.register(
      "stone_great_sword", () -> new SimpleGreatSwordItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_KATANA = ITEMS.register(
      "stone_katana", () -> new SimpleKatanaItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_KODACHI = ITEMS.register(
      "stone_kodachi", () -> new SimpleKodachiItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_TACHI = ITEMS.register(
      "stone_tachi", () -> new SimpleTachiItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_ODACHI = ITEMS.register(
      "stone_odachi", () -> new SimpleOdachiItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_SPEAR = ITEMS.register(
      "stone_spear", () -> new SimpleSpearItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> STONE_SCYTHE = ITEMS.register(
      "stone_scythe", () -> new SimpleScytheItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> STONE_SICKLE = ITEMS.register(
      "stone_sickle", () -> new SimpleSickleItem(Tiers.STONE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_SHORT_SWORD = ITEMS.register(
      "golden_short_sword", () -> new SimpleShortSwordItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_LONG_SWORD = ITEMS.register(
      "golden_long_sword", () -> new SimpleLongSwordItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_GREAT_SWORD = ITEMS.register(
      "golden_great_sword", () -> new SimpleGreatSwordItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_KATANA = ITEMS.register(
      "golden_katana", () -> new SimpleKatanaItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_KODACHI = ITEMS.register(
      "golden_kodachi", () -> new SimpleKodachiItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_TACHI = ITEMS.register(
      "golden_tachi", () -> new SimpleTachiItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_ODACHI = ITEMS.register(
      "golden_odachi", () -> new SimpleOdachiItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_SPEAR = ITEMS.register(
      "golden_spear", () -> new SimpleSpearItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> GOLDEN_SCYTHE = ITEMS.register(
      "golden_scythe", () -> new SimpleScytheItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> GOLDEN_SICKLE = ITEMS.register(
      "golden_sickle", () -> new SimpleSickleItem(Tiers.GOLD, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSwordItem> SILVER_SWORD = ITEMS.register(
      "silver_sword", () -> new SimpleSwordItem(TensuraToolTiers.SILVER, SimpleSwordItem.SwordModifier.NORMAL)
   );
   public static final RegistrySupplier<Item> SILVER_SHORT_SWORD = ITEMS.register(
      "silver_short_sword", () -> new SimpleShortSwordItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_LONG_SWORD = ITEMS.register(
      "silver_long_sword", () -> new SimpleLongSwordItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_GREAT_SWORD = ITEMS.register(
      "silver_great_sword", () -> new SimpleGreatSwordItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_KATANA = ITEMS.register(
      "silver_katana", () -> new SimpleKatanaItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_KODACHI = ITEMS.register(
      "silver_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_TACHI = ITEMS.register(
      "silver_tachi", () -> new SimpleTachiItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_ODACHI = ITEMS.register(
      "silver_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_SPEAR = ITEMS.register(
      "silver_spear", () -> new SimpleSpearItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> SILVER_SCYTHE = ITEMS.register(
      "silver_scythe", () -> new SimpleScytheItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> SILVER_PICKAXE = ITEMS.register(
      "silver_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleAxeItem> SILVER_AXE = ITEMS.register(
      "silver_axe", () -> new SimpleAxeItem(TensuraToolTiers.SILVER, SimpleAxeItem.AxeModifier.SILVER)
   );
   public static final RegistrySupplier<SimpleShovelItem> SILVER_SHOVEL = ITEMS.register(
      "silver_shovel", () -> new SimpleShovelItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleHoeItem> SILVER_HOE = ITEMS.register(
      "silver_hoe", () -> new SimpleHoeItem(TensuraToolTiers.SILVER, SimpleHoeItem.HoeModifier.SILVER)
   );
   public static final RegistrySupplier<SimpleSickleItem> SILVER_SICKLE = ITEMS.register(
      "silver_sickle", () -> new SimpleSickleItem(TensuraToolTiers.SILVER, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_SHORT_SWORD = ITEMS.register(
      "iron_short_sword", () -> new SimpleShortSwordItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_LONG_SWORD = ITEMS.register(
      "iron_long_sword", () -> new SimpleLongSwordItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_GREAT_SWORD = ITEMS.register(
      "iron_great_sword", () -> new SimpleGreatSwordItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_KATANA = ITEMS.register(
      "iron_katana", () -> new SimpleKatanaItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_KODACHI = ITEMS.register(
      "iron_kodachi", () -> new SimpleKodachiItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_TACHI = ITEMS.register(
      "iron_tachi", () -> new SimpleTachiItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_ODACHI = ITEMS.register(
      "iron_odachi", () -> new SimpleOdachiItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_SPEAR = ITEMS.register(
      "iron_spear", () -> new SimpleSpearItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> IRON_SCYTHE = ITEMS.register(
      "iron_scythe", () -> new SimpleScytheItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> IRON_SICKLE = ITEMS.register(
      "iron_sickle", () -> new SimpleSickleItem(Tiers.IRON, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_SHORT_SWORD = ITEMS.register(
      "diamond_short_sword", () -> new SimpleShortSwordItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_LONG_SWORD = ITEMS.register(
      "diamond_long_sword", () -> new SimpleLongSwordItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_GREAT_SWORD = ITEMS.register(
      "diamond_great_sword", () -> new SimpleGreatSwordItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_KATANA = ITEMS.register(
      "diamond_katana", () -> new SimpleKatanaItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_KODACHI = ITEMS.register(
      "diamond_kodachi", () -> new SimpleKodachiItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_TACHI = ITEMS.register(
      "diamond_tachi", () -> new SimpleTachiItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_ODACHI = ITEMS.register(
      "diamond_odachi", () -> new SimpleOdachiItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_SPEAR = ITEMS.register(
      "diamond_spear", () -> new SimpleSpearItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> DIAMOND_SCYTHE = ITEMS.register(
      "diamond_scythe", () -> new SimpleScytheItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> DIAMOND_SICKLE = ITEMS.register(
      "diamond_sickle", () -> new SimpleSickleItem(Tiers.DIAMOND, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSwordItem> LOW_MAGISTEEL_SWORD = ITEMS.register(
      "low_magisteel_sword", () -> new SimpleSwordItem(TensuraToolTiers.LOW_MAGISTEEL, SimpleSwordItem.SwordModifier.NORMAL)
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_SHORT_SWORD = ITEMS.register(
      "low_magisteel_short_sword", () -> new SimpleShortSwordItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_LONG_SWORD = ITEMS.register(
      "low_magisteel_long_sword", () -> new SimpleLongSwordItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_GREAT_SWORD = ITEMS.register(
      "low_magisteel_great_sword", () -> new SimpleGreatSwordItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_KATANA = ITEMS.register(
      "low_magisteel_katana", () -> new SimpleKatanaItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_KODACHI = ITEMS.register(
      "low_magisteel_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_TACHI = ITEMS.register(
      "low_magisteel_tachi", () -> new SimpleTachiItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_ODACHI = ITEMS.register(
      "low_magisteel_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_SPEAR = ITEMS.register(
      "low_magisteel_spear", () -> new SimpleSpearItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_SCYTHE = ITEMS.register(
      "low_magisteel_scythe", () -> new SimpleScytheItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> LOW_MAGISTEEL_PICKAXE = ITEMS.register(
      "low_magisteel_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleAxeItem> LOW_MAGISTEEL_AXE = ITEMS.register(
      "low_magisteel_axe", () -> new SimpleAxeItem(TensuraToolTiers.LOW_MAGISTEEL, SimpleAxeItem.AxeModifier.LOW_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleShovelItem> LOW_MAGISTEEL_SHOVEL = ITEMS.register(
      "low_magisteel_shovel", () -> new SimpleShovelItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleHoeItem> LOW_MAGISTEEL_HOE = ITEMS.register(
      "low_magisteel_hoe", () -> new SimpleHoeItem(TensuraToolTiers.LOW_MAGISTEEL, SimpleHoeItem.HoeModifier.LOW_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleSickleItem> LOW_MAGISTEEL_SICKLE = ITEMS.register(
      "low_magisteel_sickle", () -> new SimpleSickleItem(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<Item> NETHERITE_SHORT_SWORD = ITEMS.register(
      "netherite_short_sword", () -> new SimpleShortSwordItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_LONG_SWORD = ITEMS.register(
      "netherite_long_sword", () -> new SimpleLongSwordItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_GREAT_SWORD = ITEMS.register(
      "netherite_great_sword", () -> new SimpleGreatSwordItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_KATANA = ITEMS.register(
      "netherite_katana", () -> new SimpleKatanaItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_KODACHI = ITEMS.register(
      "netherite_kodachi", () -> new SimpleKodachiItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_TACHI = ITEMS.register(
      "netherite_tachi", () -> new SimpleTachiItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_ODACHI = ITEMS.register(
      "netherite_odachi", () -> new SimpleOdachiItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_SPEAR = ITEMS.register(
      "netherite_spear", () -> new SimpleSpearItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> NETHERITE_SCYTHE = ITEMS.register(
      "netherite_scythe", () -> new SimpleScytheItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimpleSickleItem> NETHERITE_SICKLE = ITEMS.register(
      "netherite_sickle", () -> new SimpleSickleItem(Tiers.NETHERITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> HIGH_MAGISTEEL_SWORD = ITEMS.register(
      "high_magisteel_sword", () -> new SimpleSwordItem(TensuraToolTiers.HIGH_MAGISTEEL, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_SHORT_SWORD = ITEMS.register(
      "high_magisteel_short_sword",
      () -> new SimpleShortSwordItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_LONG_SWORD = ITEMS.register(
      "high_magisteel_long_sword",
      () -> new SimpleLongSwordItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_GREAT_SWORD = ITEMS.register(
      "high_magisteel_great_sword",
      () -> new SimpleGreatSwordItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_KATANA = ITEMS.register(
      "high_magisteel_katana",
      () -> new SimpleKatanaItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_KODACHI = ITEMS.register(
      "high_magisteel_kodachi",
      () -> new SimpleKodachiItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_TACHI = ITEMS.register(
      "high_magisteel_tachi", () -> new SimpleTachiItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_ODACHI = ITEMS.register(
      "high_magisteel_odachi",
      () -> new SimpleOdachiItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_SPEAR = ITEMS.register(
      "high_magisteel_spear", () -> new SimpleSpearItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_SCYTHE = ITEMS.register(
      "high_magisteel_scythe", () -> new SimpleScytheItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> HIGH_MAGISTEEL_PICKAXE = ITEMS.register(
      "high_magisteel_pickaxe",
      () -> new SimplePickaxeItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> HIGH_MAGISTEEL_AXE = ITEMS.register(
      "high_magisteel_axe", () -> new SimpleAxeItem(TensuraToolTiers.HIGH_MAGISTEEL, SimpleAxeItem.AxeModifier.HIGH_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleShovelItem> HIGH_MAGISTEEL_SHOVEL = ITEMS.register(
      "high_magisteel_shovel",
      () -> new SimpleShovelItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> HIGH_MAGISTEEL_HOE = ITEMS.register(
      "high_magisteel_hoe", () -> new SimpleHoeItem(TensuraToolTiers.HIGH_MAGISTEEL, SimpleHoeItem.HoeModifier.HIGH_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleSickleItem> HIGH_MAGISTEEL_SICKLE = ITEMS.register(
      "high_magisteel_sickle",
      () -> new SimpleSickleItem(TensuraToolTiers.HIGH_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> MITHRIL_SWORD = ITEMS.register(
      "mithril_sword", () -> new SimpleSwordItem(TensuraToolTiers.MITHRIL, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> MITHRIL_SHORT_SWORD = ITEMS.register(
      "mithril_short_sword", () -> new SimpleShortSwordItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_LONG_SWORD = ITEMS.register(
      "mithril_long_sword", () -> new SimpleLongSwordItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_GREAT_SWORD = ITEMS.register(
      "mithril_great_sword", () -> new SimpleGreatSwordItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_KATANA = ITEMS.register(
      "mithril_katana", () -> new SimpleKatanaItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_KODACHI = ITEMS.register(
      "mithril_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_TACHI = ITEMS.register(
      "mithril_tachi", () -> new SimpleTachiItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_ODACHI = ITEMS.register(
      "mithril_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_SPEAR = ITEMS.register(
      "mithril_spear", () -> new SimpleSpearItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> MITHRIL_SCYTHE = ITEMS.register(
      "mithril_scythe", () -> new SimpleScytheItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> MITHRIL_PICKAXE = ITEMS.register(
      "mithril_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> MITHRIL_AXE = ITEMS.register(
      "mithril_axe", () -> new SimpleAxeItem(TensuraToolTiers.MITHRIL, SimpleAxeItem.AxeModifier.MITHRIL)
   );
   public static final RegistrySupplier<SimpleShovelItem> MITHRIL_SHOVEL = ITEMS.register(
      "mithril_shovel", () -> new SimpleShovelItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> MITHRIL_HOE = ITEMS.register(
      "mithril_hoe", () -> new SimpleHoeItem(TensuraToolTiers.MITHRIL, SimpleHoeItem.HoeModifier.MITHRIL)
   );
   public static final RegistrySupplier<SimpleSickleItem> MITHRIL_SICKLE = ITEMS.register(
      "mithril_sickle", () -> new SimpleSickleItem(TensuraToolTiers.MITHRIL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> ORICHALCUM_SWORD = ITEMS.register(
      "orichalcum_sword", () -> new SimpleSwordItem(TensuraToolTiers.ORICHALCUM, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> ORICHALCUM_SHORT_SWORD = ITEMS.register(
      "orichalcum_short_sword",
      () -> new SimpleShortSwordItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_LONG_SWORD = ITEMS.register(
      "orichalcum_long_sword", () -> new SimpleLongSwordItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_GREAT_SWORD = ITEMS.register(
      "orichalcum_great_sword",
      () -> new SimpleGreatSwordItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_KATANA = ITEMS.register(
      "orichalcum_katana", () -> new SimpleKatanaItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_KODACHI = ITEMS.register(
      "orichalcum_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_TACHI = ITEMS.register(
      "orichalcum_tachi", () -> new SimpleTachiItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_ODACHI = ITEMS.register(
      "orichalcum_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_SPEAR = ITEMS.register(
      "orichalcum_spear", () -> new SimpleSpearItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORICHALCUM_SCYTHE = ITEMS.register(
      "orichalcum_scythe", () -> new SimpleScytheItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> ORICHALCUM_PICKAXE = ITEMS.register(
      "orichalcum_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> ORICHALCUM_AXE = ITEMS.register(
      "orichalcum_axe", () -> new SimpleAxeItem(TensuraToolTiers.ORICHALCUM, SimpleAxeItem.AxeModifier.ORICHALCUM)
   );
   public static final RegistrySupplier<SimpleShovelItem> ORICHALCUM_SHOVEL = ITEMS.register(
      "orichalcum_shovel", () -> new SimpleShovelItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> ORICHALCUM_HOE = ITEMS.register(
      "orichalcum_hoe", () -> new SimpleHoeItem(TensuraToolTiers.ORICHALCUM, SimpleHoeItem.HoeModifier.ORICHALCUM)
   );
   public static final RegistrySupplier<SimpleSickleItem> ORICHALCUM_SICKLE = ITEMS.register(
      "orichalcum_sickle", () -> new SimpleSickleItem(TensuraToolTiers.ORICHALCUM, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> PURE_MAGISTEEL_SWORD = ITEMS.register(
      "pure_magisteel_sword", () -> new SimpleSwordItem(TensuraToolTiers.PURE_MAGISTEEL, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_SHORT_SWORD = ITEMS.register(
      "pure_magisteel_short_sword",
      () -> new SimpleShortSwordItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_LONG_SWORD = ITEMS.register(
      "pure_magisteel_long_sword",
      () -> new SimpleLongSwordItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_GREAT_SWORD = ITEMS.register(
      "pure_magisteel_great_sword",
      () -> new SimpleGreatSwordItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_KATANA = ITEMS.register(
      "pure_magisteel_katana",
      () -> new SimpleKatanaItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_KODACHI = ITEMS.register(
      "pure_magisteel_kodachi",
      () -> new SimpleKodachiItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_TACHI = ITEMS.register(
      "pure_magisteel_tachi", () -> new SimpleTachiItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_ODACHI = ITEMS.register(
      "pure_magisteel_odachi",
      () -> new SimpleOdachiItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_SPEAR = ITEMS.register(
      "pure_magisteel_spear", () -> new SimpleSpearItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_SCYTHE = ITEMS.register(
      "pure_magisteel_scythe", () -> new SimpleScytheItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> PURE_MAGISTEEL_PICKAXE = ITEMS.register(
      "pure_magisteel_pickaxe",
      () -> new SimplePickaxeItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> PURE_MAGISTEEL_AXE = ITEMS.register(
      "pure_magisteel_axe", () -> new SimpleAxeItem(TensuraToolTiers.PURE_MAGISTEEL, SimpleAxeItem.AxeModifier.PURE_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleShovelItem> PURE_MAGISTEEL_SHOVEL = ITEMS.register(
      "pure_magisteel_shovel",
      () -> new SimpleShovelItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> PURE_MAGISTEEL_HOE = ITEMS.register(
      "pure_magisteel_hoe", () -> new SimpleHoeItem(TensuraToolTiers.PURE_MAGISTEEL, SimpleHoeItem.HoeModifier.PURE_MAGISTEEL)
   );
   public static final RegistrySupplier<SimpleSickleItem> PURE_MAGISTEEL_SICKLE = ITEMS.register(
      "pure_magisteel_sickle",
      () -> new SimpleSickleItem(TensuraToolTiers.PURE_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> ADAMANTITE_SWORD = ITEMS.register(
      "adamantite_sword", () -> new SimpleSwordItem(TensuraToolTiers.ADAMANTITE, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> ADAMANTITE_SHORT_SWORD = ITEMS.register(
      "adamantite_short_sword",
      () -> new SimpleShortSwordItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_LONG_SWORD = ITEMS.register(
      "adamantite_long_sword", () -> new SimpleLongSwordItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_GREAT_SWORD = ITEMS.register(
      "adamantite_great_sword",
      () -> new SimpleGreatSwordItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_KATANA = ITEMS.register(
      "adamantite_katana", () -> new SimpleKatanaItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_KODACHI = ITEMS.register(
      "adamantite_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_TACHI = ITEMS.register(
      "adamantite_tachi", () -> new SimpleTachiItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_ODACHI = ITEMS.register(
      "adamantite_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_SPEAR = ITEMS.register(
      "adamantite_spear", () -> new SimpleSpearItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ADAMANTITE_SCYTHE = ITEMS.register(
      "adamantite_scythe", () -> new SimpleScytheItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> ADAMANTITE_PICKAXE = ITEMS.register(
      "adamantite_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> ADAMANTITE_AXE = ITEMS.register(
      "adamantite_axe", () -> new SimpleAxeItem(TensuraToolTiers.ADAMANTITE, SimpleAxeItem.AxeModifier.ADAMANTITE)
   );
   public static final RegistrySupplier<SimpleShovelItem> ADAMANTITE_SHOVEL = ITEMS.register(
      "adamantite_shovel", () -> new SimpleShovelItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> ADAMANTITE_HOE = ITEMS.register(
      "adamantite_hoe", () -> new SimpleHoeItem(TensuraToolTiers.ADAMANTITE, SimpleHoeItem.HoeModifier.ADAMANTITE)
   );
   public static final RegistrySupplier<SimpleSickleItem> ADAMANTITE_SICKLE = ITEMS.register(
      "adamantite_sickle", () -> new SimpleSickleItem(TensuraToolTiers.ADAMANTITE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleSwordItem> HIHIIROKANE_SWORD = ITEMS.register(
      "hihiirokane_sword", () -> new SimpleSwordItem(TensuraToolTiers.HIHIIROKANE, SimpleSwordItem.SwordModifier.FIRE_RESISTED)
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_SHORT_SWORD = ITEMS.register(
      "hihiirokane_short_sword",
      () -> new SimpleShortSwordItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_LONG_SWORD = ITEMS.register(
      "hihiirokane_long_sword",
      () -> new SimpleLongSwordItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_GREAT_SWORD = ITEMS.register(
      "hihiirokane_great_sword",
      () -> new SimpleGreatSwordItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_KATANA = ITEMS.register(
      "hihiirokane_katana", () -> new SimpleKatanaItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_KODACHI = ITEMS.register(
      "hihiirokane_kodachi", () -> new SimpleKodachiItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_TACHI = ITEMS.register(
      "hihiirokane_tachi", () -> new SimpleTachiItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_ODACHI = ITEMS.register(
      "hihiirokane_odachi", () -> new SimpleOdachiItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_SPEAR = ITEMS.register(
      "hihiirokane_spear", () -> new SimpleSpearItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIHIIROKANE_SCYTHE = ITEMS.register(
      "hihiirokane_scythe", () -> new SimpleScytheItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS))
   );
   public static final RegistrySupplier<SimplePickaxeItem> HIHIIROKANE_PICKAXE = ITEMS.register(
      "hihiirokane_pickaxe", () -> new SimplePickaxeItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleAxeItem> HIHIIROKANE_AXE = ITEMS.register(
      "hihiirokane_axe", () -> new SimpleAxeItem(TensuraToolTiers.HIHIIROKANE, SimpleAxeItem.AxeModifier.HIHIIROKANE)
   );
   public static final RegistrySupplier<SimpleShovelItem> HIHIIROKANE_SHOVEL = ITEMS.register(
      "hihiirokane_shovel", () -> new SimpleShovelItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<SimpleHoeItem> HIHIIROKANE_HOE = ITEMS.register(
      "hihiirokane_hoe", () -> new SimpleHoeItem(TensuraToolTiers.HIHIIROKANE, SimpleHoeItem.HoeModifier.HIHIIROKANE)
   );
   public static final RegistrySupplier<SimpleSickleItem> HIHIIROKANE_SICKLE = ITEMS.register(
      "hihiirokane_sickle", () -> new SimpleSickleItem(TensuraToolTiers.HIHIIROKANE, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORB_OF_DOMINATION = ITEMS.register("orb_of_domination", OrbOfDominationItem::new);
   public static final RegistrySupplier<ArmorsaurusGauntletItem> ARMORSAURUS_GAUNTLET = ITEMS.register("armorsaurus_gauntlet", ArmorsaurusGauntletItem::new);
   public static final RegistrySupplier<DragonKnuckleItem> DRAGON_KNUCKLE = ITEMS.register("dragon_knuckle", DragonKnuckleItem::new);
   public static final RegistrySupplier<Item> SEVERER_BLADE = ITEMS.register(
      "severer_blade", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.GEARS).stacksTo(64))
   );
   public static final RegistrySupplier<SpatialBladeItem> SPATIAL_BLADE = ITEMS.register("spatial_blade", SpatialBladeItem::new);
   public static final RegistrySupplier<IceBladeItem> ICE_BLADE = ITEMS.register("ice_blade", IceBladeItem::new);
   public static final RegistrySupplier<MeatCrusherItem> MEAT_CRUSHER = ITEMS.register("meat_crusher", MeatCrusherItem::new);
   public static final RegistrySupplier<VortexSpearItem> VORTEX_SPEAR = ITEMS.register("vortex_spear", VortexSpearItem::new);
   public static final RegistrySupplier<TensuraSwordItem> DEAD_END_RAINBOW = ITEMS.register(
      "dead_end_rainbow",
      () -> new TensuraSwordItem(
         TensuraToolTiers.PURE_MAGISTEEL, 5, -2.4F, 1.0, -1.0, 0.0, 0.0, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()
      )
   );
   public static final RegistrySupplier<TensuraSwordItem> MOONLIGHT = ITEMS.register(
      "moonlight",
      () -> new TensuraSwordItem(
         TensuraToolTiers.MITHRIL, 19, -2.2F, 1.0, 0.25, 50.0, 0.0, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()
      )
   );
   public static final RegistrySupplier<TensuraSwordItem> RUHK = ITEMS.register(
      "ruhk",
      () -> new TensuraSwordItem(
         TensuraToolTiers.PURE_MAGISTEEL, 21, -2.2F, 1.0, 0.25, 50.0, 0.5, new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()
      )
   );
   public static final RegistrySupplier<Item> WALTHER_P99 = ITEMS.register("walther_p99", WaltherP99Item::new);
   public static final RegistrySupplier<WebGunItem> WEB_GUN = ITEMS.register("web_gun", WebGunItem::new);
   public static final RegistrySupplier<Item> COPPER_SHELL = ITEMS.register(
      "copper_shell", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.GEARS).stacksTo(64))
   );
   public static final RegistrySupplier<Item> WEB_CARTRIDGE = ITEMS.register("web_cartridge", () -> new WebCartridgeItem(100, 100, 0.25, 0, Blocks.COBWEB));
   public static final RegistrySupplier<Item> STICKY_WEB_CARTRIDGE = ITEMS.register(
      "sticky_web_cartridge", () -> new WebCartridgeItem(200, 200, 0.25, 160, (Block)TensuraBlocks.STICKY_COBWEB.get())
   );
   public static final RegistrySupplier<Item> STICKY_STEEL_WEB_CARTRIDGE = ITEMS.register(
      "sticky_steel_web_cartridge", () -> new WebCartridgeItem(200, 200, 0.25, 240, (Block)TensuraBlocks.STICKY_STEEL_COBWEB.get())
   );

   public static void init() {
      ITEMS.register();
   }
}
