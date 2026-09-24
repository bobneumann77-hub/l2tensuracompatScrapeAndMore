package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BodyDoubleSkill extends Skill implements ICloning {
   private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
   public static final ExtraSkillConfig.BodyDouble CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).BodyDouble;

   public BodyDoubleSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.cloneEP / 5.0;
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (EnergyHelper.getMaxEP(entity) < CONFIG.epAcquirement) {
         return false;
      } else if (SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.DOPPELGANGER.get())) {
         return true;
      } else {
         return entity instanceof ServerPlayer player
            ? player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)MonsterEntityTypes.IFRIT.get())) > 0
            : false;
      }
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
         case 0 -> "body_double.creation";
         case 1 -> "body_double.control";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getTag() == null ? false : instance.getTag().contains("Original");
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("Original")) {
         Holder<MobEffect> fragility = TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY);
         Holder<MobEffect> energyBlockade = TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE);
         MobEffectInstance existingFragility = entity.getEffect(fragility);
         if (existingFragility == null || existingFragility.getDuration() < 40) {
            entity.addEffect(new MobEffectInstance(fragility, 200, CONFIG.cloneFragility - 1, true, false, true));
         }

         MobEffectInstance existingBlockade = entity.getEffect(energyBlockade);
         if (existingBlockade == null || existingBlockade.getDuration() < 40) {
            entity.addEffect(new MobEffectInstance(energyBlockade, 200, 4, false, false, false));
         }

         if (!entity.hasInfiniteMaterials()) {
            if (entity.level() instanceof ServerLevel level) {
               UUID var15 = tag.getUUID("Original");
               Entity body = ObjectSelectionHelper.getEntityFromUUID(level, var15, clone -> clone instanceof CloneEntity);
               double bodyRadius = CONFIG.originalBodyRadius;
               if (!(body instanceof CloneEntity) || !body.isAlive() || body.level() != level || body.distanceToSqr(entity) > bodyRadius * bodyRadius) {
                  DamageSource source = TensuraDamageTypes.getDamageSource(level, TensuraDamageTypes.ENERGY_SOURCE_LOST).tensura$setBarrierBypassLevel(3.0F);
                  entity.hurt(source, entity.getMaxHealth() * CONFIG.originalBodyDamage);
                  TensuraDamageHelper.directSpiritualHurt(
                     entity, null, source, (float)entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.originalBodyDamage, 0.0F
                  );
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.skill.mode.body_double.main_too_far").withStyle(ChatFormatting.RED), true);
                     player.playNotifySound(SoundEvents.THORNS_HIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (existence.isSpiritualForm()) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
      } else {
         Level level = entity.level();
         if (mode != 0) {
            CloneEntity clone = ObjectSelectionHelper.getTargetingEntity(CloneEntity.class, entity, 50.0, 0.2, false);
            if (clone != null) {
               if (clone.isOwnedBy(entity)) {
                  if (clone.isAlive() && clone.getSkill().getSkill() == this) {
                     if (entity.isShiftKeyDown()) {
                        if (!clone.isStatic()) {
                           DamageSource source = TensuraDamageTypes.getDamageSource(level, TensuraDamageTypes.ENERGY_SOURCE_LOST)
                              .tensura$setBarrierBypassLevel(3.0F);
                           clone.hurt(source, clone.getMaxHealth());
                           entity.level()
                              .playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.0F,
                                 1.0F
                              );
                        }
                     } else {
                        double MP = EnergyHelper.getBaseMaxMagicule(entity);
                        CloneEntity newClone = summonClone(instance, entity, level, MP, existence.getMagicule(), entity.position());
                        CloneEntity.copyEffects(entity, newClone);
                        newClone.setHealth(entity.getHealth());
                        entity.setHealth(clone.getHealth());
                        existence.setMagicule(TensuraStorages.getExistenceFrom(clone).getMagicule());
                        existence.markDirty();
                        if (entity instanceof ServerPlayer player) {
                           player.teleportTo((ServerLevel)level, clone.position().x, clone.position().y, clone.position().z, clone.getYRot(), clone.getXRot());
                           player.hurtMarked = true;
                        }

                        if (entity instanceof Player player) {
                           newClone.copyInventory(player);
                           clone.copyInventoryOntoOwner(player, true);
                        } else {
                           newClone.copyEquipments(entity);
                           clone.copyEquipmentsOntoOwner(entity, true);
                        }

                        CloneEntity.copyRotation(entity, newClone);
                        CloneEntity.copyEffects(clone, entity);
                        clone.remove();
                        CompoundTag tag = instance.getOrCreateTag();
                        if (tag.contains("Original")) {
                           if (Objects.equals(clone.getUUID(), tag.getUUID("Original"))) {
                              tag.remove("Original");
                           }
                        } else {
                           newClone.setStatic(true);
                           tag.putUUID("Original", newClone.getUUID());
                        }

                        instance.markDirty();
                     }
                  }
               }
            }
         } else {
            double EP = EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.cloneEP;
            if (!EnergyHelper.isOutOfEnergy(entity, 0.0, EP)) {
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 5.0);
               CloneEntity clone = summonClone(instance, entity, level, EP, existence.getMagicule() * CONFIG.cloneEP, result.getLocation());
               CloneEntity.copyEffects(entity, clone);

               for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
                  clone.addFakeItem(slot, entity.getItemBySlot(slot).copy());
               }
            }
         }
      }
   }

   public static CloneEntity summonClone(ManasSkillInstance instance, LivingEntity entity, Level level, double EP, double currentEP, Vec3 position) {
      return summonClone(instance, entity, level, EP, currentEP, CloneEntity.CopySkill.ALL, position);
   }

   public static CloneEntity summonClone(
      ManasSkillInstance instance, LivingEntity entity, Level level, double EP, double currentEP, CloneEntity.CopySkill copySkill, Vec3 position
   ) {
      EntityType<CloneEntity> type = (EntityType<CloneEntity>)HumanEntityTypes.CLONE.get();
      CloneEntity clone = new CloneEntity(type, level);
      if (entity instanceof Player player) {
         clone.tame(player);
      }

      clone.setSkill(instance);
      clone.copyStatsAndSkills(entity, copySkill, true);
      CloneEntity.copyStatusEffect(entity, clone, copySkill, false);
      clone.setHealth(clone.getMaxHealth());
      EnergyHelper.setMaxMagicule(clone, EP);
      EnergyHelper.setMaxAura(clone, 50.0);
      IExistence existence = TensuraStorages.getExistenceFrom(clone);
      existence.setMagicule(Math.min(currentEP, EP));
      existence.markDirty();
      clone.setPos(position);
      CloneEntity.copyRotation(entity, clone);
      level.addFreshEntity(clone);
      level.playSound(
         null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
      );
      double size = clone.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(clone, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
      return clone;
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
      if (subordinate instanceof CloneEntity clone) {
         if (clone.getSkill().getSkill() == this) {
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.contains("Original")) {
               UUID uuid = tag.getUUID("Original");
               if (Objects.equals(clone.getUUID(), uuid)) {
                  tag.remove("Original");
                  instance.markDirty();
                  owner.hurt(source.tensura$setBarrierBypassLevel(3.0F).tensura$setResistanceBypassLevel(2.0F), owner.getMaxHealth() * 10.0F);
                  if (owner.isAlive() && owner instanceof ServerPlayer player) {
                     player.teleportTo((ServerLevel)owner.level(), clone.position().x, clone.position().y, clone.position().z, clone.getYRot(), clone.getXRot());
                     player.hurtMarked = true;
                  }

                  double size = owner.getAttributeValue(Attributes.SCALE) * 4.0;
                  TensuraParticleHelper.addServerAuraParticles(owner, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
                  return;
               }
            }

            if (!clone.isStatic()) {
               IExistence existence = TensuraStorages.getExistenceFrom(clone);
               EnergyHelper.gainMagicule(owner, existence.getMagicule(), EnergyHelper.GainType.NORMAL);
               existence.setSkippingEPDrop(true);
               existence.markDirty();
               double size = clone.getAttributeValue(Attributes.SCALE) * 4.0;
               TensuraParticleHelper.addServerAuraParticles(clone, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
               if (owner instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.ep.acquire_mp", new Object[]{existence.getMagicule()})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     false
                  );
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
               }
            }
         }
      }
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      if (entity.isAlive()) {
         return true;
      } else {
         return !(entity instanceof Player) ? true : !transferToMainBody(instance, entity, source);
      }
   }

   public static boolean transferToMainBody(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      if (entity.level() instanceof ServerLevel level) {
         CompoundTag tag = instance.getOrCreateTag();
         if (!tag.contains("Original")) {
            return false;
         }

         UUID uuid = tag.getUUID("Original");
         tag.remove("Original");
         if (ObjectSelectionHelper.getEntityFromUUID(level, uuid, clonex -> clonex instanceof CloneEntity) instanceof CloneEntity body) {
            if (!body.isRemoved() && !(body.getHealth() <= 0.0F)) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setMagicule(TensuraStorages.getExistenceFrom(body).getMagicule());
               existence.markDirty();
               entity.setHealth(Math.max(body.getHealth(), 1.0F));
               double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
               TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               CloneEntity clone = summonClone(instance, entity, level, 100.0, 100.0, entity.position());
               if (entity instanceof Player player) {
                  clone.copyInventory(player);
               } else {
                  clone.copyEquipments(entity);
               }

               CloneEntity.copyStatusEffect(entity, clone, CloneEntity.CopySkill.INTRINSIC, true);
               CloneEntity.copyEffects(entity, clone);
               TensuraStorages.resetEffect(entity);
               instance.addMasteryPoint(entity);
               boolean keepInv = level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
               if (!keepInv) {
                  clone.die(source);
                  clone.remove();
               }

               SpawnPointHelper.teleportToAcrossDimensions(
                  entity, body.level().dimension(), body.getX(), body.getY(), body.getZ(), body.getYRot(), body.getXRot()
               );
               if (entity instanceof Player player) {
                  body.copyInventoryOntoOwner(player, true);
               } else {
                  body.copyEquipmentsOntoOwner(entity, true);
               }

               CloneEntity.copyStatusEffect(entity, body, CloneEntity.CopySkill.INTRINSIC, true);
               CloneEntity.copyEffects(body, entity);
               body.remove();
               if (keepInv) {
                  SpawnPointHelper.teleportToAcrossDimensions(
                     clone, level.dimension(), entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot()
                  );
                  clone.remove();
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer owner, boolean conqueredEnd) {
      if (!conqueredEnd) {
         instance.getOrCreateTag().remove("Original");
         instance.markDirty();
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      if (TensuraStorages.getExistenceFrom(clone).getSleepModeTime() > 0) {
         clone.remove();
      } else {
         Holder<MobEffect> fragility = TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY);
         MobEffectInstance existingFragility = clone.getEffect(fragility);
         if (existingFragility == null || existingFragility.getDuration() < 40) {
            clone.addEffect(new MobEffectInstance(fragility, 200, CONFIG.cloneFragility - 1, false, false, false));
         }

         Holder<MobEffect> energyBlockade = TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE);
         MobEffectInstance existingBlockade = clone.getEffect(energyBlockade);
         if (existingBlockade == null || existingBlockade.getDuration() < 40) {
            clone.addEffect(new MobEffectInstance(energyBlockade, 200, 4, false, false, false));
         }

         if (clone.tickCount % 20 == 0 && clone.getHealth() < clone.getMaxHealth() && clone.isAlive()) {
            clone.heal(CONFIG.cloneHeal);
            EnergyHelper.drainEnergy(clone, null, CONFIG.cloneHealEnergy, false, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NONE);
         }

         if (owner.distanceToSqr(clone) > 2500.0) {
            clone.getNavigation().stop();
         }
      }
   }
}
