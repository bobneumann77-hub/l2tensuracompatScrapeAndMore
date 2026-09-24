package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.network.c2s.RequestEvolutionPacket;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

public class EvolutionScreen extends SimpleScreen implements IScrollBar {
   public static final Component TITLE = Component.translatable("tensura.evolution_menu");
   public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/evolution/evolution_gui.png");
   public static final ResourceLocation RACE_BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/ability_button.png");
   public static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/evolution/track_button.png");
   protected IExistence existenceData;
   protected ITensuraPlayer playerData;
   protected ManasRaceInstance playerRace;
   protected List<ManasRace> evolutions;
   protected List<ManasRace> copyOfEvolutions;
   protected ManasRace trackedEvolution;
   protected ManasRace selectedEvolution;
   protected String filter;
   protected float scrollOffset;
   protected boolean isScrolling;
   protected int startIndex;
   protected int actionCooldown;
   protected int textStartIndex;

   public EvolutionScreen() {
      super(TITLE, 256, 163);
      this.shouldRenderWidgets = false;
      this.playerData = TensuraStorages.getPlayerDataFrom(this.player);
      this.existenceData = TensuraStorages.getExistenceFrom(this.player);
      this.trackedEvolution = this.playerData.getTrackedEvolution();
      this.selectedEvolution = this.trackedEvolution;
      this.filter = "";
      this.evolutions = new ArrayList<>();
      this.copyOfEvolutions = new ArrayList<>();
   }

