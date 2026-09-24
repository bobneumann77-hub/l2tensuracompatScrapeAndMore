package io.github.manasmods.tensura.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class TitleScreenHelper {
   private static final ResourceLocation LOGO = ResourceLocation.fromNamespaceAndPath("tensura", "textures/logo.png");
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/background.png");
   private static final RandomSource random = RandomSource.create();
   private static final Font font;
   private static List<String> splashText;

   public static SplashRenderer getSplash() {
      if (splashText == null || splashText.isEmpty()) {
         splashText = ((MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class)).customSplashText;
      }

      return splashText.isEmpty() ? null : new SplashRenderer(splashText.get(random.nextInt(splashText.size())));
   }

   public static void renderBackground(GuiGraphics graphics, int width, int height) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      graphics.blit(BACKGROUND, 0, 0, 0.0F, 0.0F, width, height, width, height);
   }

   public static void renderLogo(GuiGraphics guiGraphics, int i, float f, int j, boolean keepLogoThroughFade) {
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, keepLogoThroughFade ? 1.0F : f);
      RenderSystem.enableBlend();
      int k = i / 2 - 128;
      guiGraphics.blit(LOGO, k, 10, 0.0F, 0.0F, 256, 93, 256, 93);
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }

   public static void drawVersion(GuiGraphics ms, int alphaFormatted) {
      int textColor = 16777215 | alphaFormatted;
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      ms.drawString(font, "Tensura: Reincarnated - 2.0.1.2", 2, 2, textColor, false);
   }

   static {
      font = Minecraft.getInstance().font;
   }
}
