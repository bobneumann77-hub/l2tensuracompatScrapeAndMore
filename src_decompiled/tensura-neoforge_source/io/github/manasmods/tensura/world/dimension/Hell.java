package io.github.manasmods.tensura.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import io.github.manasmods.tensura.registry.dimension.TensuraNoises;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class Hell {
   public static class GenerationNoises extends NoiseRouterData {
      private static final DensityFunction ZERO = DensityFunctions.zero();

      private static DensityFunction slide(DensityFunction func, int i, int i1, int i2, int i3, double v, int i4, int i5, double v1) {
         DensityFunction toReturn = func;
         DensityFunction func1 = DensityFunctions.yClampedGradient(i + i1 - i2, i + i1 - i3, 1.0, 0.0);
         toReturn = DensityFunctions.lerp(func1, v, toReturn);
         DensityFunction func2 = DensityFunctions.yClampedGradient(i + i4, i + i5, 0.0, 1.0);
         return DensityFunctions.lerp(func2, v1, toReturn);
      }

      private static DensityFunction slideEndLike(DensityFunction func, int i, int i1) {
         return slide(func, i, i1, 72, -184, -23.4375, 4, 32, -0.234375);
      }

      private static NoiseRouter hell(HolderGetter<NoiseParameters> holderGetter) {
         DensityFunction uFunc1 = DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(holderGetter.getOrThrow(Noises.SHIFT))));
         DensityFunction uFunc2 = DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(holderGetter.getOrThrow(Noises.SHIFT))));
         DensityFunction temperature = DensityFunctions.flatCache(
            DensityFunctions.shiftedNoise2d(uFunc1, uFunc2, 0.25, holderGetter.getOrThrow(Noises.TEMPERATURE))
         );
         DensityFunction vegetation = DensityFunctions.flatCache(
            DensityFunctions.shiftedNoise2d(uFunc1, uFunc2, 0.25, holderGetter.getOrThrow(Noises.VEGETATION))
         );
         DensityFunction continentalness = DensityFunctions.flatCache(
            DensityFunctions.shiftedNoise2d(uFunc1, uFunc2, 0.25, holderGetter.getOrThrow(Noises.CONTINENTALNESS))
         );
         DensityFunction erosion = DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(uFunc1, uFunc2, 0.25, holderGetter.getOrThrow(Noises.EROSION)));
         DensityFunction uFunc3 = DensityFunctions.add(
            DensityFunctions.constant(0.05), DensityFunctions.noise(holderGetter.getOrThrow(TensuraNoises.HELL), 0.6, 1.0)
         );
         DensityFunction uFunc4 = DensityFunctions.add(DensityFunctions.constant(0.4), uFunc3);
         DensityFunction uFunc5 = DensityFunctions.mul(DensityFunctions.yClampedGradient(16, 128, 1.0, 0.0), uFunc4);
         DensityFunction uFunc6 = DensityFunctions.add(DensityFunctions.constant(-0.2), uFunc5);
         DensityFunction uFunc7 = DensityFunctions.add(DensityFunctions.constant(0.1), uFunc6);
         DensityFunction uFunc8 = DensityFunctions.mul(DensityFunctions.yClampedGradient(0, 48, 0.0, 1.0), uFunc7);
         DensityFunction uFunc9 = DensityFunctions.add(DensityFunctions.constant(-0.1), uFunc8);
         DensityFunction uFunc10 = DensityFunctions.add(DensityFunctions.constant(-0.05), uFunc9);
         DensityFunction finalDensity = DensityFunctions.interpolated(DensityFunctions.blendDensity(slideEndLike(uFunc10, 0, 128))).squeeze();
         return new NoiseRouter(ZERO, ZERO, ZERO, ZERO, temperature, vegetation, continentalness, erosion, ZERO, ZERO, ZERO, finalDensity, ZERO, ZERO, ZERO);
      }

      public static NoiseGeneratorSettings hell(BootstrapContext<?> bootstrapContext) {
         return new NoiseGeneratorSettings(
            NoiseSettings.create(0, 128, 2, 1),
            Blocks.BUDDING_AMETHYST.defaultBlockState(),
            Blocks.AIR.defaultBlockState(),
            hell(bootstrapContext.lookup(Registries.NOISE)),
            Hell.Rules.HELL_DIMENSION,
            List.of(),
            0,
            false,
            false,
            false,
            false
         );
      }
   }

   public static class Rules {
      private static final RuleSource SAND = makeStateRule(Blocks.SAND);
      private static final RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
      private static final RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
      private static final RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
      private static final RuleSource STONE = makeStateRule(Blocks.STONE);
      private static final RuleSource DIORITE = makeStateRule(Blocks.DIORITE);
      private static final RuleSource GRANITE = makeStateRule(Blocks.GRANITE);
      private static final RuleSource ANDESITE = makeStateRule(Blocks.ANDESITE);
      public static final RuleSource HELL_DIMENSION = hell();

      private static RuleSource makeStateRule(Block pBlock) {
         return SurfaceRules.state(pBlock.defaultBlockState());
      }

      private static RuleSource hell() {
         ConditionSource onCeilingLayer = SurfaceRules.ON_CEILING;
         ConditionSource underCeilingLayer = SurfaceRules.UNDER_CEILING;
         ConditionSource topLayer = SurfaceRules.ON_FLOOR;
         ConditionSource middleLayer = SurfaceRules.UNDER_FLOOR;
         ConditionSource preBottomLayer = SurfaceRules.DEEP_UNDER_FLOOR;
         ConditionSource bottomLayer = SurfaceRules.VERY_DEEP_UNDER_FLOOR;
         ConditionSource barrens = SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.UNDERWORLD_BARRENS});
         RuleSource barrensSurface = SurfaceRules.sequence(
            new RuleSource[]{
               SurfaceRules.ifTrue(topLayer, STONE), SurfaceRules.ifTrue(middleLayer, GRANITE), SurfaceRules.ifTrue(bottomLayer, DIORITE), ANDESITE
            }
         );
         ConditionSource spikes = SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.UNDERWORLD_SPIKES});
         RuleSource spikesSurface = SurfaceRules.sequence(
            new RuleSource[]{
               SurfaceRules.ifTrue(topLayer, ANDESITE), SurfaceRules.ifTrue(middleLayer, STONE), SurfaceRules.ifTrue(bottomLayer, GRANITE), DIORITE
            }
         );
         ConditionSource sands = SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.UNDERWORLD_SANDS});
         RuleSource sandsSurface = SurfaceRules.sequence(new RuleSource[]{SurfaceRules.ifTrue(topLayer, SAND), SANDSTONE});
         ConditionSource redSands = SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.UNDERWORLD_RED_SANDS});
         RuleSource redSandSurface = SurfaceRules.sequence(new RuleSource[]{SurfaceRules.ifTrue(topLayer, RED_SAND), RED_SANDSTONE});
         Builder<RuleSource> builder = ImmutableList.builder();
         builder.add(SurfaceRules.ifTrue(barrens, barrensSurface));
         builder.add(SurfaceRules.ifTrue(spikes, spikesSurface));
         builder.add(SurfaceRules.ifTrue(sands, sandsSurface));
         builder.add(SurfaceRules.ifTrue(redSands, redSandSurface));
         return SurfaceRules.sequence((RuleSource[])builder.build().toArray(RuleSource[]::new));
      }
   }
}
