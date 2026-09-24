package io.github.manasmods.tensura.config.ability;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.Arrays;
import java.util.List;

public class BattlewillConfig extends ManasConfig {
   public BattlewillConfig.AuraSlash AuraSlash = new BattlewillConfig.AuraSlash();
   public BattlewillConfig.AuraSword AuraSword = new BattlewillConfig.AuraSword();
   public BattlewillConfig.EarthshatterKick EarthshatterKick = new BattlewillConfig.EarthshatterKick();
   public BattlewillConfig.HeavySlash HeavySlash = new BattlewillConfig.HeavySlash();
   public BattlewillConfig.OgreSwordGuillotine OgreSwordGuillotine = new BattlewillConfig.OgreSwordGuillotine();
   public BattlewillConfig.RoaringLionPunch RoaringLionPunch = new BattlewillConfig.RoaringLionPunch();
   public BattlewillConfig.FivePetalsThrust FivePetalsThrust = new BattlewillConfig.FivePetalsThrust();
   public BattlewillConfig.EightPetalsFlash EightPetalsFlash = new BattlewillConfig.EightPetalsFlash();
   public BattlewillConfig.DarkEightPalms DarkEightPalms = new BattlewillConfig.DarkEightPalms();
   public BattlewillConfig.DeathMarchDance DeathMarchDance = new BattlewillConfig.DeathMarchDance();
   public BattlewillConfig.ElephantStampede ElephantStampede = new BattlewillConfig.ElephantStampede();
   public BattlewillConfig.MagicBullet MagicBullet = new BattlewillConfig.MagicBullet();
   public BattlewillConfig.MaximumMagicBullet MaximumMagicBullet = new BattlewillConfig.MaximumMagicBullet();
   public BattlewillConfig.OgreFlame OgreFlame = new BattlewillConfig.OgreFlame();
   public BattlewillConfig.OgreSwordCannon OgreSwordCannon = new BattlewillConfig.OgreSwordCannon();
   public BattlewillConfig.AirFlight AirFlight = new BattlewillConfig.AirFlight();
   public BattlewillConfig.AuraShield AuraShield = new BattlewillConfig.AuraShield();
   public BattlewillConfig.Battlewill Battlewill = new BattlewillConfig.Battlewill();
   public BattlewillConfig.DiamondPath DiamondPath = new BattlewillConfig.DiamondPath();
   public BattlewillConfig.Formhide Formhide = new BattlewillConfig.Formhide();
   public BattlewillConfig.Haze Haze = new BattlewillConfig.Haze();
   public BattlewillConfig.InstantMove InstantMove = new BattlewillConfig.InstantMove();
   public BattlewillConfig.ViolentBreak ViolentBreak = new BattlewillConfig.ViolentBreak();

   public String getFileName() {
      return "tensura/ability/battlewill_config";
   }

