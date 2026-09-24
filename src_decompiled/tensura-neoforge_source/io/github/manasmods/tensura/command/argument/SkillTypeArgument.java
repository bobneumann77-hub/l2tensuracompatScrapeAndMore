package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;

public class SkillTypeArgument extends StringRepresentableArgument<Skill.SkillType> {
   private SkillTypeArgument() {
      super(Skill.SkillType.CODEC, Skill.SkillType::values);
   }

   public static SkillTypeArgument skillType() {
      return new SkillTypeArgument();
   }

   public static Skill.SkillType getSkillType(CommandContext<CommandSourceStack> context, String string) {
      return (Skill.SkillType)context.getArgument(string, Skill.SkillType.class);
   }
}
