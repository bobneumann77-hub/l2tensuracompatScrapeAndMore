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

public class SpiritBirdRace extends HarpyQueenRace {
   public SpiritBirdRace(Difficulty difficulty) {
      super(difficulty);
      this.addAttributeModifier(
         Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).SpiritBird.safeFalling, Operation.ADD_VALUE
      );
   }

   public SpiritBirdRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).SpiritBird;
   }

   @Nullable
   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_BIRD.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_BIRD.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DIVINE_BIRD.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HARPY_QUEEN.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).SpiritBird.epRequirement), 100.0F);
   }

   @Override
   protected float getUpwardBoost() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).SpiritBird.flightBoost;
   }

   @Override
   protected int getFlightBoostCooldown() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).SpiritBird.flightCooldown;
   }
}
