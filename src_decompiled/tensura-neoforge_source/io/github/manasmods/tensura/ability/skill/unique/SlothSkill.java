package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SlothSkill extends Skill {
   public static final UniqueSkillConfig.Sloth CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Sloth;

   public SlothSkill() {
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

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getOrCreateTag().getDouble(this.getModeId(instance, 4)) >= BASE_CONFIG.Learning.learningPointRequirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 6;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 5 : 4;
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            case 5 -> 4;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> instance.isMastered(entity) ? 5 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(4, 5);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "sloth.deep_hypno";
         case 1 -> "sloth.fallen_hypno";
         case 2 -> "sloth.deprive";
         case 3 -> "sloth.rest";
         case 4 -> "sloth.phantasmal_style";
         case 5 -> "sloth.fallen_strike";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 2 -> CONFIG.magiculeCostDeprive;
         case 5 -> CONFIG.magiculeCostFallen;
         default -> 0.0;
      };
   }

   @Override
   protected boolean isAffectedByAbility(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP)) && !this.canActivateInRaceLimit(instance, mode);
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      } else if (TensuraDamageHelper.isPhysicalAttack(source)) {
         double bonus = instance.isMastered(owner) ? target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.phantasmalSHPMastered : 0.0;
         DamageSource damagesource = this.createSource(instance, owner, TensuraDamageTypes.DROWSY_DEATH, 4);
         TensuraDamageHelper.directSpiritualHurt(target, owner, damagesource, CONFIG.phantasmalSHP + (float)bonus);
         TensuraParticleHelper.spawnServerParticles(
            target.level(),
            TensuraParticleUtils.getDrowsinessWave(Math.max(1.0F, target.getBbWidth()), 0.3F),
            target.getX(),
            target.getY() + target.getBbHeight() * 0.5,
            target.getZ()
         );
         return target.isAlive();
      } else {
         return true;
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 4 || mode == 5) {
         return false;
      }

      if (mode != 3 && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
         }

         return false;
      } else {
         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         Level level = entity.level();
         switch (mode) {
            case 0:
               LivingEntity targetx = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.deepRange, false);
               if (targetx != null) {
                  if (targetx instanceof Player player && player.getAbilities().invulnerable) {
                     return false;
                  }

                  if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
                     instance.addMasteryPoint(entity);
                  }

                  if (heldTicks % 20 == 0) {
                     float size = Math.max(1.0F, targetx.getBbWidth());
                     level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 0.5F, 10.0F);
                     TensuraParticleHelper.spawnServerParticles(
                        level, TensuraParticleUtils.getDrowsinessWave(size, 0.3F), targetx.getX(), targetx.getY() + targetx.getBbHeight() * 0.5, targetx.getZ()
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        level,
                        TensuraParticleUtils.getDrowsinessWave(size, 0.3F),
                        targetx.getX(),
                        targetx.getY() + targetx.getBbHeight() * 0.25,
                        targetx.getZ()
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        level,
                        TensuraParticleUtils.getDrowsinessWave(size, 0.3F),
                        targetx.getX(),
                        targetx.getY() + targetx.getBbHeight() * 0.75,
                        targetx.getZ()
                     );
                  }

                  int i = 0;
                  int originalDuration = instance.isMastered(entity) ? CONFIG.deepDurationMastered : CONFIG.deepDuration;
                  MobEffectInstance drowsiness = targetx.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS));
                  int duration;
                  if (drowsiness != null && heldTicks > 0) {
                     duration = drowsiness.getDuration() + 2;
                     i = (duration - originalDuration) / (CONFIG.deepIncreaseTick + 100 * getSpiritualResistLevel(targetx));
                  } else {
                     duration = originalDuration;
                  }

                  MobEffectInstance effectInstance = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS), duration, i, false, false, false
                  );
                  TensuraMobEffect.addEffect(targetx, effectInstance, entity, this, mode);
               }
               break;
            case 1:
               float radiusx = instance.isMastered(entity) ? CONFIG.fallenRadiusMastered : CONFIG.fallenRadius;
               if (heldTicks % 20 == 0) {
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  TensuraParticleHelper.spawnServerParticles(
                     level, TensuraParticleUtils.getDrowsinessWave(radiusx, 0.0F), entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ()
                  );
               }

               List<LivingEntity> list = level.getEntitiesOfClass(
                  LivingEntity.class, entity.getBoundingBox().inflate(radiusx), living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
               );
               if (!list.isEmpty()) {
                  if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
                     instance.addMasteryPoint(entity);
                  }

                  for (LivingEntity targetx : list) {
                     if (!(targetx instanceof Player player && player.getAbilities().invulnerable)) {
                        int i = 0;
                        int originalDuration = instance.isMastered(entity) ? CONFIG.fallenDurationMastered : CONFIG.fallenDuration;
                        MobEffectInstance drowsiness = targetx.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS));
                        int duration;
                        if (drowsiness != null && heldTicks > 0) {
                           duration = drowsiness.getDuration() + 2;
                           i = (duration - originalDuration) / (CONFIG.fallenIncreaseTick + 100 * getSpiritualResistLevel(targetx));
                        } else {
                           duration = originalDuration;
                        }

                        MobEffectInstance effectInstance = new MobEffectInstance(
                           TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS), duration, i, false, false, false
                        );
                        TensuraMobEffect.addEffect(targetx, effectInstance, entity, this, mode);
                     }
                  }
               }
               break;
            case 2:
               boolean mastered = instance.isMastered(entity);
               double radius = mastered ? CONFIG.depriveRadiusMastered : CONFIG.depriveRadius;
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, radius, false);
               if (heldTicks > 0 && heldTicks % 20 == 0 && target != null) {
                  if (target instanceof Player player && player.getAbilities().invulnerable) {
                     return false;
                  }

                  if (EnergyHelper.drainEnergy(target, entity, CONFIG.depriveEP, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL)) {
                     if (mastered) {
                        EnergyHelper.drainEnergy(target, entity, CONFIG.depriveEPMastered, true, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL);
                     }

                     double bonus = mastered ? target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.depriveSHPMastered : 0.0;
                     DamageSource damagesource = this.createSource(instance, entity, TensuraDamageTypes.DROWSY_DEATH, mode);
                     TensuraDamageHelper.directSpiritualHurt(target, entity, damagesource, CONFIG.depriveSHP + (float)bonus);
                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        level,
                        TensuraParticleUtils.getDrowsinessWave(Math.max(1.0F, target.getBbWidth()), 0.3F),
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.5,
                        target.getZ()
                     );
                     if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0) {
                        instance.addMasteryPoint(entity);
                     }

                     IExistence existence = TensuraStorages.getExistenceFrom(target);
                     if (target.isAlive() && existence.getSpiritualHealth() > 0.0) {
                        double SHP = target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
                        if (existence.getSpiritualHealth() < SHP / 2.0 && CharmSkill.canMindControl(target, level)) {
                           UUID uuid = entity.getUUID();
                           if (!Objects.equals(existence.getTemporaryOwner(), uuid)
                              && !((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, false).isFalse()) {
                              existence.setTemporaryOwner(uuid);
                              if (target instanceof Mob mob) {
                                 SubordinateHelper.removeTarget(mob);
                              }

                              if (entity instanceof Player player && target instanceof ISubordinate animal) {
                                 animal.tame(player);
                              }

                              existence.markDirty();
                              entity.swing(InteractionHand.MAIN_HAND, true);
                              TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART);
                              level.playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
                              );
                           }
                        }
                     }
                  }
               }
               break;
            case 3:
               if (entity instanceof Player player && player.isSecondaryUseActive()) {
                  return false;
               }

               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.REST), 5, 0, false, false, false));
               if (heldTicks > 0 && heldTicks % 20 == 0) {
                  entity.heal(instance.isMastered(entity) ? CONFIG.restHP + entity.getMaxHealth() * CONFIG.restHPMastered : CONFIG.restHP);
                  if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0) {
                     instance.addMasteryPoint(entity);
                  }

                  double maxSHP = entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
                  IExistence existence = TensuraStorages.getExistenceFrom(entity);
                  if (existence.getSpiritualHealth() < maxSHP) {
                     float healSHP = instance.isMastered(entity) ? CONFIG.restSHP + (float)(maxSHP * CONFIG.restSHPMastered) : CONFIG.restSHP;
                     existence.setSpiritualHealth(Math.min(existence.getSpiritualHealth() + healSHP, maxSHP));
                     existence.markDirty();
                  }

                  double maxMP = EnergyHelper.getMaxMagicule(entity);
                  if (existence.getMagicule() >= maxMP) {
                     double areaMagicule = AreaMagiculeHelper.getMagicule(entity, true);
                     double mana = areaMagicule * entity.getAttributeValue(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER);
                     CompoundTag tag = instance.getOrCreateTag();
                     tag.putDouble("storedMagicule", tag.getDouble("storedMagicule") + mana);
                     instance.markDirty();
                  }

                  float size = Math.max(1.0F, entity.getBbWidth());
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F);
                  TensuraParticleHelper.spawnServerParticles(
                     level, TensuraParticleUtils.getSlothRestWave(size * 1.5F), entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ()
                  );
                  TensuraParticleHelper.spawnServerParticles(
                     level, TensuraParticleUtils.getSlothRestWave(size * 1.5F), entity.getX(), entity.getY() + entity.getBbHeight() * 0.25, entity.getZ()
                  );
                  TensuraParticleHelper.spawnServerParticles(
                     level, TensuraParticleUtils.getSlothRestWave(size * 1.5F), entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ()
                  );
               }
         }

         return true;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 3 && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
         }
      } else {
         Level level = entity.level();
         switch (mode) {
            case 0:
               MagicCircle circle = ObjectSelectionHelper.getTargetingEntity(MagicCircle.class, entity, CONFIG.deepRangeMagic, 0.5, true, true, false);
               if (circle != null && circle.isAlive()) {
                  ManasSkillInstance targetInstance = circle.getSkill();
                  if (targetInstance != null && targetInstance.is(TensuraSkillTags.MAGIC)) {
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     level.playSound(
                        null,
                        circle.getX(),
                        circle.getY(),
                        circle.getZ(),
                        (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.75F,
                        1.0F
                     );
                     if (circle.getOwner() instanceof LivingEntity owner) {
                        Skills skills = SkillAPI.getSkillsFrom(owner);
                        Optional<ManasSkillInstance> optional = skills.getSkill(targetInstance.getSkill());
                        if (optional.isPresent()) {
                           instance.addMasteryPoint(entity);
                           int cooldown = instance.isMastered(entity) ? CONFIG.deepMagicCooldownMastered : CONFIG.deepMagicCooldown;
                           optional.get().setCoolDown(Math.max(optional.get().getCoolDown(circle.getMode()), cooldown), circle.getMode());
                           skills.markDirty();
                        }
                     }

                     circle.discard();
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
                  }
               }
            case 1:
            case 2:
            default:
               break;
            case 3:
               if (entity instanceof Player player && !player.isSecondaryUseActive()) {
                  return;
               }

               List<LivingEntity> list = entity.level()
                  .getEntitiesOfClass(
                     LivingEntity.class,
                     entity.getBoundingBox().inflate(CONFIG.restAllyRadius),
                     living -> !living.is(entity) && living.isAlive() && living.isAlliedTo(entity)
                  );
               if (list.isEmpty()) {
                  return;
               }

               if (this.outOfStoredMP(entity, instance, CONFIG.restAllyCost * list.size())) {
                  return;
               }

               for (LivingEntity ally : list) {
                  ally.heal(CONFIG.restAllyHP);
                  float healSHP = CONFIG.restAllySHP;
                  IExistence existence = TensuraStorages.getExistenceFrom(ally);
                  existence.setSpiritualHealth(existence.getSpiritualHealth() + healSHP);
                  EnergyHelper.gainAura(entity, CONFIG.restAllyEP, EnergyHelper.GainType.NORMAL);
                  EnergyHelper.gainMagicule(entity, CONFIG.restAllyEP, EnergyHelper.GainType.NORMAL);
                  float size = Math.max(1.0F, ally.getBbWidth());
                  level.playSound(null, ally.getX(), ally.getY(), ally.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F);
                  TensuraParticleHelper.spawnServerParticles(
                     level, TensuraParticleUtils.getSlothRestWave(size * 1.5F), ally.getX(), ally.getY() + ally.getBbHeight() * 0.5, ally.getZ()
                  );
               }
               break;
            case 4:
               this.learnMode(instance, entity, mode);
               break;
            case 5:
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               if (this.learnMode(instance, entity, mode)) {
                  return;
               }

               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.fallStrikeRange, false);
               if (target == null) {
                  return;
               }

               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return;
               }

               DamageSource damagesource = this.createSource(instance, entity, TensuraDamageTypes.DROWSY_DEATH, mode);
               TensuraDamageHelper.directSpiritualHurt(target, entity, damagesource, CONFIG.fallStrikeSHP, 0.0F);
               instance.setCoolDown(CONFIG.fallStrikeCooldown, mode);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 2.0);
               TensuraParticleHelper.spawnServerParticles(
                  level,
                  TensuraParticleUtils.getDrowsinessWave(Math.max(1.0F, target.getBbWidth() * 2.5F), 0.1F),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
         }
      }
   }

   private boolean outOfStoredMP(LivingEntity entity, ManasSkillInstance skillInstance, double cost) {
      CompoundTag tag = skillInstance.getOrCreateTag();
      double newStored = tag.getDouble("storedMagicule") - cost;
      if (newStored >= 0.0) {
         tag.putDouble("storedMagicule", newStored);
         skillInstance.markDirty();
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         return false;
      } else {
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
         return true;
      }
   }

   public static int getSpiritualResistLevel(LivingEntity entity) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
         return 2;
      } else {
         return SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get()) ? 1 : 0;
      }
   }
}
