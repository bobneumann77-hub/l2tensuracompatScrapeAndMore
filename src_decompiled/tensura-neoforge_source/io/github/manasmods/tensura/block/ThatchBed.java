package io.github.manasmods.tensura.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ThatchBed extends BedBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

   public ThatchBed(Properties pProperties) {
      super(DyeColor.YELLOW, pProperties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, Boolean.FALSE))
            .setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
      pEntity.causeFallDamage(pEntity.fallDistance, 0.75F, pEntity.damageSources().fall());
   }

   public void updateEntityAfterFallOn(BlockGetter pLevel, Entity entity) {
      entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return BASE;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pNewState.is(this) && !pLevel.isClientSide) {
         BlockPos blockpos = pPos.relative(getNeighbourDirection((BedPart)pState.getValue(PART), (Direction)pState.getValue(FACING)));
         BlockState blockstate = pLevel.getBlockState(blockpos);
         if (blockstate.is(this)) {
            BlockState replacement = blockstate.getValue(WATERLOGGED) ? Fluids.WATER.defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState();
            pLevel.setBlock(blockpos, replacement, 35);
         }
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   private static Direction getNeighbourDirection(BedPart pPart, Direction pDirection) {
      return pPart == BedPart.FOOT ? pDirection : pDirection.getOpposite();
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return pathComputationType.equals(PathComputationType.WATER) ? state.getFluidState().is(FluidTags.WATER) : true;
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, PART, OCCUPIED}).add(new Property[]{WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockState state = super.getStateForPlacement(pContext);
      if (state == null) {
         return null;
      }

      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }
}
