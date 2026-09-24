package io.github.manasmods.tensura.item.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class SimpleShieldItem extends Item implements Equipable {
   public SimpleShieldItem(Properties pProperties) {
      super(pProperties);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack stack = pPlayer.getItemInHand(pHand);
      pPlayer.startUsingItem(pHand);
      return InteractionResultHolder.consume(stack);
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.OFFHAND;
   }
}
