package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class TransmissionTypeArgument extends StringRepresentableArgument<WarpPoint.TransmissionType> {
   private TransmissionTypeArgument() {
      super(WarpPoint.TransmissionType.CODEC, WarpPoint.TransmissionType::values);
   }

   public static TransmissionTypeArgument type() {
      return new TransmissionTypeArgument();
   }

   public static WarpPoint.TransmissionType getType(CommandContext<CommandSourceStack> context, String string) {
      return (WarpPoint.TransmissionType)context.getArgument(string, WarpPoint.TransmissionType.class);
   }
}
