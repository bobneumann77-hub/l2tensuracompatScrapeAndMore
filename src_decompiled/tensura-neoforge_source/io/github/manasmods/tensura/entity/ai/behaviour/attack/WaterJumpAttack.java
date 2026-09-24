package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.ISwimmingJumper;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class WaterJumpAttack<E extends PathfinderMob & ISwimmingJumper> extends CustomRangeAttack<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.ATTACK_TARGET)
      .noMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);
   protected Consumer<E> onJumpSupplier = entity -> entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get());
   protected Consumer<E> onLandSupplier = entity -> entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get());
   protected Function<E, Float> xzSpeedSupplier = entity -> 0.6F;
   protected Function<E, Float> jumpStrengthSupplier = entity -> 1.0F + entity.getRandom().nextFloat() * 0.8F;
   protected BiFunction<E, LivingEntity, Integer> midairAttackIntervalSupplier = (entity, target) -> 20;
   private boolean inWater = false;
   private int attackCooldown = 0;

   public WaterJumpAttack(int delayTicks) {
      super(delayTicks);
   }

   public WaterJumpAttack<E> onJump(Consumer<E> consumer) {
      this.onJumpSupplier = consumer;
      return this;
   }

   public WaterJumpAttack<E> onLand(Consumer<E> consumer) {
      this.onLandSupplier = consumer;
      return this;
   }

   public WaterJumpAttack<E> xzSpeedMultiplier(Function<E, Float> supplier) {
      this.xzSpeedSupplier = supplier;
      return this;
   }

   public WaterJumpAttack<E> jumpStrength(Function<E, Float> supplier) {
      this.jumpStrengthSupplier = supplier;
      return this;
   }

   public WaterJumpAttack<E> midairAttackInterval(BiFunction<E, LivingEntity, Integer> supplier) {
      this.midairAttackIntervalSupplier = supplier;
      return this;
   }

   @Override
   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (entity.isVehicle()) {
         return false;
      }

      if (!this.canJump(entity)) {
         return false;
      }

      this.target = BrainUtils.getTargetOfEntity(entity);
      return entity.shouldUseJumpAttack(entity, this.target, true);
   }

   private boolean canJump(E entity) {
      return entity.shouldFlop() ? false : entity.isInWater() || this.inWater;
   }

   protected boolean shouldKeepRunning(E entity) {
      if (this.target == null || !this.target.isAlive()) {
         return false;
      }

      if (entity.shouldFlop()) {
         return false;
      }

      double d0 = entity.getDeltaMovement().y;
      return (!(d0 * d0 < 0.03F) || !entity.isInWater() && !this.inWater) && !entity.onGround();
   }

   protected void doDelayedAction(E entity) {
      if (this.target != null && this.target.isAlive() && this.canJump(entity)) {
         entity.lookAt(Anchor.EYES, entity.getEyePosition());
         double smoothX = Mth.clamp(Math.abs(this.target.getX() - entity.getX()), 0.0, 1.0);
         double smoothZ = Mth.clamp(Math.abs(this.target.getZ() - entity.getZ()), 0.0, 1.0);
         float xz = this.xzSpeedSupplier.apply(entity);
         double d0 = (this.target.getX() - entity.getX()) * 0.3 * smoothX;
         double d2 = (this.target.getZ() - entity.getZ()) * 0.3 * smoothZ;
         float up = this.jumpStrengthSupplier.apply(entity);
         entity.setDeltaMovement(entity.getDeltaMovement().add(d0 * xz, up, d2 * xz));
         this.onJumpSupplier.accept(entity);
         entity.getNavigation().stop();
      }
   }

   protected void tick(E entity) {
      boolean inWater = entity.isInWaterOrBubble();
      if (inWater && !this.inWater) {
         this.onLandSupplier.accept(entity);
      }

      this.inWater = inWater;
      if (this.attackCooldown-- <= 0 && entity.isWithinMeleeAttackRange(this.target)) {
         this.performAttackSupplier.accept(entity, this.target);
         this.attackCooldown = this.midairAttackIntervalSupplier.apply(entity, this.target);
      }

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
      this.target = null;
      int cooldown = this.attackIntervalSupplier.apply(entity);
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, cooldown, cooldown);
   }
}
