package io.github.manasmods.tensura.command;

import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

@Command("nameable")
@Permission(value = "tensura.command.nameable", permissionLevel = PermissionLevel.PLAYER)
public class NameCommand {
   @Execute
   public boolean nameable(@SenderArg CommandSourceStack stack, @BooleanArg("nameable") boolean nameable) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      existence.setNameable(nameable);
      existence.markDirty();
      Component nameableComponent = Component.translatable(nameable ? "tensura.message.enabled" : "tensura.message.disabled");
      MutableComponent component = Component.translatable("tensura.naming.nameable_status", new Object[]{nameableComponent})
         .withStyle(ChatFormatting.DARK_GREEN);
      stack.sendSuccess(() -> component, true);
      return true;
   }
}
