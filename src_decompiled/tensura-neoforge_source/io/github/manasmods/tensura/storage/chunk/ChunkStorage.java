package io.github.manasmods.tensura.storage.chunk;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageHolder;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.config.AreaMagiculeConfig;
import io.github.manasmods.tensura.data.chunk.BlockMagiculeModifier;
import io.github.manasmods.tensura.data.chunk.MagiculeModifier;
import io.github.manasmods.tensura.event.TensuraLevelEvents;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkStorage extends Storage implements IChunk {
   @Generated
   private static final Logger log = LogManager.getLogger(ChunkStorage.class);
   public static final AreaMagiculeConfig CONFIG = (AreaMagiculeConfig)ConfigRegistry.getConfig(AreaMagiculeConfig.class);
   private static StorageKey<ChunkStorage> key = null;
   private double magicule;
   private double maxMagicule;
   private double regenerationRate;
   private long magiculeSeed;
   @Nullable
   private ChunkStorage.MagiculeCacheEntry magiculeCache = null;
   private List<BlockMagiculeModifier> blockModifiers;

   public static void init() {
      StorageEvents.REGISTER_CHUNK_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "chunk_storage"), ChunkStorage.class, levelChunk -> true, ChunkStorage::new
            )
         );
      TensuraLevelEvents.CHUNK_TICK_POST.register((TensuraLevelEvents.ChunkTickEvent)(level, levelChunk) -> TensuraStorages.getChunkFrom(levelChunk).tick());
   }

   public void tick() {
      if (this.isInitialized()) {
         double maxMagicule = this.getMaxMagicule();
         if (this.magicule > maxMagicule) {
            this.magicule = Math.max(maxMagicule, this.magicule - this.getRegenerationRate());
            this.markDirty();
         } else if (this.magicule < maxMagicule) {
            this.magicule = Math.max(maxMagicule, this.magicule + this.getRegenerationRate());
            this.markDirty();
         }

         ChunkStorage.MagiculeCacheEntry entry = this.magiculeCache;
         if (entry != null) {
            entry.tick();
            if (entry.isOutdated()) {
               this.magiculeCache = null;
            }
         }
      }
   }

   protected ChunkStorage(StorageHolder holder) {
      super(holder);
      this.magiculeSeed = -1L;
      this.maxMagicule = CONFIG.baseMagicule;
      this.magicule = this.maxMagicule;
      this.regenerationRate = CONFIG.baseMagiculeRegeneration;
      this.blockModifiers = new ArrayList<>();
   }

   protected LevelChunk getOwner() {
      return (LevelChunk)this.holder;
   }

   public void initialize() {
      if (!this.isInitialized()) {
         this.resetBaseValues();
         this.magiculeSeed = CONFIG.magiculeSeed;
         this.markDirty();
      }
   }

   private boolean isInitialized() {
      return this.getOwner().getLevel().isClientSide() ? true : this.magiculeSeed == CONFIG.magiculeSeed;
   }

   @Override
   public void reinitialize() {
      Tensura.LOG
         .debug(
            "Reinitializing magicule capability for chunk at {},{} in {}",
            new Object[]{this.getOwner().getPos().x, this.getOwner().getPos().z, this.getOwner().getLevel().dimension().location()}
         );
      this.magiculeSeed = -1L;
      this.initialize();
   }

   @Override
   public void markDirty() {
      super.markDirty();
      this.getOwner().setUnsaved(true);
   }

   public void save(CompoundTag tag) {
      tag.putLong("magiculeSeed", this.magiculeSeed);
      tag.putDouble("maxMagicule", this.maxMagicule);
      tag.putDouble("magicule", this.magicule);
      tag.putDouble("regenerationRate", this.regenerationRate);
      if (!this.blockModifiers.isEmpty()) {
         CompoundTag modifiers = new CompoundTag();

         for (int i = 0; i < this.blockModifiers.size(); i++) {
            modifiers.put(String.valueOf(i), this.blockModifiers.get(i).save(new CompoundTag()));
         }

         tag.put("blockModifiers", modifiers);
      }
   }

   public void load(CompoundTag tag) {
      this.magiculeSeed = tag.getLong("magiculeSeed");
      this.maxMagicule = tag.getDouble("maxMagicule");
      this.magicule = tag.getDouble("magicule");
      this.regenerationRate = tag.getDouble("regenerationRate");
      this.blockModifiers.clear();
      if (tag.contains("blockModifiers")) {
         CompoundTag modifiers = tag.getCompound("blockModifiers");
         modifiers.getAllKeys().forEach(s -> {
            Optional<BlockMagiculeModifier> optional = BlockMagiculeModifier.load(modifiers.getCompound(s));
            optional.ifPresent(blockMagiculeModifier -> this.blockModifiers.add(blockMagiculeModifier));
         });
         this.blockModifiers.sort(MagiculeModifier::compareTo);
      }
   }

   @Override
   public double getBaseMaxMagicule() {
      return this.maxMagicule;
   }

   @Override
   public double getMaxMagicule() {
      ChunkStorage.MagiculeCacheEntry entry = this.getOrCreateCacheEntry();
      return entry == null ? this.maxMagicule : entry.getMaxMagicule(this.maxMagicule);
   }

   @Override
   public double getRegenerationRate() {
      ChunkStorage.MagiculeCacheEntry entry = this.getOrCreateCacheEntry();
      return entry == null ? this.regenerationRate : entry.getRegenerationRate(this.regenerationRate);
   }

   @Override
   public void addBlockModifier(BlockMagiculeModifier blockModifier) {
      if (!this.blockModifiers.removeIf(modifier -> modifier.pos().equals(blockModifier.pos()))) {
         this.blockModifiers.add(blockModifier);
         this.blockModifiers.sort(MagiculeModifier::compareTo);
      }
   }

   @Override
   public void addBlockModifier(BlockPos pos, double addition, double distance) {
      this.addBlockModifier(BlockMagiculeModifier.getSimpleMagiculeAddition(pos, addition, distance));
   }

   @Override
   public void removeBlockModifier(BlockMagiculeModifier blockModifier) {
      this.blockModifiers.remove(blockModifier);
   }

   @Override
   public void removeBlockModifier(BlockPos pos) {
      for (int i = this.blockModifiers.size() - 1; i >= 0; i--) {
         if (this.blockModifiers.get(i).pos().equals(pos)) {
            this.blockModifiers.remove(i);
            return;
         }
      }
   }

   @Override
   public void clearBlockModifiers() {
      this.blockModifiers.clear();
   }

   @Override
   public boolean consumeMagicule(double amount) {
      ChunkStorage.MagiculeCacheEntry entry = this.getOrCreateCacheEntry();
      if (entry != null && !(this.magicule < amount)) {
         this.magicule = Math.max(0.0, this.magicule - amount);
         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public void resetBaseValues() {
      this.maxMagicule = CONFIG.baseMagicule;
      this.magicule = this.maxMagicule;
      this.regenerationRate = CONFIG.baseMagiculeRegeneration;
      this.magiculeCache = null;
      this.blockModifiers.clear();
   }

   private ChunkStorage.MagiculeCacheEntry getOrCreateCacheEntry() {
      if (this.magiculeCache == null) {
         Level level = this.getOwner().getLevel();
         List<MagiculeModifier> modifiers = new ArrayList<>();
         level.registryAccess()
            .registryOrThrow(TensuraCustomData.BIOME_MAGICULE)
            .stream()
            .filter(modifier -> level.getBiome(getMiddlePos(this.getOwner())).is(modifier.biomeId()))
            .forEach(modifiers::add);
         level.registryAccess()
            .registryOrThrow(TensuraCustomData.LEVEL_MAGICULE)
            .stream()
            .filter(modifier -> level.dimension().location().equals(modifier.worldId()))
            .forEach(modifiers::add);
         this.magiculeCache = ChunkStorage.MagiculeCacheEntry.of(ImmutableList.sortedCopyOf(Comparator.reverseOrder(), modifiers));
      }

      return this.magiculeCache;
   }

   public static BlockPos getMiddlePos(LevelChunk chunk) {
      return chunk.getPos().getMiddleBlockPosition(120);
   }

   @Generated
   public static StorageKey<ChunkStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public double getMagicule() {
      return this.magicule;
   }

   @Generated
   @Override
   public void setMagicule(double magicule) {
      this.magicule = magicule;
   }

   @Generated
   @Override
   public void setMaxMagicule(double maxMagicule) {
      this.maxMagicule = maxMagicule;
   }

   @Generated
   @Override
   public void setRegenerationRate(double regenerationRate) {
      this.regenerationRate = regenerationRate;
   }

   @Generated
   @Override
   public List<BlockMagiculeModifier> getBlockModifiers() {
      return this.blockModifiers;
   }

   private static class MagiculeCacheEntry {
      private int remains = ChunkStorage.CONFIG.modifierUpdateInterval;
      private final List<MagiculeModifier> modifiers;
      private Pair<Double, Double> maxValueCache = null;
      private Pair<Double, Double> regenValueCache = null;

      public void tick() {
         this.remains--;
      }

      public boolean isOutdated() {
         return this.remains <= 0;
      }

      public double getMaxMagicule(double maxMagicule) {
         if (this.maxValueCache != null && (Double)this.maxValueCache.getFirst() == maxMagicule) {
            return (Double)this.maxValueCache.getSecond();
         }

         double newMaxMagicule = maxMagicule;

         for (MagiculeModifier modifier : this.modifiers) {
            newMaxMagicule = modifier.getMagicule(newMaxMagicule);
         }

         newMaxMagicule = Math.max(newMaxMagicule, 0.0);
         this.maxValueCache = Pair.of(maxMagicule, newMaxMagicule);
         return newMaxMagicule;
      }

      public double getRegenerationRate(double regen) {
         if (this.regenValueCache != null && (Double)this.regenValueCache.getFirst() == regen) {
            return (Double)this.regenValueCache.getSecond();
         }

         double newRegenRate = regen;

         for (MagiculeModifier modifier : this.modifiers) {
            newRegenRate = modifier.getRegenerationRate(newRegenRate);
         }

         newRegenRate = Math.max(newRegenRate, 0.0);
         this.regenValueCache = Pair.of(regen, newRegenRate);
         return newRegenRate;
      }

      @Generated
      private MagiculeCacheEntry(List<MagiculeModifier> modifiers) {
         this.modifiers = modifiers;
      }

      @Generated
      public static ChunkStorage.MagiculeCacheEntry of(List<MagiculeModifier> modifiers) {
         return new ChunkStorage.MagiculeCacheEntry(modifiers);
      }
   }
}
