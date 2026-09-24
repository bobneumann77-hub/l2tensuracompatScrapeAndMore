package io.github.manasmods.tensura.registry.world;

import io.github.manasmods.tensura.block.template.TensuraSapling;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class TensuraPlacedFeatures {
   public static final ResourceKey<PlacedFeature> DEAD_OAK = create("dead_oak");
   public static final ResourceKey<PlacedFeature> PALM_TREE = create("palm_tree");
   public static final ResourceKey<PlacedFeature> PALM_TREE_RARE = create("palm_tree_rare");
   public static final ResourceKey<PlacedFeature> ORE_SILVER_SMALL = create("ore_silver_small");
   public static final ResourceKey<PlacedFeature> ORE_SILVER_LARGE = create("ore_silver_large");
   public static final ResourceKey<PlacedFeature> ORE_SILVER_BURIED = create("ore_silver_buried");
   public static final ResourceKey<PlacedFeature> ORE_MAGIC = create("ore_magic");
   public static final ResourceKey<PlacedFeature> ORE_MAGIC_BURIED = create("ore_magic_buried");
   public static final ResourceKey<PlacedFeature> ORE_MAGIC_EXTRA = create("ore_magic_extra");
   public static final ResourceKey<PlacedFeature> HIPOKUTE_GRASS = create("hipokute_grass");
   public static final ResourceKey<PlacedFeature> HIPOKUTE_GRASS_COMMON = create("hipokute_grass_common");
   public static final ResourceKey<PlacedFeature> HIPOKUTE_GRASS_UNCOMMON = create("hipokute_grass_uncommon");
   public static final ResourceKey<PlacedFeature> BAFFLEDIL = create("baffledil");
   public static final ResourceKey<PlacedFeature> BAFFLEDIL_COMMON = create("baffledil_common");
   public static final ResourceKey<PlacedFeature> COMMON_FERN_PATCHES = create("common_fern_patches");
   public static final ResourceKey<PlacedFeature> COMMON_TALL_GRASS_PATCHES = create("common_tall_grass_patches");
   public static final ResourceKey<PlacedFeature> COMMON_BROWN_MUSHROOM_PATCHES = create("common_brown_mushroom_patches");
   public static final ResourceKey<PlacedFeature> COMMON_RED_MUSHROOM_PATCHES = create("common_red_mushroom_patches");
   public static final ResourceKey<PlacedFeature> FLOATING_DEBRIS = create("floating_debris");
   public static final ResourceKey<PlacedFeature> HELL_BLOCK_BLOB = create("hell_block_blob");
   public static final ResourceKey<PlacedFeature> ROCK_SPIKE = create("rock_spike");

   public static void bootstrap(BootstrapContext<PlacedFeature> context) {
      HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
      PlacementUtils.register(
         context,
         DEAD_OAK,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.DEAD_OAK),
         VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1F, 1), Blocks.OAK_SAPLING)
      );
      PlacementUtils.register(
         context,
         PALM_TREE,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.PALM_TREE),
         VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.2F, 1), (Block)TensuraBlocks.PALM_SAPLING.get())
      );
      PlacementUtils.register(
         context,
         PALM_TREE_RARE,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.PALM_TREE),
         new PlacementModifier[]{
            RarityFilter.onAverageOnceEvery(300),
            CountPlacement.of(1),
            InSquarePlacement.spread(),
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            BlockPredicateFilter.forPredicate(
               BlockPredicate.wouldSurvive(((TensuraSapling)TensuraBlocks.PALM_SAPLING.get()).defaultBlockState(), BlockPos.ZERO)
            ),
            BiomeFilter.biome()
         }
      );
      PlacementUtils.register(
         context,
         ORE_SILVER_SMALL,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_SILVER_SMALL),
         commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)))
      );
      PlacementUtils.register(
         context,
         ORE_SILVER_LARGE,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_SILVER_LARGE),
         rareOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-24), VerticalAnchor.aboveBottom(56)))
      );
      PlacementUtils.register(
         context,
         ORE_SILVER_BURIED,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_SILVER_BURIED),
         commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)))
      );
      PlacementUtils.register(
         context,
         ORE_MAGIC,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_MAGIC),
         rareOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)))
      );
      PlacementUtils.register(
         context,
         ORE_MAGIC_BURIED,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_MAGIC_BURIED),
         commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)))
      );
      PlacementUtils.register(
         context,
         ORE_MAGIC_EXTRA,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ORE_MAGIC),
         rareOrePlacement(1, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(120)))
      );
      PlacementUtils.register(
         context,
         HIPOKUTE_GRASS,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.HIPOKUTE_GRASS),
         new PlacementModifier[]{
            RarityFilter.onAverageOnceEvery(20),
            CountPlacement.of(20),
            InSquarePlacement.spread(),
            PlacementUtils.FULL_RANGE,
            EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 10),
            BiomeFilter.biome()
         }
      );
      PlacementUtils.register(
         context,
         HIPOKUTE_GRASS_COMMON,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.HIPOKUTE_GRASS),
         new PlacementModifier[]{
            RarityFilter.onAverageOnceEvery(2),
            CountPlacement.of(20),
            InSquarePlacement.spread(),
            PlacementUtils.FULL_RANGE,
            EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 10),
            BiomeFilter.biome()
         }
      );
      PlacementUtils.register(
         context,
         HIPOKUTE_GRASS_UNCOMMON,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.HIPOKUTE_GRASS),
         new PlacementModifier[]{
            RarityFilter.onAverageOnceEvery(8),
            CountPlacement.of(20),
            InSquarePlacement.spread(),
            PlacementUtils.FULL_RANGE,
            EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 10),
            BiomeFilter.biome()
         }
      );
      PlacementUtils.register(
         context,
         BAFFLEDIL,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.BAFFLEDIL),
         new PlacementModifier[]{RarityFilter.onAverageOnceEvery(15), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()}
      );
      PlacementUtils.register(
         context,
         BAFFLEDIL_COMMON,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.BAFFLEDIL),
         new PlacementModifier[]{RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()}
      );
      PlacementUtils.register(
         context,
         COMMON_FERN_PATCHES,
         configuredFeatures.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
         List.of(CountPlacement.of(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         COMMON_TALL_GRASS_PATCHES,
         configuredFeatures.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
         List.of(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         COMMON_BROWN_MUSHROOM_PATCHES,
         configuredFeatures.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM),
         List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         COMMON_RED_MUSHROOM_PATCHES,
         configuredFeatures.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM),
         List.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         FLOATING_DEBRIS,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.FLOATING_DEBRIS),
         List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         HELL_BLOCK_BLOB,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.HELL_BLOCK_BLOB),
         List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())
      );
      PlacementUtils.register(
         context,
         ROCK_SPIKE,
         configuredFeatures.getOrThrow(TensuraConfiguredFeatures.ROCK_SPIKE),
         List.of(CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())
      );
   }

   public static ResourceKey<PlacedFeature> create(String name) {
      return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   private static List<PlacementModifier> commonOrePlacement(int amount, HeightRangePlacement placement) {
      return orePlacement(CountPlacement.of(amount), placement);
   }

   private static List<PlacementModifier> rareOrePlacement(int chance, HeightRangePlacement placement) {
      return orePlacement(RarityFilter.onAverageOnceEvery(chance), placement);
   }

   private static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, HeightRangePlacement heightRangePlacement) {
      return List.of(placementModifier, InSquarePlacement.spread(), heightRangePlacement, BiomeFilter.biome());
   }
}
