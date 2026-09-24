package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.tensura.block.entity.SpellbindingBlockEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpellbindingBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<SpellbindingBlock> CODEC = simpleCodec(properties -> new SpellbindingBlock());
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public SpellbindingBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .sound(SoundType.LODESTONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 7)
            .strength(5.0F, 1200.0F)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   protected boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   @NotNull
   protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
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

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock() && pLevel.getBlockEntity(pPos) instanceof SpellbindingBlockEntity entity) {
         entity.drops();
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      return level.isClientSide() ? InteractionResult.sidedSuccess(true) : this.openMenu((ServerPlayer)player, level, blockPos);
   }

   private InteractionResult openMenu(ServerPlayer player, Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof SpellbindingBlockEntity blockEntity) {
         MenuRegistry.openExtendedMenu(player, blockEntity, buf -> buf.writeBlockPos(pos));
         return InteractionResult.sidedSuccess(false);
      } else {
         throw new IllegalStateException(
            String.format("Container provider for Spellbinding table is missing!\n Position: %s; World: %s", pos, level.dimension())
         );
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SpellbindingBlockEntity(pos, state);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      super.animateTick(blockState, level, blockPos, randomSource);
      if (level.getBlockEntity(blockPos) instanceof SpellbindingBlockEntity blockEntity && !blockEntity.isEmpty()) {
         TensuraParticleHelper.spawnEnchantingTableParticle(level, Vec3.atCenterOf(blockPos.above(2)), ParticleTypes.ENCHANT, 32);
      }
   }
}
