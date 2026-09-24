package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class FlameDominationSkill extends Skill {
   public FlameDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get())
         ? false
         : newEP > FlameManipulationSkill.CONFIG.dominationEpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.FLAME_BOOST, FlameManipulationSkill.CONFIG.dominationBoost);
      if (instance.isMastered(entity)) {
         AttributeHelper.applyDominationDegradation(
            entity, TensuraAttributes.FLAME_RESIST_DEGRADATION, FlameManipulationSkill.CONFIG.resistDegradationAcquirement
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.FLAME_BOOST, FlameManipulationSkill.CONFIG.dominationBoost);
      AttributeHelper.removeDominationDegradation(entity, TensuraAttributes.FLAME_RESIST_DEGRADATION);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance)) {
         return true;
      }

      if (!FlameManipulationSkill.canUseFire(attacker)) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source) && !(source.getEntity() instanceof AbstractArrow)) {
         return true;
      }

      int tick = FlameManipulationSkill.CONFIG.dominationBurnTick;
      target.setRemainingFireTicks(this.isMastered(instance, attacker) ? tick * 2 : tick);
      attacker.level()
         .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLAME, 1.0);
      return true;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (!FlameManipulationSkill.canUseFire(entity)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      FlameManipulationSkill.spawnFlameBreath(entity, instance, this, mode, heldTicks);
      return true;
   }
}
