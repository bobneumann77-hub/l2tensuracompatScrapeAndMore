package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class OneForEnchantedBookTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemLike outputItem;
   private final TagKey<Enchantment> enchantable;
   private final int minLevel;
   private final int maxLevel;
   private final int numberOfEnchant;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForEnchantedBookTrade(
      ItemLike input, int minInput, int maxInput, TagKey<Enchantment> enchantable, int max, int numberOfEnchant, int maxUses, int xp
   ) {
      this(input, minInput, maxInput, enchantable, 1, max, numberOfEnchant, maxUses, xp);
   }

   public OneForEnchantedBookTrade(
      ItemLike input, int minInput, int maxInput, TagKey<Enchantment> enchantable, int min, int max, int numberOfEnchant, int maxUses, int xp
   ) {
      this(input, Pair.of(minInput, maxInput), Items.ENCHANTED_BOOK, enchantable, min, max, numberOfEnchant, maxUses, xp, 0.05F);
   }

   public OneForEnchantedBookTrade(
      ItemLike input, int minInput, int maxInput, ItemLike output, TagKey<Enchantment> enchantable, int max, int numberOfEnchant, int maxUses, int xp
   ) {
      this(input, minInput, maxInput, output, enchantable, 1, max, numberOfEnchant, maxUses, xp);
   }

   public OneForEnchantedBookTrade(
      ItemLike input, int minInput, int maxInput, ItemLike output, TagKey<Enchantment> enchantable, int min, int max, int numberOfEnchant, int maxUses, int xp
   ) {
      this(input, Pair.of(minInput, maxInput), output, enchantable, min, max, numberOfEnchant, maxUses, xp, 0.05F);
   }

   public OneForEnchantedBookTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      ItemLike output,
      TagKey<Enchantment> enchantable,
      int min,
      int max,
      int numberOfEnchant,
      int maxUses,
      int xp,
      float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.enchantable = enchantable;
      this.minLevel = min;
      this.maxLevel = max;
      this.numberOfEnchant = numberOfEnchant;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      ItemStack book = this.outputItem.asItem().getDefaultInstance();

      for (int i = 0; i < this.numberOfEnchant; i++) {
         Optional<Holder<Enchantment>> optional = entity.level()
            .registryAccess()
            .registryOrThrow(Registries.ENCHANTMENT)
            .getRandomElementOf(this.enchantable, randomSource);
         if (optional.isPresent()) {
            Holder<Enchantment> holder = optional.get();
            Enchantment enchantment = (Enchantment)holder.value();
            int minLevel = Math.max(enchantment.getMinLevel(), this.minLevel);
            int maxLevel = Math.min(enchantment.getMaxLevel(), this.maxLevel);
            int level = minLevel >= maxLevel ? maxLevel : Mth.nextInt(randomSource, minLevel, maxLevel);
            book.enchant(holder, level);
         }
      }

      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(new ItemCost(this.inputItem, inputNumber), book, this.maxUses, this.villagerXp, this.priceMultiplier);
   }
}
