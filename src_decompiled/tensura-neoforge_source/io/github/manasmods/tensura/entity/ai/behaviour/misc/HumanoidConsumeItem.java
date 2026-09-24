package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

public class HumanoidConsumeItem<E extends TensuraHumanoidEntity> extends CustomConsumeItem<E> {
   private int switchedSlot = -1;
   private ItemStack consumingStack = ItemStack.EMPTY;

   public HumanoidConsumeItem() {
      this.delayFor(entity -> 0);
      this.canConsumeFood((entity, stack) -> stack.is(TensuraItemTags.ARCANE_POTIONS) ? false : entity.isFood(stack));
      this.whenActivating(
         entity -> {
            if (!this.consumingStack.isEmpty()) {
               FoodProperties foodProperties = (FoodProperties)this.consumingStack.get(DataComponents.FOOD);
               if (foodProperties != null) {
                  entity.heal(Math.round(foodProperties.saturation() * 10.0F) / 10.0F);
                  this.spawnItemParticles(entity, this.consumingStack, 16);
                  entity.playSound(
                     this.consumingStack.getUseAnimation() == UseAnim.DRINK ? SoundEvents.GENERIC_DRINK : SoundEvents.GENERIC_EAT,
                     1.0F,
                     entity.getRandom().nextFloat() * 0.2F + 0.9F
                  );
                  this.consumingStack = ItemStack.EMPTY;
               }
            }
         }
      );
   }

   protected int getDelayTime(E entity) {
      if (this.consumingStack.isEmpty()) {
         boolean foundFood = false;
         int offID = entity.getSlotId(EquipmentSlot.OFFHAND);
         ItemStack offhand = entity.inventory.getItem(offID).copy();

         for (int slot = 0; slot < entity.inventory.getContainerSize(); slot++) {
            ItemStack stack = entity.inventory.getItem(slot);
            if (this.canConsumeFood.apply(entity, stack)) {
               entity.inventory.setItem(offID, stack.copy());
               entity.inventory.setItem(slot, offhand);
               entity.updateContainerEquipment();
               this.switchedSlot = slot;
               foundFood = true;
               break;
            }
         }

         if (!foundFood) {
            return this.delayTime.apply(entity);
         }

         entity.startUsingItem(InteractionHand.OFF_HAND);
         this.consumingStack = entity.inventory.getItem(offID).copy();
      }

      if (!this.consumingStack.isEmpty()) {
         FoodProperties foodProperties = (FoodProperties)this.consumingStack.get(DataComponents.FOOD);
         if (foodProperties != null) {
            return this.delayTime.apply(entity) + foodProperties.eatDurationTicks();
         }
      }

      return this.delayTime.apply(entity);
   }

   protected void doDelayedAction(E entity) {
      if (this.switchedSlot != -1) {
         int offID = entity.getSlotId(EquipmentSlot.OFFHAND);
         ItemStack offhand = entity.inventory.getItem(offID).copy();
         ItemStack stack = entity.inventory.getItem(this.switchedSlot);
         entity.inventory.setItem(offID, stack.copy());
         entity.inventory.setItem(this.switchedSlot, offhand);
         entity.updateContainerEquipment();
         this.switchedSlot = -1;
      }

      BrainUtils.setForgettableMemory(entity, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, this.consumeIntervalSupplier.apply(entity));
   }
}
