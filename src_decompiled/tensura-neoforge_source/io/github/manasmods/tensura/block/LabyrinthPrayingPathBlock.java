package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.block.entity.PrayingPathBlockEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;

public class LabyrinthPrayingPathBlock extends BaseEntityBlock {
   public static final MapCodec<LabyrinthPrayingPathBlock> CODEC = simpleCodec(LabyrinthPrayingPathBlock::new);

   public LabyrinthPrayingPathBlock(Properties properties) {
      super(properties);
   }

   public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
      if (!stateFrom.is(this) && !stateFrom.is((Block)TensuraBlocks.LABYRINTH_LIGHT_PATH.get())) {
         if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB.get() && this.isInvisibleToGlassSlab(stateFrom, direction)) {
            return true;
         } else {
            return stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS.get() && this.isInvisibleToGlassStairs(stateFrom, direction)
               ? true
               : super.skipRendering(state, stateFrom, direction);
         }
      } else {
         return true;
      }
   }

   private boolean isInvisibleToGlassSlab(BlockState stateFrom, Direction direction) {
      SlabType typeFrom = (SlabType)stateFrom.getValue(SlabBlock.TYPE);
      if (typeFrom == SlabType.DOUBLE) {
         return true;
      } else {
         return direction == Direction.UP && typeFrom != SlabType.TOP ? true : direction == Direction.DOWN && typeFrom != SlabType.BOTTOM;
      }
   }

   private boolean isInvisibleToGlassStairs(BlockState stateFrom, Direction direction) {
      Half halfFrom = (Half)stateFrom.getValue(StairBlock.HALF);
      Direction facingFrom = (Direction)stateFrom.getValue(StairBlock.FACING);
      StairsShape shapeFrom = (StairsShape)stateFrom.getValue(StairBlock.SHAPE);
      if (direction == Direction.UP && halfFrom == Half.BOTTOM) {
         return true;
      } else if (direction == Direction.DOWN && halfFrom == Half.TOP) {
         return true;
      } else if (facingFrom == direction.getOpposite() && shapeFrom != StairsShape.OUTER_LEFT && shapeFrom != StairsShape.OUTER_RIGHT) {
         return true;
      } else {
         return facingFrom.getCounterClockWise() == direction && shapeFrom == StairsShape.INNER_RIGHT
            ? true
            : facingFrom.getClockWise() == direction && shapeFrom == StairsShape.INNER_LEFT;
      }
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new PrayingPathBlockEntity(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
      return createTickerHelper(type, (BlockEntityType)TensuraBlockEntities.PRAYING_PATH.get(), PrayingPathBlockEntity::tick);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }
}
