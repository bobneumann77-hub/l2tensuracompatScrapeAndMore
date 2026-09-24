package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.logging.log4j.util.TriConsumer;

public class PanicAroundEntity<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
      .hasMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
      .usesMemories(new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.IS_PANICKING});
   protected Predicate<LivingEntity> avoidingPredicate = target -> false;
   protected Function<E, Integer> panicFor = entity -> entity.getRandom().nextInt(100, 120);
   protected TriConsumer<E, LivingEntity, Vec3> additionalPanicFunction = (entity, target, pos) -> {};
   protected Object2FloatFunction<E> speedMod = entity -> 1.25F;
   protected SquareRadius radius = new SquareRadius(5.0, 4.0);
   protected float noCloserThanSqr = 9.0F;
   protected LivingEntity avoidingTarget = null;
   protected Vec3 targetPos = null;
   protected int panicEndTime = 0;

   public PanicAroundEntity() {
      this.noTimeout();
   }

   public PanicAroundEntity<E> noCloserThan(float blocks) {
      this.noCloserThanSqr = blocks * blocks;
      return this;
   }

   public PanicAroundEntity<E> avoiding(Predicate<LivingEntity> predicate) {
      this.avoidingPredicate = predicate;
      return this;
   }

   public PanicAroundEntity<E> additionAvoidingFunction(TriConsumer<E, LivingEntity, Vec3> function) {
      this.additionalPanicFunction = function;
      return this;
   }

   public PanicAroundEntity<E> speedMod(Object2FloatFunction<E> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public PanicAroundEntity<E> setRadius(double radius) {
      return this.setRadius(radius, radius);
   }

   public PanicAroundEntity<E> setRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public PanicAroundEntity<E> panicFor(Function<E, Integer> function) {
      this.panicFor = function;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      Optional<LivingEntity> target = ((NearestVisibleLivingEntities)BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
         .findClosest(this.avoidingPredicate);
      if (target.isEmpty()) {
         return false;
      }

      LivingEntity avoidingEntity = target.get();
      double distToTarget = avoidingEntity.distanceToSqr(entity);
      if (distToTarget > this.noCloserThanSqr) {
         return false;
      }

      this.avoidingTarget = avoidingEntity;
      this.setPanicTarget(entity);
      return this.targetPos != null;
   }

   protected boolean shouldKeepRunning(E entity) {
      return entity.tickCount < this.panicEndTime;
   }

   protected void start(E entity) {
      BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos, (Float)this.speedMod.apply(entity), 0));
      BrainUtils.setMemory(entity, MemoryModuleType.IS_PANICKING, true);
      this.panicEndTime = entity.tickCount + this.panicFor.apply(entity);
   }

   protected void tick(E entity) {
      if (this.targetPos != null && this.avoidingTarget != null) {
         this.additionalPanicFunction.accept(entity, this.avoidingTarget, this.targetPos);
      }

      if (entity.getNavigation().isDone()) {
         this.targetPos = null;
         this.setPanicTarget(entity);
         if (this.targetPos != null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.PATH);
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos, (Float)this.speedMod.apply(entity), 1));
         }
      }
   }

   protected void stop(E entity) {
      this.targetPos = null;
      this.panicEndTime = 0;
      BrainUtils.setMemory(entity, MemoryModuleType.IS_PANICKING, false);
   }

   protected Vec3 findNearbyWater(E entity) {
      BlockPos pos = entity.blockPosition();
      Level level = entity.level();
      return !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
         ? null
         : BlockPos.findClosestMatch(
               entity.blockPosition(), (int)this.radius.xzRadius(), (int)this.radius.yRadius(), checkPos -> level.getFluidState(checkPos).is(FluidTags.WATER)
            )
            .<Vec3>map(Vec3::atBottomCenterOf)
            .orElse(null);
   }

   protected void setPanicTarget(E entity) {
      if (entity.isOnFire()) {
         this.targetPos = this.findNearbyWater(entity);
      }

      if (this.targetPos == null) {
         if (this.avoidingTarget != null) {
            this.targetPos = DefaultRandomPos.getPosAway(entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius(), this.avoidingTarget.position());
         }

         if (this.targetPos == null) {
            this.targetPos = DefaultRandomPos.getPos(entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius());
         }
      }
   }
}
