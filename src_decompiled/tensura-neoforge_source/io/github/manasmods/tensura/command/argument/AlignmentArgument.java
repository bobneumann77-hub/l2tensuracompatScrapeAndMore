package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.storage.Alignment;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class AlignmentArgument extends StringRepresentableArgument<Alignment> {
   private AlignmentArgument() {
      super(Alignment.CODEC, Alignment::values);
   }

   public static AlignmentArgument alignment() {
      return new AlignmentArgument();
   }

   public static Alignment getAlignment(CommandContext<CommandSourceStack> context, String string) {
      return (Alignment)context.getArgument(string, Alignment.class);
   }
}
