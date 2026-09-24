package io.github.manasmods.tensura.config;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import java.util.List;

public class EnchantmentConfig extends ManasConfig {
   @Comment("The amount of EP needed to become a Unique Equipment.")
   public double uniqueEP = 50000.0;
   @Comment("The amount of EP needed to become a Legend Equipment.")
   public double legendEP = 250000.0;
   @Comment("The amount of EP needed to become a God Equipment.")
   public double godEP = 1000000.0;
   @Comment("The amount of EP after the previous tier needed gain additional Engraving with Blessing of Transcendence.")
   public double transcendenceEP = 500000.0;
   @Comment("The percentage chance to obtain an Epic Engraving when reaching Unique Level.")
   public int uniqueEpic = 5;
   @Comment("The percentage chance to obtain a Rare Engraving when reaching Unique Level.")
   public int uniqueRare = 15;
   @Comment("The percentage chance to obtain an Uncommon Engraving when reaching Unique Level.")
   public int uniqueUncommon = 30;
   @Comment("The percentage chance to obtain an Epic Engraving when reaching Legend Level.")
   public int legendEpic = 20;
   @Comment("The percentage chance to obtain a Rare Engraving when reaching Legend Level.")
   public int legendRare = 40;
   @Comment("The percentage chance to obtain an Epic Engraving when reaching God Level.")
   public int godEpic = 40;
   @Comment("The list of Common engraving enchantments.")
   public List<String> commonEngraving = List.of("tensura:crushing", "tensura:sturdy", "tensura:swift");
   @Comment("The list of Uncommon engraving enchantments.")
   public List<String> uncommonEngraving = List.of(
      "tensura:magic_weapon",
      "tensura:holy_weapon",
      "tensura:slotting",
      "tensura:breathing_support",
      "tensura:elemental_resistance",
      "tensura:severance_protection",
      "tensura:magicule_absorption",
      "tensura:magic_capacity"
   );
   @Comment("The list of Rare engraving enchantments.")
   public List<String> rareEngraving = List.of(
      "tensura:energy_steal",
      "tensura:slotting",
      "tensura:barrier_piercing",
      "tensura:elemental_boost",
      "tensura:energy_protection",
      "tensura:magicule_absorption",
      "tensura:magic_protection",
      "tensura:magic_capacity"
   );
   @Comment("The list of Epic engraving enchantments.")
   public List<String> epicEngraving = List.of(
      "tensura:severance",
      "tensura:soul_eater",
      "tensura:slotting",
      "tensura:elemental_resistance",
      "tensura:intangibility",
      "tensura:magic_protection",
      "tensura:spiritual_protection",
      "tensura:magic_capacity"
   );
   @Comment("The percentage chance to obtain a Curse Engraving everytime gaining an engraving naturally.")
   public double curseChance = 3.0;
   @Comment("The list of Curse engraving enchantments can be obtained.")
   public List<String> curseEngraving = List.of("tensura:enervation", "tensura:lethargy", "tensura:sealing", "tensura:stagnation", "tensura:ruination");
   @Comment("The amount of input physical/magic damage that Enervation will reduce per piece per level.")
   public float enervationBarrier = 10.0F;
   @Comment("The multiplier of input magic damage that Magic Interference will reduce per level per armor/shield piece.")
   public float magicInterferenceMitigation = 0.2F;

   public String getFileName() {
      return "tensura/enchantment_config";
   }
}
