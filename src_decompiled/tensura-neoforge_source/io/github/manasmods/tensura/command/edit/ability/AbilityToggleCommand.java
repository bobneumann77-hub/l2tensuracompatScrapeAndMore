package io.github.manasmods.tensura.command.edit.ability;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
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

@Command("toggle")
public class AbilityToggleCommand {
   @Execute
   public boolean toggle(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.no_skill", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()}));
            } else {
               ManasSkillInstance skillInstance = optional.get();
               if (skillInstance.canBeToggled(entity) && skillInstance.canInteractSkill(entity)) {
                  if (skillInstance.isToggled()) {
                     if (!toggle) {
                        skillInstance.setToggled(false);
                        skillInstance.onToggleOff(entity);
                     }

                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable("tensura.skill.toggle_off", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()})
                     );
                  } else if (toggle) {
                     skillInstance.setToggled(true);
                     skillInstance.onToggleOn(entity);
                     TensuraCommands.sendSuccess(
                        stack, entity, Component.translatable("tensura.skill.toggle_on", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()})
                     );
                  }

                  storage.markDirty();
               } else {
                  stack.sendFailure(
                     Component.translatable("tensura.skill.toggle.failed", new Object[]{((ManasSkill)skill.value()).getName(), entity.getName()})
                  );
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean toggleAll(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("everything") String everything,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> true, toggle);
      return true;
   }

   @Execute
   public boolean toggleAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> skill instanceof Skill, toggle);
      return true;
   }

   @Execute
   public boolean toggleAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, toggle);
      return true;
   }

   @Execute
   public boolean toggleAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> skill instanceof Magic, toggle);
      return true;
   }

   @Execute
   public boolean toggleAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, toggle);
      return true;
   }

   @Execute
   public boolean toggleAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleAllAbility(stack, selector, skill -> skill instanceof Battlewill, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandom(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("everything") String everything,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> true, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandomSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("skill") String allSkill,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> skill instanceof Skill, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandomSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandomMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("magic") String allMagic,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> skill instanceof Magic, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandomMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, toggle);
      return true;
   }

   @Execute
   public boolean toggleRandomBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @BooleanArg("toggle") boolean toggle
   ) throws CommandSyntaxException {
      toggleRandomAbility(stack, selector, skill -> skill instanceof Battlewill, toggle);
      return true;
   }

   private static void toggleAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, boolean toggle) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            int i = 0;
            Skills skills = SkillAPI.getSkillsFrom(entity);

            for (ManasSkillInstance instance : skills.getLearnedSkills()) {
               if (predicate.test(instance.getSkill())
                  && instance.getSkill().canBeToggled(instance, entity)
                  && instance.getSkill().canInteractSkill(instance, entity)
                  && instance.isToggled() != toggle) {
                  instance.setToggled(toggle);
                  if (toggle) {
                     instance.onToggleOn(entity);
                  } else {
                     instance.onToggleOff(entity);
                  }

                  i++;
               }
            }

            skills.markDirty();
            String message = toggle ? "tensura.skill.toggle_all.on" : "tensura.skill.toggle_all.off";
            TensuraCommands.sendSuccess(stack, entity, Component.translatable(message, new Object[]{i, entity.getName()}));
         }
      }
   }

   private static void toggleRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, boolean toggle) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkillInstance> list = skills.getLearnedSkills().stream().filter(skillInstancex -> predicate.test(skillInstancex.getSkill())).toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.set_cooldown.all.no_changes", new Object[]{entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().getRandom().nextInt(list.size())).getSkill();
               Optional<ManasSkillInstance> optional = skills.getSkill(skill);
               if (!optional.isEmpty()) {
                  ManasSkillInstance skillInstance = optional.get();
                  if (skillInstance.canBeToggled(entity) && skillInstance.canInteractSkill(entity)) {
                     if (skillInstance.isToggled()) {
                        if (!toggle) {
                           skillInstance.setToggled(false);
                           skillInstance.onToggleOff(entity);
                        }

                        TensuraCommands.sendSuccess(
                           stack, entity, Component.translatable("tensura.skill.toggle_off", new Object[]{skill.getName(), entity.getName()})
                        );
                     } else if (toggle) {
                        skillInstance.setToggled(true);
                        skillInstance.onToggleOn(entity);
                        TensuraCommands.sendSuccess(
                           stack, entity, Component.translatable("tensura.skill.toggle_on", new Object[]{skill.getName(), entity.getName()})
                        );
                     }

                     skills.markDirty();
                  } else {
                     stack.sendFailure(Component.translatable("tensura.skill.toggle.failed", new Object[]{skill.getName(), entity.getName()}));
                  }
               }
            }
         }
      }
   }
}
