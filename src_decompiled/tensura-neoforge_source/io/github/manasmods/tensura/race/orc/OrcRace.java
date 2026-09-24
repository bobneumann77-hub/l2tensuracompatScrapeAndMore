package io.github.manasmods.tensura.race.orc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.OrcConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class OrcRace extends DefaultRace {
   public OrcRace(Difficulty difficulty) {
      super(difficulty);
   }

   public OrcRace() {
      this(Difficulty.INTERMEDIATE);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OrcConfig)ConfigRegistry.getConfig(OrcConfig.class)).Orc;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HIGH_ORC.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_BOAR.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HIGH_ORC.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HIGH_ORC.get());
   }
}
