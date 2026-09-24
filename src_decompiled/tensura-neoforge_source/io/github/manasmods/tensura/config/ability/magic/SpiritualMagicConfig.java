package io.github.manasmods.tensura.config.ability.magic;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class SpiritualMagicConfig extends ManasConfig {
   public SpiritualMagicConfig.Darkness Darkness = new SpiritualMagicConfig.Darkness();
   public SpiritualMagicConfig.DarkCube DarkCube = new SpiritualMagicConfig.DarkCube();
   public SpiritualMagicConfig.ShadowBind ShadowBind = new SpiritualMagicConfig.ShadowBind();
   public SpiritualMagicConfig.DarknessCannon DarknessCannon = new SpiritualMagicConfig.DarknessCannon();
   public SpiritualMagicConfig.TrueDarkness TrueDarkness = new SpiritualMagicConfig.TrueDarkness();
   public SpiritualMagicConfig.Earth Earth = new SpiritualMagicConfig.Earth();
   public SpiritualMagicConfig.EarthSpikes EarthSpikes = new SpiritualMagicConfig.EarthSpikes();
   public SpiritualMagicConfig.EarthStorm EarthStorm = new SpiritualMagicConfig.EarthStorm();
   public SpiritualMagicConfig.MagmaSurge MagmaSurge = new SpiritualMagicConfig.MagmaSurge();
   public SpiritualMagicConfig.EarthJail EarthJail = new SpiritualMagicConfig.EarthJail();
   public SpiritualMagicConfig.Fire Fire = new SpiritualMagicConfig.Fire();
   public SpiritualMagicConfig.FireBolt FireBolt = new SpiritualMagicConfig.FireBolt();
   public SpiritualMagicConfig.FireBreath FireBreath = new SpiritualMagicConfig.FireBreath();
   public SpiritualMagicConfig.FlareCircle FlareCircle = new SpiritualMagicConfig.FlareCircle();
   public SpiritualMagicConfig.Hellfire Hellfire = new SpiritualMagicConfig.Hellfire();
   public SpiritualMagicConfig.Light Light = new SpiritualMagicConfig.Light();
   public SpiritualMagicConfig.SolarBeam SolarBeam = new SpiritualMagicConfig.SolarBeam();
   public SpiritualMagicConfig.SolarWave SolarWave = new SpiritualMagicConfig.SolarWave();
   public SpiritualMagicConfig.SolarRain SolarRain = new SpiritualMagicConfig.SolarRain();
   public SpiritualMagicConfig.SolarFlare SolarFlare = new SpiritualMagicConfig.SolarFlare();
   public SpiritualMagicConfig.Space Space = new SpiritualMagicConfig.Space();
   public SpiritualMagicConfig.Gate Gate = new SpiritualMagicConfig.Gate();
   public SpiritualMagicConfig.Shrink Shrink = new SpiritualMagicConfig.Shrink();
   public SpiritualMagicConfig.Teleport Teleport = new SpiritualMagicConfig.Teleport();
   public SpiritualMagicConfig.Swipe Swipe = new SpiritualMagicConfig.Swipe();
   public SpiritualMagicConfig.Water Water = new SpiritualMagicConfig.Water();
   public SpiritualMagicConfig.WaterCutter WaterCutter = new SpiritualMagicConfig.WaterCutter();
   public SpiritualMagicConfig.AcidRain AcidRain = new SpiritualMagicConfig.AcidRain();
   public SpiritualMagicConfig.Blizzard Blizzard = new SpiritualMagicConfig.Blizzard();
   public SpiritualMagicConfig.Megiddo Megiddo = new SpiritualMagicConfig.Megiddo();
   public SpiritualMagicConfig.Wind Wind = new SpiritualMagicConfig.Wind();
   public SpiritualMagicConfig.WindBlade WindBlade = new SpiritualMagicConfig.WindBlade();
   public SpiritualMagicConfig.LightningLance LightningLance = new SpiritualMagicConfig.LightningLance();
   public SpiritualMagicConfig.ElectroBlast ElectroBlast = new SpiritualMagicConfig.ElectroBlast();
   public SpiritualMagicConfig.AerialBlade AerialBlade = new SpiritualMagicConfig.AerialBlade();
   public SpiritualMagicConfig.CreateLesserUndead CreateLesserUndead = new SpiritualMagicConfig.CreateLesserUndead();
   public SpiritualMagicConfig.CreateGreaterUndead CreateGreaterUndead = new SpiritualMagicConfig.CreateGreaterUndead();
   public SpiritualMagicConfig.Curse Curse = new SpiritualMagicConfig.Curse();
   public SpiritualMagicConfig.CurseBind CurseBind = new SpiritualMagicConfig.CurseBind();

   public String getFileName() {
      return "tensura/ability/magic/spiritual_config";
   }

   public static class AcidRain extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 80;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 1000.0;
      @Comment("The range in block of the magic.")
      public double range = 20.0;
      @Comment("The damage of the acid rain.")
      public float rainDamage = 40.0F;
      @Comment("The radius in block of the acid rain.")
      public float rainRadius = 7.5F;
      @Comment("The radius in block of the acid rain when mastered.")
      public float rainRadiusMastered = 10.0F;
      @Comment("The max duration in tick that the magic can be used.")
      public int rainDuration = 400;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 3;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 1;
   }

   public static class AerialBlade extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 100;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 35000.0;
      @Comment("The range in block of the magic.")
      public float range = 3.0F;
      @Comment("The range in block of the magic when mastered.")
      public float rangeMastered = 4.0F;
      @Comment("The damage of the magic.")
      public float damage = 200.0F;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 10;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 5;
   }

   public static class Blizzard extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 160;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 160;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50000.0;
      @Comment("The damage per second of the blizzard.")
      public float blizzardDamage = 30.0F;
      @Comment("The radius in block of the blizzard.")
      public float blizzardRadius = 15.0F;
      @Comment("The duration of the Chill effect every 5 seconds.")
      public int chillDuration = 120;
      @Comment("The duration in tick of the blizzard.")
      public int blizzardDuration = 2000;
      @Comment("The duration in tick of the blizzard when mastered.")
      public int blizzardDurationMastered = 3200;
      @Comment("The damage of the icicle.")
      public float icicleDamage = 250.0F;
      @Comment("The size of the icicle.")
      public float icicleSize = 2.0F;
      @Comment("The cooldown in second of the icicle.")
      public int icicleCooldown = 10;
   }

   public static class CreateGreaterUndead extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Magicule Cost to create each undead.")
      public double magiculeCost = 8000.0;
      @Comment("How long in second will the summoned undead will stay.")
      public int undeadDuration = 300;
      @Comment("The number of undead getting summoned by the magic.")
      public int undeadNumber = 1;
      @Comment("The number of undead getting summoned by the magic when sneaking.")
      public int undeadNumberSneak = 3;
      @Comment("The number of undead getting summoned by the magic when sneaking with Mastery.")
      public int undeadNumberSneakMastered = 6;
      @Comment("The HP that each undead created has.")
      public int undeadHP = 100;
      @Comment("The attack damage that each undead created has.")
      public int undeadAttack = 5;
      @Comment("The EP boost that each undead created has.")
      public int undeadEpBoost = 5000;
      @Comment("The Sharpness level of created Zombie's sword.")
      public int undeadSharpness = 1;
      @Comment("The Power level of created Skeleton's bow.")
      public int undeadPower = 2;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 10;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 5;
   }

   public static class CreateLesserUndead extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 80;
      @Comment("Magicule Cost to create each undead.")
      public double magiculeCost = 800.0;
      @Comment("How long in second will the summoned undead will stay.")
      public int undeadDuration = 300;
      @Comment("The number of undead getting summoned by the magic.")
      public int undeadNumber = 1;
      @Comment("The number of undead getting summoned by the magic when sneaking with mastery.")
      public int undeadNumberMastered = 3;
      @Comment("The HP that each undead created has.")
      public int undeadHP = 20;
      @Comment("The attack damage that each undead created has.")
      public int undeadAttack = 5;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 10;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 5;
   }

   public static class Curse extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 10000.0;
      @Comment("The radius in block of the miasmic mist.")
      public int mistRadius = 5;
      @Comment("The damage interval in tick of the miasmic mist.")
      public int mistInterval = 40;
      @Comment("The amount of magic damage each interval of the miasmic mist.")
      public int mistDamage = 40;
      @Comment("The amount of magic damage each interval of the miasmic mist when mastered.")
      public int mistDamageMastered = 60;
      @Comment("The level of the curse effect.")
      public int curseLevel = 1;
      @Comment("The level of the curse effect when mastered.")
      public int curseLevelMastered = 2;
      @Comment("The duration in tick of the curse effect.")
      public int curseDuration = 600;
      @Comment("The duration in tick of the miasmic mist.")
      public int mistDuration = 400;
   }

   public static class CurseBind extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 100;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 80000.0;
      @Comment("The range in block of the magic.")
      public double range = 20.0;
      @Comment("The level of the Paralysis effect to apply on trapped targets.")
      public int paralysisLevel = 3;
      @Comment("The damage interval in tick of the Curse Bind.")
      public int damageInterval = 40;
      @Comment("The amount of Magic damage dealt on trapped targets.")
      public float bindDamage = 100.0F;
      @Comment("The amount of Magic damage dealt on trapped targets.")
      public float bindDamageMastered = 150.0F;
      @Comment("The curse interval in tick of the Curse Bind.")
      public int curseInterval = 100;
      @Comment("The level of the Curse effect to add/increase on trapped targets every curse interval.")
      public int curseLevel = 1;
      @Comment("The duration in tick of the Curse effect to add/increase on trapped targets every curse interval.")
      public int curseDuration = 600;
      @Comment("The level of the Corrosion effect to add/increase on trapped targets every curse interval.")
      public int corrosionLevel = 1;
      @Comment("The duration of the Curse Bind.")
      public int bindDuration = 400;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 3;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 1;
   }

   public static class DarkCube extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 60;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 2000.0;
      @Comment("The range in block of the magic.")
      public double range = 15.0;
      @Comment("The range in block of the magic when mastered.")
      public double rangeMastered = 20.0;
      @Comment("The radius in block of the cube.")
      public float cubeRadius = 5.0F;
      @Comment("The damage each 10 ticks of the cube.")
      public float cubeDamage = 10.0F;
      @Comment("The damage each 10 ticks of the cube when mastered.")
      public float cubeDamageMastered = 20.0F;
      @Comment("The level of Movement Interference effect (-10% speed each) when applied by the cube.")
      public int cubeSpeed = 5;
      @Comment("The duration in tick of the cube when casted.")
      public int cubeDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 4;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 2;
   }

   public static class Darkness extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 40;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 100.0;
      @Comment("The radius in block of the magic.")
      public double radius = 7.5;
      @Comment("The radius in block of the magic when mastered.")
      public double radiusMastered = 15.0;
      @Comment("The level of the Darkness effect when casted.")
      public int darknessLevel = 5;
      @Comment("The duration of the Darkness effect when casted.")
      public int darknessDuration = 1800;
      @Comment("The duration of the Darkness effect when casted with mastery.")
      public int darknessDurationMastered = 3600;
   }

   public static class DarknessCannon extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 80;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 40;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 35000.0;
      @Comment("The range in block of the magic.")
      public float range = 20.0F;
      @Comment("The damage of the magic.")
      public float damage = 250.0F;
      @Comment("The level of Wither effect when attacked by the magic.")
      public int witherLevel = 2;
      @Comment("The duration in tick of Wither effect when attacked by the magic.")
      public int witherDuration = 600;
      @Comment("The level of Hunger effect when attacked by the magic.")
      public int hungerLevel = 2;
      @Comment("The duration in tick of Hunger effect when attacked by the magic.")
      public int hungerDuration = 600;
      @Comment("The duration in tick of Insanity effect when attacked by the magic.")
      public int insanityDuration = 200;
      @Comment("The additional amount of durability break for the targets' armors.")
      public int durabilityBreak = 1000;
   }

   public static class Earth extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 10;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50.0;
      @Comment("Magicule Cost to cast when mastered.")
      public double magiculeCostMastered = 20.0;
   }

   public static class EarthJail extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 140;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 140;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 30000.0;
      @Comment("The range in block of the magic.")
      public double range = 20.0;
      @Comment("The range in block of the magic when mastered.")
      public double rangeMastered = 30.0;
      @Comment("The damage of the magic.")
      public float jailDamage = 50.0F;
      @Comment("The level of Movement Interference effect (-10% speed each) when applied by the magic.")
      public int jailSpeed = 6;
      @Comment("The decreased level of Movement Interference effect if the target has Earth Attack Resistance.")
      public int jailSpeedResisted = 4;
      @Comment("The level of the Mining Fatigue effect when applied by the magic.")
      public int jailFatigue = 2;
      @Comment("The level of the Burden effect when applied by the magic.")
      public int jailBurden = 2;
      @Comment("The level of the Fragility effect when applied by the magic.")
      public int jailFragility = 2;
      @Comment("The duration in tick of each effect when applied by the magic.")
      public int jailDuration = 1200;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 20;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 10;
   }

   public static class EarthSpikes extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 40;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("The range in block of the magic.")
      public double range = 20.0;
      @Comment("The damage of each spike.")
      public float spikeDamage = 30.0F;
      @Comment("The height in block of each spike.")
      public float spikeHeight = 3.0F;
   }

   public static class EarthStorm extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 120;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 5000.0;
      @Comment("The radius in block of the magic.")
      public float radius = 5.0F;
      @Comment("The damage of the magic.")
      public float stormDamage = 20.0F;
      @Comment("The chance for targets to be affected by Levitation.")
      public float levitationChance = 0.3F;
      @Comment("The level of the Levitation effect.")
      public int levitationLevel = 1;
      @Comment("The duration in tick of the Levitation effect.")
      public int levitationDuration = 40;
      @Comment("The max duration in tick that the magic can be used.")
      public int stormDuration = 300;
      @Comment("The max duration in tick that the magic can be used with mastery.")
      public int stormDurationMastered = 600;
   }

   public static class ElectroBlast extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 140;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 100;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 25000.0;
      @Comment("The range in block of the magic.")
      public float range = 20.0F;
      @Comment("The damage of the magic.")
      public float damage = 125.0F;
      @Comment("The explosion level of the magic.")
      public float explosion = 2.0F;
      @Comment("The level of Paralysis when applied by the magic.")
      public int paralysisLevel = 2;
      @Comment("The duration in tick of Paralysis when applied by the magic.")
      public int paralysisDuration = 600;
   }

   public static class Fire extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 1;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 10.0;
      @Comment("The range in block of the magic.")
      public double range = 6.0;
   }

   public static class FireBolt extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 20;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 1000.0;
      @Comment("The damage of the fire bolt.")
      public float damage = 40.0F;
      @Comment("The burning duration in tick of targets hit by the fire bolt.")
      public int burnTick = 60;
   }

   public static class FireBreath extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 10;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 200.0;
      @Comment("The damage of the fire breath.")
      public float damage = 15.0F;
      @Comment("The damage of the fire breath when mastered.")
      public float damageMastered = 30.0F;
   }

   public static class FlareCircle extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 60;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 10000.0;
      @Comment("The range in block of the magic.")
      public double range = 20.0;
      @Comment("The damage of the flare circle.")
      public float flareDamage = 80.0F;
      @Comment("The radius in block of the flare circle.")
      public float flareRadius = 5.0F;
      @Comment("The height in block of the flare circle.")
      public float flareHeight = 7.0F;
      @Comment("The max duration in tick that the magic can be used.")
      public int flareDuration = 100;
      @Comment("The max duration in tick that the magic can be used with mastery.")
      public int flareDurationMastered = 200;
   }

   public static class Gate extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 300;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 300;
      @Comment("Magicule Cost per block to warp.")
      public double magiculeCost = 50.0;
      @Comment("The charge tick of the portal before warping any entity.")
      public int warpChargeTick = 100;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 20;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 10;
   }

   public static class Hellfire extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 100;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 45000.0;
      @Comment("The range in block of the magic.")
      public double range = 15.0;
      @Comment("The range in block of the magic when mastered.")
      public double rangeMastered = 20.0;
      @Comment("The damage of the Hellfire.")
      public float sphereDamage = 200.0F;
      @Comment("The damage of the Hellfire.")
      public float sphereDamageMastered = 300.0F;
      @Comment("The radius of the Hellfire.")
      public float sphereRadius = 2.5F;
      @Comment("The radius of the Hellfire when mastered.")
      public float sphereRadiusMastered = 5.0F;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 5;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 3;
   }

   public static class Light extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 10;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50.0;
      @Comment("The range in block of the magic.")
      public double range = 6.0;
   }

   public static class LightningLance extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 40;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 1000.0;
      @Comment("The damage of the lightning lance.")
      public float damage = 30.0F;
      @Comment("The damage of the lightning lance when mastered.")
      public float damageMastered = 40.0F;
   }

   public static class MagmaSurge extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 80;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 80;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 40000.0;
      @Comment("The damage of each magma projectile.")
      public float magmaDamage = 200.0F;
      @Comment("The damage of each magma projectile when mastered.")
      public float magmaDamageMastered = 300.0F;
      @Comment("The level of the Burden effect when mastered.")
      public int burdenLevel = 2;
      @Comment("The duration in tick of the Burden effect when mastered.")
      public int burdenDuration = 200;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 10;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 5;
   }

   public static class Megiddo extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 140;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 140;
      @Comment("Magicule Cost to cast the Single Target mode.")
      public double magiculeCostSingle = 30000.0;
      @Comment("Magicule Cost to cast the Auto Target mode.")
      public double magiculeCostAuto = 50000.0;
      @Comment("The damage of each beam.")
      public float beamDamage = 150.0F;
      @Comment("The duration in tick of the Single Target mode.")
      public int singleDuration = 6000;
      @Comment("The number of attack beam of the Single Target mode.")
      public int singleBeam = 10;
      @Comment("The number of attack beam of the Single Target mode when mastered.")
      public int singleBeamMastered = 20;
      @Comment("The range in block of the Single Target mode.")
      public double singleRange = 60.0;
      @Comment("The cooldown in second for each beam of the Single Target mode.")
      public int singleCooldown = 1;
      @Comment("The duration in tick of the Single Target mode.")
      public int autoDuration = 600;
      @Comment("The number of attack beam of the Single Target mode.")
      public int autoBeam = 5;
      @Comment("The number of attack beam of the Single Target mode when mastered.")
      public int autoBeamMastered = 10;
      @Comment("The range in block of the Single Target mode.")
      public float autoRange = 40.0F;
      @Comment("How often in tick that the Auto Target mode releases more beams.")
      public int autoBeamTime = 200;
   }

   public static class ShadowBind extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 40;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 1000.0;
      @Comment("The range in block of the magic.")
      public double range = 10.0;
      @Comment("The level of the Movement Interference effect when the hands catch a target (-10% speed each level).")
      public int bindLevel = 10;
      @Comment("The level of the Movement Interference effect when the hands catch a target with Darkness Attack Resistance.")
      public int bindLevelResisted = 2;
      @Comment("The amount of Spiritual damage dealt on targets when applied with the Shadow Bind effect.")
      public float bindDamage = 10.0F;
      @Comment("The bonus multiplier of the target's Spiritual damage dealt when mastered.")
      public float bindDamageMastered = 0.01F;
      @Comment("The duration of the Shadow Bind effect.")
      public int bindDuration = 200;
   }

   public static class Shrink extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 60;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 300.0;
      @Comment("The duration in second of the Shrink effect.")
      public double shrinkDuration = 60.0;
      @Comment("The duration in second of the Shrink effect when mastered.")
      public double shrinkDurationMastered = 120.0;
      @Comment("The size multiplier when casted.")
      public float shrinkSize = 0.2F;
      @Comment("The level of the Fragility effect when shrunk.")
      public int fragilityLevel = 5;
      @Comment("The level of the Fragility effect when shrunk with mastery.")
      public int fragilityLevelMastered = 3;
   }

   public static class SolarBeam extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("The range in block of the magic.")
      public float range = 30.0F;
      @Comment("The damage of the magic.")
      public float damage = 20.0F;
   }

   public static class SolarFlare extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 120;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 35000.0;
      @Comment("The radius in block of the magic.")
      public double radius = 15.0;
      @Comment("The damage of the flare.")
      public float flareDamage = 200.0F;
      @Comment("The level of the Flashed Blindness effect when applied by the magic.")
      public int flareBlindness = 2;
      @Comment("The level of the Nausea effect when applied by the magic.")
      public int flareNausea = 1;
      @Comment("The level of the Slowness effect when applied by the magic.")
      public int flareSlowness = 1;
      @Comment("The duration in tick of each effect when applied by the magic.")
      public int flareDuration = 300;
      @Comment("The duration in tick of each effect when applied by the magic when mastered.")
      public int flareDurationMastered = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 20;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 10;
   }

   public static class SolarRain extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 80;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 80;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 8000.0;
      @Comment("The range in block of the magic.")
      public float range = 20.0F;
      @Comment("The range in block of the magic when mastered.")
      public float rangeMastered = 30.0F;
      @Comment("The number of arrows when casted.")
      public int arrowNumber = 10;
      @Comment("The number of arrows when casted with mastery.")
      public int arrowNumberMastered = 20;
      @Comment("The damage of each arrow when casted.")
      public float arrowDamage = 30.0F;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 5;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 3;
   }

   public static class SolarWave extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 60;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 1000.0;
      @Comment("The damage of the projectile.")
      public float waveDamage = 30.0F;
      @Comment("The radius in block of the wave.")
      public float waveRadius = 4.0F;
      @Comment("The duration in tick of the Blindness effect when affected by the wave.")
      public int blindnessDuration = 300;
      @Comment("The level of the Blindness effect when affected by the wave.")
      public int blindnessLevel = 1;
      @Comment("The level of the Blindness effect when affected by the wave with mastery.")
      public int blindnessLevelMastered = 2;
   }

   public static class Space extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 20;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50.0;
      @Comment("The range in block of the magic.")
      public double range = 4.0;
      @Comment("The duration in tick of the solid space block before removing itself.")
      public int duration = 1200;
   }

   public static class Swipe extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 60;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50000.0;
      @Comment("The range in block of the magic.")
      public double range = 15.0;
      @Comment("The range in block of the magic when mastered.")
      public double rangeMastered = 20.0;
      @Comment("The damage of the magic.")
      public float damage = 200.0F;
      @Comment("The damage of the magic when mastered.")
      public float damageMastered = 300.0F;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 5;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 3;
   }

   public static class Teleport extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 5;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 5;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 150.0;
      @Comment("Additional Magicule Cost per block to teleport.")
      public double magiculeCostBlock = 10.0;
      @Comment("The range in block of the magic.")
      public double range = 30.0;
      @Comment("The range in block of the magic when mastered.")
      public double rangeMastered = 50.0;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 20;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 10;
   }

   public static class TrueDarkness extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 120;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 120;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 30000.0;
      @Comment("The radius in block of the magic.")
      public float radius = 7.5F;
      @Comment("The spiritual damage each second of the magic.")
      public float damage = 100.0F;
      @Comment("The level of Darkness effect when attacked by the magic.")
      public int darknessLevel = 10;
      @Comment("The duration in tick of Insanity effect when attacked by the magic.")
      public int insanityDuration = 200;
      @Comment("The max duration in tick that the magic can be used.")
      public int trueDuration = 200;
      @Comment("The max duration in tick that the magic can be used with mastery.")
      public int trueDurationMastered = 400;
   }

   public static class Water extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 20;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50.0;
      @Comment("The range in block of the magic.")
      public double range = 4.0;
   }

   public static class WaterCutter extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 30;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 10;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("The damage of the water cutter.")
      public float damage = 30.0F;
   }

   public static class Wind extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 20;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 1;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 25.0;
      @Comment("The power of the wind charge blast.")
      public float power = 1.5F;
   }

   public static class WindBlade extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 40;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 20;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("The damage of the wind blade.")
      public float damage = 20.0F;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 2;
   }
}
