package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class DwarfConfig extends ManasConfig {
   public DwarfConfig.Dwarf Dwarf = new DwarfConfig.Dwarf();
   public DwarfConfig.EnlightenedDwarf EnlightenedDwarf = new DwarfConfig.EnlightenedDwarf();
   public DwarfConfig.DwarfSaint DwarfSaint = new DwarfConfig.DwarfSaint();
   public DwarfConfig.DivineDwarf DivineDwarf = new DwarfConfig.DivineDwarf();

   public String getFileName() {
      return "tensura/race/dwarf_config";
   }

   public static class DivineDwarf extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Dwarf.")
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
      public double attack = 3.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
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

   public static class Dwarf extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 720.0;
      @Comment("Maximum aura.")
      public double maxAura = 1080.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 80.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 120.0;
      @Comment("Bonus Size.")
      public double size = -0.375;
      @Comment("Bonus Max Health.")
      public double maxHealth = 4.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 10.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.1;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.02;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = -0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = -0.1;

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

   public static class DwarfSaint extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Dwarf Saint.")
      public double epRequirement = 400000.0;
      @Comment("The number of Bosses defeated to evolve into Dwarf Saint.")
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
      public double size = -0.125;
      @Comment("Bonus Max Health.")
      public double maxHealth = 520.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.4;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;

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

   public static class EnlightenedDwarf extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Enlightened Dwarf.")
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
      public double size = -0.25;
      @Comment("Bonus Max Health.")
      public double maxHealth = 90.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 320.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.1;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
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
}
