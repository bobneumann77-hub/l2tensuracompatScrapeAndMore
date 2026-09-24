package io.github.manasmods.tensura.command.world;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.DimensionArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.BlockPosArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg.Type;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.BossFightArg;
import io.github.manasmods.tensura.command.argument.BossFightPlayerHandlerArg;
import io.github.manasmods.tensura.command.argument.EntityTypeArg;
import io.github.manasmods.tensura.command.argument.GameModeArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.handler.LabyrinthHandler;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.BossFightStorage;
import io.github.manasmods.tensura.storage.boss.exit.SpawnPointForceExit;
import io.github.manasmods.tensura.storage.boss.exit.WarpPointForceExit;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;

@Command("bossFight")
public class BossFightCommand {
   @Execute
   public boolean resetBuiltinBoss(
      @SenderArg CommandSourceStack stack, @LiteralArg("builtinReset") String builtin, @LiteralArg("boss") String boss, @LiteralArg("GazelDwargo") String name
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      bossFightHolder.addBossFight("GazelDwargoArena", BossFightStorage.createGazelBossFight());
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.boss_fight.builtin.reset", new Object[]{((EntityType)HumanEntityTypes.GAZEL_DWARGO.get()).getDescription()}),
         ChatFormatting.DARK_GREEN
      );
      return true;
   }

   @Execute
   public boolean resetBuiltinArena(
      @SenderArg CommandSourceStack stack, @LiteralArg("builtinReset") String builtin, @LiteralArg("arena") String boss, @LiteralArg("GazelDwargo") String name
   ) {
      ServerLevel level = stack.getServer().getLevel(TensuraDimensions.BOSS_AREA);
      if (level == null) {
         return false;
      }

      LabyrinthHandler.placeGazelArena(level);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.boss_fight.builtin.reset_arena", new Object[]{((EntityType)HumanEntityTypes.GAZEL_DWARGO.get()).getDescription()}),
         ChatFormatting.DARK_GREEN
      );
      return true;
   }

   @Execute
   public boolean create(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @BlockPosArg BlockPos pos,
      @DimensionArg ServerLevel dimension,
      @DoubleArg(value = "radius", min = 1.0) double radius
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = new BossFightInstance(pos, dimension.dimension(), radius);
         bossFightHolder.addBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.boss_fight.created", new Object[]{name, center, dimension.dimension().location().toString(), radius}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean create(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @BlockPosArg BlockPos pos,
      @DimensionArg ServerLevel dimension,
      @DoubleArg("radius") double radius,
      @IntegerArg(value = "maxPlayer", min = 1) int maxPlayer
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = new BossFightInstance(pos, dimension.dimension(), radius);
         instance.setMaxPlayer(maxPlayer);
         bossFightHolder.addBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable(
               "tensura.boss_fight.created.max_player", new Object[]{name, center, dimension.dimension().location().toString(), radius, maxPlayer}
            ),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean create(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @BlockPosArg BlockPos pos,
      @DimensionArg ServerLevel dimension,
      @DoubleArg("radius") double radius,
      @IntegerArg(value = "maxPlayer", min = 1) int maxPlayer,
      @IntegerArg(value = "timer", min = 0) int timer
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = new BossFightInstance(pos, dimension.dimension(), radius);
         instance.setMaxPlayer(maxPlayer);
         instance.setTimer(timer);
         bossFightHolder.addBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable(
               "tensura.boss_fight.created.timer", new Object[]{name, center, dimension.dimension().location().toString(), radius, maxPlayer, timer}
            ),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean remove(@SenderArg CommandSourceStack stack, @LiteralArg("remove") String remove, @BossFightArg String name) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else if (!bossFightHolder.getBossFight(name).isBuiltin()) {
         bossFightHolder.removeBossFight(name);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.removed", new Object[]{name}), ChatFormatting.DARK_GREEN);
      } else {
         stack.sendFailure(Component.translatable("tensura.boss_fight.builtin.remove").withStyle(ChatFormatting.RED));
      }

      return true;
   }

   @Execute
   public boolean list(@SenderArg CommandSourceStack stack, @LiteralArg("list") String get) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (bossFightHolder.getBossFights().isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.list.empty").withStyle(ChatFormatting.RED));
      } else {
         MutableComponent component = null;

         for (String name : bossFightHolder.getBossFights().keySet()) {
            if (component == null) {
               component = Component.literal(name);
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }

         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.list", new Object[]{component}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean join(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("join") String join,
      @EntityArg(io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.PLAYER) EntitySelector selector,
      @TextArg(value = Type.STRING, name = "name") String name
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
         return true;
      }

      ServerPlayer player = selector.findSinglePlayer(stack);
      BossFightInstance instance = bossFightHolder.getBossFights().get(name);
      if (!instance.joinBossFight(player, stack.getServer().getLevel(instance.getDimension()), name, false)) {
         if (player != stack.getPlayer()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.full").withStyle(ChatFormatting.RED));
         }
      } else {
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.joined", new Object[]{player.getName(), name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean leave(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("leave") String leave,
      @EntityArg(io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.PLAYER) EntitySelector selector,
      @TextArg(value = Type.STRING, name = "name") String name
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
         return true;
      } else {
         ServerPlayer player = selector.findSinglePlayer(stack);
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.leaveBossFight(player, stack.getServer().getLevel(instance.getDimension()));
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.left", new Object[]{player.getName(), name}), ChatFormatting.DARK_GREEN);
         return true;
      }
   }

   @Execute
   public boolean get(@SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @BossFightArg String name) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.boss_fight.get", new Object[]{name})
               .withStyle(ChatFormatting.DARK_GREEN)
               .append("\n")
               .append(instance.getDataMessage())
         );
      }

      return true;
   }

   @Execute
   public boolean reload(@SenderArg CommandSourceStack stack, @LiteralArg("reload") String reload) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      bossFightHolder.reloadFromJson();
      TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.reloaded").withStyle(ChatFormatting.DARK_GREEN));
      return true;
   }

   @Execute
   public boolean editCenter(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("center") String s,
      @BlockPosArg("position") BlockPos pos
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setCenter(pos);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.center", new Object[]{name, center}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editRadius(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("radius") String s,
      @DoubleArg(value = "radius", min = 1.0) double radius
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setRadius(radius);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.radius", new Object[]{name, radius}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editEntrance(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("entrance") String b,
      @BlockPosArg("entrancePos") BlockPos pos
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setEntrance(pos);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.entrance", new Object[]{name, center}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editEntrance(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("entrance") String b,
      @LiteralArg("remove") String c
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setEntrance(null);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.entrance.removed"), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editBossPosition(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("boss") String b,
      @LiteralArg("spawnPos") String p,
      @BlockPosArg("spawnPos") BlockPos pos
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBossPosition(pos);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.boss.spawn_position", new Object[]{name, center}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editBossPosition(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("boss") String b,
      @LiteralArg("spawnPos") String p,
      @LiteralArg("remove") String c
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBossPosition(null);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.boss.spawn_position.removed"), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editBossType(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("boss") String b,
      @LiteralArg("type") String t,
      @EntityTypeArg Holder<EntityType<?>> type
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBossType(((EntityType)type.value()).arch$registryName());
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.boss_fight.edit.boss", new Object[]{name, ((EntityType)type.value()).getDescription()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editBossType(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("boss") String b,
      @LiteralArg("type") String t,
      @LiteralArg("remove") String remove
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBossType(ResourceLocation.withDefaultNamespace("none"));
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.boss.removed", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editNextBoss(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("nextBoss") String b,
      @LiteralArg("set") String t,
      @TextArg(value = Type.STRING, name = "name") String nextBoss
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setNextBossFight(nextBoss);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.next_boss", new Object[]{name, nextBoss}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editNextBoss(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("nextBoss") String b,
      @LiteralArg("remove") String t
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setNextBossFight(null);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.next_boss.removed", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editTimer(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("timer") String s,
      @IntegerArg(value = "tick", min = -1) int timer
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setTimer(timer);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.timer", new Object[]{name, timer}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editTimerRemove(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("timer") String s,
      @LiteralArg("remove") String r
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setTimer(0);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.timer.remove", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editStartDelay(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startDelay") String s,
      @IntegerArg(value = "tick", min = 0) int timer
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setStartDelay(timer);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.start_delay", new Object[]{name, timer}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editStartDelayRemove(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startDelay") String s,
      @LiteralArg("remove") String r
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setStartDelay(0);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.start_delay.remove", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editExitTimer(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forceExit") String s,
      @LiteralArg("timer") String t,
      @IntegerArg(value = "tick", min = 0) int timer
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForceExitTimer(timer);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.force_exit.timer", new Object[]{name, timer}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editExitTimerRemove(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forceExit") String s,
      @LiteralArg("timer") String t,
      @LiteralArg("remove") String r
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForceExitTimer(0);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.force_exit.timer.remove", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editExitHandler(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forceExit") String s,
      @LiteralArg("handler") String t,
      @LiteralArg("spawnPoint") String p
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForceExitHandler(new SpawnPointForceExit());
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.force_exit.handler.spawn_point", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editExitHandler(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forceExit") String s,
      @LiteralArg("handler") String t,
      @BlockPosArg BlockPos pos,
      @DimensionArg ServerLevel dimension
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForceExitHandler(new WarpPointForceExit(pos, dimension.dimension(), WarpPoint.TransmissionType.FORCE_EXIT));
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         String center = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable(
               "tensura.boss_fight.edit.force_exit.handler.position_set", new Object[]{name, center, dimension.dimension().location().toString()}
            ),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editExitOnLeave(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forceExit") String s,
      @LiteralArg("onLeave") String t,
      @BooleanArg("set") boolean ban
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForceExitOnLeave(ban);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         Component reset = Component.translatable(ban ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.force_exit.on_leave", new Object[]{name, reset}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editPlayer(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("maxPlayer") String s,
      @IntegerArg(value = "count", min = 1) int maxPlayer
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setMaxPlayer(maxPlayer);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.player", new Object[]{name, maxPlayer}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editPlayerHandler(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("playerCountHandler") String s,
      @BossFightPlayerHandlerArg BossFightInstance.PlayerCountHandler handler
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setPlayerCountHandler(handler);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.player_handler", new Object[]{name, handler.getSerializedName()}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editGameMode(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forcedGameMode") String g,
      @LiteralArg("set") String s,
      @GameModeArg GameType type
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForcedGameMode(type);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.game_mode", new Object[]{name, type.getShortDisplayName()}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editGameMode(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("forcedGameMode") String g,
      @LiteralArg("remove") String s
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setForcedGameMode(null);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.game_mode.remove", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editBanTeleport(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("banTeleportation") String s,
      @BooleanArg("ban") boolean ban
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBanTeleportation(ban);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         Component reset = Component.translatable(ban ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.ban_teleportation", new Object[]{name, reset}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editBarrier(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("hasBarrier") String s,
      @BooleanArg("barrier") boolean sealed
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setBarrierSealed(sealed);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         Component reset = Component.translatable(sealed ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.barrier_sealed", new Object[]{name, reset}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editResetBoss(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("resetBossOnEnd") String s,
      @BooleanArg("reset") boolean resetBoss
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.setResetBoss(resetBoss);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         Component reset = Component.translatable(resetBoss ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.reset_boss", new Object[]{name, reset}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editStartCommand(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startCommands") String a,
      @LiteralArg("add") String add,
      @TextArg(value = Type.GREEDY_STRING, name = "command") String command
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.addStartCommand(command);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.start_command.add", new Object[]{name, command}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editStartCommandLast(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("last") String last
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getStartCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.start_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeStartCommand(true);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.start_command.remove_last", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editStartCommandFirst(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("first") String first
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getStartCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.start_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeStartCommand(false);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.start_command.remove_first", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editStartCommandClear(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("startCommands") String a,
      @LiteralArg("clear") String clear
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.clearStartCommands();
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.start_command.clear", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editSuccessCommand(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("successCommands") String a,
      @LiteralArg("add") String add,
      @TextArg(value = Type.GREEDY_STRING, name = "command") String command
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.addSuccessCommand(command);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.success_command.add", new Object[]{name, command}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editSuccessCommandLast(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("successCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("last") String last
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getSuccessCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.success_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeSuccessCommand(true);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.success_command.remove_last", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editSuccessCommandFirst(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("successCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("first") String first
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getSuccessCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.success_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeSuccessCommand(false);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.success_command.remove_first", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editSuccessCommandClear(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("successCommands") String a,
      @LiteralArg("clear") String clear
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.clearSuccessCommands();
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.success_command.clear", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editFailCommand(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("failCommands") String a,
      @LiteralArg("add") String add,
      @TextArg(value = Type.GREEDY_STRING, name = "command") String command
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.addFailCommand(command);
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.boss_fight.edit.fail_command.add", new Object[]{name, command}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editFailCommandLast(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("failCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("last") String last
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getFailCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.fail_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeFailCommand(true);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.fail_command.remove_last", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editFailCommandFirst(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("failCommands") String a,
      @LiteralArg("remove") String add,
      @LiteralArg("first") String first
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getFailCommands().isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.edit.fail_command.empty", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            instance.removeFailCommand(false);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.boss_fight.edit.fail_command.remove_first", new Object[]{name}), ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editFailCommandClear(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("failCommands") String a,
      @LiteralArg("clear") String clear
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.clearFailCommands();
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.fail_command.clear", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean editBannedAbilities(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("ban") String ban,
      @SkillArg Holder<ManasSkill> skill
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (instance.getBannedAbilities().contains(ability)) {
            stack.sendFailure(
               Component.translatable("tensura.boss_fight.edit.ability.banned", new Object[]{name, ability.getChatDisplayName(true)})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.addBannedAbilities(ability);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.boss_fight.edit.ability.ban", new Object[]{name, ability.getChatDisplayName(true)}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editUnbannedAbilities(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("unban") String unban,
      @SkillArg Holder<ManasSkill> skill
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         if (!instance.getBannedAbilities().contains(ability)) {
            stack.sendFailure(
               Component.translatable("tensura.boss_fight.edit.ability.not_banned", new Object[]{name, ability.getChatDisplayName(true)})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.removeBannedAbilities(ability);
            BossFightStorage.getConfigManager().saveBossFight(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.boss_fight.edit.ability.unban", new Object[]{name, ability.getChatDisplayName(true)}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean editBannedAbilities(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @BossFightArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("clear") String unban
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
      if (!bossFightHolder.getBossFights().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         BossFightInstance instance = bossFightHolder.getBossFights().get(name);
         instance.clearBannedAbilities();
         BossFightStorage.getConfigManager().saveBossFight(name, instance);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.boss_fight.edit.ability.clear", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }
}
