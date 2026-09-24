package io.github.manasmods.tensura.registry.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class TensuraTemplatePools {
   public static final ResourceKey<StructureTemplatePool> ANCIENT_FOREST_TREE = create("ancient_forest/tree");
   public static final ResourceKey<StructureTemplatePool> ANCIENT_FOREST_MUSHROOM = create("ancient_forest/mushroom");
   public static final ResourceKey<StructureTemplatePool> ANCIENT_FOREST_ROCK = create("ancient_forest/rock");
   public static final ResourceKey<StructureTemplatePool> ANT_NEST_START = create("ant_nest/surface");
   public static final ResourceKey<StructureTemplatePool> ANT_NEST_PARTS = create("ant_nest/parts");
   public static final ResourceKey<StructureTemplatePool> ANT_NEST_FALLBACK = create("ant_nest/fallback");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_START = create("charybdis_cave/start");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_DESERT_START = create("charybdis_cave/start_desert");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_ICE_START = create("charybdis_cave/start_ice");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_MESA_START = create("charybdis_cave/start_mesa");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_STAIRS = create("charybdis_cave/stairs");
   public static final ResourceKey<StructureTemplatePool> CHARYBDIS_CAVE_ROOMS = create("charybdis_cave/rooms");
   public static final ResourceKey<StructureTemplatePool> DWARF_VILLAGE_SPAWN = create("dwarf_village/spawn");
   public static final ResourceKey<StructureTemplatePool> DWARF_VILLAGE_START = create("dwarf_village/start");
   public static final ResourceKey<StructureTemplatePool> DWARF_VILLAGE_BUILDING = create("dwarf_village/buildings");
   public static final ResourceKey<StructureTemplatePool> DWARF_VILLAGE_STREETS = create("dwarf_village/paths");
   public static final ResourceKey<StructureTemplatePool> HELL_GATE = create("hell_gate/base");
   public static final ResourceKey<StructureTemplatePool> HELL_GATE_PARTS = create("hell_gate/parts");
   public static final ResourceKey<StructureTemplatePool> HELL_SAND_RUIN = create("hell_ruins/sand");
   public static final ResourceKey<StructureTemplatePool> HELL_RED_SAND_RUIN = create("hell_ruins/red_sand");
   public static final ResourceKey<StructureTemplatePool> LABYRINTH_TREE_START = create("labyrinth_tree/start");
   public static final ResourceKey<StructureTemplatePool> LABYRINTH_TREE_PARTS = create("labyrinth_tree/parts");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_SPAWN = create("lizardman_village/spawn");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_TALL_TOWER = create("lizardman_village/tower_tall");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_SHORT_TOWER = create("lizardman_village/tower_short");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_UNDERGROUND_START = create("lizardmen_village/underground/surface");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_UNDERGROUND_ENTRANCE_STAIRS = create(
      "lizardmen_village/underground/entrance_stairs"
   );
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_UNDERGROUND_HALLWAYS = create("lizardmen_village/underground/hallways");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_UNDERGROUND_ROOMS = create("lizardmen_village/underground/rooms");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_UNDERGROUND_FALLBACK = create("lizardmen_village/underground/fallback");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_WATER_START = create("lizardmen_village/water/surface");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_WATER_BUILDINGS = create("lizardmen_village/water/buildings");
   public static final ResourceKey<StructureTemplatePool> LIZARDMAN_VILLAGE_WATER_PATHS = create("lizardmen_village/water/paths");
   public static final ResourceKey<StructureTemplatePool> ORC_VILLAGE_SPAWN = create("orc_village/spawn");
   public static final ResourceKey<StructureTemplatePool> ORC_VILLAGE_START = create("orc_village/start");
   public static final ResourceKey<StructureTemplatePool> ORC_VILLAGE_BUILDS = create("orc_village/builds");
   public static final ResourceKey<StructureTemplatePool> ORC_VILLAGE_PATHS = create("orc_village/paths");
   public static final ResourceKey<StructureTemplatePool> SPIDER_NEST_START = create("spider_nest/surface");
   public static final ResourceKey<StructureTemplatePool> SPIDER_NEST_PARTS = create("spider_nest/parts");
   public static final ResourceKey<StructureTemplatePool> PLAINS_WARP_PAD = create("ruin/warp/plains_warp_pad");
   public static final ResourceKey<StructureTemplatePool> COLD_WARP_PAD = create("ruin/warp/cold_warp_pad");
   public static final ResourceKey<StructureTemplatePool> DESERT_WARP_PAD = create("ruin/warp/desert_warp_pad");
   public static final ResourceKey<StructureTemplatePool> MESA_WARP_PAD = create("ruin/warp/mesa_warp_pad");
   public static final ResourceKey<StructureTemplatePool> SWAMP_WARP_PAD = create("ruin/warp/swamp_warp_pad");
   public static final ResourceKey<StructureTemplatePool> BURIED_WIZARD_TOWER = create("ruin/wizard_tower/buried");
   public static final ResourceKey<StructureTemplatePool> BURNT_WIZARD_TOWER = create("ruin/wizard_tower/burnt");
   public static final ResourceKey<StructureTemplatePool> FROZEN_WIZARD_TOWER = create("ruin/wizard_tower/frozen");
   public static final ResourceKey<StructureTemplatePool> ROTTED_WIZARD_TOWER = create("ruin/wizard_tower/rotten");
   public static final ResourceKey<StructureTemplatePool> RUINED_WIZARD_TOWER = create("ruin/wizard_tower/ruined");
   public static final ResourceKey<StructureTemplatePool> GOBLIN_VILLAGE_SPAWN = create("goblin_village/spawn");
   public static final ResourceKey<StructureTemplatePool> ACACIA_GOBLIN_VILLAGE_START = create("goblin_village/acacia/main");
   public static final ResourceKey<StructureTemplatePool> ACACIA_GOBLIN_VILLAGE_BUILDING = create("goblin_village/acacia/buildings");
   public static final ResourceKey<StructureTemplatePool> ACACIA_GOBLIN_VILLAGE_STREETS = create("goblin_village/acacia/streets");
   public static final ResourceKey<StructureTemplatePool> BIRCH_GOBLIN_VILLAGE_START = create("goblin_village/birch/main");
   public static final ResourceKey<StructureTemplatePool> BIRCH_GOBLIN_VILLAGE_BUILDING = create("goblin_village/birch/buildings");
   public static final ResourceKey<StructureTemplatePool> BIRCH_GOBLIN_VILLAGE_STREETS = create("goblin_village/birch/streets");
   public static final ResourceKey<StructureTemplatePool> JUNGLE_GOBLIN_VILLAGE_START = create("goblin_village/jungle/main");
   public static final ResourceKey<StructureTemplatePool> JUNGLE_GOBLIN_VILLAGE_BUILDING = create("goblin_village/jungle/buildings");
   public static final ResourceKey<StructureTemplatePool> JUNGLE_GOBLIN_VILLAGE_STREETS = create("goblin_village/jungle/streets");
   public static final ResourceKey<StructureTemplatePool> OAK_GOBLIN_VILLAGE_START = create("goblin_village/oak/main");
   public static final ResourceKey<StructureTemplatePool> OAK_GOBLIN_VILLAGE_BUILDING = create("goblin_village/oak/buildings");
   public static final ResourceKey<StructureTemplatePool> OAK_GOBLIN_VILLAGE_STREETS = create("goblin_village/oak/streets");
   public static final ResourceKey<StructureTemplatePool> PALM_GOBLIN_VILLAGE_START = create("goblin_village/palm/main");
   public static final ResourceKey<StructureTemplatePool> PALM_GOBLIN_VILLAGE_BUILDING = create("goblin_village/palm/buildings");
   public static final ResourceKey<StructureTemplatePool> PALM_GOBLIN_VILLAGE_STREETS = create("goblin_village/palm/streets");
   public static final ResourceKey<StructureTemplatePool> SPRUCE_GOBLIN_VILLAGE_START = create("goblin_village/spruce/main");
   public static final ResourceKey<StructureTemplatePool> SPRUCE_GOBLIN_VILLAGE_BUILDING = create("goblin_village/spruce/buildings");
   public static final ResourceKey<StructureTemplatePool> SPRUCE_GOBLIN_VILLAGE_STREETS = create("goblin_village/spruce/streets");

   public static ResourceKey<StructureTemplatePool> create(String name) {
      return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
      Holder<StructureTemplatePool> emptyPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
      HolderGetter<StructureProcessorList> processors = context.lookup(Registries.PROCESSOR_LIST);
      ancientForests(context, emptyPool, processors);
      antNests(context, emptyPool, processors);
      charybdisCaves(context, emptyPool, processors);
      dwarfVillage(context, emptyPool, processors);
      goblinVillages(context, emptyPool, processors);
      hellStructures(context, emptyPool, processors);
      labyrinthTree(context, emptyPool, processors);
      lizardmanVillages(context, emptyPool, processors);
      orcVillages(context, emptyPool, processors);
      spiderNests(context, emptyPool, processors);
      ruinedTowers(context, emptyPool, processors);
      ruinedWapPads(context, emptyPool, processors);
   }

   public static void ancientForests(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> labyrinthTree1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_labyrinth_tree_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> labyrinthTree2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_labyrinth_tree_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tree6 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_tree_6")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ANCIENT_FOREST_TREE,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(labyrinthTree1, 1),
               Pair.of(labyrinthTree2, 1),
               Pair.of(tree1, 25),
               Pair.of(tree2, 25),
               Pair.of(tree3, 25),
               Pair.of(tree4, 25),
               Pair.of(tree5, 25),
               Pair.of(tree6, 25)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> mushroom1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_mushroom_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> mushroom2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_mushroom_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> mushroom3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_mushroom_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> mushroom4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_mushroom_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ANCIENT_FOREST_MUSHROOM,
         new StructureTemplatePool(
            emptyPool, ImmutableList.of(Pair.of(mushroom1, 4), Pair.of(mushroom2, 4), Pair.of(mushroom3, 1), Pair.of(mushroom4, 1)), Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> rock1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock6 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_6")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock7 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_7")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock8 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_8")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock9 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_9")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock10 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_10")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock11 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_11")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock12 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_12")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock13 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_13")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> rock14 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ancient_forest/ancient_forest_rock_14")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ANCIENT_FOREST_ROCK,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(rock1, 1),
               Pair.of(rock2, 1),
               Pair.of(rock3, 1),
               Pair.of(rock4, 1),
               Pair.of(rock5, 1),
               Pair.of(rock6, 1),
               Pair.of(rock7, 1),
               Pair.of(rock8, 1),
               Pair.of(rock9, 1),
               Pair.of(rock10, 1),
               Pair.of(rock11, 1),
               Pair.of(rock12, 1),
               new Pair[]{Pair.of(rock13, 1), Pair.of(rock14, 1)}
            ),
            Projection.RIGID
         )
      );
   }

   public static void antNests(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> fallback = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_fallback")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(ANT_NEST_FALLBACK, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(fallback, 1)), Projection.RIGID));
      Holder<StructureTemplatePool> fallbackPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(ANT_NEST_FALLBACK);
      Function<Projection, SinglePoolElement> hill = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_hill")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(ANT_NEST_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(hill, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> entranceRamp = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_entrance_ramp")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> corridor = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_i_corridor")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> lCorridor = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_l_corridor")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tCorridor = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_t_corridor")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xCorridor = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_x")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> ramp = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_ramp")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallRoom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_small_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigRoom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ant_nest/ant_nest_big_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ANT_NEST_PARTS,
         new StructureTemplatePool(
            fallbackPool,
            ImmutableList.of(
               Pair.of(entranceRamp, 1),
               Pair.of(corridor, 5),
               Pair.of(lCorridor, 3),
               Pair.of(tCorridor, 4),
               Pair.of(xCorridor, 3),
               Pair.of(ramp, 1),
               Pair.of(smallRoom, 3),
               Pair.of(bigRoom, 1)
            ),
            Projection.RIGID
         )
      );
   }

   public static void charybdisCaves(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> entrance = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_entrance")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(CHARYBDIS_CAVE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(entrance, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> entranceDesert = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_entrance_desert")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(CHARYBDIS_CAVE_DESERT_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(entranceDesert, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> entranceIce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_entrance_ice")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(CHARYBDIS_CAVE_ICE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(entranceIce, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> entranceMesa = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_entrance_mesa")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(CHARYBDIS_CAVE_MESA_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(entranceMesa, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> stairs = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_stairs")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stairsDesert = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_stairs_desert")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stairsIce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_stairs_ice")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stairsMesa = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_stairs_mesa")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         CHARYBDIS_CAVE_STAIRS,
         new StructureTemplatePool(
            emptyPool, ImmutableList.of(Pair.of(stairs, 1), Pair.of(stairsDesert, 1), Pair.of(stairsIce, 1), Pair.of(stairsMesa, 1)), Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> room = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room3End = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_3_end")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomDesert = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_desert")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomDesert2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_desert_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomDesert3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_desert_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomDesert4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_desert_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomIce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_ice")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomIce2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_ice_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomIce3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_ice_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomMesa = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_mesa")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomMesa2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_mesa_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomMesa3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_mesa_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomMesa4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_cave/charybdis_room_mesa_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         CHARYBDIS_CAVE_ROOMS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(room, 5),
               Pair.of(room2, 5),
               Pair.of(room3, 5),
               Pair.of(room3End, 5),
               Pair.of(room4, 5),
               Pair.of(room5, 5),
               Pair.of(roomDesert, 5),
               Pair.of(roomDesert2, 5),
               Pair.of(roomDesert3, 5),
               Pair.of(roomDesert4, 5),
               Pair.of(roomIce, 5),
               Pair.of(roomIce2, 5),
               new Pair[]{Pair.of(roomIce3, 5), Pair.of(roomMesa, 5), Pair.of(roomMesa2, 5), Pair.of(roomMesa3, 5), Pair.of(roomMesa4, 5)}
            ),
            Projection.RIGID
         )
      );
   }

   public static void dwarfVillage(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> spawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> royalGuard = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_royal_guard")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerDwarf = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_dwarf")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerGoblin = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_goblin")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerLizardman = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_lizardman")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerPillager = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_pillager")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerSlime = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_slime")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prisonerVillager = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/spawn/dwarf_village_prisoner_villager")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         DWARF_VILLAGE_SPAWN,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(spawn, 1),
               Pair.of(royalGuard, 1),
               Pair.of(prisonerDwarf, 3),
               Pair.of(prisonerGoblin, 4),
               Pair.of(prisonerLizardman, 4),
               Pair.of(prisonerPillager, 5),
               Pair.of(prisonerSlime, 1),
               Pair.of(prisonerVillager, 5)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> center = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_plaza")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(DWARF_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(center, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> xPath1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_x_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPath2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_x_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPath3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_x_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPath4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_x_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPath5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_x_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> plaza = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_plaza")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> iPath1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_i_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> iPath2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_i_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> iPath3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_i_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> iPath4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_i_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> iPath5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_i_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_t_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_t_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_t_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_t_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_path_t_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> dojoPath = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_dojo_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> farmRoad = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_village_farm_road")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> mineRoad = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> plazaPath = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_plaza_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         DWARF_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(xPath1, 3),
               Pair.of(xPath2, 3),
               Pair.of(xPath3, 3),
               Pair.of(xPath4, 3),
               Pair.of(xPath5, 3),
               Pair.of(iPath1, 4),
               Pair.of(iPath2, 4),
               Pair.of(iPath3, 4),
               Pair.of(iPath4, 4),
               Pair.of(iPath5, 4),
               Pair.of(tPath1, 5),
               Pair.of(tPath2, 4),
               new Pair[]{
                  Pair.of(tPath3, 4),
                  Pair.of(tPath4, 4),
                  Pair.of(tPath5, 4),
                  Pair.of(farmRoad, 2),
                  Pair.of(mineRoad, 3),
                  Pair.of(dojoPath, 2),
                  Pair.of(plazaPath, 1),
                  Pair.of(plaza, 1)
               }
            ),
            Projection.TERRAIN_MATCHING
         )
      );
      Function<Projection, SinglePoolElement> smallHouse1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallHouse2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallHouse3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallHouse4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallHouse5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallHouse6 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_small_6")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> largeHouse1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_large_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> largeHouse2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_house_large_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> butcher1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_butcher_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> butcher2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_butcher_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> butcher3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_butcher_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> library1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_library_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> library2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_library_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smithy1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_smithy_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smithy2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_smithy_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> guard1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_guard_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> guard2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_guard_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> fisherman = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_fisherman")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> lumberjack = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_lumberjack")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prison = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_prison")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shepherd = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_village_shepherd")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> dojo = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_dojo")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> magicTrainer = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_magic_trainer")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> royalTower = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_royal_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> beetroot1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_beetroot_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> beetroot2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_beetroot_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> carrot1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_carrot_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> carrot2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_carrot_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> potato1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_potato_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> potato2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_potato_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> wheat1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_wheat_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> wheat2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_field_wheat_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> mineBuilding = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_building")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> elevator1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_elevator_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> elevator2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_elevator_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> underground = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_underground")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> underground1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_underground_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> underground2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/mine/dwarf_mine_underground_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> fountain1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_fountain")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop6 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_6")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop7 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_7")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> shop8 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/plaza/dwarf_village_shop_8")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> cart = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_village_cart")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> merchantCart = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/road/dwarf_merchant_cart")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> farmHut = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/farms/dwarf_village_farm_hut")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         DWARF_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallHouse1, 10),
               Pair.of(smallHouse2, 10),
               Pair.of(smallHouse3, 10),
               Pair.of(smallHouse4, 10),
               Pair.of(smallHouse5, 10),
               Pair.of(smallHouse6, 10),
               Pair.of(largeHouse1, 6),
               Pair.of(largeHouse2, 6),
               Pair.of(butcher1, 4),
               Pair.of(butcher2, 4),
               Pair.of(butcher3, 4),
               Pair.of(library1, 4),
               new Pair[]{
                  Pair.of(library2, 4),
                  Pair.of(smithy1, 6),
                  Pair.of(smithy2, 6),
                  Pair.of(fisherman, 5),
                  Pair.of(guard1, 3),
                  Pair.of(guard2, 3),
                  Pair.of(lumberjack, 5),
                  Pair.of(prison, 5),
                  Pair.of(shepherd, 5),
                  Pair.of(magicTrainer, 3),
                  Pair.of(dojo, 3),
                  Pair.of(royalTower, 3),
                  Pair.of(beetroot1, 3),
                  Pair.of(beetroot2, 3),
                  Pair.of(carrot1, 3),
                  Pair.of(carrot2, 3),
                  Pair.of(potato1, 3),
                  Pair.of(potato2, 3),
                  Pair.of(wheat1, 3),
                  Pair.of(wheat2, 3),
                  Pair.of(farmHut, 1),
                  Pair.of(mineBuilding, 1),
                  Pair.of(elevator1, 1),
                  Pair.of(elevator2, 1),
                  Pair.of(underground, 1),
                  Pair.of(underground1, 1),
                  Pair.of(underground2, 1),
                  Pair.of(fountain1, 1),
                  Pair.of(shop1, 1),
                  Pair.of(shop2, 1),
                  Pair.of(shop3, 1),
                  Pair.of(shop4, 1),
                  Pair.of(shop5, 1),
                  Pair.of(shop6, 1),
                  Pair.of(shop7, 1),
                  Pair.of(shop8, 1),
                  Pair.of(cart, 5),
                  Pair.of(merchantCart, 4)
               }
            ),
            Projection.RIGID
         )
      );
   }

   public static void hellStructures(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> gateBottom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/gate/hell_gate_bottom")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(HELL_GATE, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(gateBottom, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> gateTop = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/gate/hell_gate_top")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> gateDoor = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/gate/hell_gate_door")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> gateSkull = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/gate/hell_gate_skull")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         HELL_GATE_PARTS,
         new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(gateTop, 1), Pair.of(gateDoor, 1), Pair.of(gateSkull, 1)), Projection.RIGID)
      );
      Function<Projection, SinglePoolElement> quartzRuin1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_pillar_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> quartzRuin2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_pillar_ruined_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchQuartz1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_gate_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchQuartz2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_gate_ruined_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchQuartz1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_gate_big_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchQuartz2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/quartz/hell_gate_big_ruined_quartz")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> sandRuin1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_pillar_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> sandRuin2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_pillar_ruined_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchSand1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_gate_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchSand2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_gate_ruined_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchSand1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_gate_big_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchSand2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/sand/hell_gate_big_ruined_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         HELL_SAND_RUIN,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(sandRuin1, 10),
               Pair.of(sandRuin2, 10),
               Pair.of(quartzRuin1, 10),
               Pair.of(quartzRuin2, 10),
               Pair.of(smallArchSand1, 6),
               Pair.of(smallArchSand2, 6),
               Pair.of(smallArchQuartz1, 6),
               Pair.of(smallArchQuartz2, 6),
               Pair.of(bigArchSand1, 2),
               Pair.of(bigArchSand2, 2),
               Pair.of(bigArchQuartz1, 2),
               Pair.of(bigArchQuartz2, 2),
               new Pair[0]
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> redSandRuin1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_pillar_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> redSandRuin2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_pillar_ruined_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchRedSand1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_gate_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> smallArchRedSand2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_gate_ruined_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchRedSand1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_gate_big_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchRedSand2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_gate_big_ruined_red_sandstone")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigArchRedSandLoot = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "hell/red_sand/hell_gate_big_ruined_red_sandstone_with_chest")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         HELL_RED_SAND_RUIN,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(redSandRuin1, 10),
               Pair.of(redSandRuin2, 10),
               Pair.of(quartzRuin1, 9),
               Pair.of(quartzRuin2, 9),
               Pair.of(smallArchRedSand1, 6),
               Pair.of(smallArchRedSand2, 6),
               Pair.of(smallArchQuartz1, 5),
               Pair.of(smallArchQuartz2, 5),
               Pair.of(bigArchRedSand1, 3),
               Pair.of(bigArchRedSand2, 3),
               Pair.of(bigArchRedSandLoot, 3),
               Pair.of(bigArchQuartz1, 2),
               new Pair[]{Pair.of(bigArchQuartz2, 2)}
            ),
            Projection.RIGID
         )
      );
   }

   public static void labyrinthTree(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> bottom1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth/tree/labyrinth_tree_bottom_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(LABYRINTH_TREE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(bottom1, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> top1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth/tree/labyrinth_tree_top_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> top2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth/tree/labyrinth_tree_top_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> top3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth/tree/labyrinth_tree_top_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> top4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth/tree/labyrinth_tree_top_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LABYRINTH_TREE_PARTS,
         new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(top1, 1), Pair.of(top2, 1), Pair.of(top3, 1), Pair.of(top4, 1)), Projection.RIGID)
      );
   }

   public static void spiderNests(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> surface = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_surface")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(SPIDER_NEST_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(surface, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> entrance = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_entrance")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stairs = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_stairs")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_room_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> entranceDungeon = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_entrance_dungeon")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stairsDungeon = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_stairs_dungeon")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roomDungeon = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "spider_nest/spider_nest_room_dungeon")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         SPIDER_NEST_PARTS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(entrance, 6),
               Pair.of(stairs, 5),
               Pair.of(room1, 6),
               Pair.of(room2, 5),
               Pair.of(entranceDungeon, 5),
               Pair.of(stairsDungeon, 6),
               Pair.of(roomDungeon, 5)
            ),
            Projection.RIGID
         )
      );
   }

   public static void ruinedWapPads(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> plains = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/warp/plains_warp_pad")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(PLAINS_WARP_PAD, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(plains, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> cold = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/warp/cold_warp_pad")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(COLD_WARP_PAD, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(cold, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> desert = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/warp/desert_warp_pad")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(DESERT_WARP_PAD, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(desert, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> mesa = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/warp/mesa_warp_pad")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(MESA_WARP_PAD, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(mesa, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> swamp = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/warp/swamp_warp_pad")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(SWAMP_WARP_PAD, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(swamp, 1)), Projection.RIGID));
   }

   public static void ruinedTowers(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> buried = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/tower/buried_wizard_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(BURIED_WIZARD_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(buried, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> burnt = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/tower/burnt_wizard_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(BURNT_WIZARD_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(burnt, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> frozen = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/tower/frozen_wizard_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(FROZEN_WIZARD_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(frozen, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> rotted = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/tower/rotted_wizard_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(ROTTED_WIZARD_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(rotted, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> ruined = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "ruin/tower/ruined_wizard_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(RUINED_WIZARD_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(ruined, 1)), Projection.RIGID));
   }

   public static void goblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> goblinSpawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/goblin_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(GOBLIN_VILLAGE_SPAWN, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(goblinSpawn, 1)), Projection.RIGID));
      acaciaGoblinVillages(context, emptyPool, processors);
      birchGoblinVillages(context, emptyPool, processors);
      jungleGoblinVillages(context, emptyPool, processors);
      oakGoblinVillages(context, emptyPool, processors);
      palmGoblinVillages(context, emptyPool, processors);
      spruceGoblinVillages(context, emptyPool, processors);
   }

   public static void acaciaGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_start_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ACACIA_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startAcacia, 1)), Projection.TERRAIN_MATCHING)
      );
      Function<Projection, SinglePoolElement> smallTentAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_tent_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_medical_tent_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_chief_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfireAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_campfire_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_tower_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_arch_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ACACIA_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentAcacia, 5),
               Pair.of(medicalTentAcacia, 1),
               Pair.of(chiefTentAcacia, 1),
               Pair.of(campfireAcacia, 1),
               Pair.of(towerAcacia, 1),
               Pair.of(archAcacia, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_end_acacia_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_end_acacia_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_end_acacia_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_acacia_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_acacia_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_acacia_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_t_acacia_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Acacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_t_acacia_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_x_acacia_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathAcacia = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/acacia/goblin_arch_path_acacia")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ACACIA_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Acacia, 2),
               Pair.of(endPath2Acacia, 2),
               Pair.of(endPath3Acacia, 1),
               Pair.of(path1Acacia, 5),
               Pair.of(path2Acacia, 5),
               Pair.of(path3Acacia, 5),
               Pair.of(tPath1Acacia, 3),
               Pair.of(tPath2Acacia, 2),
               Pair.of(xPathAcacia, 1),
               Pair.of(archPathAcacia, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void birchGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_start_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(BIRCH_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startBirch, 1)), Projection.TERRAIN_MATCHING));
      Function<Projection, SinglePoolElement> smallTentBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_tent_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_medical_tent_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_chief_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfireBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_campfire_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_tower_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_arch_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         BIRCH_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentBirch, 5),
               Pair.of(medicalTentBirch, 1),
               Pair.of(chiefTentBirch, 1),
               Pair.of(campfireBirch, 1),
               Pair.of(towerBirch, 1),
               Pair.of(archBirch, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_end_birch_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_end_birch_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_end_birch_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_birch_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_birch_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_birch_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_t_birch_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Birch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_t_birch_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_x_birch_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathBirch = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/birch/goblin_arch_path_birch")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         BIRCH_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Birch, 2),
               Pair.of(endPath2Birch, 2),
               Pair.of(endPath3Birch, 1),
               Pair.of(path1Birch, 5),
               Pair.of(path2Birch, 5),
               Pair.of(path3Birch, 5),
               Pair.of(tPath1Birch, 3),
               Pair.of(tPath2Birch, 2),
               Pair.of(xPathBirch, 1),
               Pair.of(archPathBirch, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void jungleGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_start_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         JUNGLE_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startJungle, 1)), Projection.TERRAIN_MATCHING)
      );
      Function<Projection, SinglePoolElement> smallTentJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_tent_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_medical_tent_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_chief_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfireJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_campfire_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_tower_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_arch_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         JUNGLE_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentJungle, 5),
               Pair.of(medicalTentJungle, 1),
               Pair.of(chiefTentJungle, 1),
               Pair.of(campfireJungle, 1),
               Pair.of(towerJungle, 1),
               Pair.of(archJungle, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_end_jungle_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_end_jungle_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_end_jungle_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_jungle_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_jungle_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_jungle_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_t_jungle_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Jungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_t_jungle_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_x_jungle_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathJungle = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/jungle/goblin_arch_path_jungle")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         JUNGLE_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Jungle, 2),
               Pair.of(endPath2Jungle, 2),
               Pair.of(endPath3Jungle, 1),
               Pair.of(path1Jungle, 5),
               Pair.of(path2Jungle, 5),
               Pair.of(path3Jungle, 5),
               Pair.of(tPath1Jungle, 3),
               Pair.of(tPath2Jungle, 2),
               Pair.of(xPathJungle, 1),
               Pair.of(archPathJungle, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void oakGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_start_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(OAK_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startOak, 1)), Projection.TERRAIN_MATCHING));
      Function<Projection, SinglePoolElement> smallTentOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_tent_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_medical_tent_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_chief_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfireOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_campfire_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_tower_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_arch_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         OAK_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentOak, 5),
               Pair.of(medicalTentOak, 1),
               Pair.of(chiefTentOak, 1),
               Pair.of(campfireOak, 1),
               Pair.of(towerOak, 1),
               Pair.of(archOak, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_end_oak_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_end_oak_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_end_oak_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_oak_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_oak_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_oak_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_t_oak_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Oak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_t_oak_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_x_oak_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathOak = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/oak/goblin_arch_path_oak")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         OAK_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Oak, 2),
               Pair.of(endPath2Oak, 2),
               Pair.of(endPath3Oak, 1),
               Pair.of(path1Oak, 5),
               Pair.of(path2Oak, 5),
               Pair.of(path3Oak, 5),
               Pair.of(tPath1Oak, 3),
               Pair.of(tPath2Oak, 2),
               Pair.of(xPathOak, 1),
               Pair.of(archPathOak, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void palmGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_start_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(PALM_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startPalm, 1)), Projection.TERRAIN_MATCHING));
      Function<Projection, SinglePoolElement> smallTentPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_tent_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_medical_tent_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_chief_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfirePalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_campfire_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_tower_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_arch_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         PALM_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentPalm, 5),
               Pair.of(medicalTentPalm, 1),
               Pair.of(chiefTentPalm, 1),
               Pair.of(campfirePalm, 1),
               Pair.of(towerPalm, 1),
               Pair.of(archPalm, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_end_palm_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_end_palm_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_end_palm_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_palm_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_palm_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_palm_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_t_palm_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Palm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_t_palm_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_x_palm_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathPalm = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/palm/goblin_arch_path_palm")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         PALM_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Palm, 2),
               Pair.of(endPath2Palm, 2),
               Pair.of(endPath3Palm, 1),
               Pair.of(path1Palm, 5),
               Pair.of(path2Palm, 5),
               Pair.of(path3Palm, 5),
               Pair.of(tPath1Palm, 3),
               Pair.of(tPath2Palm, 2),
               Pair.of(xPathPalm, 1),
               Pair.of(archPathPalm, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void spruceGoblinVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> startSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_start_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         SPRUCE_GOBLIN_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(startSpruce, 1)), Projection.TERRAIN_MATCHING)
      );
      Function<Projection, SinglePoolElement> smallTentSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_tent_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> medicalTentSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_medical_tent_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> chiefTentSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_chief_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfireSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_campfire_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> towerSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_tower_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_arch_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         SPRUCE_GOBLIN_VILLAGE_BUILDING,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(smallTentSpruce, 5),
               Pair.of(medicalTentSpruce, 1),
               Pair.of(chiefTentSpruce, 1),
               Pair.of(campfireSpruce, 1),
               Pair.of(towerSpruce, 1),
               Pair.of(archSpruce, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> endPath1Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_end_spruce_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath2Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_end_spruce_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> endPath3Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_end_spruce_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path1Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_spruce_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path2Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_spruce_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> path3Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_spruce_path_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath1Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_t_spruce_path_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tPath2Spruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_t_spruce_path_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> xPathSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_x_spruce_path")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> archPathSpruce = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_village/spruce/goblin_arch_path_spruce")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         SPRUCE_GOBLIN_VILLAGE_STREETS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(endPath1Spruce, 2),
               Pair.of(endPath2Spruce, 2),
               Pair.of(endPath3Spruce, 1),
               Pair.of(path1Spruce, 5),
               Pair.of(path2Spruce, 5),
               Pair.of(path3Spruce, 5),
               Pair.of(tPath1Spruce, 3),
               Pair.of(tPath2Spruce, 2),
               Pair.of(xPathSpruce, 1),
               Pair.of(archPathSpruce, 1)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
   }

   public static void lizardmanVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> lizardmanSpawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/lizardman_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hoverLizardSpawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/hover_lizard_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_SPAWN,
         new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(lizardmanSpawn, 1), Pair.of(hoverLizardSpawn, 1)), Projection.RIGID)
      );
      Function<Projection, SinglePoolElement> tower = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/lizardmen_tall_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(LIZARDMAN_TALL_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(tower, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> towerShort = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/lizardmen_short_tower")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(LIZARDMAN_SHORT_TOWER, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(towerShort, 1)), Projection.RIGID));
      undergroundLizardmanVillage(context, emptyPool, processors);
      waterLizardmanVillage(context, emptyPool, processors);
   }

   public static void undergroundLizardmanVillage(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Holder<StructureTemplatePool> fallbackPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(LIZARDMAN_VILLAGE_UNDERGROUND_FALLBACK);
      Holder<StructureTemplatePool> roomPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(LIZARDMAN_VILLAGE_UNDERGROUND_ROOMS);
      Function<Projection, SinglePoolElement> fallback = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_fallback")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> fallback2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_fallback_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_UNDERGROUND_FALLBACK,
         new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(fallback, 1), Pair.of(fallback2, 1)), Projection.RIGID)
      );
      Function<Projection, SinglePoolElement> entrance = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_entrance")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(LIZARDMAN_VILLAGE_UNDERGROUND_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(entrance, 1)), Projection.RIGID));
      Function<Projection, SinglePoolElement> stair1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_stairs_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stair2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_stairs_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stair3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_stairs_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stair4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_stairs_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> curvedStair1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_curved_stairs_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> curvedStair2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_curved_stairs_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> curvedStair3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_curved_stairs_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> curvedStair4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_curved_stairs_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_UNDERGROUND_ENTRANCE_STAIRS,
         new StructureTemplatePool(
            roomPool,
            ImmutableList.of(
               Pair.of(stair1, 1),
               Pair.of(stair2, 1),
               Pair.of(stair3, 1),
               Pair.of(stair4, 1),
               Pair.of(curvedStair1, 1),
               Pair.of(curvedStair2, 1),
               Pair.of(curvedStair3, 1),
               Pair.of(curvedStair4, 1)
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> hallway1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallway2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallway3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallway4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayCorner1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_corner_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayCorner2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_corner_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayCorner3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_corner_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayCorner4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_corner_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayT1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_t_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayT2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_t_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayT3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_t_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayT4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_t_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayX1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_x_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayX2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_x_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayX3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_x_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hallwayX4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hallway_x_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_UNDERGROUND_HALLWAYS,
         new StructureTemplatePool(
            roomPool,
            ImmutableList.of(
               Pair.of(stair1, 1),
               Pair.of(stair2, 1),
               Pair.of(stair3, 1),
               Pair.of(stair4, 1),
               Pair.of(curvedStair1, 1),
               Pair.of(curvedStair2, 1),
               Pair.of(curvedStair3, 1),
               Pair.of(curvedStair4, 1),
               Pair.of(hallway1, 5),
               Pair.of(hallway2, 5),
               Pair.of(hallway3, 5),
               Pair.of(hallway4, 5),
               new Pair[]{
                  Pair.of(hallwayCorner1, 2),
                  Pair.of(hallwayCorner2, 2),
                  Pair.of(hallwayCorner3, 1),
                  Pair.of(hallwayCorner4, 1),
                  Pair.of(hallwayT1, 6),
                  Pair.of(hallwayT2, 6),
                  Pair.of(hallwayT3, 7),
                  Pair.of(hallwayT4, 6),
                  Pair.of(hallwayX1, 5),
                  Pair.of(hallwayX2, 6),
                  Pair.of(hallwayX3, 6),
                  Pair.of(hallwayX4, 6)
               }
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> entranceStair = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_entrance_stairs")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> blackSmith1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_blacksmith_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> blackSmith2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_blacksmith_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> blackSmith3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_blacksmith_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> blackSmith4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_blacksmith_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> hayRoom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_hay_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> messHall1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_mess_hall_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> messHall2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_mess_hall_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> messHall3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_mess_hall_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prison1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_prison_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> prison2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_prison_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_room_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_room_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> room3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_room_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> stable = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_stable")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> thatchRoom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_thatch_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> throneRoom = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_throne_room")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_UNDERGROUND_ROOMS,
         new StructureTemplatePool(
            fallbackPool,
            ImmutableList.of(
               Pair.of(entranceStair, 1),
               Pair.of(blackSmith1, 2),
               Pair.of(blackSmith2, 2),
               Pair.of(blackSmith3, 2),
               Pair.of(blackSmith4, 2),
               Pair.of(hayRoom, 3),
               Pair.of(messHall1, 3),
               Pair.of(messHall2, 3),
               Pair.of(messHall3, 3),
               Pair.of(prison1, 3),
               Pair.of(prison2, 3),
               Pair.of(room1, 5),
               new Pair[]{Pair.of(room2, 5), Pair.of(room3, 5), Pair.of(stable, 3), Pair.of(thatchRoom, 3), Pair.of(throneRoom, 5)}
            ),
            Projection.RIGID
         )
      );
   }

   public static void waterLizardmanVillage(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Holder<StructureTemplatePool> buildingPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(LIZARDMAN_VILLAGE_WATER_BUILDINGS);
      Function<Projection, SinglePoolElement> roadX1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_x_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadX2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_x_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadX3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_x_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_WATER_START,
         new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(roadX1, 1), Pair.of(roadX2, 1), Pair.of(roadX3, 1)), Projection.RIGID)
      );
      Function<Projection, SinglePoolElement> road1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> road2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> road3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadCorner1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_corner_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadCorner2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_corner_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadCorner3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_corner_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadT1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_t_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadT2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_t_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadT3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_road_t_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_WATER_PATHS,
         new StructureTemplatePool(
            buildingPool,
            ImmutableList.of(
               Pair.of(road1, 5),
               Pair.of(road2, 5),
               Pair.of(road3, 5),
               Pair.of(roadCorner1, 2),
               Pair.of(roadCorner2, 2),
               Pair.of(roadCorner3, 1),
               Pair.of(roadT1, 5),
               Pair.of(roadT2, 5),
               Pair.of(roadT3, 5),
               Pair.of(roadX1, 4),
               Pair.of(roadX2, 4),
               Pair.of(roadX3, 4),
               new Pair[0]
            ),
            Projection.RIGID
         )
      );
      Function<Projection, SinglePoolElement> house1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> house2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> house3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> house4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> house5 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_5")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> house6 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_house_6")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> storage1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_storage_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> storage2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/underground/lizardmen_village_storage_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> pillar = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman_village/water/lizardmen_village_pillar")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.APPLY_WATERLOGGING)
      );
      context.register(
         LIZARDMAN_VILLAGE_WATER_BUILDINGS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(pillar, 1),
               Pair.of(house1, 3),
               Pair.of(house2, 3),
               Pair.of(house3, 3),
               Pair.of(house4, 3),
               Pair.of(house5, 3),
               Pair.of(house6, 3),
               Pair.of(storage1, 3),
               Pair.of(storage2, 3)
            ),
            Projection.RIGID
         )
      );
   }

   public static void orcVillages(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Function<Projection, SinglePoolElement> orcSpawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> orcLordSpawn = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_lord_spawn")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ORC_VILLAGE_SPAWN, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(orcSpawn, 100), Pair.of(orcLordSpawn, 1)), Projection.RIGID)
      );
      orcCamp(context, emptyPool, processors);
   }

   public static void orcCamp(
      BootstrapContext<StructureTemplatePool> context, Holder<StructureTemplatePool> emptyPool, HolderGetter<StructureProcessorList> processors
   ) {
      Holder<StructureTemplatePool> buildingPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(ORC_VILLAGE_BUILDS);
      Function<Projection, SinglePoolElement> roadX1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_x_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadX2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_x_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ORC_VILLAGE_START, new StructureTemplatePool(emptyPool, ImmutableList.of(Pair.of(roadX1, 1), Pair.of(roadX2, 1)), Projection.TERRAIN_MATCHING)
      );
      Function<Projection, SinglePoolElement> road1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> road2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadCorner1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_corner_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadCorner2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_corner_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadT1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_t_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> roadT2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_road_t_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.TERRAIN_MATCHING,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ORC_VILLAGE_PATHS,
         new StructureTemplatePool(
            buildingPool,
            ImmutableList.of(
               Pair.of(road1, 4),
               Pair.of(road2, 4),
               Pair.of(roadCorner1, 2),
               Pair.of(roadCorner2, 2),
               Pair.of(roadT1, 5),
               Pair.of(roadT2, 5),
               Pair.of(roadX1, 5),
               Pair.of(roadX2, 5)
            ),
            Projection.TERRAIN_MATCHING
         )
      );
      Function<Projection, SinglePoolElement> tent1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_small_tent_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tent2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_small_tent_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tent3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_small_tent_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> tent4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_small_tent_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigTent1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_big_tent_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigTent2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_big_tent_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigTent3 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_big_tent_3")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> bigTent4 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_big_tent_4")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> storage = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_storage")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfire1 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_campfire_1")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      Function<Projection, SinglePoolElement> campfire2 = projection -> new SinglePoolElement(
         Either.left(ResourceLocation.fromNamespaceAndPath("tensura", "orc_village/orc_village_campfire_2")),
         processors.getOrThrow(ProcessorLists.EMPTY),
         Projection.RIGID,
         Optional.of(LiquidSettings.IGNORE_WATERLOGGING)
      );
      context.register(
         ORC_VILLAGE_BUILDS,
         new StructureTemplatePool(
            emptyPool,
            ImmutableList.of(
               Pair.of(tent1, 5),
               Pair.of(tent2, 5),
               Pair.of(tent3, 5),
               Pair.of(tent4, 5),
               Pair.of(bigTent1, 3),
               Pair.of(bigTent2, 3),
               Pair.of(bigTent3, 3),
               Pair.of(bigTent4, 3),
               Pair.of(storage, 4),
               Pair.of(campfire1, 2),
               Pair.of(campfire2, 2)
            ),
            Projection.RIGID
         )
      );
   }
}
