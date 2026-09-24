package io.github.manasmods.tensura.registry.world;

import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class TensuraStructureSets {
   public static final ResourceKey<StructureSet> ANCIENT_FOREST_TREE = create("ancient_forest/tree");
   public static final ResourceKey<StructureSet> ANCIENT_FOREST_DECORATION = create("ancient_forest/decoration");
   public static final ResourceKey<StructureSet> ANT_NEST = create("nests/giant_ant");
   public static final ResourceKey<StructureSet> CHARYBDIS_CAVE = create("charybdis_cave");
   public static final ResourceKey<StructureSet> DWARF_VILLAGE = create("villages/dwarf_village");
   public static final ResourceKey<StructureSet> GOBLIN_VILLAGE = create("villages/goblin_village");
   public static final ResourceKey<StructureSet> HELL_GATE = create("hell/hell_gate");
   public static final ResourceKey<StructureSet> HELL_SAND_RUIN = create("hell/sand_ruin");
   public static final ResourceKey<StructureSet> HELL_RED_SAND_RUIN = create("hell/red_sand_ruin");
   public static final ResourceKey<StructureSet> LABYRINTH_TREE = create("labyrinth/labyrinth_tree");
   public static final ResourceKey<StructureSet> LIZARDMAN_VILLAGE = create("villages/lizardmen_village");
   public static final ResourceKey<StructureSet> LIZARDMAN_TOWER = create("villages/lizardmen_tower");
   public static final ResourceKey<StructureSet> ORC_VILLAGE = create("villages/orc_village");
   public static final ResourceKey<StructureSet> SPIDER_NEST = create("nests/black_spider");
   public static final ResourceKey<StructureSet> RUINED_WARP_PADS = create("ruin/warp_pads");
   public static final ResourceKey<StructureSet> RUINED_WIZARD_TOWER = create("ruin/wizard_tower");

   public static void bootstrap(BootstrapContext<StructureSet> context) {
      HolderGetter<Structure> structureGetter = context.lookup(Registries.STRUCTURE);
      context.register(
         ANCIENT_FOREST_TREE,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.ANCIENT_FOREST_TREE), new RandomSpreadStructurePlacement(2, 1, RandomSpreadType.LINEAR, 672091483)
         )
      );
      List<StructureSelectionEntry> ancientDecoration = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.ANCIENT_FOREST_MUSHROOM), 2),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.ANCIENT_FOREST_ROCK), 1)
      );
      context.register(
         ANCIENT_FOREST_DECORATION, new StructureSet(ancientDecoration, new RandomSpreadStructurePlacement(2, 1, RandomSpreadType.LINEAR, 1745833306))
      );
      context.register(
         ANT_NEST,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.ANT_NEST), new RandomSpreadStructurePlacement(80, 20, RandomSpreadType.LINEAR, 207453891)
         )
      );
      List<StructureSelectionEntry> caves = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.CHARYBDIS_CAVE), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.CHARYBDIS_CAVE_DESERT), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.CHARYBDIS_CAVE_ICE), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.CHARYBDIS_CAVE_MESA), 1)
      );
      context.register(CHARYBDIS_CAVE, new StructureSet(caves, new RandomSpreadStructurePlacement(90, 20, RandomSpreadType.LINEAR, 892014573)));
      context.register(
         DWARF_VILLAGE,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.DWARF_VILLAGE), new RandomSpreadStructurePlacement(30, 10, RandomSpreadType.LINEAR, 1174623089)
         )
      );
      List<StructureSelectionEntry> goblinVillages = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_ACACIA), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_BIRCH), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_JUNGLE), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_OAK), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_PALM), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.GOBLIN_VILLAGE_SPRUCE), 1)
      );
      context.register(GOBLIN_VILLAGE, new StructureSet(goblinVillages, new RandomSpreadStructurePlacement(34, 16, RandomSpreadType.LINEAR, 418573921)));
      context.register(
         HELL_GATE,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.HELL_GATE), new RandomSpreadStructurePlacement(200, 50, RandomSpreadType.LINEAR, 1603847291)
         )
      );
      context.register(
         HELL_SAND_RUIN,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.HELL_SAND_RUIN), new RandomSpreadStructurePlacement(5, 4, RandomSpreadType.LINEAR, 742091583)
         )
      );
      context.register(
         HELL_RED_SAND_RUIN,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.HELL_RED_SAND_RUIN), new RandomSpreadStructurePlacement(5, 4, RandomSpreadType.LINEAR, 1815833406)
         )
      );
      context.register(
         LABYRINTH_TREE,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.LABYRINTH_TREE), new RandomSpreadStructurePlacement(100, 40, RandomSpreadType.LINEAR, 1348927561)
         )
      );
      List<StructureSelectionEntry> lizardmanVillages = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.LIZARDMAN_VILLAGE_UNDERGROUND), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.LIZARDMAN_VILLAGE_WATER), 1)
      );
      context.register(LIZARDMAN_VILLAGE, new StructureSet(lizardmanVillages, new RandomSpreadStructurePlacement(35, 20, RandomSpreadType.LINEAR, 538291047)));
      List<StructureSelectionEntry> towers = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.LIZARDMAN_TALL_TOWER), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.LIZARDMAN_SHORT_TOWER), 1)
      );
      context.register(LIZARDMAN_TOWER, new StructureSet(towers, new RandomSpreadStructurePlacement(40, 30, RandomSpreadType.LINEAR, 651928374)));
      context.register(
         ORC_VILLAGE,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.ORC_VILLAGE), new RandomSpreadStructurePlacement(40, 30, RandomSpreadType.LINEAR, 1725670197)
         )
      );
      context.register(
         SPIDER_NEST,
         new StructureSet(
            structureGetter.getOrThrow(TensuraStructures.SPIDER_NEST), new RandomSpreadStructurePlacement(70, 25, RandomSpreadType.LINEAR, 817293645)
         )
      );
      List<StructureSelectionEntry> ruinedPads = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.PLAINS_WARP_PAD), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.COLD_WARP_PAD), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.DESERT_WARP_PAD), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.MESA_WARP_PAD), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.SWAMP_WARP_PAD), 1)
      );
      context.register(RUINED_WARP_PADS, new StructureSet(ruinedPads, new RandomSpreadStructurePlacement(50, 20, RandomSpreadType.LINEAR, 1231984572)));
      List<StructureSelectionEntry> ruinedTowers = List.of(
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.BURIED_WIZARD_TOWER), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.BURNT_WIZARD_TOWER), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.FROZEN_WIZARD_TOWER), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.ROTTED_WIZARD_TOWER), 1),
         new StructureSelectionEntry(structureGetter.getOrThrow(TensuraStructures.RUINED_WIZARD_TOWER), 1)
      );
      context.register(RUINED_WIZARD_TOWER, new StructureSet(ruinedTowers, new RandomSpreadStructurePlacement(60, 20, RandomSpreadType.LINEAR, 1456829307)));
   }

   public static ResourceKey<StructureSet> create(String name) {
      return ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
