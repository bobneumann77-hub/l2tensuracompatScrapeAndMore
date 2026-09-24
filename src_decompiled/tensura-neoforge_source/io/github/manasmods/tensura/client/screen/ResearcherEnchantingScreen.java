package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.ability.subclass.IResearcherEnchanter;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import io.github.manasmods.tensura.network.c2s.RequestResearcherEnchantingPacket;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ResearcherEnchantingScreen extends AbstractContainerScreen<ResearcherEnchantingMenu> implements IScrollBar {
   protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/researcher/researcher_enchant.png");
   protected static final ResourceLocation NAME_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/researcher/enchantment_name.png");
   protected static final ResourceLocation LEVEL_BOX = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/researcher/enchantment_level.png");
   protected static final ResourceLocation CHECKBOX = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/checkbox.png");
   protected float scrollOffset;
   protected boolean scrolling;
   protected int listStartIndex;
   protected EditBox searchBar;
   protected String nameFilter = "";
   protected List<Holder<Enchantment>> filtered = new ArrayList<>();

   public ResearcherEnchantingScreen(ResearcherEnchantingMenu pMenu, Inventory pPlayerInventory) {
      super(pMenu, pPlayerInventory, pMenu.getSkill().getName());
      this.imageWidth = 176;
      this.imageHeight = 227;
   }

   protected void init() {
      super.init();
      this.scrollOffset = 0.0F;
      this.listStartIndex = 0;
      this.searchBar = new EditBox(this.font, this.leftPos + 9, this.topPos + 51, 102, 10, Component.empty());
      this.searchBar.setBordered(false);
      this.searchBar.setResponder(s -> {
         this.nameFilter = s;
         this.setScrollOffset(0.0F);
         this.setListStartIndex(0);
         this.updateFilteredEnchantments();
         this.scrolledScrollBar(0.0);
      });
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchBar.setValue(this.nameFilter);
      }

      this.addRenderableWidget(this.searchBar);
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   public void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, 88, this.titleLabelY + 25, Color.WHITE.getRGB(), false);
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mX, int mY) {
      graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      boolean hovering = mX >= this.leftPos + 145 && mX < this.leftPos + 165 && mY >= this.topPos + 44 && mY < this.topPos + 62;
      if (hovering && !((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty()) {
         graphics.blit(BACKGROUND, this.leftPos + 145, this.topPos + 44, 0, this.imageHeight, 20, 18);
      }

      this.renderScrollBar(graphics, mX, mY);
      this.renderEnchantments(graphics, mX, mY);
      graphics.renderItem(new ItemStack(Items.ENDER_CHEST), this.leftPos + 8, this.topPos + 6);
      graphics.renderItem(new ItemStack(Items.ENCHANTING_TABLE), this.leftPos + 34, this.topPos + 5);
   }

   protected void renderTooltip(GuiGraphics graphics, int mX, int mY) {
      super.renderTooltip(graphics, mX, mY);
      if (RenderHelper.mouseOver(mX, mY, this.leftPos + 3, this.leftPos + 28, this.topPos + 1, this.topPos + 23)) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.researcher_menu.storage_tab"), mX, mY);
      } else if (RenderHelper.mouseOver(mX, mY, this.leftPos + 29, this.leftPos + 54, this.topPos + 1, this.topPos + 23)) {
         graphics.renderTooltip(this.font, Component.translatable("tensura.researcher_menu.enchantment_tab"), mX, mY);
      } else if (RenderHelper.mouseOver(mX, mY, this.leftPos + 145, this.leftPos + 165, this.topPos + 44, this.topPos + 62)
         && !((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty()) {
         graphics.renderTooltip(this.font, Component.translatable("container.enchant"), mX, mY);
      } else if (RenderHelper.mouseOver(mX, mY, this.leftPos + 149, this.leftPos + 160, this.topPos + 91, this.topPos + 106)) {
         int cost = ((ResearcherEnchantingMenu)this.menu).getExperienceCost();
         long total = ResearcherEnchantingMenu.getTotalXp(((ResearcherEnchantingMenu)this.menu).getPlayer());
         ChatFormatting formatting = total >= cost ? ChatFormatting.GREEN : ChatFormatting.RED;
         graphics.renderTooltip(
            this.font,
            Component.translatable("tensura.researcher_menu.enchantment_tab.xp_cost", new Object[]{Component.literal(String.valueOf(cost)).append("/" + total)})
               .withStyle(formatting),
            mX,
            mY
         );
      } else {
         int slotClicked = this.getSlotUnderMouse(mX, mY);
         if (slotClicked >= 0 && mX <= this.leftPos + 106) {
            int id = slotClicked + this.listStartIndex;
            if (id < this.filtered.size()) {
               Holder<Enchantment> enchantment = this.filtered.get(id);
               graphics.renderTooltip(
                  this.font,
                  MutableComponent.create(((Enchantment)enchantment.value()).description().getContents()).withStyle(this.getColor(enchantment)),
                  mX,
                  mY
               );
            }
         }
      }
   }

   private ChatFormatting getColor(Holder<Enchantment> enchantment) {
      ChatFormatting color;
      if (enchantment.is(EnchantmentTags.CURSE)) {
         color = ChatFormatting.RED;
      } else if (enchantment.is(TensuraTags.Enchantments.ENGRAVING)) {
         color = ChatFormatting.GOLD;
      } else {
         color = ChatFormatting.AQUA;
      }

      return color;
   }

   protected void containerTick() {
      if (((ResearcherEnchantingMenu)this.menu).getPlayer().tickCount % 20 == 0) {
         this.updateFilteredEnchantments();
      }
   }

   private void renderEnchantments(GuiGraphics graphics, int mX, int mY) {
      List<Holder<Enchantment>> enchantments = this.filtered;
      ItemEnchantments itemEnchantments = this.getAllEnchantments();
      int pos = 0;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < enchantments.size(); i++) {
         Holder<Enchantment> enchantment = enchantments.get(i);
         Component name = ((Enchantment)enchantment.value()).description();
         ChatFormatting color = this.getColor(enchantment);
         ItemEnchantments map = IResearcherEnchanter.getSelectedEnchantments(
            ((ResearcherEnchantingMenu)this.menu).getPlayer(), ((ResearcherEnchantingMenu)this.menu).getSkill()
         );
         int offsetY = pos * 13;
         int selectedLevel = map.getLevel(enchantment);
         boolean checkBoxHovered = false;
         boolean levelBoxHovered = false;
         boolean barHovered = RenderHelper.mouseOver(mX, mY, this.leftPos + 21, this.leftPos + 107, this.topPos + 67 + offsetY, this.topPos + 80 + offsetY);
         if (!barHovered) {
            checkBoxHovered = RenderHelper.mouseOver(mX, mY, this.leftPos + 8, this.leftPos + 21, this.topPos + 67 + offsetY, this.topPos + 80 + offsetY);
            if (!checkBoxHovered) {
               levelBoxHovered = RenderHelper.mouseOver(mX, mY, this.leftPos + 107, this.leftPos + 122, this.topPos + 67 + offsetY, this.topPos + 80 + offsetY);
            }
         }

         int nameBarOffsetY = barHovered ? 13 : 0;
         int checkBoxUvOffsetX = checkBoxHovered ? 13 : 0;
         int levelBoxUvOffsetY = levelBoxHovered && selectedLevel > 0 ? 13 : 0;
         int checkBoxUvOffsetY = selectedLevel > 0 && !((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty() ? 13 : 0;
         graphics.blit(NAME_BAR, this.leftPos + 22, this.topPos + 67 + offsetY, 0.0F, nameBarOffsetY, 86, 13, 85, 26);
         graphics.blit(LEVEL_BOX, this.leftPos + 107, this.topPos + 67 + pos * 13, 0.0F, levelBoxUvOffsetY, 15, 13, 15, 26);
         graphics.blit(CHECKBOX, this.leftPos + 8, this.topPos + 67 + offsetY, checkBoxUvOffsetX, checkBoxUvOffsetY, 13, 13, 26, 26);
         RenderHelper.drawShortenedText(
            graphics, this.font, name, this.leftPos + 27, this.topPos + 70 + offsetY, 80, color.getColor() == null ? 0 : color.getColor(), true
         );
         if (selectedLevel == 0) {
            selectedLevel = itemEnchantments.getLevel(enchantment);
         }

         Component levelComponent = selectedLevel <= 10
            ? Component.translatable("enchantment.level." + selectedLevel)
            : Component.literal(String.valueOf(selectedLevel));
         RenderHelper.drawCenteredText(graphics, this.font, levelComponent, this.leftPos + 115, this.topPos + 70 + pos * 13, 11184810, false);
         pos++;
      }
   }

   private int getSlotUnderMouse(double mX, double mY) {
      for (int i = 0; i < this.getScrollBarRenderCount(); i++) {
         int y = this.topPos + 67 + i * 13;
         if (RenderHelper.mouseOver(mX, mY, this.leftPos + 21, this.leftPos + 122, y, y + 13)) {
            return i;
         }
      }

      return -1;
   }

   private ItemEnchantments getAllEnchantments() {
      ItemEnchantments enchantments = ((ResearcherEnchantingMenu)this.menu).getAllEnchantments();
      if (this.listStartIndex > enchantments.size()) {
         this.listStartIndex = 0;
      }

      return enchantments;
   }

   protected void updateFilteredEnchantments() {
      if (this.nameFilter != null && !this.nameFilter.isEmpty() && !this.nameFilter.isBlank()) {
         Predicate<Holder<Enchantment>> predicate = instance -> {
            Component name = ((Enchantment)instance.value()).description();
            return name.getString().toLowerCase().contains(this.nameFilter.toLowerCase());
         };
         this.filtered = new ArrayList<>();
         this.filtered.addAll(IResearcherEnchanter.getSortedEnchantmentList(((ResearcherEnchantingMenu)this.menu).getAllEnchantments(), predicate));
      } else {
         this.filtered = IResearcherEnchanter.getSortedEnchantmentList(((ResearcherEnchantingMenu)this.menu).getAllEnchantments());
      }
   }

   public boolean mouseClicked(double mX, double mY, int pButton) {
      if (!this.searchBar.isHovered()) {
         this.searchBar.setFocused(false);
      }

      if (this.clickedScrollBar(mX, mY)) {
         return true;
      }

      if (!RenderHelper.mouseOver(mX, mY, this.leftPos + 4, this.leftPos + 28, this.topPos + 2, this.topPos + 23)
         || !((ResearcherEnchantingMenu)this.menu).clickMenuButton(this.minecraft.player, -1)) {
         if (RenderHelper.mouseOver(mX, mY, this.leftPos + 145, this.leftPos + 165, this.topPos + 44, this.topPos + 62)
            && !((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty()) {
            if (!((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty()
               && ((ResearcherEnchantingMenu)this.menu).getExperienceCost()
                  <= ResearcherEnchantingMenu.getTotalXp(((ResearcherEnchantingMenu)this.menu).getPlayer())) {
               ScreenHelper.clicked(SoundEvents.ENCHANTMENT_TABLE_USE);
               NetworkManager.sendToServer(new RequestResearcherEnchantingPacket(-9, 0, true));
               return true;
            } else {
               return false;
            }
         } else {
            int slotClicked = this.getSlotUnderMouse(mX, mY);
            if (!((ResearcherEnchantingMenu)this.menu).getItemInput().isEmpty() && slotClicked >= 0) {
               int selected = slotClicked + this.listStartIndex;
               if (selected >= this.filtered.size()) {
                  return super.mouseClicked(mX, mY, pButton);
               }

               ItemEnchantments mapAll = this.getAllEnchantments();
               List<Holder<Enchantment>> list = IResearcherEnchanter.getSortedEnchantmentList(mapAll);
               int id = list.indexOf(this.filtered.get(selected));
               if (!mapAll.isEmpty() && id < mapAll.entrySet().size() && mX > this.leftPos + 107) {
                  Holder<Enchantment> enchantment = list.get(id);
                  ItemEnchantments map = IResearcherEnchanter.getSelectedEnchantments(
                     ((ResearcherEnchantingMenu)this.menu).getPlayer(), ((ResearcherEnchantingMenu)this.menu).getSkill()
                  );
                  if (enchantment != null && map.getLevel(enchantment) > 0) {
                     int level = map.getLevel(enchantment) + 1;
                     int maxEnchantLevel = Math.min(
                        ((ResearcherEnchantingMenu)this.menu).getMaxStoredLevel(enchantment),
                        ((ResearcherEnchantingMenu)this.menu).getMaxEnchantLevel(enchantment, ((ResearcherEnchantingMenu)this.menu).getPlayer())
                     );
                     if (level > maxEnchantLevel) {
                        level = 1;
                     }

                     NetworkManager.sendToServer(new RequestResearcherEnchantingPacket(id, level, false));
                     ScreenHelper.clicked();
                  }

                  return true;
               } else {
                  NetworkManager.sendToServer(new RequestResearcherEnchantingPacket(id, 0, false));
                  ScreenHelper.clicked(SoundEvents.ITEM_FRAME_ADD_ITEM);
                  return true;
               }
            } else {
               return super.mouseClicked(mX, mY, pButton);
            }
         }
      } else {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         ScreenHelper.clicked();
         this.minecraft.gameMode.handleInventoryButtonClick(((ResearcherEnchantingMenu)this.menu).containerId, -1);
         return true;
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDeltaX, double pDeltaY) {
      this.scrolledScrollBar(pDeltaY);
      return super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return this.draggedScrollBar(pMouseY) ? true : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.searchBar.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.searchBar.isFocused() && this.searchBar.isVisible() && pKeyCode != 256) {
         return true;
      } else {
         return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
            ? true
            : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   @Override
   public int getScrollBarX() {
      return this.leftPos + 126;
   }

   @Override
   public int getScrollBarY() {
      return this.topPos + 67;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 65;
   }

   @Override
   public int getScrollBarListSize() {
      return this.filtered.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 5;
   }

   @Generated
   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   @Override
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   public int getListStartIndex() {
      return this.listStartIndex;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }
}
