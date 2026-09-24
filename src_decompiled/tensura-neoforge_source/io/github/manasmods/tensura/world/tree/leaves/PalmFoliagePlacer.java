package io.github.manasmods.tensura.world.tree.leaves;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import io.github.manasmods.tensura.registry.world.TensuraFoliagePlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageSetter;

public class PalmFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<PalmFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> palmParts(instance).apply(instance, PalmFoliagePlacer::new));
   protected final int height;

   protected static <P extends PalmFoliagePlacer> P3<Mu<P>, IntProvider, IntProvider, Integer> palmParts(Instance<P> p_68414_) {
      return foliagePlacerParts(p_68414_).and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height));
   }

   public PalmFoliagePlacer(IntProvider pRadius, IntProvider pOffset, int height) {
      super(pRadius, pOffset);
      this.height = height;
   }

   protected FoliagePlacerType<PalmFoliagePlacer> type() {
      return (FoliagePlacerType<PalmFoliagePlacer>)TensuraFoliagePlacers.PALM_FOLIAGE.get();
   }

   public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
      return this.height;
   }

   protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
      return false;
   }

   protected void createFoliage(
      LevelSimulatedReader pLevel,
      FoliageSetter pBlockSetter,
      RandomSource pRandom,
      TreeConfiguration pConfig,
      int pMaxFreeTreeHeight,
      FoliageAttachment pAttachment,
      int pFoliageHeight,
      int pFoliageRadius,
      int pOffset
   ) {
      BlockPos base = pAttachment.pos();
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base);
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base.north());
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base.east());
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base.south());
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, base.west());
      this.cycleAround(pLevel, pBlockSetter, pRandom, pConfig, base.below());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(0, -2, -3));
      BlockPos lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(0, -1, -2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.north());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(0, -1, -1));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(3, -2, 0));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(2, -1, 0));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.east());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(1, -1, 0));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(0, -2, 3));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(0, -1, 2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.south());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(0, -1, 1));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-3, -2, 0));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-2, -1, 0));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.west());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(-1, -1, 0));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(2, -2, -2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(2, -1, -2));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(1, -1, -1));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(2, -2, 2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(2, -1, 2));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(1, -1, 1));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-2, -2, 2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-2, -1, 2));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(-1, -1, 1));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-2, -2, -2));
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, base.offset(-2, -1, -2));
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.offset(-1, -1, -1));
   }

   private void cycleAround(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, BlockPos pPos) {
      BlockPos lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, pPos);
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.north());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.east());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.south());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.south());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.west());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.west());
      lastPos = placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.north());
      placeAndContinue(pLevel, pBlockSetter, pRandom, pConfig, lastPos.north());
   }

   private static BlockPos placeAndContinue(
      LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, BlockPos pPos
   ) {
      tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, pPos);
      return pPos;
   }
}
