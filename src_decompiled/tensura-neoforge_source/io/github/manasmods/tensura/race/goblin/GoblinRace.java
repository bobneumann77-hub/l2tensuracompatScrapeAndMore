package io.github.manasmods.tensura.race.goblin;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.race.GoblinConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GoblinRace extends DefaultRace {
   public GoblinRace(Difficulty difficulty) {
      super(difficulty);
   }

   public GoblinRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((GoblinConfig)ConfigRegistry.getConfig(GoblinConfig.class)).Goblin;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HOBGOBLIN.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HOBGOBLIN_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HOBGOBLIN.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HOBGOBLIN.get());
   }

   @Override
   public boolean hasGuaranteeElemental() {
      return true;
   }

   @Override
   public double getElementalSpiritsChance(Element elemental, SpiritualMagic.SpiritLevel level) {
      if (elemental.equals(Element.FLAME)) {
         return level.equals(SpiritualMagic.SpiritLevel.LESSER) ? 100.0 : super.getElementalSpiritsChance(elemental, level) * 2.0;
      } else {
         return super.getElementalSpiritsChance(elemental, level);
      }
   }
}
