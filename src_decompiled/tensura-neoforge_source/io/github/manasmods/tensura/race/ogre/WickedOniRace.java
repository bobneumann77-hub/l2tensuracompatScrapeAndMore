package io.github.manasmods.tensura.race.ogre;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.OgreConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class WickedOniRace extends KijinRace {
   public WickedOniRace(Difficulty difficulty) {
      super(difficulty);
   }

   public WickedOniRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OgreConfig)ConfigRegistry.getConfig(OgreConfig.class)).WickedOni;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DEATH_ONI.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DEATH_ONI.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ENLIGHTENED_OGRE.get(), (ManasRace)TensuraRaces.KIJIN.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.ItemConsumeRequirement(
            (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(), ((OgreConfig)ConfigRegistry.getConfig(OgreConfig.class)).WickedOni.essenceRequirement
         ),
         100.0F
      );
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)CommonSkills.STRENGTH.get());
      list.add((ManasSkill)IntrinsicSkills.OGRE_BERSERKER.get());
      list.add((ManasSkill)ExtraSkills.ULTRASPEED_REGENERATION.get());
      return list;
   }
}
