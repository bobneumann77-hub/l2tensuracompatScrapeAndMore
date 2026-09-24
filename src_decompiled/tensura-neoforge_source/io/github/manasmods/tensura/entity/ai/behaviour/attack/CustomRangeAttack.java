package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class CustomRangeAttack<E extends LivingEntity> extends DelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.ATTACK_TARGET)
      .noMemory((MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get());
   protected Function<E, Integer> attackIntervalSupplier = entity -> entity.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
   protected BiConsumer<E, LivingEntity> performAttackSupplier = LivingEntity::doHurtTarget;
   protected float maxAttackRadius;
   protected float minAttackRadius;
   protected boolean inSightRequired = true;
   @Nullable
   protected LivingEntity target = null;

   public CustomRangeAttack(int delayTicks) {
      super(delayTicks);
      this.maxAttackRadius(10.0F);
      this.minAttackRadius(0.0F);
   }

   public CustomRangeAttack<E> attackInterval(Function<E, Integer> supplier) {
      this.attackIntervalSupplier = supplier;
      return this;
   }

   public CustomRangeAttack<E> maxAttackRadius(float radius) {
      this.maxAttackRadius = radius * radius;
      return this;
   }

   public CustomRangeAttack<E> minAttackRadius(float radius) {
      this.minAttackRadius = radius * radius;
      return this;
   }

   public CustomRangeAttack<E> requireInSight(boolean inSight) {
      this.inSightRequired = inSight;
      return this;
   }

   public CustomRangeAttack<E> performAttack(BiConsumer<E, LivingEntity> callback) {
      this.performAttackSupplier = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      this.target = BrainUtils.getTargetOfEntity(entity);
      return this.inSightRequired && !BrainUtils.canSee(entity, this.target)
         ? false
         : entity.distanceToSqr(this.target) >= this.minAttackRadius && entity.distanceToSqr(this.target) <= this.maxAttackRadius;
   }

   protected void start(E entity) {
      entity.swing(InteractionHand.MAIN_HAND);
      BehaviorUtils.lookAtEntity(entity, this.target);
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.delayTime);
   }

   protected void stop(E entity) {
      this.target = null;
   }

   protected void doDelayedAction(E entity) {
      if (this.target != null) {
         if (BrainUtils.canSee(entity, this.target) && !(entity.distanceToSqr(this.target) > this.maxAttackRadius)) {
            this.performAttackSupplier.accept(entity, this.target);
            BrainUtils.setForgettableMemory(
               entity, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, this.attackIntervalSupplier.apply(entity)
            );
         }
      }
   }
}
