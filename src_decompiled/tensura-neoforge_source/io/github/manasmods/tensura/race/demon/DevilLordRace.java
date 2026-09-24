package io.github.manasmods.tensura.race.demon;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.config.race.DaemonConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class DevilLordRace extends DaemonLordRace {
   public DevilLordRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DevilLordRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((DaemonConfig)ConfigRegistry.getConfig(DaemonConfig.class)).DevilLord;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of();
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DAEMON_LORD.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.AwakenRequirement(),
         50.0F,
         new EvolutionRequirement.NamedRequirement(),
         25.0F,
         new EvolutionRequirement.PhysicalBodyRequirement(),
         25.0F
      );
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DIVINE_KI_RELEASE.get());
      return list;
   }

   @Override
   public List<TensuraSkill> getIntrinsicLearnable(ManasRaceInstance instance, LivingEntity entity) {
      return ((DaemonConfig)ConfigRegistry.getConfig(DaemonConfig.class))
         .DevilLord
         .learnableMagics
         .stream()
         .map(id -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(id)))
         .filter(skill -> skill instanceof TensuraSkill)
         .map(skill -> (TensuraSkill)skill)
         .toList();
   }
}
