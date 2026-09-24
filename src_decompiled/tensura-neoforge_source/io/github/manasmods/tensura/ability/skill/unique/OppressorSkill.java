package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.GravityManipulationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public class OppressorSkill extends Skill {
   private static final UniqueSkillConfig.Oppressor CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Oppressor;

   public OppressorSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 4 : mode - 1;
      } else {
         return mode == 4 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "oppressor.repel";
         case 1 -> "oppressor.attract";
         case 2 -> "oppressor.oppress";
         case 3 -> "oppressor.bleve";
         case 4 -> "oppressor.flicker";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostRepel;
         case 1 -> CONFIG.magiculeCostAttract;
         case 2 -> CONFIG.magiculeCostOppress;
         case 3 -> CONFIG.magiculeCostBleve;
         case 4 -> CONFIG.magiculeCostFlicker;
         default -> 0.0;
      };
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         SkillHelper.learnSkill(entity, ((GravityManipulationSkill)ExtraSkills.GRAVITY_MANIPULATION.get()).createLearningInstance(entity));
      }
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource source, float amount) {
      if (!instance.isToggled() || !instance.isMastered(entity)) {
         return true;
      } else {
         return source.is(DamageTypeTags.IS_FALL) ? false : !(source.getDirectEntity() instanceof AbstractArrow);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 3 && mode != 2) {
         CompoundTag tag = instance.getOrCreateTag();
         switch (mode) {
            case 0:
               Entity targetx = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.repelRange, 0.75, false, true, true);
               if (targetx != null) {
                  TensuraParticleHelper.addServerParticlesAroundSelf(targetx, ParticleTypes.PORTAL, 1.0);
               }
               break;
            case 1:
               Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.attractRange, 0.75, false, true, true);
               if (target != null) {
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
               }
         }

         if (!tag.getBoolean("masteryPower")) {
            tag.putInt("scale", heldTicks);
         }

         if (tag.getInt("scale") > CONFIG.maxScale * 10) {
            tag.putInt("scale", CONFIG.maxScale * 10);
         }

         instance.markDirty();
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.power_scale", new Object[]{tag.getInt("scale") / 10.0})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)),
               true
            );
         }

         return true;
      } else {
         return false;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      switch (mode) {
         case 2:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(LivingEntity.class, entity, CONFIG.oppressRange, 0.0, false);
            if (target == null) {
               if (entity instanceof Player owner) {
                  owner.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
               }

               return;
            }

            if (target instanceof Player player && player.getAbilities().invulnerable) {
               if (entity instanceof Player owner) {
                  owner.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
               }

               return;
            }

            if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OPPRESSION))) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode, 2.0F)) {
                  return;
               }

               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.oppressCooldownMastered : CONFIG.oppressCooldown, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
               DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.GRAVITY_PRESS, mode);
               target.hurt(source, instance.isMastered(entity) ? CONFIG.oppressDamageMastered : CONFIG.oppressDamage);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            } else {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               TensuraDamageHelper.markHurt(target, entity);
               MobEffectInstance oppression = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.OPPRESSION), CONFIG.oppressDuration, 0, false, false, false
               );
               TensuraMobEffect.addEffect(target, oppression, entity, this, mode);
               target.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), CONFIG.oppressDuration, CONFIG.oppressBurden - 1, true, false, true
                  )
               );
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getRedWave(0.9F, target.getBbWidth() * 3.0F, 0.3F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.33,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getRedWave(0.9F, target.getBbWidth() * 3.0F, 0.3F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.66,
                  target.getZ()
               );
            }
            break;
         case 3:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(LivingEntity.class, entity, CONFIG.bleveRange, 0.2, false);
            if (target == null) {
               if (entity instanceof Player owner) {
                  owner.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
               }

               return;
            }

            if (target instanceof Player player && player.getAbilities().invulnerable) {
               if (entity instanceof Player owner) {
                  owner.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
               }

               return;
            }

            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.bleveCooldownMastered : CONFIG.bleveCooldown, mode);
            DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.GRAVITY_EXPLODE, mode);
            if (target.hurt(source, instance.isMastered(entity) ? CONFIG.bleveDamageMastery : CONFIG.bleveDamage)) {
               Changeable<Vec3> changeable = Changeable.of(target.getDeltaMovement().add(0.0, 2.0, 0.0));
               if (!target.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
                  if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                     .move(target, entity, instance, changeable)
                     .isFalse()) {
                     target.setDeltaMovement((Vec3)changeable.get());
                  }

                  target.hurtMarked = true;
               }
            }

            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.EXPLOSION_EMITTER, 1.0);
            entity.swing(InteractionHand.MAIN_HAND, true);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (TickingSkill.isTickingSkill(entity, this)) {
         CompoundTag tag = instance.getOrCreateTag();
         switch (mode) {
            case 0:
               if (tag.getInt("scale") <= 0) {
                  return;
               }

               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.repelRange, 0.5, false, true, true);
                  if (target != null) {
                     if (target.getType().is(TensuraEntityTags.NO_FORCED_MOVE) || target instanceof Player player && player.getAbilities().invulnerable) {
                        if (entity instanceof Player player) {
                           player.displayClientMessage(
                              Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true
                           );
                        }

                        this.resetScale(instance, entity);
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                        return;
                     }

                     double scale = tag.getInt("scale") / -15.0;
                     Changeable<Vec3> changeable = Changeable.of(entity.getLookAngle().normalize().scale(-scale));
                     if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                        .move(target, entity, instance, changeable)
                        .isFalse()) {
                        if (entity instanceof Player player) {
                           player.displayClientMessage(
                              Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true
                           );
                        }

                        this.resetScale(instance, entity);
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                        return;
                     }

                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_OFF, 1.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_OFF, 1.0);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     target.setDeltaMovement((Vec3)changeable.get());
                     target.hurtMarked = true;
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     if (scale >= 2.0) {
                        instance.addMasteryPoint(entity);
                     }
                  }
               }

               this.resetScale(instance, entity);
               break;
            case 1:
               if (tag.getInt("scale") <= 0) {
                  return;
               }

               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.attractRange, 0.5, false, true, true);
                  if (target != null) {
                     if (target.getType().is(TensuraEntityTags.NO_FORCED_MOVE) || target instanceof Player player && player.getAbilities().invulnerable) {
                        if (entity instanceof Player player) {
                           player.displayClientMessage(
                              Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true
                           );
                        }

                        this.resetScale(instance, entity);
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                        return;
                     }

                     double scale = tag.getInt("scale") / 15.0;
                     Vec3 vec3 = new Vec3(entity.getX() - target.getX(), entity.getY() - target.getY(), entity.getZ() - target.getZ())
                        .scale(1.0 / target.distanceTo(entity));
                     Changeable<Vec3> changeable = Changeable.of(vec3.normalize().scale(scale));
                     if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                        .move(target, entity, instance, changeable)
                        .isFalse()) {
                        if (entity instanceof Player player) {
                           player.displayClientMessage(
                              Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true
                           );
                        }

                        this.resetScale(instance, entity);
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                        return;
                     }

                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_OFF, 1.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_OFF, 1.0);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     target.setDeltaMovement((Vec3)changeable.get());
                     target.hurtMarked = true;
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     if (scale >= 2.0) {
                        instance.addMasteryPoint(entity);
                     }
                  }
               }

               this.resetScale(instance, entity);
            case 2:
            case 3:
            default:
               break;
            case 4:
               if (tag.getInt("scale") <= 0) {
                  return;
               }

               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  float scale = tag.getInt("scale") / 10.0F;
                  if (scale >= 2.0F) {
                     instance.addMasteryPoint(entity);
                  }

                  if (entity.getVehicle() != null) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity.getVehicle(), ParticleTypes.EXPLOSION_EMITTER, 0.5);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     SkillHelper.riptidePushVehicle(entity.getVehicle(), entity, scale);
                     entity.getVehicle().hurtMarked = true;
                  } else {
                     SkillHelper.riptidePush(entity, scale);
                     entity.hurtMarked = true;
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER, 1.0);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                  }
               }

               this.resetScale(instance, entity);
         }
      }
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer owner, boolean conqueredEnd) {
      if (!conqueredEnd) {
         instance.getOrCreateTag().putBoolean("masteryPower", false);
         instance.markDirty();
      }
   }

   @Override
   public void onNumberKeyPress(ManasSkillInstance instance, Player player, int keyNumber) {
      if (instance.isMastered(player)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (keyNumber == 0) {
            if (tag.getBoolean("masteryPower")) {
               tag.putBoolean("masteryPower", false);
               tag.putInt("scale", 10);
               instance.markDirty();
            }
         } else {
            if (!tag.getBoolean("masteryPower")) {
               tag.putBoolean("masteryPower", true);
            }

            tag.putInt("scale", keyNumber * 10 + 10);
            instance.markDirty();
         }
      }
   }

   private void resetScale(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         tag.putInt("scale", 0);
         instance.markDirty();
      }
   }
}
