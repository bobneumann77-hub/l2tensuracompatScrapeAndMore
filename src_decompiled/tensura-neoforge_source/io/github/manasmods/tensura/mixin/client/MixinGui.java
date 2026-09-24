package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.handler.client.OverlayHandler;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 800)
public abstract class MixinGui {
   @Shadow
   @Final
   private Minecraft minecraft;

   @WrapOperation(
      method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
      at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V")
   )
   private void wrapCrosshair(GuiGraphics graphics, ResourceLocation location, int i, int j, int k, int l, Operation<Void> original) {
      Player player = this.minecraft.player;
      if (player != null && !player.isShiftKeyDown() && player.getAttributeValue(TensuraAttributes.WARP_SHOT) > 0.0) {
         ResourceLocation warpShot = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/abilities/warp_shot_crosshair.png");
         graphics.blit(warpShot, (graphics.guiWidth() - 15) / 2, (graphics.guiHeight() - 15) / 2, 0.0F, 0.0F, 15, 15, 15, 15);
      } else {
         original.call(new Object[]{graphics, location, i, j, k, l});
      }
   }

   @Inject(
      method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V", shift = Shift.AFTER)
   )
   private void renderPre(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
      OverlayHandler.pre_renderEffects();
      OverlayHandler.renderHud(guiGraphics);
   }
}
