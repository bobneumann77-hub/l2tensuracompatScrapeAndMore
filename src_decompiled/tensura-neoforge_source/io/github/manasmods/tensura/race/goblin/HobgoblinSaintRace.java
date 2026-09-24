package io.github.manasmods.tensura.race.goblin;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.GoblinConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class HobgoblinSaintRace extends EnlightenedHobgoblinRace {
   public HobgoblinSaintRace(Difficulty difficulty) {
      super(difficulty);
   }

   public HobgoblinSaintRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((GoblinConfig)ConfigRegistry.getConfig(GoblinConfig.class)).HobgoblinSaint;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.HOLY;
   }

   @Nullable
   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_ONI.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_ONI.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DIVINE_ONI.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ENLIGHTENED_HOBGOBLIN.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      GoblinConfig.HobgoblinSaint config = ((GoblinConfig)ConfigRegistry.getConfig(GoblinConfig.class)).HobgoblinSaint;
      return Map.of(
         new EvolutionRequirement.EPRequirement(config.epRequirement), 50.0F, new EvolutionRequirement.BossRequirement(config.bossRequirement), 50.0F
      );
   }
}
