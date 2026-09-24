package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class MerfolkConfig extends ManasConfig {
   public MerfolkConfig.Merfolk Merfolk = new MerfolkConfig.Merfolk();
   public MerfolkConfig.EnlightenedMerfolk EnlightenedMerfolk = new MerfolkConfig.EnlightenedMerfolk();
   public MerfolkConfig.MerfolkSaint MerfolkSaint = new MerfolkConfig.MerfolkSaint();
   public MerfolkConfig.DivineFish DivineFish = new MerfolkConfig.DivineFish();

   public String getFileName() {
      return "tensura/race/merfolk_config";
   }

   public static class DivineFish extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Fish.")
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
      public double maxHealth = 1040.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.1;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.5;
      @Comment("Bonus Submerged Mining Speed.")
      public double submergedMiningSpeed = 1.0;

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
      public double getSubmergedMiningSpeed() {
         return this.submergedMiningSpeed;
      }
   }

   public static class EnlightenedMerfolk extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Enlightened Merfolk.")
      public double epRequirement = 100000.0;
      @Comment("Minimal aura.")
      public double minAura = 140000.0;
      @Comment("Maximum aura.")
      public double maxAura = 140000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 60000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 60000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 100.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 320.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.05;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.7;
      @Comment("Bonus Submerged Mining Speed.")
      public double submergedMiningSpeed = 0.6;

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
      public double getSubmergedMiningSpeed() {
         return this.submergedMiningSpeed;
      }
   }

   public static class Merfolk extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 400.0;
      @Comment("Maximum aura.")
      public double maxAura = 600.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 500.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 600.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 4.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 20.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = -0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;
      @Comment("Bonus Submerged Mining Speed.")
      public double submergedMiningSpeed = 0.4;

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
      public double getSubmergedMiningSpeed() {
         return this.submergedMiningSpeed;
      }
   }

   public static class MerfolkSaint extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Merfolk Saint.")
      public double epRequirement = 400000.0;
      @Comment("The number of Bosses defeated to evolve into Merfolk Saint.")
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
      public double maxHealth = 520.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.0;
      @Comment("Bonus Submerged Mining Speed.")
      public double submergedMiningSpeed = 0.8;

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

      @Generated
      public double getSubmergedMiningSpeed() {
         return this.submergedMiningSpeed;
      }
   }
}
