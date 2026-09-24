package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.tensura.block.entity.KilnBlockEntity;
import io.github.manasmods.tensura.block.part.KilnPart;
import io.github.manasmods.tensura.block.part.TensuraBlockParts;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.function.ToIntFunction;
import lombok.Generated;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KilnBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<KilnBlock> CODEC = simpleCodec(properties -> new KilnBlock(properties, KilnBlock.KilnType.NORMAL));
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty BOOSTED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final EnumProperty<KilnPart> PART = TensuraBlockParts.KILN_PART;
   private static final VoxelShape TOP_SHAPE = Shapes.or(
      box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0), new VoxelShape[]{box(2.0, 9.0, 2.0, 14.0, 14.0, 14.0), box(1.0, 14.0, 1.0, 15.0, 16.0, 15.0)}
   );
   private static final VoxelShape BASE_SHAPE = Shapes.or(box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), box(1.0, 12.0, 1.0, 15.0, 16.0, 15.0));
   private final KilnBlock.KilnType type;

   public KilnBlock(Properties properties, KilnBlock.KilnType type) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH))
                     .setValue(PART, KilnPart.BASE))
                  .setValue(LIT, Boolean.FALSE))
               .setValue(BOOSTED, Boolean.FALSE))
            .setValue(WATERLOGGED, Boolean.FALSE)
      );
      this.type = type;
   }

   public KilnBlock(KilnBlock.KilnType type) {
      this(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_BLACK)
            .strength(50.0F, 1200.0F)
            .sound(SoundType.STONE)
            .noOcclusion()
            .lightLevel(litBlockEmission(13))
            .requiresCorrectToolForDrops(),
         type
      );
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
      if (!pLevel.isClientSide()) {
         BlockPos blockpos = this.getOtherPartPosition(pPos, (KilnPart)pState.getValue(PART));
         pLevel.setBlock(
            blockpos, (BlockState)((BlockState)pState.setValue(PART, KilnPart.TOP)).setValue(WATERLOGGED, this.isWaterAtPosition(pLevel, blockpos)), 3
         );
         pLevel.blockUpdated(pPos, Blocks.AIR);
         pState.updateNeighbourShapes(pLevel, pPos, 3);
      }
   }

   private BlockPos getOtherPartPosition(BlockPos sourcePos, KilnPart part) {
      return part == KilnPart.BASE ? sourcePos.above() : sourcePos.below();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, LIT, BOOSTED, PART, WATERLOGGED});
   }

   public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
      super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
      if (!pLevel.isClientSide()) {
         BlockPos blockpos = this.getOtherPartPosition(pPos, (KilnPart)pState.getValue(PART));
         pLevel.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
      }

      return pState;
   }

   public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
      if (!pLevel.isClientSide()) {
         BlockState pState = pLevel.getBlockState(pPos);
         BlockPos blockpos = this.getOtherPartPosition(pPos, (KilnPart)pState.getValue(PART));
         pLevel.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockPos blockpos = pContext.getClickedPos();
      Level level = pContext.getLevel();
      return blockpos.getY() <= level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(pContext)
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()))
            .setValue(WATERLOGGED, this.isWaterAtPosition(level, blockpos))
         : null;
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   private boolean isWaterAtPosition(Level level, BlockPos blockPos) {
      return level.getFluidState(blockPos).is(Fluids.WATER);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevek, BlockPos pPos, CollisionContext pContext) {
      KilnPart part = (KilnPart)pState.getValue(PART);
      return part == KilnPart.TOP ? TOP_SHAPE : BASE_SHAPE;
   }

   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
   }

   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      if ((Boolean)pState.getValue(LIT) && ((KilnPart)pState.getValue(PART)).equals(KilnPart.BASE)) {
         double d0 = pPos.getX() + 0.5;
         double d1 = pPos.getY() + 0.2;
         double d2 = pPos.getZ() + 0.5;
         if (pRandom.nextDouble() < 0.1) {
            pLevel.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = (Direction)pState.getValue(FACING);
         Axis direction$axis = direction.getAxis();
         double d3 = pRandom.nextDouble() * 0.6 - 0.3;
         double d4 = direction$axis == Axis.X ? direction.getStepX() * 0.52 : d3;
         double d5 = pRandom.nextDouble() * 6.0 / 16.0;
         double d6 = direction$axis == Axis.Z ? direction.getStepZ() * 0.52 : d3;
         pLevel.addParticle(ParticleTypes.SMOKE, d0 + d4, d1 + d5, d2 + d6, 0.0, 0.0, 0.0);
         if ((Boolean)pState.getValue(BOOSTED)) {
            pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0 + d4, d1 + d5, d2 + d6, 0.0, 0.0, 0.0);
         } else {
            pLevel.addParticle(ParticleTypes.FLAME, d0 + d4, d1 + d5, d2 + d6, 0.0, 0.0, 0.0);
         }
      }
   }

   public static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
      return state -> state.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock() && pLevel.getBlockEntity(pPos) instanceof KilnBlockEntity kilnblockEntity) {
         kilnblockEntity.drops();
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      return level.isClientSide()
         ? InteractionResult.sidedSuccess(true)
         : this.openMenu((ServerPlayer)player, level, ((KilnPart)blockState.getValue(PART)).equals(KilnPart.BASE) ? blockPos : blockPos.below());
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result
   ) {
      if (stack.is((Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get())
         && hand.equals(InteractionHand.MAIN_HAND)
         && level.getBlockEntity(((KilnPart)state.getValue(PART)).equals(KilnPart.BASE) ? pos : pos.below()) instanceof KilnBlockEntity kiln) {
         int cost = ObjectSelectionHelper.CONFIG.Kiln.fireCoreCost;
         if (stack.getMaxDamage() - stack.getDamageValue() < cost) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.USING_ITEM.trigger(serverPlayer, stack);
         }

         player.setItemInHand(hand, stack.hurtAndConvertOnBreak(100, (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get(), player, EquipmentSlot.MAINHAND));
         kiln.setBoostedTime(kiln.getBoostedTime() + ObjectSelectionHelper.CONFIG.Kiln.chargeDuration);
         player.playSound((SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), 1.0F, 1.0F);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private InteractionResult openMenu(ServerPlayer player, Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof KilnBlockEntity kilnblockEntity) {
         MenuRegistry.openExtendedMenu(player, kilnblockEntity, buf -> buf.writeBlockPos(pos));
         kilnblockEntity.needUpdate = true;
         return InteractionResult.sidedSuccess(false);
      } else {
         throw new IllegalStateException(String.format("Container provider for Kiln block is missing!\n Position: %s; World: %s", pos, level.dimension()));
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(PART) == KilnPart.BASE ? new KilnBlockEntity(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return state.getValue(PART) == KilnPart.BASE ? createTickerHelper(type, (BlockEntityType)TensuraBlockEntities.KILN.get(), KilnBlockEntity::tick) : null;
   }

   @Generated
   public KilnBlock.KilnType getType() {
      return this.type;
   }

   public enum KilnType {
      NORMAL,
      MITHRIL,
      ORICHALCUM;
   }
}
