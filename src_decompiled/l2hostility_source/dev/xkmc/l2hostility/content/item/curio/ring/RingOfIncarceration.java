package dev.xkmc.l2hostility.content.item.curio.ring;

import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2hostility.content.item.curio.core.SingletonItem;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.entity.EntityTypeTest;
import top.theillusivec4.curios.api.SlotContext;

public class RingOfIncarceration extends SingletonItem {
   public RingOfIncarceration(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_RING_INCARCERATION.get().withStyle(ChatFormatting.GOLD));
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      LivingEntity wearer = slotContext.entity();
      if (wearer != null) {
         if (wearer.isShiftKeyDown()) {
            if (!wearer.isSpectator()) {
               Holder<Attribute> reach = Attributes.ENTITY_INTERACTION_RANGE;
               AttributeInstance attr = wearer.getAttribute(reach);
               double r = attr == null ? ((Attribute)reach.value()).getDefaultValue() : attr.getValue();

               for (LivingEntity e : wearer.level()
                  .getEntities(EntityTypeTest.forClass(LivingEntity.class), wearer.getBoundingBox().inflate(r), ex -> wearer.distanceTo(ex) < r)) {
                  if (!e.isSpectator() && !(e instanceof Player player && player.isCreative())) {
                     EffectUtil.refreshEffect(e, new MobEffectInstance(LCEffects.INCARCERATE, 40, 0, true, true), wearer);
                  }
               }
            }
         }
      }
   }
}
