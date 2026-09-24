package com.github.b4ndithelps.l2tensura;

import com.github.b4ndithelps.l2tensura.config.EPConfig;
import com.github.b4ndithelps.l2tensura.handler.SimpleEPHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("l2tensura")
public class l2tensura {
   public static final String MODID = "l2tensura";
   private static final Logger LOGGER = LogUtils.getLogger();

   public l2tensura() {
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      modEventBus.addListener(this::commonSetup);
      EPConfig.register();
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(SimpleEPHandler.class);
      LOGGER.info("L2Hostility EP Enhancement System initialized!");
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   @EventBusSubscriber(modid = "l2tensura", bus = Bus.MOD, value = Dist.CLIENT)
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         l2tensura.LOGGER.info("HELLO FROM CLIENT SETUP");
         l2tensura.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.m_91087_().m_91094_().m_92546_());
      }
   }
}
