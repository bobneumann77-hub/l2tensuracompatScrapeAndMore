package io.github.manasmods.tensura.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BedBlockEntity.class)
public abstract class MixinBedBlockEntity extends BlockEntity {
   public MixinBedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   public boolean isValidBlockState(BlockState blockState) {
      return blockState.is(BlockTags.BEDS) ? true : super.isValidBlockState(blockState);
   }
}
