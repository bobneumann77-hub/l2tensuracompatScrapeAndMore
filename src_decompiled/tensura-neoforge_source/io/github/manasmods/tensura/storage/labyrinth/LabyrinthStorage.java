package io.github.manasmods.tensura.storage.labyrinth;

import com.google.common.base.Stopwatch;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageHolder;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura.util.MenuHelper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LabyrinthStorage extends Storage implements ILabyrinth {
   private static StorageKey<LabyrinthStorage> key = null;
   private static final Logger LOG = Tensura.createLogger(LabyrinthStorage.class);
   private static final List<LabyrinthStorage.LabyrinthBlockData> BLOCKS = new ArrayList<>();
   private static final HashMap<String, Block> CACHED_BLOCKS = new HashMap<>();
   private static volatile Thread GENERATION_THREAD;
   private static volatile Thread CLEARING_THREAD;
   private static volatile boolean IS_GENERATING;
   private static volatile boolean IS_CLEARING;
   private static CompletableFuture<Void> CLEARING_FUTURE;
   private Vec3 entrancePos = new Vec3(16.0, 1.0, 12.0);
   private Vec3 voidSavePos;
   private Vec3 voidSavePosPassed;
   private Vec3 colossusPos = new Vec3(16.0, 38.0, 537.0);
   private Vec3 passedEntrancePos = new Vec3(16.0, 37.0, 610.0);
   private double areaRadius;
   private double voidHeight;
   private boolean loaded;
   private boolean colossusFirstSpawn;
   private boolean colossusSpawned;
   private int lastPlacedBlockIndex;

   public static void init() {
      StorageEvents.REGISTER_WORLD_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth_storage"),
               LabyrinthStorage.class,
               level -> level.dimension() != null && level.dimension().equals(TensuraDimensions.LABYRINTH),
               LabyrinthStorage::new
            )
         );
   }

   protected LabyrinthStorage(StorageHolder holder) {
      super(holder);
      this.voidSavePos = new Vec3(16.0, 38.0, 313.5);
      this.voidSavePosPassed = new Vec3(16.0, 37.0, 610.0);
      this.voidHeight = -6.0;
      this.areaRadius = 40.0;
      this.loaded = false;
      this.colossusSpawned = false;
      this.lastPlacedBlockIndex = 0;
   }

   protected Level getOwner() {
      return (Level)this.holder;
   }

   public void save(CompoundTag tag) {
      tag.putBoolean("loaded", this.loaded);
      tag.putBoolean("colossusFirstSpawn", this.colossusFirstSpawn);
      tag.putBoolean("colossusSpawned", this.colossusSpawned);
      tag.putDouble("areaRadius", this.areaRadius);
      tag.putDouble("entranceX", this.entrancePos.x);
      tag.putDouble("entranceY", this.entrancePos.y);
      tag.putDouble("entranceZ", this.entrancePos.z);
      tag.putDouble("colossusX", this.colossusPos.x);
      tag.putDouble("colossusY", this.colossusPos.y);
      tag.putDouble("colossusZ", this.colossusPos.z);
      tag.putDouble("passedEntranceX", this.passedEntrancePos.x);
      tag.putDouble("passedEntranceY", this.passedEntrancePos.y);
      tag.putDouble("passedEntranceZ", this.passedEntrancePos.z);
      tag.putDouble("voidSaveX", this.voidSavePos.x);
      tag.putDouble("voidSaveY", this.voidSavePos.y);
      tag.putDouble("voidSaveZ", this.voidSavePos.z);
      tag.putDouble("voidSavePosPassedX", this.voidSavePosPassed.x);
      tag.putDouble("voidSavePosPassedY", this.voidSavePosPassed.y);
      tag.putDouble("voidSavePosPassedZ", this.voidSavePosPassed.z);
      tag.putDouble("voidHeight", this.voidHeight);
      tag.putInt("lastPlacedBlockIndex", this.lastPlacedBlockIndex);
   }

   public void load(CompoundTag tag) {
      this.loaded = tag.getBoolean("loaded");
      this.colossusFirstSpawn = tag.getBoolean("colossusFirstSpawn");
      this.colossusSpawned = tag.getBoolean("colossusSpawned");
      if (tag.contains("areaRadius")) {
         this.areaRadius = tag.getDouble("areaRadius");
      } else {
         this.areaRadius = 40.0;
      }

      this.entrancePos = new Vec3(tag.getDouble("entranceX"), tag.getDouble("entranceY"), tag.getDouble("entranceZ"));
      this.colossusPos = new Vec3(tag.getDouble("colossusX"), tag.getDouble("colossusY"), tag.getDouble("colossusZ"));
      this.passedEntrancePos = new Vec3(tag.getDouble("passedEntranceX"), tag.getDouble("passedEntranceY"), tag.getDouble("passedEntranceZ"));
      if (tag.contains("voidSaveX")) {
         this.voidSavePos = new Vec3(tag.getDouble("voidSaveX"), tag.getDouble("voidSaveY"), tag.getDouble("voidSaveZ"));
      } else {
         this.voidSavePos = new Vec3(16.0, 38.0, 313.5);
      }

      if (tag.contains("voidSavePosPassedX")) {
         this.voidSavePosPassed = new Vec3(tag.getDouble("voidSavePosPassedX"), tag.getDouble("voidSavePosPassedY"), tag.getDouble("voidSavePosPassedZ"));
      } else {
         this.voidSavePosPassed = new Vec3(16.0, 37.0, 610.0);
      }

      this.voidHeight = tag.getDouble("voidHeight");
      this.lastPlacedBlockIndex = tag.getInt("lastPlacedBlockIndex");
   }

   @Override
   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
      this.markDirty();
   }

   @Override
   public void setColossusFirstSpawned(boolean spawned) {
      this.colossusFirstSpawn = spawned;
      this.markDirty();
   }

   @Override
   public void setColossusSpawned(boolean spawned) {
      this.colossusSpawned = spawned;
      this.markDirty();
   }

   @Override
   public void setEntrancePos(Vec3 pos) {
      this.entrancePos = pos;
      this.markDirty();
   }

   @Override
   public void setColossusPos(Vec3 pos) {
      this.colossusPos = pos;
      this.markDirty();
   }

   @Override
   public void setPassedEntrancePos(Vec3 pos) {
      this.passedEntrancePos = pos;
      this.markDirty();
   }

   @Override
   public void setVoidSavePos(Vec3 pos) {
      this.voidSavePos = pos;
      this.markDirty();
   }

   @Override
   public void setVoidSavePosPassed(Vec3 pos) {
      this.voidSavePosPassed = pos;
      this.markDirty();
   }

   @Override
   public void setVoidHeight(double height) {
      this.voidHeight = height;
      this.markDirty();
   }

   @Override
   public void setAreaRadius(double radius) {
      this.areaRadius = radius;
      this.markDirty();
   }

   @Override
   public void setLastPlacedBlockIndex(int lastPlacedBlockIndex) {
      this.lastPlacedBlockIndex = lastPlacedBlockIndex;
      this.markDirty();
   }

   public static void addPassedEntity(LivingEntity entity, boolean won) {
      if (!entity.level().isClientSide()) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         spirit.setColossusStarted(false);
         spirit.setColossusPassed(true);
         if (won) {
            spirit.setColossusWon(true);
         }

         spirit.markDirty();
      }
   }

   public static void removePassedEntity(LivingEntity entity, boolean addStarted) {
      if (!entity.level().isClientSide()) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         spirit.setColossusPassed(false);
         if (addStarted) {
            spirit.setColossusStarted(true);
         }

         spirit.markDirty();
      }
   }

   public static void removeStartedEntity(LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
         spirit.setColossusStarted(false);
         spirit.markDirty();
      }
   }

   public static boolean isEntityPassedColossus(@Nullable Entity entity) {
      return entity instanceof LivingEntity living ? TensuraStorages.getSpiritFrom(living).isColossusPassed() : false;
   }

   public static boolean generateStructures(@NonNull ServerLevel level, @NonNull ILabyrinth data, boolean clearArea) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      }

      if (data == null) {
         throw new NullPointerException("data is marked non-null but is null");
      }

      if (isCurrentlyGenerating()) {
         LOG.warn("Attempted to generate during an active generation process; skipping");
         return false;
      }

      IS_GENERATING = true;
      if (clearArea) {
         clearArea(level);
      }

      LOG.info(String.format("Generating structures for dimension '%s'", level.dimensionTypeRegistration().getRegisteredName()));
      LOG.info("The server might lag during the process! Pausing the game might greatly accelerate it");
      MinecraftServer server = level.getServer();
      Stopwatch stopwatch = Stopwatch.createStarted();
      populateMaps(server.getResourceManager());
      Runnable runnable = () -> {
         GENERATION_THREAD = Thread.currentThread();

         try {
            int savedIndex = data.getLastPlacedBlockIndex();
            int size = BLOCKS.size();
            BlockPos origin = getOriginPos(savedIndex);
            if (savedIndex > 0) {
               LOG.info(String.format("Placement will begin from index '%d'", savedIndex));
            }

            for (int index = savedIndex; index < size && isCurrentlyGenerating(); index++) {
               switch (index) {
                  case 207852:
                     origin = getOriginPos(index);
                     data.setLastPlacedBlockIndex(index);
                     LOG.info("Placed 20%...");
                     break;
                  case 326197:
                     origin = getOriginPos(index);
                     data.setLastPlacedBlockIndex(index);
                     LOG.info("Placed 40%...");
                     break;
                  case 368985:
                     origin = getOriginPos(index);
                     data.setLastPlacedBlockIndex(index);
                     LOG.info("Placed 60%...");
                     break;
                  case 401091:
                     origin = getOriginPos(index);
                     data.setLastPlacedBlockIndex(index);
                     LOG.info("Placed 80%...");
               }

               LabyrinthStorage.LabyrinthBlockData blockData = BLOCKS.get(index);
               BlockPos lambdaOrigin = origin;
               server.executeIfPossible(() -> level.setBlock(lambdaOrigin.offset(blockData.blockPos), blockData.blockState, 4));
            }

            BLOCKS.clear();
            ((ArrayList)BLOCKS).trimToSize();
            data.setLoaded(true);
            data.setLastPlacedBlockIndex(0);
            LOG.info("Generation took {}", MenuHelper.toHumanString(stopwatch.elapsed()));
         } catch (Throwable e) {
            LOG.error(e.getMessage());
         } finally {
            GENERATION_THREAD = null;
            IS_GENERATING = false;
         }
      };
      if (isCurrentlyClearing()) {
         CLEARING_FUTURE.thenRunAsync(runnable);
      } else {
         CompletableFuture.runAsync(runnable);
      }

      return true;
   }

   public static void populateMaps(ResourceManager manager) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      LOG.info("Processing structure files...");
      BLOCKS.clear();

      try {
         HashMap<String, String> STATES = new HashMap<>();
         HashMap<String, String> LOCATIONS = new HashMap<>();
         ResourceLocation NAMES_LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "structure/labyrinth/dim/ids.ldat");
         ResourceLocation STATES_LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "structure/labyrinth/dim/states.ldat");
         ResourceLocation BLOCKS_LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "structure/labyrinth/dim/blocks.ldat");
         Resource NAMES_RESOURCE = manager.getResourceOrThrow(NAMES_LOCATION);
         Resource STATES_RESOURCE = manager.getResourceOrThrow(STATES_LOCATION);
         Resource BLOCKS_RESOURCE = manager.getResourceOrThrow(BLOCKS_LOCATION);
         LOG.info("Loading block locations...");

         for (String line : NAMES_RESOURCE.openAsReader().lines().toList()) {
            int index = line.indexOf(61);
            String id = line.substring(0, index);
            String name = line.substring(index + 1);
            LOCATIONS.put(id, name);
         }

         LOG.info("Loading block states...");
         String statesFile = new String(STATES_RESOURCE.open().readAllBytes(), StandardCharsets.UTF_8);
         String[] stateLines = statesFile.split(";");

         for (String line : stateLines) {
            if (!line.isEmpty()) {
               int index = line.indexOf(61);
               String id = line.substring(0, index);
               String properties = line.substring(index + 1);
               STATES.put(id, properties);
            }
         }

         LOG.info("Creating block data...");
         String blocksFile = new String(BLOCKS_RESOURCE.open().readAllBytes(), StandardCharsets.UTF_8);
         String[] blockLines = blocksFile.split(";");

         for (String line : blockLines) {
            if (!line.isEmpty()) {
               int index = line.indexOf(61);
               String id = line.substring(0, index);
               int x = Integer.parseInt(line.substring(++index, index + 4));
               index += 5;
               int y = Integer.parseInt(line.substring(index, index + 4));
               index += 5;
               int z = Integer.parseInt(line.substring(index, index + 4));
               index += 5;
               String nameId = line.substring(index);
               String name = LOCATIONS.get(nameId);
               if (name != null) {
                  String properties = STATES.getOrDefault(id, "");
                  LabyrinthStorage.LabyrinthBlockData entry = new LabyrinthStorage.LabyrinthBlockData(x, y, z, name, properties);
                  BLOCKS.add(entry);
               }
            }
         }

         CACHED_BLOCKS.clear();
         LOG.info("Action completed in {}", MenuHelper.toHumanString(stopwatch.elapsed()));
      } catch (IOException e) {
         LOG.error(e.getMessage());
      }
   }

   public static void clearArea(@NonNull ServerLevel level) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      }

      if (!isCurrentlyClearing()) {
         IS_CLEARING = true;
         MinecraftServer server = level.getServer();
         BlockState air = Blocks.AIR.defaultBlockState();
         CLEARING_FUTURE = CompletableFuture.runAsync(() -> {
            CLEARING_THREAD = Thread.currentThread();

            for (int x = -39; x <= 70; x++) {
               for (int y = -35; y <= 208; y++) {
                  for (int z = 0; z <= 737 && isCurrentlyClearing(); z++) {
                     BlockPos pos = new BlockPos(x, y, z);
                     server.executeIfPossible(() -> level.setBlock(pos, air, 4));
                  }
               }
            }

            IS_CLEARING = false;
            CLEARING_FUTURE = null;
            CLEARING_THREAD = null;
         });
      }
   }

   public static BlockPos getOriginPos(int index) {
      BlockPos blockPos = new BlockPos(0, 0, 0);
      if (index < 207852) {
         return blockPos;
      }

      blockPos = blockPos.offset(7, -35, 317);
      if (index < 326197) {
         return blockPos;
      }

      blockPos = blockPos.offset(-32, 71, 179);
      if (index < 368985) {
         return blockPos;
      }

      blockPos = blockPos.offset(32, -71, 82);
      return index < 401091 ? blockPos : blockPos.offset(-46, 0, 50);
   }

   public static boolean isCurrentlyGenerating() {
      return GENERATION_THREAD != null && !GENERATION_THREAD.isInterrupted() || IS_GENERATING;
   }

   public static boolean isCurrentlyClearing() {
      return CLEARING_THREAD != null && !CLEARING_THREAD.isInterrupted() && CLEARING_FUTURE != null || IS_CLEARING;
   }

   @Generated
   public static StorageKey<LabyrinthStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public Vec3 getEntrancePos() {
      return this.entrancePos;
   }

   @Generated
   @Override
   public Vec3 getVoidSavePos() {
      return this.voidSavePos;
   }

   @Generated
   @Override
   public Vec3 getVoidSavePosPassed() {
      return this.voidSavePosPassed;
   }

   @Generated
   @Override
   public Vec3 getColossusPos() {
      return this.colossusPos;
   }

   @Generated
   @Override
   public Vec3 getPassedEntrancePos() {
      return this.passedEntrancePos;
   }

   @Generated
   @Override
   public double getAreaRadius() {
      return this.areaRadius;
   }

   @Generated
   @Override
   public double getVoidHeight() {
      return this.voidHeight;
   }

   @Generated
   @Override
   public boolean isLoaded() {
      return this.loaded;
   }

   @Generated
   @Override
   public boolean isColossusFirstSpawn() {
      return this.colossusFirstSpawn;
   }

   @Generated
   @Override
   public boolean isColossusSpawned() {
      return this.colossusSpawned;
   }

   @Generated
   @Override
   public int getLastPlacedBlockIndex() {
      return this.lastPlacedBlockIndex;
   }

   public static class LabyrinthBlockData {
      public final BlockPos blockPos;
      public BlockState blockState;

      public LabyrinthBlockData(int x, int y, int z, String location, String state) {
         this.blockPos = new BlockPos(x, y, z);
         Block block = LabyrinthStorage.CACHED_BLOCKS.get(location);
         if (block == null) {
            block = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location));
            LabyrinthStorage.CACHED_BLOCKS.put(location, block);
         }

         this.loadProperties(block, block.defaultBlockState(), state);
      }

      public void loadProperties(Block block, BlockState blockState, String state) {
         if (state.isEmpty()) {
            this.blockState = blockState;
         } else {
            if (state.startsWith("\"") && state.endsWith("\"")) {
               state = state.substring(1, state.length() - 1);
            }

            String[] properties = state.split(",");
            StateDefinition<Block, BlockState> definition = block.getStateDefinition();

            for (String property : properties) {
               String[] pair = property.split("=");
               if (pair.length == 2) {
                  String key = pair[0].trim();
                  String value = pair[1].trim();
                  key = key.replace("\"", "");
                  value = value.replace("\"", "");
                  Property<?> prop = definition.getProperty(key);
                  if (prop != null) {
                     Optional<?> parsedValue = prop.getValue(value);
                     if (parsedValue.isPresent()) {
                        blockState = this.setBlockStateProperty(blockState, prop, parsedValue.get());
                     }
                  }
               }
            }

            this.blockState = blockState;
         }
      }

      public <T extends Comparable<T>> BlockState setBlockStateProperty(BlockState state, Property<T> property, Object value) {
         try {
            return (BlockState)state.setValue(property, (Comparable)value);
         } catch (Exception e) {
            LabyrinthStorage.LOG.warn(e.getMessage());
            return state;
         }
      }
   }
}
