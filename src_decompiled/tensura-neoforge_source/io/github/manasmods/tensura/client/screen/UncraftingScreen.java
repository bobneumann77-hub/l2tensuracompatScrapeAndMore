package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.menu.UncraftingMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UncraftingScreen extends AbstractContainerScreen<UncraftingMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/degenerate/degenerate_tab_1.png");

   public UncraftingScreen(UncraftingMenu pMenu, Inventory pPlayerInventory) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
      this.imageWidth = 210;
      this.imageHeight = 199;
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      RenderHelper.drawCenteredText(
         guiGraphics, this.font, Component.translatable("tensura.degenerate_menu.crafting"), 46, this.titleLabelY + 24, Color.WHITE.getRGB(), false
      );
      RenderHelper.drawCenteredText(
         guiGraphics, this.font, Component.translatable("tensura.degenerate_menu.uncrafting"), 164, this.titleLabelY + 24, Color.WHITE.getRGB(), false
      );
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 33, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, width, height, 0, 0, this.imageWidth, this.imageHeight);
      graphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), this.leftPos + 10, this.topPos + 6);
      graphics.renderItem(new ItemStack(Items.ENCHANTED_BOOK), this.leftPos + 38, this.topPos + 7);
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      super.renderTooltip(graphics, x, y);
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 5 && x < width + 31 && y >= height + 1 && y < height + 24) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.degenerate_menu.tab_1"), x, y);
      } else if (x >= width + 33 && x < width + 59 && y >= height + 1 && y < height + 24) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.degenerate_menu.tab_2"), x, y);
      }
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (!(x >= width + 33)
         || !(x < width + 59)
         || !(y >= height + 1)
         || !(y < height + 24)
         || !((UncraftingMenu)this.menu).clickMenuButton(this.minecraft.player, -1)) {
         return super.mouseClicked(x, y, i);
      }

      if (this.minecraft.gameMode == null) {
         return false;
      }

      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      this.minecraft.gameMode.handleInventoryButtonClick(((UncraftingMenu)this.menu).containerId, -1);
      return true;
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
