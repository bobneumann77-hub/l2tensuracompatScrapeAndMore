package io.github.manasmods.tensura.registry.world;

import com.google.common.base.Suppliers;
import io.github.manasmods.tensura.block.HipokuteGrass;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.world.features.FloatingDebrisFeature;
import io.github.manasmods.tensura.world.features.HellBlockBlobFeature;
import io.github.manasmods.tensura.world.features.RockySpikeFeature;
import io.github.manasmods.tensura.world.tree.leaves.PalmFoliagePlacer;
import io.github.manasmods.tensura.world.tree.trunk.PalmTrunkPlacer;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class TensuraConfiguredFeatures {
   public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_OAK = create("dead_oak");
   public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_TREE = create("palm_tree");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILVER_SMALL = create("ore_silver_small");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILVER_LARGE = create("ore_silver_large");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILVER_BURIED = create("ore_silver_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MAGIC = create("ore_magic");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MAGIC_BURIED = create("ore_magic_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> HIPOKUTE_GRASS = create("hipokute_grass");
   public static final ResourceKey<ConfiguredFeature<?, ?>> BAFFLEDIL = create("baffledil");
   public static final ResourceKey<ConfiguredFeature<?, ?>> FLOATING_DEBRIS = create("floating_debris");
   public static final ResourceKey<ConfiguredFeature<?, ?>> HELL_BLOCK_BLOB = create("hell_block_blob");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ROCK_SPIKE = create("rock_spike");

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
      Supplier<List<TargetBlockState>> ORE_SILVER_TARGET_LIST = Suppliers.memoize(
         () -> List.of(
            OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), ((Block)TensuraBlocks.SILVER_ORE.get()).defaultBlockState()),
            OreConfiguration.target(
               new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), ((Block)TensuraBlocks.DEEPSLATE_SILVER_ORE.get()).defaultBlockState()
            )
         )
      );
      Supplier<List<TargetBlockState>> ORE_MAGIC_TARGET_LIST = Suppliers.memoize(
         () -> List.of(
            OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), ((Block)TensuraBlocks.MAGIC_ORE.get()).defaultBlockState()),
            OreConfiguration.target(
               new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), ((Block)TensuraBlocks.DEEPSLATE_MAGIC_ORE.get()).defaultBlockState()
            )
         )
      );
      FeatureUtils.register(
         context,
         DEAD_OAK,
         Feature.TREE,
         new TreeConfigurationBuilder(
               BlockStateProvider.simple(Blocks.OAK_WOOD),
               new ForkingTrunkPlacer(5, 2, 2),
               BlockStateProvider.simple(Blocks.AIR),
               new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
               new TwoLayersFeatureSize(1, 0, 2)
            )
            .ignoreVines()
            .build()
      );
      FeatureUtils.register(
         context,
         PALM_TREE,
         Feature.TREE,
         new TreeConfigurationBuilder(
               BlockStateProvider.simple((Block)TensuraBlocks.PALM_LOG.get()),
               new PalmTrunkPlacer(11, 2, 0),
               BlockStateProvider.simple((Block)TensuraBlocks.PALM_LEAVES.get()),
               new PalmFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
               new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
            )
            .build()
      );
      FeatureUtils.register(context, ORE_SILVER_SMALL, Feature.ORE, new OreConfiguration(ORE_SILVER_TARGET_LIST.get(), 9));
      FeatureUtils.register(context, ORE_SILVER_LARGE, Feature.ORE, new OreConfiguration(ORE_SILVER_TARGET_LIST.get(), 12, 0.7F));
      FeatureUtils.register(context, ORE_SILVER_BURIED, Feature.ORE, new OreConfiguration(ORE_SILVER_TARGET_LIST.get(), 9, 0.5F));
      FeatureUtils.register(context, ORE_MAGIC, Feature.ORE, new OreConfiguration(ORE_MAGIC_TARGET_LIST.get(), 4));
      FeatureUtils.register(context, ORE_MAGIC_BURIED, Feature.ORE, new OreConfiguration(ORE_MAGIC_TARGET_LIST.get(), 4, 0.5F));
      FeatureUtils.register(
         context,
         HIPOKUTE_GRASS,
         Feature.FLOWER,
         new RandomPatchConfiguration(
            40,
            3,
            3,
            PlacementUtils.onlyWhenEmpty(
               Feature.SIMPLE_BLOCK,
               new SimpleBlockConfiguration(
                  new NoiseThresholdProvider(
                     2345L,
                     new NoiseParameters(0, 1.0, new double[0]),
                     0.005F,
                     -0.8F,
                     0.33333334F,
                     ((HipokuteGrass)TensuraBlocks.HIPOKUTE_GRASS.get()).getStateForAge(2),
                     List.of(((HipokuteGrass)TensuraBlocks.HIPOKUTE_GRASS.get()).getStateForAge(3)),
                     List.of(
                        ((HipokuteGrass)TensuraBlocks.HIPOKUTE_GRASS.get()).getStateForAge(2),
                        ((HipokuteGrass)TensuraBlocks.HIPOKUTE_GRASS.get()).getStateForAge(1)
                     )
                  )
               )
            )
         )
      );
      FeatureUtils.register(
         context,
         BAFFLEDIL,
         Feature.FLOWER,
         new RandomPatchConfiguration(
            32,
            6,
            2,
            PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple((Block)TensuraBlocks.BAFFLEDIL.get())))
         )
      );
      FeatureUtils.register(context, FLOATING_DEBRIS, (FloatingDebrisFeature)TensuraFeatures.FLOATING_DEBRIS.get(), new NoneFeatureConfiguration());
      FeatureUtils.register(context, HELL_BLOCK_BLOB, (HellBlockBlobFeature)TensuraFeatures.HELL_BLOCK_BLOB.get(), new NoneFeatureConfiguration());
      FeatureUtils.register(context, ROCK_SPIKE, (RockySpikeFeature)TensuraFeatures.ROCKY_SPIKE.get(), new NoneFeatureConfiguration());
   }

   public static ResourceKey<ConfiguredFeature<?, ?>> create(String name) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
