package dev.xkmc.l2hostility.content.item.curio.misc;

import dev.xkmc.l2complements.content.item.curios.EffectValidItem;
import dev.xkmc.l2hostility.content.item.curio.core.MultiSlotItem;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class DivinityCross extends MultiSlotItem implements EffectValidItem {
   public DivinityCross(Properties props) {
      super(props);
   }

   public boolean isEffectValid(MobEffectInstance ins, ItemStack itemStack, LivingEntity livingEntity) {
      return ((MobEffect)ins.getEffect().value()).isBeneficial() && ins.getAmplifier() == 0;
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.DIVINITY_CROSS.get().withStyle(ChatFormatting.GOLD));
   }
}
