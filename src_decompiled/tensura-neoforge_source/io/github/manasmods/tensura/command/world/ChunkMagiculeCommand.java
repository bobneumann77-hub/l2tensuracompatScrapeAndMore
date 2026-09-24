package io.github.manasmods.tensura.command.world;

import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.BlockPosArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.chunk.IChunk;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

@Command("areaMagicule")
public class ChunkMagiculeCommand {
   @Execute
   public boolean getMagicule(@SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @LiteralArg("current") String add) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.get", new Object[]{AreaMagiculeHelper.getMagicule(player)})
            .withStyle(ChatFormatting.DARK_GREEN),
         true
      );
      return true;
   }

   @Execute
   public boolean getMagicule(
      @SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @LiteralArg("current") String add, @BooleanArg("countBlockModifiers") boolean block
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.get", new Object[]{AreaMagiculeHelper.getMagicule(player, block)})
            .withStyle(ChatFormatting.DARK_GREEN),
         true
      );
      return true;
   }

   @Execute
   public boolean getMaxMagicule(@SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @LiteralArg("max") String add) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.get.max", new Object[]{AreaMagiculeHelper.getMaxMagicule(player)})
            .withStyle(ChatFormatting.DARK_GREEN),
         true
      );
      return true;
   }

   @Execute
   public boolean getMaxMagicule(
      @SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @LiteralArg("max") String add, @BooleanArg("countBlockModifiers") boolean block
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.get.max", new Object[]{AreaMagiculeHelper.getMaxMagicule(player, block)})
            .withStyle(ChatFormatting.DARK_GREEN),
         true
      );
      return true;
   }

   @Execute
   public boolean getRegeneration(@SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @LiteralArg("regeneration") String add) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.get.regeneration", new Object[]{AreaMagiculeHelper.getMagiculeRegenerationRate(player)})
            .withStyle(ChatFormatting.DARK_GREEN),
         true
      );
      return true;
   }

   @Execute
   public boolean setMagicule(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("set") String set,
      @LiteralArg("current") String add,
      @DoubleArg(value = "magicule", min = 0.0) double amount
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition()));
      chunk.setMagicule(amount);
      chunk.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.area_magicule.set", new Object[]{amount}).withStyle(ChatFormatting.DARK_GREEN), true);
      return true;
   }

   @Execute
   public boolean setMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("set") String set,
      @LiteralArg("max") String add,
      @DoubleArg(value = "magicule", min = 0.0) double amount
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition()));
      chunk.setMaxMagicule(chunk.getBaseMaxMagicule() - chunk.getMaxMagicule() + amount);
      chunk.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.area_magicule.set.max", new Object[]{amount}).withStyle(ChatFormatting.DARK_GREEN), true);
      return true;
   }

   @Execute
   public boolean setRegeneration(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("set") String set,
      @LiteralArg("regeneration") String add,
      @DoubleArg(value = "magicule", min = 0.0) double amount
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition()));
      chunk.setRegenerationRate(amount);
      chunk.markDirty();
      stack.sendSuccess(
         () -> Component.translatable("tensura.command.area_magicule.set.regeneration", new Object[]{amount}).withStyle(ChatFormatting.DARK_GREEN), true
      );
      return true;
   }

   @Execute
   public boolean clearBlockModifiers(@SenderArg CommandSourceStack stack, @LiteralArg("clearBlockModifiers") String clear) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition()));
      chunk.clearBlockModifiers();
      chunk.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.area_magicule.clear_block_modifiers").withStyle(ChatFormatting.DARK_GREEN), true);
      return true;
   }

   @Execute
   public boolean clearBlockModifiers(@SenderArg CommandSourceStack stack, @LiteralArg("clearBlockModifiers") String clear, @IntegerArg("radius") int radius) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      int amount = 0;

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition().offset(x, 0, z)));
            chunk.clearBlockModifiers();
            chunk.markDirty();
            amount++;
         }
      }

      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.area_magicule.clear_block_modifiers.radius", new Object[]{amount}), ChatFormatting.DARK_GREEN
      );
      return true;
   }

   @Execute
   public boolean reset(@SenderArg CommandSourceStack stack, @LiteralArg("reset") String set) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(player.blockPosition()));
      chunk.reinitialize();
      stack.sendSuccess(() -> Component.translatable("tensura.command.area_magicule.reinitialized", new Object[]{1}).withStyle(ChatFormatting.DARK_GREEN), true);
      return true;
   }

   @Execute
   public boolean resetCenter(@SenderArg CommandSourceStack stack, @LiteralArg("reset") String set, @BlockPosArg("center") BlockPos pos) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(pos));
      chunk.reinitialize();
      stack.sendSuccess(() -> Component.translatable("tensura.command.area_magicule.reinitialized", new Object[]{1}).withStyle(ChatFormatting.DARK_GREEN), true);
      return true;
   }

   @Execute
   public boolean resetRadius(
      @SenderArg CommandSourceStack stack, @LiteralArg("reset") String set, @BlockPosArg("center") BlockPos pos, @IntegerArg("radius") int radius
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      int amount = 0;

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            IChunk chunk = TensuraStorages.getChunkFrom(player.level().getChunkAt(pos.offset(x, 0, z)));
            chunk.reinitialize();
            amount++;
         }
      }

      TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.area_magicule.reinitialized", new Object[]{amount}), ChatFormatting.DARK_GREEN);
      return true;
   }
}
