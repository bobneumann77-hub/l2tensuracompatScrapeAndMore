package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.IDigging;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetRandomDigTarget<E extends Mob & IDigging<E>> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory(MemoryModuleType.DIG_COOLDOWN);
   protected Function<E, Integer> diggingIntervalSupplier = entity -> 40;
   protected SquareRadius radius = new SquareRadius(8.0, 8.0);
   protected BiFunction<E, Vec3, Float> speedModifier = (entity, targetPos) -> 1.0F;
   protected BiFunction<E, Vec3, Float> surfaceSpeedModifier = (entity, targetPos) -> 1.5F;
   protected BiFunction<E, Vec3, Integer> digDelayTick = (entity, targetPos) -> 0;
   protected BiConsumer<E, Vec3> onDiggingDelay = (entity, pos) -> {
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 4.0F);
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 2.0F);
      entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
   };
   protected BiConsumer<E, Vec3> startDigging = (entity, pos) -> {};
   protected BiFunction<E, Vec3, Boolean> shouldStopDigging = (entity, targetPos) -> hasFreeSpace(entity);
   protected BiConsumer<E, Vec3> stopDigging = (entity, pos) -> entity.onDigUp(entity);
   private boolean surface = false;
   private Vec3 targetPosition;
   private int delayTicks = 0;
   private long nextScanTick = 0L;

   public SetRandomDigTarget() {
      this.noTimeout();
   }

   public SetRandomDigTarget<E> digInterval(Function<E, Integer> supplier) {
      this.diggingIntervalSupplier = supplier;
      return this;
   }

   public SetRandomDigTarget<E> setRadius(double radius) {
      return this.setRadius(radius, radius);
   }

   public SetRandomDigTarget<E> setRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public SetRandomDigTarget<E> speedModifier(float modifier) {
      return this.speedModifier((entity, targetPos) -> modifier);
   }

   public SetRandomDigTarget<E> speedModifier(BiFunction<E, Vec3, Float> function) {
      this.speedModifier = function;
      return this;
   }

   public SetRandomDigTarget<E> surfaceSpeedModifier(float modifier) {
      return this.surfaceSpeedModifier((entity, targetPos) -> modifier);
   }

   public SetRandomDigTarget<E> surfaceSpeedModifier(BiFunction<E, Vec3, Float> function) {
      this.speedModifier = function;
      return this;
   }

   public SetRandomDigTarget<E> delayDigging(BiFunction<E, Vec3, Integer> function) {
      this.digDelayTick = function;
      return this;
   }

   public SetRandomDigTarget<E> onDiggingDelay(BiConsumer<E, Vec3> callback) {
      this.onDiggingDelay = callback;
      return this;
   }

   public SetRandomDigTarget<E> onStartDigging(BiConsumer<E, Vec3> callback) {
      this.startDigging = callback;
      return this;
   }

   public SetRandomDigTarget<E> shouldStopDigging(BiFunction<E, Vec3, Boolean> function) {
      this.shouldStopDigging = function;
      return this;
   }

   public SetRandomDigTarget<E> onStopDigging(BiConsumer<E, Vec3> callback) {
      this.stopDigging = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now < this.nextScanTick) {
         return false;
      }

      if (entity.isVehicle() || entity.isPassenger()) {
         return false;
      }

      if (!entity.isDigging() && !entity.onGround() && !entity.isInWall()) {
         return false;
      }

      if (entity.shouldSurface(entity)) {
         this.surface = true;
      }

      Vec3 targetPos = this.getTargetPosition(entity);
      if (targetPos == null) {
         this.nextScanTick = now + 20L;
         return false;
      } else {
         this.targetPosition = targetPos;
         return true;
      }
   }

   private void doDigging(E entity) {
      entity.setDigging(true);
      this.startDigging.accept(entity, this.targetPosition);
      float speed = this.surface ? this.surfaceSpeedModifier.apply(entity, this.targetPosition) : this.speedModifier.apply(entity, this.targetPosition);
      BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPosition, speed, 0));
   }

   protected void start(E entity) {
      int tick = this.digDelayTick.apply(entity, this.targetPosition);
      if (tick > 0 && !entity.isDigging()) {
         this.delayTicks = tick;
      } else {
         this.doDigging(entity);
      }
   }

   protected boolean shouldKeepRunning(E entity) {
      if (this.delayTicks > 0) {
         return true;
      } else {
         return entity.getTarget() != null ? false : !entity.getNavigation().isDone() && !entity.getNavigation().isStuck() && entity.isDigging();
      }
   }

   protected void tick(E entity) {
      if (this.delayTicks > 0) {
         if (--this.delayTicks == 0) {
            this.doDigging(entity);
         }

         this.onDiggingDelay.accept(entity, this.targetPosition);
      } else if (this.surface && this.shouldStopDigging.apply(entity, this.targetPosition)) {
         entity.setDigging(false);
         this.stopDigging.accept(entity, this.targetPosition);
      }
   }

   protected void stop(E entity) {
      this.surface = false;
      this.delayTicks = 0;
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, this.diggingIntervalSupplier.apply(entity));
   }

   private Vec3 getTargetPosition(E entity) {
      int xz = (int)this.radius.xzRadius();
      int y = (int)this.radius.yRadius();
      MutableBlockPos check = new MutableBlockPos();

      for (int i = 0; i < 20; i++) {
         check.set(entity.blockPosition());
         check.move(entity.getRandom().nextInt(xz * 2) - xz, entity.getRandom().nextInt(y * 2) - y, entity.getRandom().nextInt(xz * 2) - xz);
         if (check.getY() < entity.level().getMinBuildHeight() || !entity.level().isLoaded(check)) {
            break;
         }

         if (this.surface) {
            while (!entity.level().isEmptyBlock(check) && check.getY() < entity.level().getMaxBuildHeight()) {
               check.move(0, 1, 0);
            }

            if (entity.level().isEmptyBlock(check)) {
               while (entity.level().isEmptyBlock(check.below())) {
                  check.move(0, -1, 0);
               }

               return Vec3.atCenterOf(check.immutable());
            }
         } else {
            while (entity.level().isEmptyBlock(check) && check.getY() > entity.level().getMinBuildHeight() - 1) {
               check.move(0, -1, 0);
            }

            if (entity.isSafeDig(entity, entity.level(), check.immutable()) && entity.canReach(entity, check)) {
               return Vec3.atCenterOf(check.immutable());
            }
         }
      }

      return null;
   }

   public static <E extends Mob & IDigging<E>> boolean hasFreeSpace(E entity) {
      BlockPos pos = entity.getOnPos().above((int)(entity.getBbHeight() + 1.5F));
      BlockHitResult result = entity.level().clip(new ClipContext(pos.getBottomCenter(), pos.above().getBottomCenter(), Block.COLLIDER, Fluid.NONE, entity));
      return result.getType().equals(Type.MISS);
   }
}
