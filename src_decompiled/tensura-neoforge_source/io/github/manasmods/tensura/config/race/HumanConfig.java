package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class HumanConfig extends ManasConfig {
   public HumanConfig.Human Human = new HumanConfig.Human();
   public HumanConfig.EnlightenedHuman EnlightenedHuman = new HumanConfig.EnlightenedHuman();
   public HumanConfig.HumanSaint HumanSaint = new HumanConfig.HumanSaint();
   public HumanConfig.DivineHuman DivineHuman = new HumanConfig.DivineHuman();

   public String getFileName() {
      return "tensura/race/human_config";
   }

   public static class DivineHuman extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Human.")
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
      public double maxSpiritualHealth = 6000.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.1;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 1.0;

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

   public static class EnlightenedHuman extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Enlightened Human.")
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
      public double maxHealth = 80.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 300.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.1;

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

   public static class Human extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 760.0;
      @Comment("Maximum aura.")
      public double maxAura = 1140.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 50.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 70.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 0.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 0.0;
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

   public static class HumanSaint extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Human Saint.")
      public double epRequirement = 400000.0;
      @Comment("The number of Bosses defeated to evolve into Human Saint.")
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
      public double maxHealth = 480.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3000.0;
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
