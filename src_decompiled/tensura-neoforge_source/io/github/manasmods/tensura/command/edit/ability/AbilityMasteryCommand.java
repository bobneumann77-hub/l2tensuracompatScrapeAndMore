package io.github.manasmods.tensura.command.edit.ability;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillMasteryEvent;
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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("mastery")
public class AbilityMasteryCommand {
   @Execute
   public boolean master(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill,
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
               if (amount > instance.getMaxMastery()) {
                  stack.sendFailure(
                     Component.translatable("tensura.skill.mastery.above_max", new Object[]{optional.get().getSkill().getName(), instance.getMaxMastery()})
                  );
               } else {
                  Changeable<Double> newMastery = Changeable.of(amount);
                  EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(instance, entity, newMastery);
                  if (!result.isFalse()) {
                     instance.setMastery((Double)newMastery.get());
                     if (instance.isMastered(entity) && entity instanceof ServerPlayer player && instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                        tensuraSkill.addMasteryStatistic(player);
                     }

                     storage.markDirty();
                     TensuraCommands.sendSuccess(
                        stack,
                        entity,
                        Component.translatable("tensura.skill.mastery_point", new Object[]{instance.getChatDisplayName(true), entity.getName(), amount})
                     );
                     if (amount == instance.getMaxMastery()) {
                        if (entity instanceof LivingEntity livingEntity) {
                           instance.onSkillMastered(livingEntity);
                        }

                        entity.sendSystemMessage(
                           Component.translatable("tensura.skill.mastery", new Object[]{optional.get().getSkill().getName()}).withStyle(ChatFormatting.GREEN)
                        );
                     } else if (amount < 0.0 && instance.isToggled()) {
                        instance.setToggled(false);
                        instance.onToggleOff(entity);
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean master(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @SkillArg Holder<ManasSkill> skill, @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
            } else {
               ManasSkillInstance instance = optional.get();
               Changeable<Double> newMastery = Changeable.of((double)instance.getMaxMastery());
               EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(instance, entity, newMastery);
               if (!result.isFalse()) {
                  instance.setMastery((Double)newMastery.get());
                  if (instance.isMastered(entity) && entity instanceof ServerPlayer player && instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                     tensuraSkill.addMasteryStatistic(player);
                  }

                  storage.markDirty();
                  if (entity instanceof LivingEntity livingEntity) {
                     instance.onSkillMastered(livingEntity);
                  }

                  entity.sendSystemMessage(
                     Component.translatable("tensura.skill.mastery", new Object[]{optional.get().getSkill().getName()}).withStyle(ChatFormatting.GREEN)
                  );
               }
            }
         }
      }

      return true;
   }

   @Execute
   public boolean masteryAll(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> true, amount);
      return true;
   }

