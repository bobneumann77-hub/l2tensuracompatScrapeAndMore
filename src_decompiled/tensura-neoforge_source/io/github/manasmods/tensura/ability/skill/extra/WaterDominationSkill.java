package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBallProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WaterDominationSkill extends Skill {
   public WaterDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.WATER_MANIPULATION.get())
         ? false
         : newEP > WaterManipulationSkill.CONFIG.dominationEpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return entity.isShiftKeyDown() ? WaterManipulationSkill.CONFIG.magiculeCostBall : WaterManipulationSkill.CONFIG.magiculeCostBreath;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.WATER_BOOST, WaterManipulationSkill.CONFIG.dominationBoost);
      if (instance.isMastered(entity)) {
         AttributeHelper.applyDominationDegradation(
            entity, TensuraAttributes.WATER_RESIST_DEGRADATION, WaterManipulationSkill.CONFIG.resistDegradationAcquirement
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.WATER_BOOST, WaterManipulationSkill.CONFIG.dominationBoost);
      AttributeHelper.removeDominationDegradation(entity, TensuraAttributes.WATER_RESIST_DEGRADATION);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.isShiftKeyDown()) {
         instance.getOrCreateTag().putInt("BreathEntity", 0);
         instance.markDirty();
      } else {
         if (WaterManipulationSkill.hasWater(entity)) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            this.shootBall(instance, entity, mode);
         } else {
            IAbility ability = TensuraStorages.getAbilityFrom(entity);
            if (ability.getWaterPoint() < 1.0) {
               return;
            }

            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            ability.setWaterPoint(ability.getWaterPoint() - 1.0);
            this.shootBall(instance, entity, mode);
            ability.markDirty();
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.isShiftKeyDown()) {
         return false;
      }

      if (WaterManipulationSkill.hasWater(entity)) {
         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         WaterManipulationSkill.waterBreath(instance, entity, this, mode, heldTicks);
      } else {
         IAbility ability = TensuraStorages.getAbilityFrom(entity);
         if (ability.getWaterPoint() < 1.0) {
            return false;
         }

         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         if (heldTicks % 100 == 0) {
            ability.setWaterPoint(ability.getWaterPoint() - 1.0);
            ability.markDirty();
         }

         WaterManipulationSkill.waterBreath(instance, entity, this, mode, heldTicks);
      }

      return true;
   }

   private void shootBall(ManasSkillInstance instance, LivingEntity entity, int mode) {
      entity.swing(InteractionHand.MAIN_HAND, true);
      if (!WaterMagic.isWaterEvaporated(entity, entity.level())) {
         instance.addMasteryPoint(entity);
         WaterBallProjectile waterBall = new WaterBallProjectile(entity.level(), entity);
         waterBall.setSpeed(2.0F);
         waterBall.setDamage(WaterManipulationSkill.CONFIG.ballDamage);
         waterBall.setKnockForce(1.5F);
         waterBall.setBurnTicks(-1);
         waterBall.setSkill(entity, instance, this, mode);
         waterBall.setPosAndShoot(entity);
         entity.level().addFreshEntity(waterBall);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }
}
