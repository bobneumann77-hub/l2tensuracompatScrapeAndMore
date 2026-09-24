package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
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
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CharmSkill extends Skill {
   private static final IntrinsicSkillConfig.Charm CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).Charm;

   public CharmSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      charm(instance, entity, mode);
   }

   public static void charm(ManasSkillInstance instance, LivingEntity entity, int mode) {
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false);
      if (target != null) {
         UUID uuid = entity.getUUID();
         Level level = entity.level();
         if (entity.isShiftKeyDown()) {
            boolean success = false;
            IExistence existence = TensuraStorages.getExistenceFrom(target);
            if (!Objects.equals(existence.getPermanentOwner(), uuid)) {
               if (Objects.equals(existence.getTemporaryOwner(), uuid)) {
                  existence.setTemporaryOwner(null);
                  target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
                  if (target instanceof ISubordinate subordinate) {
                     subordinate.resetOwner(existence.getPermanentOwner());
                  }

                  success = true;
               }

               if (existence.isTargetNeutral(uuid)) {
                  existence.removeNeutralTarget(uuid);
                  success = true;
               }

               if (success) {
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
               }
            }
         } else if (!isMindControlFailed(entity, target, level, true)) {
            if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
               entity.sendSystemMessage(Component.translatable("tensura.naming.insane").withStyle(ChatFormatting.RED));
            } else {
               double EP = EnergyHelper.getMaxEP(entity);
               double targetEP = EnergyHelper.getMaxEP(target);
               double resisted;
               if (SkillUtils.hasSkill(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                  resisted = EP * CONFIG.resistedMultiplier;
               } else {
                  resisted = 0.0;
               }

               boolean success = false;
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (targetEP <= EP * CONFIG.fullMultiplier - resisted) {
                  if (Objects.equals(existence.getPermanentOwner(), uuid)) {
                     return;
                  }

                  if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCost + targetEP)) {
                     return;
                  }

                  int duration = instance.isMastered(entity) ? CONFIG.controlDurationMastered : CONFIG.controlDuration;
                  if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
                     if (duration != -1) {
                        MobEffectInstance mindControl = new MobEffectInstance(
                           TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
                        );
                        TensuraMobEffect.addEffect(target, mindControl, entity, instance.getSkill(), mode);
                        if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                           return;
                        }
                     }

                     existence.setTemporaryOwner(uuid);
                     if (target instanceof Mob mob) {
                        SubordinateHelper.removeTarget(mob);
                     }

                     if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                        subordinate.tame(player);
                     }

                     success = true;
                  }
               } else if (targetEP <= EP * CONFIG.neutralMultiplier - resisted) {
                  if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCost + targetEP)) {
                     return;
                  }

                  if (existence.isTargetNeutral(uuid)) {
                     return;
                  }

                  existence.addNeutralTarget(uuid);
                  if (target instanceof Mob mob) {
                     SubordinateHelper.removeTarget(mob);
                  }

                  success = true;
               }

               if (success) {
                  instance.addMasteryPoint(entity);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  existence.markDirty();
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN);
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
      }
   }

   public static boolean isMindControlFailed(LivingEntity user, LivingEntity target, Level level, boolean charm) {
      boolean failed = !canMindControl(target, level, charm, false);
      if (!failed && SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
         failed = true;
      }

      if (failed) {
         user.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
      }

      return failed;
   }

   public static boolean canMindControl(LivingEntity target, Level level) {
      return canMindControl(target, level, true, false);
   }

   public static boolean canMindControl(LivingEntity target, Level level, boolean charm, boolean ignorePlayerGamerule) {
      if (target instanceof Player player) {
         if (!ignorePlayerGamerule && TensuraGameRules.noPlayerMindControl(level)) {
            return false;
         }

         if (player.getAbilities().invulnerable) {
            return false;
         }
      }

      return charm ? !target.getType().is(TensuraEntityTags.NO_CHARM) : !target.getType().is(TensuraEntityTags.NO_MIND_CONTROL);
   }
}
