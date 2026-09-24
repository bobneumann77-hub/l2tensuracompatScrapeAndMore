package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.Objects;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
   @Inject(method = "getOverlayCoords(Lnet/minecraft/world/entity/LivingEntity;F)I", at = @At("HEAD"), cancellable = true)
   private static void getOverlayCoords(LivingEntity entity, float pU, CallbackInfoReturnable<Integer> cir) {
      if (SkillUtils.shouldCancelPain(entity)) {
         cir.setReturnValue(OverlayTexture.pack(OverlayTexture.u(pU), OverlayTexture.v(entity.deathTime > 0)));
      }
   }

   @ModifyReturnValue(method = "isEntityUpsideDown(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
   private static boolean isEntityUpsideDown(boolean original, LivingEntity livingEntity) {
      if (original) {
         return true;
      } else if (!TensuraClient.CONFIG.dinnerboneNameFlip) {
         return false;
      } else {
         return Objects.equals(TensuraStorages.getExistenceFrom(livingEntity).getName(), "Dinnerbone") ? true : original;
      }
   }
}
