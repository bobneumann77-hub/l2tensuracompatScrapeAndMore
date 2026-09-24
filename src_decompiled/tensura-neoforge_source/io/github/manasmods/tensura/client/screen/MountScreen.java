package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.menu.MountMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class MountScreen extends AbstractContainerScreen<MountMenu> {
   private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/entity/mount_gui.png");
   private final TensuraMountEntity mount;
   private final int page;
   private float xMouse;
   private float yMouse;

   public MountScreen(MountMenu menu, Inventory inventory, TensuraMountEntity entity, int page) {
      super(menu, inventory, entity.getDisplayName());
      this.mount = entity;
      this.page = page;
      this.imageWidth = 178;
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(LOCATION, width, height, 0, 0, this.imageWidth, this.imageHeight);
      graphics.blit(
         LOCATION,
         width + 7,
         height + 16,
         0,
         this.imageHeight + (this.mount.hasSaddleSlot() && this.mount.isSaddleRequired() && this.mount.isSaddleable() ? 0 : 18),
         18,
         18
      );
      if (this.mount.hasArmorSlot()) {
         graphics.blit(LOCATION, width + 7, height + 34, 18, this.imageHeight, 18, 18);
      }

      if (this.mount.hasWeaponSlot()) {
         graphics.blit(LOCATION, width + 7, height + 34, 18, this.imageHeight + (this.mount.hasArmorSlot() ? 0 : 18), 18, 18);
         graphics.blit(LOCATION, width + 7, height + 52, 36, this.imageHeight, 18, 18);
      } else if (this.mount.hasArmorSlot()) {
         graphics.blit(LOCATION, width + 7, height + 34, 18, this.imageHeight, 18, 18);
      }

      if (this.mount.isChested()) {
         int size = this.mount.getTotalChestSlots() - 15 * this.page;

         for (int i = 0; i < 3 && size > 0; i++) {
            for (int j = 0; j < 5 && size > 0; j++) {
               graphics.blit(LOCATION, width + 80 + j * 18, height + 16 + i * 18, 0, this.imageHeight + 36, 18, 18);
               size--;
            }
         }

         if (this.page > 0) {
            boolean hovering = x >= width + 80 && x < width + 98 && y >= height + 71 && y < height + 81;
            graphics.blit(LOCATION, width + 80, height + 71, 0, this.imageHeight + (hovering ? 64 : 54), 18, 10);
         }

         if (this.page < (this.mount.getTotalChestSlots() - 1) / 15) {
            boolean hovering = x >= width + 152 && x < width + 170 && y >= height + 71 && y < height + 81;
            graphics.blit(LOCATION, width + 152, height + 71, 18, this.imageHeight + (hovering ? 64 : 54), 18, 10);
         }
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(
         graphics,
         width + 26,
         height + 18,
         width + 78,
         height + 70,
         this.mount.getMenuRenderSize(),
         0.75F / this.mount.getMenuRenderSize() * 10.0F,
         this.xMouse,
         this.yMouse,
         this.mount
      );
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      this.xMouse = x;
      this.yMouse = y;
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (this.mount.isChested()) {
         if (x >= width + 80
            && x < width + 98
            && y >= height + 71
            && y < height + 81
            && this.page > 0
            && ((MountMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((MountMenu)this.menu).containerId, 0);
            return true;
         }

         if (x >= width + 152
            && x < width + 170
            && y >= height + 71
            && y < height + 81
            && this.page < (this.mount.getTotalChestSlots() - 1) / 15
            && ((MountMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((MountMenu)this.menu).containerId, 1);
            return true;
         }
      }

      return super.mouseClicked(x, y, i);
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      super.renderTooltip(guiGraphics, x, y);
      if (this.mount.isChested()) {
         int width = (this.width - this.imageWidth) / 2;
         int height = (this.height - this.imageHeight) / 2;
         if (x >= width + 80 && x < width + 98 && y >= height + 71 && y < height + 81 && this.page > 0) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), x, y);
         } else if (x >= width + 152 && x < width + 170 && y >= height + 71 && y < height + 81 && this.page < (this.mount.getTotalChestSlots() - 1) / 15) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
         }
      }
   }
}
