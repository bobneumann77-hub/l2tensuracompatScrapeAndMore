package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.menu.NamingMenu;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class NamingScreen extends AbstractContainerScreen<NamingMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/naming/naming_gui.png");
   private EditBox editBox;
   private RequestNamingMenuPacket.NamingType type;

   public NamingScreen(NamingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
      this.imageWidth = 144;
      this.imageHeight = 96;
   }

   protected void init() {
      super.init();
      this.editBox = new EditBox(this.font, this.leftPos + 19, this.topPos + 27, 85, 11, Component.empty());
      this.editBox.setBordered(false);
      this.editBox.setValue(((NamingMenu)this.menu).getEntity().getName().getString());
      this.addRenderableWidget(this.editBox);
   }

   protected void renderBg(GuiGraphics graphics, float particleTick, int pX, int pY) {
      int centerX = (this.width - this.imageWidth) / 2;
      int centerY = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, centerX, centerY, 0, 0, this.imageWidth, this.imageHeight);
      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 36, this.leftPos + 107, this.topPos + 73, this.topPos + 89)) {
         graphics.blit(BACKGROUND, this.leftPos + 37, this.topPos + 74, 1.0F, 176.0F, 70, 15, 256, 256);
      }

      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 113, this.leftPos + 128, this.topPos + 22, this.topPos + 39)) {
         graphics.blit(BACKGROUND, this.leftPos + 114, this.topPos + 23, 79.0F, 97.0F, 14, 16, 256, 256);
      }

      RenderHelper.drawCenteredText(
         graphics, this.font, Component.translatable("tensura.naming.name"), this.leftPos + 72, this.topPos + 78, Color.WHITE.getRGB(), false
      );

      for (int i = 0; i < 3; i++) {
         int x = this.leftPos + 31 + i * 30;
         int hOffset = i == 0 ? 1 : (i == 1 ? 22 : 43);
         RequestNamingMenuPacket.NamingType namingType = i == 0
            ? RequestNamingMenuPacket.NamingType.LOW
            : (i == 1 ? RequestNamingMenuPacket.NamingType.MEDIUM : RequestNamingMenuPacket.NamingType.HIGH);
         int yOffset = 0;
         if (namingType == this.type) {
            yOffset = 139;
         } else if (RenderHelper.mouseOver(pX, pY, x, x + 21, this.topPos + 47, this.topPos + 68)) {
            yOffset = 118;
         }

         if (yOffset != 0) {
            graphics.blit(BACKGROUND, x + 1, this.topPos + 48, hOffset, yOffset, 20, 20, 256, 256);
         }
      }

      this.renderTooltip(graphics, pX, pY);
   }

   protected void renderTooltip(GuiGraphics graphics, int pX, int pY) {
      int y = this.topPos + 47;

      for (int i = 0; i < 3; i++) {
         int x = this.leftPos + 31 + i * 30;

         Component tooltip = switch (i) {
            case 0 -> Component.translatable("tensura.naming.subdue");
            case 1 -> Component.translatable("tensura.naming.evolve");
            default -> Component.translatable("tensura.naming.endow");
         };
         if (RenderHelper.mouseOver(pX, pY, x, x + 21, y, y + 21)) {
            graphics.renderTooltip(this.font, tooltip, pX, pY);
         }
      }

      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 114, this.leftPos + 128, this.topPos + 23, this.topPos + 39)) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.naming.randomize"), pX, pY);
      }

      super.renderTooltip(graphics, pX, pY);
   }

   protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.handleTypeButtonsClick(pMouseX, pMouseY) != 0) {
         return true;
      }

      if (RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 114, this.leftPos + 128, this.topPos + 23, this.topPos + 39)) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.editBox.setValue(NamingMenu.getRandomName(((NamingMenu)this.menu).getEntity().getRandom()));
         this.editBox.setCursorPosition(0);
         this.editBox.setHighlightPos(0);
         this.editBox.setFocused(false);
      }

      if (RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 37, this.leftPos + 107, this.topPos + 74, this.topPos + 89)) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.editBox.getValue().isBlank() || this.editBox.getValue().isEmpty()) {
            return true;
         }

         if (this.type == null) {
            return true;
         }

         int id = ((NamingMenu)this.menu).getEntity().getId();
         NetworkManager.sendToServer(new RequestNamingMenuPacket(id, this.editBox.getValue(), this.type));
         this.onClose();
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   private int handleTypeButtonsClick(double pX, double pY) {
      int y = this.topPos + 47;

      for (int i = 1; i < 4; i++) {
         int x = this.leftPos + 31 + (i - 1) * 30;
         if (RenderHelper.mouseOver(pX, pY, x, x + 21, y, y + 21)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.type = i == 1
               ? RequestNamingMenuPacket.NamingType.LOW
               : (i == 2 ? RequestNamingMenuPacket.NamingType.MEDIUM : RequestNamingMenuPacket.NamingType.HIGH);
            return i;
         }
      }

      return 0;
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.editBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      }

      if (this.editBox.isFocused() && this.editBox.isVisible() && pKeyCode != 256) {
         return true;
      }

      if (this.minecraft != null) {
         if (TensuraKeybinds.NAME.matches(pKeyCode, pScanCode)) {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
         }

         if (this.minecraft.options.keyInventory.matches(pKeyCode, pScanCode)) {
            return true;
         }
      }

      return super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
