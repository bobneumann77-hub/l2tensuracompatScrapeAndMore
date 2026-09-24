package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class OneForOneTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemStack outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final int maxUses;
   private final int villagerXp;
   private float priceMultiplier;

   public OneForOneTrade(ItemLike input, ItemLike output, int maxUses, int xp) {
      this(input, 1, output, 1, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int inputCount, ItemLike output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, ItemLike output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, minInput, minInput, output, minOutput, maxOutput, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, int maxInput, ItemLike output, int outputCount, int maxUses, int xp) {
      this(input, minInput, maxInput, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, int maxInput, ItemLike output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), new ItemStack(output), Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public OneForOneTrade(ItemLike input, ItemStack output, int maxUses, int xp) {
      this(input, 1, output, 1, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, ItemStack output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, minInput, minInput, output, minOutput, maxOutput, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, int maxInput, ItemStack output, int outputCount, int maxUses, int xp) {
      this(input, minInput, maxInput, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int inputCount, ItemStack output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForOneTrade(ItemLike input, int minInput, int maxInput, ItemStack output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public OneForOneTrade(
      ItemLike input, Pair<Integer, Integer> inputCount, ItemStack output, Pair<Integer, Integer> outputCount, int maxUses, int xp, float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public OneForOneTrade setPriceMultiplier(float priceMultiplier) {
      this.priceMultiplier = priceMultiplier;
      return this;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), this.outputItem.getMaxStackSize(), randomSource
      );
      return new MerchantOffer(
         new ItemCost(this.inputItem, inputNumber), this.outputItem.copyWithCount(outputNumber), this.maxUses, this.villagerXp, this.priceMultiplier
      );
   }
}
