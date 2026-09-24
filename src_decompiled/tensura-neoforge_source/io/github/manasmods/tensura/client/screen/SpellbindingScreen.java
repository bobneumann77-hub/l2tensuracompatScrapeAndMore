package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.menu.SpellbindingMenu;
import io.github.manasmods.tensura.network.c2s.RequestSpellbindingPacket;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpellbindingScreen extends AbstractContainerScreen<SpellbindingMenu> implements IScrollBar {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spellbinding/spellbinding_table.png");
   private final List<ManasSkillInstance> filtered = new ArrayList<>();
   protected ExpandedEditBox searchField;
   protected String nameFilter = "";
   protected ManasSkillInstance selectedAbility;
   protected float scrollOffset;
   protected boolean scrolling;
   protected int listStartIndex;
   protected int textStartIndex;

   public SpellbindingScreen(SpellbindingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageWidth = 256;
      this.imageHeight = 191;
   }

   protected void init() {
      super.init();
      this.scrollOffset = 0.0F;
      this.listStartIndex = 0;
      this.textStartIndex = 0;
      Component hint = Component.translatable("tensura.ability_selection.suggestion").withColor(11184810);
      List<Component> suggestions = new ArrayList<>(
         Arrays.asList(
            Component.literal("using"),
            Component.literal("active"),
            Component.literal("passive"),
            Component.literal("mastered"),
            Component.literal("unmastered"),
            Component.literal("temporary")
         )
      );
      this.searchField = new ExpandedEditBox(this.font, 0, 0, 93, 10, Component.empty());
      this.searchField.setHint(hint);
      this.searchField.setBordered(false);
      this.searchField.setMultipleSuggestions(suggestions, "f:");
      this.searchField.setResponder(s -> {
         this.nameFilter = s;
         this.setScrollOffset(0.0F);
         this.setListStartIndex(0);
         this.updateFilteredAbilities();
      });
      this.searchField.setShouldRenderMultipleSuggestions(() -> this.searchField.getValue().startsWith("f:"));
      this.searchField.setX(this.leftPos + 68);
      this.searchField.setY(this.topPos + 21);
      if (this.nameFilter != null && !this.nameFilter.isEmpty()) {
         this.searchField.setValue(this.nameFilter);
      }

      this.addRenderableWidget(this.searchField);
      this.updateFilteredAbilities();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, 88, this.titleLabelY, 16777215, false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 26, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
      ItemStack stack = ((SpellbindingMenu)this.getMenu()).getBlockEntity().getItem(0);
      if (stack.isEmpty() || this.selectedAbility == null) {
         graphics.blit(BACKGROUND, x + 11, y + 65, 0.0F, 212.0F, 20, 20, 256, 256);
         graphics.blit(BACKGROUND, x + 38, y + 65, 20.0F, 212.0F, 20, 20, 256, 256);
      } else if (stack.is((Item)TensuraMaterialItems.UNBOUND_TOME.get())) {
         if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 11, this.leftPos + 31, this.topPos + 65, this.topPos + 85)) {
            graphics.blit(BACKGROUND, x + 11, y + 65, 0.0F, 192.0F, 20, 20, 256, 256);
         }

         graphics.blit(BACKGROUND, x + 38, y + 65, 20.0F, 212.0F, 20, 20, 256, 256);
      } else {
         List<ResourceLocation> skills = ((SpellbindingMenu)this.getMenu()).getExistingSkills(stack);
         boolean full = skills.size() >= SimpleSpellCastItem.getMagicSlots(((SpellbindingMenu)this.getMenu()).getPlayer().level(), stack);
         if (skills.contains(this.selectedAbility.getSkillId())) {
            if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 38, this.leftPos + 58, this.topPos + 65, this.topPos + 85)) {
               graphics.blit(BACKGROUND, x + 38, y + 65, 20.0F, 192.0F, 20, 20, 256, 256);
            }

            graphics.blit(BACKGROUND, x + 11, y + 65, 0.0F, 212.0F, 20, 20, 256, 256);
         } else {
            if (full) {
               graphics.blit(BACKGROUND, x + 11, y + 65, 0.0F, 212.0F, 20, 20, 256, 256);
            } else if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 11, this.leftPos + 31, this.topPos + 65, this.topPos + 85)) {
               graphics.blit(BACKGROUND, x + 11, y + 65, 0.0F, 192.0F, 20, 20, 256, 256);
            }

            graphics.blit(BACKGROUND, x + 38, y + 65, 20.0F, 212.0F, 20, 20, 256, 256);
         }
      }

      this.updateFilteredAbilities();
      if (this.selectedAbility != null) {
         int idx = ((SpellbindingMenu)this.getMenu()).getAbilities().indexOf(this.selectedAbility);
         if (idx != -1) {
            this.selectedAbility = ((SpellbindingMenu)this.getMenu()).getAbilities().get(idx);
         }
      }

      int listX = this.leftPos + 67;
      int listY = this.topPos + 34;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < this.filtered.size(); i++) {
         ManasSkillInstance instance = this.filtered.get(i);
         MutableComponent name;
         if (instance.getSkill() instanceof TensuraSkill tensuraSkill) {
            name = tensuraSkill.getColoredName();
         } else {
            name = instance.getDisplayName();
         }

         if (name != null) {
            String string = name.getString();
            TextColor color = name.getStyle().getColor();
            if (color == null) {
               color = TextColor.fromRgb(Color.WHITE.getRGB());
            }

            boolean hovered = RenderHelper.mouseOver(mouseX, mouseY, listX, listX + 89, listY, listY + 13);
            if (hovered) {
               this.setTooltipForNextRenderPass(name);
            }

            if (string.length() > 14) {
               string = string.substring(0, 13).concat("...");
               name = Component.literal(string).setStyle(name.getStyle());
            }

            if (((SpellbindingMenu)this.getMenu()).getUnlearntAbilities().contains(instance.getSkillId())) {
               name = Component.literal(string).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.STRIKETHROUGH});
            } else if (instance.getMastery() < 0.0) {
               name = name.copy().withColor(8750469);
            }

            boolean used = !stack.isEmpty() && ((SpellbindingMenu)this.getMenu()).getExistingSkills(stack).contains(instance.getSkillId());
            int offset = hovered ? 13 : (used ? 0 : 26);
            graphics.blit(BACKGROUND, listX, listY, 41.0F, 192 + offset, 89, 13, 256, 256);
            graphics.drawString(this.font, name, listX + 3, listY + 3, color.getValue());
            listY += 13;
         }
      }

      this.renderScrollBar(graphics, mouseX, mouseY);
      this.renderSelectedAbility(graphics, mouseX, mouseY);
   }

   protected void renderSelectedAbility(GuiGraphics graphics, double mX, double mY) {
      if (this.selectedAbility != null) {
         ManasSkill manasSkill = this.selectedAbility.getSkill();
         int iconX = this.leftPos + 199;
         int iconY = this.topPos + 12;
         int descX = this.leftPos + 184;
         int descY = this.topPos + 54;
         int descWidth = 61;
         int descHeight = 105;
         int masteryX = this.leftPos + 180;
         int masteryY = this.topPos + 170;
         int mastery = (int)this.selectedAbility.getMastery();
         int masteryUV = 233;
         Component masteryTooltip;
         if (mastery < 0) {
            int points = mastery + 100;
            if (manasSkill instanceof ResistSkill resistSkill) {
               points = mastery + resistSkill.getLearningPointRequirement();
               int learning = resistSkill.getLearningPointRequirement();
               mastery = 69 * (mastery + learning) / learning;
            } else {
               mastery = (int)(69 * (mastery + 100) / 100.0F);
            }

            masteryUV += 9;
            masteryTooltip = Component.translatable("tensura.ability_selection.learning", new Object[]{points});
         } else {
            int maxMastery = manasSkill.getMaxMastery();
            int points = mastery * 100 / maxMastery;
            mastery = 69 * mastery / maxMastery;
            masteryTooltip = Component.translatable("tensura.ability_selection.mastery", new Object[]{points + "%"});
         }

         Component description = manasSkill.getSkillDescription();
         if (description != null) {
            List<FormattedCharSequence> sequences = this.font.split(description, 62);
            int maxVisibleLines = 10;
            int descriptionStart = Math.clamp(this.textStartIndex, 0, Math.max(0, sequences.size() - maxVisibleLines));
            int descriptionEnd = Math.min(descriptionStart + maxVisibleLines, sequences.size());
            this.textStartIndex = descriptionStart;
            boolean hoveringIcon = RenderHelper.mouseOver(mX, mY, iconX, iconX + 24, iconY, iconY + 24);
            boolean hoveringDescription = RenderHelper.mouseOver(mX, mY, descX, descX + descWidth, descY, descY + descHeight);
            boolean hoveringMastery = RenderHelper.mouseOver(mX, mY, masteryX, masteryX + 69, masteryY, masteryY + 9);
            ResourceLocation icon = manasSkill.getSkillIcon();
            if (icon != null) {
               graphics.blit(icon, iconX, iconY, 0.0F, 0.0F, 32, 32, 32, 32);
            }

            graphics.blit(BACKGROUND, masteryX, masteryY, 0, masteryUV, mastery, 9);
            if (hoveringIcon) {
               MutableComponent hoverMessage = manasSkill instanceof TensuraSkill skill ? skill.getColoredName() : manasSkill.getChatDisplayName(false);
               ResourceLocation identifier = SkillAPI.getSkillRegistry().getId(manasSkill);
               if (identifier != null) {
                  hoverMessage.append("\n").append(Component.literal(identifier.toString()).withColor(5592405));
               }

               this.setTooltipForNextRenderPass(hoverMessage);
            } else if (hoveringMastery) {
               this.setTooltipForNextRenderPass(masteryTooltip);
            }

            RenderHelper.drawScrollableTextInAreaSetHighlight(
               graphics, this.font, sequences, descX, descY, descWidth, descHeight, 0, descriptionStart, descriptionEnd, hoveringDescription, 8750469, 4605510
            );
         }
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != 256) {
         return true;
      } else {
         return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(keyCode, scanCode)
            ? true
            : super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (!this.searchField.isMouseOver(mouseX, mouseY)) {
         this.searchField.setFocused(false);
      }

      if (this.clickedScrollBar(mouseX, mouseY)) {
         return true;
      }

      ItemStack stack = ((SpellbindingMenu)this.getMenu()).getBlockEntity().getItem(0);
      if (!stack.isEmpty() && this.selectedAbility != null) {
         if (stack.is((Item)TensuraMaterialItems.UNBOUND_TOME.get())) {
            if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 11, this.leftPos + 31, this.topPos + 65, this.topPos + 85)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               NetworkManager.sendToServer(new RequestSpellbindingPacket(this.selectedAbility.getSkillId(), true));
               return true;
            }
         } else {
            List<ResourceLocation> skills = ((SpellbindingMenu)this.getMenu()).getExistingSkills(stack);
            int slot = SimpleSpellCastItem.getMagicSlots(((SpellbindingMenu)this.getMenu()).getPlayer().level(), stack);
            if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 11, this.leftPos + 31, this.topPos + 65, this.topPos + 85)
               && !skills.contains(this.selectedAbility.getSkillId())
               && skills.size() < slot) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               NetworkManager.sendToServer(new RequestSpellbindingPacket(this.selectedAbility.getSkillId(), true));
               return true;
            }

            if (RenderHelper.mouseOver(mouseX, mouseY, this.leftPos + 38, this.leftPos + 58, this.topPos + 65, this.topPos + 85)
               && skills.contains(this.selectedAbility.getSkillId())) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               NetworkManager.sendToServer(new RequestSpellbindingPacket(this.selectedAbility.getSkillId(), false));
               return true;
            }
         }
      }

      int listX = this.leftPos + 67;
      int listY = this.topPos + 34;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < this.filtered.size(); i++) {
         int top = listY;
         int bottom = listY + 13;
         if (RenderHelper.mouseOver(mouseX, mouseY, listX, listX + 89, top, bottom)) {
            this.selectedAbility = this.filtered.get(i);
            this.textStartIndex = 0;
            if (this.minecraft != null) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            return true;
         }

         listY += 13;
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      if (RenderHelper.mouseOver(
         mouseX, mouseY, this.leftPos + 66, this.leftPos + 170, this.topPos + 33, this.topPos + 35 + 13 * this.getScrollBarRenderCount()
      )) {
         this.scrolledScrollBar(deltaY);
      }

      int descX = this.leftPos + 184;
      int descY = this.topPos + 48;
      int descWidth = 61;
      int descHeight = 93;
      if (RenderHelper.mouseOver(mouseX, mouseY, descX, descX + descWidth, descY, descY + descHeight)) {
         this.textStartIndex = Math.max(this.textStartIndex + (int)(-deltaY), 0);
      }

      return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      return this.draggedScrollBar(mouseY) ? true : super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   protected void updateFilteredAbilities() {
      this.filtered.clear();
      ItemStack stack = ((SpellbindingMenu)this.getMenu()).getBlockEntity().getItem(0);
      if (stack.is(TensuraItemTags.SPELL_BINDABLE)) {
         if (this.nameFilter != null && !this.nameFilter.isBlank()) {
            String filter = this.nameFilter.toLowerCase();
            Predicate<ManasSkillInstance> predicate = instance -> {
               if (stack.is((Item)TensuraMaterialItems.UNBOUND_TOME.get()) && !instance.isMastered(((SpellbindingMenu)this.getMenu()).getPlayer())) {
                  return false;
               }

               if (filter.startsWith("f:")) {
                  return switch (filter) {
                     case "f:using" -> !stack.isEmpty() && ((SpellbindingMenu)this.getMenu()).getExistingSkills(stack).contains(instance.getSkillId());
                     case "f:active" -> {
                        if (instance.getSkill() instanceof TensuraSkill skill
                           && skill.canBeSlotted(instance, ((SpellbindingMenu)this.getMenu()).getPlayer(), 0)) {
                           yield true;
                        }

                        yield false;
                     }
                     case "f:passive" -> {
                        if (instance.getSkill() instanceof TensuraSkill skill
                           && !skill.canBeSlotted(instance, ((SpellbindingMenu)this.getMenu()).getPlayer(), 0)) {
                           yield true;
                        }

                        yield false;
                     }
                     case "f:mastered" -> instance.isMastered(((SpellbindingMenu)this.getMenu()).getPlayer());
                     case "f:unmastered" -> instance.getMastery() >= 0.0 && !instance.isMastered(((SpellbindingMenu)this.getMenu()).getPlayer());
                     case "f:temporary" -> instance.isTemporarySkill();
                     default -> false;
                  };
               } else {
                  Component name = instance.getDisplayName();
                  return name == null ? false : name.getString().toLowerCase().contains(filter);
               }
            };
            this.filtered.addAll(((SpellbindingMenu)this.getMenu()).getAbilities().stream().filter(predicate).toList());
         } else if (!stack.is((Item)TensuraMaterialItems.UNBOUND_TOME.get())) {
            this.filtered.addAll(((SpellbindingMenu)this.getMenu()).getAbilities());
         } else {
            this.filtered
               .addAll(
                  ((SpellbindingMenu)this.getMenu())
                     .getAbilities()
                     .stream()
                     .filter(
                        instance -> !instance.is(TensuraSkillTags.TOME_COPY_EXCLUDED) && instance.isMastered(((SpellbindingMenu)this.getMenu()).getPlayer())
                     )
                     .toList()
               );
         }

         this.filtered.sort(Comparator.comparing(skill -> skill.getDisplayName().getString()));
      }

      this.scrolledScrollBar(0.0);
   }

   @Override
   public int getScrollBarListSize() {
      return this.filtered.size();
   }

   @Override
   public int getScrollBarX() {
      return this.leftPos + 159;
   }

   @Override
   public int getScrollBarY() {
      return this.topPos + 34;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 52;
   }

   @Override
   public int getScrollBarRenderCount() {
      return 4;
   }

   @Generated
   @Override
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }
}
