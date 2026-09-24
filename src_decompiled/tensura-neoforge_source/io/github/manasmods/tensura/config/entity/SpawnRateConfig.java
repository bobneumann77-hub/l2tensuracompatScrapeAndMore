package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.List;

public class SpawnRateConfig extends ManasConfig {
   public SpawnRateConfig.SpecialVariant SpecialVariant = new SpawnRateConfig.SpecialVariant();
   @Comment("Spawn probability for mobs (1 in X chance). First value is Day, Second is Night.\nExamples: 1=100%, 5=20%, 10=10%. 0=disabled.")
   public SpawnRateConfig.SpawnChance SpawnChance = new SpawnRateConfig.SpawnChance();

   public String getFileName() {
      return "tensura/entity/spawn_rate_config";
   }

   public static class SpawnChance extends ManasSubConfig {
      public List<Integer> otherworlder = List.of(30, 20);
      public List<Integer> aquaFrog = List.of(16, 8);
      public List<Integer> archDaemon = List.of(80, 40);
      public List<Integer> armorsaurus = List.of(4, 2);
      public List<Integer> armyWasp = List.of(11, 8);
      public List<Integer> barghest = List.of(7, 5);
      public List<Integer> basilisk = List.of(10, 7);
      public List<Integer> beastGnome = List.of(16, 8);
      public List<Integer> blackSpider = List.of(8, 5);
      public List<Integer> bladeTiger = List.of(8, 6);
      public List<Integer> cattledeer = List.of(6, 4);
      public List<Integer> direwolf = List.of(2, 2);
      public List<Integer> dragonPeacock = List.of(8, 6);
      public List<Integer> evilCentipede = List.of(11, 8);
      public List<Integer> featheredSerpent = List.of(16, 8);
      public List<Integer> giantAnt = List.of(8, 6);
      public List<Integer> giantBat = List.of(0, 4);
      public List<Integer> giantBear = List.of(11, 8);
      public List<Integer> giantCod = List.of(6, 8);
      public List<Integer> giantSalmon = List.of(6, 8);
      public List<Integer> greaterDaemon = List.of(60, 30);
      public List<Integer> hellCaterpillar = List.of(8, 6);
      public List<Integer> hellMoth = List.of(12, 9);
      public List<Integer> hornedBear = List.of(11, 8);
      public List<Integer> hornedRabbit = List.of(7, 5);
      public List<Integer> houndDog = List.of(14, 7);
      public List<Integer> hoverLizard = List.of(7, 5);
      public List<Integer> knightSpider = List.of(8, 6);
      public List<Integer> landfish = List.of(7, 5);
      public List<Integer> leechLizard = List.of(8, 6);
      public List<Integer> lesserDaemon = List.of(40, 20);
      public List<Integer> megalodon = List.of(30, 15);
      public List<Integer> oneEyedOwl = List.of(10, 7);
      public List<Integer> orc = List.of(4, 3);
      public List<Integer> pegasus = List.of(24, 12);
      public List<Integer> phantaspore = List.of(10, 7);
      public List<Integer> salamander = List.of(16, 8);
      public List<Integer> sissie = List.of(10, 10);
      public List<Integer> slime = List.of(3, 2);
      public List<Integer> spearToro = List.of(6, 6);
      public List<Integer> unicorn = List.of(24, 12);
      public List<Integer> tempestSerpent = List.of(12, 9);
      public List<Integer> wingedCat = List.of(16, 8);
   }

   public static class SpecialVariant extends ManasSubConfig {
      @Comment("One of how many chances for an otherworlder to spawn as Shizu.")
      public int shizuChance = 30;
      @Comment("One of how many chances for an otherworlder to spawn as Hinata Sakaguchi.")
      public int hinataChance = 50;
      @Comment("One of how many chances for a hell caterpillar to spawn as a gehenna caterpillar.")
      public int gehennaCaterpillarChance = 20;
      @Comment("One of how many chances for a hell caterpillar cocoon to mutate to a gehenna moth.")
      public int gehennaCocoonChance = 60;
      @Comment("One of how many chances for a hell moth to spawn as or evolve to a gehenna moth.")
      public int gehennaMothChance = 20;
      @Comment("One of how many chances for a hound dog to have a snake tail.")
      public int houndDogSnakeChance = 20;
      @Comment("One of how many chances for an orc to be an orc lord.")
      public int orcLordChance = 900;
      @Comment("One of how many chances for an orc to be a royal orc.")
      public int orcRoyalChance = 100;
      @Comment("One of how many chances for a Royal Orc to have Self-Regeneration.")
      public int orcRoyalLordChance = 7;
      @Comment("One of how many chances for a Direwolf to be an Alpha.")
      public int alphaWolf = 10;
      @Comment("One of how many chances for a Tempest Wolf to have the Star birthmark.")
      public int starBirthmark = 16;
      @Comment("The percentage chance for a direwolf pack to be evolved")
      public int evolvedPack = 40;
      @Comment("The percentage chance for a direwolf pack to be Elemental Fang based on biomes")
      public int elementalFangPack = 65;
      @Comment("The percentage chance for a direwolf pack to be Tempest Wolves")
      public int tempestWolfPack = 10;
      @Comment("One of how many chances for a slime to spawn as Metal Slime.")
      public int metalSlimeChance = 500;
      @Comment("One of how many chances for a slime to spawn as Supermassive Slime.")
      public int supermassiveSlimeChance = 1000;
      @Comment("One of how many chances for a medium spirit spawned in Ancient Forest to spawn as a greater spirit.")
      public int greaterSpiritChance = 400;
   }
}
