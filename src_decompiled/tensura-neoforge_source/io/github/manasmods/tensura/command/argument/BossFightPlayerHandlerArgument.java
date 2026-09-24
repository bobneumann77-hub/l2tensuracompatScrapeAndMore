package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class BossFightPlayerHandlerArgument extends StringRepresentableArgument<BossFightInstance.PlayerCountHandler> {
   private BossFightPlayerHandlerArgument() {
      super(BossFightInstance.PlayerCountHandler.CODEC, BossFightInstance.PlayerCountHandler::values);
   }

   public static BossFightPlayerHandlerArgument handler() {
      return new BossFightPlayerHandlerArgument();
   }

   public static BossFightInstance.PlayerCountHandler getHandler(CommandContext<CommandSourceStack> context, String string) {
      return (BossFightInstance.PlayerCountHandler)context.getArgument(string, BossFightInstance.PlayerCountHandler.class);
   }
}
