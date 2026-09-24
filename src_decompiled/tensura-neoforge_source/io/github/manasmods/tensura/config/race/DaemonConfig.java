package io.github.manasmods.tensura.config.race;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import java.util.List;
import lombok.Generated;

@SyncToClient
public class DaemonConfig extends ManasConfig {
   public DaemonConfig.LesserDaemon LesserDaemon = new DaemonConfig.LesserDaemon();
   public DaemonConfig.GreaterDaemon GreaterDaemon = new DaemonConfig.GreaterDaemon();
   public DaemonConfig.ArchDaemon ArchDaemon = new DaemonConfig.ArchDaemon();
   public DaemonConfig.DaemonLord DaemonLord = new DaemonConfig.DaemonLord();
   public DaemonConfig.DevilLord DevilLord = new DaemonConfig.DevilLord();

   public String getFileName() {
      return "tensura/race/daemon_config";
   }

   public static class ArchDaemon extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Arch Daemon.")
      public double epRequirement = 140000.0;
      @Comment("Minimal aura.")
      public double minAura = 40000.0;
      @Comment("Maximum aura.")
      public double maxAura = 40000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 100000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 100000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 100.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 606.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 2.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.2;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.4;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.01;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.1;
      @Comment("List of Magics that players automatically get as learnable.")
      public List<String> learnableMagics = List.of(
         "tensura:stone_shot",
         "tensura:fire_ball",
         "tensura:fire_storm",
         "tensura:icicle_spear",
         "tensura:icicle_lance",
         "tensura:ice_blizzard",
         "tensura:thunder_orb",
         "tensura:warp_portal",
         "tensura:water_jail",
         "tensura:acid_shell",
         "tensura:tornado_blade",
         "tensura:airflow_shut",
         "tensura:lighten",
         "tensura:burden",
         "tensura:protection",
         "tensura:confusion",
         "tensura:invisible",
         "tensura:recovery",
         "tensura:healing_rain",
         "tensura:antidote",
         "tensura:barrier",
         "tensura:magic_barrier"
      );

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
      public List<String> getLearnableMagics() {
         return this.learnableMagics;
      }
   }

   public static class DaemonLord extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 100000.0;
      @Comment("Maximum aura.")
      public double maxAura = 300000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 200000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 500000.0;
      @Comment("Bonus Size.")
      public double size = 0.0;
      @Comment("Bonus Max Health.")
      public double maxHealth = 646.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 3606.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 3.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.6;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.04;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.4;
      @Comment("List of Magics that players automatically get as learnable.")
      public List<String> learnableMagics = List.of(
         "tensura:mud_spears",
         "tensura:icicle_rain",
         "tensura:ice_breaker",
         "tensura:thunder_rain",
         "tensura:dimension_cutter",
         "tensura:full_recovery",
         "tensura:explosion",
         "tensura:dominate",
         "tensura:mirage",
         "tensura:reinforced_barrier"
      );

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
      public List<String> getLearnableMagics() {
         return this.learnableMagics;
      }
   }

   public static class DevilLord extends RaceConfig.Default {
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
      public double maxHealth = 1046.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 6606.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 4.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.7;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.8;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.08;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.8;
      @Comment("List of Magics that players automatically get as learnable.")
      public List<String> learnableMagics = List.of(
         "tensura:chain_explosion", "tensura:mental_crush", "tensura:demon_dominate", "tensura:anti_shock_area", "tensura:anti_magic_area"
      );

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
      public List<String> getLearnableMagics() {
         return this.learnableMagics;
      }
   }

   public static class GreaterDaemon extends RaceConfig.Default {
      @Comment("EP requirement to evolve into Greater Daemon.")
      public double epRequirement = 20000.0;
      @Comment("Minimal aura.")
      public double minAura = 10000.0;
      @Comment("Maximum aura.")
      public double maxAura = 20000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 10000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 30000.0;
      @Comment("Bonus Size.")
      public double size = 0.25;
      @Comment("Bonus Max Health.")
      public double maxHealth = 60.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 200.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 1.0;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = 0.0;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.2;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;
      @Comment("List of Magics that players automatically get as learnable.")
      public List<String> learnableMagics = List.of(
         "tensura:mud_hand",
         "tensura:earth_wall",
         "tensura:fire_lance",
         "tensura:fire_wall",
         "tensura:icicle_lance",
         "tensura:ice_wall",
         "tensura:thunder",
         "tensura:thunder_lance",
         "tensura:spatial_storage",
         "tensura:wind_cutter",
         "tensura:sleep_mist",
         "tensura:water_cutter_aspectual",
         "tensura:wind_protection",
         "tensura:float",
         "tensura:healing",
         "tensura:flame_wall",
         "tensura:agility",
         "tensura:reinforcement",
         "tensura:strength_aspectual",
         "tensura:magic_wall"
      );

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
      public List<String> getLearnableMagics() {
         return this.learnableMagics;
      }
   }

   public static class LesserDaemon extends RaceConfig.Default {
      @Comment("Minimal aura.")
      public double minAura = 2000.0;
      @Comment("Maximum aura.")
      public double maxAura = 3000.0;
      @Comment("Minimal magicule.")
      public double minMagicule = 5000.0;
      @Comment("Maximum magicule.")
      public double maxMagicule = 6000.0;
      @Comment("Bonus Size.")
      public double size = 0.5;
      @Comment("Bonus Max Health.")
      public double maxHealth = 20.0;
      @Comment("Bonus Max Spiritual Health.")
      public double maxSpiritualHealth = 90.0;
      @Comment("Bonus Attack Damage.")
      public double attack = 0.4;
      @Comment("Bonus Attack Speed.")
      public double attackSpeed = -0.5;
      @Comment("Bonus Knockback Resistance.")
      public double knockbackResistance = 0.1;
      @Comment("Bonus Movement Speed.")
      public double movementSpeed = 0.0;
      @Comment("Bonus Swimming Speed Multiplier.")
      public double swimSpeed = 0.0;
      @Comment("List of Magics that players automatically get as learnable.")
      public List<String> learnableMagics = List.of(
         "tensura:earth_lock",
         "tensura:liquidize",
         "tensura:fire_aspectual",
         "tensura:water_aspectual",
         "tensura:drainage",
         "tensura:wind_gust",
         "tensura:escape",
         "tensura:freeze"
      );

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
      public List<String> getLearnableMagics() {
         return this.learnableMagics;
      }
   }
}
