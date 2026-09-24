package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic;
import io.github.manasmods.tensura.ability.skill.MagicElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBallProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WaterManipulationSkill extends Skill {
   public static final ExtraSkillConfig.WaterManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).WaterManipulation;

   public WaterManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.WATER) >= 1;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return entity.isShiftKeyDown() ? CONFIG.magiculeCostBall : CONFIG.magiculeCostBreath;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!instance.isSubInstance()) {
            if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
               SkillHelper.learnSkill(entity, ((WaterDominationSkill)ExtraSkills.WATER_DOMINATION.get()).createLearningInstance(entity));
            }
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.WATER_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.WATER_BOOST, CONFIG.manipulationBoost);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.isShiftKeyDown()) {
         instance.getOrCreateTag().putInt("BreathEntity", 0);
         instance.markDirty();
      } else {
         if (hasWater(entity)) {
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

   public static boolean hasWater(LivingEntity entity) {
      if (entity.isInWaterRainOrBubble()) {
         return true;
      } else {
         return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.WATER) >= 1
            ? true
            : MagicElementalTransformSkill.hasMagicTransformEffect(entity, Element.WATER);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.isShiftKeyDown()) {
         return false;
      }

      if (hasWater(entity)) {
         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         waterBreath(instance, entity, this, mode, heldTicks);
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

         waterBreath(instance, entity, this, mode, heldTicks);
      }

      return true;
   }

   public static void waterBreath(ManasSkillInstance instance, LivingEntity entity, TensuraSkill skill, int mode, int heldTicks) {
      if (!WaterMagic.isWaterEvaporated(entity, entity.level())) {
         float damage = instance.isMastered(entity) ? CONFIG.breathDamageMastered : CONFIG.breathDamage;
         BreathEntity.spawnBreathEntity((EntityType<? extends BreathEntity>)MiscEntityTypes.WATER_BREATH.get(), entity, instance, damage, skill, mode);
         if (heldTicks % 2 == 0) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_WATER.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F
               );
         }

         entity.clearFire();
      }
   }

   private void shootBall(ManasSkillInstance instance, LivingEntity entity, int mode) {
      entity.swing(InteractionHand.MAIN_HAND, true);
      if (!WaterMagic.isWaterEvaporated(entity, entity.level())) {
         instance.addMasteryPoint(entity);
         WaterBallProjectile waterBall = new WaterBallProjectile(entity.level(), entity);
         waterBall.setSpeed(1.5F);
         waterBall.setDamage(CONFIG.ballDamage);
         waterBall.setKnockForce(1.0F);
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

   public static void learnWaterManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.WATER_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.WATER_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.WATER_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.waterSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((WaterManipulationSkill)ExtraSkills.WATER_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
