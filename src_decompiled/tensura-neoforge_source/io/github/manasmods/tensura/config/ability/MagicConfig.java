package io.github.manasmods.tensura.config.ability;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class MagicConfig extends ManasConfig {
   @Comment("Speed multiplier when casting magics.")
   public double castingSpeed = 0.25;
   @Comment("Reduced range when casting magics.")
   public double castingRange = -7.0;
   @Comment("The multiplier of cast time compared to normal cast when casting a unlearnt magic from a magic-holder item.")
   public float unlearntCastMultiplier = 2.0F;
   @Comment("The multiplier of energy cost compared to normal cost when casting a unlearnt magic from a magic-holder item.")
   public float unlearntCostMultiplier = 5.0F;
   @Comment("The multiplier of energy cost compared to normal cost when learning a new magic.")
   public float learningCostMultiplier = 2.0F;
   public MagicConfig.AspectualMagic AspectualMagic = new MagicConfig.AspectualMagic();
   public MagicConfig.SpiritualMagic SpiritualMagic = new MagicConfig.SpiritualMagic();

   public String getFileName() {
      return "tensura/ability/magic_config";
   }

   public static class AspectualMagic extends ManasSubConfig {
      @Comment("The max amount of mastery point for Summoning Magic.")
      public int masterySummoning = 200;
      @Comment("The max amount of mastery point for Low Aspectual Magic.")
      public int masteryLow = 100;
      @Comment("The max amount of mastery point for Medium Aspectual Magic.")
      public int masteryMedium = 300;
      @Comment("The max amount of mastery point for High Aspectual Magic.")
      public int masteryHigh = 700;
      @Comment("The max amount of mastery point for Great Aspectual Magic.")
      public int masteryGreat = 1500;
   }

   public static class SpiritualMagic extends ManasSubConfig {
      @Comment("The max amount of mastery point for Lesser Spiritual Magic.")
      public int masteryLesser = 100;
      @Comment("The max amount of mastery point for Medium Spiritual Magic.")
      public int masteryMedium = 500;
      @Comment("The max amount of mastery point for Greater Spiritual Magic.")
      public int masteryGreater = 1000;
      @Comment("The max amount of mastery point for Lord Spiritual Magic.")
      public int masteryLord = 10000;
   }
}
