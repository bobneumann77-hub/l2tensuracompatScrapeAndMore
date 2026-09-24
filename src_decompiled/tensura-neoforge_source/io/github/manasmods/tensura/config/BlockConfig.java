package io.github.manasmods.tensura.config;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.List;

public class BlockConfig extends ManasConfig {
   @Comment("Enable/Disable fire spread inside Biomes with disabled fire tag (Ancient Forest, Miasmic Plains by default).")
   public boolean specialBiomeFireSpread = false;
   public BlockConfig.Baffledil Baffledil = new BlockConfig.Baffledil();
   public BlockConfig.CharybdisCore CharybdisCore = new BlockConfig.CharybdisCore();
   public BlockConfig.Kiln Kiln = new BlockConfig.Kiln();

   public String getFileName() {
      return "tensura/block_config";
   }

   public static class Baffledil extends ManasSubConfig {
      @Comment("The radius in block around the Baffledil flower that players get Hypnosis effect.")
      public double hypnosisRadius = 8.0;
      @Comment("The duration in tick of Hypnosis effect applied by the flower.")
      public int hypnosisDuration = 160;
      @Comment("The level of Hypnosis effect applied by the flower.")
      public int hypnosisLevel = 1;
   }

   public static class CharybdisCore extends ManasSubConfig {
      @Comment("The amount of EP needed for Inactive Charybdis Core to turn Active.")
      public double charybdisCoreActiveEP = 100000.0;
      @Comment("List of Skills that can be obtained from right-clicking Inert Charybdis Core.")
      public List<String> charybdisCoreSkills = List.of("tensura:gravity_manipulation", "tensura:magic_jamming");
      @Comment("The amount of EP that can be obtained from fusing with Inert Charybdis Core using Degenerate and similar abilities.")
      public double charybdisCoreFusingEP = 200000.0;
      @Comment("List of Skills that can be obtained from fusing with Inert Charybdis Core using Degenerate and similar abilities.")
      public List<String> charybdisCoreFusingSkills = List.of(
         "tensura:gravity_manipulation", "tensura:magic_jamming", "tensura:magic_sense", "tensura:ultraspeed_regeneration"
      );
      @Comment("List of Skills that can be obtained from fusing with Active Charybdis Core using Degenerate and similar abilities.")
      public List<String> charybdisCoreFusingSkillsActive = List.of("tensura:gravity_manipulation", "tensura:magic_sense");
      @Comment("List of Skills that can be obtained from fusing with Inactive Charybdis Core using Degenerate and similar abilities.")
      public List<String> charybdisCoreFusingSkillsInactive = List.of("tensura:gravity_manipulation");
   }

   public static class Kiln extends ManasSubConfig {
      @Comment("The max molten amount of a Default Kiln.")
      public int moltenDefault = 144;
      @Comment("The max molten amount of a Mithril Kiln.")
      public int moltenMithril = 288;
      @Comment("The max molten amount of a Orichalcum Kiln.")
      public int moltenOrichalcum = 576;
      @Comment("The amount of durability to take from a Fire Elemental Core to charge a kiln each time.")
      public int fireCoreCost = 100;
      @Comment("The charged duration in tick that a kiln gets each time its used with a Fire Elemental Core.")
      public int chargeDuration = 2400;
   }
}
