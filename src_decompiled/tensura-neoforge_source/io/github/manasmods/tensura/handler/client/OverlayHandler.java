package io.github.manasmods.tensura.handler.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialDominationSkill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.ability.skill.unique.ReflectorSkill;
import io.github.manasmods.tensura.block.HipokuteGrass;
import io.github.manasmods.tensura.block.entity.CharybdisCoreBlockEntity;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.client.screen.SettingsScreen;
import io.github.manasmods.tensura.client.screen.widgets.OverlayElement;
import io.github.manasmods.tensura.config.client.HudConfig;
import io.github.manasmods.tensura.effect.FrostEffect;
import io.github.manasmods.tensura.effect.PetrificationEffect;
import io.github.manasmods.tensura.effect.WebbedEffect;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class OverlayHandler {
   private static final String overlay = "textures/gui/overlay/";
   private static final ResourceLocation FREEZING = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/freezing.png");
   private static final ResourceLocation PETRIFICATION = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/petrification.png");
   private static final ResourceLocation WEB_SILENCED = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/web_silenced.png");
   private static final ResourceLocation RED_VEINS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/red_veins.png");
   private static final ResourceLocation GLOW_BORDER = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/glow_border.png");
   private static final ResourceLocation LIGHTNING_BORDER = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/lightning_border.png");
   private static final ResourceLocation SHADOW_BORDER = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/overlay/shadow_border.png");
   private static final String path = "textures/player_hud/";
   private static final ResourceLocation SURVIVAL_HUD = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/main_hud.png");
   private static final ResourceLocation SURVIVAL_BARS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/main_hud_bars.png");
   private static final ResourceLocation CREATIVE_HUD = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/creative_hud.png");
   private static final ResourceLocation CREATIVE_BARS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/creative_hud_bars.png");
   private static final ResourceLocation HARDCORE_HUD = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hardcore_hud.png");
   private static final ResourceLocation HARDCORE_BARS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hardcore_hud_bars.png");
   private static final ResourceLocation SPECTATOR_HUD = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/spectator_hud.png");
   private static final ResourceLocation AURA_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/aura_bar.png");
   private static final ResourceLocation MANA_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/mana_bar.png");
   private static final String decorations = "textures/player_hud/decorations/";
   private static final ResourceLocation HEART = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/heart.png");
   private static final ResourceLocation MOUNT_HEART = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/mount_heart.png");
   private static final ResourceLocation SPIRITUAL_HEART = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/decorations/spiritual_heart.png"
   );
   private static final ResourceLocation MOUNT_SPIRITUAL_HEART = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/decorations/mount_spiritual_heart.png"
   );
   private static final ResourceLocation ARMOR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/armor.png");
   private static final ResourceLocation ARMOR_EMPTY = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/armor_empty.png");
   private static final ResourceLocation FOOD_FULL = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/food_full.png");
   private static final ResourceLocation FOOD_EMPTY = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/food_empty.png");
   private static final ResourceLocation FOOD_FULL_HUNGER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/decorations/food_full_hunger.png"
   );
   private static final ResourceLocation FOOD_EMPTY_HUNGER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/decorations/food_empty_hunger.png"
   );
   private static final ResourceLocation BARRIER = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/barrier.png");
   private static final ResourceLocation AIR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/decorations/air.png");
   private static final String bars = "textures/player_hud/hp_bars/";
   private static final ResourceLocation SEVERANCE_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/severance_hp_bar.png");
   private static final ResourceLocation HEALTH_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/hp_bar.png");
   private static final ResourceLocation INFECTION_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/infection_hp_bar.png"
   );
   private static final ResourceLocation WITHER_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/wither_hp_bar.png");
   private static final ResourceLocation FATAL_POISON_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/fatal_poison_hp_bar.png"
   );
   private static final ResourceLocation CORROSION_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/corrosion_hp_bar.png"
   );
   private static final ResourceLocation POISON_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/poison_hp_bar.png");
   private static final ResourceLocation FROZEN_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/frozen_hp_bar.png");
   private static final ResourceLocation CURSE_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/curse_hp_bar.png");
   private static final ResourceLocation ABSORPTION_HEALTH_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/absorption_hp_bar.png"
   );
   private static final ResourceLocation SPIRITUAL_HP_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/hp_bars/shp_bar.png");
   private static final ResourceLocation SOUL_DRAIN_SPIRITUAL_HP_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/soul_drain_shp_bar.png"
   );
   private static final ResourceLocation INSANITY_SPIRITUAL_HP_BAR = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/hp_bars/insanity_shp_bar.png"
   );
   private static final String abilities = "textures/player_hud/abilities/";
   private static final ResourceLocation ABILITIES_OVERLAY = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/abilities/ability_overlay.png"
   );
   private static final ResourceLocation ABILITIES_BARS = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/abilities/ability_overlay_text_bars.png"
   );
   private static final ResourceLocation COOLDOWN_ICON = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/abilities/cooldown_icon.png");
   private static final ResourceLocation LEARNING_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/abilities/learning_bar.png");
   private static final ResourceLocation MASTERY_BAR = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/abilities/mastery_bar.png");
   private static final String analysis = "textures/player_hud/analysis/";
   private static final ResourceLocation ENTITY_ANALYSIS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/player_hud/analysis/analysis_overlay.png");
   private static final ResourceLocation BLOCK_ANALYSIS = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/player_hud/analysis/analysis_overlay_blocks.png"
   );
   public static final OverlayElement EFFECTS_LAYER = new OverlayElement();
   public static final OverlayElement STATUS_LAYER = new OverlayElement();
   public static final OverlayElement STATUS_BARS_LAYER = new OverlayElement();
   public static final OverlayElement ABILITIES_LAYER = new OverlayElement();
   public static final OverlayElement ANALYSIS_LAYER = new OverlayElement();
   public static final OverlayElement ARMOR_DECORATION = new OverlayElement();
   public static final OverlayElement BARRIER_DECORATION = new OverlayElement();
   public static final OverlayElement AIR_DECORATION = new OverlayElement();
   public static final OverlayElement FOOD_DECORATION = new OverlayElement();
   public static final OverlayElement MOUNT_HEALTH_DECORATION = new OverlayElement();
   public static final OverlayElement MOUNT_SPIRITUAL_HEALTH_DECORATION = new OverlayElement();
   public static final int EFFECTS = -1;
   public static final int STATUS = 0;
   public static final int STATUS_BARS = 1;
   public static final int ABILITIES = 2;
   public static final int ANALYSIS = 3;
   public static final int ARMOR_DECO = 4;
   public static final int BARRIER_DECO = 5;
   public static final int AIR_DECO = 6;
   public static final int FOOD_DECO = 7;
   public static final int MOUNT_HP_DECO = 8;
   public static final int MOUNT_SHP_DECO = 9;
   private static Minecraft minecraft;
   private static LocalPlayer player;
   private static ClientLevel level;
   private static IExistence existenceData;
   private static Font font;
   private static PoseStack poseStack;
   private static GuiGraphics graphics;
   private static HudConfig cfg;
   private static HudConfig.Status statusCfg;
   private static HudConfig.Decorations decorationsCfg;
   private static HudConfig.StatusBars statusBarsCfg;
   private static HudConfig.Abilities abilitiesCfg;
   private static HudConfig.Analysis analysisCfg;
   private static boolean isHardcore;
   private static int analysisLevel;
   private static int analysisDistance;
   private static int analysisSlot;
   private static int analysisSlotSwitch;
   private static float armorScale;
   private static float foodScale;
   private static float airScale;
   private static float barrierScale;
   private static float mountHpScale;
   private static float mountShpScale;
   private static float statusScale;
   private static float barsScale;
   private static float abilitiesScale;
   private static float analysisScale;
   private static float centerX;
   private static float analysisOpacity;
   private static float analysisMaxOpacity;
   private static int screenWidth;
   private static int screenHeight;
   private static float[] statusPositions = new float[2];
   private static float[] barsPositions = new float[2];
   private static float[] abilitiesPositions = new float[2];
   private static float[] analysisPositions = new float[2];
   private static float[] armorPositions = new float[2];
   private static float[] foodPositions = new float[2];
   private static float[] airPositions = new float[2];
   private static float[] barrierPositions = new float[2];
   private static float[] mountHpPositions = new float[2];
   private static float[] mountShpPositions = new float[2];
   private static boolean shouldHudRender = true;
   private static UUID cachedHeadUuid = null;
   private static WeakReference<LivingEntity> cachedHeadEntity = null;
   private static long analysisLastTick = -1L;
   private static BlockPos analysisCachedHit = null;
   private static final Component ANALYSIS_HP_LABEL = Component.translatable("tensura.vanilla_attribute.health.shortened_name");
   private static final Component ANALYSIS_SHP_LABEL = Component.translatable("tensura.attribute.spiritual_health.shortened_name");
   private static final Component ANALYSIS_ARMOR_LABEL = Component.translatable("attribute.name.generic.armor");
   private static final Component ANALYSIS_AURA_LABEL = Component.translatable("tensura.attribute.aura.shortened_name");
   private static final Component ANALYSIS_MAGICULE_LABEL = Component.translatable("tensura.attribute.magicule.shortened_name");
   private static final Component ANALYSIS_EP_LABEL = Component.translatable("tensura.attribute.existence_points.shortened_name");
   private static final Component BLOCK_UNBREAKABLE_LABEL = Component.translatable("item.unbreakable");
   private static final Component BLOCK_GUI_YES_LABEL = Component.translatable("gui.yes");
   private static final Component BLOCK_GUI_NO_LABEL = Component.translatable("gui.no");
   private static final Component BLOCK_HARDNESS_LABEL = Component.translatable("tensura.attribute.block.hardness");
   private static final Component BLOCK_CORRECT_TOOL_LABEL = Component.translatable("tensura.attribute.block.correct_tool");
   private static final Component BLOCK_EXPLOSION_RES_LABEL = Component.translatable("tensura.attribute.block.explosion_resistance");
   private static final Component BLOCK_LIGHT_LEVEL_LABEL = Component.translatable("tensura.attribute.block.light_level");
   private static final Component BLOCK_EP_LABEL = Component.translatable("tensura.attribute.existence_points");
   private static final Component BLOCK_REDSTONE_LABEL = Component.translatable("tensura.attribute.block.redstone_strength");
   private static final Component BLOCK_EGG_HATCH_LABEL = Component.translatable("tensura.attribute.block.egg_hatch");
   private static final Component BLOCK_AGE_LABEL = Component.translatable("tensura.attribute.block.age");
   private static final Component BLOCK_FAILED_GROWTH_LABEL = Component.translatable("tensura.attribute.block.failed_growth");
   private static final Component SKILL_EMPTY_LABEL = Component.translatable("tensura.skill.empty");
   private static final int FULL_COUNTER_GLOW_COLOR = -3840;
   private static final int FAULT_FIELD_GLOW_COLOR = -15800373;

   public static void init() {
      EFFECTS_LAYER.setRenderer(OverlayHandler::renderEffects);
      STATUS_LAYER.setRenderer(OverlayHandler::renderStatus);
      STATUS_BARS_LAYER.setRenderer(OverlayHandler::renderStatusBars);
      ABILITIES_LAYER.setRenderer(OverlayHandler::renderAbilities);
      ANALYSIS_LAYER.setRenderer(OverlayHandler::renderAnalysis);
      ARMOR_DECORATION.setRenderer(OverlayHandler::renderArmorDecoration);
      BARRIER_DECORATION.setRenderer(OverlayHandler::renderBarrierDecoration);
      AIR_DECORATION.setRenderer(OverlayHandler::renderAirDecoration);
      FOOD_DECORATION.setRenderer(OverlayHandler::renderFoodDecoration);
      MOUNT_HEALTH_DECORATION.setRenderer(OverlayHandler::renderMountHealthDecoration);
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setRenderer(OverlayHandler::renderMountSpiritualHealthDecoration);
      cfg = (HudConfig)ConfigRegistry.getConfig(HudConfig.class);
      statusCfg = cfg.status;
      statusBarsCfg = cfg.statusBars;
      abilitiesCfg = cfg.abilities;
      analysisCfg = cfg.analysis;
      decorationsCfg = cfg.decorations;
   }

   public static void renderHud(GuiGraphics guiGraphics) {
      minecraft = Minecraft.getInstance();
      player = minecraft.player;
      if (player != null) {
         if (cfg.tensuraHud && shouldHudRender && !minecraft.getDebugOverlay().showDebugScreen() && !minecraft.options.hideGui) {
            graphics = guiGraphics;
            screenWidth = guiGraphics.guiWidth();
            screenHeight = guiGraphics.guiHeight();
            level = player.clientLevel;
            poseStack = graphics.pose();
            existenceData = TensuraStorages.getExistenceFrom(player);
            font = minecraft.font;
            centerX = screenWidth / 2.0F;
            isHardcore = level.getLevelData().isHardcore();
            reloadAllValues();
            poseStack.pushPose();
            pre_renderStatus();
            pre_renderStatusBars();
            pre_renderAbilities();
            pre_renderAnalysis();
            pre_renderDecorations();
            poseStack.popPose();
         }
      }
   }

   public static void pre_renderEffects() {
      renderElement(EFFECTS_LAYER, -1, 1.0F);
   }

   private static void pre_renderStatus() {
      if (statusCfg.render) {
         renderElement(STATUS_LAYER, 0, statusScale);
      }
   }

   private static void pre_renderDecorations() {
      if (!player.isSpectator()) {
         if (decorationsCfg.air.render || shouldPreview(6)) {
            renderElement(AIR_DECORATION, 6, airScale);
         }

         if (decorationsCfg.food.render || shouldPreview(7)) {
            renderElement(FOOD_DECORATION, 7, foodScale);
         }

         if (decorationsCfg.armor.render || shouldPreview(4)) {
            renderElement(ARMOR_DECORATION, 4, armorScale);
         }

         if (decorationsCfg.barrier.render || shouldPreview(5)) {
            renderElement(BARRIER_DECORATION, 5, barrierScale);
         }

         if (decorationsCfg.mountHp.render || shouldPreview(8)) {
            renderElement(MOUNT_HEALTH_DECORATION, 8, mountHpScale);
         }

         if (decorationsCfg.mountSpiritualHp.render || shouldPreview(9)) {
            renderElement(MOUNT_SPIRITUAL_HEALTH_DECORATION, 9, mountShpScale);
         }
      }
   }

   private static void pre_renderStatusBars() {
      if (statusBarsCfg.render && !player.isSpectator()) {
         renderElement(STATUS_BARS_LAYER, 1, barsScale);
      }
   }

   private static void pre_renderAbilities() {
      if (abilitiesCfg.render) {
         renderElement(ABILITIES_LAYER, 2, abilitiesScale);
      }
   }

   private static void pre_renderAnalysis() {
      if (!analysisCfg.render) {
         analysisOpacity = 0.0F;
      } else if (SettingsScreen.getPreviewElement() == 3) {
         renderElement(ANALYSIS_LAYER, 3, analysisScale);
      } else {
         analysisLevel = (int)player.getAttributeValue(TensuraAttributes.ANALYSIS_LEVEL);
         analysisDistance = (int)player.getAttributeValue(TensuraAttributes.ANALYSIS_DISTANCE);
         if (analysisLevel > 0 && analysisDistance > 0) {
            renderElement(ANALYSIS_LAYER, 3, analysisScale);
         } else {
            analysisOpacity = 0.0F;
         }
      }
   }

   private static void renderEffects() {
      IEffect effectData = TensuraStorages.getEffectFrom(player);
      if (effectData != null) {
         if (WebbedEffect.isFullyWebbed(player)) {
            RenderHelper.renderTextureOverlay(WEB_SILENCED, 1.0F, screenWidth, screenHeight);
         }

         if (FrostEffect.isFrozen(player)) {
            MobEffectInstance frozen = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST));
            int duration = frozen == null ? 200 : frozen.getDuration();
            RenderHelper.renderFadingTextureWithDuration(FREEZING, duration, 200, screenWidth, screenHeight);
         }

         int petrificationLevel = PetrificationEffect.getPetrificationLevel(player);
         if (petrificationLevel > 0) {
            float alpha = Math.min(0.33F * petrificationLevel, 1.0F);
            RenderHelper.renderTextureOverlay(PETRIFICATION, alpha, screenWidth, screenHeight);
         }

         MobEffectInstance fadingOverlay = getFadingGlowBorder();
         if (fadingOverlay != null) {
            RenderHelper.renderFadingTextureWithDuration(
               GLOW_BORDER, fadingOverlay.getDuration(), 200, ((MobEffect)fadingOverlay.getEffect().value()).getColor(), screenWidth, screenHeight
            );
         }

         int glowColor = getGlowBorderColor();
         if (glowColor != -1) {
            RenderHelper.renderTextureOverlay(GLOW_BORDER, glowColor, 1.0F, screenWidth, screenHeight);
         }

         MobEffectInstance lightningBorder = getLightningBorder();
         if (lightningBorder != null) {
            RenderHelper.renderFadingTextureWithDuration(
               LIGHTNING_BORDER, lightningBorder.getDuration(), 200, ((MobEffect)lightningBorder.getEffect().value()).getColor(), screenWidth, screenHeight
            );
         }

         MobEffectInstance shadowOverlay = getShadowBorder();
         int sleepModeDuration = existenceData.getSleepModeTime();
         if (shadowOverlay != null || sleepModeDuration > 0) {
            RenderHelper.renderTextureOverlay(SHADOW_BORDER, 0, 1.0F, screenWidth, screenHeight);
         }
      }
   }

   private static void renderStatus() {
      boolean isLeftSide = isLeftSide(0);
      boolean isSpectator = player.isSpectator();
      ResourceLocation hudTexture;
      if (player.isCreative()) {
         hudTexture = CREATIVE_HUD;
      } else if (isSpectator) {
         hudTexture = SPECTATOR_HUD;
      } else if (isHardcore) {
         hudTexture = HARDCORE_HUD;
      } else {
         hudTexture = SURVIVAL_HUD;
      }

      int hudWidth = isSpectator ? 44 : 150;
      int raceCooldown = 0;
      ManasRaceInstance race = null;
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (optional.isPresent()) {
         race = optional.get();
      }

      if (race != null) {
         raceCooldown = race.getCooldown();
      }

      STATUS_LAYER.setScale(statusScale);
      STATUS_LAYER.setWidth(hudWidth * statusScale);
      STATUS_LAYER.setHeight(44.0F * statusScale);
      STATUS_LAYER.setPosX(statusPositions[0]);
      STATUS_LAYER.setPosY(statusPositions[1]);
      if (isSpectator) {
         renderWithFlipping(hudTexture, 0.0F, 0.0F, hudWidth, 44, isLeftSide);
         renderPlayer(isLeftSide, true);
      } else {
         int HP = (int)Math.ceil(player.getHealth());
         int maxHP = (int)Math.ceil(player.getMaxHealth());
         int SHP = (int)Math.ceil(existenceData.getSpiritualHealth());
         int maxSHP = (int)Math.ceil(player.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
         int hpWidth = dynamicResizing(101, HP, maxHP);
         int shpWidth = dynamicResizing(85, SHP, maxSHP);
         int hpBarPosX = isLeftSide ? 44 : 106 - hpWidth;
         int shpBarPosX = isLeftSide ? 44 : 106 - shpWidth;
         int hpTextPosX = isLeftSide ? 44 : 5;
         int shpTextPosX = hpTextPosX + (isLeftSide ? 0 : 16);
         String hpText = HP + "/" + maxHP;
         String shpText = SHP + "/" + maxSHP;
         ResourceLocation hpTexture = getHealthBarToRender(false);
         ResourceLocation shpTexture = getHealthBarToRender(true);
         renderWithFlipping(hudTexture, 0.0F, 0.0F, hudWidth, 44, isLeftSide);
         renderWithFlipping(hpTexture, hpBarPosX, 5.0F, hpWidth, 9, isLeftSide);
         renderSeverance(maxHP, isLeftSide);
         renderWithFlipping(shpTexture, shpBarPosX, 19.0F, shpWidth, 9, isLeftSide);
         RenderHelper.drawCenteredText(graphics, font, hpText, hpTextPosX, 6, 101, 16777215);
         RenderHelper.drawCenteredText(graphics, font, shpText, shpTextPosX, 20, 85, 16777215);
         renderPlayer(isLeftSide, false);
         if (raceCooldown != 0) {
            int cooldownPosX = isLeftSide ? 5 : 111;
            String cooldownText = String.valueOf(raceCooldown);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            graphics.pose().translate(0.0F, 0.0F, 60.0F);
            renderWithFlipping(COOLDOWN_ICON, cooldownPosX, 5.0F, 34, 34, isLeftSide);
            RenderSystem.disableBlend();
            RenderHelper.drawCenteredText(graphics, font, cooldownText, cooldownPosX, 15, 34, 16777215);
         }
      }
   }

   private static void renderFoodDecoration() {
      FOOD_DECORATION.setScale(foodScale);
      FOOD_DECORATION.setWidth(9.0F * foodScale);
      FOOD_DECORATION.setHeight(9.0F * foodScale);
      FOOD_DECORATION.setPosX(foodPositions[0]);
      FOOD_DECORATION.setPosY(foodPositions[1]);
      boolean isLeftSide = isLeftSide(7);
      boolean hasHunger = player.hasEffect(MobEffects.HUNGER);
      int value = Math.max(0, player.getFoodData().getFoodLevel());
      String text = String.valueOf(value);
      int width = font.width(text);
      int textPosX = isLeftSide ? -2 - width : 11;
      ResourceLocation texture;
      if (hasHunger) {
         if (value == 0) {
            texture = FOOD_EMPTY_HUNGER;
         } else {
            texture = FOOD_FULL_HUNGER;
         }
      } else if (value == 0) {
         texture = FOOD_EMPTY;
      } else {
         texture = FOOD_FULL;
      }

      renderWithFlipping(texture, 0.0F, 0.0F, 9, 9, isLeftSide);
      graphics.drawString(font, text, textPosX, 1, 16777215);
   }

   private static void renderArmorDecoration() {
      ARMOR_DECORATION.setScale(armorScale);
      ARMOR_DECORATION.setWidth(9.0F * armorScale);
      ARMOR_DECORATION.setHeight(9.0F * armorScale);
      ARMOR_DECORATION.setPosX(armorPositions[0]);
      ARMOR_DECORATION.setPosY(armorPositions[1]);
      boolean isLeftSide = isLeftSide(4);
      int value = player.getArmorValue();
      String text = String.valueOf(value);
      int width = font.width(text);
      int textPosX = isLeftSide ? -2 - width : 11;
      if (value <= 0) {
         renderWithFlipping(ARMOR_EMPTY, 0.0F, 0.0F, 9, 9, isLeftSide);
      } else {
         renderWithFlipping(ARMOR, 0.0F, 0.0F, 9, 9, isLeftSide);
      }

      graphics.drawString(font, text, textPosX, 1, 16777215);
   }

   private static void renderAirDecoration() {
      AIR_DECORATION.setScale(airScale);
      AIR_DECORATION.setWidth(9.0F * airScale);
      AIR_DECORATION.setHeight(9.0F * airScale);
      AIR_DECORATION.setPosX(airPositions[0]);
      AIR_DECORATION.setPosY(airPositions[1]);
      boolean isLeftSide = isLeftSide(6);
      int value = Math.max(0, (int)((float)player.getAirSupply() / player.getMaxAirSupply() * 20.0F));
      String text = String.valueOf(value);
      int width = font.width(text);
      int textPosX = isLeftSide ? -2 - width : 11;
      renderWithFlipping(AIR, 0.0F, 0.0F, 9, 9, isLeftSide);
      graphics.drawString(font, text, textPosX, 1, 16777215);
   }

   private static void renderBarrierDecoration() {
      BARRIER_DECORATION.setScale(barrierScale);
      BARRIER_DECORATION.setWidth(9.0F * barrierScale);
      BARRIER_DECORATION.setHeight(9.0F * barrierScale);
      BARRIER_DECORATION.setPosX(barrierPositions[0]);
      BARRIER_DECORATION.setPosY(barrierPositions[1]);
      boolean isLeftSide = isLeftSide(5);
      int value = (int)player.getAttributeValue(TensuraAttributes.MULTILAYER_BARRIER);
      if (shouldPreview(5) && value <= 0) {
         value = 10;
      }

      if (value > 0) {
         String text = String.valueOf(value);
         int width = font.width(text);
         int textPosX = isLeftSide ? 11 : -2 - width;
         renderWithFlipping(BARRIER, 0.0F, 0.0F, 9, 9, isLeftSide);
         graphics.drawString(font, text, textPosX, 1, 16777215);
      }
   }

   private static void renderMountHealthDecoration() {
      MOUNT_HEALTH_DECORATION.setScale(mountHpScale);
      MOUNT_HEALTH_DECORATION.setWidth(9.0F * mountHpScale);
      MOUNT_HEALTH_DECORATION.setHeight(9.0F * mountHpScale);
      MOUNT_HEALTH_DECORATION.setPosX(mountHpPositions[0]);
      MOUNT_HEALTH_DECORATION.setPosY(mountHpPositions[1]);
      boolean isLeftSide = isLeftSide(8);
      Entity vehicle = player.getVehicle();
      boolean isLiving = vehicle instanceof LivingEntity;
      int value;
      if (shouldPreview(8) && !isLiving) {
         value = 50;
      } else {
         if (!isLiving) {
            return;
         }

         value = (int)Math.ceil(((LivingEntity)vehicle).getHealth());
      }

      String text = String.valueOf(value);
      int width = font.width(text);
      int textPosX = isLeftSide ? 11 : -2 - width;
      renderWithFlipping(MOUNT_HEART, 0.0F, 0.0F, 9, 9, isLeftSide);
      graphics.drawString(font, text, textPosX, 1, 16777215);
   }

   private static void renderMountSpiritualHealthDecoration() {
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setScale(mountShpScale);
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setWidth(9.0F * mountShpScale);
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setHeight(9.0F * mountShpScale);
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setPosX(mountShpPositions[0]);
      MOUNT_SPIRITUAL_HEALTH_DECORATION.setPosY(mountShpPositions[1]);
      boolean isLeftSide = isLeftSide(9);
      Entity vehicle = player.getVehicle();
      boolean isLiving = vehicle instanceof LivingEntity;
      int value;
      if (shouldPreview(9) && !isLiving) {
         value = 50;
      } else {
         if (!isLiving) {
            return;
         }

         IExistence livingData = TensuraStorages.getExistenceFrom((LivingEntity)vehicle);
         if (livingData == null) {
            return;
         }

         value = (int)Math.ceil(livingData.getSpiritualHealth());
      }

      String text = String.valueOf(value);
      int width = font.width(text);
      int textPosX = isLeftSide ? 11 : -2 - width;
      renderWithFlipping(MOUNT_SPIRITUAL_HEART, 0.0F, 0.0F, 9, 9, isLeftSide);
      graphics.drawString(font, text, textPosX, 1, 16777215);
   }

   private static void renderPlayer(boolean isLeftSide, boolean isSpectator) {
      float sizeMultiplier = player.getScale();
      sizeMultiplier = sizeMultiplier > 1.0F ? 1.0F / sizeMultiplier : 1.0F;
      float scale = 16.0F * sizeMultiplier;
      float x = !isLeftSide && !isSpectator ? 111.0F : 5.0F;
      float y = isSpectator ? 15.0F : 5.0F;
      float endY = y + (isSpectator ? 25 : 35);
      RenderHelper.renderEntityInInventoryFollowsMouse(
         graphics, x, y, x + 35.0F, endY, scale, isLeftSide ? centerX - 25.0F : -centerX + 25.0F, 30.0F, false, player
      );
   }

   private static void renderStatusBars() {
      STATUS_BARS_LAYER.setScale(barsScale);
      STATUS_BARS_LAYER.setWidth(81.0F * barsScale);
      STATUS_BARS_LAYER.setHeight(23.0F * barsScale);
      STATUS_BARS_LAYER.setPosX(barsPositions[0]);
      STATUS_BARS_LAYER.setPosY(barsPositions[1]);
      boolean isLeftSide = isLeftSide(1);
      ResourceLocation barsTexture;
      if (player.isCreative()) {
         barsTexture = CREATIVE_BARS;
      } else if (isHardcore) {
         barsTexture = HARDCORE_BARS;
      } else {
         barsTexture = SURVIVAL_BARS;
      }

      int aura = (int)Math.round(existenceData.getAura());
      int mana = (int)Math.round(existenceData.getMagicule());
      int maxAura = (int)Math.round(EnergyHelper.getMaxAura(player));
      int maxMana = (int)Math.round(EnergyHelper.getMaxMagicule(player));
      int auraWidth = dynamicResizing(67, aura, maxAura);
      int manaWidth = dynamicResizing(67, mana, maxMana);
      int textPosX = isLeftSide ? 5 : 9;
      int auraPosX = isLeftSide ? 5 : 76 - auraWidth;
      int manaPosX = isLeftSide ? 5 : 76 - manaWidth;
      String auraText = (int)((double)aura / maxAura * 100.0) + "%";
      String manaText = (int)((double)mana / maxMana * 100.0) + "%";
      renderWithFlipping(barsTexture, 0.0F, 0.0F, 81, 23, isLeftSide);
      renderWithFlipping(AURA_BAR, auraPosX, 12.0F, auraWidth, 9, isLeftSide);
      renderWithFlipping(MANA_BAR, manaPosX, 2.0F, manaWidth, 9, isLeftSide);
      RenderHelper.drawCenteredText(graphics, font, auraText, textPosX, 13, 67, 16777215);
      RenderHelper.drawCenteredText(graphics, font, manaText, textPosX, 3, 67, 16777215);
   }

   private static void renderSeverance(int maxHP, boolean isLeftSide) {
      IEffect effectData = TensuraStorages.getEffectFrom(player);
      if (effectData != null) {
         double severance = effectData.getSeveranceAmount();
         if (!(severance <= 0.0)) {
            int width = 1 + dynamicResizing(101, severance, maxHP);
            int pX = isLeftSide ? 145 - width : 5;
            renderWithFlipping(SEVERANCE_BAR, pX, 5.0F, width, 9, isLeftSide);
         }
      }
   }

   private static void renderAbilities() {
      boolean isLeftSide = isLeftSide(2);
      ABILITIES_LAYER.setScale(abilitiesScale);
      ABILITIES_LAYER.setWidth(158.0F * abilitiesScale);
      ABILITIES_LAYER.setHeight(104.0F * abilitiesScale);
      ABILITIES_LAYER.setPosX(isLeftSide ? abilitiesPositions[0] : abilitiesPositions[0] - 117.0F);
      ABILITIES_LAYER.setPosY(abilitiesPositions[1]);
      IAbility abilityData = TensuraStorages.getAbilityFrom(player);
      if (abilityData != null) {
         boolean showFullInfo = TensuraKeybinds.NEXT_ABILITY_MODE.isDown() || TensuraKeybinds.PREVIOUS_ABILITY_MODE.isDown();
         renderWithFlipping(ABILITIES_OVERLAY, 0.0F, 0.0F, 41, 104, isLeftSide);
         if (showFullInfo) {
            renderWithFlipping(ABILITIES_BARS, isLeftSide ? 41.0F : -117.0F, 4.0F, 117, 97, isLeftSide);
         }

         for (int i = 0; i < 3; i++) {
            AbilitySlot slot = abilityData.getAbilitySlot(i);
            ManasSkill ability = slot.getSkill();
            if (ability != null) {
               renderAbility(ability, i, slot.getMode(), isLeftSide, showFullInfo);
            }
         }
      }
   }

   private static void renderAnalysis() {
      ANALYSIS_LAYER.setScale(analysisScale);
      ANALYSIS_LAYER.setWidth(95.0F * analysisScale);
      ANALYSIS_LAYER.setHeight(156.0F * analysisScale);
      ANALYSIS_LAYER.setPosX(analysisPositions[0]);
      ANALYSIS_LAYER.setPosY(analysisPositions[1]);
      if (shouldPreview(3)) {
         analysisOpacity = analysisMaxOpacity;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, analysisOpacity);
         renderEntityAnalysis(player);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         if (data != null) {
            int mode = data.getAnalysisMode();
            LivingEntity livingEntity = ObjectSelectionHelper.getTargetingEntity(player, analysisDistance, false, true);
            boolean rendered = false;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, analysisOpacity);
            if ((mode == 0 || mode == 1) && livingEntity != null) {
               renderEntityAnalysis(livingEntity);
               rendered = true;
            }

            if ((mode == 0 || mode == 2) && !rendered) {
               long currentTick = level.getGameTime();
               BlockPos blockPos;
               BlockState blockState;
               if (currentTick == analysisLastTick && analysisCachedHit != null) {
                  blockPos = analysisCachedHit;
                  blockState = level.getBlockState(blockPos);
               } else {
                  blockPos = ObjectSelectionHelper.getPlayerPOVHitResult(level, player, Fluid.NONE, Block.OUTLINE, analysisDistance).getBlockPos();
                  blockState = level.getBlockState(blockPos);
                  if (blockState.isAir()) {
                     blockPos = ObjectSelectionHelper.getPlayerPOVHitResult(level, player, Fluid.SOURCE_ONLY, Block.OUTLINE, analysisDistance).getBlockPos();
                     blockState = level.getBlockState(blockPos);
                  }

                  analysisLastTick = currentTick;
                  analysisCachedHit = blockPos;
               }

               if (!blockState.isAir()) {
                  renderBlockAnalysis(blockState, blockPos);
                  rendered = true;
               }
            }

            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (!rendered) {
               analysisOpacity = 0.0F;
            } else {
               if (analysisOpacity < analysisMaxOpacity) {
                  analysisOpacity += 0.1F;
               }
            }
         }
      }
   }

   private static void renderEntityAnalysis(LivingEntity target) {
      boolean isLeftSide = isLeftSide(3);
      boolean isPlayer = target instanceof Player;
      renderWithFlipping(ENTITY_ANALYSIS, 0.0F, 0.0F, 95, 156, analysisOpacity, isLeftSide);
      if (target instanceof ILivingPartEntity part) {
         UUID head = part.getHeadId();
         if (head == null) {
            return;
         }

         LivingEntity cached = cachedHeadEntity != null ? cachedHeadEntity.get() : null;
         if (cached != null && cached.isAlive() && head.equals(cachedHeadUuid)) {
            target = cached;
         } else {
            List<LivingEntity> list = level.getEntitiesOfClass(
               LivingEntity.class, target.getBoundingBox().inflate(15.0), entity -> head.equals(entity.getUUID())
            );
            if (list.isEmpty()) {
               return;
            }

            target = list.getFirst();
            cachedHeadUuid = head;
            cachedHeadEntity = new WeakReference<>(target);
         }
      }

      IExistence targetExistence = TensuraStorages.getExistenceFrom(target);
      if (targetExistence != null) {
         int hp = (int)target.getHealth();
         if (analysisCfg.useHearts) {
            hp = (int)Math.ceil(hp / 2.0);
         }

         int shp = (int)Math.ceil(targetExistence.getSpiritualHealth());
         int armor = target.getArmorValue();
         int nameColor = targetExistence.getAlignment().getColor();
         if (target instanceof ArchDaemonEntity daemon) {
            nameColor = TensuraColors.getTonedARGB(daemon.getVariant().getBaseColor(), 4.0F);
         }

         int textPosY = 30;
         int verticalStep = 9 + 2;
         if (isPlayer) {
            Races races = RaceAPI.getRaceFrom(target);
            Optional<ManasRaceInstance> optional = races.getRace();
            if (optional.isPresent()) {
               ManasRace race = optional.get().getRace();
               Component component = race.getName();
               if (component != null) {
                  graphics.drawString(font, component, 7, textPosY, nameColor);
                  textPosY += verticalStep;
               }
            }
         }

         String nameText = target.getName().getString();
         if (target instanceof IGender gender) {
            if (gender.isMale()) {
               nameText = nameText + " ♂";
            } else if (gender.isFemale()) {
               nameText = nameText + " ♀";
            }
         }

         String hpText = ANALYSIS_HP_LABEL.getString() + ": " + hp;
         String shpText = ANALYSIS_SHP_LABEL.getString() + ": " + shp;
         String armorText = ANALYSIS_ARMOR_LABEL.getString() + ": " + armor;
         int namePosX = isLeftSide ? 8 : 9;
         int hpPosX = 9 + font.width(hpText);
         int hpPosY = textPosY;
         int shpPosX = 9 + font.width(shpText);
         int var40;
         int shpPosY = var40 = textPosY + verticalStep;
         int armorPosX = 9 + font.width(armorText);
         int armorPosY = textPosY = var40 + verticalStep;
         renderWithFlipping(HEART, hpPosX, hpPosY - 1, 9, 9, analysisOpacity, isLeftSide);
         renderWithFlipping(SPIRITUAL_HEART, shpPosX, shpPosY - 1, 9, 9, analysisOpacity, isLeftSide);
         renderWithFlipping(ARMOR, armorPosX, armorPosY - 1, 9, 9, analysisOpacity, isLeftSide);
         if (nameText.length() > 13) {
            RenderHelper.drawSimpleScrollingText(graphics, font, nameText, namePosX, 12, 13, nameColor);
         } else {
            RenderHelper.drawCenteredText(graphics, font, nameText, namePosX, 12, 78, nameColor);
         }

         RenderHelper.drawScaledTextInArea(graphics, font, Component.literal(hpText), 7.0F, hpPosY, 95.0F, 20.0F, 16777215);
         RenderHelper.drawScaledTextInArea(graphics, font, Component.literal(shpText), 7.0F, shpPosY, 95.0F, 20.0F, 16777215);
         RenderHelper.drawScaledTextInArea(graphics, font, Component.literal(armorText), 7.0F, armorPosY, 95.0F, 20.0F, 16777215);
         float epDifference = 1.0F + analysisLevel * 0.5F;
         double maxEPToSee = epDifference * EnergyHelper.getMaxEP(player);
         boolean tooHighEP = EnergyHelper.getMaxEP(target) > maxEPToSee;
         if (!tooHighEP) {
            double aura = targetExistence.getAura();
            double mana = targetExistence.getMagicule();
            String auraText = ANALYSIS_AURA_LABEL.getString() + ": " + RenderHelper.getShortenedNumber(aura).getString();
            String manaText = ANALYSIS_MAGICULE_LABEL.getString() + ": " + RenderHelper.getShortenedNumber(mana).getString();
            int var42;
            RenderHelper.drawScaledTextInArea(graphics, font, Component.literal(manaText), 7.0F, var42 = textPosY + verticalStep, 95.0F, 20.0F, 16777215);
            RenderHelper.drawScaledTextInArea(graphics, font, Component.literal(auraText), 7.0F, textPosY = var42 + verticalStep, 95.0F, 20.0F, 16777215);
         }

         double ePoints = EnergyHelper.getMaxEP(target);
         String epToShow = tooHighEP ? RenderHelper.getShortenedNumber(maxEPToSee).getString() + "+" : RenderHelper.getShortenedNumber(ePoints).getString();
         String ePointsText = ANALYSIS_EP_LABEL.getString() + ": " + epToShow;
         int var43;
         RenderHelper.drawScaledTextInArea(
            graphics, font, Component.literal(ePointsText), 7.0F, var43 = textPosY + verticalStep, 95.0F, 20.0F, tooHighEP ? 16733525 : 16777215
         );
         if (!tooHighEP) {
            IAbility abilityData = TensuraStorages.getAbilityFrom(target);
            if (abilityData != null) {
               ManasSkill ability = null;

               for (int i = 0; i < 3; i++) {
                  ability = abilityData.getAbilitySlot(analysisSlot).getSkill();
                  if (ability != null) {
                     break;
                  }

                  analysisSlot = (analysisSlot + 1) % 3;
               }

               if (ability != null) {
                  int var44 = 121;
                  if (!minecraft.isPaused()) {
                     analysisSlotSwitch++;
                  }

                  if (analysisSlotSwitch >= 100) {
                     analysisSlot = (analysisSlot + 1) % 3;
                     analysisSlotSwitch = 0;
                  }

                  Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(target).getSkill(ability);
                  if (!optional.isEmpty()) {
                     ManasSkillInstance instance = optional.get();
                     if (!instance.isMastered(target)) {
                        Component abilityName = instance.getDisplayName();
                        TextColor color = abilityName.getStyle().getColor();
                        if (color == null) {
                           color = TextColor.fromRgb(16711680);
                        }

                        if (abilityName.getString().length() > 12) {
                           RenderHelper.drawSimpleScrollingText(graphics, font, abilityName, 8, var44, 12, color.getValue());
                        } else {
                           RenderHelper.drawCenteredText(graphics, font, abilityName, 6, var44, 83, color.getValue());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void renderBlockAnalysis(BlockState blockState, BlockPos blockPos) {
      boolean isLeftSide = isLeftSide(3);
      net.minecraft.world.level.block.Block block = blockState.getBlock();
      renderWithFlipping(BLOCK_ANALYSIS, 0.0F, 0.0F, 95, 156, analysisOpacity, isLeftSide);
      int namePosX = isLeftSide ? 8 : 9;
      int textPosY = 30;
      int verticalOffset = 9 + 2;
      int lightLevel = Math.max(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos));
      float hardness = block.defaultDestroyTime();
      float explosionResistance = block.getExplosionResistance();
      boolean correctTool = player.hasCorrectToolForDrops(blockState);
      String text = "-> ";
      String blockName = block.getName().getString();
      String hardnessText = text + (hardness < 0.0F ? BLOCK_UNBREAKABLE_LABEL.getString() : hardness);
      String correctToolText = text + (correctTool ? BLOCK_GUI_YES_LABEL : BLOCK_GUI_NO_LABEL).getString();
      String explosionResistanceText = text + explosionResistance;
      String lightLevelText = text + lightLevel;
      if (blockName.length() > 14) {
         RenderHelper.drawSimpleScrollingText(graphics, font, blockName, namePosX, 12, 14, 16777215);
      } else {
         RenderHelper.drawCenteredText(graphics, font, blockName, namePosX, 12, 78, 16777215);
      }

      RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_HARDNESS_LABEL, 7, textPosY, 90, 16777215);
      int var21;
      RenderHelper.drawSimpleScrollingText(
         graphics, font, Component.literal(hardnessText), 7, var21 = textPosY + verticalOffset, 16, hardness < 0.0F ? 16733525 : 16777215
      );
      RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_CORRECT_TOOL_LABEL, 7, textPosY = var21 + verticalOffset, 16, 16777215);
      int var23;
      RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(correctToolText), 7, var23 = textPosY + verticalOffset, 16, 16777215);
      RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_EXPLOSION_RES_LABEL, 7, textPosY = var23 + verticalOffset, 16, 16777215);
      int var25;
      RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(explosionResistanceText), 7, var25 = textPosY + verticalOffset, 16, 16777215);
      RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_LIGHT_LEVEL_LABEL, 7, textPosY = var25 + verticalOffset, 16, 16777215);
      int var27;
      RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(lightLevelText), 7, var27 = textPosY + verticalOffset, 16, 16777215);
      if (level.getBlockEntity(blockPos) instanceof CharybdisCoreBlockEntity core) {
         double ePoints = core.getEP();
         String ePointsText = text + ePoints;
         RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_EP_LABEL, 7, textPosY = var27 + verticalOffset, 16, 16777215);
         RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(ePointsText), 7, var27 = textPosY + verticalOffset, 16, 16777215);
      }

      if (blockState.hasProperty(BlockStateProperties.POWER)) {
         int power = (Integer)blockState.getValue(BlockStateProperties.POWER);
         String powerText = text + power;
         RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_REDSTONE_LABEL, 7, textPosY = var27 + verticalOffset, 16, 16777215);
         RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(powerText), 7, var27 = textPosY + verticalOffset, 16, 16777215);
      }

      if (blockState.hasProperty(BlockStateProperties.HATCH)) {
         int hatch = (Integer)blockState.getValue(BlockStateProperties.HATCH);
         String hatchText = text + hatch + "/2";
         RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_EGG_HATCH_LABEL, 7, textPosY = var27 + verticalOffset, 16, 16777215);
         RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(hatchText), 7, var27 = textPosY + verticalOffset, 16, 16777215);
      }

      int age = getCropAge(blockState, false);
      int maxAge = getCropAge(blockState, true);
      if (age != -1 && maxAge != -1) {
         RenderHelper.drawSimpleScrollingText(graphics, font, BLOCK_AGE_LABEL, 7, textPosY = var27 + verticalOffset, 16, 16777215);
         if (blockState.getBlock() instanceof HipokuteGrass hipokute && (Integer)blockState.getValue(hipokute.getAgeProperty()) == 2) {
            String failure = text + BLOCK_FAILED_GROWTH_LABEL.getString();
            int var33;
            RenderHelper.drawSimpleScrollingText(graphics, font, Component.literal(failure), 7, var33 = textPosY + verticalOffset, 16, 16733525);
         } else {
            String ageText = text + age + "/" + maxAge;
            int var32;
            RenderHelper.drawSimpleScrollingText(
               graphics, font, Component.literal(ageText), 7, var32 = textPosY + verticalOffset, 16, age == maxAge ? 5635925 : 16777215
            );
         }
      }
   }

   private static void renderAbility(ManasSkill ability, int slot, int mode, boolean isLeftSide, boolean showFullInfo) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(ability);
      if (!optional.isEmpty()) {
         ManasSkillInstance instance = optional.get();
         ResourceLocation icon = ability.getSkillIcon();
         int slotOffset = 35 * slot;
         int iconPosX = isLeftSide ? 10 : 3;
         int iconPosY = 3 + slotOffset;
         int mastery = (int)instance.getMastery();
         int maxMastery = instance.getMaxMastery();
         boolean isLearning = mastery < 0;
         int masteryBarHeight;
         if (isLearning) {
            int max = 100;
            if (ability instanceof ResistSkill resistSkill) {
               max = resistSkill.getLearningPointRequirement();
            }

            masteryBarHeight = dynamicResizing(22, max - Math.abs(mastery), max);
         } else {
            masteryBarHeight = dynamicResizing(22, mastery, maxMastery);
         }

         int masteryBarPosX = isLeftSide ? 2 : 34;
         int masteryBarPosY = 28 + slotOffset - masteryBarHeight;
         renderWithFlipping(icon, iconPosX - 2, iconPosY - 2, 32, 32, isLeftSide);
         renderWithFlipping(isLearning ? LEARNING_BAR : MASTERY_BAR, masteryBarPosX, masteryBarPosY, 5, masteryBarHeight, isLeftSide);
         boolean isOnCooldown;
         if (ability instanceof TensuraSkill skill) {
            isOnCooldown = skill.shouldShowCoolDown(instance, player, mode);
         } else {
            isOnCooldown = instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(player, mode);
         }

         if (isOnCooldown) {
            int cooldownPosX = isLeftSide ? 10 : 3;
            int cooldownPosY = 3 + slotOffset;
            String cooldownText = String.valueOf(instance.getCoolDown(mode));
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderWithFlipping(COOLDOWN_ICON, cooldownPosX, cooldownPosY, 28, 28, isLeftSide);
            RenderSystem.disableBlend();
            RenderHelper.drawCenteredText(graphics, font, cooldownText, cooldownPosX, cooldownPosY + 10, 28, 16733525);
         }

         if (showFullInfo) {
            int color;
            String modeName;
            String abilityName;
            if (ability instanceof TensuraSkill tensuraSkill) {
               Component name = tensuraSkill.getColoredName();
               modeName = tensuraSkill.getModeName(instance, mode).getString();
               if (name == null) {
                  color = 8750469;
                  abilityName = SKILL_EMPTY_LABEL.getString();
               } else {
                  abilityName = name.getString();
                  if (isOnCooldown) {
                     color = 8750469;
                  } else {
                     TextColor textColor = name.getStyle().getColor();
                     if (textColor == null) {
                        color = 16711680;
                     } else {
                        color = textColor.getValue();
                     }
                  }
               }
            } else {
               color = 16711680;
               modeName = SKILL_EMPTY_LABEL.getString();
               abilityName = modeName;
            }

            int namePosX = isLeftSide ? 44 : -3 - font.width(abilityName.substring(0, Math.min(19, abilityName.length())));
            int namePosY = 8 + slotOffset;
            int modePosX = isLeftSide ? 43 : -2 - font.width(modeName.substring(0, Math.min(17, modeName.length())));
            int modePosY = 21 + slotOffset;
            RenderHelper.drawSimpleScrollingText(graphics, font, abilityName, namePosX, namePosY, 19, color);
            RenderHelper.drawSimpleScrollingText(graphics, font, modeName, modePosX, modePosY, 17, 8750469);
         }
      }
   }

   public static MobEffectInstance getLightningBorder() {
      if (player.getActiveEffects().isEmpty()) {
         return null;
      }

      MobEffectInstance effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE));
      if (effectInstance != null) {
         return effectInstance;
      }

      effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.BEAST_TRANSFORMATION));
      if (effectInstance != null) {
         return effectInstance;
      }

      effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE));
      if (effectInstance != null) {
         return effectInstance;
      }

      effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER));
      return effectInstance != null ? effectInstance : player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
   }

   public static MobEffectInstance getShadowBorder() {
      if (player.getActiveEffects().isEmpty()) {
         return null;
      }

      MobEffectInstance effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP));
      return effectInstance != null ? effectInstance : player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE));
   }

   public static MobEffectInstance getFadingGlowBorder() {
      if (player.getActiveEffects().isEmpty()) {
         return null;
      }

      MobEffectInstance effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION));
      if (effectInstance != null) {
         return effectInstance;
      }

      effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
      if (effectInstance != null) {
         return effectInstance;
      }

      effectInstance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
      return effectInstance != null ? effectInstance : player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
   }

   public static int getGlowBorderColor() {
      if (player == null) {
         return -1;
      }

      if (ReflectorSkill.hasFullCounter(player)) {
         return -3840;
      }

      if (SpatialDominationSkill.hasFaultField(player)) {
         return -15800373;
      }

      if (!player.getActiveEffects().isEmpty()) {
         if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT))) {
            return ((MobEffect)TensuraMobEffects.LUST_EMBRACEMENT.get()).getColor();
         }

         if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON))) {
            return ((MobEffect)TensuraMobEffects.MAGICULE_POISON.get()).getColor();
         }

         if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER))) {
            return ((MobEffect)TensuraMobEffects.FALSIFIER.get()).getColor();
         }
      }

      return player.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 1.0
         ? ((MobEffect)TensuraMobEffects.PRESENCE_CONCEALMENT.get()).getColor()
         : -1;
   }

   public static OverlayElement getElementById(int id) {
      return switch (id) {
         case 0 -> STATUS_LAYER;
         case 1 -> STATUS_BARS_LAYER;
         case 2 -> ABILITIES_LAYER;
         case 3 -> ANALYSIS_LAYER;
         case 4 -> ARMOR_DECORATION;
         case 5 -> BARRIER_DECORATION;
         case 6 -> AIR_DECORATION;
         case 7 -> FOOD_DECORATION;
         case 8 -> MOUNT_HEALTH_DECORATION;
         case 9 -> MOUNT_SPIRITUAL_HEALTH_DECORATION;
         default -> null;
      };
   }

   public static float[] getPositionsById(int id) {
      return switch (id) {
         case 0 -> statusPositions;
         case 1 -> barsPositions;
         case 2 -> abilitiesPositions;
         case 3 -> analysisPositions;
         case 4 -> armorPositions;
         case 5 -> barrierPositions;
         case 6 -> airPositions;
         case 7 -> foodPositions;
         case 8 -> mountHpPositions;
         case 9 -> mountShpPositions;
         default -> null;
      };
   }

   public static float getScaleById(int id) {
      return switch (id) {
         case 0 -> statusScale;
         case 1 -> barsScale;
         case 2 -> abilitiesScale;
         case 3 -> analysisScale;
         case 4 -> armorScale;
         case 5 -> barrierScale;
         case 6 -> airScale;
         case 7 -> foodScale;
         case 8 -> mountHpScale;
         case 9 -> mountShpScale;
         default -> 1.0F;
      };
   }

   public static void saveScaleById(int id, float scale) {
      switch (id) {
         case 0:
            statusCfg.scale = scale;
            break;
         case 1:
            statusBarsCfg.scale = scale;
            break;
         case 2:
            abilitiesCfg.scale = scale;
            break;
         case 3:
            analysisCfg.scale = scale;
            break;
         case 4:
            decorationsCfg.armor.scale = scale;
            break;
         case 5:
            decorationsCfg.barrier.scale = scale;
            break;
         case 6:
            decorationsCfg.air.scale = scale;
            break;
         case 7:
            decorationsCfg.food.scale = scale;
            break;
         case 8:
            decorationsCfg.mountHp.scale = scale;
            break;
         case 9:
            decorationsCfg.mountSpiritualHp.scale = scale;
      }
   }

   public static void savePositionById(int id) {
      switch (id) {
         case 0:
            statusCfg.positionX = statusPositions[0];
            statusCfg.positionY = statusPositions[1];
            break;
         case 1:
            statusBarsCfg.positionX = barsPositions[0];
            statusBarsCfg.positionY = barsPositions[1];
            break;
         case 2:
            abilitiesCfg.positionX = abilitiesPositions[0];
            abilitiesCfg.positionY = abilitiesPositions[1];
            break;
         case 3:
            analysisCfg.positionX = analysisPositions[0];
            analysisCfg.positionY = analysisPositions[1];
            break;
         case 4:
            decorationsCfg.armor.positionX = armorPositions[0];
            decorationsCfg.armor.positionY = armorPositions[1];
            break;
         case 5:
            decorationsCfg.barrier.positionX = barrierPositions[0];
            decorationsCfg.barrier.positionY = barrierPositions[1];
            break;
         case 6:
            decorationsCfg.air.positionX = airPositions[0];
            decorationsCfg.air.positionY = airPositions[1];
            break;
         case 7:
            decorationsCfg.food.positionX = foodPositions[0];
            decorationsCfg.food.positionY = foodPositions[1];
            break;
         case 8:
            decorationsCfg.mountHp.positionX = mountHpPositions[0];
            decorationsCfg.mountHp.positionY = mountHpPositions[1];
            break;
         case 9:
            decorationsCfg.mountSpiritualHp.positionX = mountShpPositions[0];
            decorationsCfg.mountSpiritualHp.positionY = mountShpPositions[1];
      }
   }

   public static boolean shouldPreview(int element) {
      return SettingsScreen.getPreviewElement() == element || SettingsScreen.getEditingElement() == element;
   }

   private static int dynamicResizing(int maxSize, double currentValue, double maxValue) {
      return Math.clamp((int)(maxSize * currentValue / maxValue), 0, maxSize);
   }

   private static ResourceLocation getHealthBarToRender(boolean spiritual) {
      boolean noEffects = player.getActiveEffects().isEmpty();
      if (spiritual) {
         if (noEffects) {
            return SPIRITUAL_HP_BAR;
         } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SOUL_DRAIN))) {
            return SOUL_DRAIN_SPIRITUAL_HP_BAR;
         } else {
            return player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY)) ? INSANITY_SPIRITUAL_HP_BAR : SPIRITUAL_HP_BAR;
         }
      } else if (noEffects) {
         return player.isFullyFrozen() ? FROZEN_HEALTH_BAR : HEALTH_BAR;
      } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION))) {
         return INFECTION_HEALTH_BAR;
      } else if (player.hasEffect(MobEffects.WITHER)) {
         return WITHER_HEALTH_BAR;
      } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON))) {
         return FATAL_POISON_HEALTH_BAR;
      } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION))) {
         return CORROSION_HEALTH_BAR;
      } else if (player.hasEffect(MobEffects.POISON)) {
         return POISON_HEALTH_BAR;
      } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST))
         || player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.CHILL))
         || player.isFullyFrozen()) {
         return FROZEN_HEALTH_BAR;
      } else if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.CURSE))) {
         return CURSE_HEALTH_BAR;
      } else {
         return player.hasEffect(MobEffects.ABSORPTION) ? ABSORPTION_HEALTH_BAR : HEALTH_BAR;
      }
   }

   private static int getCropAge(BlockState blockState, boolean maxAge) {
      if (blockState.getBlock() instanceof CropBlock crop) {
         return maxAge ? crop.getMaxAge() : crop.getAge(blockState);
      } else {
         return blockState.getProperties()
            .stream()
            .filter(property -> property.getName().equals("age") && property instanceof IntegerProperty)
            .findFirst()
            .map(property -> (IntegerProperty)property)
            .map(age -> maxAge ? Collections.max(age.getPossibleValues()) : (Integer)blockState.getValue(age))
            .orElse(-1);
      }
   }

   private static void renderWithFlipping(ResourceLocation texture, float x, float y, int width, int height, boolean isLeftSide) {
      RenderHelper.preciseBlit(graphics, texture, x, y, width, height, 1.0F, 0.0F, 0.0F, isLeftSide ? width : -width, height, width, height, false);
   }

   private static void renderWithFlipping(ResourceLocation texture, float x, float y, int width, int height, float alpha, boolean isLeftSide) {
      RenderHelper.preciseBlit(graphics, texture, x, y, width, height, alpha, 0.0F, 0.0F, isLeftSide ? width : -width, height, width, height, true);
   }

   public static boolean isLeftSide(int element) {
      if (element != 0 && element != 2 && element != 3 && isDefaultRendering(element)) {
         return isLeftSide(0);
      }

      float[] positions = getPositionsById(element);
      if (positions == null) {
         return true;
      }

      return switch (element) {
         case 0 -> statusCfg.side == 1 || statusCfg.side != 2 && positions[0] * statusScale <= centerX;
         case 1 -> statusBarsCfg.side == 1 || statusBarsCfg.side != 2 && positions[0] * barsScale <= centerX;
         case 2 -> abilitiesCfg.side == 1 || abilitiesCfg.side != 2 && positions[0] * abilitiesScale <= centerX;
         case 3 -> analysisCfg.side == 1 || analysisCfg.side != 2 && positions[0] * analysisScale <= centerX;
         case 4 -> decorationsCfg.armor.side == 1 || decorationsCfg.armor.side != 2 && positions[0] * armorScale <= centerX;
         case 5 -> decorationsCfg.barrier.side == 1 || decorationsCfg.barrier.side != 2 && positions[0] * barrierScale <= centerX;
         case 6 -> decorationsCfg.air.side == 1 || decorationsCfg.air.side != 2 && positions[0] * airScale <= centerX;
         case 7 -> decorationsCfg.food.side == 1 || decorationsCfg.food.side != 2 && positions[0] * foodScale <= centerX;
         case 8 -> decorationsCfg.mountHp.side == 1 || decorationsCfg.mountHp.side != 2 && positions[0] * mountHpScale <= centerX;
         case 9 -> decorationsCfg.mountSpiritualHp.side == 1 || decorationsCfg.mountSpiritualHp.side != 2 && positions[0] * mountShpScale <= centerX;
         default -> true;
      };
   }

   public static boolean isDefaultRendering(int element) {
      return switch (element) {
         case 0 -> statusCfg.defaultRendering;
         case 1 -> statusBarsCfg.defaultRendering;
         case 2 -> abilitiesCfg.defaultRendering;
         case 3 -> analysisCfg.defaultRendering;
         case 4 -> decorationsCfg.armor.defaultRendering;
         case 5 -> decorationsCfg.barrier.defaultRendering;
         case 6 -> decorationsCfg.air.defaultRendering;
         case 7 -> decorationsCfg.food.defaultRendering;
         case 8 -> decorationsCfg.mountHp.defaultRendering;
         case 9 -> decorationsCfg.mountSpiritualHp.defaultRendering;
         default -> false;
      };
   }

   private static void renderElement(OverlayElement overlayElement, int element, float scale) {
      if (poseStack != null) {
         poseStack.pushPose();
         transform(element, scale, isLeftSide(element));
         overlayElement.render();
         poseStack.popPose();
      }
   }

   private static void transform(int element, float scale, boolean isLeftSide) {
      float x = 0.0F;
      float y = 0.0F;
      float width = 0.0F;
      switch (element) {
         case 0:
            width = 150.0F;
            x = statusPositions[0];
            y = statusPositions[1];
            break;
         case 1:
            width = 81.0F;
            x = barsPositions[0];
            y = barsPositions[1];
            break;
         case 2:
            width = 41.0F;
            x = abilitiesPositions[0];
            y = abilitiesPositions[1];
            break;
         case 3:
            width = 95.0F;
            x = analysisPositions[0];
            y = analysisPositions[1];
            break;
         case 4:
            width = 9.0F;
            x = armorPositions[0];
            y = armorPositions[1];
            break;
         case 5:
            width = 9.0F;
            x = barrierPositions[0];
            y = barrierPositions[1];
            break;
         case 6:
            width = 9.0F;
            x = airPositions[0];
            y = airPositions[1];
            break;
         case 7:
            width = 9.0F;
            x = foodPositions[0];
            y = foodPositions[1];
            break;
         case 8:
            width = 9.0F;
            x = mountHpPositions[0];
            y = mountHpPositions[1];
            break;
         case 9:
            width = 9.0F;
            x = mountShpPositions[0];
            y = mountShpPositions[1];
      }

      if (element != 0 && element != 2 && element != 3 && isDefaultRendering(element)) {
         if (!isLeftSide) {
            x += (statusPositions[0] - 150.0F * (scale - 1.0F)) / scale;
         }

         y += statusPositions[1] / scale;
         poseStack.scale(scale, scale, 1.0F);
         poseStack.translate(x, y, 0.0F);
      } else {
         float adjustedX = isLeftSide ? x / scale : (x - width * (scale - 1.0F)) / scale;
         float adjustedY = y / scale;
         poseStack.scale(scale, scale, 1.0F);
         poseStack.translate(adjustedX, adjustedY, 0.0F);
      }
   }

   private static float clampScale(float scale) {
      return Math.clamp(scale, 0.05F, 5.0F);
   }

   public static void reloadAllValues() {
      boolean defaultStatusRender = isDefaultRendering(0);
      boolean defaultBarsRender = isDefaultRendering(1);
      boolean defaultAbilitiesRender = isDefaultRendering(2);
      boolean defaultAnalysisRender = isDefaultRendering(3);
      boolean defaultAirRender = isDefaultRendering(6);
      boolean defaultFoodRender = isDefaultRendering(7);
      boolean defaultArmorRender = isDefaultRendering(4);
      boolean defaultBarrierRender = isDefaultRendering(5);
      boolean defaultMountHpRender = isDefaultRendering(8);
      boolean defaultMountShpRender = isDefaultRendering(9);
      boolean isLeftSide = isLeftSide(0);
      int ignore = SettingsScreen.getEditingElement();
      statusScale = clampScale(statusCfg.scale);
      if (ignore != 0) {
         if (defaultStatusRender) {
            statusPositions[0] = isLeftSide ? 0.0F : screenWidth - (player.isSpectator() ? 44 : 150);
            statusPositions[1] = 0.0F;
         } else {
            statusPositions[0] = statusCfg.positionX;
            statusPositions[1] = statusCfg.positionY;
         }
      }

      airScale = defaultAirRender ? statusScale : clampScale(decorationsCfg.air.scale);
      if (ignore != 6) {
         if (defaultAirRender) {
            airPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 122.0F : 19.0F;
            airPositions[1] = 34.0F;
         } else {
            airPositions[0] = decorationsCfg.air.positionX;
            airPositions[1] = decorationsCfg.air.positionY;
         }
      }

      foodScale = defaultFoodRender ? statusScale : clampScale(decorationsCfg.food.scale);
      if (ignore != 7) {
         if (defaultFoodRender) {
            foodPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 64.0F : 77.0F;
            foodPositions[1] = 34.0F;
         } else {
            foodPositions[0] = decorationsCfg.food.positionX;
            foodPositions[1] = decorationsCfg.food.positionY;
         }
      }

      armorScale = defaultArmorRender ? statusScale : clampScale(decorationsCfg.armor.scale);
      if (ignore != 4) {
         if (defaultArmorRender) {
            armorPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 93.0F : 48.0F;
            armorPositions[1] = 34.0F;
         } else {
            armorPositions[0] = decorationsCfg.armor.positionX;
            armorPositions[1] = decorationsCfg.armor.positionY;
         }
      }

      barrierScale = defaultBarrierRender ? statusScale : clampScale(decorationsCfg.barrier.scale);
      if (ignore != 5) {
         if (defaultBarrierRender) {
            barrierPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 86.0F : 55.0F;
            barrierPositions[1] = 46.0F;
         } else {
            barrierPositions[0] = decorationsCfg.barrier.positionX;
            barrierPositions[1] = decorationsCfg.barrier.positionY;
         }
      }

      mountHpScale = defaultMountHpRender ? statusScale : clampScale(decorationsCfg.mountHp.scale);
      if (ignore != 8) {
         if (defaultMountHpRender) {
            mountHpPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 86.0F : 55.0F;
            mountHpPositions[1] = 56.0F;
         } else {
            mountHpPositions[0] = decorationsCfg.mountHp.positionX;
            mountHpPositions[1] = decorationsCfg.mountHp.positionY;
         }
      }

      mountShpScale = defaultMountShpRender ? statusScale : clampScale(decorationsCfg.mountSpiritualHp.scale);
      if (ignore != 9) {
         if (defaultMountShpRender) {
            mountShpPositions[0] = isLeftSide ? statusPositions[0] / statusScale + 116.0F : 30.0F;
            mountShpPositions[1] = 56.0F;
         } else {
            mountShpPositions[0] = decorationsCfg.mountSpiritualHp.positionX;
            mountShpPositions[1] = decorationsCfg.mountSpiritualHp.positionY;
         }
      }

      barsScale = defaultBarsRender ? statusScale : clampScale(statusBarsCfg.scale);
      if (ignore != 1) {
         if (defaultBarsRender) {
            barsPositions[0] = isLeftSide ? statusPositions[0] / statusScale : 69.0F;
            barsPositions[1] = 44.0F;
         } else {
            barsPositions[0] = statusBarsCfg.positionX;
            barsPositions[1] = statusBarsCfg.positionY;
         }
      }

      abilitiesScale = clampScale(abilitiesCfg.scale);
      if (ignore != 2) {
         if (defaultAbilitiesRender) {
            abilitiesPositions[0] = isLeftSide(2) ? 0.0F : screenWidth - 41;
            abilitiesPositions[1] = screenHeight / 2.0F - 20.0F;
         } else {
            abilitiesPositions[0] = abilitiesCfg.positionX;
            abilitiesPositions[1] = abilitiesCfg.positionY;
         }
      }

      analysisScale = clampScale(analysisCfg.scale);
      analysisMaxOpacity = analysisCfg.opacity;
      if (analysisOpacity > analysisMaxOpacity) {
         analysisOpacity = analysisMaxOpacity;
      }

      if (ignore != 3) {
         if (defaultAnalysisRender) {
            analysisPositions[0] = isLeftSide(3) ? 0.0F : screenWidth - 95;
            analysisPositions[1] = screenHeight / 2.0F - 78.0F;
         } else {
            analysisPositions[0] = analysisCfg.positionX;
            analysisPositions[1] = analysisCfg.positionY;
         }
      }
   }

   @Generated
   public static int getScreenWidth() {
      return screenWidth;
   }

   @Generated
   public static int getScreenHeight() {
      return screenHeight;
   }

   @Generated
   public static boolean isShouldHudRender() {
      return shouldHudRender;
   }

   @Generated
   public static void setShouldHudRender(boolean shouldHudRender) {
      OverlayHandler.shouldHudRender = shouldHudRender;
   }
}
