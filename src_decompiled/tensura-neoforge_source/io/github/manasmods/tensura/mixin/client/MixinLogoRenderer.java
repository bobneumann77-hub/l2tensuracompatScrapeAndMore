package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.util.client.TitleScreenHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LogoRenderer.class, priority = 800)
public abstract class MixinLogoRenderer {
   @Shadow
   @Final
   private boolean keepLogoThroughFade;

   @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"), cancellable = true)
   private void renderLogo(GuiGraphics guiGraphics, int i, float f, int j, CallbackInfo ci) {
      if (((MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class)).tensuraTitleScreen) {
         TitleScreenHelper.renderLogo(guiGraphics, i, f, j, this.keepLogoThroughFade);
         ci.cancel();
      }
   }
}
