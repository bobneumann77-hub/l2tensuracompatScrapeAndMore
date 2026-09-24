package dev.xkmc.l2hostility.content.item.tool;

import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;

public class DetectorGlasses extends Item {
   public DetectorGlasses(Properties properties) {
      super(properties);
   }

   @Nullable
   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return EquipmentSlot.HEAD;
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_GLASSES.get().withStyle(ChatFormatting.GRAY));
      list.add(LangData.sectionRender().withStyle(ChatFormatting.DARK_GREEN));
   }
}
