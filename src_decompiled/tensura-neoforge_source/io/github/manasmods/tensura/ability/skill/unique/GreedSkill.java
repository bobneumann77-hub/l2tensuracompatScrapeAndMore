package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;

public class GreedSkill extends Skill {
   private static final UniqueSkillConfig.Greed CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Greed;
   protected static final ResourceLocation GREED = ResourceLocation.fromNamespaceAndPath("tensura", "greed");

   public GreedSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public int getMaxMastery() {
      return SKILL_CONFIG.Mastery.masteryUniqueSin;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "greed.spiritual";
         case 1 -> "greed.flare";
         case 2 -> "greed.death";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      if (mode == 2) {
         return CONFIG.magiculeCostWish;
      } else {
         return mode == 1 ? CONFIG.magiculeCostFlare : 0.0;
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      return !tag.contains("target") && tag.getInt("heldSeconds") == 0 ? false : !TickingSkill.isTickingSkill(entity, this);
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.remove("target");
      tag.putInt("heldSeconds", 0);
      tag.putInt("targetDesire", 0);
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack == null) {
         return true;
      } else {
         AttributeModifier modifier = attack.getModifier(GREED);
         if (modifier == null) {
            return true;
         } else if (source.getEntity() != entity) {
            return true;
         } else if (!TensuraDamageHelper.isPhysicalAttack(source)) {
            return true;
         } else if (EnergyHelper.isOutOfEnergy(entity, 0.0, modifier.amount() * CONFIG.magiculeCostFlare)) {
            attack.removeModifier(GREED);
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
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            if (entity.isShiftKeyDown()) {
               return false;
            }

            CompoundTag tag = instance.getOrCreateTag();
            int second = tag.getInt("heldSeconds");
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(LivingEntity.class, entity, CONFIG.maxDistance, 0.5, true, true, false);
            if (target != null && (!tag.contains("target") || Objects.equals(tag.getUUID("target"), target.getUUID()))) {
               if (!CharmSkill.canMindControl(target, level)) {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     1.0F
                  );
                  return false;
               }

               if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
                  instance.addMasteryPoint(entity);
               }

               int controlling = target instanceof Player ? CONFIG.playerControl : CONFIG.entityControl;
               double distance = target.distanceTo(entity);
               if (distance < CONFIG.closeDistance) {
                  controlling -= CONFIG.closeControl;
               } else if (distance > CONFIG.farDistance) {
                  controlling += CONFIG.farControl;
               }

               if (heldTicks % 20 == 0) {
                  tag.putInt("heldSeconds", second + 1);
               }

               int desire = tag.getInt("targetDesire");
               if (entity instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.time_held.max", new Object[]{second, controlling - desire})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     true
                  );
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 0.2F, 1.0F);
               }

