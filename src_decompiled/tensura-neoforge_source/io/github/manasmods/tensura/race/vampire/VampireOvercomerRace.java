package io.github.manasmods.tensura.race.vampire;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.VampireConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class VampireOvercomerRace extends VampireRace {
   public VampireOvercomerRace(Difficulty difficulty) {
      super(difficulty);
   }

   public VampireOvercomerRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).VampireOvercomer;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.VAMPIRE_LORD.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.VAMPIRE_LORD.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.VAMPIRE.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.EPRequirement(((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).VampireOvercomer.epRequirement), 100.0F
      );
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.BLOOD_MIST.get());
      return list;
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (isUnderSun(entity)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, true, false, true));
      } else if (entity.level().isNight() && entity.level().getMoonPhase() == 4) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2, true, false, true));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 40, 2, true, false, true));
      }
   }

   public static boolean isUnderSun(LivingEntity entity) {
      if (!entity.isAlive()) {
         return false;
      }

      if (entity.hasInfiniteMaterials()) {
         return false;
      }

      if (!entity.level().isDay()) {
         return false;
      }

      if (SkillUtils.shouldCancelInteraction(entity)) {
         return false;
      }

      float f = entity.getLightLevelDependentMagicValue();
      boolean inWater = entity.isInLiquid() && (entity.isInWaterOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow);
      boolean flag = inWater || entity.isInWaterOrRain();
      return f > 0.5F && !flag && entity.level().canSeeSky(ObjectSelectionHelper.getBlockPos(entity.getEyePosition()));
   }
}
