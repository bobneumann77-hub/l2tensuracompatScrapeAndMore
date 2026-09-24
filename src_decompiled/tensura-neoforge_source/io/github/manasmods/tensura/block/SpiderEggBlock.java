package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.block.template.AllSidesDirectionalBlock;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.monster.BlackSpiderEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiderEggBlock extends AllSidesDirectionalBlock {
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

   public SpiderEggBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_GREEN)
            .strength(0.5F)
            .lightLevel(blockState -> 3)
            .speedFactor(0.7F)
            .jumpFactor(0.8F)
            .sound(SoundType.SHROOMLIGHT)
            .pushReaction(PushReaction.NORMAL)
            .randomTicks()
            .noOcclusion()
            .isValidSpawn(WebBlock::always)
      );
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(FACING, Direction.UP));
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      this.breakEggs(pLevel, pState, pPos, null);
   }

   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (this.shouldUpdateHatchLevel(pLevel)) {
         int i = (Integer)pState.getValue(HATCH);
         if (i < 2) {
            pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
            pLevel.setBlock(pPos, (BlockState)pState.setValue(HATCH, i + 1), 2);
         } else {
            pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
            pLevel.removeBlock(pPos, false);
            pLevel.levelEvent(2001, pPos, Block.getId(pState));
            BlackSpiderEntity spider = (BlackSpiderEntity)((EntityType)MonsterEntityTypes.BLACK_SPIDER.get()).create(pLevel);
            if (spider == null) {
               return;
            }

            spider.setAge(-24000);
            spider.moveTo(pPos.getX() + 0.3, pPos.getY(), pPos.getZ() + 0.3, 0.0F, 0.0F);
            spider.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(spider.blockPosition()), MobSpawnType.BREEDING, null);
            pLevel.addFreshEntity(spider);
         }
      }
   }

   public static boolean canLayEgg(BlockGetter pLevel, BlockPos pPos) {
      return pLevel.getBlockState(pPos).isFaceSturdy(pLevel, pPos, Direction.UP) && pLevel.getBlockState(pPos.above()).isAir();
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getClickedFace();
      return (BlockState)super.getStateForPlacement(pContext).setValue(FACING, direction);
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (!pLevel.isClientSide) {
         pLevel.levelEvent(2005, pPos, 0);
      }
   }

   public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
      super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
      if (TensuraEnchantmentHelper.getEnchantmentLevel(pLevel, Enchantments.SILK_TOUCH, pStack) <= 0) {
         this.spawnHostileBaby(pLevel, pState, pPos, pPlayer);
         this.breakSurroundingEggs(pLevel, pPos);
      }
   }

   public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
      super.wasExploded(pLevel, pPos, pExplosion);
      this.spawnHostileBaby(pLevel, pLevel.getBlockState(pPos), pPos, pExplosion.getIndirectSourceEntity());
   }

   public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
      if (!pEntity.getType().is(EntityTypeTags.ARTHROPOD)) {
         if (!pEntity.isSteppingCarefully()) {
            if ((Integer)pState.getValue(HATCH) != 0 || !(pEntity.getBbHeight() <= 1.0F)) {
               if (!(pEntity instanceof Player player && player.hasInfiniteMaterials())) {
                  this.breakEggs(pLevel, pState, pPos, pEntity);
               }
            }
         }
      }
   }

   public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
      if (!pEntity.getType().is(EntityTypeTags.ARTHROPOD)) {
         if (!(pEntity instanceof Player player && player.hasInfiniteMaterials())) {
            pEntity.causeFallDamage(pFallDistance, 0.3F, pEntity.damageSources().fall());
            this.breakEggs(pLevel, pState, pPos, pEntity);
         }
      }
   }

   private void spawnHostileBaby(Level pLevel, BlockState pState, BlockPos pPos, @Nullable Entity pEntity) {
      pLevel.levelEvent(2001, pPos, Block.getId(pState));
      if (pLevel instanceof ServerLevel serverLevel) {
         BlackSpiderEntity spider = (BlackSpiderEntity)((EntityType)MonsterEntityTypes.BLACK_SPIDER.get()).create(pLevel);
         if (spider == null) {
            return;
         }

         if (pEntity instanceof LivingEntity living && spider.canAttack(living)) {
            spider.setTarget(living);
         }

         spider.setAge(-24000);
         spider.moveTo(pPos.getX() + 0.3, pPos.getY(), pPos.getZ() + 0.3, 0.0F, 0.0F);
         spider.finalizeSpawn(serverLevel, pLevel.getCurrentDifficultyAt(spider.blockPosition()), MobSpawnType.BREEDING, null);
         pLevel.addFreshEntity(spider);
      }
   }

   private void breakEggs(Level pLevel, BlockState pState, BlockPos pPos, @Nullable Entity pEntity) {
      pLevel.destroyBlock(pPos, false);
      this.spawnHostileBaby(pLevel, pState, pPos, pEntity);
      this.breakSurroundingEggs(pLevel, pPos);
   }

   private void breakSurroundingEggs(Level pLevel, BlockPos pPos) {
      for (Direction direction : Direction.values()) {
         BlockPos relative = pPos.relative(direction);
         BlockState state = pLevel.getBlockState(relative);
         if (state.is((Block)TensuraBlocks.SPIDER_EGG.get())) {
            pLevel.scheduleTick(relative, state.getBlock(), 10);
         }

         if (direction.getAxis().isHorizontal()) {
            BlockPos relative2 = pPos.relative(direction, 2);
            BlockState state2 = pLevel.getBlockState(relative2);
            if (state2.is((Block)TensuraBlocks.SPIDER_EGG.get())) {
               pLevel.scheduleTick(relative2, state2.getBlock(), 10);
            }
         }
      }
   }

   private boolean shouldUpdateHatchLevel(Level pLevel) {
      float f = pLevel.getTimeOfDay(1.0F);
      return f < 0.69 && f > 0.65 ? true : pLevel.random.nextInt(300) == 0;
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return (pContext instanceof EntityCollisionContext entityCollision ? entityCollision.getEntity() : null) instanceof BlackSpiderEntity spider
            && !spider.isBaby()
         ? Shapes.empty()
         : super.getCollisionShape(pState, pLevel, pPos, pContext);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{HATCH}).add(new Property[]{FACING});
   }
}
