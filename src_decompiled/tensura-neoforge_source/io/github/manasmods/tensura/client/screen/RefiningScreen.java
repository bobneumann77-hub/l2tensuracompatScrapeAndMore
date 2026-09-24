package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.menu.RefiningMenu;
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

public class RefiningScreen extends AbstractContainerScreen<RefiningMenu<?>> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/refining/refining.png");
   private static final ResourceLocation GS_BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/refining/great_sage_refining.png");
   private static final ResourceLocation REPEAT = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/repeat_crafting/repeat_button.png");
   private static final ResourceLocation BREW = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/refining/brew_start.png");
   private final Player player;
   private final RefiningMenu<?> craftingMenu;

   public RefiningScreen(RefiningMenu pMenu, Inventory pPlayerInventory) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
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
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.title, this.imageWidth / 2, this.titleLabelY + 25, 16777215, false);
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 26, 4210752, false);
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pX, int pY) {
      ResourceLocation background = ((IRefining)this.craftingMenu.getSkill()).hasAutoCraftingTab() ? GS_BACKGROUND : BACKGROUND;
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(background, x, y, 0, 0, this.imageWidth, this.imageHeight);
      ManasSkillInstance instance = this.craftingMenu.getSkillInstance(this.player);
      if (instance != null && instance.getOrCreateTag().getBoolean("Brewing")) {
         guiGraphics.blit(background, x + 7, y + 35, 1.0F, 193.0F, 204, 58, 256, 256);
      }

      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 182, this.leftPos + 194, this.topPos + 58, this.topPos + 70)) {
         guiGraphics.blit(BREW, x + 182, y + 58, 0.0F, 12.0F, 12, 12, 12, 24);
      } else {
         guiGraphics.blit(BREW, x + 182, y + 58, 0.0F, 0.0F, 12, 12, 12, 24);
      }

      if (((IRefining)this.craftingMenu.getSkill()).isAutoRefiningAllowed()) {
         if (instance != null && instance.getOrCreateTag().getBoolean("RepeatBrewing")) {
            if (RenderHelper.mouseOver(pX, pY, this.leftPos + 221, this.leftPos + 233, this.topPos + 83, this.topPos + 95)) {
               guiGraphics.blit(REPEAT, x + 220, y + 82, 14.0F, 14.0F, 14, 14, 28, 28);
            } else {
               guiGraphics.blit(REPEAT, x + 220, y + 82, 0.0F, 14.0F, 14, 14, 28, 28);
            }
         } else if (RenderHelper.mouseOver(pX, pY, this.leftPos + 221, this.leftPos + 233, this.topPos + 83, this.topPos + 95)) {
            guiGraphics.blit(REPEAT, x + 220, y + 82, 14.0F, 0.0F, 14, 14, 28, 28);
         }
      }

      if (((IRefining)this.craftingMenu.getSkill()).hasAutoCraftingTab()) {
         guiGraphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), this.leftPos + 10, this.topPos + 7);
         guiGraphics.renderItem(new ItemStack(Items.BREWING_STAND), this.leftPos + 38, this.topPos + 6);
      }
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 221, this.leftPos + 233, this.topPos + 83, this.topPos + 95)
         && ((IRefining)this.craftingMenu.getSkill()).isAutoRefiningAllowed()) {
         guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.automate"), pX, pY);
      }

      if (RenderHelper.mouseOver(pX, pY, this.leftPos + 182, this.leftPos + 194, this.topPos + 58, this.topPos + 70)) {
         guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.brew"), pX, pY);
      }

      if (((IRefining)this.craftingMenu.getSkill()).hasAutoCraftingTab()) {
         if (RenderHelper.mouseOver(pX, pY, this.leftPos + 5, this.leftPos + 31, this.topPos + 2, this.topPos + 24)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.crafting"), pX, pY);
         } else if (RenderHelper.mouseOver(pX, pY, this.leftPos + 33, this.leftPos + 59, this.topPos + 2, this.topPos + 24)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.great_sage_menu.refining"), pX, pY);
         }
      }

      super.renderTooltip(guiGraphics, pX, pY);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (!RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 221, this.leftPos + 233, this.topPos + 83, this.topPos + 95)
         || !((RefiningMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
         if (RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 182, this.leftPos + 194, this.topPos + 58, this.topPos + 70)
            && ((RefiningMenu)this.menu).clickMenuButton(this.minecraft.player, 2)) {
            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((RefiningMenu)this.menu).containerId, 2);
            return true;
         } else {
            if (!((IRefining)this.craftingMenu.getSkill()).hasAutoCraftingTab()
               || !RenderHelper.mouseOver(pMouseX, pMouseY, this.leftPos + 5, this.leftPos + 31, this.topPos + 2, this.topPos + 24)
               || !((RefiningMenu)this.menu).clickMenuButton(this.minecraft.player, 3)) {
               return super.mouseClicked(pMouseX, pMouseY, pButton);
            }

            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((RefiningMenu)this.menu).containerId, 3);
            return true;
         }
      } else {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((RefiningMenu)this.menu).containerId, 1);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
