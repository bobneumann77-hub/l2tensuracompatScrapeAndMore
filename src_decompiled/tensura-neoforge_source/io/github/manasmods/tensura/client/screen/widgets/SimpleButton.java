package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.client.TensuraColors;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SimpleButton extends Button {
   public static final ResourceLocation DEFAULT = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/button.png");
   protected Supplier<Integer> additionalUvOffsetX;
   protected Supplier<Integer> additionalUvOffsetY;
   protected Supplier<Integer> overrideUvOffsetX;
   protected Supplier<Integer> overrideUvOffsetY;
   protected Component text;
   protected Component tooltip;
   protected ResourceLocation texture;
   protected Supplier<Boolean> uvOffsetCheck;
   protected Supplier<Boolean> clickedCheck;
   protected Supplier<Boolean> renderCheck;
   protected Supplier<Boolean> tooltipCheck;
   protected Supplier<Boolean> activeCheck;
   protected OnPress onPress;
   protected SimpleButton.OnPressWithButton onPressWithButton;
   protected final int textureWidth;
   protected final int textureHeight;

   public SimpleButton(int x, int y, int width, int height, ResourceLocation texture, Component text, Component tooltip, OnPress onPress) {
      this(x, y, width, height, width, height * 2, texture, text, tooltip, onPress);
   }

   public SimpleButton(int x, int y, int width, int height, ResourceLocation texture, Component text, Component tooltip, SimpleButton.OnPressWithButton onPress) {
      this(x, y, width, height, width, height * 2, texture, text, tooltip, onPress);
   }

   public SimpleButton(
      int x, int y, int width, int height, int textureWidth, int textureHeight, ResourceLocation texture, Component text, Component tooltip, OnPress onPress
   ) {
      this(x, y, width, height, textureWidth, textureHeight, texture, text, List.of((Component)(tooltip == null ? Component.empty() : tooltip)), onPress);
   }

   public SimpleButton(
      int x,
      int y,
      int width,
      int height,
      int textureWidth,
      int textureHeight,
      ResourceLocation texture,
      Component text,
      Component tooltip,
      SimpleButton.OnPressWithButton onPress
   ) {
      this(x, y, width, height, textureWidth, textureHeight, texture, text, List.of((Component)(tooltip == null ? Component.empty() : tooltip)), onPress);
   }

   public SimpleButton(
      int x,
      int y,
      int width,
      int height,
      int textureWidth,
      int textureHeight,
      ResourceLocation texture,
      Component text,
      List<Component> tooltip,
      OnPress onPress
   ) {
      super(x, y, width, height, text, onPress, supplier -> Component.empty());
      this.texture = texture;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.text = text;
      MutableComponent mutable = Component.empty();

      for (Component component : tooltip) {
         mutable.append(component);
         if (tooltip.indexOf(component) != tooltip.size() - 1) {
            mutable.append("\n\n");
         }
      }

      this.tooltip = mutable;
      this.renderCheck = () -> true;
      this.activeCheck = () -> true;
      this.tooltipCheck = this::isHovered;
      this.clickedCheck = this::isHovered;
      this.uvOffsetCheck = this::isHoveredOrFocused;
      this.onPress = onPress;
      this.setAdditionalUvOffset(0, 0);
   }

   public SimpleButton(
      int x,
      int y,
      int width,
      int height,
      int textureWidth,
      int textureHeight,
      ResourceLocation texture,
      Component text,
      List<Component> tooltip,
      SimpleButton.OnPressWithButton onPress
   ) {
      super(x, y, width, height, text, null, supplier -> Component.empty());
      this.texture = texture;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.text = text;
      MutableComponent mutable = Component.empty();

      for (Component component : tooltip) {
         mutable.append(component);
         if (tooltip.indexOf(component) != tooltip.size() - 1) {
            mutable.append("\n\n");
         }
      }

      this.tooltip = mutable;
      this.renderCheck = () -> true;
      this.activeCheck = () -> true;
      this.tooltipCheck = this::isHovered;
      this.clickedCheck = this::isHovered;
      this.uvOffsetCheck = this::isHoveredOrFocused;
      this.onPressWithButton = onPress;
      this.setAdditionalUvOffset(0, 0);
   }

   public SimpleButton(int x, int y, int width, int height, ResourceLocation texture, Component text, List<Component> tooltip, OnPress onPress) {
      this(x, y, width, height, width, height * 2, texture, text, tooltip, onPress);
   }

   public SimpleButton(
      int x, int y, int width, int height, ResourceLocation texture, Component text, List<Component> tooltip, SimpleButton.OnPressWithButton onPress
   ) {
      this(x, y, width, height, width, height * 2, texture, text, tooltip, onPress);
   }

   public SimpleButton(int x, int y, int width, int height, Component text, Component tooltip, OnPress onPress) {
      this(x, y, width, height, 200, 60, DEFAULT, text, tooltip, onPress);
   }

   public SimpleButton(int x, int y, int width, int height, Component text, Component tooltip, SimpleButton.OnPressWithButton onPress) {
      this(x, y, width, height, 200, 60, DEFAULT, text, tooltip, onPress);
   }

   public SimpleButton(int x, int y, int width, int height, Component text, List<Component> tooltip, OnPress onPress) {
      this(x, y, width, height, 200, 60, DEFAULT, text, tooltip, onPress);
   }

   public SimpleButton(int x, int y, int width, int height, Component text, List<Component> tooltip, SimpleButton.OnPressWithButton onPress) {
      this(x, y, width, height, 200, 60, DEFAULT, text, tooltip, onPress);
   }

   protected void renderWidget(GuiGraphics graphics, int mX, int mY, float pT) {
      if (this.renderCheck.get()) {
         if (this.text != null) {
            int k = this.isActive() ? 16777215 : 10526880;
            this.renderString(graphics, Minecraft.getInstance().font, k | Mth.ceil(this.alpha * 255.0F) << 24);
         }

         Screen screen = Minecraft.getInstance().screen;
         if (this.tooltip != null && !this.tooltip.equals(Component.empty()) && screen != null && this.tooltipCheck.get()) {
            screen.setTooltipForNextRenderPass(this.tooltip.copy().withColor(this.isActive() ? TensuraColors.getColor(this.tooltip) : 10526880));
         }

         float offsetX;
         if (this.overrideUvOffsetX != null) {
            offsetX = this.overrideUvOffsetX.get().intValue();
         } else {
            offsetX = this.additionalUvOffsetX.get().intValue();
         }

         float offsetY;
         if (this.overrideUvOffsetY != null) {
            offsetY = this.overrideUvOffsetY.get().intValue();
         } else if (this.texture == DEFAULT) {
            offsetY = (!this.isActive() ? 20 : (this.uvOffsetCheck.get() ? 40 : 0)) + this.additionalUvOffsetY.get();
         } else {
            offsetY = (this.uvOffsetCheck.get() ? this.height : 0.0F) + this.additionalUvOffsetY.get().intValue();
         }

         if (this.texture == DEFAULT) {
            graphics.blit(
               DEFAULT, this.getX(), this.getY(), this.width, this.height, offsetX, offsetY, this.textureWidth, 20, this.textureWidth, this.textureHeight
            );
         } else if (this.texture != null) {
            graphics.blit(this.texture, this.getX(), this.getY(), offsetX, offsetY, this.width, this.height, this.textureWidth, this.textureHeight);
         }
      }
   }

   public boolean mouseClicked(double mX, double mY, int button) {
      if (this.clickedCheck.get() && this.renderCheck.get()) {
         if (this.onPressWithButton != null) {
            this.onPressWithButton.onPress(this, button);
         } else {
            this.onClick(mX, mY);
         }

         return true;
      } else {
         return false;
      }
   }

   public void onClick(double mX, double mY) {
      if (this.onPress != null) {
         super.onClick(mX, mY);
      }
   }

   public void onPress() {
      if (this.onPress != null) {
         super.onPress();
      }
   }

   @NotNull
   public Component getMessage() {
      return this.text;
   }

   public boolean isActive() {
      return this.activeCheck.get() && this.renderCheck.get();
   }

   public void setAdditionalUvOffset(int additionalUvOffsetX, int additionalUvOffsetY) {
      this.setAdditionalUvOffset(() -> additionalUvOffsetX, () -> additionalUvOffsetY);
   }

   public void setAdditionalUvOffset(Supplier<Integer> additionalUvOffsetX, Supplier<Integer> additionalUvOffsetY) {
      this.additionalUvOffsetX = additionalUvOffsetX;
      this.additionalUvOffsetY = additionalUvOffsetY;
   }

   public void overrideUvOffset(int overrideUvOffsetX, int overrideUvOffsetY) {
      this.overrideUvOffset(() -> overrideUvOffsetX, () -> overrideUvOffsetY);
   }

   public void overrideUvOffset(Supplier<Integer> overrideUvOffsetX, Supplier<Integer> overrideUvOffsetY) {
      this.overrideUvOffsetX = overrideUvOffsetX;
      this.overrideUvOffsetY = overrideUvOffsetY;
   }

   @Generated
   public Supplier<Integer> getAdditionalUvOffsetX() {
      return this.additionalUvOffsetX;
   }

   @Generated
   public Supplier<Integer> getAdditionalUvOffsetY() {
      return this.additionalUvOffsetY;
   }

   @Generated
   public Supplier<Integer> getOverrideUvOffsetX() {
      return this.overrideUvOffsetX;
   }

   @Generated
   public Supplier<Integer> getOverrideUvOffsetY() {
      return this.overrideUvOffsetY;
   }

   @Generated
   public void setAdditionalUvOffsetX(Supplier<Integer> additionalUvOffsetX) {
      this.additionalUvOffsetX = additionalUvOffsetX;
   }

   @Generated
   public void setAdditionalUvOffsetY(Supplier<Integer> additionalUvOffsetY) {
      this.additionalUvOffsetY = additionalUvOffsetY;
   }

   @Generated
   public void setOverrideUvOffsetX(Supplier<Integer> overrideUvOffsetX) {
      this.overrideUvOffsetX = overrideUvOffsetX;
   }

   @Generated
   public void setOverrideUvOffsetY(Supplier<Integer> overrideUvOffsetY) {
      this.overrideUvOffsetY = overrideUvOffsetY;
   }

   @Generated
   public void setTooltip(Component tooltip) {
      this.tooltip = tooltip;
   }

   @Generated
   public void setUvOffsetCheck(Supplier<Boolean> uvOffsetCheck) {
      this.uvOffsetCheck = uvOffsetCheck;
   }

   @Generated
   public void setClickedCheck(Supplier<Boolean> clickedCheck) {
      this.clickedCheck = clickedCheck;
   }

   @Generated
   public void setRenderCheck(Supplier<Boolean> renderCheck) {
      this.renderCheck = renderCheck;
   }

   @Generated
   public void setTooltipCheck(Supplier<Boolean> tooltipCheck) {
      this.tooltipCheck = tooltipCheck;
   }

   @Generated
   public void setActiveCheck(Supplier<Boolean> activeCheck) {
      this.activeCheck = activeCheck;
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface OnPressWithButton {
      void onPress(SimpleButton var1, int var2);
   }
}
