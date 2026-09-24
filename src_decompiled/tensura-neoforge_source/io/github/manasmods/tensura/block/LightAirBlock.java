package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightAirBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public LightAirBlock() {
      super(Properties.of().air().replaceable().sound(SoundType.AMETHYST).noCollission().noLootTable().noOcclusion().lightLevel(blockState -> 15));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return !pContext.isHoldingItem(Items.LIGHT) && !pContext.isHoldingItem(Items.AIR) ? Shapes.empty() : Shapes.block();
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pos, RandomSource pRandom) {
      ClientHelper.spawnMarkerParticle(pLevel, pState, pos, Items.LIGHT);
   }

   protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
   }

   public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return true;
   }

   public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return 1.0F;
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      pLevel.destroyBlock(pPos, false);
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
      pBuilder.add(new Property[]{WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }
}
