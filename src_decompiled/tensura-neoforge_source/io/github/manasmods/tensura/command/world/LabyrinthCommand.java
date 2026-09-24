package io.github.manasmods.tensura.command.world;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.Vec3Arg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.Vec3Arg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;

@Command("labyrinth")
public class LabyrinthCommand {
   @Execute
   public boolean regenerate(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("regenerate") String regenerate,
      @BooleanArg("regenerate") boolean regen,
      @BooleanArg("immediately") boolean immediately,
      @BooleanArg("respawnColossus") boolean respawnColossus
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setLoaded(!regen);
      labyrinth.markDirty();
      if (regen && immediately) {
         if (LabyrinthStorage.generateStructures(serverLevel, labyrinth, true)) {
            ElementalColossusEntity colossus = new ElementalColossusEntity(serverLevel, labyrinth.getColossusPos(), MobSpawnType.NATURAL);
            if (serverLevel.addFreshEntity(colossus) && colossus.isAlive()) {
               colossus.setSleeping(true);
               labyrinth.setColossusSpawned(true);
            }

            stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.regenerate.immediately"), true);
         } else {
            stack.sendFailure(Component.translatable("tensura.command.labyrinth.regenerate.immediately.failure"));
         }
      } else {
         if (respawnColossus) {
            labyrinth.setColossusSpawned(false);
         }

         if (regen) {
            stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.regenerate"), true);
         } else {
            stack.sendFailure(Component.translatable("tensura.command.labyrinth.regenerate.false"));
         }
      }

      return true;
   }

   @Execute
   public boolean regenerate(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("regenerate") String regenerate,
      @BooleanArg("regenerate") boolean regen,
      @BooleanArg("immediately") boolean immediately
   ) {
      return this.regenerate(stack, regenerate, regen, immediately, false);
   }

   @Execute
   public boolean regenerate(@SenderArg CommandSourceStack stack, @LiteralArg("regenerate") String regenerate, @BooleanArg("regenerate") boolean regen) {
      return this.regenerate(stack, regenerate, regen, false, false);
   }

