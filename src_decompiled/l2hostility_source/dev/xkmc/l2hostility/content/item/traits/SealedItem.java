package dev.xkmc.l2hostility.content.item.traits;

import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class SealedItem extends Item {
   public static ItemStack sealItem(ItemStack stack, int time) {
      if (stack.is((Item)LHItems.SEAL.get())) {
         LHItems.DC_SEAL_TIME.set(stack, time);
         return stack;
      }

      ItemStack ans = LHItems.SEAL.asStack();
      LHItems.DC_SEAL_TIME.set(ans, time);
      LHItems.DC_SEAL_STACK.set(ans, new DCStack(stack));
      if (stack.getEnchantmentLevel(LHEnchantments.VANISH.holder()) > 0) {
         ans.enchant(LHEnchantments.VANISH.holder(), 1);
      }

      if (stack.getEnchantmentLevel(LCEnchantments.SOUL_BOUND.holder()) > 0) {
         ans.enchant(LCEnchantments.SOUL_BOUND.holder(), 1);
      }

      return ans;
   }

   public SealedItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      player.startUsingItem(hand);
      return InteractionResultHolder.consume(stack);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      user.stopUsingItem();
      DCStack sealed = (DCStack)LHItems.DC_SEAL_STACK.get(stack);
      return sealed == null ? ItemStack.EMPTY : sealed.stack();
   }

   public int getUseDuration(ItemStack stack, LivingEntity le) {
      return (Integer)LHItems.DC_SEAL_TIME.getOrDefault(stack, 0);
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.EAT;
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.TOOLTIP_SEAL_DATA.get().withStyle(ChatFormatting.GRAY));
      DCStack sealed = (DCStack)LHItems.DC_SEAL_STACK.get(stack);
      if (sealed != null) {
         list.add(sealed.stack().getHoverName());
      }

      int time = (Integer)LHItems.DC_SEAL_TIME.getOrDefault(stack, 0);
      list.add(LangData.TOOLTIP_SEAL_TIME.get(Component.literal(time / 20 + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.RED));
   }
}
