package dev.xkmc.l2hostility.content.item.consumable;

import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.Level;

public class BookEverything extends Item {
   @Nullable
   private static Holder<Enchantment> getEnch(RegistryAccess access, ItemStack stack) {
      String name = stack.getHoverName().getString();

      try {
         ResourceLocation rl = ResourceLocation.parse(name.trim());
         Registry<Enchantment> reg = access.registryOrThrow(Registries.ENCHANTMENT);
         return (Holder<Enchantment>)reg.getHolder(rl).orElse(null);
      } catch (Exception e) {
         return null;
      }
   }

   public static boolean allow(Holder<Enchantment> holder) {
      if (!holder.is(EnchantmentTags.TREASURE) && holder.is(EnchantmentTags.IN_ENCHANTING_TABLE)) {
         Enchantment ench = (Enchantment)holder.value();
         return ench.getMaxCost(ench.getMaxLevel()) >= ench.getMinCost(ench.getMaxLevel());
      } else {
         return false;
      }
   }

   public static int cost(Enchantment ench) {
      return Math.max(ench.getMaxLevel(), ench.getMaxCost(ench.getMaxLevel()) / 10);
   }

   public BookEverything(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isShiftKeyDown()) {
         if (!level.isClientSide()) {
            ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);
            Registry<Enchantment> enchreg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Mutable builder = new Mutable(ItemEnchantments.EMPTY);
            enchreg.holders().filter(BookEverything::allow).forEach(e -> builder.set(e.getDelegate(), ((Enchantment)e.value()).getMaxLevel()));
            EnchantmentHelper.setEnchantments(result, builder.toImmutable());
            stack.shrink(1);
            if (stack.isEmpty()) {
               return InteractionResultHolder.success(result);
            }

            player.getInventory().placeItemBackInInventory(result);
         }

         return InteractionResultHolder.consume(stack);
      } else {
         Holder<Enchantment> ench = getEnch(level.registryAccess(), stack);
         if (ench == null) {
            return InteractionResultHolder.fail(stack);
         }

         if (!allow(ench)) {
            return InteractionResultHolder.fail(stack);
         }

         int cost = cost((Enchantment)ench.value());
         if (player.experienceLevel < cost) {
            return InteractionResultHolder.fail(stack);
         }

         if (!level.isClientSide()) {
            player.giveExperienceLevels(-cost);
            EnchantmentInstance ins = new EnchantmentInstance(ench, ((Enchantment)ench.value()).getMaxLevel());
            ItemStack result = EnchantedBookItem.createForEnchantment(ins);
            player.getInventory().placeItemBackInInventory(result);
         }

         return InteractionResultHolder.success(stack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_BOOK_EVERYTHING_USE.get().withStyle(ChatFormatting.GRAY));
      list.add(LangData.ITEM_BOOK_EVERYTHING_SHIFT.get().withStyle(ChatFormatting.GOLD));
      Level level = ctx.level();
      Holder<Enchantment> holder = null;
      if (level != null) {
         holder = getEnch(level.registryAccess(), stack);
      }

      if (holder == null) {
         list.add(LangData.ITEM_BOOK_EVERYTHING_INVALID.get().withStyle(ChatFormatting.GRAY));
      } else {
         Enchantment e = (Enchantment)holder.value();
         Component name = Enchantment.getFullname(holder, e.getMaxLevel());
         if (allow(holder)) {
            int cost = cost(e);
            list.add(LangData.ITEM_BOOK_EVERYTHING_READY.get(name, cost).withStyle(ChatFormatting.GREEN));
         } else {
            list.add(LangData.ITEM_BOOK_EVERYTHING_FORBIDDEN.get(name).withStyle(ChatFormatting.RED));
         }
      }
   }
}
