package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.block.entity.WarpPadBlockEntity;
import io.github.manasmods.tensura.block.part.TensuraBlockParts;
import io.github.manasmods.tensura.block.part.WarpPadPart;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpPadBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<WarpPadBlock> CODEC = simpleCodec(WarpPadBlock::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final EnumProperty<WarpPadPart> PART = TensuraBlockParts.WARP_PAD_PART;
   private static final VoxelShape CORNER_NW = Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
   private static final VoxelShape CORNER_SE = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
   private static final VoxelShape CORNER_SW = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
   private static final VoxelShape CORNER_NE = Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
   private static final VoxelShape SIDE_S = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 8.0);
   private static final VoxelShape SIDE_N = Block.box(0.0, 0.0, 8.0, 16.0, 8.0, 16.0);
   private static final VoxelShape SIDE_E = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 16.0);
   private static final VoxelShape SIDE_W = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape FULL = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

   public WarpPadBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(PART, WarpPadPart.BIG_C)).setValue(WATERLOGGED, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, WATERLOGGED});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return switch ((WarpPadPart)state.getValue(PART)) {
         case BIG_NW -> CORNER_NW;
         case BIG_NE -> CORNER_NE;
         case BIG_SW -> CORNER_SW;
         case BIG_SE -> CORNER_SE;
         case BIG_N -> SIDE_N;
         case BIG_S -> SIDE_S;
         case BIG_W -> SIDE_W;
         case BIG_E -> SIDE_E;
         case BIG_C, SMALL_NW, SMALL_NE, SMALL_SW, SMALL_SE -> FULL;
      };
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      if (rot == Rotation.NONE) {
         return state;
      }

      if (!state.hasProperty(PART)) {
         return state;
      }

      WarpPadPart part = (WarpPadPart)state.getValue(PART);
      WarpPadPart rotated = rotatePart(part, rot);
      return rotated == null ? state : (BlockState)state.setValue(PART, rotated);
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      if (mirror == Mirror.NONE) {
         return state;
      }

      if (!state.hasProperty(PART)) {
         return state;
      }

      WarpPadPart part = (WarpPadPart)state.getValue(PART);
      WarpPadPart mirrored = mirrorPart(part, mirror);
      return mirrored == null ? state : (BlockState)state.setValue(PART, mirrored);
   }

   @Nullable
   private static WarpPadPart rotatePart(WarpPadPart part, Rotation rot) {
      int s = part.size;
      int x = part.dx;
      int z = part.dz;
      int nx = x;
      int nz = z;
      switch (rot) {
         case NONE:
            return part;
         case CLOCKWISE_90:
            nx = s - 1 - z;
            nz = x;
            break;
         case CLOCKWISE_180:
            nx = s - 1 - x;
            nz = s - 1 - z;
            break;
         case COUNTERCLOCKWISE_90:
            nx = z;
            nz = s - 1 - x;
      }

      return WarpPadPart.fromOffset(s, nx, nz);
   }

   @Nullable
   private static WarpPadPart mirrorPart(WarpPadPart part, Mirror mirror) {
      int s = part.size;
      int x = part.dx;
      int z = part.dz;
      int nx = x;
      int nz = z;
      switch (mirror) {
         case NONE:
            return part;
         case LEFT_RIGHT:
            nx = s - 1 - x;
            break;
         case FRONT_BACK:
            nz = s - 1 - z;
      }

      return WarpPadPart.fromOffset(s, nx, nz);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos clicked = context.getClickedPos();
      if (context.getClickedFace() != Direction.UP) {
         return null;
      }

      if (context.isSecondaryUseActive()) {
         BlockPos nw = clicked.offset(-1, 0, -1);
         return this.isSpaceOccupied(level, context, nw, 3)
            ? null
            : (BlockState)((BlockState)this.defaultBlockState().setValue(PART, WarpPadPart.BIG_C))
               .setValue(WATERLOGGED, this.isWaterAtPosition(level, clicked));
      }

      Direction forward = context.getHorizontalDirection();
      Direction right = forward.getClockWise();
      BlockPos forwardPos = clicked.relative(forward);
      BlockPos rightPos = clicked.relative(right);
      BlockPos cornerPos = forwardPos.relative(right);
      int minX = Math.min(Math.min(clicked.getX(), forwardPos.getX()), Math.min(rightPos.getX(), cornerPos.getX()));
      int minZ = Math.min(Math.min(clicked.getZ(), forwardPos.getZ()), Math.min(rightPos.getZ(), cornerPos.getZ()));
      BlockPos nw = new BlockPos(minX, clicked.getY(), minZ);
      if (this.isSpaceOccupied(level, context, nw, 2)) {
         return null;
      }

      int dx = clicked.getX() - nw.getX();
      int dz = clicked.getZ() - nw.getZ();
      WarpPadPart part = WarpPadPart.fromOffset(2, dx, dz);
      return part == null
         ? null
         : (BlockState)((BlockState)this.defaultBlockState().setValue(PART, part)).setValue(WATERLOGGED, this.isWaterAtPosition(level, clicked));
   }

   private boolean isSpaceOccupied(Level level, BlockPlaceContext ctx, BlockPos nw, int size) {
      for (int dz = 0; dz < size; dz++) {
         for (int dx = 0; dx < size; dx++) {
            BlockPos p = nw.offset(dx, 0, dz);
            if (!level.getBlockState(p).canBeReplaced(ctx)) {
               return true;
            }
         }
      }

      return false;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide()) {
         WarpPadPart part = (WarpPadPart)state.getValue(PART);
         BlockPos nw = getOriginPos(pos, part);

         for (int dz = 0; dz < part.size; dz++) {
            for (int dx = 0; dx < part.size; dx++) {
               BlockPos p = nw.offset(dx, 0, dz);
               WarpPadPart expected = WarpPadPart.fromOffset(part.size, dx, dz);
               if (expected != null) {
                  this.placePart(level, p, state, expected);
               }
            }
         }
      }
   }

   private void placePart(Level level, BlockPos targetPos, BlockState baseState, WarpPadPart part) {
      BlockState targetState = level.getBlockState(targetPos);
      if (!targetState.is(this) || targetState.getValue(PART) != part) {
         BlockState newState = (BlockState)((BlockState)baseState.setValue(PART, part)).setValue(WATERLOGGED, this.isWaterAtPosition(level, targetPos));
         level.setBlock(targetPos, newState, 3);
      }
   }

   public static BlockPos getOriginPos(BlockPos pos, WarpPadPart part) {
      return pos.offset(-part.dx, 0, -part.dz);
   }

   @NotNull
   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      super.playerWillDestroy(level, pos, state, player);
      if (!level.isClientSide()) {
         this.removeOtherPartsNoDrops(level, pos, state);
      }

      return state;
   }

   protected void onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
      super.onExplosionHit(blockState, level, blockPos, explosion, biConsumer);
      if (!level.isClientSide()
         && !blockState.isAir()
         && explosion.getBlockInteraction() != BlockInteraction.TRIGGER_BLOCK
         && explosion.getBlockInteraction() != BlockInteraction.KEEP) {
         this.removeOtherPartsNoDrops(level, blockPos, blockState);
      }
   }

   private void removeOtherPartsNoDrops(Level level, BlockPos brokenPos, BlockState state) {
      WarpPadPart part = (WarpPadPart)state.getValue(PART);
      BlockPos origin = getOriginPos(brokenPos, part);

      for (int dz = 0; dz < part.size; dz++) {
         for (int dx = 0; dx < part.size; dx++) {
            BlockPos pos = origin.offset(dx, 0, dz);
            if (!pos.equals(brokenPos) && level.getBlockState(pos).is(this)) {
               level.destroyBlock(pos, false);
            }
         }
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos curPos, BlockPos neighborPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(curPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, dir, neighbor, level, curPos, neighborPos);
   }

   private boolean isWaterAtPosition(Level level, BlockPos pos) {
      return level.getFluidState(pos).is(Fluids.WATER);
   }

   @NotNull
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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
      return new WarpPadBlockEntity(pos, state);
   }

   @Nullable
   public static BlockEntity findBlockEntity(Level level, BlockPos pos, BlockState state) {
      WarpPadPart part = (WarpPadPart)state.getValue(PART);
      BlockPos origin = getOriginPos(pos, part);

      for (int dz = 0; dz < part.size; dz++) {
         for (int dx = 0; dx < part.size; dx++) {
            BlockPos offset = origin.offset(dx, 0, dz);
            BlockEntity blockEntity = level.getBlockEntity(offset);
            if (blockEntity instanceof WarpPadBlockEntity padBlock && padBlock.getForceExitHandler() != null) {
               return blockEntity;
            }
         }
      }

      return level.getBlockEntity(getOriginPos(pos, part));
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
      return createTickerHelper(type, (BlockEntityType)TensuraBlockEntities.WARP_PAD.get(), WarpPadBlockEntity::tick);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      if (!player.isShiftKeyDown() || data.getMaxWarpPoints() <= 1) {
         return InteractionResult.PASS;
      }

      if (level.isClientSide()) {
         return InteractionResult.PASS;
      }

      String name = this.getName().getString();
      Vec3 vec3 = WarpPadBlockEntity.getWarpCenter(blockPos, blockState);
      WarpPoint pad = new WarpPoint(name, vec3.x(), vec3.y(), vec3.z(), level.dimension());
      data.getWarpPads().removeIf(warpPad -> warpPad.isSame(pad));
      data.addWarpPad(pad);
      data.markDirty();
      String center = "[" + vec3.x() + ", " + vec3.y() + ", " + vec3.z() + "]";
      player.displayClientMessage(
         Component.translatable("tensura.warp_pad.saved", new Object[]{name, center, level.dimension().location().toString()})
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
         true
      );
      level.playSound(null, vec3.x(), vec3.y(), vec3.z(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
      return InteractionResult.sidedSuccess(level.isClientSide());
   }
}
