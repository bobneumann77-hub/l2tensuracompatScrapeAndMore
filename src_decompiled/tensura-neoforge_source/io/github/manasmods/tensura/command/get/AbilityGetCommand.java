package io.github.manasmods.tensura.command.get;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.impl.SkillStorage;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.MagicTypeArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArg;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@Command(value = "ability", subCommands = {AbilityGetCommand.ListCommand.class, AbilityGetCommand.CheckFlightCommand.class})
public class AbilityGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSkill(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("check") String data,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @SkillArg Holder<ManasSkill> skill
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Skills storage = SkillAPI.getSkillsFrom(entity);
         Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
         if (optional.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName()}));
         } else {
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable(
                  "tensura.skill.skill_get", new Object[]{entity.getName(), ((ManasSkill)skill.value()).getName(), optional.get().toNBT().getAsString()}
               ),
               ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSkill(
      @SenderArg CommandSourceStack stack, @LiteralArg("check") String data, @LiteralArg("self") String self, @SkillArg Holder<ManasSkill> skill
   ) throws CommandSyntaxException {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Skills storage = SkillAPI.getSkillsFrom(player);
      Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
      if (optional.isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), ((ManasSkill)skill.value()).getName()}));
      } else {
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable(
               "tensura.skill.skill_get", new Object[]{player.getName(), ((ManasSkill)skill.value()).getName(), optional.get().toNBT().getAsString()}
            ),
            ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getStorage(
      @SenderArg CommandSourceStack stack, @LiteralArg("storage") String s, @EntityArg(Type.ENTITY) EntitySelector selector, @SkillArg Holder<ManasSkill> skill
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialStorage spatial) {
            Skills storage = SkillAPI.getSkillsFrom(entity);
            Optional<ManasSkillInstance> optional = storage.getSkill(manasSkill);
            if (optional.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{entity.getName(), manasSkill.getName()}));
            } else {
               ServerPlayer player = stack.getPlayer();
               if (player == null) {
                  return false;
               }

               spatial.openSpatialStorage(player, entity, optional.get());
            }

            return true;
         } else {
            stack.sendFailure(Component.translatable("tensura.skill.spatial_storage.failed"));
            return false;
         }
      } else {
         return false;
      }
   }

   private static void getSkillList(CommandSourceStack stack, @Nullable Entity selected, Predicate<ManasSkill> predicate, boolean excludeLearning) {
      if (selected instanceof LivingEntity entity) {
         SkillStorage skills = SkillAPI.getSkillsFrom(entity);
         MutableComponent component = null;

         for (ManasSkill manasSkill : new ArrayList<>(
            skills.getLearnedSkills()
               .stream()
               .filter(skillx -> !excludeLearning || skillx.getMastery() >= 0.0)
               .map(ManasSkillInstance::getSkill)
               .filter(skillx -> skillx.getRegistryName() != null)
               .filter(predicate)
               .sorted(Comparator.comparing(skillx -> skillx.getRegistryName().toString()))
               .toList()
         )) {
            MutableComponent name = manasSkill.getName();
            if (manasSkill instanceof TensuraSkill skill) {
               name = skill.getColoredName();
            }

            if (name != null) {
               if (component == null) {
                  component = name;
               } else {
                  component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
               }
            }
         }

         if (component == null) {
            stack.sendFailure(Component.translatable("tensura.skill.skill_list.empty", new Object[]{entity.getName()}));
         } else {
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.skill.skill_list", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
            );
         }
      }
   }

   @Command("checkFlight")
   @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
   public static class CheckFlightCommand {
      @Execute
      public boolean checkFlight(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector) throws CommandSyntaxException {
         for (ServerPlayer player : selector.findPlayers(stack)) {
            if (SkillUtils.canFlyLegit(player)) {
               TensuraCommands.sendSuccess(
                  stack, Component.translatable("tensura.skill.check_flight", new Object[]{player.getName()}), ChatFormatting.DARK_GREEN
               );
            } else {
               TensuraCommands.sendSuccess(
                  stack, Component.translatable("tensura.skill.check_flight.false", new Object[]{player.getName()}), ChatFormatting.RED
               );
            }
         }

         return true;
      }
   }

   @Command(
      value = "list",
      subCommands = {
            AbilityGetCommand.ListCommand.ListAllSkillCommand.class,
            AbilityGetCommand.ListCommand.ListAllMagicCommand.class,
            AbilityGetCommand.ListCommand.ListAllBattlewillCommand.class
      }
   )
   public static class ListCommand {
      @Execute
      @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
      public boolean listAll(
         @SenderArg CommandSourceStack stack,
         @LiteralArg("all") String all,
         @EntityArg(Type.ENTITY) EntitySelector selector,
         @BooleanArg("excludeLearning") boolean exclude
      ) throws CommandSyntaxException {
         AbilityGetCommand.getSkillList(stack, selector.findSingleEntity(stack), skill -> true, exclude);
         return true;
      }

      @Execute
      @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
      public boolean listAll(
         @SenderArg CommandSourceStack stack, @LiteralArg("all") String all, @LiteralArg("self") String self, @BooleanArg("excludeLearning") boolean exclude
      ) {
         AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> true, exclude);
         return true;
      }

      @Command("battlewill")
      public static class ListAllBattlewillCommand {
         @Execute
         @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
         public boolean listAll(
            @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @BooleanArg("excludeLearning") boolean exclude
         ) throws CommandSyntaxException {
            AbilityGetCommand.getSkillList(stack, selector.findSingleEntity(stack), skill -> skill instanceof Battlewill, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
         public boolean listAll(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @BooleanArg("excludeLearning") boolean exclude) {
            AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> skill instanceof Battlewill, exclude);
            return true;
         }
      }

      @Command("magic")
      public static class ListAllMagicCommand {
         @Execute
         @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
         public boolean listAll(
            @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @BooleanArg("excludeLearning") boolean exclude
         ) throws CommandSyntaxException {
            AbilityGetCommand.getSkillList(stack, selector.findSingleEntity(stack), skill -> skill instanceof Magic, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
         public boolean listAll(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @BooleanArg("excludeLearning") boolean exclude) {
            AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> skill instanceof Magic, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
         public boolean listAllWithType(
            @SenderArg CommandSourceStack stack,
            @EntityArg(Type.ENTITY) EntitySelector selector,
            @MagicTypeArg Magic.MagicType type,
            @BooleanArg("excludeLearning") boolean exclude
         ) throws CommandSyntaxException {
            AbilityGetCommand.getSkillList(stack, selector.findSingleEntity(stack), skill -> skill instanceof Magic magic && magic.getType() == type, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
         public boolean listAllWithType(
            @SenderArg CommandSourceStack stack,
            @LiteralArg("self") String self,
            @MagicTypeArg Magic.MagicType type,
            @BooleanArg("excludeLearning") boolean exclude
         ) {
            AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> skill instanceof Magic magic && magic.getType() == type, exclude);
            return true;
         }
      }

      @Command("skill")
      public static class ListAllSkillCommand {
         @Execute
         @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
         public boolean listAll(
            @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @BooleanArg("excludeLearning") boolean exclude
         ) throws CommandSyntaxException {
            AbilityGetCommand.getSkillList(stack, selector.findSingleEntity(stack), skill -> skill instanceof Skill, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
         public boolean listAll(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @BooleanArg("excludeLearning") boolean exclude) {
            AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> skill instanceof Skill, exclude);
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_others", permissionLevel = PermissionLevel.MODERATOR)
         public boolean listAllWithType(
            @SenderArg CommandSourceStack stack,
            @EntityArg(Type.ENTITY) EntitySelector selector,
            @SkillTypeArg Skill.SkillType type,
            @BooleanArg("excludeLearning") boolean exclude
         ) throws CommandSyntaxException {
            AbilityGetCommand.getSkillList(
               stack, selector.findSingleEntity(stack), skill -> skill instanceof Skill skillType && skillType.getType() == type, exclude
            );
            return true;
         }

         @Execute
         @Permission(value = "tensura.command.get_ability_self", permissionLevel = PermissionLevel.PLAYER)
         public boolean listAllWithType(
            @SenderArg CommandSourceStack stack,
            @LiteralArg("self") String self,
            @SkillTypeArg Skill.SkillType type,
            @BooleanArg("excludeLearning") boolean exclude
         ) {
            AbilityGetCommand.getSkillList(stack, stack.getPlayer(), skill -> skill instanceof Skill skillType && skillType.getType() == type, exclude);
            return true;
         }
      }
   }
}
