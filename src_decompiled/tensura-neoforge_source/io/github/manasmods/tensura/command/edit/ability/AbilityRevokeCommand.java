package io.github.manasmods.tensura.command.edit.ability;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
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
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("revoke")
public class AbilityRevokeCommand {
   @Execute
   public boolean revoke(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @SkillArg Holder<ManasSkill> skill) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            if (storage.getSkill((ManasSkill)skill.value()).isPresent()) {
               storage.forgetSkill((ManasSkill)skill.value());
               TensuraCommands.sendSuccess(
                  stack,
                  entity,
                  Component.translatable("tensura.skill.revoked", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()}),
                  ChatFormatting.RED
               );
            } else {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean revokeAll(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean revokeAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill
   ) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> skill instanceof Skill);
      return true;
   }

   @Execute
   public boolean revokeAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type
   ) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean revokeAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic
   ) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean revokeAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type
   ) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean revokeAll(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill
   ) throws CommandSyntaxException {
      revokeAllAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   @Execute
   public boolean revokeRandom(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("random") String random) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean revokeRandomSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSKill
   ) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> skill instanceof Skill);
      return true;
   }

   @Execute
   public boolean revokeRandomSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSKill,
      @SkillTypeArg Skill.SkillType type
   ) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean revokeRandomMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic
   ) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean revokeRandomMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @MagicTypeArg Magic.MagicType type
   ) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean revokeRandomBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("battlewill") String randomBattlewill
   ) throws CommandSyntaxException {
      revokeRandomAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   private static void revokeAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            int i = 0;
            Skills skills = SkillAPI.getSkillsFrom(entity);

            for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
               if (predicate.test(instance.getSkill())) {
                  skills.forgetSkill(instance.getSkill());
                  i++;
               }
            }

            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.skill.revoked_all", new Object[]{i, entity.getName()}), ChatFormatting.RED
            );
         }
      }
   }

   private static void revokeRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkillInstance> list = skills.getLearnedSkills().stream().filter(skillInstance -> predicate.test(skillInstance.getSkill())).toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.revoked", new Object[]{0, entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().getRandom().nextInt(list.size())).getSkill();
               skills.forgetSkill(skill);
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.skill.revoked", new Object[]{skill.getName(), entity.getName()}), ChatFormatting.RED
               );
            }
         }
      }
   }
}
