package io.github.manasmods.tensura.config.ability.skill;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import java.util.List;

public class UniqueSkillConfig extends ManasConfig {
   public UniqueSkillConfig.AbsoluteSeverance AbsoluteSeverance = new UniqueSkillConfig.AbsoluteSeverance();
   public UniqueSkillConfig.Analyst Analyst = new UniqueSkillConfig.Analyst();
   public UniqueSkillConfig.AntiSkill AntiSkill = new UniqueSkillConfig.AntiSkill();
   public UniqueSkillConfig.Berserker Berserker = new UniqueSkillConfig.Berserker();
   public UniqueSkillConfig.Berserk Berserk = new UniqueSkillConfig.Berserk();
   public UniqueSkillConfig.Bewilder Bewilder = new UniqueSkillConfig.Bewilder();
   public UniqueSkillConfig.Chef Chef = new UniqueSkillConfig.Chef();
   public UniqueSkillConfig.ChosenOne ChosenOne = new UniqueSkillConfig.ChosenOne();
   public UniqueSkillConfig.Commander Commander = new UniqueSkillConfig.Commander();
   public UniqueSkillConfig.Cook Cook = new UniqueSkillConfig.Cook();
   public UniqueSkillConfig.Creator Creator = new UniqueSkillConfig.Creator();
   public UniqueSkillConfig.Degenerate Degenerate = new UniqueSkillConfig.Degenerate();
   public UniqueSkillConfig.DivineBerserker DivineBerserker = new UniqueSkillConfig.DivineBerserker();
   public UniqueSkillConfig.Engorger Engorger = new UniqueSkillConfig.Engorger();
   public UniqueSkillConfig.Envy Envy = new UniqueSkillConfig.Envy();
   public UniqueSkillConfig.Falsifier Falsifier = new UniqueSkillConfig.Falsifier();
   public UniqueSkillConfig.Fighter Fighter = new UniqueSkillConfig.Fighter();
   public UniqueSkillConfig.Fusionist Fusionist = new UniqueSkillConfig.Fusionist();
   public UniqueSkillConfig.Gluttony Gluttony = new UniqueSkillConfig.Gluttony();
   public UniqueSkillConfig.GodlyCraftsman GodlyCraftsman = new UniqueSkillConfig.GodlyCraftsman();
   public UniqueSkillConfig.Gourmand Gourmand = new UniqueSkillConfig.Gourmand();
   public UniqueSkillConfig.Gourmet Gourmet = new UniqueSkillConfig.Gourmet();
   public UniqueSkillConfig.GreatSage GreatSage = new UniqueSkillConfig.GreatSage();
   public UniqueSkillConfig.Greed Greed = new UniqueSkillConfig.Greed();
   public UniqueSkillConfig.Guardian Guardian = new UniqueSkillConfig.Guardian();
   public UniqueSkillConfig.Healer Healer = new UniqueSkillConfig.Healer();
   public UniqueSkillConfig.InfinityPrison InfinityPrison = new UniqueSkillConfig.InfinityPrison();
   public UniqueSkillConfig.Lust Lust = new UniqueSkillConfig.Lust();
   public UniqueSkillConfig.MartialMaster MartialMaster = new UniqueSkillConfig.MartialMaster();
   public UniqueSkillConfig.Mathematician Mathematician = new UniqueSkillConfig.Mathematician();
   public UniqueSkillConfig.Merciless Merciless = new UniqueSkillConfig.Merciless();
   public UniqueSkillConfig.Murderer Murderer = new UniqueSkillConfig.Murderer();
   public UniqueSkillConfig.Musician Musician = new UniqueSkillConfig.Musician();
   public UniqueSkillConfig.Observer Observer = new UniqueSkillConfig.Observer();
   public UniqueSkillConfig.Oppressor Oppressor = new UniqueSkillConfig.Oppressor();
   public UniqueSkillConfig.Predator Predator = new UniqueSkillConfig.Predator();
   public UniqueSkillConfig.Pride Pride = new UniqueSkillConfig.Pride();
   public UniqueSkillConfig.Reaper Reaper = new UniqueSkillConfig.Reaper();
   public UniqueSkillConfig.Reflector Reflector = new UniqueSkillConfig.Reflector();
   public UniqueSkillConfig.Researcher Researcher = new UniqueSkillConfig.Researcher();
   public UniqueSkillConfig.Reverser Reverser = new UniqueSkillConfig.Reverser();
   public UniqueSkillConfig.RoyalBeast RoyalBeast = new UniqueSkillConfig.RoyalBeast();
   public UniqueSkillConfig.Seeker Seeker = new UniqueSkillConfig.Seeker();
   public UniqueSkillConfig.Seer Seer = new UniqueSkillConfig.Seer();
   public UniqueSkillConfig.Severer Severer = new UniqueSkillConfig.Severer();
   public UniqueSkillConfig.ShadowStriker ShadowStriker = new UniqueSkillConfig.ShadowStriker();
   public UniqueSkillConfig.Sloth Sloth = new UniqueSkillConfig.Sloth();
   public UniqueSkillConfig.Sniper Sniper = new UniqueSkillConfig.Sniper();
   public UniqueSkillConfig.Spearhead Spearhead = new UniqueSkillConfig.Spearhead();
   public UniqueSkillConfig.Starved Starved = new UniqueSkillConfig.Starved();
   public UniqueSkillConfig.Suppressor Suppressor = new UniqueSkillConfig.Suppressor();
   public UniqueSkillConfig.Survivor Survivor = new UniqueSkillConfig.Survivor();
   public UniqueSkillConfig.Thrower Thrower = new UniqueSkillConfig.Thrower();
   public UniqueSkillConfig.Traveler Traveler = new UniqueSkillConfig.Traveler();
   public UniqueSkillConfig.Tuner Tuner = new UniqueSkillConfig.Tuner();
   public UniqueSkillConfig.Unyielding Unyielding = new UniqueSkillConfig.Unyielding();
   public UniqueSkillConfig.Usurper Usurper = new UniqueSkillConfig.Usurper();
   public UniqueSkillConfig.Villain Villain = new UniqueSkillConfig.Villain();
   public UniqueSkillConfig.Wrath Wrath = new UniqueSkillConfig.Wrath();

   public String getFileName() {
      return "tensura/ability/skill/unique_config";
   }

