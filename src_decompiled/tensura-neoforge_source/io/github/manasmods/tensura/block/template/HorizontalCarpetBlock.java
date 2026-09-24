package io.github.manasmods.tensura.block.template;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HorizontalCarpetBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<HorizontalCarpetBlock> CODEC = simpleCodec(HorizontalCarpetBlock::new);
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

   public MapCodec<? extends HorizontalCarpetBlock> codec() {
      return CODEC;
   }

   public HorizontalCarpetBlock(Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected BlockState updateShape(
      BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2
   ) {
      return !blockState.canSurvive(levelAccessor, blockPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
   }

   protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return !levelReader.isEmptyBlock(blockPos.below());
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
