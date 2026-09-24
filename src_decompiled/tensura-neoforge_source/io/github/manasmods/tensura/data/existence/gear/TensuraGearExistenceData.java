package io.github.manasmods.tensura.data.existence.gear;

import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class TensuraGearExistenceData {
   public static void bootstrap(BootstrapContext<GearExistenceData> context) {
      monsterLeatherD(context);
      monsterLeatherC(context);
      monsterLeatherB(context);
      monsterLeatherA(context);
      monsterLeatherSpecialA(context);
      silver(context);
      giantAntCarapace(context);
      lowMagiSteel(context);
      knightSpiderCarapace(context);
      serpentScalemail(context);
      armorsaurusScale(context);
      highMagiSteel(context);
      mithril(context);
      orichalcum(context);
      pureMagiSteel(context);
      charybdisScale(context);
      adamantite(context);
      hihiirokane(context);
      uniqueGears(context);
      uniqueArmors(context);
   }

   public static void register(BootstrapContext<GearExistenceData> context, GearExistenceData data) {
      ResourceKey<GearExistenceData> key = ResourceKey.create(TensuraCustomData.GEAR_EXISTENCE, data.gear());
      context.register(key, data);
   }

   protected static void uniqueGears(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      register(context, GearExistenceData.getDefault(TensuraToolItems.KANABO.getId(), 6000, 0.01, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_KUNAI.getId(), 52000, 0.025, UniqueGearEvolutionHelper.getPureMagisteelWeapons())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.DRAGON_KNUCKLE.getId(), 52000, 0.025));
      Map<Holder<Enchantment>, Integer> deadEndRainbowEngrave = Map.of(
         enchantment.getOrThrow(TensuraEnchantments.DEAD_END_RAINBOW), 1, enchantment.getOrThrow(TensuraEnchantments.INTANGIBILITY), 1
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.DEAD_END_RAINBOW.getId(), 45000, 0.025, deadEndRainbowEngrave, UniqueGearEvolutionHelper.getPureMagisteelWeapons()
         )
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ICE_BLADE.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.MEAT_CRUSHER.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons()));
      Map<Holder<Enchantment>, Integer> moonlightEngrave = Map.of(
         enchantment.getOrThrow(TensuraEnchantments.BARRIER_PIERCING),
         10,
         enchantment.getOrThrow(TensuraEnchantments.HOLY_COAT),
         2,
         enchantment.getOrThrow(TensuraEnchantments.SEVERANCE),
         1
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MOONLIGHT.getId(), 60000, 0.025, moonlightEngrave, UniqueGearEvolutionHelper.getPureMagisteelWeapons())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.RUHK.getId(), 70000, 0.025, UniqueGearEvolutionHelper.getPureMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SPATIAL_BLADE.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.VORTEX_SPEAR.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.WEB_GUN.getId(), 6000, 0.01, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.SISSIE_TOOTH_PICKAXE.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.CENTIPEDE_DAGGER.getId(), 6000, 0.01, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SPIDER_DAGGER.getId(), 6000, 0.01, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.BEAST_HORN_SPEAR.getId(), 6000, 0.01, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.UNICORN_HORN_SPEAR.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.BLADE_TIGER_SCYTHE.getId(), 18000, 0.02, UniqueGearEvolutionHelper.getHighMagisteelWeapons())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGIC_STAFF.getId(), 6000, 18000, 0.01, TensuraToolItems.MEDIUM_MAGIC_STAFF.getId()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.MEDIUM_MAGIC_STAFF.getId(), 18000, 52000, 0.02, TensuraToolItems.HIGH_MAGIC_STAFF.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGIC_STAFF.getId(), 52000, 0.03));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SLIME_STAFF.getId(), 18000, 0.025, UniqueGearEvolutionHelper.getHighMagisteelWeapons()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.GRIMOIRE_D.getId(), 1000, 2500, 0.0025, TensuraToolItems.GRIMOIRE_C.getId()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.GRIMOIRE_C.getId(), 2500, 5000, 0.005, TensuraToolItems.GRIMOIRE_B.getId()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.GRIMOIRE_B.getId(), 5000, 8000, 0.01, TensuraToolItems.GRIMOIRE_A.getId()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.GRIMOIRE_A.getId(), 8000, 80000, 0.015, TensuraToolItems.GRIMOIRE_SPECIAL_A.getId()));
      register(context, GearExistenceData.getDefault(TensuraToolItems.GRIMOIRE_SPECIAL_A.getId(), 80000, 0.025));
   }

   protected static void uniqueArmors(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      register(context, GearExistenceData.getDefault(TensuraArmorItems.WINGED_SHOES.getId(), 4000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors()));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.BAT_GLIDER.getId(), 2500, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors()));
      Map<Holder<Enchantment>, Integer> holyArmamentsEngrave = Map.of(
         enchantment.getOrThrow(TensuraEnchantments.ELEMENTAL_BOOST), 2, enchantment.getOrThrow(TensuraEnchantments.ELEMENTAL_RESISTANCE), 2
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS.getId(), 50000, 0.025, holyArmamentsEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE.getId(), 50000, 0.025, holyArmamentsEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.HOLY_ARMAMENTS_BOOTS.getId(), 50000, 0.025, holyArmamentsEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(context, GearExistenceData.getDefault(TensuraArmorItems.DARK_BOOTS.getId(), 5000, 0.01, UniqueGearEvolutionHelper.getHighMagisteelArmors()));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.DARK_LEGGINGS.getId(), 5000, 0.01, UniqueGearEvolutionHelper.getHighMagisteelArmors()));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.DARK_JACKET.getId(), 5000, 0.01, UniqueGearEvolutionHelper.getHighMagisteelArmors()));
      Map<Holder<Enchantment>, Integer> antiMagicEngrave = Map.of(
         enchantment.getOrThrow(TensuraEnchantments.BREATHING_SUPPORT), 1, enchantment.getOrThrow(TensuraEnchantments.ELEMENTAL_RESISTANCE), 1
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.ANTI_MAGIC_MASK.getId(), 10400, 0.05, antiMagicEngrave, UniqueGearEvolutionHelper.getHighMagisteelArmors()
         )
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ANGRY_PIERROT_MASK.getId(), 10000, 0.04, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.CRAZY_PIERROT_MASK.getId(), 10000, 0.04, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.TEARY_PIERROT_MASK.getId(), 10000, 0.04, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.WONDER_PIERROT_MASK.getId(), 10000, 0.04, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraMobDropItems.ORC_DISASTER_HEAD.getId(), 10000, 0.03, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
   }

   protected static void monsterLeatherD(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.getId(), 1000, 2500, 0.0025, TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.getId(), 1000, 2500, 0.0025, TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.getId(), 1000, 2500, 0.0025, TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_D_HELMET.getId(), 1000, 2500, 0.0025, TensuraArmorItems.MONSTER_LEATHER_C_HELMET.getId()
         )
      );
      register(context, GearExistenceData.getDefault(TensuraMaterialItems.POUCH_D.getId(), 1000, 2500, 0.001, TensuraMaterialItems.POUCH_C.getId()));
   }

   protected static void monsterLeatherC(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.getId(), 2500, 5000, 0.005, TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.getId(), 2500, 5000, 0.005, TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.getId(), 2500, 5000, 0.005, TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_C_HELMET.getId(), 2500, 5000, 0.005, TensuraArmorItems.MONSTER_LEATHER_B_HELMET.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraMaterialItems.POUCH_C.getId(), 2500, 5000, 0.002, TensuraMaterialItems.POUCH_B.getId()));
   }

   protected static void monsterLeatherB(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.getId(), 5000, 8000, 0.01, TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.getId(), 5000, 8000, 0.01, TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.getId(), 5000, 8000, 0.01, TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_B_HELMET.getId(), 5000, 8000, 0.01, TensuraArmorItems.MONSTER_LEATHER_A_HELMET.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraMaterialItems.POUCH_B.getId(), 5000, 8000, 0.004, TensuraMaterialItems.POUCH_A.getId()));
   }

   protected static void monsterLeatherA(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.getId(), 8000, 80000, 0.015, TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS.getId(), 8000, 80000, 0.015, TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE.getId(), 8000, 80000, 0.015, TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MONSTER_LEATHER_A_HELMET.getId(), 8000, 80000, 0.015, TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.getId()
         )
      );
      register(context, GearExistenceData.getDefault(TensuraMaterialItems.POUCH_A.getId(), 8000, 80000, 0.006, TensuraMaterialItems.POUCH_SPECIAL_A.getId()));
   }

   protected static void monsterLeatherSpecialA(BootstrapContext<GearExistenceData> context) {
      register(context, GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.getId(), 80000, 0.025));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS.getId(), 80000, 0.025));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE.getId(), 80000, 0.025));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.getId(), 80000, 0.025));
      register(context, GearExistenceData.getDefault(TensuraMaterialItems.POUCH_SPECIAL_A.getId(), 80000, 0.01));
   }

   protected static void giantAntCarapace(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ANT_CARAPACE_BOOTS.getId(), 3000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ANT_CARAPACE_LEGGINGS.getId(), 3000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ANT_CARAPACE_CHESTPLATE.getId(), 3000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ANT_CARAPACE_HELMET.getId(), 3000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ANT_CROSSBOW.getId(), 3000, 0.005, UniqueGearEvolutionHelper.getLowMagisteelWeapons()));
   }

   protected static void silver(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      Map<Holder<Enchantment>, Integer> silverEngrave = Map.of(enchantment.getOrThrow(TensuraEnchantments.HOLY_COAT), 1);
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SWORD.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SHORT_SWORD.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_LONG_SWORD.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_GREAT_SWORD.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_KATANA.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_KODACHI.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_TACHI.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_ODACHI.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SPEAR.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SCYTHE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_AXE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_PICKAXE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SHOVEL.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_HOE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SILVER_SICKLE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.SILVER_BOOTS.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.SILVER_LEGGINGS.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.SILVER_CHESTPLATE.getId(), silverEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.SILVER_HELMET.getId(), silverEngrave));
   }

   protected static void lowMagiSteel(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_SWORD.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_KATANA.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_KATANA.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_KODACHI.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_KODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_TACHI.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_TACHI.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_ODACHI.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_ODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_SPEAR.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SPEAR.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_SCYTHE.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SCYTHE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_AXE.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_AXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_PICKAXE.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_PICKAXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_SHOVEL.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SHOVEL.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_HOE.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_HOE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.LOW_MAGISTEEL_SICKLE.getId(), 6000, 18000, 0.01, TensuraToolItems.HIGH_MAGISTEEL_SICKLE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.LOW_MAGISTEEL_BOOTS.getId(), 6000, 18000, 0.01, TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.getId(), 6000, 18000, 0.01, TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.getId(), 6000, 18000, 0.01, TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.LOW_MAGISTEEL_HELMET.getId(), 6000, 18000, 0.01, TensuraArmorItems.HIGH_MAGISTEEL_HELMET.getId())
      );
   }

   protected static void knightSpiderCarapace(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.SHORT_SPIDER_BOW.getId(), 9000, 0.015));
      register(context, GearExistenceData.getDefault(TensuraToolItems.SPIDER_BOW.getId(), 9000, 0.015));
      register(context, GearExistenceData.getDefault(TensuraToolItems.LONG_SPIDER_BOW.getId(), 9000, 0.015));
      register(context, GearExistenceData.getDefault(TensuraToolItems.WAR_SPIDER_BOW.getId(), 9000, 0.015));
   }

   protected static void serpentScalemail(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.SERPENT_SCALEMAIL_HELMET.getId(), 9000, 0.015, UniqueGearEvolutionHelper.getLowMagisteelArmors())
      );
   }

   protected static void armorsaurusScale(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ARMORSAURUS_GAUNTLET.getId(), 4800, 0.015, UniqueGearEvolutionHelper.getHighMagisteelWeapons())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ARMORSAURUS_SHIELD.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelWeapons())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_BOOTS.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_LEGGINGS.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_CHESTPLATE.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_HELMET.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET.getId(), 6000, 0.015, UniqueGearEvolutionHelper.getHighMagisteelArmors())
      );
   }

   protected static void highMagiSteel(BootstrapContext<GearExistenceData> context) {
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_SWORD.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_KATANA.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_KATANA.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_KODACHI.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_KODACHI.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_TACHI.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_TACHI.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_ODACHI.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_ODACHI.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_SPEAR.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SPEAR.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_SCYTHE.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SCYTHE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_AXE.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_AXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_PICKAXE.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_PICKAXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_SHOVEL.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SHOVEL.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_HOE.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_HOE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.HIGH_MAGISTEEL_SICKLE.getId(), 18000, 52000, 0.02, TensuraToolItems.PURE_MAGISTEEL_SICKLE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.getId(), 18000, 52000, 0.02, TensuraArmorItems.PURE_MAGISTEEL_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.getId(), 18000, 52000, 0.02, TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.getId(), 18000, 52000, 0.02, TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.HIGH_MAGISTEEL_HELMET.getId(), 18000, 52000, 0.02, TensuraArmorItems.PURE_MAGISTEEL_HELMET.getId())
      );
   }

   protected static void mithril(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      Map<Holder<Enchantment>, Integer> mithrilEngrave = Map.of(enchantment.getOrThrow(TensuraEnchantments.HOLY_COAT), 2);
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_SWORD.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SWORD.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.MITHRIL_SHORT_SWORD.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SHORT_SWORD.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.MITHRIL_LONG_SWORD.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_LONG_SWORD.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.MITHRIL_GREAT_SWORD.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_GREAT_SWORD.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_KATANA.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_KATANA.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.MITHRIL_KODACHI.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_KODACHI.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_TACHI.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_TACHI.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_ODACHI.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_ODACHI.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_SPEAR.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SPEAR.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_SCYTHE.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SCYTHE.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_AXE.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_AXE.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.MITHRIL_PICKAXE.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_PICKAXE.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_SHOVEL.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SHOVEL.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_HOE.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_HOE.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.MITHRIL_SICKLE.getId(), 45000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SICKLE.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.MITHRIL_BOOTS.getId(), 45000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_BOOTS.getId(), mithrilEngrave)
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MITHRIL_LEGGINGS.getId(), 45000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_LEGGINGS.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MITHRIL_CHESTPLATE.getId(), 45000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_CHESTPLATE.getId(), mithrilEngrave
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.MITHRIL_HELMET.getId(), 45000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_HELMET.getId(), mithrilEngrave
         )
      );
   }

   protected static void orichalcum(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SWORD.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SHORT_SWORD.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SHORT_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_LONG_SWORD.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_LONG_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_GREAT_SWORD.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_GREAT_SWORD.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_KATANA.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_KATANA.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_KODACHI.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_KODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_TACHI.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_TACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_ODACHI.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_ODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SPEAR.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SPEAR.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SCYTHE.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SCYTHE.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_AXE.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_AXE.getId()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_PICKAXE.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_PICKAXE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SHOVEL.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SHOVEL.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_HOE.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_HOE.getId()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ORICHALCUM_SICKLE.getId(), 50000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SICKLE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ORICHALCUM_BOOTS.getId(), 50000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ORICHALCUM_LEGGINGS.getId(), 50000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_LEGGINGS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ORICHALCUM_CHESTPLATE.getId(), 50000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_CHESTPLATE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ORICHALCUM_HELMET.getId(), 50000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_HELMET.getId())
      );
   }

   protected static void pureMagiSteel(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_SWORD.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SHORT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_LONG_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_GREAT_SWORD.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_KATANA.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_KATANA.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_KODACHI.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_KODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_TACHI.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_TACHI.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_ODACHI.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_ODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_SPEAR.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SPEAR.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_SCYTHE.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SCYTHE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_AXE.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_AXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_PICKAXE.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_PICKAXE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_SHOVEL.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SHOVEL.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_HOE.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_HOE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.PURE_MAGISTEEL_SICKLE.getId(), 52000, 225000, 0.025, TensuraToolItems.ADAMANTITE_SICKLE.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.PURE_MAGISTEEL_BOOTS.getId(), 52000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.getId(), 52000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_LEGGINGS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.getId(), 52000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_CHESTPLATE.getId()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.PURE_MAGISTEEL_HELMET.getId(), 52000, 225000, 0.025, TensuraArmorItems.ADAMANTITE_HELMET.getId())
      );
   }

   protected static void charybdisScale(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      Map<Holder<Enchantment>, Integer> charybdisEngrave = Map.of(enchantment.getOrThrow(TensuraEnchantments.MAGIC_INTERFERENCE), 1);
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.CHARYBDIS_SCALEMAIL_BOOTS.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.CHARYBDIS_SCALEMAIL_LEGGINGS.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.CHARYBDIS_SCALEMAIL_CHESTPLATE.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraArmorItems.CHARYBDIS_SCALEMAIL_HELMET.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelArmors()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.TEMPEST_SCALE_KNIFE.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelWeapons()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.TEMPEST_SCALE_SWORD.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelWeapons()
         )
      );
      register(
         context,
         GearExistenceData.getDefault(
            TensuraToolItems.TEMPEST_SCALE_SHIELD.getId(), 60000, 0.025, charybdisEngrave, UniqueGearEvolutionHelper.getPureMagisteelWeapons()
         )
      );
   }

   protected static void adamantite(BootstrapContext<GearExistenceData> context) {
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SWORD.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SHORT_SWORD.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SHORT_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_LONG_SWORD.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_LONG_SWORD.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_GREAT_SWORD.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_GREAT_SWORD.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_KATANA.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_KATANA.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_KODACHI.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_KODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_TACHI.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_TACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_ODACHI.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_ODACHI.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SPEAR.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SPEAR.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SCYTHE.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SCYTHE.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_AXE.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_AXE.getId()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_PICKAXE.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_PICKAXE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SHOVEL.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SHOVEL.getId())
      );
      register(context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_HOE.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_HOE.getId()));
      register(
         context, GearExistenceData.getDefault(TensuraToolItems.ADAMANTITE_SICKLE.getId(), 225000, 750000, 0.03, TensuraToolItems.HIHIIROKANE_SICKLE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ADAMANTITE_BOOTS.getId(), 225000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_BOOTS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ADAMANTITE_LEGGINGS.getId(), 225000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_LEGGINGS.getId())
      );
      register(
         context,
         GearExistenceData.getDefault(TensuraArmorItems.ADAMANTITE_CHESTPLATE.getId(), 225000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_CHESTPLATE.getId())
      );
      register(
         context, GearExistenceData.getDefault(TensuraArmorItems.ADAMANTITE_HELMET.getId(), 225000, 750000, 0.03, TensuraArmorItems.HIHIIROKANE_HELMET.getId())
      );
   }

   protected static void hihiirokane(BootstrapContext<GearExistenceData> context) {
      HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
      Map<Holder<Enchantment>, Integer> hihiirokaneEngrave = Map.of(enchantment.getOrThrow(TensuraEnchantments.TSUKUMOGAMI), 1);
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SWORD.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SHORT_SWORD.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_LONG_SWORD.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_GREAT_SWORD.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_KATANA.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_KODACHI.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_TACHI.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_ODACHI.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SPEAR.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SCYTHE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_AXE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_PICKAXE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SHOVEL.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_HOE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraToolItems.HIHIIROKANE_SICKLE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.HIHIIROKANE_BOOTS.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.HIHIIROKANE_LEGGINGS.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.HIHIIROKANE_CHESTPLATE.getId(), 750000, 0.04, hihiirokaneEngrave));
      register(context, GearExistenceData.getDefault(TensuraArmorItems.HIHIIROKANE_HELMET.getId(), 750000, 0.04, hihiirokaneEngrave));
   }
}
