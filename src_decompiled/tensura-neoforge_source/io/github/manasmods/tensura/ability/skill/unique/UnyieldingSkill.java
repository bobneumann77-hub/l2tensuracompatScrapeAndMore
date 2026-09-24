package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class UnyieldingSkill extends Skill implements ICloning {
   private static final UniqueSkillConfig.Unyielding CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Unyielding;

   public UnyieldingSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "unyielding.return";
         case 1 -> "unyielding.backup";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      Collection<String> uuidList = entity.level()
         .getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(CONFIG.unyieldingRadius), target -> this.isNamed(entity, target))
         .stream()
         .<String>map(Entity::getStringUUID)
         .collect(Collectors.toList());
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("UnyieldingList")) {
         CompoundTag list = (CompoundTag)tag.get("UnyieldingList");
         if (list == null) {
            return;
         }

         List<String> keyList = List.copyOf(list.getAllKeys());
         if (!keyList.isEmpty()) {
            for (String key : keyList) {
               if (uuidList.contains(key)) {
                  list.putInt(key, list.getInt(key) + (instance.isMastered(entity) ? CONFIG.unyieldingPointGainMastered : CONFIG.unyieldingPointGain));
                  uuidList.remove(key);
               } else {
                  int point = list.getInt(key) - 1;
                  list.putInt(key, point);
                  if (point <= 0) {
                     list.remove(key);
                  }
               }
            }
         }

         if (!uuidList.isEmpty()) {
            for (String key : uuidList) {
               list.putInt(key, Math.min(list.getInt(key) + 10, CONFIG.unyieldingPointEP * 10));
            }
         }

         instance.markDirty();
      } else if (!uuidList.isEmpty()) {
         for (String uuid : uuidList) {
            CompoundTag list = new CompoundTag();
            list.putInt(uuid, 1);
            tag.put("UnyieldingList", list);
         }

         instance.markDirty();
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 6.0, false);
         if (target != null && target.isAlliedTo(entity) && entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.mode.unyielding.check_point", new Object[]{target.getName(), this.getUnyieldingPoint(instance, target)})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)),
               true
            );
         }
      } else if (entity.level() instanceof ServerLevel level) {
         CompoundTag var14 = instance.getOrCreateTag();
         double maxMP = EnergyHelper.getMaxMagicule(entity);
         if (!var14.contains("Backup")) {
            if (!EnergyHelper.isOutOfEnergy(entity, 0.0, maxMP * CONFIG.magiculeBackupCost)) {
               this.spawnBackup(instance, entity, false);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.backupCooldownMastered : CONFIG.backupCooldown, mode);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         } else {
            UUID uuid = var14.getUUID("Backup");
            if (ObjectSelectionHelper.getEntityFromUUID(level, uuid, clone -> clone instanceof CloneEntity) instanceof CloneEntity backup) {
               if (entity.isShiftKeyDown()) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.skill.mode.unyielding.backup_remove").withStyle(ChatFormatting.RED), true);
                  }

                  backup.remove();
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else if (backup.level() != entity.level() && !instance.isMastered(entity)) {
                  entity.sendSystemMessage(Component.translatable("tensura.skill.mode.unyielding.backup_different_dimension").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else if (backup.isAlive()) {
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.backupCooldownMastered : CONFIG.backupCooldown, mode);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  CloneEntity body = this.spawnBackup(instance, entity, true);
                  CloneEntity.copyStatusEffect(entity, body, CloneEntity.CopySkill.INTRINSIC, true);
                  body.setRemainingFireTicks(entity.getRemainingFireTicks());
                  SpawnPointHelper.teleportToAcrossDimensions(
                     entity, backup.level().dimension(), backup.getX(), backup.getY(), backup.getZ(), backup.getYRot(), backup.getXRot()
                  );
                  if (entity instanceof Player player) {
                     backup.copyInventoryOntoOwner(player, true);
                  } else {
                     backup.copyEquipmentsOntoOwner(entity, true);
                  }

                  entity.removeAllEffects();
                  CloneEntity.copyStatusEffect(backup, entity, CloneEntity.CopySkill.INTRINSIC, false);
                  CloneEntity.copyRotation(backup, entity);
                  CloneEntity.copyEffects(backup, entity);
                  entity.setHealth(backup.getHealth());
                  backup.remove();
               }
            } else {
               if (!EnergyHelper.isOutOfEnergy(entity, 0.0, maxMP * CONFIG.magiculeBackupCost)) {
                  this.spawnBackup(instance, entity, false);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.backupCooldownMastered : CONFIG.backupCooldown, mode);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            }
         }
      }
   }

   private CloneEntity spawnBackup(ManasSkillInstance instance, LivingEntity entity, boolean copyEquipment) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      double EP = EnergyHelper.getMaxEP(entity);
      EntityType<CloneEntity> type = (EntityType<CloneEntity>)HumanEntityTypes.CLONE.get();
      CloneEntity clone = new CloneEntity(type, level);
      if (entity instanceof Player player) {
         clone.tame(player);
      }

      clone.setSkill(instance);
      clone.setStatic(true);
      clone.setChunkLoader(true);
      clone.setHealth(entity.getHealth());
      if (copyEquipment) {
         if (entity instanceof Player player) {
            clone.copyInventory(player);
         } else {
            clone.copyEquipments(entity);
         }
      }

      CloneEntity.copyStatusEffect(entity, clone, CloneEntity.CopySkill.INTRINSIC, false);
      clone.copyStatsAndSkills(entity, CloneEntity.CopySkill.NONE, true);
      CloneEntity.copyEffects(entity, clone);
      EnergyHelper.setMaxMagicule(clone, EP);
      EnergyHelper.setMaxAura(clone, 50.0);
      IExistence existence = TensuraStorages.getExistenceFrom(clone);
      existence.setMagicule(EP);
      existence.markDirty();
      clone.setPos(entity.position());
      clone.loadChunkHandler();
      CloneEntity.copyRotation(entity, clone);
      level.addFreshEntity(clone);
      tag.putUUID("Backup", clone.getUUID());
      instance.markDirty();
      return clone;
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      if (!entity.isAlive() && !(entity instanceof CloneEntity)) {
         if (!(entity.level() instanceof ServerLevel level)) {
            return true;
         } else {
            CompoundTag tag = instance.getOrCreateTag();
            if (!tag.contains("Backup")) {
               return true;
            } else {
               UUID uuid = tag.getUUID("Backup");
               if (!(ObjectSelectionHelper.getEntityFromUUID(level, uuid, clone -> clone instanceof CloneEntity) instanceof CloneEntity backup)) {
                  return true;
               } else {
                  if (backup.level() != entity.level() && !instance.isMastered(entity)) {
                     entity.sendSystemMessage(Component.translatable("tensura.skill.mode.unyielding.backup_different_dimension").withStyle(ChatFormatting.RED));
                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                     return true;
                  }

                  instance.addMasteryPoint(entity);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  entity.invulnerableTime = 60;
                  entity.setHealth(backup.getHealth());
                  entity.setRemainingFireTicks(backup.getRemainingFireTicks());
                  TensuraStorages.resetEffect(entity);
                  if (EnergyHelper.getMaxEP(entity) <= 0.0) {
                     EnergyHelper.setMaxAura(entity, 50.0);
                     EnergyHelper.setMaxMagicule(entity, 50.0);
                  }

                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  if (existence.getEP() <= 0.0) {
                     existence.setAura(EnergyHelper.getBaseMaxAura(entity) * 0.5);
                     existence.setMagicule(EnergyHelper.getBaseMaxMagicule(entity) * 0.5);
                     existence.markDirty();
                  }

                  double SHP = entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
                  if (existence.getSpiritualHealth() < SHP * 0.5) {
                     existence.setSpiritualHealth(SHP * 0.5);
                  }

                  existence.markDirty();
                  List<ItemStack> oldInventory = new ArrayList<>();
                  boolean keepInv = level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                  if (keepInv) {
                     if (entity instanceof Player player) {
                        oldInventory.addAll(List.copyOf(player.getInventory().items));
                        oldInventory.addAll(List.copyOf(player.getInventory().armor));
                        oldInventory.addAll(List.copyOf(player.getInventory().offhand));
                     } else {
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                           oldInventory.add(entity.getItemBySlot(slot).copy());
                        }
                     }
                  }

                  if (!keepInv) {
                     CloneEntity clone = this.spawnBackup(instance, entity, false);
                     if (entity instanceof Player player) {
                        clone.copyInventory(player);
                     } else {
                        clone.copyEquipments(entity);
                     }

                     CloneEntity.copyStatusEffect(entity, clone, CloneEntity.CopySkill.INTRINSIC, true);
                     CloneEntity.copyEffects(entity, clone);
                     clone.die(source);
                     clone.remove();
                  } else {
                     for (MobEffectInstance effectInstance : List.copyOf(entity.getActiveEffects())) {
                        if (!effectInstance.getEffect().is(TensuraTags.MobEffects.SPIRITUAL_AFFECTED)) {
                           entity.removeEffect(effectInstance.getEffect());
                        }
                     }
                  }

                  SpawnPointHelper.teleportToAcrossDimensions(
                     entity, backup.level().dimension(), backup.getX(), backup.getY(), backup.getZ(), backup.getYRot(), backup.getXRot()
                  );
                  if (entity instanceof Player player) {
                     backup.copyInventoryOntoOwner(player, true);
                  } else {
                     backup.copyEquipmentsOntoOwner(entity, true);
                  }

                  CloneEntity.copyStatusEffect(entity, backup, CloneEntity.CopySkill.INTRINSIC, true);
                  CloneEntity.copyEffects(backup, entity);
                  backup.remove();
                  if (keepInv) {
                     if (entity instanceof Player player) {
                        for (ItemStack stack : oldInventory) {
                           if (!stack.isEmpty() && !player.addItem(stack)) {
                              player.drop(stack, false);
                           }
                        }
                     } else {
                        for (ItemStack stack : oldInventory) {
                           if (!stack.isEmpty()) {
                              entity.spawnAtLocation(stack);
                           }
                        }
                     }
                  }

                  return false;
               }
            }
         }
      } else {
         return true;
      }
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
      if (!source.tensura$isNotActualDeath()) {
         if (source.getEntity() != owner) {
            if (this.isNamed(owner, subordinate)) {
               int total = this.getUnyieldingPoint(instance, subordinate);
               int unyieldingPoint = Math.min(total / CONFIG.unyieldingPointEP, 10);
               this.removeUnyielding(instance, subordinate);
               if (!(subordinate instanceof Player) || subordinate.level().getLevelData().isHardcore()) {
                  if (unyieldingPoint > 0) {
                     this.addMasteryPoint(instance, subordinate, unyieldingPoint);
                     IExistence existence = TensuraStorages.getExistenceFrom(subordinate);
                     double apGain = Math.min(existence.getAura() * 0.1 * unyieldingPoint, EnergyHelper.CONFIG.maximumEPSteal / 2.0);
                     double mpGain = Math.min(existence.getMagicule() * 0.1 * unyieldingPoint, EnergyHelper.CONFIG.maximumEPSteal / 2.0);
                     if (owner instanceof Player player) {
                        player.displayClientMessage(
                           Component.translatable("tensura.ep.acquire_fallen", new Object[]{apGain + mpGain, subordinate.getName()})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                           false
                        );
                        player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
                     }

                     boolean canGainMax = !(subordinate instanceof Player) || TensuraGameRules.canEpSteal(subordinate.level());
                     if (canGainMax) {
                        EnergyHelper.gainAura(owner, apGain, EnergyHelper.GainType.MAX);
                        EnergyHelper.gainMagicule(owner, mpGain, EnergyHelper.GainType.MAX);
                        existence.setSkippingEPDrop(true);
                        existence.markDirty();
                     }

                     EnergyHelper.gainAura(owner, apGain, EnergyHelper.GainType.NORMAL);
                     EnergyHelper.gainMagicule(owner, mpGain, EnergyHelper.GainType.NORMAL);
                  }

                  if (total >= CONFIG.unyieldingPointSkill) {
                     if (subordinate.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
                        return;
                     }

                     if (subordinate instanceof Player && !subordinate.level().getLevelData().isHardcore()) {
                        return;
                     }

                     List<ManasSkillInstance> list = List.copyOf(SkillAPI.getSkillsFrom(subordinate).getLearnedSkills());
                     if (list.isEmpty()) {
                        return;
                     }

                     for (ManasSkillInstance targetInstance : list) {
                        if (this.canGainSkill(targetInstance, total)) {
                           Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                           if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                              .plunder(subordinate, owner, true, changeable)
                              .isFalse()) {
                              ManasSkillInstance newSkill = ((ManasSkill)changeable.get()).createDefaultInstance();
                              MutableComponent message = Component.translatable(
                                    "tensura.skill.acquire_fallen", new Object[]{newSkill.getChatDisplayName(true), subordinate.getName()}
                                 )
                                 .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                              if (SkillHelper.learnSkill(owner, newSkill, instance.getRemoveTime(), message)) {
                                 subordinate.sendSystemMessage(
                                    Component.translatable(
                                       "tensura.skill.forget.stolen", new Object[]{((ManasSkill)changeable.get()).getChatDisplayName(true), owner.getName()}
                                    )
                                 );
                                 SkillAPI.getSkillsFrom(subordinate).forgetSkill((ManasSkill)changeable.get());
                                 if (owner instanceof Player player) {
                                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int getUnyieldingPoint(ManasSkillInstance instance, LivingEntity target) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!tag.contains("UnyieldingList")) {
         return 0;
      }

      CompoundTag list = (CompoundTag)tag.get("UnyieldingList");
      return list == null ? 0 : list.getInt(target.getStringUUID());
   }

   private void removeUnyielding(ManasSkillInstance instance, LivingEntity target) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("UnyieldingList")) {
         CompoundTag list = (CompoundTag)tag.get("UnyieldingList");
         if (list == null) {
            return;
         }

         list.remove(target.getStringUUID());
      }

      instance.markDirty();
   }

   private boolean isNamed(LivingEntity owner, LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      UUID permanentOwner = existence.getPermanentOwner();
      if (permanentOwner == null) {
         return false;
      } else if (!Objects.equals(permanentOwner, owner.getUUID())) {
         return false;
      } else {
         return existence.getName() == null ? false : !entity.getType().is(TensuraEntityTags.NO_EP_PLUNDER);
      }
   }

   private boolean canGainSkill(ManasSkillInstance instance, int point) {
      if (instance.getSkill() == this) {
         return false;
      } else if (instance.isTemporarySkill() || instance.getMastery() < 0.0) {
         return false;
      } else if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (instance.is(TensuraSkillTags.MAGIC) && !instance.is(TensuraSkillTags.COPIABLE_MAGIC)) {
         return false;
      } else {
         return instance.is(TensuraSkillTags.UNIQUE_SKILLS) ? point >= CONFIG.unyieldingPointSkillUnique : !instance.is(TensuraSkillTags.ULTIMATE_SKILLS);
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      clone.setYRot(owner.getYRot());
      clone.yRotO = clone.getYRot();
      clone.setYRot(clone.getYRot() % 360.0F);
      clone.setXRot(owner.getXRot() % 360.0F);
      clone.yBodyRot = clone.getYRot();
      clone.yHeadRot = clone.yBodyRot;
   }
}
