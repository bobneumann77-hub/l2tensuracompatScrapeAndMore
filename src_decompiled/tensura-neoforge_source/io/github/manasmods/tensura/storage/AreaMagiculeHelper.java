package io.github.manasmods.tensura.storage;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.data.chunk.BlockMagiculeModifier;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class AreaMagiculeHelper {
   public static double getMagicule(Level level, BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      return !level.hasChunk(chunkX, chunkZ) ? ChunkStorage.CONFIG.baseMagicule : TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ)).getMagicule();
   }

   public static double getMagicule(LivingEntity entity) {
      return getMagicule(entity.level(), entity.blockPosition());
   }

   public static double getMaxMagicule(Level level, BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      return !level.hasChunk(chunkX, chunkZ) ? ChunkStorage.CONFIG.baseMagicule : TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ)).getMaxMagicule();
   }

   public static Pair<Double, Double> getMagiculePair(Level level, BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return Pair.of(ChunkStorage.CONFIG.baseMagicule, ChunkStorage.CONFIG.baseMagicule);
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      return Pair.of(storage.getMagicule(), storage.getMaxMagicule());
   }

   public static Pair<Double, Double> getMagiculePair(LivingEntity entity) {
      return getMagiculePair(entity.level(), entity.blockPosition());
   }

   public static double getMaxMagicule(LivingEntity entity) {
      return getMaxMagicule(entity.level(), entity.blockPosition());
   }

   public static boolean consumeMagicule(Level level, BlockPos pos, double amount) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return false;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      return storage.consumeMagicule(amount);
   }

   public static boolean consumeMagicule(LivingEntity entity, double amount) {
      return consumeMagicule(entity.level(), entity.blockPosition(), amount);
   }

   public static double getMagiculeRegenerationRate(Level level, BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return ChunkStorage.CONFIG.baseMagiculeRegeneration;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      return storage.getRegenerationRate();
   }

   public static double getMagiculeRegenerationRate(LivingEntity entity) {
      return getMagiculeRegenerationRate(entity.level(), entity.blockPosition());
   }

   public static List<BlockMagiculeModifier> getBlockModifiers(Level level, BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      return !level.hasChunk(chunkX, chunkZ) ? List.of() : TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ)).getBlockModifiers();
   }

   public static double getMagicule(LivingEntity entity, boolean countBlockModifier) {
      Level level = entity.level();
      BlockPos pos = entity.blockPosition();
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return ChunkStorage.CONFIG.baseMagicule;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      double areaMP = storage.getMagicule();
      if (countBlockModifier) {
         List<BlockMagiculeModifier> modifiers = storage.getBlockModifiers();
         if (!modifiers.isEmpty()) {
            double px = entity.getX();
            double py = entity.getY();
            double pz = entity.getZ();

            for (BlockMagiculeModifier modifier : modifiers) {
               if (withinCube(modifier.pos(), px, py, pz, modifier.effectDistance() + 0.5)) {
                  areaMP = modifier.getMagicule(areaMP);
               }
            }
         }
      }

      return Math.max(areaMP, 0.0);
   }

   public static double getMagicule(Level level, BlockPos pos, boolean countBlockModifier) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return ChunkStorage.CONFIG.baseMagicule;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      double areaMP = storage.getMagicule();
      if (countBlockModifier) {
         List<BlockMagiculeModifier> modifiers = storage.getBlockModifiers();
         if (!modifiers.isEmpty()) {
            double px = pos.getX() + 0.5;
            double py = pos.getY() + 0.5;
            double pz = pos.getZ() + 0.5;

            for (BlockMagiculeModifier modifier : modifiers) {
               if (withinCube(modifier.pos(), px, py, pz, modifier.effectDistance() + 0.5)) {
                  areaMP = modifier.getMagicule(areaMP);
               }
            }
         }
      }

      return Math.max(areaMP, 0.0);
   }

   public static double getMaxMagicule(LivingEntity entity, boolean countBlockModifier) {
      Level level = entity.level();
      BlockPos pos = entity.blockPosition();
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return ChunkStorage.CONFIG.baseMagicule;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      double areaMP = storage.getMaxMagicule();
      if (countBlockModifier) {
         List<BlockMagiculeModifier> modifiers = storage.getBlockModifiers();
         if (!modifiers.isEmpty()) {
            double px = entity.getX();
            double py = entity.getY();
            double pz = entity.getZ();

            for (BlockMagiculeModifier modifier : modifiers) {
               if (withinCube(modifier.pos(), px, py, pz, modifier.effectDistance() + 0.5)) {
                  areaMP = modifier.getMagicule(areaMP);
               }
            }
         }
      }

      return Math.max(areaMP, 0.0);
   }

   public static double getMaxMagicule(Level level, BlockPos pos, boolean countBlockModifier) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      if (!level.hasChunk(chunkX, chunkZ)) {
         return ChunkStorage.CONFIG.baseMagicule;
      }

      ChunkStorage storage = TensuraStorages.getChunkFrom(level.getChunk(chunkX, chunkZ));
      double areaMP = storage.getMaxMagicule();
      if (countBlockModifier) {
         List<BlockMagiculeModifier> modifiers = storage.getBlockModifiers();
         if (!modifiers.isEmpty()) {
            double px = pos.getX() + 0.5;
            double py = pos.getY() + 0.5;
            double pz = pos.getZ() + 0.5;

            for (BlockMagiculeModifier modifier : modifiers) {
               if (withinCube(modifier.pos(), px, py, pz, modifier.effectDistance() + 0.5)) {
                  areaMP = modifier.getMagicule(areaMP);
               }
            }
         }
      }

      return Math.max(areaMP, 0.0);
   }

   private static boolean withinCube(BlockPos modifierPos, double px, double py, double pz, double r) {
      double dx = Math.abs(modifierPos.getX() + 0.5 - px);
      if (dx > r) {
         return false;
      }

      double dy = Math.abs(modifierPos.getY() + 0.5 - py);
      if (dy > r) {
         return false;
      }

      double dz = Math.abs(modifierPos.getZ() + 0.5 - pz);
      return dz <= r;
   }

   @Generated
   private AreaMagiculeHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
