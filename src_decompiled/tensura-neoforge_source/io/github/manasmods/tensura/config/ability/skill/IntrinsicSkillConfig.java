package io.github.manasmods.tensura.config.ability.skill;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class IntrinsicSkillConfig extends ManasConfig {
   public IntrinsicSkillConfig.AbsorbDissolve AbsorbDissolve = new IntrinsicSkillConfig.AbsorbDissolve();
   public IntrinsicSkillConfig.BeastTransformation BeastTransformation = new IntrinsicSkillConfig.BeastTransformation();
   public IntrinsicSkillConfig.BloodMist BloodMist = new IntrinsicSkillConfig.BloodMist();
   public IntrinsicSkillConfig.BodyArmor BodyArmor = new IntrinsicSkillConfig.BodyArmor();
   public IntrinsicSkillConfig.Charm Charm = new IntrinsicSkillConfig.Charm();
   public IntrinsicSkillConfig.DivineKiRelease DivineKiRelease = new IntrinsicSkillConfig.DivineKiRelease();
   public IntrinsicSkillConfig.ElementalTransform ElementalTransform = new IntrinsicSkillConfig.ElementalTransform();
   public IntrinsicSkillConfig.DragonEar DragonEar = new IntrinsicSkillConfig.DragonEar();
   public IntrinsicSkillConfig.DragonEye DragonEye = new IntrinsicSkillConfig.DragonEye();
   public IntrinsicSkillConfig.DragonMode DragonMode = new IntrinsicSkillConfig.DragonMode();
   public IntrinsicSkillConfig.DragonSkin DragonSkin = new IntrinsicSkillConfig.DragonSkin();
   public IntrinsicSkillConfig.Drain Drain = new IntrinsicSkillConfig.Drain();
   public IntrinsicSkillConfig.EyeOfTruth EyeOfTruth = new IntrinsicSkillConfig.EyeOfTruth();
   public IntrinsicSkillConfig.FlameBreath FlameBreath = new IntrinsicSkillConfig.FlameBreath();
   public IntrinsicSkillConfig.Giantification Giantification = new IntrinsicSkillConfig.Giantification();
   public IntrinsicSkillConfig.IceBreath IceBreath = new IntrinsicSkillConfig.IceBreath();
   public IntrinsicSkillConfig.OgreBerserker OgreBerserker = new IntrinsicSkillConfig.OgreBerserker();
   public IntrinsicSkillConfig.ParalysisBreath ParalysisBreath = new IntrinsicSkillConfig.ParalysisBreath();
   public IntrinsicSkillConfig.PoisonousBreath PoisonousBreath = new IntrinsicSkillConfig.PoisonousBreath();
   public IntrinsicSkillConfig.Possession Possession = new IntrinsicSkillConfig.Possession();
   public IntrinsicSkillConfig.ScaleArmor ScaleArmor = new IntrinsicSkillConfig.ScaleArmor();
   public IntrinsicSkillConfig.ThunderBreath ThunderBreath = new IntrinsicSkillConfig.ThunderBreath();
   public IntrinsicSkillConfig.Titanification Titanification = new IntrinsicSkillConfig.Titanification();
   public IntrinsicSkillConfig.UltrasonicWaves UltrasonicWaves = new IntrinsicSkillConfig.UltrasonicWaves();
   public IntrinsicSkillConfig.Unpredictability Unpredictability = new IntrinsicSkillConfig.Unpredictability();
   public IntrinsicSkillConfig.WaterBreathing WaterBreathing = new IntrinsicSkillConfig.WaterBreathing();

   public String getFileName() {
      return "tensura/ability/skill/intrinsic_config";
   }

   public static class AbsorbDissolve extends ManasSubConfig {
      @Comment("The multiplier of magicule gained from dissolving items.")
      public double magiculeMultiplier = 1.0;
      @Comment("The multiplier of health healed from dissolving items.")
      public float healthMultiplier = 1.0F;
      @Comment("The bonus max HP that Slime players can get from each Slime Core used to increase their size.")
      public float bonusHP = 5.0F;
      @Comment("The bonus max size that Slime players can get from each Slime Core used to increase their size.")
      public double bonusSize = 0.1;
      @Comment("The max bonus size that Slime players can get from consuming Slime Cores.")
      public double maxSize = 0.75;
   }

   public static class BeastTransformation extends ManasSubConfig {
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 1200;
   }

   public static class BloodMist extends ManasSubConfig {
      @Comment("EP Requirement to use Blood Ray.")
      public double rayAcquirement = 500000.0;
      @Comment("Magicule Cost to activate the Default Mode.")
      public double magiculeCost = 500.0;
      @Comment("Magicule Cost to activate Blood Ray.")
      public double rayMagiculeCost = 10000.0;
      @Comment("The spawn range in block of the blood mist.")
      public int mistRange = 10;
      @Comment("The damage per second of the blood mist.")
      public int mistDamage = 10;
      @Comment("The radius of the blood mist.")
      public int mistRadius = 5;
      @Comment("The cooldown of the blood mist (halved with mastery).")
      public int mistCooldown = 4;
      @Comment("The spawn range in block of the blood ray.")
      public int rayRange = 40;
      @Comment("The damage per second of the blood ray.")
      public int rayDamage = 50;
      @Comment("How much HP the user loses every 10 tick of using blood ray.")
      public int rayHPCost = 10;
   }

   public static class BodyArmor extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 50.0;
   }

   public static class Charm extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 80.0;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 5;
      @Comment("The range in block for targeting an entity.")
      public double range = 5.0;
      @Comment("The multiplier of the user's EP that the target's EP needs to be below for full mind control.")
      public float fullMultiplier = 0.25F;
      @Comment("The multiplier of the user's EP that the target's EP needs to be below to become neutral toward the user.")
      public float neutralMultiplier = 0.5F;
      @Comment("The multiplier of the user's EP that the target's Spiritual Attack Resistance reduces onto the EP requirement.")
      public float resistedMultiplier = 0.2F;
      @Comment("The duration in tick of the Mind Control effect (-1 = permanent).")
      public int controlDuration = 6000;
      @Comment("The duration in tick of the Mind Control effect when mastered (-1 = permanent).")
      public int controlDurationMastered = 12000;
   }

   public static class DivineKiRelease extends ManasSubConfig {
      @Comment("The bonus battlewill damage when toggled.")
      public float battlewillDamage = 50.0F;
      @Comment("The bonus battlewill damage when toggled with mastery.")
      public float battlewillDamageMastered = 100.0F;
      @Comment("The multiplier of the durability break of the target's equipments when damaged by user's physical/battlewill attack.")
      public float durabilityBreak = 3.0F;
      @Comment("The multiplier of the durability break of the target's equipments when damaged by user's physical/battlewill attack when mastered.")
      public float durabilityBreakMastered = 5.0F;
   }

   public static class DragonEar extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 0.0;
      @Comment("The bonus Presence Sense Radius when activated.")
      public int bonusRadius = 30;
   }

   public static class DragonEye extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 0.0;
      @Comment("The Max Zoom Multiplier.")
      public int maxZoom = 50;
      @Comment("The Max Bonus Presence Sense Radius when activated.")
      public int maxSenseRadius = 100;
      @Comment("The Bonus Presence Sense Level when activated.")
      public int senseLevel = 1;
      @Comment("The Bonus Presence Sense Level when activated with Mastery.")
      public int senseLevelMastered = 2;
   }

   public static class DragonMode extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 0.0;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 1200;
   }

   public static class DragonSkin extends ManasSubConfig {
      @Comment("The amount of EP needed for Hihi'irokane Armor's stats.")
      public double hihiirokaneEP = 800000.0;
      @Comment("The amount of EP needed for Adamantite Armor's stats.")
      public double adamantiteEP = 400000.0;
      @Comment("The amount of EP needed for Pure Magisteel Armor's stats.")
      public double magisteelEP = 50000.0;
   }

   public static class Drain extends ManasSubConfig {
      @Comment("The activation range of the blood drain.")
      public double range = 3.0;
      @Comment("The amount of HP that the user will drain from the target.")
      public float drainAmount = 6.0F;
      @Comment("The duration in second that the temporary obtained skill will stay with the user.")
      public int temporaryDuration = 80;
   }

   public static class ElementalTransform extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 30.0;
      @Comment("The movement speed multiplier of the user when activated.")
      public double speedMultiplier = 0.5;
      @Comment("The level of Spiritual Magic the user learns upon obtaining this skill.")
      public double spiritLevel = 2.0;
      @Comment("The radius in block of the elemental effect upon targets around the user.")
      public double radius = 5.0;
      @Comment("The elemental damage amount per second on targets when activated.")
      public float damage = 2.0F;
      @Comment("The duration in tick of the status effects on targets when activated.")
      public int effectDuration = 160;
   }

   public static class EyeOfTruth extends ManasSubConfig {
      @Comment("The level Presence Sense when activated.")
      public int senseLevel = 4;
   }

   public static class FlameBreath extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 30.0;
      @Comment("The damage each second of the Flame Breath.")
      public int damage = 8;
      @Comment("The damage each second of the Flame Breath when mastered.")
      public int damageMastered = 16;
   }

   public static class Giantification extends ManasSubConfig {
      @Comment("The attack damage buff the user gains when activated.")
      public double damage = 6.0;
      @Comment("The maximum size change in block when activated.")
      public int maxSize = 3;
      @Comment("The minimum size change in block when activated with Mastery.")
      public int minSize = -1;
      @Comment("The interaction range multiplier to apply with size when activated.")
      public float rangeMultiplier = 1.5F;
   }

   public static class IceBreath extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 30.0;
      @Comment("The damage each second of the Ice Breath.")
      public int damage = 8;
      @Comment("The damage each second of the Ice Breath when mastered.")
      public int damageMastered = 16;
   }

   public static class OgreBerserker extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 5000.0;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 600;
   }

   public static class ParalysisBreath extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate (halved when mastered).")
      public double magiculeCost = 50.0;
      @Comment("The damage each second of the Paralysis Breath.")
      public int damage = 2;
      @Comment("The level of the Paralysis effect when applied.")
      public int paralysisLevel = 3;
      @Comment("The duration in tick of the Paralysis effect when applied.")
      public int paralysisDuration = 200;
   }

   public static class PoisonousBreath extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate (halved when mastered).")
      public double magiculeCost = 100.0;
      @Comment("The damage each second of the Poison Breath.")
      public int damage = 15;
      @Comment("The damage each second of the Poison Breath when mastered.")
      public int damageMastered = 30;
      @Comment("The level of the Fatal Poison effect when applied.")
      public int poisonLevel = 1;
      @Comment("The duration in tick of the Fatal Poison effect when applied.")
      public int poisonDuration = 200;
   }

   public static class Possession extends ManasSubConfig {
      @Comment("The possession range in block.")
      public double range = 5.0;
      @Comment("The multiplier of the maximum Health that a target needs to be under to be possessed.")
      public double hpMultiplier = 0.1;
      @Comment("The multiplier of the maximum Spiritual Health that a target needs to be under to be possessed.")
      public double shpMultiplier = 0.1;
      @Comment("The multiplier of the user's maximum EP that a target needs to be under to be possessed.")
      public double epMultiplier = 0.25;
      @Comment("The multiplier of the possession requirement multipliers when the target has Spiritual Attack Resistance.")
      public double resistanceMultiplier = 0.5;
      @Comment("The maximum amount of HP the user can get from possessing an entity.")
      public double maxHealth = 1000.0;
      @Comment("The maximum amount of Attack Damage the user can get from possessing an entity.")
      public double maxAttack = 100.0;
      @Comment("The number of seconds that Possession Bodies will despawn. (0 = instant despawn, -1 = doesn't despawn)")
      public int bodyDespawnTick = 300;
   }

   public static class ScaleArmor extends ManasSubConfig {
      @Comment("The swimming speed multiplier that the user gains when toggled.")
      public int swimMultiplier = 2;
      @Comment("The number of armor points that the user gains when toggled.")
      public int armorPoint = 6;
   }

   public static class ThunderBreath extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate (halved when mastered).")
      public double magiculeCost = 50.0;
      @Comment("The damage each second of the Poison Breath.")
      public int damage = 10;
      @Comment("The damage each second of the Poison Breath when mastered.")
      public int damageMastered = 20;
   }

   public static class Titanification extends ManasSubConfig {
      @Comment("The attack damage buff the user gains when activated.")
      public double damage = 60.0;
      @Comment("The armor buff the user gains when activated.")
      public double armor = 20.0;
      @Comment("The maximum size change in block when activated.")
      public int maxSize = 6;
      @Comment("The minimum size change in block when activated with Mastery.")
      public int minSize = 0;
      @Comment("The interaction range multiplier to apply with size when activated.")
      public float rangeMultiplier = 1.75F;
      @Comment("The duration in second that Titanification effect lasts once activated.")
      public int duration = 120;
      @Comment("The cooldown in second once the effect runs out or deactivated.")
      public int cooldown = 300;
   }

   public static class UltrasonicWaves extends ManasSubConfig {
      @Comment("Magicule Cost to activate .")
      public double magiculeCost = 30.0;
      @Comment("The duration in tick of the Auditory Sense effect when activated.")
      public int auditoryDuration = 200;
      @Comment("The range in block of the Sonic Waves mode.")
      public int sonicRange = 8;
      @Comment("The damage of the Sonic Waves when hit a target.")
      public int sonicDamage = 8;
      @Comment("The cooldown in second after activating Sonic Waves (halved with mastery).")
      public int sonicCooldown = 3;
   }

   public static class Unpredictability extends ManasSubConfig {
      @Comment("The bonus chance to negate dodging when activated.")
      public int negateDodge = 100;
   }

   public static class WaterBreathing extends ManasSubConfig {
      @Comment("The level of the Water Breathing effect when activated.")
      public int waterBreathLevel = 3;
   }
}
