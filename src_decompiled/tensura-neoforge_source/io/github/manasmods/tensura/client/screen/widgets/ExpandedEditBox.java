package io.github.manasmods.tensura.client.screen.widgets;

import io.github.manasmods.tensura.util.client.RenderHelper;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ExpandedEditBox extends EditBox {
   private int maxLength;
   protected Component tooltip;
   private boolean canBeEdited = true;
   protected List<String> suggestions;
   protected String suggestionsPrefix;
   protected Supplier<Boolean> shouldRenderMultipleSuggestions;
   protected Supplier<Boolean> tooltipCheck;
   protected Consumer<Boolean> onFocused;
   protected Consumer<Component> onTooltipChanged;
   protected RenderHelper.RenderAction renderAction = (graphics, mouseX, mouseY) -> {};
   protected Supplier<Boolean> isVisible;
   protected Supplier<Boolean> isFocused = () -> false;

   public ExpandedEditBox(Font font, int pX, int pY, int width, int height, String tooltip) {
      this(font, pX, pY, width, height, Component.literal(tooltip));
   }

   public ExpandedEditBox(Font font, int pX, int pY, int width, int height, Component tooltip) {
      this(font, pX, pY, width, height, List.of(tooltip));
   }

   public ExpandedEditBox(Font font, int pX, int pY, int width, int height, List<Component> tooltip) {
      super(font, pX, pY, width, height, Component.empty());
      MutableComponent mutable = Component.empty();

      for (Component component : tooltip) {
         mutable.append(component);
         if (tooltip.indexOf(component) != tooltip.size() - 1) {
            mutable.append("\n\n");
         }
      }

      this.tooltip = mutable;
      this.isVisible = () -> true;
      this.tooltipCheck = this::isHovered;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mX, int mY, float pT) {
      super.renderWidget(guiGraphics, mX, mY, pT);
      if (this.isVisible()) {
         this.renderAction.run(guiGraphics, mX, mY);
         Screen screen = Minecraft.getInstance().screen;
         if (this.tooltip != null && !this.tooltip.getString().isEmpty() && !this.tooltip.getString().isBlank() && screen != null && this.tooltipCheck.get()) {
            screen.setTooltipForNextRenderPass(this.tooltip);
         }

         if (this.suggestions != null && this.shouldRenderMultipleSuggestions != null) {
            if (this.isFocused() && this.isActive() && this.shouldRenderMultipleSuggestions.get() && !this.suggestions.isEmpty()) {
               int prefixEnd = this.suggestionsPrefix == null ? 0 : this.suggestionsPrefix.length();
               if (!this.suggestions.contains(this.getValue().substring(prefixEnd))) {
                  int pY = this.getY() + 9;

                  for (String suggestion : this.suggestions) {
                     if (suggestion.startsWith(this.getValue().substring(prefixEnd))) {
                        guiGraphics.fill(this.getX(), pY, this.getX() + this.maxLength + 1, pY + 8, -14342875);
                        guiGraphics.drawString(this.font, suggestion, this.getX(), pY, -8026747);
                        pY += 8;
                     }
                  }
               }
            }
         }
      }
   }

   public void setMultipleSuggestions(List<Component> suggestions) {
      this.setMultipleSuggestions(suggestions, "");
   }

   public void setMultipleSuggestions(List<Component> suggestions, String prefix) {
      this.setMultipleSuggestions((Collection<String>)suggestions.stream().<String>map(Component::getString).toList(), prefix);
   }

   public void setMultipleSuggestions(Collection<String> suggestions) {
      this.setMultipleSuggestions(suggestions, "");
   }

   public void setMultipleSuggestions(Collection<String> suggestions, String prefix) {
      this.suggestionsPrefix = prefix;
      this.suggestions = suggestions.stream().toList();
      this.maxLength = 0;

      for (String suggestion : suggestions) {
         int len = this.font.width(suggestion);
         if (len > this.maxLength) {
            this.maxLength = len;
         }
      }

      if (this.shouldRenderMultipleSuggestions == null) {
         this.shouldRenderMultipleSuggestions = () -> true;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.isVisible() && this.isFocused() ? super.keyPressed(keyCode, scanCode, modifiers) : false;
   }

   public boolean mouseClicked(double mX, double mY, int button) {
      if (!this.canBeEdited) {
         return false;
      }

      boolean visible = this.isVisible();
      this.setFocused(visible && this.isHovered());
      return !visible ? false : super.mouseClicked(mX, mY, button);
   }

   public void setVisible(Supplier<Boolean> value) {
      this.isVisible = value;
   }

   public void setFocused(Supplier<Boolean> value) {
      this.isFocused = value;
      if (this.onFocused != null) {
         this.onFocused.accept(value.get());
      }
   }

   public void setVisible(boolean value) {
      this.isVisible = () -> value;
   }

   public void setFocused(boolean value) {
      this.isFocused = () -> value;
      if (this.onFocused != null) {
         this.onFocused.accept(value);
      }
   }

   public boolean isVisible() {
      return this.isVisible.get();
   }

   public boolean isFocused() {
      return this.isFocused.get();
   }

   public void setTooltip(String tooltip) {
      this.setTooltip(Component.literal(tooltip));
   }

   public void setTooltip(Component tooltip) {
      this.tooltip = tooltip;
      if (this.onTooltipChanged != null) {
         this.onTooltipChanged.accept(tooltip);
      }
   }

   public void setEditable(boolean bl) {
      super.setEditable(bl);
      this.canBeEdited = bl;
   }

   @Generated
   public String getSuggestionsPrefix() {
      return this.suggestionsPrefix;
   }

   @Generated
   public void setSuggestionsPrefix(String suggestionsPrefix) {
      this.suggestionsPrefix = suggestionsPrefix;
   }

   @Generated
   public Supplier<Boolean> getShouldRenderMultipleSuggestions() {
      return this.shouldRenderMultipleSuggestions;
   }

   @Generated
   public Supplier<Boolean> getTooltipCheck() {
      return this.tooltipCheck;
   }

   @Generated
   public void setShouldRenderMultipleSuggestions(Supplier<Boolean> shouldRenderMultipleSuggestions) {
      this.shouldRenderMultipleSuggestions = shouldRenderMultipleSuggestions;
   }

   @Generated
   public void setTooltipCheck(Supplier<Boolean> tooltipCheck) {
      this.tooltipCheck = tooltipCheck;
   }

   @Generated
   public void setOnFocused(Consumer<Boolean> onFocused) {
      this.onFocused = onFocused;
   }

   @Generated
   public void setOnTooltipChanged(Consumer<Component> onTooltipChanged) {
      this.onTooltipChanged = onTooltipChanged;
   }

   @Generated
   public void setRenderAction(RenderHelper.RenderAction renderAction) {
      this.renderAction = renderAction;
   }
}
