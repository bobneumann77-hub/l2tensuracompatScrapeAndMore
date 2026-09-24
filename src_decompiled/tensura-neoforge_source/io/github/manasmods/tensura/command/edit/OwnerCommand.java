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
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("owner")
@Permission(value = "tensura.command.edit_owner", permissionLevel = PermissionLevel.GAMEMASTER)
public class OwnerCommand {
   @Execute
   public boolean setName(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("name") String dl,
      @LiteralArg("set") String set,
      @TextArg(name = "name") String name
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setName(name);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.owner.name.set", new Object[]{entity.getName(), name}));
         }
      }

      return true;
   }

   @Execute
   public boolean removeName(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("name") String dl,
      @LiteralArg("remove") String remove
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setName(null);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.owner.name.remove", new Object[]{entity.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setPermanentOwner(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("permanent") String dl,
      @LiteralArg("set") String set,
      @EntityArg(value = Type.ENTITY, name = "owner") EntitySelector ownerSelector
   ) throws CommandSyntaxException {
      if (ownerSelector.findSingleEntity(stack) instanceof LivingEntity owner) {
         for (Entity target : selector.findEntities(stack)) {
            if (target instanceof LivingEntity entity) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setPermanentOwner(owner.getUUID());
               existence.markDirty();
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.owner.permanent.set", new Object[]{entity.getName(), owner.getName()})
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   public boolean clearPermanentOwner(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("permanent") String dl,
      @LiteralArg("remove") String set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setPermanentOwner(null);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.owner.permanent.remove", new Object[]{entity.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setTemporaryOwner(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("temporary") String dl,
      @LiteralArg("set") String set,
      @EntityArg(value = Type.ENTITY, name = "owner") EntitySelector ownerSelector
   ) throws CommandSyntaxException {
      if (ownerSelector.findSingleEntity(stack) instanceof LivingEntity owner) {
         for (Entity target : selector.findEntities(stack)) {
            if (target instanceof LivingEntity entity) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setTemporaryOwner(owner.getUUID());
               existence.markDirty();
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.owner.temporary.set", new Object[]{entity.getName(), owner.getName()})
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   public boolean clearTemporaryOwner(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("temporary") String dl,
      @LiteralArg("remove") String set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setTemporaryOwner(null);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.owner.temporary.remove", new Object[]{entity.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setNeutral(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("neutral") String dl,
      @LiteralArg("add") String set,
      @EntityArg(value = Type.ENTITY, name = "owner") EntitySelector ownerSelector
   ) throws CommandSyntaxException {
      if (ownerSelector.findSingleEntity(stack) instanceof LivingEntity owner) {
         for (Entity target : selector.findEntities(stack)) {
            if (target instanceof LivingEntity entity) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.addNeutralTarget(owner.getUUID());
               existence.markDirty();
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.owner.neutral.add", new Object[]{entity.getName(), owner.getName()})
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   public boolean removeNeutral(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("neutral") String dl,
      @LiteralArg("remove") String set,
      @EntityArg(value = Type.ENTITY, name = "owner") EntitySelector ownerSelector
   ) throws CommandSyntaxException {
      if (ownerSelector.findSingleEntity(stack) instanceof LivingEntity owner) {
         for (Entity target : selector.findEntities(stack)) {
            if (target instanceof LivingEntity entity) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.removeNeutralTarget(owner.getUUID());
               existence.markDirty();
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.owner.neutral.remove", new Object[]{entity.getName(), owner.getName()})
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   public boolean clearNeutral(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("neutral") String dl, @LiteralArg("clear") String set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.clearNeutralTargets();
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.owner.neutral.clear", new Object[]{entity.getName()}));
         }
      }

      return true;
   }
}
