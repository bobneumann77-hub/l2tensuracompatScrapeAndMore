package dev.xkmc.l2hostility.content.item.curio.ring;

import dev.xkmc.l2hostility.content.item.curio.core.SingletonItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import top.theillusivec4.curios.api.SlotContext;

public class RingOfHealing extends SingletonItem {
   public RingOfHealing(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_RING_HEALING.get(LangData.perc((Double)LHConfig.SERVER.ringOfHealingRate.get())).withStyle(ChatFormatting.GOLD));
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      LivingEntity wearer = slotContext.entity();
      if (wearer != null) {
         if (!wearer.level().isClientSide()) {
            if (wearer.tickCount % 20 == 0) {
               wearer.heal((float)((Double)LHConfig.SERVER.ringOfHealingRate.get() * wearer.getMaxHealth()));
            }
         }
      }
   }
}
