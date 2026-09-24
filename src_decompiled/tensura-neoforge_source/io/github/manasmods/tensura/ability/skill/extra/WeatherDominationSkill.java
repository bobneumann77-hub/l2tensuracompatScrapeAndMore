package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WeatherDominationSkill extends Skill {
   public WeatherDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.WEATHER_MANIPULATION.get())
         ? false
         : newEP > WeatherManipulationSkill.CONFIG.dominationEpAcquirement;
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
      return WeatherManipulationSkill.CONFIG.magiculeCostDomination;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.level() instanceof ServerLevel level) {
         if (entity.level().dimension().equals(Level.OVERWORLD)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               boolean sucess = false;
               switch (mode) {
                  case 0:
                     if (level.isRaining() || level.isThundering()) {
                        level.setWeatherParameters(24000, 0, false, false);
                        sucess = true;
                     }
                     break;
                  case 1:
                     if (!level.isRaining() || level.isThundering()) {
                        level.setWeatherParameters(0, 24000, true, false);
                        sucess = true;
                     }
                     break;
                  case 2:
                     if (!level.isThundering()) {
                        level.setWeatherParameters(0, 24000, true, true);
                        sucess = true;
                     }
               }

               if (sucess) {
                  instance.addMasteryPoint(entity);
                  instance.setCoolDown(
                     instance.isMastered(entity)
                        ? WeatherManipulationSkill.CONFIG.cooldownDominationMastered
                        : WeatherManipulationSkill.CONFIG.cooldownDomination,
                     mode
                  );
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
