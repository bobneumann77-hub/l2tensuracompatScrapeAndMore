package io.github.manasmods.tensura.world;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.network.s2c.SendBooleanGameruleUpdatePayload;
import io.github.manasmods.tensura.network.s2c.SendIntegerGameruleUpdatePayload;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.util.TensuraEnumHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import org.jetbrains.annotations.Nullable;

public class TensuraGameRules {
   public static Category ABILITY = TensuraEnumHelper.createGameRuleCategory("ABILITY", "gamerule.category.ability");
   public static Category EXISTENCE = TensuraEnumHelper.createGameRuleCategory("EXISTENCE", "gamerule.category.existence");
   public static Category RACE = TensuraEnumHelper.createGameRuleCategory("RACE", "gamerule.category.race");
   public static Category TENSURA_PLAYER = TensuraEnumHelper.createGameRuleCategory("TENSURA_PLAYER", "gamerule.category.tensura_player");
   public static Category TENSURA_MISC = TensuraEnumHelper.createGameRuleCategory("TENSURA_MISC", "gamerule.category.tensura_misc");
   public static Key<IntegerValue> DEMON_LORD_SEED;
   public static Key<IntegerValue> DEMON_LORD_AWAKEN;
   public static Key<IntegerValue> FORCE_HARVEST_FESTIVAL;
   public static Key<BooleanValue> LABYRINTH_PVP;
   public static Key<BooleanValue> LABYRINTH_DEATH;
   public static Key<BooleanValue> COLOSSUS_RESPAWN;
   public static Key<IntegerValue> EP_DEATH_PENALTY;
   public static Key<IntegerValue> MP_SKILL_COST;
   public static Key<BooleanValue> NO_UNIQUE_START;
   public static Key<BooleanValue> TRULY_UNIQUE;
   public static Key<IntegerValue> RESET_INCOMPLETE_PENALTY;
   public static Key<IntegerValue> RESET_COUNTER_BONUS_UNIQUE;
   public static Key<IntegerValue> RESET_PER_SKILL_LOCK;
   public static Key<BooleanValue> SKILL_BEFORE_RACE;
   public static Key<BooleanValue> HARDCORE_RACE;
   public static Key<IntegerValue> MAXIMUM_MAGIC_EXPLOSION;
   public static Key<BooleanValue> SKILL_GRIEFING;
   public static Key<BooleanValue> SKILL_STEAL;
   public static Key<BooleanValue> EP_STEAL;
   public static Key<BooleanValue> MIND_CONTROL;
   public static Key<BooleanValue> DISABLE_NULLIFICATION;
   public static Key<BooleanValue> DISABLE_DAEMON_AUTO_MAGIC;
   public static Key<BooleanValue> DISABLE_SPIRITUAL_LIMIT;
   public static Key<BooleanValue> PLAYER_MANUAL_DODGING;
   public static Key<BooleanValue> PLAYER_SUMMONING;
   public static Key<BooleanValue> PLAYER_NAME;
   public static Key<BooleanValue> TENSURA_DISPLAY_NAME;
   public static Key<BooleanValue> RIMURU_MODE;
   public static Key<BooleanValue> NPC_GRIEF;
   public static Key<BooleanValue> NPC_WORKING;
   public static Key<IntegerValue> MAX_MP_GAIN;
   public static Key<IntegerValue> MAX_AP_GAIN;
   public static Key<IntegerValue> EP_GAIN_MULTIPLIER;
   public static Key<IntegerValue> PLAYER_EP;
   public static Key<IntegerValue> VANILLA_EP;
   public static Key<IntegerValue> TENSURA_EP;
   public static Key<IntegerValue> MODDED_EP;
   public static Key<IntegerValue> SPAWNER_EP;

