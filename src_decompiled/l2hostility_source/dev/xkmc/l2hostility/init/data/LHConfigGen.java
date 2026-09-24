package dev.xkmc.l2hostility.init.data;

import dev.xkmc.l2archery.init.L2Archery;
import dev.xkmc.l2archery.init.registrate.ArcheryItems;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.l2core.serial.config.ConfigDataProvider.Collector;
import dev.xkmc.l2hostility.compat.data.BoMDData;
import dev.xkmc.l2hostility.compat.data.CataclysmData;
import dev.xkmc.l2hostility.compat.data.MutantMonsterData;
import dev.xkmc.l2hostility.compat.data.TFData;
import dev.xkmc.l2hostility.compat.gateway.GatewayConfigGen;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.WeaponConfig;
import dev.xkmc.l2hostility.content.config.WorldDifficultyConfig;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2weaponry.init.L2Weaponry;
import dev.xkmc.l2weaponry.init.registrate.LWItems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.fml.ModList;

public class LHConfigGen extends ConfigDataProvider {
   public LHConfigGen(DataGenerator generator, CompletableFuture<Provider> pvd) {
      super(generator, pvd, "L2Hostility Config");
   }

   public void add(Collector collector) {
      collector.add(
         L2Hostility.DIFFICULTY,
         L2Hostility.loc("overworld"),
         new WorldDifficultyConfig()
            .putDim(Level.OVERWORLD, 0, 0, 4.0, 1.0)
            .putBiome(
               0,
               5,
               1.0,
               0.0,
               Biomes.LUSH_CAVES,
               Biomes.FOREST,
               Biomes.FLOWER_FOREST,
               Biomes.BIRCH_FOREST,
               Biomes.JUNGLE,
               Biomes.BAMBOO_JUNGLE,
               Biomes.SPARSE_JUNGLE,
               Biomes.DESERT,
               Biomes.SAVANNA,
               Biomes.SAVANNA_PLATEAU,
               Biomes.TAIGA,
               Biomes.SNOWY_TAIGA
            )
            .putBiome(
               0,
               5,
               1.0,
               0.2,
               Biomes.DRIPSTONE_CAVES,
               Biomes.DARK_FOREST,
               Biomes.WINDSWEPT_GRAVELLY_HILLS,
               Biomes.WINDSWEPT_FOREST,
               Biomes.WINDSWEPT_HILLS,
               Biomes.WINDSWEPT_SAVANNA
            )
            .putBiome(
               0,
               10,
               1.0,
               0.2,
               Biomes.BADLANDS,
               Biomes.ERODED_BADLANDS,
               Biomes.WOODED_BADLANDS,
               Biomes.DEEP_COLD_OCEAN,
               Biomes.DEEP_OCEAN,
               Biomes.DEEP_FROZEN_OCEAN,
               Biomes.DEEP_LUKEWARM_OCEAN,
               Biomes.MUSHROOM_FIELDS,
               Biomes.STONY_SHORE,
               Biomes.SWAMP,
               Biomes.MANGROVE_SWAMP
            )
            .putBiome(0, 20, 1.0, 0.2, Biomes.SNOWY_SLOPES, Biomes.ICE_SPIKES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS)
            .putBiome(0, 50, 4.0, 0.5, Biomes.DEEP_DARK)
      );
      collector.add(
         L2Hostility.DIFFICULTY,
         L2Hostility.loc("nether"),
         new WorldDifficultyConfig()
            .putDim(Level.NETHER, 0, 20, 9.0, 1.2)
            .putLevelDef(
               Level.NETHER,
               EntityConfig.entity(0, 10, 0.0, 0.0, List.of(EntityType.ZOMBIE)).trait(List.of(EntityConfig.trait((MobTrait)LHTraits.TANK.get(), 1, 1)))
            )
            .putLevelDef(
               Level.NETHER,
               EntityConfig.entity(0, 10, 0.0, 0.0, List.of(EntityType.SKELETON)).trait(List.of(EntityConfig.trait((MobTrait)LHTraits.SPEEDY.get(), 1, 1)))
            )
            .putStructureDef(
               BuiltinStructures.BASTION_REMNANT,
               EntityConfig.entity(0, 20, 0.0, 0.0, List.of(EntityType.PIGLIN)).trait(List.of(EntityConfig.trait((MobTrait)LHTraits.PROTECTION.get(), 1, 1)))
            )
      );
      collector.add(L2Hostility.DIFFICULTY, L2Hostility.loc("end"), new WorldDifficultyConfig().putDim(Level.END, 0, 40, 16.0, 1.5));
      collector.add(
         L2Hostility.ENTITY,
         L2Hostility.loc("bosses"),
         new EntityConfig()
            .putEntity(0, 20, 1.0, 0.0, List.of(EntityType.ELDER_GUARDIAN), List.of(EntityConfig.trait((MobTrait)LHTraits.REPELLING.get(), 1, 1, 300, 0.5F)))
            .putEntity(0, 20, 1.0, 0.0, List.of(EntityType.PIGLIN_BRUTE), List.of(EntityConfig.trait((MobTrait)LHTraits.PULLING.get(), 1, 1, 300, 0.5F)))
            .putEntity(
               0,
               20,
               1.0,
               0.0,
               List.of(EntityType.WARDEN),
               List.of(
                  EntityConfig.trait((MobTrait)LHTraits.DISPELL.get(), 1, 1, 200, 1.0F), EntityConfig.trait((MobTrait)LHTraits.REPRINT.get(), 1, 1, 300, 1.0F)
               )
            )
            .putEntity(0, 50, 1.0, 0.0, List.of(EntityType.WITHER), List.of(EntityConfig.trait((MobTrait)LHTraits.CURSED.get(), 0, 1)))
            .putEntity(100, 50, 1.0, 0.0, List.of(EntityType.ENDER_DRAGON), List.of())
      );
      collector.add(
         L2Hostility.WEAPON,
         L2Hostility.loc("armors"),
         new WeaponConfig()
            .putArmor(0, 200, Items.AIR)
            .putArmor(20, 100, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS)
            .putArmor(35, 100, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS)
            .putArmor(45, 100, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS)
            .putArmor(60, 200, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
            .putArmor(80, 300, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
            .putArmor(100, 100, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
      );
      collector.add(
         L2Hostility.WEAPON,
         L2Hostility.loc("vanilla"),
         new WeaponConfig()
            .putMeleeWeapon(0, 200, Items.AIR)
            .putMeleeWeapon(30, 100, Items.IRON_AXE, Items.IRON_SWORD)
            .putMeleeWeapon(50, 100, Items.DIAMOND_AXE, Items.DIAMOND_SWORD)
            .putMeleeWeapon(70, 100, Items.NETHERITE_AXE, Items.NETHERITE_SWORD)
            .putRangedWeapon(0, 100, Items.AIR)
            .putWeaponEnch(30, 0.5F, Enchantments.SHARPNESS, Enchantments.POWER)
            .putWeaponEnch(40, 0.2F, Enchantments.KNOCKBACK, Enchantments.PUNCH)
            .putWeaponEnch(50, 0.1F, Enchantments.FIRE_ASPECT, Enchantments.FLAME)
            .putArmorEnch(30, 0.5F, Enchantments.PROTECTION)
            .putArmorEnch(
               30, 0.2F, Enchantments.PROJECTILE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.FEATHER_FALLING
            )
            .putArmorEnch(70, 0.3F, Enchantments.BINDING_CURSE)
      );
      collector.add(
         L2Hostility.WEAPON,
         L2Complements.loc("l2complements"),
         new WeaponConfig()
            .putWeaponEnch(
               100,
               0.02F,
               LCEnchantments.CURSE_BLADE.id(),
               LCEnchantments.SHARP_BLADE.id(),
               LCEnchantments.HELLFIRE_BLADE.id(),
               LCEnchantments.FREEZING_BLADE.id()
            )
            .putWeaponEnch(200, 0.01F, LCEnchantments.VOID_TOUCH.id())
            .putArmorEnch(70, 0.2F, LCEnchantments.STABLE_BODY.id(), LCEnchantments.SNOW_WALKER.id())
            .putArmorEnch(100, 0.02F, LCEnchantments.FREEZING_THORN.id(), LCEnchantments.HELLFIRE_THORN.id(), LCEnchantments.SAFEGUARD.id())
      );
      WeaponConfig config = new WeaponConfig();
      config.special_weapons
         .put(
            HolderSet.direct(
               e -> e.builtInRegistryHolder(),
               new EntityType[]{
                  EntityType.ZOMBIE, EntityType.HUSK, EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.ZOMBIFIED_PIGLIN
               }
            ),
            new ArrayList<>(
               List.of(
                  new WeaponConfig.ItemConfig(new ArrayList<>(List.of(LCItems.WINTERSTORM_WAND.asStack())), 250, 40),
                  new WeaponConfig.ItemConfig(new ArrayList<>(List.of(LCItems.SONIC_SHOOTER.asStack())), 300, 60),
                  new WeaponConfig.ItemConfig(new ArrayList<>(List.of(LCItems.HELLFIRE_WAND.asStack())), 350, 20),
                  new WeaponConfig.ItemConfig(new ArrayList<>(List.of(LCItems.HELIOS_SCEPTER.asStack())), 450, 20),
                  new WeaponConfig.ItemConfig(new ArrayList<>(List.of(LCItems.BOREAS_CEEPTER.asStack())), 550, 20)
               )
            )
         );
      collector.add(L2Hostility.WEAPON, ResourceLocation.fromNamespaceAndPath("l2complements", "special"), config);
      collector.add(
         L2Hostility.WEAPON,
         L2Weaponry.loc("weapons"),
         new WeaponConfig()
            .putMeleeWeapon(200, 10, (Item)LWItems.STORM_JAVELIN.get(), (Item)LWItems.FLAME_AXE.get(), (Item)LWItems.FROZEN_SPEAR.get())
            .putMeleeWeapon(300, 5, (Item)LWItems.ABYSS_MACHETE.get(), (Item)LWItems.HOLY_AXE.get(), (Item)LWItems.BLACK_AXE.get())
            .putMeleeWeapon(400, 2, (Item)LWItems.CHEATER_CLAW.get(), (Item)LWItems.CHEATER_MACHETE.get())
      );
      collector.add(
         L2Hostility.WEAPON,
         L2Archery.loc("bows"),
         new WeaponConfig()
            .putRangedWeapon(50, 10, (Item)ArcheryItems.STARTER_BOW.get())
            .putRangedWeapon(70, 5, (Item)ArcheryItems.IRON_BOW.get(), (Item)ArcheryItems.MASTER_BOW.get())
            .putRangedWeapon(
               100,
               2,
               (Item)ArcheryItems.FLAME_BOW.get(),
               (Item)ArcheryItems.BLACKSTONE_BOW.get(),
               (Item)ArcheryItems.TURTLE_BOW.get(),
               (Item)ArcheryItems.EAGLE_BOW.get(),
               (Item)ArcheryItems.EXPLOSION_BOW.get(),
               (Item)ArcheryItems.FROZE_BOW.get()
            )
      );
      if (ModList.get().isLoaded("twilightforest")) {
         TFData.genConfig(collector);
      }

      if (ModList.get().isLoaded("gateways")) {
         GatewayConfigGen.genConfig(collector);
      }

      if (ModList.get().isLoaded("bosses_of_mass_destruction")) {
         BoMDData.genConfig(collector);
      }

      if (ModList.get().isLoaded("mutantmonsters")) {
         MutantMonsterData.genConfig(collector);
      }

      if (ModList.get().isLoaded("cataclysm")) {
         CataclysmData.genConfig(collector);
      }
   }

   public static void addEntity(Collector collector, int min, int base, Holder<EntityType<?>> obj, EntityConfig.TraitBase... traits) {
      collector.add(
         L2Hostility.ENTITY, obj.getKey().location(), new EntityConfig().putEntity(min, base, 0.0, 0.0, List.of((EntityType<?>)obj.value()), List.of(traits))
      );
   }

   public static void addEntity(Collector collector, int min, int base, double hp, double atk, Holder<EntityType<?>> obj, EntityConfig.TraitBase... traits) {
      collector.add(
         L2Hostility.ENTITY,
         obj.getKey().location(),
         new EntityConfig().putEntity(min, base, 0.0, 0.0, hp, atk, List.of((EntityType<?>)obj.value()), List.of(traits))
      );
   }

   public static void addEntity(
      Collector collector, int min, int base, Holder<EntityType<?>> obj, List<EntityConfig.TraitBase> traits, List<EntityConfig.ItemPool> items
   ) {
      collector.add(
         L2Hostility.ENTITY,
         obj.getKey().location(),
         new EntityConfig().putEntityAndItem(min, base, 0.0, 0.0, List.of((EntityType<?>)obj.value()), traits, items)
      );
   }

   public static void addEntity(
      Collector collector,
      int min,
      int base,
      double hp,
      double atk,
      Holder<EntityType<?>> obj,
      List<EntityConfig.TraitBase> traits,
      List<EntityConfig.ItemPool> items
   ) {
      collector.add(
         L2Hostility.ENTITY,
         obj.getKey().location(),
         new EntityConfig().putEntityAndItem(min, base, 0.0, 0.0, hp, atk, List.of((EntityType<?>)obj.value()), traits, items)
      );
   }
}
