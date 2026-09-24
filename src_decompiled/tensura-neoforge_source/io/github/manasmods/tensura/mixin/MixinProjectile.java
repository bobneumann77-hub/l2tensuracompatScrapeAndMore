package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class MixinProjectile {
   @Inject(method = "shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V", at = @At("HEAD"), cancellable = true)
   public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy, CallbackInfo ci) {
      Projectile projectile = (Projectile)this;
      if (TensuraFlyingProjectile.applyWarpShot(projectile, pShooter, pVelocity)) {
         ci.cancel();
      }
   }

   @Inject(method = "shoot(DDDFF)V", at = @At("HEAD"), cancellable = true)
   public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy, CallbackInfo ci) {
      Projectile projectile = (Projectile)this;
      if (TensuraFlyingProjectile.applyWarpShot(projectile, projectile.getOwner(), pVelocity)) {
         ci.cancel();
      }
   }
}
