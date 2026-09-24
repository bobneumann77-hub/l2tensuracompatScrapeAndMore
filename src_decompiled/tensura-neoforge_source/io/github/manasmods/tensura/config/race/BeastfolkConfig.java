package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class BeastfolkConfig extends ManasConfig {
   public BeastfolkConfig.Beastfolk Beastfolk = new BeastfolkConfig.Beastfolk();
   public BeastfolkConfig.BeastLord BeastLord = new BeastfolkConfig.BeastLord();
   public BeastfolkConfig.SpiritBeast SpiritBeast = new BeastfolkConfig.SpiritBeast();
   public BeastfolkConfig.DivineBeast DivineBeast = new BeastfolkConfig.DivineBeast();

   public String getFileName() {
      return "tensura/race/beastfolk_config";
   }

   public static class BeastLord extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Beast Lord.")
      public double epRequirement = 100000.0;
      @Comment("Minimal aura.")
      public double minAura = 100000.0;
      @Comment("Maximum aura.")
      public double maxAura = 100000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 100000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 140.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 440.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.3;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.06;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.7;

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
   }

   public static class Beastfolk extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 1500.0;
      @Comment("Maximum aura.")
      public double maxAura = 2500.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 300.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 600.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 2.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 10.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.1;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;

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

   public static class DivineBeast extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Beast.")
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
      public double maxHealth = 1180.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 7940.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 7.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.9;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.12;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.2;

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
   }

   public static class SpiritBeast extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Spirit Beast.")
      public double epRequirement = 400000.0;
      @Comment("The number of Bosses defeated to evolve into Spirit Beast.")
      public int bossRequirement = 4;
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
      public double maxSpiritualHealth = 6340.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.6;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.1;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.0;

      @Generated
      public double getEpRequirement() {
         return this.epRequirement;
      }

      @Generated
      public int getBossRequirement() {
         return this.bossRequirement;
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
   }
}
