package io.github.manasmods.tensura.menu.slot;

import io.github.manasmods.tensura.menu.SpatialStorageMenu;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import java.util.Arrays;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public class WaterStorageInputSlot extends Slot {
   private final SpatialStorageMenu menu;

   public WaterStorageInputSlot(SpatialStorageMenu menu, Container container, int xPosition, int yPosition) {
      super(container, 0, xPosition, yPosition);
      this.menu = menu;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack pStack) {
      if (pStack.get(DataComponents.POTION_CONTENTS) != null && ((PotionContents)pStack.get(DataComponents.POTION_CONTENTS)).hasEffects()) {
         return false;
      } else {
         ItemStack output = WaterStorageInputSlot.WaterStorage.getOutputStack(pStack);
         if (output.isEmpty()) {
            return false;
         } else {
            ItemStack currentOutput = this.menu.waterStorageOutput.getItem(0);
            if (currentOutput.isEmpty()) {
               return true;
            } else {
               return currentOutput.getCount() >= currentOutput.getMaxStackSize() ? false : ItemStack.isSameItemSameComponents(output, currentOutput);
            }
         }
      }
   }

   public void set(ItemStack pStack) {
      super.set(pStack);

      for (WaterStorageInputSlot.WaterStorage water : WaterStorageInputSlot.WaterStorage.values()) {
         if (water.getInput().equals(pStack.getItem())) {
            IAbility ability = TensuraStorages.getAbilityFrom(this.menu.getStorageOwner());
            double point = ability.getWaterPoint() + water.getWaterPoint();
            if (point < 0.0
               || ability.getWaterPoint() >= this.menu.getPlayer().getAttributeValue(TensuraAttributes.WATER_CAPACITY) && water.getWaterPoint() > 0.0) {
               return;
            }

            ability.setWaterPoint(point);
            ability.markDirty();
            this.menu.getPlayer().playSound(this.soundEvent(water.getWaterPoint()));
            ItemStack currentOutput = this.menu.waterStorageOutput.getItem(0);
            if (currentOutput.isEmpty()) {
               this.menu.waterStorageOutput.setItem(0, water.getOutput().getDefaultInstance());
            } else {
               ItemStack newOutput = currentOutput.copy();
               newOutput.grow(1);
               this.menu.waterStorageOutput.setItem(0, newOutput);
            }

            this.menu.waterStorageOutput.setChanged();
            this.container.setItem(0, ItemStack.EMPTY);
            this.container.setChanged();
         }
      }
   }

   private SoundEvent soundEvent(double point) {
      return point > 0.0 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BUCKET_FILL;
   }

   public enum WaterStorage {
      WET_SPONGE(Items.WET_SPONGE, 3.0, Items.SPONGE),
      WATER_BUCKET(Items.WATER_BUCKET, 3.0, Items.BUCKET),
      MAGIC_WATER((Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(), 1.0, (Item)TensuraConsumableItems.MAGIC_BOTTLE.get()),
      VACUUMED_MAGIC_WATER((Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get(), 1.0, (Item)TensuraConsumableItems.MAGIC_BOTTLE.get()),
      WATER_BOTTLE(Items.POTION, 1.0, Items.GLASS_BOTTLE),
      SPLASH_BOTTLE(Items.SPLASH_POTION, 1.0, Items.GLASS_BOTTLE),
      LINGERING_BOTTLE(Items.LINGERING_POTION, 1.0, Items.GLASS_BOTTLE),
      SPONGE(Items.SPONGE, -18.0, Items.WET_SPONGE),
      BUCKET(Items.BUCKET, -3.0, Items.WATER_BUCKET),
      BOTTLE(Items.GLASS_BOTTLE, -1.0, Items.POTION);

      private final Item input;
      private final double waterPoint;
      private final Item output;

      public static ItemStack getOutputStack(ItemStack input) {
         Optional<Item> output = Arrays.stream(values())
            .filter(waterStorage -> waterStorage.getInput().equals(input.getItem()))
            .map(WaterStorageInputSlot.WaterStorage::getOutput)
            .findFirst();
         return output.<ItemStack>map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
      }

      @Generated
      public Item getInput() {
         return this.input;
      }

      @Generated
      public double getWaterPoint() {
         return this.waterPoint;
      }

      @Generated
      public Item getOutput() {
         return this.output;
      }

      @Generated
      WaterStorage(final Item input, final double waterPoint, final Item output) {
         this.input = input;
         this.waterPoint = waterPoint;
         this.output = output;
      }
   }
}
