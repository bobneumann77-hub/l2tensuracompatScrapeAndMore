package io.github.manasmods.tensura.command.edit.race;

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
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.AlignmentArg;
import io.github.manasmods.tensura.command.argument.RaceArg;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("race")
@Permission(value = "tensura.command.edit_race", permissionLevel = PermissionLevel.GAMEMASTER)
public class RaceCommand {
   @Execute
   public boolean set(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("set") String s, @RaceArg Holder<ManasRace> holder
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            Races races = RaceAPI.getRaceFrom(entity);
            if (races.setRace((ManasRace)holder.value(), false)) {
               ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(entity);
               if (playerData != null) {
                  playerData.setTrackedEvolution(null);
                  playerData.markDirty();
               }

               TensuraCommands.sendSuccess(
                  stack, target, Component.translatable("tensura.command.race.edit", new Object[]{target.getName(), ((ManasRace)holder.value()).getName()})
               );
            } else {
               stack.sendFailure(Component.translatable("tensura.argument.race.invalid"));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean set(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("set") String s,
      @RaceArg Holder<ManasRace> holder,
      @BooleanArg("resetExistenceStats") boolean resetExistence
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            Races races = RaceAPI.getRaceFrom(entity);
            if (races.setRace((ManasRace)holder.value(), false)) {
               if (resetExistence && holder.value() instanceof TensuraRace race) {
                  race.resetExistenceData(entity);
               }

               ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(entity);
               if (playerData != null) {
                  playerData.setTrackedEvolution(null);
                  playerData.markDirty();
               }

               TensuraCommands.sendSuccess(
                  stack, target, Component.translatable("tensura.command.race.edit", new Object[]{target.getName(), ((ManasRace)holder.value()).getName()})
               );
            } else {
               stack.sendFailure(Component.translatable("tensura.argument.race.invalid"));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean set(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("alignment") String s,
      @AlignmentArg Alignment alignment
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAlignment(alignment);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, target, Component.translatable("tensura.command.race.alignment.set", new Object[]{target.getName(), alignment.getName()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean set(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("alignment") String s,
      @BooleanArg("spiritualForm") boolean spiritualForm
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSpiritualForm(spiritualForm);
            existence.markDirty();
            Component component = Component.translatable(spiritualForm ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(
               stack, target, Component.translatable("tensura.command.race.spiritual_form.set", new Object[]{target.getName(), component})
            );
         }
      }

      return true;
   }
}