   @Execute
   public boolean masteryAll(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("all") String all, @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean masteryAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Skill, amount);
      return true;
   }

   @Execute
   public boolean masteryAllSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Skill);
      return true;
   }

   @Execute
   public boolean masteryAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean masteryAllSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("skill") String allSkill,
      @SkillTypeArg Skill.SkillType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean masteryAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Magic, amount);
      return true;
   }

   @Execute
   public boolean masteryAllMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean masteryAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean masteryAllMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("magic") String allMagic,
      @MagicTypeArg Magic.MagicType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean masteryAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Battlewill, amount);
      return true;
   }

   @Execute
   public boolean masteryAllBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("all") String all,
      @LiteralArg("battlewill") String allBattlewill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryAllAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   @Execute
   public boolean masteryRandom(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> true, amount);
      return true;
   }

   @Execute
   public boolean masteryRandom(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("random") String random, @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> true);
      return true;
   }

   @Execute
   public boolean masteryRandomSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSkill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Skill, amount);
      return true;
   }

   @Execute
   public boolean masteryRandomSkill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSkill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Skill);
      return true;
   }

   @Execute
   public boolean masteryRandomSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSkill,
      @SkillTypeArg Skill.SkillType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean masteryRandomSkillWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("skill") String randomSkill,
      @SkillTypeArg Skill.SkillType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Skill skillType && skillType.getType() == type);
      return true;
   }

   @Execute
   public boolean masteryRandomMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Magic, amount);
      return true;
   }

   @Execute
   public boolean masteryRandomMagic(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Magic);
      return true;
   }

   @Execute
   public boolean masteryRandomMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @MagicTypeArg Magic.MagicType type,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type, amount);
      return true;
   }

   @Execute
   public boolean masteryRandomMagicWithType(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("magic") String randomMagic,
      @MagicTypeArg Magic.MagicType type,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Magic magic && magic.getType() == type);
      return true;
   }

   @Execute
   public boolean masteryRandomBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("battlewill") String randomBattlewill,
      @DoubleArg(value = "mastery", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Battlewill, amount);
      return true;
   }

   @Execute
   public boolean masteryRandomBattlewill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("random") String random,
      @LiteralArg("battlewill") String randomBattlewill,
      @LiteralArg("max") String s
   ) throws CommandSyntaxException {
      masteryRandomAbility(stack, selector, skill -> skill instanceof Battlewill);
      return true;
   }

   private static void masteryAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, double amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            if (skills.getLearnedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.empty_storage", new Object[]{entity.getName()}));
            } else {
               int i = 0;
               int j = 0;

               for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
                  if (instance != null && predicate.test(instance.getSkill())) {
                     Changeable<Double> newMastery = Changeable.of(Math.min(amount, instance.getMaxMastery()));
                     EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(instance, entity, newMastery);
                     if (!result.isFalse()) {
                        instance.setMastery((Double)newMastery.get());
                        skills.markDirty();
                        i++;
                        if (amount >= instance.getMaxMastery()) {
                           if (entity instanceof LivingEntity livingEntity) {
                              instance.onSkillMastered(livingEntity);
                              if (entity instanceof ServerPlayer player && instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                                 tensuraSkill.addMasteryStatistic(player);
                              }
                           }

                           j++;
                        } else if (amount < 0.0 && instance.isToggled()) {
                           instance.setToggled(false);
                           instance.onToggleOff(entity);
                        }
                     }
                  }
               }

               if (i > 0) {
                  skills.markDirty();
                  TensuraCommands.sendSuccess(
                     stack, entity, Component.translatable("tensura.skill.mastery_point.all", new Object[]{entity.getName(), i, amount})
                  );
                  if (j > 0) {
                     entity.sendSystemMessage(
                        Component.translatable("tensura.skill.mastery.all", new Object[]{entity.getName(), j}).withStyle(ChatFormatting.GREEN)
                     );
                  }
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.mastery.no_changes", new Object[]{entity.getName()}));
               }
            }
         }
      }
   }

   private static void masteryAllAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            if (skills.getLearnedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.empty_storage", new Object[]{entity.getName()}));
            } else {
               int i = 0;
               int j = 0;

               for (ManasSkillInstance instance : List.copyOf(skills.getLearnedSkills())) {
                  if (instance != null && predicate.test(instance.getSkill()) && !instance.isMastered(entity)) {
                     Changeable<Double> newMastery = Changeable.of((double)instance.getMaxMastery());
                     EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(instance, entity, newMastery);
                     if (!result.isFalse()) {
                        instance.setMastery(instance.getMaxMastery());
                        skills.markDirty();
                        i++;
                        if (entity instanceof LivingEntity livingEntity) {
                           instance.onSkillMastered(livingEntity);
                           if (entity instanceof ServerPlayer player && instance.getSkill() instanceof TensuraSkill tensuraSkill) {
                              tensuraSkill.addMasteryStatistic(player);
                           }
                        }

                        j++;
                     }
                  }
               }

               if (i > 0) {
                  skills.markDirty();
                  entity.sendSystemMessage(
                     Component.translatable("tensura.skill.mastery.all", new Object[]{entity.getName(), j}).withStyle(ChatFormatting.GREEN)
                  );
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.mastery.no_changes", new Object[]{entity.getName()}));
               }
            }
         }
      }
   }

   private static void masteryRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate, double amount) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkillInstance> list = skills.getLearnedSkills().stream().filter(skillInstancex -> predicate.test(skillInstancex.getSkill())).toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.mastery.no_changes", new Object[]{entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().getRandom().nextInt(list.size())).getSkill();
               Optional<ManasSkillInstance> instance = skills.getSkill(skill);
               if (instance.isPresent()) {
                  ManasSkillInstance skillInstance = instance.get();
                  Changeable<Double> newMastery = Changeable.of(Math.min(amount, skill.getMaxMastery()));
                  EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(skillInstance, entity, newMastery);
                  if (!result.isFalse()) {
                     skillInstance.setMastery((Double)newMastery.get());
                     skills.markDirty();
                     TensuraCommands.sendSuccess(
                        stack, entity, Component.translatable("tensura.skill.mastery_point", new Object[]{skill.getName(), entity.getName(), amount})
                     );
                     if (amount >= skill.getMaxMastery()) {
                        skillInstance.onSkillMastered(entity);
                        if (entity instanceof ServerPlayer player && skillInstance.getSkill() instanceof TensuraSkill tensuraSkill) {
                           tensuraSkill.addMasteryStatistic(player);
                        }

                        entity.sendSystemMessage(
                           Component.translatable("tensura.skill.mastery", new Object[]{skillInstance.getSkill().getName()}).withStyle(ChatFormatting.GREEN)
                        );
                     } else if (amount < 0.0 && skillInstance.isToggled()) {
                        skillInstance.setToggled(false);
                        skillInstance.onToggleOff(entity);
                     }
                  }
               }
            }
         }
      }
   }

   private static void masteryRandomAbility(CommandSourceStack stack, EntitySelector selector, Predicate<ManasSkill> predicate) throws CommandSyntaxException {
      for (Entity selected : selector.findEntities(stack)) {
         if (selected instanceof LivingEntity entity) {
            Skills skills = SkillAPI.getSkillsFrom(entity);
            List<ManasSkillInstance> list = skills.getLearnedSkills().stream().filter(skillInstancex -> predicate.test(skillInstancex.getSkill())).toList();
            if (list.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.mastery.no_changes", new Object[]{entity.getName()}));
            } else {
               ManasSkill skill = list.get(entity.level().getRandom().nextInt(list.size())).getSkill();
               Optional<ManasSkillInstance> instance = skills.getSkill(skill);
               if (instance.isPresent()) {
                  ManasSkillInstance skillInstance = instance.get();
                  Changeable<Double> newMastery = Changeable.of((double)skillInstance.getMaxMastery());
                  EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(skillInstance, entity, newMastery);
                  if (!result.isFalse()) {
                     skillInstance.setMastery((Double)newMastery.get());
                     skillInstance.onSkillMastered(entity);
                     if (entity instanceof ServerPlayer player && skillInstance.getSkill() instanceof TensuraSkill tensuraSkill) {
                        tensuraSkill.addMasteryStatistic(player);
                     }

                     skills.markDirty();
                     entity.sendSystemMessage(Component.translatable("tensura.skill.mastery", new Object[]{skill.getName()}).withStyle(ChatFormatting.GREEN));
                  }
               }
            }
         }
      }
   }
}
