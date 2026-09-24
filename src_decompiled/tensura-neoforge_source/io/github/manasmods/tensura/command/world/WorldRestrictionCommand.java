package io.github.manasmods.tensura.command.world;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.parameter.DimensionArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.BlockPosArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg.Type;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.WorldRestrictionArg;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.restriction.WorldRestrictionStorage;
import io.github.manasmods.tensura.storage.restriction.template.IWorldRestriction;
import io.github.manasmods.tensura.storage.restriction.template.WorldRestrictionInstance;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

@Command("worldRestriction")
public class WorldRestrictionCommand {
   @Execute
   public boolean create(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @BlockPosArg("corner1") BlockPos corner1,
      @BlockPosArg("corner2") BlockPos corner2,
      @DimensionArg ServerLevel dimension
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = new WorldRestrictionInstance(corner1, corner2, dimension.dimension());
         holder.addRestriction(name, instance);
         String c1 = "[" + corner1.getX() + ", " + corner1.getY() + ", " + corner1.getZ() + "]";
         String c2 = "[" + corner2.getX() + ", " + corner2.getY() + ", " + corner2.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.created", new Object[]{name, c1, c2, dimension.dimension().location().toString()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean create(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @BlockPosArg("corner1") BlockPos corner1,
      @BlockPosArg("corner2") BlockPos corner2
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = new WorldRestrictionInstance(corner1, corner2, stack.getLevel().dimension());
         holder.addRestriction(name, instance);
         String c1 = "[" + corner1.getX() + ", " + corner1.getY() + ", " + corner1.getZ() + "]";
         String c2 = "[" + corner2.getX() + ", " + corner2.getY() + ", " + corner2.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.created", new Object[]{name, c1, c2, stack.getLevel().dimension().location().toString()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean createGlobal(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @LiteralArg("global") String global,
      @DimensionArg ServerLevel dimension
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = new WorldRestrictionInstance(dimension.dimension());
         holder.addRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.created.global", new Object[]{name, dimension.dimension().location().toString()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean createGlobal(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("create") String add,
      @TextArg(value = Type.STRING, name = "name") String name,
      @LiteralArg("global") String global
   ) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.existed", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = new WorldRestrictionInstance(stack.getLevel().dimension());
         holder.addRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.created.global", new Object[]{name, stack.getLevel().dimension().location().toString()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean remove(@SenderArg CommandSourceStack stack, @LiteralArg("remove") String remove, @WorldRestrictionArg String name) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         holder.removeRestriction(name);
         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.world_restriction.removed", new Object[]{name}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean list(@SenderArg CommandSourceStack stack, @LiteralArg("list") String get) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (holder.getRestrictions().isEmpty()) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.list.empty").withStyle(ChatFormatting.RED));
      } else {
         MutableComponent component = null;

         for (String name : holder.getRestrictions().keySet()) {
            if (component == null) {
               component = Component.literal(name);
            } else {
               component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
            }
         }

         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.world_restriction.list", new Object[]{component}), ChatFormatting.DARK_GREEN);
      }

      return true;
   }

   @Execute
   public boolean get(@SenderArg CommandSourceStack stack, @LiteralArg("get") String get, @WorldRestrictionArg String name) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.get", new Object[]{name})
               .withStyle(ChatFormatting.DARK_GREEN)
               .append("\n")
               .append(instance.getDataMessage())
         );
      }

      return true;
   }

   @Execute
   public boolean reload(@SenderArg CommandSourceStack stack, @LiteralArg("reload") String reload) throws CommandSyntaxException {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      holder.reloadFromJson();
      TensuraCommands.sendSuccess(stack, Component.translatable("tensura.world_restriction.reloaded").withStyle(ChatFormatting.DARK_GREEN));
      return true;
   }

   @Execute
   public boolean editCorner1(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("corner1") String s,
      @BlockPosArg("position") BlockPos pos
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.setCorner1(pos);
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         String posStr = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.world_restriction.edit.corner1", new Object[]{name, posStr}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editCorner2(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("corner2") String s,
      @BlockPosArg("position") BlockPos pos
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.setCorner2(pos);
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         String posStr = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.world_restriction.edit.corner2", new Object[]{name, posStr}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean clearCorner1(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("corner1") String s,
      @LiteralArg("remove") String r
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.setCorner1(null);
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.world_restriction.edit.corner1.removed", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean clearCorner2(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("corner2") String s,
      @LiteralArg("remove") String r
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.setCorner2(null);
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.world_restriction.edit.corner2.removed", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean editDimension(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("dimension") String s,
      @DimensionArg ServerLevel dimension
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.setDimension(dimension.dimension());
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.world_restriction.edit.dimension", new Object[]{name, dimension.dimension().location().toString()}),
            ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }

   @Execute
   public boolean banAbilityAll(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("ban") String ban,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("all") String all
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         if (instance.isAbilityBanned(ability)) {
            stack.sendFailure(
               Component.translatable("tensura.world_restriction.edit.ability.banned", new Object[]{name, ability.getChatDisplayName(true)})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.addBannedAbility(ability);
            WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.world_restriction.edit.ability.ban", new Object[]{name, ability.getChatDisplayName(true)}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean banAbilityMode(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("ban") String ban,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("mode") String all,
      @IntegerArg(value = "mode", min = 0) int mode
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         if (instance.isAbilityBanned(ability, mode)) {
            stack.sendFailure(
               Component.translatable("tensura.world_restriction.edit.ability.banned_mode", new Object[]{name, ability.getChatDisplayName(true), mode})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.addBannedAbility(ability, mode);
            WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.world_restriction.edit.ability.ban_mode", new Object[]{name, ability.getChatDisplayName(true), mode}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean unbanAbility(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("unban") String unban,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("all") String all
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         Set<Integer> modes = instance.getBannedModes(ability);
         if (modes.isEmpty() && !instance.getBannedAbilities().containsKey(ability)) {
            stack.sendFailure(
               Component.translatable("tensura.world_restriction.edit.ability.not_banned", new Object[]{name, ability.getChatDisplayName(true)})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.removeBannedAbility(ability);
            WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.world_restriction.edit.ability.unban", new Object[]{name, ability.getChatDisplayName(true)}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean unbanAbilityMode(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("unban") String unban,
      @SkillArg Holder<ManasSkill> skill,
      @LiteralArg("mode") String all,
      @IntegerArg(value = "mode", min = 0) int mode
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         ManasSkill ability = (ManasSkill)skill.value();
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         Set<Integer> modes = instance.getBannedModes(ability);
         if (!modes.contains(mode)) {
            stack.sendFailure(
               Component.translatable("tensura.world_restriction.edit.ability.not_banned_mode", new Object[]{name, ability.getChatDisplayName(true), mode})
                  .withStyle(ChatFormatting.RED)
            );
         } else {
            instance.removeBannedMode(ability, mode);
            WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
            TensuraCommands.sendSuccess(
               stack,
               Component.translatable("tensura.world_restriction.edit.ability.unban_mode", new Object[]{name, ability.getChatDisplayName(true), mode}),
               ChatFormatting.DARK_GREEN
            );
         }
      }

      return true;
   }

   @Execute
   public boolean clearAbilities(
      @SenderArg CommandSourceStack stack,
      @LiteralArg("edit") String edit,
      @WorldRestrictionArg String name,
      @LiteralArg("ability") String a,
      @LiteralArg("clear") String clear
   ) {
      ServerLevel overworld = stack.getServer().overworld();
      IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
      if (!holder.getRestrictions().containsKey(name)) {
         stack.sendFailure(Component.translatable("tensura.world_restriction.not_found", new Object[]{name}).withStyle(ChatFormatting.RED));
      } else {
         WorldRestrictionInstance instance = holder.getRestrictions().get(name);
         instance.clearBannedAbilities();
         WorldRestrictionStorage.getConfigManager().saveRestriction(name, instance);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.world_restriction.edit.ability.clear", new Object[]{name}), ChatFormatting.DARK_GREEN
         );
      }

      return true;
   }
}
