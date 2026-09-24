package io.github.manasmods.tensura.item.weapon;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleSickleItem extends DiggerItem {
   public SimpleSickleItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
      super(
         pTier, TensuraBlockTags.MINEABLE_WITH_SICKLE, pProperties.attributes(DiggerItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier))
      );
   }

   public SimpleSickleItem(Tier pTier, Properties properties) {
      this(pTier, 2, -2.8F, properties);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      if (!pContext.getLevel().isClientSide()) {
         Level level = pContext.getLevel();
         BlockPos positionClicked = pContext.getClickedPos();
         BlockState blockClicked = level.getBlockState(positionClicked);
         if (blockClicked.is(TensuraBlockTags.MINEABLE_WITH_SICKLE)) {
            Item thatch = (Item)TensuraMaterialItems.THATCH.get();
            ItemEntity entityItem = new ItemEntity(
               level, positionClicked.getX(), positionClicked.getY(), positionClicked.getZ(), new ItemStack(thatch, this.drop(blockClicked.getBlock()))
            );
            level.destroyBlock(positionClicked, false);
            level.addFreshEntity(entityItem);
            pContext.getItemInHand().hurtAndBreak(1, Objects.requireNonNull(pContext.getPlayer()), LivingEntity.getSlotForHand(pContext.getHand()));
         }
      }

      return InteractionResult.SUCCESS;
   }

   public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity entity) {
      if (!pLevel.isClientSide()) {
         if (!pState.is(TensuraBlockTags.MINEABLE_WITH_SICKLE)) {
            return super.mineBlock(pStack, pLevel, pState, pPos, entity);
         }

         Item thatch = (Item)TensuraMaterialItems.THATCH.get();
         ItemEntity entityItem = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), new ItemStack(thatch, this.drop(pState.getBlock())));
         pLevel.destroyBlock(pPos, false);
         pLevel.addFreshEntity(entityItem);
         pStack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
      }

      return true;
   }

   private int drop(Block block) {
      if (block == Blocks.GRASS_BLOCK || block == Blocks.SEAGRASS) {
         return 1;
      } else if (block == Blocks.TALL_GRASS || block == Blocks.TALL_SEAGRASS) {
         return 2;
      } else if (block == TensuraBlocks.THATCH_BLOCK.get()) {
         return 9;
      } else if (block == TensuraBlocks.THATCH_SLAB.get()) {
         return 3;
      } else if (block == TensuraBlocks.THATCH_STAIRS.get()) {
         return 6;
      } else {
         return block == TensuraBlocks.THATCH_WALL.get() ? 9 : 1;
      }
   }
}
