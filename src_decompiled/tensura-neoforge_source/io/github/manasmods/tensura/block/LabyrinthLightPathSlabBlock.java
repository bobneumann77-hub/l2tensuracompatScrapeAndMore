package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public class LabyrinthLightPathSlabBlock extends SlabBlock {
   public LabyrinthLightPathSlabBlock(Properties pProperties) {
      super(pProperties);
   }

   public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
      if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH.get()) {
         return true;
      } else if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_PRAYING_PATH.get()) {
         return true;
      } else if (stateFrom.getBlock() == this && this.isInvisibleToGlassSlab(state, stateFrom, direction)) {
         return true;
      } else {
         return stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS.get() && this.isInvisibleToGlassStairs(state, stateFrom, direction)
            ? true
            : super.skipRendering(state, stateFrom, direction);
      }
   }

   private boolean isInvisibleToGlassSlab(BlockState state, BlockState stateFrom, Direction direction) {
      SlabType type = (SlabType)state.getValue(SlabBlock.TYPE);
      SlabType typeFrom = (SlabType)stateFrom.getValue(SlabBlock.TYPE);
      switch (direction) {
         case UP:
            if (typeFrom != SlabType.TOP && type != SlabType.BOTTOM) {
               return true;
            }
            break;
         case DOWN:
            if (typeFrom != SlabType.BOTTOM && type != SlabType.TOP) {
               return true;
            }
            break;
         case NORTH:
         case EAST:
         case SOUTH:
         case WEST:
            if (type == typeFrom || typeFrom == SlabType.DOUBLE) {
               return true;
            }
      }

      return false;
   }

   private boolean isInvisibleToGlassStairs(BlockState state, BlockState stateFrom, Direction direction) {
      SlabType type = (SlabType)state.getValue(SlabBlock.TYPE);
      Half halfFrom = (Half)stateFrom.getValue(StairBlock.HALF);
      Direction facingFrom = (Direction)stateFrom.getValue(StairBlock.FACING);
      if (direction == Direction.UP && halfFrom == Half.BOTTOM) {
         return true;
      } else if (direction == Direction.DOWN && halfFrom == Half.TOP) {
         return true;
      } else if (facingFrom == direction.getOpposite()) {
         return true;
      } else if (direction.get2DDataValue() != -1) {
         return type == SlabType.BOTTOM && halfFrom == Half.BOTTOM ? true : type == SlabType.TOP && halfFrom == Half.TOP;
      } else {
         return false;
      }
   }
}
