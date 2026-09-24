package io.github.manasmods.tensura.item.weapon.ranged;

import lombok.Generated;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SimpleCrossbowItem extends CrossbowItem {
   protected final int chargeTicks;
   protected final float inaccuracy;
   protected final float arrowPower;
   protected final float fireworkPower;

   public SimpleCrossbowItem(Properties pProperties, int pChargeTicks, float arrowPower, float fireworkPower, float pInaccuracy) {
      super(pProperties);
      this.arrowPower = arrowPower;
      this.fireworkPower = fireworkPower;
      this.inaccuracy = pInaccuracy;
      this.chargeTicks = pChargeTicks;
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.CROSSBOW;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return getChargeDuration(itemStack, livingEntity, this.getChargeTicks()) + 3;
   }

   public static int getChargeDuration(ItemStack itemStack, LivingEntity livingEntity, int chargeTick) {
      float f = EnchantmentHelper.modifyCrossbowChargingTime(itemStack, livingEntity, chargeTick / 20.0F);
      return Mth.floor(f * 20.0F);
   }

   public float getShootingPower(ChargedProjectiles chargedProjectiles) {
      return chargedProjectiles.contains(Items.FIREWORK_ROCKET) ? this.fireworkPower : this.arrowPower;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.get(DataComponents.CHARGED_PROJECTILES);
      if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
         this.performShooting(level, player, interactionHand, itemStack, this.getShootingPower(chargedProjectiles), this.inaccuracy, null);
         return InteractionResultHolder.consume(itemStack);
      } else if (!player.getProjectile(itemStack).isEmpty()) {
         this.startSoundPlayed = false;
         this.midLoadSoundPlayed = false;
         player.startUsingItem(interactionHand);
         return InteractionResultHolder.consume(itemStack);
      } else {
         return InteractionResultHolder.fail(itemStack);
      }
   }

   public static boolean containsChargedProjectile(ItemStack stack, Item ammo) {
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)stack.get(DataComponents.CHARGED_PROJECTILES);
      return chargedProjectiles != null && chargedProjectiles.contains(ammo);
   }

   @Generated
   public int getChargeTicks() {
      return this.chargeTicks;
   }

   @Generated
   public float getInaccuracy() {
      return this.inaccuracy;
   }
}
