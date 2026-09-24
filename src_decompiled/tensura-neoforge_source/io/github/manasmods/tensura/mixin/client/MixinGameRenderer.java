package io.github.manasmods.tensura.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class, priority = 800)
public class MixinGameRenderer {
   @Unique
   private Double tensuraMod$defaultSensitivity;
   @Unique
   private Minecraft tensuraMod$minecraft = Minecraft.getInstance();

   @Inject(method = "bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("HEAD"), cancellable = true)
   private void getOverlayCoords(PoseStack poseStack, float f, CallbackInfo ci) {
      if (this.tensuraMod$minecraft.getCameraEntity() instanceof LivingEntity entity) {
         if (entity.isDeadOrDying()) {
            return;
         }

         if (SkillUtils.shouldCancelPainShake(entity)) {
            ci.cancel();
         }
      }
   }

   @Inject(at = @At(value = "RETURN", ordinal = 1), method = "getFov(Lnet/minecraft/client/Camera;FZ)D", cancellable = true)
   private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
      if (camera.getEntity() instanceof Player player) {
         cir.setReturnValue(this.tensuraMod$zoom((Double)cir.getReturnValue(), player));
      }
   }

   @Unique
   public double tensuraMod$zoom(double fov, Player player) {
      OptionInstance<Double> mouseSensitivity = this.tensuraMod$minecraft.options.sensitivity();
      double zoom = player.getAttributeValue(TensuraAttributes.VIEW_ZOOM);
      if (zoom == 0.0) {
         if (this.tensuraMod$defaultSensitivity != null) {
            mouseSensitivity.set(this.tensuraMod$defaultSensitivity);
            this.tensuraMod$defaultSensitivity = null;
         }

         return fov;
      } else {
         if (this.tensuraMod$defaultSensitivity == null) {
            this.tensuraMod$defaultSensitivity = (Double)mouseSensitivity.get();
         }

         mouseSensitivity.set(this.tensuraMod$defaultSensitivity * (1.0 / zoom));
         return fov / zoom;
      }
   }
}
