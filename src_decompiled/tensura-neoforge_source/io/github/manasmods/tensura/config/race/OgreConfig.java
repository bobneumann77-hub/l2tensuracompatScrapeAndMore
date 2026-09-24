package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class OgreConfig extends ManasConfig {
   public OgreConfig.Ogre Ogre = new OgreConfig.Ogre();
   public OgreConfig.Kijin Kijin = new OgreConfig.Kijin();
   public OgreConfig.EnlightenedOgre EnlightenedOgre = new OgreConfig.EnlightenedOgre();
   public OgreConfig.MysticOni MysticOni = new OgreConfig.MysticOni();
   public OgreConfig.WickedOni WickedOni = new OgreConfig.WickedOni();
   public OgreConfig.SpiritOni SpiritOni = new OgreConfig.SpiritOni();
   public OgreConfig.DeathOni DeathOni = new OgreConfig.DeathOni();
   public OgreConfig.DivineOni DivineOni = new OgreConfig.DivineOni();
   public OgreConfig.DivineFighter DivineFighter = new OgreConfig.DivineFighter();

   public String getFileName() {
      return "tensura/race/ogre_config";
   }

   public static class DeathOni extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Ogre Saint.")
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
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 400.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3040.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;

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

   public static class DivineFighter extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Oni.")
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
      public double maxHealth = 940.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 5840.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 6.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.5;
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

   public static class DivineOni extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Oni.")
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
      public double maxSpiritualHealth = 6140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 7.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.5;
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

   public static class EnlightenedOgre extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Enlightened Ogre.")
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
      public double maxHealth = 100.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 340.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.3;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.02;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.2;

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

   public static class Kijin extends RaceConfig.Default {
      @Comment("The number of Spirits obtained to evolve into Kijin.")
      public int spiritRequirement = 1;
      @Comment("Minimal aura.")
      public double minAura = 5000.0;
      @Comment("Maximum aura.")
      public double maxAura = 5000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 5000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 5000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 10.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 100.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.02;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.2;

      @Generated
      public int getSpiritRequirement() {
         return this.spiritRequirement;
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

   public static class MysticOni extends RaceConfig.Default {
      @Comment("The number of Elemental Essences consumed to evolve into Mystic Oni.")
      public int essenceRequirement = 10;
      @Comment("Minimal aura.")
      public double minAura = 40000.0;
      @Comment("Maximum aura.")
      public double maxAura = 100000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 40000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 140.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 400.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.4;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;

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
   }

   public static class Ogre extends RaceConfig.Default {
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
      public double maxHealth = 6.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 15.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.1;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
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

   public static class SpiritOni extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Ogre Saint.")
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
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 500.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3440.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 5.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.3;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;

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

   public static class WickedOni extends RaceConfig.Default {
      @Comment("The number of Demon Essences consumed to evolve into Wicked Oni.")
      public int essenceRequirement = 10;
      @Comment("Minimal aura.")
      public double minAura = 40000.0;
      @Comment("Maximum aura.")
      public double maxAura = 100000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 40000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 110.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 460.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.5;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.4;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;

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
   }
}
