package io.github.manasmods.tensura.storage.boss;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.event.events.common.LifecycleEvent.ServerState;
import dev.architectury.event.events.common.TickEvent.Server;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageHolder;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.exit.SpawnPointForceExit;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BossFightStorage extends Storage implements IBossFightHolder {
   @Generated
   private static final Logger log = LogManager.getLogger(BossFightStorage.class);
   private static StorageKey<BossFightStorage> key = null;
   private static BossFightConfigManager configManager = null;
   private boolean loaded;
   private final Map<String, BossFightInstance> bossFights = new HashMap<>();

   public static void init() {
      StorageEvents.REGISTER_WORLD_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "boss_fights"),
               BossFightStorage.class,
               level -> level.dimension() != null && level.dimension().equals(Level.OVERWORLD),
               BossFightStorage::new
            )
         );
      LifecycleEvent.SERVER_STARTED.register((ServerState)server -> {
         configManager = new BossFightConfigManager(server);
         configManager.loadBossFights();
         IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(server.overworld());
         bossFightHolder.reloadFromJson();
         if (!bossFightHolder.isLoaded()) {
            bossFightHolder.addBossFight("GazelDwargoArena", createGazelBossFight());
            bossFightHolder.setLoaded(true);
            log.info("Created Gazel Dwargo Arena boss fight.");
         }

         log.info("Loaded {} boss fight configurations from JSON", configManager.getAllBossFights().size());
      });
      TickEvent.SERVER_POST.register((Server)server -> {
         ServerLevel overworld = server.getLevel(Level.OVERWORLD);
         if (overworld != null) {
            IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
            bossFightHolder.getBossFights().forEach((name, instance) -> instance.tick(server, name));
         }
      });
   }

   protected BossFightStorage(StorageHolder holder) {
      super(holder);
   }

   protected Level getOwner() {
      return (Level)this.holder;
   }

   public void save(CompoundTag tag) {
      ListTag runtimeList = new ListTag();
      this.bossFights.forEach((name, bossFight) -> {
         if (bossFight.hasRuntimeState()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("name", name);
            entry.put("runtime", bossFight.saveRuntimeStateToNBT());
            runtimeList.add(entry);
         }
      });
      tag.put("runtimeStates", runtimeList);
      tag.putBoolean("loaded", this.loaded);
      log.debug("Saved runtime state for {} boss fights", runtimeList.size());
   }

   public void load(CompoundTag tag) {
      this.loaded = tag.getBoolean("loaded");
      this.bossFights.clear();
      if (configManager != null) {
         Map<String, BossFightInstance> jsonConfigs = configManager.getAllBossFights();
         this.bossFights.putAll(jsonConfigs);
         log.info("Loaded {} boss fight configurations from JSON", jsonConfigs.size());
      }

      if (tag.contains("runtimeStates")) {
         ListTag runtimeList = tag.getList("runtimeStates", 10);
         runtimeList.forEach(entry -> {
            CompoundTag compound = (CompoundTag)entry;
            String name = compound.getString("name");
            if (this.bossFights.containsKey(name)) {
               BossFightInstance instance = this.bossFights.get(name);
               instance.loadRuntimeStateFromNBT(compound.getCompound("runtime"));
               log.debug("Restored runtime state for boss fight: {}", name);
            } else {
               log.warn("Found runtime state for unknown boss fight '{}' - skipping", name);
            }
         });
      }
   }

   @Override
   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
      this.markDirty();
   }

   @Override
   public BossFightInstance getBossFight(String name) {
      return !this.bossFights.containsKey(name) ? null : this.bossFights.get(name);
   }

   @Override
   public void addBossFight(String name, BossFightInstance bossFight) {
      if (configManager != null) {
         configManager.saveBossFight(name, bossFight);
         this.bossFights.put(name, bossFight);
         log.info("Saved boss fight '{}' to JSON configuration", name);
      } else {
         log.error("Cannot add boss fight '{}' - config manager not initialized", name);
      }
   }

   @Override
   public void removeBossFight(String name) {
      if (this.bossFights.containsKey(name)) {
         BossFightInstance bossFight = this.bossFights.get(name);
         ServerLevel fightLevel = this.getOwner().getServer().getLevel(bossFight.getDimension());
         if (fightLevel != null) {
            bossFight.stopBossFight(fightLevel, false);
         }

         this.bossFights.remove(name);
         if (configManager != null) {
            configManager.deleteBossFight(name);
         }

         this.markDirty();
      }
   }

   @Override
   public void reloadFromJson() {
      if (configManager == null) {
         log.error("Cannot reload - config manager not initialized");
      } else {
         Map<String, CompoundTag> runtimeStates = new HashMap<>();
         this.bossFights.forEach((name, instance) -> {
            if (instance.hasRuntimeState()) {
               runtimeStates.put(name, instance.saveRuntimeStateToNBT());
            }
         });
         configManager.reloadBossFights();
         this.bossFights.clear();
         this.bossFights.putAll(configManager.getAllBossFights());
         runtimeStates.forEach((name, runtimeState) -> {
            if (this.bossFights.containsKey(name)) {
               this.bossFights.get(name).loadRuntimeStateFromNBT(runtimeState);
               log.debug("Restored runtime state for '{}' after reload", name);
            } else {
               log.warn("Boss fight '{}' removed from config - runtime state discarded", name);
            }
         });
         log.info("Reloaded {} boss fight configurations", this.bossFights.size());
         this.markDirty();
      }
   }

   public static BossFightInstance createGazelBossFight() {
      BossFightInstance instance = new BossFightInstance(new BlockPos(42, 87, 25), TensuraDimensions.BOSS_AREA, 25.0);
      instance.setEntrance(new BlockPos(2, 83, 25));
      instance.setForcedGameMode(GameType.ADVENTURE);
      instance.setTimer(-1);
      instance.setBuiltin(true);
      instance.setBanTeleportation(true);
      instance.setResetBoss(true);
      instance.setBossPosition(new BlockPos(56, 88, 25));
      instance.setBossType(HumanEntityTypes.GAZEL_DWARGO.getId());
      instance.setForceExitHandler(new SpawnPointForceExit());
      instance.setForceExitOnLeave(true);
      instance.setForceExitTimer(300);
      return instance;
   }

   @Generated
   public static StorageKey<BossFightStorage> getKey() {
      return key;
   }

   @Generated
   public static BossFightConfigManager getConfigManager() {
      return configManager;
   }

   @Generated
   @Override
   public boolean isLoaded() {
      return this.loaded;
   }

   @Generated
   @Override
   public Map<String, BossFightInstance> getBossFights() {
      return this.bossFights;
   }
}