   @Override
   protected void init() {
      super.init();
      List<SimpleButton> tabs = ScreenHelper.getMenuTabs(4, guiLeft, guiTop);
      tabs.forEach(x$0 -> {
         SimpleButton var10000 = (SimpleButton)this.addRenderableWidget(x$0);
      });
      SimpleButton returnButton = new SimpleButton(
         guiLeft + 93,
         guiTop + 34,
         18,
         10,
         null,
         null,
         Component.translatable("tooltip.tensura.return"),
         self -> ScreenHelper.clicked(SoundEvents.UI_BUTTON_CLICK, () -> ScreenHelper.openScreen(4))
      );
      SimpleButton trackButton = new SimpleButton(
         guiLeft + 98,
         guiTop + 142,
         43,
         13,
         BUTTON,
         Component.translatable("tensura.evolution_menu.track"),
         Component.empty(),
         self -> ScreenHelper.clicked(SoundEvents.UI_BUTTON_CLICK, () -> {
            if (this.selectedEvolution != null) {
               NetworkManager.sendToServer(RequestEvolutionPacket.getSetTrackedEvolutionPacket(this.selectedEvolution.getRegistryName()));
               this.playerData.setTrackedEvolution(this.selectedEvolution);
               this.trackedEvolution = this.selectedEvolution;
               this.actionCooldown = 40;
            }
         })
      );
      SimpleButton evolveButton = new SimpleButton(
         guiLeft + 160,
         guiTop + 142,
         43,
         13,
         BUTTON,
         Component.translatable("tensura.evolution_menu.evolve"),
         Component.empty(),
         self -> ScreenHelper.clicked(SoundEvents.UI_BUTTON_CLICK, () -> {
            if (this.selectedEvolution != null) {
               NetworkManager.sendToServer(RequestEvolutionPacket.getEvolutionPacket(this.selectedEvolution.getRegistryName()));
               this.actionCooldown = 40;
            }
         })
      );
      ExpandedEditBox editBox = new ExpandedEditBox(this.font, guiLeft + 116, guiTop + 35, 103, 8, Component.empty());
      editBox.setValue(this.filter);
      editBox.setBordered(false);
      editBox.setResponder(
         s -> {
            this.filter = s;
            this.scrollOffset = 0.0F;
            this.startIndex = 0;
            this.copyOfEvolutions = new ArrayList<>(
               List.copyOf(
                  this.evolutions
                     .stream()
                     .filter(manasRace -> manasRace.getName() != null && manasRace.getName().getString().toLowerCase().contains(this.filter.toLowerCase()))
                     .sorted(Comparator.comparing(manasRace -> manasRace.getName().getString()))
                     .toList()
               )
            );
         }
      );
      trackButton.setUvOffsetCheck(() -> trackButton.isHovered() && this.actionCooldown == 0);
      trackButton.setClickedCheck(() -> trackButton.isHovered() && this.actionCooldown == 0);
      evolveButton.setUvOffsetCheck(() -> evolveButton.isHovered() && this.actionCooldown == 0);
      evolveButton.setClickedCheck(() -> evolveButton.isHovered() && this.actionCooldown == 0);
      this.addRenderableWidget(returnButton);
      this.addRenderableWidget(trackButton);
      this.addRenderableWidget(evolveButton);
      this.addRenderableWidget(editBox);
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      if (this.actionCooldown > 0) {
         this.actionCooldown--;
      }

      ManasRaceInstance manasRaceInstance = (ManasRaceInstance)RaceAPI.getRaceFrom(this.player).getRace().orElse(null);
      if (manasRaceInstance != null && !manasRaceInstance.equals(this.playerRace)) {
         this.filter = "";
         this.scrollOffset = 0.0F;
         this.startIndex = 0;
         this.playerRace = manasRaceInstance;
         this.trackedEvolution = this.playerData.getTrackedEvolution();
         this.selectedEvolution = this.trackedEvolution;
         this.evolutions = new ArrayList<>(this.playerRace.getNextEvolutions(this.player));
         this.copyOfEvolutions = new ArrayList<>(
            List.copyOf(
               this.evolutions
                  .stream()
                  .filter(manasRace -> manasRace.getName() != null && manasRace.getName().getString().toLowerCase().contains(this.filter.toLowerCase()))
                  .sorted(Comparator.comparing(manasRace -> manasRace.getName().getString()))
                  .toList()
            )
         );
      }

      graphics.drawString(this.font, TITLE, guiLeft + 105, guiTop + 10, 16777215);
      graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      super.renderWidgets(graphics, mX, mY, partialTick);
      ScreenHelper.renderTabIcon(graphics, this, 4, guiLeft, guiTop, mX, mY);
      float sizeMultiplier = this.player.getScale();
      sizeMultiplier = sizeMultiplier > 1.0F ? 1.0F / sizeMultiplier : 1.0F;
      RenderHelper.renderEntityInInventoryFollowsMouse(
         graphics, guiLeft + 6, guiTop + 55, guiLeft + 54, guiTop + 126, 30.0F * sizeMultiplier, mX, mY, false, this.player
      );
      this.renderScrollBar(graphics, mX, mY);
      this.renderPlayerStats(graphics, mX, mY);
      this.renderEvolutionsList(graphics, mX, mY);
      this.renderProgressBar(graphics, mX, mY);
      if (this.selectedEvolution instanceof TensuraRace tensuraRace && this.playerRace != null) {
         this.renderEvolution(graphics, tensuraRace, mX, mY);
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      if (this.clickedScrollBar(mX, mY)) {
         return true;
      }

      if (!this.copyOfEvolutions.isEmpty()) {
         for (int i = 0; i < 3; i++) {
            int offset = i * 13;
            if (RenderHelper.mouseOver(mX, mY, guiLeft + 94, guiLeft + 207, guiTop + 48 + offset, guiTop + 61 + offset)) {
               int index = this.startIndex + i;
               if (index >= this.copyOfEvolutions.size()) {
                  break;
               }

               ManasRace race = this.copyOfEvolutions.get(index);
               if (race != null) {
                  this.selectedEvolution = race;
                  this.textStartIndex = 0;
                  ScreenHelper.clicked();
                  return true;
               }
            }
         }
      }

      return super.mouseClicked(mX, mY, button);
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      return this.draggedScrollBar(mY) ? true : super.mouseDragged(mX, mY, button, dragX, dragY);
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 93, guiLeft + 222, guiTop + 47, guiTop + 88)) {
         this.scrolledScrollBar(deltaY);
         return true;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 97, guiLeft + 204, guiTop + 105, guiTop + 136)) {
         this.textStartIndex = Math.max(this.textStartIndex + (int)(-deltaY), 0);
         return true;
      } else {
         return super.mouseScrolled(mX, mY, deltaX, deltaY);
      }
   }

   protected void renderPlayerStats(GuiGraphics graphics, int mX, int mY) {
      Component name = (Component)(this.existenceData.getName() == null ? this.player.getName() : Component.literal(this.existenceData.getName()));
      int nameColor = Color.WHITE.getRGB();
      if (this.existenceData.isTrueDemonLord()) {
         nameColor = 11141375;
      } else if (this.existenceData.isTrueHero()) {
         nameColor = 16766720;
      }

      double maxMana = EnergyHelper.getMaxMagicule(this.player);
      double maxAura = EnergyHelper.getMaxAura(this.player);
      RenderHelper.drawCenteredText(graphics, this.font, name, guiLeft + 6, guiTop + 35, 72, nameColor, true);
      RenderHelper.drawTextWithTooltip(
         graphics,
         this.font,
         Component.translatable("tensura.main_menu.existence_points", new Object[]{RenderHelper.getShortenedNumber(maxMana + maxAura)}).getString(),
         guiLeft + 13,
         guiTop + 144,
         Color.ORANGE.getRGB(),
         mX,
         mY,
         Component.translatable("tensura.main_menu.existence_points", new Object[]{Math.round(maxMana + maxAura)}),
         this
      );
      int resetCounter = this.playerData.getResetCounter();
      if (resetCounter > 0) {
         RenderHelper.drawCenteredTextWithTooltip(
            graphics,
            this.font,
            Component.literal(String.valueOf(resetCounter)),
            guiLeft + 33,
            guiTop - 16,
            18,
            16777215,
            true,
            mX,
            mY,
            Component.translatable("tensura.main_menu.reset_counter", new Object[]{resetCounter}),
            this
         );
      }
   }

   protected void renderEvolutionsList(GuiGraphics graphics, int mX, int mY) {
      if (!this.copyOfEvolutions.isEmpty()) {
         int x = guiLeft + 94;
         int y = guiTop + 48;

         for (int index = this.startIndex; index < this.copyOfEvolutions.size() && index < this.startIndex + 3; index++) {
            boolean isHovered = RenderHelper.mouseOver(mX, mY, x, x + 113, y, y + 13);
            ManasRace race = this.copyOfEvolutions.get(index);
            Component name = race.getName();
            graphics.blit(RACE_BUTTON, x, y, 113, 13, 0.0F, isHovered ? 13.0F : 0.0F, 89, 13, 89, 26);
            if (name != null) {
               MutableComponent var10;
               if (race.equals(this.trackedEvolution)) {
                  var10 = name.copy().append(String.format(" (%s)", Component.translatable("tensura.evolution_menu.tracked").getString())).withColor(16766720);
               } else {
                  var10 = name.copy().withColor(16777215);
               }

               RenderHelper.drawScrollingText(graphics, this.font, var10, x + 3, y + 3, 105, var10.getStyle().getColor().getValue());
            }

            y += 13;
         }
      }
   }

   protected void renderEvolution(GuiGraphics graphics, TensuraRace race, int mX, int mY) {
      Map<EvolutionRequirement, Float> requirements = race.getEvolutionRequirements(this.playerRace, this.player);
      MutableComponent finalComponent = Component.empty();
      Component prefix = Component.literal("-> ");
      List<EvolutionRequirement> list = new ArrayList<>(
         requirements.keySet().stream().sorted(Comparator.comparing(key -> key.getRequirementComponent(this.playerRace, this.player).getString())).toList()
      );

      for (int index = 0; index < list.size(); index++) {
         EvolutionRequirement key = list.get(index);
         int progress = (int)Math.min(key.getProgress(this.playerRace, this.player) * 100.0F, 100.0F);
         Component completion = Component.literal(String.format(" [%s%%]", progress)).withColor(5635925);
         Component component = prefix.copy().append(key.getRequirementComponent(this.playerRace, this.player).copy().append(completion));
         finalComponent.append(component);
         if (index < list.size() - 1) {
            finalComponent.append("\n");
         }
      }

      List<FormattedCharSequence> sequences = this.font.split(finalComponent, 107);
      int descriptionStart = Math.clamp(this.textStartIndex, 0, Math.max(0, sequences.size() - 3));
      int descriptionEnd = Math.min(descriptionStart + 3, sequences.size());
      boolean hovered = RenderHelper.mouseOver(mX, mY, guiLeft + 97, guiLeft + 204, guiTop + 105, guiTop + 136);
      this.textStartIndex = descriptionStart;
      RenderHelper.drawCenteredText(
         graphics, this.font, Component.translatable("tensura.evolution_menu.requirements"), guiLeft + 95, guiTop + 94, 111, 16777215, false
      );
      RenderHelper.drawScrollableTextInAreaSetHighlight(
         graphics, this.font, sequences, guiLeft + 97, guiTop + 105, 107, 31, 1, descriptionStart, descriptionEnd, hovered, 16777215, 863042
      );
   }

   protected void renderProgressBar(GuiGraphics graphics, int mX, int mY) {
      boolean isHoveredTracked = RenderHelper.mouseOver(mX, mY, guiLeft + 62, guiLeft + 79, guiTop + 51, guiTop + 130);
      boolean isHoveredSelected = RenderHelper.mouseOver(mX, mY, guiLeft + 210, guiLeft + 222, guiTop + 90, guiTop + 154);
      if (this.playerRace == null) {
         if (isHoveredTracked || isHoveredSelected) {
            this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.none").withColor(16733525));
         }
      } else if (!this.playerRace.getNextEvolutions(this.player).isEmpty()) {
         if (this.trackedEvolution == null) {
            if (isHoveredTracked) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.select").withColor(5636095));
            }
         } else {
            int evolutionProgress = (int)this.playerRace.getEvolutionProgress(this.player, this.trackedEvolution);
            int fill = (int)(73 * evolutionProgress / 100.0F);
            int length = 73 - fill;
            int barYOffset = 164 + length;
            int pX = guiLeft + 65;
            int pY = guiTop + 54 + length;
            graphics.blit(BACKGROUND, pX, pY, 1, barYOffset, 11, Math.clamp(fill, 0, 73));
            if (isHoveredTracked) {
               if (evolutionProgress == 100) {
                  this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.ready").withColor(5635925));
               } else {
                  this.setTooltipForNextRenderPass(
                     Component.translatable("tensura.main_menu.evolution_progress", new Object[]{String.valueOf(evolutionProgress)})
                  );
               }
            }
         }

         if (this.selectedEvolution == null) {
            if (isHoveredSelected) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.select").withColor(5636095));
            }
         } else {
            int evolutionProgress = (int)this.playerRace.getEvolutionProgress(this.player, this.selectedEvolution);
            int fill = (int)(58 * evolutionProgress / 100.0F);
            int length = 58 - fill;
            int barYOffset = 164 + length;
            int pX = guiLeft + 213;
            int pY = guiTop + 93 + length;
            graphics.blit(BACKGROUND, pX, pY, 13, barYOffset, 6, Math.clamp(fill, 0, 58));
            if (isHoveredSelected) {
               if (evolutionProgress == 100) {
                  this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.ready").withColor(5635925));
               } else {
                  this.setTooltipForNextRenderPass(
                     Component.translatable("tensura.main_menu.evolution_progress", new Object[]{String.valueOf(evolutionProgress)})
                  );
               }
            }
         }
      } else {
         if (isHoveredTracked || isHoveredSelected) {
            this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.none").withColor(16733525));
         }
      }
   }

   @Override
   public int getScrollBarX() {
      return guiLeft + 211;
   }

   @Override
   public int getScrollBarY() {
      return guiTop + 49;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 38;
   }

   @Override
   public int getScrollBarListSize() {
      return this.copyOfEvolutions.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 3;
   }

   @Override
   public void setListStartIndex(int value) {
      this.startIndex = value;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.minecraft != null && TensuraKeybinds.MAIN_GUI.matches(keyCode, scanCode)) {
         this.minecraft.setScreen(null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
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
   public boolean isScrolling() {
      return this.isScrolling;
   }

   @Generated
   @Override
   public void setScrolling(boolean isScrolling) {
      this.isScrolling = isScrolling;
   }
}
