package io.github.manasmods.tensura.registry.world;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.world.structure.JigsawMinHeightStructure;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class TensuraStructures {
   public static final ResourceKey<Structure> ANCIENT_FOREST_TREE = create("ancient_forest/tree");
   public static final ResourceKey<Structure> ANCIENT_FOREST_MUSHROOM = create("ancient_forest/mushroom");
   public static final ResourceKey<Structure> ANCIENT_FOREST_ROCK = create("ancient_forest/rock");
   public static final ResourceKey<Structure> ANT_NEST = create("nests/giant_ant");
   public static final ResourceKey<Structure> CHARYBDIS_CAVE = create("charybdis_cave/plains");
   public static final ResourceKey<Structure> CHARYBDIS_CAVE_DESERT = create("charybdis_cave/desert");
   public static final ResourceKey<Structure> CHARYBDIS_CAVE_ICE = create("charybdis_cave/ice");
   public static final ResourceKey<Structure> CHARYBDIS_CAVE_MESA = create("charybdis_cave/mesa");
   public static final ResourceKey<Structure> DWARF_VILLAGE = create("villages/dwarf_village");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_ACACIA = create("villages/goblin_village/acacia");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_BIRCH = create("villages/goblin_village/birch");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_JUNGLE = create("villages/goblin_village/jungle");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_OAK = create("villages/goblin_village/oak");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_PALM = create("villages/goblin_village/palm");
   public static final ResourceKey<Structure> GOBLIN_VILLAGE_SPRUCE = create("villages/goblin_village/spruce");
   public static final ResourceKey<Structure> HELL_GATE = create("hell/hell_gate");
   public static final ResourceKey<Structure> HELL_SAND_RUIN = create("hell/sand_ruin");
   public static final ResourceKey<Structure> HELL_RED_SAND_RUIN = create("hell/red_sand_ruin");
   public static final ResourceKey<Structure> LABYRINTH_TREE = create("labyrinth/labyrinth_tree");
   public static final ResourceKey<Structure> LIZARDMAN_TALL_TOWER = create("villages/lizardmen_village/tower_tall");
   public static final ResourceKey<Structure> LIZARDMAN_SHORT_TOWER = create("villages/lizardmen_village/tower_short");
   public static final ResourceKey<Structure> LIZARDMAN_VILLAGE_UNDERGROUND = create("villages/lizardmen_village/underground");
   public static final ResourceKey<Structure> LIZARDMAN_VILLAGE_WATER = create("villages/lizardmen_village/water");
   public static final ResourceKey<Structure> ORC_VILLAGE = create("villages/orc_village");
   public static final ResourceKey<Structure> SPIDER_NEST = create("nests/black_spider");
   public static final ResourceKey<Structure> PLAINS_WARP_PAD = create("ruin/warp/plains_warp_pad");
   public static final ResourceKey<Structure> COLD_WARP_PAD = create("ruin/warp/cold_warp_pad");
   public static final ResourceKey<Structure> DESERT_WARP_PAD = create("ruin/warp/desert_warp_pad");
   public static final ResourceKey<Structure> MESA_WARP_PAD = create("ruin/warp/mesa_warp_pad");
   public static final ResourceKey<Structure> SWAMP_WARP_PAD = create("ruin/warp/swamp_warp_pad");
   public static final ResourceKey<Structure> BURIED_WIZARD_TOWER = create("ruin/wizard_tower/buried");
   public static final ResourceKey<Structure> BURNT_WIZARD_TOWER = create("ruin/wizard_tower/burnt");
   public static final ResourceKey<Structure> FROZEN_WIZARD_TOWER = create("ruin/wizard_tower/frozen");
   public static final ResourceKey<Structure> ROTTED_WIZARD_TOWER = create("ruin/wizard_tower/rotted");
   public static final ResourceKey<Structure> RUINED_WIZARD_TOWER = create("ruin/wizard_tower/ruined");

   public static void bootstrap(BootstrapContext<Structure> context) {
      HolderGetter<StructureTemplatePool> poolGetter = context.lookup(Registries.TEMPLATE_POOL);
      HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
      ancientForests(context, poolGetter, biomeGetter);
      antNests(context, poolGetter, biomeGetter);
      charybdisCaves(context, poolGetter, biomeGetter);
      dwarfVillage(context, poolGetter, biomeGetter);
      goblinVillages(context, poolGetter, biomeGetter);
      hellStructures(context, poolGetter, biomeGetter);
      labyrinthTree(context, poolGetter, biomeGetter);
      lizardmanVillages(context, poolGetter, biomeGetter);
      orcVillages(context, poolGetter, biomeGetter);
      ruinedTowers(context, poolGetter, biomeGetter);
      ruinedWarpPads(context, poolGetter, biomeGetter);
      spiderNests(context, poolGetter, biomeGetter);
   }

   public static void ancientForests(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      context.register(
         ANCIENT_FOREST_TREE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.ANCIENT_FOREST),
               Map.of(MobCategory.CREATURE, emptySpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ANCIENT_FOREST_TREE),
            Optional.empty(),
            2,
            ConstantHeight.of(VerticalAnchor.absolute(-5)),
            false,
            Optional.of(Types.OCEAN_FLOOR_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         ANCIENT_FOREST_MUSHROOM,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.ANCIENT_FOREST),
               Map.of(MobCategory.CREATURE, emptySpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ANCIENT_FOREST_MUSHROOM),
            Optional.empty(),
            2,
            ConstantHeight.of(VerticalAnchor.absolute(-3)),
            false,
            Optional.of(Types.OCEAN_FLOOR_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         ANCIENT_FOREST_ROCK,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.ANCIENT_FOREST),
               Map.of(MobCategory.CREATURE, emptySpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ANCIENT_FOREST_ROCK),
            Optional.empty(),
            2,
            ConstantHeight.of(VerticalAnchor.absolute(-4)),
            false,
            Optional.of(Types.OCEAN_FLOOR_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void antNests(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride antSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)MonsterEntityTypes.GIANT_ANT.get(), 1, 3, 5)})
      );
      context.register(
         ANT_NEST,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.ANT_NEST),
               Map.of(MobCategory.CREATURE, antSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ANT_NEST_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(-9)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            100,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void charybdisCaves(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      context.register(
         CHARYBDIS_CAVE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.CHARYBDIS_CAVE),
               Map.of(MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.CHARYBDIS_CAVE_START),
            Optional.empty(),
            7,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            116,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         CHARYBDIS_CAVE_DESERT,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.CHARYBDIS_CAVE_DESERT),
               Map.of(MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.CHARYBDIS_CAVE_DESERT_START),
            Optional.empty(),
            7,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            80,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         CHARYBDIS_CAVE_ICE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.CHARYBDIS_CAVE_ICE),
               Map.of(MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.CHARYBDIS_CAVE_ICE_START),
            Optional.empty(),
            7,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            80,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         CHARYBDIS_CAVE_MESA,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.CHARYBDIS_CAVE_MESA),
               Map.of(MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.CHARYBDIS_CAVE_MESA_START),
            Optional.empty(),
            7,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            80,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void dwarfVillage(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride dwarfSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)HumanEntityTypes.DWARF.get(), 1, 1, 2)})
      );
      context.register(
         DWARF_VILLAGE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.DWARF_VILLAGE),
               Map.of(MobCategory.CREATURE, dwarfSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.DWARF_VILLAGE_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            100,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void goblinVillages(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride goblinSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)MonsterEntityTypes.GOBLIN.get(), 1, 1, 2)})
      );
      context.register(
         GOBLIN_VILLAGE_ACACIA,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_ACACIA),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ACACIA_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         GOBLIN_VILLAGE_BIRCH,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_BIRCH),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.BIRCH_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         GOBLIN_VILLAGE_JUNGLE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_JUNGLE),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.JUNGLE_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         GOBLIN_VILLAGE_OAK,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_OAK),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.OAK_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         GOBLIN_VILLAGE_PALM,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_PALM),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.PALM_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         GOBLIN_VILLAGE_SPRUCE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.GOBLIN_VILLAGE_SPRUCE),
               Map.of(MobCategory.CREATURE, goblinSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.SPRUCE_GOBLIN_VILLAGE_START),
            Optional.empty(),
            10,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
   }

   public static void hellStructures(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      context.register(
         HELL_GATE,
         new JigsawMinHeightStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.HELL_GATE),
               Map.of(MobCategory.MONSTER, emptySpawn, MobCategory.CREATURE, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.HELL_GATE),
            Optional.empty(),
            4,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            116,
            20,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         HELL_SAND_RUIN,
         new JigsawMinHeightStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.HELL_SAND_RUIN), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.HELL_SAND_RUIN),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            80,
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         HELL_RED_SAND_RUIN,
         new JigsawMinHeightStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.HELL_RED_SAND_RUIN), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.HELL_RED_SAND_RUIN),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            80,
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void labyrinthTree(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      context.register(
         LABYRINTH_TREE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.LABYRINTH_TREE),
               Map.of(MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.LABYRINTH_TREE_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(-5)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            116,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void lizardmanVillages(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride lizardmanSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)MonsterEntityTypes.LIZARDMAN.get(), 1, 1, 2)})
      );
      context.register(
         LIZARDMAN_TALL_TOWER,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.LIZARDMAN_VILLAGE_SURFACE),
               Map.of(MobCategory.MONSTER, emptySpawn, MobCategory.CREATURE, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.LIZARDMAN_TALL_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         LIZARDMAN_SHORT_TOWER,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.LIZARDMAN_VILLAGE_UNDERGROUND),
               Map.of(MobCategory.MONSTER, emptySpawn, MobCategory.CREATURE, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.LIZARDMAN_SHORT_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         LIZARDMAN_VILLAGE_UNDERGROUND,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.LIZARDMAN_VILLAGE_UNDERGROUND),
               Map.of(MobCategory.MONSTER, emptySpawn, MobCategory.CREATURE, lizardmanSpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.LIZARDMAN_VILLAGE_UNDERGROUND_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            40,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         LIZARDMAN_VILLAGE_WATER,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.LIZARDMAN_VILLAGE_SURFACE),
               Map.of(MobCategory.MONSTER, emptySpawn, MobCategory.CREATURE, lizardmanSpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.NONE
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.LIZARDMAN_VILLAGE_WATER_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            30,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
   }

   public static void orcVillages(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride orcSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)MonsterEntityTypes.ORC.get(), 1, 3, 5)})
      );
      context.register(
         ORC_VILLAGE,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.ORC_VILLAGE),
               Map.of(MobCategory.CREATURE, orcSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ORC_VILLAGE_START),
            Optional.empty(),
            20,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            20,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void ruinedTowers(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      context.register(
         BURIED_WIZARD_TOWER,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.IS_DESERT), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
            poolGetter.getOrThrow(TensuraTemplatePools.BURIED_WIZARD_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-30)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         BURNT_WIZARD_TOWER,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(BiomeTags.IS_BADLANDS), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
            poolGetter.getOrThrow(TensuraTemplatePools.BURNT_WIZARD_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         FROZEN_WIZARD_TOWER,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.IS_COLD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
            poolGetter.getOrThrow(TensuraTemplatePools.FROZEN_WIZARD_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-10)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         ROTTED_WIZARD_TOWER,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.HAS_ROTTEN_TREASURE), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.ROTTED_WIZARD_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-2)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
      context.register(
         RUINED_WIZARD_TOWER,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(BiomeTags.IS_FOREST), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
            poolGetter.getOrThrow(TensuraTemplatePools.RUINED_WIZARD_TOWER),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static void ruinedWarpPads(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      context.register(
         PLAINS_WARP_PAD,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.PLAINS_WARP_PAD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
            poolGetter.getOrThrow(TensuraTemplatePools.PLAINS_WARP_PAD),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         COLD_WARP_PAD,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.COLD_WARP_PAD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
            poolGetter.getOrThrow(TensuraTemplatePools.COLD_WARP_PAD),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         DESERT_WARP_PAD,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.DESERT_WARP_PAD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
            poolGetter.getOrThrow(TensuraTemplatePools.DESERT_WARP_PAD),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         MESA_WARP_PAD,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.MESA_WARP_PAD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
            poolGetter.getOrThrow(TensuraTemplatePools.MESA_WARP_PAD),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
      context.register(
         SWAMP_WARP_PAD,
         new JigsawStructure(
            new StructureSettings(biomeGetter.getOrThrow(TensuraBiomeTags.SWAMP_WARP_PAD), Map.of(), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
            poolGetter.getOrThrow(TensuraTemplatePools.SWAMP_WARP_PAD),
            Optional.empty(),
            1,
            ConstantHeight.of(VerticalAnchor.absolute(-1)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            10,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         )
      );
   }

   public static void spiderNests(BootstrapContext<Structure> context, HolderGetter<StructureTemplatePool> poolGetter, HolderGetter<Biome> biomeGetter) {
      StructureSpawnOverride emptySpawn = new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      StructureSpawnOverride spiderSpawn = new StructureSpawnOverride(
         BoundingBoxType.STRUCTURE, WeightedRandomList.create(new SpawnerData[]{new SpawnerData((EntityType)MonsterEntityTypes.BLACK_SPIDER.get(), 1, 3, 5)})
      );
      context.register(
         SPIDER_NEST,
         new JigsawStructure(
            new StructureSettings(
               biomeGetter.getOrThrow(TensuraBiomeTags.BLACK_SPIDER_NEST),
               Map.of(MobCategory.CREATURE, spiderSpawn, MobCategory.MONSTER, emptySpawn),
               Decoration.SURFACE_STRUCTURES,
               TerrainAdjustment.BEARD_BOX
            ),
            poolGetter.getOrThrow(TensuraTemplatePools.SPIDER_NEST_START),
            Optional.empty(),
            7,
            ConstantHeight.of(VerticalAnchor.absolute(0)),
            false,
            Optional.of(Types.WORLD_SURFACE_WG),
            116,
            List.of(),
            DimensionPadding.ZERO,
            LiquidSettings.IGNORE_WATERLOGGING
         )
      );
   }

   public static ResourceKey<Structure> create(String name) {
      return ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
