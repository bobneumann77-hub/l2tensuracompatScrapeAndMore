package dev.xkmc.l2hostility.content.item.consumable;

import dev.xkmc.l2hostility.content.item.traits.EffectBooster;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class EffectBoosterBottle extends DrinkableBottleItem {
   public EffectBoosterBottle(Properties props) {
      super(props);
   }

   @Override
   protected void doServerLogic(ServerPlayer player) {
      EffectBooster.boostBottle(player);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(
         LangData.TOOLTIP_WITCH_BOTTLE
            .get(
               (Integer)LHConfig.SERVER.witchChargeMinDuration.get() / 20,
               Math.round(100.0 * (Double)LHConfig.SERVER.drainDuration.get()),
               (Integer)LHConfig.SERVER.drainDurationMax.get() / 20
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
