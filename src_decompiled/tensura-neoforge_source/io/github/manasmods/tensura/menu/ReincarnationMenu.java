package io.github.manasmods.tensura.menu;

import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.config.ReincarnationConfig;
import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReincarnationMenu extends AbstractContainerMenu {
   @Generated
   private static final Logger log = LogManager.getLogger(ReincarnationMenu.class);
   public static final PlayerConfig PLAYER_CONFIG = (PlayerConfig)ConfigRegistry.getConfig(PlayerConfig.class);
   public static final ReincarnationConfig CONFIG = (ReincarnationConfig)ConfigRegistry.getConfig(ReincarnationConfig.class);
   public final DataSlot selectedManasRaceIndex = DataSlot.standalone();
   public static final int SUBMIT_BUTTON_ID = -1;
   public static final int CHANGE_RACE_ONLY_SUBMIT_ID = -2;
   private final Player player;
   private boolean changeRaceOnly = false;
   private float customMaxAP = 0.0F;
   private float customMaxMP = 0.0F;
   private int racePool = 0;
   public static final List<TensuraSkill> RIMURU_SKILLS = List.of(
      (TensuraSkill)UniqueSkills.PREDATOR.get(),
      (TensuraSkill)UniqueSkills.GREAT_SAGE.get(),
      (TensuraSkill)ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.get(),
      (TensuraSkill)ResistanceSkills.ELECTRICITY_RESISTANCE.get(),
      (TensuraSkill)ResistanceSkills.PIERCE_RESISTANCE.get(),
      (TensuraSkill)ResistanceSkills.PAIN_NULLIFICATION.get(),
      (TensuraSkill)ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get()
   );
   public static final List<ManasSkill> RIMURU_UNIQUE_SKILLS = List.of((ManasSkill)UniqueSkills.PREDATOR.get(), (ManasSkill)UniqueSkills.GREAT_SAGE.get());

   public ReincarnationMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
      this(pContainerId, inventory, inventory.player, buf.readBoolean(), buf.readInt(), buf.readFloat(), buf.readFloat());
   }

   public ReincarnationMenu(int pContainerId, Inventory inventory, Player player) {
      this(pContainerId, inventory, player, false);
   }

   public ReincarnationMenu(int pContainerId, Inventory inventory, Player player, boolean changeRaceOnly) {
      this(pContainerId, inventory, player, changeRaceOnly, 0, 0.0F, 0.0F);
   }

   public ReincarnationMenu(int pContainerId, Inventory inventory, Player player, boolean changeRaceOnly, int pool, float maxAP, float maxMP) {
      super((MenuType)TensuraMenuTypes.REINCARNATION.get(), pContainerId);
      this.player = player;
      this.addDataSlot(this.selectedManasRaceIndex).set(0);
      this.changeRaceOnly = changeRaceOnly;
      this.racePool = pool;
      this.customMaxAP = maxAP;
      this.customMaxMP = maxMP;
   }

   public boolean stillValid(Player pPlayer) {
      return true;
   }

   public List<ManasRace> getRacePool() {
      ReincarnationConfig.Races config = ((ReincarnationConfig)ConfigRegistry.getConfig(ReincarnationConfig.class)).Races;

      return switch (this.racePool) {
         case 1 -> config.reincarnationRaces
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
         case 2 -> config.reincarnationRacesMastered
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
         default -> config.startingRaces
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
      };
   }

   public List<ManasRace> getRandomRacePool() {
      ReincarnationConfig.Races config = ((ReincarnationConfig)ConfigRegistry.getConfig(ReincarnationConfig.class)).Races;

      return switch (this.racePool) {
         case 1 -> config.reincarnationRandomRaces
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
         case 2 -> config.reincarnationRandomRacesMastered
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
         default -> config.randomRaces
            .stream()
            .map(id -> (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(id)))
            .filter(Objects::nonNull)
            .toList();
      };
   }

   public static List<ManasSkill> getSkillPool() {
      return CONFIG.Skills
         .startingSkills
         .stream()
         .map(id -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(id)))
         .filter(Objects::nonNull)
         .toList();
   }

   public static List<ManasSkill> getSecondSkillPool() {
      return CONFIG.Skills
         .secondSkills
         .stream()
         .map(id -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(id)))
         .filter(Objects::nonNull)
         .toList();
   }

   public boolean clickMenuButton(Player player, int pId) {
      List<ManasRace> pool = this.getRacePool();
      if (pId >= 0 && pId <= pool.size()) {
         this.selectedManasRaceIndex.set(pId);
         return true;
      }

      if ((pId != -1 || this.changeRaceOnly) && pId != -2) {
         return super.clickMenuButton(player, pId);
      }

      int index = this.selectedManasRaceIndex.get();
      ManasRace race;
      if (index < pool.size() && index >= 0) {
         race = pool.get(index);
      } else {
         List<ManasRace> randomRaces = this.getRandomRacePool();
         race = randomRaces.get(player.getRandom().nextInt(randomRaces.size()));
      }

      if (!player.level().isClientSide()) {
         TensuraStorages.getPlayerDataFrom(player).setInResetProgress(0);
         setRace(player, race, true, pId != -2 && !player.level().getGameRules().getBoolean(TensuraGameRules.SKILL_BEFORE_RACE));
         if (this.customMaxAP > 0.0F) {
            EnergyHelper.setMaxAura(player, this.customMaxAP);
         }

         if (this.customMaxMP > 0.0F) {
            EnergyHelper.setMaxMagicule(player, this.customMaxMP);
         }

         if (player.level().getGameRules().getBoolean(TensuraGameRules.NO_UNIQUE_START)) {
            double cost = 10000 * CONFIG.Skills.skillNumber * player.level().getGameRules().getInt(TensuraGameRules.MP_SKILL_COST) / 100.0;
            EnergyHelper.increaseMaxEP(player, cost);
         }
      }

      return true;
   }

   public static void setRace(Player player, ManasRace race, boolean resetEP, boolean grantUnique) {
      Races races = RaceAPI.getRaceFrom(player);
      ManasRaceInstance instance = race.createDefaultInstance();
      races.setRace(
         instance,
         false,
         true,
         Component.translatable("tensura.command.race.edit", new Object[]{player.getName(), race.getName()}).withStyle(ChatFormatting.GOLD)
      );
      if (resetEP && race instanceof TensuraRace tensuraRace) {
         tensuraRace.resetExistenceData(player);
      }

      races.markDirty();
      ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
      playerData.setTrackedEvolution(null);
      playerData.markDirty();
      IExistence existence = TensuraStorages.getExistenceFrom(player);
      existence.setSpiritualForm(instance.is(TensuraRaceTags.SPAWN_AS_SPIRITUAL));
      applySpiritBlessingChance(player, existence);
      existence.markDirty();
      if (grantUnique) {
         grantUniqueSkill(player);
      }

      player.setInvulnerable(false);
      if (player instanceof ServerPlayer serverPlayer) {
         ((PlayerTrigger)TensuraCriteriaTriggers.REINCARNATED.get()).trigger(serverPlayer);
      }
   }

   public static void grantUniqueSkill(Player player) {
      if (player.level().getGameRules().getBoolean(TensuraGameRules.NO_UNIQUE_START)) {
         double cost = 10000 * CONFIG.Skills.skillNumber * player.level().getGameRules().getInt(TensuraGameRules.MP_SKILL_COST) / 100.0;
         EnergyHelper.increaseMaxEP(player, cost);
      } else {
         randomUniqueSkill(player, true);
      }
   }

   public static void randomUniqueSkill(Player player, boolean coverEP) {
      if (player.level() instanceof ServerLevel level) {
         List<ManasSkill> collection = new ArrayList<>(getReincarnationSkills(getSkillPool(), level, false, player));
         if (!collection.isEmpty()) {
            List<ManasSkill> secondCollection = new ArrayList<>(getReincarnationSkills(getSecondSkillPool(), level, false, player));
            ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
            int counter = playerData.getResetCounter();
            int locked = 0;
            if (playerData.getBonusSkillLock() > 0 || level.getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK) > 0) {
               playerData.limitLockedSkills(player.level());

               for (ResourceLocation lockedSkill : playerData.getLockedSkills()) {
                  ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(lockedSkill);
                  if (skill != null && collection.contains(skill) && grantedUniqueSkill(player, level, skill, coverEP)) {
                     collection.remove(skill);
                     secondCollection.remove(skill);
                     locked++;
                  }
               }
            }

            int skills = CONFIG.Skills.skillNumber - locked;
            int counterGamerule = level.getGameRules().getInt(TensuraGameRules.RESET_COUNTER_BONUS_UNIQUE);
            int bonusReset = counterGamerule <= 0 ? 0 : Math.min(counter / counterGamerule, PLAYER_CONFIG.ResetScroll.maxCounterBonus);
            int total = skills + bonusReset;
            if (total > 0) {
               double secondChance = CONFIG.Skills.additionalUniqueChance;

               for (int i = 1; i <= total && (i < bonusReset + 2 || !(secondChance < 100.0) || !(player.getRandom().nextFloat() * 100.0F > secondChance)); i++) {
                  ManasSkill manasSkill;
                  if (skills == 2 && i == bonusReset + 2 && !secondCollection.isEmpty()) {
                     manasSkill = secondCollection.get(player.getRandom().nextInt(secondCollection.size()));
                  } else {
                     manasSkill = collection.get(player.getRandom().nextInt(collection.size()));
                  }

                  if (grantedUniqueSkill(player, level, manasSkill, coverEP)) {
                     collection.remove(manasSkill);
                     secondCollection.remove(manasSkill);
                  }
               }
            }
         }
      }
   }

   private static boolean grantedUniqueSkill(Player player, ServerLevel level, ManasSkill skill, boolean coverEP) {
      TensuraSkillInstance instance = new TensuraSkillInstance(skill);
      if (coverEP) {
         instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
      }

      if (SkillHelper.learnSkill(player, instance)) {
         TensuraStorages.getUniqueStorageFrom(level.getServer().overworld()).addSkill(skill.getRegistryName(), player.getUUID());
         return true;
      } else {
         return false;
      }
   }

   public static List<ManasSkill> getReincarnationSkills(List<ManasSkill> pool, ServerLevel level, boolean ignoreGameRule, Player roller) {
      return pool.stream().filter(skill -> {
         if (!ignoreGameRule) {
            if (!level.getGameRules().getBoolean(TensuraGameRules.TRULY_UNIQUE)) {
               return true;
            }

            if (TensuraStorages.getUniqueStorageFrom(level.getServer().overworld()).hasSkill(skill.getRegistryName())) {
               return false;
            }
         }

         return !SkillUtils.hasSkillFully(roller, skill);
      }).toList();
   }

   public static void applySpiritBlessingChance(Player player, IExistence existence) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (!optional.isPresent() || !(optional.get().getRace() instanceof TensuraRace race && !race.getAlignment().isCanBecomeHero())) {
         if (player.getRandom().nextFloat() * 100.0F <= TensuraRace.BASE_CONFIG.Spirit.blessedPercentage) {
            existence.setBlessed(true);
         }
      }
   }

   public static void reincarnateAsRimuru(ServerPlayer player) {
      setRace(player, (ManasRace)TensuraRaces.SLIME.get(), true, false);
      if (!player.level().isClientSide()) {
         for (ManasSkill skill : RIMURU_SKILLS) {
            TensuraSkillInstance instance = new TensuraSkillInstance(skill);
            instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
            if (SkillHelper.learnSkill(player, instance) && !RIMURU_UNIQUE_SKILLS.contains(skill)) {
               Races races = RaceAPI.getRaceFrom(player);
               Optional<ManasRaceInstance> optional = races.getRace();
               if (optional.isPresent()) {
                  optional.get().addIntrinsicSkill(skill);
                  optional.get().markDirty();
                  RaceAPI.getRaceFrom(player).markDirty();
               }
            }
         }
      }
   }

   public static void grantLearningResistance(LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         Skills storage = SkillAPI.getSkillsFrom(entity);

         for (String id : CONFIG.Skills.learnableResistances) {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(id));
            if (skill != null && !storage.getSkill(skill).isPresent()) {
               int learning;
               if (skill instanceof ResistSkill resist) {
                  learning = resist.getLearningPointRequirement();
               } else {
                  learning = TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement;
               }

               ManasSkillInstance instance = skill.createDefaultInstance();
               instance.setMastery(learning * -1);
               SkillHelper.learnSkill(entity, instance, -1, null);
            }
         }
      }
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      return ItemStack.EMPTY;
   }

   public static boolean checkForFirstLogin(ServerPlayer player) {
      if (player.isSpectator()) {
         return true;
      }

      Races races = RaceAPI.getRaceFrom(player);
      if (races.getRace().isEmpty()) {
         if (player.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE)) {
            reincarnateAsRimuru(player);
         } else {
            if (player.level().getGameRules().getBoolean(TensuraGameRules.SKILL_BEFORE_RACE)) {
               races.setRace((ManasRace)TensuraRaces.HUMAN.get(), false, null);
               if (!player.level().getGameRules().getBoolean(TensuraGameRules.NO_UNIQUE_START)) {
                  grantUniqueSkill(player);
               }
            }

            player.setInvulnerable(true);
            TensuraStorages.getPlayerDataFrom(player).setInResetProgress(1);
            MenuRegistry.openExtendedMenu(player, new SimpleMenuProvider(ReincarnationMenu::new, Component.translatable("tensura.reincarnation")), buf -> {
               buf.writeBoolean(false);
               buf.writeInt(0);
               buf.writeFloat(0.0F);
               buf.writeFloat(0.0F);
            });
         }

         grantLearningResistance(player);
         return true;
      } else {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         if (data.getInResetProgress() != 0) {
            player.setInvulnerable(true);
            MenuRegistry.openExtendedMenu(player, new SimpleMenuProvider(ReincarnationMenu::new, Component.translatable("tensura.reincarnation")), buf -> {
               buf.writeBoolean(data.getInResetProgress() != 1);
               buf.writeInt(0);
               buf.writeFloat(0.0F);
               buf.writeFloat(0.0F);
            });
            return true;
         } else {
            return false;
         }
      }
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public boolean isChangeRaceOnly() {
      return this.changeRaceOnly;
   }

   @Generated
   public float getCustomMaxAP() {
      return this.customMaxAP;
   }

   @Generated
   public float getCustomMaxMP() {
      return this.customMaxMP;
   }
}
