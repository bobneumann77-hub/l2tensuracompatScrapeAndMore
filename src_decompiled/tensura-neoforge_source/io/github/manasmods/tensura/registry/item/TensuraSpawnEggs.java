package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.awt.Color;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;

public class TensuraSpawnEggs {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<Item> FOLGEN = ITEMS.register(
      "folgen_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.FOLGEN.get(),
         new Color(171, 171, 171).getRGB(),
         new Color(157, 143, 102).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HINATA_SAKAGUCHI = ITEMS.register(
      "hinata_sakaguchi_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.HINATA_SAKAGUCHI.get(),
         new Color(185, 184, 186).getRGB(),
         new Color(246, 196, 16).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> KIRARA_MIZUTANI = ITEMS.register(
      "kirara_mizutani_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.KIRARA_MIZUTANI.get(),
         new Color(150, 125, 129).getRGB(),
         new Color(72, 58, 56).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> KYOYA_TACHIBANA = ITEMS.register(
      "kyoya_tachibana_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.KYOYA_TACHIBANA.get(),
         new Color(60, 64, 72).getRGB(),
         new Color(113, 105, 97).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> MAI_FURUKI = ITEMS.register(
      "mai_furuki_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.MAI_FURUKI.get(),
         new Color(33, 34, 38).getRGB(),
         new Color(190, 156, 137).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> MARK_LAUREN = ITEMS.register(
      "mark_lauren_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.MARK_LAUREN.get(),
         new Color(181, 137, 113).getRGB(),
         new Color(51, 58, 69).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SHINJI_TANIMURA = ITEMS.register(
      "shinji_tanimura_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.SHINJI_TANIMURA.get(),
         new Color(202, 202, 202).getRGB(),
         new Color(78, 64, 93).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SHIN_RYUSEI = ITEMS.register(
      "shin_ryusei_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.SHIN_RYUSEI.get(),
         new Color(165, 165, 165).getRGB(),
         new Color(61, 58, 63).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SHIZU = ITEMS.register(
      "shizu_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.SHIZU.get(),
         new Color(51, 50, 68).getRGB(),
         new Color(168, 58, 65).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SHOGO_TAGUCHI = ITEMS.register(
      "shogo_taguchi_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.SHOGO_TAGUCHI.get(),
         new Color(99, 107, 106).getRGB(),
         new Color(50, 49, 54).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> AKASH = ITEMS.register(
      "akash_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.AKASH.get(),
         new Color(211, 211, 211).getRGB(),
         new Color(113, 192, 178).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> AQUA_FROG = ITEMS.register(
      "aqua_frog_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.AQUA_FROG.get(),
         new Color(105, 204, 214).getRGB(),
         new Color(234, 101, 47).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ARCH_DAEMON = ITEMS.register(
      "arch_daemon_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ARCH_DAEMON.get(),
         new Color(104, 32, 32).getRGB(),
         new Color(37, 29, 29).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ARMORSAURUS = ITEMS.register(
      "armorsaurus_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ARMORSAURUS.get(),
         new Color(65, 99, 155).getRGB(),
         new Color(21, 86, 32).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ARMY_WASP = ITEMS.register(
      "army_wasp_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ARMY_WASP.get(),
         new Color(220, 199, 50).getRGB(),
         new Color(23, 23, 24).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> BARGHEST = ITEMS.register(
      "barghest_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.BARGHEST.get(),
         new Color(60, 56, 66).getRGB(),
         new Color(25, 84, 185).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> BASILISK = ITEMS.register(
      "basilisk_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.BASILISK.get(),
         new Color(88, 79, 97).getRGB(),
         new Color(202, 203, 207).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> BEAST_GNOME = ITEMS.register(
      "beast_gnome_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.BEAST_GNOME.get(),
         new Color(186, 117, 139).getRGB(),
         new Color(212, 181, 182).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> BLACK_SPIDER = ITEMS.register(
      "black_spider_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.BLACK_SPIDER.get(),
         new Color(164, 162, 21).getRGB(),
         new Color(33, 29, 44).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> BLADE_TIGER = ITEMS.register(
      "blade_tiger_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.BLADE_TIGER.get(),
         new Color(179, 65, 65).getRGB(),
         new Color(34, 44, 58).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> CATTLEDEER = ITEMS.register(
      "cattledeer_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.CATTLEDEER.get(),
         new Color(187, 132, 75).getRGB(),
         new Color(225, 212, 190).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> CHARYBDIS = ITEMS.register(
      "charybdis_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.CHARYBDIS.get(),
         new Color(30, 87, 142).getRGB(),
         new Color(23, 60, 131).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> DIREWOLF = ITEMS.register(
      "direwolf_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.DIREWOLF.get(),
         new Color(73, 76, 111).getRGB(),
         new Color(24, 25, 37).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> DRAGON_PEACOCK = ITEMS.register(
      "dragon_peacock_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.DRAGON_PEACOCK.get(),
         new Color(113, 209, 155).getRGB(),
         new Color(248, 97, 122).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> DWARF = ITEMS.register(
      "dwarf_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.DWARF.get(),
         new Color(119, 80, 24).getRGB(),
         new Color(218, 191, 152).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GAZEL_DWARGO = ITEMS.register(
      "gazel_dwargo_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)HumanEntityTypes.GAZEL_DWARGO.get(),
         new Color(158, 118, 87).getRGB(),
         new Color(167, 169, 175).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ELEMENTAL_COLOSSUS = ITEMS.register(
      "elemental_colossus_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ELEMENTAL_COLOSSUS.get(),
         new Color(186, 104, 116).getRGB(),
         new Color(172, 172, 195).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> EVIL_CENTIPEDE = ITEMS.register(
      "evil_centipede_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.EVIL_CENTIPEDE.get(),
         new Color(96, 41, 41).getRGB(),
         new Color(45, 46, 49).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> FEATHERED_SERPENT = ITEMS.register(
      "feathered_serpent_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.FEATHERED_SERPENT.get(),
         new Color(131, 196, 116).getRGB(),
         new Color(174, 58, 40).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GIANT_ANT = ITEMS.register(
      "giant_ant_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GIANT_ANT.get(),
         new Color(149, 50, 43).getRGB(),
         new Color(71, 21, 19).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GIANT_BAT = ITEMS.register(
      "giant_bat_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GIANT_BAT.get(),
         new Color(68, 68, 69).getRGB(),
         new Color(30, 30, 31).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GIANT_BEAR = ITEMS.register(
      "giant_bear_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GIANT_BEAR.get(),
         new Color(63, 63, 64).getRGB(),
         new Color(160, 160, 161).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GIANT_COD = ITEMS.register(
      "giant_cod_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GIANT_COD.get(),
         new Color(173, 145, 121).getRGB(),
         new Color(86, 73, 61).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GIANT_SALMON = ITEMS.register(
      "giant_salmon_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GIANT_SALMON.get(),
         new Color(133, 36, 33).getRGB(),
         new Color(65, 65, 46).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GOBLIN = ITEMS.register(
      "goblin_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GOBLIN.get(),
         new Color(144, 157, 110).getRGB(),
         new Color(94, 99, 68).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> GREATER_DAEMON = ITEMS.register(
      "greater_daemon_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.GREATER_DAEMON.get(),
         new Color(64, 37, 45).getRGB(),
         new Color(50, 36, 36).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HELL_CATERPILLAR = ITEMS.register(
      "hell_caterpillar_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HELL_CATERPILLAR.get(),
         new Color(236, 236, 237).getRGB(),
         new Color(176, 214, 21).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HELL_MOTH = ITEMS.register(
      "hell_moth_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HELL_MOTH.get(),
         new Color(187, 180, 172).getRGB(),
         new Color(35, 18, 13).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HORNED_BEAR = ITEMS.register(
      "horned_bear_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HORNED_BEAR.get(),
         new Color(100, 69, 70).getRGB(),
         new Color(160, 134, 121).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HORNED_RABBIT = ITEMS.register(
      "horned_rabbit_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HORNED_RABBIT.get(),
         new Color(234, 231, 228).getRGB(),
         new Color(200, 175, 160).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HOUND_DOG = ITEMS.register(
      "hound_dog_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HOUND_DOG.get(),
         new Color(40, 32, 47).getRGB(),
         new Color(93, 63, 56).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> HOVER_LIZARD = ITEMS.register(
      "hover_lizard_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.HOVER_LIZARD.get(),
         new Color(121, 114, 73).getRGB(),
         new Color(226, 219, 198).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> IFRIT = ITEMS.register(
      "ifrit_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.IFRIT.get(),
         new Color(112, 81, 60).getRGB(),
         new Color(197, 66, 33).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER = ITEMS.register(
      "knight_spider_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.KNIGHT_SPIDER.get(),
         new Color(110, 75, 74).getRGB(),
         new Color(39, 55, 103).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> LANDFISH = ITEMS.register(
      "landfish_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.LANDFISH.get(),
         new Color(84, 125, 129).getRGB(),
         new Color(43, 78, 77).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> LEECH_LIZARD = ITEMS.register(
      "leech_lizard_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.LEECH_LIZARD.get(),
         new Color(77, 94, 61).getRGB(),
         new Color(37, 47, 29).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> LESSER_DAEMON = ITEMS.register(
      "lesser_daemon_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.LESSER_DAEMON.get(),
         new Color(98, 69, 76).getRGB(),
         new Color(30, 22, 22).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> LIZARDMAN = ITEMS.register(
      "lizardman_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.LIZARDMAN.get(),
         new Color(94, 117, 109).getRGB(),
         new Color(197, 194, 178).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> MEGALODON = ITEMS.register(
      "megalodon_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.MEGALODON.get(),
         new Color(113, 141, 174).getRGB(),
         new Color(87, 109, 134).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ONE_EYED_OWL = ITEMS.register(
      "one_eyed_owl_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ONE_EYED_OWL.get(),
         new Color(217, 217, 217).getRGB(),
         new Color(136, 187, 134).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ORC = ITEMS.register(
      "orc_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ORC.get(),
         new Color(182, 147, 106).getRGB(),
         new Color(127, 81, 56).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ORC_LORD = ITEMS.register(
      "orc_lord_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ORC_LORD.get(),
         new Color(105, 78, 61).getRGB(),
         new Color(74, 42, 80).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ORC_DISASTER = ITEMS.register(
      "orc_disaster_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.ORC_DISASTER.get(),
         new Color(105, 78, 61).getRGB(),
         new Color(51, 51, 51).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> PEGASUS = ITEMS.register(
      "pegasus_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.PEGASUS.get(),
         new Color(255, 255, 255).getRGB(),
         new Color(200, 236, 236).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> PEGACORN = ITEMS.register(
      "pegacorn_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.PEGACORN.get(),
         new Color(255, 255, 255).getRGB(),
         new Color(234, 227, 219).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> PHANTASPORE = ITEMS.register(
      "phantaspore_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.PHANTASPORE.get(),
         new Color(124, 53, 34).getRGB(),
         new Color(179, 177, 173).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SALAMANDER = ITEMS.register(
      "salamander_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SALAMANDER.get(),
         new Color(124, 50, 41).getRGB(),
         new Color(251, 80, 4).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SISSIE = ITEMS.register(
      "sissie_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SISSIE.get(),
         new Color(65, 65, 65).getRGB(),
         new Color(180, 180, 180).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SKELETON = ITEMS.register(
      "skeleton_spawn_egg",
      () -> new SpawnEggItem((EntityType)HumanEntityTypes.SKELETON.get(), 12698049, 4802889, new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG))
   );
   public static final RegistrySupplier<Item> SLIME = ITEMS.register(
      "slime_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SLIME.get(),
         new Color(156, 186, 195).getRGB(),
         new Color(114, 142, 174).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> METAL_SLIME = ITEMS.register(
      "metal_slime_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.METAL_SLIME.get(),
         new Color(139, 141, 141).getRGB(),
         new Color(78, 78, 80).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SUPERMASSIVE_SLIME = ITEMS.register(
      "supermassive_slime_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SUPERMASSIVE_SLIME.get(),
         new Color(124, 174, 232).getRGB(),
         new Color(93, 129, 201).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SPEAR_TORO = ITEMS.register(
      "spear_toro_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SPEAR_TORO.get(),
         new Color(27, 69, 145).getRGB(),
         new Color(204, 195, 225).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> SYLPHIDE = ITEMS.register(
      "sylphide_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.SYLPHIDE.get(),
         new Color(138, 211, 181).getRGB(),
         new Color(218, 198, 181).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> UNDINE = ITEMS.register(
      "undine_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.UNDINE.get(),
         new Color(151, 190, 215).getRGB(),
         new Color(54, 128, 223).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> UNICORN = ITEMS.register(
      "unicorn_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.UNICORN.get(),
         new Color(255, 255, 255).getRGB(),
         new Color(251, 251, 203).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> TEMPEST_SERPENT = ITEMS.register(
      "tempest_serpent_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.TEMPEST_SERPENT.get(),
         new Color(59, 58, 63).getRGB(),
         new Color(86, 93, 125).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> WAR_GNOME = ITEMS.register(
      "war_gnome_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.WAR_GNOME.get(),
         new Color(73, 73, 73).getRGB(),
         new Color(170, 52, 18).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> WINGED_CAT = ITEMS.register(
      "winged_cat_spawn_egg",
      () -> new SpawnEggItem(
         (EntityType)MonsterEntityTypes.WINGED_CAT.get(),
         new Color(112, 92, 61).getRGB(),
         new Color(168, 165, 157).getRGB(),
         new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG)
      )
   );
   public static final RegistrySupplier<Item> ZOMBIE = ITEMS.register(
      "zombie_spawn_egg",
      () -> new SpawnEggItem((EntityType)HumanEntityTypes.ZOMBIE.get(), 44975, 7969893, new Properties().arch$tab(TensuraCreativeTabs.SPAWN_EGG))
   );

   public static void init() {
      ITEMS.register();
   }
}
