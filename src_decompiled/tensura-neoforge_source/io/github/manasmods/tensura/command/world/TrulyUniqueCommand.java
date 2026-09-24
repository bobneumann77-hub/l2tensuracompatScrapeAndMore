package io.github.manasmods.tensura.command.world;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.unique.ITrulyUnique;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@Command("trulyUnique")
public class TrulyUniqueCommand {
   @Execute
   public boolean add(
      @SenderArg CommandSourceStack stack, @LiteralArg("add") String add, @EntityArg(Type.PLAYER) EntitySelector selector, @SkillArg Holder<ManasSkill> skill
   ) throws CommandSyntaxException {
      ServerLevel level = stack.getLevel();
      if (!level.getGameRules().getBoolean(TensuraGameRules.TRULY_UNIQUE)) {
         stack.sendFailure(Component.translatable("tensura.truly_unique.off"));
         return true;
      }

      ResourceLocation location = ((ManasSkill)skill.value()).getRegistryName();
      ITrulyUnique unique = TensuraStorages.getUniqueStorageFrom(level.getServer().overworld());
      if (unique.hasSkill(location)) {
         Component name = ObjectSelectionHelper.getPlayerNameFromUUID(level, unique.getOwner(location));
         if (name == null) {
            name = Component.literal(unique.getOwner(location).toString());
         }

         stack.sendFailure(
            Component.translatable("tensura.truly_unique.already_have", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), name})
               .withStyle(ChatFormatting.RED)
         );
      } else {
         ServerPlayer player = selector.findSinglePlayer(stack);
         unique.addSkill(location, player.getUUID());
         stack.sendSuccess(
            () -> Component.translatable("tensura.truly_unique.added", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), player.getName()})
               .withStyle(ChatFormatting.DARK_GREEN),
            true
         );
      }

      return true;
   }

   @Execute
   public boolean remove(@SenderArg CommandSourceStack stack, @LiteralArg("remove") String add, @SkillArg Holder<ManasSkill> skill) throws CommandSyntaxException {
      ServerLevel level = stack.getLevel();
      if (!level.getGameRules().getBoolean(TensuraGameRules.TRULY_UNIQUE)) {
         stack.sendFailure(Component.translatable("tensura.truly_unique.off"));
         return true;
      }

      ResourceLocation location = ((ManasSkill)skill.value()).getRegistryName();
      ITrulyUnique unique = TensuraStorages.getUniqueStorageFrom(level.getServer().overworld());
      if (!unique.hasSkill(location)) {
         stack.sendFailure(
            Component.translatable("tensura.truly_unique.do_not_have", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true)})
               .withStyle(ChatFormatting.RED)
         );
      } else {
         Component name = ObjectSelectionHelper.getPlayerNameFromUUID(level, unique.getOwner(location));
         if (name == null) {
            name = Component.literal(unique.getOwner(location).toString());
         }

         unique.removeSkill(location);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.truly_unique.removed", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), name}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean check(@SenderArg CommandSourceStack stack, @LiteralArg("check") String add, @SkillArg Holder<ManasSkill> skill) {
      ServerLevel level = stack.getLevel();
      if (!level.getGameRules().getBoolean(TensuraGameRules.TRULY_UNIQUE)) {
         stack.sendFailure(Component.translatable("tensura.truly_unique.off"));
         return true;
      }

      ResourceLocation location = ((ManasSkill)skill.value()).getRegistryName();
      ITrulyUnique unique = TensuraStorages.getUniqueStorageFrom(level.getServer().overworld());
      if (unique.hasSkill(location)) {
         Component name = ObjectSelectionHelper.getPlayerNameFromUUID(level, unique.getOwner(location));
         if (name == null) {
            name = Component.literal(unique.getOwner(location).toString());
         }

         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.truly_unique.already_have", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), name}),
            ChatFormatting.DARK_GREEN
         );
      } else {
         stack.sendFailure(
            Component.translatable("tensura.truly_unique.do_not_have", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true)})
               .withStyle(ChatFormatting.RED)
         );
      }

      return true;
   }

   @Execute
   public boolean listTaken(@SenderArg CommandSourceStack stack, @LiteralArg("list") String s, @LiteralArg("taken") String taken) {
      ServerLevel level = stack.getLevel();
      MutableComponent component = null;
      ITrulyUnique unique = TensuraStorages.getUniqueStorageFrom(level.getServer().overworld());
      Map<ResourceLocation, UUID> skillMap = unique.getSkillMap();

      for (ResourceLocation location : skillMap.keySet().stream().toList()) {
         ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
         if (manasSkill != null) {
            MutableComponent name = manasSkill.getChatDisplayName(true);
            MutableComponent playerName = ObjectSelectionHelper.getPlayerNameFromUUID(level, skillMap.get(location));
            if (playerName == null) {
               playerName = Component.literal(skillMap.get(location).toString());
            }

            name = name.append(" - ").append(playerName.withStyle(ChatFormatting.LIGHT_PURPLE));
            if (component == null) {
               component = name;
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }
      }

      if (component == null) {
         stack.sendFailure(Component.translatable("tensura.truly_unique.list.taken.empty"));
      } else {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.truly_unique.list.taken", new Object[]{component}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean listRemain(@SenderArg CommandSourceStack stack, @LiteralArg("list") String s, @LiteralArg("remaining") String taken) {
      ServerLevel level = stack.getLevel();
      MutableComponent component = null;
      ITrulyUnique unique = TensuraStorages.getUniqueStorageFrom(level.getServer().overworld());

      for (ManasSkill skill : ReincarnationMenu.getSkillPool()) {
         if (!unique.hasSkill(skill.getRegistryName())) {
            MutableComponent name = skill.getChatDisplayName(true);
            if (component == null) {
               component = name;
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }
      }

      if (component == null) {
         stack.sendFailure(Component.translatable("tensura.truly_unique.list.remain.empty"));
      } else {
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.truly_unique.list.remain", new Object[]{component}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }
}
