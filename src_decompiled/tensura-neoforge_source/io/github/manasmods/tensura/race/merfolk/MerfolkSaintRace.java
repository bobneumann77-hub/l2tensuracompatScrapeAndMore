package io.github.manasmods.tensura.race.merfolk;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.MerfolkConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public class MerfolkSaintRace extends EnlightenedMerfolkRace {
   public MerfolkSaintRace(Difficulty difficulty) {
      super(difficulty);
   }

   public MerfolkSaintRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
      this.addAttributeModifier(
         Attributes.SUBMERGED_MINING_SPEED,
         DEFAULT_RACE_ID,
         ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).MerfolkSaint.submergedMiningSpeed,
         Operation.ADD_VALUE
      );
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).MerfolkSaint;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.HOLY;
   }

   @Nullable
   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_FISH.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_FISH.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DIVINE_FISH.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      MerfolkConfig.MerfolkSaint config = ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).MerfolkSaint;
      return Map.of(
         new EvolutionRequirement.EPRequirement(config.epRequirement), 50.0F, new EvolutionRequirement.BossRequirement(config.bossRequirement), 50.0F
      );
   }
}
