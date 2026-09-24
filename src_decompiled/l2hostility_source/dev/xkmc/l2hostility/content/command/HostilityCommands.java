package dev.xkmc.l2hostility.content.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "l2hostility", bus = Bus.GAME)
public class HostilityCommands {
   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("hostility").then(LHPlayerCommands.build()))
                  .then(LHRegionCommands.build()))
               .then(LHMobCommands.build())
         );
   }

   protected static LiteralArgumentBuilder<CommandSourceStack> literal(String str) {
      return LiteralArgumentBuilder.literal(str);
   }

   protected static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }
}
