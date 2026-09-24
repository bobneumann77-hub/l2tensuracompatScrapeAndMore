package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.menu.WoodcutterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoodcutterBlock extends Block implements SimpleWaterloggedBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   public WoodcutterBlock() {
      super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).instrument(NoteBlockInstrument.BANJO).strength(2.5F));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE)).setValue(FACING, Direction.NORTH)
      );
   }

   protected boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected BlockState rotate(BlockState blockState, Rotation rotation) {
      return (BlockState)blockState.setValue(FACING, rotation.rotate((Direction)blockState.getValue(FACING)));
   }

   protected BlockState mirror(BlockState blockState, Mirror mirror) {
      return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{WATERLOGGED, FACING});
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

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      }

      player.openMenu(blockState.getMenuProvider(level, blockPos));
      return InteractionResult.CONSUME;
   }

   @Nullable
   protected MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return new SimpleMenuProvider(
         (i, inventory, player) -> new WoodcutterMenu(i, inventory, ContainerLevelAccess.create(level, blockPos)),
         Component.translatable("block.tensura.woodcutter")
      );
   }
}
