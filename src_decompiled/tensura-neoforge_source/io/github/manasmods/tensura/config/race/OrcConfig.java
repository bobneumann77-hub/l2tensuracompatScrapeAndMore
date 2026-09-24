package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class OrcConfig extends ManasConfig {
   public OrcConfig.Orc Orc = new OrcConfig.Orc();
   public OrcConfig.HighOrc HighOrc = new OrcConfig.HighOrc();
   public OrcConfig.OrcLord OrcLord = new OrcConfig.OrcLord();
   public OrcConfig.OrcDisaster OrcDisaster = new OrcConfig.OrcDisaster();
   public OrcConfig.SpiritBoar SpiritBoar = new OrcConfig.SpiritBoar();
   public OrcConfig.DivineBoar DivineBoar = new OrcConfig.DivineBoar();

   public String getFileName() {
      return "tensura/race/orc_config";
   }

   public static class DivineBoar extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Boar.")
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
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 980.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6440.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 1.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.08;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.8;

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

   public static class HighOrc extends RaceConfig.Default {
      @Comment("EP requirement to evolve into High Orc.")
      public double epRequirement = 5000.0;
      @Comment("Minimal aura.")
      public double minAura = 2000.0;
      @Comment("Maximum aura.")
      public double maxAura = 2000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 1000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 1000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 12.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 100.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.25;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;

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

   public static class Orc extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 400.0;
      @Comment("Maximum aura.")
      public double maxAura = 600.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 50.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 8.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 16.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = -0.02;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = -0.2;

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

   public static class OrcDisaster extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Orc Disaster.")
      public double epRequirement = 200000.0;
      @Comment("Minimal aura.")
      public double minAura = 100000.0;
      @Comment("Maximum aura.")
      public double maxAura = 100000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 100000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100000.0;
      @Comment("Bonus Size.")
      public double size = 0.625;
      @Comment("Bonus Max Health.")
      public double maxHealth = 280.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 980.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.5;
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

   public static class OrcLord extends RaceConfig.Default {
      @Comment("The number of Royal Blood consumed to evolve into Orc Lord.")
      public int bloodRequirement = 10;
      @Comment("Minimal aura.")
      public double minAura = 3000.0;
      @Comment("Maximum aura.")
      public double maxAura = 5000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 7000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 10000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 80.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 360.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;

      @Generated
      public int getBloodRequirement() {
         return this.bloodRequirement;
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

   public static class SpiritBoar extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Spirit Boar.")
      public double epRequirement = 400000.0;
      @Comment("Minimal aura.")
      public double minAura = 400000.0;
      @Comment("Maximum aura.")
      public double maxAura = 400000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 400000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 400000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 580.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3340.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.3;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.6;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;

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
}
