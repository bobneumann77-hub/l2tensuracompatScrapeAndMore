package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;

@SyncToClient
public class AttributeConfig extends ManasConfig {
   @Comment("The maximum Health an entity can get.")
   public double maxHP = 10000.0;
   @Comment("The maximum Attack an entity can get.")
   public double maxAttack = 10000.0;
   @Comment("The maximum Armor an entity can get.")
   public double maxArmor = 1024.0;
   @Comment("The maximum Armor Toughness an entity can get.")
   public double maxArmorToughness = 1024.0;
   @Comment("The maximum Size Scale an entity can get.")
   public double maxScale = 12.0;
   @Comment("The minimum Size Scale an entity can get.")
   public double minScale = 0.15;
   @Comment("The amount of armor a non-player mob gains for every 10000 EP it gains.")
   public double armorGain = 0.5;
   @Comment("The amount of attack a non-player mob gains for every 10000 EP it gains.")
   public double attackGain = 0.5;
   @Comment("The amount of HP a non-player mob gains for every 10000 EP it gains.")
   public double HPGain = 1.0;
   @Comment("The amount of SHP a non-player mob gains for every 10000 EP it gains.")
   public double SHPGain = 5.0;

   public String getFileName() {
      return "tensura/entity/attribute_config";
   }
}
