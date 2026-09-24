package io.github.manasmods.tensura.block.template;

import io.github.manasmods.tensura.block.entity.TensuraHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TensuraWallHangingSignBlock extends WallHangingSignBlock {
   public TensuraWallHangingSignBlock(WoodType woodType, Properties properties) {
      super(woodType, properties);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new TensuraHangingSignBlockEntity(pPos, pState);
   }
}
