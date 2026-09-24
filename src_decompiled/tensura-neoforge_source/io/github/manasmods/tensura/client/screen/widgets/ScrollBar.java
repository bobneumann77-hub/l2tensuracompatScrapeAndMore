package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;

public class ScrollBar implements IScrollBar {
   private ResourceLocation scrollBarTexture = IScrollBar.TEXTURE;
   private float scrollOffset;
   private boolean scrolling;
   private boolean hasCustomScrollLogic;
   private int scrollBarX;
   private int scrollBarY;
   private int listStartIndex;
   private int scrollBarTotalSpace;
   private int scrollBarRenderCount;
   private int customScrollAmount = IScrollBar.super.getCustomScrollAmount();
   private int scrollBarWidth = IScrollBar.super.getScrollBarWidth();
   private int scrollBarHeight = IScrollBar.super.getScrollBarHeight();
   private int scrollBarTextureWidth = IScrollBar.super.getScrollBarTextureWidth();
   private int scrollBarTextureHeight = IScrollBar.super.getScrollBarTextureHeight();
   private Supplier<Boolean> scrollBarActive = () -> true;
   private Supplier<Integer> scrollBarListSize;

   public ScrollBar(int pX, int pY, int totalSpace, int renderCount, Supplier<Integer> listSize) {
      this.scrollBarX = pX;
      this.scrollBarY = pY;
      this.scrollBarTotalSpace = totalSpace;
      this.scrollBarRenderCount = renderCount;
      this.scrollBarListSize = listSize;
   }

   @Override
   public boolean hasCustomScrollLogic() {
      return this.isHasCustomScrollLogic();
   }

   @Override
   public int getScrollBarListSize() {
      return this.scrollBarListSize.get();
   }

   @Override
   public boolean isScrollBarActive() {
      return IScrollBar.super.isScrollBarActive() && this.scrollBarActive.get();
   }

   @Generated
   public void setScrollBarTexture(ResourceLocation scrollBarTexture) {
      this.scrollBarTexture = scrollBarTexture;
   }

   @Generated
   @Override
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   public void setHasCustomScrollLogic(boolean hasCustomScrollLogic) {
      this.hasCustomScrollLogic = hasCustomScrollLogic;
   }

   @Generated
   public void setScrollBarX(int scrollBarX) {
      this.scrollBarX = scrollBarX;
   }

   @Generated
   public void setScrollBarY(int scrollBarY) {
      this.scrollBarY = scrollBarY;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }

   @Generated
   public void setScrollBarTotalSpace(int scrollBarTotalSpace) {
      this.scrollBarTotalSpace = scrollBarTotalSpace;
   }

   @Generated
   public void setScrollBarRenderCount(int scrollBarRenderCount) {
      this.scrollBarRenderCount = scrollBarRenderCount;
   }

   @Generated
   public void setCustomScrollAmount(int customScrollAmount) {
      this.customScrollAmount = customScrollAmount;
   }

   @Generated
   public void setScrollBarWidth(int scrollBarWidth) {
      this.scrollBarWidth = scrollBarWidth;
   }

   @Generated
   public void setScrollBarHeight(int scrollBarHeight) {
      this.scrollBarHeight = scrollBarHeight;
   }

   @Generated
   public void setScrollBarTextureWidth(int scrollBarTextureWidth) {
      this.scrollBarTextureWidth = scrollBarTextureWidth;
   }

   @Generated
   public void setScrollBarTextureHeight(int scrollBarTextureHeight) {
      this.scrollBarTextureHeight = scrollBarTextureHeight;
   }

   @Generated
   public void setScrollBarActive(Supplier<Boolean> scrollBarActive) {
      this.scrollBarActive = scrollBarActive;
   }

   @Generated
   public void setScrollBarListSize(Supplier<Integer> scrollBarListSize) {
      this.scrollBarListSize = scrollBarListSize;
   }

   @Generated
   @Override
   public ResourceLocation getScrollBarTexture() {
      return this.scrollBarTexture;
   }

   @Generated
   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Generated
   public boolean isHasCustomScrollLogic() {
      return this.hasCustomScrollLogic;
   }

   @Generated
   @Override
   public int getScrollBarX() {
      return this.scrollBarX;
   }

   @Generated
   @Override
   public int getScrollBarY() {
      return this.scrollBarY;
   }

   @Generated
   public int getListStartIndex() {
      return this.listStartIndex;
   }

   @Generated
   @Override
   public int getScrollBarTotalSpace() {
      return this.scrollBarTotalSpace;
   }

   @Generated
   @Override
   public int getScrollBarRenderCount() {
      return this.scrollBarRenderCount;
   }

   @Generated
   @Override
   public int getCustomScrollAmount() {
      return this.customScrollAmount;
   }

   @Generated
   @Override
   public int getScrollBarWidth() {
      return this.scrollBarWidth;
   }

   @Generated
   @Override
   public int getScrollBarHeight() {
      return this.scrollBarHeight;
   }

   @Generated
   @Override
   public int getScrollBarTextureWidth() {
      return this.scrollBarTextureWidth;
   }

   @Generated
   @Override
   public int getScrollBarTextureHeight() {
      return this.scrollBarTextureHeight;
   }
}
