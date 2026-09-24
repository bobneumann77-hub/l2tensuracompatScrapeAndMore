package io.github.manasmods.tensura.race.merfolk;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.race.MerfolkConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public class MerfolkRace extends DefaultRace {
   public MerfolkRace(Difficulty difficulty) {
      super(difficulty);
   }

   public MerfolkRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
      this.addAttributeModifier(
         Attributes.SUBMERGED_MINING_SPEED,
         DEFAULT_RACE_ID,
         ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).Merfolk.submergedMiningSpeed,
         Operation.ADD_VALUE
      );
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((MerfolkConfig)ConfigRegistry.getConfig(MerfolkConfig.class)).Merfolk;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.MERFOLK_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get());
   }

   @Override
   public boolean hasGuaranteeElemental() {
      return true;
   }

   @Override
   public double getElementalSpiritsChance(Element elemental, SpiritualMagic.SpiritLevel level) {
      if (elemental.equals(Element.WATER)) {
         return level.equals(SpiritualMagic.SpiritLevel.LESSER) ? 100.0 : super.getElementalSpiritsChance(elemental, level) * 2.0;
      } else {
         return super.getElementalSpiritsChance(elemental, level);
      }
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)IntrinsicSkills.WATER_BREATHING.get());
      list.add((ManasSkill)CommonSkills.HYDRAULIC_PROPULSION.get());
      return list;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      return !entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE) ? false : !entity.hasInfiniteMaterials() && !entity.isSpectator();
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.getAirSupply() >= entity.getMaxAirSupply() && shouldLoseMoistness(entity, instance)) {
         entity.setAirSupply(entity.getMaxAirSupply() - 10);
      }
   }

   public static boolean shouldLoseMoistness(LivingEntity entity, ManasRaceInstance race) {
      if (!entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         return false;
      } else if (entity.hasInfiniteMaterials()) {
         return false;
      } else if (entity.isSpectator()) {
         return false;
      } else if (race.is(TensuraRaceTags.NEED_MOIST)) {
         return !Alignment.shouldConsumeAir(entity) ? false : !entity.isInWaterRainOrBubble();
      } else {
         return false;
      }
   }
}
