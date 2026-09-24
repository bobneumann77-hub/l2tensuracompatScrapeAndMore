package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class GiantConfig extends ManasConfig {
   public GiantConfig.Giant Giant = new GiantConfig.Giant();
   public GiantConfig.AncientGiant AncientGiant = new GiantConfig.AncientGiant();
   public GiantConfig.DivineGiant DivineGiant = new GiantConfig.DivineGiant();

   public String getFileName() {
      return "tensura/race/giant_config";
   }

   public static class AncientGiant extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Ancient Giant.")
      public double epRequirement = 300000.0;
      @Comment("The number of Ancient Debris needed to evolve into Ancient Giant.")
      public int debrisRequirement = 20;
      @Comment("Minimal aura.")
      public double minAura = 150000.0;
      @Comment("Maximum aura.")
      public double maxAura = 150000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 150000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 150000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 480.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 640.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 7.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.25;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.8;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;
      @Comment("How much of the attack damage that Giant will inflict on targets' armor durability.")
      public double armorDurability = 0.25;

      @Generated
      public double getEpRequirement() {
         return this.epRequirement;
      }

      @Generated
      public int getDebrisRequirement() {
         return this.debrisRequirement;
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
      public double getArmorDurability() {
         return this.armorDurability;
      }
   }

   public static class DivineGiant extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Divine Giant.")
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
      public double maxHealth = 1080.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6940.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 14.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 1.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.05;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.5;
      @Comment("How much of the attack damage that Giant will inflict on targets' armor durability.")
      public double armorDurability = 0.5;

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
      public double getArmorDurability() {
         return this.armorDurability;
      }
   }

   public static class Giant extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 4000.0;
      @Comment("Maximum aura.")
      public double maxAura = 6000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 6000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 8000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 30.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 140.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 5.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
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
}
