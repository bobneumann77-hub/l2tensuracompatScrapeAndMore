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

public class OneForTaggedItemTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final TagKey<Item> outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForTaggedItemTrade(ItemLike input, TagKey<Item> output, int maxUses, int xp) {
      this(input, 1, output, 1, maxUses, xp);
   }

   public OneForTaggedItemTrade(ItemLike input, int minInput, TagKey<Item> output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, minInput, minInput, output, minOutput, maxOutput, maxUses, xp);
   }

   public OneForTaggedItemTrade(ItemLike input, int minInput, int maxInput, TagKey<Item> output, int outputCount, int maxUses, int xp) {
      this(input, minInput, maxInput, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForTaggedItemTrade(ItemLike input, int inputCount, TagKey<Item> output, int outputCount, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, xp);
   }

   public OneForTaggedItemTrade(ItemLike input, int minInput, int maxInput, TagKey<Item> output, int minOutput, int maxOutput, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, maxOutput), maxUses, xp, 0.05F);
   }

   public OneForTaggedItemTrade(
      ItemLike input, Pair<Integer, Integer> inputCount, TagKey<Item> output, Pair<Integer, Integer> outputCount, int maxUses, int xp, float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      Optional<Named<Item>> optional = entity.registryAccess().registryOrThrow(Registries.ITEM).getTag(this.outputItem);
      Item output;
      if (optional.isEmpty()) {
         output = Items.AIR;
      } else {
         List<Holder<Item>> random = optional.get().stream().toList();
         output = (Item)random.get(randomSource.nextInt(random.size())).value();
      }

      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), output.getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(
         new ItemCost(this.inputItem, inputNumber),
         output.getDefaultInstance().copyWithCount(outputNumber),
         this.maxUses,
         this.villagerXp,
         this.priceMultiplier
      );
   }
}
