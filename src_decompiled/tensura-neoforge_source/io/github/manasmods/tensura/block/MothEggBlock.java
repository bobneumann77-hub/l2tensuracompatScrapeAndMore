package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.entity.monster.HellCaterpillarEntity;
import io.github.manasmods.tensura.entity.monster.HellMothEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MothEggBlock extends Block {
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

   public MothEggBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
   }

   public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
      if (!pEntity.isSteppingCarefully() && !(pEntity instanceof HellMothEntity) && !(pEntity instanceof HellCaterpillarEntity)) {
         this.destroyEgg(pLevel, pState, pPos, pEntity);
      }

      super.stepOn(pLevel, pPos, pState, pEntity);
   }

   private void destroyEgg(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity) {
      if (this.canDestroyEgg(pEntity)) {
         if (pLevel.isClientSide() || pLevel.random.nextInt(100) != 0 || !pState.is((Block)TensuraBlocks.MOTH_EGG.get())) {
            if (!(pEntity instanceof Player player && player.getAbilities().instabuild)) {
               this.decreaseEggs(pLevel, pPos, pState);
               Block.popResource(pLevel, pPos, ((Item)TensuraMobDropItems.HELL_MOTH_SILK.get()).getDefaultInstance());
            }
         }
      }
   }

   private void decreaseEggs(Level pLevel, BlockPos pPos, BlockState pState) {
      pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + pLevel.random.nextFloat() * 0.2F);
      int i = (Integer)pState.getValue(EGGS);
      if (i <= 1) {
         pLevel.destroyBlock(pPos, false);
      } else {
         pLevel.setBlock(pPos, (BlockState)pState.setValue(EGGS, i - 1), 2);
         pLevel.gameEvent(GameEvent.BLOCK_DESTROY, pPos, Context.of(pState));
         pLevel.levelEvent(2001, pPos, Block.getId(pState));
      }
   }

   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (this.shouldUpdateHatchLevel(pLevel) && onEggCanPlace(pLevel, pPos)) {
         int i = (Integer)pState.getValue(HATCH);
         if (i < 2) {
            pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
            pLevel.setBlock(pPos, (BlockState)pState.setValue(HATCH, i + 1), 2);
         } else {
            pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
            pLevel.removeBlock(pPos, false);

            for (int j = 0; j < pState.getValue(EGGS); j++) {
               pLevel.levelEvent(2001, pPos, Block.getId(pState));
               HellCaterpillarEntity caterpillar = (HellCaterpillarEntity)((EntityType)MonsterEntityTypes.HELL_CATERPILLAR.get()).create(pLevel);
               if (caterpillar == null) {
                  break;
               }

               caterpillar.setAge(-24000);
               caterpillar.moveTo(pPos.getX() + 0.3 + j * 0.2, pPos.getY(), pPos.getZ() + 0.3, 0.0F, 0.0F);
               caterpillar.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(caterpillar.blockPosition()), MobSpawnType.BREEDING, null);
               pLevel.addFreshEntity(caterpillar);
            }
         }
      }
   }

   public static boolean onEggCanPlace(BlockGetter pLevel, BlockPos pPos) {
      return canPlaceEgg(pLevel, pPos.below());
   }

   public static boolean canPlaceEgg(BlockGetter pReader, BlockPos pPos) {
      return pReader.getBlockState(pPos).is(BlockTags.LEAVES) || pReader.getBlockState(pPos).is(BlockTags.WOOL);
   }

   public static boolean canLayEgg(BlockGetter pReader, BlockPos pPos) {
      return !canPlaceEgg(pReader, pPos) ? false : pReader.getBlockState(pPos.above()).isAir() && pReader.getBlockState(pPos.above(2)).isAir();
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (onEggCanPlace(pLevel, pPos) && !pLevel.isClientSide) {
         pLevel.levelEvent(2005, pPos, 0);
      }
   }

   private boolean shouldUpdateHatchLevel(Level pLevel) {
      float f = pLevel.getTimeOfDay(1.0F);
      return f < 0.69 && f > 0.65 ? true : pLevel.random.nextInt(300) == 0;
   }

   public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
      super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
      this.decreaseEggs(pLevel, pPos, pState);
   }

   public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
      return !pUseContext.isSecondaryUseActive() && pUseContext.getItemInHand().is(this.asItem()) && (Integer)pState.getValue(EGGS) < 4
         || super.canBeReplaced(pState, pUseContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      if (!fluidstate.isEmpty()) {
         return null;
      }

      BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
      return blockstate.is(this)
         ? (BlockState)blockstate.setValue(EGGS, Math.min(4, (Integer)blockstate.getValue(EGGS) + 1))
         : super.getStateForPlacement(pContext);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return pState.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{HATCH, EGGS});
   }

   private boolean canDestroyEgg(Entity pEntity) {
      if (pEntity instanceof HellMothEntity) {
         return false;
      } else {
         return pEntity instanceof HellCaterpillarEntity ? false : pEntity instanceof LivingEntity;
      }
   }
}
