package io.github.manasmods.tensura.item.weapon;

import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TwoHandedSwordItem extends TensuraSwordItem {
   private static final MutableComponent TOOLTIP = Component.translatable("tooltip.tensura.long_sword.tooltip");

   public TwoHandedSwordItem(
      Tier pTier,
      int damage,
      float speed,
      double range,
      double sweepRatio,
      double critChance,
      double critMultiplier,
      int oneHandedDamage,
      float oneHandedSpeed,
      double oneHandedRange,
      double oneHandedSweepRatio,
      double oneHandedCritChance,
      double oneHandedCritMultiplier,
      Properties pProperties
   ) {
      super(
         pTier,
         damage,
         speed,
         range,
         sweepRatio,
         critChance,
         critMultiplier,
         pProperties.component(
               (DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get(),
               createAttributes(pTier, oneHandedDamage, oneHandedSpeed, oneHandedRange, oneHandedSweepRatio, oneHandedCritChance, oneHandedCritMultiplier)
            )
            .component(
               (DataComponentType)TensuraDataComponents.TWO_HANDED_MODIFIERS.get(),
               createAttributes(pTier, damage, speed, range, sweepRatio, critChance, critMultiplier)
            )
      );
   }

   public void setTwoHanded(ItemStack stack, boolean twoHand) {
      stack.set(
         DataComponents.ATTRIBUTE_MODIFIERS,
         twoHand
            ? (ItemAttributeModifiers)stack.get((DataComponentType)TensuraDataComponents.TWO_HANDED_MODIFIERS.get())
            : (ItemAttributeModifiers)stack.get((DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get())
      );
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(this.getTwoHandedTooltip());
   }

   protected MutableComponent getTwoHandedTooltip() {
      return TOOLTIP;
   }
}
