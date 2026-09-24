package io.github.manasmods.tensura.util.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.github.manasmods.tensura.client.TensuraColors;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderHelper {
   private static RenderHelper.FadeParameters fadeParameters;
   private static final int maxTextAreaHighlight = 10;
   private static int textAreaHighlight = 10;

   public static void renderCustomBlur(int blurStrength) {
      Minecraft minecraft = Minecraft.getInstance();
      PostChain postChain = minecraft.gameRenderer.blurEffect;
      if (postChain != null) {
         postChain.setUniform("Radius", blurStrength);
         postChain.process(1.0F);
         minecraft.getMainRenderTarget().bindWrite(false);
      }
   }

   public static void fadeScreen(boolean fadeIn) {
      fadeParameters = new RenderHelper.FadeParameters(fadeIn, 10, () -> {});
   }

   public static void fadeScreen(boolean fadeIn, int duration) {
      fadeParameters = new RenderHelper.FadeParameters(fadeIn, duration, () -> {});
   }

   public static void fadeScreen(boolean fadeIn, int duration, Runnable runnable) {
      fadeParameters = new RenderHelper.FadeParameters(fadeIn, duration, runnable);
   }

   public static void cancelFadeScreen() {
      fadeParameters = null;
   }

   public static boolean currentlyFading() {
      return fadeParameters != null;
   }

   public static boolean handleFade(GuiGraphics graphics, Runnable runnable) {
      if (fadeParameters == null) {
         return true;
      }

      if (graphics == null) {
         fadeParameters = null;
         return false;
      }

      float value = (float)fadeParameters.copyDuration / fadeParameters.duration;
      float alpha = fadeParameters.fadeIn ? 1.0F - value : 0.0F + value;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
      runnable.run();
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      fadeParameters.copyDuration--;
      if (fadeParameters.copyDuration > 0) {
         return false;
      }

      fadeParameters.runnable.run();
      boolean returnValue = fadeParameters.fadeIn;
      fadeParameters = null;
      return returnValue;
   }

   public static void preciseBlit(GuiGraphics graphics, ResourceLocation texture, float pX, float pY, float uvX, float uvY, int width, int height) {
      preciseBlit(graphics, texture, pX, pY, width, height, 1.0F, uvX, uvY, width, height, width, height, true);
   }

   public static void preciseBlit(
      GuiGraphics graphics, ResourceLocation texture, float pX, float pY, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight
   ) {
      preciseBlit(graphics, texture, pX, pY, width, height, 1.0F, uvX, uvY, width, height, textureWidth, textureHeight, true);
   }

   public static void preciseBlit(
      GuiGraphics graphics,
      ResourceLocation texture,
      float pX,
      float pY,
      int width,
      int height,
      float alpha,
      float uvX,
      float uvY,
      float regionWidth,
      float regionHeight,
      float textureWidth,
      float textureHeight,
      boolean keepAlpha
   ) {
      float x1 = pX + width;
      float y1 = pY + height;
      float u0 = (uvX + 0.0F) / textureWidth;
      float u1 = (uvX + regionWidth) / textureWidth;
      float v0 = (uvY + 0.0F) / textureHeight;
      float v1 = (uvY + regionHeight) / textureHeight;
      RenderSystem.setShaderTexture(0, texture);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
      Matrix4f matrix4f = graphics.pose().last().pose();
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferBuilder.addVertex(matrix4f, pX, pY, 0.0F).setUv(u0, v0);
      bufferBuilder.addVertex(matrix4f, pX, y1, 0.0F).setUv(u0, v1);
      bufferBuilder.addVertex(matrix4f, x1, y1, 0.0F).setUv(u1, v1);
      bufferBuilder.addVertex(matrix4f, x1, pY, 0.0F).setUv(u1, v0);
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
      if (!keepAlpha) {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void renderFadingTextureWithDuration(ResourceLocation texture, int duration, int startFade, float screenWidth, float screenHeight) {
      float alphaValue = 1.0F;
      if (duration < startFade) {
         alphaValue = duration / 200.0F;
      }

      renderTextureOverlay(texture, alphaValue, screenWidth, screenHeight);
   }

   public static void renderFadingTextureWithDuration(ResourceLocation texture, int duration, int startFade, int color, float screenWidth, float screenHeight) {
      float alphaValue = 1.0F;
      if (duration < startFade) {
         alphaValue = duration / 200.0F;
      }

      renderTextureOverlay(texture, color, alphaValue, screenWidth, screenHeight);
   }

   public static void renderFadingTextureWithDuration(
      ResourceLocation texture, int duration, int startFade, float red, float green, float blue, float screenWidth, float screenHeight
   ) {
      float alphaValue = 1.0F;
      if (duration < startFade) {
         alphaValue = duration / 200.0F;
      }

      renderTextureOverlay(texture, red, green, blue, alphaValue, screenWidth, screenHeight);
   }

   public static void renderTextureOverlay(ResourceLocation texture, float alpha, float screenWidth, float screenHeight) {
      renderTextureOverlay(texture, 1.0F, 1.0F, 1.0F, alpha, screenWidth, screenHeight);
   }

   public static void renderTextureOverlay(ResourceLocation texture, int color, float alpha, float screenWidth, float screenHeight) {
      renderTextureOverlay(texture, (color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha, screenWidth, screenHeight);
   }

   public static void renderTextureOverlay(ResourceLocation texture, float red, float green, float blue, float alpha, float screenWidth, float screenHeight) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(red, green, blue, alpha);
      RenderSystem.setShaderTexture(0, texture);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.addVertex(0.0F, screenHeight, -90.0F).setUv(0.0F, 1.0F);
      bufferbuilder.addVertex(screenWidth, screenHeight, -90.0F).setUv(1.0F, 1.0F);
      bufferbuilder.addVertex(screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F);
      bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void renderButton(
      GuiGraphics graphics,
      ResourceLocation texture,
      @Nullable Component tooltip,
      int posX,
      int posY,
      int width,
      int height,
      int textureWidth,
      int textureHeight,
      int hoveredOffsetX,
      int hoveredOffsetY,
      int mX,
      int mY,
      Screen screen
   ) {
      boolean hovered = mouseOver(mX, mY, posX, posX + width, posY, posY + height);
      graphics.blit(texture, posX, posY, hoveredOffsetX, hoveredOffsetY, width, height, textureWidth, textureHeight);
      if (hovered && tooltip != null) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void renderButton(
      GuiGraphics graphics,
      ResourceLocation texture,
      @Nullable Component tooltip,
      int posX,
      int posY,
      int width,
      int height,
      int hoveredOffsetX,
      int hoveredOffsetY,
      int mX,
      int mY,
      Screen screen
   ) {
      renderButton(graphics, texture, tooltip, posX, posY, width, height, width, height, hoveredOffsetX, hoveredOffsetY, mX, mY, screen);
   }

   public static void renderButton(
      GuiGraphics graphics, ResourceLocation texture, @Nullable Component tooltip, int posX, int posY, int width, int height, int mX, int mY, Screen screen
   ) {
      renderButton(graphics, texture, tooltip, posX, posY, width, height, width, height, width / 2, height / 2, mX, mY, screen);
   }

   public static void renderWithTooltip(
      GuiGraphics graphics, ResourceLocation texture, int posX, int posY, int width, int height, int mX, int mY, Component tooltip, Screen screen
   ) {
      graphics.blit(texture, posX, posY, 0.0F, 0.0F, width, height, width, height);
      if (mouseOver(mX, mY, posX, posX + width, posY, posY + height)) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void renderEntityInInventoryFollowsMouse(
      GuiGraphics guiGraphics,
      float posX,
      float posY,
      float endPosX,
      float endPosY,
      float scale,
      float angleXcomponent,
      float angleYcomponent,
      boolean scissors,
      LivingEntity livingEntity
   ) {
      float centerX = (posX + endPosX) / 2.0F;
      float centerY = (posY + endPosY) / 2.0F;
      float newRotX = (float)Math.atan((centerX - angleXcomponent) / 40.0F);
      float newRotY = (float)Math.atan((centerY - angleYcomponent) / 40.0F);
      Quaternionf quaternionZ = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf quaternionX = new Quaternionf().rotateX(newRotY * 20.0F * (float) (Math.PI / 180.0));
      quaternionZ.mul(quaternionX);
      float yBodyRot = livingEntity.yBodyRot;
      float yRot = livingEntity.getYRot();
      float xRot = livingEntity.getXRot();
      float yHeadRotO = livingEntity.yHeadRotO;
      float yHeadRot = livingEntity.yHeadRot;
      livingEntity.yBodyRot = 180.0F + newRotX * 20.0F;
      livingEntity.setYRot(180.0F + newRotX * 40.0F);
      livingEntity.setXRot(-newRotY * 20.0F);
      livingEntity.yHeadRot = livingEntity.getYRot();
      livingEntity.yHeadRotO = livingEntity.getYRot();
      float translateY = livingEntity.getBbHeight() / 2.0F + 0.0625F * livingEntity.getScale();
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(centerX, centerY, 50.0F);
      poseStack.scale(scale, scale, -scale);
      poseStack.translate(0.0F, translateY, 0.0F);
      poseStack.mulPose(quaternionZ);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      dispatcher.overrideCameraOrientation(quaternionX.rotateY((float) Math.PI));
      dispatcher.setRenderShadow(false);
      if (scissors) {
         guiGraphics.enableScissor((int)posX, (int)posY, (int)endPosX, (int)endPosY);
      }

      RenderSystem.runAsFancy(() -> dispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, guiGraphics.bufferSource(), 15728880));
      if (scissors) {
         guiGraphics.disableScissor();
      }

      guiGraphics.flush();
      dispatcher.setRenderShadow(true);
      poseStack.popPose();
      Lighting.setupFor3DItems();
      livingEntity.yBodyRot = yBodyRot;
      livingEntity.setYRot(yRot);
      livingEntity.setXRot(xRot);
      livingEntity.yHeadRotO = yHeadRotO;
      livingEntity.yHeadRot = yHeadRot;
   }

   public static void drawShortenedText(GuiGraphics graphics, Font font, Component text, int posX, int posY, int maxWidth, int color, boolean appendDots) {
      if (font.width(text) <= maxWidth) {
         graphics.drawString(font, text, posX, posY, color);
      } else {
         String string = text.getString();
         String ellipsis = "...";
         int ellipsisWidth = appendDots ? font.width(ellipsis) : 0;
         int availableWidth = maxWidth - ellipsisWidth;
         int stopAt = 0;

         for (int i = 1; i <= string.length(); stopAt = i++) {
            String substr = string.substring(0, i);
            if (font.width(substr) > availableWidth) {
               break;
            }
         }

         MutableComponent truncated = Component.literal(string.substring(0, stopAt)).setStyle(text.getStyle());
         if (appendDots && stopAt < string.length()) {
            truncated.append(Component.literal(ellipsis));
         }

         graphics.drawString(font, truncated, posX, posY, color);
      }
   }

   public static void drawScrollingText(GuiGraphics guiGraphics, Font font, String text, int posX, int posY, int width, int color) {
      drawScrollingText(guiGraphics, font, text, posX, posY, width, color, true);
   }

   public static void drawScrollingText(GuiGraphics guiGraphics, Font font, String text, int posX, int posY, int width, int color, boolean scissors) {
      drawScrollingText(guiGraphics, font, Component.literal(text), posX, posY, width, color, scissors);
   }

   public static void drawScrollingText(GuiGraphics guiGraphics, Font font, Component text, int posX, int posY, int width, int color) {
      drawScrollingText(guiGraphics, font, text, posX, posY, width, color, true);
   }

   public static void drawScrollingText(GuiGraphics guiGraphics, Font font, Component text, int posX, int posY, int width, int color, boolean scissors) {
      int textWidth = font.width(text);
      if (textWidth <= width) {
         guiGraphics.drawString(font, text, posX, posY, color);
      } else {
         int scrollDistance = textWidth - width;
         double timeSeconds = Util.getMillis() / 1000.0;
         double scrollPeriod = Math.max(scrollDistance * 0.5, 3.0);
         double scrollPhase = Math.cos(timeSeconds / scrollPeriod * 2.0 * Math.PI);
         double scrollFactor = (Math.sin(scrollPhase * (Math.PI / 2)) + 1.0) / 2.0;
         int offset = (int)Mth.lerp(scrollFactor, 0.0, scrollDistance);
         if (scissors) {
            guiGraphics.enableScissor(posX, posY, posX + width, posY + 9);
         }

         guiGraphics.drawString(font, text, posX - offset, posY, color);
         if (scissors) {
            guiGraphics.disableScissor();
         }
      }
   }

   public static void drawSimpleScrollingText(GuiGraphics guiGraphics, Font font, Component text, int posX, int posY, int maxLength, int color) {
      drawSimpleScrollingText(guiGraphics, font, text.getString(), posX, posY, maxLength, color);
   }

   public static void drawSimpleScrollingText(GuiGraphics guiGraphics, Font font, String text, int posX, int posY, int maxLength, int color) {
      int totalLength = text.length();
      if (totalLength <= maxLength) {
         guiGraphics.drawString(font, text, posX, posY, color);
      } else {
         int maxIndex = totalLength - maxLength;
         int pause = Math.clamp(500L * maxLength / totalLength, 250, 750);
         int cycle = (int)(Util.getMillis() / pause) % (maxIndex * 2 + 4);
         if (cycle == maxIndex * 2 + 3) {
            cycle = 0;
         } else if (cycle > maxIndex + 2) {
            cycle = maxIndex * 2 + 2 - cycle;
         } else if (cycle > maxIndex) {
            cycle = maxIndex;
         }

         guiGraphics.drawString(font, text.substring(cycle, cycle + maxLength), posX, posY, color);
      }
   }

   public static void drawTextWithTooltip(
      GuiGraphics graphics, Font font, String string, int x, int y, int color, int mX, int mY, Component tooltip, Screen screen
   ) {
      graphics.drawString(font, string, x, y, color);
      if (mouseOver(mX, mY, x, x + font.width(string), y - 1, y + 7)) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void drawShortenedTextWithTooltip(
      GuiGraphics graphics,
      Font font,
      Component text,
      Component tooltip,
      int posX,
      int posY,
      int textXOffset,
      int textYOffset,
      int width,
      int height,
      int mX,
      int mY,
      int color,
      boolean appendDots,
      Screen screen
   ) {
      drawShortenedText(graphics, font, text, posX + textXOffset, posY + textYOffset, width - textXOffset, color, appendDots);
      if (mouseOver(mX, mY, posX, posX + width, posY, posY + height)) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void drawCenteredText(GuiGraphics graphics, Font font, Component text, int posX, int posY, int width, int color) {
      drawCenteredText(graphics, font, text.getString(), posX, posY, width, color);
   }

   public static void drawCenteredText(GuiGraphics graphics, Font font, String string, int posX, int posY, int width, int color) {
      int textWidth = font.width(string);
      graphics.drawString(font, string, posX + width / 2 - textWidth / 2, posY, color);
   }

   public static void drawCenteredText(GuiGraphics graphics, Font font, Component component, int x, int y, int color, boolean shadow) {
      FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
      graphics.drawString(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2, y, color, shadow);
   }

   public static void drawCenteredText(GuiGraphics graphics, Font font, Component component, int x, int y, int width, int color, boolean shadow) {
      drawCenteredText(graphics, font, component, x + width / 2, y, color, shadow);
   }

   public static void drawCenteredText(GuiGraphics graphics, Font font, String string, int x, int y, int width, int color, boolean shadow) {
      drawCenteredText(graphics, font, Component.literal(string), x + width / 2, y, color, shadow);
   }

   public static void drawCenteredTextWithTooltip(
      GuiGraphics graphics, Font font, Component component, int x, int y, int color, boolean shadow, int mX, int mY, Component tooltip, Screen screen
   ) {
      drawCenteredText(graphics, font, component, x, y, color, shadow);
      if (mouseOver(mX, mY, x, x + font.width(component), y - 1, y + 7)) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void drawCenteredTextWithTooltip(
      GuiGraphics graphics,
      Font font,
      Component component,
      int x,
      int y,
      int width,
      int color,
      boolean shadow,
      int mX,
      int mY,
      Component tooltip,
      Screen screen
   ) {
      drawCenteredText(graphics, font, component, x, y, width, color, shadow);
      if (mouseOver(mX, mY, x, x + width, y - 1, y + 7)) {
         screen.setTooltipForNextRenderPass(tooltip);
      }
   }

   public static void drawScrollableTextInArea(
      GuiGraphics graphics,
      Font font,
      List<FormattedCharSequence> sequences,
      int posX,
      int posY,
      int width,
      int height,
      int ySeparation,
      int startIndex,
      int endIndex
   ) {
      internalDrawScrollableTextInArea(graphics, font, sequences, posX, posY, width, height, ySeparation, startIndex, endIndex, false, 0, 0);
   }

   public static void drawScrollableTextInAreaAutoHighlight(
      GuiGraphics graphics,
      Font font,
      List<FormattedCharSequence> sequences,
      int posX,
      int posY,
      int width,
      int height,
      int ySeparation,
      int startIndex,
      int endIndex,
      boolean highlightText,
      int color
   ) {
      int red = Math.clamp(color >> 16 & 0xFF, 0, 180);
      int green = Math.clamp(color >> 8 & 0xFF, 0, 180);
      int blue = Math.clamp(color & 0xFF, 0, 180);
      int highlightColor = new Color(red, green, blue, 255 / textAreaHighlight).darker().getRGB();
      internalDrawScrollableTextInArea(
         graphics, font, sequences, posX, posY, width, height, ySeparation, startIndex, endIndex, highlightText, color, highlightColor
      );
   }

   public static void drawScrollableTextInAreaSetHighlight(
      GuiGraphics graphics,
      Font font,
      List<FormattedCharSequence> sequences,
      int posX,
      int posY,
      int width,
      int height,
      int ySeparation,
      int startIndex,
      int endIndex,
      boolean highlightText,
      int color,
      int highlightColor
   ) {
      int red = highlightColor >> 16 & 0xFF;
      int green = highlightColor >> 8 & 0xFF;
      int blue = highlightColor & 0xFF;
      highlightColor = new Color(red, green, blue, 255 / textAreaHighlight).getRGB();
      internalDrawScrollableTextInArea(
         graphics, font, sequences, posX, posY, width, height, ySeparation, startIndex, endIndex, highlightText, color, highlightColor
      );
   }

   private static void internalDrawScrollableTextInArea(
      GuiGraphics graphics,
      Font font,
      List<FormattedCharSequence> sequences,
      int posX,
      int posY,
      int width,
      int height,
      int ySeparation,
      int startIndex,
      int endIndex,
      boolean highlightText,
      int color,
      int highlightColor
   ) {
      int maxY = posY + height;
      boolean notMaxHighlight = textAreaHighlight < 10;
      if (highlightText || notMaxHighlight) {
         graphics.fill(posX, posY, posX + width, maxY, highlightColor);
         if (highlightText && textAreaHighlight > 1) {
            textAreaHighlight--;
         } else if (!highlightText) {
            textAreaHighlight++;
         }
      }

      for (int i = startIndex; i < endIndex; i++) {
         if (i == endIndex - 1 && i < sequences.size() - 1) {
            FormattedCharSequence sequence = FormattedCharSequence.fromPair(sequences.get(i), Component.literal("...").getVisualOrderText());
            graphics.drawString(font, sequence, posX, posY, color, false);
         } else {
            graphics.drawString(font, sequences.get(i), posX, posY, color, false);
         }

         posY += 9 + ySeparation;
      }
   }

   public static void drawScaledTextInArea(GuiGraphics graphics, Font font, Component text, float x, float y, float width, float height, Color color) {
      drawScaledTextInArea(graphics, font, text, x, y, width, height, color, 0.0F);
   }

   public static void drawScaledTextInArea(GuiGraphics graphics, Font font, Component text, float x, float y, float width, float height, int color) {
      drawScaledTextInArea(graphics, font, text, x, y, width, height, color, 0.0F);
   }

   public static void drawScaledTextInArea(
      GuiGraphics graphics, Font font, Component text, float x, float y, float width, float height, Color color, float spacePerLine
   ) {
      drawScaledTextInArea(graphics, font, text, x, y, width, height, color, spacePerLine, 0.01F);
   }

   public static void drawScaledTextInArea(
      GuiGraphics graphics, Font font, Component text, float x, float y, float width, float height, int color, float spacePerLine
   ) {
      drawScaledTextInArea(graphics, font, text, x, y, width, height, color, spacePerLine, 0.01F);
   }

   public static void drawScaledTextInArea(
      GuiGraphics graphics, Font font, Component text, float x, float y, float width, float height, Color color, float spacePerLine, float scalingSteps
   ) {
      drawScaledTextInArea(graphics, font, text, x, y, width, height, color.getRGB(), spacePerLine, scalingSteps);
   }

   public static void drawScaledTextInArea(
      GuiGraphics graphics, Font font, Component component, float x, float y, float width, float height, int color, float spacePerLine, float scalingSteps
   ) {
      float scaling = 1.0F;

      while (true) {
         List<FormattedCharSequence> list = split(font, component, Math.round(width / scaling));
         float size = list.size();
         if (size * (9.0F + spacePerLine) * scaling <= height) {
            drawScaledText(graphics, font, scaling, list, x, y, color, spacePerLine);
            return;
         }

         scaling -= scalingSteps;
      }
   }

   public static void drawScaledWrappedText(GuiGraphics graphics, Font font, Component text, float x, float y, float width, Color color) {
      drawScaledWrappedText(graphics, font, text, x, y, width, color, 0.0F);
   }

   public static void drawScaledWrappedText(GuiGraphics graphics, Font font, Component text, float x, float y, float width, int color) {
      drawScaledWrappedText(graphics, font, text, x, y, width, color, 0.0F);
   }

   public static void drawScaledWrappedText(GuiGraphics graphics, Font font, Component text, float x, float y, float width, Color color, float spacePerLine) {
      drawScaledWrappedText(graphics, font, text, x, y, width, color.getRGB(), spacePerLine);
   }

   public static void drawScaledWrappedText(GuiGraphics graphics, Font font, Component component, float x, float y, float width, int color, float spacePerLine) {
      List<FormattedCharSequence> sequences = split(font, component, Minecraft.getInstance().getWindow().getScreenWidth());
      int maxBaseWidth = 0;

      for (FormattedCharSequence seq : sequences) {
         int w = font.width(seq);
         if (w > maxBaseWidth) {
            maxBaseWidth = w;
         }
      }

      float scaling = 1.0F;

      while (Math.round(maxBaseWidth * scaling) > width) {
         scaling -= 0.01F;
      }

      drawScaledText(graphics, font, scaling, split(font, component, Math.round(width / scaling)), x, y, color, spacePerLine);
   }

   private static void drawScaledText(
      GuiGraphics graphics, Font font, float scaling, List<FormattedCharSequence> text, float x, float y, int color, float spacePerLine
   ) {
      graphics.pose().scale(scaling, scaling, scaling);
      Iterator<FormattedCharSequence> iterator = text.iterator();

      while (iterator.hasNext()) {
         graphics.drawString(font, iterator.next(), (int)(x / scaling), (int)(y / scaling), color);
         y += (9.0F + spacePerLine) * scaling;
      }

      graphics.pose().scale(1.0F / scaling, 1.0F / scaling, 1.0F / scaling);
   }

   public static List<FormattedCharSequence> split(Font font, Component component, int i) {
      return Language.getInstance().getVisualOrder(font.getSplitter().splitLines(component, i, component.getStyle()));
   }

   public static boolean mouseOver(double mouseX, double mouseY, double x1, double x2, double y1, double y2) {
      return mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
   }

   public static Component getShortenedNumber(double value) {
      if (value < 1000000.0) {
         return Component.literal(new DecimalFormat("#").format(value));
      }

      DecimalFormat decimal = new DecimalFormat("#.##");
      return value < 1.0E9
         ? Component.translatable("tensura.main_menu.million_index", new Object[]{decimal.format(value / 1000000.0)})
         : Component.translatable("tensura.main_menu.billion_index", new Object[]{decimal.format(value / 1.0E9)});
   }

   public static void highlightArea(GuiGraphics graphics, float x1, float y1, float x2, float y2) {
      highlightArea(graphics, x1, y1, x2, y2, 16777215);
   }

   public static void highlightArea(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
      highlightArea(graphics, x1, y1, x2, y2, color, 5000);
   }

   public static void highlightArea(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color, int cycleSpeedMs) {
      double time = System.currentTimeMillis() % cycleSpeedMs / (cycleSpeedMs / 2.0);
      float alpha = (float)(0.35F + 0.15F * Math.sin(time * 2.0 * Math.PI));
      highlightArea(graphics, x1, y1, x2, y2, color, alpha);
   }

   public static void highlightArea(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color, float alpha) {
      color = TensuraColors.getARGB(color, alpha);
      Matrix4f matrix = graphics.pose().last().pose();
      VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
      consumer.addVertex(matrix, x1, y1, 0.0F).setColor(color);
      consumer.addVertex(matrix, x1, y2, 0.0F).setColor(color);
      consumer.addVertex(matrix, x2, y2, 0.0F).setColor(color);
      consumer.addVertex(matrix, x2, y1, 0.0F).setColor(color);
      graphics.flush();
   }

   private static class FadeParameters {
      public boolean fadeIn;
      public int duration;
      public int copyDuration;
      public Runnable runnable;

      public FadeParameters(boolean fadeIn, int duration, Runnable runnable) {
         this.fadeIn = fadeIn;
         this.duration = duration;
         this.copyDuration = duration;
         this.runnable = runnable;
      }
   }

   @FunctionalInterface
   public interface RenderAction {
      void run(GuiGraphics var1, double var2, double var4);
   }
}
