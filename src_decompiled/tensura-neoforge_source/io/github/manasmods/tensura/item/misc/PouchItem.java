package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.BundleContents.Mutable;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

public class PouchItem extends Item {
   private final int maxSize;

   public PouchItem(int maxSize, Properties properties) {
      super(properties);
      this.maxSize = maxSize;
   }

   public PouchItem(int maxSize) {
      this(maxSize, new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).stacksTo(1));
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   @NotNull
   public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
      return !itemStack.has(DataComponents.HIDE_TOOLTIP) && !itemStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
         ? Optional.ofNullable((BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
         : Optional.empty();
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      BundleContents bundleContents = (BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents != null) {
         int i = Mth.mulAndTruncate(bundleContents.weight(), 100);
         list.add(Component.translatable("item.minecraft.bundle.fullness", new Object[]{i, this.getMaxSize() * 100}).withStyle(ChatFormatting.GRAY));
      }
   }

   public boolean isBarVisible(ItemStack itemStack) {
      BundleContents bundleContents = (BundleContents)itemStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return bundleContents.weight().compareTo(Fraction.ZERO) > 0;
   }

   public int getBarWidth(ItemStack itemStack) {
      BundleContents bundleContents = (BundleContents)itemStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      float i = (float)Mth.mulAndTruncate(bundleContents.weight(), 1) / this.getMaxSize();
      return Math.min(1 + (int)(i * 12.0F), 13);
   }

   public int getBarColor(ItemStack itemStack) {
      return 16766720;
   }

   public void onDestroyed(ItemEntity itemEntity) {
      BundleContents bundleContents = (BundleContents)itemEntity.getItem().get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents != null) {
         itemEntity.getItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         ItemUtils.onContainerDestroyed(itemEntity, bundleContents.itemsCopy());
      }
   }

   public boolean overrideStackedOnOther(ItemStack pouch, Slot slot, ClickAction clickAction, Player player) {
      if (clickAction != ClickAction.SECONDARY) {
         return false;
      }

      if (pouch.getCount() > 1) {
         return false;
      }

      BundleContents bundleContents = (BundleContents)pouch.get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents == null) {
         return false;
      }

      ItemStack coin = slot.getItem();
      Mutable mutable = new Mutable(bundleContents);
      if (coin.isEmpty()) {
         player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
         ItemStack toRemove = mutable.removeOne();
         if (toRemove != null) {
            ItemStack toInsert = slot.safeInsert(toRemove);
            mutable.tryInsert(toInsert);
         }
      } else if (coin.getItem().canFitInsideContainerItems() && coin.is(TensuraItemTags.COINS)) {
         int i = this.tryTransfer(mutable, slot, player, this.getMaxSize());
         if (i > 0) {
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
         }
      }

      pouch.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
      return true;
   }

   public boolean overrideOtherStackedOnMe(ItemStack pouch, ItemStack coin, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
      if (pouch.getCount() > 1) {
         return false;
      }

      if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
         BundleContents bundleContents = (BundleContents)pouch.get(DataComponents.BUNDLE_CONTENTS);
         if (bundleContents == null) {
            return false;
         }

         Mutable mutable = new Mutable(bundleContents);
         if (coin.isEmpty()) {
            ItemStack toRemove = mutable.removeOne();
            if (toRemove != null) {
               player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
               slotAccess.set(toRemove);
            }
         } else if (coin.is(TensuraItemTags.COINS)) {
            int i = this.tryInsert(mutable, coin, this.getMaxSize());
            if (i > 0) {
               player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            }
         }

         pouch.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
         return true;
      } else {
         return false;
      }
   }

   public boolean onPickUpCoin(Player player, ItemStack pouch, ItemEntity entity, ItemStack coin) {
      if (pouch.getCount() > 1) {
         return false;
      }

      BundleContents bundleContents = (BundleContents)pouch.get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents == null) {
         return false;
      }

      Mutable mutable = new Mutable(bundleContents);
      int coinStack = coin.getCount();
      int i = this.tryInsert(mutable, coin, this.getMaxSize());
      if (i > 0) {
         player.take(entity, i);
         player.awardStat(Stats.ITEM_PICKED_UP.get(coin.getItem()), i);
         player.onItemPickup(entity);
      }

      pouch.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
      return i >= coinStack;
   }

   private int tryInsert(Mutable mutable, ItemStack itemStack, int slots) {
      if (!itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems()) {
         int i = Math.min(itemStack.getCount(), this.getMaxAmountToAdd(mutable, itemStack, slots));
         if (i == 0) {
            return 0;
         }

         mutable.weight = mutable.weight.add(this.getBundleWeight(itemStack).multiplyBy(Fraction.getFraction(i, 1)));
         ItemStack toAdd = itemStack.split(i);

         while (!toAdd.isEmpty()) {
            int j = this.findStackIndex(mutable, toAdd);
            if (j != -1) {
               ItemStack toRemove = (ItemStack)mutable.items.remove(j);
               int missing = Math.min(toRemove.getMaxStackSize() - toRemove.getCount(), toAdd.getCount());
               ItemStack copy = toRemove.copyWithCount(toRemove.getCount() + missing);
               toAdd.shrink(missing);
               mutable.items.addFirst(copy);
            } else {
               mutable.items.addFirst(toAdd.split(toAdd.getMaxStackSize()));
            }
         }

         return i;
      } else {
         return 0;
      }
   }

   private int tryTransfer(Mutable mutable, Slot slot, Player player, int slots) {
      ItemStack itemStack = slot.getItem();
      int i = this.getMaxAmountToAdd(mutable, itemStack, slots);
      return this.tryInsert(mutable, slot.safeTake(itemStack.getCount(), i, player), slots);
   }

   private int findStackIndex(Mutable mutable, ItemStack itemStack) {
      if (!itemStack.isStackable()) {
         return -1;
      }

      for (int i = 0; i < mutable.items.size(); i++) {
         ItemStack stack = (ItemStack)mutable.items.get(i);
         if (stack.getCount() < stack.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, itemStack)) {
            return i;
         }
      }

      return -1;
   }

   private int getMaxAmountToAdd(Mutable mutable, ItemStack itemStack, int slots) {
      Fraction fraction = Fraction.getFraction(slots, 1).subtract(mutable.weight);
      return Math.max(fraction.divideBy(this.getBundleWeight(itemStack)).intValue(), 0);
   }

   private Fraction getBundleWeight(ItemStack itemStack) {
      BundleContents bundleContents = (BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS);
      return bundleContents != null ? Fraction.getFraction(1, 16).add(bundleContents.weight()) : Fraction.getFraction(1, itemStack.getMaxStackSize());
   }

   private static boolean dropContents(ItemStack itemStack, Player player) {
      BundleContents bundleContents = (BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents != null && !bundleContents.isEmpty()) {
         itemStack.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         if (player instanceof ServerPlayer) {
            bundleContents.itemsCopy().forEach(stack -> player.drop(stack, true));
         }

         return true;
      } else {
         return false;
      }
   }
}
