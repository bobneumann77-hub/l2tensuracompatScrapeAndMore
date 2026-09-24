package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.menu.ResearcherStorageMenu;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ResearcherStorageScreen extends AbstractContainerScreen<ResearcherStorageMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/researcher/researcher_spatial.png");
   private final Player player;
   private final int maxSize;
   public final int page;

   public ResearcherStorageScreen(ResearcherStorageMenu pMenu, Inventory pPlayerInventory, int maxSize, int page) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
      this.player = pPlayerInventory.player;
      this.maxSize = maxSize;
      this.page = page;
      this.imageWidth = 256;
      this.imageHeight = 201;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.title, 88, this.titleLabelY + 25, Color.WHITE.getRGB(), false);
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, 88, this.inventoryLabelY + 35, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, width, height, 0, 0, 256, this.imageHeight);
      int size = this.maxSize - 27 * this.page;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            if (size <= 0) {
               graphics.blit(BACKGROUND, width + 7 + j * 18, height + 43 + i * 18, 0, this.imageHeight, 18, 18);
            }

            size--;
         }
      }

      if (this.page > 0) {
         boolean hovering = x >= width + 7 && x < width + 25 && y >= height + 99 && y < height + 109;
         graphics.blit(BACKGROUND, width + 7, height + 99, 0, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }

      if (this.page < (this.maxSize - 1) / 27) {
         boolean hovering = x >= width + 151 && x < width + 169 && y >= height + 99 && y < height + 109;
         graphics.blit(BACKGROUND, width + 151, height + 99, 18, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }

      graphics.renderItem(new ItemStack(Items.ENDER_CHEST), this.leftPos + 8, this.topPos + 5);
      graphics.renderItem(new ItemStack(Items.ENCHANTING_TABLE), this.leftPos + 34, this.topPos + 6);
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      super.renderTooltip(graphics, x, y);
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 7 && x < width + 25 && y >= height + 99 && y < height + 109 && this.page > 0) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), x, y);
      } else if (x >= width + 151 && x < width + 169 && y >= height + 99 && y < height + 109 && this.page < (this.maxSize - 1) / 27) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
      } else if (x >= width + 3 && x < width + 28 && y >= height + 1 && y < height + 23) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.researcher_menu.storage_tab"), x, y);
      } else if (x >= width + 29 && x < width + 54 && y >= height + 1 && y < height + 23) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.researcher_menu.enchantment_tab"), x, y);
      }
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 30
         && x < width + 54
         && y >= height + 2
         && y < height + 23
         && ((ResearcherStorageMenu)this.menu).clickMenuButton(this.minecraft.player, -1)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((ResearcherStorageMenu)this.menu).containerId, -1);
         return true;
      } else if (x >= width + 7
         && x < width + 25
         && y >= height + 99
         && y < height + 109
         && this.page > 0
         && ((ResearcherStorageMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((ResearcherStorageMenu)this.menu).containerId, 0);
         return true;
      } else {
         if (!(x >= width + 151)
            || !(x < width + 169)
            || !(y >= height + 99)
            || !(y < height + 109)
            || this.page >= (this.maxSize - 1) / 27
            || !((ResearcherStorageMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
            return super.mouseClicked(x, y, i);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((ResearcherStorageMenu)this.menu).containerId, 1);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
