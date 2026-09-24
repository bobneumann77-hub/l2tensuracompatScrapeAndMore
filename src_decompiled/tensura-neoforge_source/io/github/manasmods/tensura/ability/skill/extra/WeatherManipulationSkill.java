package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class WeatherManipulationSkill extends Skill {
   public static final ExtraSkillConfig.WeatherManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).WeatherManipulation;

   public WeatherManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "weather_manipulation.clear";
         case 1 -> "weather_manipulation.rain";
         case 2 -> "weather_manipulation.thunder";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCostManipulation;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((WeatherDominationSkill)ExtraSkills.WEATHER_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.level() instanceof ServerLevel level) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (entity.level().dimension().equals(Level.OVERWORLD)) {
               if (entity.level().canSeeSky(ObjectSelectionHelper.getBlockPos(entity.getEyePosition()))) {
                  boolean success = false;
                  switch (mode) {
                     case 0:
                        if (level.isRaining() || level.isThundering()) {
                           level.setWeatherParameters(12000, 0, false, false);
                           success = true;
                        }
                        break;
                     case 1:
                        if (!level.isRaining() || level.isThundering()) {
                           level.setWeatherParameters(0, 12000, true, false);
                           success = true;
                        }
                        break;
                     case 2:
                        if (!level.isThundering()) {
                           level.setWeatherParameters(0, 12000, true, true);
                           success = true;
                        }
                  }

                  if (success) {
                     instance.addMasteryPoint(entity);
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(), (ParticleOptions)TensuraParticleTypes.WEATHER_MANIPULATION.get(), entity.getX(), entity.getY(), entity.getZ(), true
                     );
                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.5F,
                        1.0F
                     );
                  }
               }
            }
         }
      }
   }

   public static void learnWeatherManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.WEATHER_MANIPULATION.get())) {
         if (instance.getSkill() == ExtraSkills.WATER_MANIPULATION.get()
            || SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.WATER_MANIPULATION.get())) {
            if (instance.getSkill() == ExtraSkills.WIND_MANIPULATION.get()
               || SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get())) {
               if (instance.getSkill() == ExtraSkills.LIGHTNING_MANIPULATION.get()
                  || SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.LIGHTNING_MANIPULATION.get())) {
                  SkillHelper.learnSkill(entity, ((WeatherManipulationSkill)ExtraSkills.WEATHER_MANIPULATION.get()).createLearningInstance(entity));
               }
            }
         }
      }
   }
}
