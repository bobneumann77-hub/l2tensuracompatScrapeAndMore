package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.menu.SpatialStorageMenu;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class SpatialStorageScreen extends AbstractContainerScreen<SpatialStorageMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial_storage/spatial_storage.png");
   private static final ResourceLocation WATER_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial_storage/water_bar.png");
   private static final ResourceLocation LAVA_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial_storage/lava_bar.png");
   private final int maxSize;
   private final int page;

   public SpatialStorageScreen(SpatialStorageMenu pMenu, Inventory pPlayerInventory, int maxSize, int page) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
      this.maxSize = maxSize;
      this.page = page;
      this.imageWidth = 234;
      this.imageHeight = 184;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.title, this.imageWidth / 2, this.titleLabelY + 5, Color.WHITE.getRGB(), false);
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 17, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, width, height, 0, 0, this.imageWidth, this.imageHeight);
      this.renderWaterBar(graphics);
      this.renderLavaBar(graphics);
      int size = this.maxSize - 27 * this.page;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            if (size <= 0) {
               graphics.blit(BACKGROUND, width + 36 + j * 18, height + 24 + i * 18, 0, this.imageHeight, 18, 18);
            }

            size--;
         }
      }

      if (this.page > 0) {
         boolean hovering = x >= width + 31 && x < width + 49 && y >= height + 81 && y < height + 91;
         graphics.blit(BACKGROUND, width + 31, height + 81, 0, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }

      if (this.page < (this.maxSize - 1) / 27) {
         boolean hovering = x >= width + 185 && x < width + 203 && y >= height + 81 && y < height + 91;
         graphics.blit(BACKGROUND, width + 185, height + 80, 18, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }
   }

   private void renderWaterBar(GuiGraphics graphics) {
      double waterPoint = TensuraStorages.getAbilityFrom(((SpatialStorageMenu)this.getMenu()).getStorageOwner()).getWaterPoint();
      double maxCapacity = ((SpatialStorageMenu)this.menu).getStorageOwner().getAttributeValue(TensuraAttributes.WATER_CAPACITY);
      int waterBar = (int)Mth.clamp(waterPoint, 0.0, maxCapacity);
      int fill = (int)(122 * waterBar / maxCapacity);
      int length = 122 - fill;
      int barYOffset = 118 + length;
      int pX = this.leftPos + 10;
      int pY = this.topPos + 31 + length;
      graphics.blit(WATER_BAR, pX, pY, 0.0F, barYOffset, 10, fill, 10, 122);
   }

   private void renderLavaBar(GuiGraphics graphics) {
      double lavaPoint = TensuraStorages.getAbilityFrom(((SpatialStorageMenu)this.getMenu()).getStorageOwner()).getLavaPoint();
      double maxCapacity = ((SpatialStorageMenu)this.menu).getStorageOwner().getAttributeValue(TensuraAttributes.LAVA_CAPACITY);
      int waterBar = (int)Mth.clamp(lavaPoint, 0.0, maxCapacity);
      int fill = (int)(122 * waterBar / maxCapacity);
      int length = 122 - fill;
      int barYOffset = 118 + length;
      int pX = this.leftPos + 214;
      int pY = this.topPos + 31 + length;
      graphics.blit(LAVA_BAR, pX, pY, 0.0F, barYOffset, 10, fill, 10, 122);
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      if (RenderHelper.mouseOver(x, y, this.leftPos + 10, this.leftPos + 20, this.topPos + 30, this.topPos + 154)) {
         double maxCapacity = ((SpatialStorageMenu)this.menu).getStorageOwner().getAttributeValue(TensuraAttributes.WATER_CAPACITY);
         Component tooltip = Component.literal(
               TensuraStorages.getAbilityFrom(((SpatialStorageMenu)this.getMenu()).getStorageOwner()).getWaterPoint() + "/" + maxCapacity
            )
            .withStyle(ChatFormatting.AQUA);
         graphics.renderTooltip(this.font, tooltip, x, y);
      } else if (RenderHelper.mouseOver(x, y, this.leftPos + 214, this.leftPos + 224, this.topPos + 30, this.topPos + 154)) {
         double maxCapacity = ((SpatialStorageMenu)this.menu).getStorageOwner().getAttributeValue(TensuraAttributes.LAVA_CAPACITY);
         Component tooltip = Component.literal(
               TensuraStorages.getAbilityFrom(((SpatialStorageMenu)this.getMenu()).getStorageOwner()).getLavaPoint() + "/" + maxCapacity
            )
            .withStyle(ChatFormatting.RED);
         graphics.renderTooltip(this.font, tooltip, x, y);
      }

      super.renderTooltip(graphics, x, y);
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 31 && x < width + 49 && y >= height + 81 && y < height + 91 && this.page > 0) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), x, y);
      } else if (x >= width + 185 && x < width + 203 && y >= height + 81 && y < height + 91 && this.page < (this.maxSize - 1) / 27) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
      }
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 31
         && x < width + 49
         && y >= height + 81
         && y < height + 91
         && this.page > 0
         && ((SpatialStorageMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SpatialStorageMenu)this.menu).containerId, 0);
         return true;
      } else {
         if (!(x >= width + 185)
            || !(x < width + 203)
            || !(y >= height + 81)
            || !(y < height + 91)
            || this.page >= (this.maxSize - 1) / 27
            || !((SpatialStorageMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
            return super.mouseClicked(x, y, i);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SpatialStorageMenu)this.menu).containerId, 1);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
