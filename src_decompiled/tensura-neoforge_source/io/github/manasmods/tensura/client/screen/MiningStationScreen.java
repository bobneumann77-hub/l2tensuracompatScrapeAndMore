package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.menu.MiningStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MiningStationScreen extends AbstractContainerScreen<MiningStationMenu> {
   public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/mining_station/mining_station_gui.png");

   public MiningStationScreen(MiningStationMenu miningStationMenu, Inventory inventory, Component title) {
      super(miningStationMenu, inventory, title);
      this.imageWidth = 176;
      this.imageHeight = 169;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics graphics, int mX, int mY, float partialTick) {
      super.render(graphics, mX, mY, partialTick);
      this.renderTooltip(graphics, mX, mY);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY + 1, 4210752, false);
      guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 3, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mX, int mY) {
      int centerX = (this.width - this.imageWidth) / 2;
      int centerY = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, centerX, centerY, 0, 0, this.imageWidth, this.imageHeight);
   }
}
