package io.github.manasmods.tensura.race.dwarf;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.race.DwarfConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class DwarfRace extends DefaultRace {
   public DwarfRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DwarfRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((DwarfConfig)ConfigRegistry.getConfig(DwarfConfig.class)).Dwarf;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_DWARF.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DWARF_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_DWARF.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ENLIGHTENED_DWARF.get());
   }

   @Override
   public boolean hasGuaranteeElemental() {
      return true;
   }

   @Override
   public double getElementalSpiritsChance(Element elemental, SpiritualMagic.SpiritLevel level) {
      if (elemental.equals(Element.EARTH)) {
         return level.equals(SpiritualMagic.SpiritLevel.LESSER) ? 100.0 : super.getElementalSpiritsChance(elemental, level) * 2.0;
      } else {
         return super.getElementalSpiritsChance(elemental, level);
      }
   }
}
