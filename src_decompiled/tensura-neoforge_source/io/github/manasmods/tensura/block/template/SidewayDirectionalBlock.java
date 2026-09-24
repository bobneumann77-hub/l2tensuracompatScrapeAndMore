package io.github.manasmods.tensura.block.template;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class SidewayDirectionalBlock extends DirectionalBlock {
   public static final MapCodec<SidewayDirectionalBlock> CODEC = simpleCodec(SidewayDirectionalBlock::new);

   public SidewayDirectionalBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   protected MapCodec<? extends DirectionalBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING});
   }

   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
   }

   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
   }
}
