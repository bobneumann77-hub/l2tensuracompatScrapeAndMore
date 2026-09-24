package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.client.screen.widgets.DialogueWindow;
import io.github.manasmods.tensura.client.screen.widgets.ExpandedEditBox;
import io.github.manasmods.tensura.client.screen.widgets.ScrollBar;
import io.github.manasmods.tensura.client.screen.widgets.SimpleButton;
import io.github.manasmods.tensura.network.c2s.RequestSpatialActionPacket;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

public class SpatialMovementScreen<T extends ManasSkill & ISpatialMovement> extends SimpleScreen {
   protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial/space_gui.png");
   protected static final ResourceLocation TOP_TAB = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/spatial/top_tab.png");
   protected static final int COORDINATES_ACTION = 0;
   protected static final int X_ACTION = 1;
   protected static final int Y_ACTION = 2;
   protected static final int Z_ACTION = 3;
   protected static final int PORTAL_ACTION = 4;
   protected static final int WARP_ACTION = 5;
   protected static final int SAVE_ACTION = 6;
   protected final T skill;
   protected ManasSkillInstance instance;
   protected final List<String> dimensionLocations;
   protected final List<String> dimensionNames;
   protected ITensuraPlayer data;
   protected ScrollBar warpListScrollBar;
   protected ScrollBar dimensionListScrollBar;
   protected DialogueWindow dialogueWindow;
   protected SimpleButton saveWarp;
   protected SimpleButton deleteWarp;
   protected ExpandedEditBox fieldX;
   protected ExpandedEditBox fieldY;
   protected ExpandedEditBox fieldZ;
   protected ExpandedEditBox fieldD;
   protected ExpandedEditBox editName;
   protected ExpandedEditBox rename;
   protected ExpandedEditBox searchBar;
   protected int selectedWarp;
   protected int warnTick;
   protected int pointsY;
   protected int pointsHeight;
   protected int padsY;
   protected int padsHeight;
   protected boolean savingWarp;
   protected boolean deletedIndex;
   protected boolean warnedDelete;
   protected boolean dimensionDropdown;
   protected boolean pointsHovered;
   protected boolean padsHovered;
   protected String filter;
   protected static boolean viewingWarpPads;
   protected static String oX = "0.0";
   protected static String oY = "0.0";
   protected static String oZ = "0.0";
   protected static String oD = "Overworld";
   protected static String tooltipFieldD = "minecraft:overworld";
   protected final Supplier<Boolean> dialoguesHidden = () -> !this.dialogueWindow.isActive() && !this.savingWarp;
   protected final ItemStack pointsTabItem;
   protected final ItemStack padsTabItem;

   public SpatialMovementScreen(ManasSkill skill) {
      this(skill, new ArrayList<>());
   }

   public SpatialMovementScreen(ManasSkill skill, List<String> dimensionLocations) {
      super(skill.getName(), 233, 127);
      this.skill = (T)skill;
      this.instance = this.getSkillInstance();
      this.dimensionLocations = dimensionLocations;
      this.dimensionNames = dimensionLocations.stream().map(this::getDimensionName).toList();
      this.data = TensuraStorages.getPlayerDataFrom(this.player);
      this.shouldRenderWidgets = false;
      this.pointsTabItem = new ItemStack(Blocks.END_PORTAL_FRAME.asItem());
      this.padsTabItem = new ItemStack(TensuraBlocks.Items.ROYAL_DWARVEN_WARP_PAD);
   }

   @Override
   protected void init() {
      super.init();
      this.registerFields();
      this.registerFieldButtons();
      this.registerActionButtons();
      this.registerDialogueWindow();
      this.searchBar = new ExpandedEditBox(this.font, guiLeft + 140, guiTop + 41, 82, 9, Component.empty());
      this.searchBar.setBordered(false);
      this.searchBar.setResponder(s -> {
         this.filter = s;
         this.warpListScrollBar.setScrollOffset(0.0F);
         this.warpListScrollBar.setListStartIndex(0);
      });
      this.addRenderableWidget(this.searchBar);
      this.warpListScrollBar = new ScrollBar(
         guiLeft + 213, guiTop + 53, 65, 5, () -> viewingWarpPads ? this.getFilteredWarpPadsList().size() : this.getFilteredWarpPointsList().size()
      );
      this.warpListScrollBar.setScrollBarActive(this.dialoguesHidden);
      this.dimensionListScrollBar = new ScrollBar(guiLeft + 116, guiTop + 98, 65, 5, () -> this.getFilteredDimensionsList().size());
      this.dimensionListScrollBar.setScrollBarWidth(6);
      this.dimensionListScrollBar.setScrollBarHeight(23);
      this.dimensionListScrollBar.setScrollBarTextureWidth(256);
      this.dimensionListScrollBar.setScrollBarTextureHeight(256);
      this.dimensionListScrollBar.setScrollBarTexture(BACKGROUND);
      this.dimensionListScrollBar.setScrollBarActive(() -> this.dimensionDropdown);
      String currentDimension = this.player.level().dimension().location().toString();
      if (!Objects.equals(tooltipFieldD, currentDimension)) {
         this.performAction(0);
      }
   }

