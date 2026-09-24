package io.github.manasmods.tensura.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.DimensionArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.coordinate.BlockPosArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.TextArg.Type;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.command.argument.MagicTypeArg;
import io.github.manasmods.tensura.command.argument.SkillArg;
import io.github.manasmods.tensura.command.argument.SkillTypeArg;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.network.c2s.RequestSpatialActionPacket;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

@Command(
   value = "ability",
   subCommands = {
         AbilityCommand.SkillSetCommand.class,
         AbilityCommand.SkillLockCommand.class,
         AbilityCommand.SkillToggleCommand.class,
         AbilityCommand.PresetCommand.class,
         AbilityCommand.SpatialCommand.class
   }
)
@Permission(value = "tensura.command.ability_slot", permissionLevel = PermissionLevel.PLAYER)
public class AbilityCommand {
   private static void toggleAllAbility(CommandSourceStack stack, Predicate<ManasSkill> predicate, boolean toggle) {
      ServerPlayer entity = stack.getPlayer();
      if (entity != null) {
         int i = 0;

         for (ManasSkillInstance instance : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (predicate.test(instance.getSkill()) && instance.canBeToggled(entity) && instance.canInteractSkill(entity) && instance.isToggled() != toggle) {
               instance.setToggled(toggle);
               if (toggle) {
                  instance.onToggleOn(entity);
               } else {
                  instance.onToggleOff(entity);
               }

               i++;
            }
         }

         String message = toggle ? "tensura.skill.toggle_all.on" : "tensura.skill.toggle_all.off";
         TensuraCommands.sendSuccess(stack, entity, Component.translatable(message, new Object[]{i, entity.getName()}));
      }
   }

   private static void toggleRandomAbility(CommandSourceStack stack, Predicate<ManasSkill> predicate, boolean toggle) {
      ServerPlayer player = stack.getPlayer();
      if (player != null) {
         Skills skills = SkillAPI.getSkillsFrom(player);
         List<ManasSkill> list = SkillAPI.getSkillRegistry()
            .entrySet()
            .stream()
            .map(Entry::getValue)
            .filter(manasSkill -> skills.getSkill(manasSkill).isEmpty() ? false : predicate.test(manasSkill))
            .toList();
         if (list.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.set_cooldown.all.no_changes", new Object[]{player.getName()}));
         } else {
            ManasSkill skill = list.get(player.level().random.nextInt(list.size()));
            Optional<ManasSkillInstance> optional = skills.getSkill(skill);
            if (optional.isEmpty()) {
               return;
            }

            ManasSkillInstance skillInstance = optional.get();
            if (!skillInstance.canBeToggled(player) || !skillInstance.canInteractSkill(player)) {
               stack.sendFailure(Component.translatable("tensura.skill.toggle.failed", new Object[]{skill.getChatDisplayName(true), player.getName()}));
               return;
            }

            if (skillInstance.isToggled()) {
               if (!toggle) {
                  skillInstance.setToggled(false);
                  skillInstance.onToggleOff(player);
                  TensuraCommands.sendSuccess(
                     stack, player, Component.translatable("tensura.skill.toggle_off", new Object[]{skill.getChatDisplayName(true), player.getName()})
                  );
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.toggle_off.already", new Object[]{skill.getChatDisplayName(true), player.getName()}));
               }
            } else if (toggle) {
               skillInstance.setToggled(true);
               skillInstance.onToggleOn(player);
               TensuraCommands.sendSuccess(
                  stack, player, Component.translatable("tensura.skill.toggle_on", new Object[]{skill.getChatDisplayName(true), player.getName()})
               );
            } else {
               stack.sendFailure(Component.translatable("tensura.skill.toggle_on.already", new Object[]{skill.getChatDisplayName(true), player.getName()}));
            }
         }
      }
   }

