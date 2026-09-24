package io.github.manasmods.tensura.ability.magic.aspectual.mental;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class DominateMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Dominate CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Dominate;

   public DominateMagic() {
      super(AspectualMagic.AspectualType.MENTAL);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("TargetID", 0);
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, false);
      if (target == null || !target.isAlive()) {
         entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
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
      } else if (entity.isShiftKeyDown() && cancelDominate(entity, target)) {
         instance.getOrCreateTag().putInt("FieldID", 0);
         instance.markDirty();
      } else if (!SubordinateHelper.isSubordinate(entity, target) && !RaceUtils.isSpiritual(target)) {
         if (!CharmSkill.isMindControlFailed(entity, target, entity.level(), false)) {
            double requirement = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
               ? CONFIG.resistedRequirement
               : CONFIG.epRequirement;
            if (EnergyHelper.getMaxEP(target) > requirement) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
            } else {
               tag.putInt("TargetID", target.getId());
               instance.markDirty();
            }
         }
      } else {
         entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.5F
            );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      } else if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("TargetID");
         if (id == 0) {
            return false;
         } else {
            Entity target = entity.level().getEntity(id);
            double range = CONFIG.range;
            if (target instanceof LivingEntity living && living.isAlive() && living.distanceToSqr(entity) <= range * range) {
               MagicCircle.castTargetedMagicCircle(
                  target.getBbWidth() * 2.0F,
                  25,
                  target.position(),
                  MagicCircleVariant.MENTAL,
                  false,
                  entity,
                  instance.getOrCreateTag(),
                  instance,
                  mode,
                  Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
               );
               this.applyCastingVisual(instance, entity, heldTicks, mode);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("TargetID");
         if (id != 0) {
            Entity target = entity.level().getEntity(id);
            if (target instanceof LivingEntity living) {
               double range = CONFIG.range;
               if (!target.isAlive() || target.distanceToSqr(entity) > range * range) {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.5F,
                        0.5F
                     );
               } else if (RaceUtils.isSpiritual(living)) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.5F,
                        0.5F
                     );
               } else if (!CharmSkill.isMindControlFailed(entity, living, entity.level(), false)) {
                  double requirement = SkillUtils.isSkillToggled(living, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                     ? CONFIG.resistedRequirement
                     : CONFIG.epRequirement;
                  int duration = instance.isMastered(entity) ? CONFIG.controlDurationMastered : CONFIG.controlDuration;
                  dominate(instance, entity, mode, living, requirement, duration);
               }
            }
         }
      }
   }

   public static boolean cancelDominate(LivingEntity entity, LivingEntity target) {
      UUID uuid = entity.getUUID();
      IExistence existence = TensuraStorages.getExistenceFrom(target);
      if (Objects.equals(existence.getPermanentOwner(), uuid)) {
         return false;
      }

      if (Objects.equals(existence.getTemporaryOwner(), uuid)) {
         existence.setTemporaryOwner(null);
         target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
         if (target instanceof ISubordinate subordinate) {
            subordinate.resetOwner(existence.getPermanentOwner());
         }

         entity.swing(InteractionHand.MAIN_HAND, true);
         existence.markDirty();
         TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(),
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.5F
            );
         return true;
      } else {
         return false;
      }
   }

   public static void dominate(ManasSkillInstance instance, LivingEntity entity, int mode, LivingEntity target, double epRequirement, int duration) {
      double targetEP = EnergyHelper.getMaxEP(target);
      if (targetEP < epRequirement) {
         IExistence existence = TensuraStorages.getExistenceFrom(target);
         if (Objects.equals(existence.getPermanentOwner(), entity.getUUID())) {
            return;
         }

         if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (duration != -1) {
               MobEffectInstance mindControl = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
               );
               if (instance.getRemoveTime() == -3) {
                  TensuraMobEffect.addEffect(target, mindControl, entity, null, 0);
               } else {
                  TensuraMobEffect.addEffect(target, mindControl, entity, instance.getSkill(), mode);
               }

               if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                  return;
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
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN, 2.0);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
         }
      } else {
         entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.5F
            );
      }
   }
}
