package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.client.screen.SettingsScreen;
import io.github.manasmods.tensura.client.screen.templates.IResetButton;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SettingsEditBox extends ExpandedEditBox implements IResetButton {
   private final Component optionText;
   private final Supplier<String> value;
   private final SimpleButton resetButton;
   private final SettingsEditBox.OnPress onPress;
   private final boolean centerOnFocus;
   private int guiWidth;
   private int guiHeight;

   public SettingsEditBox(
      Font font,
      Supplier<String> value,
      Component optionText,
      List<Component> tooltip,
      Consumer<String> responder,
      SettingsEditBox.OnPress onPress,
      net.minecraft.client.gui.components.Button.OnPress onReset,
      boolean centerOnFocus
   ) {
      super(font, 0, 0, 100, 20, tooltip);
      this.value = value;
      this.onPress = onPress;
      this.optionText = optionText;
      this.centerOnFocus = centerOnFocus;
      this.setResponder(responder);
      this.setValue(value.get());
      if (onReset == null) {
         this.resetButton = null;
      } else {
         this.resetButton = new SimpleButton(0, 0, 40, 20, Component.translatable("controls.reset").withColor(16711680), (Component)null, onReset);
      }
   }

   @Override
   public void renderWidget(GuiGraphics guiGraphics, int mX, int mY, float pT) {
      this.guiWidth = guiGraphics.guiWidth();
      this.guiHeight = guiGraphics.guiHeight();
      if (this.centerOnFocus && this.isFocused()) {
         super.renderWidget(guiGraphics, mX, mY, pT);
      } else {
         if (this.resetButton == null) {
            this.setX(this.guiWidth - 165);
         } else {
            int pX = this.guiWidth - 180;
            this.setX(pX);
            this.resetButton.setX(pX + this.width + 10);
         }

         super.renderWidget(guiGraphics, mX, mY, pT);
         guiGraphics.drawString(Minecraft.getInstance().font, this.optionText, 20, this.getY() + 7, 16777215);
         if (this.resetButton != null) {
            this.resetButton.setY(this.getY());
            this.resetButton.render(guiGraphics, mX, mY, pT);
         }
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      this.setFocused(this.isHovered());
      if (this.centerOnFocus) {
         if (this.isFocused()) {
            SettingsScreen.setInputEditBox(this);
            SettingsScreen.getInputEditBox().setX(this.guiWidth / 2 - this.width / 2);
            SettingsScreen.getInputEditBox().setY(this.guiHeight / 2 - this.height / 2);
            return true;
         }

         if (SettingsScreen.getInputEditBox() == this) {
            SettingsScreen.setInputEditBox(null);
         }
      }

      if (this.isHovered()) {
         if (this.onPress != null) {
            this.onPress.action(this);
         }

         return true;
      } else if (this.resetButton.mouseClicked(mX, mY, button)) {
         this.setValue(this.value.get());
         return false;
      } else {
         return super.mouseClicked(mX, mY, button);
      }
   }

   @Override
   public SimpleButton getResetButton() {
      return this.resetButton;
   }

   @FunctionalInterface
   public interface OnPress {
      void action(SettingsEditBox var1);
   }
}
