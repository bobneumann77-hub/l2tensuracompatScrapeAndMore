package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.util.client.RenderHelper;
import lombok.Generated;

public class OverlayElement {
   private OverlayElement.Renderer renderer;
   private float posX;
   private float posY;
   private float width;
   private float height;
   private float scale;
   private boolean shouldRender = true;
   private boolean focused = false;

   public OverlayElement() {
   }

   public OverlayElement(OverlayElement.Renderer renderer) {
      this.renderer = renderer;
   }

   public void render() {
      if (this.shouldRender) {
         this.renderer.render();
      }
   }

   public void render(float posX, float posY, float width, float height, float scale) {
      if (this.shouldRender) {
         this.renderer.render();
      }

      this.setPosX(posX);
      this.setPosY(posY);
      this.setScale(scale);
      this.setWidth(width);
      this.setHeight(height);
   }

   public boolean isHovered(double mX, double mY) {
      double x1 = this.getPosX();
      double y1 = this.getPosY();
      double x2 = this.getRight();
      double y2 = this.getBottom();
      return RenderHelper.mouseOver(mX, mY, x1, x2, y1, y2);
   }

   public float getRight() {
      return this.getPosX() + this.getWidth();
   }

   public float getBottom() {
      return this.getPosY() + this.getHeight();
   }

   @Generated
   public void setRenderer(OverlayElement.Renderer renderer) {
      this.renderer = renderer;
   }

   @Generated
   public void setPosX(float posX) {
      this.posX = posX;
   }

   @Generated
   public void setPosY(float posY) {
      this.posY = posY;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setScale(float scale) {
      this.scale = scale;
   }

   @Generated
   public void setShouldRender(boolean shouldRender) {
      this.shouldRender = shouldRender;
   }

   @Generated
   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   @Generated
   public float getPosX() {
      return this.posX;
   }

   @Generated
   public float getPosY() {
      return this.posY;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getScale() {
      return this.scale;
   }

   @Generated
   public boolean isShouldRender() {
      return this.shouldRender;
   }

   @Generated
   public boolean isFocused() {
      return this.focused;
   }

   public interface Renderer {
      void render();
   }
}
