package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class OneForEnchantedItemTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final ItemLike outputItem;
   private final TagKey<Enchantment> enchantable;
   private final int minEnchantCost;
   private final int maxEnchantCost;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForEnchantedItemTrade(ItemLike input, int minInput, int maxInput, ItemLike output, int level, int maxUses, int xp) {
      this(input, minInput, maxInput, output, 5, level, maxUses, xp);
   }

   public OneForEnchantedItemTrade(ItemLike input, int minInput, int maxInput, ItemLike output, int minLevel, int maxLevel, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, EnchantmentTags.ON_TRADED_EQUIPMENT, minLevel, maxLevel, maxUses, xp, 0.05F);
   }

   public OneForEnchantedItemTrade(ItemLike input, int minInput, int maxInput, ItemLike output, TagKey<Enchantment> enchantable, int level, int maxUses, int xp) {
      this(input, minInput, maxInput, output, enchantable, 5, level, maxUses, xp);
   }

   public OneForEnchantedItemTrade(
      ItemLike input, int minInput, int maxInput, ItemLike output, TagKey<Enchantment> enchantable, int minLevel, int maxLevel, int maxUses, int xp
   ) {
      this(input, Pair.of(minInput, maxInput), output, enchantable, minLevel, maxLevel, maxUses, xp, 0.05F);
   }

   public OneForEnchantedItemTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      ItemLike output,
      TagKey<Enchantment> enchantable,
      int minLevel,
      int maxLevel,
      int maxUses,
      int xp,
      float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.outputItem = output;
      this.enchantable = enchantable;
      this.minEnchantCost = minLevel;
      this.maxEnchantCost = maxLevel;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      RegistryAccess registryAccess = entity.level().registryAccess();
      Optional<Named<Enchantment>> optional = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getTag(this.enchantable);
      int enchantCost = randomSource.nextInt(this.minEnchantCost, this.maxEnchantCost + 1);
      ItemStack output = EnchantmentHelper.enchantItem(randomSource, new ItemStack(this.outputItem.asItem()), enchantCost, registryAccess, optional);
      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(new ItemCost(this.inputItem, inputNumber), output, this.maxUses, this.villagerXp, this.priceMultiplier);
   }
}
