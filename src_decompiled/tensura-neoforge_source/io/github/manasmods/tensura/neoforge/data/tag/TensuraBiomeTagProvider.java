package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraBiomeTagProvider extends BiomeTagsProvider {
   public TensuraBiomeTagProvider(PackOutput output, CompletableFuture<Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, completableFuture, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
      this.tag(BiomeTags.IS_FOREST).add(TensuraBiomes.ANCIENT_FOREST);
      this.tag(BiomeTags.IS_OVERWORLD)
         .add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(BiomeTags.HAS_STRONGHOLD)
         .add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(BiomeTags.HAS_CLOSER_WATER_FOG).add(TensuraBiomes.MIASMIC_PLAINS);
      this.tag(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS).add(TensuraBiomes.MIASMIC_PLAINS);
      this.tag(BiomeTags.SNOW_GOLEM_MELTS).add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(BiomeTags.WITHOUT_PATROL_SPAWNS)
         .add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)
         .add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(BiomeTags.SPAWNS_WARM_VARIANT_FROGS).add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(BiomeTags.SPAWNS_GOLD_RABBITS).add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(TensuraBiomeTags.IS_FIRE_SPREAD_DISABLED).add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(TensuraBiomeTags.IS_UNSAFE_FOR_SPAWN)
         .add(new ResourceKey[]{TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(TensuraBiomeTags.IS_MIASMIC).add(TensuraBiomes.MIASMIC_PLAINS);
      this.tag(TensuraBiomeTags.IS_FOGGY)
         .add(new ResourceKey[]{TensuraBiomes.MIASMIC_PLAINS, TensuraBiomes.BARREN_LAND, TensuraBiomes.UNDERWORLD_BARRENS, TensuraBiomes.UNDERWORLD_SPIKES});
      this.tag(TensuraBiomeTags.HAS_SANDSTORM).add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(TensuraBiomeTags.IS_CAVE)
         .addOptional(Biomes.IS_CAVE.location())
         .add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.LUSH_CAVES, net.minecraft.world.level.biome.Biomes.DRIPSTONE_CAVES});
      this.tag(TensuraBiomeTags.IS_DESERT)
         .addOptional(Biomes.IS_DESERT.location())
         .addTag(BiomeTags.HAS_DESERT_PYRAMID)
         .addTag(BiomeTags.HAS_RUINED_PORTAL_DESERT)
         .addTag(BiomeTags.HAS_VILLAGE_DESERT)
         .add(new ResourceKey[]{TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.BARREN_LAND});
      this.tag(TensuraBiomeTags.IS_FLAT_LAND)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_SNOWY_PLAINS.location())
         .add(
            new ResourceKey[]{
               net.minecraft.world.level.biome.Biomes.PLAINS,
               net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS,
               net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS,
               net.minecraft.world.level.biome.Biomes.MEADOW,
               net.minecraft.world.level.biome.Biomes.DESERT,
               net.minecraft.world.level.biome.Biomes.BADLANDS
            }
         );
      this.tag(TensuraBiomeTags.IS_COLD)
         .addOptional(Biomes.IS_COLD.location())
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .add(net.minecraft.world.level.biome.Biomes.ICE_SPIKES);
      this.tag(TensuraBiomeTags.IS_SWAMP)
         .addOptional(Biomes.IS_SWAMP.location())
         .addTag(BiomeTags.HAS_SWAMP_HUT)
         .addTag(BiomeTags.HAS_RUINED_PORTAL_SWAMP)
         .addTag(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS);
      this.tag(TensuraBiomeTags.HAS_PALM).add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.BEACH, net.minecraft.world.level.biome.Biomes.RIVER});
      this.tag(TensuraBiomeTags.HAS_HIPOKUTE).addTag(BiomeTags.IS_OVERWORLD);
      this.tag(TensuraBiomeTags.HAS_ROTTEN_TREASURE).addTag(TensuraBiomeTags.IS_MIASMIC).add(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP);
      this.tag(TensuraBiomeTags.IS_HELL)
         .add(
            new ResourceKey[]{
               TensuraBiomes.UNDERWORLD_BARRENS, TensuraBiomes.UNDERWORLD_SPIKES, TensuraBiomes.UNDERWORLD_RED_SANDS, TensuraBiomes.UNDERWORLD_SANDS
            }
         );
      this.tag(TensuraBiomeTags.IS_HELL_STONE).add(new ResourceKey[]{TensuraBiomes.UNDERWORLD_BARRENS, TensuraBiomes.UNDERWORLD_SPIKES});
      this.tag(TensuraBiomeTags.IS_HELL_SAND).add(new ResourceKey[]{TensuraBiomes.UNDERWORLD_RED_SANDS, TensuraBiomes.UNDERWORLD_SANDS});
      this.tag(TensuraBiomeTags.ANT_NEST)
         .add(
            new ResourceKey[]{
               net.minecraft.world.level.biome.Biomes.SAVANNA,
               net.minecraft.world.level.biome.Biomes.FOREST,
               net.minecraft.world.level.biome.Biomes.BIRCH_FOREST
            }
         );
      this.tag(TensuraBiomeTags.CHARYBDIS_CAVE)
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SAVANNA)
         .addTag(BiomeTags.HAS_VILLAGE_TAIGA)
         .add(TensuraBiomes.ANCIENT_FOREST);
      this.tag(TensuraBiomeTags.CHARYBDIS_CAVE_DESERT)
         .addTag(BiomeTags.HAS_VILLAGE_DESERT)
         .add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(TensuraBiomeTags.CHARYBDIS_CAVE_ICE).addTag(TensuraBiomeTags.IS_COLD);
      this.tag(TensuraBiomeTags.CHARYBDIS_CAVE_MESA).addTag(BiomeTags.IS_BADLANDS);
      this.tag(TensuraBiomeTags.DWARF_VILLAGE)
         .addOptional(Biomes.IS_MOUNTAIN_SLOPE.location())
         .add(
            new ResourceKey[]{
               net.minecraft.world.level.biome.Biomes.MEADOW,
               net.minecraft.world.level.biome.Biomes.CHERRY_GROVE,
               net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU,
               net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES
            }
         );
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_ACACIA).addOptional(Biomes.IS_SAVANNA.location()).addTag(BiomeTags.HAS_VILLAGE_SAVANNA);
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_BIRCH).addOptional(Biomes.IS_BIRCH_FOREST.location()).add(net.minecraft.world.level.biome.Biomes.BIRCH_FOREST);
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_JUNGLE).addOptional(Biomes.IS_JUNGLE.location()).addTag(BiomeTags.IS_JUNGLE);
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_OAK).addOptional(Biomes.IS_FOREST.location()).add(net.minecraft.world.level.biome.Biomes.FOREST);
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_PALM).addOptional(Biomes.IS_PLAINS.location()).addTag(BiomeTags.HAS_VILLAGE_PLAINS);
      this.tag(TensuraBiomeTags.GOBLIN_VILLAGE_SPRUCE).addOptional(Biomes.IS_TAIGA.location()).addTag(BiomeTags.HAS_VILLAGE_TAIGA);
      this.tag(TensuraBiomeTags.HELL_GATE)
         .addTag(TensuraBiomeTags.IS_HELL)
         .addTag(TensuraBiomeTags.IS_FLAT_LAND)
         .addTag(BiomeTags.IS_BADLANDS)
         .addTag(BiomeTags.IS_SAVANNA)
         .add(new ResourceKey[]{TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH});
      this.tag(TensuraBiomeTags.HELL_SAND_RUIN).add(TensuraBiomes.UNDERWORLD_SANDS);
      this.tag(TensuraBiomeTags.HELL_RED_SAND_RUIN).add(TensuraBiomes.UNDERWORLD_RED_SANDS);
      this.tag(TensuraBiomeTags.ANCIENT_FOREST).add(TensuraBiomes.ANCIENT_FOREST);
      this.tag(TensuraBiomeTags.LABYRINTH_TREE).add(TensuraBiomes.ANCIENT_FOREST);
      this.tag(TensuraBiomeTags.LIZARDMAN_VILLAGE_SURFACE).add(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP);
      this.tag(TensuraBiomeTags.LIZARDMAN_VILLAGE_UNDERGROUND)
         .addTag(TensuraBiomeTags.IS_SWAMP)
         .add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.SWAMP, net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP});
      this.tag(TensuraBiomeTags.ORC_VILLAGE).addOptional(Biomes.IS_DESERT.location()).addTag(BiomeTags.HAS_VILLAGE_DESERT);
      this.tag(TensuraBiomeTags.BLACK_SPIDER_NEST)
         .add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.PLAINS, net.minecraft.world.level.biome.Biomes.DARK_FOREST});
      this.tag(TensuraBiomeTags.PLAINS_WARP_PAD)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SAVANNA)
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(BiomeTags.IS_SAVANNA);
      this.tag(TensuraBiomeTags.COLD_WARP_PAD).addTag(TensuraBiomeTags.IS_COLD);
      this.tag(TensuraBiomeTags.DESERT_WARP_PAD)
         .addTag(TensuraBiomeTags.IS_DESERT)
         .add(new ResourceKey[]{TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.BARREN_LAND});
      this.tag(TensuraBiomeTags.MESA_WARP_PAD).addTag(BiomeTags.IS_BADLANDS);
      this.tag(TensuraBiomeTags.SWAMP_WARP_PAD).add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP, TensuraBiomes.MIASMIC_PLAINS});
      this.tag(TensuraBiomeTags.EntitySpawn.AQUA_FROG)
         .addOptional(Biomes.IS_STONY_SHORES.location())
         .add(net.minecraft.world.level.biome.Biomes.STONY_SHORE)
         .addTag(TensuraBiomeTags.ANCIENT_FOREST)
         .addTag(TensuraBiomeTags.IS_SWAMP)
         .addTag(BiomeTags.HAS_OCEAN_RUIN_COLD);
      this.tag(TensuraBiomeTags.EntitySpawn.ARCH_DAEMON).addTag(TensuraBiomeTags.IS_HELL);
      this.tag(TensuraBiomeTags.EntitySpawn.ARMORSAURUS)
         .addOptional(Biomes.IS_BADLANDS.location())
         .addTag(TensuraBiomeTags.IS_CAVE)
         .addTag(BiomeTags.IS_BADLANDS)
         .add(TensuraBiomes.DESERT_OF_DEATH);
      this.tag(TensuraBiomeTags.EntitySpawn.ARMY_WASP)
         .add(
            new ResourceKey[]{
               net.minecraft.world.level.biome.Biomes.FLOWER_FOREST,
               net.minecraft.world.level.biome.Biomes.FLOWER_FOREST,
               net.minecraft.world.level.biome.Biomes.CHERRY_GROVE
            }
         );
      this.tag(TensuraBiomeTags.EntitySpawn.BARGHEST)
         .addOptional(Biomes.IS_PLAINS.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(BiomeTags.IS_NETHER)
         .add(TensuraBiomes.MIASMIC_PLAINS);
      this.tag(TensuraBiomeTags.EntitySpawn.BASILISK)
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .add(TensuraBiomes.DESERT_OF_DEATH)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_MOUNTAIN);
      this.tag(TensuraBiomeTags.EntitySpawn.BEAST_GNOME)
         .addOptional(Biomes.IS_BADLANDS.location())
         .addTag(TensuraBiomeTags.ANCIENT_FOREST)
         .addTag(TensuraBiomeTags.IS_CAVE)
         .addTag(BiomeTags.IS_BADLANDS);
      this.tag(TensuraBiomeTags.EntitySpawn.BLACK_SPIDER).addTag(TensuraBiomeTags.IS_CAVE).addTag(BiomeTags.HAS_WOODLAND_MANSION);
      this.tag(TensuraBiomeTags.EntitySpawn.BLADE_TIGER).addOptional(Biomes.IS_FOREST.location()).addTag(BiomeTags.IS_FOREST);
      this.tag(TensuraBiomeTags.EntitySpawn.CATTLEDEER)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_TAIGA.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_TAIGA)
         .add(net.minecraft.world.level.biome.Biomes.CHERRY_GROVE);
      this.tag(TensuraBiomeTags.EntitySpawn.DIREWOLF)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_TAIGA.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(BiomeTags.IS_HILL)
         .addTag(BiomeTags.IS_BADLANDS)
         .addTag(BiomeTags.IS_MOUNTAIN)
         .addTag(TensuraBiomeTags.IS_CAVE)
         .addTag(TensuraBiomeTags.IS_SWAMP);
      this.tag(TensuraBiomeTags.EntitySpawn.DRAGON_PEACOCK)
         .addOptional(Biomes.IS_JUNGLE.location())
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.IS_JUNGLE)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_FOREST);
      this.tag(TensuraBiomeTags.EntitySpawn.EVIL_CENTIPEDE)
         .addOptional(Biomes.IS_JUNGLE.location())
         .addOptional(Biomes.IS_TAIGA.location())
         .addTag(BiomeTags.IS_JUNGLE)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(TensuraBiomeTags.IS_CAVE)
         .addTag(TensuraBiomeTags.IS_SWAMP)
         .add(TensuraBiomes.MIASMIC_PLAINS);
      this.tag(TensuraBiomeTags.EntitySpawn.FEATHERED_SERPENT)
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addTag(TensuraBiomeTags.ANCIENT_FOREST)
         .addTag(BiomeTags.IS_MOUNTAIN)
         .addTag(BiomeTags.IS_HILL);
      this.tag(TensuraBiomeTags.EntitySpawn.GIANT_ANT)
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_SAVANNA);
      this.tag(TensuraBiomeTags.EntitySpawn.GIANT_BAT)
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addTag(BiomeTags.IS_HILL)
         .addTag(BiomeTags.IS_MOUNTAIN)
         .addTag(BiomeTags.IS_BADLANDS)
         .addTag(BiomeTags.HAS_WOODLAND_MANSION)
         .add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.DRIPSTONE_CAVES, net.minecraft.world.level.biome.Biomes.DEEP_DARK});
      this.tag(TensuraBiomeTags.EntitySpawn.GIANT_BEAR)
         .addOptional(Biomes.IS_TAIGA.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_TAIGA);
      this.tag(TensuraBiomeTags.EntitySpawn.GIANT_COD)
         .addOptional(Biomes.IS_RIVER.location())
         .addOptional(Biomes.IS_OCEAN.location())
         .addTag(BiomeTags.IS_RIVER)
         .addTag(BiomeTags.IS_OCEAN);
      this.tag(TensuraBiomeTags.EntitySpawn.GIANT_SALMON)
         .addOptional(Biomes.IS_RIVER.location())
         .addOptional(Biomes.IS_OCEAN.location())
         .addTag(BiomeTags.IS_RIVER)
         .addTag(BiomeTags.IS_OCEAN);
      this.tag(TensuraBiomeTags.EntitySpawn.GREATER_DAEMON).addTag(TensuraBiomeTags.IS_HELL);
      this.tag(TensuraBiomeTags.EntitySpawn.HELL_CATERPILLAR).addTag(BiomeTags.HAS_WOODLAND_MANSION).add(net.minecraft.world.level.biome.Biomes.CHERRY_GROVE);
      this.tag(TensuraBiomeTags.EntitySpawn.HELL_MOTH).addTag(BiomeTags.HAS_WOODLAND_MANSION);
      this.tag(TensuraBiomeTags.EntitySpawn.HORNED_BEAR)
         .addOptional(Biomes.IS_TAIGA.location())
         .addTag(BiomeTags.IS_HILL)
         .addTag(BiomeTags.IS_TAIGA)
         .add(net.minecraft.world.level.biome.Biomes.CHERRY_GROVE);
      this.tag(TensuraBiomeTags.EntitySpawn.HORNED_RABBIT)
         .addOptional(Biomes.IS_TAIGA.location())
         .addTag(BiomeTags.HAS_WOODLAND_MANSION)
         .addTag(BiomeTags.IS_TAIGA)
         .add(net.minecraft.world.level.biome.Biomes.CHERRY_GROVE);
      this.tag(TensuraBiomeTags.EntitySpawn.HOUND_DOG)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .addTag(BiomeTags.IS_FOREST);
      this.tag(TensuraBiomeTags.EntitySpawn.HOVER_LIZARD).addTag(TensuraBiomeTags.IS_SWAMP).addTag(BiomeTags.IS_RIVER);
      this.tag(TensuraBiomeTags.EntitySpawn.KNIGHT_SPIDER)
         .addOptional(Biomes.IS_DESERT.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addTag(TensuraBiomeTags.IS_DESERT)
         .addTag(BiomeTags.IS_BADLANDS)
         .add(TensuraBiomes.DESERT_OF_DEATH)
         .remove(TensuraBiomes.BARREN_LAND);
      this.tag(TensuraBiomeTags.EntitySpawn.LANDFISH)
         .addOptional(Biomes.IS_RIVER.location())
         .addOptional(Biomes.IS_BEACH.location())
         .addTag(BiomeTags.IS_RIVER)
         .addTag(BiomeTags.IS_BEACH);
      this.tag(TensuraBiomeTags.EntitySpawn.LEECH_LIZARD)
         .addOptional(Biomes.IS_FOREST.location())
         .addOptional(Biomes.IS_TAIGA.location())
         .addOptional(Biomes.IS_PLAINS.location())
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS);
      this.tag(TensuraBiomeTags.EntitySpawn.LESSER_DAEMON).addTag(TensuraBiomeTags.IS_HELL);
      this.tag(TensuraBiomeTags.EntitySpawn.ONE_EYED_OWL)
         .addOptional(Biomes.IS_FOREST.location())
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.HAS_WOODLAND_MANSION);
      this.tag(TensuraBiomeTags.EntitySpawn.ORC)
         .addOptional(Biomes.IS_DESERT.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addTag(TensuraBiomeTags.IS_DESERT)
         .addTag(BiomeTags.IS_BADLANDS)
         .remove(TensuraBiomes.DESERT_OF_DEATH)
         .remove(TensuraBiomes.BARREN_LAND);
      this.tag(TensuraBiomeTags.EntitySpawn.OTHERWORLDER)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_DESERT.location())
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addTag(TensuraBiomeTags.IS_DESERT)
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .addTag(BiomeTags.IS_HILL)
         .addTag(BiomeTags.IS_MOUNTAIN)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_BADLANDS)
         .remove(TensuraBiomes.DESERT_OF_DEATH)
         .remove(TensuraBiomes.BARREN_LAND);
      this.tag(TensuraBiomeTags.EntitySpawn.PEGASUS).addOptional(Biomes.IS_MOUNTAIN.location()).addTag(BiomeTags.IS_MOUNTAIN);
      this.tag(TensuraBiomeTags.EntitySpawn.PHANTASPORE)
         .addOptional(Biomes.IS_FOREST.location())
         .addOptional(Biomes.IS_MUSHROOM.location())
         .addTag(BiomeTags.IS_FOREST)
         .add(
            new ResourceKey[]{
               net.minecraft.world.level.biome.Biomes.CRIMSON_FOREST,
               net.minecraft.world.level.biome.Biomes.WARPED_FOREST,
               net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS
            }
         );
      this.tag(TensuraBiomeTags.EntitySpawn.SALAMANDER)
         .addOptional(Biomes.IS_HOT_NETHER.location())
         .addTag(TensuraBiomeTags.ANCIENT_FOREST)
         .add(new ResourceKey[]{net.minecraft.world.level.biome.Biomes.CRIMSON_FOREST, net.minecraft.world.level.biome.Biomes.NETHER_WASTES});
      this.tag(TensuraBiomeTags.EntitySpawn.SISSIE).addOptional(Biomes.IS_DEEP_OCEAN.location()).addTag(BiomeTags.IS_DEEP_OCEAN);
      this.tag(TensuraBiomeTags.EntitySpawn.SLIME)
         .addOptional(Biomes.IS_PLAINS.location())
         .addOptional(Biomes.IS_DESERT.location())
         .addOptional(Biomes.IS_SAVANNA.location())
         .addOptional(Biomes.IS_BADLANDS.location())
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addOptional(Biomes.IS_FOREST.location())
         .addOptional(Biomes.IS_TAIGA.location())
         .addTag(BiomeTags.HAS_VILLAGE_PLAINS)
         .addTag(BiomeTags.HAS_VILLAGE_SNOWY)
         .addTag(BiomeTags.IS_FOREST)
         .addTag(BiomeTags.IS_SAVANNA)
         .addTag(BiomeTags.IS_TAIGA)
         .addTag(BiomeTags.IS_HILL)
         .addTag(BiomeTags.IS_BADLANDS)
         .addTag(BiomeTags.IS_MOUNTAIN)
         .addTag(TensuraBiomeTags.IS_CAVE)
         .addTag(TensuraBiomeTags.IS_SWAMP);
      this.tag(TensuraBiomeTags.EntitySpawn.SUPERMASSIVE_SLIME).addTag(TensuraBiomeTags.IS_FLAT_LAND);
      this.tag(TensuraBiomeTags.EntitySpawn.SPEAR_TORO).addOptional(Biomes.IS_OCEAN.location()).addTag(BiomeTags.IS_OCEAN);
      this.tag(TensuraBiomeTags.EntitySpawn.TEMPEST_SERPENT)
         .addTag(BiomeTags.HAS_WOODLAND_MANSION)
         .addTag(TensuraBiomeTags.IS_CAVE)
         .add(TensuraBiomes.DESERT_OF_DEATH);
      this.tag(TensuraBiomeTags.EntitySpawn.UNICORN)
         .addOptional(Biomes.IS_MOUNTAIN.location())
         .addTag(BiomeTags.IS_MOUNTAIN)
         .add(net.minecraft.world.level.biome.Biomes.FLOWER_FOREST);
      this.tag(TensuraBiomeTags.EntitySpawn.WINGED_CAT)
         .addOptional(Biomes.IS_JUNGLE.location())
         .addOptional(Biomes.IS_OUTER_END_ISLAND.location())
         .addTag(TensuraBiomeTags.ANCIENT_FOREST)
         .addTag(BiomeTags.IS_JUNGLE)
         .addTag(BiomeTags.HAS_END_CITY);
   }
}
