package io.github.manasmods.tensura.storage.restriction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.manasmods.tensura.storage.restriction.template.WorldRestrictionInstance;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldRestrictionConfigManager {
   private static final Logger LOGGER = LoggerFactory.getLogger(WorldRestrictionConfigManager.class);
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Path configDirectory;
   private final Map<String, WorldRestrictionInstance> restrictions = new HashMap<>();

   public WorldRestrictionConfigManager(MinecraftServer server) {
      this.configDirectory = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("world_restrictions");
   }

   public void loadRestrictions() {
      this.restrictions.clear();

      try {
         Files.createDirectories(this.configDirectory);

         try (Stream<Path> paths = Files.walk(this.configDirectory, 1)) {
            paths.filter(x$0 -> Files.isRegularFile(x$0)).filter(path -> path.toString().endsWith(".json")).forEach(this::loadRestriction);
         }

         LOGGER.info("Loaded {} world restriction configurations", this.restrictions.size());
      } catch (IOException e) {
         LOGGER.error("Failed to load world restriction configurations", e);
      }
   }

   private void loadRestriction(Path path) {
      try (Reader reader = Files.newBufferedReader(path)) {
         JsonObject json = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         WorldRestrictionInstance instance = WorldRestrictionInstance.fromJson(json);
         String name = path.getFileName().toString().replace(".json", "");
         this.restrictions.put(name, instance);
         LOGGER.info("Loaded world restriction: {}", name);
      } catch (Exception e) {
         LOGGER.error("Failed to load world restriction from {}", path, e);
      }
   }

   public void saveRestriction(String name, WorldRestrictionInstance instance) {
      try {
         Path filePath = this.configDirectory.resolve(name + ".json");
         instance.saveToFile(filePath);
         this.restrictions.put(name, instance);
         LOGGER.info("Saved world restriction: {}", name);
      } catch (IOException e) {
         LOGGER.error("Failed to save world restriction {}", name, e);
      }
   }

   public void reloadRestrictions() {
      LOGGER.info("Reloading world restriction configurations...");
      this.loadRestrictions();
   }

   public WorldRestrictionInstance getRestriction(String name) {
      return this.restrictions.get(name);
   }

   public Map<String, WorldRestrictionInstance> getAllRestrictions() {
      return new HashMap<>(this.restrictions);
   }

   public boolean hasRestriction(String name) {
      return this.restrictions.containsKey(name);
   }

   public boolean deleteRestriction(String name) {
      try {
         Path filePath = this.configDirectory.resolve(name + ".json");
         if (Files.deleteIfExists(filePath)) {
            this.restrictions.remove(name);
            LOGGER.info("Deleted world restriction: {}", name);
            return true;
         }
      } catch (IOException e) {
         LOGGER.error("Failed to delete world restriction {}", name, e);
      }

      return false;
   }
}
