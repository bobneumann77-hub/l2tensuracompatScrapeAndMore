package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileWeaponItem.class)
public abstract class MixinProjectileWeaponItem {
   @Inject(
      method = "getHeldProjectile(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Lnet/minecraft/world/item/ItemStack;",
      at = @At("HEAD"),
      cancellable = true
   )
   private static void getHeldProjectile(LivingEntity livingEntity, Predicate<ItemStack> predicate, CallbackInfoReturnable<ItemStack> cir) {
      if (livingEntity.getOffhandItem().is(TensuraMobDropItems.UNICORN_HORN) && livingEntity.getMainHandItem().getItem() instanceof CrossbowItem) {
         cir.setReturnValue(livingEntity.getOffhandItem());
         cir.cancel();
      }
   }
}
