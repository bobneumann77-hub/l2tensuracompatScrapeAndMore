package io.github.manasmods.tensura.config;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;

public class EnergyConfig extends ManasConfig {
   @Comment("The Minimum amount of Aura an entity can have.")
   public double minAura = 10.0;
   @Comment("The Maximum amount of Aura an entity can have.")
   public double maxAura = 1.0E9;
   @Comment("The Base percentage of Aura an entity can gain from slain enemies' EP.")
   public double baseAuraGain = 1.0;
   @Comment("The Max percentage of Aura an entity can gain from slain enemies' EP.")
   public double maxAuraGain = 10.0;
   @Comment("The Minimum amount of Magicule an entity can have.")
   public double minMagicule = 10.0;
   @Comment("The Maximum amount of Magicule an entity can have.")
   public double maxMagicule = 1.0E9;
   @Comment("The Base percentage of Magicule an entity can gain from slain enemies' EP.")
   public double baseMagiculeGain = 1.0;
   @Comment("The Max percentage of Magicule an entity can gain from slain enemies' EP.")
   public double maxMagiculeGain = 10.0;
   @Comment("The base amount of Aura that entities regenerate each 10 ticks (half a second).")
   public double baseAuraRegen = 5.0;
   @Comment("The percentage of the current chunk's Magicule gets turned into players' MP within the chunk each 10 ticks (half a second).")
   public double areaMagiculeRegen = 0.01;
   @Comment("The minimum amount of Magicule the current chunk needs to have to apply Magicule Poison on players if applicable.")
   public double minimumMagiculePoison = 1000.0;
   @Comment("The number of seconds will the entity be in sleep mode when Magicule reaches 0.")
   public int sleepModeTick = 180;
   @Comment("The bonus multiplier of Max Aura that the entity regenerates each 10 ticks during Sleep Mode.")
   public double sleepModeAura = 0.003;
   @Comment("The bonus multiplier of Max Magicule that the entity regenerates each 10 ticks during Sleep Mode.")
   public double sleepModeMagicule = 0.003;
   @Comment("The multiplier of Max Aura that the entity regenerates after waking up naturally.")
   public double wakeUpAura = 0.16;
   @Comment("The multiplier of Max Magicule that the entity regenerates after waking up naturally.")
   public double wakeUpMagicule = 0.16;
   @Comment("Amount of Magicule that an entity in Spiritual Form loses each 10 ticks (half a second) while in unsuitable area.")
   public double spiritualMagiculeLost = 115.0;
   @Comment("Amount of Aura/Magicule that an entity loses each 10 ticks when exceeding the max Aura/Magicule.")
   public double exceedMaxLost = 5.0;
   @Comment(
      "Multiplier of max aura an entity needs to exceed for each level of Insanity.\nExample: By default, Aura = 125% Max Aura -> Insanity I, Aura = 150% Max Aura -> Insanity II"
   )
   public double auraMultiplierForInsanity = 0.25;
   @Comment(
      "Multiplier of max magicule an entity needs to exceed for each level of Magicule Poison.\nExample: By default, Magicule = 125% Max Magicule -> Poison I, Magicule = 150% Max Magicule -> Poison II"
   )
   public double magiculeMultiplierForPoison = 0.25;
   @Comment("The maximum amount of EP that an EP stealing ability can take.")
   public double maximumEPSteal = 1000000.0;
   @Comment("The maximum percentage of EP reduction when used in calculation for EP gain after killing mobs.")
   public double maxEPReductionPercentage = 90.0;
   @Comment("Whether the EP Gain from killing Players limit by the EP death Penalty gamerule.")
   public boolean penaltyLimitGain = true;

   public String getFileName() {
      return "tensura/energy_config";
   }
}
