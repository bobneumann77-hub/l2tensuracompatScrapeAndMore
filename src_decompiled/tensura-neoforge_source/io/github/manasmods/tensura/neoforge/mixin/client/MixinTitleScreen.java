package io.github.manasmods.tensura.neoforge.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.util.client.TitleScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class, priority = 800)
public abstract class MixinTitleScreen {
   @Inject(
      method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/internal/BrandingControl;forEachLine(ZZLjava/util/function/BiConsumer;)V",
         shift = Shift.BEFORE
      )
   )
   private void renderVersion(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local(name = "i") int i) {
      if (((MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class)).tensuraTitleScreen && Minecraft.getInstance().screen instanceof TitleScreen) {
         TitleScreenHelper.drawVersion(context, 16777215 | i);
      }
   }
}
