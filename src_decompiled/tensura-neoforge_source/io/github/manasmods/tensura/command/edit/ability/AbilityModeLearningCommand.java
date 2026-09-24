package io.github.manasmods.tensura.command.edit.ability;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.TensuraSkill;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("modeLearning")
public class AbilityModeLearningCommand {
   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @IntegerArg(value = "mode", min = 0) int mode,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               ManasSkillInstance instance = optional.get();
               if (instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                  List<Integer> list = tensuraSkill.getModeLearningList(instance);
                  if (list.isEmpty()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
                  } else if (mode < 0 || mode > instance.getModes()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_mode", new Object[]{optional.get().getSkill().getName(), mode}));
                  } else if (!list.contains(mode)) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn", new Object[]{mode, optional.get().getSkill().getName()}));
                  } else {
                     CompoundTag tag = instance.getOrCreateTag();
                     tag.putDouble(tensuraSkill.getModeId(instance, mode), amount);
                     instance.markDirty();
                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable(
                           "tensura.skill.mode_learning.success", new Object[]{mode, instance.getChatDisplayName(true), amount, entity.getName()}
                        )
                     );
                  }
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @IntegerArg(value = "mode", min = 0) int mode,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               ManasSkillInstance instance = optional.get();
               if (instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                  List<Integer> list = tensuraSkill.getModeLearningList(instance);
                  if (list.isEmpty()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
                  } else if (mode < 0 || mode > instance.getModes()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_mode", new Object[]{optional.get().getSkill().getName(), mode}));
                  } else if (!list.contains(mode)) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn", new Object[]{mode, optional.get().getSkill().getName()}));
                  } else {
                     CompoundTag tag = instance.getOrCreateTag();
                     int amount = TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2;
                     tag.putDouble(tensuraSkill.getModeId(instance, mode), amount);
                     instance.markDirty();
                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable(
                           "tensura.skill.mode_learning.success", new Object[]{mode, instance.getChatDisplayName(true), amount, entity.getName()}
                        )
                     );
                  }
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("all") String all,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               ManasSkillInstance instance = optional.get();
               if (!(instance.getSkill() instanceof TensuraSkill tensuraSkill)) {
                  stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
               } else {
                  List<Integer> list = tensuraSkill.getModeLearningList(instance);
                  if (list.isEmpty()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
                  } else {
                     int i = 0;
                     CompoundTag tag = instance.getOrCreateTag();

                     for (int mode : list) {
                        tag.putDouble(tensuraSkill.getModeId(instance, mode), amount);
                        i++;
                     }

                     instance.markDirty();
                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable(
                           "tensura.skill.mode_learning.success_all", new Object[]{i, instance.getChatDisplayName(true), amount, entity.getName()}
                        )
                     );
                  }
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("all") String all,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               ManasSkillInstance instance = optional.get();
               if (!(instance.getSkill() instanceof TensuraSkill tensuraSkill)) {
                  stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
               } else {
                  List<Integer> list = tensuraSkill.getModeLearningList(instance);
                  if (list.isEmpty()) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode_learning.no_learn_all", new Object[]{optional.get().getSkill().getName()}));
                  } else {
                     int i = 0;
                     CompoundTag tag = instance.getOrCreateTag();
                     int amount = TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement;

                     for (int mode : list) {
                        tag.putDouble(tensuraSkill.getModeId(instance, mode), amount);
                        i++;
                     }

                     instance.markDirty();
                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable(
                           "tensura.skill.mode_learning.success_all", new Object[]{i, instance.getChatDisplayName(true), amount, entity.getName()}
                        )
                     );
                  }
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> true, amount);
      return true;
   }

   @Execute
   public boolean setModeLearning(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all, @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> true, TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2);
      return true;
   }

   @Execute
   public boolean setModeLearningSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Skill, amount);
      return true;
   }

   @Execute
   public boolean setModeLearningSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Skill, TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2);
      return true;
   }

   @Execute
   public boolean setModeLearningSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean setModeLearningSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(
         stack,
         selector,
         skill -> skill instanceof Skill skillType && skillType.getType() == type,
         TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2
      );
      return true;
   }

   @Execute
   public boolean setModeLearningMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Magic, amount);
      return true;
   }

   @Execute
   public boolean setModeLearningMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Magic, TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2);
      return true;
   }

   @Execute
   public boolean setModeLearningMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean setModeLearningMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(
         stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2
      );
      return true;
   }

   @Execute
   public boolean setModeLearningBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Battlewill, amount);
      return true;
   }

   @Execute
   public boolean setModeLearningBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      setModeLearningAll(stack, selector, skill -> skill instanceof Battlewill, TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * 2);
      return true;
   }

   private static void setModeLearningAll(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, double amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            if (skills.getLearnedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.empty_storage", new Object[]{entity.getName()}));
            } else {
               int i = 0;

               for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
                  if (instance != null && predicate.test(instance.getSkill()) && instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                     List<Integer> list = tensuraSkill.getModeLearningList(instance);
                     if (!list.isEmpty()) {
                        CompoundTag tag = instance.getOrCreateTag();

                        for (int mode : list) {
                           tag.putDouble(tensuraSkill.getModeId(instance, mode), amount);
                        }

                        instance.markDirty();
                        i++;
                     }
                  }
               }

               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.skill.mode_learning.success_everything", new Object[]{i, entity.getName()})
               );
            }
         }
      }
   }
}
