package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.menu.HumanoidInventoryMenu;
import io.github.manasmods.tensura.util.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class HumanoidInventoryScreen extends AbstractContainerScreen<HumanoidInventoryMenu> {
   private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/entity/humanoid_inventory_gui.png");
   private final TensuraHumanoidEntity humanoid;
   private final int page;

   public HumanoidInventoryScreen(HumanoidInventoryMenu menu, Inventory inventory, TensuraHumanoidEntity entity, int page) {
      super(menu, inventory, entity.getDisplayName());
      this.humanoid = entity;
      this.page = page;
      this.imageHeight = 175;
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(LOCATION, width, height, 0, 0, this.imageWidth, this.imageHeight);
      int size = this.humanoid.getChestSlots() - 36 * this.page;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 9; j++) {
            graphics.blit(LOCATION, width + 7 + j * 18, height + 7 + i * 18, size > 0 ? 0 : 18, this.imageHeight, 18, 18);
            size--;
         }
      }

      boolean backHovering = x >= width + 7 && x < width + 25 && y >= height + 80 && y < height + 90;
      graphics.blit(LOCATION, width + 7, height + 80, 0, this.imageHeight + (backHovering ? 28 : 18), 18, 10);
      if (this.page < (this.humanoid.getChestSlots() - 1) / 36) {
         boolean hovering = x >= width + 150 && x < width + 168 && y >= height + 80 && y < height + 90;
         graphics.blit(LOCATION, width + 150, height + 80, 18, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 9, 4210752, false);
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 7
         && x < width + 25
         && y >= height + 80
         && y < height + 90
         && ((HumanoidInventoryMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((HumanoidInventoryMenu)this.menu).containerId, 0);
         return true;
      } else {
         if (!(x >= width + 150)
            || !(x < width + 169)
            || !(y >= height + 80)
            || !(y < height + 90)
            || this.page >= (this.humanoid.getChestSlots() - 1) / 36
            || !((HumanoidInventoryMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
            return super.mouseClicked(x, y, i);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((HumanoidInventoryMenu)this.menu).containerId, 1);
         return true;
      }
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      super.renderTooltip(guiGraphics, x, y);
      if (this.humanoid.getChestSlots() > 0) {
         int width = (this.width - this.imageWidth) / 2;
         int height = (this.height - this.imageHeight) / 2;
         if (x >= width + 7 && x < width + 25 && y >= height + 80 && y < height + 90) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), x, y);
         } else if (x >= width + 150 && x < width + 169 && y >= height + 80 && y < height + 90 && this.page < (this.humanoid.getChestSlots() - 1) / 36) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
         }
      }
   }
}
