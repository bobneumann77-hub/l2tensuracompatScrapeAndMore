package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class OneForRandomListTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final List<ItemStack> outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final int maxUses;
   private final int villagerXp;
   private float priceMultiplier;

   public OneForRandomListTrade(ItemLike input, int inputCount, List<ItemLike> output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForRandomListTrade(ItemLike input, int minInput, List<ItemLike> output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, minInput, minInput, output, minOutput, maxOutput, maxUses, xp);
   }

   public OneForRandomListTrade(ItemLike input, int minInput, int maxInput, List<ItemLike> output, int outputCount, int maxUses, int xp) {
      this(input, minInput, maxInput, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForRandomListTrade(ItemLike input, int minInput, int maxInput, List<ItemLike> output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output.stream().<ItemStack>map(ItemStack::new).toList(), Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public OneForRandomListTrade(
      ItemLike input, Pair<Integer, Integer> inputCount, List<ItemStack> output, Pair<Integer, Integer> outputCount, int maxUses, int xp, float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public OneForRandomListTrade setPriceMultiplier(float priceMultiplier) {
      this.priceMultiplier = priceMultiplier;
      return this;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      ItemStack outputStack = this.outputItem.get(randomSource.nextInt(this.outputItem.size()));
      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), outputStack.getMaxStackSize(), randomSource
      );
      return new MerchantOffer(
         new ItemCost(this.inputItem, inputNumber), outputStack.copyWithCount(outputNumber), this.maxUses, this.villagerXp, this.priceMultiplier
      );
   }
}
