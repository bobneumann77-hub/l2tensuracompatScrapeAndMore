package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class OneForMapTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final TagKey<Structure> destination;
   private final Holder<MapDecorationType> destinationType;
   private final String displayName;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForMapTrade(
      ItemLike input, int count, TagKey<Structure> destination, Holder<MapDecorationType> destinationType, String displayName, int maxUses, int xp
   ) {
      this(input, count, count, destination, destinationType, displayName, maxUses, xp);
   }

   public OneForMapTrade(
      ItemLike input, int min, int max, TagKey<Structure> destination, Holder<MapDecorationType> destinationType, String displayName, int maxUses, int xp
   ) {
      this(input, Pair.of(min, max), destination, destinationType, displayName, maxUses, xp);
   }

   public OneForMapTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      TagKey<Structure> destination,
      Holder<MapDecorationType> destinationType,
      String displayName,
      int maxUses,
      int xp
   ) {
      this(input, inputCount, destination, destinationType, displayName, maxUses, xp, 0.05F);
   }

   public OneForMapTrade(
      ItemLike input,
      Pair<Integer, Integer> inputCount,
      TagKey<Structure> destination,
      Holder<MapDecorationType> destinationType,
      String displayName,
      int maxUses,
      int xp,
      float priceMultiplier
   ) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.destination = destination;
      this.destinationType = destinationType;
      this.displayName = displayName;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      if (entity.level() instanceof ServerLevel level) {
         BlockPos blockPos = level.findNearestMapStructure(this.destination, entity.blockPosition(), 100, true);
         if (blockPos == null) {
            return null;
         }

         ItemStack itemStack = MapItem.create(level, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
         MapItem.renderBiomePreviewMap(level, itemStack);
         MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", this.destinationType);
         itemStack.set(DataComponents.ITEM_NAME, Component.translatable(this.displayName));
         int inputNumber = TensuraTradeHelper.getClampedStackSize(
            (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
         );
         return new MerchantOffer(new ItemCost(this.inputItem, inputNumber), itemStack, this.maxUses, this.villagerXp, this.priceMultiplier);
      } else {
         return null;
      }
   }
}
