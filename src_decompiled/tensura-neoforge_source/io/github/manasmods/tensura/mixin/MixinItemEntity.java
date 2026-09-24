package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
   @WrapOperation(
      method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canBeHurtBy(Lnet/minecraft/world/damagesource/DamageSource;)Z")
   )
   private boolean canBeHurtBy(ItemStack instance, DamageSource damageSource, Operation<Boolean> original) {
      if (!(Boolean)original.call(new Object[]{instance, damageSource})) {
         return false;
      }

      ItemEntity entity = (ItemEntity)this;
      return damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         ? true
         : TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.STURDY, instance) <= 0;
   }
}
