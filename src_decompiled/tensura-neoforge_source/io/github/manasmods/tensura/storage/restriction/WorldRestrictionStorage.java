package io.github.manasmods.tensura.storage.restriction;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent.ServerState;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageHolder;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.restriction.template.IWorldRestriction;
import io.github.manasmods.tensura.storage.restriction.template.WorldRestrictionInstance;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldRestrictionStorage extends Storage implements IWorldRestriction {
   @Generated
   private static final Logger log = LogManager.getLogger(WorldRestrictionStorage.class);
   private static StorageKey<WorldRestrictionStorage> key = null;
   private static WorldRestrictionConfigManager configManager = null;
   private final Map<String, WorldRestrictionInstance> restrictions = new HashMap<>();

   public static void init() {
      StorageEvents.REGISTER_WORLD_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "world_restrictions"),
               WorldRestrictionStorage.class,
               level -> level.dimension() != null && level.dimension().equals(Level.OVERWORLD),
               WorldRestrictionStorage::new
            )
         );
      LifecycleEvent.SERVER_STARTED.register((ServerState)server -> {
         configManager = new WorldRestrictionConfigManager(server);
         configManager.loadRestrictions();
         IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(server.overworld());
         holder.reloadFromJson();
         log.info("Loaded {} world restriction configurations from JSON", configManager.getAllRestrictions().size());
      });
   }

   protected WorldRestrictionStorage(StorageHolder holder) {
      super(holder);
   }

   protected Level getOwner() {
      return (Level)this.holder;
   }

   public void save(CompoundTag tag) {
   }

   public void load(CompoundTag tag) {
      this.restrictions.clear();
      if (configManager != null) {
         Map<String, WorldRestrictionInstance> jsonConfigs = configManager.getAllRestrictions();
         this.restrictions.putAll(jsonConfigs);
         log.info("Loaded {} world restriction configurations from JSON", jsonConfigs.size());
      }
   }

   @Override
   public WorldRestrictionInstance getRestriction(String name) {
      return !this.restrictions.containsKey(name) ? null : this.restrictions.get(name);
   }

   @Override
   public void addRestriction(String name, WorldRestrictionInstance restriction) {
      if (configManager != null) {
         configManager.saveRestriction(name, restriction);
         this.restrictions.put(name, restriction);
         log.info("Saved world restriction '{}' to JSON configuration", name);
      } else {
         log.error("Cannot add world restriction '{}' - config manager not initialized", name);
      }
   }

   @Override
   public void removeRestriction(String name) {
      if (this.restrictions.containsKey(name)) {
         this.restrictions.remove(name);
         if (configManager != null) {
            configManager.deleteRestriction(name);
         }

         this.markDirty();
      }
   }

   @Override
   public void reloadFromJson() {
      if (configManager == null) {
         log.error("Cannot reload - config manager not initialized");
      } else {
         configManager.reloadRestrictions();
         this.restrictions.clear();
         this.restrictions.putAll(configManager.getAllRestrictions());
         log.info("Reloaded {} world restriction configurations", this.restrictions.size());
         this.markDirty();
      }
   }

   @Generated
   public static StorageKey<WorldRestrictionStorage> getKey() {
      return key;
   }

   @Generated
   public static WorldRestrictionConfigManager getConfigManager() {
      return configManager;
   }

   @Generated
   @Override
   public Map<String, WorldRestrictionInstance> getRestrictions() {
      return this.restrictions;
   }
}
