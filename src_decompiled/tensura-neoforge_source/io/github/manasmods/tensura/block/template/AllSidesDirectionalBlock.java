package io.github.manasmods.tensura.block.template;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class AllSidesDirectionalBlock extends DirectionalBlock {
   public static final MapCodec<AllSidesDirectionalBlock> CODEC = simpleCodec(AllSidesDirectionalBlock::new);

   public AllSidesDirectionalBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   @NotNull
   protected MapCodec<AllSidesDirectionalBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING});
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(FACING, pRot.rotate((Direction)pState.getValue(FACING)));
   }

   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getClickedFace();
      return (BlockState)this.defaultBlockState().setValue(FACING, direction.getOpposite());
   }
}
