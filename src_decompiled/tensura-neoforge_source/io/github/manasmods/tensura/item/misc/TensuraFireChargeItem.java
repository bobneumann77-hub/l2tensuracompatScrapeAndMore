package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.block.BlackFireBlock;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraFireChargeItem extends Item {
   public final Supplier<Block> fire;
   private static final Component TOOLTIP_CREATIVE_ONLY = Component.translatable("tooltip.tensura.creative_only").withStyle(ChatFormatting.RED);

   public TensuraFireChargeItem(Supplier<Block> fire, Properties pProperties) {
      super(pProperties);
      this.fire = fire;
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(TOOLTIP_CREATIVE_ONLY);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      Level level = pContext.getLevel();
      BlockPos blockpos = pContext.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      boolean flag = false;
      if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
         blockpos = blockpos.relative(pContext.getClickedFace());
         Block fireBlock = this.fire.get();
         boolean canBePlaced = fireBlock instanceof BlackFireBlock
            ? BlackFireBlock.canBePlacedAt(level, blockpos)
            : BaseFireBlock.canBePlacedAt(level, blockpos, pContext.getHorizontalDirection());
         if (canBePlaced) {
            this.playSound(level, blockpos);
            level.setBlockAndUpdate(blockpos, fireBlock.defaultBlockState());
            level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_PLACE, blockpos);
            flag = true;
         }
      } else {
         this.playSound(level, blockpos);
         level.setBlockAndUpdate(blockpos, (BlockState)blockstate.setValue(BlockStateProperties.LIT, Boolean.TRUE));
         level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
         flag = true;
      }

      if (flag) {
         pContext.getItemInHand().shrink(1);
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.FAIL;
      }
   }

   private void playSound(Level pLevel, BlockPos pPos) {
      RandomSource randomsource = pLevel.getRandom();
      pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
   }
}
