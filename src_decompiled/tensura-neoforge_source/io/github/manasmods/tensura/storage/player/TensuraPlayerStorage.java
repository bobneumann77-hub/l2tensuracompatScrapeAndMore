package io.github.manasmods.tensura.storage.player;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.PlayerJoin;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TensuraPlayerStorage extends Storage implements ITensuraPlayer {
   @Generated
   private static final Logger log = LogManager.getLogger(TensuraPlayerStorage.class);
   private static StorageKey<TensuraPlayerStorage> key = null;
   private int analysisMode;
   private int presenceSenseMode;
   private int dodgeCooldown;
   private int dodgeInvulnerability;
   private int maxWarpPoints = 1;
   private int resetCounter;
   private int bonusSkillLock;
   private int inResetProgress;
   private boolean forcedThirdPerson;
   private String previouslyInBossFight = null;
   @Nullable
   private ManasRace trackedEvolution;
   private final Map<ResourceLocation, Double> reputations = new HashMap<>();
   private final List<ResourceLocation> knownSchematics = new ArrayList<>();
   private final List<ResourceLocation> lockedSkills = new ArrayList<>();
   private final List<WarpPoint> warpPoints = new ArrayList<>();
   private final List<WarpPoint> warpPads = new ArrayList<>();

   public static void init() {
      StorageEvents.REGISTER_ENTITY_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "player_storage"),
               TensuraPlayerStorage.class,
               Player.class::isInstance,
               target -> new TensuraPlayerStorage((LivingEntity)target)
            )
         );
      PlayerEvent.PLAYER_JOIN.register((PlayerJoin)player -> {
         if (!player.level().isClientSide()) {
            if (!player.onGround() && !player.isInLiquid() && !player.isCreative() && !player.isSpectator() && SkillUtils.canFlyLegit(player)) {
               player.getAbilities().mayfly = true;
               player.getAbilities().flying = true;
               player.onUpdateAbilities();
            }

            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            if (data.getPreviouslyInBossFight() != null) {
               IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(player.serverLevel().getServer().overworld());
               BossFightInstance instance = bossFightHolder.getBossFight(data.getPreviouslyInBossFight());
               if (instance != null) {
                  if (instance.getJoinedPlayers().size() < instance.getMaxPlayer() - 1 && instance.isStarted()) {
                     double distance = instance.getRadius();
                     ServerLevel level = player.serverLevel().getServer().getLevel(instance.getDimension());
                     if (player.level() == level && player.isAlive() && instance.getCenter().distToCenterSqr(player.position()) <= distance * distance) {
                        instance.joinBossFight(player, level, data.getPreviouslyInBossFight(), false);
                     }
                  } else {
                     ServerLevel level = player.serverLevel().getServer().getLevel(instance.getDimension());
                     if (level != null) {
                        instance.leaveBossFight(player, level);
                     }

                     if (instance.getForceExitHandler() != null) {
                        instance.getForceExitHandler().apply(player, 0.0);
                     }

                     player.displayClientMessage(Component.translatable("tensura.boss_fight.ended").withStyle(ChatFormatting.RED), true);
                     instance.resetGameMode(player);
                     data.setPreviouslyInBossFight(null);
                     data.markDirty();
                  }
               }
            }
         }
      });
   }

   protected TensuraPlayerStorage(LivingEntity holder) {
      super(holder);
   }

   public void save(CompoundTag data) {
      data.putInt("resetCounter", this.resetCounter);
      data.putInt("bonusSkillLock", this.bonusSkillLock);
      data.putInt("analysisMode", this.analysisMode);
      data.putInt("presenceSenseMode", this.presenceSenseMode);
      data.putInt("dodgeCooldown", this.dodgeCooldown);
      data.putInt("dodgeInvulnerability", this.dodgeInvulnerability);
      data.putInt("maxWarpPoints", this.maxWarpPoints);
      data.putInt("inResetProgress", this.inResetProgress);
      data.putBoolean("forcedThirdPerson", this.forcedThirdPerson);
      if (this.previouslyInBossFight != null) {
         data.putString("previouslyInBossFight", this.previouslyInBossFight);
      }

      if (this.trackedEvolution == null) {
         data.remove("trackedEvolution");
      } else {
         data.putString("trackedEvolution", this.trackedEvolution.getRegistryName().toString());
      }

      CompoundTag warpPoints = new CompoundTag();

      for (int i = 0; i < this.warpPoints.size(); i++) {
         warpPoints.put(String.valueOf(i), this.warpPoints.get(i).serialize(this.getOwner().registryAccess()));
      }

      data.put("warpPoints", warpPoints);
      CompoundTag warpPads = new CompoundTag();

      for (int i = 0; i < this.warpPads.size(); i++) {
         warpPads.put(String.valueOf(i), this.warpPads.get(i).serialize(this.getOwner().registryAccess()));
      }

      data.put("warpPads", warpPads);
      CompoundTag schematics = new CompoundTag();

      for (int i = 0; i < this.knownSchematics.size(); i++) {
         schematics.putString(String.valueOf(i), this.knownSchematics.get(i).toString());
      }

      data.put("schematics", schematics);
      CompoundTag lockedSkills = new CompoundTag();

      for (int i = 0; i < this.lockedSkills.size(); i++) {
         lockedSkills.putString(String.valueOf(i), this.lockedSkills.get(i).toString());
      }

      data.put("lockedSkills", lockedSkills);
      CompoundTag reputation = new CompoundTag();

      for (Entry<ResourceLocation, Double> entry : this.reputations.entrySet()) {
         reputation.putDouble(entry.getKey().toString(), entry.getValue());
      }

      data.put("reputations", reputation);
   }

   public void load(CompoundTag data) {
      this.resetCounter = data.getInt("resetCounter");
      this.bonusSkillLock = data.getInt("bonusSkillLock");
      this.analysisMode = data.getInt("analysisMode");
      this.presenceSenseMode = data.getInt("presenceSenseMode");
      this.dodgeCooldown = data.getInt("dodgeCooldown");
      this.dodgeInvulnerability = data.getInt("dodgeInvulnerability");
      this.maxWarpPoints = data.getInt("maxWarpPoints");
      this.inResetProgress = data.getInt("inResetProgress");
      this.forcedThirdPerson = data.getBoolean("forcedThirdPerson");
      if (data.contains("previouslyInBossFight")) {
         this.previouslyInBossFight = data.getString("previouslyInBossFight");
      }

      if (data.contains("trackedEvolution", 8)) {
         this.trackedEvolution = (ManasRace)RaceAPI.getRaceRegistry().get(ResourceLocation.parse(data.getString("trackedEvolution")));
      } else {
         this.trackedEvolution = null;
      }

      this.warpPoints.clear();
      CompoundTag warpLists = data.getCompound("warpPoints");

      for (int i = 0; warpLists.contains(String.valueOf(i), 10); i++) {
         this.warpPoints.add(WarpPoint.fromNBT(warpLists.getCompound(String.valueOf(i)), this.getOwner().registryAccess()));
      }

      this.warpPads.clear();
      CompoundTag padLists = data.getCompound("warpPads");

      for (int i = 0; padLists.contains(String.valueOf(i), 10); i++) {
         this.warpPads.add(WarpPoint.fromNBT(padLists.getCompound(String.valueOf(i)), this.getOwner().registryAccess()));
      }

      this.knownSchematics.clear();
      if (data.contains("schematics")) {
         CompoundTag nbt = data.getCompound("schematics");
         nbt.getAllKeys().forEach(s -> this.knownSchematics.add(ResourceLocation.tryParse(nbt.getString(s))));
      }

      this.lockedSkills.clear();
      if (data.contains("lockedSkills")) {
         CompoundTag nbt = data.getCompound("lockedSkills");
         nbt.getAllKeys().forEach(s -> this.lockedSkills.add(ResourceLocation.tryParse(nbt.getString(s))));
      }

      this.reputations.clear();
      if (data.contains("reputations")) {
         CompoundTag nbt = data.getCompound("reputations");
         nbt.getAllKeys().forEach(s -> this.reputations.put(ResourceLocation.tryParse(s), nbt.getDouble(s)));
      }
   }

   protected LivingEntity getOwner() {
      return (LivingEntity)this.holder;
   }

   @Override
   public void addWarpPoint(String name, double x, double y, double z, ResourceKey<Level> dimension) {
      this.warpPoints.add(new WarpPoint(name, x, y, z, dimension));
   }

   @Override
   public void addWarpPoint(String name, double x, double y, double z) {
      this.warpPoints.add(new WarpPoint(name, x, y, z));
   }

   @Override
   public void addWarpPoint(double x, double y, double z) {
      this.warpPoints.add(new WarpPoint(x, y, z));
   }

   @Override
   public void removeWarpPoint(int index) {
      if (index >= 0 && index < this.warpPoints.size()) {
         this.warpPoints.remove(index);
      }
   }

   @Override
   public void removeWarpPoint(WarpPoint warpPoint) {
      this.warpPoints.remove(warpPoint);
   }

   @Override
   public void removeWarpPointOverMax(int number) {
      if (this.getMaxWarpPoints() <= 1) {
         this.clearWarpPoints();
      } else {
         while (number > 0 && this.warpPoints.size() > this.getMaxWarpPoints()) {
            this.warpPoints.removeFirst();
            number--;
         }
      }
   }

   @Override
   public void clearWarpPoints() {
      this.warpPoints.clear();
   }

   @Override
   public void addWarpPad(WarpPoint warpPoint) {
      this.warpPads.add(warpPoint);
   }

   @Override
   public void addWarpPad(String name, double x, double y, double z, ResourceKey<Level> dimension) {
      this.warpPads.add(new WarpPoint(name, x, y, z, dimension));
   }

   @Override
   public void removeWarpPad(int index) {
      if (index >= 0 && index < this.warpPads.size()) {
         this.warpPads.remove(index);
      }
   }

   @Override
   public void removeWarpPad(WarpPoint warpPoint) {
      this.warpPads.remove(warpPoint);
   }

   @Override
   public void clearWarpPads() {
      this.warpPads.clear();
   }

   @Override
   public boolean hasSchematic(ResourceLocation schematic) {
      return this.knownSchematics.contains(schematic);
   }

   @Override
   public boolean hasSchematics(List<ResourceLocation> requiredSchematics) {
      return new HashSet<>(this.knownSchematics).containsAll(requiredSchematics);
   }

   @Override
   public void unlockSchematic(ResourceLocation schematicId) {
      this.knownSchematics.add(schematicId);
   }

   @Override
   public void clearSchematics() {
      this.knownSchematics.clear();
   }

   @Override
   public void addLockedSkill(ResourceLocation skill) {
      this.lockedSkills.add(skill);
   }

   @Override
   public void removeLockedSkill(ResourceLocation skill) {
      this.lockedSkills.remove(skill);
   }

   @Override
   public void limitLockedSkills(Level level) {
      int per = level.getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
      if (this.getBonusSkillLock() <= 0 && per <= 0) {
         this.clearLockedSkills();
      } else {
         int count = this.getBonusSkillLock() + this.getResetCounter() / per;
         List<ResourceLocation> lockedSkills = this.getLockedSkills();
         if (lockedSkills.size() > count) {
            for (int i = 0; i < lockedSkills.size() - count; i++) {
               lockedSkills.removeLast();
            }
         }
      }
   }

   @Override
   public void clearLockedSkills() {
      this.lockedSkills.clear();
   }

   @Override
   public double getReputation(ResourceLocation location) {
      return this.reputations.getOrDefault(location, 0.0);
   }

   @Override
   public void setReputation(ResourceLocation location, double reputation) {
      this.reputations.put(location, reputation);
   }

   @Override
   public void resetReputation(ResourceLocation location) {
      this.reputations.remove(location);
   }

   @Override
   public void clearReputation() {
      this.reputations.clear();
   }

   @Generated
   public static StorageKey<TensuraPlayerStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public int getAnalysisMode() {
      return this.analysisMode;
   }

   @Generated
   @Override
   public int getPresenceSenseMode() {
      return this.presenceSenseMode;
   }

   @Generated
   @Override
   public int getDodgeCooldown() {
      return this.dodgeCooldown;
   }

   @Generated
   @Override
   public int getDodgeInvulnerability() {
      return this.dodgeInvulnerability;
   }

   @Generated
   @Override
   public void setAnalysisMode(int analysisMode) {
      this.analysisMode = analysisMode;
   }

   @Generated
   @Override
   public void setPresenceSenseMode(int presenceSenseMode) {
      this.presenceSenseMode = presenceSenseMode;
   }

   @Generated
   @Override
   public void setDodgeCooldown(int dodgeCooldown) {
      this.dodgeCooldown = dodgeCooldown;
   }

   @Generated
   @Override
   public void setDodgeInvulnerability(int dodgeInvulnerability) {
      this.dodgeInvulnerability = dodgeInvulnerability;
   }

   @Generated
   @Override
   public int getMaxWarpPoints() {
      return this.maxWarpPoints;
   }

   @Generated
   @Override
   public int getResetCounter() {
      return this.resetCounter;
   }

   @Generated
   @Override
   public int getBonusSkillLock() {
      return this.bonusSkillLock;
   }

   @Generated
   @Override
   public int getInResetProgress() {
      return this.inResetProgress;
   }

   @Generated
   @Override
   public void setMaxWarpPoints(int maxWarpPoints) {
      this.maxWarpPoints = maxWarpPoints;
   }

   @Generated
   @Override
   public void setResetCounter(int resetCounter) {
      this.resetCounter = resetCounter;
   }

   @Generated
   @Override
   public void setBonusSkillLock(int bonusSkillLock) {
      this.bonusSkillLock = bonusSkillLock;
   }

   @Generated
   @Override
   public void setInResetProgress(int inResetProgress) {
      this.inResetProgress = inResetProgress;
   }

   @Generated
   @Override
   public boolean isForcedThirdPerson() {
      return this.forcedThirdPerson;
   }

   @Generated
   @Override
   public void setForcedThirdPerson(boolean forcedThirdPerson) {
      this.forcedThirdPerson = forcedThirdPerson;
   }

   @Generated
   @Override
   public String getPreviouslyInBossFight() {
      return this.previouslyInBossFight;
   }

   @Generated
   @Override
   public void setPreviouslyInBossFight(String previouslyInBossFight) {
      this.previouslyInBossFight = previouslyInBossFight;
   }

   @Nullable
   @Generated
   @Override
   public ManasRace getTrackedEvolution() {
      return this.trackedEvolution;
   }

   @Generated
   @Override
   public void setTrackedEvolution(@Nullable ManasRace trackedEvolution) {
      this.trackedEvolution = trackedEvolution;
   }

   @Generated
   @Override
   public Map<ResourceLocation, Double> getReputations() {
      return this.reputations;
   }

   @Generated
   @Override
   public List<ResourceLocation> getKnownSchematics() {
      return this.knownSchematics;
   }

   @Generated
   @Override
   public List<ResourceLocation> getLockedSkills() {
      return this.lockedSkills;
   }

   @Generated
   @Override
   public List<WarpPoint> getWarpPoints() {
      return this.warpPoints;
   }

   @Generated
   @Override
   public List<WarpPoint> getWarpPads() {
      return this.warpPads;
   }

   public enum PresenceSenseMode {
      ALL_ENTITIES(0),
      HOSTILE_ONLY(1),
      TRAPS(2),
      TREASURES(3);

      private final int id;

      PresenceSenseMode(int i) {
         this.id = i;
      }

      @Generated
      public int getId() {
         return this.id;
      }
   }
}
