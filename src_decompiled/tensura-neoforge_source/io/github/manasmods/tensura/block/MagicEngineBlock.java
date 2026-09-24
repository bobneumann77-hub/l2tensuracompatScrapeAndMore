package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.block.entity.MagicEngineBlockEntity;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import java.util.function.ToIntFunction;
import lombok.Generated;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicEngineBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   public static final MapCodec<MagicEngineBlock> CODEC = simpleCodec(MagicEngineBlock::new);
   private static final VoxelShape SHAPE_DOWN = Block.box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_EAST = Block.box(0.0, 0.0, 0.0, 17.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_WEST = Block.box(-1.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 17.0);
   private static final VoxelShape SHAPE_NORTH = Block.box(0.0, 0.0, -1.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_UP = Block.box(0.0, 0.0, 0.0, 16.0, 17.0, 16.0);
   private final double magiculeReduction;
   private final double reductionRange;
   private final boolean creativeOnly;

   public MagicEngineBlock(double reduction, double range, boolean creativeOnly, Properties properties) {
      super(properties);
      this.magiculeReduction = reduction;
      this.reductionRange = range;
      this.creativeOnly = creativeOnly;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(ENABLED, Boolean.FALSE)
      );
   }

   public MagicEngineBlock(Properties properties) {
      this(ChunkStorage.CONFIG.magicEngineReduction, ChunkStorage.CONFIG.magicEngineRange, false, properties);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> SHAPE_NORTH;
         case SOUTH -> SHAPE_SOUTH;
         case WEST -> SHAPE_WEST;
         case EAST -> SHAPE_EAST;
         case DOWN -> SHAPE_DOWN;
         case UP -> SHAPE_UP;
         default -> throw new MatchException(null, null);
      };
   }

   public static ToIntFunction<BlockState> getLightEmission() {
      return state -> state.getValue(ENABLED) ? 15 : 0;
   }

   public boolean isSignalSource(BlockState pState) {
      return true;
   }

   public int getSignal(BlockState pState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
      return pState.getValue(ENABLED) ? 15 : 0;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING}).add(new Property[]{ENABLED});
   }

   @NotNull
   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(FACING, pRot.rotate((Direction)pState.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getClickedFace();
      return (BlockState)super.getStateForPlacement(pContext).setValue(FACING, direction);
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pLevel.isClientSide()) {
         this.applyMagiculeModifier(pLevel, pPos, false);
         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      if (this.isCreativeOnly() && !player.isCreative()) {
         return super.useWithoutItem(state, level, pos, player, blockHitResult);
      }

      boolean current = (Boolean)state.getValue(ENABLED);
      state = (BlockState)state.setValue(ENABLED, !current);
      level.setBlock(pos, state, 3);
      this.applyMagiculeModifier(level, pos, !current);
      if (level.getBlockEntity(pos) instanceof MagicEngineBlockEntity entity) {
         entity.setTracked(!current);
      }

      level.playSound(null, pos, !current ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, !current ? 0.6F : 0.5F);
      level.gameEvent(player, !current ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
      if (player instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, player.getMainHandItem());
      }

      return InteractionResult.SUCCESS;
   }

   protected void applyMagiculeModifier(Level level, BlockPos pPos, boolean enable) {
      double radius = this.getReductionRange();
      double reduction = this.getMagiculeReduction() * -1.0;
      int minX = (int)(pPos.getX() - radius);
      int maxX = (int)(pPos.getX() + radius);
      int minZ = (int)(pPos.getZ() - radius);
      int maxZ = (int)(pPos.getZ() + radius);
      int minChunkX = minX >> 4;
      int maxChunkX = maxX >> 4;
      int minChunkZ = minZ >> 4;
      int maxChunkZ = maxZ >> 4;

      for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
         for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);
            ChunkStorage storage = TensuraStorages.getChunkFrom(chunk);
            if (enable) {
               storage.addBlockModifier(pPos, reduction, radius);
            } else {
               storage.removeBlockModifier(pPos);
            }

            storage.markDirty();
         }
      }
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new MagicEngineBlockEntity(pos, state);
   }

   @Generated
   public double getMagiculeReduction() {
      return this.magiculeReduction;
   }

   @Generated
   public double getReductionRange() {
      return this.reductionRange;
   }

   @Generated
   public boolean isCreativeOnly() {
      return this.creativeOnly;
   }
}
