package io.github.manasmods.tensura.world.features;

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
import org.jetbrains.annotations.Nullable;

public class FloatingDebrisFeature extends Feature<NoneFeatureConfiguration> {
   public FloatingDebrisFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      BlockPos pos = context.origin();
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      if (pos.getY() < 84 || pos.getY() > 128) {
         int newY = 84 + random.nextIntBetweenInclusive(2, 26);
         pos = new BlockPos(pos.getX(), newY, pos.getZ());
      }

      BlockState QUARTZ = Blocks.QUARTZ_BLOCK.defaultBlockState();
      BlockState QUARTZ_PILLAR = Blocks.QUARTZ_PILLAR.defaultBlockState();
      BlockPos previousPos = pos;

      for (int i = 0; i < 24 && (!(random.nextFloat() < 0.2F) || i <= 10); i++) {
         Direction direction = this.getDirection(level, pos, previousPos, random);
         if (direction == null) {
            break;
         }

         pos = pos.relative(direction);
         previousPos = pos;
         level.setBlock(pos, random.nextFloat() > 0.5F ? QUARTZ : QUARTZ_PILLAR, 4);
      }

      return false;
   }

   @Nullable
   private Direction getDirection(WorldGenLevel level, BlockPos pos, BlockPos previousPos, RandomSource random) {
      MutableBlockPos candidate = new MutableBlockPos();

      for (int i = 0; i < 4; i++) {
         Direction direction = Direction.getRandom(random);
         candidate.set(pos.getX() + direction.getStepX(), pos.getY() + direction.getStepY(), pos.getZ() + direction.getStepZ());
         boolean flag1 = candidate.equals(previousPos);
         boolean flag2 = !level.isEmptyBlock(candidate);
         if (!flag1 && !flag2) {
            return direction;
         }
      }

      return null;
   }
}
