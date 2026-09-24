package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.util.client.TitleScreenHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PanoramaRenderer.class, priority = 800)
public abstract class MixinPanorama {
   @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIFF)V", at = @At("HEAD"), cancellable = true)
   private void onRenderBackground(GuiGraphics guiGraphics, int i, int j, float f, float g, CallbackInfo ci) {
      if (((MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class)).tensuraTitleScreen) {
         TitleScreenHelper.renderBackground(guiGraphics, i, j);
         ci.cancel();
      }
   }
}
