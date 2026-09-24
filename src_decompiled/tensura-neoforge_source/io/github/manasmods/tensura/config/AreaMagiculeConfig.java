package io.github.manasmods.tensura.config;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import java.util.UUID;

public class AreaMagiculeConfig extends ManasConfig {
   @Comment("Base magicule value for a chunk.")
   public double baseMagicule = 500.0;
   @Comment("The maximum amount of magicule value a chunk can get.")
   public double maximumMagicule = 1000000.0;
   @Comment("Base magicule regeneration per second for a chunk.")
   public double baseMagiculeRegeneration = 10.0;
   @Comment("How often maxMagiculeModifier are updated in ticks.")
   public int modifierUpdateInterval = 200;
   @Comment("Seed for magicule generation\", \"Changing this will change the magicule values for all chunks.")
   public long magiculeSeed = UUID.randomUUID().getLeastSignificantBits();
   @Comment("The amount that a magic engine will reduce from surrounding chunks (replace the blocks to apply the new value).")
   public double magicEngineReduction = 1000.0;
   @Comment("The radius in block that a magic engine will affect on entities (replace the blocks to apply the new value).")
   public double magicEngineRange = 16.0;
   @Comment("The amount that a labyrinth magic engine will reduce from surrounding chunks (replace the blocks to apply the new value).")
   public double labyrinthMagicEngineReduction = 10000.0;
   @Comment("The radius in block that a labyrinth magic engine will affect on entities (replace the blocks to apply the new value).")
   public double labyrinthMagicEngineRange = 32.0;
   @Comment("The minimal amount of Magicule in a chunk to allow mob spawn.")
   public double minimalMagiculeSpawn = 10.0;

   public String getFileName() {
      return "tensura/area_magicule_config";
   }
}
