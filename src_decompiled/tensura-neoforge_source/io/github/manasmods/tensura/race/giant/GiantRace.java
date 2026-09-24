package io.github.manasmods.tensura.race.giant;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.GiantConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GiantRace extends DefaultRace {
   public GiantRace(Difficulty difficulty) {
      super(difficulty);
   }

   public GiantRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).Giant;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ANCIENT_GIANT.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ANCIENT_GIANT.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasRace> list = new ArrayList<>();
      list.add((ManasRace)TensuraRaces.ANCIENT_GIANT.get());
      return list;
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)IntrinsicSkills.GIANTIFICATION.get());
      list.add((ManasSkill)ExtraSkills.STEEL_STRENGTH.get());
      list.add((ManasSkill)ExtraSkills.STRENGTHEN_BODY.get());
      list.add((ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get());
      return list;
   }
}