   @Override
   public void render(GuiGraphics g, int mX, int mY, float pT) {
      super.render(g, mX, mY, pT);
      if (this.player.tickCount % 10 == 0) {
         this.instance = this.getSkillInstance();
         this.data = TensuraStorages.getPlayerDataFrom(this.player);
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      if (this.warnTick > 0) {
         this.warnTick--;
         if (this.warnTick == 0) {
            this.warnedDelete = false;
            this.deleteWarp.setTooltip(Component.translatable("tensura.dialogue_window.delete").withColor(16733525));
         }
      }

      Component skillName = this.skill.getName();
      if (skillName == null) {
         skillName = Component.translatable("tensura.skill.empty").withColor(16711680);
      } else {
         skillName = skillName.copy().withColor(16777215);
      }

      RenderHelper.drawCenteredText(graphics, this.font, skillName, guiLeft, guiTop + 12, 233, TensuraColors.getColor(skillName));
      graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      graphics.blit(BACKGROUND, guiLeft + 46, guiTop + 34, 87.0F, viewingWarpPads ? 142.0F : 127.0F, 81, 15, 256, 256);
      graphics.blit(BACKGROUND, guiLeft + 46, guiTop + 50, 87.0F, viewingWarpPads ? 142.0F : 127.0F, 81, 15, 256, 256);
      graphics.blit(BACKGROUND, guiLeft + 46, guiTop + 66, 87.0F, viewingWarpPads ? 142.0F : 127.0F, 81, 15, 256, 256);
      boolean canTravelDimensions = this.instance != null && this.skill.canDimensionTravel(this.instance, this.player);
      graphics.blit(BACKGROUND, guiLeft + 46, guiTop + 82, 87.0F, !viewingWarpPads && canTravelDimensions ? 127.0F : 142.0F, 81, 15, 256, 256);
      this.pointsY = guiTop - (viewingWarpPads ? 21 : 23);
      this.pointsHeight = viewingWarpPads ? 21 : 24;
      this.padsY = guiTop - (viewingWarpPads ? 23 : 21);
      this.padsHeight = viewingWarpPads ? 24 : 21;
      this.pointsHovered = RenderHelper.mouseOver(mX, mY, guiLeft + 7, guiLeft + 31, this.pointsY, this.pointsY + this.pointsHeight);
      this.padsHovered = RenderHelper.mouseOver(mX, mY, guiLeft + 34, guiLeft + 58, this.padsY, this.padsY + this.padsHeight);
      graphics.blit(TOP_TAB, guiLeft + 7, this.pointsY, 24, this.pointsHeight, 0.0F, this.pointsHovered ? 21.0F : 0.0F, 24, 21, 24, 42);
      graphics.blit(TOP_TAB, guiLeft + 34, this.padsY, 24, this.padsHeight, 0.0F, this.padsHovered ? 21.0F : 0.0F, 24, 21, 24, 42);
      graphics.renderItem(this.pointsTabItem, guiLeft + 11, this.pointsY + 4);
      graphics.renderItem(this.padsTabItem, guiLeft + 38, this.padsY + 4);
      this.renderWidgets(graphics, mX, mY, partialTick);
      this.renderWarpPointsList(graphics, mX, mY);
      this.renderDimensionDropdown(graphics, mX, mY);
      if (!this.dialogueWindow.isActive()) {
         if (this.savingWarp) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 1.0F);
            graphics.fill(0, 0, this.width, this.height, TensuraColors.getARGB(0, 0.5F));
            this.editName.render(graphics, mX, mY, partialTick);
            this.saveWarp.render(graphics, mX, mY, partialTick);
            graphics.pose().popPose();
         }
      } else {
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 1.0F);
         int x = this.dialogueWindow.getX();
         int y = this.dialogueWindow.getY();
         this.dialogueWindow.render(graphics, mX, mY, partialTick);
         RenderHelper.drawCenteredText(graphics, this.font, Component.translatable("tensura.spatial_menu.edit_warp"), x + 4, y + 11, 127, 16777215);
         this.rename.render(graphics, mX, mY, partialTick);
         graphics.pose().popPose();
      }
   }

   @Override
   public void renderTooltip(GuiGraphics graphics, int mX, int mY) {
      if (this.pointsHovered) {
         this.setTooltipForNextRenderPass(Component.translatable("tensura.spatial_menu.points_tab"));
      } else if (this.padsHovered) {
         this.setTooltipForNextRenderPass(Component.translatable("tensura.spatial_menu.pads_tab"));
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int button) {
      boolean hoveredFieldD = RenderHelper.mouseOver(mX, mY, guiLeft + 34, guiLeft + 127, guiTop + 82, guiTop + 97);
      boolean hoveredDimensionDropdown = RenderHelper.mouseOver(mX, mY, guiLeft + 46, guiLeft + 127, guiTop + 97, guiTop + 168);
      if (!hoveredFieldD && !hoveredDimensionDropdown) {
         this.dimensionDropdown = false;
      }

      if (this.pointsHovered && this.dialoguesHidden.get()) {
         viewingWarpPads = false;
         this.warpListScrollBar.setScrollOffset(0.0F);
         this.warpListScrollBar.setListStartIndex(0);
         this.warpListScrollBar.setScrolling(false);
         this.fieldX.setEditable(true);
         this.fieldY.setEditable(true);
         this.fieldZ.setEditable(true);
         this.fieldD.setEditable(this.skill.canDimensionTravel(this.instance, this.player));
         this.fieldX.setFocused(false);
         this.fieldY.setFocused(false);
         this.fieldZ.setFocused(false);
         this.fieldD.setFocused(false);
         ScreenHelper.clicked();
         return true;
      }

      if (this.padsHovered && this.dialoguesHidden.get()) {
         viewingWarpPads = true;
         this.warpListScrollBar.setScrollOffset(0.0F);
         this.warpListScrollBar.setListStartIndex(0);
         this.warpListScrollBar.setScrolling(false);
         this.fieldX.setEditable(false);
         this.fieldY.setEditable(false);
         this.fieldZ.setEditable(false);
         this.fieldD.setEditable(false);
         this.fieldX.setFocused(false);
         this.fieldY.setFocused(false);
         this.fieldZ.setFocused(false);
         this.fieldD.setFocused(false);
         ScreenHelper.clicked();
         return true;
      }

      if (this.savingWarp) {
         if (this.saveWarp.mouseClicked(mX, mY, button)) {
            return true;
         } else if (this.editName.mouseClicked(mX, mY, button)) {
            this.setFocused(this.editName);
            return true;
         } else {
            return false;
         }
      } else if (this.handleClickedWarpPointsList(mX, mY)) {
         return true;
      } else if (this.handleClickedDimensionsList(mX, mY)) {
         return true;
      } else {
         return this.handleClickedDialogueWindow(mX, mY, button) ? true : super.mouseClicked(mX, mY, button);
      }
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
      if (this.dialogueWindow.isActive() || this.savingWarp) {
         return false;
      } else if (this.fieldX.isHovered()) {
         this.fieldScrolled(this.fieldX, deltaY);
         return true;
      } else if (this.fieldY.isHovered()) {
         this.fieldScrolled(this.fieldY, deltaY);
         return true;
      } else if (this.fieldZ.isHovered()) {
         this.fieldScrolled(this.fieldZ, deltaY);
         return true;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 137, guiLeft + 225, guiTop + 35, guiTop + 120)) {
         return this.warpListScrollBar.scrolledScrollBar(deltaY);
      } else {
         return RenderHelper.mouseOver(mX, mY, guiLeft + 46, guiLeft + 127, guiTop + 97, guiTop + 168)
            ? this.dimensionListScrollBar.scrolledScrollBar(deltaY)
            : super.mouseScrolled(mX, mY, deltaX, deltaY);
      }
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dragX, double dragY) {
      if (this.dialogueWindow.isActive() || this.savingWarp) {
         return false;
      } else if (this.warpListScrollBar.isScrolling()) {
         return this.warpListScrollBar.draggedScrollBar(mY);
      } else {
         return this.dimensionListScrollBar.isScrolling() ? this.dimensionListScrollBar.draggedScrollBar(mY) : super.mouseDragged(mX, mY, button, dragX, dragY);
      }
   }

   @Override
   public void onClose() {
      Double x = this.getFieldValue(this.fieldX);
      Double y = this.getFieldValue(this.fieldY);
      Double z = this.getFieldValue(this.fieldZ);
      String d = this.fieldD.getValue();
      if (x != null) {
         oX = String.valueOf(x);
      }

      if (y != null) {
         oY = String.valueOf(y);
      }

      if (z != null) {
         oZ = String.valueOf(z);
      }

      if (!d.isEmpty() && !d.isBlank()) {
         oD = d;
      } else {
         oD = "Overworld";
      }

      tooltipFieldD = this.getDimensionLocation(d);
      super.onClose();
   }

   protected void registerFields() {
      this.fieldX = new ExpandedEditBox(this.font, guiLeft + 49, guiTop + 38, 75, 9, Component.empty());
      this.fieldY = new ExpandedEditBox(this.font, guiLeft + 49, guiTop + 54, 75, 9, Component.empty());
      this.fieldZ = new ExpandedEditBox(this.font, guiLeft + 49, guiTop + 70, 75, 9, Component.empty());
      this.fieldD = new ExpandedEditBox(this.font, guiLeft + 49, guiTop + 86, 75, 9, tooltipFieldD);
      this.fieldX.setValue(oX);
      this.fieldY.setValue(oY);
      this.fieldZ.setValue(oZ);
      this.fieldD.setValue(oD);
      this.fieldX.setBordered(false);
      this.fieldY.setBordered(false);
      this.fieldZ.setBordered(false);
      this.fieldD.setBordered(false);
      this.fieldX.setEditable(!viewingWarpPads);
      this.fieldY.setEditable(!viewingWarpPads);
      this.fieldZ.setEditable(!viewingWarpPads);
      this.fieldD.setEditable(!viewingWarpPads && this.skill.canDimensionTravel(this.instance, this.player));
      Predicate<String> filter = s -> s.equals("-") || s.equals(".") || s.isEmpty() || s.isBlank() || NumberUtils.isCreatable(s);
      this.fieldX.setFilter(filter);
      this.fieldY.setFilter(filter);
      this.fieldZ.setFilter(filter);
      this.fieldX.setResponder(s -> oX = s);
      this.fieldY.setResponder(s -> oY = s);
      this.fieldZ.setResponder(s -> oZ = s);
      this.fieldD.setResponder(s -> {
         this.dimensionListScrollBar.setScrollOffset(0.0F);
         this.dimensionListScrollBar.setListStartIndex(0);
         oD = s;
         String location = this.getDimensionLocation(s);
         if (!location.isEmpty()) {
            tooltipFieldD = location;
            this.fieldD.setTooltip(location);
         }
      });
      this.fieldD.setOnFocused(focused -> {
         String location = this.getDimensionLocation(this.fieldD.getValue());
         if (location.isEmpty()) {
            tooltipFieldD = this.player.level().dimension().location().toString();
         } else {
            tooltipFieldD = location;
         }

         this.fieldD.setTooltip(tooltipFieldD);
      });
      this.fieldD.setMultipleSuggestions((Collection<String>)this.dimensionNames);
      this.fieldD.setShouldRenderMultipleSuggestions(() -> !this.dimensionDropdown && this.fieldD.isFocused());
      this.fieldD.setTooltipCheck(() -> this.dialoguesHidden.get() && this.fieldD.isHovered());
      this.addRenderableWidget(this.fieldX);
      this.addRenderableWidget(this.fieldY);
      this.addRenderableWidget(this.fieldZ);
      this.addRenderableWidget(this.fieldD);
   }

   protected void registerFieldButtons() {
      SimpleButton xAxis = new SimpleButton(
         guiLeft + 34,
         guiTop + 34,
         13,
         15,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.insert_axis", new Object[]{"X"}),
         onPress -> this.performAction(1)
      );
      xAxis.setClickedCheck(() -> this.dialoguesHidden.get() && xAxis.isHovered() && !viewingWarpPads);
      xAxis.setTooltipCheck(() -> this.dialoguesHidden.get() && xAxis.isHovered() && !viewingWarpPads);
      xAxis.overrideUvOffset(() -> 21, () -> viewingWarpPads ? 223 : (this.dialoguesHidden.get() && xAxis.isHovered() ? 208 : 193));
      SimpleButton yAxis = new SimpleButton(
         guiLeft + 34,
         guiTop + 50,
         13,
         15,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.insert_axis", new Object[]{"Y"}),
         onPress -> this.performAction(2)
      );
      yAxis.setClickedCheck(() -> this.dialoguesHidden.get() && yAxis.isHovered() && !viewingWarpPads);
      yAxis.setTooltipCheck(() -> this.dialoguesHidden.get() && yAxis.isHovered() && !viewingWarpPads);
      yAxis.overrideUvOffset(() -> 34, () -> viewingWarpPads ? 223 : (this.dialoguesHidden.get() && yAxis.isHovered() ? 208 : 193));
      SimpleButton zAxis = new SimpleButton(
         guiLeft + 34,
         guiTop + 66,
         13,
         15,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.insert_axis", new Object[]{"Z"}),
         onPress -> this.performAction(3)
      );
      zAxis.setClickedCheck(() -> this.dialoguesHidden.get() && zAxis.isHovered() && !viewingWarpPads);
      zAxis.setTooltipCheck(() -> this.dialoguesHidden.get() && zAxis.isHovered() && !viewingWarpPads);
      zAxis.overrideUvOffset(() -> 47, () -> viewingWarpPads ? 223 : (this.dialoguesHidden.get() && zAxis.isHovered() ? 208 : 193));
      SimpleButton dimension = new SimpleButton(
         guiLeft + 34,
         guiTop + 82,
         13,
         15,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.select_dimension"),
         (onPress, button) -> {
            if (this.skill.canDimensionTravel(this.instance, this.player)) {
               if (button == 0) {
                  this.dimensionDropdown = !this.dimensionDropdown;
                  if (this.dimensionDropdown) {
                     this.fieldD.setValue("");
                     this.fieldD.setTooltip(Component.empty());
                  }

                  ScreenHelper.clicked();
               } else {
                  if (button == 1) {
                     ResourceLocation location = this.player.level().dimension().location();
                     this.fieldD.setValue(this.getDimensionName(location));
                     tooltipFieldD = location.toString();
                     this.fieldD.setTooltip(tooltipFieldD);
                     ScreenHelper.clicked();
                  }
               }
            }
         }
      );
      dimension.setClickedCheck(() -> this.dialoguesHidden.get() && !viewingWarpPads && dimension.isHovered());
      dimension.setTooltipCheck(
         () -> this.dialoguesHidden.get() && !viewingWarpPads && dimension.isHovered() && this.skill.canDimensionTravel(this.instance, this.player)
      );
      dimension.overrideUvOffset(() -> 60, () -> {
         if (this.instance == null || !this.skill.canDimensionTravel(this.instance, this.player) || viewingWarpPads) {
            return 223;
         } else {
            return dimension.isHovered() && this.dialoguesHidden.get() ? 208 : 193;
         }
      });
      this.addRenderableWidget(xAxis);
      this.addRenderableWidget(yAxis);
      this.addRenderableWidget(zAxis);
      this.addRenderableWidget(dimension);
   }

   protected void registerActionButtons() {
      SimpleButton setCoordinates = new SimpleButton(
         guiLeft + 8,
         guiTop + 47,
         22,
         22,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.get_coordinates"),
         onPress -> this.performAction(0)
      );
      setCoordinates.setClickedCheck(() -> this.dialoguesHidden.get() && !viewingWarpPads && setCoordinates.isHovered());
      setCoordinates.setTooltipCheck(() -> this.dialoguesHidden.get() && !viewingWarpPads && setCoordinates.isHovered());
      setCoordinates.overrideUvOffset(() -> 43, () -> viewingWarpPads ? 171 : (this.dialoguesHidden.get() && setCoordinates.isHovered() ? 149 : 127));
      SimpleButton portal = new SimpleButton(
         guiLeft + 40,
         guiTop + 100,
         22,
         22,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.portal"),
         onPress -> this.performAction(4)
      );
      portal.setClickedCheck(() -> this.dialoguesHidden.get() && !this.dimensionDropdown && portal.isHovered());
      portal.setTooltipCheck(() -> this.dialoguesHidden.get() && !this.dimensionDropdown && portal.isHovered());
      portal.overrideUvOffset(() -> 21, () -> {
         if (this.instance == null || !this.skill.canPortal(this.instance, this.player)) {
            return 171;
         } else {
            return this.dialoguesHidden.get() && portal.isHovered() && !this.dimensionDropdown ? 149 : 127;
         }
      });
      SimpleButton warp = new SimpleButton(
         guiLeft + 70,
         guiTop + 100,
         21,
         21,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.warp"),
         onPress -> this.performAction(5)
      );
      warp.setClickedCheck(() -> this.dialoguesHidden.get() && !this.dimensionDropdown && warp.isHovered());
      warp.setTooltipCheck(() -> this.dialoguesHidden.get() && !this.dimensionDropdown && warp.isHovered());
      warp.overrideUvOffset(() -> 0, () -> {
         if (this.instance == null || !this.skill.canWarp(this.instance, this.player)) {
            return 169;
         } else {
            return this.dialoguesHidden.get() && warp.isHovered() && !this.dimensionDropdown ? 148 : 127;
         }
      });
      SimpleButton saveWarp = new SimpleButton(
         guiLeft + 99,
         guiTop + 100,
         22,
         22,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.spatial_menu.save"),
         onPress -> this.performAction(6)
      );
      saveWarp.setClickedCheck(() -> this.dialoguesHidden.get() && !viewingWarpPads && !this.dimensionDropdown && saveWarp.isHovered());
      saveWarp.setTooltipCheck(() -> this.dialoguesHidden.get() && !viewingWarpPads && !this.dimensionDropdown && saveWarp.isHovered());
      saveWarp.overrideUvOffset(() -> 65, () -> {
         if (this.data == null || this.data.getWarpPoints().size() >= this.data.getMaxWarpPoints() || viewingWarpPads) {
            return 171;
         } else {
            return this.dialoguesHidden.get() && saveWarp.isHovered() && !this.dimensionDropdown ? 149 : 127;
         }
      });
      this.addRenderableWidget(warp);
      this.addRenderableWidget(portal);
      this.addRenderableWidget(saveWarp);
      this.addRenderableWidget(setCoordinates);
   }

   protected void registerDialogueWindow() {
      boolean wasDialogueWindowActive = this.dialogueWindow != null && this.dialogueWindow.isActive();
      this.dialogueWindow = new DialogueWindow(guiLeft, guiTop, this.imageWidth, this.imageHeight, this.width, this.height);
      this.dialogueWindow.setDrawDarkenedBackground(true);
      this.dialogueWindow.setDrawExitButton(false);
      this.dialogueWindow.setActive(wasDialogueWindowActive);
      this.dialogueWindow.setOnClose(() -> {
         this.rename.setFocused(false);
         if (!this.deletedIndex) {
            String name = this.rename.getValue();
            if (name.isEmpty() || name.isBlank()) {
               name = viewingWarpPads ? "Warp Pad" : "New Warp";
            }

            if (viewingWarpPads) {
               NetworkManager.sendToServer(RequestSpatialActionPacket.getRenamePadPacket(this.skill, this.selectedWarp, name));
            } else {
               NetworkManager.sendToServer(RequestSpatialActionPacket.getRenamePacket(this.skill, this.selectedWarp, name));
            }
         }
      });
      int x = this.dialogueWindow.getX();
      int y = this.dialogueWindow.getY();
      String renameText = this.rename == null ? "" : this.rename.getValue();
      this.rename = new ExpandedEditBox(this.font, x + 11, y + 33, 119, 9, Component.empty());
      this.rename.setBordered(false);
      this.rename.setValue(renameText);
      this.rename
         .setRenderAction((graphics, mouseX, mouseY) -> graphics.blit(DialogueWindow.BACKGROUND, x + 7, y + 29, 121, 15, 135.0F, 35.0F, 81, 15, 256, 256));
      SimpleButton update = new SimpleButton(
         x + 7,
         y + 53,
         42,
         17,
         DialogueWindow.DEFAULT_BUTTON,
         Component.translatable("tensura.dialogue_window.update"),
         Component.translatable("tensura.dialogue_window.save_pos"),
         onPress -> {
            if (viewingWarpPads) {
               this.player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
               this.dialogueWindow.onClose();
            } else {
               WarpPoint warp = this.getWarpPointsList().get(this.selectedWarp);
               Double dX = this.getFieldValue(this.fieldX);
               Double dY = this.getFieldValue(this.fieldY);
               Double dZ = this.getFieldValue(this.fieldZ);
               if (dX != null && dY != null && dZ != null) {
                  String dimension = this.getDimensionLocation(this.fieldD.getValue());
                  if (dimension.isEmpty()) {
                     dimension = this.player.level().dimension().location().toString();
                  }

                  warp.setX(dX);
                  warp.setY(dY);
                  warp.setZ(dZ);
                  warp.setDimension(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimension)));
                  NetworkManager.sendToServer(RequestSpatialActionPacket.getUpdatePacket(this.skill, this.selectedWarp, dX, dY, dZ, dimension));
                  this.player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                  this.dialogueWindow.onClose();
               }
            }
         }
      );
      this.deleteWarp = new SimpleButton(
         x + 57,
         y + 51,
         21,
         21,
         256,
         256,
         BACKGROUND,
         Component.empty(),
         Component.translatable("tensura.dialogue_window.delete").withColor(16733525),
         button -> {
            if (!this.warnedDelete) {
               this.warnedDelete = true;
               this.warnTick = 100;
               ((SimpleButton)button).setTooltip(Component.translatable("tensura.dialogue_window.confirm").withColor(16733525));
               ScreenHelper.clicked();
            } else {
               this.warnTick = 0;
               this.warnedDelete = false;
               this.deletedIndex = true;
               ((SimpleButton)button).setTooltip(Component.translatable("tensura.dialogue_window.delete").withColor(16733525));
               this.player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               if (viewingWarpPads) {
                  NetworkManager.sendToServer(RequestSpatialActionPacket.getDeletePadPacket(this.skill, this.selectedWarp));
               } else {
                  NetworkManager.sendToServer(RequestSpatialActionPacket.getDeletePacket(this.skill, this.selectedWarp));
               }

               this.dialogueWindow.onClose();
            }
         }
      );
      this.deleteWarp.setAdditionalUvOffset(0, 190);
      SimpleButton cancel = new SimpleButton(
         x + 87,
         y + 53,
         42,
         17,
         DialogueWindow.DEFAULT_BUTTON,
         Component.translatable("tensura.dialogue_window.save"),
         Component.translatable("tensura.dialogue_window.save_name"),
         onPress -> this.dialogueWindow.onClose()
      );
      this.dialogueWindow.addButton(update, this.deleteWarp, cancel);
      String text = this.editName == null ? "New Warp" : this.editName.getValue();
      this.editName = new ExpandedEditBox(this.font, guiCenterX - 100, guiCenterY - 15, 200, 14, Component.empty());
      this.editName.setValue(text);
      this.saveWarp = new SimpleButton(
         guiCenterX - 75,
         guiCenterY + 1,
         150,
         14,
         200,
         60,
         SimpleButton.DEFAULT,
         Component.translatable("tensura.dialogue_window.save"),
         Component.empty(),
         onPress -> {
            String name = this.editName.getValue();
            if (name.isEmpty() || name.isBlank()) {
               name = "New Warp";
            }

            Double dX = this.getFieldValue(this.fieldX);
            Double dY = this.getFieldValue(this.fieldY);
            Double dZ = this.getFieldValue(this.fieldZ);
            if (dX != null && dY != null && dZ != null) {
               String dimension = this.getDimensionLocation(this.fieldD.getValue());
               if (dimension.isEmpty()) {
                  dimension = this.player.level().dimension().location().toString();
               }

               NetworkManager.sendToServer(RequestSpatialActionPacket.getSavePacket(this.skill, name, dX, dY, dZ, dimension));
               this.player.playNotifySound(SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
               this.savingWarp = false;
               this.editName.setValue("New Warp");
               if (this.dialogueWindow.isActive()) {
                  this.dialogueWindow.onClose();
               }

               this.setFocused(null);
            }
         }
      );
      this.editName.setVisible(() -> this.savingWarp);
      this.saveWarp.setRenderCheck(() -> this.editName.isVisible());
   }

   protected void renderWarpPointsList(GuiGraphics graphics, int mX, int mY) {
      this.warpListScrollBar.renderScrollBar(graphics, mX, mY);
      List<WarpPoint> warps = viewingWarpPads ? this.getFilteredWarpPadsList() : this.getFilteredWarpPointsList();
      if (!warps.isEmpty()) {
         this.warpListScrollBar.setListStartIndex(Math.clamp(this.warpListScrollBar.getListStartIndex(), 0, Math.max(0, warps.size() - 1)));
         int i = 0;
         int listStartIndex = this.warpListScrollBar.getListStartIndex();

         for (int index = listStartIndex; index < listStartIndex + 5 && index < warps.size(); index++) {
            WarpPoint warp = warps.get(index);
            String name = warp.getName();
            int y = guiTop + 53 + i * 13;
            boolean hoveredMain = RenderHelper.mouseOver(mX, mY, guiLeft + 139, guiLeft + 210, y, y + 13) && this.dialoguesHidden.get();
            boolean hoveredButton = hoveredMain && mX >= guiLeft + 198;
            graphics.blit(BACKGROUND, guiLeft + 139, y, 168.0F, hoveredMain && !hoveredButton ? 140.0F : 127.0F, 59, 13, 256, 256);
            graphics.blit(BACKGROUND, guiLeft + 198, y, 227.0F, hoveredButton ? 140.0F : 127.0F, 12, 13, 256, 256);
            if (name.length() > 10) {
               RenderHelper.drawScrollingText(graphics, this.font, name, guiLeft + 140, y + 3, 57, 16777215);
            } else {
               RenderHelper.drawCenteredText(graphics, this.font, name, guiLeft + 139, y + 3, 59, 16777215);
            }

            if (hoveredButton) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.dialogue_window.edit"));
            } else if (hoveredMain) {
               this.setTooltipForNextRenderPass(Component.translatable("tensura.dialogue_window.load.specific", new Object[]{name}));
            }

            i++;
         }
      }
   }

   protected void renderDimensionDropdown(GuiGraphics graphics, int mX, int mY) {
      if (this.dimensionDropdown) {
         graphics.blit(BACKGROUND, guiLeft + 46, guiTop + 97, 87.0F, 157.0F, 81, 71, 256, 256);
         this.dimensionListScrollBar.renderScrollBar(graphics, mX, mY, 231, 179, 237, 179);
         List<String> names = this.getFilteredDimensionsList();
         if (!names.isEmpty()) {
            this.dimensionListScrollBar.setListStartIndex(Math.clamp(this.dimensionListScrollBar.getListStartIndex(), 0, Math.max(0, names.size() - 1)));
            int i = 0;
            int listStartIndex = this.dimensionListScrollBar.getListStartIndex();

            for (int index = listStartIndex; index < listStartIndex + 5 && index < names.size(); index++) {
               String name = names.get(index);
               int y = guiTop + 98 + i * 13;
               boolean hovered = RenderHelper.mouseOver(mX, mY, guiLeft + 51, guiLeft + 114, y, y + 13) && this.dialoguesHidden.get();
               graphics.blit(BACKGROUND, guiLeft + 51, y, 168.0F, hovered ? 192.0F : 179.0F, 63, 13, 256, 256);
               if (name.length() > 10) {
                  RenderHelper.drawScrollingText(graphics, this.font, name, guiLeft + 53, y + 3, 57, 16777215);
               } else {
                  RenderHelper.drawCenteredText(graphics, this.font, name, guiLeft + 51, y + 3, 63, 16777215);
               }

               if (hovered) {
                  this.setTooltipForNextRenderPass(Component.literal(this.getDimensionLocation(name)));
               }

               i++;
            }
         }
      }
   }

   protected boolean handleClickedWarpPointsList(double mX, double mY) {
      if (!this.dialoguesHidden.get()) {
         return false;
      }

      if (this.warpListScrollBar.isScrollBarAreaHovered(mX, mY)) {
         return this.warpListScrollBar.clickedScrollBar(mX, mY);
      }

      List<WarpPoint> warps = viewingWarpPads ? this.getFilteredWarpPadsList() : this.getFilteredWarpPointsList();
      if (warps.isEmpty()) {
         return false;
      }

      int i = 0;
      int listStartIndex = this.warpListScrollBar.getListStartIndex();

      for (int index = listStartIndex; index < listStartIndex + 5 && index < warps.size(); index++) {
         int y = guiTop + 53 + i * 13;
         i++;
         if (RenderHelper.mouseOver(mX, mY, guiLeft + 139, guiLeft + 210, y, y + 13)) {
            ScreenHelper.clicked();
            WarpPoint warp = warps.get(index);
            if (mX >= guiLeft + 198) {
               this.selectedWarp = viewingWarpPads ? this.getWarpPadsList().indexOf(warp) : this.getWarpPointsList().indexOf(warp);
               this.deletedIndex = false;
               this.dialogueWindow.setActive(true);
               this.rename.setValue(warp.getName());
               this.setFocused(null);
               return true;
            }

            this.fieldX.setValue(String.valueOf(warp.getX()));
            this.fieldY.setValue(String.valueOf(warp.getY()));
            this.fieldZ.setValue(String.valueOf(warp.getZ()));
            this.fieldD.setValue(this.getDimensionName(warp.getDimension().location().toString()));
            this.player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return true;
         }
      }

      return false;
   }

   protected boolean handleClickedDimensionsList(double mX, double mY) {
      if (!this.dimensionDropdown) {
         return false;
      }

      if (this.dimensionListScrollBar.isScrollBarAreaHovered(mX, mY)) {
         return this.dimensionListScrollBar.clickedScrollBar(mX, mY);
      }

      List<String> names = this.getFilteredDimensionsList();
      if (names.isEmpty()) {
         return false;
      }

      int i = 0;
      int listStartIndex = this.dimensionListScrollBar.getListStartIndex();

      for (int index = listStartIndex; index < listStartIndex + 5 && index < names.size(); index++) {
         int y = guiTop + 98 + i * 13;
         i++;
         if (RenderHelper.mouseOver(mX, mY, guiLeft + 51, guiLeft + 114, y, y + 13)) {
            ScreenHelper.clicked();
            String name = names.get(index);
            String location = this.getDimensionLocation(name);
            this.fieldD.setValue(name);
            tooltipFieldD = location;
            this.fieldD.setTooltip(location);
            this.dimensionDropdown = false;
            return true;
         }
      }

      return false;
   }

   protected boolean handleClickedDialogueWindow(double mX, double mY, int button) {
      if (!this.dialogueWindow.isActive()) {
         return false;
      } else if (this.rename.mouseClicked(mX, mY, button)) {
         this.setFocused(this.rename);
         return true;
      } else {
         return this.dialogueWindow.mouseClicked(mX, mY, button);
      }
   }

   protected void performAction(int button) {
      ScreenHelper.clicked();
      switch (button) {
         case 0:
            Vec3 pos = this.player.position();
            this.fieldX.setValue(String.valueOf(Math.floor(pos.x * 1000.0) / 1000.0));
            this.fieldY.setValue(String.valueOf(Math.floor(pos.y * 1000.0) / 1000.0));
            this.fieldZ.setValue(String.valueOf(Math.floor(pos.z * 1000.0) / 1000.0));
            String currentDimension = this.player.level().dimension().location().toString();
            this.fieldD.setValue(this.getDimensionName(currentDimension));
            tooltipFieldD = currentDimension;
            this.fieldD.setTooltip(tooltipFieldD);
            break;
         case 1:
            this.fieldX.setValue(String.valueOf(Math.floor(this.player.position().x * 1000.0) / 1000.0));
            ScreenHelper.clicked();
            break;
         case 2:
            this.fieldY.setValue(String.valueOf(Math.floor(this.player.position().y * 1000.0) / 1000.0));
            ScreenHelper.clicked();
            break;
         case 3:
            this.fieldZ.setValue(String.valueOf(Math.floor(this.player.position().z * 1000.0) / 1000.0));
            ScreenHelper.clicked();
            break;
         case 4:
            Double x = this.getFieldValue(this.fieldX);
            Double y = this.getFieldValue(this.fieldY);
            Double z = this.getFieldValue(this.fieldZ);
            if (x == null || y == null || z == null) {
               this.player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               return;
            }

            String dimension = this.getDimensionLocation(this.fieldD.getValue());
            if (dimension.isEmpty()) {
               dimension = this.player.level().dimension().location().toString();
            }

            if (this.instance != null && this.skill.canPortal(this.instance, this.player)) {
               NetworkManager.sendToServer(RequestSpatialActionPacket.getPortalPacket(this.skill, x, y, z, dimension, viewingWarpPads));
            }
            break;
         case 5:
            Double x = this.getFieldValue(this.fieldX);
            Double y = this.getFieldValue(this.fieldY);
            Double z = this.getFieldValue(this.fieldZ);
            if (x == null || y == null || z == null) {
               this.player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               return;
            }

            String dimension = this.getDimensionLocation(this.fieldD.getValue());
            if (dimension.isEmpty()) {
               dimension = this.player.level().dimension().location().toString();
            }

            if (this.instance != null && this.skill.canWarp(this.instance, this.player)) {
               NetworkManager.sendToServer(RequestSpatialActionPacket.getWarpPacket(this.skill, x, y, z, dimension, viewingWarpPads));
            }
            break;
         case 6:
            if (this.data == null || this.data.getWarpPoints().size() >= this.data.getMaxWarpPoints()) {
               return;
            }

            this.savingWarp = true;
            this.setFocused(null);
      }
   }

   protected void fieldScrolled(EditBox editBox, double deltaY) {
      Double d = this.getFieldValue(editBox);
      if (d == null) {
         d = 0.0;
      } else {
         d = deltaY < 0.0 ? d - 1.0 : d + 1.0;
      }

      editBox.setValue(String.valueOf(d));
   }

   @Nullable
   protected ManasSkillInstance getSkillInstance() {
      return (ManasSkillInstance)SkillAPI.getSkillsFrom(this.player).getSkill(this.skill).orElse(null);
   }

   @Nullable
   protected Double getFieldValue(EditBox editBox) {
      try {
         return Double.parseDouble(editBox.getValue());
      } catch (NullPointerException | NumberFormatException ignored) {
         return null;
      }
   }

   protected String getDimensionName(ResourceKey<Level> level) {
      return this.getDimensionName(level.location());
   }

   protected String getDimensionName(ResourceLocation location) {
      return this.getDimensionName(location.toString());
   }

   protected String getDimensionName(String string) {
      Component component = Component.translatable("dimension." + string.replace(":", "."));
      if (component.getString().isEmpty()) {
         component = Component.translatable(string);
      }

      if (component.getString().isEmpty()) {
         component = Component.translatable("dimension.minecraft.overworld");
      }

      return component.getString();
   }

   protected String getDimensionLocation(String string) {
      int index = this.dimensionNames.indexOf(string);
      return index == -1 ? "" : this.dimensionLocations.get(index);
   }

   protected List<WarpPoint> getWarpPointsList() {
      return this.data.getWarpPoints();
   }

   protected List<WarpPoint> getFilteredWarpPointsList() {
      return this.filter != null && !this.filter.isEmpty() && !this.filter.isBlank()
         ? this.getWarpPointsList().stream().filter(warp -> warp.getName().toLowerCase().contains(this.filter.toLowerCase())).toList()
         : this.getWarpPointsList();
   }

   protected List<WarpPoint> getWarpPadsList() {
      return this.data.getWarpPads();
   }

   protected List<WarpPoint> getFilteredWarpPadsList() {
      return this.filter != null && !this.filter.isEmpty() && !this.filter.isBlank()
         ? this.getWarpPadsList().stream().filter(warp -> warp.getName().toLowerCase().contains(this.filter.toLowerCase())).toList()
         : this.getWarpPadsList();
   }

   protected List<String> getFilteredDimensionsList() {
      String value = this.fieldD.getValue();
      return !value.isEmpty() && !value.isBlank()
         ? this.dimensionNames.stream().filter(name -> name.toLowerCase().contains(value.toLowerCase())).toList()
         : this.dimensionNames;
   }
}
