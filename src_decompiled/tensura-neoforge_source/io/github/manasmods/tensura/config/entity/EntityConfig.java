package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.List;

public class EntityConfig extends ManasConfig {
   @Comment("The maximum radius that a tamed mobs can wander away from the position where it was set to wander.")
   public int tamedWanderRadius = 20;
   @Comment("The maximum radius that a tamed mobs can sleep away from the owner's position when set to follow.")
   public double tamedSleepRadius = 12.0;
   @Comment("The multiplier of max HP that a hostile tensura mob needs to be below to start targeting passive animals.")
   public float hostileHPMultiplier = 0.9F;
   @Comment("The amount of HP that an entity heal whenever it kills an enemy and gains EP.")
   public float successKillHeal = 1.0F;
   @Comment("Multiplier applied to the mob's EP to determine the amount of XP dropped on death.")
   public float xpDropMultiplier = 0.001F;
   public EntityConfig.Boss Boss = new EntityConfig.Boss();
   public EntityConfig.Dwarf Dwarf = new EntityConfig.Dwarf();
   public EntityConfig.Goblin Goblin = new EntityConfig.Goblin();
   public EntityConfig.Lizardman Lizardman = new EntityConfig.Lizardman();
   public EntityConfig.Orc Orc = new EntityConfig.Orc();
   public EntityConfig.MobSpecific MobSpecific = new EntityConfig.MobSpecific();

   public String getFileName() {
      return "tensura/entity/entity_config";
   }

   public static class Boss extends ManasSubConfig {
      @Comment("Allow players to name entities in the Boss Area dimension.")
      public boolean bossAreaName = false;
      @Comment("Allow players to mind control entities in the Boss Area dimension.")
      public boolean bossAreaMindControl = false;
      @Comment("Allow players to possess entities in the Boss Area dimension.")
      public boolean bossAreaPossess = false;
      @Comment("Allow players to copy/steal abilities in the Boss Area dimension.")
      public boolean bossAreaSkillPlunder = false;
      @Comment("Allow players to grief the environment using abilities in the Boss Area dimension.")
      public boolean bossAreaSkillGrief = false;
   }

   public static class Dwarf extends ManasSubConfig {
      @Comment("Dwarf's trade price multiplier to apply to most trades from Armorer dwarves.")
      public double armorerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Butcher dwarves.")
      public double butcherPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Battlewill Trainer dwarves.")
      public double battlewillTrainerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Cartographer dwarves.")
      public double cartographerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Alchemist dwarves.")
      public double alchemistPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Farmer dwarves.")
      public double farmerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Fisherman dwarves.")
      public double fishermanPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Fletcher dwarves.")
      public double fletcherPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Leatherworker dwarves.")
      public double leatherWorkerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Librarian dwarves.")
      public double librarianPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Lumberjack dwarves.")
      public double lumberjackPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Magic Trainer dwarves.")
      public double magicTrainerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Mason dwarves.")
      public double masonPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Merchant dwarves.")
      public double merchantPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Miner dwarves.")
      public double minerPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Shepherd dwarves.")
      public double shepherdPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Toolsmith dwarves.")
      public double toolSmithPriceMultiplier = 1.0;
      @Comment("Dwarf's trade price multiplier to apply to most trades from Weaponsmith dwarves.")
      public double weaponSmithPriceMultiplier = 1.0;
      @Comment("The chance for the dwarf to spawn with a scarred eye.")
      public double scarChance = 0.02;
      @Comment("Random colors for dwarves' hair.")
      public List<Integer> dwarfHairColors = List.of(-7558, -6260652, -1326982, -9418704, -12966368, -1052689);
      @Comment("Random colors for dwarves' top clothes.")
      public List<Integer> dwarfTopClothesColors = List.of(-13738962, -10867110, -5010688, -14540254, -1, -9560289, -6533601, -11184811, -2239048, -7640241);
      @Comment("Random colors for dwarves' bottom clothes.")
      public List<Integer> dwarfBottomClothesColors = List.of(-14540254, -11184811, -2239048, -11850209, -7640241);
      @Comment("Random colors for dwarves' boots.")
      public List<Integer> dwarfBootsColors = List.of(-11850209, -15066598);
      @Comment("Random names for Dwarves.")
      public List<String> dwarfNames = List.of(
         "Ana",
         "Anrietta",
         "Colette",
         "Dord",
         "Dorf",
         "Dyna",
         "Fio",
         "Inga",
         "Garm",
         "Jaine",
         "Johann",
         "Kaidou",
         "Kaijin",
         "Kaine",
         "Marche",
         "Mite",
         "Myrd",
         "Tosca",
         "Uhura",
         "Vaughn",
         "Vesta",
         "Vevely",
         "Yoshika",
         "Nieadni",
         "Onyx",
         "Noii",
         "Issimat",
         "Leon",
         "Ren",
         "Scimmia",
         "Kaedrin",
         "Borik",
         "Thalden",
         "Grald",
         "Rurik",
         "Maldor",
         "Dregnar",
         "Varkun",
         "Zorik",
         "Orvik",
         "Brondar",
         "Keldrin",
         "Hrogar",
         "Tovrik",
         "Dunrik",
         "Jorven",
         "Vradin",
         "Korrim",
         "Guldar",
         "Thovald",
         "Marvik",
         "Branik",
         "Fendral",
         "Lokrin",
         "Dornak",
         "Vornir",
         "Haldric",
         "Stogan",
         "Kravik"
      );
   }

