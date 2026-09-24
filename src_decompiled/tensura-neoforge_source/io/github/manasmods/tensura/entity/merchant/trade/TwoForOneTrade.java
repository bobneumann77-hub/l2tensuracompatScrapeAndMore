package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class TwoForOneTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemLike inputItemSecond;
   private final Pair<Integer, Integer> inputCountSecond;
   private final ItemStack outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public TwoForOneTrade(ItemLike input, ItemLike inputSecond, ItemLike output, int maxUses, int xp) {
      this(input, 1, inputSecond, 1, output, 1, maxUses, xp);
   }

   public TwoForOneTrade(ItemLike input, int inputCount, ItemLike inputSecond, int inputCountSecond, ItemLike output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount + 1, inputSecond, inputCountSecond, inputCountSecond + 1, output, outputCount, outputCount + 1, maxUses, xp);
   }

   public TwoForOneTrade(
      ItemLike input,
      int minInput,
      int maxInput,
      ItemLike inputSecond,
      int minInputSecond,
      int maxInputSecond,
      ItemLike output,
      int minOutput,
      int maxOutput,
      int maxUses,
      int xp
   ) {
      this(
         input,
         Pair.of(minInput, maxInput),
         inputSecond,
         Pair.of(minInputSecond, maxInputSecond),
         new ItemStack(output),
         Pair.of(minOutput, maxOutput),
         maxUses,
         xp,
         0.05F
      );
   }

   public TwoForOneTrade(ItemLike input, ItemLike inputSecond, ItemStack output, int maxUses, int xp) {
      this(input, 1, inputSecond, 1, output, 1, maxUses, xp);
   }

   public TwoForOneTrade(ItemLike input, int inputCount, ItemLike inputSecond, int inputCountSecond, ItemStack output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount + 1, inputSecond, inputCountSecond, inputCountSecond + 1, output, outputCount, outputCount + 1, maxUses, xp);
   }

   public TwoForOneTrade(
      ItemLike input,
      int minInput,
      int maxInput,
      ItemLike inputSecond,
      int minInputSecond,
      int maxInputSecond,
      ItemStack output,
      int minOutput,
      int maxOutput,
      int maxUses,
      int xp
   ) {
      this(input, Pair.of(minInput, maxInput), inputSecond, Pair.of(minInputSecond, maxInputSecond), output, Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public TwoForOneTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      ItemLike inputSecond,
      Pair<Integer, Integer> inputCountSecond,
      ItemStack output,
      Pair<Integer, Integer> outputCount,
      int maxUses,
      int xp,
      float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.inputItemSecond = inputSecond;
      this.inputCountSecond = inputCountSecond;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      int inputNumberSecond = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCountSecond.getFirst(),
         (Integer)this.inputCountSecond.getSecond(),
         this.inputItemSecond.asItem().getDefaultMaxStackSize(),
         randomSource
      );
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), this.outputItem.getMaxStackSize(), randomSource
      );
      return new MerchantOffer(
         new ItemCost(this.inputItem, inputNumber),
         Optional.of(new ItemCost(this.inputItemSecond, inputNumberSecond)),
         this.outputItem.copyWithCount(outputNumber),
         this.maxUses,
         this.villagerXp,
         this.priceMultiplier
      );
   }
}
