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
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Command("awakening")
public class AwakeningGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSoul(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("demonLord") String dl, @LiteralArg("soul") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.demon_lord.soul.get", new Object[]{entity.getName(), existence.getSoulPoints() / 1000}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSoul(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("demonLord") String dl, @LiteralArg("soul") String soul
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.demon_lord.soul.get", new Object[]{player.getName(), existence.getSoulPoints() / 1000}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getHFTick(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("harvestFestivalTick") String tick
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.demon_lord.harvest_tick.get", new Object[]{entity.getName(), existence.getHarvestTick()}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getHFTick(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("demonLord") String dl, @LiteralArg("harvestFestivalTick") String tick
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.demon_lord.harvest_tick.get", new Object[]{player.getName(), existence.getHarvestTick()}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSeed(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("demonLord") String dl, @LiteralArg("seed") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Component component = Component.translatable(
            TensuraStorages.getExistenceFrom(entity).isDemonLordSeed() ? "tensura.message.enabled" : "tensura.message.disabled"
         );
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.demon_lord.seed.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSeed(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("demonLord") String dl, @LiteralArg("seed") String soul
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Component component = Component.translatable(
         TensuraStorages.getExistenceFrom(player).isDemonLordSeed() ? "tensura.message.enabled" : "tensura.message.disabled"
      );
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.demon_lord.seed.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getDemonLord(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("awakened") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Component component = Component.translatable(
            TensuraStorages.getExistenceFrom(entity).isTrueDemonLord() ? "tensura.message.enabled" : "tensura.message.disabled"
         );
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.demon_lord.awakened.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getDemonLord(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("demonLord") String dl, @LiteralArg("awakened") String soul
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Component component = Component.translatable(
         TensuraStorages.getExistenceFrom(player).isTrueDemonLord() ? "tensura.message.enabled" : "tensura.message.disabled"
      );
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.demon_lord.awakened.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getBlessed(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("hero") String dl, @LiteralArg("blessed") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Component component = Component.translatable(
            TensuraStorages.getExistenceFrom(entity).isBlessed() ? "tensura.message.enabled" : "tensura.message.disabled"
         );
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.spirit.blessed.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getBlessed(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("hero") String dl, @LiteralArg("blessed") String soul
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Component component = Component.translatable(
         TensuraStorages.getExistenceFrom(player).isBlessed() ? "tensura.message.enabled" : "tensura.message.disabled"
      );
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.spirit.blessed.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getEgg(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("hero") String dl, @LiteralArg("egg") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Component component = Component.translatable(
            TensuraStorages.getExistenceFrom(entity).isHeroEgg() ? "tensura.message.enabled" : "tensura.message.disabled"
         );
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.hero.egg.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getEgg(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("hero") String dl, @LiteralArg("egg") String soul) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Component component = Component.translatable(
         TensuraStorages.getExistenceFrom(player).isHeroEgg() ? "tensura.message.enabled" : "tensura.message.disabled"
      );
      TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.hero.egg.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA);
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getHero(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("hero") String dl, @LiteralArg("awakened") String soul
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         Component component = Component.translatable(
            TensuraStorages.getExistenceFrom(entity).isTrueHero() ? "tensura.message.enabled" : "tensura.message.disabled"
         );
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.hero.awakened.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getHero(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("hero") String dl, @LiteralArg("awakened") String soul
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      Component component = Component.translatable(
         TensuraStorages.getExistenceFrom(player).isTrueHero() ? "tensura.message.enabled" : "tensura.message.disabled"
      );
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.hero.awakened.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getHumanKill(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("humanKill") String soul) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.human_kill.get", new Object[]{entity.getName(), existence.getHumanKill()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_awakening_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getHumanKill(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("humanKill") String soul) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.human_kill.get", new Object[]{player.getName(), existence.getHumanKill()}), ChatFormatting.AQUA
      );
      return true;
   }
}
