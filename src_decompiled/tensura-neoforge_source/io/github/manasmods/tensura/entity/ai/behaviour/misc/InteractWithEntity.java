package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class InteractWithEntity<E extends LivingEntity, T extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(4)
      .usesMemory(MemoryModuleType.INTERACTION_TARGET)
      .usesMemory(MemoryModuleType.LOOK_TARGET)
      .noMemory(MemoryModuleType.WALK_TARGET)
      .hasMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   private final EntityType<?> targetType;
   private final MemoryModuleType<T> targetMemory;
   private Function<E, Integer> radius = entity -> 8;
   private Function<E, Integer> interactTime = entity -> 100;
   private Function<E, Float> speed = entity -> 1.0F;
   private Function<E, Integer> priority = entity -> 2;
   private Predicate<E> selfPredicate = entity -> true;
   private Predicate<T> targetPredicate = target -> true;
   private BiPredicate<E, T> bothPredicate = (entity, target) -> true;
   @Nullable
   private T chosenTarget = (T)null;

   public InteractWithEntity(EntityType<?> targetType, MemoryModuleType<T> targetMemory) {
      this.targetType = targetType;
      this.targetMemory = targetMemory;
   }

   public InteractWithEntity<E, T> radius(Function<E, Integer> radius) {
      this.radius = radius;
      return this;
   }

   public InteractWithEntity<E, T> interactTime(Function<E, Integer> time) {
      this.interactTime = time;
      return this;
   }

   public InteractWithEntity<E, T> speed(Function<E, Float> speed) {
      this.speed = speed;
      return this;
   }

   public InteractWithEntity<E, T> priority(Function<E, Integer> function) {
      this.priority = function;
      return this;
   }

   public InteractWithEntity<E, T> selfPredicate(Predicate<E> predicate) {
      this.selfPredicate = predicate;
      return this;
   }

   public InteractWithEntity<E, T> targetPredicate(Predicate<T> predicate) {
      this.targetPredicate = predicate;
      return this;
   }

   public InteractWithEntity<E, T> bothPredicate(BiPredicate<E, T> predicate) {
      this.bothPredicate = predicate;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!this.selfPredicate.test(entity)) {
         return false;
      }

      NearestVisibleLivingEntities visible = (NearestVisibleLivingEntities)BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      if (visible == null) {
         return false;
      }

      int r = Math.max(0, this.radius.apply(entity));
      double maxDistSqr = r * r;
      this.chosenTarget = (T)visible.findClosest(
            target -> target.getType() == this.targetType
               && target.distanceToSqr(entity) <= maxDistSqr
               && this.targetPredicate.test((T)target)
               && this.bothPredicate.test(entity, (T)target)
         )
         .orElse(null);
      return this.chosenTarget != null;
   }

   protected void start(E entity) {
      if (this.chosenTarget != null) {
         int time = this.interactTime.apply(entity);
         if (time < 0) {
            BrainUtils.setMemory(entity, this.targetMemory, this.chosenTarget);
         } else {
            BrainUtils.setForgettableMemory(entity, this.targetMemory, this.chosenTarget, time);
         }

         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.chosenTarget, true));
         BrainUtils.setMemory(
            entity,
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(new EntityTracker(this.chosenTarget, false), this.speed.apply(entity), this.priority.apply(entity))
         );
      }
   }

   protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
      return false;
   }

   protected void stop(ServerLevel level, E entity, long gameTime) {
      this.chosenTarget = null;
   }
}
