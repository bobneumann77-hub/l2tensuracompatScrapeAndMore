package io.github.manasmods.tensura.client.screen;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.subclass.ISubAbilityModeHolder;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;

public class SubAbilityModeSelectionScreen<S extends ManasSkill & ISubAbilityModeHolder> extends SimpleScreen implements IScrollBar {
   protected static final ResourceLocation ABILITY_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/ability_button.png");
   protected static final ResourceLocation PRESET_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/preset_button.png");
   protected static final ResourceLocation COOLDOWN_CLOCK = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/gui/ability_selection/cooldown_clock.png"
   );
   protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/gui/sub_ability_selection/sub_ability_selection.png"
   );
   protected final List<AbilitySlot> abilities = new ArrayList<>();
   protected final List<AbilitySlot> filtered = new ArrayList<>();
   private final S parentAbility;
   protected AbilitySlot selectedAbility;
   protected ManasSkill slot1;
   protected ManasSkill slot2;
   protected ManasSkill slot3;
   protected ManasSkillInstance slot1Instance;
   protected ManasSkillInstance slot2Instance;
   protected ManasSkillInstance slot3Instance;
   protected ExpandedEditBox searchField;
   protected String nameFilter;
   protected float scrollOffset;
   protected boolean scrolling;
   protected boolean slotsDropdown;
   protected int listStartIndex;
   protected int textStartIndex;
   protected int activePreset;
   protected int selectedPreset;
   private int interactionCd;

   public SubAbilityModeSelectionScreen(ManasSkill skill) {
      super(skill.getName(), 233, 145);
      this.parentAbility = (S)skill;
   }

   private ManasSkillInstance getParentAbility() {
      return (ManasSkillInstance)SkillAPI.getSkillsFrom(this.player).getSkill(this.parentAbility).orElse(null);
   }

   @Override
   protected void init() {
      super.init();
      this.shouldRenderWidgets = false;
      this.activePreset = TensuraStorages.getAbilityFrom(this.player).getActivePreset();
      this.selectedPreset = this.activePreset;
      this.loadPreset();
      Component hint = Component.translatable("tensura.ability_selection.suggestion").withColor(11184810);
      List<Component> suggestions = new ArrayList<>(
         Arrays.asList(
            Component.literal("active"),
            Component.literal("passive"),
            Component.literal("mastered"),
            Component.literal("learned"),
            Component.literal("learning"),
            Component.literal("temporary"),
            Component.literal("toggleable"),
            Component.literal("toggledon"),
            Component.literal("toggledoff")
         )
      );
      this.searchField = new ExpandedEditBox(this.font, 0, 0, 79, 9, Component.empty());
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
      guiLeft--;
      this.searchField.setX(guiLeft + 19);
      this.searchField.setY(guiTop + 27);
      this.addRenderableWidget(this.searchField);
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchField.setValue(this.nameFilter);
      }

      for (int i = 0; i < 9; i++) {
         int j = i;
         SimpleButton button = new SimpleButton(guiLeft + 5 + i * 10, guiTop + 129, 10, 11, PRESET_BUTTON, null, (Component)null, self -> {
            if (j == this.selectedPreset && this.activePreset != this.selectedPreset) {
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(j - this.activePreset, true));
               this.activePreset = this.selectedPreset;
            } else {
               this.selectedPreset = j;
               this.loadPreset();
            }

            this.slotsDropdown = true;
         });
         button.setUvOffsetCheck(() -> this.selectedPreset == j || button.isHovered());
         this.addRenderableWidget(button);
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, guiLeft + 57, guiTop + 7, 16777215, true);
      graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      super.renderWidgets(graphics, mX, mY, partialTick);
      if (this.interactionCd > 0) {
         this.interactionCd--;
      }

      this.updateAllAbilities();
      this.updateFilteredAbilities();
      int update = this.abilities.indexOf(this.selectedAbility);
      if (update != -1) {
         this.selectedAbility = this.abilities.get(update);
      }

      int pX = guiLeft + 6;
      int pY = guiTop + 43;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < this.filtered.size(); i++) {
         AbilitySlot slot = this.filtered.get(i);
         ManasSkill manasSkill = slot.getSkill();
         MutableComponent name;
         if (manasSkill instanceof TensuraSkill skill) {
            ManasSkillInstance parent = this.getParentAbility();
            if (parent == null) {
               name = skill.getColoredName();
            } else {
               Map<ManasSkill, ManasSkillInstance> subInstances = parent.getSubInstances();
               ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
               if (Objects.equals(skill.getModeId(subInstance, slot.getMode()), "default")) {
                  name = skill.getColoredName();
               } else {
                  name = skill.getModeName(subInstance, slot.getMode()).copy().withStyle(skill.getColoredName().getStyle());
               }
            }
         } else {
            name = manasSkill.getName().withStyle(ChatFormatting.GRAY);
         }

         boolean hovered = RenderHelper.mouseOver(mX, mY, pX, pX + 89, pY, pY + 13);
         if (hovered) {
            this.setTooltipForNextRenderPass(name);
         }

         graphics.blit(ABILITY_BAR, pX, pY, 0.0F, hovered ? 13.0F : 0.0F, 89, 13, 89, 26);
         RenderHelper.drawShortenedText(graphics, this.font, name, pX + 3, pY + 3, 80, 0, true);
         pY += 13;
      }

      this.renderScrollBar(graphics, mX, mY);
      this.renderAbility(graphics, mX, mY);
      this.renderPreset(graphics, mX, mY);
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 197, guiLeft + 217, guiTop + 11, guiTop + 31) && this.selectedAbility != null) {
         graphics.blit(BACKGROUND, guiLeft + 197, guiTop + 11, 141, 146, 20, 20);
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      int pX = this.searchField.getX();
      int pY = this.searchField.getY();
      int sfW = this.searchField.getWidth();
      int sfH = this.searchField.getHeight();
      if (!RenderHelper.mouseOver(mX, mY, pX, pX + sfW, pY, pY + sfH)) {
         this.searchField.setFocused(false);
      }

      if (this.clickedScrollBar(mX, mY)) {
         return true;
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 96, guiLeft + 109, guiTop + 129, guiTop + 140)) {
         this.slotsDropdown = !this.slotsDropdown;
         ScreenHelper.clicked();
         return true;
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 197, guiLeft + 217, guiTop + 11, guiTop + 31) && this.selectedAbility != null) {
         this.abilities.remove(this.getSelectedModeIndex());
         this.updateFilteredAbilities();
         NetworkManager.sendToServer(
            RequestAbilityModeChangePacket.removeSubAbilityMode(
               this.parentAbility.getRegistryName(), this.selectedAbility.getSkill().getRegistryName().toString(), this.selectedAbility.getMode()
            )
         );
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(this.player).getSkill(this.parentAbility);
         optional.ifPresent(
            instance -> this.parentAbility.removeSubSkill(instance, this.player, this.selectedAbility.getSkill(), this.selectedAbility.getMode())
         );
         this.selectedAbility = null;
         this.loadPreset();
         this.player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
         ScreenHelper.clicked();
         return true;
      }

      pX = guiLeft + 6;
      pY = guiTop + 30;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < this.filtered.size(); i++) {
         pY += 13;
         if (RenderHelper.mouseOver(mX, mY, pX, pX + 89, pY, pY + 13)) {
            this.selectedAbility = this.filtered.get(i);
            this.textStartIndex = 0;
            ScreenHelper.clicked();
            return true;
         }
      }

      int hoveredSlot = this.getHoveredSlot(mX, mY);
      return hoveredSlot != 0 ? this.handleClickedSlot(hoveredSlot, button) : super.mouseClicked(mX, mY, button);
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      return this.draggedScrollBar(mY) ? true : super.mouseDragged(mX, mY, button, dragX, dragY);
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 5, guiLeft + 109, guiTop + 42, guiTop + 122)) {
         this.scrolledScrollBar(deltaY);
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 121, guiLeft + 223, guiTop + 40, guiTop + 119)) {
         this.textStartIndex = Math.max(this.textStartIndex + (int)(-deltaY), 0);
      }

      int hoveredSlot = this.getHoveredSlot(mX, mY);
      if (hoveredSlot != 0 && this.interactionCd == 0) {
         switch (hoveredSlot) {
            case 1:
               if (this.slot1 != null && this.slot1Instance.getModes() > 1) {
                  this.handleScrolledSlot(0, this.slot1, this.slot1Instance, deltaY < 0.0);
               }
               break;
            case 2:
               if (this.slot2 != null && this.slot2Instance.getModes() > 1) {
                  this.handleScrolledSlot(1, this.slot2, this.slot2Instance, deltaY < 0.0);
               }
               break;
            case 3:
               if (this.slot3 != null && this.slot3Instance.getModes() > 1) {
                  this.handleScrolledSlot(2, this.slot3, this.slot3Instance, deltaY < 0.0);
               }
         }

         return true;
      } else {
         return super.mouseScrolled(mX, mY, deltaX, deltaY);
      }
   }

   @Override
   public void renderTooltip(GuiGraphics graphics, int mX, int mY) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 197, guiLeft + 217, guiTop + 11, guiTop + 31) && this.selectedAbility != null) {
         this.setTooltipForNextRenderPass(Component.translatable("selectWorld.delete").withStyle(ChatFormatting.RED));
      } else {
         int hoveredSlot = this.getHoveredSlot(mX, mY);
         if (hoveredSlot != 0) {
            MutableComponent tooltip = null;
            switch (hoveredSlot) {
               case 1:
                  if (this.slot1Instance == null) {
                     return;
                  }

                  if (this.slot1 instanceof TensuraSkill skill) {
                     tooltip = skill.getColoredName();
                     if (tooltip == null) {
                        return;
                     }

                     tooltip.append("\n")
                        .append(
                           skill.getModeName(this.slot1Instance, TensuraStorages.getAbilityFrom(this.player).getAbilitySlot(this.selectedPreset, 0).getMode())
                        );
                  }
                  break;
               case 2:
                  if (this.slot2Instance == null) {
                     return;
                  }

                  if (this.slot2 instanceof TensuraSkill skill) {
                     tooltip = skill.getColoredName();
                     if (tooltip == null) {
                        return;
                     }

                     tooltip.append("\n")
                        .append(
                           skill.getModeName(this.slot2Instance, TensuraStorages.getAbilityFrom(this.player).getAbilitySlot(this.selectedPreset, 1).getMode())
                        );
                  }
                  break;
               case 3:
                  if (this.slot3Instance == null) {
                     return;
                  }

                  if (this.slot3 instanceof TensuraSkill skill) {
                     tooltip = skill.getColoredName();
                     if (tooltip == null) {
                        return;
                     }

                     tooltip.append("\n")
                        .append(
                           skill.getModeName(this.slot3Instance, TensuraStorages.getAbilityFrom(this.player).getAbilitySlot(this.selectedPreset, 2).getMode())
                        );
                  }
            }

            if (tooltip != null) {
               this.setTooltipForNextRenderPass(tooltip);
            }
         }
      }
   }

   protected void renderAbility(GuiGraphics graphics, double mX, double mY) {
      if (this.selectedAbility != null) {
         ManasSkill manasSkill = this.selectedAbility.getSkill();
         ManasSkillInstance instance = this.getSkillInstance(this.parentAbility);
         ResourceLocation icon = manasSkill.getSkillIcon();
         Component description = manasSkill.getSkillDescription();
         List<FormattedCharSequence> sequences = this.font.split(description, 94);
         int descriptionStart = Math.clamp(this.textStartIndex, 0, Math.max(0, sequences.size() - 7));
         int descriptionEnd = Math.min(descriptionStart + 8, sequences.size());
         this.textStartIndex = descriptionStart;
         boolean hoveringIcon = false;
         boolean hoveringDescription = false;
         boolean hoveringCooldownClock = false;
         int masteryX = guiLeft + 119;
         int masteryY = guiTop + 128;
         boolean hoveringMastery = RenderHelper.mouseOver(mX, mY, masteryX, masteryX + 106, masteryY, masteryY + 9);
         int mastery = (int)this.parentAbility.getModeMastery(instance, manasSkill, this.selectedAbility.getMode());
         int maxMastery = manasSkill.getMaxMastery();
         int points = mastery * 100 / maxMastery;
         mastery = 106 * mastery / maxMastery;
         Component masteryTooltip = Component.translatable("tensura.ability_selection.mastery", new Object[]{points + "%"});
         if (!hoveringMastery) {
            hoveringIcon = RenderHelper.mouseOver(mX, mY, guiLeft + 156, guiLeft + 188, guiTop + 6, guiTop + 38);
            if (!hoveringIcon) {
               hoveringDescription = RenderHelper.mouseOver(mX, mY, guiLeft + 121, guiLeft + 223, guiTop + 48, guiTop + 117);
               if (!hoveringDescription) {
                  hoveringCooldownClock = RenderHelper.mouseOver(mX, mY, guiLeft + 134, guiLeft + 146, guiTop + 15, guiTop + 29);
               }
            }
         }

         int cooldown = this.parentAbility.getModeCooldown(instance, manasSkill, this.selectedAbility.getMode());
         if (cooldown > 0) {
            graphics.blit(COOLDOWN_CLOCK, guiLeft + 134, guiTop + 15, 0.0F, 0.0F, 12, 14, 12, 14);
         }

         graphics.blit(BACKGROUND, masteryX, masteryY, 1, 202, mastery, 9);
         if (icon != null) {
            graphics.blit(icon, guiLeft + 156, guiTop + 6, 0.0F, 0.0F, 32, 32, 32, 32);
         }

         if (hoveringMastery) {
            this.setTooltipForNextRenderPass(masteryTooltip);
         } else if (hoveringIcon) {
            MutableComponent hoverMessage = manasSkill instanceof TensuraSkill skill ? skill.getColoredName() : manasSkill.getChatDisplayName(false);
            ResourceLocation identifier = SkillAPI.getSkillRegistry().getId(manasSkill);
            if (identifier != null) {
               hoverMessage.append("\n").append(Component.literal(identifier.toString()).withColor(5592405));
            }

            this.setTooltipForNextRenderPass(hoverMessage);
         } else if (cooldown > 0 && hoveringCooldownClock) {
            MutableComponent hoverMessage = Component.translatable("tensura.ability.on_cooldown", new Object[]{cooldown}).withColor(5636095);
            this.setTooltipForNextRenderPass(hoverMessage);
         }

         RenderHelper.drawScrollableTextInAreaSetHighlight(
            graphics, this.font, sequences, guiLeft + 123, guiTop + 48, 98, 69, 0, descriptionStart, descriptionEnd, hoveringDescription, 8750469, 863042
         );
      }
   }

   protected void renderPreset(GuiGraphics graphics, double mX, double mY) {
      int pX = guiLeft + 96;
      int pY = guiTop + 129;
      boolean hoveringDropdown = RenderHelper.mouseOver(mX, mY, pX, guiLeft + 109, pY, guiTop + 140);

      for (int i = 0; i < 9; i++) {
         int color = i == this.activePreset ? Color.GREEN.getRGB() : Color.LIGHT_GRAY.getRGB();
         Component text = Component.literal(String.valueOf(i + 1));
         RenderHelper.drawCenteredText(graphics, this.font, text, guiLeft + 5 + i * 10, guiTop + 131, 10, color, false);
      }

      if (this.slotsDropdown) {
         graphics.blit(BACKGROUND, guiLeft, guiTop + 142, 1, 148, 112, 54);

         for (int i = 0; i < 3; i++) {
            ManasSkill manasSkill = i == 0 ? this.slot1 : (i == 1 ? this.slot2 : this.slot3);
            MutableComponent name = this.skillName(manasSkill);
            TextColor color = name.getStyle().getColor();
            if (color == null) {
               color = TextColor.fromRgb(8750469);
            }

            int xName = guiLeft + 8;
            int yName = guiTop + 148 + i * 16;
            if (manasSkill == null) {
               name = name.setStyle(name.getStyle());
               graphics.drawString(this.font, name.withStyle(ChatFormatting.ITALIC), xName, yName, 8750469);
            } else {
               ManasSkillInstance instance = this.getSkillInstance(manasSkill);
               if (instance != null && instance.getMastery() < 0.0) {
                  name = name.withColor(8750469);
               }

               RenderHelper.drawScrollingText(graphics, this.font, name, xName, yName, 97, color.getValue());
            }
         }
      }

      graphics.blit(BACKGROUND, guiLeft + 96, guiTop + 129, this.slotsDropdown ? 114 : 127, hoveringDropdown ? 157 : 146, 13, 11);
   }

   private Pair<ManasSkill, Integer> getSlottedSkill(int slot) {
      ManasSkill manasSkill = slot == 0 ? this.slot1 : (slot == 1 ? this.slot2 : this.slot3);
      int skillMode = 0;
      if (manasSkill == this.parentAbility) {
         int mode = TensuraStorages.getAbilityFrom(this.player).getAbilitySlot(this.selectedPreset, slot).getMode();
         ManasSkillInstance instance = this.getParentAbility();
         int offset = this.parentAbility.getSubModeOffset(instance);
         if (mode >= offset && mode - offset >= 0 && mode - offset < this.abilities.size()) {
            AbilitySlot abilitySlot = this.abilities.get(mode - offset);
            manasSkill = abilitySlot.getSkill();
            skillMode = abilitySlot.getMode();
         }
      }

      return Pair.of(manasSkill, skillMode);
   }

   private int getSelectedModeIndex() {
      for (AbilitySlot slot : this.abilities) {
         if (slot.getSkill() == this.selectedAbility.getSkill() && slot.getMode() == this.selectedAbility.getMode()) {
            return this.abilities.indexOf(slot);
         }
      }

      return -1;
   }

   protected void handleScrolledSlot(int slotIndex, ManasSkill slot, ManasSkillInstance instance, boolean reverse) {
      if (this.interactionCd == 0) {
         if (instance.canInteractSkill(this.player) && slot instanceof TensuraSkill skill) {
            this.interactionCd = 10;
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeModePacket(slotIndex, reverse));
            IAbility data = TensuraStorages.getAbilityFrom(this.player);
            int nextMode = skill.nextMode(this.player, instance, data.getAbilitySlot(this.selectedPreset, slotIndex).getMode(), reverse);
            if (nextMode != -1) {
               data.setAbilitySlot(this.selectedPreset, slotIndex, skill, nextMode);
            }
         }
      }
   }

   protected boolean handleClickedSlot(int slot, int button) {
      if (button == 0) {
         if (this.selectedAbility == null) {
            return false;
         }

         ManasSkill manasSkill = this.selectedAbility.getSkill();
         Pair<ManasSkill, Integer> pair = this.getSlottedSkill(slot - 1);
         int index = this.getSelectedModeIndex();
         if (pair.getFirst() != manasSkill || (Integer)pair.getSecond() != this.selectedAbility.getMode()) {
            int offset = this.parentAbility.getSubModeOffset(this.getParentAbility());
            if (slot == 1) {
               this.slot1 = this.parentAbility;
               this.slot1Instance = this.getParentAbility();
               NetworkManager.sendToServer(
                  RequestAbilityModeChangePacket.changeAbilityPacket(0, this.selectedPreset, this.parentAbility.getRegistryName(), index + offset)
               );
               this.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            } else if (slot == 2) {
               this.slot2 = this.parentAbility;
               this.slot2Instance = this.getParentAbility();
               NetworkManager.sendToServer(
                  RequestAbilityModeChangePacket.changeAbilityPacket(1, this.selectedPreset, this.parentAbility.getRegistryName(), index + offset)
               );
               this.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            } else if (slot == 3) {
               this.slot3 = this.parentAbility;
               this.slot3Instance = this.getParentAbility();
               NetworkManager.sendToServer(
                  RequestAbilityModeChangePacket.changeAbilityPacket(2, this.selectedPreset, this.parentAbility.getRegistryName(), index + offset)
               );
               this.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            }
         }

         return true;
      } else {
         if (button != 2) {
            return false;
         }

         boolean changed = false;
         if (slot == 1 && this.slot1 != null) {
            this.slot1 = null;
            changed = true;
         } else if (slot == 2 && this.slot2 != null) {
            this.slot2 = null;
            changed = true;
         } else if (slot == 3 && this.slot3 != null) {
            this.slot3 = null;
            changed = true;
         }

         if (!changed) {
            return false;
         }

         this.player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
         NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeAbilityPacket(slot - 1, this.selectedPreset, null));
         return true;
      }
   }

   protected void updateFilteredAbilities() {
      this.filtered.clear();
      if (this.nameFilter != null && !this.nameFilter.isEmpty() && !this.nameFilter.isBlank()) {
         Predicate<? super AbilitySlot> predicate = slot -> {
            if (slot.getSkill() instanceof TensuraSkill skill) {
               ManasSkillInstance parentInstance = this.getParentAbility();
               ManasSkillInstance instance = (ManasSkillInstance)parentInstance.getSubInstances().get(skill);

               return switch (this.nameFilter) {
                  case "f:" -> false;
                  case "f:active" -> skill.canBeSlotted(instance, this.player, 0);
                  case "f:passive" -> !skill.canBeSlotted(instance, this.player, 0);
                  case "f:mastered" -> instance.isMastered(this.player);
                  case "f:learned" -> instance.getMastery() >= 0.0 || instance.isMastered(this.player);
                  case "f:learning" -> instance.getMastery() < 0.0;
                  case "f:temporary" -> instance.isTemporarySkill();
                  case "f:toggleable" -> instance.canBeToggled(this.player);
                  case "f:toggledon" -> instance.isToggled();
                  case "f:toggledoff" -> instance.canBeToggled(this.player) && !instance.isToggled();
                  default -> {
                     Component name = skill.getName();
                     yield name != null && name.getString().toLowerCase().contains(this.nameFilter.toLowerCase())
                        ? true
                        : skill.getModeName(instance, slot.getMode()).getString().toLowerCase().contains(this.nameFilter.toLowerCase());
                  }
               };
            } else {
               return false;
            }
         };
         this.filtered.addAll(this.abilities.stream().filter(predicate).toList());
      } else {
         this.filtered.addAll(this.abilities);
      }

      this.scrolledScrollBar(0.0);
   }

   protected void updateAllAbilities() {
      this.abilities.clear();
      this.abilities.addAll(ISubAbilityModeHolder.getSubSlots(this.getParentAbility()));
   }

   protected int getHoveredSlot(double mX, double mY) {
      if (!this.slotsDropdown) {
         return 0;
      } else {
         int x1 = guiLeft + 4;
         int x2 = guiLeft + 108;
         if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 146, guiTop + 161)) {
            return 1;
         } else if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 162, guiTop + 177)) {
            return 2;
         } else {
            return RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 178, guiTop + 193) ? 3 : 0;
         }
      }
   }

   protected void loadPreset() {
      IAbility ability = TensuraStorages.getAbilityFrom(this.player);
      this.slot1 = ability.getAbilitySlot(this.selectedPreset, 0).getSkill();
      this.slot2 = ability.getAbilitySlot(this.selectedPreset, 1).getSkill();
      this.slot3 = ability.getAbilitySlot(this.selectedPreset, 2).getSkill();
      if (this.slot1 != null) {
         this.slot1Instance = SkillAPI.getSkillsFrom(this.player).getSkill(this.slot1).orElse(this.slot1.createDefaultInstance());
      }

      if (this.slot2 != null) {
         this.slot2Instance = SkillAPI.getSkillsFrom(this.player).getSkill(this.slot2).orElse(this.slot2.createDefaultInstance());
      }

      if (this.slot3 != null) {
         this.slot3Instance = SkillAPI.getSkillsFrom(this.player).getSkill(this.slot3).orElse(this.slot3.createDefaultInstance());
      }
   }

   protected MutableComponent skillName(ManasSkill skill) {
      return skill != null && skill.getName() != null ? ((TensuraSkill)skill).getColoredName() : Component.translatable("tensura.skill.empty");
   }

   protected ManasSkillInstance getSkillInstance(ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(this.player).getSkill(skill);
      return instance.orElse(null);
   }

   @Override
   public int getScrollBarX() {
      return guiLeft + 98;
   }

   @Override
   public int getScrollBarY() {
      return guiTop + 43;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 78;
   }

   @Override
   public int getScrollBarListSize() {
      return this.filtered.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 6;
   }

   @Override
   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else {
         return this.searchField.isFocused() && this.searchField.isVisible() && pKeyCode != 256 ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
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
   public boolean isSlotsDropdown() {
      return this.slotsDropdown;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   public void setSlotsDropdown(boolean slotsDropdown) {
      this.slotsDropdown = slotsDropdown;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }

   @Generated
   public void setTextStartIndex(int textStartIndex) {
      this.textStartIndex = textStartIndex;
   }

   @Generated
   public void setActivePreset(int activePreset) {
      this.activePreset = activePreset;
   }

   @Generated
   public void setSelectedPreset(int selectedPreset) {
      this.selectedPreset = selectedPreset;
   }
}
