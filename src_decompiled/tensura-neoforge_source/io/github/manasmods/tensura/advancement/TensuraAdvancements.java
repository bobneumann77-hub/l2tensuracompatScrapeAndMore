package io.github.manasmods.tensura.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TensuraAdvancements {
   @Nullable
   public static AdvancementHolder getAdvancement(Player player, ResourceLocation advancement) {
      return getAdvancement(player.level(), advancement);
   }

   @Nullable
   public static AdvancementHolder getAdvancement(Level level, ResourceLocation advancement) {
      return level.getServer().getAdvancements().get(advancement);
   }

   public static void grant(ServerPlayer serverPlayer, ResourceLocation resourceLocation) {
      AdvancementHolder advancement = getAdvancement(serverPlayer, resourceLocation);
      PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
      AdvancementProgress advancementProgress = playerAdvancements.getOrStartProgress(advancement);
      if (!advancementProgress.isDone()) {
         for (String key : advancementProgress.getRemainingCriteria()) {
            playerAdvancements.award(advancement, key);
         }
      }
   }

   public static void revoke(ServerPlayer serverPlayer, ResourceLocation resourceLocation) {
      AdvancementHolder advancement = getAdvancement(serverPlayer, resourceLocation);
      PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
      AdvancementProgress advancementProgress = playerAdvancements.getOrStartProgress(advancement);
      if (advancementProgress.isDone()) {
         for (String key : advancementProgress.getCompletedCriteria()) {
            playerAdvancements.revoke(advancement, key);
         }
      }
   }

   public static void revokeAllTensuraAdvancements(ServerPlayer serverPlayer) {
      for (AdvancementHolder advancement : serverPlayer.getServer().getAdvancements().getAllAdvancements()) {
         revoke(serverPlayer, advancement.id());
      }
   }

   public static class Adventure {
      public static final ResourceLocation REAL_ADVENTURE = ResourceLocation.fromNamespaceAndPath("tensura", "real_adventure");
      public static final ResourceLocation FAST_LEARNER = ResourceLocation.fromNamespaceAndPath("tensura", "fast_learner");
      public static final ResourceLocation MASTER_SKILL = ResourceLocation.fromNamespaceAndPath("tensura", "master_skill");
      public static final ResourceLocation MASTER_UNIQUE_SKILL = ResourceLocation.fromNamespaceAndPath("tensura", "master_unique_skill");
      public static final ResourceLocation FORBIDDEN_MANUAL = ResourceLocation.fromNamespaceAndPath("tensura", "forbidden_manual");
      public static final ResourceLocation YOU_A_WIZARD = ResourceLocation.fromNamespaceAndPath("tensura", "youre_a_wizard");
      public static final ResourceLocation WANDERFUL = ResourceLocation.fromNamespaceAndPath("tensura", "wanderful");
      public static final ResourceLocation BOOKED_ON_MAGIC = ResourceLocation.fromNamespaceAndPath("tensura", "booked_on_magic");
      public static final ResourceLocation EXPLOSION = ResourceLocation.fromNamespaceAndPath("tensura", "explosion");
      public static final ResourceLocation MONSTER_TAMER = ResourceLocation.fromNamespaceAndPath("tensura", "monster_tamer");
      public static final ResourceLocation HEAR_ME_DIREWOLVES = ResourceLocation.fromNamespaceAndPath("tensura", "hear_me_direwolves");
      public static final ResourceLocation GOOD_BOY = ResourceLocation.fromNamespaceAndPath("tensura", "good_boy");
      public static final ResourceLocation NAME_A_MOB = ResourceLocation.fromNamespaceAndPath("tensura", "name_a_mob");
      public static final ResourceLocation RULER_OF_MONSTERS = ResourceLocation.fromNamespaceAndPath("tensura", "ruler_of_monster");
      public static final ResourceLocation TAMED_A_SLIME = ResourceLocation.fromNamespaceAndPath("tensura", "tamed_a_slime");
      public static final ResourceLocation GET_BUCKETED = ResourceLocation.fromNamespaceAndPath("tensura", "get_bucketed");
      public static final ResourceLocation TRAITOR = ResourceLocation.fromNamespaceAndPath("tensura", "slime_traitor");
      public static final ResourceLocation GROW_A_SLIME = ResourceLocation.fromNamespaceAndPath("tensura", "grow_a_slime");
      public static final ResourceLocation KING_SLIME = ResourceLocation.fromNamespaceAndPath("tensura", "king_slime");
      public static final ResourceLocation SLIME_ARMY = ResourceLocation.fromNamespaceAndPath("tensura", "slime_army");
      public static final ResourceLocation MONSTER_RIDER = ResourceLocation.fromNamespaceAndPath("tensura", "monster_rider");
      public static final ResourceLocation KILLER_FISH = ResourceLocation.fromNamespaceAndPath("tensura", "killer_fish");
      public static final ResourceLocation CHOO_CHOO = ResourceLocation.fromNamespaceAndPath("tensura", "choo_choo");
      public static final ResourceLocation GETCHA_BETTER_LEATHERS = ResourceLocation.fromNamespaceAndPath("tensura", "getcha_better_leathers");
      public static final ResourceLocation BELIEVE_T0_FLY = ResourceLocation.fromNamespaceAndPath("tensura", "believe_to_fly");
      public static final ResourceLocation RIPOFF_ELYTRA = ResourceLocation.fromNamespaceAndPath("tensura", "ripoff_elytra");
      public static final ResourceLocation VIGILANT = ResourceLocation.fromNamespaceAndPath("tensura", "vigilant");
      public static final ResourceLocation SHELL_LIZARD = ResourceLocation.fromNamespaceAndPath("tensura", "shell_lizard");
      public static final ResourceLocation HISS_TORY = ResourceLocation.fromNamespaceAndPath("tensura", "hiss_tory");
      public static final ResourceLocation GOODNIGHT_SPIDER = ResourceLocation.fromNamespaceAndPath("tensura", "goodnight_spider");
      public static final ResourceLocation ARACHNOPHOBIC = ResourceLocation.fromNamespaceAndPath("tensura", "arachnophobic");
      public static final ResourceLocation EAT_OR_BE_EATEN = ResourceLocation.fromNamespaceAndPath("tensura", "eat_or_be_eaten");
      public static final ResourceLocation CONQUEROR_OF_FLAMES = ResourceLocation.fromNamespaceAndPath("tensura", "conqueror_of_flames");
      public static final ResourceLocation RULER_OF_THE_SKIES = ResourceLocation.fromNamespaceAndPath("tensura", "ruler_of_the_skies");
      public static final ResourceLocation NANODA = ResourceLocation.fromNamespaceAndPath("tensura", "nanoda");
      public static final ResourceLocation GREAT_SAINT_OF_THE_WEST = ResourceLocation.fromNamespaceAndPath("tensura", "great_saint_of_the_west");
      public static final ResourceLocation HERO_KING = ResourceLocation.fromNamespaceAndPath("tensura", "hero_king");
      public static final ResourceLocation START_SMITHING = ResourceLocation.fromNamespaceAndPath("tensura", "start_smithing");
      public static final ResourceLocation BECOME_NINJA = ResourceLocation.fromNamespaceAndPath("tensura", "become_ninja");
      public static final ResourceLocation UNHEALABLE_WOUND = ResourceLocation.fromNamespaceAndPath("tensura", "unhealable_wound");
      public static final ResourceLocation A_BIT_COLD = ResourceLocation.fromNamespaceAndPath("tensura", "a_bit_cold");
      public static final ResourceLocation MASTER_SMITH = ResourceLocation.fromNamespaceAndPath("tensura", "master_smith");
      public static final ResourceLocation NO_NO_SQUARE = ResourceLocation.fromNamespaceAndPath("tensura", "no_no_square");
      public static final ResourceLocation WAY_STONE = ResourceLocation.fromNamespaceAndPath("tensura", "way_stone");
      public static final ResourceLocation BETTER_SMELTER = ResourceLocation.fromNamespaceAndPath("tensura", "better_smelter");
      public static final ResourceLocation EVEN_BETTER_SMELTER = ResourceLocation.fromNamespaceAndPath("tensura", "even_better_smelter");
      public static final ResourceLocation BEST_SMELTER = ResourceLocation.fromNamespaceAndPath("tensura", "best_smelter");
      public static final ResourceLocation PIERROT_MASK = ResourceLocation.fromNamespaceAndPath("tensura", "pierrot_mask");
      public static final ResourceLocation TOO_STRONG = ResourceLocation.fromNamespaceAndPath("tensura", "too_strong");
      public static final ResourceLocation HELL = ResourceLocation.fromNamespaceAndPath("tensura", "hell");
      public static final ResourceLocation HELLA_COOL = ResourceLocation.fromNamespaceAndPath("tensura", "hella_cool");
      public static final ResourceLocation BUILD_BODY = ResourceLocation.fromNamespaceAndPath("tensura", "build_body");
      public static final ResourceLocation RAINBOW_IN_HELL = ResourceLocation.fromNamespaceAndPath("tensura", "rainbow_in_hell");
      public static final ResourceLocation UNHOLY_TOURISM = ResourceLocation.fromNamespaceAndPath("tensura", "unholy_tourism");
      public static final ResourceLocation OTHERWORLDLY_BIOMES = ResourceLocation.fromNamespaceAndPath("tensura", "otherworldly_biomes");
   }

   public static class Basic {
      public static final ResourceLocation REINCARNATED = ResourceLocation.fromNamespaceAndPath("tensura", "reincarnated");
      public static final ResourceLocation EMPOWERMENT = ResourceLocation.fromNamespaceAndPath("tensura", "empowerment");
      public static final ResourceLocation D_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "d_rank");
      public static final ResourceLocation C_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "c_rank");
      public static final ResourceLocation B_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "b_rank");
      public static final ResourceLocation A_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "a_rank");
      public static final ResourceLocation SA_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "sa_rank");
      public static final ResourceLocation S_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "s_rank");
      public static final ResourceLocation SS_RANK = ResourceLocation.fromNamespaceAndPath("tensura", "ss_rank");
      public static final ResourceLocation GROWTH_SPURT = ResourceLocation.fromNamespaceAndPath("tensura", "grow_spurt");
      public static final ResourceLocation INFAMY_FAMOUS = ResourceLocation.fromNamespaceAndPath("tensura", "infamy_famous");
      public static final ResourceLocation HIGHER_FORM = ResourceLocation.fromNamespaceAndPath("tensura", "higher_form");
      public static final ResourceLocation GETCHA_LEATHERS = ResourceLocation.fromNamespaceAndPath("tensura", "getcha_leathers");
      public static final ResourceLocation GOLD_RUSH = ResourceLocation.fromNamespaceAndPath("tensura", "gold_rush");
      public static final ResourceLocation ACQUIRE_SILVERWARE = ResourceLocation.fromNamespaceAndPath("tensura", "acquire_silverware");
      public static final ResourceLocation MAGIC_ORE = ResourceLocation.fromNamespaceAndPath("tensura", "magic_ore");
      public static final ResourceLocation LOW_MAGISTEEL = ResourceLocation.fromNamespaceAndPath("tensura", "low_magisteel");
      public static final ResourceLocation HIGH_MAGISTEEL = ResourceLocation.fromNamespaceAndPath("tensura", "high_magisteel");
      public static final ResourceLocation MITHRIL = ResourceLocation.fromNamespaceAndPath("tensura", "mithril");
      public static final ResourceLocation ORICHALCUM = ResourceLocation.fromNamespaceAndPath("tensura", "orichalcum");
      public static final ResourceLocation PURE_MAGISTEEL = ResourceLocation.fromNamespaceAndPath("tensura", "pure_magisteel");
      public static final ResourceLocation ADAMANTITE = ResourceLocation.fromNamespaceAndPath("tensura", "adamantite");
      public static final ResourceLocation HIHIIROKANE = ResourceLocation.fromNamespaceAndPath("tensura", "hihiirokane");
      public static final ResourceLocation LABYRINTH = ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth");
      public static final ResourceLocation JUST_A_TEST = ResourceLocation.fromNamespaceAndPath("tensura", "just_a_test");
      public static final ResourceLocation SPIRIT_PROTECTOR = ResourceLocation.fromNamespaceAndPath("tensura", "spirit_protector");
      public static final ResourceLocation ELEMENTALIST = ResourceLocation.fromNamespaceAndPath("tensura", "elementalist");
      public static final ResourceLocation BLESSED_ONE = ResourceLocation.fromNamespaceAndPath("tensura", "blessed_one");
      public static final ResourceLocation INFINITY_CORES = ResourceLocation.fromNamespaceAndPath("tensura", "infinity_cores");
      public static final ResourceLocation MAGIC_SEEDY_PLACE = ResourceLocation.fromNamespaceAndPath("tensura", "magic_seedy_place");
      public static final ResourceLocation HIPOKUTE_FLOWER = ResourceLocation.fromNamespaceAndPath("tensura", "hipokute_flower");
      public static final ResourceLocation GOOD_AS_NEW = ResourceLocation.fromNamespaceAndPath("tensura", "good_as_new");
      public static final ResourceLocation DELIGHTFUL_TRADE = ResourceLocation.fromNamespaceAndPath("tensura", "delightful_trade");
      public static final ResourceLocation MY_PRECIOUS = ResourceLocation.fromNamespaceAndPath("tensura", "my_precious");
      public static final ResourceLocation MILLION_DOLLAR = ResourceLocation.fromNamespaceAndPath("tensura", "million_dollar");
      public static final ResourceLocation REWIND_TIME = ResourceLocation.fromNamespaceAndPath("tensura", "rewind_time");
      public static final ResourceLocation OBTAIN_HIHIIROKANE_HOE = ResourceLocation.fromNamespaceAndPath("tensura", "obtain_hihiirokane_hoe");
      public static final ResourceLocation UNICORN_HORN = ResourceLocation.fromNamespaceAndPath("tensura", "unicorn_horn");
      public static final ResourceLocation LIGHT_AS_HORNED_RABBIT = ResourceLocation.fromNamespaceAndPath("tensura", "light_as_horned_rabbit");
      public static final ResourceLocation MONSTROUS_DIET = ResourceLocation.fromNamespaceAndPath("tensura", "monstrous_diet");
   }
}
