package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.menu.HumanoidMainMenu;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;

public class HumanoidMainScreen extends AbstractContainerScreen<HumanoidMainMenu> {
   private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/entity/humanoid_main_gui.png");
   private final TensuraHumanoidEntity humanoid;
   private float xMouse;
   private float yMouse;

   public HumanoidMainScreen(HumanoidMainMenu menu, Inventory inventory, TensuraHumanoidEntity entity) {
      super(menu, inventory, entity.getDisplayName());
      this.humanoid = entity;
      this.imageHeight = 175;
   }

   protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(LOCATION, width, height, 0, 0, this.imageWidth, this.imageHeight);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (!slot.equals(EquipmentSlot.BODY)) {
            int id = this.humanoid.getSlotId(slot);
            if (id == -1) {
               int xSlot = slot.isArmor() ? 7 : 76;

               int ySlot = switch (slot) {
                  case CHEST -> 25;
                  case LEGS, OFFHAND -> 43;
                  case FEET, MAINHAND -> 61;
                  default -> 7;
               };
               graphics.blit(LOCATION, width + xSlot, height + ySlot, 0, this.imageHeight, 18, 18);
            }
         }
      }

      if (this.humanoid.getChestSlots() > 0) {
         boolean hovering = x >= width + 150 && x < width + 168 && y >= height + 80 && y < height + 90;
         graphics.blit(LOCATION, width + 150, height + 80, 18, this.imageHeight + (hovering ? 28 : 18), 18, 10);
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(
         graphics,
         width + 26,
         height + 8,
         width + 75,
         height + 78,
         this.humanoid.getMenuRenderSize(),
         0.0625F / this.humanoid.getMenuRenderSize() * 30.0F,
         this.xMouse,
         this.yMouse,
         this.humanoid
      );
   }

   public void render(GuiGraphics graphics, int x, int y, float f) {
      this.xMouse = x;
      this.yMouse = y;
      super.render(graphics, x, y, f);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics graphics, int i, int j) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, 134, this.titleLabelY + 7, Color.WHITE.getRGB(), false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 9, 4210752, false);
      int offset = 0;
      if (this.humanoid.shouldShowEP()) {
         Component EP = Component.translatable(
            "tensura.main_menu.existence_points", new Object[]{RenderHelper.getShortenedNumber(EnergyHelper.getMaxEP(this.humanoid))}
         );
         RenderHelper.drawCenteredText(graphics, this.font, EP, 134, this.titleLabelY + 24, Color.WHITE.getRGB(), false);
         offset += 17;
      }

      if (this.humanoid.shouldShowHP()) {
         Component HP = Component.translatable("tensura.main_menu.health", new Object[]{RenderHelper.getShortenedNumber(this.humanoid.getHealth())});
         RenderHelper.drawCenteredText(graphics, this.font, HP, 134, this.titleLabelY + 24 + offset, Color.WHITE.getRGB(), false);
         offset += 17;
      }

      if (this.humanoid.shouldShowSHP()) {
         Component SHP = Component.translatable(
            "tensura.main_menu.spiritual_health",
            new Object[]{RenderHelper.getShortenedNumber(TensuraStorages.getExistenceFrom(this.humanoid).getSpiritualHealth())}
         );
         RenderHelper.drawCenteredText(graphics, this.font, SHP, 134, this.titleLabelY + 24 + offset, Color.WHITE.getRGB(), false);
         offset += 17;
      }

      if (this.humanoid.shouldShowArmor()) {
         Component armor = Component.translatable("tensura.main_menu.armor", new Object[]{this.humanoid.getArmorValue()});
         RenderHelper.drawCenteredText(graphics, this.font, armor, 134, this.titleLabelY + 24 + offset, Color.WHITE.getRGB(), false);
      }
   }

   public boolean mouseClicked(double x, double y, int i) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (!(x >= width + 150)
         || !(x < width + 168)
         || !(y >= height + 80)
         || !(y < height + 90)
         || this.humanoid.getChestSlots() <= 0
         || !((HumanoidMainMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
         return super.mouseClicked(x, y, i);
      }

      if (this.minecraft.gameMode == null) {
         return false;
      }

      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      this.minecraft.gameMode.handleInventoryButtonClick(((HumanoidMainMenu)this.menu).containerId, 1);
      return true;
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      super.renderTooltip(guiGraphics, x, y);
      if (this.humanoid.getChestSlots() > 0) {
         int width = (this.width - this.imageWidth) / 2;
         int height = (this.height - this.imageHeight) / 2;
         if (x >= width + 150 && x < width + 168 && y >= height + 80 && y < height + 90) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), x, y);
         }
      }
   }
}
