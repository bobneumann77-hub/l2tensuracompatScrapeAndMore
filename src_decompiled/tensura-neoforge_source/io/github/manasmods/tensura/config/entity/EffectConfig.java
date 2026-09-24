package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class EffectConfig extends ManasConfig {
   @Comment("The max level of Fear can be applied legally in game.")
   public int maxFear = 20;
   public EffectConfig.Rampage Rampage = new EffectConfig.Rampage();

   public String getFileName() {
      return "tensura/entity/effect_config";
   }

   public static class Rampage extends ManasSubConfig {
      @Comment("The duration in tick of rampage that affected entities get replenished to each time damaging or getting hurt.")
      public int replenishEach = 60;
      @Comment("The maximum duration of rampage that affected entities get replenished to when damaging another entity.")
      public int replenishDamage = 600;
      @Comment("The maximum duration of rampage that affected entities get replenished to after getting hurt by another entity.")
      public int replenishHurt = 1200;
      @Comment("The radius in block for affected mobs to find targets to attack.")
      public double mobAggroRadius = 15.0;
      @Comment("The duration in tick of the effect that affected players need to have below to start taking damage.")
      public int playerDamageDuration = 200;
      @Comment("The max health multiplier that affected players lose every second when the effect's duration is too low.")
      public float playerDamageHP = 0.1F;
      @Comment("The max spiritual health multiplier that affected players lose every second when the effect's duration is too low.")
      public float playerDamageSHP = 0.1F;
   }
}
