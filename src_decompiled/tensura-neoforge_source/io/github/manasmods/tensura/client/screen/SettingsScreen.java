package io.github.manasmods.tensura.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.templates.SettingsOptions;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.client.screen.widgets.OverlayElement;
import io.github.manasmods.tensura.client.screen.widgets.SettingsButton;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.config.client.HudConfig;
import io.github.manasmods.tensura.config.client.MenuConfig;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.handler.client.OverlayHandler;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SettingsScreen extends SimpleScreen {
   private final ResourceLocation SCROLL_BAR_AREA = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/scroll_bar_area.png");
   private final ImmutableSet<SettingsOptions.Category> categories;
   private final int totalSize;
   private static HudConfig hudConfig;
   private static MenuConfig menuConfig;
   private static MiscClientConfig miscConfig;
   private static ExpandedEditBox scaleEditBox;
   private static EditBox inputEditBox;
   private float grabOffsetX;
   private float grabOffsetY;
   private int translateY;
   private int scissorTop;
   private int scissorBottom;
   private boolean draggingElement;
   private boolean draggingScrollbar;
   private boolean hoveredScrollbar;
   private static int editingElement = -1;
   private static boolean editingMode;
   private static int previewElement = -1;

   public SettingsScreen() {
      super(Component.empty(), 0, 0);
      scaleEditBox = new ExpandedEditBox(this.font, 0, 0, 100, 20, Component.empty());
      scaleEditBox.setResponder(s -> {
         try {
            float value = Float.parseFloat(s);
            OverlayHandler.saveScaleById(editingElement, value);
         } catch (NumberFormatException var2x) {
         }
      });
      scaleEditBox.setVisible(() -> editingElement >= 0);
      ImmutableMap<SettingsOptions.Category, ImmutableList<AbstractWidget>> map = SettingsOptions.getAllOptions();
      this.categories = map.keySet();
      int totalHeight = 5;
      totalHeight += 9 * this.categories.size() + 15 * this.categories.size();
      UnmodifiableIterator var3 = map.values().iterator();

      while (var3.hasNext()) {
         ImmutableList<AbstractWidget> option = (ImmutableList<AbstractWidget>)var3.next();
         totalHeight += option.size() * 20 + (option.size() - 1) * 10;
      }

      this.totalSize = totalHeight;
      blurStrength = 5;
      this.shouldRenderWidgets = false;
   }

   @Override
   protected void init() {
      this.imageWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
      this.imageHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
      super.init();
      scaleEditBox.setX(guiCenterX - 50);
      scaleEditBox.setY(guiCenterY - 10);
      this.draggingScrollbar = false;
      SimpleButton save = new SimpleButton(
         guiLeft + 120, guiBottom - 22, 100, 20, Component.translatable("tensura.settings.save"), (Component)null, onPress -> SettingsOptions.saveAll()
      );
      SimpleButton reset = new SimpleButton(
         guiLeft + 10,
         guiBottom - 22,
         100,
         20,
         Component.translatable("tensura.settings.reset").withColor(16711680),
         (Component)null,
         onPress -> SettingsOptions.resetAll()
      );
      SimpleButton exit = new SimpleButton(
         guiRight - 110, guiBottom - 22, 100, 20, Component.translatable("tooltip.tensura.return"), (Component)null, onPress -> {
            SettingsOptions.saveAll();
            ScreenHelper.openScreen(4);
         }
      );
      this.addRenderableWidget(save);
      this.addRenderableWidget(reset);
      this.addRenderableWidget(exit);
      this.scissorTop = guiTop + 25;
      this.scissorBottom = guiBottom - 25;
      float maxScroll = Math.max(0, this.totalSize + 50 - (guiBottom - guiTop));
      this.translateY = (int)Math.clamp(this.translateY, -maxScroll, 0.0F);
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      if (editingMode) {
         blurStrength = 0;
         this.highlightElement(graphics);
         scaleEditBox.render(graphics, mX, mY, partialTick);
      } else if (inputEditBox != null) {
         blurStrength = 0;
         inputEditBox.render(graphics, mX, mY, partialTick);
      } else {
         int color = TensuraColors.getARGB(0, 0.4F);
         int posY = this.getScrollbarY();
         this.hoveredScrollbar = RenderHelper.mouseOver(mX, mY, guiRight - 20, guiRight - 10, posY, posY + 13);
         graphics.fill(0, 0, guiRight, guiBottom, color);
         graphics.fill(0, 0, guiRight, guiTop + 25, color);
         graphics.fill(0, guiBottom - 25, guiRight, guiBottom, color);
         super.renderWidgets(graphics, mX, mY, partialTick);
         graphics.enableScissor(0, this.scissorTop, guiRight, this.scissorBottom);
         this.renderWidgets(graphics, mX, mY, partialTick);
         graphics.disableScissor();
         graphics.blit(this.SCROLL_BAR_AREA, guiRight - 21, guiTop + 28, 12, guiBottom - guiTop - 56, 0.0F, 0.0F, 12, 256, 12, 256);
         graphics.blit(IScrollBar.TEXTURE, guiRight - 20, posY, 0.0F, this.hoveredScrollbar ? 13.0F : 0.0F, 10, 13, 10, 26);
      }
   }

   @Override
   public void renderWidgets(GuiGraphics graphics, int mX, int mY, float partialTick) {
      int posY = guiTop + 30 + this.translateY;
      UnmodifiableIterator var6 = this.categories.iterator();

      while (var6.hasNext()) {
         SettingsOptions.Category category = (SettingsOptions.Category)var6.next();
         RenderHelper.drawCenteredText(graphics, this.font, category.getHeader(), guiCenterX, posY, 16777215, false);
         posY += 15;
         UnmodifiableIterator var8 = SettingsOptions.getOptionsFromCategory(category).iterator();

         while (var8.hasNext()) {
            AbstractWidget option = (AbstractWidget)var8.next();
            option.setY(posY);
            posY += 30;
            if (option.getY() <= this.scissorBottom && option.getY() + option.getHeight() >= this.scissorTop) {
               if (option instanceof EditBox box && box.font == null) {
                  box.font = this.font;
               }

               option.render(graphics, mX, mY, partialTick);
            }
         }
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      if (inputEditBox != null) {
         if (!inputEditBox.isHovered()) {
            blurStrength = 5;
            inputEditBox.setFocused(false);
            inputEditBox = null;
         }

         return true;
      } else if (editingMode && button == 0) {
         if (scaleEditBox.mouseClicked(mX, mY, button)) {
            return true;
         }

         OverlayElement element = OverlayHandler.getElementById(editingElement);
         if (element == null) {
            return true;
         }

         if (element.isHovered(mX, mY)) {
            this.draggingElement = true;
            float[] positions = OverlayHandler.getPositionsById(editingElement);
            if (positions != null) {
               this.grabOffsetX = (float)(mX - positions[0]);
               this.grabOffsetY = (float)(mY - positions[1]);
            }
         }

         return true;
      } else {
         if (this.hoveredScrollbar && button == 0) {
            this.draggingScrollbar = true;
            this.grabOffsetY = (float)(mY - this.getScrollbarY());
            return true;
         }

         if (super.mouseClicked(mX, mY, button)) {
            return true;
         }

         UnmodifiableIterator element = this.categories.iterator();

         while (element.hasNext()) {
            SettingsOptions.Category category = (SettingsOptions.Category)element.next();
            UnmodifiableIterator var8 = SettingsOptions.getOptionsFromCategory(category).iterator();

            while (var8.hasNext()) {
               AbstractWidget widget = (AbstractWidget)var8.next();
               if (widget.getY() <= this.scissorBottom && widget.mouseClicked(mX, mY, button)) {
                  this.setFocused(widget);
                  if (button == 0) {
                     this.setDragging(true);
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean mouseReleased(double mX, double mY, int button) {
      if (editingMode && button == 0) {
         this.draggingElement = false;
      } else if (this.draggingScrollbar) {
         this.draggingScrollbar = false;
      }

      return scaleEditBox.mouseReleased(mX, mY, button) ? true : super.mouseReleased(mX, mY, button);
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      if (this.draggingScrollbar) {
         int minY = guiTop + 29;
         int maxY = guiBottom - 42;
         int maxScroll = Math.max(0, this.totalSize + 50 - (guiBottom - guiTop));
         int newY = (int)Math.clamp(mY - this.grabOffsetY, minY, maxY);
         this.translateY = (int)Mth.map(newY, maxY, minY, -maxScroll, 0.0F);
         return true;
      }

      if (this.draggingElement && button == 0) {
         OverlayElement element = OverlayHandler.getElementById(editingElement);
         if (element == null) {
            return false;
         }

         float[] positions = OverlayHandler.getPositionsById(editingElement);
         if (positions == null) {
            return false;
         }

         float xOffset = 0.0F;
         if (!OverlayHandler.isLeftSide(editingElement)) {
            if (editingElement == 2) {
               xOffset = 117.0F;
            }

            xOffset += element.getWidth() / element.getScale() * (element.getScale() - 1.0F);
         }

         float newX = (float)Math.clamp(mX - this.grabOffsetX, 0.0, OverlayHandler.getScreenWidth() - element.getWidth() + xOffset);
         float newY = (float)Math.clamp(mY - this.grabOffsetY, 0.0, OverlayHandler.getScreenHeight() - element.getHeight());
         positions[0] = newX;
         positions[1] = newY;
         OverlayHandler.savePositionById(editingElement);
         return true;
      } else {
         return super.mouseDragged(mX, mY, button, dragX, dragY);
      }
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      if (this.draggingScrollbar) {
         return false;
      }

      int maxScroll = Math.max(0, this.totalSize + 50 - (guiBottom - guiTop));
      this.translateY = this.translateY + (int)Math.clamp(deltaY * 8.0, -8.0, 8.0);
      this.translateY = Math.clamp(this.translateY, -maxScroll, 0);
      return true;
   }

   private int getScrollbarY() {
      int minY = guiTop + 29;
      int maxY = guiBottom - 42;
      int maxScroll = Math.max(0, this.totalSize + 50 - (guiBottom - guiTop));
      return (int)Mth.map(this.translateY, -maxScroll, 0.0F, maxY, minY);
   }

   private void highlightElement(GuiGraphics guiGraphics) {
      OverlayElement element = OverlayHandler.getElementById(editingElement);
      if (element != null) {
         float x1 = element.getPosX();
         float x2 = element.getRight();
         boolean isLeftSide = OverlayHandler.isLeftSide(editingElement);
         if (!isLeftSide) {
            float subtract = element.getWidth() / element.getScale() * (element.getScale() - 1.0F);
            x1 -= subtract;
            x2 -= subtract;
         }

         if (this.draggingElement) {
            RenderHelper.highlightArea(guiGraphics, x1, element.getPosY(), x2, element.getBottom(), 16777215, 0.5F);
         } else {
            RenderHelper.highlightArea(guiGraphics, x1, element.getPosY(), x2, element.getBottom());
         }
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return scaleEditBox.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char character, int i) {
      return scaleEditBox.charTyped(character, i) ? true : super.charTyped(character, i);
   }

   @Override
   public void onClose() {
      if (editingMode) {
         blurStrength = 5;
         editingElement = -1;
         previewElement = -1;
         editingMode = false;
         scaleEditBox.setFocused(false);
      } else if (inputEditBox != null) {
         blurStrength = 5;
         inputEditBox.setFocused(false);
         inputEditBox = null;
         previewElement = -1;
      } else {
         SettingsOptions.saveAll();
         super.onClose();
      }
   }

   public static void loadSettings() {
      hudConfig = (HudConfig)ConfigRegistry.getConfig(HudConfig.class);
      menuConfig = (MenuConfig)ConfigRegistry.getConfig(MenuConfig.class);
      miscConfig = (MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class);
      String key = "tensura.settings.tensuraHud";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.tensuraHud,
            getDescription(key, 2),
            b -> hudConfig.tensuraHud = !hudConfig.tensuraHud,
            b -> hudConfig.tensuraHud = true
         ),
         SettingsOptions.BuiltinCategories.GLOBAL.getCategory()
      );
      key = "tensura.settings.vanillaHud";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.vanillaHud,
            getDescription(key, 2),
            b -> hudConfig.vanillaHud = !hudConfig.vanillaHud,
            b -> hudConfig.vanillaHud = false
         ),
         SettingsOptions.BuiltinCategories.GLOBAL.getCategory()
      );
      key = "tensura.settings.modifyFov";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> menuConfig.modifyFov,
            getDescription(key, 2),
            b -> menuConfig.modifyFov = !menuConfig.modifyFov,
            b -> menuConfig.modifyFov = true
         ),
         SettingsOptions.BuiltinCategories.MENU.getCategory()
      );
      key = "tensura.settings.fadeEffects";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> menuConfig.fadeEffects,
            getDescription(key, 2),
            b -> menuConfig.fadeEffects = !menuConfig.fadeEffects,
            b -> menuConfig.fadeEffects = true
         ),
         SettingsOptions.BuiltinCategories.MENU.getCategory()
      );
      key = "tensura.settings.scale";
      SettingsOptions.addOption(
         new SettingsOptions.InputOption(
            null, () -> String.valueOf(menuConfig.scale), Component.translatable(key).withStyle(ChatFormatting.RED), getDescription(key, 2), s -> {
               if (canParseFloat(s)) {
                  menuConfig.scale = Float.parseFloat(s);
               }
            }, b -> menuConfig.scale = 1.0F, false
         ),
         SettingsOptions.BuiltinCategories.MENU.getCategory()
      );
      key = "tensura.settings.blur";
      SettingsOptions.addOption(
         new SettingsOptions.InputOption(null, () -> String.valueOf(menuConfig.blurStrength), Component.translatable(key), getDescription(key, 3), s -> {
            if (canParseInt(s)) {
               menuConfig.blurStrength = Math.clamp(Integer.parseInt(s), 0, 5);
            }
         }, b -> menuConfig.blurStrength = 1, false), SettingsOptions.BuiltinCategories.MENU.getCategory()
      );
      key = "tensura.settings.autoAbilitySlot";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> menuConfig.autoAbilitySlot,
            getDescription(key, 3),
            b -> menuConfig.autoAbilitySlot = !menuConfig.autoAbilitySlot,
            b -> menuConfig.autoAbilitySlot = false
         ),
         SettingsOptions.BuiltinCategories.MENU.getCategory()
      );
      key = "tensura.settings.cameraShakeStrength";
      SettingsOptions.addOption(
         new SettingsOptions.InputOption(
            null, () -> String.valueOf(miscConfig.cameraShakeStrength), Component.translatable(key), getDescription(key, 2), s -> {
               if (canParseFloat(s)) {
                  miscConfig.cameraShakeStrength = Float.parseFloat(s);
               }
            }, b -> miscConfig.cameraShakeStrength = 1.0F, false
         ),
         SettingsOptions.BuiltinCategories.MISC.getCategory()
      );
      key = "tensura.settings.arachnophobia";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> miscConfig.arachnophobia,
            getDescription(key, 2),
            b -> miscConfig.arachnophobia = !miscConfig.arachnophobia,
            b -> miscConfig.arachnophobia = false
         ),
         SettingsOptions.BuiltinCategories.MISC.getCategory()
      );
      key = "tensura.settings.dinnerbone";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> miscConfig.dinnerboneNameFlip,
            getDescription(key, 2),
            b -> miscConfig.dinnerboneNameFlip = !miscConfig.dinnerboneNameFlip,
            b -> miscConfig.dinnerboneNameFlip = false
         ),
         SettingsOptions.BuiltinCategories.MISC.getCategory()
      );
      key = "tensura.settings.title_screen";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> miscConfig.tensuraTitleScreen,
            getDescription(key, 2),
            b -> miscConfig.tensuraTitleScreen = !miscConfig.tensuraTitleScreen,
            b -> miscConfig.tensuraTitleScreen = false
         ),
         SettingsOptions.BuiltinCategories.MISC.getCategory()
      );
      key = "tensura.settings.renderStatus";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.status.render,
            getDescription(key, 2),
            b -> hudConfig.status.render = !hudConfig.status.render,
            b -> hudConfig.status.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS.getCategory()
      );
      key = "tensura.settings.defaultStatus";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.status.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.status.defaultRendering = !hudConfig.status.defaultRendering,
            b -> hudConfig.status.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS.getCategory()
      );
      key = "tensura.settings.sideStatus";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.status.side),
            getDescription(key, 4),
            b -> hudConfig.status.side = (hudConfig.status.side + 1) % 3,
            b -> hudConfig.status.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS.getCategory()
      );
      key = "tensura.settings.editStatus";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.status.defaultRendering = false;
            editingElement = 0;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(hudConfig.status.scale));
         }, b -> {
            hudConfig.status.positionX = 0.0F;
            hudConfig.status.positionY = 0.0F;
            hudConfig.status.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_STATUS.getCategory()
      );
      key = "tensura.settings.renderStatusBars";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.statusBars.render,
            getDescription(key, 2),
            b -> hudConfig.statusBars.render = !hudConfig.statusBars.render,
            b -> hudConfig.statusBars.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS_BARS.getCategory()
      );
      key = "tensura.settings.defaultStatusBars";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.statusBars.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.statusBars.defaultRendering = !hudConfig.statusBars.defaultRendering,
            b -> hudConfig.statusBars.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS_BARS.getCategory()
      );
      key = "tensura.settings.sideStatusBars";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.statusBars.side),
            getDescription(key, 4),
            b -> hudConfig.statusBars.side = (hudConfig.statusBars.side + 1) % 3,
            b -> hudConfig.statusBars.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_STATUS_BARS.getCategory()
      );
      key = "tensura.settings.editStatusBars";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.statusBars.defaultRendering = false;
            editingElement = 1;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.statusBars.positionX = 0.0F;
            hudConfig.statusBars.positionY = 0.0F;
            hudConfig.statusBars.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_STATUS_BARS.getCategory()
      );
      key = "tensura.settings.renderAbilities";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.abilities.render,
            getDescription(key, 2),
            b -> hudConfig.abilities.render = !hudConfig.abilities.render,
            b -> hudConfig.abilities.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_ABILITIES.getCategory()
      );
      key = "tensura.settings.defaultAbilities";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.abilities.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.abilities.defaultRendering = !hudConfig.abilities.defaultRendering,
            b -> hudConfig.abilities.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_ABILITIES.getCategory()
      );
      key = "tensura.settings.sideAbilities";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.abilities.side),
            getDescription(key, 4),
            b -> hudConfig.abilities.side = (hudConfig.abilities.side + 1) % 3,
            b -> hudConfig.abilities.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_ABILITIES.getCategory()
      );
      key = "tensura.settings.editAbilities";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.abilities.defaultRendering = false;
            editingElement = 2;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.abilities.positionX = 0.0F;
            hudConfig.abilities.positionY = 0.0F;
            hudConfig.abilities.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_ABILITIES.getCategory()
      );
      key = "tensura.settings.renderAnalysis";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.analysis.render,
            getDescription(key, 2),
            b -> hudConfig.analysis.render = !hudConfig.analysis.render,
            b -> hudConfig.analysis.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory()
      );
      key = "tensura.settings.defaultAnalysis";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.analysis.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.analysis.defaultRendering = !hudConfig.analysis.defaultRendering,
            b -> hudConfig.analysis.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory()
      );
      key = "tensura.settings.heartsAnalysis";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.analysis.useHearts,
            getDescription(key, 2),
            b -> hudConfig.analysis.useHearts = !hudConfig.analysis.useHearts,
            b -> hudConfig.analysis.useHearts = false
         ),
         SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory()
      );
      key = "tensura.settings.sideAnalysis";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.analysis.side),
            getDescription(key, 4),
            b -> hudConfig.analysis.side = (hudConfig.analysis.side + 1) % 3,
            b -> hudConfig.analysis.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory()
      );
      key = "tensura.settings.editAnalysis";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.analysis.defaultRendering = false;
            editingElement = 3;
            previewElement = editingElement;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.analysis.positionX = 0.0F;
            hudConfig.analysis.positionY = 0.0F;
            hudConfig.analysis.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory()
      );
      key = "tensura.settings.opacityAnalysis";
      SettingsOptions.InputOption option = new SettingsOptions.InputOption(
         null, () -> String.valueOf(hudConfig.analysis.opacity), Component.translatable(key), getDescription(key, 3), s -> {
            if (canParseFloat(s)) {
               hudConfig.analysis.opacity = Math.clamp(Float.parseFloat(s), 0.0F, 1.0F);
            }
         }, b -> hudConfig.analysis.opacity = 1.0F, true
      );
      option.setOnFocused(focused -> {
         if (focused) {
            previewElement = 3;
         } else {
            previewElement = -1;
         }
      });
      SettingsOptions.addOption(option, SettingsOptions.BuiltinCategories.HUD_ANALYSIS.getCategory());
      key = "tensura.settings.renderAirDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.air.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.air.render = !hudConfig.decorations.air.render,
            b -> hudConfig.decorations.air.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultAirDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.air.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.air.defaultRendering = !hudConfig.decorations.air.defaultRendering,
            b -> hudConfig.decorations.air.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideAirDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.air.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.air.side = (hudConfig.decorations.air.side + 1) % 3,
            b -> hudConfig.decorations.air.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editAirDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.air.defaultRendering = false;
            editingElement = 6;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.air.positionX = 0.0F;
            hudConfig.decorations.air.positionY = 0.0F;
            hudConfig.decorations.armor.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.renderFoodDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.food.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.food.render = !hudConfig.decorations.food.render,
            b -> hudConfig.decorations.food.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultFoodDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.food.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.food.defaultRendering = !hudConfig.decorations.food.defaultRendering,
            b -> hudConfig.decorations.food.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideFoodDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.food.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.food.side = (hudConfig.decorations.food.side + 1) % 3,
            b -> hudConfig.decorations.food.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editFoodDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.food.defaultRendering = false;
            editingElement = 7;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.food.positionX = 0.0F;
            hudConfig.decorations.food.positionY = 0.0F;
            hudConfig.decorations.food.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.renderArmorDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.armor.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.armor.render = !hudConfig.decorations.armor.render,
            b -> hudConfig.decorations.armor.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultArmorDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.armor.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.armor.defaultRendering = !hudConfig.decorations.armor.defaultRendering,
            b -> hudConfig.decorations.armor.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideArmorDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.armor.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.armor.side = (hudConfig.decorations.armor.side + 1) % 3,
            b -> hudConfig.decorations.armor.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editArmorDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.armor.defaultRendering = false;
            editingElement = 4;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.armor.positionX = 0.0F;
            hudConfig.decorations.armor.positionY = 0.0F;
            hudConfig.decorations.armor.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.renderBarrierDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.barrier.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.barrier.render = !hudConfig.decorations.barrier.render,
            b -> hudConfig.decorations.barrier.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultBarrierDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.barrier.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.barrier.defaultRendering = !hudConfig.decorations.barrier.defaultRendering,
            b -> hudConfig.decorations.barrier.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideBarrierDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.barrier.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.barrier.side = (hudConfig.decorations.barrier.side + 1) % 3,
            b -> hudConfig.decorations.barrier.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editBarrierDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.barrier.defaultRendering = false;
            editingElement = 5;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.barrier.positionX = 0.0F;
            hudConfig.decorations.barrier.positionY = 0.0F;
            hudConfig.decorations.barrier.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.renderMountHpDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.mountHp.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.mountHp.render = !hudConfig.decorations.mountHp.render,
            b -> hudConfig.decorations.mountHp.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultMountHpDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.mountHp.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.mountHp.defaultRendering = !hudConfig.decorations.mountHp.defaultRendering,
            b -> hudConfig.decorations.mountHp.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideMountHpDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.mountHp.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.mountHp.side = (hudConfig.decorations.mountHp.side + 1) % 3,
            b -> hudConfig.decorations.mountHp.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editMountHpDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.mountHp.defaultRendering = false;
            editingElement = 8;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.mountHp.positionX = 0.0F;
            hudConfig.decorations.mountHp.positionY = 0.0F;
            hudConfig.decorations.mountHp.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.renderMountShpDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.mountSpiritualHp.render,
            getDescription(key, 2),
            b -> hudConfig.decorations.mountSpiritualHp.render = !hudConfig.decorations.mountSpiritualHp.render,
            b -> hudConfig.decorations.mountSpiritualHp.render = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.defaultMountShpDeco";
      SettingsOptions.addOption(
         new SettingsOptions.BooleanOption(
            Component.translatable(key),
            () -> hudConfig.decorations.mountSpiritualHp.defaultRendering,
            getDescription(key, 2),
            b -> hudConfig.decorations.mountSpiritualHp.defaultRendering = !hudConfig.decorations.mountSpiritualHp.defaultRendering,
            b -> hudConfig.decorations.mountSpiritualHp.defaultRendering = true
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.sideMountShpDeco";
      SettingsOptions.addOption(
         new SettingsButton(
            Component.translatable(key),
            () -> bySide(hudConfig.decorations.mountSpiritualHp.side),
            getDescription(key, 4),
            b -> hudConfig.decorations.mountSpiritualHp.side = (hudConfig.decorations.mountSpiritualHp.side + 1) % 3,
            b -> hudConfig.decorations.mountSpiritualHp.side = 0
         ),
         SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
      key = "tensura.settings.editMountShpDeco";
      SettingsOptions.addOption(
         new SettingsButton(Component.translatable(key), () -> Component.translatable("tensura.settings.edit"), getDescription(key, 2), b -> {
            hudConfig.decorations.mountSpiritualHp.defaultRendering = false;
            editingElement = 9;
            editingMode = true;
            scaleEditBox.setValue(String.valueOf(OverlayHandler.getScaleById(editingElement)));
         }, b -> {
            hudConfig.decorations.mountSpiritualHp.positionX = 0.0F;
            hudConfig.decorations.mountSpiritualHp.positionY = 0.0F;
            hudConfig.decorations.mountSpiritualHp.scale = 1.0F;
         }), SettingsOptions.BuiltinCategories.HUD_DECORATIONS.getCategory()
      );
   }

   private static List<Component> getDescription(String key, int count) {
      List<Component> list = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         list.add(Component.translatable(key + ".description" + i));
      }

      return list;
   }

   private static boolean canParseInt(String value) {
      try {
         Integer.parseInt(value);
         return true;
      } catch (NumberFormatException ignored) {
         return false;
      }
   }

   private static boolean canParseFloat(String value) {
      try {
         Float.parseFloat(value);
         return true;
      } catch (NumberFormatException ignored) {
         return false;
      }
   }

   private static Component bySide(int side) {
      return switch (side) {
         case 1 -> Component.translatable("tensura.settings.left");
         case 2 -> Component.translatable("tensura.settings.right");
         default -> Component.translatable("tensura.settings.dynamic");
      };
   }

   @Generated
   public static EditBox getInputEditBox() {
      return inputEditBox;
   }

   @Generated
   public static void setInputEditBox(EditBox inputEditBox) {
      SettingsScreen.inputEditBox = inputEditBox;
   }

   @Generated
   public static int getEditingElement() {
      return editingElement;
   }

   @Generated
   public static boolean isEditingMode() {
      return editingMode;
   }

   @Generated
   public static int getPreviewElement() {
      return previewElement;
   }

   @Generated
   public static void setPreviewElement(int previewElement) {
      SettingsScreen.previewElement = previewElement;
   }
}
