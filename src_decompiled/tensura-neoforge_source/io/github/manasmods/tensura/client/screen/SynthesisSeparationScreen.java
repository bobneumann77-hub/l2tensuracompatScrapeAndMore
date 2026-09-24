package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.menu.SynthesisSeparationMenu;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class SynthesisSeparationScreen extends AbstractContainerScreen<SynthesisSeparationMenu> implements IScrollBar {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/degenerate/degenerate_tab_2.png");
   private static final ResourceLocation ENCHANTMENT_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/ability_button.png");
   private float scrollOffset;
   protected boolean scrolling;
   private int listStartIndex;

   public SynthesisSeparationScreen(SynthesisSeparationMenu pMenu, Inventory pPlayerInventory) {
      super(pMenu, pPlayerInventory, Component.empty());
      this.imageWidth = 210;
      this.imageHeight = 199;
   }

   protected void init() {
      super.init();
      this.scrollOffset = 0.0F;
      this.listStartIndex = 0;
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
      guiGraphics.drawString(this.font, Component.translatable("tensura.degenerate_menu.synthesis"), 10, this.titleLabelY + 28, 16777215);
      guiGraphics.drawString(this.font, Component.translatable("tensura.degenerate_menu.separation"), 10, this.titleLabelY + 48, 16777215);
      RenderHelper.drawCenteredText(guiGraphics, this.font, this.playerInventoryTitle, this.imageWidth / 2, this.inventoryLabelY + 35, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, width, height, 0, 0, this.imageWidth, this.imageHeight);
      this.renderScrollBar(graphics, pMouseX, pMouseY);
      int lastVisibleElementIndex = this.getListStartIndex() + 3;
      this.renderButtons(graphics, pMouseX, pMouseY, lastVisibleElementIndex);
      graphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), this.leftPos + 10, this.topPos + 7);
      graphics.renderItem(new ItemStack(Items.ENCHANTED_BOOK), this.leftPos + 38, this.topPos + 6);
   }

   private void renderButtons(GuiGraphics graphics, int pMouseX, int pMouseY, int pLastVisibleElementIndex) {
      ItemEnchantments input = ((SynthesisSeparationMenu)this.menu).getInputEnchantments();
      List<Holder<Enchantment>> enchantments = ((SynthesisSeparationMenu)this.menu).getSortedEnchantmentList(input);

      for (int i = this.getListStartIndex(); i < pLastVisibleElementIndex && i < enchantments.size(); i++) {
         int x = this.leftPos + 69;
         int y = this.topPos + 63 + (i - this.getListStartIndex()) * 13;
         int offset = 0;
         boolean hovering = pMouseX >= x && pMouseY >= y && pMouseX < x + 89 && pMouseY < y + 13;
         if (hovering) {
            offset = 13;
         }

         graphics.blit(ENCHANTMENT_BUTTON, x, y, 0.0F, offset, 89, 13, 89, 26);
         Holder<Enchantment> enchantment = enchantments.get(i);
         int level = input.getLevel(enchantment);
         Component name = Enchantment.getFullname(enchantment, level > 10 ? 1 : level).copy().withColor(this.getColor(enchantment));
         RenderHelper.drawShortenedTextWithTooltip(
            graphics,
            this.font,
            name,
            name,
            this.leftPos + 69,
            this.topPos + 63 + (i - this.getListStartIndex()) * 13,
            3,
            3,
            88,
            13,
            pMouseX,
            pMouseY,
            this.getColor(enchantment),
            true,
            this
         );
      }
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

   public boolean mouseClicked(double x, double y, int pButton) {
      if (this.minecraft == null) {
         return false;
      }

      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (!(x >= width + 5)
         || !(x < width + 31)
         || !(y >= height + 1)
         || !(y < height + 24)
         || !((SynthesisSeparationMenu)this.menu).clickMenuButton(this.minecraft.player, -1)) {
         List<Holder<Enchantment>> enchantment = ((SynthesisSeparationMenu)this.menu)
            .getSortedEnchantmentList(((SynthesisSeparationMenu)this.menu).getInputEnchantments());
         int lastDisplayedRecipeIndex = this.getListStartIndex() + this.getScrollBarRenderCount();

         for (int i = this.getListStartIndex(); i < lastDisplayedRecipeIndex; i++) {
            int xPos = this.leftPos + 69;
            int yPos = this.topPos + 63 + (i - this.getListStartIndex()) * 13;
            if (enchantment.size() <= i) {
               break;
            }

            if (x >= xPos && y >= yPos && x < xPos + 89 && y < yPos + 13 && ((SynthesisSeparationMenu)this.menu).clickMenuButton(this.minecraft.player, i)) {
               if (this.minecraft.gameMode == null) {
                  return false;
               }

               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick(((SynthesisSeparationMenu)this.menu).containerId, i);
               if (enchantment.size() <= lastDisplayedRecipeIndex) {
                  this.scrolledScrollBar(-1.0);
               }

               return true;
            }
         }

         if (this.clickedScrollBar(x, y)) {
            return true;
         }

         Slot slot = ((SynthesisSeparationMenu)this.getMenu()).inputSlot;
         if (slot != null && this.isHovering(slot.x, slot.y, 16, 16, x, y)) {
            this.setScrollOffset(0.0F);
            this.setListStartIndex(0);
         }

         return super.mouseClicked(x, y, pButton);
      } else {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((SynthesisSeparationMenu)this.menu).containerId, -1);
         return true;
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta, double deltaY) {
      this.scrolledScrollBar(deltaY);
      return true;
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return this.draggedScrollBar(pMouseY) ? true : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }

   private int getColor(Holder<Enchantment> enchantment) {
      if (enchantment.is(EnchantmentTags.CURSE)) {
         return 16733525;
      } else {
         return enchantment.is(TensuraTags.Enchantments.ENGRAVING) ? 16766720 : 5636095;
      }
   }

   @Override
   public int getScrollBarX() {
      return this.leftPos + 161;
   }

   @Override
   public int getScrollBarY() {
      return this.topPos + 63;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 39;
   }

   @Override
   public int getScrollBarListSize() {
      return ((SynthesisSeparationMenu)this.menu).getInputEnchantments().size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 3;
   }

   @Generated
   @Override
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }

   @Generated
   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Generated
   public int getListStartIndex() {
      return this.listStartIndex;
   }
}
