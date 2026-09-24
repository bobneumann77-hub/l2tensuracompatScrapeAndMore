package io.github.manasmods.tensura.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Command("evolve")
@Permission(value = "tensura.command.evolve", permissionLevel = PermissionLevel.PLAYER)
public class EvolveCommand {
   @Execute
   public boolean evolveRace(@SenderArg CommandSourceStack stack, @LiteralArg("race") String l) throws CommandSyntaxException {
      Player player = stack.getPlayerOrException();
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (optional.isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.command.race.no_race", new Object[]{player.getName()}));
      } else {
         ManasRace evolution = optional.get().getRace().getDefaultEvolution(optional.get(), player);
         if (evolution != null && optional.get().getEvolutionProgress(player, evolution) >= 100.0F) {
            RaceHelper.evolveRace(player, evolution, true);
            TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.race.evolve.succeed", new Object[]{player.getName()}));
         } else {
            stack.sendFailure(Component.translatable("tensura.command.race.evolve.fail"));
         }
      }

      return true;
   }

   @Execute
   public boolean evolveDemonLord(@SenderArg CommandSourceStack stack, @LiteralArg("demonLord") String l) throws CommandSyntaxException {
      evolveTrueDemonLord(stack, stack.getPlayerOrException());
      return true;
   }

   @Execute
   public boolean evolveHero(@SenderArg CommandSourceStack stack, @LiteralArg("hero") String l) throws CommandSyntaxException {
      evolveTrueHero(stack, stack.getPlayerOrException());
      return true;
   }

   private static void evolveTrueDemonLord(CommandSourceStack stack, Player player) {
      Level level = player.level();
      IExistence cap = TensuraStorages.getExistenceFrom(player);
      int requirement = level.getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN);
      if (!cap.isDemonLordSeed()) {
         stack.sendFailure(Component.translatable("tensura.evolve.demon_lord.not_seed"));
      } else if (RaceUtils.shouldNamingStopAwakening(player, cap)) {
         stack.sendFailure(Component.translatable("tensura.evolve.demon_lord.seed_lost"));
      } else if (cap.getSoulPoints() / 1000 < requirement) {
         stack.sendFailure(Component.translatable("tensura.evolve.demon_lord.lack_soul"));
      } else if (cap.isTrueDemonLord() || cap.getHarvestTick() > 0) {
         stack.sendFailure(Component.translatable("tensura.evolve.demon_lord.already"));
      } else if (cap.isTrueHero()) {
         stack.sendFailure(Component.translatable("tensura.evolve.demon_lord.hero"));
      } else {
         ExistenceStorage.enterHarvestFestival(cap, player);
      }
   }

   private static void evolveTrueHero(CommandSourceStack stack, Player player) {
      IExistence cap = TensuraStorages.getExistenceFrom(player);
      if (!cap.isHeroEgg()) {
         stack.sendFailure(Component.translatable("tensura.evolve.hero.not_egg"));
      } else if (RaceUtils.shouldNamingStopAwakening(player, cap)) {
         stack.sendFailure(Component.translatable("tensura.evolve.hero.egg_lost"));
      } else if (cap.isTrueDemonLord() || cap.getHarvestTick() > 0) {
         stack.sendFailure(Component.translatable("tensura.evolve.hero.demon_lord"));
      } else if (cap.isTrueHero()) {
         stack.sendFailure(Component.translatable("tensura.evolve.hero.already"));
      } else if (!RaceUtils.isFightingBossForHero(player)) {
         stack.sendFailure(Component.translatable("tensura.evolve.hero.boss_requirement"));
      } else {
         cap.setTrueHero(true);
         RaceHelper.awakening(player, true);
      }
   }
}
