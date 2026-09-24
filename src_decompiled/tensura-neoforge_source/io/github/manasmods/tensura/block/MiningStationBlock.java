package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.tensura.block.entity.MiningStationBlockEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiningStationBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<MiningStationBlock> CODEC = simpleCodec(properties -> new MiningStationBlock());
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public MiningStationBlock() {
      super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).noOcclusion().instrument(NoteBlockInstrument.BASS).strength(5.0F));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   @NotNull
   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   @NotNull
   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @NotNull
   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   protected void onRemove(BlockState oldState, Level level, BlockPos blockPos, BlockState newState, boolean bl) {
      if (oldState.getBlock() != newState.getBlock() && level.getBlockEntity(blockPos) instanceof MiningStationBlockEntity content) {
         content.dropContent();
      }

      super.onRemove(oldState, level, blockPos, newState, bl);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      return level.isClientSide() ? InteractionResult.sidedSuccess(true) : this.openMenu((ServerPlayer)player, level, blockPos);
   }

   private InteractionResult openMenu(ServerPlayer player, Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof MiningStationBlockEntity blockEntity) {
         MenuRegistry.openExtendedMenu(player, blockEntity, buf -> buf.writeBlockPos(pos));
         return InteractionResult.sidedSuccess(false);
      } else {
         throw new IllegalStateException(
            String.format("Container provider for Mining Station block is missing!\n Position: %s; World: %s", pos, level.dimension())
         );
      }
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MiningStationBlockEntity(blockPos, blockState);
   }

   @NotNull
   protected RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
      return createTickerHelper(blockEntityType, (BlockEntityType)TensuraBlockEntities.MINING_STATION.get(), MiningStationBlockEntity::tick);
   }
}
