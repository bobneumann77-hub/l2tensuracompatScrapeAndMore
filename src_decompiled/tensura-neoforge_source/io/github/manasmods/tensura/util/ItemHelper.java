package io.github.manasmods.tensura.util;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemHelper {
   public static void breakItem(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
      Item item = stack.getItem();
      stack.shrink(1);
      entity.onEquippedItemBroken(item, slot);
   }

   public static ItemEntity dropItem(Entity entity, RandomSource random, ItemStack stack, int pickUpDelay, float throwForce) {
      double d0 = entity.getEyeY() - 0.3;
      ItemEntity item = new ItemEntity(entity.level(), entity.getX(), d0, entity.getZ(), stack);
      item.setPickUpDelay(pickUpDelay);
      float f8 = Mth.sin(entity.getXRot() * (float) (Math.PI / 180.0));
      float f2 = Mth.cos(entity.getXRot() * (float) (Math.PI / 180.0));
      float f3 = Mth.sin(entity.getYRot() * (float) (Math.PI / 180.0));
      float f4 = Mth.cos(entity.getYRot() * (float) (Math.PI / 180.0));
      float f5 = random.nextFloat() * (float) (Math.PI * 2);
      float f6 = 0.02F * random.nextFloat();
      Vec3 throwVec = new Vec3(
         -f3 * f2 * 0.3F + Math.cos(f5) * f6, -f8 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, f4 * f2 * 0.3F + Math.sin(f5) * f6
      );
      item.setDeltaMovement(throwVec.scale(throwForce));
      entity.level().addFreshEntity(item);
      return item;
   }
}
