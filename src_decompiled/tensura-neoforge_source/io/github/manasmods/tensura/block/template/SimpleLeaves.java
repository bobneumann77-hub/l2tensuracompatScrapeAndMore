package io.github.manasmods.tensura.block.template;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public class SimpleLeaves extends LeavesBlock {
   public static final IntegerProperty DISTANCE_15 = BlockStateProperties.AGE_15;

   public SimpleLeaves() {
      this(SoundType.GRASS);
   }

   public SimpleLeaves(SoundType soundType) {
      super(
         Properties.of()
            .mapColor(MapColor.GRASS)
            .strength(0.2F)
            .randomTicks()
            .sound(soundType)
            .noOcclusion()
            .isValidSpawn(SimpleLeaves::ocelotOrBird)
            .isSuffocating(SimpleLeaves::never)
            .isViewBlocking(SimpleLeaves::never)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(DISTANCE_15, 15))
               .setValue(PERSISTENT, Boolean.FALSE))
            .setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public boolean isRandomlyTicking(BlockState pState) {
      return pState.getBlock().equals(TensuraBlocks.PALM_LEAVES.get())
         ? false
         : (Integer)pState.getValue(DISTANCE_15) == 15 && !(Boolean)pState.getValue(PERSISTENT);
   }

   protected boolean decaying(BlockState state) {
      return !(Boolean)state.getValue(PERSISTENT) && (Integer)state.getValue(DISTANCE_15) == 15;
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      pLevel.setBlock(pPos, updateDistance(pState, pLevel, pPos), 3);
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 60;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 30;
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      int i = getDistanceAt(pFacingState) + 1;
      if (i != 1 || (Integer)pState.getValue(DISTANCE_15) != i) {
         pLevel.scheduleTick(pCurrentPos, this, 1);
      }

      return pState;
   }

   private static BlockState updateDistance(BlockState pState, LevelAccessor pLevel, BlockPos pPos) {
      int i = 15;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for (Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(pPos, direction);
         i = Math.min(i, getDistanceAt(pLevel.getBlockState(blockpos$mutableblockpos)) + 1);
         if (i == 1) {
            break;
         }
      }

      return (BlockState)pState.setValue(DISTANCE_15, i);
   }

   private static int getDistanceAt(BlockState pNeighbor) {
      if (pNeighbor.is(BlockTags.LOGS)) {
         return 0;
      } else {
         return pNeighbor.getBlock() instanceof SimpleLeaves ? (Integer)pNeighbor.getValue(DISTANCE_15) : 15;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{DISTANCE, DISTANCE_15, PERSISTENT, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      BlockState blockstate = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)).setValue(DISTANCE, 7))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
      return updateDistance(blockstate, pContext.getLevel(), pContext.getClickedPos());
   }

   public static boolean ocelotOrBird(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
      return type == EntityType.OCELOT
         || type == EntityType.PARROT
         || type == MonsterEntityTypes.ONE_EYED_OWL.get()
         || type == MonsterEntityTypes.DRAGON_PEACOCK.get();
   }

   public static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
      return false;
   }
}
