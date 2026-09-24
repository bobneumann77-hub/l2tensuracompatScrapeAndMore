package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.item.misc.ResetScrollItem;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.c2s.RequestEvolutionPacket;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class MainScreen extends SimpleScreen {
   public static final Component TITLE = ScreenHelper.getMenuName(4);
   protected final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/main/main_gui.png");
   protected final ResourceLocation TDL_AWAKENING = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/main/tdl_awakening.png");
   protected final ResourceLocation TH_AWAKENING = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/main/th_awakening.png");
   protected final ResourceLocation SETTINGS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/settings_tab_button.png");
   protected ITensuraPlayer playerData;
   protected IExistence existenceData;
   protected Races raceData;
   protected boolean canAwakenTDL;
   protected boolean canAwakenTH;

   public MainScreen() {
      super(TITLE, 256, 163);
      this.shouldRenderWidgets = false;
      this.playerData = TensuraStorages.getPlayerDataFrom(this.player);
      this.existenceData = TensuraStorages.getExistenceFrom(this.player);
      this.raceData = RaceAPI.getRaceFrom(this.player);
   }

   @Override
   protected void init() {
      super.init();
      if (this.minecraft != null && this.minecraft.getConnection() != null) {
         this.minecraft.getConnection().send(new ServerboundClientCommandPacket(Action.REQUEST_STATS));
      }

      List<SimpleButton> tabs = ScreenHelper.getMenuTabs(4, guiLeft, guiTop);
      tabs.add(
         new SimpleButton(
            guiLeft + 202, guiTop + 2, 24, 21, this.SETTINGS, null, Component.translatable("tensura.settings"), onPress -> ScreenHelper.openScreen(-1)
         )
      );
      tabs.forEach(x$0 -> {
         SimpleButton var10000 = (SimpleButton)this.addRenderableWidget(x$0);
      });
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      RenderHelper.drawCenteredText(graphics, this.font, TITLE, guiLeft + 80, guiTop + 10, 122, 16777215);
      graphics.blit(this.BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      super.renderWidgets(graphics, mX, mY, partialTick);
      ScreenHelper.renderTabIcon(graphics, this, 4, guiLeft, guiTop, mX, mY);
      this.canAwakenTDL = RaceUtils.canAwaken(this.player, false);
      this.canAwakenTH = RaceUtils.canAwaken(this.player, true);
      if (this.canAwakenTDL) {
         RenderHelper.renderWithTooltip(
            graphics,
            this.TDL_AWAKENING,
            guiLeft - 50,
            this.canAwakenTH ? guiTop + 52 : guiTop + 71,
            36,
            36,
            mX,
            mY,
            Component.translatable("tensura.main_menu.tdl_awaken").withColor(11141375),
            this
         );
      }

      if (this.canAwakenTH) {
         RenderHelper.renderWithTooltip(
            graphics,
            this.TH_AWAKENING,
            guiLeft - 50,
            this.canAwakenTDL ? guiTop + 94 : guiTop + 71,
            36,
            36,
            mX,
            mY,
            Component.translatable("tensura.main_menu.th_awaken").withColor(16766720),
            this
         );
      }

      float sizeMultiplier = this.player.getScale();
      sizeMultiplier = sizeMultiplier > 1.0F ? 1.0F / sizeMultiplier : 1.0F;
      RenderHelper.renderEntityInInventoryFollowsMouse(
         graphics, guiLeft + 6, guiTop + 55, guiLeft + 54, guiTop + 126, 30.0F * sizeMultiplier, mX, mY, false, this.player
      );
      this.renderPlayerStats(graphics, mX, mY);
      this.renderRaceStats(graphics, mX, mY);
      this.renderProgressBar(graphics, mX, mY);
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 62, guiLeft + 79, guiTop + 51, guiTop + 130)) {
         ScreenHelper.openScreen(-2);
         ScreenHelper.clicked();
         return true;
      }

      if (this.canAwakenTDL && this.canAwakenTH) {
         if (RenderHelper.mouseOver(mX, mY, guiLeft - 50, guiLeft - 14, guiTop + 52, guiTop + 88)) {
            NetworkManager.sendToServer(RequestEvolutionPacket.getAwakeningPacket(false));
            ScreenHelper.clicked();
            return true;
         }

         if (RenderHelper.mouseOver(mX, mY, guiLeft - 50, guiLeft - 14, guiTop + 94, guiTop + 130)) {
            NetworkManager.sendToServer(RequestEvolutionPacket.getAwakeningPacket(true));
            ScreenHelper.clicked();
            return true;
         }
      }

      if (RenderHelper.mouseOver(mX, mY, guiLeft - 50, guiLeft - 14, guiTop + 71, guiTop + 107)) {
         if (this.canAwakenTDL) {
            NetworkManager.sendToServer(RequestEvolutionPacket.getAwakeningPacket(false));
            ScreenHelper.clicked();
            return true;
         }

         if (this.canAwakenTH) {
            NetworkManager.sendToServer(RequestEvolutionPacket.getAwakeningPacket(true));
            ScreenHelper.clicked();
            return true;
         }
      }

      return super.mouseClicked(mX, mY, button);
   }

   protected void renderPlayerStats(GuiGraphics graphics, int mX, int mY) {
      Component name = (Component)(this.existenceData.getName() == null ? this.player.getName() : Component.literal(this.existenceData.getName()));
      int nameColor = Color.WHITE.getRGB();
      if (this.existenceData.isTrueDemonLord()) {
         nameColor = 11141375;
      } else if (this.existenceData.isTrueHero()) {
         nameColor = 16766720;
      }

      RenderHelper.drawCenteredText(graphics, this.font, name, guiLeft + 6, guiTop + 35, 72, nameColor, true);
      int resetCounter = Math.max(this.playerData.getResetCounter(), 0);
      ResetScrollItem.ResetCounterType type = ResetScrollItem.ResetCounterType.get(resetCounter);
      RenderHelper.drawCenteredTextWithTooltip(
         graphics,
         this.font,
         Component.literal(String.valueOf(resetCounter)),
         guiLeft + 133,
         guiTop - 11,
         18,
         type == ResetScrollItem.ResetCounterType.DIAMOND ? 5592405 : 16777215,
         true,
         mX,
         mY,
         Component.translatable("tensura.main_menu.reset_counter", new Object[]{resetCounter})
            .append(Component.literal("\n"))
            .append(this.getResetCounterProgress()),
         this
      );
      graphics.blit(type.getTextureLocation(), guiLeft + 126, guiTop - 31, 32, 32, 0.0F, 0.0F, 32, 32, 32, 32);
   }

   protected void renderRaceStats(GuiGraphics graphics, int mX, int mY) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(this.player).getRace();
      if (!optional.isEmpty()) {
         ManasRaceInstance race = optional.get();
         Alignment alignment = this.existenceData.getAlignment();
         String raceName = race.getDisplayName().getString();

         Color color = switch (alignment) {
            case CHAOS -> Color.RED;
            case MAJIN -> Color.MAGENTA;
            case HOLY -> Color.YELLOW;
            default -> Color.WHITE;
         };
         double mana = this.existenceData.getMagicule();
         double aura = this.existenceData.getAura();
         double maxMana = this.player.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
         double maxAura = this.player.getAttributeValue(TensuraAttributes.MAX_AURA);
         graphics.drawString(this.font, raceName, guiLeft + 95, guiTop + 36, color.getRGB());
         graphics.drawString(
            this.font,
            Component.translatable("tensura.main_menu.souls", new Object[]{this.existenceData.getSoulPoints() / 100 / 10.0}),
            guiLeft + 95,
            guiTop + 52,
            Color.WHITE.getRGB()
         );
         double EP = EnergyHelper.getMaxEP(this.player);
         RenderHelper.drawTextWithTooltip(
            graphics,
            this.font,
            Component.translatable("tensura.main_menu.existence_points", new Object[]{RenderHelper.getShortenedNumber(EP)}).getString(),
            guiLeft + 13,
            guiTop + 144,
            Color.ORANGE.getRGB(),
            mX,
            mY,
            Component.translatable("tensura.main_menu.existence_points", new Object[]{Math.round(EP)}),
            this
         );
         RenderHelper.drawTextWithTooltip(
            graphics,
            this.font,
            Component.translatable("tensura.main_menu.magicule", new Object[]{RenderHelper.getShortenedNumber(mana), RenderHelper.getShortenedNumber(maxMana)})
               .getString(),
            guiLeft + 95,
            guiTop + 78,
            Color.WHITE.getRGB(),
            mX,
            mY,
            Component.translatable("tensura.main_menu.magicule", new Object[]{Math.round(mana), Math.round(maxMana)}),
            this
         );
         RenderHelper.drawTextWithTooltip(
            graphics,
            this.font,
            Component.translatable("tensura.main_menu.aura", new Object[]{RenderHelper.getShortenedNumber(aura), RenderHelper.getShortenedNumber(maxAura)})
               .getString(),
            guiLeft + 95,
            guiTop + 99,
            Color.WHITE.getRGB(),
            mX,
            mY,
            Component.translatable("tensura.main_menu.aura", new Object[]{Math.round(aura), Math.round(maxAura)}),
            this
         );
      }
   }

   protected void renderProgressBar(GuiGraphics graphics, int mX, int mY) {
      boolean isHovered = RenderHelper.mouseOver(mX, mY, guiLeft + 62, guiLeft + 79, guiTop + 51, guiTop + 130);
      ManasRace trackedEvolution = this.playerData.getTrackedEvolution();
      Optional<ManasRaceInstance> optional = this.raceData.getRace();
      if (optional.isEmpty()) {
         if (isHovered) {
            this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.none").withColor(16733525));
         }
      } else {
         ManasRaceInstance race = optional.get();
         boolean hasNextEvolutions = !race.getNextEvolutions(this.player).isEmpty();
         if (!hasNextEvolutions) {
            if (isHovered) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.none").withColor(16733525));
            }
         } else if (trackedEvolution == null) {
            if (isHovered) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.select").withColor(5636095));
            }

            RenderHelper.highlightArea(graphics, guiLeft + 62, guiTop + 51, guiLeft + 79, guiTop + 130);
         } else {
            int evolutionProgress = (int)race.getEvolutionProgress(this.player, trackedEvolution);
            int fill = (int)(73 * evolutionProgress / 100.0F);
            int length = 73 - fill;
            int barXOffset = evolutionProgress == 100 ? 13 : 1;
            int barYOffset = 164 + length;
            int pX = guiLeft + 65;
            int pY = guiTop + 54 + length;
            graphics.blit(this.BACKGROUND, pX, pY, barXOffset, barYOffset, 11, Math.clamp(fill, 0, 73));
            if (isHovered) {
               if (evolutionProgress == 100) {
                  this.setTooltipForNextRenderPass(Component.translatable("tensura.main_menu.evolution_progress.ready").withColor(5635925));
               } else {
                  this.setTooltipForNextRenderPass(
                     Component.translatable("tensura.main_menu.evolution_progress", new Object[]{String.valueOf(evolutionProgress)})
                  );
               }
            }
         }
      }
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

   private Component getResetCounterProgress() {
      PlayerConfig.ResetScroll CONFIG = ReincarnationMenu.PLAYER_CONFIG.ResetScroll;
      MutableComponent component = Component.translatable("tensura.command.reset_counter.check_next").withStyle(ChatFormatting.AQUA);
      boolean full = true;
      if (CONFIG.raceCounter) {
         component.append("\n");
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(this.player).getRace();
         boolean met = optional.isPresent() && optional.get().getNextEvolutions(this.player).isEmpty();
         Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
         component.append(
            Component.translatable("tensura.command.reset_counter.check.race", new Object[]{metComponent})
               .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
         );
         if (!met) {
            full = false;
         }
      }

      if (CONFIG.awakenCounter) {
         component.append("\n");
         IExistence existence = TensuraStorages.getExistenceFrom(this.player);
         boolean met = existence.isTrueDemonLord() || existence.isTrueHero();
         Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
         component.append(
            Component.translatable("tensura.command.reset_counter.check.awakening", new Object[]{metComponent})
               .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
         );
         if (!met) {
            full = false;
         }
      }

      for (String string : CONFIG.bossesCounter) {
         EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(string));
         if (entityType != null) {
            component.append("\n");
            boolean met = this.player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get(entityType)) > 0;
            Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
            component.append(
               Component.translatable("tensura.command.reset_counter.check.boss", new Object[]{entityType.getDescription(), metComponent})
                  .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.YELLOW)
            );
            if (!met) {
               full = false;
            }
         }
      }

      return full
         ? Component.translatable(
               "tensura.command.reset_counter.check.full", new Object[]{((Item)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get()).getDescription()}
            )
            .withStyle(ChatFormatting.GREEN)
            .append("\n")
            .append("\n")
            .append(component)
         : component;
   }
}
