package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.util.client.TitleScreenHelper;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class, priority = 1200)
public abstract class MixinTitleScreen {
   @Shadow
   @Nullable
   private SplashRenderer splash;

   @Inject(method = "init()V", at = @At("RETURN"))
   private void onInit(CallbackInfo ci) {
      MiscClientConfig config = (MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class);
      if (config != null && config.tensuraTitleScreen) {
         SplashRenderer renderer = TitleScreenHelper.getSplash();
         if (renderer != null) {
            this.splash = renderer;
         }
      }
   }
}
