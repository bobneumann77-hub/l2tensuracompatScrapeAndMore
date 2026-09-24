package io.github.manasmods.tensura.race.orc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.OrcConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class OrcDisasterRace extends OrcLordRace {
   public OrcDisasterRace(Difficulty difficulty) {
      super(difficulty);
   }

   public OrcDisasterRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OrcConfig)ConfigRegistry.getConfig(OrcConfig.class)).OrcDisaster;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_BOAR.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.SPIRIT_BOAR.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ORC_LORD.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.EPRequirement(((OrcConfig)ConfigRegistry.getConfig(OrcConfig.class)).OrcDisaster.epRequirement),
         50.0F,
         new EvolutionRequirement.AbilityRequirement((ManasSkill)UniqueSkills.STARVED.get(), true),
         50.0F
      );
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 40, 4, false, false, false));
      }
   }
}
