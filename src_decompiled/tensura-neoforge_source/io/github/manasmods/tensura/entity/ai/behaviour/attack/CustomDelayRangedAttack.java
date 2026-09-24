package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.ai.behaviour.CustomTimeDelayedBehaviour;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class CustomDelayRangedAttack<E extends LivingEntity & RangedAttackMob> extends CustomTimeDelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.ATTACK_TARGET)
      .noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);
   protected Function<E, Integer> attackIntervalSupplier = entity -> entity.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
   protected float attackRadius;
   @Nullable
   protected LivingEntity target = null;

   public CustomDelayRangedAttack() {
      this.attackRadius(16.0F);
   }

   public CustomDelayRangedAttack<E> attackInterval(Function<E, Integer> supplier) {
      this.attackIntervalSupplier = supplier;
      return this;
   }

   public CustomDelayRangedAttack<E> attackRadius(float radius) {
      this.attackRadius = radius * radius;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      this.target = BrainUtils.getTargetOfEntity(entity);
      return BrainUtils.canSee(entity, this.target) && entity.distanceToSqr(this.target) <= this.attackRadius;
   }

   protected void start(E entity) {
      entity.swing(InteractionHand.MAIN_HAND);
      BehaviorUtils.lookAtEntity(entity, this.target);
   }

   protected void stop(E entity) {
      this.target = null;
   }

   @Override
   protected void doDelayedAction(E entity) {
      if (this.target != null) {
         if (BrainUtils.canSee(entity, this.target) && !(entity.distanceToSqr(this.target) > this.attackRadius)) {
            entity.performRangedAttack(this.target, 1.0F);
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
         }
      }
   }
}
