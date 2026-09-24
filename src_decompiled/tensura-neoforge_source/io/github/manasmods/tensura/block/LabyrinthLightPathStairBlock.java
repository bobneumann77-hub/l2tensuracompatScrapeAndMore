package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class LabyrinthLightPathStairBlock extends StairBlock {
   public LabyrinthLightPathStairBlock(BlockState state, Properties properties) {
      super(state, properties);
   }

   public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
      if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH.get()) {
         return true;
      } else if (stateFrom.getBlock() == TensuraBlocks.LABYRINTH_PRAYING_PATH.get()) {
         return true;
      } else if (stateFrom.getBlock() == this && this.isInvisibleToGlassStairs(state, stateFrom, direction)) {
         return true;
      } else {
         return stateFrom.getBlock() == TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB.get() && this.isInvisibleToGlassSlab(state, stateFrom, direction)
            ? true
            : super.skipRendering(state, stateFrom, direction);
      }
   }

   private boolean isInvisibleToGlassSlab(BlockState state, BlockState stateFrom, Direction direction) {
      Half half = (Half)state.getValue(StairBlock.HALF);
      Direction facing = (Direction)state.getValue(StairBlock.FACING);
      StairsShape shape = (StairsShape)state.getValue(StairBlock.SHAPE);
      SlabType typeFrom = (SlabType)stateFrom.getValue(SlabBlock.TYPE);
      if (direction == Direction.UP && typeFrom != SlabType.TOP) {
         return true;
      }

      if (direction == Direction.DOWN && typeFrom != SlabType.BOTTOM) {
         return true;
      }

      if (typeFrom == SlabType.DOUBLE) {
         return true;
      }

      if (direction == facing.getOpposite()) {
         if (typeFrom == SlabType.BOTTOM && half == Half.BOTTOM) {
            return true;
         }

         if (typeFrom == SlabType.TOP && half == Half.TOP) {
            return true;
         }
      }

      if (direction == facing.getClockWise() && shape == StairsShape.OUTER_LEFT) {
         if (typeFrom == SlabType.BOTTOM && half == Half.BOTTOM) {
            return true;
         }

         if (typeFrom == SlabType.TOP && half == Half.TOP) {
            return true;
         }
      }

      if (direction != facing.getCounterClockWise() || shape != StairsShape.OUTER_RIGHT) {
         return false;
      } else {
         return typeFrom == SlabType.BOTTOM && half == Half.BOTTOM ? true : typeFrom == SlabType.TOP && half == Half.TOP;
      }
   }

   private boolean isInvisibleToGlassStairs(BlockState state, BlockState stateFrom, Direction direction) {
      Half half = (Half)state.getValue(StairBlock.HALF);
      Half halfFrom = (Half)stateFrom.getValue(StairBlock.HALF);
      Direction facing = (Direction)state.getValue(StairBlock.FACING);
      Direction facingFrom = (Direction)stateFrom.getValue(StairBlock.FACING);
      StairsShape shape = (StairsShape)state.getValue(StairBlock.SHAPE);
      StairsShape shapeFrom = (StairsShape)stateFrom.getValue(StairBlock.SHAPE);
      if (direction == Direction.UP) {
         if (halfFrom == Half.BOTTOM) {
            return true;
         }

         if (half != halfFrom) {
            if (facing == facingFrom && shape == shapeFrom) {
               return true;
            }

            switch (shape) {
               case STRAIGHT:
                  if (shapeFrom == StairsShape.INNER_LEFT && (facingFrom == facing || facingFrom == facing.getClockWise())) {
                     return true;
                  }

                  if (shapeFrom == StairsShape.INNER_RIGHT && (facingFrom == facing || facingFrom == facing.getCounterClockWise())) {
                     return true;
                  }
                  break;
               case INNER_LEFT:
                  if (shapeFrom == StairsShape.INNER_RIGHT && facingFrom == facing.getCounterClockWise()) {
                     return true;
                  }
                  break;
               case INNER_RIGHT:
                  if (shapeFrom == StairsShape.INNER_LEFT && facingFrom == facing.getClockWise()) {
                     return true;
                  }
                  break;
               case OUTER_LEFT:
                  if (shapeFrom == StairsShape.OUTER_RIGHT && facingFrom == facing.getCounterClockWise()) {
                     return true;
                  }

                  if (shapeFrom == StairsShape.STRAIGHT && (facingFrom == facing || facingFrom == facing.getCounterClockWise())) {
                     return true;
                  }
                  break;
               case OUTER_RIGHT:
                  if (shapeFrom == StairsShape.OUTER_LEFT && facingFrom == facing.getClockWise()) {
                     return true;
                  }

                  if (shapeFrom == StairsShape.STRAIGHT && (facingFrom == facing || facingFrom == facing.getClockWise())) {
                     return true;
                  }
            }
         }
      }

      if (direction == Direction.DOWN) {
         if (halfFrom == Half.TOP) {
            return true;
         }

         switch (shape) {
            case STRAIGHT:
               if (shapeFrom == StairsShape.INNER_LEFT && (facingFrom == facing || facingFrom == facing.getClockWise())) {
                  return true;
               }

               if (shapeFrom == StairsShape.INNER_RIGHT && (facingFrom == facing || facingFrom == facing.getCounterClockWise())) {
                  return true;
               }
               break;
            case INNER_LEFT:
               if (shapeFrom == StairsShape.INNER_RIGHT && facingFrom == facing.getCounterClockWise()) {
                  return true;
               }
               break;
            case INNER_RIGHT:
               if (shapeFrom == StairsShape.INNER_LEFT && facingFrom == facing.getClockWise()) {
                  return true;
               }
               break;
            case OUTER_LEFT:
               if (shapeFrom == StairsShape.OUTER_RIGHT && facingFrom == facing.getCounterClockWise()) {
                  return true;
               }

               if (shapeFrom == StairsShape.STRAIGHT && (facingFrom == facing || facingFrom == facing.getCounterClockWise())) {
                  return true;
               }
               break;
            case OUTER_RIGHT:
               if (shapeFrom == StairsShape.OUTER_LEFT && facingFrom == facing.getClockWise()) {
                  return true;
               }

               if (shapeFrom == StairsShape.STRAIGHT && (facingFrom == facing || facingFrom == facing.getClockWise())) {
                  return true;
               }
         }
      }

      if (facingFrom == direction.getOpposite() && shapeFrom != StairsShape.OUTER_LEFT && shapeFrom != StairsShape.OUTER_RIGHT) {
         return true;
      }

      if (facingFrom.getCounterClockWise() == direction && shapeFrom == StairsShape.INNER_RIGHT) {
         return true;
      }

      if (facingFrom.getClockWise() == direction && shapeFrom == StairsShape.INNER_LEFT) {
         return true;
      }

      if (direction == facing && half == halfFrom) {
         if (facingFrom == facing.getCounterClockWise() && shape == StairsShape.OUTER_LEFT && shapeFrom != StairsShape.OUTER_RIGHT) {
            return true;
         }

         if (facingFrom == facing.getClockWise() && shape == StairsShape.OUTER_RIGHT && shapeFrom != StairsShape.OUTER_LEFT) {
            return true;
         }
      }

      if (direction == facing.getOpposite() && half == halfFrom) {
         if (facingFrom == facing.getCounterClockWise() && shapeFrom != StairsShape.OUTER_LEFT) {
            return true;
         }

         if (facingFrom == facing.getClockWise() && shapeFrom != StairsShape.OUTER_RIGHT) {
            return true;
         }

         if (facingFrom == facing.getOpposite()) {
            return true;
         }
      }

      if (direction == facing.getCounterClockWise() && half == halfFrom) {
         if (facingFrom == direction && shape != StairsShape.INNER_LEFT && shapeFrom == StairsShape.INNER_RIGHT) {
            return true;
         }

         if (facingFrom == facing && shapeFrom != StairsShape.OUTER_LEFT) {
            return true;
         }

         if (facingFrom == facing.getClockWise() && shapeFrom == StairsShape.OUTER_LEFT && shape != StairsShape.INNER_LEFT) {
            return true;
         }

         if (facingFrom == facing.getOpposite() && shape == StairsShape.OUTER_RIGHT) {
            return true;
         }
      }

      if (direction != facing.getClockWise() || half != halfFrom) {
         return false;
      } else if (facingFrom == direction && shape != StairsShape.INNER_RIGHT && shapeFrom == StairsShape.INNER_LEFT) {
         return true;
      } else if (facingFrom == facing && shapeFrom != StairsShape.OUTER_RIGHT) {
         return true;
      } else {
         return facingFrom == facing.getCounterClockWise() && shapeFrom == StairsShape.OUTER_RIGHT && shape != StairsShape.INNER_RIGHT
            ? true
            : facingFrom == facing.getOpposite() && shape == StairsShape.OUTER_LEFT;
      }
   }
}
