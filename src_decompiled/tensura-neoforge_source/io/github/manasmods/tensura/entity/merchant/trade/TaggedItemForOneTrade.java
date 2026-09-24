package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class TaggedItemForOneTrade implements ItemListing {
   private final TagKey<Item> inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemLike outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final int maxUses;
   private final int villagerXp;
   private float priceMultiplier;

   public TaggedItemForOneTrade(TagKey<Item> input, ItemLike output, int maxUses, int xp) {
      this(input, 1, output, 1, maxUses, xp);
   }

   public TaggedItemForOneTrade(TagKey<Item> input, int minInput, ItemLike output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, minInput, minInput, output, minOutput, maxOutput, maxUses, xp);
   }

   public TaggedItemForOneTrade(TagKey<Item> input, int minInput, int maxInput, ItemLike output, int outputCount, int maxUses, int xp) {
      this(input, minInput, maxInput, output, outputCount, outputCount, maxUses, xp);
   }

   public TaggedItemForOneTrade(TagKey<Item> input, int inputCount, ItemLike output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, xp);
   }

   public TaggedItemForOneTrade(TagKey<Item> input, int minInput, int maxInput, ItemLike output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public TaggedItemForOneTrade(
      TagKey<Item> input, Pair<Integer, Integer> inputCount, ItemLike output, Pair<Integer, Integer> outputCount, int maxUses, int xp, float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public TaggedItemForOneTrade setPriceMultiplier(float priceMultiplier) {
      this.priceMultiplier = priceMultiplier;
      return this;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      Optional<Named<Item>> optional = entity.registryAccess().registryOrThrow(Registries.ITEM).getTag(this.inputItem);
      Item input;
      if (optional.isEmpty()) {
         input = Items.AIR;
      } else {
         List<Holder<Item>> random = optional.get().stream().toList();
         input = (Item)random.get(randomSource.nextInt(random.size())).value();
      }

      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), input.getDefaultMaxStackSize(), randomSource
      );
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), this.outputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(
         new ItemCost(input, inputNumber),
         this.outputItem.asItem().getDefaultInstance().copyWithCount(outputNumber),
         this.maxUses,
         this.villagerXp,
         this.priceMultiplier
      );
   }
}
