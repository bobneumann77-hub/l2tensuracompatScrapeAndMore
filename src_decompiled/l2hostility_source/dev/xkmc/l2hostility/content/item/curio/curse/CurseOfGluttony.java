package dev.xkmc.l2hostility.content.item.curio.curse;

import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class CurseOfGluttony extends CurseCurioItem {
   public CurseOfGluttony(Properties props) {
      super(props);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      String rate = LangData.perc((Double)LHConfig.SERVER.gluttonyBottleDropRate.get());
      list.add(LangData.ITEM_CHARM_GLUTTONY.get(rate).withStyle(ChatFormatting.GOLD));
   }
}
