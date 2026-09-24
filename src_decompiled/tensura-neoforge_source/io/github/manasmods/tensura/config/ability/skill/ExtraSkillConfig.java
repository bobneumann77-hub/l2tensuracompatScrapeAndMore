package io.github.manasmods.tensura.config.ability.skill;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class ExtraSkillConfig extends ManasConfig {
   public ExtraSkillConfig.AllSeeingEye AllSeeingEye = new ExtraSkillConfig.AllSeeingEye();
   public ExtraSkillConfig.AnalyticalAppraisal AnalyticalAppraisal = new ExtraSkillConfig.AnalyticalAppraisal();
   public ExtraSkillConfig.BlackFlame BlackFlame = new ExtraSkillConfig.BlackFlame();
   public ExtraSkillConfig.BlackLightning BlackLightning = new ExtraSkillConfig.BlackLightning();
   public ExtraSkillConfig.BodyDouble BodyDouble = new ExtraSkillConfig.BodyDouble();
   public ExtraSkillConfig.ChantAnnulment ChantAnnulment = new ExtraSkillConfig.ChantAnnulment();
   public ExtraSkillConfig.DangerSense DangerSense = new ExtraSkillConfig.DangerSense();
   public ExtraSkillConfig.DemonLordHaki DemonLordHaki = new ExtraSkillConfig.DemonLordHaki();
   public ExtraSkillConfig.Haki Haki = new ExtraSkillConfig.Haki();
   public ExtraSkillConfig.EarthManipulation EarthManipulation = new ExtraSkillConfig.EarthManipulation();
   public ExtraSkillConfig.FlameManipulation FlameManipulation = new ExtraSkillConfig.FlameManipulation();
   public ExtraSkillConfig.GodwolfSense GodwolfSense = new ExtraSkillConfig.GodwolfSense();
   public ExtraSkillConfig.GravityManipulation GravityManipulation = new ExtraSkillConfig.GravityManipulation();
   public ExtraSkillConfig.HeatWave HeatWave = new ExtraSkillConfig.HeatWave();
   public ExtraSkillConfig.HeavenlyEye HeavenlyEye = new ExtraSkillConfig.HeavenlyEye();
   public ExtraSkillConfig.HeroHaki HeroHaki = new ExtraSkillConfig.HeroHaki();
   public ExtraSkillConfig.InfiniteRegeneration InfiniteRegeneration = new ExtraSkillConfig.InfiniteRegeneration();
   public ExtraSkillConfig.LawManipulation LawManipulation = new ExtraSkillConfig.LawManipulation();
   public ExtraSkillConfig.LightningManipulation LightningManipulation = new ExtraSkillConfig.LightningManipulation();
   public ExtraSkillConfig.MagicAura MagicAura = new ExtraSkillConfig.MagicAura();
   public ExtraSkillConfig.MagicElementalTransform MagicElementalTransform = new ExtraSkillConfig.MagicElementalTransform();
   public ExtraSkillConfig.MagicJamming MagicJamming = new ExtraSkillConfig.MagicJamming();
   public ExtraSkillConfig.MagicSense MagicSense = new ExtraSkillConfig.MagicSense();
   public ExtraSkillConfig.Majesty Majesty = new ExtraSkillConfig.Majesty();
   public ExtraSkillConfig.ManaManipulation ManaManipulation = new ExtraSkillConfig.ManaManipulation();
   public ExtraSkillConfig.MolecularManipulation MolecularManipulation = new ExtraSkillConfig.MolecularManipulation();
   public ExtraSkillConfig.MortalFear MortalFear = new ExtraSkillConfig.MortalFear();
   public ExtraSkillConfig.MultilayerBarrier MultilayerBarrier = new ExtraSkillConfig.MultilayerBarrier();
   public ExtraSkillConfig.SacredHaki SacredHaki = new ExtraSkillConfig.SacredHaki();
   public ExtraSkillConfig.Sage Sage = new ExtraSkillConfig.Sage();
   public ExtraSkillConfig.SenseHeatSource SenseHeatSource = new ExtraSkillConfig.SenseHeatSource();
   public ExtraSkillConfig.ShadowMotion ShadowMotion = new ExtraSkillConfig.ShadowMotion();
   public ExtraSkillConfig.SnakeEye SnakeEye = new ExtraSkillConfig.SnakeEye();
   public ExtraSkillConfig.SoundManipulation SoundManipulation = new ExtraSkillConfig.SoundManipulation();
   public ExtraSkillConfig.SpatialManipulation SpatialManipulation = new ExtraSkillConfig.SpatialManipulation();
   public ExtraSkillConfig.SpatialMotion SpatialMotion = new ExtraSkillConfig.SpatialMotion();
   public ExtraSkillConfig.SteelStrength SteelStrength = new ExtraSkillConfig.SteelStrength();
   public ExtraSkillConfig.StickySteelThread StickySteelThread = new ExtraSkillConfig.StickySteelThread();
   public ExtraSkillConfig.StrengthenBody StrengthenBody = new ExtraSkillConfig.StrengthenBody();
   public ExtraSkillConfig.ThoughtAcceleration ThoughtAcceleration = new ExtraSkillConfig.ThoughtAcceleration();
   public ExtraSkillConfig.UltraInstinct UltraInstinct = new ExtraSkillConfig.UltraInstinct();
   public ExtraSkillConfig.UltraspeedRegeneration UltraspeedRegeneration = new ExtraSkillConfig.UltraspeedRegeneration();
   public ExtraSkillConfig.UniversalPerception UniversalPerception = new ExtraSkillConfig.UniversalPerception();
   public ExtraSkillConfig.WaterManipulation WaterManipulation = new ExtraSkillConfig.WaterManipulation();
   public ExtraSkillConfig.WeatherManipulation WeatherManipulation = new ExtraSkillConfig.WeatherManipulation();
   public ExtraSkillConfig.WindManipulation WindManipulation = new ExtraSkillConfig.WindManipulation();

   public String getFileName() {
      return "tensura/ability/skill/extra_config";
   }

   public static class AllSeeingEye extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 5000.0;
      @Comment("The Attack Speed Boost when activated.")
      public double attackSpeed = 0.1;
      @Comment("The Movement Speed Boost when activated.")
      public double movementSpeed = 0.02;
      @Comment("The Swim Speed Multiplier Boost when activated.")
      public double swimSpeed = 0.5;
      @Comment("The Mining Speed Boost when activated.")
      public double miningSpeed = 0.02;
      @Comment("The Presence Sense Boost when activated.")
      public double presenceSense = 1.0;
      @Comment("The Presence Sense Radius Boost when activated.")
      public double presenceRadius = 10.0;
      @Comment("The Boost Multiplier when mastered.")
      public double boostMultiplierMastered = 2.0;
      @Comment("The Melee Dodge Chance when mastered.")
      public double meleeDodge = 10.0;
      @Comment("The Projectile Dodge Chance when mastered.")
      public double projectileDodge = 10.0;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 1;
   }

   public static class AnalyticalAppraisal extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 20000.0;
      @Comment("The Bonus Analysis Level when activated.")
      public int level = 1;
      @Comment("The Bonus Analysis Level when activated with Mastery.")
      public int levelMastered = 2;
      @Comment("The Bonus Analysis Radius when activated.")
      public int radius = 0;
      @Comment("The Bonus Analysis Radius when activated with Mastery.")
      public int radiusMastered = 5;
   }

   public static class BlackFlame extends ManasSubConfig {
      @Comment("Magicule Cost to activate Flame Breath.")
      public double magiculeCostBreath = 50.0;
      @Comment("Magicule Cost to activate Fire Ball.")
      public double magiculeCostBall = 100.0;
      @Comment("Magicule Cost to activate Coating.")
      public double magiculeCostCoating = 5.0;
      @Comment("Magicule Cost to activate Hell Flare.")
      public double magiculeCostFlare = 2000.0;
      @Comment("The Duration in tick of Black Burn when applied on target.")
      public int blackBurnDuration = 200;
      @Comment("How much damage that the effect Black Burn does each second per level.")
      public float blackBurnDamage = 2.0F;
      @Comment("How much Flame damage that Black Flame Breath does each second.")
      public float breathFlameDamage = 9.0F;
      @Comment("How much Flame damage that Black Flame Breath does each second when mastered.")
      public float breathFlameDamageMastered = 18.0F;
      @Comment("How much Magic damage that Black Flame Breath does each second.")
      public float breathMagicDamage = 1.0F;
      @Comment("How much Magic damage that Black Flame Breath does each second when mastered.")
      public float breathMagicDamageMastered = 2.0F;
      @Comment("How much Flame damage that Black Fire Ball does on impact.")
      public float ballFlameDamage = 45.0F;
      @Comment("How much Magic damage that Black Fire Ball does on impact.")
      public float ballMagicDamage = 5.0F;
      @Comment("The cooldown to activate Black Fire Ball.")
      public int ballCooldown = 1;
      @Comment("How much Flame damage that Hell Flare does on activation.")
      public float flareFlameDamage = 270.0F;
      @Comment("How much Magic damage that Hell Flare does on activation.")
      public float flareMagicDamage = 30.0F;
      @Comment("The radius of Hell Flare.")
      public float flareRadius = 15.0F;
      @Comment("The cooldown to activate Hell Flare.")
      public int flareCooldown = 1;
      @Comment("How much Flame damage that Hell Flare Limited does on activation.")
      public float flareFlameDamageLimited = 720.0F;
      @Comment("How much Magic damage that Hell Flare Limited does on activation.")
      public float flareMagicDamageLimited = 80.0F;
      @Comment("The radius of Hell Flare Limited.")
      public float flareRadiusLimited = 2.5F;
      @Comment("The cooldown to activate Hell Flare Limited.")
      public int flareCooldownLimited = 2;
   }

   public static class BlackLightning extends ManasSubConfig {
      @Comment("Magicule Cost to activate Default Mode.")
      public double magiculeCost = 500.0;
      @Comment("Magicule Cost to activate Weak Mode.")
      public double magiculeCostWeak = 100.0;
      @Comment("Magicule Cost to activate Strong Mode.")
      public double magiculeCostStrong = 1000.0;
      @Comment("Magicule Cost to activate Blast Mode.")
      public double magiculeCostBlast = 100.0;
      @Comment("Magicule Cost to activate Death Storm.")
      public double magiculeCostStorm = 5000.0;
      @Comment("How much Lightning damage that Default Mode does when activated.")
      public float defaultLightningDamage = 45.0F;
      @Comment("How much Magic damage that Default Mode does when activated.")
      public float defaultMagicDamage = 5.0F;
      @Comment("The range for damage that Default Mode does when activated.")
      public float defaultRange = 8.0F;
      @Comment("The cooldown in second of the Default Mode does when activated.")
      public int defaultCooldown = 5;
      @Comment("How much Lightning damage that Weak Mode does when activated.")
      public float weakLightningDamage = 22.5F;
      @Comment("How much Magic damage that Weak Mode does when activated.")
      public float weakMagicDamage = 2.5F;
      @Comment("The range for damage that Weak Mode does when activated.")
      public float weakRange = 5.0F;
      @Comment("The cooldown in second of the Weak Mode does when activated.")
      public int weakCooldown = 3;
      @Comment("How much Lightning damage that Strong Mode does when activated.")
      public float strongLightningDamage = 135.0F;
      @Comment("How much Magic damage that Strong Mode does when activated.")
      public float strongMagicDamage = 15.0F;
      @Comment("The range for damage that Strong Mode does when activated.")
      public float strongRange = 12.0F;
      @Comment("The cooldown in second of the Strong Mode does when activated.")
      public int strongCooldown = 7;
      @Comment("How much Lightning damage that Blast Mode does each second.")
      public float blastLightningDamage = 22.5F;
      @Comment("How much Magic damage that Weak Mode does when activated.")
      public float blastMagicDamage = 2.5F;
      @Comment("The range in block of Blast Mode.")
      public float blastRange = 30.0F;
      @Comment("The number of strikes that the Death Storm will create when activated (5s each strike).")
      public int stormStrikes = 30;
      @Comment("The radius of attack of the Death Storm when activated.")
      public float stormRadius = 30.0F;
      @Comment("The Lightning damage of the bolt from the Death Storm.")
      public float stormBoltDamage = 90.0F;
      @Comment("The Magic damage of the bolt from the Death Storm.")
      public float stormBoltMagicDamage = 10.0F;
      @Comment("The damage range of the bolt from the Death Storm.")
      public float stormBoltRange = 4.0F;
      @Comment("The distance in block away from other lightning bolts from the Death Storm.")
      public double stormBoltDistance = 10.0;
      @Comment("The maximum number of new lightning bolts to spawn each time.")
      public int stormBoltNumber = 10;
      @Comment("The Wind damage of the Tornado from the Death Storm.")
      public float stormTornadoDamage = 45.0F;
      @Comment("The Magic damage of the Tornado from the Death Storm.")
      public float stormTornadoMagicDamage = 5.0F;
      @Comment("The size of the Tornado from the Death Storm.")
      public float stormTornadoSize = 4.0F;
      @Comment("The distance in block away from other tornadoes from the Death Storm.")
      public double stormTornadoDistance = 20.0;
      @Comment("The maximum number of new tornadoes to spawn each time.")
      public int stormTornadoNumber = 5;
   }

   public static class BodyDouble extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 100000.0;
      @Comment("The EP multiplier of the user's maximum Magicule that a body double will have.")
      public double cloneEP = 0.1;
      @Comment("The radius in block that the user needs to stay near the Original Body when controlling a clone.")
      public double originalBodyRadius = 50.0;
      @Comment(
         "The multiplier of max health and spiritual health that the use loses every 5 second when controlling a clone too far away from the Original Body."
      )
      public float originalBodyDamage = 0.1F;
      @Comment("The amount of HP that a clone heals each second.")
      public float cloneHeal = 2.0F;
      @Comment("The amount of Energy that a clone uses each second when healing.")
      public double cloneHealEnergy = 20.0;
      @Comment("The level of Fragility to given to a clone or the user has when controlling a clone.")
      public int cloneFragility = 2;
      @Comment("The cooldown in second to spawn a clone.")
      public int cooldown = 3;
      @Comment("The cooldown in second to spawn a clone when mastered.")
      public int cooldownMastered = 1;
   }

   public static class ChantAnnulment extends ManasSubConfig {
      @Comment("The number of magic needs to be masted to acquire this skill.")
      public int magicNumber = 10;
   }

   public static class DangerSense extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 0.0;
   }

   public static class DemonLordHaki extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 200000.0;
      @Comment("Magicule Cost to activate Magicule Release.")
      public double magiculeCost = 50.0;
      @Comment("Magicule Cost to activate Haki Coat.")
      public double magiculeCostCoat = 100.0;
      @Comment("The duration in tick of the Haki Coat when activated.")
      public int coatDuration = 2400;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.5;
   }

   public static class EarthManipulation extends ManasSubConfig {
      @Comment("The number of mastered earth skills needed to learn Earth Manipulation.")
      public double earthSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("Magicule Cost to activate Wall.")
      public double magiculeCostWall = 5.0;
      @Comment("Magicule Cost to activate Break.")
      public double magiculeCostBreak = 10.0;
      @Comment("Magicule Cost to activate Pit.")
      public double magiculeCostPit = 20.0;
      @Comment("The Earth Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Earth Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("The damage of the Earth Wall.")
      public int wallDamage = 5;
      @Comment("The radius of the Earth Wall.")
      public int wallRadius = 2;
      @Comment("The height of the Earth Wall.")
      public int wallHeight = 4;
      @Comment("The duration in tick of the Earth Wall.")
      public int wallDuration = 1200;
      @Comment("The extending radius of the Earth Break.")
      public int breakRadius = 1;
      @Comment("The radius of the Earth Pit.")
      public int pitRadius = 2;
   }

   public static class FlameManipulation extends ManasSubConfig {
      @Comment("The number of mastered fire skills needed to learn Flame Manipulation.")
      public double fireSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Flame Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Flame Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("How long in tick that the target will be set on fire when attacked with Manipulation's Coating (doubled with Mastery).")
      public int manipulationBurnTick = 100;
      @Comment("How long in tick that the target will be set on fire when attacked with Domination's Coating (doubled with Mastery).")
      public int dominationBurnTick = 200;
      @Comment("The damage of the Fire Breath when activated.")
      public int breathDamage = 8;
      @Comment("The damage of the Fire Breath when activated with Mastery.")
      public int breathDamageMastered = 20;
   }

   public static class GodwolfSense extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 10000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 25.0;
      @Comment("The level of Presence Sense when activated.")
      public double presenceSense = 3.0;
   }

   public static class GravityManipulation extends ManasSubConfig {
      @Comment("The number of mastered gravity skills needed to learn Gravity Manipulation.")
      public double gravitySkillMasteredAcquirement = 3.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 1000.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Gravity Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Gravity Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
   }

   public static class Haki extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 100000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 25.0;
      @Comment("Activation Speed Multiplier when activated.")
      public double speedMultiplier = 0.05;
      @Comment("Activation Speed Multiplier when activated with mastery.")
      public double speedMultiplierMastered = 0.1;
      @Comment("The attack radius of the haki in blocks.")
      public double hakiRadius = 15.0;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.25;
      @Comment("The duration in tick of the Fear effect when applied.")
      public int fearDuration = 200;
      @Comment("The cooldown in second of the haki.")
      public int cooldown = 5;
      @Comment("The cooldown in second of the haki when mastered.")
      public int cooldownMastered = 3;
   }

   public static class HeatWave extends ManasSubConfig {
      @Comment("Magicule Cost to activate Heat Sphere.")
      public double magiculeCostSphere = 40.0;
      @Comment("Magicule Cost to activate Heat Storm.")
      public double magiculeCostStorm = 20.0;
      @Comment("The Damage of the Heat Sphere.")
      public int sphereDamage = 30;
      @Comment("The duration in tick of the burning when a target got inflicted by the Heat Sphere.")
      public int sphereBurnTick = 20;
      @Comment("The Radius of the Heat Storm.")
      public int stormRadius = 5;
      @Comment("The Damage of the Heat Storm.")
      public int stormDamage = 5;
      @Comment("The duration in tick of the burning when a target got inflicted by the Heat Storm.")
      public int stormBurnTick = 200;
      @Comment("The cooldown for Heat Sphere.")
      public int sphereCooldown = 3;
      @Comment("The cooldown for Heat Sphere when mastered.")
      public int sphereCooldownMastered = 1;
   }

   public static class HeavenlyEye extends ManasSubConfig {
      @Comment("The level of Presence Sense when activated.")
      public double presenceSense = 4.0;
   }

   public static class HeroHaki extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 200000.0;
      @Comment("Magicule Cost to activate Magicule Release.")
      public double magiculeCost = 50.0;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.375;
   }

   public static class InfiniteRegeneration extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 2000000.0;
      @Comment("Magicule Cost per HP regenerated.")
      public double magiculeCost = 100.0;
      @Comment("Magicule Cost per HP regenerated when mastered.")
      public double magiculeCostMastered = 60.0;
      @Comment("Magicule Cost per SHP regenerated.")
      public double shpMagiculeCost = 120.0;
      @Comment("Magicule Cost per SHP regenerated when mastered.")
      public double shpMagiculeCostMastered = 80.0;
   }

   public static class LawManipulation extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 800000.0;
      @Comment("The number of Magic needed to be mastered to learn.")
      public double magicMastered = 30.0;
      @Comment("The amount of EP that the user needs to have minimum to be able to bypass Resist Skills with Magic/Battlewill when toggled.")
      public double resistBypassEP = 1000000.0;
      @Comment("The range in block of Abnormality Cleanse.")
      public double cleanseRange = 20.0;
      @Comment("The range in block of Takeover.")
      public double takeoverRange = 30.0;
      @Comment("The cooldown in second of the magic circle destroyed by Takeover for the targeted caster.")
      public int brokenMagicCooldown = 2;
      @Comment("The cooldown in second of the magic circle destroyed by Takeover for the targeted caster when mastered.")
      public int brokenMagicCooldownMastered = 4;
      @Comment("The multiplier of the user's EP that a target with Law Manipulation needs to have above to be unaffected by Takeover.")
      public double takeoverMultiplier = 0.75;
   }

   public static class LightningManipulation extends ManasSubConfig {
      @Comment("The number of mastered lightning skills needed to learn Lightning Manipulation.")
      public double lightningSkillAcquirement = 3.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 100.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Lightning Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Lightning Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("How much damage that the Lightning Bolt does when activated.")
      public int boltDamage = 15;
      @Comment("The range for damage that the Lightning Bolt does when activated.")
      public int boltRange = 3;
      @Comment("How much damage that the Domination Lightning Bolt does when activated.")
      public int boltDamageDomination = 30;
      @Comment("The range for damage that the Domination Lightning Bolt does when activated.")
      public int boltRangeDomination = 5;
   }

   public static class MagicAura extends ManasSubConfig {
      @Comment("The number of Magic needed to be mastered to learn Magic Aura.")
      public double magicMastered = 7.0;
      @Comment("Magicule Cost to activate the Default Mode.")
      public double magiculeCost = 500.0;
      @Comment("Magicule Cost to activate other Elemental Modes.")
      public double magiculeCostElemental = 1000.0;
      @Comment("The duration in tick of the Magic Aura effect when activated.")
      public int auraDuration = 6000;
      @Comment("The damage multiplier for the bonus Aura Damage compared to the user's normal attack damage.")
      public float auraMultiplier = 0.5F;
   }

   public static class MagicElementalTransform extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 400000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 10000.0;
      @Comment("The level of Spiritual Magic the user learns upon obtaining this skill.")
      public double spiritLevel = 3.0;
      @Comment("The Elemental Damage Multiplier of the respective Element during the Transformation.")
      public float transformBoost = 1.2F;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 600;
   }

   public static class MagicJamming extends ManasSubConfig {
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 100.0;
      @Comment("The multiplier of the user's EP that the target needs to be higher to ignore the effect of this skill.")
      public double epMultiplier = 3.5;
      @Comment("The multiplier of input skill/magic/art damage that the user takes less when toggled.")
      public float inputDamageMitigation = 0.5F;
      @Comment("The multiplier of input skill/magic/art damage that the user takes less when toggled with mastery.")
      public float inputDamageMitigationMastered = 0.67F;
      @Comment("The duration in tick of the Jamming effect when a target is physically attacked by this skill.")
      public int jammingDuration = 600;
      @Comment("The radius of the Jamming area.")
      public int jammingRadius = 10;
      @Comment("The radius of the Jamming area when mastered.")
      public int jammingRadiusMastered = 15;
      @Comment("The duration in tick of the Jamming effect when a target is in the Jamming Arena.")
      public int jammingArenaDuration = 120;
   }

   public static class MagicSense extends ManasSubConfig {
      @Comment("The level of Presence Sense when activated.")
      public double presenceSensePress = 0.5;
      @Comment("The level of Presence Sense when activated with mastery.")
      public double presenceSenseMastered = 1.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 5.0;
   }

   public static class Majesty extends ManasSubConfig {
      @Comment("The number of raid needed to be done to gain this skill.")
      public int raidNumber = 10;
      @Comment("The level of Hero of the Village effect when applied.")
      public int heroLevel = 5;
   }

   public static class ManaManipulation extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 200000.0;
      @Comment("The input magicule-based damage multiplier that user takes when toggled.")
      public float magiculeDamageMultiplier = 0.9F;
      @Comment("The magicule cost reduction multiplier on the user's magics when toggled.")
      public float magicCostReduction = 0.1F;
      @Comment("The magicule cost reduction multiplier on the user's magics when toggled with mastery.")
      public float magicCostReductionMastered = 0.2F;
   }

   public static class MolecularManipulation extends ManasSubConfig {
      @Comment("The number of mastered elemental manipulation skills needed to learn Molecular Manipulation.")
      public double masteredManipulationAcquirement = 2.0;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 5.0;
      @Comment("The maximum range in block for activation.")
      public double maxRange = 30.0;
      @Comment("The maximum size in block of a target for the Entity Mode.")
      public double maxSize = 1.0;
      @Comment("The maximum size in block of a target for the Entity Mode when mastered.")
      public double maxSizeMastered = 2.0;
      @Comment("The bonus maximum size in block of a target for the Entity Mode with Gravity Manipulation.")
      public double maxSizeManipulation = 6.0;
      @Comment("The bonus maximum size in block of a target for the Entity Mode with Gravity Domination.")
      public double maxSizeDomination = 10.0;
   }

   public static class MortalFear extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 200000.0;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.25;
      @Comment("The base radius of the haki in blocks before multiplying with entities' size.")
      public double hakiRadius = 7.0;
      @Comment("The duration in tick of the Strength effect applied on subordinates.")
      public int strengthDuration = 1200;
      @Comment("The level of the Strength effect applied on subordinates (+3 Attack Damage per level).")
      public int strengthLevel = 3;
      @Comment("The level of the Strength effect applied on subordinates when mastered.")
      public int strengthLevelMastered = 7;
   }

   public static class MultilayerBarrier extends ManasSubConfig {
      @Comment("Base Magicule Cost to block damage per damage point.")
      public double magiculeCost = 5.0;
      @Comment("The barrier point multiplier compared to the user's maximum health.")
      public double pointMultiplier = 1.5;
      @Comment("The barrier point multiplier compared to the target's maximum health when used on an ally.")
      public double allyPointMultiplier = 0.75;
      @Comment("The cooldown in seconds of this skill when activated.")
      public int cooldown = 10;
   }

   public static class SacredHaki extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 200000.0;
      @Comment("Magicule Cost to activate Magicule Release.")
      public double magiculeCost = 50.0;
      @Comment("Magicule Cost to activate Haki Coat.")
      public double magiculeCostCoat = 100.0;
      @Comment("The duration in tick of the Haki Coat when activated.")
      public int coatDuration = 2400;
      @Comment("The EP difference multiplier for each Fear Level.")
      public double epDifferenceMultiplier = 0.5;
      @Comment("The amount of HP to heal allies every 5 seconds.")
      public float healHP = 60.0F;
   }

   public static class Sage extends ManasSubConfig {
      @Comment("The number of Magic/Art needed to be mastered to learn Sage.")
      public double abilityMastered = 20.0;
      @Comment("The bonus number of learning point to gain when toggled.")
      public double learningPoint = 2.0;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 2.0;
   }

   public static class SenseHeatSource extends ManasSubConfig {
      @Comment("The radius in block that mobs and blocks around the user will be detected by heat sense.")
      public double heatRadius = 20.0;
   }

   public static class ShadowMotion extends ManasSubConfig {
      @Comment("Magicule Cost to activate Default Mode.")
      public double magiculeCost = 10.0;
      @Comment("Magicule Cost to activate Shadow Step.")
      public double magiculeCostStep = 200.0;
      @Comment("The maximum range in block of Shadow Step.")
      public double stepRange = 20.0;
   }

   public static class SnakeEye extends ManasSubConfig {
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 80.0;
      @Comment("The maximum range in block of Snake Eye.")
      public double maxRange = 20.0;
      @Comment("The amount of tick activated needed to increase 1 level of a effect.")
      public int increaseTick = 300;
   }

   public static class SoundManipulation extends ManasSubConfig {
      @Comment("The number of mastered sound/wind skills needed to learn Sound Manipulation.")
      public double soundMasteredSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("The Sound Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Sound Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
   }

   public static class SpatialManipulation extends ManasSubConfig {
      @Comment("The number of mastered space skills needed to learn Space Manipulation.")
      public double spaceSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Spatial Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Spatial Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("Magicule Cost to activate Warp Shot.")
      public double magiculeCostWarpShot = 50.0;
      @Comment("Magicule Cost to activate Spatial Cleanse per Severance value.")
      public double magiculeCostCleanse = 100.0;
      @Comment("Magicule Cost to activate Dimension Ray.")
      public double magiculeCostRay = 5000.0;
      @Comment("Magicule Cost to activate Dimension Storm.")
      public double magiculeCostStorm = 50000.0;
      @Comment("Magicule Cost to activate Fault Field.")
      public double magiculeCostField = 2000.0;
      @Comment("Magicule Cost to block damage with Fault Field per damage point.")
      public double magiculeCostFieldDamage = 50.0;
      @Comment("The warp shot level when activated Spatial Manipulation's Warp Shot.")
      public double warpShotManipulation = 0.5;
      @Comment("The warp shot level when activated Spatial Domination's Warp Shot (stackable with Spatial Manipulation).")
      public double warpShotDomination = 0.5;
      @Comment("The maximum distance in block that warp shot can work with.")
      public double warpShotDistance = 50.0;
      @Comment("The cooldown for Spatial Cleanse.")
      public int cleanseCooldown = 2;
      @Comment("The range in block of the Dimension Ray.")
      public float rayRange = 30.0F;
      @Comment("The damage of the Dimension Ray.")
      public float rayDamage = 50.0F;
      @Comment("The damage of the Dimension Ray when mastered.")
      public float rayDamageMastered = 200.0F;
      @Comment("The maximum activate time at once for Dimension Ray.")
      public float rayDuration = 60.0F;
      @Comment("The cooldown for Dimension Ray.")
      public int rayCooldown = 10;
      @Comment("The cooldown for Dimension Ray when mastered.")
      public int rayCooldownMastered = 7;
      @Comment("The activation range of the Dimension Storm.")
      public float stormRange = 30.0F;
      @Comment("The damage of each of Dimension Ray in a Dimension Storm.")
      public float stormDamage = 50.0F;
      @Comment("The damage of each of Dimension Ray in a Dimension Storm when mastered.")
      public float stormDamageMastered = 100.0F;
      @Comment("The number of Rays in a Dimension Storm.")
      public int stormAmount = 20;
      @Comment("The number of Rays in a Dimension Storm when mastered.")
      public int stormAmountMastered = 30;
      @Comment("The cooldown for Dimension Storm.")
      public int stormCooldown = 20;
      @Comment("The cooldown for Dimension Storm when mastered.")
      public int stormCooldownMastered = 10;
      @Comment("The speed multiplier when using Fault Field or Dimension Ray.")
      public double faultFieldSpeed = 0.5;
   }

   public static class SpatialMotion extends ManasSubConfig {
      @Comment("Base Magicule Cost to teleport per block.")
      public double magiculeCost = 10.0;
      @Comment("Base Magicule Cost to teleport per block using portal.")
      public double magiculeCostPortal = 50.0;
      @Comment("The maximum Blink range in block.")
      public int blinkRange = 30;
      @Comment("The maximum Blink range in block when mastered.")
      public int blinkRangeMastered = 50;
      @Comment("The cooldown for Blink Mode.")
      public int blinkCooldown = 5;
      @Comment("The cooldown for Blink Mode when mastered.")
      public int blinkCooldownMastered = 2;
      @Comment("The charge tick of the warp mode before warping any entity.")
      public int warpChargeTick = 100;
      @Comment("The charge tick of the warp mode before warping any entity when mastered.")
      public int warpChargeTickMastered = 0;
      @Comment("The cooldown for Warp Mode.")
      public int warpCooldown = 20;
      @Comment("The cooldown for Warp Mode when mastered.")
      public int warpCooldownMastered = 10;
   }

   public static class SteelStrength extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 20000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 30.0;
      @Comment("The duration of the Strengthen effect when activated.")
      public int strengthenDuration = 600;
      @Comment("The duration of the Strengthen effect when activated with mastery.")
      public int strengthenDurationMastered = 1800;
      @Comment("The level of the Strengthen effect when activated (+3 Attack Damage per level).")
      public int strengthenLevel = 2;
      @Comment("The level of the Strengthen effect when activated when Mastered.")
      public int strengthenLevelMastered = 3;
      @Comment("The Cooldown in second of the skill.")
      public int cooldown = 3;
   }

   public static class StickySteelThread extends ManasSubConfig {
      @Comment("Magicule Cost to activate Sticky Thread mode.")
      public double magiculeCostSticky = 50.0;
      @Comment("Magicule Cost to activate Steel Thread mode.")
      public double magiculeCostSteel = 100.0;
      @Comment("Magicule Cost to activate Slinger mode.")
      public double magiculeCostSlinger = 200.0;
      @Comment("Magicule Cost to activate Arcane Thread mode.")
      public double magiculeCostArcane = 200.0;
      @Comment("The cooldown in second of Sticky/Steel Thread mode.")
      public int threadCooldown = 3;
      @Comment("The cooldown in second of Sticky/Steel Thread mode when mastered.")
      public int threadCooldownMastered = 1;
      @Comment("The maximum range in block of Arcane Thread mode.")
      public int arcaneRange = 10;
      @Comment("The maximum range in block of Arcane Thread mode when mastered.")
      public int arcaneRangeMastered = 20;
      @Comment("The duration in tick of the Webbed effect of Arcane Thread mode.")
      public int arcaneDuration = 500;
      @Comment("The maximum duration since activated for Arcane Thread to reactivate to deal damage.")
      public int arcaneReactivate = 100;
      @Comment("The maximum duration since activated for Arcane Thread to reactivate to deal damage when mastered.")
      public int arcaneReactivateMastered = 200;
      @Comment("The damage of Arcane Thread when reactivate.")
      public int arcaneReactivateDamage = 30;
      @Comment("The damage of Arcane Thread when reactivate with mastery.")
      public int arcaneReactivateDamageMastered = 60;
      @Comment("The cooldown in second of Arcane Thread mode.")
      public int arcaneCooldown = 5;
      @Comment("The cooldown in second of Arcane Thread mode when mastered.")
      public int arcaneCooldownMastered = 3;
   }

   public static class StrengthenBody extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 15000.0;
      @Comment("The bonus armor amount when activated.")
      public double bonusArmor = 5.0;
      @Comment("The input damage multiplier that the user takes when activated.")
      public float inputMultiplier = 0.8F;
   }

   public static class ThoughtAcceleration extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 8000.0;
      @Comment("The chant speed multiplier when activated.")
      public double chantSpeed = 2.0;
      @Comment("The bonus movement speed when activated.")
      public double movementSpeed = 0.01;
      @Comment("The bonus movement speed when activated with mastery.")
      public double movementSpeedMastered = 0.02;
      @Comment("The bonus movement speed when activated.")
      public double attackSpeed = 0.2;
      @Comment("The bonus movement speed when activated with mastery.")
      public double attackSpeedMastered = 0.4;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 1;
   }

   public static class UltraInstinct extends ManasSubConfig {
      @Comment("Melee Dodge Chance when activated.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance when activated.")
      public double projectileDodge = 10.0;
   }

   public static class UltraspeedRegeneration extends ManasSubConfig {
      @Comment("EP Requirement for Learning.")
      public double epAcquirement = 300000.0;
   }

   public static class UniversalPerception extends ManasSubConfig {
      @Comment("The level of Presence Sense when activated.")
      public double presenceSense = 3.0;
      @Comment("The bonus Presence Sense Radius when activated.")
      public double presenceRadius = 20.0;
      @Comment("The radius in block that mobs and blocks around the user will be detected by heat sense.")
      public double heatRadius = 30.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 25.0;
   }

   public static class WaterManipulation extends ManasSubConfig {
      @Comment("The number of mastered water skills needed to learn Water Manipulation.")
      public double waterSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Water Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Water Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("Magicule Cost to activate Water Breath.")
      public double magiculeCostBreath = 10.0;
      @Comment("Magicule Cost to activate Water Ball.")
      public double magiculeCostBall = 100.0;
      @Comment("The damage of the Water Breath when activated.")
      public float breathDamage = 8.0F;
      @Comment("The damage of the Water Breath when activated with Mastery.")
      public float breathDamageMastered = 4.0F;
      @Comment("The damage of the Water Ball when activated.")
      public float ballDamage = 12.0F;
   }

   public static class WeatherManipulation extends ManasSubConfig {
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("Magicule Cost to activate Manipulation.")
      public double magiculeCostManipulation = 1000.0;
      @Comment("Magicule Cost to activate Domination.")
      public double magiculeCostDomination = 500.0;
      @Comment("The cooldown in second when activated Manipulation.")
      public int cooldown = 10;
      @Comment("The cooldown in second when activated Manipulation with mastery.")
      public int cooldownMastered = 5;
      @Comment("The cooldown in second when activated Domination.")
      public int cooldownDomination = 5;
      @Comment("The cooldown in second when activated Domination with mastery.")
      public int cooldownDominationMastered = 3;
   }

   public static class WindManipulation extends ManasSubConfig {
      @Comment("The number of mastered wind skills needed to learn Wind Manipulation.")
      public double windSkillAcquirement = 3.0;
      @Comment("EP Requirement for to learn Domination.")
      public double dominationEpAcquirement = 400000.0;
      @Comment("EP Requirement for to use Resist Degradation when mastered.")
      public double resistDegradationAcquirement = 800000.0;
      @Comment("The Wind Damage Boost when activated Manipulation.")
      public double manipulationBoost = 1.5;
      @Comment("The Wind Damage Boost when activated Domination.")
      public double dominationBoost = 3.0;
      @Comment("Magicule Cost to activate Water Breath.")
      public double magiculeCostBreath = 20.0;
      @Comment("Magicule Cost to activate Water Ball.")
      public double magiculeCostBall = 200.0;
      @Comment("The damage of the Water Breath when activated.")
      public float breathDamage = 8.0F;
      @Comment("The damage of the Water Breath when activated with Mastery.")
      public float breathDamageMastered = 4.0F;
      @Comment("The damage of the Water Ball when activated.")
      public float ballDamage = 12.0F;
   }
}