   public static void init() {
      NO_UNIQUE_START = GameRules.register("noUniqueStart", ABILITY, BooleanValue.create(false));
      TRULY_UNIQUE = GameRules.register("trulyUnique", ABILITY, BooleanValue.create(false));
      MP_SKILL_COST = GameRules.register("mpSkillCost", ABILITY, IntegerValue.create(100));
      MAXIMUM_MAGIC_EXPLOSION = GameRules.register("maximumMagicExplosion", ABILITY, IntegerValue.create(100));
      DISABLE_NULLIFICATION = GameRules.register(
         "disableNullification",
         ABILITY,
         BooleanValue.create(
            false,
            (server, value) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendBooleanGameruleUpdatePayload(SendBooleanGameruleUpdatePayload.GameruleKey.DISABLE_NULLIFICATION, value.get())
            )
         )
      );
      SKILL_GRIEFING = GameRules.register("skillGriefing", ABILITY, BooleanValue.create(true));
      SKILL_STEAL = GameRules.register("skillSteal", ABILITY, BooleanValue.create(true));
      EP_STEAL = GameRules.register("epSteal", ABILITY, BooleanValue.create(false));
      MIND_CONTROL = GameRules.register("playerMindControl", ABILITY, BooleanValue.create(true));
      MAX_MP_GAIN = GameRules.register("maxMpGain", EXISTENCE, IntegerValue.create(1000000000));
      MAX_AP_GAIN = GameRules.register("maxApGain", EXISTENCE, IntegerValue.create(1000000000));
      EP_GAIN_MULTIPLIER = GameRules.register("epGainMultiplier", EXISTENCE, IntegerValue.create(1));
      PLAYER_EP = GameRules.register("playerEP", EXISTENCE, IntegerValue.create(100));
      VANILLA_EP = GameRules.register("vanillaEP", EXISTENCE, IntegerValue.create(100));
      TENSURA_EP = GameRules.register("tensuraEP", EXISTENCE, IntegerValue.create(100));
      MODDED_EP = GameRules.register("moddedEP", EXISTENCE, IntegerValue.create(100));
      SPAWNER_EP = GameRules.register("spawnerEP", EXISTENCE, IntegerValue.create(10));
      EP_DEATH_PENALTY = GameRules.register("epDeathPenalty", EXISTENCE, IntegerValue.create(5));
      RIMURU_MODE = GameRules.register("rimuruMode", RACE, BooleanValue.create(false));
      HARDCORE_RACE = GameRules.register("hardcoreRace", RACE, BooleanValue.create(false));
      SKILL_BEFORE_RACE = GameRules.register("skillBeforeRace", RACE, BooleanValue.create(false));
      DISABLE_DAEMON_AUTO_MAGIC = GameRules.register("disableDaemonAutoMagic", RACE, BooleanValue.create(false));
      DISABLE_SPIRITUAL_LIMIT = GameRules.register("disableSpiritualLimit", RACE, BooleanValue.create(false));
      TENSURA_DISPLAY_NAME = GameRules.register(
         "tensuraDisplayName",
         TENSURA_PLAYER,
         BooleanValue.create(
            false,
            (server, value) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendBooleanGameruleUpdatePayload(SendBooleanGameruleUpdatePayload.GameruleKey.TENSURA_NAME, value.get())
            )
         )
      );
      PLAYER_NAME = GameRules.register("playerNaming", TENSURA_PLAYER, BooleanValue.create(true));
      PLAYER_SUMMONING = GameRules.register("playerSummoning", TENSURA_PLAYER, BooleanValue.create(true));
      PLAYER_MANUAL_DODGING = GameRules.register(
         "playerManualDodging",
         TENSURA_PLAYER,
         BooleanValue.create(
            false,
            (server, value) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendBooleanGameruleUpdatePayload(SendBooleanGameruleUpdatePayload.GameruleKey.PLAYER_MANUAL_DODGING, value.get())
            )
         )
      );
      DEMON_LORD_SEED = GameRules.register("demonLordSeed", TENSURA_PLAYER, IntegerValue.create(200000));
      DEMON_LORD_AWAKEN = GameRules.register(
         "demonLordAwaken",
         TENSURA_PLAYER,
         IntegerValue.create(
            10000,
            (server, integerValue) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendIntegerGameruleUpdatePayload(SendIntegerGameruleUpdatePayload.GameruleKey.AWAKEN_SOUL, integerValue.get())
            )
         )
      );
      FORCE_HARVEST_FESTIVAL = GameRules.register("forceHarvestFestival", TENSURA_PLAYER, IntegerValue.create(20000));
      LABYRINTH_PVP = GameRules.register("labyrinthPvp", TENSURA_PLAYER, BooleanValue.create(true));
      LABYRINTH_DEATH = GameRules.register("labyrinthDeath", TENSURA_PLAYER, BooleanValue.create(false));
      NPC_GRIEF = GameRules.register("npcGrief", TENSURA_MISC, BooleanValue.create(true));
      NPC_WORKING = GameRules.register("npcWorking", TENSURA_MISC, BooleanValue.create(true));
      COLOSSUS_RESPAWN = GameRules.register("colossusRespawn", TENSURA_MISC, BooleanValue.create(false));
      RESET_COUNTER_BONUS_UNIQUE = GameRules.register("resetCounterBonusUnique", TENSURA_MISC, IntegerValue.create(0));
      RESET_INCOMPLETE_PENALTY = GameRules.register(
         "resetIncompletePenalty",
         TENSURA_MISC,
         IntegerValue.create(
            0,
            (server, integerValue) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendIntegerGameruleUpdatePayload(SendIntegerGameruleUpdatePayload.GameruleKey.RESET_INCOMPLETE_PENALTY, integerValue.get())
            )
         )
      );
      RESET_PER_SKILL_LOCK = GameRules.register(
         "resetPerSkillLock",
         TENSURA_MISC,
         IntegerValue.create(
            0,
            (server, integerValue) -> NetworkManager.sendToPlayers(
               server.getPlayerList().getPlayers(),
               new SendIntegerGameruleUpdatePayload(SendIntegerGameruleUpdatePayload.GameruleKey.RESET_PER_SKILL_LOCK, integerValue.get())
            )
         )
      );
   }

   public static boolean canStealSkill(Level level) {
      return level.getGameRules().getBoolean(SKILL_STEAL);
   }

   public static boolean canEpSteal(Level level) {
      return level.getGameRules().getBoolean(EP_STEAL);
   }

   public static boolean noPlayerMindControl(Level level) {
      return !level.getGameRules().getBoolean(MIND_CONTROL);
   }

   public static boolean canSkillGrief(Level level) {
      return level.dimension().equals(TensuraDimensions.LABYRINTH) ? false : level.getGameRules().getBoolean(SKILL_GRIEFING);
   }

   public static boolean isLabyrinthPvpOff(Level level) {
      return !level.dimension().equals(TensuraDimensions.LABYRINTH) ? false : !level.getGameRules().getBoolean(LABYRINTH_PVP);
   }

   public static boolean isLabyrinthPvpOff(Level level, Entity target, @Nullable Entity attacker) {
      if (!isLabyrinthPvpOff(level)) {
         return false;
      }

      if (attacker == target) {
         return false;
      }

      if (attacker instanceof Player player) {
         if (player.isCreative()) {
            return false;
         } else {
            return target instanceof Player ? true : target instanceof CloneEntity;
         }
      } else if (!(attacker instanceof ISubordinate animal && animal.isTame())) {
         return false;
      } else {
         return target instanceof Player ? true : target instanceof ISubordinate entity && entity.isTame();
      }
   }
}
