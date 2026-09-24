package dev.xkmc.l2hostility.content.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class LHPlayerCommands extends HostilityCommands {
   protected static LiteralArgumentBuilder<CommandSourceStack> build() {
      return (LiteralArgumentBuilder<CommandSourceStack>)literal("player")
         .then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)argument("player", EntityArgument.players()).then(difficulty())).then(trait())).then(dim()));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> difficulty() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)literal("difficulty")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("base")
                        .then(
                           ((LiteralArgumentBuilder)literal("set").requires(e -> e.hasPermission(2)))
                              .then(
                                 argument("level", IntegerArgumentType.integer(0))
                                    .executes(playerLevel((cap, pl, level) -> cap.getLevelEditor(pl).setBase(level)))
                              )
                        ))
                     .then(
                        ((LiteralArgumentBuilder)literal("add").requires(e -> e.hasPermission(2)))
                           .then(
                              argument("level", IntegerArgumentType.integer()).executes(playerLevel((cap, pl, level) -> cap.getLevelEditor(pl).addBase(level)))
                           )
                     ))
                  .then(
                     literal("get")
                        .executes(playerGet((cap, pl) -> LangData.COMMAND_PLAYER_GET_BASE.get(pl.getDisplayName(), cap.getLevelEditor(pl).getBase())))
                  )
            ))
         .then(
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("total")
                     .then(
                        ((LiteralArgumentBuilder)literal("set").requires(e -> e.hasPermission(2)))
                           .then(
                              argument("level", IntegerArgumentType.integer(0))
                                 .executes(playerLevel((cap, pl, level) -> cap.getLevelEditor(pl).setTotal(level)))
                           )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)literal("add").requires(e -> e.hasPermission(2)))
                        .then(
                           argument("level", IntegerArgumentType.integer()).executes(playerLevel((cap, pl, level) -> cap.getLevelEditor(pl).addTotal(level)))
                        )
                  ))
               .then(
                  literal("get")
                     .executes(playerGet((cap, pl) -> LangData.COMMAND_PLAYER_GET_TOTAL.get(pl.getDisplayName(), cap.getLevelEditor(pl).getTotal())))
               )
         );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> trait() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)literal("traitCap")
            .then(
               ((LiteralArgumentBuilder)literal("set").requires(e -> e.hasPermission(2)))
                  .then(argument("level", IntegerArgumentType.integer(0, TraitManager.getMaxLevel())).executes(playerLevel((cap, pl, level) -> {
                     cap.maxRankKilled = level;
                     return true;
                  })))
            ))
         .then(literal("get").executes(playerGet((cap, pl) -> LangData.COMMAND_PLAYER_GET_TRAIT_CAP.get(pl.getDisplayName(), cap.maxRankKilled))));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> dim() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)literal("dimensions")
            .then(((LiteralArgumentBuilder)literal("clear").requires(e -> e.hasPermission(2))).executes(playerRun((cap, pl) -> {
               boolean ans = !cap.dimensions.isEmpty();
               cap.dimensions.clear();
               return ans;
            }))))
         .then(literal("get").executes(playerGet((cap, pl) -> LangData.COMMAND_PLAYER_GET_DIM.get(pl.getDisplayName(), cap.dimensions.size()))));
   }

   private static Command<CommandSourceStack> playerRun(LHPlayerCommands.PlayerCommand cmd) {
      return ctx -> {
         EntitySelector sel = (EntitySelector)ctx.getArgument("player", EntitySelector.class);
         List<ServerPlayer> list = sel.findPlayers((CommandSourceStack)ctx.getSource());
         int count = iterate(list, cmd::run);
         printCompletion((CommandSourceStack)ctx.getSource(), count);
         return 0;
      };
   }

   private static Command<CommandSourceStack> playerGet(LHPlayerCommands.PlayerGet cmd) {
      return ctx -> {
         EntitySelector sel = (EntitySelector)ctx.getArgument("player", EntitySelector.class);

         for (ServerPlayer e : sel.findPlayers((CommandSourceStack)ctx.getSource())) {
            ((CommandSourceStack)ctx.getSource())
               .sendSystemMessage(cmd.run((PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(e), e));
         }

         return 0;
      };
   }

   private static Command<CommandSourceStack> playerLevel(LHPlayerCommands.PlayerLevelCommand cmd) {
      return ctx -> {
         int level = (Integer)ctx.getArgument("level", Integer.class);
         EntitySelector sel = (EntitySelector)ctx.getArgument("player", EntitySelector.class);
         List<ServerPlayer> list = sel.findPlayers((CommandSourceStack)ctx.getSource());
         int count = iterate(list, (cap, pl) -> cmd.run(cap, pl, level));
         printCompletion((CommandSourceStack)ctx.getSource(), count);
         return 0;
      };
   }

   private static int iterate(List<ServerPlayer> list, BiPredicate<PlayerDifficulty, Player> task) {
      int count = 0;

      for (ServerPlayer e : list) {
         PlayerDifficulty cap = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(e);
         if (task.test(cap, e)) {
            cap.sync(e);
            count++;
         }
      }

      return count;
   }

   private static void printCompletion(CommandSourceStack ctx, int count) {
      if (count > 0) {
         ctx.sendSystemMessage(LangData.COMMAND_PLAYER_SUCCEED.get(count));
      } else {
         ctx.sendSystemMessage(LangData.COMMAND_PLAYER_FAIL.get().withStyle(ChatFormatting.RED));
      }
   }

   private interface PlayerCommand {
      boolean run(PlayerDifficulty var1, Player var2);
   }

   private interface PlayerGet {
      Component run(PlayerDifficulty var1, Player var2);
   }

   private interface PlayerLevelCommand {
      boolean run(PlayerDifficulty var1, Player var2, int var3);
   }
}
