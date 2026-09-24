package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.ISwimmingJumper;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class JumpOutOfWater<E extends PathfinderMob & ISwimmingJumper> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);
   protected Function<E, Integer> jumpIntervalSupplier = entity -> 300;
   protected Function<E, Integer> jumpBlockedHeightCheckSupplier = entity -> 5;
   protected Function<E, Float> xzSpeedSupplier = entity -> 0.6F;
   protected Function<E, Float> jumpStrengthSupplier = entity -> 0.7F + entity.getRandom().nextFloat() * 0.8F;
   protected Consumer<E> onJumpSupplier = entity -> entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get());
   protected Consumer<E> onLandSupplier = entity -> entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get());
   private static final int[] JUMP_DISTANCES = new int[]{0, 1, 4, 5, 6, 7};
   private boolean inWater = false;

   public JumpOutOfWater() {
      this.noTimeout();
   }

   public JumpOutOfWater<E> jumpInterval(Function<E, Integer> supplier) {
      this.jumpIntervalSupplier = supplier;
      return this;
   }

   public JumpOutOfWater<E> jumpBlockedHeightCheck(Function<E, Integer> supplier) {
      this.jumpBlockedHeightCheckSupplier = supplier;
      return this;
   }

   public JumpOutOfWater<E> xzSpeedMultiplier(Function<E, Float> supplier) {
      this.xzSpeedSupplier = supplier;
      return this;
   }

   public JumpOutOfWater<E> jumpStrength(Function<E, Float> supplier) {
      this.jumpStrengthSupplier = supplier;
      return this;
   }

   public JumpOutOfWater<E> onJump(Consumer<E> consumer) {
      this.onJumpSupplier = consumer;
      return this;
   }

   public JumpOutOfWater<E> onLand(Consumer<E> consumer) {
      this.onLandSupplier = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (entity.isVehicle()) {
         return false;
      }

      if (entity.shouldFlop()) {
         return false;
      }

      if (entity.getTarget() != null) {
         return false;
      }

      Direction dir = entity.getMotionDirection();
      float xz = this.xzSpeedSupplier.apply(entity);
      int dx = (int)(dir.getStepX() * xz);
      int dz = (int)(dir.getStepZ() * xz);
      BlockPos pos = entity.blockPosition();
      int height = this.jumpBlockedHeightCheckSupplier.apply(entity);

      for (int dist : JUMP_DISTANCES) {
         if (!entity.isWaterClear(entity, pos, dx, dz, dist)) {
            return false;
         }

         if (!entity.isSurfaceClear(entity, pos, 3, height, dx, dz, dist)) {
            return false;
         }
      }

      return true;
   }

   protected boolean shouldKeepRunning(E entity) {
      if (entity.shouldFlop()) {
         return false;
      }

      double d0 = entity.getDeltaMovement().y;
      return (!(d0 * d0 < 0.03F) || !entity.isInWater()) && !entity.onGround();
   }

   protected void start(E entity) {
      Direction direction = entity.getMotionDirection();
      float up = this.jumpStrengthSupplier.apply(entity);
      float xz = this.xzSpeedSupplier.apply(entity);
      entity.setDeltaMovement(entity.getDeltaMovement().add(direction.getStepX() * xz, up, direction.getStepZ() * xz));
      entity.hasImpulse = true;
      entity.getNavigation().stop();
      this.onJumpSupplier.accept(entity);
   }

   protected void tick(E entity) {
      boolean inWater = entity.isInWaterOrBubble();
      if (inWater && !this.inWater) {
         this.onLandSupplier.accept(entity);
      }

      this.inWater = inWater;
      Vec3 motion = entity.getDeltaMovement();
      if (motion.y * motion.y < 0.1F && entity.getXRot() != 0.0F) {
         entity.setXRot(Mth.rotLerp(entity.getXRot(), 0.0F, 0.2F));
      } else {
         double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
         double angle = Math.signum(-motion.y) * Math.acos(horizontalSpeed / motion.length()) * 180.0F / (float)Math.PI;
         entity.setXRot((float)angle);
      }
   }

   protected void stop(E entity) {
      int cooldown = this.jumpIntervalSupplier.apply(entity);
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, cooldown, cooldown);
   }
}