   public static class Goblin extends ManasSubConfig {
      @Comment("Random colors for Goblins' hair.")
      public List<Integer> goblinHairColors = List.of(-14869219, -11849440, -10855846, -12961222, -1, -4741456, -1326982);
      @Comment("Random colors for Goblins' headband.")
      public List<Integer> goblinHeadColors = List.of(-1, -11184811, -2239048, -7640241, -11850209);
      @Comment("Random colors for Goblins' top clothes.")
      public List<Integer> goblinClothingColors = List.of(-14513374, -14540254, -1, -11184811, -2239048, -7640241, -8388480, -7650029, -16763765);
      @Comment("Random colors for Goblins' bottom clothes.")
      public List<Integer> goblinBottomClothesColors = List.of(-14540254, -11184811, -2239048, -11850209, -7640241);
   }

   public static class Lizardman extends ManasSubConfig {
      @Comment("Random colors for Lizardmen's hair.")
      public List<Integer> lizardmanHairColors = List.of(-8760265, -3893419, -11062239, -14477549, -2565928, -14472113, -13164473);
      @Comment("Random colors for Lizardmen's top clothes.")
      public List<Integer> lizardmanTopClothesColors = List.of(-14540254, -1, -11184811, -2239048, -7640241);
      @Comment("Random colors for Lizardmen's bottom clothes.")
      public List<Integer> lizardmanBottomClothesColors = List.of(-14540254, -11184811, -2239048, -11850209, -7640241);
      @Comment("Random colors for Lizardmen's cape/helmet/hood.")
      public List<Integer> lizardmanMiscClothesColors = List.of(-14540254, -11184811, -11850209, -7640241);
   }

   public static class MobSpecific extends ManasSubConfig {
      @Comment("The normal size multiplier for Charybdis.")
      public float charybdisSize = 1.0F;
      @Comment("The amount of EP that an empty elemental core will take from the spirit when used.")
      public double elementalCoreCost = 1000.0;
      @Comment("The chance in percentage for otherworlders to drop their Unique skills to the attacker - Range 0 -> 100.")
      public double otherworlderSkillDrop = 0.0;
   }

   public static class Orc extends ManasSubConfig {
      @Comment("Random colors for Orcs' top clothes.")
      public List<Integer> orcTopClothesColors = List.of(-14540254, -1, -11184811, -2239048, -7640241);
      @Comment("Random colors for Orcs' bottom clothes.")
      public List<Integer> orcBottomClothesColors = List.of(-14540254, -11184811, -2239048, -11850209, -7640241);
      @Comment("Random colors for Orcs' boots/capes/belts.")
      public List<Integer> orcLeatherColors = List.of(-14540254, -11184811, -11850209, -7640241);
   }
}
