package io.github.manasmods.tensura.item.tool.custom;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.tool.MultitoolItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArmorsaurusGauntletItem extends MultitoolItem {
   public ArmorsaurusGauntletItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         TensuraBlockTags.MINEABLE_WITH_MULTITOOL,
         new Properties().durability(100).arch$tab(TensuraCreativeTabs.GEARS),
         MultitoolItem.createAttributes(7, -2.8F, 2.0, -1.0)
      );
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 72000;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      pPlayer.startUsingItem(pHand);
      return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
   }

   public float getDestroySpeed(ItemStack pStack, BlockState pState) {
      return pState.is(TensuraBlockTags.DIGGABLE_BY_MONSTER) ? 18.0F : super.getDestroySpeed(pStack, pState);
   }
}
