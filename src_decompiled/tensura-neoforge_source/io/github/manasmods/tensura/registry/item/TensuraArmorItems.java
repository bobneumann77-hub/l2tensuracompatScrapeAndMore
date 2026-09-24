package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.item.armor.SimpleBootsItem;
import io.github.manasmods.tensura.item.armor.SimpleChestplateItem;
import io.github.manasmods.tensura.item.armor.SimpleDyableArmorItem;
import io.github.manasmods.tensura.item.armor.SimpleHelmetItem;
import io.github.manasmods.tensura.item.armor.SimpleLeggingsItem;
import io.github.manasmods.tensura.item.armor.custom.AdamantiteArmorItem;
import io.github.manasmods.tensura.item.armor.custom.AntCarapaceArmorItem;
import io.github.manasmods.tensura.item.armor.custom.AntiMagicMaskItem;
import io.github.manasmods.tensura.item.armor.custom.ArmorsaurusArmorItem;
import io.github.manasmods.tensura.item.armor.custom.ArmorsaurusScalemailArmorItem;
import io.github.manasmods.tensura.item.armor.custom.BatGliderItem;
import io.github.manasmods.tensura.item.armor.custom.CharybdisScalemailItem;
import io.github.manasmods.tensura.item.armor.custom.DarkSetArmorItem;
import io.github.manasmods.tensura.item.armor.custom.HighMagisteelArmorItem;
import io.github.manasmods.tensura.item.armor.custom.HihiirokaneArmorItem;
import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import io.github.manasmods.tensura.item.armor.custom.KnightSpiderCarapaceArmorItem;
import io.github.manasmods.tensura.item.armor.custom.LowMagisteelArmorItem;
import io.github.manasmods.tensura.item.armor.custom.MithrilArmorItem;
import io.github.manasmods.tensura.item.armor.custom.MonsterLeatherSpecialAHelmetItem;
import io.github.manasmods.tensura.item.armor.custom.OrichalcumArmorItem;
import io.github.manasmods.tensura.item.armor.custom.PierrotMaskItem;
import io.github.manasmods.tensura.item.armor.custom.PureMagisteelArmorItem;
import io.github.manasmods.tensura.item.armor.custom.SerpentScalemailArmorItem;
import io.github.manasmods.tensura.item.armor.custom.WingedShoesItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class TensuraArmorItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<Item> MONSTER_LEATHER_D_HELMET = ITEMS.register(
      "monster_leather_helmet_d",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_D, Type.HELMET, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 11)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_D_CHESTPLATE = ITEMS.register(
      "monster_leather_chestplate_d",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_D, Type.CHESTPLATE, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 11)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_D_LEGGINGS = ITEMS.register(
      "monster_leather_leggings_d",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_D, Type.LEGGINGS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 11)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_D_BOOTS = ITEMS.register(
      "monster_leather_boots_d",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_D, Type.BOOTS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 11)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_C_HELMET = ITEMS.register(
      "monster_leather_helmet_c",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_C, Type.HELMET, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 33)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_C_CHESTPLATE = ITEMS.register(
      "monster_leather_chestplate_c",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_C, Type.CHESTPLATE, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 33)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_C_LEGGINGS = ITEMS.register(
      "monster_leather_leggings_c",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_C, Type.LEGGINGS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 33)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_C_BOOTS = ITEMS.register(
      "monster_leather_boots_c",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_C, Type.BOOTS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 33)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_B_HELMET = ITEMS.register(
      "monster_leather_helmet_b",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_B, Type.HELMET, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 35)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_B_CHESTPLATE = ITEMS.register(
      "monster_leather_chestplate_b",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_B, Type.CHESTPLATE, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 35)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_B_LEGGINGS = ITEMS.register(
      "monster_leather_leggings_b",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_B, Type.LEGGINGS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 35)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_B_BOOTS = ITEMS.register(
      "monster_leather_boots_b",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_B, Type.BOOTS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 35)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_A_HELMET = ITEMS.register(
      "monster_leather_helmet_a",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_A, Type.HELMET, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 36)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_A_CHESTPLATE = ITEMS.register(
      "monster_leather_chestplate_a",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_A, Type.CHESTPLATE, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 36)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_A_LEGGINGS = ITEMS.register(
      "monster_leather_leggings_a",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_A, Type.LEGGINGS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 36)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_A_BOOTS = ITEMS.register(
      "monster_leather_boots_a",
      () -> new SimpleDyableArmorItem(TensuraArmorMaterials.MONSTER_LEATHER_A, Type.BOOTS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 36)
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_SPECIAL_A_HELMET = ITEMS.register(
      "monster_leather_helmet_special_a",
      () -> new MonsterLeatherSpecialAHelmetItem(
         TensuraArmorMaterials.MONSTER_LEATHER_SPECIAL_A, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant()
      )
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_SPECIAL_A_CHESTPLATE = ITEMS.register(
      "monster_leather_chestplate_special_a",
      () -> new SimpleDyableArmorItem(
         TensuraArmorMaterials.MONSTER_LEATHER_SPECIAL_A, Type.CHESTPLATE, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant(), 75
      )
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_SPECIAL_A_LEGGINGS = ITEMS.register(
      "monster_leather_leggings_special_a",
      () -> new SimpleDyableArmorItem(
         TensuraArmorMaterials.MONSTER_LEATHER_SPECIAL_A, Type.LEGGINGS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant(), 75
      )
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_SPECIAL_A_BOOTS = ITEMS.register(
      "monster_leather_boots_special_a",
      () -> new SimpleDyableArmorItem(
         TensuraArmorMaterials.MONSTER_LEATHER_SPECIAL_A, Type.BOOTS, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant(), 75
      )
   );
   public static final RegistrySupplier<Item> BAT_GLIDER = ITEMS.register("bat_glider", BatGliderItem::new);
   public static final RegistrySupplier<Item> WINGED_SHOES = ITEMS.register("winged_shoes", WingedShoesItem::new);
   public static final RegistrySupplier<Item> SILVER_HELMET = ITEMS.register(
      "silver_helmet", () -> new SimpleHelmetItem(TensuraArmorMaterials.SILVER, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 15)
   );
   public static final RegistrySupplier<Item> SILVER_CHESTPLATE = ITEMS.register(
      "silver_chestplate", () -> new SimpleChestplateItem(TensuraArmorMaterials.SILVER, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 15)
   );
   public static final RegistrySupplier<Item> SILVER_LEGGINGS = ITEMS.register(
      "silver_leggings", () -> new SimpleLeggingsItem(TensuraArmorMaterials.SILVER, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 15)
   );
   public static final RegistrySupplier<Item> SILVER_BOOTS = ITEMS.register(
      "silver_boots", () -> new SimpleBootsItem(TensuraArmorMaterials.SILVER, new Properties().arch$tab(TensuraCreativeTabs.ARMOR), 15)
   );
   public static final RegistrySupplier<Item> ANT_CARAPACE_HELMET = ITEMS.register("ant_carapace_helmet", () -> new AntCarapaceArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> ANT_CARAPACE_CHESTPLATE = ITEMS.register(
      "ant_carapace_chestplate", () -> new AntCarapaceArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> ANT_CARAPACE_LEGGINGS = ITEMS.register("ant_carapace_leggings", () -> new AntCarapaceArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> ANT_CARAPACE_BOOTS = ITEMS.register("ant_carapace_boots", () -> new AntCarapaceArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> SERPENT_SCALEMAIL_HELMET = ITEMS.register(
      "serpent_scalemail_helmet", () -> new SerpentScalemailArmorItem(Type.HELMET)
   );
   public static final RegistrySupplier<Item> SERPENT_SCALEMAIL_CHESTPLATE = ITEMS.register(
      "serpent_scalemail_chestplate", () -> new SerpentScalemailArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> SERPENT_SCALEMAIL_LEGGINGS = ITEMS.register(
      "serpent_scalemail_leggings", () -> new SerpentScalemailArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> SERPENT_SCALEMAIL_BOOTS = ITEMS.register(
      "serpent_scalemail_boots", () -> new SerpentScalemailArmorItem(Type.BOOTS)
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_CARAPACE_HELMET = ITEMS.register(
      "knight_spider_carapace_helmet", () -> new KnightSpiderCarapaceArmorItem(Type.HELMET)
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_CARAPACE_CHESTPLATE = ITEMS.register(
      "knight_spider_carapace_chestplate", () -> new KnightSpiderCarapaceArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_CARAPACE_LEGGINGS = ITEMS.register(
      "knight_spider_carapace_leggings", () -> new KnightSpiderCarapaceArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_CARAPACE_BOOTS = ITEMS.register(
      "knight_spider_carapace_boots", () -> new KnightSpiderCarapaceArmorItem(Type.BOOTS)
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_HELMET = ITEMS.register("low_magisteel_helmet", () -> new LowMagisteelArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_CHESTPLATE = ITEMS.register(
      "low_magisteel_chestplate", () -> new LowMagisteelArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_LEGGINGS = ITEMS.register("low_magisteel_leggings", () -> new LowMagisteelArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> LOW_MAGISTEEL_BOOTS = ITEMS.register("low_magisteel_boots", () -> new LowMagisteelArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ARMORSAURUS_HELMET = ITEMS.register("armorsaurus_helmet", () -> new ArmorsaurusArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> ARMORSAURUS_CHESTPLATE = ITEMS.register("armorsaurus_chestplate", () -> new ArmorsaurusArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> ARMORSAURUS_LEGGINGS = ITEMS.register("armorsaurus_leggings", () -> new ArmorsaurusArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> ARMORSAURUS_BOOTS = ITEMS.register("armorsaurus_boots", () -> new ArmorsaurusArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ARMORSAURUS_SCALEMAIL_HELMET = ITEMS.register(
      "armorsaurus_scalemail_helmet", () -> new ArmorsaurusScalemailArmorItem(Type.HELMET)
   );
   public static final RegistrySupplier<Item> ARMORSAURUS_SCALEMAIL_CHESTPLATE = ITEMS.register(
      "armorsaurus_scalemail_chestplate", () -> new ArmorsaurusScalemailArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> ARMORSAURUS_SCALEMAIL_LEGGINGS = ITEMS.register(
      "armorsaurus_scalemail_leggings", () -> new ArmorsaurusScalemailArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> ARMORSAURUS_SCALEMAIL_BOOTS = ITEMS.register(
      "armorsaurus_scalemail_boots", () -> new ArmorsaurusScalemailArmorItem(Type.BOOTS)
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_HELMET = ITEMS.register("high_magisteel_helmet", () -> new HighMagisteelArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_CHESTPLATE = ITEMS.register(
      "high_magisteel_chestplate", () -> new HighMagisteelArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_LEGGINGS = ITEMS.register(
      "high_magisteel_leggings", () -> new HighMagisteelArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> HIGH_MAGISTEEL_BOOTS = ITEMS.register("high_magisteel_boots", () -> new HighMagisteelArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> CHARYBDIS_SCALEMAIL_HELMET = ITEMS.register(
      "charybdis_scalemail_helmet", () -> new CharybdisScalemailItem(Type.HELMET)
   );
   public static final RegistrySupplier<Item> CHARYBDIS_SCALEMAIL_CHESTPLATE = ITEMS.register(
      "charybdis_scalemail_chestplate", () -> new CharybdisScalemailItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> CHARYBDIS_SCALEMAIL_LEGGINGS = ITEMS.register(
      "charybdis_scalemail_leggings", () -> new CharybdisScalemailItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> CHARYBDIS_SCALEMAIL_BOOTS = ITEMS.register(
      "charybdis_scalemail_boots", () -> new CharybdisScalemailItem(Type.BOOTS)
   );
   public static final RegistrySupplier<Item> MITHRIL_HELMET = ITEMS.register("mithril_helmet", () -> new MithrilArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> MITHRIL_CHESTPLATE = ITEMS.register("mithril_chestplate", () -> new MithrilArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> MITHRIL_LEGGINGS = ITEMS.register("mithril_leggings", () -> new MithrilArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> MITHRIL_BOOTS = ITEMS.register("mithril_boots", () -> new MithrilArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ORICHALCUM_HELMET = ITEMS.register("orichalcum_helmet", () -> new OrichalcumArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> ORICHALCUM_CHESTPLATE = ITEMS.register("orichalcum_chestplate", () -> new OrichalcumArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> ORICHALCUM_LEGGINGS = ITEMS.register("orichalcum_leggings", () -> new OrichalcumArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> ORICHALCUM_BOOTS = ITEMS.register("orichalcum_boots", () -> new OrichalcumArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_HELMET = ITEMS.register("pure_magisteel_helmet", () -> new PureMagisteelArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_CHESTPLATE = ITEMS.register(
      "pure_magisteel_chestplate", () -> new PureMagisteelArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_LEGGINGS = ITEMS.register(
      "pure_magisteel_leggings", () -> new PureMagisteelArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> PURE_MAGISTEEL_BOOTS = ITEMS.register("pure_magisteel_boots", () -> new PureMagisteelArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ADAMANTITE_HELMET = ITEMS.register("adamantite_helmet", () -> new AdamantiteArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> ADAMANTITE_CHESTPLATE = ITEMS.register("adamantite_chestplate", () -> new AdamantiteArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> ADAMANTITE_LEGGINGS = ITEMS.register("adamantite_leggings", () -> new AdamantiteArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> ADAMANTITE_BOOTS = ITEMS.register("adamantite_boots", () -> new AdamantiteArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> HIHIIROKANE_HELMET = ITEMS.register("hihiirokane_helmet", () -> new HihiirokaneArmorItem(Type.HELMET));
   public static final RegistrySupplier<Item> HIHIIROKANE_CHESTPLATE = ITEMS.register("hihiirokane_chestplate", () -> new HihiirokaneArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> HIHIIROKANE_LEGGINGS = ITEMS.register("hihiirokane_leggings", () -> new HihiirokaneArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> HIHIIROKANE_BOOTS = ITEMS.register("hihiirokane_boots", () -> new HihiirokaneArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> HOLY_ARMAMENTS_CHESTPLATE = ITEMS.register(
      "holy_armaments_chestplate", () -> new HolyArmamentsArmorItem(Type.CHESTPLATE)
   );
   public static final RegistrySupplier<Item> HOLY_ARMAMENTS_LEGGINGS = ITEMS.register(
      "holy_armaments_leggings", () -> new HolyArmamentsArmorItem(Type.LEGGINGS)
   );
   public static final RegistrySupplier<Item> HOLY_ARMAMENTS_BOOTS = ITEMS.register("holy_armaments_boots", () -> new HolyArmamentsArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ANTI_MAGIC_MASK = ITEMS.register("anti_magic_mask", AntiMagicMaskItem::new);
   public static final RegistrySupplier<Item> DARK_JACKET = ITEMS.register("dark_jacket", () -> new DarkSetArmorItem(Type.CHESTPLATE));
   public static final RegistrySupplier<Item> DARK_LEGGINGS = ITEMS.register("dark_leggings", () -> new DarkSetArmorItem(Type.LEGGINGS));
   public static final RegistrySupplier<Item> DARK_BOOTS = ITEMS.register("dark_boots", () -> new DarkSetArmorItem(Type.BOOTS));
   public static final RegistrySupplier<Item> ANGRY_PIERROT_MASK = ITEMS.register(
      "angry_pierrot_mask", () -> new PierrotMaskItem(PierrotMaskItem.MaskType.ANGRY)
   );
   public static final RegistrySupplier<Item> CRAZY_PIERROT_MASK = ITEMS.register(
      "crazy_pierrot_mask", () -> new PierrotMaskItem(PierrotMaskItem.MaskType.CRAZY)
   );
   public static final RegistrySupplier<Item> TEARY_PIERROT_MASK = ITEMS.register(
      "teary_pierrot_mask", () -> new PierrotMaskItem(PierrotMaskItem.MaskType.TEARY)
   );
   public static final RegistrySupplier<Item> WONDER_PIERROT_MASK = ITEMS.register(
      "wonder_pierrot_mask", () -> new PierrotMaskItem(PierrotMaskItem.MaskType.WONDER)
   );

   public static void init() {
      ITEMS.register();
   }
}
