package io.github.manasmods.tensura.race.harpy;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.HarpyConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public class HarpyQueenRace extends HarpyRace {
   public HarpyQueenRace(Difficulty difficulty) {
      super(difficulty);
      this.addAttributeModifier(
         Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).HarpyQueen.safeFalling, Operation.ADD_VALUE
      );
   }

   public HarpyQueenRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).HarpyQueen;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_BIRD.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.SPIRIT_BIRD.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HARPY.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).HarpyQueen.epRequirement), 100.0F);
   }

   @Override
   protected float getUpwardBoost() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).HarpyQueen.flightBoost;
   }

   @Override
   protected int getFlightBoostCooldown() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).HarpyQueen.flightCooldown;
   }
}
