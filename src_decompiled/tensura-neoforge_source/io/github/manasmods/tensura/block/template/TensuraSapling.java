package io.github.manasmods.tensura.block.template;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class TensuraSapling extends SaplingBlock {
   public TensuraSapling(TreeGrower treeGrower) {
      super(treeGrower, Properties.of().mapColor(MapColor.GRASS).ignitedByLava().noCollission().noOcclusion().randomTicks().instabreak().sound(SoundType.GRASS));
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      BlockPos blockpos = pPos.below();
      BlockState state = pLevel.getBlockState(blockpos);
      return state.is(BlockTags.SAND) ? true : this.mayPlaceOn(state, pLevel, blockpos);
   }
}
