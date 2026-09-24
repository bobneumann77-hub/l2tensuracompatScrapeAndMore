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
import java.util.Collection;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Command("owner")
public class OwnerGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_owner_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getName(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("name") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getName() == null) {
            stack.sendFailure(Component.translatable("tensura.command.owner.name.get.no_name", new Object[]{entity.getName()}));
         } else {
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.command.owner.name.get", new Object[]{entity.getName(), existence.getName()}), ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getName(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("name") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      if (existence.getName() == null) {
         stack.sendFailure(Component.translatable("tensura.command.owner.name.get.no_name", new Object[]{player.getName()}));
      } else {
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.owner.name.get", new Object[]{player.getName(), existence.getName()}), ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getPermanentOwner(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("permanent") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getPermanentOwner() == null) {
            stack.sendFailure(Component.translatable("tensura.command.owner.permanent.get.no_owner", new Object[]{entity.getName()}));
         } else {
            Entity owner = stack.getLevel().getEntity(existence.getPermanentOwner());
            Component component = (Component)(owner != null ? owner.getName() : Component.literal(existence.getPermanentOwner().toString()));
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.command.owner.permanent.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getPermanentOwner(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("name") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      if (existence.getPermanentOwner() == null) {
         stack.sendFailure(Component.translatable("tensura.command.owner.permanent.get.no_owner", new Object[]{player.getName()}));
      } else {
         Entity owner = stack.getLevel().getEntity(existence.getPermanentOwner());
         Component component = (Component)(owner != null ? owner.getName() : Component.literal(existence.getPermanentOwner().toString()));
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.owner.permanent.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getTemporaryOwner(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("temporary") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getTemporaryOwner() == null) {
            stack.sendFailure(Component.translatable("tensura.command.owner.temporary.get.no_owner", new Object[]{entity.getName()}));
         } else {
            Entity owner = stack.getLevel().getEntity(existence.getTemporaryOwner());
            Component component = (Component)(owner != null ? owner.getName() : Component.literal(existence.getTemporaryOwner().toString()));
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.command.owner.temporary.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getTemporaryOwner(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("name") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      if (existence.getTemporaryOwner() == null) {
         stack.sendFailure(Component.translatable("tensura.command.owner.temporary.get.no_owner", new Object[]{player.getName()}));
      } else {
         Entity owner = stack.getLevel().getEntity(existence.getTemporaryOwner());
         Component component = (Component)(owner != null ? owner.getName() : Component.literal(existence.getTemporaryOwner().toString()));
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.owner.temporary.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getNeutral(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("neutral") String dl) throws CommandSyntaxException {
      if (!(selector.findSingleEntity(stack) instanceof LivingEntity entity)) {
         return false;
      } else {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         Collection<UUID> list = existence.getTargetNeutralList();
         if (list.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.command.owner.neutral.get.empty", new Object[]{entity.getName()}));
         } else {
            MutableComponent component = null;

            for (UUID uuid : list) {
               Entity target = stack.getLevel().getEntity(uuid);
               MutableComponent name = target != null ? target.getName().copy() : Component.literal(uuid.toString());
               if (component == null) {
                  component = name;
               } else {
                  component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
               }
            }

            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.command.owner.neutral.get", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
            );
         }

         return true;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_owner_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getNeutral(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("neutral") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      Collection<UUID> list = existence.getTargetNeutralList();
      if (list.isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.command.owner.neutral.get.empty", new Object[]{player.getName()}));
      } else {
         MutableComponent component = null;

         for (UUID uuid : list) {
            Entity target = stack.getLevel().getEntity(uuid);
            MutableComponent name = target != null ? target.getName().copy() : Component.literal(uuid.toString());
            if (component == null) {
               component = name;
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }

         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.owner.neutral.get", new Object[]{player.getName(), component}), ChatFormatting.AQUA
         );
      }

      return true;
   }
}
