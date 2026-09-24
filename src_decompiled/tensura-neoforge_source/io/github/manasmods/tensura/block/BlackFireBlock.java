package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlackFireBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   private static final VoxelShape BASE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

   public BlackFireBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .noLootTable()
            .replaceable()
            .noCollission()
            .instabreak()
            .lightLevel(state -> 15)
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.DESTROY)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{AGE});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)this.defaultBlockState().setValue(AGE, 0);
   }

   public static boolean canBePlacedAt(Level pLevel, BlockPos pPos) {
      BlockState blockstate = pLevel.getBlockState(pPos);
      return !blockstate.isAir() ? false : canBlackFireSurvive(pLevel, pPos);
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (!pOldState.is(pState.getBlock()) && !pState.canSurvive(pLevel, pPos)) {
         pLevel.removeBlock(pPos, false);
      } else {
         pLevel.scheduleTick(pPos, this, getFireTickDelay(pLevel.random));
      }
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      return canBlackFireSurvive(pLevel, pCurrentPos)
         ? (BlockState)((Block)TensuraBlocks.BLACK_FIRE.get()).defaultBlockState().setValue(AGE, (Integer)pState.getValue(AGE))
         : Blocks.AIR.defaultBlockState();
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return BASE_SHAPE;
   }

   public static int getFireTickDelay(RandomSource pRandom) {
      return 30 + pRandom.nextInt(10);
   }

   public static boolean canBlackFireSurvive(LevelReader pLevel, BlockPos pPos) {
      BlockPos blockpos = pPos.below();
      if (pLevel.getBlockState(blockpos).canBeReplaced()) {
         return false;
      } else {
         return isValidFireLocation(pLevel, pPos) ? true : pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, Direction.UP);
      }
   }

   private static boolean isValidFireLocation(BlockGetter pLevel, BlockPos pPos) {
      for (Direction direction : Direction.values()) {
         if (pLevel.getBlockState(pPos.relative(direction)).ignitedByLava()) {
            return true;
         }
      }

      return false;
   }

   public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if (!level.isClientSide()) {
         level.levelEvent(null, 1009, blockPos, 0);
      }

      level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, Context.of(player, blockState));
      return super.playerWillDestroy(level, blockPos, blockState, player);
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      pLevel.scheduleTick(pPos, this, getFireTickDelay(pLevel.random));
      if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if (!pState.canSurvive(pLevel, pPos)) {
            pLevel.removeBlock(pPos, false);
         }

         int i = (Integer)pState.getValue(AGE);
         int j = Math.min(15, i + pRandom.nextInt(3) / 2);
         if (i != j) {
            pState = (BlockState)pState.setValue(AGE, j);
            pLevel.setBlock(pPos, pState, 4);
         }

         if (!pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.BLACK_FIRE_SOURCE) && i == 15 && pRandom.nextInt(4) == 0) {
            pLevel.removeBlock(pPos, false);
         }
      }
   }

   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pEntity.fireImmune() && pEntity instanceof LivingEntity target) {
         int damage = 2;
         DamageSource source = TensuraDamageTypes.getDamageSource(pLevel, TensuraDamageTypes.BLACK_FLAME);
         TensuraDamageHelper.hurtSplitElemental(target, source, 0.9F, damage);
         target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 200));
         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN), 200, 0, false, false));
      }

      super.entityInside(pState, pLevel, pPos, pEntity);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pRandom.nextInt(24) == 0) {
         pLevel.playLocalSound(
            pPos.getX() + 0.5,
            pPos.getY() + 0.5,
            pPos.getZ() + 0.5,
            SoundEvents.FIRE_AMBIENT,
            SoundSource.BLOCKS,
            1.0F + pRandom.nextFloat(),
            pRandom.nextFloat() * 0.7F + 0.3F,
            false
         );
      }

      for (int i = 0; i < 3; i++) {
         double d0 = pPos.getX() + pRandom.nextDouble();
         double d1 = pPos.getY() + pRandom.nextDouble() * 0.5 + 0.5;
         double d2 = pPos.getZ() + pRandom.nextDouble();
         pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0, 0.02, 0.0);
      }
   }

   protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
   }
}
