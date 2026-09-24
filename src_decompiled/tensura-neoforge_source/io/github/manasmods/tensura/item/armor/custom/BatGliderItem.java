package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class BatGliderItem extends Item implements Equipable {
   public BatGliderItem() {
      super(new Properties().arch$tab(TensuraCreativeTabs.ARMOR).durability(500).attributes(createWingedAttributes()));
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static ItemAttributeModifiers createWingedAttributes() {
      return ItemAttributeModifiers.builder()
         .add(
            ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER,
            new AttributeModifier(ResourceLocation.fromNamespaceAndPath("tensura", "bat_glider"), 0.5, Operation.ADD_VALUE),
            EquipmentSlotGroup.CHEST
         )
         .build();
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
      if (slot == 38) {
         if (entity instanceof Player player && player.isFallFlying()) {
            if (ElytraItem.isFlyEnabled(itemStack)) {
               int nextRoll = player.getFallFlyingTicks() + 1;
               if (!entity.level().isClientSide() && nextRoll % 10 == 0) {
                  if (nextRoll / 10 % 2 == 0) {
                     itemStack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
                  }

                  entity.gameEvent(GameEvent.ELYTRA_GLIDE);
               }
            } else {
               player.stopFallFlying();
            }
         }
      }
   }

   public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
      return itemStack2.is((Item)TensuraMobDropItems.GIANT_BAT_WING.get());
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      return this.swapWithEquipmentSlot(this, level, player, interactionHand);
   }

   public Holder<SoundEvent> getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_ELYTRA;
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.CHEST;
   }
}
