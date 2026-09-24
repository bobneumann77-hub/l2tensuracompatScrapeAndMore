package io.github.manasmods.tensura.storage.boss;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
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

public class BossFightConfigManager {
   private static final Logger LOGGER = LoggerFactory.getLogger(BossFightConfigManager.class);
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Path configDirectory;
   private final Map<String, BossFightInstance> bossFights = new HashMap<>();

   public BossFightConfigManager(MinecraftServer server) {
      this.configDirectory = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("boss_fights");
   }

   public void loadBossFights() {
      this.bossFights.clear();

      try {
         Files.createDirectories(this.configDirectory);

         try (Stream<Path> paths = Files.walk(this.configDirectory, 1)) {
            paths.filter(x$0 -> Files.isRegularFile(x$0)).filter(path -> path.toString().endsWith(".json")).forEach(this::loadBossFight);
         }

         LOGGER.info("Loaded {} boss fight configurations", this.bossFights.size());
      } catch (IOException e) {
         LOGGER.error("Failed to load boss fight configurations", e);
      }
   }

   private void loadBossFight(Path path) {
      try (Reader reader = Files.newBufferedReader(path)) {
         JsonObject json = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         BossFightInstance instance = BossFightInstance.fromJson(json);
         String name = path.getFileName().toString().replace(".json", "");
         this.bossFights.put(name, instance);
         LOGGER.info("Loaded boss fight: {}", name);
      } catch (Exception e) {
         LOGGER.error("Failed to load boss fight from {}", path, e);
      }
   }

   public void saveBossFight(String name, BossFightInstance instance) {
      try {
         Path filePath = this.configDirectory.resolve(name + ".json");
         instance.saveToFile(filePath);
         this.bossFights.put(name, instance);
         LOGGER.info("Saved boss fight: {}", name);
      } catch (IOException e) {
         LOGGER.error("Failed to save boss fight {}", name, e);
      }
   }

   public void reloadBossFights() {
      LOGGER.info("Reloading boss fight configurations...");
      this.loadBossFights();
   }

   public BossFightInstance getBossFight(String name) {
      return this.bossFights.get(name);
   }

   public Map<String, BossFightInstance> getAllBossFights() {
      return new HashMap<>(this.bossFights);
   }

   public boolean hasBossFight(String name) {
      return this.bossFights.containsKey(name);
   }

   public boolean deleteBossFight(String name) {
      try {
         Path filePath = this.configDirectory.resolve(name + ".json");
         if (Files.deleteIfExists(filePath)) {
            this.bossFights.remove(name);
            LOGGER.info("Deleted boss fight: {}", name);
            return true;
         }
      } catch (IOException e) {
         LOGGER.error("Failed to delete boss fight {}", name, e);
      }

      return false;
   }
}
