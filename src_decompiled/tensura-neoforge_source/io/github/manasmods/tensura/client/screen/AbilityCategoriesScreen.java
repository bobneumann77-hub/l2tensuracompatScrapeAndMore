package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class AbilityCategoriesScreen extends SimpleScreen {
   public static final Component TITLE = ScreenHelper.getMenuName(1);
   protected static final ResourceLocation BACKGROUND_SKILL = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/skill_gui.png");
   protected static final ResourceLocation BACKGROUND_MAGIC = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/magic_gui.png");
   protected static final ResourceLocation PRESET_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/preset_button.png");
   private static final String skillPath = "textures/gui/skill/";
   protected static final ResourceLocation RESISTANCE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/resistance.png");
   protected static final ResourceLocation COMMON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/common.png");
   protected static final ResourceLocation INTRINSIC = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/intrinsic.png");
   protected static final ResourceLocation EXTRA = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/extra.png");
   protected static final ResourceLocation UNIQUE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/unique.png");
   protected static final ResourceLocation ULTIMATE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/ultimate.png");
   private static final String magicPath = "textures/gui/magic/";
   protected static final ResourceLocation ASPECTUAL = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/aspectual_magic_button.png");
   protected static final ResourceLocation SPIRITUAL = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/spiritual_magic_button.png");
   protected static final ResourceLocation SUMMONING = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/summoning_magic_button.png");
   protected static final ResourceLocation MISCELLANEOUS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/misc_magic_button.png");
   protected final AbilityCategoriesScreen.AbilityType abilityType;
   protected final EditBox presetName;
   protected final SimpleButton returnButton;
   protected final List<String> presetNames;
   protected Component title;
   protected IAbility abilityData;
   protected ManasSkill slot1;
   protected ManasSkill slot2;
   protected ManasSkill slot3;
   protected ManasSkillInstance slot1Instance;
   protected ManasSkillInstance slot2Instance;
   protected ManasSkillInstance slot3Instance;
   protected int activePreset;
   protected int selectedPreset;
   private int interactionCd;

   public AbilityCategoriesScreen(AbilityCategoriesScreen.AbilityType abilityType) {
      super(TITLE, 254, 163);
      this.abilityType = abilityType;
      this.title = (Component)(abilityType == AbilityCategoriesScreen.AbilityType.SKILL ? TITLE : Component.translatable("tensura.magic_menu"));
      this.abilityData = TensuraStorages.getAbilityFrom(this.player);
      this.activePreset = this.abilityData.getActivePreset();
      this.presetNames = this.abilityData.getAllPresetNames();
      this.selectedPreset = this.activePreset;
      this.shouldRenderWidgets = false;
      this.presetName = new EditBox(Minecraft.getInstance().font, 0, 0, 94, 13, Component.empty());
      this.presetName.setBordered(false);
      this.presetName.setTextColor(16777215);
      this.presetName.setMaxLength(20);
      this.returnButton = new SimpleButton(
         0,
         0,
         18,
         10,
         null,
         null,
         Component.translatable("tooltip.tensura.return"),
         self -> ScreenHelper.clicked(SoundEvents.UI_BUTTON_CLICK, () -> ScreenHelper.openScreen(4))
      );
      this.loadPreset();
   }

   @Override
   protected void init() {
      super.init();
      guiLeft--;
      int tab = this.abilityType == AbilityCategoriesScreen.AbilityType.SKILL ? 1 : 2;
      List<SimpleButton> tabs = ScreenHelper.getMenuTabs(tab, guiLeft, guiTop);
      tabs.forEach(x$0 -> {
         SimpleButton var10000 = (SimpleButton)this.addRenderableWidget(x$0);
      });
      this.presetName.setX(guiLeft + 130);
      this.presetName.setY(guiTop + 63);
      this.returnButton.setX(guiLeft + 5);
      this.returnButton.setY(guiTop + 28);
      this.addRenderableWidget(this.presetName);
      this.addRenderableWidget(this.returnButton);

      for (int i = 0; i < 9; i++) {
         int j = i;
         SimpleButton button = new SimpleButton(guiLeft + 128 + i * 10, guiTop + 40, 10, 11, PRESET_BUTTON, null, (Component)null, self -> {
            if (j == this.selectedPreset && this.activePreset != this.selectedPreset) {
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(j - this.activePreset, true));
               this.activePreset = this.selectedPreset;
            } else {
               this.savePresetName();
               this.selectedPreset = j;
               this.loadPreset();
            }
         });
         button.setUvOffsetCheck(() -> this.selectedPreset == j || button.isHovered());
         this.addRenderableWidget(button);
      }

      if (this.abilityType == AbilityCategoriesScreen.AbilityType.SKILL) {
         this.addSkillCategories();
      } else {
         this.addMagicCategories();
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      if (this.interactionCd > 0) {
         this.interactionCd--;
      }

      graphics.drawString(this.font, this.title, guiLeft + 105, guiTop + 10, Color.WHITE.getRGB());
      if (this.abilityType == AbilityCategoriesScreen.AbilityType.SKILL) {
         graphics.blit(BACKGROUND_SKILL, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      } else {
         graphics.blit(BACKGROUND_MAGIC, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      }

      super.renderWidgets(graphics, mX, mY, partialTick);

      int id = switch (this.abilityType) {
         case SKILL -> 1;
         case MAGIC -> 2;
         case BATTLEWILL -> 3;
      };
      ScreenHelper.renderTabIcon(graphics, this, id, guiLeft, guiTop, mX, mY);

      for (int i = 0; i < 9; i++) {
         int color = i == this.activePreset ? Color.GREEN.getRGB() : Color.LIGHT_GRAY.getRGB();
         Component text = Component.literal(String.valueOf(i + 1));
         RenderHelper.drawCenteredText(graphics, this.font, text, guiLeft + 128 + i * 10, guiTop + 42, 10, color, false);
         if (i < 3) {
            int xName = guiLeft + 148;
            int yName = guiTop + 88 + i * 22;
            ManasSkill manasSkill = i == 0 ? this.slot1 : (i == 1 ? this.slot2 : this.slot3);
            MutableComponent name = this.skillName(manasSkill);
            TextColor nameColor = name.getStyle().getColor();
            if (nameColor == null) {
               nameColor = TextColor.fromRgb(16711680);
            }

            if (manasSkill == null) {
               name = name.setStyle(name.getStyle());
               graphics.drawString(this.font, name.withStyle(ChatFormatting.ITALIC), xName, yName, 8750469);
            } else {
               ResourceLocation icon = manasSkill.getSkillIcon();
               ManasSkillInstance instance = this.getSkillInstance(manasSkill);
               if (instance != null && instance.getMastery() < 0.0) {
                  name = name.withColor(8750469);
               }

               boolean hoveringName = RenderHelper.mouseOver(mX, mY, guiLeft + 146, guiLeft + 220, guiTop + 84 + i * 22, guiTop + 99 + i * 22);
               boolean hoveringIcon = RenderHelper.mouseOver(mX, mY, guiLeft + 126, guiLeft + 146, guiTop + 81 + i * 22, guiTop + 101 + i * 22);
               RenderHelper.drawScrollingText(graphics, this.font, name, xName, yName, 69, nameColor.getValue());
               if (icon != null) {
                  graphics.blit(icon, guiLeft + 128, guiTop + 83 + i * 22, 0.0F, 0.0F, 16, 16, 16, 16);
               }

               if (hoveringName || hoveringIcon) {
                  MutableComponent hoverMessage = manasSkill instanceof TensuraSkill skill ? skill.getColoredName() : manasSkill.getChatDisplayName(false);
                  ResourceLocation identifier = SkillAPI.getSkillRegistry().getId(manasSkill);
                  if (identifier != null) {
                     hoverMessage.append("\n").append(Component.literal(identifier.toString()).withColor(5592405));
                  }

                  this.setTooltipForNextRenderPass(hoverMessage);
               }
            }
         }
      }

      this.abilityData = TensuraStorages.getAbilityFrom(this.player);
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

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.presetName.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.presetName.isFocused() && this.presetName.isVisible() && keyCode != 256) {
         return true;
      } else if (this.minecraft != null && TensuraKeybinds.MAIN_GUI.matches(keyCode, scanCode)) {
         this.minecraft.setScreen(null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      if (!this.presetName.isHovered() && this.presetName.isFocused()) {
         this.savePresetName();
      }

      if (button == 2) {
         for (int i = 0; i < 3; i++) {
            boolean hoveringName = RenderHelper.mouseOver(mX, mY, guiLeft + 146, guiLeft + 220, guiTop + 84 + i * 22, guiTop + 99 + i * 22);
            boolean hoveringIcon = RenderHelper.mouseOver(mX, mY, guiLeft + 126, guiLeft + 146, guiTop + 81 + i * 22, guiTop + 101 + i * 22);
            if (hoveringName || hoveringIcon) {
               boolean changed = false;
               if (i == 0 && this.slot1 != null) {
                  this.slot1 = null;
                  changed = true;
               } else if (i == 1 && this.slot2 != null) {
                  this.slot2 = null;
                  changed = true;
               } else if (i == 2 && this.slot3 != null) {
                  this.slot3 = null;
                  changed = true;
               }

               if (changed) {
                  this.player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
                  NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeAbilityPacket(i, this.selectedPreset, null));
                  this.abilityData = TensuraStorages.getAbilityFrom(this.player);
                  return true;
               }
            }
         }
      }

      return super.mouseClicked(mX, mY, button);
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
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

   protected MutableComponent getSlotTooltip(int hoveredSlot, ManasSkill skill, ManasSkillInstance instance) {
      if (instance != null && skill instanceof TensuraSkill tensuraSkill) {
         Component component = tensuraSkill.getColoredName();
         return component == null
            ? null
            : component.copy()
               .append("\n")
               .append(tensuraSkill.getModeName(instance, this.abilityData.getAbilitySlot(this.selectedPreset, hoveredSlot - 1).getMode()));
      } else {
         return null;
      }
   }

   protected void handleScrolledSlot(int slotIndex, ManasSkill slot, ManasSkillInstance instance, boolean reverse) {
      if (this.interactionCd == 0) {
         if (instance.canInteractSkill(this.player) && slot instanceof TensuraSkill skill) {
            this.interactionCd = 10;
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeModePacket(slotIndex, reverse));
            int nextMode = skill.nextMode(this.player, instance, this.abilityData.getAbilitySlot(this.selectedPreset, slotIndex).getMode(), reverse);
            if (nextMode != -1) {
               this.abilityData.setAbilitySlot(this.selectedPreset, slotIndex, skill, nextMode);
            }
         }
      }
   }

   protected int getHoveredSlot(double mX, double mY) {
      int x1 = guiLeft + 146;
      int x2 = guiLeft + 220;
      if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 84, guiTop + 99)) {
         return 1;
      } else if (RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 106, guiTop + 121)) {
         return 2;
      } else {
         return RenderHelper.mouseOver(mX, mY, x1, x2, guiTop + 128, guiTop + 143) ? 3 : 0;
      }
   }

   protected void addSkillCategories() {
      for (int i = 0; i < 6; i++) {
         int j = i;
         Skill.SkillType type;
         ResourceLocation texture;
         switch (i) {
            case 0:
               type = Skill.SkillType.RESISTANCE;
               texture = RESISTANCE;
               break;
            case 1:
               type = Skill.SkillType.INTRINSIC;
               texture = INTRINSIC;
               break;
            case 2:
               type = Skill.SkillType.COMMON;
               texture = COMMON;
               break;
            case 3:
               type = Skill.SkillType.EXTRA;
               texture = EXTRA;
               break;
            case 4:
               type = Skill.SkillType.UNIQUE;
               texture = UNIQUE;
               break;
            default:
               type = Skill.SkillType.ULTIMATE;
               texture = ULTIMATE;
         }

         SimpleButton categoryButton = new SimpleButton(
            i % 2 == 0 ? guiLeft + 19 : guiLeft + 63,
            i < 2 ? guiTop + 45 : (i < 4 ? guiTop + 83 : guiTop + 121),
            32,
            32,
            texture,
            null,
            type.getName(),
            self -> ScreenHelper.clicked(() -> ScreenHelper.openAbilityScreen(this.abilityType, getSkillCategoryById(j), null))
         );
         this.addRenderableWidget(categoryButton);
      }
   }

   protected void addMagicCategories() {
      for (int i = 0; i < 4; i++) {
         int j = i;
         Magic.MagicType type;

         ResourceLocation texture = switch (i) {
            case 0 -> {
               type = Magic.MagicType.ASPECTUAL;
               yield ASPECTUAL;
            }
            case 1 -> {
               type = Magic.MagicType.SPIRITUAL;
               yield SPIRITUAL;
            }
            case 2 -> {
               type = null;
               yield ScreenHelper.COMING_SOON_ICON;
            }
            default -> {
               type = Magic.MagicType.SUMMONING;
               yield SUMMONING;
            }
         };
         SimpleButton categoryButton;
         if (type == null) {
            categoryButton = new SimpleButton(
               guiLeft + 19, guiTop + 83, 32, 32, texture, null, ScreenHelper.getComingSoonComponent(), self -> ScreenHelper.clicked()
            );
         } else {
            categoryButton = new SimpleButton(
               i % 2 == 0 ? guiLeft + 19 : guiLeft + 63,
               i < 2 ? guiTop + 45 : guiTop + 83,
               32,
               32,
               texture,
               null,
               type.getName(),
               self -> ScreenHelper.clicked(() -> ScreenHelper.openAbilityScreen(this.abilityType, null, getMagicCategoryById(j)))
            );
         }

         this.addRenderableWidget(categoryButton);
      }

      Magic.MagicType type = Magic.MagicType.MISC;
      ResourceLocation texture = MISCELLANEOUS;
      SimpleButton categoryButton = new SimpleButton(
         guiLeft + 41,
         guiTop + 121,
         32,
         32,
         texture,
         null,
         type.getName(),
         self -> ScreenHelper.clicked(() -> ScreenHelper.openAbilityScreen(this.abilityType, null, getMagicCategoryById(4)))
      );
      this.addRenderableWidget(categoryButton);
   }

   protected MutableComponent skillName(ManasSkill skill) {
      return skill != null && skill.getName() != null ? ((TensuraSkill)skill).getColoredName() : Component.translatable("tensura.skill.empty");
   }

   protected ManasSkillInstance getSkillInstance(ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(this.player).getSkill(skill);
      return instance.orElse(null);
   }

   protected void loadPreset() {
      this.presetName.setValue(this.presetNames.get(this.selectedPreset));
      this.slot1 = this.abilityData.getAbilitySlot(this.selectedPreset, 0).getSkill();
      this.slot2 = this.abilityData.getAbilitySlot(this.selectedPreset, 1).getSkill();
      this.slot3 = this.abilityData.getAbilitySlot(this.selectedPreset, 2).getSkill();
      Collection<ManasSkillInstance> abilities = SkillAPI.getSkillsFrom(this.player).getLearnedSkills();
      if (this.slot1 != null) {
         this.slot1Instance = abilities.stream()
            .filter(ability -> ability.getSkill().equals(this.slot1))
            .findFirst()
            .orElse(this.slot1.createDefaultInstance());
      }

      if (this.slot2 != null) {
         this.slot2Instance = abilities.stream()
            .filter(ability -> ability.getSkill().equals(this.slot2))
            .findFirst()
            .orElse(this.slot2.createDefaultInstance());
      }

      if (this.slot3 != null) {
         this.slot3Instance = abilities.stream()
            .filter(ability -> ability.getSkill().equals(this.slot3))
            .findFirst()
            .orElse(this.slot3.createDefaultInstance());
      }
   }

   protected void savePresetName() {
      String value = this.presetName.getValue();
      if (!value.isEmpty() && !value.isBlank()) {
         String name = this.abilityData.getPresetName(this.selectedPreset);
         if (!name.equals(value)) {
            this.presetNames.set(this.selectedPreset, value);
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.renamePresetPacket(this.selectedPreset, value));
            this.presetName.setFocused(false);
         }
      }
   }

   public static Skill.SkillType getSkillCategoryById(int id) {
      return switch (id) {
         case 0 -> Skill.SkillType.RESISTANCE;
         case 1 -> Skill.SkillType.INTRINSIC;
         case 2 -> Skill.SkillType.COMMON;
         case 3 -> Skill.SkillType.EXTRA;
         case 4 -> Skill.SkillType.UNIQUE;
         default -> Skill.SkillType.ULTIMATE;
      };
   }

   public static Magic.MagicType getMagicCategoryById(int id) {
      return switch (id) {
         case 0 -> Magic.MagicType.ASPECTUAL;
         case 1 -> Magic.MagicType.SPIRITUAL;
         case 2 -> null;
         case 3 -> Magic.MagicType.SUMMONING;
         default -> Magic.MagicType.MISC;
      };
   }

   @Override
   public void onClose() {
      super.onClose();
      if (this.presetName.isFocused()) {
         this.savePresetName();
      }
   }

   public enum AbilityType {
      SKILL,
      MAGIC,
      BATTLEWILL;
   }
}
