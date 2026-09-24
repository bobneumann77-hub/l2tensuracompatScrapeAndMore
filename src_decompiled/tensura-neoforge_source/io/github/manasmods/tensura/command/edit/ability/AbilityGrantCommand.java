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
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.MagicTypeArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArg;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("grant")
public class AbilityGrantCommand {
   @Execute
   public boolean grant(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @SkillArg Holder<ManasSkill> skill) throws CommandSyntaxException {
      for (Entity entity : selector.findEntities(stack)) {
         if (entity instanceof LivingEntity living) {
            TensuraSkillInstance instance = new TensuraSkillInstance((ManasSkill)skill.value());
            instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
            if (SkillHelper.learnSkill(living, instance, -1, null)) {
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.skill.granted", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()})
               );
            } else {
               stack.sendFailure(Component.translatable("tensura.skill.already_has", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean grantAll(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean grantAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill
   ) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean grantAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type
   ) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean grantAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic
   ) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean grantAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type
   ) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean grantAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allSkill
   ) throws CommandSyntaxException {
      grantAllAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   @Execute
   public boolean grantRandom(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("random") String random) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean grantRandomSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSKill
   ) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> skill instanceof Skill);
      return true;
   }

   @Execute
   public boolean grantRandomSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSKill,
      @SkillTypeArg Skill.SkillType type
   ) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean grantRandomMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic
   ) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean grantRandomMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @MagicTypeArg Magic.MagicType type
   ) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean grantRandomBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("battlewill") String randomBattlewill
   ) throws CommandSyntaxException {
      grantRandomAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   private static void grantAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            int i = 0;

            for (ManasSkill skill : SkillAPI.getSkillRegistry()
               .entrySet()
               .stream()
               .map(Entry::getValue)
               .filter(skillx -> predicate.test(skillx) && !SkillUtils.hasSkill(entity, skillx))
               .toList()) {
               TensuraSkillInstance instance = new TensuraSkillInstance(skill);
               instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
               if (SkillHelper.learnSkill(entity, instance, -1, null)) {
                  i++;
               }
            }

            TensuraCommands.sendSuccess(stack, selected, Component.translatable("tensura.skill.granted_all", new Object[]{i, selected.getName()}));
         }
      }
   }

   private static void grantRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkill> list = SkillAPI.getSkillRegistry()
               .entrySet()
               .stream()
               .map(Entry::getValue)
               .filter(
                  manasSkill -> skills.getSkill(manasSkill).isPresent() && ((ManasSkillInstance)skills.getSkill(manasSkill).get()).getMastery() > 0.0
                     ? false
                     : predicate.test(manasSkill)
               )
               .toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.granted_all", new Object[]{0, entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().random.nextInt(list.size()));
               TensuraSkillInstance instance = new TensuraSkillInstance(skill);
               instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
               if (SkillHelper.learnSkill(entity, instance, -1, null)) {
                  TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.skill.granted", new Object[]{skill.getName(), entity.getName()}));
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.already_has", new Object[]{entity.getName(), skill.getName()}));
               }
            }
         }
      }
   }
}
