package io.github.manasmods.tensura.entity.merchant.trade;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class TensuraTradeHelper {
   public static ItemStack getDamagedItem(ItemLike item, int damage) {
      ItemStack stack = new ItemStack(item);
      stack.setDamageValue(damage);
      return stack;
   }

   public static ItemStack getDamagedItem(ItemLike item, int minDamage, RandomSource random) {
      ItemStack stack = new ItemStack(item);
      stack.setDamageValue(random.nextInt(minDamage, stack.getMaxDamage()));
      return stack;
   }

   public static ItemStack getDamagedItem(ItemLike item, int minDamage, int maxDamage, RandomSource random) {
      ItemStack stack = new ItemStack(item);
      stack.setDamageValue(random.nextInt(minDamage, maxDamage));
      return stack;
   }

   public static int getClampedStackSize(int min, int max, int maxStack, RandomSource randomSource) {
      min = Math.max(min, 1);
      max = Math.max(Math.min(max, maxStack), min);
      return Math.min(randomSource.nextInt(min, max + 1), maxStack);
   }
}
