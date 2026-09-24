package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.menu.SkillCreationMenu;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class SkillCreationScreen extends AbstractContainerScreen<SkillCreationMenu> implements IScrollBar {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill_creation/skill_creation.png");
   protected static final ResourceLocation ABILITY_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/ability_button.png");
   private float scrollOffset;
   protected boolean scrolling;
   private int listStartIndex;
   private List<ManasSkill> filtered = new ArrayList<>();
   private ManasSkill selectedSkill = null;
   private int textStartIndex;
   private EditBox searchField;
   private String nameFilter = "";

   public SkillCreationScreen(SkillCreationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pMenu.getAbility().getName());
      this.imageWidth = 233;
      this.imageHeight = 140;
   }

   protected void init() {
      super.init();
      this.scrollOffset = 0.0F;
      this.listStartIndex = 0;
      this.searchField = new EditBox(this.font, this.leftPos + 19, this.topPos + 27, 79, 9, Component.empty());
      this.searchField.setBordered(false);
      this.searchField.setResponder(s -> {
         this.nameFilter = s;
         this.setScrollOffset(0.0F);
         this.setListStartIndex(0);
         this.updateFilteredSkills();
      });
      this.addRenderableWidget(this.searchField);
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchField.setValue(this.nameFilter);
      }
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   public void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, 56, this.titleLabelY, 16777215, false);
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int mX, int mY) {
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
      List<ManasSkill> filteredSkills = this.filtered;
      int lastVisibleElementIndex = Math.min(this.getListStartIndex() + this.getScrollBarRenderCount(), filteredSkills.size());
      this.renderButtons(guiGraphics, mX, mY, lastVisibleElementIndex, filteredSkills);
      this.renderScrollBar(guiGraphics, mX, mY);
      if (this.selectedSkill != null) {
         ResourceLocation location = this.selectedSkill.getSkillIcon();
         if (location != null) {
            guiGraphics.blit(location, this.leftPos + 156, this.topPos + 6, 0.0F, 0.0F, 32, 32, 32, 32);
         }

         Component description = this.selectedSkill.getSkillDescription();
         List<FormattedCharSequence> sequences = this.font.split(description, 94);
         int descriptionStart = Math.clamp(this.textStartIndex, 0, Math.max(0, sequences.size() - 7));
         int descriptionEnd = Math.min(descriptionStart + 7, sequences.size());
         this.textStartIndex = descriptionStart;
         boolean hoveringDescription = RenderHelper.mouseOver(mX, mY, this.leftPos + 125, this.leftPos + 219, this.topPos + 48, this.topPos + 108);
         RenderHelper.drawScrollableTextInAreaSetHighlight(
            guiGraphics,
            this.font,
            sequences,
            this.leftPos + 125,
            this.topPos + 48,
            94,
            60,
            0,
            descriptionStart,
            descriptionEnd,
            hoveringDescription,
            8750469,
            863042
         );
         if (mX > this.leftPos + 156 && mX < this.leftPos + 188 && mY > this.topPos + 6 && mY < this.topPos + 38) {
            MutableComponent hoverMessage = this.selectedSkill instanceof TensuraSkill skill
               ? skill.getColoredName()
               : this.selectedSkill.getChatDisplayName(false);
            ResourceLocation identifier = SkillAPI.getSkillRegistry().getId(this.selectedSkill);
            if (identifier != null) {
               hoverMessage.append("\n").append(Component.literal(identifier.toString()).withColor(5592405));
            }

            this.setTooltipForNextRenderPass(hoverMessage);
         }
      }
   }

   private void renderButtons(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int pLastVisibleElementIndex, List<ManasSkill> list) {
      for (int i = this.getListStartIndex(); i < pLastVisibleElementIndex && i < list.size(); i++) {
         int x = this.leftPos + 6;
         int y = this.topPos + 43 + (i - this.getListStartIndex()) * 13;
         int offset = 0;
         boolean hovering = pMouseX >= x && pMouseY >= y && pMouseX < x + 89 && pMouseY < y + 13;
         if (hovering) {
            offset = 13;
         }

         guiGraphics.blit(ABILITY_BAR, x, y, 0.0F, offset, 89, 13, 89, 26);
         ManasSkill manasSkill = list.get(i);
         MutableComponent name = this.getSkillName(manasSkill);
         RenderHelper.drawShortenedTextWithTooltip(
            guiGraphics, this.font, name, name, x, y, 3, 3, 88, 13, pMouseX, pMouseY, name.getStyle().getColor().getValue(), true, this
         );
      }

      if (pMouseX >= this.leftPos + 162
         && pMouseX < this.leftPos + 182
         && pMouseY >= this.topPos + 116
         && pMouseY < this.topPos + 136
         && this.selectedSkill != null) {
         guiGraphics.blit(BACKGROUND, this.leftPos + 162, this.topPos + 116, 0, this.imageHeight, 20, 20);
      }
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      if (x >= this.leftPos + 162 && x < this.leftPos + 182 && y >= this.topPos + 116 && y < this.topPos + 136 && this.selectedSkill != null) {
         guiGraphics.renderTooltip(this.font, Component.translatable("tensura.skill_creator.create_skill"), x, y);
      }
   }

   public boolean mouseClicked(double x, double y, int pButton) {
      if (this.clickedScrollBar(x, y)) {
         return true;
      }

      if (!this.searchField.isHovered()) {
         this.searchField.setFocused(false);
      }

      if (x >= this.leftPos + 162 && x < this.leftPos + 182 && y >= this.topPos + 116 && y < this.topPos + 136 && this.selectedSkill != null) {
         int id = ((SkillCreationMenu)this.menu).getSkills().indexOf(this.selectedSkill);
         if (id < 0 || id >= ((SkillCreationMenu)this.menu).getSkills().size()) {
            return false;
         }

         if (((SkillCreationMenu)this.menu).clickMenuButton(this.minecraft.player, id)) {
            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((SkillCreationMenu)this.menu).containerId, id);
            return true;
         }
      }

      List<ManasSkill> skills = this.filtered;
      int lastDisplayedIndex = Math.min(this.getListStartIndex() + this.getScrollBarRenderCount(), skills.size());

      for (int i = this.getListStartIndex(); i < lastDisplayedIndex; i++) {
         int pX = this.leftPos + 6;
         int pY = this.topPos + 43 + (i - this.getListStartIndex()) * 13;
         if (skills.size() <= i) {
            break;
         }

         if (x >= pX && y >= pY && x < pX + 89 && y < pY + 13) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.selectedSkill = skills.get(i);
            this.textStartIndex = 0;
            return true;
         }
      }

      return super.mouseClicked(x, y, pButton);
   }

   protected void updateFilteredSkills() {
      if (this.nameFilter != null && !this.nameFilter.isEmpty() && !this.nameFilter.isBlank()) {
         this.filtered = new ArrayList<>();
         Predicate<ManasSkill> predicate = instance -> instance.getName().getString().toLowerCase().contains(this.nameFilter.toLowerCase());
         this.filtered.addAll(((SkillCreationMenu)this.menu).getSkills().stream().filter(predicate).toList());
      } else {
         this.filtered = ((SkillCreationMenu)this.menu).getSkills();
      }

      this.scrolledScrollBar(0.0);
   }

   public boolean mouseScrolled(double mX, double mY, double pDeltaX, double pDeltaY) {
      if (RenderHelper.mouseOver(mX, mY, this.leftPos + 125, this.leftPos + 219, this.topPos + 43, this.topPos + 109)) {
         this.textStartIndex = Math.max(this.textStartIndex + (int)(-pDeltaY), 0);
      } else {
         this.scrolledScrollBar(pDeltaY);
      }

      return super.mouseScrolled(mX, mY, pDeltaX, pDeltaY);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return this.draggedScrollBar(pMouseY) ? true : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   private MutableComponent getSkillName(@Nullable ManasSkill skill) {
      if (skill == null || skill.getName() == null) {
         return Component.translatable("tensura.skill.empty");
      } else {
         return skill instanceof TensuraSkill tensuraSkill ? tensuraSkill.getColoredName() : skill.getName();
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.searchField.isFocused() && this.searchField.isVisible() && pKeyCode != 256) {
         return true;
      } else {
         return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
            ? true
            : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   @Override
   public int getScrollBarX() {
      return this.leftPos + 98;
   }

   @Override
   public int getScrollBarY() {
      return this.topPos + 43;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 91;
   }

   @Override
   public int getScrollBarListSize() {
      return this.filtered.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 7;
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
