package io.github.manasmods.tensura.config.ability.skill;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.List;

public class ResistanceConfig extends ManasConfig {
   @Comment(
      "How many times of current HP that incoming damage value needs to be higher to deal damage to the user with Resistances\n-1 = always applied regardless of HP"
   )
   public double hpDamageBypassResistance = 0.5;
   @Comment("The multiplier that will be applied on incoming damage when the damage went through Resistances")
   public double resistanceDamageMultiplier = 0.5;
   @Comment(
      "How many times of current HP that incoming damage value needs to be higher to deal damage to the user with Nullifications\n-1 = always applied regardless of HP"
   )
   public double hpDamageBypassNullification = -1.0;
   @Comment("The multiplier that will be applied on incoming damage when the damage went through Nullifications")
   public double nullificationDamageMultiplier = 0.0;
   @Comment("The multiplier of learning point requirement for damage points that the skill to reach for 1 learning point")
   public double damagePointMultiplier = 10.0;
   public ResistanceConfig.EasyResistance EasyResistance = new ResistanceConfig.EasyResistance();
   public ResistanceConfig.MediumResistance MediumResistance = new ResistanceConfig.MediumResistance();
   public ResistanceConfig.HardResistance HardResistance = new ResistanceConfig.HardResistance();

   public String getFileName() {
      return "tensura/ability/skill/resistance_config";
   }

   public static class EasyResistance extends ManasSubConfig {
      @Comment("The List of Resistance that are considered to be easy to acquire")
      public List<String> easyResistances = List.of("tensura:corrosion_resistance", "tensura:poison_resistance", "tensura:paralysis_resistance");
      @Comment("The amount of damage that a player need to take at once to acquire 1 learning point for an easy Resistance")
      public double easyResistanceDamageRequirement = 5.0;
      @Comment("The amount of learning points that a player need to have to acquire an easy Resistance")
      public int easyResistancePointRequirement = 350;
   }

   public static class HardResistance extends ManasSubConfig {
      @Comment("The List of Resistance that are considered to be hard to acquire")
      public List<String> hardResistances = List.of(
         "tensura:physical_attack_resistance", "tensura:spiritual_attack_resistance", "tensura:abnormal_condition_resistance", "tensura:magic_resistance"
      );
      @Comment("The amount of damage that a player need to take at once to acquire 1 learning point for a hard Resistance")
      public double hardResistanceDamageRequirement = 20.0;
      @Comment("The amount of learning points that a player need to have to acquire a hard Resistance")
      public int hardResistancePointRequirement = 700;
   }

   public static class MediumResistance extends ManasSubConfig {
      @Comment("The List of Resistance that are considered to be medium to acquire")
      public List<String> mediumResistances = List.of(
         "tensura:cold_resistance",
         "tensura:heat_resistance",
         "tensura:darkness_attack_resistance",
         "tensura:earth_attack_resistance",
         "tensura:flame_attack_resistance",
         "tensura:light_attack_resistance",
         "tensura:spatial_attack_resistance",
         "tensura:water_attack_resistance",
         "tensura:wind_attack_resistance",
         "tensura:gravity_attack_resistance",
         "tensura:holy_attack_resistance",
         "tensura:electricity_resistance",
         "tensura:pain_resistance",
         "tensura:thermal_fluctuation_resistance"
      );
      @Comment("The amount of damage that a player need to take at once to acquire 1 learning point for a medium Resistance")
      public double mediumResistanceDamageRequirement = 15.0;
      @Comment("The amount of learning points that a player need to have to acquire a medium Resistance")
      public int mediumResistancePointRequirement = 500;
   }
}
