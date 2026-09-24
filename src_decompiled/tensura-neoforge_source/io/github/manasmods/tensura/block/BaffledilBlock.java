package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.config.BlockConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BaffledilBlock extends FlowerBlock {
   public static final BooleanProperty DEACTIVATED = BlockStateProperties.DISARMED;
   public static final BlockConfig.Baffledil CONFIG = ObjectSelectionHelper.CONFIG.Baffledil;

   public BaffledilBlock(SuspiciousStewEffects suspiciousStewEffects, Properties properties) {
      super(suspiciousStewEffects, properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DEACTIVATED, Boolean.FALSE));
   }

   public BaffledilBlock(Holder<MobEffect> holder, float f, Properties properties) {
      super(holder, f, properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DEACTIVATED, Boolean.FALSE));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{DEACTIVATED});
   }

   public boolean isRandomlyTicking(BlockState pState) {
      return !(Boolean)pState.getValue(DEACTIVATED);
   }

   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
      this.applyHypnosis(state, level, pos);
      level.scheduleTick(pos, this, CONFIG.hypnosisDuration);
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
      this.tick(state, level, pos, source);
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (!level.isClientSide()) {
         this.applyHypnosis(state, level, pos);
      }
   }

   public void playerDestroy(Level level, Player pPlayer, BlockPos pos, BlockState state, @Nullable BlockEntity pTe, ItemStack pStack) {
      super.playerDestroy(level, pPlayer, pos, state, pTe, pStack);
      this.applyHypnosis(state, level, pos);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if ((Boolean)blockState.getValue(DEACTIVATED) || !itemStack.is(Items.SHEARS)) {
         return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
      }

      if (level.isClientSide()) {
         return ItemInteractionResult.sidedSuccess(level.isClientSide());
      }

      level.playSound(null, blockPos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.setBlock(blockPos, (BlockState)blockState.setValue(DEACTIVATED, true), 11);
      itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(interactionHand));
      level.gameEvent(player, GameEvent.SHEAR, blockPos);
      player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
      return ItemInteractionResult.sidedSuccess(level.isClientSide());
   }

   private void applyHypnosis(BlockState state, Level level, BlockPos pos) {
      if (!(Boolean)state.getValue(DEACTIVATED)) {
         if (!(CONFIG.hypnosisRadius <= 0.0) && CONFIG.hypnosisDuration > 0 && CONFIG.hypnosisLevel > 0) {
            for (Player player : level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(CONFIG.hypnosisRadius), Alignment::shouldConsumeAir)) {
               MobEffectInstance hypnosis = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), CONFIG.hypnosisDuration, CONFIG.hypnosisLevel - 1, false, false, false
               );
               player.addEffect(hypnosis);
            }
         }
      }
   }
}
