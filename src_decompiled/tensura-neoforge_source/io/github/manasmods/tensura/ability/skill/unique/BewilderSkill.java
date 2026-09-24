package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BewilderSkill extends Skill {
   private static final UniqueSkillConfig.Bewilder CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Bewilder;

   public BewilderSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 4;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 3 : mode - 1;
      } else {
         return mode == 3 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "bewilder.target";
         case 1 -> "bewilder.area";
         case 2 -> "bewilder.charm";
         case 3 -> "bewilder.kill";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostArea;
         case 2 -> CONFIG.magiculeCostCharm;
         case 3 -> CONFIG.magiculeCostKill;
         default -> CONFIG.magiculeCostTarget;
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      UUID uuid = entity.getUUID();
      switch (mode) {
         case 0:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            if (target == null) {
               return;
            }

            IExistence existence = TensuraStorages.getExistenceFrom(target);
            if (entity.isShiftKeyDown()) {
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
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
                  entity.swing(InteractionHand.MAIN_HAND, true);
               }
            } else {
               if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
                  return;
               }

               if (CharmSkill.isMindControlFailed(entity, target, level, true)) {
                  return;
               }

               if (RaceUtils.isSpiritual(target)) {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
                  return;
               }

               if (Objects.equals(existence.getPermanentOwner(), uuid)) {
                  return;
               }

               if (Objects.equals(existence.getTemporaryOwner(), uuid)) {
                  return;
               }

               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               int duration = SkillUtils.hasSkill(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                  ? CONFIG.controlResistedDuration
                  : CONFIG.controlDuration;
               if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
                  if (duration != -1) {
                     MobEffectInstance mindControl = new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
                     );
                     TensuraMobEffect.addEffect(target, mindControl, entity, this, mode);
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

                  existence.markDirty();
                  instance.addMasteryPoint(entity);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BAT_HURT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN, 2.0);
               }
            }
            break;
         case 1:
            float radius = instance.isMastered(entity) ? CONFIG.areaRadiusMastered : CONFIG.areaRadius;
            List<LivingEntity> list = level.getEntitiesOfClass(
               LivingEntity.class, entity.getBoundingBox().inflate(radius), livingEntity -> !livingEntity.is(entity) && livingEntity.isAlive()
            );
            if (list.isEmpty()) {
               return;
            }

            if (EnergyHelper.isOutOfEnergy(entity, instance, mode, list.size())) {
               return;
            }

            instance.addMasteryPoint(entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BAT_HURT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);

            for (LivingEntity target : list) {
               if (CharmSkill.canMindControl(target, level)
                  && !target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))
                  && !RaceUtils.isSpiritual(target)
                  && !SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                  int duration = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                     ? CONFIG.controlAreaResistedDuration
                     : CONFIG.controlAreaDuration;
                  if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
                     if (duration != -1) {
                        MobEffectInstance mindControl = new MobEffectInstance(
                           TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
                        );
                        TensuraMobEffect.addEffect(target, mindControl, entity, this, mode);
                        if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                           return;
                        }
                     }

                     IExistence existence = TensuraStorages.getExistenceFrom(target);
                     if (!Objects.equals(existence.getTemporaryOwner(), uuid)) {
                        existence.setTemporaryOwner(uuid);
                        if (target instanceof Mob mob) {
                           SubordinateHelper.removeTarget(mob);
                        }

                        if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                           subordinate.tame(player);
                        }

                        existence.markDirty();
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BAT_HURT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN);
                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.RAID_OMEN, 2.0);
                     }
                  }
               }
            }
            break;
         case 2:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            entity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, CONFIG.heroDuration, CONFIG.heroLevel - 1, true, false, true));
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART);
            break;
         case 3:
            float radius = instance.isMastered(entity) ? CONFIG.areaRadiusMastered : CONFIG.areaRadius;
            List<LivingEntity> list = level.getEntitiesOfClass(
               LivingEntity.class,
               entity.getBoundingBox().inflate(radius),
               livingEntity -> !livingEntity.is(entity)
                  && livingEntity.isAlive()
                  && livingEntity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))
            );
            if (list.isEmpty()) {
               return;
            }

            if (EnergyHelper.isOutOfEnergy(entity, instance, mode, list.size())) {
               return;
            }

            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.killCooldownMastered : CONFIG.killCooldown, mode);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.SUICIDE, mode);

            for (LivingEntity target : list) {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (Objects.equals(uuid, existence.getTemporaryOwner())) {
                  float damage = SkillUtils.hasSkill(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                     ? target.getHealth() * CONFIG.killHPResistedMultiplier
                     : target.getHealth() * CONFIG.killHPMultiplier;
                  if (target.hurt(source, damage)) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ENCHANTED_HIT);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
                  }
               }
            }
      }
   }
}
