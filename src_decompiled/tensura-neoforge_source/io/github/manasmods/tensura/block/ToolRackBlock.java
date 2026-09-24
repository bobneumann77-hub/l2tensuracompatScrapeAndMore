package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.block.entity.ToolRackBlockEntity;
import io.github.manasmods.tensura.data.TensuraItemTags;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolRackBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<ToolRackBlock> CODEC = simpleCodec(ToolRackBlock::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED
   );
   private static final VoxelShape FRONT = Shapes.or(
      Block.box(0.0, 0.0, 2.0, 16.0, 2.0, 14.0),
      new VoxelShape[]{
         Block.box(0.0, 2.0, 4.0, 2.0, 4.0, 12.0),
         Block.box(14.0, 2.0, 4.0, 16.0, 4.0, 12.0),
         Block.box(0.0, 2.0, 7.0, 2.0, 14.0, 9.0),
         Block.box(14.0, 2.0, 7.0, 16.0, 14.0, 9.0),
         Block.box(0.0, 14.0, 4.0, 16.0, 16.0, 12.0)
      }
   );
   private static final VoxelShape SIDE = Shapes.or(
      Block.box(2.0, 0.0, 0.0, 14.0, 2.0, 16.0),
      new VoxelShape[]{
         Block.box(4.0, 2.0, 0.0, 12.0, 4.0, 2.0),
         Block.box(4.0, 2.0, 14.0, 12.0, 4.0, 16.0),
         Block.box(7.0, 2.0, 0.0, 9.0, 14.0, 2.0),
         Block.box(7.0, 2.0, 14.0, 9.0, 14.0, 16.0),
         Block.box(4.0, 14.0, 0.0, 12.0, 16.0, 16.0)
      }
   );

   public ToolRackBlock() {
      this(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).instrument(NoteBlockInstrument.HARP).strength(1.5F));
   }

   public ToolRackBlock(Properties properties) {
      super(properties);
      BlockState blockState = (BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

      for (BooleanProperty booleanProperty : SLOT_OCCUPIED_PROPERTIES) {
         blockState = (BlockState)blockState.setValue(booleanProperty, false);
      }

      this.registerDefaultState((BlockState)blockState.setValue(WATERLOGGED, Boolean.FALSE));
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch ((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)) {
         case EAST, WEST -> SIDE;
         default -> FRONT;
      };
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (level.getBlockEntity(blockPos) instanceof ToolRackBlockEntity entity) {
         if (!canInsert(itemStack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         OptionalInt optionalInt = this.getHitSlot(blockHitResult, blockState, player);
         if (optionalInt.isEmpty()) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
         }

         if ((Boolean)blockState.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(optionalInt.getAsInt()))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         addTool(level, blockPos, player, entity, itemStack, optionalInt.getAsInt());
         return ItemInteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (level.getBlockEntity(blockPos) instanceof ToolRackBlockEntity entity) {
         OptionalInt optionalInt = this.getHitSlot(blockHitResult, blockState, player);
         if (optionalInt.isEmpty()) {
            return InteractionResult.PASS;
         }

         if (!(Boolean)blockState.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(optionalInt.getAsInt()))) {
            return InteractionResult.CONSUME;
         }

         removeTool(level, blockPos, player, entity, optionalInt.getAsInt());
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   private OptionalInt getHitSlot(BlockHitResult hit, BlockState state, Player player) {
      Direction facing = (Direction)state.getValue(HorizontalDirectionalBlock.FACING);
      return getRelativeHitCoordinates(hit, facing, player).map(vec2 -> OptionalInt.of(getSection(vec2.x))).orElseGet(OptionalInt::empty);
   }

   private static Optional<Vec2> getRelativeHitCoordinates(BlockHitResult hit, Direction facing, Player player) {
      Direction side = hit.getDirection();
      if (side == facing || side == facing.getOpposite()) {
         boolean back = side == facing.getOpposite();
         BlockPos relative = hit.getBlockPos().relative(side);
         Vec3 local = hit.getLocation().subtract(relative.getX(), relative.getY(), relative.getZ());
         double x = local.x();
         double y = local.y();
         double z = local.z();

         Vec2 uv = switch (side) {
            case EAST -> new Vec2((float)(1.0 - z), (float)y);
            case WEST -> new Vec2((float)z, (float)y);
            case NORTH -> new Vec2((float)(1.0 - x), (float)y);
            case SOUTH -> new Vec2((float)x, (float)y);
            default -> null;
         };
         if (uv == null) {
            return Optional.empty();
         }

         if (back) {
            uv = new Vec2(1.0F - uv.x, uv.y);
         }

         return Optional.of(uv);
      } else if (side == Direction.UP) {
         BlockPos pos = hit.getBlockPos();
         Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
         double x = local.x();
         double z = local.z();

         float u = switch (facing) {
            case EAST -> (float)(1.0 - z);
            case WEST -> (float)z;
            case NORTH -> (float)(1.0 - x);
            case SOUTH -> (float)x;
            default -> (float)x;
         };
         return Optional.of(new Vec2(u, 0.5F));
      } else {
         return Optional.empty();
      }
   }

   private static int getSection(float f) {
      if (f < 0.33F) {
         return 0;
      } else {
         return f > 0.66F ? 2 : 1;
      }
   }

   private static void addTool(Level level, BlockPos blockPos, Player player, ToolRackBlockEntity entity, ItemStack itemStack, int i) {
      if (!level.isClientSide()) {
         player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
         entity.setItem(i, itemStack.consumeAndReturn(1, player));
         level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   private static void removeTool(Level level, BlockPos blockPos, Player player, ToolRackBlockEntity entity, int i) {
      if (!level.isClientSide()) {
         ItemStack itemStack = entity.removeItem(i, 1);
         level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
         }

         level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      }
   }

   public static boolean canInsert(ItemStack itemStack) {
      return itemStack.is(TensuraItemTags.TOOL_RACK_EXCLUDED) ? false : itemStack.is(ItemTags.DURABILITY_ENCHANTABLE);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ToolRackBlockEntity(blockPos, blockState);
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
      if (pState.getBlock() != pNewState.getBlock() && pLevel.getBlockEntity(pPos) instanceof ToolRackBlockEntity entity) {
         entity.drops();
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockPos blockpos = pContext.getClickedPos();
      Level level = pContext.getLevel();
      return blockpos.getY() <= level.getMaxBuildHeight() && level.getBlockState(blockpos).canBeReplaced(pContext)
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite()))
            .setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER)
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING});
      builder.add(new Property[]{WATERLOGGED});
      SLOT_OCCUPIED_PROPERTIES.forEach(property -> builder.add(new Property[]{property}));
   }

   @NotNull
   public BlockState rotate(BlockState blockState, Rotation rotation) {
      return (BlockState)blockState.setValue(
         HorizontalDirectionalBlock.FACING, rotation.rotate((Direction)blockState.getValue(HorizontalDirectionalBlock.FACING))
      );
   }

   @NotNull
   public BlockState mirror(BlockState blockState, Mirror mirror) {
      return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   protected boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      if (level.isClientSide()) {
         return 0;
      } else {
         return level.getBlockEntity(blockPos) instanceof ToolRackBlockEntity entity ? entity.getLastInteractedSlot() + 1 : 0;
      }
   }
}
