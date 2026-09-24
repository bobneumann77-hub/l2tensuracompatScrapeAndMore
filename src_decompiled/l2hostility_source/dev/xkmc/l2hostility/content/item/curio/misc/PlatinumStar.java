package dev.xkmc.l2hostility.content.item.curio.misc;

import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.item.curio.core.EquipCurioItem;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class PlatinumStar extends CurseCurioItem implements EquipCurioItem {
   public PlatinumStar(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_PLATINUM_STAR.get().withStyle(ChatFormatting.GOLD));
   }
}
