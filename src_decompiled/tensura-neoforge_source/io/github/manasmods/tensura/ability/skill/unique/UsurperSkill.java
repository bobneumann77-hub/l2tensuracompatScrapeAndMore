package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UsurperSkill extends Skill {
   private static final UniqueSkillConfig.Usurper CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Usurper;

   public UsurperSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 2 : 1;
            case 1 -> 0;
            case 2 -> 1;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> instance.isMastered(entity) ? 2 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "usurper.rob";
         case 1 -> "usurper.copy";
         case 2 -> "usurper.force_takeover";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostRob;
         case 1 -> CONFIG.magiculeCostCopy;
         case 2 -> CONFIG.magiculeCostTakeover;
         default -> 0.0;
      };
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance)) {
         return true;
      }

      if (!owner.getRandom().nextBoolean()) {
         return true;
      }

      if (EnergyHelper.drainEnergy(target, owner, CONFIG.epDrain, true, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL)
         && owner instanceof Player player) {
         player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 10.0, false);
         if (target != null && target.isAlive()) {
            if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
               double EP = EnergyHelper.getMaxEP(entity);
               double targetEP = EnergyHelper.getMaxEP(target);
               entity.swing(InteractionHand.MAIN_HAND, true);
               ServerLevel level = (ServerLevel)entity.level();
               switch (mode) {
                  case 0:
                     if (this.canBypassRob(target)) {
                        entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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
                        return;
                     }

                     if (targetEP > EP) {
                        double chancex = instance.isMastered(entity) ? CONFIG.robSuccessMastered : CONFIG.robSuccess;
                        if (entity.getRandom().nextInt(100) <= chancex) {
                           this.robRandomSkill(instance, entity, target);
                           instance.isMastered(entity);
                           instance.setCoolDown(CONFIG.robCooldown, mode);
                           TensuraDamageHelper.markHurt(target, entity);
                        } else {
                           if (entity instanceof Player player) {
                              player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                           }

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
                        }
                     } else {
                        entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
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
                     }
                     break;
                  case 1:
                     double chance = instance.isMastered(entity) ? CONFIG.copySuccessMastered : CONFIG.copySuccess;
                     if (entity.getRandom().nextInt(100) <= chance) {
                        this.copyRandomSkill(instance, entity, target);
                        instance.addMasteryPoint(entity);
                        instance.setCoolDown(CONFIG.copyCooldown, mode);
                     } else {
                        if (entity instanceof Player player) {
                           player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                        }

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
                     }
                     break;
                  case 2:
                     IExistence existence = TensuraStorages.getExistenceFrom(target);
                     if (existence.getSummoner() != null && existence.getSummonedSecond() > 0) {
                        if (entity.isShiftKeyDown()) {
                           if (!Objects.equals(existence.getPermanentOwner(), entity.getUUID())
                              && Objects.equals(existence.getTemporaryOwner(), entity.getUUID())) {
                              existence.setTemporaryOwner(null);
                              existence.setSummoner(existence.getPermanentOwner());
                              target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
                              if (target instanceof ISubordinate subordinate) {
                                 subordinate.resetOwner(existence.getPermanentOwner());
                              }

                              existence.markDirty();
                              TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
                              level.playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.0F,
                                 1.0F
                              );
                              return;
                           }
                        } else {
                           LivingEntity owner = target;
                           if (SubordinateHelper.getSubordinateOwner(target) instanceof LivingEntity summoner) {
                              owner = summoner;
                           }

                           double ownerEP = EnergyHelper.getMaxEP(owner);
                           if (ownerEP <= EP * CONFIG.takeoverEP) {
                              int duration = instance.isMastered(entity) ? CONFIG.takeoverDurationMastered : CONFIG.takeoverDuration;
                              if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker())
                                 .tame(target, entity, duration != -1)
                                 .isFalse()) {
                                 if (duration != -1) {
                                    MobEffectInstance mindControl = new MobEffectInstance(
                                       TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
                                    );
                                    TensuraMobEffect.addEffect(target, mindControl, entity, instance.getSkill(), mode);
                                    if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                                       return;
                                    }
                                 }

                                 existence.setSummoner(entity.getUUID());
                                 existence.setTemporaryOwner(entity.getUUID());
                                 if (target instanceof Mob mob) {
                                    SubordinateHelper.removeTarget(mob);
                                 }

                                 if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                                    subordinate.tame(player);
                                 }

                                 existence.markDirty();
                                 instance.setCoolDown(CONFIG.takeoverCooldown, mode);
                                 TensuraDamageHelper.markHurt(target, entity);
                                 owner.sendSystemMessage(
                                    Component.translatable("tensura.summon.stolen", new Object[]{target.getName(), entity.getName()})
                                       .withStyle(ChatFormatting.RED)
                                 );
                                 level.playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                                    TensuraSkill.ABILITY_SOUND,
                                    1.0F,
                                    1.0F
                                 );
                              }

                              return;
                           }
                        }
                     }

                     if (this.canBypassRob(target)) {
                        entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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
                        return;
                     }

                     if (targetEP <= EP) {
                        this.robRandomSkill(instance, entity, target);
                        instance.setCoolDown(CONFIG.takeoverCooldown, mode);
                        TensuraDamageHelper.markHurt(target, entity);
                     } else {
                        entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
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
                     }
               }
            }
         }
      }
   }

   public void robRandomSkill(ManasSkillInstance instance, LivingEntity entity, LivingEntity target) {
      Level level = entity.level();
      Skills storage = SkillAPI.getSkillsFrom(entity);
      List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canRob).toList();
      if (collection.isEmpty()) {
         entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.plunder.empty").withStyle(ChatFormatting.RED));
      } else {
         ManasSkill skill = collection.get(target.getRandom().nextInt(collection.size())).getSkill();
         boolean skillSteal = TensuraGameRules.canStealSkill(level);
         if (storage.getSkill(skill).isEmpty()) {
            Changeable<ManasSkill> changeable = Changeable.of(skill);
            if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, skillSteal, changeable).isFalse()) {
               TensuraSkillInstance copy = new TensuraSkillInstance((ManasSkill)changeable.get());
               copy.setMastery(copy.getMaxMastery() * CONFIG.robMastery);
               if (SkillHelper.learnSkill(entity, copy, instance.getRemoveTime())) {
                  if (skillSteal) {
                     target.sendSystemMessage(
                        Component.translatable(
                           "tensura.skill.forget.stolen", new Object[]{((ManasSkill)changeable.get()).getChatDisplayName(true), entity.getName()}
                        )
                     );
                     SkillAPI.getSkillsFrom(target).forgetSkill((ManasSkill)changeable.get());
                  }

                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               } else {
                  entity.sendSystemMessage(
                     Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{copy.getChatDisplayName(true)})
                        .withStyle(ChatFormatting.RED)
                  );
                  entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
               }
            }
         } else {
            Changeable<ManasSkill> changeable = Changeable.of(skill);
            if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, skillSteal, changeable).isFalse()) {
               ManasSkillInstance copy = (ManasSkillInstance)storage.getSkill((ManasSkill)changeable.get()).get();
               double newMastery = copy.getMaxMastery() * CONFIG.robMastery;
               if (copy.getMastery() >= newMastery) {
                  entity.sendSystemMessage(
                     Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{copy.getChatDisplayName(true)})
                        .withStyle(ChatFormatting.RED)
                  );
                  entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
                  return;
               }

               copy.setMastery(newMastery);
               if (skillSteal) {
                  target.sendSystemMessage(
                     Component.translatable(
                        "tensura.skill.forget.stolen", new Object[]{((ManasSkill)changeable.get()).getChatDisplayName(true), entity.getName()}
                     )
                  );
                  SkillAPI.getSkillsFrom(target).forgetSkill((ManasSkill)changeable.get());
               }

               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }
      }
   }

   public void copyRandomSkill(ManasSkillInstance instance, LivingEntity entity, LivingEntity target) {
      Level level = entity.level();
      Skills storage = SkillAPI.getSkillsFrom(entity);
      List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
      if (collection.isEmpty()) {
         entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.plunder.empty").withStyle(ChatFormatting.RED));
      } else {
         ManasSkill skill = collection.get(target.getRandom().nextInt(collection.size())).getSkill();
         if (storage.getSkill(skill).isEmpty()) {
            Changeable<ManasSkill> changeable = Changeable.of(skill);
            if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, false, changeable).isFalse()) {
               TensuraSkillInstance copy = new TensuraSkillInstance((ManasSkill)changeable.get());
               copy.setMastery(copy.getMaxMastery() * CONFIG.copyMastery);
               if (SkillHelper.learnSkill(entity, copy, instance.getRemoveTime())) {
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               } else {
                  entity.sendSystemMessage(
                     Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{copy.getChatDisplayName(true)})
                        .withStyle(ChatFormatting.RED)
                  );
                  entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
               }
            }
         } else {
            Changeable<ManasSkill> changeable = Changeable.of(skill);
            if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, false, changeable).isFalse()) {
               ManasSkillInstance copy = (ManasSkillInstance)storage.getSkill((ManasSkill)changeable.get()).get();
               double newMastery = copy.getMaxMastery() * CONFIG.copyMastery;
               if (copy.getMastery() >= newMastery) {
                  entity.sendSystemMessage(
                     Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{copy.getChatDisplayName(true)})
                        .withStyle(ChatFormatting.RED)
                  );
                  entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), 1.0F, 1.0F);
                  return;
               }

               copy.setMastery(newMastery);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }
      }
   }

   private boolean canBypassRob(LivingEntity target) {
      return target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)
         ? true
         : SkillUtils.isSkillToggled(target, (ManasSkill)UniqueSkills.ANTI_SKILL.get())
            || TensuraStorages.getAbilityFrom(target).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
   }

   private boolean canRob(ManasSkillInstance instance) {
      if (instance.isTemporarySkill() || instance.getMastery() < 0.0) {
         return false;
      } else if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (instance.is(TensuraSkillTags.EXTRA_SKILLS)) {
         return true;
      } else {
         return instance.is(TensuraSkillTags.COMMON_SKILLS) ? true : instance.is(TensuraSkillTags.COPIABLE_MAGIC);
      }
   }

   private boolean canCopy(ManasSkillInstance instance) {
      if (instance.isTemporarySkill() || instance.getMastery() < 0.0) {
         return false;
      } else if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (instance.is(TensuraSkillTags.EXTRA_SKILLS)) {
         return true;
      } else if (instance.is(TensuraSkillTags.COMMON_SKILLS)) {
         return true;
      } else {
         return instance.is(TensuraSkillTags.INTRINSIC_SKILLS) ? true : instance.is(TensuraSkillTags.COPIABLE_MAGIC);
      }
   }
}
