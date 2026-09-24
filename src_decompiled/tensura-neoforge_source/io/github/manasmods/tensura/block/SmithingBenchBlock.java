package io.github.manasmods.tensura.block;

import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.tensura.menu.SmithingBenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmithingBenchBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final MutableComponent TITLE = Component.translatable("block.tensura.smithing_bench");

   public SmithingBenchBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(3.0F)
            .sound(SoundType.WOOD)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .noOcclusion()
            .pushReaction(PushReaction.BLOCK)
      );
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

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, this.isWaterAtPosition(pContext.getLevel(), pContext.getClickedPos()));
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

   private boolean isWaterAtPosition(Level level, BlockPos blockPos) {
      return level.getFluidState(blockPos).is(Fluids.WATER);
   }

   @NotNull
   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (level.isClientSide()) {
         return InteractionResult.sidedSuccess(true);
      }

      MenuRegistry.openExtendedMenu(
         (ServerPlayer)player,
         new SimpleMenuProvider((i, inventory, pPlayer) -> new SmithingBenchMenu(i, inventory, ContainerLevelAccess.create(pPlayer.level(), blockPos)), TITLE),
         buf -> {}
      );
      return InteractionResult.sidedSuccess(false);
   }
}
