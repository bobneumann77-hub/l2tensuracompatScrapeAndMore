package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class LabyrinthLightPathBlock extends Block {
   public LabyrinthLightPathBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.NONE)
            .isViewBlocking((blockState, blockGetter, blockPos) -> false)
            .noLootTable()
            .sound(SoundType.AMETHYST)
            .noOcclusion()
            .pushReaction(PushReaction.IGNORE)
            .strength(-1.0F, 3600000.0F)
      );
   }

   public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
      if (!stateFrom.is(this) && !stateFrom.is((Block)TensuraBlocks.LABYRINTH_PRAYING_PATH.get())) {
         if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB.get() && this.isInvisibleToGlassSlab(state, stateFrom, direction)) {
            return true;
         } else {
            return stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS.get() && this.isInvisibleToGlassStairs(state, stateFrom, direction)
               ? true
               : super.skipRendering(state, stateFrom, direction);
         }
      } else {
         return true;
      }
   }

   private boolean isInvisibleToGlassSlab(BlockState state, BlockState stateFrom, Direction direction) {
      SlabType typeFrom = (SlabType)stateFrom.getValue(SlabBlock.TYPE);
      if (typeFrom == SlabType.DOUBLE) {
         return true;
      } else {
         return direction == Direction.UP && typeFrom != SlabType.TOP ? true : direction == Direction.DOWN && typeFrom != SlabType.BOTTOM;
      }
   }

   private boolean isInvisibleToGlassStairs(BlockState state, BlockState stateFrom, Direction direction) {
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
}
