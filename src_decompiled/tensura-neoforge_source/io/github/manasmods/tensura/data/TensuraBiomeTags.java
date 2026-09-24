package io.github.manasmods.tensura.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class TensuraBiomeTags {
   public static TagKey<Biome> IS_FIRE_SPREAD_DISABLED = modTag("is_fire_spread_disabled");
   public static TagKey<Biome> IS_UNSAFE_FOR_SPAWN = modTag("is_unsafe_for_spawn");
   public static TagKey<Biome> IS_MIASMIC = modTag("is_miasmic");
   public static TagKey<Biome> IS_FOGGY = modTag("is_foggy");
   public static TagKey<Biome> HAS_SANDSTORM = modTag("has_sandstorm");
   public static TagKey<Biome> IS_CAVE = modTag("is_cave");
   public static TagKey<Biome> IS_DESERT = modTag("is_desert");
   public static TagKey<Biome> IS_FLAT_LAND = modTag("is_flat_land");
   public static TagKey<Biome> IS_COLD = modTag("is_cold");
   public static TagKey<Biome> IS_SWAMP = modTag("is_swamp");
   public static TagKey<Biome> HAS_HIPOKUTE = modTag("has_hipokute");
   public static TagKey<Biome> HAS_PALM = modTag("has_palm");
   public static TagKey<Biome> HAS_ROTTEN_TREASURE = modTag("has_rotten_treasure");
   public static TagKey<Biome> IS_HELL = modTag("is_hell");
   public static TagKey<Biome> IS_HELL_STONE = modTag("is_hell_stone");
   public static TagKey<Biome> IS_HELL_SAND = modTag("is_hell_sand");
   public static TagKey<Biome> ANT_NEST = hasStructure("giant_ant_nest");
   public static TagKey<Biome> CHARYBDIS_CAVE = hasStructure("charybdis_cave");
   public static TagKey<Biome> CHARYBDIS_CAVE_DESERT = hasStructure("charybdis_cave_desert");
   public static TagKey<Biome> CHARYBDIS_CAVE_ICE = hasStructure("charybdis_cave_ice");
   public static TagKey<Biome> CHARYBDIS_CAVE_MESA = hasStructure("charybdis_cave_mesa");
   public static TagKey<Biome> DWARF_VILLAGE = hasStructure("dwarf_village");
   public static TagKey<Biome> GOBLIN_VILLAGE_ACACIA = hasStructure("goblin_village_acacia");
   public static TagKey<Biome> GOBLIN_VILLAGE_BIRCH = hasStructure("goblin_village_birch");
   public static TagKey<Biome> GOBLIN_VILLAGE_JUNGLE = hasStructure("goblin_village_jungle");
   public static TagKey<Biome> GOBLIN_VILLAGE_OAK = hasStructure("goblin_village_oak");
   public static TagKey<Biome> GOBLIN_VILLAGE_PALM = hasStructure("goblin_village_palm");
   public static TagKey<Biome> GOBLIN_VILLAGE_SPRUCE = hasStructure("goblin_village_spruce");
   public static TagKey<Biome> HELL_GATE = hasStructure("hell_gate");
   public static TagKey<Biome> HELL_SAND_RUIN = hasStructure("hell_sand_ruin");
   public static TagKey<Biome> HELL_RED_SAND_RUIN = hasStructure("hell_red_sand_ruin");
   public static TagKey<Biome> ANCIENT_FOREST = hasStructure("ancient_forest");
   public static TagKey<Biome> LABYRINTH_TREE = hasStructure("labyrinth_tree");
   public static TagKey<Biome> LIZARDMAN_VILLAGE_SURFACE = hasStructure("lizardman_village_surface");
   public static TagKey<Biome> LIZARDMAN_VILLAGE_UNDERGROUND = hasStructure("lizardman_village_underground");
   public static TagKey<Biome> ORC_VILLAGE = hasStructure("orc_village");
   public static TagKey<Biome> BLACK_SPIDER_NEST = hasStructure("black_spider_nest");
   public static TagKey<Biome> PLAINS_WARP_PAD = hasStructure("ruin/plains_warp_pad");
   public static TagKey<Biome> COLD_WARP_PAD = hasStructure("ruin/cold_warp_pad");
   public static TagKey<Biome> DESERT_WARP_PAD = hasStructure("ruin/desert_warp_pad");
   public static TagKey<Biome> MESA_WARP_PAD = hasStructure("ruin/mesa_warp_pad");
   public static TagKey<Biome> SWAMP_WARP_PAD = hasStructure("ruin/swamp_warp_pad");

   static TagKey<Biome> hasStructure(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", "has_structure/" + name));
   }

   static TagKey<Biome> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<Biome> create(ResourceLocation name) {
      return TagKey.create(Registries.BIOME, name);
   }

   public static class EntitySpawn {
      public static TagKey<Biome> AQUA_FROG = TensuraBiomeTags.modTag("aqua_frog_spawn");
      public static TagKey<Biome> ARCH_DAEMON = TensuraBiomeTags.modTag("arch_daemon_spawn");
      public static TagKey<Biome> ARMORSAURUS = TensuraBiomeTags.modTag("armorsaurus_spawn");
      public static TagKey<Biome> ARMY_WASP = TensuraBiomeTags.modTag("army_wasp_spawn");
      public static TagKey<Biome> BARGHEST = TensuraBiomeTags.modTag("barghest_spawn");
      public static TagKey<Biome> BASILISK = TensuraBiomeTags.modTag("basilisk_spawn");
      public static TagKey<Biome> BEAST_GNOME = TensuraBiomeTags.modTag("beast_gnome_spawn");
      public static TagKey<Biome> BLACK_SPIDER = TensuraBiomeTags.modTag("black_spider_spawn");
      public static TagKey<Biome> BLADE_TIGER = TensuraBiomeTags.modTag("blade_tiger_spawn");
      public static TagKey<Biome> CATTLEDEER = TensuraBiomeTags.modTag("cattledeer_spawn");
      public static TagKey<Biome> DIREWOLF = TensuraBiomeTags.modTag("direwolf_spawn");
      public static TagKey<Biome> DRAGON_PEACOCK = TensuraBiomeTags.modTag("dragon_peacock_spawn");
      public static TagKey<Biome> EVIL_CENTIPEDE = TensuraBiomeTags.modTag("evil_centipede_spawn");
      public static TagKey<Biome> FEATHERED_SERPENT = TensuraBiomeTags.modTag("feathered_serpent_spawn");
      public static TagKey<Biome> GIANT_ANT = TensuraBiomeTags.modTag("giant_ant_spawn");
      public static TagKey<Biome> GIANT_BAT = TensuraBiomeTags.modTag("giant_bat_spawn");
      public static TagKey<Biome> GIANT_BEAR = TensuraBiomeTags.modTag("giant_bear_spawn");
      public static TagKey<Biome> GIANT_COD = TensuraBiomeTags.modTag("giant_cod_spawn");
      public static TagKey<Biome> GIANT_SALMON = TensuraBiomeTags.modTag("giant_salmon_spawn");
      public static TagKey<Biome> GREATER_DAEMON = TensuraBiomeTags.modTag("greater_daemon_spawn");
      public static TagKey<Biome> HELL_CATERPILLAR = TensuraBiomeTags.modTag("hell_caterpillar_spawn");
      public static TagKey<Biome> HELL_MOTH = TensuraBiomeTags.modTag("hell_moth_spawn");
      public static TagKey<Biome> HORNED_BEAR = TensuraBiomeTags.modTag("horned_bear_spawn");
      public static TagKey<Biome> HORNED_RABBIT = TensuraBiomeTags.modTag("horned_rabbit_spawn");
      public static TagKey<Biome> HOUND_DOG = TensuraBiomeTags.modTag("hound_dog_spawn");
      public static TagKey<Biome> HOVER_LIZARD = TensuraBiomeTags.modTag("hover_lizard_spawn");
      public static TagKey<Biome> KNIGHT_SPIDER = TensuraBiomeTags.modTag("knight_spider_spawn");
      public static TagKey<Biome> OTHERWORLDER = TensuraBiomeTags.modTag("otherworlder_spawn");
      public static TagKey<Biome> LANDFISH = TensuraBiomeTags.modTag("landfish_spawn");
      public static TagKey<Biome> LEECH_LIZARD = TensuraBiomeTags.modTag("leech_lizard_spawn");
      public static TagKey<Biome> LESSER_DAEMON = TensuraBiomeTags.modTag("lesser_daemon_spawn");
      public static TagKey<Biome> MEGALODON = TensuraBiomeTags.modTag("megalodon_spawn");
      public static TagKey<Biome> ONE_EYED_OWL = TensuraBiomeTags.modTag("one_eyed_owl_spawn");
      public static TagKey<Biome> ORC = TensuraBiomeTags.modTag("orc_spawn");
      public static TagKey<Biome> PEGASUS = TensuraBiomeTags.modTag("pegasus_spawn");
      public static TagKey<Biome> PHANTASPORE = TensuraBiomeTags.modTag("phantaspore_spawn");
      public static TagKey<Biome> SALAMANDER = TensuraBiomeTags.modTag("salamander_spawn");
      public static TagKey<Biome> SISSIE = TensuraBiomeTags.modTag("sissie_spawn");
      public static TagKey<Biome> SLIME = TensuraBiomeTags.modTag("slime_spawn");
      public static TagKey<Biome> SUPERMASSIVE_SLIME = TensuraBiomeTags.modTag("super_massive_slime_spawn");
      public static TagKey<Biome> SPEAR_TORO = TensuraBiomeTags.modTag("spear_toro_spawn");
      public static TagKey<Biome> TEMPEST_SERPENT = TensuraBiomeTags.modTag("tempest_serpent_spawn");
      public static TagKey<Biome> UNICORN = TensuraBiomeTags.modTag("unicorn_spawn");
      public static TagKey<Biome> WINGED_CAT = TensuraBiomeTags.modTag("winged_cat_spawn");
   }
}
