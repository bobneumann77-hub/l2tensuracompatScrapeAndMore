package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class DialogueWindow {
   public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/dialogue/dialogue_window.png");
   public static final ResourceLocation DEFAULT_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/dialogue/button.png");
   protected final int WIDTH = 135;
   protected final int HEIGHT = 80;
   protected final int LEFTMOST;
   protected final int CENTER;
   protected final int RIGHTMOST;
   protected final int LEFTMOST_CENTER;
   protected final int RIGHTMOST_CENTER;
   protected final int guiLeft;
   protected final int guiTop;
   protected final int guiRight;
   protected final int guiBottom;
   protected final int guiWidth;
   protected final int guiHeight;
   protected final int screenWidth;
   protected final int screenHeight;
   protected final int x;
   protected final int y;
   protected final List<SimpleButton> buttons = new ArrayList<>();
   protected Runnable onClose = () -> {};
   protected boolean drawDarkenedBackground = true;
   protected boolean drawExitButton = true;
   protected boolean active;

   public DialogueWindow(int guiLeft, int guiTop, int guiWidth, int guiHeight, int screenWidth, int screenHeight) {
      this.guiWidth = guiWidth;
      this.guiHeight = guiHeight;
      this.guiLeft = guiLeft;
      this.guiTop = guiTop;
      this.guiRight = guiLeft + guiWidth;
      this.guiBottom = guiTop + guiHeight;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
      this.x = (screenWidth - 135) / 2;
      this.y = (screenHeight - 80) / 2;
      this.LEFTMOST = this.x + 5;
      this.CENTER = this.x + 47;
      this.RIGHTMOST = this.x + 89;
      this.LEFTMOST_CENTER = this.x + 25;
      this.RIGHTMOST_CENTER = this.x + 69;
   }

   public void render(GuiGraphics graphics, int mX, int mY, float pT) {
      if (this.drawDarkenedBackground) {
         graphics.fill(0, 0, this.screenWidth, this.screenHeight, TensuraColors.getARGB(0, 0.5F));
      }

      graphics.blit(BACKGROUND, this.x, this.y, 0, 0, 135, 80);

      for (SimpleButton button : this.buttons) {
         button.render(graphics, mX, mY, pT);
      }

      if (this.drawExitButton) {
         boolean hovered = RenderHelper.mouseOver(mX, mY, this.x + 114, this.x + 131, this.y + 4, this.y + 21);
         graphics.blit(BACKGROUND, this.x + 114, this.y + 4, 181.0F, hovered ? 17.0F : 0.0F, 17, 17, 256, 256);
      }
   }

   public void draw(GuiGraphics graphics, Font font, Component text, int pX, int pY, int width, int color) {
      graphics.drawWordWrap(font, FormattedText.of(text.getString()), this.x + pX, this.y + pY, width, color);
   }

   public void draw(GuiGraphics graphics, Font font, String text, int pX, int pY, int width, int color) {
      graphics.drawWordWrap(font, FormattedText.of(text), this.x + pX, this.y + pY, width, color);
   }

   public boolean mouseClicked(double mX, double mY, int mB) {
      if (this.drawExitButton && RenderHelper.mouseOver(mX, mY, this.x + 114, this.x + 131, this.y + 4, this.y + 21)) {
         this.onClose();
         ScreenHelper.clicked();
         return true;
      }

      for (SimpleButton button : this.buttons) {
         if (button.mouseClicked(mX, mY, mB)) {
            ScreenHelper.clicked();
            return true;
         }
      }

      return false;
   }

   public void addButton(SimpleButton... button) {
      this.buttons.addAll(List.of(button));
   }

   public void clearButtons() {
      this.buttons.clear();
   }

   public void onClose() {
      this.active = false;
      this.onClose.run();
   }

   @Generated
   public int getWIDTH() {
      return 135;
   }

   @Generated
   public int getHEIGHT() {
      return 80;
   }

   @Generated
   public int getLEFTMOST() {
      return this.LEFTMOST;
   }

   @Generated
   public int getCENTER() {
      return this.CENTER;
   }

   @Generated
   public int getRIGHTMOST() {
      return this.RIGHTMOST;
   }

   @Generated
   public int getLEFTMOST_CENTER() {
      return this.LEFTMOST_CENTER;
   }

   @Generated
   public int getRIGHTMOST_CENTER() {
      return this.RIGHTMOST_CENTER;
   }

   @Generated
   public int getX() {
      return this.x;
   }

   @Generated
   public int getY() {
      return this.y;
   }

   @Generated
   public void setOnClose(Runnable onClose) {
      this.onClose = onClose;
   }

   @Generated
   public boolean isDrawDarkenedBackground() {
      return this.drawDarkenedBackground;
   }

   @Generated
   public boolean isDrawExitButton() {
      return this.drawExitButton;
   }

   @Generated
   public void setDrawDarkenedBackground(boolean drawDarkenedBackground) {
      this.drawDarkenedBackground = drawDarkenedBackground;
   }

   @Generated
   public void setDrawExitButton(boolean drawExitButton) {
      this.drawExitButton = drawExitButton;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public void setActive(boolean active) {
      this.active = active;
   }
}
