package io.github.manasmods.tensura.item.weapon.ranged;

import java.util.List;
import lombok.Generated;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SimpleBowItem extends BowItem {
   protected final int chargeTicks;
   protected final float inaccuracy;
   protected final double baseDamage;
   protected final int projectileRange;
   protected final float maxPower;

   public SimpleBowItem(Properties pProperties, int pRange, int pChargeTicks, double pBaseDamage, float pInaccuracy, float maxPower) {
      super(pProperties);
      this.projectileRange = pRange;
      this.baseDamage = pBaseDamage;
      this.inaccuracy = pInaccuracy;
      this.chargeTicks = pChargeTicks;
      this.maxPower = maxPower;
   }

   public SimpleBowItem(Properties pProperties, int pRange, int pChargeTicks, double pBaseDamage, float pInaccuracy) {
      this(pProperties, pRange, pChargeTicks, pBaseDamage, pInaccuracy, pChargeTicks / 20.0F);
   }

   public float getPowerForChargeTime(int i) {
      float f = i / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      float maxCharge = this.getChargeTicks() / this.getMaxPower();
      if (f > this.getChargeTicks() / maxCharge) {
         f = this.getChargeTicks() / maxCharge;
      }

      return f;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 3600 * this.getChargeTicks();
   }

   public int getDefaultProjectileRange() {
      return this.projectileRange;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int i) {
      if (entity instanceof Player player) {
         ItemStack itemStack2 = player.getProjectile(itemStack);
         if (!itemStack2.isEmpty()) {
            int j = this.getUseDuration(itemStack, entity) - i;
            float f = this.getPowerForChargeTime(j);
            if (f >= 0.1) {
               List<ItemStack> list = draw(itemStack, itemStack2, player);
               if (level instanceof ServerLevel serverLevel && !list.isEmpty()) {
                  this.shoot(
                     serverLevel, player, player.getUsedItemHand(), itemStack, list, f * 3.0F, this.inaccuracy, f == this.getChargeTicks() / 20.0F, null
                  );
               }

               level.playSound(
                  null,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  SoundEvents.ARROW_SHOOT,
                  SoundSource.PLAYERS,
                  1.0F,
                  1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
               );
               player.awardStat(Stats.ITEM_USED.get(this));
            }
         }
      }
   }

   @NotNull
   protected Projectile createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack ammoStack, boolean crit) {
      ArrowItem ammo;
      if (ammoStack.getItem() instanceof ArrowItem arrowItem) {
         ammo = arrowItem;
      } else {
         ammo = (ArrowItem)Items.ARROW;
      }

      AbstractArrow arrow = ammo.createArrow(level, ammoStack, livingEntity, itemStack);
      arrow.setBaseDamage(this.getBaseDamage());
      if (crit) {
         arrow.setCritArrow(true);
      }

      return arrow;
   }

   @Generated
   public int getChargeTicks() {
      return this.chargeTicks;
   }

   @Generated
   public float getInaccuracy() {
      return this.inaccuracy;
   }

   @Generated
   public double getBaseDamage() {
      return this.baseDamage;
   }

   @Generated
   public float getMaxPower() {
      return this.maxPower;
   }
}