   @Execute
   public boolean getEntrance(@SenderArg CommandSourceStack stack, @LiteralArg("entrance") String entrance, @LiteralArg("get") String get) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      Vec3 location = labyrinth.getEntrancePos();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.entrance.get", new Object[]{location.x, location.y, location.z}), false);
      return true;
   }

   @Execute
   public boolean setEntrance(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("entrance") String entrance,
      @LiteralArg("set") String get,
      @Vec3Arg(name = "location", value = Type.CENTER) Vec3 location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setEntrancePos(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.entrance.set", new Object[]{location.x, location.y, location.z}), true);
      return true;
   }

   @Execute
   public boolean getColossusPos(@SenderArg CommandSourceStack stack, @LiteralArg("colossusPosition") String entrance, @LiteralArg("get") String get) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      Vec3 location = labyrinth.getColossusPos();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.colossus_pos.get", new Object[]{location.x, location.y, location.z}), false);
      return true;
   }

   @Execute
   public boolean setColossusPos(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("colossusPosition") String entrance,
      @LiteralArg("set") String get,
      @Vec3Arg(name = "location", value = Type.CENTER) Vec3 location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setColossusPos(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.colossus_pos.set", new Object[]{location.x, location.y, location.z}), true);
      return true;
   }

   @Execute
   public boolean getAreaRadius(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("colossusPosition") String entrance,
      @LiteralArg("areaRadius") String height,
      @LiteralArg("get") String get
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.area_radius.get", new Object[]{labyrinth.getAreaRadius()}), false);
      return true;
   }

   @Execute
   public boolean setAreaRadius(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("colossusPosition") String entrance,
      @LiteralArg("areaRadius") String height,
      @LiteralArg("set") String get,
      @DoubleArg("radius") double location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setAreaRadius(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.area_radius.set", new Object[]{location}), true);
      return true;
   }

   @Execute
   public boolean getPassedEntrance(@SenderArg CommandSourceStack stack, @LiteralArg("passedEntrance") String entrance, @LiteralArg("get") String get) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      Vec3 location = labyrinth.getPassedEntrancePos();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.passed_entrance.get", new Object[]{location.x, location.y, location.z}), false);
      return true;
   }

   @Execute
   public boolean setPassedEntrance(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passedEntrance") String entrance,
      @LiteralArg("set") String get,
      @Vec3Arg(name = "location", value = Type.CENTER) Vec3 location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setPassedEntrancePos(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.passed_entrance.set", new Object[]{location.x, location.y, location.z}), true);
      return true;
   }

   @Execute
   public boolean getVoidSave(
      @SenderArg CommandSourceStack stack, @LiteralArg("voidSave") String entrance, @LiteralArg("location") String pos, @LiteralArg("get") String get
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      Vec3 location = labyrinth.getVoidSavePos();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_save.get", new Object[]{location.x, location.y, location.z}), false);
      return true;
   }

   @Execute
   public boolean setVoidSave(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("voidSave") String entrance,
      @LiteralArg("location") String pos,
      @LiteralArg("set") String get,
      @Vec3Arg(name = "location", value = Type.CENTER) Vec3 location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setVoidSavePos(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_save.set", new Object[]{location.x, location.y, location.z}), true);
      return true;
   }

   @Execute
   public boolean getVoidSavePassed(
      @SenderArg CommandSourceStack stack, @LiteralArg("voidSave") String entrance, @LiteralArg("passedLocation") String pos, @LiteralArg("get") String get
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      Vec3 location = labyrinth.getVoidSavePosPassed();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_save_passed.get", new Object[]{location.x, location.y, location.z}), false);
      return true;
   }

   @Execute
   public boolean setVoidSavePassed(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("voidSave") String entrance,
      @LiteralArg("passedLocation") String pos,
      @LiteralArg("set") String get,
      @Vec3Arg(name = "location", value = Type.CENTER) Vec3 location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setVoidSavePosPassed(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_save_passed.set", new Object[]{location.x, location.y, location.z}), true);
      return true;
   }

   @Execute
   public boolean getVoidHeight(
      @SenderArg CommandSourceStack stack, @LiteralArg("voidSave") String entrance, @LiteralArg("height") String height, @LiteralArg("get") String get
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_height.get", new Object[]{labyrinth.getVoidHeight()}), false);
      return true;
   }

   @Execute
   public boolean setVoidHeight(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("voidSave") String entrance,
      @LiteralArg("height") String height,
      @LiteralArg("set") String get,
      @DoubleArg("height") double location
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setVoidHeight(location);
      labyrinth.markDirty();
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.void_height.set", new Object[]{location}), true);
      return true;
   }

   @Execute
   public boolean getColossusSpawned(@SenderArg CommandSourceStack stack, @LiteralArg("colossusSpawned") String entrance, @LiteralArg("get") String get) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.colossus_spawned.get", new Object[]{labyrinth.isColossusSpawned()}), false);
      return true;
   }

   @Execute
   public boolean setColossusSpawned(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("colossusSpawned") String entrance,
      @LiteralArg("set") String get,
      @BooleanArg("spawned") boolean spawned
   ) {
      ServerLevel serverLevel = stack.getLevel().getServer().getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return false;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      labyrinth.setColossusSpawned(spawned);
      labyrinth.markDirty();
      if (spawned) {
         stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.colossus_spawned.set_true"), true);
      } else {
         stack.sendSuccess(() -> Component.translatable("tensura.command.labyrinth.colossus_spawned.set_false"), true);
      }

      return true;
   }

   @Execute
   public boolean checkPassing(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("check") String check,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector
   ) throws CommandSyntaxException {
      Entity selected = selector.findSingleEntity(stack);
      if (LabyrinthStorage.isEntityPassedColossus(selector.findSingleEntity(stack))) {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.check_true", new Object[]{selected.getName()}));
      } else {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.check_false", new Object[]{selected.getName()}));
      }

      return true;
   }

   @Execute
   public boolean checkPassing(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("check") String check,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector,
      @LiteralArg("pass") String pass
   ) throws CommandSyntaxException {
      Entity selected = selector.findSingleEntity(stack);
      if (LabyrinthStorage.isEntityPassedColossus(selected)) {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.check_true", new Object[]{selected.getName()}));
      } else {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.check_false", new Object[]{selected.getName()}));
      }

      return true;
   }

   @Execute
   public boolean checkWon(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("check") String check,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector,
      @LiteralArg("won") String won
   ) throws CommandSyntaxException {
      Entity selected = selector.findSingleEntity(stack);
      if (selected instanceof LivingEntity entity && TensuraStorages.getSpiritFrom(entity).isColossusWon()) {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.won.check_true", new Object[]{entity.getName()}));
      } else {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.labyrinth.passed_list.won.check_false", new Object[]{selected.getName()}));
      }

      return true;
   }

   @Execute
   public boolean addPassing(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("add") String add,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         LabyrinthStorage.addPassedEntity(entity, false);
         TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.labyrinth.passed_list.add", new Object[]{entity.getName()}));
      }

      return true;
   }

   @Execute
   public boolean removePassing(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("remove") String add,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         LabyrinthStorage.removePassedEntity(entity, false);
         TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.labyrinth.passed_list.remove", new Object[]{entity.getName()}));
      }

      return true;
   }

   @Execute
   public boolean wonPassing(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("passing") String passing,
      @LiteralArg("won") String add,
      @EntityArg(name = "entity", value = io.github.manasmods.manascore.command.api.parameter.EntityArg.Type.ENTITY) EntitySelector selector,
      @BooleanArg("won") boolean won
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         spirit.setColossusWon(won);
         spirit.markDirty();
         Component component = Component.translatable(won ? "tensura.message.enabled" : "tensura.message.disabled");
         TensuraCommands.sendSuccess(
            stack, entity, Component.translatable("tensura.command.labyrinth.passed_list.won", new Object[]{entity.getName(), component})
         );
      }

      return true;
   }
}
