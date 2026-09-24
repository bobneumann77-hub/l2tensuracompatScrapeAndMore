package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.SleepOnBed;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetNearestPoiWalkTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
      .noMemory(MemoryModuleType.WALK_TARGET);
   private Predicate<Holder<PoiType>> poiPredicate = poi -> true;
   protected BiPredicate<E, BlockPos> additionalPositionCheck = (entity, pos) -> SleepOnBed.isValidBedPosition(entity.level().getBlockState(pos));
   protected Function<E, Boolean> ignorePathReach = entity -> false;
   protected Function<E, Integer> searchDistance = entity -> 48;
   protected BiFunction<E, BlockPos, Integer> closeEnoughDist = (entity, pos) -> 2;
   protected BiFunction<E, BlockPos, Float> speedMod = (owner, pos) -> 1.2F;
   protected BiConsumer<E, BlockPos> action = (entity, pos) -> {};
   private BlockPos target = null;
   private long nextScanTick = 0L;

   public SetNearestPoiWalkTarget<E> predicate(Predicate<Holder<PoiType>> predicate) {
      this.poiPredicate = predicate;
      return this;
   }

   public SetNearestPoiWalkTarget<E> ignorePathReach(Function<E, Boolean> ignore) {
      this.ignorePathReach = ignore;
      return this;
   }

   public SetNearestPoiWalkTarget<E> additionalPredicate(BiPredicate<E, BlockPos> predicate) {
      this.additionalPositionCheck = predicate;
      return this;
   }

   public SetNearestPoiWalkTarget<E> searchDistance(Function<E, Integer> distance) {
      this.searchDistance = distance;
      return this;
   }

   public SetNearestPoiWalkTarget<E> closeEnoughWhen(BiFunction<E, BlockPos, Integer> function) {
      this.closeEnoughDist = function;
      return this;
   }

   public SetNearestPoiWalkTarget<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public SetNearestPoiWalkTarget<E> action(BiConsumer<E, BlockPos> consumer) {
      this.action = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now < this.nextScanTick) {
         return false;
      } else {
         Optional<BlockPos> nearest = level.getPoiManager()
            .getInRange(this.poiPredicate, entity.blockPosition(), this.searchDistance.apply(entity), Occupancy.HAS_SPACE)
            .<BlockPos>map(PoiRecord::getPos)
            .filter(blockPos -> {
               if (!this.additionalPositionCheck.test(entity, blockPos)) {
                  return false;
               }

               if (this.ignorePathReach.apply(entity)) {
                  return true;
               }

               Path path = entity.getNavigation().createPath(blockPos, 8);
               return path != null && path.canReach();
            })
            .min(Comparator.comparingDouble(pos -> pos.distSqr(entity.blockPosition())));
         if (nearest.isEmpty()) {
            this.nextScanTick = now + 20L;
            return false;
         } else {
            this.target = nearest.get();
            int distance = this.closeEnoughDist.apply(entity, this.target);
            return this.target.distSqr(entity.blockPosition()) > distance * distance;
         }
      }
   }

   protected void start(E entity) {
      if (this.target != null) {
         ServerLevel level = (ServerLevel)entity.level();
         if (level.getPoiManager().getType(this.target).isPresent()) {
            BrainUtils.setMemory(
               entity,
               MemoryModuleType.WALK_TARGET,
               new WalkTarget(this.target, this.speedMod.apply(entity, this.target), this.closeEnoughDist.apply(entity, this.target))
            );
            BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            this.action.accept(entity, this.target);
         }
      }
   }
}
