package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class LizardmanConfig extends ManasConfig {
   public LizardmanConfig.Lizardman Lizardman = new LizardmanConfig.Lizardman();
   public LizardmanConfig.Dragonewt Dragonewt = new LizardmanConfig.Dragonewt();
   public LizardmanConfig.TrueDragonewt TrueDragonewt = new LizardmanConfig.TrueDragonewt();
   public LizardmanConfig.DivineDragon DivineDragon = new LizardmanConfig.DivineDragon();

   public String getFileName() {
      return "tensura/race/lizardman_config";
   }

   public static class DivineDragon extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Dragon.")
      public double epRequirement = 2000000.0;
      @Comment("Minimal aura.")
      public double minAura = 1000000.0;
      @Comment("Maximum aura.")
      public double maxAura = 1000000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 1000000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 1000000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 960.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6100.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 5.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.5;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.1;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.0;
      @Comment("Flight Boost Power.")
      public float flightBoost = 0.5F;
      @Comment("Flight Boost Cooldown.")
      public int flightCooldown = 3;

      @Generated
      public double getEpRequirement() {
         return this.epRequirement;
      }

      @Generated
      @Override
      public double getMinAura() {
         return this.minAura;
      }

      @Generated
      @Override
      public double getMaxAura() {
         return this.maxAura;
      }

      @Generated
      @Override
      public double getMinMagicule() {
         return this.minMagicule;
      }

      @Generated
      @Override
      public double getMaxMagicule() {
         return this.maxMagicule;
      }

      @Generated
      @Override
      public double getSize() {
         return this.size;
      }

      @Generated
      @Override
      public double getMaxHealth() {
         return this.maxHealth;
      }

      @Generated
      @Override
      public double getMaxSpiritualHealth() {
         return this.maxSpiritualHealth;
      }

      @Generated
      @Override
      public double getAttack() {
         return this.attack;
      }

      @Generated
      @Override
      public double getAttackSpeed() {
         return this.attackSpeed;
      }

      @Generated
      @Override
      public double getKnockbackResistance() {
         return this.knockbackResistance;
      }

      @Generated
      @Override
      public double getMovementSpeed() {
         return this.movementSpeed;
      }

      @Generated
      @Override
      public double getSwimSpeed() {
         return this.swimSpeed;
      }

      @Generated
      public float getFlightBoost() {
         return this.flightBoost;
      }

      @Generated
      public int getFlightCooldown() {
         return this.flightCooldown;
      }
   }

   public static class Dragonewt extends RaceConfig.Default {
      @Comment("The number of Dragon Essence consumed to evolve into Dragonewt.")
      public int essenceRequirement = 10;
      @Comment("Minimal aura.")
      public double minAura = 6000.0;
      @Comment("Maximum aura.")
      public double maxAura = 6000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 4000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 4000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 34.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 150.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.3;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;
      @Comment("Flight Boost Power.")
      public float flightBoost = 0.1F;
      @Comment("Flight Boost Cooldown.")
      public int flightCooldown = 3;

      @Generated
      public int getEssenceRequirement() {
         return this.essenceRequirement;
      }

      @Generated
      @Override
      public double getMinAura() {
         return this.minAura;
      }

      @Generated
      @Override
      public double getMaxAura() {
         return this.maxAura;
      }

      @Generated
      @Override
      public double getMinMagicule() {
         return this.minMagicule;
      }

      @Generated
      @Override
      public double getMaxMagicule() {
         return this.maxMagicule;
      }

      @Generated
      @Override
      public double getSize() {
         return this.size;
      }

      @Generated
      @Override
      public double getMaxHealth() {
         return this.maxHealth;
      }

      @Generated
      @Override
      public double getMaxSpiritualHealth() {
         return this.maxSpiritualHealth;
      }

      @Generated
      @Override
      public double getAttack() {
         return this.attack;
      }

      @Generated
      @Override
      public double getAttackSpeed() {
         return this.attackSpeed;
      }

      @Generated
      @Override
      public double getKnockbackResistance() {
         return this.knockbackResistance;
      }

      @Generated
      @Override
      public double getMovementSpeed() {
         return this.movementSpeed;
      }

      @Generated
      @Override
      public double getSwimSpeed() {
         return this.swimSpeed;
      }

      @Generated
      public float getFlightBoost() {
         return this.flightBoost;
      }

      @Generated
      public int getFlightCooldown() {
         return this.flightCooldown;
      }
   }

   public static class Lizardman extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 600.0;
      @Comment("Maximum aura.")
      public double maxAura = 800.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 100.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 200.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 4.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 8.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.1;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.1;

      @Generated
      @Override
      public double getMinAura() {
         return this.minAura;
      }

      @Generated
      @Override
      public double getMaxAura() {
         return this.maxAura;
      }

      @Generated
      @Override
      public double getMinMagicule() {
         return this.minMagicule;
      }

      @Generated
      @Override
      public double getMaxMagicule() {
         return this.maxMagicule;
      }

      @Generated
      @Override
      public double getSize() {
         return this.size;
      }

      @Generated
      @Override
      public double getMaxHealth() {
         return this.maxHealth;
      }

      @Generated
      @Override
      public double getMaxSpiritualHealth() {
         return this.maxSpiritualHealth;
      }

      @Generated
      @Override
      public double getAttack() {
         return this.attack;
      }

      @Generated
      @Override
      public double getAttackSpeed() {
         return this.attackSpeed;
      }

      @Generated
      @Override
      public double getKnockbackResistance() {
         return this.knockbackResistance;
      }

      @Generated
      @Override
      public double getMovementSpeed() {
         return this.movementSpeed;
      }

      @Generated
      @Override
      public double getSwimSpeed() {
         return this.swimSpeed;
      }
   }

   public static class TrueDragonewt extends RaceConfig.Default {
      @Comment("EP requirement to evolve into True Dragonewt.")
      public double epRequirement = 800000.0;
      @Comment("Minimal aura.")
      public double minAura = 600000.0;
      @Comment("Maximum aura.")
      public double maxAura = 600000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 200000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 200000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 460.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3240.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;
      @Comment("Flight Boost Power.")
      public float flightBoost = 0.25F;
      @Comment("Flight Boost Cooldown.")
      public int flightCooldown = 3;

      @Generated
      public double getEpRequirement() {
         return this.epRequirement;
      }

      @Generated
      @Override
      public double getMinAura() {
         return this.minAura;
      }

      @Generated
      @Override
      public double getMaxAura() {
         return this.maxAura;
      }

      @Generated
      @Override
      public double getMinMagicule() {
         return this.minMagicule;
      }

      @Generated
      @Override
      public double getMaxMagicule() {
         return this.maxMagicule;
      }

      @Generated
      @Override
      public double getSize() {
         return this.size;
      }

      @Generated
      @Override
      public double getMaxHealth() {
         return this.maxHealth;
      }

      @Generated
      @Override
      public double getMaxSpiritualHealth() {
         return this.maxSpiritualHealth;
      }

      @Generated
      @Override
      public double getAttack() {
         return this.attack;
      }

      @Generated
      @Override
      public double getAttackSpeed() {
         return this.attackSpeed;
      }

      @Generated
      @Override
      public double getKnockbackResistance() {
         return this.knockbackResistance;
      }

      @Generated
      @Override
      public double getMovementSpeed() {
         return this.movementSpeed;
      }

      @Generated
      @Override
      public double getSwimSpeed() {
         return this.swimSpeed;
      }

      @Generated
      public float getFlightBoost() {
         return this.flightBoost;
      }

      @Generated
      public int getFlightCooldown() {
         return this.flightCooldown;
      }
   }
}
