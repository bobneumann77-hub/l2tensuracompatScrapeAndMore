package io.github.manasmods.tensura.entity.merchant.trade;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.item.misc.MagicTomeItem;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

public class OneForMagicTomeTrade implements ItemListing {
   private final ItemLike inputItem;
   private final Pair<Integer, Integer> inputCount;
   private final TagKey<ManasSkill> magicGroup;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public OneForMagicTomeTrade(ItemLike input, TagKey<ManasSkill> output, int maxUses, int xp) {
      this(input, 1, output, maxUses, xp);
   }

   public OneForMagicTomeTrade(ItemLike input, int inputCount, TagKey<ManasSkill> output, int maxUses, int xp) {
      this(input, inputCount, inputCount, output, maxUses, xp);
   }

   public OneForMagicTomeTrade(ItemLike input, int minInput, int maxInput, TagKey<ManasSkill> output, int maxUses, int xp) {
      this(input, Pair.of(minInput, maxInput), output, maxUses, xp, 0.05F);
   }

   public OneForMagicTomeTrade(ItemLike input, Pair<Integer, Integer> inputCount, TagKey<ManasSkill> output, int maxUses, int xp, float priceMultiplier) {
      this.inputItem = input;
      this.inputCount = inputCount;
      this.magicGroup = output;
      this.maxUses = maxUses;
      this.villagerXp = xp;
      this.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
      Optional<Named<ManasSkill>> optional = entity.registryAccess().registryOrThrow(SkillAPI.getSkillRegistry().key()).getTag(this.magicGroup);
      ItemStack output;
      if (optional.isEmpty()) {
         output = ((Item)TensuraMaterialItems.MAGIC_TOME.get()).getDefaultInstance();
      } else {
         List<Holder<ManasSkill>> random = optional.get().stream().toList();
         output = MagicTomeItem.createForMagic((ManasSkill)random.get(randomSource.nextInt(random.size())).value());
      }

      int inputNumber = TensuraTradeHelper.getClampedStackSize(
         (Integer)this.inputCount.getFirst(), (Integer)this.inputCount.getSecond(), this.inputItem.asItem().getDefaultMaxStackSize(), randomSource
      );
      return new MerchantOffer(new ItemCost(this.inputItem, inputNumber), output, this.maxUses, this.villagerXp, this.priceMultiplier);
   }
}
