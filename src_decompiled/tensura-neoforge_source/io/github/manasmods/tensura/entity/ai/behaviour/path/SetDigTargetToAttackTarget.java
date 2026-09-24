package io.github.manasmods.tensura.entity.ai.behaviour.path;

import io.github.manasmods.tensura.entity.template.subclass.IDigging;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.logging.log4j.util.TriConsumer;

public class SetDigTargetToAttackTarget<E extends Mob & IDigging<E>> extends SetWalkTargetToAttackTarget<E> {
   protected Function<E, Integer> diggingCooldownSupplier = entity -> 60;
   protected BiFunction<E, LivingEntity, Boolean> shouldDigAttack = (entity, target) -> !entity.isDigging()
      && entity.distanceToSqr(target) > 49.0
      && entity.getRandom().nextFloat() < 0.2F;
   protected BiConsumer<E, LivingEntity> startDigging = (entity, target) -> {};
   protected TriConsumer<E, LivingEntity, Integer> onDiggingDelay = (entity, target, delay) -> {
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 4.0F);
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 2.0F);
      entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
   };
   private final int maxDelayTick;
   private int delayTick = 0;

   public SetDigTargetToAttackTarget(int digDelay) {
      this.maxDelayTick = digDelay;
      if (digDelay > 0) {
         this.runFor(entity -> digDelay);
      }
   }

   public SetDigTargetToAttackTarget<E> digInterval(Function<E, Integer> supplier) {
      this.diggingCooldownSupplier = supplier;
      return this;
   }

   public SetDigTargetToAttackTarget<E> shouldDigAttack(BiFunction<E, LivingEntity, Boolean> function) {
      this.shouldDigAttack = function;
      return this;
   }

   public SetDigTargetToAttackTarget<E> onStartDigging(BiConsumer<E, LivingEntity> callback) {
      this.startDigging = callback;
      return this;
   }

   public SetDigTargetToAttackTarget<E> onDiggingDelay(TriConsumer<E, LivingEntity, Integer> callback) {
      this.onDiggingDelay = callback;
      return this;
   }

   private boolean canDig(E entity, LivingEntity target) {
      if (entity.isDigging()) {
         return false;
      } else {
         return BrainUtils.hasMemory(entity, MemoryModuleType.HAS_HUNTING_COOLDOWN) ? false : this.shouldDigAttack.apply(entity, target);
      }
   }

   protected void start(E entity) {
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      if (this.canDig(entity, target)) {
         this.startDigging.accept(entity, target);
         if (this.maxDelayTick <= 0) {
            entity.setDigging(true);
         } else {
            this.delayTick = this.maxDelayTick;
         }
      } else {
         this.setWalkTarget(entity, target);
      }
   }

   protected void setWalkTarget(E entity, LivingEntity target) {
      Brain<?> brain = entity.getBrain();
      if (BehaviorUtils.isWithinAttackRange(entity, target, 1)) {
         BrainUtils.clearMemory(brain, MemoryModuleType.WALK_TARGET);
      } else {
         BrainUtils.setMemory(brain, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
         BrainUtils.setMemory(
            brain,
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(new EntityTracker(target, false), (Float)this.speedMod.apply(entity, target), this.closeEnoughWhen.applyAsInt(entity, target))
         );
      }
   }

   protected boolean shouldKeepRunning(E entity) {
      if (this.delayTick <= 0) {
         return false;
      }

      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      return target == null ? false : !BehaviorUtils.isWithinAttackRange(entity, target, 1);
   }

   protected void tick(E entity) {
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      if (this.delayTick > 0) {
         if (--this.delayTick == 0) {
            entity.setDigging(true);
            this.setWalkTarget(entity, target);
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.HAS_HUNTING_COOLDOWN, true, this.diggingCooldownSupplier.apply(entity));
         } else {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
         }

         this.onDiggingDelay.accept(entity, target, this.delayTick);
      }
   }

   protected void stop(E entity) {
      this.delayTick = 0;
   }
}
