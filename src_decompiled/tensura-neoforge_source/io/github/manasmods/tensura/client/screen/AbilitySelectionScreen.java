package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.config.client.MenuConfig;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

public class AbilitySelectionScreen extends SimpleScreen implements IScrollBar {
   public static final Component TITLE = ScreenHelper.getMenuName(0);
   protected static final ResourceLocation CHECKBOX = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/checkbox.png");
   protected static final ResourceLocation ABILITY_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/ability_button.png");
   protected static final ResourceLocation PRESET_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/preset_button.png");
   protected static final ResourceLocation COOLDOWN_CLOCK = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/gui/ability_selection/cooldown_clock.png"
   );
   protected static final ResourceLocation LOCK = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/locks.png");
   protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/gui/ability_selection/ability_selection_gui.png"
   );
   protected final Skill.SkillType skillFilter;
   protected final Magic.MagicType magicFilter;
   protected final AbilityCategoriesScreen.AbilityType abilityType;
   public final List<ManasSkillInstance> abilities = new ArrayList<>();
   public final List<ManasSkillInstance> filtered = new ArrayList<>();
   protected SimpleButton returnButton;
   protected ExpandedEditBox searchField;
   public ManasSkill slot1;
   public ManasSkill slot2;
   public ManasSkill slot3;
   public ManasSkillInstance selectedAbility;
   public ManasSkillInstance slot1Instance;
   public ManasSkillInstance slot2Instance;
   public ManasSkillInstance slot3Instance;
   public int selectedPreset;
   protected String nameFilter;
   protected float scrollOffset;
   protected boolean scrolling;
   protected boolean slotsDropdown;
   protected int listStartIndex;
   protected int textStartIndex;
   protected int activePreset;
   private int interactionCd;

   public AbilitySelectionScreen(AbilityCategoriesScreen.AbilityType abilityType, Skill.SkillType skillFilter, Magic.MagicType magicFilter) {
      super(TITLE, 254, 163);
      this.abilityType = abilityType;
      this.skillFilter = skillFilter;
      this.magicFilter = magicFilter;
   }

   @Override
   protected void init() {
      super.init();
      this.shouldRenderWidgets = false;
      IAbility abilityData = TensuraStorages.getAbilityFrom(this.player);
      this.activePreset = abilityData.getActivePreset();
      this.selectedPreset = this.activePreset;
      this.loadPreset(abilityData);
      Component hint = Component.translatable("tensura.ability_selection.suggestion").withColor(11184810);
      List<Component> suggestions = new ArrayList<>(
         Arrays.asList(
            Component.literal("active"),
            Component.literal("passive"),
            Component.literal("mastered"),
            Component.literal("unmastered"),
            Component.literal("learned"),
            Component.literal("learning"),
            Component.literal("temporary"),
            Component.literal("toggleable"),
            Component.literal("toggledon"),
            Component.literal("toggledoff")
         )
      );
      ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(this.player);
      int per = this.player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
      if (playerData.getBonusSkillLock() > 0 || per > 0) {
         suggestions.add(Component.literal("locked"));
      }

      this.searchField = new ExpandedEditBox(this.font, 0, 0, 78, 9, Component.empty());
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

      int tab = switch (this.abilityType) {
         case SKILL -> 1;
         case MAGIC -> 2;
         default -> 4;
      };
      this.returnButton = new SimpleButton(
         0,
         0,
         18,
         10,
         null,
         null,
         Component.translatable("tooltip.tensura.return"),
         self -> ScreenHelper.clicked(SoundEvents.UI_BUTTON_CLICK, () -> ScreenHelper.openScreen(tab))
      );
      guiLeft--;

      int currentTab = switch (this.abilityType) {
         case SKILL -> 1;
         case MAGIC -> 2;
         case BATTLEWILL -> 3;
      };
      List<SimpleButton> tabs = ScreenHelper.getMenuTabs(currentTab, guiLeft, guiTop);
      tabs.forEach(x$0 -> {
         SimpleButton var10000 = (SimpleButton)this.addRenderableWidget(x$0);
      });
      this.searchField.setX(guiLeft + 28);
      this.searchField.setY(guiTop + 31);
      this.returnButton.setX(guiLeft + 5);
      this.returnButton.setY(guiTop + 28);
      this.addRenderableWidget(this.returnButton);
      this.addRenderableWidget(this.searchField);
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchField.setValue(this.nameFilter);
      }

      for (int i = 0; i < 9; i++) {
         int j = i;
         SimpleButton button = new SimpleButton(guiLeft + 5 + i * 10, guiTop + 147, 10, 11, PRESET_BUTTON, null, (Component)null, self -> {
            if (j == this.selectedPreset && this.activePreset != this.selectedPreset) {
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(j - this.activePreset, true));
               this.activePreset = this.selectedPreset;
            } else {
               this.selectedPreset = j;
               this.loadPreset(TensuraStorages.getAbilityFrom(this.player));
            }

            this.slotsDropdown = true;
         });
         button.setUvOffsetCheck(() -> this.selectedPreset == j || button.isHovered());
         this.addRenderableWidget(button);
      }

      this.slotsDropdown = ((MenuConfig)ConfigRegistry.getConfig(MenuConfig.class)).autoAbilitySlot;
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      graphics.drawString(this.font, TITLE, guiLeft + 105, guiTop + 10, Color.WHITE.getRGB());
      graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      super.renderWidgets(graphics, mX, mY, partialTick);

      int id = switch (this.abilityType) {
         case SKILL -> 1;
         case MAGIC -> 2;
         case BATTLEWILL -> 3;
      };
      ScreenHelper.renderTabIcon(graphics, this, id, guiLeft, guiTop, mX, mY);
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
      int pY = guiTop + 46;

      for (int i = this.listStartIndex; i < this.listStartIndex + this.getScrollBarRenderCount() && i < this.filtered.size(); i++) {
         ManasSkillInstance instance = this.filtered.get(i);
         ManasSkill manasSkill = instance.getSkill();
         Component name = ((TensuraSkill)manasSkill).getColoredName();
         if (name != null) {
            String string = name.getString();
            TextColor color = name.getStyle().getColor();
            if (color == null) {
               color = TextColor.fromRgb(16711680);
            }

            boolean hovered = RenderHelper.mouseOver(mX, mY, pX, pX + 89, pY, pY + 13);
            if (hovered) {
               this.setTooltipForNextRenderPass(name);
            }

            if (string.length() > 14) {
               string = string.substring(0, 14).concat("...");
               name = Component.literal(string).setStyle(name.getStyle());
            }

            if (instance.getMastery() < 0.0) {
               name = name.copy().withColor(8750469);
            }

            graphics.blit(ABILITY_BAR, pX, pY, 0.0F, hovered ? 13.0F : 0.0F, 89, 13, 89, 26);
            graphics.drawString(this.font, name, pX + 3, pY + 3, color.getValue());
            pY += 13;
         }
      }

      this.renderScrollBar(graphics, mX, mY);
      this.renderAbility(graphics, mX, mY);
      this.renderPreset(graphics, mX, mY);
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

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 96, guiLeft + 109, guiTop + 147, guiTop + 158)) {
         this.slotsDropdown = !this.slotsDropdown;
         ScreenHelper.clicked();
         return true;
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 198, guiLeft + 211, guiTop + 38, guiTop + 52)) {
         if (this.interactionCd > 0) {
            return false;
         }

         if (this.selectedAbility == null) {
            return false;
         }

         if (!this.selectedAbility.canBeToggled(this.player)) {
            return false;
         }

         if (!this.selectedAbility.canInteractSkill(this.player)) {
            return false;
         }

         ScreenHelper.clicked();
         NetworkManager.sendToServer(RequestAbilityModeChangePacket.toggleAbilityPacket(this.selectedAbility.getSkillId()));
         this.interactionCd = 10;
         return true;
      } else {
         if (this.selectedAbility != null) {
            ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(this.player);
            int top = playerData.getLockedSkills().contains(this.selectedAbility.getSkillId()) ? 37 : 39;
            if (RenderHelper.mouseOver(mX, mY, guiLeft + 215, guiLeft + 228, guiTop + top, guiTop + 53)) {
               if (this.interactionCd > 0) {
                  return false;
               }

               int per = this.player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
               boolean skillLockEnabled = playerData.getBonusSkillLock() > 0 || per > 0;
               if (!skillLockEnabled) {
                  return false;
               }

               ManasSkill manasSkill = this.selectedAbility.getSkill();
               if (playerData.getLockedSkills().contains(manasSkill.getRegistryName())) {
                  ScreenHelper.playSound(SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, 5.0F);
                  NetworkManager.sendToServer(RequestAbilityModeChangePacket.lockAbilityPacket(this.selectedAbility.getSkillId()));
                  this.interactionCd = 10;
                  return true;
               }

               int count = per > 0 ? playerData.getBonusSkillLock() + playerData.getResetCounter() / per : playerData.getBonusSkillLock();
               if (playerData.getLockedSkills().size() >= count) {
                  return false;
               }

               if (this.selectedAbility.getMastery() >= 0.0
                  && !this.selectedAbility.isTemporarySkill()
                  && RequestAbilityModeChangePacket.getLockableSkills(ReincarnationMenu.getSkillPool(), this.player.level()).contains(manasSkill)) {
                  ScreenHelper.playSound(SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, 5.0F);
                  NetworkManager.sendToServer(RequestAbilityModeChangePacket.lockAbilityPacket(this.selectedAbility.getSkillId()));
                  this.interactionCd = 10;
                  return true;
               }
            }
         }

         pX = guiLeft + 6;
         pY = guiTop + 33;

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
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      return this.draggedScrollBar(mY) ? true : super.mouseDragged(mX, mY, button, dragX, dragY);
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 5, guiLeft + 109, guiTop + 45, guiTop + 138)) {
         this.scrolledScrollBar(deltaY);
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft + 123, guiLeft + 221, guiTop + 78, guiTop + 135)) {
         this.textStartIndex = Math.max(this.textStartIndex + (int)(-deltaY), 0);
      }

      int hoveredSlot = this.getHoveredSlot(mX, mY);
      if (hoveredSlot != 0) {
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
      int hoveredSlot = this.getHoveredSlot(mX, mY);
      if (hoveredSlot != 0) {
         Component tooltip = null;
         switch (hoveredSlot) {
            case 1:
               tooltip = this.getSlotTooltip(hoveredSlot, this.slot1, this.slot1Instance);
               break;
            case 2:
               tooltip = this.getSlotTooltip(hoveredSlot, this.slot2, this.slot2Instance);
               break;
            case 3:
               tooltip = this.getSlotTooltip(hoveredSlot, this.slot3, this.slot3Instance);
         }

         if (tooltip != null) {
            this.setTooltipForNextRenderPass(tooltip);
         }
      }
   }

   protected void renderAbility(GuiGraphics graphics, double mX, double mY) {
      if (this.selectedAbility != null) {
         ManasSkill manasSkill = this.selectedAbility.getSkill();
         int mastery = (int)this.selectedAbility.getMastery();
         int masteryUV = 220;
         Component masteryTooltip;
         if (mastery < 0) {
            int points = mastery + 100;
            if (manasSkill instanceof ResistSkill resistSkill) {
               points = mastery + resistSkill.getLearningPointRequirement();
               int learning = resistSkill.getLearningPointRequirement();
               mastery = 106 * (mastery + learning) / learning;
            } else {
               mastery = (int)(106 * (mastery + 100) / 100.0F);
            }

            masteryUV += 9;
            masteryTooltip = Component.translatable("tensura.ability_selection.learning", new Object[]{points});
         } else {
            int maxMastery = manasSkill.getMaxMastery();
            int points = mastery * 100 / maxMastery;
            mastery = 106 * mastery / maxMastery;
            masteryTooltip = Component.translatable("tensura.ability_selection.mastery", new Object[]{points + "%"});
         }

         int masteryX = guiLeft + 119;
         int masteryY = guiTop + 146;
         int[] cooldowns = this.getModeCooldowns(this.selectedAbility);
         ResourceLocation icon = manasSkill.getSkillIcon();
         Component description = manasSkill.getSkillDescription();
         List<FormattedCharSequence> sequences = this.font.split(description, 94);
         Component tooltip = Component.translatable("tensura.ability_selection.untoggleable");
         int descriptionStart = Math.clamp(this.textStartIndex, 0, Math.max(0, sequences.size() - 7));
         int descriptionEnd = Math.min(descriptionStart + 7, sequences.size());
         this.textStartIndex = descriptionStart;
         ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(this.player);
         int per = this.player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
         boolean skillLockEnabled = playerData.getBonusSkillLock() > 0 || per > 0;
         boolean hasCooldowns = false;
         boolean isToggled = this.selectedAbility.isToggled();
         boolean canBeToggled = this.selectedAbility.canBeToggled(this.player);
         boolean canToggleAndInteract = canBeToggled && this.selectedAbility.canInteractSkill(this.player);
         boolean hoveringMastery = RenderHelper.mouseOver(mX, mY, masteryX, masteryX + 106, masteryY, masteryY + 9);
         boolean hoveringIcon = false;
         boolean hoveringToggle = false;
         boolean hoveringDescription = false;
         boolean hoveringCooldownClock = false;
         boolean hoveringLock = false;
         if (!hoveringMastery) {
            hoveringIcon = RenderHelper.mouseOver(mX, mY, guiLeft + 156, guiLeft + 188, guiTop + 29, guiTop + 61);
            if (!hoveringIcon) {
               hoveringToggle = RenderHelper.mouseOver(mX, mY, guiLeft + 198, guiLeft + 211, guiTop + 38, guiTop + 52);
               if (!hoveringToggle) {
                  hoveringDescription = RenderHelper.mouseOver(mX, mY, guiLeft + 123, guiLeft + 221, guiTop + 71, guiTop + 137);
                  if (!hoveringDescription) {
                     hoveringCooldownClock = RenderHelper.mouseOver(mX, mY, guiLeft + 134, guiLeft + 146, guiTop + 38, guiTop + 52);
                     if (!hoveringCooldownClock && skillLockEnabled) {
                        int top = playerData.getLockedSkills().contains(manasSkill.getRegistryName()) ? 37 : 39;
                        hoveringLock = RenderHelper.mouseOver(mX, mY, guiLeft + 215, guiLeft + 228, guiTop + top, guiTop + 53);
                     }
                  }
               }
            }
         }

         if (canBeToggled) {
            tooltip = Component.translatable("options." + (isToggled ? "on" : "off"));
         }

         graphics.blit(BACKGROUND, masteryX, masteryY, 1, masteryUV, mastery, 9);
         if (icon != null) {
            graphics.blit(icon, guiLeft + 156, guiTop + 29, 0.0F, 0.0F, 32, 32, 32, 32);
         }

         if (canToggleAndInteract) {
            graphics.blit(CHECKBOX, guiLeft + 198, guiTop + 39, hoveringToggle ? 13.0F : 0.0F, isToggled ? 13.0F : 0.0F, 13, 13, 26, 26);
         }

         if (skillLockEnabled) {
            if (playerData.getLockedSkills().contains(manasSkill.getRegistryName())) {
               if (hoveringLock) {
                  graphics.blit(LOCK, guiLeft + 215, guiTop + 39, 10.0F, 0.0F, 10, 13, 30, 29);
                  this.setTooltipForNextRenderPass(Component.translatable("tensura.ability_selection.unlock").withStyle(ChatFormatting.RED));
               } else {
                  graphics.blit(LOCK, guiLeft + 215, guiTop + 39, 0.0F, 0.0F, 10, 13, 30, 29);
               }
            } else if (mastery >= 0
               && !this.selectedAbility.isTemporarySkill()
               && RequestAbilityModeChangePacket.getLockableSkills(ReincarnationMenu.getSkillPool(), this.player.level()).contains(manasSkill)) {
               int count = per > 0 ? playerData.getBonusSkillLock() + playerData.getResetCounter() / per : playerData.getBonusSkillLock();
               if (playerData.getLockedSkills().size() >= count) {
                  graphics.blit(LOCK, guiLeft + 215, guiTop + 39, 20.0F, 0.0F, 10, 13, 30, 29);
                  if (hoveringLock) {
                     this.setTooltipForNextRenderPass(
                        Component.translatable("tensura.ability_selection.lock.not_enough", new Object[]{playerData.getLockedSkills().size(), count})
                           .withStyle(ChatFormatting.RED)
                     );
                  }
               } else if (hoveringLock) {
                  graphics.blit(LOCK, guiLeft + 215, guiTop + 37, 10.0F, 13.0F, 10, 16, 30, 29);
                  this.setTooltipForNextRenderPass(
                     Component.translatable("tensura.ability_selection.lock", new Object[]{playerData.getLockedSkills().size(), count})
                        .withStyle(ChatFormatting.GOLD)
                  );
               } else {
                  graphics.blit(LOCK, guiLeft + 215, guiTop + 37, 0.0F, 13.0F, 10, 16, 30, 29);
               }
            } else {
               graphics.blit(LOCK, guiLeft + 215, guiTop + 39, 20.0F, 0.0F, 10, 13, 30, 29);
               if (hoveringLock) {
                  this.setTooltipForNextRenderPass(Component.translatable("tensura.ability_selection.lock.wrong").withStyle(ChatFormatting.RED));
               }
            }
         }

         for (int cooldown : cooldowns) {
            if (cooldown > 0) {
               hasCooldowns = true;
               break;
            }
         }

         if (hasCooldowns) {
            graphics.blit(COOLDOWN_CLOCK, guiLeft + 134, guiTop + 38, 0.0F, 0.0F, 12, 14, 12, 14);
         }

         if (hoveringToggle) {
            this.setTooltipForNextRenderPass(tooltip);
         } else if (hoveringMastery) {
            this.setTooltipForNextRenderPass(masteryTooltip);
         } else if (hoveringIcon) {
            MutableComponent hoverMessage = manasSkill instanceof TensuraSkill skill ? skill.getColoredName() : manasSkill.getChatDisplayName(false);
            ResourceLocation identifier = SkillAPI.getSkillRegistry().getId(manasSkill);
            if (identifier != null) {
               hoverMessage.append("\n").append(Component.literal(identifier.toString()).withColor(5592405));
            }

            this.setTooltipForNextRenderPass(hoverMessage);
         } else if (hasCooldowns && hoveringCooldownClock && this.selectedAbility.getSkill() instanceof TensuraSkill skill) {
            boolean changed = false;
            MutableComponent hoverMessage = Component.translatable("tensura.ability_selection.cooldowns").withColor(5636095);

            for (int mode = 0; mode < cooldowns.length; mode++) {
               if (cooldowns[mode] != 0) {
                  changed = true;
                  hoverMessage.append(Component.literal("\n"))
                     .append(skill.getModeName(this.selectedAbility, mode))
                     .append(String.format(": %ds", cooldowns[mode]));
               }
            }

            if (changed) {
               this.setTooltipForNextRenderPass(hoverMessage);
            }
         }

         RenderHelper.drawScrollableTextInAreaSetHighlight(
            graphics, this.font, sequences, guiLeft + 123, guiTop + 71, 98, 64, 0, descriptionStart, descriptionEnd, hoveringDescription, 8750469, 863042
         );
      }
   }

   protected void renderPreset(GuiGraphics graphics, double mX, double mY) {
      int pX = guiLeft + 96;
      int pY = guiTop + 147;
      boolean hoveringDropdown = RenderHelper.mouseOver(mX, mY, pX, guiLeft + 109, pY, guiTop + 158);

      for (int i = 0; i < 9; i++) {
         int color = i == this.activePreset ? Color.GREEN.getRGB() : Color.LIGHT_GRAY.getRGB();
         Component text = Component.literal(String.valueOf(i + 1));
         RenderHelper.drawCenteredText(graphics, this.font, text, guiLeft + 5 + i * 10, guiTop + 149, 10, color, false);
      }

      if (this.slotsDropdown) {
         graphics.blit(BACKGROUND, guiLeft, guiTop + 160, 1, 164, 112, 55);

         for (int i = 0; i < 3; i++) {
            ManasSkill manasSkill = i == 0 ? this.slot1 : (i == 1 ? this.slot2 : this.slot3);
            MutableComponent name = this.skillName(manasSkill);
            TextColor color = name.getStyle().getColor();
            if (color == null) {
               color = TextColor.fromRgb(16711680);
            }

            int xName = guiLeft + 8;
            int yName = guiTop + 168 + i * 16;
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

      graphics.blit(BACKGROUND, guiLeft + 96, guiTop + 147, this.slotsDropdown ? 114 : 127, hoveringDropdown ? 175 : 164, 13, 11);
   }

   protected MutableComponent getSlotTooltip(int hoveredSlot, ManasSkill skill, ManasSkillInstance instance) {
      if (instance != null && skill instanceof TensuraSkill tensuraSkill) {
         Component component = tensuraSkill.getColoredName();
         return component == null
            ? null
            : component.copy()
               .append("\n")
               .append(
                  tensuraSkill.getModeName(instance, TensuraStorages.getAbilityFrom(this.player).getAbilitySlot(this.selectedPreset, hoveredSlot - 1).getMode())
               );
      } else {
         return null;
      }
   }

   protected void handleScrolledSlot(int slotIndex, ManasSkill slot, ManasSkillInstance instance, boolean reverse) {
      if (this.interactionCd == 0) {
         if (instance.canInteractSkill(this.player) && slot instanceof TensuraSkill skill) {
            this.interactionCd = 10;
            IAbility ability = TensuraStorages.getAbilityFrom(this.player);
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeModePacket(slotIndex, this.selectedPreset, reverse));
            int nextMode = skill.nextMode(this.player, instance, ability.getAbilitySlot(this.selectedPreset, slotIndex).getMode(), reverse);
            if (nextMode != -1) {
               ability.setAbilitySlot(this.selectedPreset, slotIndex, skill, nextMode);
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
         if (manasSkill instanceof TensuraSkill tensuraSkill) {
            if (!tensuraSkill.canBeSlotted(this.selectedAbility, this.player, 0)) {
               this.player.playSound(SoundEvents.ITEM_BREAK);
               return false;
            }

            if (slot == 1 && this.slot1 != manasSkill) {
               this.slot1 = manasSkill;
               this.slot1Instance = this.selectedAbility;
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeAbilityPacket(0, this.selectedPreset, this.selectedAbility.getSkillId()));
            } else if (slot == 2 && this.slot2 != manasSkill) {
               this.slot2 = manasSkill;
               this.slot2Instance = this.selectedAbility;
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeAbilityPacket(1, this.selectedPreset, this.selectedAbility.getSkillId()));
            } else if (slot == 3 && this.slot3 != manasSkill) {
               this.slot3 = manasSkill;
               this.slot3Instance = this.selectedAbility;
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeAbilityPacket(2, this.selectedPreset, this.selectedAbility.getSkillId()));
            }

            this.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            return true;
         } else {
            return false;
         }
      } else if (button == 1) {
         if (slot == 1 && this.slot1 != null) {
            this.selectedAbility = this.getSkillInstance(this.slot1);
         } else if (slot == 2 && this.slot2 != null) {
            this.selectedAbility = this.getSkillInstance(this.slot2);
         } else if (slot == 3 && this.slot3 != null) {
            this.selectedAbility = this.getSkillInstance(this.slot3);
         }

         this.player.playSound(SoundEvents.BOOK_PAGE_TURN);
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
         Predicate<? super ManasSkillInstance> predicate = instance -> {
            if (instance.getSkill() instanceof TensuraSkill skill) {
               return switch (this.nameFilter) {
                  case "f:" -> false;
                  case "f:active" -> skill.canBeSlotted(instance, this.player, 0);
                  case "f:passive" -> !skill.canBeSlotted(instance, this.player, 0);
                  case "f:mastered" -> instance.isMastered(this.player);
                  case "f:unmastered" -> instance.getMastery() >= 0.0 && !instance.isMastered(this.player);
                  case "f:learned" -> instance.getMastery() >= 0.0;
                  case "f:learning" -> instance.getMastery() < 0.0;
                  case "f:temporary" -> instance.isTemporarySkill();
                  case "f:toggleable" -> instance.canBeToggled(this.player);
                  case "f:toggledon" -> instance.isToggled();
                  case "f:toggledoff" -> instance.canBeToggled(this.player) && !instance.isToggled();
                  case "f:locked" -> TensuraStorages.getPlayerDataFrom(this.player).getLockedSkills().contains(instance.getSkillId());
                  default -> {
                     Component name = skill.getName();
                     yield name == null
                        ? true
                        : (
                           skill instanceof SpiritualMagic magic && magic.getElemental().getNamespace().toLowerCase().contains(this.nameFilter.toLowerCase())
                              ? true
                              : (
                                 skill instanceof AspectualMagic magic
                                       && magic.getAspectualType().getNamespace().toLowerCase().contains(this.nameFilter.toLowerCase())
                                    ? true
                                    : name.getString().toLowerCase().contains(this.nameFilter.toLowerCase())
                              )
                        );
                  }
               };
            } else {
               return false;
            }
         };
         this.filtered.addAll(this.abilities.stream().filter(predicate).toList());
      } else {
         switch (this.abilityType) {
            case SKILL: {
               Predicate<ManasSkillInstance> predicate = instance -> ((Skill)instance.getSkill()).getType() == this.skillFilter;
               this.filtered.addAll(this.abilities.stream().filter(predicate).toList());
               break;
            }
            case MAGIC: {
               Predicate<ManasSkillInstance> predicate = instance -> ((Magic)instance.getSkill()).getType() == this.magicFilter;
               this.filtered.addAll(this.abilities.stream().filter(predicate).toList());
               break;
            }
            case BATTLEWILL:
               this.filtered.addAll(this.abilities);
         }
      }

      this.filtered.sort(Comparator.comparing(skill -> skill.getDisplayName().getString()));
      this.scrolledScrollBar(0.0);
   }

   protected void updateAllAbilities() {
      this.abilities.clear();
      this.abilities.addAll(new ArrayList<>(SkillAPI.getSkillsFrom(this.player).getLearnedSkills()));
      this.abilities
         .removeIf(
            ability -> {
               if (ability.getDisplayName() == null) {
                  return true;
               } else if (this.abilityType == AbilityCategoriesScreen.AbilityType.SKILL) {
                  return !(ability.getSkill() instanceof Skill);
               } else {
                  return this.abilityType == AbilityCategoriesScreen.AbilityType.MAGIC
                     ? !(ability.getSkill() instanceof Magic)
                     : !(ability.getSkill() instanceof Battlewill);
               }
            }
         );
   }

   protected int[] getModeCooldowns(ManasSkillInstance instance) {
      int[] array = new int[instance.getModes()];

      for (int mode = 0; mode < instance.getModes(); mode++) {
         array[mode] = instance.getCoolDown(mode);
      }

      return array;
   }

   protected int getHoveredSlot(double mX, double mY) {
      if (!this.slotsDropdown) {
         return 0;
      } else {
         int x1 = guiLeft + 4;
         int x2 = guiLeft + 108;
         if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 164, guiTop + 179)) {
            return 1;
         } else if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 180, guiTop + 195)) {
            return 2;
         } else {
            return RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 196, guiTop + 211) ? 3 : 0;
         }
      }
   }

   protected void loadPreset(IAbility abilityData) {
      this.slot1 = abilityData.getAbilitySlot(this.selectedPreset, 0).getSkill();
      this.slot2 = abilityData.getAbilitySlot(this.selectedPreset, 1).getSkill();
      this.slot3 = abilityData.getAbilitySlot(this.selectedPreset, 2).getSkill();
      if (this.slot1 != null) {
         this.slot1Instance = this.abilities
            .stream()
            .filter(ability -> ability.getSkill().equals(this.slot1))
            .findFirst()
            .orElse(this.slot1.createDefaultInstance());
      }

      if (this.slot2 != null) {
         this.slot2Instance = this.abilities
            .stream()
            .filter(ability -> ability.getSkill().equals(this.slot2))
            .findFirst()
            .orElse(this.slot2.createDefaultInstance());
      }

      if (this.slot3 != null) {
         this.slot3Instance = this.abilities
            .stream()
            .filter(ability -> ability.getSkill().equals(this.slot3))
            .findFirst()
            .orElse(this.slot3.createDefaultInstance());
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
   public void setListStartIndex(int value) {
      this.listStartIndex = value;
   }

   @Override
   public void setScrolling(boolean value) {
      this.scrolling = value;
   }

   @Override
   public void setScrollOffset(float value) {
      this.scrollOffset = value;
   }

   @Override
   public int getScrollBarX() {
      return guiLeft + 98;
   }

   @Override
   public int getScrollBarY() {
      return guiTop + 46;
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

   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != 256) {
         return true;
      } else if (this.minecraft != null && TensuraKeybinds.MAIN_GUI.matches(keyCode, scanCode)) {
         this.minecraft.setScreen(null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }
}
