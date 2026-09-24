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
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("forceevo")
@Permission(value = "tensura.command.edit_forceevo", permissionLevel = PermissionLevel.GAMEMASTER)
public class ForceEvoCommand {
   @Execute
   public boolean forceRace(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("race") String dl) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            if (RaceHelper.evolveRace(entity)) {
               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.force_evo.race", new Object[]{entity.getName()}));
            } else {
               stack.sendFailure(Component.translatable("tensura.command.force_evo.race.fail", new Object[]{entity.getName()}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean forceRace(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("race") String dl,
      @BooleanArg("triggerRewards") boolean rewards
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            if (RaceHelper.evolveRace(entity, rewards)) {
               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.force_evo.race", new Object[]{entity.getName()}));
            } else {
               stack.sendFailure(Component.translatable("tensura.command.force_evo.race.fail", new Object[]{entity.getName()}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean forceDemonLord(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("demonLord") String dl) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setTrueDemonLord(true);
            existence.markDirty();
            RaceHelper.awakening(entity, false);
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.force_evo.demon_lord", new Object[]{entity.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean forceHero(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("hero") String dl) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setTrueHero(true);
            existence.markDirty();
            RaceHelper.awakening(entity, true);
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.force_evo.hero", new Object[]{entity.getName()}));
         }
      }

      return true;
   }
}
