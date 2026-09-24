package io.github.manasmods.tensura.client.screen.templates;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.client.MenuConfig;
import io.github.manasmods.tensura.util.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public abstract class SimpleScreen extends Screen {
   private float translateX;
   private float translateY;
   protected LocalPlayer player;
   protected float scale;
   protected int imageWidth;
   protected int imageHeight;
   protected boolean shouldFade;
   protected boolean shouldRenderWidgets;
   protected static int guiLeft;
   protected static int guiRight;
   protected static int guiTop;
   protected static int guiBottom;
   protected static int guiCenterX;
   protected static int guiCenterY;
   protected static int blurStrength;

   protected SimpleScreen(Component title, int width, int height) {
      super(title);
      this.player = Minecraft.getInstance().player;
      this.font = Minecraft.getInstance().font;
      this.imageWidth = width;
      this.imageHeight = height;
      this.shouldRenderWidgets = true;
      MenuConfig cfg = (MenuConfig)ConfigRegistry.getConfig(MenuConfig.class);
      this.scale = cfg.scale;
      this.shouldFade = cfg.fadeEffects;
      blurStrength = Math.clamp(cfg.blurStrength, 0, 5);
   }

   protected void init() {
      guiLeft = (this.width - this.imageWidth) / 2;
      guiTop = (this.height - this.imageHeight) / 2;
      guiRight = this.imageWidth;
      guiBottom = this.imageHeight;
      guiCenterX = guiLeft + guiRight / 2;
      guiCenterY = guiTop + guiBottom / 2;
      if (this.shouldFade) {
         RenderHelper.fadeScreen(true);
      }
   }

   public void render(GuiGraphics g, int mX, int mY, float pT) {
      if (blurStrength > 0) {
         RenderHelper.renderCustomBlur(blurStrength);
      }

      Runnable runnable = () -> this.renderMain(g, mX, mY, pT);
      if (RenderHelper.handleFade(g, runnable)) {
         runnable.run();
      }
   }

   public void renderMain(GuiGraphics graphics, int mX, int mY, float partialTick) {
      this.renderBackground(graphics, mX, mY, partialTick);
      if (this.shouldRenderWidgets) {
         this.renderWidgets(graphics, mX, mY, partialTick);
      }

      this.renderTooltip(graphics, mX, mY);
   }

   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
   }

   protected final void renderBlurredBackground(float f) {
   }

   public void renderWidgets(GuiGraphics graphics, int mX, int mY, float partialTick) {
      for (Renderable renderable : this.renderables) {
         renderable.render(graphics, mX, mY, partialTick);
      }
   }

   public void renderTooltip(GuiGraphics graphics, int mX, int mY) {
   }

   public boolean mouseClicked(double mX, double mY, int button) {
      return super.mouseClicked(mX, mY, button);
   }

   public boolean mouseReleased(double mX, double mY, int button) {
      return super.mouseReleased(mX, mY, button);
   }

   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      return super.mouseDragged(mX, mY, button, dragX, dragY);
   }

   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      return super.mouseScrolled(mX, mY, deltaX, deltaY);
   }

   public void mouseMoved(double mX, double mY) {
      super.mouseMoved(mX, mY);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void onClose() {
      if (!this.shouldFade) {
         super.onClose();
      } else if (RenderHelper.currentlyFading()) {
         super.onClose();
         RenderHelper.cancelFadeScreen();
         RenderSystem.disableBlend();
      } else {
         RenderHelper.fadeScreen(false, 10, () -> {
            super.onClose();
            RenderSystem.disableBlend();
         });
      }
   }
}
