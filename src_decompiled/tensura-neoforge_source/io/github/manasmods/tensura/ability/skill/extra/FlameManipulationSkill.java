package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.MagicElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class FlameManipulationSkill extends Skill {
   public static final ExtraSkillConfig.FlameManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).FlameManipulation;

   public FlameManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.FLAME) >= 1;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((HeatWaveSkill)ExtraSkills.HEAT_WAVE.get()).createLearningInstance(entity));
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((FlameDominationSkill)ExtraSkills.FLAME_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.FLAME_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.FLAME_BOOST, CONFIG.manipulationBoost);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance)) {
         return true;
      }

      if (!canUseFire(attacker)) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source) && !(source.getEntity() instanceof AbstractArrow)) {
         return true;
      }

      target.setRemainingFireTicks(instance.isMastered(attacker) ? CONFIG.manipulationBurnTick * 2 : CONFIG.manipulationBurnTick);
      attacker.level()
         .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLAME, 1.0);
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("BreathEntity", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (!canUseFire(entity)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      spawnFlameBreath(entity, instance, this, mode, heldTicks);
      return true;
   }

   public static void spawnFlameBreath(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode, int heldTicks) {
      EntityType<? extends BreathEntity> entityType = (EntityType<? extends BreathEntity>)MiscEntityTypes.FLAME_BREATH.get();
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN))) {
         entityType = (EntityType<? extends BreathEntity>)MiscEntityTypes.BLACK_FLAME_BREATH.get();
      }

      float damage = instance.isMastered(entity) ? CONFIG.breathDamageMastered : CONFIG.breathDamage;
      BreathEntity.spawnBreathEntity(entityType, entity, instance, damage, skill, mode);
      if (heldTicks % 2 == 0) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_FIRE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F
            );
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN))) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN));
      }

      entity.clearFire();
   }

   public static boolean canUseFire(LivingEntity entity) {
      if (entity.isOnFire()) {
         return true;
      } else if (TensuraStorages.getAbilityFrom(entity).getLavaPoint() > 0.0) {
         return true;
      } else {
         return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.FLAME) >= 1
            ? true
            : MagicElementalTransformSkill.hasMagicTransformEffect(entity, Element.FLAME);
      }
   }

   public static void learnFlameManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.FLAME_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.FLAME_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.fireSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((FlameManipulationSkill)ExtraSkills.FLAME_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
