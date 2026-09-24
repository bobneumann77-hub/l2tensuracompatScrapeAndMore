package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.menu.RepeatCraftingMenu;
import io.github.manasmods.tensura.util.client.RenderHelper;
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

public class RepeatCraftingScreen extends AbstractContainerScreen<RepeatCraftingMenu<?>> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/repeat_crafting/repeat_crafting.png");
   private static final ResourceLocation GS_BACKGROUND = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/gui/repeat_crafting/great_sage_crafting.png"
   );
   private static final ResourceLocation REPEAT = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/repeat_crafting/repeat_button.png");
   private final Player player;
   private final RepeatCraftingMenu<?> craftingMenu;

   public RepeatCraftingScreen(RepeatCraftingMenu pMenu, Inventory pPlayerInventory) {
      super(pMenu, pPlayerInventory, Component.empty());
      this.player = pPlayerInventory.player;
      this.craftingMenu = pMenu;
      this.imageWidth = 249;
      this.imageHeight = 192;
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 26, 4210752, false);
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pX, int pY) {
      boolean refining = this.craftingMenu.getSkill() instanceof IRefining;
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(refining ? GS_BACKGROUND : BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
      ManasSkillInstance instance = this.craftingMenu.getSkillInstance(this.player);
      if (instance != null && instance.getOrCreateTag().getBoolean("Repeating")) {
         if (RenderHelper.mouseOver(pX, pY, this.leftPos + 208, this.leftPos + 220, this.topPos + 55, this.topPos + 67)) {
            guiGraphics.blit(REPEAT, x + 207, y + 54, 14.0F, 14.0F, 14, 14, 28, 28);
         } else {
            guiGraphics.blit(REPEAT, x + 207, y + 54, 0.0F, 14.0F, 14, 14, 28, 28);
         }
      } else if (RenderHelper.mouseOver(pX, pY, this.leftPos + 208, this.leftPos + 220, this.topPos + 55, this.topPos + 67)) {
         guiGraphics.blit(REPEAT, x + 207, y + 54, 14.0F, 0.0F, 14, 14, 28, 28);
      }

      if (refining) {
         guiGraphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), this.leftPos + 10, this.topPos + 7);
         guiGraphics.renderItem(new ItemStack(Items.BREWING_STAND), this.leftPos + 38, this.topPos + 6);
      }
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 208, this.leftPos + 220, this.topPos + 55, this.topPos + 67)) {
         guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.automate"), pX, pY);
      }

      if (this.craftingMenu.getSkill() instanceof IRefining) {
         if (RenderHelper.mouseOver(pX, pY, this.leftPos + 5, this.leftPos + 31, this.topPos + 2, this.topPos + 24)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.crafting"), pX, pY);
         } else if (RenderHelper.mouseOver(pX, pY, this.leftPos + 33, this.leftPos + 59, this.topPos + 2, this.topPos + 24)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.refining"), pX, pY);
         }
      }

      super.renderTooltip(guiGraphics, pX, pY);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 208, this.leftPos + 220, this.topPos + 55, this.topPos + 67)
         && ((RepeatCraftingMenu)this.menu).clickMenuButton(this.minecraft.player, -1)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((RepeatCraftingMenu)this.menu).containerId, -1);
         return true;
      } else {
         if (!(this.craftingMenu.getSkill() instanceof IRefining)
            || !RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 33, this.leftPos + 59, this.topPos + 2, this.topPos + 24)
            || !((RepeatCraftingMenu)this.menu).clickMenuButton(this.minecraft.player, 3)) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((RepeatCraftingMenu)this.menu).containerId, 3);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
