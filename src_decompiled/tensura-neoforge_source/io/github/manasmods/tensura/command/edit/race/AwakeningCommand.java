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
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("awakening")
@Permission(value = "tensura.command.edit_awakening", permissionLevel = PermissionLevel.GAMEMASTER)
public class AwakeningCommand {
   @Execute
   public boolean setHumanKill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("humanKill") String dl,
      @LiteralArg("set") String set,
      @IntegerArg("amount") int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setHumanKill(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.human_kill", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean addHumanKill(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("humanKill") String dl,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setHumanKill(existence.getHumanKill() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.human_kill", new Object[]{entity.getName(), existence.getHumanKill()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setSoul(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("soul") String soul,
      @LiteralArg("set") String set,
      @IntegerArg("amount") int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSoulPoints(amount * 1000);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.demon_lord.soul", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean addSoul(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("soul") String soul,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSoulPoints(existence.getSoulPoints() + amount * 1000);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.demon_lord.soul", new Object[]{entity.getName(), existence.getSoulPoints() / 1000})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setHFTick(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("harvestFestivalTick") String tick,
      @LiteralArg("set") String set,
      @IntegerArg("amount") int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setHarvestTick(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.demon_lord.harvest_tick", new Object[]{entity.getName(), amount})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean addHFTick(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("harvestFestivalTick") String tick,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setHarvestTick(existence.getSoulPoints() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.demon_lord.harvest_tick", new Object[]{entity.getName(), existence.getHarvestTick()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setSeed(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("seed") String soul,
      @BooleanArg("set") boolean set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setDemonLordSeed(set);
            existence.markDirty();
            Component component = Component.translatable(set ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.demon_lord.seed", new Object[]{entity.getName(), component}));
         }
      }

      return true;
   }

   @Execute
   public boolean setDemonLord(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("demonLord") String dl,
      @LiteralArg("awakened") String soul,
      @BooleanArg("set") boolean set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setTrueDemonLord(set);
            existence.markDirty();
            Component component = Component.translatable(set ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.demon_lord.awakened", new Object[]{entity.getName(), component}));
         }
      }

      return true;
   }

   @Execute
   public boolean setBlessed(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("hero") String dl,
      @LiteralArg("blessed") String soul,
      @BooleanArg("set") boolean set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setBlessed(set);
            existence.markDirty();
            Component component = Component.translatable(set ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spirit.blessed", new Object[]{entity.getName(), component}));
         }
      }

      return true;
   }

   @Execute
   public boolean setEgg(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("hero") String dl,
      @LiteralArg("egg") String soul,
      @BooleanArg("set") boolean set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setHeroEgg(set);
            existence.markDirty();
            Component component = Component.translatable(set ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.hero.egg", new Object[]{entity.getName(), component}));
         }
      }

      return true;
   }

   @Execute
   public boolean setHero(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("hero") String dl,
      @LiteralArg("awakened") String soul,
      @BooleanArg("set") boolean set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setTrueHero(set);
            existence.markDirty();
            Component component = Component.translatable(set ? "tensura.message.enabled" : "tensura.message.disabled");
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.hero.awakened", new Object[]{entity.getName(), component}));
         }
      }

      return true;
   }
}
