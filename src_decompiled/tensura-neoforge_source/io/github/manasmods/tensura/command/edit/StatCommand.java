package io.github.manasmods.tensura.command.edit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.BooleanArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.DoubleArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.FloatArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.primitive.LiteralArg;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.command.TensuraCommands;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

@Command("stat")
@Permission(value = "tensura.command.edit_stat", permissionLevel = PermissionLevel.GAMEMASTER)
public class StatCommand {
   @Execute
   public boolean setMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setMagicule(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.magicule.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean setMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @LiteralArg("max") String m
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            double max = EnergyHelper.getMaxMagicule(entity);
            existence.setMagicule(max);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.magicule.set", new Object[]{entity.getName(), max}));
         }
      }

      return true;
   }

   @Execute
   public boolean addMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("current") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setMagicule(existence.getMagicule() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.magicule.set", new Object[]{entity.getName(), existence.getMagicule()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg("amount") double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (instance != null) {
               instance.setBaseValue(amount);
               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.magicule.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg("amount") double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (instance != null) {
               instance.setBaseValue(amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setMagicule(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.magicule.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg("amount") double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.magicule.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxMagicule(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("magicule") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg("amount") double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setMagicule(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.magicule.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAura(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.aura.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean setAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @LiteralArg("max") String m
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            double max = EnergyHelper.getMaxAura(entity);
            existence.setAura(max);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.aura.set", new Object[]{entity.getName(), max}));
         }
      }

      return true;
   }

   @Execute
   public boolean addAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("current") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAura(existence.getAura() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.aura.set", new Object[]{entity.getName(), existence.getAura()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg("amount") double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (instance != null) {
               instance.setBaseValue(amount);
               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.aura.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg("amount") double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (instance != null) {
               instance.setBaseValue(amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setAura(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.aura.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.aura.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxAura(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("aura") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setAura(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.aura.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSpiritualHealth(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spiritual.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean setSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @LiteralArg("max") String m
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            double max = entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            existence.setSpiritualHealth(max);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spiritual.set", new Object[]{entity.getName(), max}));
         }
      }

      return true;
   }

   @Execute
   public boolean addSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("current") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSpiritualHealth(existence.getSpiritualHealth() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.spiritual.set", new Object[]{entity.getName(), existence.getSpiritualHealth()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            if (instance != null) {
               instance.setBaseValue(amount);
               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spiritual.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            if (instance != null) {
               instance.setBaseValue(amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setSpiritualHealth(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.spiritual.set_max", new Object[]{entity.getName(), amount}));
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.spiritual.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxSpiritualHP(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("spiritualHP") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            if (instance != null) {
               instance.setBaseValue(instance.getBaseValue() + amount);
               if (resetCurrent) {
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  existence.setSpiritualHealth(instance.getValue());
                  existence.markDirty();
               }

               TensuraCommands.sendSuccess(
                  stack, entity, Component.translatable("tensura.command.spiritual.set_max", new Object[]{entity.getName(), instance.getBaseValue()})
               );
            }
         }
      }

      return true;
   }

   @Execute
   public boolean setEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAura(amount / 2.0);
            existence.setMagicule(amount / 2.0);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.ep.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean setEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("current") String current,
      @LiteralArg("set") String set,
      @LiteralArg("max") String max
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAura(EnergyHelper.getMaxAura(entity));
            existence.setMagicule(EnergyHelper.getMaxMagicule(entity));
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.ep.set", new Object[]{entity.getName(), EnergyHelper.getMaxEP(entity)})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean addEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("current") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setAura(existence.getAura() + amount / 2.0);
            existence.setMagicule(existence.getMagicule() + amount / 2.0);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.ep.set", new Object[]{entity.getName(), existence.getEP()}));
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (aura != null) {
               aura.setBaseValue(amount / 2.0);
            }

            AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (magicule != null) {
               magicule.setBaseValue(amount / 2.0);
            }

            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.ep.set_max", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("max") String current,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (aura != null) {
               aura.setBaseValue(amount / 2.0);
            }

            AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (magicule != null) {
               magicule.setBaseValue(amount / 2.0);
            }

            if (resetCurrent) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setAura(EnergyHelper.getMaxAura(entity));
               existence.setMagicule(EnergyHelper.getMaxMagicule(entity));
               existence.markDirty();
            }

            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.ep.set_max", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (aura != null) {
               aura.setBaseValue(aura.getBaseValue() + amount / 2.0);
            }

            AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (magicule != null) {
               magicule.setBaseValue(magicule.getBaseValue() + amount / 2.0);
            }

            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.ep.set_max", new Object[]{entity.getName(), EnergyHelper.getMaxEP(entity)})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean addMaxEp(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("ep") String l,
      @LiteralArg("max") String current,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -Double.MAX_VALUE) double amount,
      @BooleanArg("resetCurrent") boolean resetCurrent
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (aura != null) {
               aura.setBaseValue(aura.getBaseValue() + amount / 2.0);
            }

            AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (magicule != null) {
               magicule.setBaseValue(magicule.getBaseValue() + amount / 2.0);
            }

            if (resetCurrent) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setAura(EnergyHelper.getMaxAura(entity));
               existence.setMagicule(EnergyHelper.getMaxMagicule(entity));
               existence.markDirty();
            }

            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.ep.set_max", new Object[]{entity.getName(), EnergyHelper.getMaxEP(entity)})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setSeverance(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("severance") String l,
      @LiteralArg("set") String set,
      @FloatArg(value = "amount", min = -Float.MAX_VALUE) float amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IEffect effect = TensuraStorages.getEffectFrom(entity);
            if (amount != 0.0F) {
               int removeSec = TensuraSkill.BASE_CONFIG.Misc.severanceRemoveSec;
               if (effect.getSeveranceRemoveTime() < removeSec) {
                  effect.setSeveranceRemoveTime(removeSec);
               }
            } else {
               effect.setSeveranceRemoveTime(0);
            }

            effect.setSeveranceAmount(amount);
            effect.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.severance.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean clearSeverance(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("severance") String l,
      @LiteralArg("clear") String set
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IEffect effect = TensuraStorages.getEffectFrom(entity);
            effect.setSeveranceAmount(0.0F);
            effect.setSeveranceRemoveTime(0);
            effect.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.severance.set", new Object[]{entity.getName(), 0}));
         }
      }

      return true;
   }

   @Execute
   public boolean setResetCounter(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("resetCounter") String l,
      @LiteralArg("set") String set,
      @IntegerArg(value = "amount", min = 0) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setResetCounter(amount);
         data.markDirty();
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reset_counter.set", new Object[]{player.getName(), amount}));
      }

      return true;
   }

   @Execute
   public boolean addResetCounter(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("resetCounter") String l,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setResetCounter(data.getResetCounter() + amount);
         data.markDirty();
         TensuraCommands.sendSuccess(
            stack, player, Component.translatable("tensura.command.reset_counter.set", new Object[]{player.getName(), data.getResetCounter()})
         );
      }

      return true;
   }

   @Execute
   public boolean setBonusLock(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("bonusSkillLock") String l,
      @LiteralArg("set") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setBonusSkillLock(amount);
         data.markDirty();
         TensuraCommands.sendSuccess(
            stack, player, Component.translatable("tensura.command.reset_counter.bonus_lock.set", new Object[]{player.getName(), amount})
         );
      }

      return true;
   }

   @Execute
   public boolean addBonusLock(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("bonusSkillLock") String l,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setBonusSkillLock(data.getBonusSkillLock() + amount);
         data.markDirty();
         TensuraCommands.sendSuccess(
            stack, player, Component.translatable("tensura.command.reset_counter.bonus_lock.set", new Object[]{player.getName(), data.getBonusSkillLock()})
         );
      }

      return true;
   }

   @Execute
   public boolean setSleepMode(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("sleepMode") String l,
      @LiteralArg("set") String set,
      @IntegerArg(value = "amount", min = 0) int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSleepModeTime(amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(stack, entity, Component.translatable("tensura.command.sleep_mode.set", new Object[]{entity.getName(), amount}));
         }
      }

      return true;
   }

   @Execute
   public boolean addSleepMode(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.ENTITIES) EntitySelector selector,
      @LiteralArg("sleepMode") String l,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Entity target : selector.findEntities(stack)) {
         if (target instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            existence.setSleepModeTime(existence.getSleepModeTime() + amount);
            existence.markDirty();
            TensuraCommands.sendSuccess(
               stack, entity, Component.translatable("tensura.command.sleep_mode.set", new Object[]{entity.getName(), existence.getEP()})
            );
         }
      }

      return true;
   }

   @Execute
   public boolean setMaxWarpCount(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("maxWarp") String l,
      @LiteralArg("set") String set,
      @IntegerArg(value = "amount", min = 0) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setMaxWarpPoints(amount);
         data.markDirty();
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.max_warp.set", new Object[]{player.getName(), amount}));
      }

      return true;
   }

   @Execute
   public boolean addMaxWarpCount(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("maxWarp") String l,
      @LiteralArg("add") String set,
      @IntegerArg(value = "amount", min = -2147483647) int amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setMaxWarpPoints(data.getMaxWarpPoints() + amount);
         data.markDirty();
         TensuraCommands.sendSuccess(
            stack, player, Component.translatable("tensura.command.max_warp.set", new Object[]{player.getName(), data.getResetCounter()})
         );
      }

      return true;
   }

   @Execute
   public boolean setReputation(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("reputation") String l,
      @LiteralArg("set") String set,
      @DoubleArg(value = "amount", min = -1024.0) double amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setReputation((EntityType<?>)HumanEntityTypes.DWARF.get(), amount);
         data.markDirty();
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reputation.set", new Object[]{player.getName(), amount}));
      }

      return true;
   }

   @Execute
   public boolean addReputation(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("reputation") String l,
      @LiteralArg("add") String set,
      @DoubleArg(value = "amount", min = -2.147483647E9) double amount
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.setReputation((EntityType<?>)HumanEntityTypes.DWARF.get(), data.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get()) + amount);
         data.markDirty();
         TensuraCommands.sendSuccess(
            stack,
            player,
            Component.translatable(
               "tensura.command.reputation.set", new Object[]{player.getName(), data.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get())}
            )
         );
      }

      return true;
   }

   @Execute
   public boolean resetReputation(
      @SenderArg CommandSourceStack stack,
      @EntityArg(Type.PLAYERS) EntitySelector selector,
      @LiteralArg("reputation") String l,
      @LiteralArg("reset") String set
   ) throws CommandSyntaxException {
      for (Player player : selector.findPlayers(stack)) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         data.resetReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
         data.markDirty();
         TensuraCommands.sendSuccess(stack, player, Component.translatable("tensura.command.reputation.set", new Object[]{player.getName(), 0}));
      }

      return true;
   }
}
