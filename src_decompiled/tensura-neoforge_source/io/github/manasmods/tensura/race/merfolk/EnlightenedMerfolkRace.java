package io.github.manasmods.tensura.race.merfolk;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.MerfolkConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public class EnlightenedMerfolkRace extends MerfolkRace {
   public EnlightenedMerfolkRace(Difficulty difficulty) {
      super(difficulty);
   }

   public EnlightenedMerfolkRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
      this.addAttributeModifier(
         Attributes.SUBMERGED_MINING_SPEED,
         DEFAULT_RACE_ID,
         ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).EnlightenedMerfolk.submergedMiningSpeed,
         Operation.ADD_VALUE
      );
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).EnlightenedMerfolk;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.MERFOLK_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.MERFOLK_SAINT.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.MERFOLK.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.EPRequirement(((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).EnlightenedMerfolk.epRequirement), 100.0F
      );
   }
}
