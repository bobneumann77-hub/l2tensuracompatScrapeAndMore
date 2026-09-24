package io.github.manasmods.tensura.command.world;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.DimensionArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.BlockPosArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.block.WarpPadBlock;
import io.github.manasmods.tensura.block.entity.WarpPadBlockEntity;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.BossFightArg;
import io.github.manasmods.tensura.command.argument.TransmissionTypeArg;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.exit.OffsetPointForceExit;
import io.github.manasmods.tensura.storage.boss.exit.RandomRangeForceExit;
import io.github.manasmods.tensura.storage.boss.exit.SpawnPointForceExit;
import io.github.manasmods.tensura.storage.boss.exit.WarpPointForceExit;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

@Command("warpPad")
public class WarpPadCommand {
   @Execute
   public boolean boss(
      @SenderArg CommandSourceStack stack, @BlockPosArg BlockPos pos, @LiteralArg("bossFight") String b, @LiteralArg("set") String s, @BossFightArg String name
   ) throws CommandSyntaxException {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }

      if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         ServerLevel overworld = stack.getServer().overworld();
         IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
         if (!bossFightHolder.getBossFights().containsKey(name)) {
            stack.sendFailure(Component.translatable("tensura.boss_fight.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
         } else {
            padEntity.setBossFightId(name);
            TensuraCommands.sendSuccess(stack, Component.translatable("tensura.warp_pad.set.boss_fight", new Object[]{name}), ChatFormatting.DARK_GREEN);
         }

         return true;
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean boss(@SenderArg CommandSourceStack stack, @BlockPosArg BlockPos pos, @LiteralArg("bossFight") String b, @LiteralArg("remove") String s) throws CommandSyntaxException {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }

      if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         if (padEntity.getBossFightId() == null) {
            stack.sendFailure(Component.translatable("tensura.warp_pad.no_boss").withStyle(ChatFormatting.RED));
            return true;
         } else {
            padEntity.setBossFightId(null);
            TensuraCommands.sendSuccess(stack, Component.translatable("tensura.warp_pad.clear.boss_fight"), ChatFormatting.DARK_GREEN);
            return true;
         }
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean getBoss(@SenderArg CommandSourceStack stack, @BlockPosArg BlockPos pos, @LiteralArg("bossFight") String b, @LiteralArg("get") String s) {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }

      if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         if (padEntity.getBossFightId() == null) {
            stack.sendFailure(Component.translatable("tensura.warp_pad.no_boss").withStyle(ChatFormatting.RED));
            return true;
         } else {
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.warp_pad.get.boss_fight", new Object[]{padEntity.getBossFightId()}), ChatFormatting.DARK_GREEN
            );
            return true;
         }
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean preset(
      @SenderArg CommandSourceStack stack,
      @BlockPosArg BlockPos pos,
      @LiteralArg("preset") String pr,
      @LiteralArg("set") String set,
      @LiteralArg("position") String p,
      @BlockPosArg("destination") BlockPos destination,
      @DimensionArg ServerLevel dimension,
      @TransmissionTypeArg WarpPoint.TransmissionType type,
      @IntegerArg(value = "timer", min = 0) int timer,
      @DoubleArg(value = "costPerBlock", min = 0.0) double cost
   ) throws CommandSyntaxException {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      } else if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         padEntity.setWarpTime(timer);
         padEntity.setMagiculeCost(cost);
         padEntity.setForceExitHandler(new WarpPointForceExit(destination, dimension.dimension(), type));
         String center = "[" + destination.getX() + ", " + destination.getY() + ", " + destination.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.warp_pad.set.preset", new Object[]{center, dimension.dimension().location().toString(), timer, cost}),
            ChatFormatting.DARK_GREEN
         );
         return true;
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean offset(
      @SenderArg CommandSourceStack stack,
      @BlockPosArg BlockPos pos,
      @LiteralArg("preset") String pr,
      @LiteralArg("set") String set,
      @LiteralArg("offset") String preset,
      @DoubleArg(value = "xOffset", min = -Double.MAX_VALUE) double xOffset,
      @DoubleArg(value = "yOffset", min = -Double.MAX_VALUE) double yOffset,
      @DoubleArg(value = "zOffset", min = -Double.MAX_VALUE) double zOffset,
      @TransmissionTypeArg WarpPoint.TransmissionType type,
      @IntegerArg(value = "timer", min = 0) int timer,
      @DoubleArg(value = "costPerBlock", min = 0.0) double cost
   ) throws CommandSyntaxException {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      } else if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         padEntity.setWarpTime(timer);
         padEntity.setMagiculeCost(cost);
         padEntity.setForceExitHandler(new OffsetPointForceExit(xOffset, yOffset, zOffset, type));
         String center = "[" + xOffset + ", " + yOffset + ", " + zOffset + "]";
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.warp_pad.set.offset", new Object[]{center, timer, cost}), ChatFormatting.DARK_GREEN);
         return true;
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean spawnPoint(
      @SenderArg CommandSourceStack stack,
      @BlockPosArg BlockPos pos,
      @LiteralArg("preset") String pr,
      @LiteralArg("set") String set,
      @LiteralArg("spawnPoint") String spawnPoint,
      @IntegerArg(value = "timer", min = 0) int timer,
      @DoubleArg(value = "costPerBlock", min = 0.0) double cost
   ) {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      } else if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         padEntity.setWarpTime(timer);
         padEntity.setMagiculeCost(cost);
         padEntity.setForceExitHandler(new SpawnPointForceExit());
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.warp_pad.set.spawn_point", new Object[]{timer, cost}), ChatFormatting.DARK_GREEN);
         return true;
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean randomRange(
      @SenderArg CommandSourceStack stack,
      @BlockPosArg BlockPos pos,
      @LiteralArg("preset") String pr,
      @LiteralArg("set") String set,
      @LiteralArg("random") String random,
      @DoubleArg(value = "xzRange", min = 0.0) double xzRange,
      @DoubleArg(value = "yRange", min = 0.0) double yRange,
      @BooleanArg("worldSurface") boolean worldSurface,
      @DimensionArg ServerLevel dimension,
      @TransmissionTypeArg WarpPoint.TransmissionType type,
      @IntegerArg(value = "timer", min = 0) int timer,
      @DoubleArg(value = "costPerBlock", min = 0.0) double cost
   ) {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      } else if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         padEntity.setWarpTime(timer);
         padEntity.setMagiculeCost(cost);
         padEntity.setForceExitHandler(new RandomRangeForceExit(xzRange, yRange, worldSurface, dimension.dimension(), type));
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.warp_pad.set.random", new Object[]{xzRange, yRange, dimension.dimension().location().toString(), timer, cost}),
            ChatFormatting.DARK_GREEN
         );
         return true;
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean clear(@SenderArg CommandSourceStack stack, @BlockPosArg BlockPos pos, @LiteralArg("preset") String pr, @LiteralArg("clear") String set) throws CommandSyntaxException {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (!(state.getBlock() instanceof WarpPadBlock)) {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }

      if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
         if (padEntity.getForceExitHandler() == null) {
            stack.sendFailure(Component.translatable("tensura.warp_pad.no_preset").withStyle(ChatFormatting.RED));
            return true;
         } else {
            padEntity.setForceExitHandler(null);
            TensuraCommands.sendSuccess(stack, Component.translatable("tensura.warp_pad.clear"), ChatFormatting.DARK_GREEN);
            return true;
         }
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }

   @Execute
   public boolean getPreset(@SenderArg CommandSourceStack stack, @BlockPosArg BlockPos pos, @LiteralArg("preset") String pr, @LiteralArg("get") String get) {
      BlockState state = stack.getLevel().getBlockState(pos);
      if (state.getBlock() instanceof WarpPadBlock block) {
         if (WarpPadBlock.findBlockEntity(stack.getLevel(), pos, state) instanceof WarpPadBlockEntity padEntity) {
            switch (padEntity.getForceExitHandler()) {
               case WarpPointForceExit exit: {
                  String center = "[" + exit.getWarpPoint().getX() + ", " + exit.getWarpPoint().getY() + ", " + exit.getWarpPoint().getZ() + "]";
                  TensuraCommands.sendSuccess(
                     stack,
                     Component.translatable(
                        "tensura.warp_pad.set.preset",
                        new Object[]{center, exit.getWarpPoint().getDimension().location().toString(), padEntity.getWarpTime(), padEntity.getMagiculeCost()}
                     ),
                     ChatFormatting.DARK_GREEN
                  );
                  break;
               }
               case OffsetPointForceExit exit: {
                  String center = "[" + exit.getXOffset() + ", " + exit.getYOffset() + ", " + exit.getZOffset() + "]";
                  TensuraCommands.sendSuccess(
                     stack,
                     Component.translatable("tensura.warp_pad.set.offset", new Object[]{center, padEntity.getWarpTime(), padEntity.getMagiculeCost()}),
                     ChatFormatting.DARK_GREEN
                  );
                  break;
               }
               case RandomRangeForceExit exit:
                  TensuraCommands.sendSuccess(
                     stack,
                     Component.translatable(
                        "tensura.warp_pad.set.random",
                        new Object[]{
                           exit.getXzRange(), exit.getYRange(), exit.getDimension().location().toString(), padEntity.getWarpTime(), padEntity.getMagiculeCost()
                        }
                     ),
                     ChatFormatting.DARK_GREEN
                  );
                  break;
               case SpawnPointForceExit ignored:
                  TensuraCommands.sendSuccess(
                     stack,
                     Component.translatable("tensura.warp_pad.set.spawn_point", new Object[]{padEntity.getWarpTime(), padEntity.getMagiculeCost()}),
                     ChatFormatting.DARK_GREEN
                  );
                  break;
               case null:
               default:
                  stack.sendFailure(Component.translatable("tensura.warp_pad.no_preset").withStyle(ChatFormatting.RED));
            }

            return true;
         } else {
            stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
            return true;
         }
      } else {
         stack.sendFailure(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED));
         return true;
      }
   }
}
