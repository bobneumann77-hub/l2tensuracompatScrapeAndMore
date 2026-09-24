package io.github.manasmods.tensura.neoforge.handler.client;

import io.github.manasmods.tensura.client.TensuraItemProperties;
import io.github.manasmods.tensura.client.layer.HumanoidEffectLayer;
import io.github.manasmods.tensura.client.layer.WingsLayer;
import io.github.manasmods.tensura.client.layer.multi.DiamondPathLayer;
import io.github.manasmods.tensura.client.layer.multi.EarthLockLayer;
import io.github.manasmods.tensura.client.layer.multi.EmbracementLayer;
import io.github.manasmods.tensura.client.layer.multi.FaultFieldLayer;
import io.github.manasmods.tensura.client.layer.multi.FrozenLayer;
import io.github.manasmods.tensura.client.layer.multi.HarvestFestivalLayer;
import io.github.manasmods.tensura.client.layer.multi.MadOgreLayer;
import io.github.manasmods.tensura.client.layer.multi.MagicBarrierLayer;
import io.github.manasmods.tensura.client.layer.multi.OppressionLayer;
import io.github.manasmods.tensura.client.layer.multi.PetrificationLayer;
import io.github.manasmods.tensura.client.layer.multi.WebbedLayer;
import io.github.manasmods.tensura.client.layer.multi.WindLayer;
import io.github.manasmods.tensura.client.screen.KilnScreen;
import io.github.manasmods.tensura.client.screen.MiningStationScreen;
import io.github.manasmods.tensura.client.screen.NamingScreen;
import io.github.manasmods.tensura.client.screen.ReincarnationScreen;
import io.github.manasmods.tensura.client.screen.SkillCreationScreen;
import io.github.manasmods.tensura.client.screen.SmithingBenchScreen;
import io.github.manasmods.tensura.client.screen.SpellbindingScreen;
import io.github.manasmods.tensura.client.screen.WoodcutterScreen;
import io.github.manasmods.tensura.particle.TensuraParticleRegister;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.world.dimension.TensuraDimensionEffects;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "tensura", value = Dist.CLIENT)
public class ClientSetupHandlers {
   @SubscribeEvent
   private static void clientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(TensuraItemProperties::addCustomItemProperties);
   }

   @SubscribeEvent
   private static void onDimensionSpecialEffectRegistry(RegisterDimensionSpecialEffectsEvent event) {
      event.register(TensuraDimensions.HELL_LOCATION, new TensuraDimensionEffects.Hell());
   }

   @SubscribeEvent
   private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
      event.register((MenuType)TensuraMenuTypes.NAMING_MENU.get(), NamingScreen::new);
      event.register((MenuType)TensuraMenuTypes.KILN.get(), KilnScreen::new);
      event.register((MenuType)TensuraMenuTypes.SKILL_CREATION.get(), SkillCreationScreen::new);
      event.register((MenuType)TensuraMenuTypes.SMITHING_BENCH.get(), SmithingBenchScreen::new);
      event.register((MenuType)TensuraMenuTypes.SPELLBINDING.get(), SpellbindingScreen::new);
      event.register((MenuType)TensuraMenuTypes.REINCARNATION.get(), ReincarnationScreen::new);
      event.register((MenuType)TensuraMenuTypes.MINING_STATION_MENU.get(), MiningStationScreen::new);
      event.register((MenuType)TensuraMenuTypes.WOOD_CUTTER_MENU.get(), WoodcutterScreen::new);
   }

   @SubscribeEvent
   private static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
      TensuraParticleRegister.registerParticleProviders(event::registerSpriteSet);
   }

   @SubscribeEvent
   public static void onLayersAdd(AddLayers event) {
      for (EntityType<?> type : event.getEntityTypes()) {
         if (event.getRenderer(type) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new WingsLayer(renderer));
            renderer.addLayer(new DiamondPathLayer(renderer));
            renderer.addLayer(new EarthLockLayer(renderer));
            renderer.addLayer(new EmbracementLayer(renderer));
            renderer.addLayer(new FaultFieldLayer(renderer));
            renderer.addLayer(new HarvestFestivalLayer(renderer));
            renderer.addLayer(new MadOgreLayer(renderer));
            renderer.addLayer(new MagicBarrierLayer(renderer));
            renderer.addLayer(new WindLayer(renderer));
            renderer.addLayer(new FrozenLayer(renderer));
            renderer.addLayer(new OppressionLayer(renderer));
            renderer.addLayer(new PetrificationLayer(renderer));
            renderer.addLayer(new WebbedLayer(renderer));
         }
      }

      for (Model model : event.getSkins()) {
         if (event.getSkin(model) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new WingsLayer(renderer));
            renderer.addLayer(new HumanoidEffectLayer(renderer));
            renderer.addLayer(new DiamondPathLayer(renderer));
            renderer.addLayer(new EarthLockLayer(renderer));
            renderer.addLayer(new EmbracementLayer(renderer));
            renderer.addLayer(new FaultFieldLayer(renderer));
            renderer.addLayer(new HarvestFestivalLayer(renderer));
            renderer.addLayer(new MadOgreLayer(renderer));
            renderer.addLayer(new MagicBarrierLayer(renderer));
            renderer.addLayer(new WindLayer(renderer));
            renderer.addLayer(new FrozenLayer(renderer));
            renderer.addLayer(new OppressionLayer(renderer));
            renderer.addLayer(new PetrificationLayer(renderer));
            renderer.addLayer(new WebbedLayer(renderer));
         }
      }
   }
}
