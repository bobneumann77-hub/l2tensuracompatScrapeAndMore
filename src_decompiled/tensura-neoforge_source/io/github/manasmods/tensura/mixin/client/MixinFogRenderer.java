package io.github.manasmods.tensura.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.world.biome.BiomeRendererUtils;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
   @Unique
   private static final float FOG_TRANSITION_SPEED = 0.05F;
   @Unique
   private static float tensura$currentFogStrength = 0.0F;
   @Unique
   private static float tensura$targetFogStrength = 0.0F;
   @Unique
   private static long tensura$lastUpdateTick = -1L;
   @Unique
   private static float tensura$cachedFogDensity = 0.0F;
   @Unique
   private static ResourceKey<Level> tensura$lastDim = null;

   @Inject(method = "levelFogColor()V", at = @At("HEAD"), cancellable = true)
   private static void setupColor(CallbackInfo ci) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         double vision = player.getAttributeValue(TensuraAttributes.DARK_VISION);
         if (vision > 0.0) {
            RenderSystem.clearColor(0.1F, 0.1F, 0.1F, 0.0F);
            RenderSystem.setShaderFogColor(0.1F, 0.1F, 0.1F);
            ci.cancel();
         } else if (vision < 0.0) {
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 0.0F);
            RenderSystem.setShaderFogColor(1.0F, 1.0F, 1.0F);
            ci.cancel();
         }
      }
   }

   @Inject(method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V", at = @At("TAIL"))
   private static void setupFog(Camera camera, FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            if (tensura$lastDim != level.dimension()) {
               tensura$currentFogStrength = 0.0F;
               tensura$targetFogStrength = 0.0F;
               tensura$cachedFogDensity = 0.0F;
               tensura$lastDim = level.dimension();
               tensura$lastUpdateTick = -1L;
            }

            long currentTick = level.getGameTime();
            boolean newTick = currentTick != tensura$lastUpdateTick;
            if (newTick) {
               tensura$lastUpdateTick = currentTick;
               tensura$cachedFogDensity = BiomeRendererUtils.getFogDensity(level, camera, fogMode, camera.getFluidInCamera());
            }

            float fogDensity = tensura$cachedFogDensity;
            double vision = Math.abs(player.getAttributeValue(TensuraAttributes.DARK_VISION));
            if (fogDensity <= 0.0F && vision > 0.0) {
               tensura$targetFogStrength = 0.88F + 0.1F * (float)vision;
            } else {
               tensura$targetFogStrength = fogDensity > 0.0F ? 1.0F : 0.0F;
            }

            if (newTick && tensura$currentFogStrength != tensura$targetFogStrength) {
               if (tensura$currentFogStrength < tensura$targetFogStrength) {
                  tensura$currentFogStrength = Math.min(tensura$targetFogStrength, tensura$currentFogStrength + 0.05F);
               } else {
                  tensura$currentFogStrength = Math.max(tensura$targetFogStrength, tensura$currentFogStrength - 0.05F);
               }
            }

            if (tensura$currentFogStrength > 0.0F) {
               if (vision > 0.0) {
                  float h = Mth.lerp(1.0F, f, 5.0F);
                  float noFogFarDistance = Math.max(Minecraft.getInstance().gameRenderer.getRenderDistance(), 32.0F);
                  float noFogNearDistance = noFogFarDistance - Mth.clamp(noFogFarDistance / 10.0F, 4.0F, 64.0F);
                  if (fogMode == FogMode.FOG_SKY) {
                     RenderSystem.setShaderFogStart(0.0F);
                     RenderSystem.setShaderFogEnd(Mth.lerp(tensura$currentFogStrength, noFogNearDistance, h * 0.8F));
                  } else {
                     RenderSystem.setShaderFogStart(h * 0.25F);
                     RenderSystem.setShaderFogEnd(Mth.lerp(tensura$currentFogStrength, noFogNearDistance, h));
                  }
               } else {
                  tensura$setUpFog(level, camera, fogDensity, tensura$currentFogStrength, RenderSystem::setShaderFogEnd, RenderSystem::setShaderFogStart);
                  RenderSystem.setShaderFogShape(FogShape.CYLINDER);
               }
            }
         }
      }
   }

   @Unique
   private static void tensura$setUpFog(
      ClientLevel level, Camera camera, float fogDensity, float fogStrength, FloatConsumer farPlaneDistance, FloatConsumer nearPlaneDistance
   ) {
      float cameraHeight = (float)camera.getPosition().y;
      float skyLight = level.getBrightness(LightLayer.SKY, camera.getBlockPosition());
      float delta = Math.abs(cameraHeight - 105.0F);
      float fogCeiling = Math.max(0.0F, cameraHeight - 142.0F);
      float caveDelta = Math.max(0.0F, 1.0F - skyLight / 15.0F);
      float baseFarDistance = Mth.clamp(150.0F - delta * 4.0F, 50.0F, 150.0F) + fogCeiling;
      float baseNearDistance = Mth.clamp(-150.0F + delta * 7.0F + caveDelta * 170.0F, -150.0F, 20.0F) + fogCeiling;
      if (fogDensity > 0.0F) {
         baseFarDistance /= fogDensity;
         baseNearDistance *= fogDensity;
      }

      float noFogFarDistance = Math.max(Minecraft.getInstance().gameRenderer.getRenderDistance(), 32.0F);
      float noFogNearDistance = noFogFarDistance - Mth.clamp(noFogFarDistance / 10.0F, 4.0F, 64.0F);
      float interpolatedFarDistance = Mth.lerp(fogStrength, noFogFarDistance, baseFarDistance);
      float interpolatedNearDistance = Mth.lerp(fogStrength, noFogNearDistance, baseNearDistance);
      farPlaneDistance.accept(interpolatedFarDistance);
      nearPlaneDistance.accept(interpolatedNearDistance);
   }
}
