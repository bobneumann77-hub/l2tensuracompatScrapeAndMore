package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TensuraSignBlockEntity extends SignBlockEntity {
   public TensuraSignBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(pWorldPosition, pBlockState);
   }

   public boolean isValidBlockState(BlockState blockState) {
      if (blockState.is((Block)TensuraBlocks.PALM_STANDING_SIGN.get())) {
         return true;
      } else {
         return blockState.is((Block)TensuraBlocks.PALM_WALL_SIGN.get()) ? true : super.isValidBlockState(blockState);
      }
   }

   @NotNull
   public BlockEntityType<?> getType() {
      return (BlockEntityType<?>)TensuraBlockEntities.SIGN.get();
   }
}
