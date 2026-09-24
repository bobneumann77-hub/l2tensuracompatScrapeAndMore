package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import java.util.List;

@SyncToClient
public class PlayerConfig extends ManasConfig {
   public PlayerConfig.ResetScroll ResetScroll = new PlayerConfig.ResetScroll();
   public PlayerConfig.Naming Naming = new PlayerConfig.Naming();
   public PlayerConfig.Reputation Reputation = new PlayerConfig.Reputation();

   public String getFileName() {
      return "tensura/entity/player_config";
   }

   public static class Naming extends ManasSubConfig {
      @Comment("The percentage of max Health that the target need to be under to submit to the namer")
      public double lowHPToName = 25.0;
      @Comment("The percentage of EP of the namer that the target needs to be under to submit to the namer")
      public double maximumEPToName = 1.0;
      @Comment("The multiplier of EP that the target will gain from the Subdue option.")
      public double subdueGain = 0.5;
      @Comment("The multiplier of EP that the target will gain from the Evolve option.")
      public double evolveGain = 1.5;
      @Comment("The multiplier of EP that the target will gain from the Endow option.")
      public double endowGain = 9.0;
      @Comment("The maximum amount of EP that an entity can gain from being named.")
      public double maxEPGain = 1000000.0;
      @Comment("The percentage chance that the namer will lose maximum Magicule when choosing the Subdue option.")
      public double subdueLostChance = 0.0;
      @Comment("The percentage chance that the namer will lose maximum Magicule when choosing the Evolve option.")
      public double evolveLostChance = 20.0;
      @Comment("The percentage chance that the namer will lose maximum Magicule when choosing the Endow option.")
      public double endowLostChance = 50.0;
      @Comment("The maximum amount of Magicule that the namer can lose from naming a target.")
      public double maxCost = 3000000.0;
      @Comment("Random names for Subordinate Naming.")
      public List<String> randomNames = List.of(
         "Subordinate",
         "Pet",
         "MinhEragon",
         "Arthur",
         "Chris",
         "Nightishaman",
         "Gen",
         "Burack",
         "JustSomebody",
         "Noii",
         "Stewy",
         "Leo",
         "Onyx",
         "Nie",
         "Hunter",
         "Gold",
         "Alex",
         "Steve",
         "Viciel",
         "Sen",
         "Remkes",
         "Welt",
         "R.E",
         "Borniuus",
         "Romance",
         "Memoires",
         "Dranyas",
         "SunWine",
         "Slime",
         "Kiziro",
         "AxelDude",
         "3xMike",
         "Noxus",
         "Gwum",
         "Issimat",
         "Cipher",
         "Geld",
         "Benimaru",
         "Shion",
         "Shuna",
         "Rimuru",
         "Veldora",
         "Hakurou",
         "Milim",
         "Gabiru",
         "Souei",
         "Ranga",
         "Gobta",
         "Rigurd",
         "Beretta",
         "Clayman",
         "Leon",
         "Ifrit",
         "Mjurran",
         "Carrion",
         "Zegion",
         "Apito",
         "Luminous",
         "Dino",
         "Frey",
         "Dagruel",
         "Leon",
         "Guy",
         "Diablo",
         "Carrera",
         "Testarossa",
         "Ultima",
         "Misery",
         "Rain"
      );
   }

   public static class Reputation extends ManasSubConfig {
      @Comment("The maximum amount of reputation the player can get from dwarves.")
      public double maxReputation = 100.0;
      @Comment("The minimum amount of reputation the player can get from dwarves.")
      public double minReputation = -100.0;
      @Comment("How much reputation that the player gains after each selling trade with a dwarf (selling items for coins).")
      public double sellingTradePoint = 0.01;
      @Comment("How much reputation that the player gains after each buying trade with a dwarf (buying items with coins).")
      public double buyingTradePoint = 0.05;
      @Comment("How much reputation that the player gains after increasing a dwarf's trader level.")
      public double levelTradePoint = 0.1;
      @Comment("How much reputation that the player gains after saving a dwarf from a monster targeting them.")
      public double saveHelpPoint = 1.0;
      @Comment("How much reputation that the player loses after hitting a dwarf while having witnesses.")
      public double hurtLostPoint = 0.1;
      @Comment("How much reputation that the player loses after killing a dwarf while having witnesses.")
      public double killLostPoint = 1.0;
      @Comment("The price percentage of trade that the player get discounted for each positive reputation point.")
      public double discountPercentage = 0.005;
      @Comment("The price percentage of trade that the player get charged more for each negative reputation point.")
      public double chargePercentage = 0.02;
      @Comment("The amount of negative reputation that the player needs to reach for the dwarves to stop trading and guards to attack.")
      public double hostileReputation = -50.0;
   }

   public static class ResetScroll extends ManasSubConfig {
      @Comment("Whether Reset Counter should require Final Evolution of the player's race.")
      public boolean raceCounter = true;
      @Comment("Whether Reset Counter should require Awakening (True Hero/Demon Lord).")
      public boolean awakenCounter = true;
      @Comment("The list of bosses required for Reset Counter.")
      public List<String> bossesCounter = List.of(
         "tensura:orc_disaster", "tensura:ifrit", "tensura:charybdis", "tensura:elemental_colossus", "tensura:hinata_sakaguchi", "tensura:gazel_dwargo"
      );
      @Comment("The maximum number of bonus Unique Skills that resetCounterBonusUnique gamerule can give to a player.")
      public int maxCounterBonus = 1024;
      @Comment(
         "Apply the reset counter Penalty when a player uses a Race/Skill Reset Scroll even when they meet all of their reset requirements\nOnly applies when the resetIncompletePenalty gamerule is higher than 1."
      )
      public boolean counterPenaltyNonCharScroll = false;
      @Comment("List of Reset Scroll that can be used.")
      public List<String> resetScrolls = List.of("tensura:race_reset_scroll", "tensura:skill_reset_scroll", "tensura:character_reset_scroll");
   }
}
