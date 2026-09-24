package io.github.manasmods.tensura.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Command("despawn")
@Permission(value = "tensura.command.despawn", permissionLevel = PermissionLevel.OWNER)
public class DespawnCommand {
   @Execute
   public boolean despawn(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector) throws CommandSyntaxException {
      List<? extends Entity> entities = selector.findEntities(stack);
      if (entities.size() == 1) {
         Entity entity = entities.getFirst();
         if (entity instanceof Player) {
            stack.sendSuccess(() -> Component.translatable("tensura.targeting.not_allowed", new Object[]{entity.getName()}), true);
         } else {
            entity.discard();
            stack.sendSuccess(
               () -> Component.translatable("tensura.command.despawn", new Object[]{entity.getName()}).withStyle(ChatFormatting.DARK_GREEN), true
            );
         }

         return true;
      } else {
         int i = 0;

         for (Entity entity : entities) {
            if (!(entity instanceof Player)) {
               entity.discard();
               i++;
            }
         }

         MutableComponent component = Component.translatable("tensura.command.despawn.all", new Object[]{i}).withStyle(ChatFormatting.DARK_GREEN);
         stack.sendSuccess(() -> component, true);
         return true;
      }
   }
}