               if (second >= controlling - desire) {
                  IExistence existence = TensuraStorages.getExistenceFrom(target);
                  if (!Objects.equals(existence.getPermanentOwner(), entity.getUUID())
                     && !((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, false).isFalse()) {
                     existence.setTemporaryOwner(entity.getUUID());
                     if (target instanceof Mob mob) {
                        SubordinateHelper.removeTarget(mob);
                     }

                     if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                        subordinate.tame(player);
                     }

                     entity.swing(InteractionHand.MAIN_HAND, true);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN, 2.0);
                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.5F,
                        1.0F
                     );
                  }

                  tag.putInt("targetDesire", 0);
                  tag.putInt("heldSeconds", 0);
                  tag.remove("target");
               }
            } else if (heldTicks % 20 == 0) {
               tag.putInt("heldSeconds", Math.max(0, second - 1));
               if (tag.getInt("heldSeconds") <= 0) {
                  tag.remove("target");
               }

               if (entity instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.time_held", new Object[]{second}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), true
                  );
               }
            }
            break;
         case 2:
            CompoundTag tag = instance.getOrCreateTag();
            int second = tag.getInt("heldSeconds");
            double range = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(LivingEntity.class, entity, range, 0.5, true, true, false);
            if (target != null && (!tag.contains("target") || Objects.equals(tag.getUUID("target"), target.getUUID()))) {
               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return false;
               }

               if (heldTicks % 20 == 0) {
                  tag.putInt("heldSeconds", second + 1);
               }

               if (second >= CONFIG.deathTime) {
                  tag.putInt("heldSeconds", 0);
                  tag.remove("target");
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return false;
                  }

                  instance.addMasteryPoint(entity);
                  DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.DEATH_WISH, mode).tensura$setDodgeBypass();
                  if (target.hurt(source, target.getMaxHealth() * 10.0F)) {
                     double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                     TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
                     return false;
                  }
               } else {
                  target.addEffect(
                     new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 10, CONFIG.deathInterference - 1, false, false, false
                     ),
                     entity
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WHITE_ASH, 1.0);
                  if (entity instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.time_held.max", new Object[]{second, 10}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                        true
                     );
                  }
               }
            } else if (heldTicks % 20 == 0) {
               tag.putInt("heldSeconds", Math.max(0, second - 1));
               if (tag.getInt("heldSeconds") <= 0) {
                  tag.remove("target");
               }

               if (entity instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.time_held", new Object[]{second}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), true
                  );
               }
            }
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 0:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.maxDistance, false);
            if (target == null) {
               return;
            }

            UUID uuid = entity.getUUID();
            if (entity.isShiftKeyDown()) {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (Objects.equals(existence.getPermanentOwner(), uuid)) {
                  return;
               }

               if (Objects.equals(existence.getTemporaryOwner(), uuid)) {
                  existence.setTemporaryOwner(null);
                  target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
                  if (target instanceof ISubordinate subordinate) {
                     subordinate.resetOwner(existence.getPermanentOwner());
                  }

                  existence.markDirty();
                  if (target instanceof Mob mob) {
                     SubordinateHelper.setWander(mob);
                  }

                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
                  entity.swing(InteractionHand.MAIN_HAND, true);
               }

               return;
            }

            if (target instanceof Player player && player.getAbilities().invulnerable) {
               return;
            }

            if (target.isAlliedTo(entity)) {
               return;
            }

            if (target instanceof Player && TensuraGameRules.noPlayerMindControl(level)) {
               return;
            }

            int desire = 0;
            if (target instanceof ServerPlayer player) {
               desire = player.getStats().getValue(Stats.CUSTOM.get(Stats.TRADED_WITH_VILLAGER)) / CONFIG.playerTradeControl;
            } else if (target instanceof Merchant merchant) {
               desire = merchant.getOffers().size() * CONFIG.mobTradeControl;
            }

            tag.putInt("targetDesire", desire);
            tag.putUUID("target", target.getUUID());
            tag.putInt("heldSeconds", 0);
            break;
         case 1:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.flareRange, false);
            if (target != null && target.isAlive()) {
               if (target.isAlliedTo(entity)) {
                  float multiplier = instance.isMastered(entity) ? CONFIG.flareAllyBuffMastery : CONFIG.flareAllyBuff;
                  float boost = (float)(TensuraStorages.getExistenceFrom(target).getSpiritualHealth() * multiplier);
                  if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostFlareAlly * boost)) {
                     return;
                  }

                  AttributeInstance attack = target.getAttribute(Attributes.ATTACK_DAMAGE);
                  if (attack == null) {
                     return;
                  }

                  AttributeModifier modifier = attack.getModifier(GREED);
                  if (modifier != null) {
                     if (entity.isShiftKeyDown()) {
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
                        attack.removeModifier(GREED);
                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_ON);
                     }
                  } else {
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_OFF);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     AttributeModifier attackModifier = new AttributeModifier(GREED, boost, Operation.ADD_VALUE);
                     attack.addOrReplacePermanentModifier(attackModifier);
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.flareCooldownMastered : CONFIG.flareCooldown, mode);
                  }
               } else {
                  if (target instanceof Player player && player.getAbilities().invulnerable) {
                     return;
                  }

                  float multiplier = instance.isMastered(entity) ? CONFIG.flareAttackMastery : CONFIG.flareAttack;
                  float boost = (float)(TensuraStorages.getExistenceFrom(entity).getSpiritualHealth() * multiplier);
                  if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostFlareAttack * boost)) {
                     return;
                  }

                  DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.DEATH_WISH, mode);
                  if (target.hurt(source, boost)) {
                     instance.addMasteryPoint(entity);
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.flareAttackCooldownMastered : CONFIG.flareAttackCooldown, mode);
                     double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                     if (size <= 0.0) {
                        size = entity.getBbWidth();
                     }

                     TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
                  }
               }
            } else {
               float multiplier = instance.isMastered(entity) ? CONFIG.flareBuffMastery : CONFIG.flareBuff;
               float boost = (float)(TensuraStorages.getExistenceFrom(entity).getSpiritualHealth() * multiplier);
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode, boost)) {
                  return;
               }

               AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
               if (attack == null) {
                  return;
               }

               AttributeModifier modifier = attack.getModifier(GREED);
               if (modifier != null) {
                  if (entity.isShiftKeyDown()) {
                     attack.removeModifier(GREED);
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
                  AttributeModifier attackModifier = new AttributeModifier(GREED, boost, Operation.ADD_VALUE);
                  attack.addOrReplacePermanentModifier(attackModifier);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.flareCooldownMastered : CONFIG.flareCooldown, mode);
                  double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
                  TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            }
            break;
         case 2:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 30.0, false);
            if (target == null) {
               return;
            }

            if (target instanceof Player player && player.getAbilities().invulnerable) {
               return;
            }

            if (target.isAlliedTo(entity)) {
               return;
            }

            if (target instanceof Player && TensuraGameRules.noPlayerMindControl(level)) {
               return;
            }

            tag.putUUID("target", target.getUUID());
            tag.putInt("heldSeconds", 0);
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         AttributeModifier modifier = attack.getModifier(GREED);
         if (modifier != null) {
            attack.removeModifier(GREED);
         }
      }

      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("targetDesire", 0);
      tag.remove("target");
      tag.putInt("heldSeconds", 0);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("targetDesire", 0);
      tag.remove("target");
      tag.putInt("heldSeconds", 0);
   }
}