   public static class AbsoluteSeverance extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("Magicule Cost to activate Coating.")
      public double magiculeCostCoating = 1000.0;
      @Comment("Magicule Cost to activate Severance Projectile.")
      public double magiculeCostProjectile = 20000.0;
      @Comment("The duration in tick of the Severance Blade effect (Coating).")
      public int coatingDuration = 12000;
      @Comment("The level of the Severance Blade effect (+ 10 Damage Boost each level).")
      public int coatingLevel = 5;
      @Comment("The level of the Severance Blade effect when mastered.")
      public int coatingLevelMastered = 20;
      @Comment("The damage of the Severance Projectile.")
      public float projectileDamage = 50.0F;
      @Comment("The damage of the Severance Projectile when mastered.")
      public float projectileDamageMastered = 300.0F;
      @Comment("The size of the Severance Projectile.")
      public float projectileSize = 5.0F;
      @Comment("The size of the Severance Projectile when mastered.")
      public float projectileSizeMastered = 8.0F;
      @Comment("The duration in tick of the Severance Projectile.")
      public int projectileDuration = 20;
      @Comment("The duration in tick of the Severance Projectile when mastered.")
      public int projectileDurationMastered = 40;
      @Comment("The cooldown in second of the Severance Projectile.")
      public int projectileCooldown = 3;
   }

   public static class Analyst extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("The Analysis Level when activated.")
      public int analysisLevel = 18;
      @Comment("The Analysis Level when activated with Mastery.")
      public int analysisLevelMastered = 28;
      @Comment("The Analysis Radius when activated.")
      public int analysisRadius = 20;
      @Comment("The Analysis Radius when activated with Mastery.")
      public int analysisRadiusMastered = 25;
      @Comment("The range in block of Analyze.")
      public float analyzeRange = 30.0F;
      @Comment("The hold time in tick of Analyze to copy a Magic.")
      public int analyzeTime = 100;
      @Comment("The hold time in tick of Analyze to copy a Magic.")
      public int analyzeTimeMastered = 60;
      @Comment("The bonus number of learning point to gain when toggled.")
      public double learningPoint = 2.0;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 2.0;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
   }

   public static class AntiSkill extends ManasSubConfig {
      @Comment("The duration in tick of the Anti-skill effect to apply on targets when used.")
      public int antiDuration = 100;
   }

   public static class Berserk extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 20000.0;
      @Comment("Magicule Cost to each attack while toggled Flame Aura.")
      public double magiculeCostFlameAura = 200.0;
      @Comment("Magicule Cost to activate Rage.")
      public double magiculeCostRage = 300.0;
      @Comment("Magicule Cost to activate Mad Ogre.")
      public double magiculeCostMadOgre = 5000.0;
      @Comment("The multiplier for mastery point gaining when activating Mad Ogre.")
      public double masteryGainMultiplier = 5.0;
      @Comment("The duration in tick of the Strengthen effect when activated Rage.")
      public int rageDuration = 6000;
      @Comment("The level of the Strengthen effect when activated Rage (+3 Attack Damage per level).")
      public int rageLevel = 5;
      @Comment("The level of the Strengthen effect when activated Rage with Mastered.")
      public int rageLevelMastered = 10;
      @Comment("The duration in tick of the Mad Ogre effect when activated.")
      public int madOgreDuration = 12000;
      @Comment("The level of the Mad Ogre effect when activated.")
      public int madOgreLevel = 1;
      @Comment("The level of the Mad Ogre effect when activated with Mastered.")
      public int madOgreLevelMastered = 2;
      @Comment("The damage of each flame orb shot by Mad Ogres when mastered.")
      public int orbDamage = 100;
      @Comment("The blast radius of each flame orb when triggered when mastered.")
      public int orbBlast = 4;
      @Comment("The input damage multiplier that the user takes when in defence mode of Mad Ogres.")
      public float defenceMultiplier = 0.5F;
      @Comment("The Cooldown in second after Mad Ogre runs out.")
      public int cooldown = 600;
      @Comment("The Flame Damage Boost when toggled Flame Aura.")
      public double flameAuraBoost = 2.0;
      @Comment("How long in tick that the target will be set on fire when attacked with Flame Aura toggled.")
      public int flameAuraBurnTick = 200;
      @Comment("How Flame damage multiplied based on the user's physical/battlewill attack with Flame Aura toggled.")
      public float flameAuraDamage = 0.5F;
   }

   public static class Berserker extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 40000.0;
      @Comment("The bonus Aura percentage the user gains when toggled on.")
      public double auraPercentage = 2.0;
      @Comment("The bonus Magicule percentage the user gains when toggled on.")
      public double magiculePercentage = 2.0;
      @Comment("How much of the attack damage that Berserker will inflict on targets' armor durability.")
      public double armorDurability = 0.25;
      @Comment("How much durability that Berserker will take from attackers' weapon when attacking the user.")
      public int weaponDurability = 5;
      @Comment("The Hold Time in Tick to activate Berserker.")
      public int holdTime = 40;
      @Comment("How much EP the user needs to have for each armor point.")
      public double armorEP = 20000.0;
      @Comment("The maximum amount of armor point that the user can gain.")
      public double armorMax = 100.0;
      @Comment("The base attack point the user gain when activated.")
      public double attackBase = 5.0;
      @Comment("How much EP the user needs to have for each additional attack point.")
      public double attackEP = 40000.0;
      @Comment("The maximum amount of armor point that the user can gain (every bonus attack point is doubled with mastery).")
      public double attackMax = 55.0;
      @Comment("How much EP the user needs to have for each additional speed point.")
      public double speedEP = 25000.0;
      @Comment("The maximum amount of armor point that the user can gain.")
      public double speedMax = 40.0;
   }

   public static class Bewilder extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate Target Mode.")
      public double magiculeCostTarget = 50.0;
      @Comment("Magicule Cost to activate Area Mode.")
      public double magiculeCostArea = 80.0;
      @Comment("Magicule Cost to activate Charm Mode.")
      public double magiculeCostCharm = 200.0;
      @Comment("Magicule Cost to activate Kill Mode.")
      public double magiculeCostKill = 100.0;
      @Comment("The duration in tick of the Mind Control effect (-1 = permanent).")
      public int controlDuration = 12000;
      @Comment("The duration in tick of the Mind Control effect when the target has Spiritual Attack Resistance (-1 = permanent).")
      public int controlResistedDuration = 6000;
      @Comment("The radius in block of the Area/Kill Mode.")
      public int areaRadius = 10;
      @Comment("The radius in block of the Area/Kill Mode when mastered.")
      public int areaRadiusMastered = 15;
      @Comment("The duration in tick of the Mind Control effect when using Area Mode (-1 = permanent).")
      public int controlAreaDuration = 6000;
      @Comment("The duration in tick of the Mind Control effect when using Area Mode while the target has Spiritual Attack Resistance (-1 = permanent).")
      public int controlAreaResistedDuration = 3000;
      @Comment("The duration in tick of the Hero of the Village effect when activating Charm Mode.")
      public int heroDuration = 2400;
      @Comment("The level of the Hero of the Village effect when activating Charm Mode.")
      public int heroLevel = 5;
      @Comment("The multiplier of targets' current HP to deal when activating Kill Mode.")
      public float killHPMultiplier = 1.0F;
      @Comment("The multiplier of targets' current HP to deal when activating Kill Mode while the target has Spiritual Attack Resistance.")
      public float killHPResistedMultiplier = 0.5F;
      @Comment("The cooldown in second of the Kill Mode.")
      public int killCooldown = 5;
      @Comment("The cooldown in second of the Kill Mode when mastered.")
      public int killCooldownMastered = 3;
   }

   public static class Chef extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 10000.0;
      @Comment("Magicule Cost to remove each harmful status effect.")
      public double magiculeCostEffect = 100.0;
      @Comment("Magicule Cost to heal each HP.")
      public double magiculeCostHP = 60.0;
      @Comment("Magicule Cost to heal each HP when mastered.")
      public double magiculeCostHPMastered = 40.0;
      @Comment("The cooldown in second when activated.")
      public int cooldown = 5;
      @Comment("The cooldown in second when activated with mastery.")
      public int cooldownMastered = 3;
   }

   public static class ChosenOne extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 90000.0;
      @Comment("Magicule Cost to activate Hero Haki.")
      public double magiculeCostHaki = 25.0;
      @Comment("Magicule Cost to activate Hero's Charisma.")
      public double magiculeCostCharisma = 200.0;
      @Comment("The level of the Hero of the Village effect.")
      public int heroLevel = 5;
      @Comment("The level of the Luck effect.")
      public int luckLevel = 5;
      @Comment("The amount of Critical Chance of the Ally Boost each level.\nChosen One has Ally Boost II, Villain has Ally Boost I")
      public double allyCritChance = 50.0;
      @Comment("Melee Dodge Chance for the user and allies when activated.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance for the user and allies when activated.")
      public double projectileDodge = 10.0;
      @Comment("The radius in block of the Hero's Blessing's effect on allies.")
      public int blessingRadius = 15;
      @Comment("The radius in block of the Hero's Charisma's effect.")
      public int controlRadius = 10;
      @Comment("The duration in tick of the Mind Control effect when activating Hero's Charisma (-1 = permanent).")
      public int controlDuration = 2400;
      @Comment(
         "The duration in tick of the Mind Control effect when activating Hero's Charisma while the target has Spiritual Attack Resistance (-1 = permanent)."
      )
      public int controlResistedDuration = 1200;
      @Comment("The multiplier of Max Health when an entity is revived as Ally with Hero's Charisma.")
      public float hpMultiplier = 0.5F;
      @Comment("The multiplier of Max Spiritual Health when an entity is revived as Ally with Hero's Charisma.")
      public double shpMultiplier = 0.5;
   }

   public static class Commander extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 10.0;
      @Comment("Dodge Negation Chance when toggled.")
      public double dodgeNegation = 50.0;
      @Comment("The radius in block for Inspire Force's effect on allies.")
      public double inspireRadius = 30.0;
      @Comment("The multiplier of boost on each physical stats of allies when applied with Inspire Force (doubled with mastery).")
      public double inspireMultiplier = 0.3;
      @Comment("The Critical Attack Chance for allies when applied with Inspire Force (doubled with mastery).")
      public double inspireCritChance = 30.0;
   }

   public static class Cook extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("Critical Attack Chance when toggled.")
      public double critChance = 100.0;
      @Comment("Dodge Negation Chance when toggled.")
      public double dodgeNegation = 100.0;
      @Comment("The bonus number of learning point to gain when toggled.")
      public double learningPoint = 4.0;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 4.0;
      @Comment("The multiplier of the user's EP that the target to have above to ignore Barrier shattering when attacked by the user.")
      public double barrierEP = 2.0;
      @Comment("The multiplier of the user's damage dealt that the target's Max Health get reduced by.")
      public double hpReducedMultiplier = 1.0;
   }

   public static class Creator extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 75000.0;
      @Comment("The Analysis Level when activated.")
      public int analysisLevel = 2;
      @Comment("The Analysis Level when activated with Mastery.")
      public int analysisLevelMastered = 6;
      @Comment("The Analysis Radius when activated.")
      public int analysisRadius = 0;
      @Comment("The Analysis Radius when activated with Mastery.")
      public int analysisRadiusMastered = 5;
      @Comment("The cooldown in second after creating a Skill.")
      public int creationCooldown = 1200;
      @Comment("The cooldown in second before the created skill vanishes.")
      public int creationVanishTimer = 1200;
      @Comment("The multiplier for mastery point gaining when created a Skill.")
      public double masteryGainMultiplier = 5.0;
      @Comment("List of Unique skills that can be created by Creator.")
      public List<String> uniqueSkills = List.of(
         "tensura:anti_skill",
         "tensura:analyst",
         "tensura:absolute_severance",
         "tensura:berserk",
         "tensura:berserker",
         "tensura:bewilder",
         "tensura:chef",
         "tensura:commander",
         "tensura:cook",
         "tensura:falsifier",
         "tensura:fighter",
         "tensura:fusionist",
         "tensura:gourmand",
         "tensura:guardian",
         "tensura:healer",
         "tensura:martial_master",
         "tensura:mathematician",
         "tensura:murderer",
         "tensura:musician",
         "tensura:observer",
         "tensura:oppressor",
         "tensura:reflector",
         "tensura:researcher",
         "tensura:royal_beast",
         "tensura:reaper",
         "tensura:reverser",
         "tensura:seeker",
         "tensura:seer",
         "tensura:severer",
         "tensura:shadow_striker",
         "tensura:sniper",
         "tensura:spearhead",
         "tensura:suppressor",
         "tensura:survivor",
         "tensura:traveler",
         "tensura:thrower",
         "tensura:tuner",
         "tensura:usurper",
         "tensura:villain"
      );
   }

   public static class Degenerate extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 35000.0;
      @Comment("Whether EP gained from Charybdis Core using Degenerate will follow the EP reduction calculation")
      public boolean coreEPReduction = false;
      @Comment("The multiplier of Max Health that a Spiritual entity needs to have below to be affected by the Synthesize mode.")
      public float spiritualEntityHP = 0.25F;
      @Comment("The multiplier of the Synthesized Spiritual entity's EP to be added to the user's Each of Aura and Magicule.")
      public double spiritualEntityEP = 1.0;
      @Comment("The cooldown in second of the Synthesize mode.")
      public int synthesizeCooldown = 5;
      @Comment("The cooldown in second of the Synthesize mode when mastered.")
      public int synthesizeCooldownMastered = 3;
      @Comment("The multiplier of EP that an entity needs to have below to be affected by the Separate mode.")
      public float separateEP = 0.75F;
      @Comment("Magicule Cost to remove each harmful status effect from Allies using the Separate Mode.")
      public double magiculeCostEffect = 100.0;
      @Comment("The cooldown in second of the Synthesize mode.")
      public int separateCooldown = 5;
      @Comment("The cooldown in second of the Synthesize mode when mastered.")
      public int separateCooldownMastered = 3;
      @Comment("How many levels that Degenerate can go above the maximum level of an enchantment.")
      public int maxBonusLevel = 2;
      @Comment("Lists of enchantments that Degenerate cannot separate.")
      public List<String> separateBlacklist = TensuraEnchantmentHelper.ENGRAVEMENT_ID_LIST;
      @Comment("Lists of enchantments that Degenerate cannot synthesis to increase the enchantment's current level.")
      public List<String> synthesisBlacklist = TensuraEnchantmentHelper.ENGRAVEMENT_ID_LIST;
      @Comment("Lists of enchantments that Degenerate cannot synthesis above the enchantment's maximum level.")
      public List<String> maxBonusBlacklist = List.of(
         "minecraft:aqua_affinity",
         "minecraft:channeling",
         "minecraft:flame",
         "minecraft:infinity",
         "minecraft:mending",
         "minecraft:silk_touch",
         "minecraft:binding_curse",
         "minecraft:vanishing_curse",
         "tensura:enervation",
         "tensura:lethargy",
         "tensura:sealing",
         "tensura:stagnation",
         "tensura:ruination",
         "tensura:vitality",
         "tensura:vigor",
         "tensura:transcendence",
         "tensura:growth",
         "tensura:restoration"
      );
   }

   public static class DivineBerserker extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 10000.0;
      @Comment("The Battlewill damage multiplier that the user does when activated.")
      public float battlewillMultiplier = 1.5F;
      @Comment("The Battlewill damage multiplier that the user does when activated with mastery.")
      public float battlewillMultiplierMastered = 2.0F;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 600;
   }

   public static class Engorger extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 300.0;
      @Comment("The amount of armor point from Engorgement.")
      public double armor = 10.0;
      @Comment("The amount of attack point from Engorgement.")
      public double attack = 10.0;
      @Comment("The amount of attack knockback from Engorgement.")
      public double attackKnock = 1.0;
      @Comment("The amount of knockback resistance from Engorgement.")
      public double knockResistance = 0.4;
      @Comment("The amount of speed from Engorgement.")
      public double speed = 0.1;
      @Comment("The amount of jump boost from Engorgement.")
      public double jumpBoost = 0.1;
      @Comment("The amount of range from Engorgement.")
      public double range = 2.5;
      @Comment("The amount of size from Engorgement.")
      public double size = 1.5;
      @Comment("The amount of HP the user regenerates from Engorgement each second.")
      public float heal = 1.0F;
      @Comment("The level of the dash boost when activated (similar to Riptide).")
      public int dashLevel = 3;
      @Comment("The duration in tick of the dash boost when activated.")
      public int dashDuration = 15;
      @Comment("The damage multiplier compared to the user's attack damage when hit target during Dash boost.")
      public int dashAttackMultiplier = 1;
      @Comment("The Bonus attack damage of the dash boost on top of the user's attack damage.")
      public int dashAttackBonus = 50;
      @Comment("The Bonus attack damage of the dash boost on top of the user's attack damage when mastered.")
      public int dashAttackBonusMastered = 100;
   }

   public static class Envy extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Magicule Cost to activate Strength Sap.")
      public double magiculeCostSap = 1000.0;
      @Comment("The level of the Luck effect when toggled.")
      public int luckLevel = 3;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 10.0;
      @Comment("The chance for the user to be applied with Insanity when using Strength Sap every 10 second.")
      public int insanityChance = 20;
      @Comment("The increasing level of the Insanity effect when the user is applied by Strength Sap.")
      public int insanityLevel = 1;
      @Comment("The duration in tick of the Insanity effect when the user is applied by Strength Sap.")
      public int insanityDuration = 240;
      @Comment("The radius in block of the Strength Sap effect.")
      public int sapRadius = 15;
      @Comment("The multiplier of EP that an entity needs to have below to be affected by the Strength Sap.")
      public float sapEP = 0.6F;
      @Comment("The multiplier of EP that an entity needs to have below to be affected by the Strength Sap when mastered.")
      public float sapEPMastered = 0.8F;
      @Comment("The EP difference multiplier for each Slowness/Weakness Level.")
      public double epDifferenceMultiplier = 0.1;
      @Comment("The duration in tick of the Slowness/Weakness effect when targets are applied by Strength Sap.")
      public int sapDuration = 200;
      @Comment("The multiplier of EP that the user drains from targets each second.")
      public float epDrain = 0.001F;
   }

   public static class Falsifier extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 15000.0;
      @Comment("Magicule Cost to activate Concealment.")
      public double magiculeCost = 200.0;
      @Comment("The duration in tick of the Concealment effect when activated.")
      public int concealmentDuration = 2400;
      @Comment("The cooldown in second of the Concealment Mode.")
      public int concealmentCooldown = 20;
      @Comment("The cooldown in second of the Concealment Mode when mastered.")
      public int concealmentCooldownMastered = 0;
      @Comment("Activation Speed Multiplier when activating Fake Death.")
      public double fakeSpeedMultiplier = 0.6;
      @Comment("Activation Speed Multiplier when activating Fake Death with mastery.")
      public double fakeSpeedMultiplierMastered = 0.8;
      @Comment("The maximum time in tick that the user can hold down Fake Death.")
      public int fakeMaxTime = 600;
      @Comment("The input damage multiplier that the user takes when Fake Death is triggered.")
      public float fakeInputMultiplier = 0.5F;
      @Comment("The duration in tick of the Concealment effect after Fake Death is triggered.")
      public int fakeConcealmentDuration = 60;
   }

   public static class Fighter extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("The physical attack damage boost when in Slot.")
      public int attackBoost = 75;
      @Comment("The physical attack damage boost when in Slot with mastery.")
      public int attackBoostMastered = 150;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 2.0;
      @Comment("The bonus dodge strength when toggled.")
      public double dodgeStrength = 0.25;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 2;
   }

   public static class Fusionist extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Fuse mode.")
      public double magiculeCostFuse = 1000.0;
      @Comment("Magicule Cost to activate Projectile mode.")
      public double magiculeCostProjectile = 200.0;
      @Comment("The minimum range in block of the skill.")
      public double range = 5.0;
      @Comment("The amount of disassembled matter points needed to activate Fuse.")
      public int fuseMatterCost = 5;
      @Comment("The blast damage of the landmine from Fuse.")
      public int fuseBlastDamage = 300;
      @Comment("The blast damage of the landmine from Fuse.")
      public int fuseBlastDamageMastered = 500;
      @Comment("The blast size of the landmine from Fuse.")
      public int fuseBlastRadius = 25;
      @Comment("The amount of disassembled matter points needed to add charge more power on a landmine with mastery.")
      public int bonusBlastCost = 10;
      @Comment("The blast damage to charge on a landmine with mastery.")
      public int bonusBlastDamage = 50;
      @Comment("The blast size to charge on a landmine with mastery.")
      public int bonusBlastRadius = 5;
      @Comment("The max size the landmine can reach when charged.")
      public int maxBlastRadius = 50;
      @Comment("The amount of disassembled matter points needed to activate Projectile.")
      public int projectileMatterCost = 1;
      @Comment("The blast radius of the Projectile shot.")
      public int projectileBlastRadius = 6;
   }

   public static class Gluttony extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Magicule Cost to remove each harmful status effect with Isolation.")
      public double magiculeCostIsolation = 200.0;
      @Comment("Magicule Cost to activate Corrosion.")
      public double magiculeCostCorrosion = 200.0;
      @Comment("The max range in block of the Predation Mode.")
      public double predationRange = 10.0;
      @Comment("The max range in block of the Predation Mode when mastered.")
      public double predationRangeMastered = 15.0;
      @Comment("The attack damage of the Predation Mode.")
      public float predationDamage = 50.0F;
      @Comment("The amount of EP that the user drains from target using the Predation Mode.")
      public float predationEPDrain = 1000.0F;
      @Comment("The chance to obtain skills from targets without killing them with the Predation Mode.")
      public float predationSkillChance = 30.0F;
      @Comment("The number of skills to gain from targets at a time without killing them with the Predation Mode.")
      public int predationSkillNumber = 3;
      @Comment("The duration in tick of the Corrosion effect applied by the Predation Mode.")
      public int predationCorrosionDuration = 100;
      @Comment("The level of the Corrosion effect applied by the Predation Mode.")
      public int predationCorrosionLevel = 2;
      @Comment("The multiplier of the target's EP to be turned into the user's EP when killed with the Predation Mode.")
      public float predationEPSteal = 0.5F;
      @Comment("The multiplier of magicule gained from dissolving items with the Isolation Mode.")
      public double magiculeMultiplier = 2.0;
      @Comment("The multiplier of health healed from dissolving items with the Isolation Mode..")
      public float healthMultiplier = 2.0F;
      @Comment("The cooldown in second of the Isolation Mode.")
      public int isolationCooldown = 5;
      @Comment("The cooldown in second of the Isolation Mode when mastered.")
      public int isolationCooldownMastered = 3;
      @Comment("Activation Speed Multiplier when activating the Corrosion Mode.")
      public double corrosionSpeedMultiplier = 0.5;
      @Comment("The radius in block of the Corrosion Mode.")
      public double corrosionRadius = 5.0;
      @Comment("The amount of damage dealt onto targets every 10 tick using the Corrosion Mode.")
      public float corrosionDamage = 10.0F;
      @Comment("The EP multiplier of targets killed by the Corrosion Mode to be added to the user's Each of Aura and Magicule.")
      public double corrosionEPSteal = 0.4;
      @Comment("The bonus water capacity when the skill is acquired.")
      public float waterCapacity = 3000.0F;
      @Comment("The bonus lava capacity when the skill is acquired.")
      public float lavaCapacity = 3000.0F;
   }

   public static class GodlyCraftsman extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("How many levels that GodlyCraftsman can go above the maximum level of an enchantment.")
      public int maxBonusLevel = 2;
      @Comment("Lists of enchantments that Godly Craftsman cannot learn or add.")
      public List<String> enchantmentBlacklist = List.of(
         "tensura:dead_end_rainbow",
         "tensura:holy_coat",
         "tensura:magic_interference",
         "tensura:tsukumogami",
         "tensura:enervation",
         "tensura:lethargy",
         "tensura:sealing",
         "tensura:stagnation",
         "tensura:ruination",
         "tensura:vitality",
         "tensura:vigor",
         "tensura:transcendence",
         "tensura:growth",
         "tensura:restoration"
      );
      @Comment("Lists of enchantments that Godly Craftsman cannot learn or add above the enchantment's maximum level.")
      public List<String> maxBonusBlacklist = TensuraEnchantmentHelper.ENGRAVEMENT_ID_LIST;
      @Comment("The percentage chance to obtain a Curse Engraving per Engraving on the item.")
      public float curseChance = 0.03F;
   }

   public static class Gourmand extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 70000.0;
      @Comment("The bonus Aura percentage the user gains when toggled on.")
      public double auraPercentage = 3.0;
      @Comment("The bonus Aura percentage the user gains when toggled on with Mastery.")
      public double auraPercentageMastered = 7.5;
      @Comment("The bonus Magicule percentage the user gains when toggled on.")
      public double magiculePercentage = 3.0;
      @Comment("The bonus Magicule percentage the user gains when toggled on with Mastery.")
      public double magiculePercentageMastered = 7.5;
      @Comment("The chance to steal MP from targets when the user attack them with Gourmand.")
      public double epStealChance = 50.0;
      @Comment("The chance to steal MP from targets when the user attack them with Gourmand when mastered.")
      public double epStealChanceMastered = 75.0;
      @Comment("The multiplier of MP to steal from targets when the user attack them with Gourmand.")
      public double epStealPercentage = 0.01;
      @Comment("The optional level of Fear that the target needs to have to be affected by Heart Eat.")
      public double fearHeartEat = 5.0;
      @Comment("The optional multiplier of the user's EP that the target needs to have below to be affected by Heart Eat.")
      public double epHeartEat = 0.1;
      @Comment("The multiplier of the target's EP that the user will recover with once activated Heart Eat (Split bewteen MP and AP).")
      public float heartEatEpMultiplier = 1.0F;
   }

   public static class Gourmet extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Corrosion.")
      public double magiculeCostCorrosion = 200.0;
      @Comment("The range in block of the Predation Mode.")
      public float predationRange = 3.0F;
      @Comment("The attack damage of the Predation Mode.")
      public float predationDamage = 25.0F;
      @Comment("The cooldown in second of the Predation Mode.")
      public int predationCooldown = 5;
      @Comment("The cooldown in second of the Predation Mode when mastered.")
      public int predationCooldownMastered = 1;
      @Comment("Activation Speed Multiplier when activating the Corrosion Mode.")
      public double corrosionSpeedMultiplier = 0.5;
      @Comment("The radius in block of the Corrosion Mode.")
      public double corrosionRadius = 5.0;
      @Comment("The amount of damage dealt onto targets every 10 tick using the Corrosion Mode.")
      public float corrosionDamage = 5.0F;
      @Comment("The EP multiplier of targets killed by the Corrosion Mode to be added to the user's Each of Aura and Magicule.")
      public double corrosionEPSteal = 0.3;
      @Comment("The bonus water capacity when the skill is acquired.")
      public float waterCapacity = 6000.0F;
      @Comment("The bonus lava capacity when the skill is acquired.")
      public float lavaCapacity = 6000.0F;
   }

   public static class GreatSage extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 75000.0;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("The bonus number of learning point to gain when toggled.")
      public double learningPoint = 9.0;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 9.0;
      @Comment("The Analysis Level when activated.")
      public int analysisLevel = 8;
      @Comment("The Analysis Level when activated with Mastery.")
      public int analysisLevelMastered = 18;
      @Comment("The Analysis Radius when activated.")
      public int analysisRadius = 15;
      @Comment("The Analysis Radius when activated with Mastery.")
      public int analysisRadiusMastered = 25;
      @Comment("The range in block of Analysis's Copy on mobs.")
      public float copyRange = 10.0F;
      @Comment("The range in block of Analysis's Copy on magic circles.")
      public float copyRangeMagic = 30.0F;
      @Comment("The chance to success copying skills from targets.")
      public float copyChance = 25.0F;
      @Comment("The chance to success copying skills from targets when mastered.")
      public float copyChanceMastered = 50.0F;
      @Comment("The cooldown in second when the user successfully copied a skill from targets.")
      public int copyCooldownSuccess = 10;
      @Comment("The cooldown in second when the user failed to copy a skill from targets.")
      public int copyCooldownFail = 5;
   }

   public static class Greed extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Base Magicule Cost to activate Greed Flare's buff.")
      public double magiculeCostFlare = 10.0;
      @Comment("Base Magicule Cost to activate Greed Flare's ally buff.")
      public double magiculeCostFlareAlly = 50.0;
      @Comment("Base Magicule Cost to activate Greed Flare's attack.")
      public double magiculeCostFlareAttack = 20.0;
      @Comment("Base Magicule Cost to activate Death Wish.")
      public double magiculeCostWish = 1000.0;
      @Comment("The max distance in block for Spiritual Domination.")
      public int maxDistance = 30;
      @Comment("The base time in second to control a player with Spiritual Domination.")
      public int playerControl = 60;
      @Comment("The base time in second to control a non-player entity with Spiritual Domination.")
      public int entityControl = 90;
      @Comment("The distance in block to be considered close-range for Spiritual Domination.")
      public int closeDistance = 5;
      @Comment("The reduced time in second to control a player with Spiritual Domination when in close range.")
      public int closeControl = 30;
      @Comment("The distance in block to be considered far-range for Spiritual Domination.")
      public int farDistance = 20;
      @Comment("The increased time in second to control a player with Spiritual Domination when in far range.")
      public int farControl = 30;
      @Comment("The number of Villager Trade that the targeted player needed to do to reduce 1 second in control time for Spiritual Domination.")
      public int playerTradeControl = 10;
      @Comment("The amount of second to reduce from control time of Spiritual Domination for each merchant trade of a trader mob.")
      public int mobTradeControl = 2;
      @Comment("The activation time in second needed to activate Death Wish.")
      public int deathTime = 10;
      @Comment("The level of movement interference when the target is being casted with Death Wish (-10% speed each level).")
      public int deathInterference = 6;
      @Comment("The range in block of Greed Flare.")
      public double flareRange = 20.0;
      @Comment("The multiplier of the user's current SHP when using Greed Flare's Buff.")
      public float flareBuff = 0.05F;
      @Comment("The multiplier of the user's current SHP when using Greed Flare's Buff with Mastery.")
      public float flareBuffMastery = 0.1F;
      @Comment("The multiplier of the ally's current SHP when using Greed Flare's Buff on allies.")
      public float flareAllyBuff = 0.025F;
      @Comment("The multiplier of the ally's current SHP when using Greed Flare's Buff on allies with Mastery.")
      public float flareAllyBuffMastery = 0.05F;
      @Comment("The multiplier of the user's current SHP when using Greed Flare's Attack.")
      public float flareAttack = 0.025F;
      @Comment("The multiplier of the user's current SHP when using Greed Flare's Attack with Mastery.")
      public float flareAttackMastery = 0.05F;
      @Comment("The cooldown in second of Greed Flare's Buff activate.")
      public int flareCooldown = 20;
      @Comment("The cooldown in second of Greed Flare's Buff activate when mastered.")
      public int flareCooldownMastered = 10;
      @Comment("The cooldown in second of Greed Flare's Attack.")
      public int flareAttackCooldown = 20;
      @Comment("The cooldown in second of Greed Flare's Attack when mastered.")
      public int flareAttackCooldownMastered = 10;
   }

   public static class Guardian extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Base Magicule Cost to activate.")
      public double magiculeCost = 100.0;
      @Comment("The radius of the Grant Protection Mode.")
      public double protectionRadius = 25.0;
      @Comment("The duration in tick of the Protection effect to apply on Allies.")
      public int protectionDuration = 3600;
      @Comment("The amount of armor point gained when applied with Protection.")
      public double protectionArmor = 10.0;
      @Comment("The amount of barrier point gained when applied with Protection.")
      public double protectionBarrier = 30.0;
      @Comment("The amount of armor point gained when activated Iron Wall.")
      public double wallArmor = 15.0;
      @Comment("The amount of armor point gained when activated Iron Wall with mastery.")
      public double wallArmorMastered = 40.0;
      @Comment("The amount of knockback resistance gained when activated Iron Wall.")
      public double wallKnockResist = 0.4;
      @Comment("The amount of knockback resistance gained when activated Iron Wall with mastery.")
      public double wallKnockResistMastered = 1.0;
   }

   public static class Healer extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to apply/remove Infection for other entities.")
      public double magiculeCostInfection = 200.0;
      @Comment("Magicule Cost to heal each HP.")
      public double magiculeCostHP = 80.0;
      @Comment("Magicule Cost to heal each HP when mastered.")
      public double magiculeCostHPMastered = 40.0;
      @Comment("Magicule Cost to heal each SHP when mastered.")
      public double magiculeCostSHP = 60.0;
      @Comment("The cooldown in second when activated Heal.")
      public int cooldown = 5;
      @Comment("The cooldown in second when activated Heal with mastery.")
      public int cooldownMastered = 3;
      @Comment("The duration in tick of the Infection effect when applied on targets.")
      public int infectionDuration = 900;
      @Comment("The radius in block of the Plague Mode.")
      public int plagueRadius = 7;
      @Comment("The cooldown in second when activated Infection.")
      public int cooldownInfection = 3;
      @Comment("The cooldown in second when activated Infection with mastery.")
      public int cooldownInfectionMastered = 1;
   }

   public static class InfinityPrison extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 90000.0;
      @Comment("The base magicule cost to block per damage point with Absolute Guard.")
      public double magiculeCostGuard = 25.0;
      @Comment("The Minimal Magicule Cost to activate Imprison.")
      public double magiculeCostImprison = 50000.0;
      @Comment("The multiplier of the target's EP to be added as Magicule Cost for the user to imprison the target.")
      public double magiculeCostImprisonTarget = 0.5;
      @Comment("The multiplier of the user's EP that an attacker needs to have above to bypass Absolute Guard.")
      public double guardEP = 0.75;
      @Comment("The max range in block to Imprison an target.")
      public double imprisonRange = 30.0;
      @Comment("The duration in tick of the Imprison effect.")
      public int imprisonDuration = 6000;
      @Comment("The duration in tick of the Imprison effect when mastered.")
      public int imprisonDurationMastered = 12000;
      @Comment("The cooldown in second when activated Imprison.")
      public int cooldownImprison = 20;
      @Comment("The cooldown in second when activated Imprison with mastery.")
      public int cooldownImprisonMastered = 10;
      @Comment("The bonus water capacity when the skill is acquired.")
      public float waterCapacity = 9000.0F;
      @Comment("The bonus lava capacity when the skill is acquired.")
      public float lavaCapacity = 9000.0F;
   }

   public static class Lust extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Magicule Cost to activate Drain.")
      public double magiculeCostDrain = 100.0;
      @Comment("Magicule Cost to activate Rebirth.")
      public double magiculeCostRebirth = 3000.0;
      @Comment("Magicule Cost to activate Embracing Drain.")
      public double magiculeCostEmbrace = 500.0;
      @Comment("Magicule Cost to activate Death Blessing.")
      public double magiculeCostBless = 7500.0;
      @Comment("Magicule Cost to heal each HP with Invigorate.")
      public double magiculeCostHP = 80.0;
      @Comment("Magicule Cost to heal each HP with Invigorate with Mastery.")
      public double magiculeCostHPMastered = 40.0;
      @Comment("The amount of EP drained from targets when using Drain.")
      public double drainEP = 200.0;
      @Comment("The multiplier of EP drained from targets when using Drain with mastery.")
      public double drainEPMastered = 0.005;
      @Comment("The duration in tick of the Lust Drain effect applied on the user's attack when activated Drain.")
      public int drainDuration = 200;
      @Comment("The cooldown in second of the Drain Mode.")
      public int cooldownDrain = 1;
      @Comment("The cooldown in second when activated Invigorate.")
      public int cooldownInvigorate = 5;
      @Comment("The cooldown in second when activated Invigorate with mastery.")
      public int cooldownInvigorateMastered = 3;
      @Comment("The duration in tick of the Embracing Drain effect.")
      public int embraceDuration = 100;
      @Comment("The amount of EP drained from targets when using Embracing Drain.")
      public double embraceEP = 200.0;
      @Comment("The multiplier of EP drained from targets when using Embracing Drain with mastery.")
      public double embraceEPMastered = 0.005;
      @Comment("The max range in block of the Death Blessing attack.")
      public double blessRange = 10.0;
      @Comment("The radius in block of the Death Blessing attack.")
      public double blessRadius = 3.0;
      @Comment("The amount of time in tick needed to activate the Death Blessing attack.")
      public int blessTime = 100;
      @Comment("The level of movement interference when the target is being casted with Death Wish (-10% speed each level).")
      public int blessInterference = 6;
      @Comment("The multiplier of the user's EP that the target needs to have below to take full effect of Death Blessing.")
      public float blessEP = 0.5F;
      @Comment("The multiplier of the user's EP that the target needs to have below to take half effect of Death Blessing.")
      public float blessHalfEP = 0.75F;
      @Comment("The multiplier of the target's EP that the user will use for energy restoring when killed with full-effect Death Blessing.")
      public float blessFullRestore = 1.0F;
      @Comment("The multiplier of the target's HP that it takes when applied with half-effect Death Blessing.")
      public float blessHalfDamage = 0.5F;
      @Comment("The multiplier of the target's EP that the user will use for energy restoring when killed with half-effect Death Blessing.")
      public float blessHalfRestore = 0.25F;
      @Comment("The multiplier of the user's EP that the target needs to have below to take minimal effect of Death Blessing.")
      public float blessMinimalEP = 1.0F;
      @Comment("The multiplier of the target's HP that it takes when applied with minimal-effect Death Blessing.")
      public float blessMinimalDamage = 0.25F;
      @Comment("The multiplier of the target's EP that the user will use for energy restoring when killed with minimal-effect Death Blessing.")
      public float blessMinimalRestore = 0.05F;
      @Comment(
         "The multiplier of the target's EP that the player will use to restore Magicule when killed with Death Blessing (the other half used for restoring Aura)."
      )
      public float blessMPHeal = 0.75F;
      @Comment("The cooldown in second of the Death Bless mode.")
      public int cooldownBless = 5;
   }

   public static class MartialMaster extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 40000.0;
      @Comment("Aura Cost to activate Ultra Acceleration.")
      public double auraCost = 100.0;
      @Comment("The melee/battlewill damage multiplier when using Secret.")
      public float damageMultiplier = 1.5F;
      @Comment("The Ultra Acceleration distance when activated.")
      public double ultraDistance = 15.0;
      @Comment("The Ultra Acceleration distance when activated with mastery.")
      public double ultraDistanceMastered = 20.0;
      @Comment("The bonus attack when using Ultra Acceleration on a target.")
      public float ultraDamage = 15.0F;
      @Comment("The bonus attack when using Ultra Acceleration on a target when mastered.")
      public float ultraDamageMastered = 75.0F;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 25.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 100.0;
      @Comment("The bonus dodge strength when toggled.")
      public double dodgeStrength = 0.25;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 2;
      @Comment("The bonus number of bonus art-learning point to gain when toggled.")
      public double learningPoint = 4.0;
      @Comment("The bonus number of bonus art-mastery point to gain when toggled.")
      public double masteryPoint = 4.0;
   }

   public static class Mathematician extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 40000.0;
      @Comment("The Analysis Level when activated.")
      public int analysisLevel = 8;
      @Comment("The Analysis Level when activated with Mastery.")
      public int analysisLevelMastered = 12;
      @Comment("The Analysis Radius when activated.")
      public int analysisRadius = 5;
      @Comment("The Analysis Radius when activated with Mastery.")
      public int analysisRadiusMastered = 15;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 10.0;
      @Comment("Dodge Negation Chance when toggled.")
      public double dodgeNegation = 50.0;
      @Comment("Critical Attack Chance when toggled.")
      public double critChance = 75.0;
      @Comment("The bonus number of learning point to gain when toggled.")
      public double learningPoint = 2.0;
      @Comment("The bonus number of mastery point to gain when toggled.")
      public double masteryPoint = 2.0;
   }

   public static class Merciless extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 70000.0;
      @Comment("Magicule Cost to activate Soul Steal.")
      public double magiculeCostSteal = 100.0;
      @Comment("Magicule Cost to activate Soul Consume.")
      public double magiculeCostConsume = 100.0;
      @Comment("The radius in block of the Soul Steal mode.")
      public int stealRadius = 15;
      @Comment("The multiplier of HP that the target needs to have below to be affected by the Soul Steal mode.")
      public double stealHP = 0.1;
      @Comment("The multiplier of user's EP that the target needs to have below to be affected by the Soul Steal mode.")
      public double stealEP = 0.1;
      @Comment("The level of Fear that the target needs to have to be affected by the Soul Steal mode.")
      public double stealFear = 5.0;
      @Comment("The duration in tick of the Soul Drain effect applied on targets with Soul Consume.")
      public int drainDuration = 100;
      @Comment("The level of the Soul Drain effect applied on targets with Soul Consume.")
      public int drainLevel = 1;
   }

   public static class Murderer extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 50.0;
      @Comment("The level of the Presence Conceal when hold down the skill.")
      public int concealmentLevel = 3;
      @Comment("The amount of bonus physical damage when hold down the skill.")
      public double damageBoost = 50.0;
      @Comment("The amount of bonus physical damage when hold down the skill with mastery.")
      public double damageBoostMastery = 150.0;
   }

   public static class Musician extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 70000.0;
      @Comment("Magicule Cost to activate Sonic Blast.")
      public double magiculeCostBlast = 50.0;
      @Comment("Magicule Cost to activate Sonic Wave.")
      public double magiculeCostWave = 100.0;
      @Comment("Magicule Cost to activate Mind Requiem.")
      public double magiculeCostRequiem = 200.0;
      @Comment("The range in block of the Sonic Blast mode.")
      public int blastRange = 8;
      @Comment("The range in block of the Sonic Blast mode when mastered.")
      public int blastRangeMastered = 12;
      @Comment("The damage of the Sonic Blast mode.")
      public int blastDamage = 75;
      @Comment("The damage of the Sonic Blast mode when mastered.")
      public int blastDamageMastered = 150;
      @Comment("The cooldown in second of the Sonic Blast mode.")
      public int blastCooldown = 1;
      @Comment("The radius in block of the Sonic Wave mode.")
      public int waveRadius = 5;
      @Comment("The damage of the Sonic Wave mode.")
      public int waveDamage = 40;
      @Comment("The damage of the Sonic Wave mode when mastered.")
      public int waveDamageMastered = 75;
      @Comment("The cooldown in second of the Sonic Wave mode.")
      public int waveCooldown = 1;
      @Comment("The range in block of the Mind Requiem mode.")
      public int requiemRange = 10;
      @Comment("The damage of the Mind Requiem mode.")
      public int requiemDamage = 150;
      @Comment("The spiritual damage of the Mind Requiem mode.")
      public int requiemSpiritualDamage = 100;
      @Comment("The cooldown in second of the Mind Requiem mode.")
      public int requiemCooldown = 3;
   }

   public static class Observer extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("The Bonus Presence Sense Level when activated.")
      public double bonusSenseLevel = 1.0;
      @Comment("The Bonus Presence Sense Radius when activated.")
      public double bonusSenseRadius = 20.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 20.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 100.0;
   }

   public static class Oppressor extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Repel.")
      public double magiculeCostRepel = 50.0;
      @Comment("Magicule Cost to activate Attract.")
      public double magiculeCostAttract = 50.0;
      @Comment("Magicule Cost to activate Oppress.")
      public double magiculeCostOppress = 350.0;
      @Comment("Magicule Cost to activate Bleve.")
      public double magiculeCostBleve = 1000.0;
      @Comment("Magicule Cost to activate Flicker.")
      public double magiculeCostFlicker = 50.0;
      @Comment("The max range in block of the Repel mode.")
      public double repelRange = 10.0;
      @Comment("The max range in block of the Attract mode.")
      public double attractRange = 30.0;
      @Comment("The max power scale of the Repel/Attract/Flicker mode.")
      public int maxScale = 10;
      @Comment("The max range in block of the Oppress mode.")
      public double oppressRange = 20.0;
      @Comment("The level of the Burden effect from the Oppress mode.")
      public int oppressBurden = 2;
      @Comment("The duration in tick of the Oppression effect from the Oppress mode.")
      public int oppressDuration = 600;
      @Comment("The oppress damage of the Oppress mode.")
      public float oppressDamage = 100.0F;
      @Comment("The oppress damage of the Oppress mode with mastery.")
      public float oppressDamageMastered = 200.0F;
      @Comment("The cooldown in second of the Oppress mode.")
      public int oppressCooldown = 10;
      @Comment("The cooldown in second of the Oppress mode with mastery.")
      public int oppressCooldownMastered = 5;
      @Comment("The max range in block of the Bleve mode.")
      public double bleveRange = 20.0;
      @Comment("The bleve damage of the Bleve mode.")
      public float bleveDamage = 100.0F;
      @Comment("The bleve damage of the Bleve mode with mastery.")
      public float bleveDamageMastery = 200.0F;
      @Comment("The cooldown in second of the Bleve mode.")
      public int bleveCooldown = 10;
      @Comment("The cooldown in second of the Bleve mode with mastery.")
      public int bleveCooldownMastered = 5;
   }

   public static class Predator extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to remove each harmful status effect with Isolation.")
      public double magiculeCostIsolation = 200.0;
      @Comment("The max range in block of the Predation Mode.")
      public float predationRange = 3.0F;
      @Comment("The attack damage of the Predation Mode.")
      public float predationDamage = 10.0F;
      @Comment("The amount of EP that the user drains from target using the Predation Mode.")
      public float predationEPDrain = 100.0F;
      @Comment("The chance to obtain skills from targets without killing them with the Predation Mode.")
      public float predationSkillChance = 10.0F;
      @Comment("The number of skills to gain from targets at a time without killing them with the Predation Mode.")
      public int predationSkillNumber = 1;
      @Comment("The multiplier of the target's EP to be turned into the user's EP when killed with the Predation Mode.")
      public float predationEPSteal = 0.3F;
      @Comment("The chance multiplier for each of the target's learnable magic to be obtained by the user when killed with the Predation Mode.")
      public float predationMagicCopy = 0.5F;
      @Comment("The multiplier of magicule gained from dissolving items with the Isolation Mode.")
      public double magiculeMultiplier = 2.0;
      @Comment("The multiplier of health healed from dissolving items with the Isolation Mode..")
      public float healthMultiplier = 2.0F;
      @Comment("The cooldown in second of the Isolation Mode.")
      public int isolationCooldown = 5;
      @Comment("The cooldown in second of the Isolation Mode when mastered.")
      public int isolationCooldownMastered = 3;
      @Comment("The bonus water capacity when the skill is acquired.")
      public float waterCapacity = 3000.0F;
      @Comment("The bonus lava capacity when the skill is acquired.")
      public float lavaCapacity = 3000.0F;
   }

   public static class Pride extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("The skill copy chance when attacked.")
      public double copyChance = 20.0;
      @Comment("The skill copy chance when attacked with mastery.")
      public double copyChanceMastered = 100.0;
      @Comment("The amount of mastery that Pride gains per Magicule Cost of the successfully copied ability.")
      public float copyMastery = 4.0E-4F;
      @Comment("The multiplier of mastery that Pride gains per Magicule Cost of the failed ability.")
      public float copyMasteryFail = 4.0E-5F;
      @Comment("The cooldown in second that Pride gets per Mastery gained from successfully copying an ability.")
      public double copyCooldown = 45.0;
      @Comment("The cooldown in second that Pride gets per Mastery gained from failing to copy an ability.")
      public double copyCooldownFail = 4.5;
   }

   public static class Reaper extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("The size when activated Recon.")
      public double size = 0.5;
      @Comment("The bonus Presence Sense Level when activated Recon.")
      public double bonusSenseLevel = 1.0;
      @Comment("The bonus Presence Sense Radius when activated Recon.")
      public double bonusSenseRadius = 10.0;
      @Comment("The bonus Presence Sense Radius when activated Recon with mastery.")
      public double bonusSenseRadiusMastered = 20.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 15.0;
      @Comment("Melee Dodge Chance when toggled with mastered.")
      public double meleeDodgeMastered = 30.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 15.0;
      @Comment("Projectile Dodge Chance when toggled with mastered.")
      public double projectileDodgeMastered = 30.0;
      @Comment("The multiplier of size/aura/magicule when activated the Attack Mode.")
      public float attackMultiplier = 0.5F;
      @Comment("The max number of clones when activated the Attack Mode.")
      public int attackNumber = 5;
      @Comment("The cooldown in second of the Attack Mode.")
      public int attackCooldown = 10;
      @Comment("The bonus range in block of the Infinite Eater Mode.")
      public double eaterBonusRange = 3.0;
      @Comment("The multiplier of the target's EP to be turned into the user's EP when killed with the Infinite Eater Mode.")
      public double eaterEPSteal = 0.5;
      @Comment("The cooldown in second of the Infinite Eater Mode.")
      public int eaterCooldown = 10;
      @Comment("The cooldown in second of the Infinite Eater Mode when mastered.")
      public int eaterCooldownMastered = 5;
   }

   public static class Reflector extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("The speed multiplier when activating the Echo Counter mode.")
      public double counterSpeedMultiplier = 0.0;
      @Comment("The speed multiplier when activating the Echo Counter mode with mastery.")
      public double counterSpeedMultiplierMastered = 1.0;
      @Comment("The reflected damage when using Echo Counter.")
      public float counterDamageMultiplier = 2.5F;
      @Comment("The reflected projectile speed when using Echo Counter.")
      public double counterProjectileSpeedMultiplier = 2.0;
      @Comment("The cooldown in second of the Echo Counter mode.")
      public int counterCooldown = 5;
      @Comment("The base maximum echo point to store.")
      public int maximumPoint = 100;
      @Comment("The multiplier of user's EP to be calculated for bonus echo point.")
      public double bonusPointMultiplier = 0.001;
      @Comment("The projectile damage multiplier when using Echo Reflection.")
      public float reflectionDamageMultiplier = 3.0F;
      @Comment("The projectile damage multiplier when using Echo Reflection with mastery.")
      public float reflectionDamageMultiplierMastered = 5.0F;
   }

   public static class Researcher extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("How many levels that Researcher can go above the maximum level of an enchantment.")
      public int maxBonusLevel = 1;
      @Comment("Lists of enchantments that Researcher cannot learn or add.")
      public List<String> enchantmentBlacklist = TensuraEnchantmentHelper.ENGRAVEMENT_ID_LIST;
      @Comment("Lists of enchantments that Researcher cannot learn or add above the enchantment's maximum level.")
      public List<String> maxBonusBlacklist = TensuraEnchantmentHelper.ENGRAVEMENT_ID_LIST;
      @Comment("The percentage chance to obtain a Curse Engraving per Engraving on the item.")
      public float curseChance = 0.03F;
   }

   public static class Reverser extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to reverse each Curses on the equipped items when mastered.")
      public double magiculeCostCurse = 10000.0;
   }

   public static class RoyalBeast extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDuration = 3600;
      @Comment("The duration in tick of the Transformation.")
      public int transformationDurationMastered = 7200;
      @Comment("The Cooldown in second after activation.")
      public int cooldown = 1200;
   }

   public static class Seeker extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 40000.0;
      @Comment("The range in block of Analyze.")
      public float analyzeRange = 15.0F;
      @Comment("The hold time in tick of Analyze to copy a Magic.")
      public int analyzeTime = 60;
      @Comment("The hold time in tick of Analyze to copy a Magic.")
      public int analyzeTimeMastered = 20;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("The bonus number of bonus magic-learning point to gain when toggled.")
      public double learningPoint = 4.0;
      @Comment("The bonus number of bonus magic-mastery point to gain when toggled.")
      public double masteryPoint = 4.0;
   }

   public static class Seer extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 20000.0;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 25.0;
      @Comment("Melee Dodge Chance when toggled with mastered.")
      public double meleeDodgeMastered = 50.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 25.0;
      @Comment("Projectile Dodge Chance when toggled with mastered.")
      public double projectileDodgeMastered = 50.0;
      @Comment("Critical Attack Chance when toggled.")
      public double criticalChance = 33.0;
      @Comment("Critical Attack Chance when toggled with mastered.")
      public double criticalChanceMastered = 50.0;
      @Comment("The input damage multiplier when toggled.")
      public float inputMultiplier = 0.7F;
      @Comment("The input damage multiplier when toggled with mastery.")
      public float inputMultiplierMastered = 0.5F;
      @Comment("The duration in tick of the Future Vision effect.")
      public int visionDuration = 200;
      @Comment("The duration in tick of the Future Vision effect when mastered.")
      public int visionDurationMastered = 400;
      @Comment("The cooldown in second of the Future Vision effect.")
      public int visionCooldown = 10;
   }

   public static class Severer extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate Dummy Sword.")
      public double magiculeCostDummy = 100.0;
      @Comment("Magicule Cost to activate Blade Storm.")
      public double magiculeCostStorm = 2000.0;
      @Comment("Magicule Cost to activate Severance.")
      public double magiculeCostSeverance = 200.0;
      @Comment("The level of the Severance engraving of the Dummy Sword.")
      public int engravingLevel = 5;
      @Comment("The level of the Severance engraving of the Dummy Sword when mastered.")
      public int engravingLevelMastered = 10;
      @Comment("The number of blades of the Blade Storm.")
      public int stormNumber = 5;
      @Comment("The number of blades of the Blade Storm when mastered.")
      public int stormNumberMastered = 10;
      @Comment("The cooldown in second of the Blade Storm.")
      public int stormCooldown = 3;
      @Comment("The cooldown in second of the Blade Storm when mastered.")
      public int stormCooldownMastered = 1;
      @Comment("The level of the Severance effect when activating the Severance mode.")
      public int severanceLevel = 1;
      @Comment("The level of the Severance effect when activating the Severance mode with mastery.")
      public int severanceLevelMastered = 2;
      @Comment("The duration in tick of the Severance effect when activating the Severance mode.")
      public int severanceDuration = 2400;
   }

   public static class ShadowStriker extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("Magicule Cost to activate Insta-kill.")
      public double magiculeCostKill = 3000.0;
      @Comment("Aura Cost to activate Ultra Acceleration.")
      public double auraCost = 100.0;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("The bonus dodge strength when toggled.")
      public double dodgeStrength = 0.25;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 2;
      @Comment("The Ultra Acceleration distance when activated.")
      public double ultraDistance = 15.0;
      @Comment("The Ultra Acceleration distance when activated with mastery.")
      public double ultraDistanceMastered = 20.0;
      @Comment("The bonus attack when using Ultra Acceleration on a target.")
      public float ultraDamage = 0.0F;
      @Comment("The bonus attack when using Ultra Acceleration on a target when mastered.")
      public float ultraDamageMastered = 70.0F;
      @Comment("The amount of spiritual damage on target when using Insta-Kill.")
      public float killDamage = 500.0F;
      @Comment("The amount of spiritual damage on target when using Insta-Kill with mastery.")
      public float killDamageMastered = 1000.0F;
      @Comment("The cooldown in second of the Insta-Kill.")
      public int killCooldown = 10;
      @Comment("The cooldown in second of the Insta-Kill when mastered.")
      public int killCooldownMastered = 5;
      @Comment("The level of Presence Concealment when using Espionage.")
      public int concealmentLevel = 1;
      @Comment("The level of Presence Concealment when using Espionage with mastery.")
      public int concealmentLevelMastered = 2;
   }

   public static class Sloth extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Magicule Cost to activate Deprive.")
      public double magiculeCostDeprive = 200.0;
      @Comment("Magicule Cost to activate Fallen Strike.")
      public double magiculeCostFallen = 5000.0;
      @Comment("The range in block of the Deep Hypno mode when used on a living target.")
      public int deepRange = 10;
      @Comment("The range in block of the Deep Hypno mode when used on a magic circle.")
      public int deepRangeMagic = 30;
      @Comment("The base duration of the Drowsiness effect when using the Deep Hypno mode.")
      public int deepDuration = 100;
      @Comment("The base duration of the Drowsiness effect when using the Deep Hypno mode with mastery.")
      public int deepDurationMastered = 200;
      @Comment("The amount of tick activated needed to increase 1 level of Drowsiness when using the Deep Hypno.")
      public int deepIncreaseTick = 200;
      @Comment("The cooldown in second of the magic circle destroyed by Deep Hypno for the targeted caster.")
      public int deepMagicCooldown = 3;
      @Comment("The cooldown in second of the magic circle destroyed by Deep Hypno for the targeted caster when mastered.")
      public int deepMagicCooldownMastered = 5;
      @Comment("The radius in block of the Fallen Hypno mode.")
      public int fallenRadius = 5;
      @Comment("The radius in block of the Fallen Hypno mode when mastered.")
      public int fallenRadiusMastered = 10;
      @Comment("The base duration of the Drowsiness effect when using the Fallen Hypno mode.")
      public int fallenDuration = 100;
      @Comment("The base duration of the Drowsiness effect when using the Fallen Hypno mode with mastery.")
      public int fallenDurationMastered = 200;
      @Comment("The amount of tick activated needed to increase 1 level of Drowsiness when using the Fallen Hypno.")
      public int fallenIncreaseTick = 200;
      @Comment("The radius in block of the Deprive mode.")
      public int depriveRadius = 5;
      @Comment("The radius in block of the Deprive mode when mastered.")
      public int depriveRadiusMastered = 15;
      @Comment("The amount of EP to drain from targets when using Deprive.")
      public double depriveEP = 1000.0;
      @Comment("The multiplier of targets' EP to drain from targets when using Deprive with mastery.")
      public float depriveEPMastered = 0.003F;
      @Comment("The amount of Spiritual damage dealt on targets when using Deprive.")
      public float depriveSHP = 10.0F;
      @Comment("The multiplier of Spiritual damage dealt on targets when using Deprive with mastery.")
      public float depriveSHPMastered = 0.01F;
      @Comment("The amount of SHP to heal each second when using Rest.")
      public float restHP = 10.0F;
      @Comment("The multiplier of SHP to heal each second when using Rest with mastery.")
      public float restHPMastered = 0.01F;
      @Comment("The amount of HP to heal each second when using Rest.")
      public float restSHP = 10.0F;
      @Comment("The multiplier of HP to heal each second when using Rest with mastery.")
      public float restSHPMastered = 0.01F;
      @Comment("The multiplier of Aura Regeneration when using Rest.")
      public float restAP = 10.0F;
      @Comment("The multiplier of Magicule Regeneration when using Rest.")
      public float restMP = 10.0F;
      @Comment("The radius in block of ally heal area when using Rest.")
      public double restAllyRadius = 15.0;
      @Comment("The Stored Magicule cost to heal each Ally.")
      public double restAllyCost = 1000.0;
      @Comment("The amount of HP to heal Allies each second when using Rest.")
      public float restAllyHP = 50.0F;
      @Comment("The amount of SHP to heal Allies each second when using Rest.")
      public float restAllySHP = 10.0F;
      @Comment("The amount of Aura/Magicule each to heal Allies each second when using Rest.")
      public float restAllyEP = 1000.0F;
      @Comment("The amount of Spiritual damage dealt on targets when using Phantasmal Style.")
      public float phantasmalSHP = 10.0F;
      @Comment("The multiplier of Spiritual damage dealt on targets when using Phantasmal Style with mastery.")
      public float phantasmalSHPMastered = 0.01F;
      @Comment("The max range in block of Fallen Strike.")
      public double fallStrikeRange = 8.0;
      @Comment("The amount of Spiritual Damage to deal on targets when using Fallen Strike.")
      public float fallStrikeSHP = 500.0F;
      @Comment("The cooldown in second of the Fallen Strike mode.")
      public int fallStrikeCooldown = 10;
   }

   public static class Sniper extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Create Weapon.")
      public int magiculeCostWeapon = 100;
      @Comment("Melee Dodge Chance when toggled.")
      public double meleeDodge = 10.0;
      @Comment("Projectile Dodge Chance when toggled.")
      public double projectileDodge = 10.0;
      @Comment("Dodge Negation Chance when toggled.")
      public double dodgeNegation = 50.0;
      @Comment("The level of Presence Sense when toggled.")
      public double presenceSense = 2.0;
      @Comment("The Spatial Damage Boost when activated Manipulation.")
      public double manipulationBoost = 2.0;
      @Comment("The warp shot level when activated Spatial Manipulation's Warp Shot.")
      public double warpShot = 0.5;
      @Comment("Magicule/Aura Cost to use the created pistol.")
      public int energyCostPistol = 100;
      @Comment("The damage output of the Physical bullet.")
      public int physicalDamage = 30;
      @Comment("The cooldown in tick of the Physical bullet.")
      public int physicalCooldown = 10;
      @Comment("The damage output of the Magic bullet.")
      public int magicDamage = 100;
      @Comment("The damage output of the Magic bullet when mastered.")
      public int magicDamageMastered = 200;
      @Comment("The cooldown in tick of the Magic bullet.")
      public int magicCooldown = 60;
      @Comment("The explosion radius of the Grenade.")
      public int grenadeExplosion = 4;
      @Comment("The cooldown in second of the Grenade.")
      public int grenadeCooldown = 3;
      @Comment("The cooldown in second of the Grenade when mastered.")
      public int grenadeCooldownMastered = 1;
   }

   public static class Spearhead extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("The radius in block to apply skill effect on Allies.")
      public double allyRadius = 20.0;
      @Comment("The bonus attack damage allies gain when activated (doubled with Mastery).")
      public double allyAttack = 10.0;
      @Comment("The bonus armor allies gain when activated (doubled with Mastery).")
      public double allyArmor = 5.0;
      @Comment("The bonus speed allies gain when activated (doubled with Mastery).")
      public double allySpeed = 0.02;
      @Comment("The bonus swimming speed allies gain when activated (doubled with Mastery).")
      public int allySwim = 1;
      @Comment("The range in block that the owner needs to be within fallen subordinates to gain their power.")
      public double fallenRange = 20.0;
   }

   public static class Starved extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Corrosion.")
      public double magiculeCostCorrosion = 200.0;
      @Comment("The duration in tick of the Corrosion effect when attacking targets.")
      public int corrosionDuration = 200;
      @Comment("The level of the Corrosion effect when attacking targets.")
      public int corrosionLevel = 2;
      @Comment("Activation Speed Multiplier when activating the Corrosion Mode.")
      public double corrosionSpeedMultiplier = 0.5;
      @Comment("The radius in block of the Corrosion Mode.")
      public double corrosionRadius = 5.0;
      @Comment("The amount of damage dealt onto targets every 10 tick using the Corrosion Mode.")
      public float corrosionDamage = 5.0F;
      @Comment("The EP multiplier of targets killed by the Corrosion Mode to be added to the user's Each of Aura and Magicule.")
      public double corrosionEPSteal = 0.2;
      @Comment("The radius of the Spiritual Domination mode.")
      public int dominationRadius = 15;
      @Comment("The bonus water capacity when the skill is acquired.")
      public float waterCapacity = 3000.0F;
      @Comment("The bonus lava capacity when the skill is acquired.")
      public float lavaCapacity = 3000.0F;
   }

   public static class Suppressor extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate Swap.")
      public double magiculeCostSwap = 100.0;
      @Comment("Magicule Cost to activate Spatial Blockade.")
      public double magiculeCostBlockade = 300.0;
      @Comment("Magicule Cost per block to use Spatial Motion.")
      public double magiculeCostMotion = 10.0;
      @Comment("Magicule Cost per block to use Spatial Motion per block using Portal.")
      public double magiculeCostPortal = 50.0;
      @Comment("The chant speed multiplier when toggled.")
      public double chantSpeed = 2.0;
      @Comment("The duration in tick of the Spatial Blockade when activated.")
      public int blockadeDuration = 1200;
      @Comment("The max range in block of the Swap mode.")
      public int swapRange = 30;
      @Comment("The max range in block of the Spatial Motion mode.")
      public int motionRange = 30;
      @Comment("The max range in block of the Spatial Motion mode when mastered.")
      public int motionRangeMastered = 50;
      @Comment("The charge tick of the Teleport mode before warping any entity.")
      public int warpChargeTick = 0;
      @Comment("The cooldown in second of the Spatial Motion mode.")
      public int motionCooldown = 5;
      @Comment("The cooldown in second of the Spatial Motion mode when mastered.")
      public int motionCooldownMastered = 2;
   }

   public static class Survivor extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 40000.0;
   }

   public static class Thrower extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 30000.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 50.0;
      @Comment("The base entity throw power.")
      public double entityThrow = 3.0;
      @Comment("The bonus entity throw power from Gravity Manipulation.")
      public double entityThrowManipulation = 1.0;
      @Comment("The bonus entity throw power from Gravity Domination.")
      public double entityThrowDomination = 2.0;
      @Comment("The base damage of thrown air.")
      public float airThrowDamage = 30.0F;
      @Comment("The base damage of thrown air when mastered.")
      public float airThrowDamageMastered = 50.0F;
      @Comment("The base damage of thrown items.")
      public float itemThrowDamage = 50.0F;
      @Comment("The base damage of thrown items when mastered.")
      public float itemThrowDamageMastered = 100.0F;
      @Comment("The bonus damage multiplier from Gravity Manipulation.")
      public float itemThrowManipulation = 2.0F;
      @Comment("The bonus damage multiplier from Gravity Domination.")
      public float itemThrowDomination = 3.0F;
      @Comment("The maximum number of blocks that can be broken by throwing Mining Tools.")
      public int maxBreakableBlocks = 10;
   }

   public static class Traveler extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost per block to use Instant Motion.")
      public double magiculeCostMotion = 5.0;
      @Comment("Magicule Cost per block to use Teleport per block.")
      public double magiculeCostTeleport = 5.0;
      @Comment("Magicule Cost per block to use Teleport per block using Portal.")
      public double magiculeCostPortal = 25.0;
      @Comment("Magicule Cost to activate Stardust Arrow.")
      public double magiculeCostArrow = 150.0;
      @Comment("Aura Cost to activate Stardust Arrow.")
      public double auraCostArrow = 150.0;
      @Comment("Magicule Cost to activate Stardust Rain.")
      public double magiculeCostRain = 5000.0;
      @Comment("Aura Cost to activate Stardust Rain.")
      public double auraCostRain = 5000.0;
      @Comment("The Spatial Damage Boost when activated Manipulation.")
      public double manipulationBoost = 2.0;
      @Comment("The warp shot level when activated Spatial Manipulation's Warp Shot.")
      public double warpShot = 0.5;
      @Comment("The max range in block of the Instant Motion mode.")
      public double motionRange = 200.0;
      @Comment("The cooldown in second of the Instant Motion mode.")
      public int motionCooldown = 5;
      @Comment("The cooldown in second of the Instant Motion mode when mastered.")
      public int motionCooldownMastered = 2;
      @Comment("The charge tick of the Teleport mode before warping any entity.")
      public int warpChargeTick = 0;
      @Comment("The cooldown in second of the Teleport mode.")
      public int teleportCooldown = 10;
      @Comment("The cooldown in second of the Teleport mode when mastered.")
      public int teleportCooldownMastered = 5;
      @Comment("The damage of an arrow when using Stardust Arrow.")
      public float arrowDamage = 30.0F;
      @Comment("The max range in block of Stardust Rain.")
      public double rainRange = 20.0;
      @Comment("The number of arrows in Stardust Rain.")
      public int rainNumber = 12;
      @Comment("The damage of an arrow when using Stardust Rain.")
      public float rainDamage = 30.0F;
      @Comment("The cooldown for Stardust Rain.")
      public int rainCooldown = 5;
      @Comment("The cooldown for Stardust Rain when mastered.")
      public int rainCooldownMastered = 4;
   }

   public static class Tuner extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("The multiplier of HP that the user needs to below to activate Unexpected Result.")
      public double hpMultiplier = 0.25;
      @Comment("The bonus attack damage when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusAttack = 30.0;
      @Comment("The bonus attack speed when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusAttackSpeed = 0.2;
      @Comment("The bonus speed when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusSpeed = 0.04;
      @Comment("The bonus swim speed when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusSwim = 1.0;
      @Comment("The bonus melee dodge chance when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusMeleeDodge = 15.0;
      @Comment("The bonus projectile dodge chance when activated Unexpected Result with mastery (doubled with Mastery).")
      public double bonusProjectileDodge = 15.0;
      @Comment("The multiplier of HP that the user gets when revived by the skill.")
      public float hpRevive = 1.0F;
      @Comment("The multiplier of SHP that the user gets when revived by the skill.")
      public double shpRevive = 1.0;
      @Comment("The multiplier of Aura/Magicule that the user gets when revived by the skill.")
      public double epRevive = 0.5;
      @Comment("The multiplier of Aura/Magicule that the user gets when revived by the skill with mastery.")
      public double epReviveMastered = 0.75;
      @Comment("The timer in tick till the next death count reset.")
      public int deathReset = 1200;
   }

   public static class Unyielding extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("Magicule Cost multiplier of the user's maximum Magicule required to spawn a backup.")
      public double magiculeBackupCost = 0.25;
      @Comment("The radius in block that subordinates need to stay within the user to increase Unyielding point.")
      public int unyieldingRadius = 30;
      @Comment("The number of Unyielding points that a subordinate gains every 5 seconds while in required radius.")
      public int unyieldingPointGain = 1;
      @Comment("The number of Unyielding points that a subordinate gains every 5 seconds while in required radius when mastered.")
      public int unyieldingPointGainMastered = 2;
      @Comment("The number of Unyielding points needed to gain each 10% of the EP of a fallen subordinate.")
      public int unyieldingPointEP = 120;
      @Comment("The number of Unyielding points needed to gain skills from a fallen subordinate.")
      public int unyieldingPointSkill = 120;
      @Comment("The number of Unyielding points needed to gain Unique skills from a fallen subordinate.")
      public int unyieldingPointSkillUnique = 600;
      @Comment("The cooldown in second to spawn/swap a backup.")
      public int backupCooldown = 5;
      @Comment("The cooldown in second to spawn/swap a backup when mastered.")
      public int backupCooldownMastered = 3;
   }

   public static class Usurper extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 50000.0;
      @Comment("Magicule Cost to activate Rob.")
      public double magiculeCostRob = 1000.0;
      @Comment("Magicule Cost to activate Copy.")
      public double magiculeCostCopy = 1000.0;
      @Comment("Magicule Cost to activate Force Takeover.")
      public double magiculeCostTakeover = 5000.0;
      @Comment("The multiplier of the target's EP to drain when attacked by the user.")
      public double epDrain = 0.01;
      @Comment("The percentage chance to rob successfully.")
      public double robSuccess = 25.0;
      @Comment("The percentage chance to rob successfully when mastered.")
      public double robSuccessMastered = 50.0;
      @Comment("The multiplier of max mastery that the robbed skill gains.")
      public double robMastery = 0.5;
      @Comment("The cooldown in second of the Rob mode.")
      public int robCooldown = 10;
      @Comment("The percentage chance to copy successfully.")
      public double copySuccess = 25.0;
      @Comment("The percentage chance to copy successfully when mastered.")
      public double copySuccessMastered = 50.0;
      @Comment("The multiplier of max mastery that the copied skill gains.")
      public double copyMastery = 0.5;
      @Comment("The cooldown in second of the Copy mode.")
      public int copyCooldown = 10;
      @Comment("The multiplier of the user's EP that the targeted spirit's owner needs to be higher to not be affected by Takeover.")
      public double takeoverEP = 0.75;
      @Comment("The duration in tick of the Takeover effect on the controlled spirit (-1 = permanent).")
      public int takeoverDuration = 6000;
      @Comment("The duration in tick of the Takeover effect on the controlled spirit when mastered (-1 = permanent).")
      public int takeoverDurationMastered = 12000;
      @Comment("The cooldown in second of the Force Takeover mode.")
      public int takeoverCooldown = 10;
   }

   public static class Villain extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 60000.0;
      @Comment("Magicule Cost to activate Hero Haki.")
      public double magiculeCostHaki = 25.0;
      @Comment("Magicule Cost to activate Hero's Charisma.")
      public double magiculeCostCharisma = 200.0;
      @Comment("The radius in block of the Villain's Intimidation's effect on allies.")
      public int intimidationRadius = 15;
      @Comment("The radius in block of the Villain's Charisma's effect.")
      public int controlRadius = 10;
      @Comment("The duration in tick of the Mind Control effect when activating Villain's Charisma (-1 = permanent).")
      public int controlDuration = 2400;
      @Comment(
         "The duration in tick of the Mind Control effect when activating Villain's Charisma while the target has Spiritual Attack Resistance (-1 = permanent)."
      )
      public int controlResistedDuration = 1200;
      @Comment("The percentage to become Majin when dying of Magicule Poison while having this skill.")
      public int majinPercentage = 100;
      @Comment("The bonus Aura percentage the user gains when toggled on.")
      public double auraPercentage = 1.5;
      @Comment("The bonus Magicule percentage the user gains when toggled on.")
      public double magiculePercentage = 1.5;
      @Comment("The bonus Dodge negate chance the user gains when toggled on.")
      public double dodgeNegateChance = 0.25;
   }

   public static class Wrath extends ManasSubConfig {
      @Comment("Magicule Acquirement Cost.")
      public double mpAcquirement = 100000.0;
      @Comment("Magicule Cost to activate Enrage.")
      public double magiculeCostEnrage = 100.0;
      @Comment("The multiplier of the user's maximum magicule to gain every 100 ticks.")
      public double breederMultiplier = 0.02;
      @Comment("The chance to gain an Rampage level when the user is under maximum Magicule (doubled with mastery).")
      public double breederUnderChance = 3.0;
      @Comment("The chance to gain an Rampage level when the user is above maximum Magicule (doubled with mastery).")
      public double breederAboveChance = 6.0;
      @Comment("The maximum level of Rampage the user can get from using Magicule Breader.")
      public int maxRampage = 10;
      @Comment("The maximum level of Rampage the user can get from using Magicule Breader when mastered.")
      public int maxRampageMastered = 20;
      @Comment("The duration in tick of the Rampage effect when using Magicule Breader.")
      public int breederDuration = 600;
      @Comment("The bonus armor points each level of Rampage on the user when using Magicule Breader.")
      public double rampageArmor = 5.0;
      @Comment("The bonus attack points each level of Rampage on the user when using Magicule Breader.")
      public double rampageAttack = 30.0;
      @Comment("The bonus attack speed each level of Rampage on the user when using Magicule Breader.")
      public double rampageAttackSpeed = 0.02;
      @Comment("The bonus speed points each level of Rampage on the user when using Magicule Breader.")
      public double rampageSpeed = 0.01;
      @Comment("The bonus knockback resistance each level of Rampage on the user when using Magicule Breader.")
      public double rampageKnockbackResistance = 0.1;
      @Comment("The radius in block of the Enrage mode.")
      public double enrageRadius = 7.0;
      @Comment("The base duration of the Rampage effect when using the Enrage mode.")
      public int enrageDuration = 400;
      @Comment("The amount of tick activated needed to increase 1 level of Rampage when using the Enrage.")
      public int enrageIncreaseTick = 200;
   }
}
