package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.data.TensuraTags;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class OneForPotionTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemLike outputItem;
   private final Pair<Integer, Integer> outputCount;
   private final OneForPotionTrade.PotionType type;
   private final TagKey<Potion> tradeable;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForPotionTrade(ItemLike input, int minInput, int maxInput, ItemLike output, int minOutput, OneForPotionTrade.PotionType type, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, minOutput), type, TensuraTags.Potions.DWARF_ALCHEMIST, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input, int minInput, ItemLike output, int minOutput, OneForPotionTrade.PotionType type, TagKey<Potion> tradeable, int maxUses, int xp
   ) {
      this(input, Pair.of(minInput, minInput), output, Pair.of(minOutput, minOutput), type, tradeable, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input,
      int minInput,
      int maxInput,
      ItemLike output,
      int minOutput,
      OneForPotionTrade.PotionType type,
      TagKey<Potion> tradeable,
      int maxUses,
      int xp
   ) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, minOutput), type, tradeable, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input,
      int minInput,
      ItemLike output,
      int minOutput,
      int maxOutput,
      OneForPotionTrade.PotionType type,
      TagKey<Potion> tradeable,
      int maxUses,
      int xp
   ) {
      this(input, Pair.of(minInput, minInput), output, Pair.of(minOutput, maxOutput), type, tradeable, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input,
      int minInput,
      int maxInput,
      ItemLike output,
      int minOutput,
      int maxOutput,
      OneForPotionTrade.PotionType type,
      TagKey<Potion> tradeable,
      int maxUses,
      int xp
   ) {
      this(input, Pair.of(minInput, maxInput), output, Pair.of(minOutput, maxOutput), type, tradeable, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      ItemLike output,
      Pair<Integer, Integer> outputCount,
      OneForPotionTrade.PotionType type,
      TagKey<Potion> tradeable,
      int maxUses,
      int xp
   ) {
      this(input, inputCount, output, outputCount, type, tradeable, maxUses, xp, 0.05F);
   }

   public OneForPotionTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      ItemLike output,
      Pair<Integer, Integer> outputCount,
      OneForPotionTrade.PotionType type,
      TagKey<Potion> tradeable,
      int maxUses,
      int xp,
      float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.outputCount = outputCount;
      this.type = type;
      this.tradeable = tradeable;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      List<Reference<Potion>> list = BuiltInRegistries.POTION
         .holders()
         .filter(
            reference -> !((Potion)reference.value()).getEffects().isEmpty()
               && reference.is(this.tradeable)
               && OneForPotionTrade.PotionType.check((Reference<Potion>)reference, this.type, entity.level())
         )
         .collect(Collectors.toList());
      Holder<Potion> holder = (Holder<Potion>)Util.getRandom(list, randomSource);
      int outputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.outputCount.getFirst(), (Integer)this.outputCount.getSecond(), this.outputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      ItemStack potion = new ItemStack(this.outputItem, outputNumber);
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(new ItemCost(this.inputItem, inputNumber), potion, this.maxUses, this.villagerXp, this.priceMultiplier);
   }

   public enum PotionType {
      ANY,
      NORMAL,
      LONG,
      STRONG;

      static boolean check(Reference<Potion> potion, OneForPotionTrade.PotionType type, Level level) {
         if (!level.potionBrewing().isBrewablePotion(potion)) {
            return false;
         }

         return switch (type) {
            case ANY -> true;
            case NORMAL -> !potion.key().location().getPath().startsWith("long_") && !potion.key().location().getPath().startsWith("strong_");
            case LONG -> potion.key().location().getPath().startsWith("long_");
            case STRONG -> potion.key().location().getPath().startsWith("strong_");
         };
      }
   }
}
