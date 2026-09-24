package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;

public class ProjectileConfig extends ManasConfig {
   @Comment("The number of ticks that a spear, kunai or tensura arrow needs to be on ground to despawn.")
   public int spearDespawnTick = 600;
   @Comment("The number of ticks that a thrown item (normally by Thrower) needs to be on ground to despawn.")
   public int thrownItemDespawnTick = 400;
   @Comment("The number of ticks that a magic entity needs to despawn by default if not custom set.")
   public int magicDespawnTick = 200;
   @Comment("The number of ticks that a magic projectile needs to despawn by default if not custom set.")
   public int projectileDespawnTick = 100;
   @Comment("How many blocks away from the shooter that a magic projectile needs to be to despawn.\nSet to a value higher than 0 to enable this.")
   public double projectileDespawnRange = -1.0;

   public String getFileName() {
      return "tensura/entity/projectile_config";
   }
}
