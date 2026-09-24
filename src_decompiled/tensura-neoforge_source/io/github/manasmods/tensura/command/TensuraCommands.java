package io.github.manasmods.tensura.command;

import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.CommandArgumentRegistrationEvent;
import io.github.manasmods.manascore.command.api.CommandRegistry;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.command.argument.AlignmentArg;
import io.github.manasmods.tensura.command.argument.AlignmentArgument;
import io.github.manasmods.tensura.command.argument.BossFightArg;
import io.github.manasmods.tensura.command.argument.BossFightArgument;
import io.github.manasmods.tensura.command.argument.BossFightPlayerHandlerArg;
import io.github.manasmods.tensura.command.argument.BossFightPlayerHandlerArgument;
import io.github.manasmods.tensura.command.argument.ElementArg;
import io.github.manasmods.tensura.command.argument.ElementArgument;
import io.github.manasmods.tensura.command.argument.EntityTypeArg;
import io.github.manasmods.tensura.command.argument.GameModeArg;
import io.github.manasmods.tensura.command.argument.MagicTypeArg;
import io.github.manasmods.tensura.command.argument.MagicTypeArgument;
import io.github.manasmods.tensura.command.argument.RaceArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArgument;
import io.github.manasmods.tensura.command.argument.SpiritLevelArg;
import io.github.manasmods.tensura.command.argument.SpiritLevelArgument;
import io.github.manasmods.tensura.command.argument.TransmissionTypeArg;
import io.github.manasmods.tensura.command.argument.TransmissionTypeArgument;
import io.github.manasmods.tensura.command.argument.WorldRestrictionArg;
import io.github.manasmods.tensura.command.argument.WorldRestrictionArgument;
import io.github.manasmods.tensura.command.edit.OwnerCommand;
import io.github.manasmods.tensura.command.edit.ResetCommand;
import io.github.manasmods.tensura.command.edit.SpiritCommand;
import io.github.manasmods.tensura.command.edit.StatCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityCooldownCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityGrantCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityMasteryCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityModeLearningCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityRemoveTimeCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityRevokeCommand;
import io.github.manasmods.tensura.command.edit.ability.AbilityToggleCommand;
import io.github.manasmods.tensura.command.edit.race.AwakeningCommand;
import io.github.manasmods.tensura.command.edit.race.ForceEvoCommand;
import io.github.manasmods.tensura.command.edit.race.RaceCommand;
import io.github.manasmods.tensura.command.get.AbilityGetCommand;
import io.github.manasmods.tensura.command.get.AwakeningGetCommand;
import io.github.manasmods.tensura.command.get.OwnerGetCommand;
import io.github.manasmods.tensura.command.get.RaceGetCommand;
import io.github.manasmods.tensura.command.get.SpiritGetCommand;
import io.github.manasmods.tensura.command.get.StatGetCommand;
import io.github.manasmods.tensura.command.world.BossFightCommand;
import io.github.manasmods.tensura.command.world.ChunkMagiculeCommand;
import io.github.manasmods.tensura.command.world.LabyrinthCommand;
import io.github.manasmods.tensura.command.world.TrulyUniqueCommand;
import io.github.manasmods.tensura.command.world.WarpPadCommand;
import io.github.manasmods.tensura.command.world.WorldRestrictionCommand;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public class TensuraCommands {
   public static void init() {
      CommandArgumentRegistrationEvent.EVENT.register((CommandArgumentRegistrationEvent)(registry, dispatcher, buildContext) -> {
         registry.register(Holder.class, EntityTypeArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, ResourceArgument.resource(buildContext, Registries.ENTITY_TYPE)));
            handler.addValueExtractor(commandContext -> ResourceArgument.getResource(commandContext, argumentName, Registries.ENTITY_TYPE));
         });
         registry.register(GameType.class, GameModeArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, GameModeArgument.gameMode()));
            handler.addValueExtractor(commandContext -> GameModeArgument.getGameMode(commandContext, argumentName));
         });
         registry.register(Holder.class, RaceArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, ResourceArgument.resource(buildContext, RaceAPI.getRaceRegistryKey())));
            handler.addValueExtractor(commandContext -> ResourceArgument.getResource(commandContext, argumentName, RaceAPI.getRaceRegistryKey()));
         });
         registry.register(Holder.class, SkillArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, ResourceArgument.resource(buildContext, SkillAPI.getSkillRegistryKey())));
            handler.addValueExtractor(commandContext -> ResourceArgument.getResource(commandContext, argumentName, SkillAPI.getSkillRegistryKey()));
         });
         registry.register(Alignment.class, AlignmentArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, AlignmentArgument.alignment()));
            handler.addValueExtractor(commandContext -> AlignmentArgument.getAlignment(commandContext, argumentName));
         });
         registry.register(Element.class, ElementArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, ElementArgument.element()));
            handler.addValueExtractor(commandContext -> ElementArgument.getElement(commandContext, argumentName));
         });
         registry.register(Magic.MagicType.class, MagicTypeArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, MagicTypeArgument.magicType()));
            handler.addValueExtractor(commandContext -> MagicTypeArgument.getMagicType(commandContext, argumentName));
         });
         registry.register(Skill.SkillType.class, SkillTypeArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, SkillTypeArgument.skillType()));
            handler.addValueExtractor(commandContext -> SkillTypeArgument.getSkillType(commandContext, argumentName));
         });
         registry.register(SpiritualMagic.SpiritLevel.class, SpiritLevelArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, SpiritLevelArgument.spiritLevel()));
            handler.addValueExtractor(commandContext -> SpiritLevelArgument.getSpiritLevel(commandContext, argumentName));
         });
         registry.register(String.class, BossFightArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, BossFightArgument.bossFight()));
            handler.addValueExtractor(commandContext -> BossFightArgument.getBossFightName(commandContext, argumentName));
         });
         registry.register(String.class, WorldRestrictionArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, WorldRestrictionArgument.worldRestriction()));
            handler.addValueExtractor(commandContext -> WorldRestrictionArgument.getWorldRestrictionName(commandContext, argumentName));
         });
         registry.register(BossFightInstance.PlayerCountHandler.class, BossFightPlayerHandlerArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, BossFightPlayerHandlerArgument.handler()));
            handler.addValueExtractor(commandContext -> BossFightPlayerHandlerArgument.getHandler(commandContext, argumentName));
         });
         registry.register(WarpPoint.TransmissionType.class, TransmissionTypeArg.class, (annotation, handler) -> {
            String argumentName = annotation.value().isBlank() ? handler.getAutoGeneratedArgumentName() : annotation.value();
            handler.addNode(Commands.argument(argumentName, TransmissionTypeArgument.type()));
            handler.addValueExtractor(commandContext -> TransmissionTypeArgument.getType(commandContext, argumentName));
         });
      });
      CommandRegistry.registerCommand(TensuraCommands.TensuraCommand.class);
      CommandRegistry.registerCommand(AbilityCommand.class);
      CommandRegistry.registerCommand(DespawnCommand.class);
      CommandRegistry.registerCommand(EngraveCommand.class);
      CommandRegistry.registerCommand(EvolveCommand.class);
      CommandRegistry.registerCommand(NameCommand.class);
      CommandRegistry.registerCommand(SyncCommand.class);
   }

   public static void sendSuccess(CommandSourceStack stack, MutableComponent component) {
      stack.sendSuccess(() -> component, false);
   }

   public static void sendSuccess(CommandSourceStack stack, MutableComponent component, ChatFormatting formatting) {
      stack.sendSuccess(() -> component.withStyle(formatting), false);
   }

   public static void sendSuccess(CommandSourceStack stack, Entity entity, MutableComponent component) {
      sendSuccess(stack, entity, component, ChatFormatting.DARK_GREEN);
   }

   public static void sendSuccess(CommandSourceStack stack, Entity entity, MutableComponent component, ChatFormatting formatting) {
      if (stack.getPlayer() != entity) {
         entity.sendSystemMessage(component);
      }

      stack.sendSuccess(() -> component.withStyle(formatting), true);
   }

   @Command(
      value = "tensura",
      subCommands = {
            TensuraCommands.TensuraCommand.TensuraEditCommand.class,
            TensuraCommands.TensuraCommand.TensuraGetCommand.class,
            ResetCommand.class,
            TensuraCommands.TensuraCommand.WorldDataCommand.class
      }
   )
   public static class TensuraCommand {
      @Command(
         value = "edit",
         subCommands = {
               TensuraCommands.TensuraCommand.TensuraEditCommand.TensuraEditAbilityCommand.class,
               SpiritCommand.class,
               StatCommand.class,
               RaceCommand.class,
               AwakeningCommand.class,
               ForceEvoCommand.class,
               OwnerCommand.class
         }
      )
      public static class TensuraEditCommand {
         @Command(
            value = "ability",
            subCommands = {
                  AbilityGrantCommand.class,
                  AbilityRevokeCommand.class,
                  AbilityMasteryCommand.class,
                  AbilityModeLearningCommand.class,
                  AbilityToggleCommand.class,
                  AbilityRemoveTimeCommand.class,
                  AbilityCooldownCommand.class
            }
         )
         @Permission(value = "tensura.command.edit_ability", permissionLevel = PermissionLevel.GAMEMASTER)
         public static class TensuraEditAbilityCommand {
         }
      }

      @Command(
         value = "get",
         subCommands = {
               AbilityGetCommand.class, RaceGetCommand.class, AwakeningGetCommand.class, StatGetCommand.class, SpiritGetCommand.class, OwnerGetCommand.class
         }
      )
      public static class TensuraGetCommand {
      }

      @Command(
         value = "worldData",
         subCommands = {
               TrulyUniqueCommand.class,
               LabyrinthCommand.class,
               ChunkMagiculeCommand.class,
               BossFightCommand.class,
               WarpPadCommand.class,
               WorldRestrictionCommand.class
         }
      )
      @Permission(value = "tensura.command.world_data", permissionLevel = PermissionLevel.ADMIN)
      public static class WorldDataCommand {
      }
   }
}
