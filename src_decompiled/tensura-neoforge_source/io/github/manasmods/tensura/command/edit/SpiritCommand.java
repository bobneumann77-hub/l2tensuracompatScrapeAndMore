package io.github.manasmods.tensura.command.edit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.ElementArg;
import io.github.manasmods.tensura.command.argument.SpiritLevelArg;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Command("spirit")
@Permission(value = "tensura.command.edit_spirit", permissionLevel = PermissionLevel.GAMEMASTER)
public class SpiritCommand {
   @Execute
   public boolean setSpirit(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("set") String dl,
      @ElementArg Element element,
      @SpiritLevelArg SpiritualMagic.SpiritLevel spiritLevel
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
            spirit.setSpiritLevel(element, spiritLevel);
            spirit.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.spirit.set", new Object[]{entity.getName(), element.getName(), spiritLevel.getName()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean clearSpirit(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @LiteralArg("clear") String dl) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
            spirit.clearSpiritLevel();
            spirit.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spirit.clear", new Object[]{entity.getName()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setCooldown(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("cooldown") String dl,
      @IntegerArg(value = "cooldown", min = 0) int cooldown
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
            spirit.setSpiritCooldown(cooldown);
            spirit.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spirit.cooldown.set", new Object[]{entity.getName(), cooldown}));
         }
      }

      return true;
   }
}
