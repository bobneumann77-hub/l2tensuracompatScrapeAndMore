package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.entity.projectile.magic.WindSphereProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WindDominationSkill extends Skill {
   public WindDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get())
         ? false
         : newEP > WindManipulationSkill.CONFIG.dominationEpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return entity.isShiftKeyDown() ? WindManipulationSkill.CONFIG.magiculeCostBall : WindManipulationSkill.CONFIG.magiculeCostBreath;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.WIND_BOOST, WindManipulationSkill.CONFIG.dominationBoost);
      if (instance.isMastered(entity)) {
         AttributeHelper.applyDominationDegradation(
            entity, TensuraAttributes.WIND_RESIST_DEGRADATION, WindManipulationSkill.CONFIG.resistDegradationAcquirement
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.WIND_BOOST, WindManipulationSkill.CONFIG.dominationBoost);
      AttributeHelper.removeDominationDegradation(entity, TensuraAttributes.WIND_RESIST_DEGRADATION);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.isShiftKeyDown()) {
         instance.getOrCreateTag().putInt("BreathEntity", 0);
         instance.markDirty();
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         entity.swing(InteractionHand.MAIN_HAND, true);
         WindSphereProjectile windSphere = new WindSphereProjectile(entity.level(), entity);
         windSphere.setSpeed(1.5F);
         windSphere.setDamage(WindManipulationSkill.CONFIG.ballDamage);
         windSphere.setNoGravity(true);
         windSphere.setKnockForce(3.0F);
         windSphere.setBurnTicks(-1);
         windSphere.setSkill(entity, instance, this, mode);
         windSphere.setPosAndShoot(entity);
         entity.level().addFreshEntity(windSphere);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.isShiftKeyDown()) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      float damage = instance.isMastered(entity) ? WindManipulationSkill.CONFIG.breathDamageMastered : WindManipulationSkill.CONFIG.breathDamage;
      BreathEntity.spawnBreathEntity((EntityType<? extends BreathEntity>)MiscEntityTypes.WIND_BREATH.get(), entity, instance, damage, this, mode);
      if (heldTicks % 2 == 0) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_WIND.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F
            );
      }

      entity.clearFire();
      return true;
   }
}
