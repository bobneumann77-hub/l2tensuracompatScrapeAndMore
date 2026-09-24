package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.client.screen.templates.IResetButton;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class SettingsButton extends SimpleButton implements IResetButton {
   private final Component optionText;
   private final SimpleButton resetButton;
   private final Supplier<Component> displayText;

   public SettingsButton(Component optionText, Supplier<Component> displayText, List<Component> tooltip, OnPress onPress, OnPress onReset) {
      super(0, 0, 100, 20, Component.empty(), tooltip, onPress);
      this.optionText = optionText;
      this.displayText = displayText;
      if (onReset == null) {
         this.resetButton = null;
      } else {
         this.resetButton = new SimpleButton(0, 0, 40, 20, Component.translatable("controls.reset").withColor(16711680), (Component)null, onReset);
      }
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mX, int mY, float pT) {
      if (this.resetButton == null) {
         this.setX(graphics.guiWidth() - 165);
      } else {
         int pX = graphics.guiWidth() - 180;
         this.setX(pX);
         this.resetButton.setX(pX + this.width + 10);
      }

      this.text = this.displayText.get();
      super.renderWidget(graphics, mX, mY, pT);
      graphics.drawString(Minecraft.getInstance().font, this.optionText, 20, this.getY() + 7, 16777215);
      if (this.resetButton != null) {
         this.resetButton.setY(this.getY());
         this.resetButton.render(graphics, mX, mY, pT);
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      if (super.mouseClicked(mX, mY, button)) {
         return true;
      } else {
         return this.resetButton != null ? this.resetButton.mouseClicked(mX, mY, button) : false;
      }
   }

   @Override
   public SimpleButton getResetButton() {
      return this.resetButton;
   }
}
