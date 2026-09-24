package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.tensura.entity.projectile.InvisibleArrow;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

public class InvisibleArrowItem extends ArrowItem {
   public InvisibleArrowItem(Properties pProperties) {
      super(pProperties);
      DispenserBlock.registerProjectileBehavior(this);
   }

   public AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity livingEntity, @Nullable ItemStack itemStack2) {
      return new InvisibleArrow(level, livingEntity, itemStack.copyWithCount(1), itemStack2);
   }

   public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
      InvisibleArrow arrow = new InvisibleArrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1), null);
      arrow.pickup = Pickup.ALLOWED;
      return arrow;
   }
}
