package io.github.manasmods.tensura.item.weapon;

import io.github.manasmods.tensura.entity.projectile.SpearProjectile;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class SimpleSpearItem extends TwoHandedSwordItem implements ProjectileItem {
   public SimpleSpearItem(Tier pTier, Properties properties) {
      super(pTier, 3, -2.6F, 2.0, -1.0, 0.0, 0.0, 2, -3.0F, 2.0, -1.0, 0.0, 0.0, properties);
      DispenserBlock.registerProjectileBehavior(this);
   }

   public SimpleSpearItem(
      Tier pTier,
      int pAttackDamageModifier,
      float pAttackSpeedModifier,
      double critChance,
      double critDamageMultiplier,
      int oneHandedAttackDamageModifier,
      float oneHandedAttackSpeedModifier,
      Properties pProperties
   ) {
      super(
         pTier,
         pAttackDamageModifier,
         pAttackSpeedModifier,
         2.0,
         -1.0,
         critChance,
         critDamageMultiplier,
         oneHandedAttackDamageModifier,
         oneHandedAttackSpeedModifier,
         2.0,
         -1.0,
         0.0,
         0.0,
         pProperties
      );
      DispenserBlock.registerProjectileBehavior(this);
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 36000;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
      if (livingEntity instanceof Player player) {
         int duration = this.getUseDuration(itemStack, livingEntity) - i;
         if (duration >= 10 && !isTooDamagedToUse(itemStack)) {
            Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(itemStack, EnchantmentEffectComponents.TRIDENT_SOUND)
               .orElse(SoundEvents.TRIDENT_THROW);
            if (!level.isClientSide) {
               itemStack.hurtAndBreak(10, player, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
               SpearProjectile spear = new SpearProjectile(level, player, itemStack, player.getUsedItemHand() == InteractionHand.MAIN_HAND);
               spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
               if (player.hasInfiniteMaterials()) {
                  spear.pickup = Pickup.CREATIVE_ONLY;
               }

               level.addFreshEntity(spear);
               level.playSound(null, spear, (SoundEvent)holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
               if (!player.hasInfiniteMaterials()) {
                  player.getInventory().removeItem(itemStack);
               }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (isTooDamagedToUse(itemStack)) {
         return InteractionResultHolder.fail(itemStack);
      }

      player.startUsingItem(interactionHand);
      return InteractionResultHolder.consume(itemStack);
   }

   private static boolean isTooDamagedToUse(ItemStack itemStack) {
      return itemStack.getDamageValue() >= itemStack.getMaxDamage() - 10;
   }

   public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
      SpearProjectile thrownTrident = new SpearProjectile(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1), null);
      thrownTrident.pickup = Pickup.ALLOWED;
      return thrownTrident;
   }
}
