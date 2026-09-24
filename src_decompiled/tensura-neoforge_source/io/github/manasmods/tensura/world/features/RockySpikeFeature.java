package io.github.manasmods.tensura.world.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RockySpikeFeature extends Feature<NoneFeatureConfiguration> {
   private final ImmutableList<Block> SET_1 = ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.ANDESITE);
   private final ImmutableList<Block> SET_2 = ImmutableList.of(Blocks.COBBLESTONE, Blocks.ANDESITE, Blocks.STONE);
   private final ImmutableList<Block> SET_3 = ImmutableList.of(Blocks.ANDESITE, Blocks.STONE, Blocks.COBBLESTONE);

   public RockySpikeFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      BlockPos pos = context.origin();
      RandomSource random = context.random();

      ImmutableList<Block> blocks = switch (random.nextIntBetweenInclusive(1, 3)) {
         case 1 -> this.SET_1;
         case 2 -> this.SET_2;
         case 3 -> this.SET_3;
         default -> ImmutableList.of();
      };
      if (blocks.isEmpty()) {
         return false;
      }

      BlockState block1 = ((Block)blocks.get(0)).defaultBlockState();
      BlockState block2 = ((Block)blocks.get(1)).defaultBlockState();
      BlockState block3 = ((Block)blocks.get(2)).defaultBlockState();
      WorldGenLevel level = context.level();
      MutableBlockPos descent = pos.mutable();

      while (level.isEmptyBlock(descent) && descent.getY() > level.getMinBuildHeight() + 2) {
         descent.move(Direction.DOWN);
      }

      pos = descent.immutable();
      pos = pos.above(random.nextInt(4));
      int i = random.nextInt(4) + 7;
      int i1 = i / 4 + random.nextInt(2);
      if (i1 > 1 && random.nextInt(60) == 0) {
         pos = pos.above(10 + random.nextInt(30));
      }

      MutableBlockPos cursor = new MutableBlockPos();

      for (int i2 = 0; i2 < i; i2++) {
         float v = (1.0F - (float)i2 / i) * i1;
         int i3 = Mth.ceil(v);

         for (int i4 = -i3; i4 <= i3; i4++) {
            float v1 = Mth.abs(i4) - 0.25F;

            for (int i5 = -i3; i5 <= i3; i5++) {
               float v2 = Mth.abs(i5) - 0.25F;
               if ((i4 == 0 && i5 == 0 || !(v1 * v1 + v2 * v2 > v * v)) && (i4 != -i3 && i4 != i3 && i5 != -i3 && i5 != i3 || !(random.nextFloat() > 0.75F))) {
                  cursor.set(pos.getX() + i4, pos.getY() + i2, pos.getZ() + i5);
                  this.setBlock(level, cursor, block1);
                  if (i2 != 0 && i3 > 1) {
                     cursor.set(pos.getX() + i4, pos.getY() - i2, pos.getZ() + i5);
                     this.setBlock(level, cursor, block2);
                  }
               }
            }
         }
      }

      int var22 = i1 - 1;
      if (var22 < 0) {
         var22 = 0;
      } else if (var22 > 1) {
         var22 = 1;
      }

      MutableBlockPos descentCursor = new MutableBlockPos();
      int hardCap = 0;

      for (int i4 = -var22; i4 <= var22; i4++) {
         for (int i3 = -var22; i3 <= var22; i3++) {
            descentCursor.set(pos.getX() + i4, pos.getY() - 1, pos.getZ() + i3);
            int i5 = 50;
            if (Math.abs(i4) == 1 && Math.abs(i3) == 1) {
               i5 = random.nextInt(5);
            }

            hardCap = 0;

            while (descentCursor.getY() > 50 && hardCap++ < 300) {
               this.setBlock(level, descentCursor, block3);
               descentCursor.move(Direction.DOWN);
               if (--i5 <= 0) {
                  descentCursor.move(Direction.DOWN, random.nextInt(5) + 1);
                  i5 = random.nextInt(5);
               }
            }
         }
      }

      return true;
   }
}
