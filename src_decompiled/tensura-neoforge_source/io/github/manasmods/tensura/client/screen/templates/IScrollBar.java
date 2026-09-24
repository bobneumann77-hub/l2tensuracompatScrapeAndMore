package io.github.manasmods.tensura.client.screen.templates;

import io.github.manasmods.tensura.util.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public interface IScrollBar {
   ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/scroll_bar.png");

   int getScrollBarX();

   int getScrollBarY();

   int getScrollBarTotalSpace();

   int getScrollBarListSize();

   int getScrollBarRenderCount();

   float getScrollOffset();

   boolean isScrolling();

   void setListStartIndex(int var1);

   void setScrolling(boolean var1);

   void setScrollOffset(float var1);

   default int getScrollBarWidth() {
      return 10;
   }

   default int getScrollBarHeight() {
      return 13;
   }

   default int getCustomScrollAmount() {
      return 1;
   }

   default int getScrollBarTextureWidth() {
      return this.getScrollBarWidth();
   }

   default int getScrollBarTextureHeight() {
      return this.getScrollBarHeight() * 2;
   }

   default boolean hasCustomScrollLogic() {
      return false;
   }

   default boolean isScrollBarActive() {
      return this.getScrollBarListSize() > this.getScrollBarRenderCount();
   }

   default ResourceLocation getScrollBarTexture() {
      return TEXTURE;
   }

   default void renderScrollBar(GuiGraphics graphics, int mX, int mY, int uvOffsetX, int uvOffsetY, int uvHoveredOffsetX, int uvHoveredOffsetY) {
      int pX = this.getScrollBarX();
      int pY = this.getScrollBarY();
      int width = this.getScrollBarWidth();
      int height = this.getScrollBarHeight();
      int textureWidth = this.getScrollBarTextureWidth();
      int textureHeight = this.getScrollBarTextureHeight();
      int yOffset = (int)(pY + (this.getScrollBarTotalSpace() - height) * this.getScrollOffset());
      boolean isHovered = this.isScrollBarActive() && RenderHelper.mouseOver(mX, mY, pX, pX + width, yOffset, yOffset + height);
      graphics.blit(
         this.getScrollBarTexture(),
         pX,
         yOffset,
         isHovered ? uvHoveredOffsetX : uvOffsetX,
         isHovered ? uvHoveredOffsetY : uvOffsetY,
         width,
         height,
         textureWidth,
         textureHeight
      );
   }

   default void renderScrollBar(GuiGraphics graphics, int mX, int mY) {
      this.renderScrollBar(graphics, mX, mY, 0, 0, 0, this.getScrollBarHeight());
   }

   default boolean clickedScrollBar(double mX, double mY) {
      int pX = this.getScrollBarX();
      int pY = this.getScrollBarY();
      int width = this.getScrollBarWidth();
      int totalSpace = this.getScrollBarTotalSpace();
      boolean hovered = RenderHelper.mouseOver(mX, mY, pX, pX + width, pY, pY + totalSpace);
      this.setScrolling(hovered);
      return hovered;
   }

   default boolean draggedScrollBar(double mY) {
      if (this.isScrollBarActive() && this.isScrolling()) {
         int pY = this.getScrollBarY();
         int totalSpace = this.getScrollBarTotalSpace();
         int renderCount = this.getScrollBarRenderCount();
         int offscreenRenderCount = this.getScrollBarListSize() - renderCount;
         float scrollOffset = Mth.clamp((float)((mY - pY - 7.5) / (totalSpace - 15.0F)), 0.0F, 1.0F);
         this.setScrollOffset(scrollOffset);
         this.setListStartIndex((int)(scrollOffset * offscreenRenderCount + 0.5));
         return true;
      } else {
         return false;
      }
   }

   default boolean scrolledScrollBar(double deltaY) {
      if (!this.isScrollBarActive()) {
         return false;
      }

      if (this.hasCustomScrollLogic()) {
         deltaY = Math.copySign(1.0, deltaY) * this.getCustomScrollAmount();
      }

      int listSize = this.getScrollBarListSize();
      int renderCount = this.getScrollBarRenderCount();
      int offscreenRenderCount = listSize - renderCount;
      float division = (float)(deltaY / offscreenRenderCount);
      float scrollOffset = Mth.clamp(this.getScrollOffset() - division, 0.0F, 1.0F);
      this.setScrollOffset(scrollOffset);
      this.setListStartIndex((int)(scrollOffset * offscreenRenderCount + 0.5));
      return true;
   }

   default boolean isScrollBarAreaHovered(double mX, double mY) {
      int pX = this.getScrollBarX();
      int pY = this.getScrollBarY();
      int width = this.getScrollBarWidth();
      return RenderHelper.mouseOver(mX, mY, pX, pX + width, pY, pY + this.getScrollBarTotalSpace());
   }

   default boolean isScrollBarHovered(double mX, double mY) {
      int pX = this.getScrollBarX();
      int width = this.getScrollBarWidth();
      int height = this.getScrollBarHeight();
      int yOffset = (int)(this.getScrollBarY() + (this.getScrollBarTotalSpace() - height) * this.getScrollOffset());
      return RenderHelper.mouseOver(mX, mY, pX, pX + width, yOffset, yOffset + height);
   }
}
