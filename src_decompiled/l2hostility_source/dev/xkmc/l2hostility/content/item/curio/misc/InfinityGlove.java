package dev.xkmc.l2hostility.content.item.curio.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2hostility.content.item.curio.core.SingletonItem;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class InfinityGlove extends SingletonItem {
   public InfinityGlove(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext ctx, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
      CuriosApi.addSlotModifier(map, "ring", id, 5.0, Operation.ADD_VALUE);
      CuriosApi.addSlotModifier(map, "charm", id, 1.0, Operation.ADD_VALUE);
      return map;
   }
}
