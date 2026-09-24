package io.github.manasmods.tensura.command.get;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Command("race")
public class RaceGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_race_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getRace(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("race") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         if (optional.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.command.race.no_race", new Object[]{entity.getName()}));
         } else {
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.command.race.get", new Object[]{entity.getName(), optional.get().getRace().getName()}),
               ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_race_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getRace(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("race") String dl) throws CommandSyntaxException {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (optional.isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.command.race.no_race", new Object[]{player.getName()}));
      } else {
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.race.get", new Object[]{player.getName(), optional.get().getRace().getName()}), ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_race_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getAlignment(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("alignment") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable(
               "tensura.command.race.alignment.get", new Object[]{entity.getName(), TensuraStorages.getExistenceFrom(entity).getAlignment().getName()}
            ),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_race_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getAlignment(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("alignment") String dl) throws CommandSyntaxException {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      TensuraCommands.sendSuccess(
         stack,
         Component.translatable(
            "tensura.command.race.alignment.get", new Object[]{player.getName(), TensuraStorages.getExistenceFrom(player).getAlignment().getName()}
         ),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_race_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSpiritualForm(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("spiritualForm") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         boolean form = TensuraStorages.getExistenceFrom(entity).isSpiritualForm();
         Component component = Component.translatable(form ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.race.spiritual_form.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_race_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSpiritualForm(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("spiritualForm") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      boolean form = TensuraStorages.getExistenceFrom(player).isSpiritualForm();
      Component component = Component.translatable(form ? "tensura.message.enabled" : "tensura.message.disabled");
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.race.spiritual_form.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
      );
      return true;
   }
}
