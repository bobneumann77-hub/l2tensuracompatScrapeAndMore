package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class HarpyConfig extends ManasConfig {
   public HarpyConfig.Harpy Harpy = new HarpyConfig.Harpy();
   public HarpyConfig.HarpyQueen HarpyQueen = new HarpyConfig.HarpyQueen();
   public HarpyConfig.SpiritBird SpiritBird = new HarpyConfig.SpiritBird();
   public HarpyConfig.DivineBird DivineBird = new HarpyConfig.DivineBird();

   public String getFileName() {
      return "tensura/race/harpy_config";
   }

   public static class DivineBird extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Bird.")
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
      public double maxHealth = 980.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 7140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.8;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.09;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.9;
      @Comment("Bonus Safe Falling distance.")
      public double safeFalling = 9.0;
      @Comment("Flight Upward Boost Power.")
      public float flightBoost = 1.05F;
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
      public double getSafeFalling() {
         return this.safeFalling;
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

   public static class Harpy extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 300.0;
      @Comment("Maximum aura.")
      public double maxAura = 600.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 1500.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 2500.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 10.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 5.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;
      @Comment("Bonus Safe Falling distance.")
      public double safeFalling = 3.0;
      @Comment("Flight Upward Boost Power.")
      public float flightBoost = 0.75F;
      @Comment("Flight Boost Cooldown.")
      public int flightCooldown = 3;

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
      public double getSafeFalling() {
         return this.safeFalling;
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

   public static class HarpyQueen extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Harpy Queen.")
      public double epRequirement = 200000.0;
      @Comment("Minimal aura.")
      public double minAura = 150000.0;
      @Comment("Maximum aura.")
      public double maxAura = 150000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 150000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 150000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 120.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 290.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;
      @Comment("Bonus Safe Falling distance.")
      public double safeFalling = 5.0;
      @Comment("Flight Upward Boost Power.")
      public float flightBoost = 0.85F;
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
      public double getSafeFalling() {
         return this.safeFalling;
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

   public static class SpiritBird extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Spirit Bird.")
      public double epRequirement = 800000.0;
      @Comment("Minimal aura.")
      public double minAura = 400000.0;
      @Comment("Maximum aura.")
      public double maxAura = 400000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 400000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 400000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 580.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 4540.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;
      @Comment("Bonus Safe Falling distance.")
      public double safeFalling = 7.0;
      @Comment("Flight Upward Boost Power.")
      public float flightBoost = 0.95F;
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
      public double getSafeFalling() {
         return this.safeFalling;
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
