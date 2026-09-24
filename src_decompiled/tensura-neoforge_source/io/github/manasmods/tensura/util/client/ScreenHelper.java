package io.github.manasmods.tensura.util.client;

import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.client.screen.AbilityCategoriesScreen;
import io.github.manasmods.tensura.client.screen.AbilitySelectionScreen;
import io.github.manasmods.tensura.client.screen.EvolutionScreen;
import io.github.manasmods.tensura.client.screen.MainScreen;
import io.github.manasmods.tensura.client.screen.SettingsScreen;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class ScreenHelper {
   public static final ResourceLocation TOP_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/top_tab.png");
   public static final ResourceLocation RIGHT_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/right_tab.png");
   public static final ResourceLocation STATUS_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/main/status_tab.png");
   public static final ResourceLocation SKILL_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/skill_tab.png");
   public static final ResourceLocation MAGIC_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/magic_tab.png");
   public static final ResourceLocation BATTLEWILL_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/battlewill/battlewill_tab.png");
   public static final ResourceLocation COMING_SOON_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/coming_soon_tab.png");
   public static final ResourceLocation STATUS_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/main/status_tab_icon.png");
   public static final ResourceLocation SKILL_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/skill/skill_tab_icon.png");
   public static final ResourceLocation MAGIC_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/magic/magic_tab_icon.png");
   public static final ResourceLocation BATTLEWILL_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/battlewill/battlewill_tab_icon.png");
   public static final ResourceLocation COMING_SOON_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/coming_soon_button.png");
   public static final int EVOLUTIONS = -2;
   public static final int SETTINGS = -1;
   public static final int ABILITY_SELECTION = 0;
   public static final int SKILL_CATEGORIES = 1;
   public static final int MAGIC_CATEGORIES = 2;
   public static final int BATTLEWILL_CATEGORIES = 3;
   public static final int STATUS_MENU = 4;

   public static <T extends Screen> void openScreen(T screen) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null && !player.hasContainerOpen()) {
         minecraft.setScreen(screen);
      }
   }

   public static void openScreen(int id) {
      switch (id) {
         case -2:
            openScreen(new EvolutionScreen());
            break;
         case -1:
            openScreen(new SettingsScreen());
            break;
         case 0:
            openAbilityScreen(AbilityCategoriesScreen.AbilityType.SKILL, Skill.SkillType.UNIQUE, null);
            break;
         case 1:
            openCategoryScreen(AbilityCategoriesScreen.AbilityType.SKILL);
            break;
         case 2:
            openCategoryScreen(AbilityCategoriesScreen.AbilityType.MAGIC);
            break;
         case 3:
            openAbilityScreen(AbilityCategoriesScreen.AbilityType.BATTLEWILL, null, null);
            break;
         case 4:
            openScreen(new MainScreen());
      }
   }

   public static Component getComingSoonComponent() {
      Component component = Component.translatable("tooltip.tensura.coming_soon");
      String string = component.getString();
      return Component.literal(string.toUpperCase()).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BOLD).withColor(16733525);
   }

   public static void openAbilityScreen(AbilityCategoriesScreen.AbilityType abilityType, Skill.SkillType skillType, Magic.MagicType magicType) {
      AbilitySelectionScreen screen = new AbilitySelectionScreen(abilityType, skillType, magicType);
      openScreen(screen);
   }

   public static void openCategoryScreen(AbilityCategoriesScreen.AbilityType abilityType) {
      AbilityCategoriesScreen screen = new AbilityCategoriesScreen(abilityType);
      openScreen(screen);
   }

   public static void clicked(@Nullable SoundEvent event, @Nullable Runnable action) {
      if (event != null) {
         playSound(event);
      }

      if (action != null) {
         action.run();
      }
   }

   public static void clicked(Reference<SoundEvent> event, Runnable action) {
      clicked((SoundEvent)event.value(), action);
   }

   public static void clicked(Reference<SoundEvent> event) {
      clicked((SoundEvent)event.value(), null);
   }

   public static void clicked(SoundEvent event) {
      clicked(event, null);
   }

   public static void clicked(Runnable action) {
      clicked((SoundEvent)null, action);
   }

   public static void clicked() {
      clicked(SoundEvents.UI_BUTTON_CLICK, null);
   }

   public static void playSound(SoundEvent event, float volume) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, volume));
   }

   public static void playSound(SoundEvent event) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
   }

   public static void playSound(Reference<SoundEvent> event, float volume) {
      playSound((SoundEvent)event.value(), volume);
   }

   public static void playSound(Reference<SoundEvent> event) {
      playSound((SoundEvent)event.value(), 1.0F);
   }

   public static List<SimpleButton> getMenuTabs(int currentTab, int guiLeft, int guiTop) {
      List<SimpleButton> list = new ArrayList<>();

      for (int i = 1; i < 8; i++) {
         if (i != currentTab) {
            int j = i;
            if (i < 4) {
               int x = guiLeft + 4 + (i - 1) * 26;
               int y = guiTop + 2;
               SimpleButton button = new SimpleButton(x, y, 24, 21, getTabTexture(i), null, getMenuName(i), self -> clicked(() -> openScreen(j)));
               list.add(button);
            } else {
               int x = guiLeft + 233;
               int y = guiTop + 30 + (i - 4) * 26;
               SimpleButton button = new SimpleButton(x, y, 21, 24, getTabTexture(i), null, getMenuName(i), self -> clicked(() -> openScreen(j)));
               list.add(button);
            }
         }
      }

      return list;
   }

   public static Component getMenuName(int id) {
      return (Component)(switch (id) {
         case 0 -> Component.translatable("tensura.ability_selection");
         case 1 -> Component.translatable("tensura.skill_menu");
         case 2 -> Component.translatable("tensura.magic_menu");
         case 3 -> Component.translatable("tensura.battlewill_menu");
         case 4 -> Component.translatable("tensura.main_menu");
         default -> getComingSoonComponent();
      });
   }

   public static void renderTabIcon(GuiGraphics graphics, Screen screen, int id, int guiLeft, int guiTop, float mouseX, float mouseY) {
      boolean top = false;
      int x;
      int y;
      int tooltipX;
      int tooltipY;
      if (id < 4) {
         x = guiLeft + 8 + (id - 1) * 26;
         y = guiTop + 5;
         tooltipX = x - 5;
         tooltipY = guiTop - 1;
         top = true;
      } else {
         x = guiLeft + 234;
         y = guiTop + 34 + (id - 4) * 26;
         tooltipX = x - 2;
         tooltipY = y - 5;
      }

      int width = top ? 25 : 24;
      int height = top ? 24 : 25;
      if (id == 3) {
         graphics.blit(BATTLEWILL_ICON, x, y + 3, 0.0F, 0.0F, 16, 13, 16, 13);
      } else {
         graphics.blit(getTabIcon(id), x, y, 0.0F, 0.0F, 16, 16, 16, 16);
      }

      if (RenderHelper.mouseOver(mouseX, mouseY, tooltipX, tooltipX + width, tooltipY, tooltipY + height)) {
         screen.setTooltipForNextRenderPass(getMenuName(id));
      }
   }

   public static ResourceLocation getTabTexture(int id) {
      return switch (id) {
         case 1 -> SKILL_TAB;
         case 2 -> MAGIC_TAB;
         case 3 -> BATTLEWILL_TAB;
         case 4 -> STATUS_TAB;
         default -> COMING_SOON_TAB;
      };
   }

   public static ResourceLocation getTabIcon(int id) {
      return switch (id) {
         case 1 -> SKILL_ICON;
         case 2 -> MAGIC_ICON;
         case 3 -> BATTLEWILL_ICON;
         case 4 -> STATUS_ICON;
         default -> COMING_SOON_ICON;
      };
   }
}
