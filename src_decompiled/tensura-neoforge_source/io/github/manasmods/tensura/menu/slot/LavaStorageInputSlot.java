package io.github.manasmods.tensura.menu.slot;

import io.github.manasmods.tensura.menu.SpatialStorageMenu;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
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

public class LavaStorageInputSlot extends Slot {
   private final SpatialStorageMenu menu;

   public LavaStorageInputSlot(SpatialStorageMenu menu, Container container, int xPosition, int yPosition) {
      super(container, 1, xPosition, yPosition);
      this.menu = menu;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack pStack) {
      if (pStack.get(DataComponents.POTION_CONTENTS) != null && ((PotionContents)pStack.get(DataComponents.POTION_CONTENTS)).hasEffects()) {
         return false;
      } else {
         ItemStack output = LavaStorageInputSlot.LavaStorage.getOutputStack(pStack);
         if (output.isEmpty()) {
            return false;
         } else {
            ItemStack currentOutput = this.menu.lavaStorageOutput.getItem(0);
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

      for (LavaStorageInputSlot.LavaStorage lava : LavaStorageInputSlot.LavaStorage.values()) {
         if (lava.getInput().equals(pStack.getItem())) {
            IAbility ability = TensuraStorages.getAbilityFrom(this.menu.getStorageOwner());
            double point = ability.getLavaPoint() + lava.getLavaPoint();
            if (point < 0.0 || ability.getLavaPoint() >= this.menu.getPlayer().getAttributeValue(TensuraAttributes.LAVA_CAPACITY) && lava.getLavaPoint() > 0.0) {
               return;
            }

            ability.setLavaPoint(point);
            ability.markDirty();
            this.menu.getPlayer().playSound(this.soundEvent(lava.getLavaPoint()));
            ItemStack currentOutput = this.menu.lavaStorageOutput.getItem(0);
            if (currentOutput.isEmpty()) {
               this.menu.lavaStorageOutput.setItem(0, lava.getOutput().getDefaultInstance());
            } else {
               ItemStack newOutput = currentOutput.copy();
               newOutput.grow(1);
               this.menu.lavaStorageOutput.setItem(0, newOutput);
            }

            this.menu.lavaStorageOutput.setChanged();
            this.container.setItem(1, ItemStack.EMPTY);
            this.container.setChanged();
         }
      }
   }

   private SoundEvent soundEvent(double point) {
      return point > 0.0 ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_FILL_LAVA;
   }

   public enum LavaStorage {
      MAGMA_BLOCK(Items.MAGMA_BLOCK, 1.0, Items.COBBLESTONE),
      LAVE_BUCKET(Items.LAVA_BUCKET, 3.0, Items.BUCKET),
      BUCKET(Items.BUCKET, -3.0, Items.LAVA_BUCKET);

      private final Item input;
      private final double lavaPoint;
      private final Item output;

      public static ItemStack getOutputStack(ItemStack input) {
         Optional<Item> output = Arrays.stream(values())
            .filter(lavaStorage -> lavaStorage.getInput().equals(input.getItem()))
            .map(LavaStorageInputSlot.LavaStorage::getOutput)
            .findFirst();
         return output.<ItemStack>map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
      }

      @Generated
      public Item getInput() {
         return this.input;
      }

      @Generated
      public double getLavaPoint() {
         return this.lavaPoint;
      }

      @Generated
      public Item getOutput() {
         return this.output;
      }

      @Generated
      LavaStorage(final Item input, final double lavaPoint, final Item output) {
         this.input = input;
         this.lavaPoint = lavaPoint;
         this.output = output;
      }
   }
}
