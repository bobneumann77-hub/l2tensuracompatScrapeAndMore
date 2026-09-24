package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.ability.magic.Magic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class MagicTypeArgument extends StringRepresentableArgument<Magic.MagicType> {
   private MagicTypeArgument() {
      super(Magic.MagicType.CODEC, Magic.MagicType::values);
   }

   public static MagicTypeArgument magicType() {
      return new MagicTypeArgument();
   }

   public static Magic.MagicType getMagicType(CommandContext<CommandSourceStack> context, String string) {
      return (Magic.MagicType)context.getArgument(string, Magic.MagicType.class);
   }
}
