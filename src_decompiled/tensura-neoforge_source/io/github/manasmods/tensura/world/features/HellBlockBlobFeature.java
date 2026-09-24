package io.github.manasmods.tensura.world.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class HellBlockBlobFeature extends Feature<NoneFeatureConfiguration> {
   private final ImmutableList<BlockState> BLOCKS = ImmutableList.of(
      Blocks.TUFF.defaultBlockState(), Blocks.COBBLESTONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.GRAVEL.defaultBlockState()
   );

   public HellBlockBlobFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      BlockPos origin = context.origin();
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      MutableBlockPos cursor = origin.mutable();
      MutableBlockPos below = new MutableBlockPos();

      while (cursor.getY() > level.getMinBuildHeight() + 3) {
         below.set(cursor.getX(), cursor.getY() - 1, cursor.getZ());
         if (level.isEmptyBlock(below)) {
            cursor.move(Direction.DOWN);
         } else {
            if (this.isValidPosition(level, below)) {
               break;
            }

            cursor.move(Direction.DOWN);
         }
      }

      BlockPos pos = cursor.immutable();
      if (pos.getY() <= level.getMinBuildHeight() + 3) {
         return false;
      }

      BlockState state = (BlockState)this.BLOCKS.get(random.nextIntBetweenInclusive(0, this.BLOCKS.size() - 1));

      for (int i = 0; i < 3; i++) {
         int i1 = random.nextInt(2);
         int i2 = random.nextInt(2);
         int i3 = random.nextInt(2);
         float v = (i1 + i2 + i3) * 0.333F + 0.5F;

         for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-i1, -i2, -i3), pos.offset(i1, i2, i3))) {
            if (!(blockPos.distSqr(pos) >= v * v)) {
               level.setBlock(blockPos, state, 4);
            }
         }

         pos = pos.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
      }

      return true;
   }

   private boolean isValidPosition(WorldGenLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.is(Blocks.STONE) || state.is(Blocks.GRANITE) || state.is(Blocks.ANDESITE) || state.is(Blocks.COBBLESTONE);
   }
}
