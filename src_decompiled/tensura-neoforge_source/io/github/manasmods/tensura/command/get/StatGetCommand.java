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
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

@Command("stat")
public class StatGetCommand {
   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("aura") String dl,
      @LiteralArg("current") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.aura.get", new Object[]{entity.getName(), existence.getAura()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getAura(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("aura") String dl, @LiteralArg("current") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.aura.get", new Object[]{player.getName(), existence.getAura()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMaxAura(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("aura") String dl, @LiteralArg("max") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
         if (aura == null) {
            return false;
         }

         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.aura.get_max", new Object[]{entity.getName(), aura.getBaseValue()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMaxAura(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("aura") String dl, @LiteralArg("max") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      AttributeInstance aura = player.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura == null) {
         return false;
      }

      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.aura.get_max", new Object[]{player.getName(), aura.getBaseValue()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("magicule") String dl,
      @LiteralArg("current") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.magicule.get", new Object[]{entity.getName(), existence.getMagicule()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMagicule(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("magicule") String dl, @LiteralArg("current") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.magicule.get", new Object[]{player.getName(), existence.getMagicule()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("magicule") String dl,
      @LiteralArg("max") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
         if (magicule == null) {
            return false;
         }

         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.magicule.get_max", new Object[]{entity.getName(), magicule.getBaseValue()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMaxMagicule(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("magicule") String dl, @LiteralArg("max") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      AttributeInstance magicule = player.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule == null) {
         return false;
      }

      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.magicule.get_max", new Object[]{player.getName(), magicule.getBaseValue()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getEp(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("ep") String dl, @LiteralArg("current") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.ep.get", new Object[]{entity.getName(), existence.getEP()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getEp(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("ep") String dl, @LiteralArg("current") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.ep.get", new Object[]{player.getName(), existence.getEP()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMaxEp(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("ep") String dl, @LiteralArg("max") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         double amount = 0.0;
         AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
         if (aura != null) {
            amount += aura.getBaseValue();
         }

         AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
         if (magicule != null) {
            amount += magicule.getBaseValue();
         }

         TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.ep.get_max", new Object[]{entity.getName(), amount}), ChatFormatting.AQUA);
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMaxEp(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("ep") String dl, @LiteralArg("max") String current) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      double amount = 0.0;
      AttributeInstance aura = player.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         amount += aura.getBaseValue();
      }

      AttributeInstance magicule = player.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         amount += magicule.getBaseValue();
      }

      TensuraCommands.sendSuccess(stack, Component.translatable("tensura.command.ep.get_max", new Object[]{player.getName(), amount}), ChatFormatting.AQUA);
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSpiritualHealth(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("spiritualHealth") String dl,
      @LiteralArg("current") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.spiritual.get", new Object[]{entity.getName(), existence.getSpiritualHealth()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSpiritualHealth(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("spiritualHealth") String dl, @LiteralArg("current") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.spiritual.get", new Object[]{player.getName(), existence.getSpiritualHealth()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMaxSpiritualHealth(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITY) EntitySelector selector,
      @LiteralArg("spiritualHealth") String dl,
      @LiteralArg("max") String current
   ) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         AttributeInstance spiritualHealth = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
         if (spiritualHealth == null) {
            return false;
         }

         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.spiritual.get_max", new Object[]{entity.getName(), spiritualHealth.getBaseValue()}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMaxSpiritualHealth(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("spiritualHealth") String dl, @LiteralArg("max") String current
   ) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      AttributeInstance spiritualHealth = player.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
      if (spiritualHealth == null) {
         return false;
      }

      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.spiritual.get_max", new Object[]{player.getName(), spiritualHealth.getBaseValue()}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSeverance(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("severance") String dl) throws CommandSyntaxException {
      if (selector.findSingleEntity(stack) instanceof LivingEntity entity) {
         IEffect effect = TensuraStorages.getEffectFrom(entity);
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.severance.get", new Object[]{entity.getName(), effect.getSeveranceAmount()}), ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSeverance(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("severance") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IEffect effect = TensuraStorages.getEffectFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.severance.get", new Object[]{player.getName(), effect.getSeveranceAmount()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getSleepMode(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITY) EntitySelector selector, @LiteralArg("sleepMode") String dl) throws CommandSyntaxException {
      Entity selected = selector.findSingleEntity(stack);
      if (selected instanceof LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         TensuraCommands.sendSuccess(
            stack,
            Component.translatable("tensura.command.sleep_mode.get", new Object[]{selected.getName(), existence.getSleepModeTime()}),
            ChatFormatting.AQUA
         );
         return true;
      } else {
         return false;
      }
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getSleepMode(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("sleepMode") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.sleep_mode.get", new Object[]{player.getName(), existence.getSleepModeTime()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getMaxWarp(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYER) EntitySelector selector, @LiteralArg("maxWarp") String dl) throws CommandSyntaxException {
      Player selected = selector.findSinglePlayer(stack);
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(selected);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.max_warp.get", new Object[]{selected.getName(), data.getMaxWarpPoints()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getMaxWarp(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("maxWarp") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.max_warp.get", new Object[]{player.getName(), data.getMaxWarpPoints()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getBonusSkillLock(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYER) EntitySelector selector, @LiteralArg("bonusSkillLock") String dl
   ) throws CommandSyntaxException {
      Player selected = selector.findSinglePlayer(stack);
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(selected);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.reset_counter.bonus_lock.get", new Object[]{selected.getName(), data.getBonusSkillLock()}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getBonusSkillLock(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("bonusSkillLock") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable("tensura.command.reset_counter.bonus_lock.get", new Object[]{player.getName(), data.getBonusSkillLock()}),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getReputation(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYER) EntitySelector selector, @LiteralArg("reputation") String dl) throws CommandSyntaxException {
      Player selected = selector.findSinglePlayer(stack);
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(selected);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable(
            "tensura.command.reputation.get", new Object[]{selected.getName(), data.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get())}
         ),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getReputation(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("reputation") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      TensuraCommands.sendSuccess(
         stack,
         Component.translatable(
            "tensura.command.reputation.get", new Object[]{player.getName(), data.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get())}
         ),
         ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean getResetCounter(@SenderArg CommandSourceStack stack, @EntityArg(Type.PLAYER) EntitySelector selector, @LiteralArg("resetCounter") String dl) throws CommandSyntaxException {
      Player selected = selector.findSinglePlayer(stack);
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(selected);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.reset_counter.get", new Object[]{selected.getName(), data.getResetCounter()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean getResetCounter(@SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("resetCounter") String dl) {
      Player player = stack.getPlayer();
      if (player == null) {
         return false;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      TensuraCommands.sendSuccess(
         stack, Component.translatable("tensura.command.reset_counter.get", new Object[]{player.getName(), data.getResetCounter()}), ChatFormatting.AQUA
      );
      return true;
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_others", permissionLevel = PermissionLevel.MODERATOR)
   public boolean checkResetCounter(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYER) EntitySelector selector,
      @LiteralArg("resetCounter") String dl,
      @LiteralArg("check") String check
   ) throws CommandSyntaxException {
      return this.checkResetCounter(stack, selector.findSinglePlayer(stack));
   }

   @Execute
   @Permission(value = "tensura.command.get_stat_self", permissionLevel = PermissionLevel.PLAYER)
   public boolean checkResetCounter(
      @SenderArg CommandSourceStack stack, @LiteralArg("self") String self, @LiteralArg("resetCounter") String dl, @LiteralArg("check") String check
   ) {
      ServerPlayer player = stack.getPlayer();
      return player == null ? false : this.checkResetCounter(stack, player);
   }

   private boolean checkResetCounter(CommandSourceStack stack, ServerPlayer player) {
      PlayerConfig.ResetScroll CONFIG = ReincarnationMenu.PLAYER_CONFIG.ResetScroll;
      MutableComponent component = Component.translatable("tensura.command.reset_counter.check", new Object[]{player.getName()}).withStyle(ChatFormatting.AQUA);
      boolean checkAll = true;
      if (CONFIG.raceCounter) {
         component.append("\n");
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         boolean met = optional.isPresent() && optional.get().getNextEvolutions(player).isEmpty();
         Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
         component.append(
            Component.translatable("tensura.command.reset_counter.check.race", new Object[]{metComponent})
               .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
         );
         if (!met) {
            checkAll = false;
         }
      }

      if (CONFIG.awakenCounter) {
         component.append("\n");
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         boolean met = existence.isTrueDemonLord() || existence.isTrueHero();
         Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
         component.append(
            Component.translatable("tensura.command.reset_counter.check.awakening", new Object[]{metComponent})
               .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
         );
         if (!met) {
            checkAll = false;
         }
      }

      for (String string : CONFIG.bossesCounter) {
         EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(string));
         if (entityType != null) {
            component.append("\n");
            boolean met = player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get(entityType)) > 0;
            Component metComponent = Component.translatable(met ? "tensura.message.enabled" : "tensura.message.disabled");
            component.append(
               Component.translatable("tensura.command.reset_counter.check.boss", new Object[]{entityType.getDescription(), metComponent})
                  .withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
            if (!met) {
               checkAll = false;
            }
         }
      }

      if (checkAll) {
         TensuraCommands.sendSuccess(
            stack, Component.translatable("tensura.command.reset_counter.check.met", new Object[]{player.getName()}), ChatFormatting.GREEN
         );
      } else {
         TensuraCommands.sendSuccess(stack, component);
      }

      return true;
   }
}
