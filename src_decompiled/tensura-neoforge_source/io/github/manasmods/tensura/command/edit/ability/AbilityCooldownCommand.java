package io.github.manasmods.tensura.command.edit.ability;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.MagicTypeArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArg;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("cooldown")
public class AbilityCooldownCommand {
   @Execute
   public boolean cooldown(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @IntegerArg("seconds") int amount,
      @IntegerArg("mode") int mode
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               if (amount < 0) {
                  amount = 0;
               }

               optional.get().setCoolDown(amount, mode);
               storage.markDirty();
               TensuraCommands.sendSuccess(
                  stack,
                  entity,
                  Component.translatable("tensura.skill.set_cooldown", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName(), mode, amount})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean cooldownAll(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all, @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> true, amount);
      return true;
   }

   @Execute
   public boolean cooldownAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> skill instanceof Skill, amount);
      return true;
   }

   @Execute
   public boolean cooldownAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean cooldownAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> skill instanceof Magic, amount);
      return true;
   }

   @Execute
   public boolean cooldownAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean cooldownAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      cooldownAllAbility(stack, selector, skill -> skill instanceof Battlewill, amount);
      return true;
   }

   private static void cooldownAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, int amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            if (skills.getLearnedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.empty_storage"));
            } else {
               int i = 0;

               for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
                  if (instance != null && predicate.test(instance.getSkill())) {
                     for (int mode = 0; mode < instance.getModes(); mode++) {
                        instance.setCoolDown(amount, mode);
                     }

                     instance.markDirty();
                     i++;
                  }
               }

               if (i > 0) {
                  skills.markDirty();
                  TensuraCommands.sendSuccess(
                     stack, entity, Component.translatable("tensura.skill.set_cooldown.all", new Object[]{entity.getName(), i, amount})
                  );
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.set_cooldown.all.no_changes", new Object[]{entity.getName()}));
               }
            }
         }
      }
   }
}
