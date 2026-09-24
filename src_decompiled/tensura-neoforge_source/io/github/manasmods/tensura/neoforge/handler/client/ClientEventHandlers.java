package io.github.manasmods.tensura.neoforge.handler.client;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.config.client.HudConfig;
import io.github.manasmods.tensura.event.TensuraInputEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.client.XrayHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "tensura", value = Dist.CLIENT)
public class ClientEventHandlers {
   private static final List<ResourceLocation> CANCELLABLE_LAYERS = new ArrayList<>(
      Arrays.asList(
         VanillaGuiLayers.PLAYER_HEALTH, VanillaGuiLayers.ARMOR_LEVEL, VanillaGuiLayers.FOOD_LEVEL, VanillaGuiLayers.AIR_LEVEL, VanillaGuiLayers.VEHICLE_HEALTH
      )
   );

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   private static void onNameTagRender(RenderNameTagEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (player.level().getGameRules().getBoolean(TensuraGameRules.TENSURA_DISPLAY_NAME)) {
            IExistence existence = TensuraStorages.getExistenceFrom(player);
            if (existence.getName() != null) {
               event.setContent(Component.literal(existence.getName()).withStyle(event.getContent().getStyle()));
            }
         }
      }
   }

   @SubscribeEvent
   private static void onEntityRendering(Pre<? extends LivingEntity, ? extends EntityModel<?>> event) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         LivingEntity target = event.getEntity();
         if (!(target.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) < 1.0)) {
            if (!SkillUtils.shouldCancelInvisibility(player, target)) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   private static void onOverlay(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre event) {
      if (!((HudConfig)ConfigRegistry.getConfig(HudConfig.class)).vanillaHud) {
         if (CANCELLABLE_LAYERS.contains(event.getName())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onRenderWorld(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_PARTICLES) {
         XrayHelper.onXrayRendering(event.getPoseStack());
      }
   }

   @SubscribeEvent
   private static void onMovementInput(MovementInputUpdateEvent event) {
      ((TensuraInputEvents.MovementInputUpdateEvent)TensuraInputEvents.MOVEMENT_INPUT_UPDATE_EVENT.invoker()).input(event.getEntity(), event.getInput());
   }

   @SubscribeEvent
   private static void onComputeCameraAngles(ComputeCameraAngles event) {
      Player player = Minecraft.getInstance().player;
      if (player != null && !(TensuraClient.CONFIG.cameraShakeStrength <= 0.0F)) {
         IEffect effect = TensuraStorages.getEffectFrom(player);
         if (effect.getCameraShakeDuration() > 0) {
            float tickCount = (float)(player.tickCount + Minecraft.getInstance().getFrameTimeNs());
            float shake = effect.getCameraShakeLevel() * TensuraClient.CONFIG.cameraShakeStrength;
            event.setPitch((float)(event.getPitch() + shake * Math.cos(tickCount * 3.0F + 2.0F) * 25.0));
            event.setYaw((float)(event.getYaw() + shake * Math.cos(tickCount * 5.0F + 1.0F) * 25.0));
            event.setRoll((float)(event.getRoll() + shake * Math.cos(tickCount * 4.0F) * 25.0));
         }
      }
   }
}
