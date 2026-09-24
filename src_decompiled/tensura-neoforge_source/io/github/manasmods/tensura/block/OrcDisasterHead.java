package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.block.entity.OrcDisasterHeadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrcDisasterHead extends BaseEntityBlock {
   public static final MapCodec<OrcDisasterHead> CODEC = simpleCodec(OrcDisasterHead::new);
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   private static final VoxelShape SHAPE_NORTH = Block.box(3.0, 3.5, 7.2, 13.0, 12.5, 16.2);
   private static final VoxelShape SHAPE_SOUTH = Block.box(3.0, 3.5, 0.0, 13.0, 12.5, 8.8);
   private static final VoxelShape SHAPE_WEST = Block.box(7.2, 3.5, 3.0, 16.2, 12.5, 13.0);
   private static final VoxelShape SHAPE_EAST = Block.box(0.2, 3.5, 3.0, 8.8, 12.5, 13.0);
   private static final VoxelShape SHAPE_ROT_0 = Block.box(3.0, 0.0, 4.0, 13.0, 9.0, 13.0);
   private static final VoxelShape SHAPE_ROT_8 = Block.box(3.0, 0.0, 3.0, 13.0, 9.0, 12.0);
   private static final VoxelShape SHAPE_ROT_12 = Block.box(4.0, 0.0, 3.0, 13.0, 9.0, 13.0);
   private static final VoxelShape SHAPE_ROT_16 = Block.box(3.0, 0.0, 3.0, 12.0, 9.0, 13.0);

   public OrcDisasterHead(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(FACING, Direction.NORTH));
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new OrcDisasterHeadBlockEntity(pPos, pState);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @NotNull
   public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return Shapes.empty();
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, Mth.floor(pContext.getRotation() * 16.0F / 360.0F + 0.5) & 15))
         .setValue(FACING, pContext.getClickedFace());
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch ((Direction)pState.getValue(FACING)) {
         case NORTH -> SHAPE_NORTH;
         case SOUTH -> SHAPE_SOUTH;
         case WEST -> SHAPE_WEST;
         case EAST -> SHAPE_EAST;
         default -> {
            switch (pState.getValue(ROTATION)) {
               case 0:
                  yield SHAPE_ROT_0;
                  break;
               case 8:
                  yield SHAPE_ROT_8;
                  break;
               case 12:
                  yield SHAPE_ROT_12;
                  break;
               default:
                  yield SHAPE_ROT_16;
            }
         }
      };
   }

   @NotNull
   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return (BlockState)((BlockState)pState.setValue(ROTATION, pRotation.rotate((Integer)pState.getValue(ROTATION), 16)))
         .setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return (BlockState)((BlockState)pState.setValue(ROTATION, pMirror.mirror((Integer)pState.getValue(ROTATION), 16)))
         .setValue(FACING, pMirror.mirror((Direction)pState.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{ROTATION, FACING});
   }
}
