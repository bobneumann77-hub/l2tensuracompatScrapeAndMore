package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.menu.SpatialBagMenu;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class SpatialBagScreen extends AbstractContainerScreen<SpatialBagMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial_storage/spatial_bag.png");
   private final int maxSize;
   private final int page;
   private final boolean armorSlot;

   public SpatialBagScreen(SpatialBagMenu pMenu, Inventory pPlayerInventory, int maxSize, boolean armorSlot, int page) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
      this.maxSize = armorSlot ? maxSize - 4 : maxSize;
      this.page = page;
      this.armorSlot = armorSlot;
      this.imageHeight = 169;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.title, this.imageWidth / 2, this.titleLabelY + 1, 4210752, false);
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 3, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, width, height, 0, 0, this.imageWidth, this.imageHeight);
      if (this.armorSlot) {
         graphics.blit(BACKGROUND, width + 176, height + 14, this.imageWidth, 14, 22, 99);
         boolean hovering = x >= width + 176 && x < width + 194 && y >= height + 91 && y < height + 109;
         if (hovering) {
            graphics.blit(BACKGROUND, width + 176, height + 91, 36, this.imageHeight, 18, 18);
         }
      }

      int size = this.maxSize - 27 * this.page;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            graphics.blit(BACKGROUND, width + 7 + j * 18, height + 18 + i * 18, size > 0 ? 0 : 18, this.imageHeight, 18, 18);
            size--;
         }
      }

      if (this.page > 0) {
         boolean backHovering = x >= width + 7 && x < width + 25 && y >= height + 74 && y < height + 84;
         graphics.blit(BACKGROUND, width + 7, height + 74, 0, this.imageHeight + (backHovering ? 28 : 18), 18, 10);
      }

      if (this.page < (this.maxSize - 1) / 27) {
         boolean hovering = x >= width + 150 && x < width + 168 && y >= height + 74 && y < height + 84;
         graphics.blit(BACKGROUND, width + 150, height + 74, 18, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      super.renderTooltip(graphics, x, y);
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 7 && x < width + 25 && y >= height + 74 && y < height + 84) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), x, y);
      } else if (x >= width + 150 && x < width + 169 && y >= height + 74 && y < height + 84 && this.page < (this.maxSize - 1) / 27) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
      } else if (x >= width + 176 && x < width + 194 && y >= height + 91 && y < height + 109 && this.armorSlot) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.skill.mode.spatial_storage.dress"), x, y);
      }
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 7 && x < width + 25 && y >= height + 74 && y < height + 84 && ((SpatialBagMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SpatialBagMenu)this.menu).containerId, 0);
         return true;
      } else if (x >= width + 150
         && x < width + 169
         && y >= height + 74
         && y < height + 84
         && this.page < (this.maxSize - 1) / 27
         && ((SpatialBagMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SpatialBagMenu)this.menu).containerId, 1);
         return true;
      } else {
         if (!(x >= width + 176)
            || !(x < width + 194)
            || !(y >= height + 91)
            || !(y < height + 109)
            || !this.armorSlot
            || !((SpatialBagMenu)this.menu).clickMenuButton(this.minecraft.player, 2)) {
            return super.mouseClicked(x, y, i);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SpatialBagMenu)this.menu).containerId, 2);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
