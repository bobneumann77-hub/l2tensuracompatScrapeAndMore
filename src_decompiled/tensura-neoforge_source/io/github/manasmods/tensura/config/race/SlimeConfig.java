package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import lombok.Generated;

@SyncToClient
public class SlimeConfig extends ManasConfig {
   public SlimeConfig.Slime Slime = new SlimeConfig.Slime();
   public SlimeConfig.MetalSlime MetalSlime = new SlimeConfig.MetalSlime();
   public SlimeConfig.DemonSlime DemonSlime = new SlimeConfig.DemonSlime();
   public SlimeConfig.GodSlime GodSlime = new SlimeConfig.GodSlime();
   @Comment("The physical damage input multiplier taken by this race.")
   public float physicalInput = 0.5F;

   public String getFileName() {
      return "tensura/race/slime_config";
   }

   public static class DemonSlime extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 400000.0;
      @Comment("Maximum aura.")
      public double maxAura = 400000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 400000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 400000.0;
      @Comment("Bonus Size.")
      public double size = -0.675;
      @Comment("Bonus Max Health.")
      public double maxHealth = 500.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3100.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.5;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.3;
      @Comment("Bonus Jump Strength.")
      public double jumpStrength = 0.6;
      @Comment("Fall Damage Multiplier.")
      public double fallDamage = -0.75;
      @Comment("Get Max Jump Charge tick.")
      public int maxChargeTick = 60;
      @Comment("Hitbox width multiplier.")
      public float width = 4.0F;

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
      public double getJumpStrength() {
         return this.jumpStrength;
      }

      @Generated
      public double getFallDamage() {
         return this.fallDamage;
      }

      @Generated
      public int getMaxChargeTick() {
         return this.maxChargeTick;
      }

      @Generated
      public float getWidth() {
         return this.width;
      }
   }

   public static class GodSlime extends RaceConfig.Default {
      @Comment("EP requirement to evolve into God Slime.")
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
      public double size = -0.675;
      @Comment("Bonus Max Health.")
      public double maxHealth = 1000.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6400.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.7;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.07;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.7;
      @Comment("Bonus Jump Strength.")
      public double jumpStrength = 0.7;
      @Comment("Fall Damage Multiplier.")
      public double fallDamage = -1.0;
      @Comment("Get Max Jump Charge tick.")
      public int maxChargeTick = 80;
      @Comment("Hitbox width multiplier.")
      public float width = 4.0F;

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
      public double getJumpStrength() {
         return this.jumpStrength;
      }

      @Generated
      public double getFallDamage() {
         return this.fallDamage;
      }

      @Generated
      public int getMaxChargeTick() {
         return this.maxChargeTick;
      }

      @Generated
      public float getWidth() {
         return this.width;
      }
   }

   public static class MetalSlime extends RaceConfig.Default {
      @Comment("The number of Magic Ore consumed to evolve into Metal Slime.")
      public int oreRequirement = 100;
      @Comment("Minimal aura.")
      public double minAura = 3000.0;
      @Comment("Maximum aura.")
      public double maxAura = 3000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 7000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 7000.0;
      @Comment("Bonus Size.")
      public double size = -0.725;
      @Comment("Bonus Max Health.")
      public double maxHealth = 80.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 260.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;
      @Comment("Bonus Jump Strength.")
      public double jumpStrength = 0.5;
      @Comment("Fall Damage Multiplier.")
      public double fallDamage = -0.5;
      @Comment("Get Max Jump Charge tick.")
      public int maxChargeTick = 50;
      @Comment("Hitbox width multiplier.")
      public float width = 4.0F;

      @Generated
      public int getOreRequirement() {
         return this.oreRequirement;
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
      public double getJumpStrength() {
         return this.jumpStrength;
      }

      @Generated
      public double getFallDamage() {
         return this.fallDamage;
      }

      @Generated
      public int getMaxChargeTick() {
         return this.maxChargeTick;
      }

      @Generated
      public float getWidth() {
         return this.width;
      }
   }

   public static class Slime extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 200.0;
      @Comment("Maximum aura.")
      public double maxAura = 500.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 200.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 500.0;
      @Comment("Bonus Size.")
      public double size = -0.75;
      @Comment("Bonus Max Health.")
      public double maxHealth = -10.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 15.0;
      @Comment("Bonus Attack Damage.")
      public double attack = -0.7;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.0;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = -0.03;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = -0.3;
      @Comment("Bonus Jump Strength.")
      public double jumpStrength = 0.4;
      @Comment("Fall Damage Multiplier.")
      public double fallDamage = -0.5;
      @Comment("Get Max Jump Charge tick.")
      public int maxChargeTick = 40;
      @Comment("Hitbox width multiplier.")
      public float width = 4.0F;

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
      public double getJumpStrength() {
         return this.jumpStrength;
      }

      @Generated
      public double getFallDamage() {
         return this.fallDamage;
      }

      @Generated
      public int getMaxChargeTick() {
         return this.maxChargeTick;
      }

      @Generated
      public float getWidth() {
         return this.width;
      }
   }
}
