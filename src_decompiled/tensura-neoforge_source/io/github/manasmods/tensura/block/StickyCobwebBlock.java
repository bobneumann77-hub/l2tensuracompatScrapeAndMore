package io.github.manasmods.tensura.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class StickyCobwebBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public StickyCobwebBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PERSISTENT, Boolean.FALSE)).setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      pEntity.makeStuckInBlock(pState, new Vec3(0.2, 0.04F, 0.2));
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      pLevel.destroyBlock(pPos, false);
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{PERSISTENT, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }
}
