package io.github.manasmods.tensura.config.ability;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import io.github.manasmods.manascore.config.api.SyncToClient;
import java.util.List;

@SyncToClient
public class AbilityConfig extends ManasConfig {
   public AbilityConfig.Learning Learning = new AbilityConfig.Learning();
   public AbilityConfig.Mastery Mastery = new AbilityConfig.Mastery();
   public AbilityConfig.Dodge Dodge = new AbilityConfig.Dodge();
   public AbilityConfig.Misc Misc = new AbilityConfig.Misc();
   @Comment("List of Battlewills that can be randomly obtained from using the Battlewill Manual.")
   public List<String> battlewillManualList = List.of(
      "tensura:aura_slash",
      "tensura:aura_sword",
      "tensura:earthshatter_kick",
      "tensura:ogre_sword_guillotine",
      "tensura:roaring_lion_punch",
      "tensura:dark_eight_palms",
      "tensura:elephant_stampede",
      "tensura:magic_bullet",
      "tensura:ogre_flame",
      "tensura:air_flight",
      "tensura:aura_shield",
      "tensura:battlewill",
      "tensura:diamond_path",
      "tensura:formhide",
      "tensura:instant_move",
      "tensura:violent_break"
   );

   public String getFileName() {
      return "tensura/ability/ability_config";
   }

   public static class Dodge extends ManasSubConfig {
      @Comment("The base dodge strength when the player doesn't have any dodge strength ability.")
      public double weakDodgeStrength = 0.3;
      @Comment("The base dodge strength when the player has any dodge strength ability.")
      public double strongDodgeStrength = 0.75;
      @Comment("The jump strength multiplier to applied on the player's dodge vertical force.")
      public double verticalDodgeMultiplier = 0.5;
      @Comment("The base invulnerable duration in tick to apply on the player when they uses dodge.")
      public int baseDodgeInvulnerableTick = 5;
      @Comment("The maximum invulnerable duration in tick to apply on the player when they uses dodge.")
      public int maxDodgeInvulnerableTick = 10;
      @Comment("The cooldown in tick of player manual dodging")
      public int dodgeCooldown = 20;
   }

   public static class Learning extends ManasSubConfig {
      @Comment("The base value of how many learning points the player gains when learning an ability.")
      public int learningPoint = 1;
      @Comment("The min bonus learning points the player can gain when learning an ability.")
      public int minBonus = 0;
      @Comment("The max bonus learning points the player can gain when learning an ability.")
      public int maxBonus = 4;
      @Comment("The multiplier of energy cost compared to normal cost when learning a new ability.")
      public float learningCostMultiplier = 5.0F;
      @Comment("The number of learning points a new ability need to get to become fully learnt.")
      public int learningPointRequirement = 100;
      @Comment("The number of seconds of cooldown when a new ability gains a learning point.")
      public int learningCooldown = 10;
      @Comment("The number of seconds of cooldown when a new ability fails to gain a learning point.")
      public int learningFailCooldown = 3;
      @Comment("The chance to the failing penalty to apply.")
      public double failingPenaltyChance = 0.1;
      @Comment("The level of the misfire status effect when failing penalty applies.")
      public int failingPenaltyLevel = 1;
      @Comment("The duration in ticks of the misfire status effect when failing penalty applies.")
      public int failingPenaltyDuration = 200;
      @Comment("The min learning points the player can lose when failing to learn an ability.")
      public int failingPenaltyMin = 1;
      @Comment("The max learning points the player can lose when failing to learn an ability.")
      public int failingPenaltyMax = 3;
   }

   public static class Mastery extends ManasSubConfig {
      @Comment(
         "The base value of how many mastery points the player gains when using an ability - Only applies for new players when changed cus this is an attribute."
      )
      public int masteryPoint = 1;
      @Comment("The multiplier of mastery point the player gains when the ability hit a target.")
      public int masteryHitMultiplier = 2;
      @Comment("The multiplier of mastery point the player gains when the ability kills a target.")
      public int masteryKillMultiplier = 2;
      @Comment("How long in tick the user need to hold down an ability to gain a mastery point for holding abilities.")
      public int masteryHoldTick = 60;
      @Comment("How many time the user need to activate some passive abilities (tick/on attack) to gain a mastery point for holding abilities.")
      public int masteryActivateTime = 10;
   }

   public static class Misc extends ManasSubConfig {
      @Comment("The multiplier of the damage dealt to be applied with Severance when doing a severance attack")
      public float severanceMultiplier = 0.5F;
      @Comment("The number of seconds that Severance will remove itself after if not updated.\n0 = disable Severance\n-1 = lasts forever")
      public int severanceRemoveSec = 300;
      @Comment("Let abilities (like Observer) to have block x-ray vision.")
      public boolean blockXrayVision = true;
      @Comment("Let thrown items (mostly from Thrower) place liquid on impact.")
      public boolean thrownLiquid = true;
   }
}
