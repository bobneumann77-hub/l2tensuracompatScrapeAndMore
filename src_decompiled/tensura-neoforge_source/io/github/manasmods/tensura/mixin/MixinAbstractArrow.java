package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class MixinAbstractArrow {
   @Inject(
      method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         ordinal = 0,
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )
   )
   private void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) int damage, @Local DamageSource source) {
      AbstractArrow arrow = (AbstractArrow)this;
      if (!arrow.level().isClientSide() && arrow.getWeaponItem() != null) {
         if (entityHitResult.getEntity().getType() != EntityType.ENDERMAN) {
            if (arrow.getOwner() instanceof LivingEntity owner) {
               if (SkillUtils.shouldCancelInteraction(owner)) {
                  return;
               }

               TensuraEnchantmentHelper.doAdditionalAfterAttack(
                  (ServerLevel)arrow.level(), entityHitResult.getEntity(), owner, source, arrow.getWeaponItem(), damage
               );
            }
         }
      }
   }

   @Inject(
      method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         ordinal = 0,
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostAttackEffectsWithItemSource(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/item/ItemStack;)V"
      )
   )
   private void doPostAttackEffectsWithItemSource(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) int damage, @Local DamageSource source) {
      AbstractArrow arrow = (AbstractArrow)this;
      if (!arrow.level().isClientSide() && arrow.getWeaponItem() != null) {
         if (arrow.getOwner() instanceof LivingEntity owner) {
            TensuraEnchantmentHelper.doAdditionalAfterDamage(
               (ServerLevel)arrow.level(), entityHitResult.getEntity(), owner, source, arrow.getWeaponItem(), damage
            );
         }
      }
   }
}
