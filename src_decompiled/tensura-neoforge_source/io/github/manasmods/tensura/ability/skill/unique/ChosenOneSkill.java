package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.HeroHakiSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ChosenOneSkill extends Skill {
   public static final UniqueSkillConfig.ChosenOne CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).ChosenOne;
   private static final ResourceLocation CHOSEN_ONE = ResourceLocation.fromNamespaceAndPath("tensura", "chosen_one");

   public ChosenOneSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, CHOSEN_ONE, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
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
         case 0 -> "chosen_one.haki";
         case 1 -> "chosen_one.charisma";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return mode == 0;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostHaki;
         case 1 -> CONFIG.magiculeCostCharisma;
         default -> 0.0;
      };
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() || this.isInSlot(entity, instance);
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         existence.setBlessed(true);
         existence.markDirty();
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled()) {
         entity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 240, CONFIG.heroLevel - 1, false, false, false));
      }

      if (this.isInSlot(entity, instance)) {
         entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 240, CONFIG.luckLevel - 1, false, false, false));
         List<LivingEntity> list = entity.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               entity.getBoundingBox().inflate(CONFIG.blessingRadius),
               living -> living.isAlive() && (living.isAlliedTo(entity) || living.is(entity))
            );
         if (list.isEmpty()) {
            return;
         }

         boolean autoTame = !instance.isMastered(entity) || this.isInSlot(entity, instance, 1);

         for (LivingEntity target : list) {
            MobEffectInstance allyBoost = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ALLY_BOOST), 240, 1, false, false, false);
            allyBoost.tensura$setOverride(true);
            if (autoTame) {
               TensuraMobEffect.addEffect(target, allyBoost, entity, this, 0);
            } else {
               TensuraMobEffect.addEffect(target, allyBoost, null, (AbilitySlot)null);
            }
         }
      }
   }

   @Override
   public boolean onAbilityEquipped(
      Player player, Changeable<ManasSkillInstance> instance, Changeable<Integer> mode, Changeable<Integer> slot, Changeable<Integer> preset
   ) {
      if ((Integer)preset.get() != -1) {
         return true;
      }

      ManasSkillInstance skillInstance = (ManasSkillInstance)instance.get();
      if (skillInstance != null && skillInstance.getSkill() == this) {
         List<LivingEntity> list = player.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               player.getBoundingBox().inflate(CONFIG.blessingRadius),
               living -> living.isAlive() && (living.isAlliedTo(player) || living.is(player))
            );
         boolean autoTame = !skillInstance.isMastered(player) || (Integer)mode.get() == 1;

         for (LivingEntity target : list) {
            MobEffectInstance allyBoost = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ALLY_BOOST), 240, 1, false, false, false);
            allyBoost.tensura$setOverride(true);
            if (autoTame) {
               TensuraMobEffect.addEffect(target, allyBoost, player, this, 0);
            } else {
               TensuraMobEffect.addEffect(target, allyBoost, null, (AbilitySlot)null);
            }
         }
      }

      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      if (mode != 0) {
         return 0.0;
      } else {
         return instance.isMastered(entity) ? (HakiSkill.CONFIG.speedMultiplierMastered - 1.0) / (HakiSkill.CONFIG.speedMultiplier - 1.0) : 1.0;
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return HeroHakiSkill.activateHeroHaki(instance, entity, mode, heldTicks, this);
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, CHOSEN_ONE)) {
            instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         instance.getOrCreateTag().putInt("HakiID", 0);
         instance.markDirty();
      } else {
         Level level = entity.level();
         List<LivingEntity> list = level.getEntitiesOfClass(
            LivingEntity.class, entity.getBoundingBox().inflate(CONFIG.controlRadius), targetx -> canApplyCharisma(targetx, entity)
         );
         if (list.isEmpty()) {
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
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.addMasteryPoint(entity);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );

            for (LivingEntity target : list) {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (!Objects.equals(existence.getTemporaryOwner(), entity.getUUID())) {
                  int duration = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                     ? CONFIG.controlResistedDuration
                     : CONFIG.controlDuration;
                  if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
                     if (duration != -1) {
                        MobEffectInstance mindControl = new MobEffectInstance(
                           TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 1, false, false, false
                        );
                        TensuraMobEffect.addEffect(target, mindControl, entity, this, mode);
                        if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                           continue;
                        }
                     }

                     existence.setTemporaryOwner(entity.getUUID());
                     if (target instanceof Mob mob) {
                        SubordinateHelper.removeTarget(mob);
                     }

                     if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                        subordinate.tame(player);
                     }

                     existence.markDirty();
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.TRIAL_OMEN);
                  }
               }
            }
         }
      }
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
      if (!source.tensura$isNotActualDeath()) {
         if (instance.isMastered(owner)) {
            if (source.getEntity() != owner) {
               if (!subordinate.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
                  if (!(subordinate instanceof Player) || subordinate.level().getLevelData().isHardcore()) {
                     List<ManasSkillInstance> list = List.copyOf(SkillAPI.getSkillsFrom(subordinate).getLearnedSkills());
                     if (!list.isEmpty()) {
                        for (ManasSkillInstance targetInstance : list) {
                           if (!targetInstance.isTemporarySkill()
                              && !(targetInstance.getMastery() < 0.0)
                              && targetInstance.getSkill() != this
                              && !targetInstance.is(TensuraSkillTags.NO_PLUNDERING)
                              && !targetInstance.is(TensuraSkillTags.ULTIMATE_SKILLS)
                              && (!targetInstance.is(TensuraSkillTags.MAGIC) || targetInstance.is(TensuraSkillTags.COPIABLE_MAGIC))) {
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
   }

   public static boolean canApplyCharisma(LivingEntity target, LivingEntity entity) {
      if (target.isAlive() && target != entity) {
         if (target.getType().is(TensuraEntityTags.NO_CHARISMA)) {
            return false;
         } else if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
            return false;
         } else if (!CharmSkill.canMindControl(target, entity.level())) {
            return false;
         } else if (target instanceof Mob mob && mob.isAggressive()) {
            return false;
         } else if (target instanceof NeutralMob mob && mob.isAngry()) {
            return false;
         } else if (target.isAlliedTo(entity)) {
            return false;
         } else {
            return SubordinateHelper.getSubordinateOwnerUUID(target) != null
               ? false
               : !SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());
         }
      } else {
         return false;
      }
   }
}
