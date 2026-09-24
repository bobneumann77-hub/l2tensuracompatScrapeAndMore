package io.github.manasmods.tensura.world.tree.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.world.TensuraTrunkPlacers;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class PalmTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<PalmTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> trunkPlacerParts(instance).apply(instance, PalmTrunkPlacer::new)
   );

   public PalmTrunkPlacer(int pBaseHeight, int pHeightRandA, int pHeightRandB) {
      super(pBaseHeight, pHeightRandA, pHeightRandB);
   }

   protected TrunkPlacerType<?> type() {
      return (TrunkPlacerType<?>)TensuraTrunkPlacers.PALM_TRUNK_PLACER.get();
   }

   public List<FoliageAttachment> placeTrunk(
      LevelSimulatedReader pLevel,
      BiConsumer<BlockPos, BlockState> pBlockSetter,
      RandomSource pRandom,
      int pFreeTreeHeight,
      BlockPos pPos,
      TreeConfiguration pConfig
   ) {
      setDirtAt(pLevel, pBlockSetter, pRandom, pPos.below(), pConfig);
      int randomNumber = pRandom.nextInt(4) + 1;

      Direction treeDirection = switch (randomNumber) {
         case 1 -> Direction.NORTH;
         case 2 -> Direction.EAST;
         case 3 -> Direction.SOUTH;
         case 4 -> Direction.WEST;
         default -> throw new IllegalStateException("Calculated " + randomNumber + " but it should be between 1 and 4");
      };
      int currentHeight = 1;
      BlockPos lastPos = this.placeAndContinue(pLevel, pBlockSetter, pRandom, pPos, pConfig);
      lastPos = this.placeAndContinue(pLevel, pBlockSetter, pRandom, lastPos.relative(treeDirection), pConfig);
      lastPos = this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight++);
      lastPos = this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight++);
      lastPos = this.placeAndContinue(pLevel, pBlockSetter, pRandom, lastPos.relative(treeDirection), pConfig);
      lastPos = this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight++);
      lastPos = this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight++);
      lastPos = this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight++);
      lastPos = this.placeAndContinue(pLevel, pBlockSetter, pRandom, lastPos.relative(treeDirection), pConfig);

      while (!lastPos.equals(this.placeAndContinueIf(pLevel, pBlockSetter, pRandom, lastPos.above(), pConfig, pFreeTreeHeight, currentHeight))) {
         if (++currentHeight > 64) {
            break;
         }
      }

      return ImmutableList.of(new FoliageAttachment(lastPos.above().above(), 0, false));
   }

   private BlockPos placeAndContinue(
      LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig
   ) {
      this.placeLog(pLevel, pBlockSetter, pRandom, pPos, pConfig);
      return pPos;
   }

   private BlockPos placeAndContinueIf(
      LevelSimulatedReader pLevel,
      BiConsumer<BlockPos, BlockState> pBlockSetter,
      RandomSource pRandom,
      BlockPos pPos,
      TreeConfiguration pConfig,
      int maxHeight,
      int allowedHeight
   ) {
      return maxHeight >= allowedHeight ? this.placeAndContinue(pLevel, pBlockSetter, pRandom, pPos, pConfig) : pPos.below();
   }
}
