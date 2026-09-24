package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.WaterManipulationSkill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBladeProjectile;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WaterBladeSkill extends Skill {
   private static final CommonSkillConfig.WaterBlade CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).WaterBlade;

   public WaterBladeSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)CommonSkills.HYDRAULIC_PROPULSION.get()) ? false : newEP > CONFIG.epAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (WaterManipulationSkill.hasWater(entity)) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         this.shootBlade(instance, entity, mode);
      } else {
         IAbility ability = TensuraStorages.getAbilityFrom(entity);
         if (ability.getWaterPoint() < 1.0) {
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         ability.setWaterPoint(ability.getWaterPoint() - 1.0);
         this.shootBlade(instance, entity, mode);
         ability.markDirty();
      }
   }

   private void shootBlade(ManasSkillInstance instance, LivingEntity entity, int mode) {
      instance.addMasteryPoint(entity);
      WaterBladeProjectile blade = new WaterBladeProjectile(entity.level(), entity);
      blade.setSpeed(CONFIG.speedMultiplier);
      blade.setDamage(CONFIG.damage);
      blade.setSkill(entity, instance, this, mode);
      blade.setPosAndShoot(entity);
      entity.level().addFreshEntity(blade);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }
}
