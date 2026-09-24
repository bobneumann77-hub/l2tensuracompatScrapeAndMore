package io.github.manasmods.tensura.util;

import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public final class MagicEngineHelper {
   private static final AtomicInteger ACTIVE_COUNT = new AtomicInteger();

   private MagicEngineHelper() {
   }

   public static void increment() {
      ACTIVE_COUNT.incrementAndGet();
   }

   public static void decrement() {
      ACTIVE_COUNT.decrementAndGet();
   }

   public static boolean hasActive() {
      return ACTIVE_COUNT.get() > 0;
   }

   public static boolean shouldSkipForMob(Mob mob) {
      if (mob == null) {
         return true;
      }

      if (!hasActive()) {
         return true;
      }

      double threshold = ChunkStorage.CONFIG.minimalMagiculeSpawn;
      if (AreaMagiculeHelper.getMagicule(mob, true) < threshold) {
         return true;
      }

      LivingEntity target = mob.getTarget();
      return target != null
         && (target == mob.getLastHurtByMob() || target == mob.getLastHurtMob() || target == mob.getLastAttacker())
         && AreaMagiculeHelper.getMagicule(target, true) < threshold;
   }

   public static boolean isNodeInZone(Mob mob, int x, int y, int z) {
      return AreaMagiculeHelper.getMagicule(mob.level(), new BlockPos(x, y, z), true) < ChunkStorage.CONFIG.minimalMagiculeSpawn;
   }
}
