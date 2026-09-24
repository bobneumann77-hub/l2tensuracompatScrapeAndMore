package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class SpiritLevelArgument extends StringRepresentableArgument<SpiritualMagic.SpiritLevel> {
   private SpiritLevelArgument() {
      super(SpiritualMagic.SpiritLevel.CODEC, SpiritualMagic.SpiritLevel::values);
   }

   public static SpiritLevelArgument spiritLevel() {
      return new SpiritLevelArgument();
   }

   public static SpiritualMagic.SpiritLevel getSpiritLevel(CommandContext<CommandSourceStack> context, String string) {
      return (SpiritualMagic.SpiritLevel)context.getArgument(string, SpiritualMagic.SpiritLevel.class);
   }
}
