package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class RaceConfig extends ManasConfig {
   @Comment("The number of raids needed for Mass Naming.")
   public int massNamingRaid = 25;
   @Comment("The number of human kills needed for Mass Naming.")
   public int massNamingHuman = 1000;
   @Comment("The percentage to become Majin when dying of Magicule Poison.")
   public int majinPercentage = 1;
   @Comment("The percentage of fallen targets' EP getting added into the attacker's soul points.")
   public int epToSoulRate = 50;
   @Comment("The max aura that a spiritual-originated being gets limited at when moving to a physical world.")
   public int limitedSpiritualAura = 40000;
   @Comment("The max magicule that a spiritual-originated being gets limited at when moving to a physical world.")
   public int limitedSpiritualMagicule = 100000;
   public RaceConfig.Spirit Spirit = new RaceConfig.Spirit();
   public RaceConfig.Hero Hero = new RaceConfig.Hero();
   public RaceConfig.DemonLord DemonLord = new RaceConfig.DemonLord();

   public String getFileName() {
      return "tensura/race/race_config";
   }

   public abstract static class Default extends ManasSubConfig {
      public abstract double getMinAura();

      public abstract double getMaxAura();

      public abstract double getMinMagicule();

      public abstract double getMaxMagicule();

      public abstract double getSize();

      public abstract double getMaxHealth();

      public abstract double getMaxSpiritualHealth();

      public abstract double getAttack();

      public abstract double getAttackSpeed();

      public abstract double getKnockbackResistance();

      public abstract double getMovementSpeed();

      public abstract double getSwimSpeed();
   }

   public static class DemonLord extends ManasSubConfig {
      @Comment("The duration in tick of the Harvest Festival.")
      public int harvestFestivalTick = 3600;
      @Comment("The multiplier in EP that the entity gets when awakening as a true demon lord.")
      public float epMultiplierDemonLord = 3.0F;
      @Comment("The range in block of harvest festival boost on subordinates when their owner awakens as a true demon lord.")
      public double harvestFestivalRange = 30.0;
   }

   public static class Hero extends ManasSubConfig {
      @Comment("The level of each Spirit needed to be counted for Hero Egg.")
      public int heroSpiritLevel = 3;
      @Comment("The number of Hero Spirits needed to be a Hero Egg (Darkness & Light).")
      public int heroSpiritNumber = 1;
      @Comment("The number of Non-Hero Spirits needed to be a Hero Egg.")
      public int heroCommonSpiritNumber = 5;
      @Comment("The max HP multiplier that a boss needs to have below for the Hero Egg to hatch.")
      public float bossHPMultiplier = 0.25F;
      @Comment("The multiplier in EP that the entity gets when awakening as a true hero.")
      public float epMultiplierHero = 3.0F;
   }

   public static class Spirit extends ManasSubConfig {
      @Comment("The number of ticks that players need to pray for spirits.")
      public int prayingTime = 200;
      @Comment("The number of seconds of cooldown between each time of Spirit Praying.")
      public int prayingCooldown = 1200;
      @Comment("The percentage to be blessed on reincarnation by 6 Greater Spirits when praying.")
      public int blessedPercentage = 5;
      @Comment("The percentage to get a Lesser Spirit when praying in the labyrinth.")
      public int lesserSpiritPercentage = 40;
      @Comment("The percentage to get a Medium Spirit when praying in the labyrinth.")
      public int mediumSpiritPercentage = 30;
      @Comment("The percentage to get a Greater Spirit when praying in the labyrinth.")
      public int greaterSpiritPercentage = 20;
      @Comment("The percentage to get a Spirit Lord when praying in the labyrinth.")
      public int lordSpiritPercentage = 1;
      @Comment("The percentage to get a Medium Spirit contract when taming a Medium Spirit in the wild.")
      public int mediumSpiritTamePercentage = 15;
      @Comment("The percentage to get a Greater Spirit contract when taming a Greater Spirit in the wild.")
      public int greaterSpiritTamePercentage = 5;
   }
}
