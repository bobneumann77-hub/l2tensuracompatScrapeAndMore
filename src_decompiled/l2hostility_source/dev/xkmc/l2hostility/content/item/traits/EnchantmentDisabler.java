package dev.xkmc.l2hostility.content.item.traits;

import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.Level;

public class EnchantmentDisabler {
   private static final String ROOT = "l2hostility_enchantment";
   private static final String OLD = "originalEnchantments";
   private static final String TIME = "startTime";

   public static void disableEnchantment(Level level, ItemStack stack, int duration) {
      ItemEnchantments enchs = (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS);
      if (enchs != null) {
         double durability = stack.getMaxDamage() == 0 ? 0.0 : 1.0 * stack.getDamageValue() / stack.getMaxDamage();
         Mutable retain = new Mutable(ItemEnchantments.EMPTY);
         Mutable disabled = new Mutable(ItemEnchantments.EMPTY);

         for (Entry<Holder<Enchantment>> e : enchs.entrySet()) {
            if (((Holder)e.getKey()).is(LHTagGen.NO_DISPELL)) {
               retain.set((Holder)e.getKey(), e.getIntValue());
            } else {
               disabled.set((Holder)e.getKey(), e.getIntValue());
            }
         }

         if (!disabled.keySet().isEmpty()) {
            stack.set(DataComponents.ENCHANTMENTS, retain.toImmutable());
            LHItems.DC_DISPELL_START.set(stack, level.getGameTime() + duration);
            LHItems.DC_DISPELL_ENCH.set(stack, disabled.toImmutable());
            if (stack.isDamageableItem()) {
               stack.setDamageValue(Mth.clamp((int)Math.floor(durability * stack.getMaxDamage()), 0, stack.getMaxDamage() - 1));
            }
         }
      }
   }

   public static void tickStack(Level level, Entity user, ItemStack stack) {
      if (!level.isClientSide()) {
         if (user instanceof Player player
            && !player.getAbilities().instabuild
            && stack.isEnchanted()
            && stack.getEnchantmentLevel(LHEnchantments.VANISH.holder()) > 0) {
            stack.setCount(0);
         } else {
            ItemEnchantments disabled = (ItemEnchantments)LHItems.DC_DISPELL_ENCH.get(stack);
            if (disabled != null) {
               long time = (Long)LHItems.DC_DISPELL_START.getOrDefault(stack, 0L);
               if (level.getGameTime() >= time) {
                  stack.remove(LHItems.DC_DISPELL_ENCH);
                  stack.remove(LHItems.DC_DISPELL_START);
                  Mutable builder = new Mutable(disabled);
                  ItemEnchantments enchs = (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS);
                  if (enchs != null) {
                     for (Entry<Holder<Enchantment>> e : enchs.entrySet()) {
                        builder.set((Holder)e.getKey(), e.getIntValue());
                     }
                  }

                  EnchantmentHelper.setEnchantments(stack, builder.toImmutable());
               }
            }
         }
      }
   }

   public static void modifyTooltip(ItemStack stack, List<Component> tooltip, Level level, TooltipContext context, TooltipFlag flags) {
      ItemEnchantments disabled = (ItemEnchantments)LHItems.DC_DISPELL_ENCH.get(stack);
      if (disabled != null) {
         long time = Math.max(0L, (Long)LHItems.DC_DISPELL_START.getOrDefault(stack, 0L) - level.getGameTime());
         tooltip.add(
            LangData.TOOLTIP_DISABLE
               .get(
                  Component.literal(disabled.size() + "").withStyle(ChatFormatting.LIGHT_PURPLE),
                  Component.literal(time / 20L + "").withStyle(ChatFormatting.AQUA)
               )
               .withStyle(ChatFormatting.RED)
         );
         disabled.addToTooltip(context, e -> tooltip.add(e.copy().withStyle(ChatFormatting.DARK_GRAY)), flags);
      }
   }
}
