package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HipokuteGrass extends BushBlock {
   public static final MapCodec<HipokuteGrass> CODEC = simpleCodec(HipokuteGrass::new);
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
      Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
   };

   public HipokuteGrass(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   public boolean isRandomlyTicking(BlockState pState) {
      return super.isRandomlyTicking(pState) && (Integer)pState.getValue(AGE) < 2;
   }

   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pLevel.isLoaded(pPos)) {
         int i = this.getAge(pState);
         if (i < 2) {
            float f = this.getGrowthSpeed(this, pLevel, pPos);
            if (pRandom.nextInt((int)(25.0F / f) + 1) == 0) {
               if (i == 0) {
                  int bound = Math.max(10 - (int)AreaMagiculeHelper.getMagicule(pLevel, pPos) / 500, 1);
                  if (pRandom.nextInt(bound) == 0) {
                     pLevel.setBlock(pPos, this.getStateForAge(1), 2);
                  } else {
                     BlockState newState = (BlockState)Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3);
                     pLevel.setBlock(pPos, newState, 2);
                     BlockState belowState = pLevel.getBlockState(pPos.below());
                     if (!super.mayPlaceOn(belowState, pLevel, pPos.below())) {
                        pLevel.destroyBlock(pPos, true);
                     }
                  }
               } else {
                  int nextAge = pRandom.nextBoolean() ? i + 1 : 3;
                  pLevel.setBlock(pPos, this.getStateForAge(nextAge), 2);
               }
            }
         }
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (!level.isClientSide() && this.getAge(state) == this.getMaxAge()) {
         popResource(level, blockPos, new ItemStack((ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get()));
         level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         BlockState blockState = (BlockState)state.setValue(AGE, 1);
         level.setBlock(blockPos, blockState, 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(player, blockState));
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return super.useWithoutItem(state, level, blockPos, player, blockHitResult);
      }
   }

   public float getGrowthSpeed(Block pBlock, BlockGetter pLevel, BlockPos pPos) {
      float f = 1.0F;
      BlockPos blockpos = pPos.below();

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            float f1 = 1.0F;
            BlockState blockState = pLevel.getBlockState(blockpos.offset(i, 0, j));
            if (blockState.is(Blocks.FARMLAND) && (Integer)blockState.getValue(FarmBlock.MOISTURE) > 0) {
               f1 = 3.0F;
            }

            if (i != 0 || j != 0) {
               f1 /= 4.0F;
            }

            f += f1;
         }
      }

      BlockPos north = pPos.north();
      BlockPos south = pPos.south();
      BlockPos west = pPos.west();
      BlockPos east = pPos.east();
      boolean flag = (pLevel.getBlockState(west).is(pBlock) || pLevel.getBlockState(east).is(pBlock))
         && (pLevel.getBlockState(north).is(pBlock) || pLevel.getBlockState(south).is(pBlock));
      if (flag) {
         return f / 2.0F;
      }

      flag = pLevel.getBlockState(west.north()).is(pBlock)
         || pLevel.getBlockState(east.north()).is(pBlock)
         || pLevel.getBlockState(east.south()).is(pBlock)
         || pLevel.getBlockState(west.south()).is(pBlock);
      return flag ? f / 2.0F : f;
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE_BY_AGE[pState.getValue(AGE)];
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   public int getAge(BlockState pState) {
      return (Integer)pState.getValue(AGE);
   }

   public BlockState getStateForAge(int pAge) {
      return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), pAge > 2 ? 3 : pAge);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{AGE});
   }

   protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return pState.is(Blocks.GRASS_BLOCK) || pState.is(Blocks.FARMLAND) || pState.is(Blocks.DIRT);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockState belowState = level.getBlockState(pos.below());
      return belowState.is(Blocks.GRASS_BLOCK) || belowState.is(Blocks.FARMLAND) || belowState.is(Blocks.DIRT);
   }
}
