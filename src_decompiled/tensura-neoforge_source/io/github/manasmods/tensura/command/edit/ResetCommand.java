package io.github.manasmods.tensura.command.edit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.RemoveSkillEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.RaceArg;
import io.github.manasmods.tensura.item.misc.ResetScrollItem;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@Command("reset")
@Permission(value = "tensura.command.reset", permissionLevel = PermissionLevel.GAMEMASTER)
public class ResetCommand {
   @Execute
   public boolean resetEverything(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("everything") String dl) throws CommandSyntaxException {
      for (ServerPlayer player : selector.findPlayers(stack)) {
         if (player.isSpectator()) {
            player.displayClientMessage(
               Component.translatable("tooltip.tensura.reset_scroll.disable.name", new Object[]{player.getName()}).withStyle(ChatFormatting.RED), false
            );
         } else {
            ResetScrollItem.resetEverything(player);
            TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.all", new Object[]{player.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean resetRace(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("race") String dl) throws CommandSyntaxException {
      resetRace(stack, selector.findPlayers(stack), null);
      return true;
   }

   @Execute
   public boolean resetRace(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("race") String dl, @RaceArg Holder<ManasRace> holder
   ) throws CommandSyntaxException {
      resetRace(stack, selector.findPlayers(stack), (ManasRace)holder.value());
      return true;
   }

   @Execute
   public boolean resetAwakening(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("awakening") String dl) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         existence.setDemonLordSeed(false);
         existence.setTrueDemonLord(false);
         existence.setHeroEgg(false);
         existence.setTrueHero(false);
         existence.setSoulPoints(0);
         existence.setBlessed(false);
         existence.markDirty();
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.awakening", new Object[]{player.getName()}));
      }

      return true;
   }

   @Execute
   public boolean resetSkill(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("skill") String dl) throws CommandSyntaxException {
      for (ServerPlayer player : selector.findPlayers(stack)) {
         ResetScrollItem.resetSkill(player, true);
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.skill", new Object[]{player.getName()}));
      }

      return true;
   }

   @Execute
   public boolean resetStatistic(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYERS) EntitySelector selector, @LiteralArg("statistic") String dl) throws CommandSyntaxException {
      for (ServerPlayer player : selector.findPlayers(stack)) {
         MinecraftServer server = player.getServer();
         if (server != null) {
            ServerStatsCounter stats = server.getPlayerList().getPlayerStats(player);
            stats.markAllDirty();

            for (Stat<?> stat : stats.getDirty()) {
               stats.setValue(player, stat, 0);
            }
         }

         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.stat", new Object[]{player.getName()}));
      }

      return true;
   }

   private static void resetRace(CommandSourceStack stack, Collection<? extends ServerPlayer> pTargets, @Nullable ManasRace race) {
      for (ServerPlayer player : pTargets) {
         if (player.isSpectator()) {
            player.displayClientMessage(
               Component.translatable("tooltip.tensura.reset_scroll.disable.name", new Object[]{player.getName()}).withStyle(ChatFormatting.RED), false
            );
         } else if (race == null) {
            ResetScrollItem.resetRace(player);
            TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.race", new Object[]{player.getName()}));
         } else {
            MinecraftServer server = player.getServer();
            if (server != null) {
               ServerStatsCounter stats = server.getPlayerList().getPlayerStats(player);
               stats.markAllDirty();

               for (Stat<?> stat : stats.getDirty()) {
                  stats.setValue(player, stat, 0);
               }
            }

            Races races = RaceAPI.getRaceFrom(player);
            Optional<ManasRaceInstance> optional = races.getRace();
            if (optional.isPresent()) {
               Skills storage = SkillAPI.getSkillsFrom(player);
               Iterator<ManasSkillInstance> iterator = storage.getLearnedSkills().iterator();

               while (iterator.hasNext()) {
                  if (iterator.next() instanceof TensuraSkillInstance instance
                     && ResetScrollItem.shouldRaceResetRemove(player, instance)
                     && !((RemoveSkillEvent)SkillEvents.REMOVE_SKILL.invoker()).removeSkill(instance, player, Changeable.of(null)).isFalse()) {
                     instance.onForgetSkill(player);
                     instance.markDirty();
                     iterator.remove();
                  }
               }
            }

            if (SkillUtils.hasSkill(player, (ManasSkill)UniqueSkills.CHOSEN_ONE.get())) {
               IExistence existence = TensuraStorages.getExistenceFrom(player);
               existence.setBlessed(true);
               existence.markDirty();
            }

            CookSkill.removeCookedHP(player);
            TensuraStorages.resetPlayerData(player);
            TensuraStorages.resetExistence(player);
            TensuraStorages.resetEffect(player);
            TensuraStorages.resetSpirit(player);
            ReincarnationMenu.setRace(player, race, true, false);
            ResetScrollItem.resetFlight(player);
            ResetScrollItem.resetWarpPoints(player);
            TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset.race", new Object[]{player.getName()}));
         }
      }
   }
}
