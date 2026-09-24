package io.github.manasmods.tensura.config.ability.magic;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class SummoningMagicConfig extends ManasConfig {
   public SummoningMagicConfig.SummonMediumElemental SummonMediumElemental = new SummoningMagicConfig.SummonMediumElemental();
   public SummoningMagicConfig.SummonGreaterElemental SummonGreaterElemental = new SummoningMagicConfig.SummonGreaterElemental();
   public SummoningMagicConfig.SummonBasilisk SummonBasilisk = new SummoningMagicConfig.SummonBasilisk();
   public SummoningMagicConfig.SummonDaemon SummonDaemon = new SummoningMagicConfig.SummonDaemon();
   public SummoningMagicConfig.SummonHoundDog SummonHoundDog = new SummoningMagicConfig.SummonHoundDog();
   public SummoningMagicConfig.SummonOtherworlder SummonOtherworlder = new SummoningMagicConfig.SummonOtherworlder();

   public String getFileName() {
      return "tensura/ability/magic/summoning_config";
   }

   public static class SummonBasilisk extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("Magicule Cost each second to keep the Basilisk alive.")
      public double magiculeCostSecond = 100.0;
      @Comment("How long in second will the summoned Basilisk will stay.")
      public int summonDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 600;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 300;
   }

   public static class SummonDaemon extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 300;
      @Comment("The EP multiplier of the summoned daemon to be counted as Magicule Cost.")
      public double costMultiplier = 2.0;
      @Comment("The EP multiplier of the summoned daemon that the summoner needs have to be able to control the daemon when its Negotiable.")
      public double obeyMultiplier = 3.0;
      @Comment("The EP multiplier of the summoned daemon that the summoner needs have to be able to control the daemon when its Whimsical.")
      public double obeyMultiplierWhimsical = 5.0;
      @Comment("The EP multiplier of the summoned daemon that the summoner needs have to be able to control the daemon when its Non-Negotiable.")
      public double obeyMultiplierNonNegotiable = 10.0;
      @Comment("The chance for the magic to summon an Arch Daemon when using Random mode.")
      public float archChance = 0.1F;
      @Comment("The chance for the magic to summon a Greater Daemon when using Random mode.")
      public float greaterChance = 0.3F;
      @Comment("The minimal amount of EP that a daemon player needs to have to be qualified for Lesser Daemon mode.")
      public double lesserEP = 0.0;
      @Comment("The minimal amount of EP that a daemon player needs to have to be qualified for Greater Daemon mode (and maximum for Lesser Daemon mode).")
      public double greaterEP = 10000.0;
      @Comment("The minimal amount of EP that a daemon player needs to have to be qualified for Arch Daemon mode (and maximum for Greater Daemon mode).")
      public double archEP = 140000.0;
      @Comment("The maximum amount of EP that a daemon player can be summoned by the Arch Daemon mode.")
      public double archEPMax = 1.0E7;
      @Comment("The radius in block from the magic circle center that mobs can be used as sacrifices.")
      public double sacrificeRadius = 3.0;
      @Comment("The multiplier of max HP that an entity needs to have below to be qualified for Sacrificing.")
      public double sacrificeHP = 0.25;
      @Comment("The multiplier of max SHP that an entity needs to have below to be qualified for Sacrificing.")
      public double sacrificeSHP = 0.25;
      @Comment("The multiplier of the summoned daemon's EP that an entity needs to have below to be qualified for Sacrificing.")
      public double sacrificeEP = 0.25;
      @Comment("The multiplier of the total EP sacrificed will be added onto the summoned Daemon.")
      public double sacrificeEPBoost = 0.5;
      @Comment("How long in second will the summoned Daemon will stay.")
      public int summonDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 600;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 300;
   }

   public static class SummonGreaterElemental extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 3000.0;
      @Comment("Magicule Cost each second to keep the Spirit alive.")
      public double magiculeCostSecond = 300.0;
      @Comment("How long in second will the summoned Spirit will stay.")
      public int spiritDuration = 600;
      @Comment("The multiplier of Attack damage for the summoned Greater Spirit compared to the wild Boss version.")
      public double attackMultiplier = 0.5;
      @Comment("The multiplier of Health for the summoned Greater Spirit compared to the wild Boss version.")
      public double healthMultiplier = 0.5;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 600;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 300;
   }

   public static class SummonHoundDog extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 50.0;
      @Comment("Magicule Cost each second to keep the Hound Dog alive.")
      public double magiculeCostSecond = 10.0;
      @Comment("How long in second will the summoned Hound Dog will stay.")
      public int summonDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 600;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 300;
   }

   public static class SummonMediumElemental extends ManasSubConfig {
      @Comment("Cast time in tick.")
      public int castTime = 100;
      @Comment("Cast time in tick when mastered.")
      public int castTimeMastered = 60;
      @Comment("Magicule Cost to cast.")
      public double magiculeCost = 500.0;
      @Comment("Magicule Cost each second to keep the Spirit alive.")
      public double magiculeCostSecond = 50.0;
      @Comment("How long in second will the summoned Spirit will stay.")
      public int spiritDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 600;
      @Comment("The cooldown in second of the magic when mastered.")
      public int cooldownMastered = 300;
   }

   public static class SummonOtherworlder extends ManasSubConfig {
      @Comment("Cast time in tick each time the magic circle can be provided with Magicule.")
      public int castInterval = 60;
      @Comment("Cast range in block of the magic.")
      public int castRange = 8;
      @Comment("Magicule Cost total to summon an otherworlder.")
      public float magiculeCostTotal = 3000000.0F;
      @Comment("Magicule Cost the magic circle takes each cast interval.")
      public float magiculeCostInterval = 50000.0F;
      @Comment("Magicule Cost the magic circle takes each cast interval when mastered.")
      public float magiculeCostIntervalMastered = 100000.0F;
      @Comment("The chance to fail summon an otherworlder.")
      public float failChance = 0.5F;
      @Comment("The chance to fail summon an otherworlder when mastered.")
      public float failChanceMastered = 0.3F;
      @Comment("How long in second will the magic circle stay each time it is provided with Magicule.")
      public int circleDuration = 600;
      @Comment("The cooldown in second of the magic.")
      public int cooldown = 1200;
   }
}
