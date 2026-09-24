package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

public class OrbitAttack<E extends Mob> extends HeldBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);
   protected BiFunction<E, LivingEntity, Float> speedMod = (owner, target) -> 1.2F;
   protected ToIntBiFunction<E, LivingEntity> closeEnoughWhen = (owner, target) -> 0;
   protected boolean insightRequired = true;
   protected boolean lookAtTargetWhenOrbiting = false;
   protected BiFunction<E, LivingEntity, Double> orbitRadiusSupplier = (entity, target) -> 6.0;
   protected BiFunction<E, LivingEntity, Double> orbitMinRadiusSupplier = (entity, target) -> 0.0;
   protected BiFunction<E, LivingEntity, Double> orbitHeightSupplier = (entity, target) -> 4.0;
   protected TriFunction<E, LivingEntity, Pair<Vec3, Integer>, Boolean> orbitTickConsumer = (entity, target, pair) -> true;
   protected BiFunction<E, LivingEntity, Boolean> canDoOrbitalAttack = (owner, target) -> true;
   protected BiConsumer<E, LivingEntity> startOrbitalAttackSupplier = (owner, target) -> {};
   protected BiFunction<E, LivingEntity, Float> attackSpeedMod = (owner, target) -> 2.0F;
   protected Function<E, Integer> orbitalAttackIntervalSupplier = entity -> 60 + entity.getRandom().nextInt(80);
   protected BiFunction<E, LivingEntity, Boolean> isWithinMeleeAttack = (entity, target) -> BehaviorUtils.isWithinAttackRange(entity, target, 1);
   protected BiConsumer<E, LivingEntity> performOrbitalAttackSupplier = LivingEntity::doHurtTarget;
   @Nullable
   protected LivingEntity target = null;
   private Vec3 startOrbitFrom;
   private int orbitTime;
   private int orbitalAttackCooldown;

   public OrbitAttack<E> requireInSight(boolean inSight) {
      this.insightRequired = inSight;
      return this;
   }

   public OrbitAttack<E> lookAtTargetWhileOrbiting(boolean look) {
      this.lookAtTargetWhenOrbiting = look;
      return this;
   }

   public OrbitAttack<E> speedMod(BiFunction<E, LivingEntity, Float> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public OrbitAttack<E> closeEnoughDist(ToIntBiFunction<E, LivingEntity> closeEnoughMod) {
      this.closeEnoughWhen = closeEnoughMod;
      return this;
   }

   public OrbitAttack<E> orbitRadius(BiFunction<E, LivingEntity, Double> supplier) {
      this.orbitRadiusSupplier = supplier;
      return this;
   }

   public OrbitAttack<E> orbitMinRadius(BiFunction<E, LivingEntity, Double> supplier) {
      this.orbitMinRadiusSupplier = supplier;
      return this;
   }

   public OrbitAttack<E> orbitHeight(BiFunction<E, LivingEntity, Double> supplier) {
      this.orbitHeightSupplier = supplier;
      return this;
   }

   public OrbitAttack<E> onOrbitTick(TriFunction<E, LivingEntity, Pair<Vec3, Integer>, Boolean> tickConsumer) {
      this.orbitTickConsumer = tickConsumer;
      return this;
   }

   public OrbitAttack<E> canDoOrbitalAttack(BiFunction<E, LivingEntity, Boolean> attack) {
      this.canDoOrbitalAttack = attack;
      return this;
   }

   public OrbitAttack<E> onStartOrbitAttack(BiConsumer<E, LivingEntity> callback) {
      this.startOrbitalAttackSupplier = callback;
      return this;
   }

   public OrbitAttack<E> attackSpeedMod(BiFunction<E, LivingEntity, Float> speedModifier) {
      this.attackSpeedMod = speedModifier;
      return this;
   }

   public OrbitAttack<E> orbitAttackInterval(Function<E, Integer> supplier) {
      this.orbitalAttackIntervalSupplier = supplier;
      return this;
   }

   public OrbitAttack<E> shouldDoMeleeAttack(BiFunction<E, LivingEntity, Boolean> attack) {
      this.isWithinMeleeAttack = attack;
      return this;
   }

   public OrbitAttack<E> performOrbitAttack(BiConsumer<E, LivingEntity> callback) {
      this.performOrbitalAttackSupplier = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      this.target = BrainUtils.getTargetOfEntity(entity);
      if (this.target == null || !this.target.isAlive()) {
         return false;
      }

      if (this.insightRequired && !BrainUtils.canSee(entity, this.target)) {
         return false;
      }

      this.startOrbitFrom = this.target.getEyePosition();
      return true;
   }

   protected boolean shouldKeepRunning(E entity) {
      return this.target != null && this.target.isAlive() && !this.target.isAlliedTo(entity) && this.target == BrainUtils.getTargetOfEntity(entity);
   }

   protected void start(E entity) {
      this.orbitTime = 0;
      this.orbitalAttackCooldown = this.orbitalAttackIntervalSupplier.apply(entity);
   }

   protected void stop(E entity) {
      this.target = null;
      this.startOrbitFrom = null;
      this.orbitTime = 0;
      this.orbitalAttackCooldown = 0;
   }

   protected void tick(E entity) {
      LivingEntity target = this.target;
      if (target != null && target.isAlive()) {
         if ((Boolean)this.orbitTickConsumer.apply(entity, target, Pair.of(this.startOrbitFrom, this.orbitTime))) {
            if (this.startOrbitFrom == null) {
               BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
               BrainUtils.setMemory(
                  entity,
                  MemoryModuleType.WALK_TARGET,
                  new WalkTarget(new EntityTracker(target, false), this.attackSpeedMod.apply(entity, target), this.closeEnoughWhen.applyAsInt(entity, target))
               );
               if (this.isWithinMeleeAttack.apply(entity, target)) {
                  this.performOrbitalAttackSupplier.accept(entity, target);
                  this.orbitalAttackCooldown = this.orbitalAttackIntervalSupplier.apply(entity);
                  this.startOrbitFrom = target.getEyePosition();
                  BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
                  BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET});
               }
            } else if (this.orbitTime < this.orbitalAttackCooldown) {
               this.orbitTime++;
               Vec3 orbitPos = this.getOrbitPosition(entity, this.orbitRadiusSupplier.apply(entity, target), this.orbitMinRadiusSupplier.apply(entity, target));
               BrainUtils.setMemory(
                  entity,
                  MemoryModuleType.WALK_TARGET,
                  new WalkTarget(orbitPos, this.speedMod.apply(entity, target), this.closeEnoughWhen.applyAsInt(entity, target))
               );
               if (this.lookAtTargetWhenOrbiting) {
                  BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
               } else {
                  BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(BlockPos.containing(orbitPos)));
               }
            } else {
               this.orbitTime = 0;
               if (this.canDoOrbitalAttack.apply(entity, target)) {
                  this.startOrbitalAttackSupplier.accept(entity, target);
                  this.startOrbitFrom = null;
               } else {
                  this.startOrbitFrom = target.getEyePosition();
               }
            }
         }
      }
   }

   public Vec3 getOrbitPosition(E entity, double radius, double minRadius) {
      float progress = (float)this.orbitTime / this.orbitalAttackCooldown;
      float zoomIn = 1.0F - progress;
      float angle = (float)((Math.PI * 2) * progress);
      double currentRadius = Mth.lerp(progress, minRadius, radius);
      double extraX = currentRadius * Mth.sin(angle);
      double extraZ = currentRadius * Mth.cos(angle);
      double extraY = this.orbitHeightSupplier.apply(entity, this.target) + zoomIn * 3.0F;
      return this.startOrbitFrom.add(extraX, extraY, extraZ);
   }
}