   @Command(value = "preset", subCommands = {AbilityCommand.PresetCommand.PresetSetCommand.class, AbilityCommand.PresetCommand.PresetNameCommand.class})
   public static class PresetCommand {
      @Command("name")
      public static class PresetNameCommand {
         @Execute
         public boolean setPresetName(
            @SenderArg CommandSourceStack stack, @IntegerArg("preset") int preset, @TextArg(value = Type.STRING, name = "name") String name
         ) {
            if (preset >= 1 && preset <= 9) {
               ServerPlayer player = stack.getPlayer();
               if (player == null) {
                  return false;
               }

               IAbility ability = TensuraStorages.getAbilityFrom(player);
               int i = preset - 1;
               if (!Objects.equals(ability.getPresetName(i), name)) {
                  ability.setPresetName(i, name);
                  TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.skill.preset.changed_name", new Object[]{preset, name}));
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.preset.no_change", new Object[]{ability.getPresetName(i)}));
               }

               return true;
            } else {
               stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
               return false;
            }
         }
      }

      @Command("set")
      public static class PresetSetCommand {
         @Execute
         public boolean setPreset(@SenderArg CommandSourceStack stack, @IntegerArg("preset") int preset) {
            if (preset >= 1 && preset <= 9) {
               ServerPlayer player = stack.getPlayer();
               if (player == null) {
                  return false;
               }

               IAbility ability = TensuraStorages.getAbilityFrom(player);
               int newPreset = preset - 1;
               if (ability.getActivePreset() != newPreset) {
                  ability.setActivePreset(newPreset);
                  TensuraCommands.sendSuccess(
                     stack, player, Component.translatable("tensura.skill.preset.changed", new Object[]{ability.getPresetName(newPreset)})
                  );
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.preset.no_change", new Object[]{ability.getPresetName(newPreset)}));
               }

               return true;
            } else {
               stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
               return false;
            }
         }
      }
   }

   @Command("lock")
   public static class SkillLockCommand {
      @Execute
      public boolean lockSkill(@SenderArg CommandSourceStack stack, @LiteralArg("add") String a, @SkillArg Holder<ManasSkill> skill) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() == null) {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }

         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
         if (!instance.isEmpty() && !(instance.get().getMastery() < 0.0) && !instance.get().isTemporarySkill()) {
            ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
            int per = stack.getLevel().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
            if (per <= 0 && playerData.getBonusSkillLock() <= 0) {
               stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.disabled"));
               return false;
            } else {
               int count = playerData.getBonusSkillLock() + playerData.getResetCounter() / per;
               if (playerData.getLockedSkills().size() >= count) {
                  stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.not_enough"));
                  return false;
               } else if (playerData.getLockedSkills().contains(manasSkill.getRegistryName())) {
                  stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.locked"));
                  return false;
               } else if (!RequestAbilityModeChangePacket.getLockableSkills(ReincarnationMenu.getSkillPool(), stack.getLevel()).contains(manasSkill)) {
                  stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.unavailable"));
                  return false;
               } else {
                  playerData.addLockedSkill(manasSkill.getRegistryName());
                  stack.sendSuccess(
                     () -> Component.translatable("tensura.command.reset_counter.lock", new Object[]{manasSkill.getChatDisplayName(true)}), false
                  );
                  return true;
               }
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
            return false;
         }
      }

