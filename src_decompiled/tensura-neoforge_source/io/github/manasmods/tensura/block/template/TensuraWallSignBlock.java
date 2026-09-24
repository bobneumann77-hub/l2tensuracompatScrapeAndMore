package io.github.manasmods.tensura.block.template;

import io.github.manasmods.tensura.block.entity.TensuraSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TensuraWallSignBlock extends WallSignBlock {
   public TensuraWallSignBlock(WoodType woodType, Properties properties) {
      super(woodType, properties);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new TensuraSignBlockEntity(pPos, pState);
   }
}
