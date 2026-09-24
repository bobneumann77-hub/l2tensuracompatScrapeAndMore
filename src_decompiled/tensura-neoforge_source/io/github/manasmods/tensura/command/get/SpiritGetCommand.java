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
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.ElementArg;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Command("spirit")
public class SpiritGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_spirit_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSpirit(
      @SenderArg CommandSourceStack stack, @LiteralArg("check") String check, @EntityArg(Type.ENTITY) EntitySelector selector, @ElementArg Element element
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         SpiritualMagic.SpiritLevel level = spirit.getSpiritLevel(element);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.spirit.get", new Object[]{entity.getName(), element.getName(), level == null ? 0 : level.getName()}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_spirit_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSpirit(
      @SenderArg CommandSourceStack stack, @LiteralArg("check") String check, @LiteralArg("self") String self, @ElementArg Element element
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
      SpiritualMagic.SpiritLevel level = spirit.getSpiritLevel(element);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.spirit.get", new Object[]{player.getName(), element.getName(), level == null ? 0 : level.getName()}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_spirit_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getList(@SenderArg CommandSourceStack stack, @LiteralArg("list") String dl, @EntityArg(Type.ENTITY) EntitySelector selector) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         MutableComponent component = null;

         for (Element element : Element.values()) {
            SpiritualMagic.SpiritLevel level = spirit.getSpiritLevel(element);
            if (level != null) {
               MutableComponent name = element.getName().withColor(element.getColor()).append(": ").append(level.getName());
               if (component == null) {
                  component = name;
               } else {
                  component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
               }
            }
         }

         if (component == null) {
            stack.sendFailure(Component.translatable("tensura.command.spirit.list.empty", new Object[]{entity.getName()}));
         } else {
            TensuraCommands.sendSuccess(
               stack, Component.translatable("tensura.command.spirit.list", new Object[]{entity.getName(), component}), ChatFormatting.AQUA
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_spirit_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getList(@SenderArg CommandSourceStack stack, @LiteralArg("list") String dl, @LiteralArg("self") String self) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
      MutableComponent component = null;

      for (Element element : Element.values()) {
         SpiritualMagic.SpiritLevel level = spirit.getSpiritLevel(element);
         if (level != null) {
            MutableComponent name = element.getName().withColor(element.getColor()).append(": ").append(level.getName());
            if (component == null) {
               component = name;
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }
      }

      if (component == null) {
         stack.sendFailure(Component.translatable("tensura.command.spirit.list.empty", new Object[]{player.getName()}));
      } else {
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.spirit.list", new Object[]{player.getName(), component}), ChatFormatting.AQUA
         );
      }

      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_spirit_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getCooldown(@SenderArg CommandSourceStack stack, @LiteralArg("cooldown") String dl, @EntityArg(Type.ENTITY) EntitySelector selector) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.spirit.cooldown.get", new Object[]{entity.getName(), spirit.getSpiritCooldown()}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_spirit_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getCooldown(@SenderArg CommandSourceStack stack, @LiteralArg("cooldown") String dl, @LiteralArg("self") String self) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.spirit.cooldown.get", new Object[]{player.getName(), spirit.getSpiritCooldown()}), ChatFormatting.AQUA
      );
      return true;
   }
}
