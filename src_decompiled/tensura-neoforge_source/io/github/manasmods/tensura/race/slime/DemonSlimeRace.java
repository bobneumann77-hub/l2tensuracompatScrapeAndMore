package io.github.manasmods.tensura.race.slime;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.SlimeConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DemonSlimeRace extends SlimeRace {
   public DemonSlimeRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DemonSlimeRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
      SlimeConfig.DemonSlime config = ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).DemonSlime;
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, DEFAULT_RACE_ID, config.jumpStrength, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, 2.0 * config.jumpStrength, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, DEFAULT_RACE_ID, config.fallDamage, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.WIDTH_MULTIPLIER, DEFAULT_RACE_ID, this.getCustomWidth() - 1.0, Operation.ADD_VALUE);
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).DemonSlime;
   }

   @Override
   protected int getMaxJumpCharge() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).DemonSlime.maxChargeTick;
   }

   @Override
   public double getCustomWidth() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).DemonSlime.width;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.GOD_SLIME.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.GOD_SLIME.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.SLIME.get(), (ManasRace)TensuraRaces.METAL_SLIME.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.AwakenRequirement(), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.POSSESSION.get());
      list.add((ManasSkill)ExtraSkills.INFINITE_REGENERATION.get());
      list.add((ManasSkill)ExtraSkills.UNIVERSAL_PERCEPTION.get());
      list.add((ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get());
      list.add((ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.COLD_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.CORROSION_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.EARTH_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.ELECTRICITY_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.GRAVITY_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.HEAT_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.LIGHT_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.SPATIAL_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.WATER_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.WIND_ATTACK_RESISTANCE.get());
      return list;
   }
}