   public static class AirFlight extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 15.0;
      @Comment("Magicule Cost to activate.")
      public double magiculeCost = 15.0;
      @Comment("The boost power of the user's forward movement when activated.")
      public float forwardBoost = 0.2F;
      @Comment("The boost power of the user's forward movement when activated with Mastery.")
      public float forwardBoostMastered = 0.4F;
   }

   public static class AuraShield extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 1000.0;
      @Comment("The size in blocks of the created shield gets.")
      public float size = 3.0F;
      @Comment("How much Health that the created shield gets.")
      public int health = 100;
      @Comment("The cooldown in second of the battlewill.")
      public int cooldown = 5;
      @Comment("The cooldown in second of the battlewill when mastered.")
      public int cooldownMastered = 3;
   }

   public static class AuraSlash extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 50.0;
      @Comment("The damage multiplier of the projectiles compare to the user's weapon base attack damage when activated.")
      public float attackMultiplier = 1.0F;
      @Comment("The damage multiplier of the projectiles compare to the user's weapon base attack damage when activated when Mastered.")
      public float attackMultiplierMastered = 2.0F;
   }

   public static class AuraSword extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 200.0;
      @Comment("How long in tick that Aura Sword will stay on user after activated.")
      public int effectTime = 1200;
      @Comment("The bonus multiplier of the user's weapon base attack damage when activated.")
      public float attackMultiplier = 1.0F;
   }

   public static class Battlewill extends ManasSubConfig {
      @Comment("Aura Cost to learn (before multiplier).")
      public double auraCost = 200.0;
      @Comment("How much percentage of Magicule gets converted into Aura each 30 ticks (doubled when Mastered).")
      public int percentage = 1;
   }

   public static class DarkEightPalms extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 200.0;
      @Comment("The Max Multiplier of the Attack Power.")
      public double maxMultiplier = 8.0;
      @Comment("The Time the user need to hold down to increase 1 Power level.")
      public int holdTime = 20;
      @Comment("The Time the user need to hold down to increase 1 Power level with Mastery.")
      public int holdTimeMastered = 10;
      @Comment("The Base Damage of each Aura Bullet.")
      public float baseDamage = 100.0F;
   }

   public static class DeathMarchDance extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 100.0;
      @Comment("The range in block of the bullets when homing.")
      public double range = 50.0;
      @Comment("The Max Multiplier of the Attack Power.")
      public double maxMultiplier = 5.0;
      @Comment("The Max Multiplier of the Attack Power when Mastered.")
      public double maxMultiplierMastered = 10.0;
      @Comment("The Time the user need to hold down to increase 1 Power level.")
      public int holdTime = 40;
      @Comment("The Time the user need to hold down to increase 1 Power level with Mastery.")
      public int holdTimeMastered = 20;
      @Comment("The Base Damage of each Aura Bullet.")
      public float baseDamage = 25.0F;
   }

   public static class DiamondPath extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 150.0;
      @Comment("How long in tick that Diamond Path will stay on user after activated.")
      public int effectTime = 1200;
      @Comment("How long in tick that Diamond Path will stay on user after activated while mastered.")
      public int effectTimeMastered = 3600;
      @Comment("How much attack damage that the user gains after activated (doubled when Mastered).")
      public double damageBoost = 10.0;
      @Comment("How much knockback resistance that the user gains after activated (doubled when Mastered).")
      public double knockBackResistanceBoost = 0.4;
   }

   public static class EarthshatterKick extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 150.0;
      @Comment("The radius of the earthquake.")
      public double radius = 5.0;
      @Comment("The Damage the affected targets get take when activated (doubled when mastered).")
      public float baseDamage = 10.0F;
   }

   public static class EightPetalsFlash extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 10000.0;
      @Comment("Speed multiplier when charging the attack.")
      public double chargingSpeed = 0.25;
      @Comment("The charge duration in tick of the attack.")
      public int chargeTick = 80;
      @Comment("The distance in block of the dash attack.")
      public double dashDistance = 20.0;
      @Comment("The damage of the dash attack.")
      public float dashDamage = 200.0F;
      @Comment("The number of petals (melee damage negation times) of the blossom.")
      public int blossomPetal = 8;
      @Comment("The duration in tick of the blossom after the dash attack.")
      public int blossomDuration = 1200;
      @Comment("The bonus charge duration in tick of the attack when mastered.")
      public int bonusChargeTick = 160;
      @Comment("The bonus damage of the attack per each bonus charged second when mastered.")
      public float bonusDamage = 50.0F;
      @Comment("The bonus Aura Cost of the attack per each bonus charged second when mastered.")
      public double bonusCost = 2500.0;
      @Comment("The number of petals to consume to reperform the dash attack when mastered.")
      public int dashPetal = 4;
   }

   public static class ElephantStampede extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 100.0;
      @Comment("The Time the user need to hold down to do each time of attack.")
      public int holdTime = 20;
      @Comment("The Damage of each aura bullet (doubled with Mastery).")
      public float baseDamage = 25.0F;
      @Comment("The Number of aura bullets each time activated.")
      public int bulletNumber = 8;
   }

   public static class FivePetalsThrust extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 8000.0;
      @Comment("Speed multiplier when charging the attack.")
      public double chargingSpeed = 0.25;
      @Comment("The charge duration in tick of the attack.")
      public int chargeTick = 80;
      @Comment("The distance in block of the dash attack.")
      public double dashDistance = 15.0;
      @Comment("The damage of the dash attack.")
      public float dashDamage = 100.0F;
      @Comment("The number of petals (melee damage negation times) of the blossom.")
      public int blossomPetal = 5;
      @Comment("The duration in tick of the blossom after the dash attack.")
      public int blossomDuration = 1200;
      @Comment("The bonus charge duration in tick of the attack when mastered.")
      public int bonusChargeTick = 160;
      @Comment("The bonus damage of the attack per each bonus charged second when mastered.")
      public float bonusDamage = 25.0F;
      @Comment("The bonus Aura Cost of the attack per each bonus charged second when mastered.")
      public double bonusCost = 1500.0;
   }

   public static class Formhide extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 15.0;
      @Comment("The Presence Concealment level when activated.")
      public int concealment = 1;
   }

   public static class Haze extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 20.0;
      @Comment("The Presence Concealment level when activated.")
      public int concealment = 2;
   }

   public static class HeavySlash extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 80.0;
      @Comment("The max distance from the target that the melee attack can be activated (doubled when mastered).")
      public double maxDistance = 5.0;
      @Comment("The Damage multiplier compared to the user's attack damage for the melee attack.")
      public double meleeDamageMultiplier = 1.5;
      @Comment("The Damage multiplier compared to the user's attack damage for the projectile attack.")
      public double projectileDamageMultiplier = 0.5;
   }

   public static class InstantMove extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 50.0;
      @Comment("How far ahead the user will instant move toward (doubled when Mastered).")
      public double distance = 6.0;
      @Comment("The bonus dodge strength when toggled.")
      public double dodgeStrength = 0.1;
      @Comment("The bonus dodge invulnerability when toggled.")
      public int dodgeInvulnerability = 1;
   }

   public static class MagicBullet extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 25.0;
      @Comment("The Max Multiplier of the Attack Power.")
      public double maxMultiplier = 10.0;
      @Comment("The Max Multiplier of the Attack Power when Mastered.")
      public double maxMultiplierMastered = 20.0;
      @Comment("The Time the user need to hold down to increase 1 Power level.")
      public int holdTime = 30;
      @Comment("The Time the user need to hold down to increase 1 Power level with Mastery.")
      public int holdTimeMastered = 20;
      @Comment("The Base Damage before Power Level calculation.")
      public float baseDamage = 10.0F;
   }

   public static class MaximumMagicBullet extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 100.0;
      @Comment("The Max Multiplier of the Attack Power.")
      public double maxMultiplier = 15.0;
      @Comment("The Max Multiplier of the Attack Power when Mastered.")
      public double maxMultiplierMastered = 30.0;
      @Comment("The Time the user need to hold down to increase 1 Power level.")
      public int holdTime = 30;
      @Comment("The Time the user need to hold down to increase 1 Power level with Mastery.")
      public int holdTimeMastered = 20;
      @Comment("The Base Damage before Power Level calculation.")
      public float baseDamage = 25.0F;
   }

   public static class OgreFlame extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 200.0;
      @Comment("The Cast Time in tick to activate.")
      public int castTime = 100;
      @Comment("The Max Time in tick for the Ogre Flame to stay after activation (doubled when Mastered).")
      public int maxTime = 100;
      @Comment("The Max Distance away from the user to spawn Ogre Flame.")
      public double maxDistance = 15.0;
      @Comment("The Ogre Flame's damage each second.")
      public float flameDamage = 50.0F;
      @Comment("The Ogre Flame's radius.")
      public float flameRadius = 4.0F;
   }

   public static class OgreSwordCannon extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 200.0;
      @Comment("The Max Multiplier of the Attack Power.")
      public double maxMultiplier = 5.0;
      @Comment("The Max Multiplier of the Attack Power when Mastered.")
      public double maxMultiplierMastered = 10.0;
      @Comment("The Time the user need to hold down to increase 1 Power level.")
      public int holdTime = 40;
      @Comment("The Time the user need to hold down to increase 1 Power level with Mastery.")
      public int holdTimeMastered = 20;
      @Comment("The Base Damage multiplier compared to the user's attack damage.")
      public double baseMultiplier = 1.5;
      @Comment("The Bonus Damage multiplier compared to the user's attack damage each Power Level.")
      public double bonusMultiplier = 0.5;
   }

   public static class OgreSwordGuillotine extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 200.0;
      @Comment("How long in tick that Ogre-Sword Guillotine will stay on user after activated.")
      public int effectTime = 300;
      @Comment("How long in tick that Ogre-Sword Guillotine will stay on user after activated while mastered.")
      public int effectTimeMastered = 600;
      @Comment("The multiplier of Attack Damage that the user gains when activated.")
      public double attackMultiplier = 1.5;
      @Comment("The multiplier of Attack Reach that the user gains when activated.")
      public double reachMultiplier = 1.5;
      @Comment("The multiplier of Attack Speed that the user gains when activated.")
      public double attackSpeedMultiplier = 0.8;
   }

   public static class RoaringLionPunch extends ManasSubConfig {
      @Comment("Base Aura Cost to activate.")
      public double auraCost = 10.0;
      @Comment("The Multiplier compared to user's Max Aura that will be used for the attack.")
      public double maxAuraMultiplier = 0.1;
      @Comment("The Maximum amount of Aura can be used for the attack.")
      public int maxAuraUsed = 2000;
      @Comment("The Maximum amount of Aura can be used for the attack when mastered.")
      public int maxAuraUsedMastered = 4000;
   }

   public static class ViolentBreak extends ManasSubConfig {
      @Comment("Aura Cost to activate.")
      public double auraCost = 150.0;
      @Comment("The Hold Time in Tick to activate.")
      public int holdTime = 60;
      @Comment("The Strengthen Time in Tick when activated.")
      public int strengthenTime = 1200;
      @Comment("The Strengthen Level when activated (doubled when Mastered).")
      public int strengthenLevel = 1;
      @Comment("The List of harmful effects that get removed upon activation.")
      public List<String> effectToRemove = Arrays.asList(
         "minecraft:bad_omen",
         "minecraft:nausea",
         "minecraft:weakness",
         "minecraft:blindness",
         "minecraft:hunger",
         "minecraft:poison",
         "minecraft:darkness",
         "minecraft:mining_fatigue",
         "minecraft:levitation",
         "minecraft:slowness",
         "minecraft:unluck",
         "minecraft:wither",
         "tensura:burden",
         "tensura:chill",
         "tensura:fragility",
         "tensura:silence",
         "tensura:corrosion",
         "tensura:fatal_poison",
         "tensura:infection",
         "tensura:paralysis",
         "tensura:mind_control"
      );
   }
}
