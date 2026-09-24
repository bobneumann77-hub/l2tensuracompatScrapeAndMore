package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class AcquirePoi<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private BiFunction<E, Holder<PoiType>, Boolean> poiPredicate = (entity, poi) -> true;
   private MemoryModuleType<GlobalPos> writeMemory = MemoryModuleType.MEETING_POINT;
   private MemoryModuleType<GlobalPos> absentCheckMemory = MemoryModuleType.MEETING_POINT;
   private Function<E, Boolean> skipBabies = e -> false;
   private BiPredicate<E, BlockPos> additionalPositionCheck = (e, pos) -> true;
   private Function<E, Integer> searchDistance = e -> 48;
   private Function<E, Integer> candidateLimit = e -> 7;
   private Optional<Byte> successEvent = Optional.empty();
   private BiConsumer<E, BlockPos> onAcquire = (e, pos) -> {};
   private BlockPos targetPos = null;
   private final Long2ObjectMap<AcquirePoi.JitteredLinearRetry> retryByPos = new Long2ObjectOpenHashMap();
   private long nextScanTime = 0L;

   public AcquirePoi<E> predicate(BiFunction<E, Holder<PoiType>, Boolean> predicate) {
      this.poiPredicate = predicate;
      return this;
   }

   public AcquirePoi<E> writeTo(MemoryModuleType<GlobalPos> memory) {
      this.writeMemory = memory;
      this.absentCheckMemory = memory;
      return this;
   }

   public AcquirePoi<E> writeTo(MemoryModuleType<GlobalPos> memory, MemoryModuleType<GlobalPos> absentCheckMemory) {
      this.writeMemory = memory;
      this.absentCheckMemory = absentCheckMemory;
      return this;
   }

   public AcquirePoi<E> skipBabies(Function<E, Boolean> skip) {
      this.skipBabies = skip;
      return this;
   }

   public AcquirePoi<E> additionalPredicate(BiPredicate<E, BlockPos> predicate) {
      this.additionalPositionCheck = predicate;
      return this;
   }

   public AcquirePoi<E> searchDistance(Function<E, Integer> distance) {
      this.searchDistance = distance;
      return this;
   }

   public AcquirePoi<E> candidateLimit(Function<E, Integer> limit) {
      this.candidateLimit = limit;
      return this;
   }

   public AcquirePoi<E> successEvent(byte event) {
      this.successEvent = Optional.of(event);
      return this;
   }

   public AcquirePoi<E> onAcquire(BiConsumer<E, BlockPos> consumer) {
      this.onAcquire = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MemoryTest.builder(0);
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (this.skipBabies.apply(entity) && entity.isBaby()) {
         return false;
      }

      if (BrainUtils.hasMemory(entity, this.absentCheckMemory)) {
         return false;
      }

      long now = level.getGameTime();
      if (this.nextScanTime == 0L) {
         this.nextScanTime = now + level.random.nextInt(20);
         return false;
      }

      if (now < this.nextScanTime) {
         return false;
      }

      this.nextScanTime = now + 20L + level.getRandom().nextInt(20);
      int radius = this.searchDistance.apply(entity);
      int limit = Math.max(1, this.candidateLimit.apply(entity));
      this.retryByPos.long2ObjectEntrySet().removeIf(e -> !((AcquirePoi.JitteredLinearRetry)e.getValue()).isStillValid(now));
      Predicate<BlockPos> retryGate = posx -> {
         AcquirePoi.JitteredLinearRetry r = (AcquirePoi.JitteredLinearRetry)this.retryByPos.get(posx.asLong());
         if (r == null) {
            return true;
         }

         if (!r.shouldRetry(now)) {
            return false;
         }

         r.markAttempt(now);
         return true;
      };
      BlockPos entityPos = entity.blockPosition();
      Predicate<Holder<PoiType>> predicate = poi -> this.poiPredicate.apply(entity, poi);
      Set<BlockPos> candidates = level.getPoiManager()
         .getInRange(predicate, entityPos, radius, Occupancy.HAS_SPACE)
         .<BlockPos>map(PoiRecord::getPos)
         .filter(retryGate)
         .filter(posx -> this.additionalPositionCheck.test(entity, posx))
         .sorted(Comparator.comparingDouble(posx -> posx.distSqr(entityPos)))
         .limit(limit)
         .collect(Collectors.toCollection(HashSet::new));
      if (candidates.isEmpty()) {
         return false;
      }

      Path multiPath = findPathToAny(entity, candidates);
      if (multiPath != null && multiPath.canReach()) {
         this.targetPos = multiPath.getTarget();
         return true;
      }

      for (BlockPos pos : candidates) {
         long key = pos.asLong();
         this.retryByPos.computeIfAbsent(key, k -> new AcquirePoi.JitteredLinearRetry(level.random, now));
      }

      return false;
   }

   protected void start(E entity) {
      if (this.targetPos != null) {
         ServerLevel level = (ServerLevel)entity.level();
         Predicate<Holder<PoiType>> predicate = poi -> this.poiPredicate.apply(entity, poi);
         level.getPoiManager().getType(this.targetPos).ifPresent(holder -> {
            level.getPoiManager().take(predicate, (h, bp) -> bp.equals(this.targetPos), this.targetPos, 1);
            BrainUtils.setMemory(entity, this.writeMemory, GlobalPos.of(level.dimension(), this.targetPos));
            this.successEvent.ifPresent(b -> level.broadcastEntityEvent(entity, b));
            this.retryByPos.clear();
            this.onAcquire.accept(entity, this.targetPos);
         });
         this.targetPos = null;
      }
   }

   private static Path findPathToAny(PathfinderMob mob, Set<BlockPos> targets) {
      if (targets.isEmpty()) {
         return null;
      }

      int range = 1;
      return mob.getNavigation().createPath(targets, range);
   }

   private static class JitteredLinearRetry {
      private static final int MIN_INC = 40;
      private static final int MAX_INC = 80;
      private static final int MAX_INTERVAL = 400;
      private static final long MAX_VALID_AGE = 400L;
      private final RandomSource random;
      private long previousAttempt;
      private long nextAttempt;
      private int currentDelay;

      JitteredLinearRetry(RandomSource random, long now) {
         this.random = random;
         this.markAttempt(now);
      }

      void markAttempt(long now) {
         this.previousAttempt = now;
         int inc = this.currentDelay + this.random.nextInt(40) + 40;
         this.currentDelay = Math.min(inc, 400);
         this.nextAttempt = now + this.currentDelay;
      }

      boolean isStillValid(long now) {
         return now - this.previousAttempt < 400L;
      }

      boolean shouldRetry(long now) {
         return now >= this.nextAttempt;
      }
   }
}
