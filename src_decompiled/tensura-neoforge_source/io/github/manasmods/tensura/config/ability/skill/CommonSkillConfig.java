package io.github.manasmods.tensura.config.ability.skill;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class CommonSkillConfig extends ManasConfig {
   public CommonSkillConfig.Coercion Coercion = new CommonSkillConfig.Coercion();
   public CommonSkillConfig.Corrosion Corrosion = new CommonSkillConfig.Corrosion();
   public CommonSkillConfig.FarSight FarSight = new CommonSkillConfig.FarSight();
   public CommonSkillConfig.GravityField GravityField = new CommonSkillConfig.GravityField();
   public CommonSkillConfig.GravityFlight GravityFlight = new CommonSkillConfig.GravityFlight();
   public CommonSkillConfig.HydraulicPropulsion HydraulicPropulsion = new CommonSkillConfig.HydraulicPropulsion();
   public CommonSkillConfig.Paralysis Paralysis = new CommonSkillConfig.Paralysis();
   public CommonSkillConfig.Poison Poison = new CommonSkillConfig.Poison();
   public CommonSkillConfig.RangedBarrier RangedBarrier = new CommonSkillConfig.RangedBarrier();
   public CommonSkillConfig.SelfRegeneration SelfRegeneration = new CommonSkillConfig.SelfRegeneration();
   public CommonSkillConfig.Strength Strength = new CommonSkillConfig.Strength();
   public CommonSkillConfig.Telepathy Telepathy = new CommonSkillConfig.Telepathy();
   public CommonSkillConfig.ThoughtCommunication ThoughtCommunication = new CommonSkillConfig.ThoughtCommunication();
   public CommonSkillConfig.VoiceCannon VoiceCannon = new CommonSkillConfig.VoiceCannon();
   public CommonSkillConfig.WaterBlade WaterBlade = new CommonSkillConfig.WaterBlade();
   public CommonSkillConfig.WaterCurrentControl WaterCurrentControl = new CommonSkillConfig.WaterCurrentControl();

   public String getFileName() {
      return "tensura/ability/skill/common_config";
   }

   public static class Coercion extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 5000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 50.0;
      @Comment("The attack range of the roar in blocks.")
      public double roarRange = 14.0;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.25;
      @Comment("The duration in tick of the Fear effect.")
      public int fearDuration = 200;
      @Comment("The Cooldown in second after activation (halved when mastered).")
      public int cooldown = 2;
   }

   public static class Corrosion extends ManasSubConfig {
      @Comment("Rotten flesh eaten Optional Requirement for Learning.")
      public double rottenFleshAcquirement = 100.0;
      @Comment("Tempest Serpent beaten Optional Requirement for Learning.")
      public double serpentAcquirement = 100.0;
      @Comment("Orc Lord/Disaster beaten Optional Requirement for Learning.")
      public double orcAcquirement = 1.0;
      @Comment("The duration in tick of the Corrosion/Wither effect.")
      public int corrosionDuration = 200;
      @Comment("The level of the Corrosion/Wither effect.")
      public int corrosionLevel = 1;
   }

   public static class FarSight extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 5000.0;
      @Comment("The Max Zoom Multiplier.")
      public int maxZoom = 50;
      @Comment("The Max Bonus Presence Sense Radius when mastered.")
      public int maxSenseRadius = 60;
   }

   public static class GravityField extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 20000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 50.0;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 5;
      @Comment("The duration in tick of the Gravity Field.")
      public int fieldDuration = 1200;
      @Comment("The radius of the Gravity Field.")
      public int fieldRadius = 6;
      @Comment("The radius of the Gravity Field when mastered.")
      public int fieldRadiusMastered = 10;
      @Comment("The level of the speed/slowness effect when applied.")
      public int speedLevel = 2;
      @Comment("The level of the slow-fall/burden effect when applied.")
      public int slowFallLevel = 1;
   }

   public static class GravityFlight extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 10000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 5.0;
      @Comment("The multiplier of the going-up speed when hold down.")
      public int upMultiplier = 1;
   }

   public static class HydraulicPropulsion extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 10000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 2.0;
      @Comment("The level of the riptide boost when activated.")
      public int riptideLevel = 3;
      @Comment("The duration in tick of the riptide boost when activated.")
      public int riptideDuration = 10;
      @Comment("The damage multiplier compared to the user's attack when hit target during Riptide boost.")
      public int riptideMultiplier = 1;
   }

   public static class Paralysis extends ManasSubConfig {
      @Comment("Evil Centipede beaten Requirement for Learning.")
      public int centipedeAcquirement = 500;
      @Comment("The duration in tick of the Paralysis effect.")
      public int paralysisDuration = 200;
      @Comment("The level of the Paralysis effect.")
      public int paralysisLevel = 1;
      @Comment("The level of the Paralysis effect when Mastered.")
      public int paralysisLevelMastered = 2;
   }

   public static class Poison extends ManasSubConfig {
      @Comment("Spider Eye eaten Optional Requirement for Learning.")
      public double spiderEyeAcquirement = 100.0;
      @Comment("Black Spider beaten Optional Requirement for Learning.")
      public int spiderAcquirement = 100;
      @Comment("The duration in tick of the Poison effect.")
      public int poisonDuration = 200;
      @Comment("The level of the Poison effect.")
      public int poisonLevel = 1;
   }

   public static class RangedBarrier extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 80000.0;
      @Comment("Ifrit beaten Requirement for Learning.")
      public int ifritAcquirement = 1;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 5.0;
      @Comment("The max activation range for the barriers in blocks.")
      public double barrierRange = 30.0;
      @Comment("The duration of the barrier when activated (doubled when mastered).")
      public int barrierDuration = 1200;
      @Comment("The radius of the barrier in the first mode.")
      public int barrierRadius = 2;
      @Comment("The radius of the barrier in the second mode.")
      public int barrierRadiusSecond = 5;
      @Comment("The radius of the barrier in the third mode.")
      public int barrierRadiusThird = 10;
   }

   public static class SelfRegeneration extends ManasSubConfig {
      @Comment("Slime beaten Requirement for Learning.")
      public int slimeAcquirement = 500;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 100.0;
      @Comment("The level of the self-regeneration effect.")
      public int regenLevel = 1;
      @Comment("The level of the self-regeneration effect when mastered.")
      public int regenLevelMastered = 2;
      @Comment("How much HP to regenerate each second per level.")
      public int regenHP = 2;
      @Comment("How much SHP to regenerate each second per level when mastered.")
      public int regenSHP = 4;
   }

   public static class Strength extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 3000.0;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 30.0;
      @Comment("The duration of the Strengthen effect when activated.")
      public int strengthenDuration = 1200;
      @Comment("The duration of the Strengthen effect when activated with mastery.")
      public int strengthenDurationMastered = 2400;
      @Comment("The level of the Strengthen effect when activated (+3 Attack Damage per level).")
      public int strengthenLevel = 1;
      @Comment("The level of the Strengthen effect when activated with Mastered.")
      public int strengthenLevelMastered = 2;
      @Comment("The Cooldown in second of the skill.")
      public int cooldown = 3;
   }

   public static class Telepathy extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 2000.0;
      @Comment("The radius of the Telepathy activation.")
      public int telepathyRadius = 30;
   }

   public static class ThoughtCommunication extends ManasSubConfig {
      @Comment("The radius of the Telepathy activation.")
      public int telepathyRadius = 30;
   }

   public static class VoiceCannon extends ManasSubConfig {
      @Comment("EP Requirement for Learning when Coercion mastered.")
      public double epAcquirement = 10000.0;
      @Comment("Magicule Cost to activate .")
      public double magiculeCost = 500.0;
      @Comment("The damage of the Voice Cannon when hit a target.")
      public int cannonDamage = 20;
      @Comment("The damage of the Voice Cannon when hit a target with mastery.")
      public int cannonDamageMastered = 30;
      @Comment("The range in block of the Voice Cannon.")
      public int cannonRange = 15;
      @Comment("The range in block of the Voice Cannon when mastered.")
      public int cannonRangeMastered = 20;
      @Comment("The cooldown in second after activating Voice Cannon.")
      public int cannonCooldown = 3;
   }

   public static class WaterBlade extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 10000.0;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 10.0;
      @Comment("The damage of the Water Blade.")
      public float damage = 40.0F;
      @Comment("The speed multiplier of the Water Blade.")
      public int speedMultiplier = 5;
   }

   public static class WaterCurrentControl extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 6000.0;
      @Comment("The Swim Speed Multiplier when activated.")
      public double swimBoost = 4.0;
   }
}