      @Execute
      public boolean removeSkill(@SenderArg CommandSourceStack stack, @LiteralArg("remove") String a, @SkillArg Holder<ManasSkill> skill) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() == null) {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         } else {
            ServerPlayer player = stack.getPlayer();
            if (player == null) {
               return false;
            } else {
               Skills skills = SkillAPI.getSkillsFrom(player);
               Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
               if (instance.isEmpty()) {
                  stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
                  return false;
               } else {
                  ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
                  if (!playerData.getLockedSkills().contains(manasSkill.getRegistryName())) {
                     stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.not_locked"));
                     return false;
                  } else {
                     playerData.removeLockedSkill(manasSkill.getRegistryName());
                     stack.sendSuccess(
                        () -> Component.translatable("tensura.command.reset_counter.lock.remove", new Object[]{manasSkill.getChatDisplayName(true)}), false
                     );
                     return true;
                  }
               }
            }
         }
      }

      @Execute
      public boolean clear(@SenderArg CommandSourceStack stack, @LiteralArg("clear") String a) {
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         } else {
            ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
            if (playerData.getLockedSkills().isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.empty"));
               return false;
            } else {
               playerData.clearLockedSkills();
               stack.sendSuccess(() -> Component.translatable("tensura.command.reset_counter.lock.clear"), false);
               return true;
            }
         }
      }

      @Execute
      public boolean list(@SenderArg CommandSourceStack stack, @LiteralArg("list") String a) {
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
         MutableComponent component = null;

         for (ResourceLocation location : playerData.getLockedSkills()) {
            ManasSkill locked = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
            if (locked != null) {
               MutableComponent name = locked.getChatDisplayName(true);
               if (component == null) {
                  component = name;
               } else {
                  component = component.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
               }
            }
         }

         if (component == null) {
            stack.sendFailure(Component.translatable("tensura.command.reset_counter.lock.empty"));
         } else {
            TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.reset_counter.lock.list", new Object[]{component}));
         }

         return true;
      }
   }

   @Command("set")
   public static class SkillSetCommand {
      @Execute
      public boolean setSlot(
         @SenderArg CommandSourceStack stack, @SkillArg Holder<ManasSkill> skill, @IntegerArg("mode") int mode, @IntegerArg("slot") int slot
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() != null && mode >= 0 && slot >= 1 && slot <= 3) {
            ServerPlayer player = stack.getPlayer();
            if (player == null) {
               return false;
            }

            Skills skills = SkillAPI.getSkillsFrom(player);
            Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
            if (!instance.isEmpty() && mode < instance.get().getModes()) {
               if (manasSkill instanceof TensuraSkill tensuraSkill) {
                  if (!tensuraSkill.canBeSlotted(instance.get(), player, mode)) {
                     stack.sendFailure(Component.translatable("tensura.skill.skill_set.failed", new Object[]{manasSkill.getChatDisplayName(true)}));
                     return false;
                  }

                  int prevMode = mode == 0 ? instance.get().getModes() - 1 : mode - 1;
                  if (tensuraSkill.nextMode(player, instance.get(), prevMode, false) != mode) {
                     stack.sendFailure(Component.translatable("tensura.skill.mode.cannot_change", new Object[]{manasSkill.getChatDisplayName(true)}));
                     return false;
                  }
               }

               IAbility ability = TensuraStorages.getAbilityFrom(player);
               ability.setAbilitySlot(slot - 1, manasSkill, mode);
               ability.markDirty();
               stack.sendSuccess(() -> Component.translatable("tensura.skill.skill_set", new Object[]{manasSkill.getChatDisplayName(true), slot}), false);
               return true;
            } else {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
               return false;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }
      }
   }

   @Command(
      value = "toggle",
      subCommands = {AbilityCommand.SkillToggleCommand.ToggleAllCommand.class, AbilityCommand.SkillToggleCommand.ToggleRandomCommand.class}
   )
   public static class SkillToggleCommand {
      @Execute
      public boolean toggle(@SenderArg CommandSourceStack stack, @SkillArg Holder<ManasSkill> skill, @BooleanArg("toggle") boolean toggle) {
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills storage = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)skill.value());
         if (optional.isEmpty()) {
            stack.sendFailure(
               Component.translatable("tensura.skill.no_skill", new Object[]{player.getName(), ((ManasSkill)skill.value()).getChatDisplayName(true)})
            );
            return true;
         }

         ManasSkillInstance instance = optional.get();
         if (instance.canBeToggled(player) && instance.canInteractSkill(player) && !(instance.getMastery() < 0.0)) {
            if (toggle) {
               if (!instance.isToggled()) {
                  instance.setToggled(true);
                  instance.onToggleOn(player);
                  TensuraCommands.sendSuccess(
                     stack,
                     player,
                     Component.translatable("tensura.skill.toggle_on", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), player.getName()})
                  );
               } else {
                  stack.sendFailure(
                     Component.translatable(
                        "tensura.skill.toggle_on.already", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), player.getName()}
                     )
                  );
               }
            } else if (instance.isToggled()) {
               instance.setToggled(false);
               instance.onToggleOff(player);
               TensuraCommands.sendSuccess(
                  stack,
                  player,
                  Component.translatable("tensura.skill.toggle_off", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), player.getName()})
               );
            } else {
               stack.sendFailure(
                  Component.translatable(
                     "tensura.skill.toggle_off.already", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true), player.getName()}
                  )
               );
            }

            return true;
         } else {
            stack.sendFailure(Component.translatable("tensura.skill.toggle.failed", new Object[]{((ManasSkill)skill.value()).getChatDisplayName(true)}));
            return true;
         }
      }

      @Command(
         value = "all",
         subCommands = {
               AbilityCommand.SkillToggleCommand.ToggleAllCommand.ToggleAllSkillCommand.class,
               AbilityCommand.SkillToggleCommand.ToggleAllCommand.ToggleAllMagicCommand.class,
               AbilityCommand.SkillToggleCommand.ToggleAllCommand.ToggleAllBattlewillCommand.class
         }
      )
      public static class ToggleAllCommand {
         @Execute
         public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
            AbilityCommand.toggleAllAbility(stack, skill -> true, toggle);
            return true;
         }

         @Command("battlewill")
         public static class ToggleAllBattlewillCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleAllAbility(stack, skill -> skill instanceof Battlewill, toggle);
               return true;
            }
         }

         @Command("magic")
         public static class ToggleAllMagicCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleAllAbility(stack, skill -> skill instanceof Magic, toggle);
               return true;
            }

            @Execute
            public boolean toggleAllWithType(@SenderArg CommandSourceStack stack, @MagicTypeArg Magic.MagicType type, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleAllAbility(stack, skill -> skill instanceof Magic magic && magic.getType() == type, toggle);
               return true;
            }
         }

         @Command("skill")
         public static class ToggleAllSkillCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleAllAbility(stack, skill -> skill instanceof Skill, toggle);
               return true;
            }

            @Execute
            public boolean toggleAllWithType(@SenderArg CommandSourceStack stack, @SkillTypeArg Skill.SkillType type, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleAllAbility(stack, skill -> skill instanceof Skill skillType && skillType.getType() == type, toggle);
               return true;
            }
         }
      }

      @Command(
         value = "random",
         subCommands = {
               AbilityCommand.SkillToggleCommand.ToggleRandomCommand.ToggleRandomSkillCommand.class,
               AbilityCommand.SkillToggleCommand.ToggleRandomCommand.ToggleRandomMagicCommand.class,
               AbilityCommand.SkillToggleCommand.ToggleRandomCommand.ToggleRandomBattlewillCommand.class
         }
      )
      public static class ToggleRandomCommand {
         @Execute
         public boolean toggleRandom(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
            AbilityCommand.toggleRandomAbility(stack, skill -> true, toggle);
            return true;
         }

         @Command("battlewill")
         public static class ToggleRandomBattlewillCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleRandomAbility(stack, skill -> skill instanceof Battlewill, toggle);
               return true;
            }
         }

         @Command("magic")
         public static class ToggleRandomMagicCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleRandomAbility(stack, skill -> skill instanceof Magic, toggle);
               return true;
            }

            @Execute
            public boolean toggleAllWithType(@SenderArg CommandSourceStack stack, @MagicTypeArg Magic.MagicType type, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleRandomAbility(stack, skill -> skill instanceof Magic magic && magic.getType() == type, toggle);
               return true;
            }
         }

         @Command("skill")
         public static class ToggleRandomSkillCommand {
            @Execute
            public boolean toggleAll(@SenderArg CommandSourceStack stack, @BooleanArg("toggle") boolean toggle) throws CommandSyntaxException {
               AbilityCommand.toggleRandomAbility(stack, skill -> skill instanceof Skill, toggle);
               return true;
            }

            @Execute
            public boolean toggleAllWithType(@SenderArg CommandSourceStack stack, @SkillTypeArg Skill.SkillType type, @BooleanArg("toggle") boolean toggle) {
               AbilityCommand.toggleRandomAbility(stack, skill -> skill instanceof Skill skillType && skillType.getType() == type, toggle);
               return true;
            }
         }
      }
   }

   @Command("spatial")
   public static class SpatialCommand {
      @Execute
      public boolean warp(
         @SenderArg CommandSourceStack stack,
         @LiteralArg("warp") String warp,
         @SkillArg Holder<ManasSkill> skill,
         @BlockPosArg("location") BlockPos pos,
         @DimensionArg ServerLevel dimension
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
         if (instance.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
            return false;
         }

         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialMovement spatial) {
            if (!instance.get().canInteractSkill(player)
               || instance.get().getMastery() < 0.0
               || !spatial.canWarp(instance.get(), player)
               || player.level().dimension() == TensuraDimensions.LABYRINTH
               || player.level().dimension() == TensuraDimensions.BOSS_AREA) {
               stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
               return false;
            } else if (instance.get().onCoolDown(spatial.getSpatialMovementModeIndex())
               && !instance.get().canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
               stack.sendFailure(Component.translatable("tensura.skill.cooldown", new Object[]{manasSkill.getChatDisplayName(true)}));
               return false;
            } else if (SkillUtils.shouldCancelTeleportation(player)) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
               return false;
            } else {
               RequestSpatialActionPacket.handleWarp(player, instance.get(), spatial, pos.getX(), pos.getY(), pos.getZ(), dimension.dimension(), false);
               return true;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }
      }

      @Execute
      public boolean warp(
         @SenderArg CommandSourceStack stack, @LiteralArg("warp") String warp, @SkillArg Holder<ManasSkill> skill, @BlockPosArg("location") BlockPos pos
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
         if (instance.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
            return false;
         }

         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialMovement spatial) {
            if (!instance.get().canInteractSkill(player)
               || instance.get().getMastery() < 0.0
               || !spatial.canWarp(instance.get(), player)
               || player.level().dimension() == TensuraDimensions.LABYRINTH
               || player.level().dimension() == TensuraDimensions.BOSS_AREA) {
               stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
               return false;
            } else if (instance.get().onCoolDown(spatial.getSpatialMovementModeIndex())
               && !instance.get().canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
               stack.sendFailure(Component.translatable("tensura.skill.cooldown", new Object[]{manasSkill.getChatDisplayName(true)}));
               return false;
            } else if (SkillUtils.shouldCancelTeleportation(player)) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
               return false;
            } else {
               RequestSpatialActionPacket.handleWarp(player, instance.get(), spatial, pos.getX(), pos.getY(), pos.getZ(), player.level().dimension(), false);
               return true;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }
      }

      @Execute
      public boolean portal(
         @SenderArg CommandSourceStack stack,
         @LiteralArg("portal") String portal,
         @SkillArg Holder<ManasSkill> skill,
         @BlockPosArg("location") BlockPos pos,
         @DimensionArg ServerLevel dimension
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
         if (instance.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
            return false;
         }

         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialMovement spatial) {
            if (!instance.get().canInteractSkill(player)
               || instance.get().getMastery() < 0.0
               || !spatial.canPortal(instance.get(), player)
               || player.level().dimension() == TensuraDimensions.LABYRINTH
               || player.level().dimension() == TensuraDimensions.BOSS_AREA) {
               stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
               return false;
            } else if (instance.get().onCoolDown(spatial.getSpatialMovementModeIndex())
               && !instance.get().canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
               stack.sendFailure(Component.translatable("tensura.skill.cooldown", new Object[]{manasSkill.getChatDisplayName(true)}));
               return false;
            } else if (SkillUtils.shouldCancelTeleportation(player)) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
               return false;
            } else {
               RequestSpatialActionPacket.handlePortal(player, instance.get(), spatial, pos.getX(), pos.getY(), pos.getZ(), dimension.dimension(), false);
               return true;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }
      }

      @Execute
      public boolean portal(
         @SenderArg CommandSourceStack stack, @LiteralArg("portal") String portal, @SkillArg Holder<ManasSkill> skill, @BlockPosArg("location") BlockPos pos
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         ServerPlayer player = stack.getPlayer();
         if (player == null) {
            return false;
         }

         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
         if (instance.isEmpty()) {
            stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
            return false;
         }

         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialMovement spatial) {
            if (!instance.get().canInteractSkill(player)
               || instance.get().getMastery() < 0.0
               || !spatial.canPortal(instance.get(), player)
               || player.level().dimension() == TensuraDimensions.LABYRINTH
               || player.level().dimension() == TensuraDimensions.BOSS_AREA) {
               stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
               return false;
            } else if (instance.get().onCoolDown(spatial.getSpatialMovementModeIndex())
               && !instance.get().canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
               stack.sendFailure(Component.translatable("tensura.skill.cooldown", new Object[]{manasSkill.getChatDisplayName(true)}));
               return false;
            } else if (SkillUtils.shouldCancelTeleportation(player)) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
               return false;
            } else {
               RequestSpatialActionPacket.handlePortal(player, instance.get(), spatial, pos.getX(), pos.getY(), pos.getZ(), player.level().dimension(), false);
               return true;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.argument.skill.invalid"));
            return false;
         }
      }

      @Execute
      public boolean openStorage(
         @SenderArg CommandSourceStack stack, @LiteralArg("storage") String portal, @SkillArg Holder<ManasSkill> skill, @LiteralArg("open") String open
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialStorage spatial) {
            ServerPlayer player = stack.getPlayer();
            if (player == null) {
               return false;
            } else {
               Skills skills = SkillAPI.getSkillsFrom(player);
               Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
               if (instance.isEmpty()) {
                  stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
                  return false;
               } else if (instance.get().canInteractSkill(player) && !(instance.get().getMastery() < 0.0)) {
                  spatial.openSpatialStorage(player, instance.get());
                  return true;
               } else {
                  stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
                  return false;
               }
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.skill.spatial_storage.failed", new Object[]{manasSkill.getChatDisplayName(true)}));
            return false;
         }
      }

      @Execute
      public boolean addStorage(
         @SenderArg CommandSourceStack stack, @LiteralArg("storage") String portal, @SkillArg Holder<ManasSkill> skill, @LiteralArg("add") String add
      ) {
         ManasSkill manasSkill = (ManasSkill)skill.value();
         if (manasSkill.getRegistryName() != null && manasSkill instanceof ISpatialStorage spatial) {
            ServerPlayer player = stack.getPlayer();
            if (player == null) {
               return false;
            }

            Skills skills = SkillAPI.getSkillsFrom(player);
            Optional<ManasSkillInstance> instance = skills.getSkill(manasSkill);
            if (instance.isEmpty()) {
               stack.sendFailure(Component.translatable("tensura.skill.do_not_have", new Object[]{player.getName(), manasSkill.getChatDisplayName(true)}));
               return false;
            }

            if (instance.get().canInteractSkill(player) && !(instance.get().getMastery() < 0.0)) {
               if (spatial.addItemToSpatialStorage(instance.get(), player, player.getMainHandItem())) {
                  player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), SoundSource.PLAYERS, 0.75F, 1.0F);
               } else {
                  stack.sendFailure(Component.translatable("tensura.skill.spatial_storage.full", new Object[]{manasSkill.getName()}));
               }

               return true;
            } else {
               stack.sendFailure(Component.translatable("tensura.ability.activation_failed"));
               return false;
            }
         } else {
            stack.sendFailure(Component.translatable("tensura.skill.spatial_storage.failed", new Object[]{manasSkill.getChatDisplayName(true)}));
            return false;
         }
      }
   }
}
