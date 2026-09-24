package io.github.manasmods.tensura.race.elf;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.ElfConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class EnlightenedElfRace extends ElfRace {
   public EnlightenedElfRace(Difficulty difficulty) {
      super(difficulty);
   }

   public EnlightenedElfRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((ElfConfig)ConfigRegistry.getConfig(ElfConfig.class)).EnlightenedElf;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ELF_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ELF_SAINT.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ELF.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((ElfConfig)ConfigRegistry.getConfig(ElfConfig.class)).EnlightenedElf.epRequirement), 100.0F);
   }
}
