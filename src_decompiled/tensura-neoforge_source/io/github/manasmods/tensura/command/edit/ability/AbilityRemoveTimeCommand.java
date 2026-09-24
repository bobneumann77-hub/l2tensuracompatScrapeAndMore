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

@Command("removeTime")
public class AbilityRemoveTimeCommand {
   @Execute
   public boolean removeTime(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @IntegerArg("seconds") int amount
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

               optional.get().setRemoveTime(amount);
               optional.get().markDirty();
               TensuraCommands.sendSuccess(
                  stack,
                  entity,
                  Component.translatable("tensura.skill.set_remove_time", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName(), amount})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean removeTimeAll(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all, @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> true, amount);
      return true;
   }

   @Execute
   public boolean removeTimeAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> skill instanceof Skill, amount);
      return true;
   }

   @Execute
   public boolean removeTimeAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean removeTimeAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> skill instanceof Magic, amount);
      return true;
   }

   @Execute
   public boolean removeTimeAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean removeTimeAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @IntegerArg("seconds") int amount
   ) throws CommandSyntaxException {
      removeTimeAllAbility(stack, selector, skill -> skill instanceof Battlewill, amount);
      return true;
   }

   private static void removeTimeAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, int amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            if (skills.getLearnedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.empty_storage"));
            } else {
               int i = 0;

               for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
                  if (instance != null && predicate.test(instance.getSkill())) {
                     instance.setRemoveTime(amount);
                     instance.markDirty();
                     i++;
                  }
               }

               if (i > 0) {
                  TensuraCommands.sendSuccess(
                     stack, entity, Component.translatable("tensura.skill.set_remove_time.all", new Object[]{entity.getName(), amount})
                  );
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.set_cooldown.all.no_changes", new Object[]{entity.getName()}));
               }
            }
         }
      }
   }

   private static void removeTimeRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, int amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkillInstance> list = skills.getLearnedSkills().stream().filter(skillInstance -> predicate.test(skillInstance.getSkill())).toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.set_cooldown.all.no_changes", new Object[]{entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().random.nextInt(list.size())).getSkill();
               Optional<ManasSkillInstance> instance = skills.getSkill(skill);
               if (instance.isPresent()) {
                  instance.get().setRemoveTime(amount);
                  instance.get().markDirty();
                  TensuraCommands.sendSuccess(
                     stack, entity, Component.translatable("tensura.skill.set_remove_time", new Object[]{skill.getName(), entity.getName(), amount})
                  );
               }
            }
         }
      }
   }
}
