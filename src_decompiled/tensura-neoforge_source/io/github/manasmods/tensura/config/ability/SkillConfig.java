package io.github.manasmods.tensura.config.ability;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class SkillConfig extends ManasConfig {
   @Comment("The base Magicule Acquirement Cost for Resistance Skills.")
   public double mpAcquirementResistance = 100.0;
   @Comment("The base Magicule Acquirement Cost for Nullification Skills.")
   public double mpAcquirementNullification = 1000.0;
   @Comment("The base Magicule Acquirement Cost for Intrinsic Skills.")
   public double mpAcquirementIntrinsic = 100.0;
   @Comment("The base Magicule Acquirement Cost for Common Skills.")
   public double mpAcquirementCommon = 100.0;
   @Comment("The base Magicule Acquirement Cost for Extra Skills.")
   public double mpAcquirementExtra = 1000.0;
   public SkillConfig.Mastery Mastery = new SkillConfig.Mastery();

   public String getFileName() {
      return "tensura/ability/skill_config";
   }

   public static class Mastery extends ManasSubConfig {
      @Comment("The max amount of mastery point for Intrinsic Skills.")
      public int masteryIntrinsic = 100;
      @Comment("The max amount of mastery point for Extra Skills.")
      public int masteryExtra = 500;
      @Comment("The max amount of mastery point for Unique Skills.")
      public int masteryUnique = 1000;
      @Comment("The max amount of mastery point for Unique Skills of Sins.")
      public int masteryUniqueSin = 1500;
      @Comment("The max amount of mastery point for Ultimate Skills.")
      public int masteryUltimate = 10000;
   }
}
