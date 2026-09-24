package io.github.manasmods.tensura;

import dev.architectury.platform.Platform;
import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.config.TensuraConfigs;
import io.github.manasmods.tensura.event.TensuraLevelEvents;
import io.github.manasmods.tensura.handler.TensuraHandlers;
import io.github.manasmods.tensura.network.TensuraNetwork;
import io.github.manasmods.tensura.registry.TensuraRegistry;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.world.TensuraGameRules;
import io.github.manasmods.tensura.world.biome.TensuraOverworldDesertRegion;
import io.github.manasmods.tensura.world.biome.TensuraOverworldRegion;
import io.github.manasmods.tensura.world.biome.TensuraSurfaceRules;
import net.neoforged.api.distmarker.Dist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;

public final class Tensura {
   public static final String MOD_ID = "tensura";
   public static final String VERSION = "2.0.1.2";
   public static final Logger LOG = LoggerFactory.getLogger("Tensura");

   public static void init() {
      TensuraConfigs.init();
      TensuraRegistry.init();
      TensuraStorages.init();
      TensuraNetwork.init();
      TensuraCommands.init();
      TensuraGameRules.init();
      TensuraHandlers.init();
      if (Platform.getEnv() == Dist.CLIENT) {
         TensuraClient.init();
      }

      ((Runnable)TensuraLevelEvents.POST_INIT.invoker()).run();
   }

   public static void initTerraBlender() {
      Regions.register(new TensuraOverworldRegion(4));
      Regions.register(new TensuraOverworldDesertRegion(5));
      SurfaceRuleManager.addSurfaceRules(RuleCategory.OVERWORLD, "tensura", TensuraSurfaceRules.makeRules());
   }

   public static Logger createLogger(String string) {
      return LoggerFactory.getLogger(String.format("Tensura/%s", string));
   }

   public static Logger createLogger(Class<?> clazz) {
      return LoggerFactory.getLogger(String.format("Tensura/%s", clazz.getSimpleName()));
   }
}
