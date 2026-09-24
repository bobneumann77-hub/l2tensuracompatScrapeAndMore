package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.entity.human.golem.TrainingDummyEntity;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DummyBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape SHAPE = Shapes.or(Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0), Block.box(6.5, 1.0, 6.5, 9.5, 16.0, 9.5));

   public DummyBlock() {
      super(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.CROP).noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
      if (!pLevel.isClientSide()) {
         TrainingDummyEntity dummy = new TrainingDummyEntity((EntityType<? extends TrainingDummyEntity>)HumanEntityTypes.TRAINING_DUMMY.get(), pLevel);
         dummy.setPos(pPos.getBottomCenter().add(0.0, 0.25, 0.0));
         float yRot = pPlacer != null ? pPlacer.getYRot() : 0.0F;
         float placedYRot = Mth.floor((Mth.wrapDegrees(yRot - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         dummy.setYRot(placedYRot);
         dummy.yBodyRot = placedYRot;
         dummy.yBodyRotO = placedYRot;
         dummy.yHeadRot = placedYRot;
         dummy.yHeadRotO = placedYRot;
         pLevel.addFreshEntity(dummy);
      }
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
      BlockPos blockpos = pContext.getClickedPos();
      Level level = pContext.getLevel();
      return blockpos.getY() <= level.getMaxBuildHeight() - 1
            && level.getBlockState(blockpos.above()).canBeReplaced(pContext)
            && level.getBlockState(blockpos).canBeReplaced(pContext)
         ? (BlockState)this.defaultBlockState().setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER)
         : null;
   }
}
