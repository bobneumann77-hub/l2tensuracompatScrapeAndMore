package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
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

public class SpittingRangedMonsterAttack<E extends LivingEntity & SpittingRangedMonster> extends DelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.ATTACK_TARGET)
      .noMemory((MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get());
   protected Function<E, Integer> attackIntervalSupplier = entity -> entity.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
   protected BiConsumer<E, LivingEntity> performAttackSupplier = (entity, target) -> entity.performRangedAttack(target, 1.0F);
   protected boolean inSightRequired = true;
   protected float attackRadius;
   @Nullable
   protected LivingEntity target = null;

   public SpittingRangedMonsterAttack(int delayTicks) {
      super(delayTicks);
      this.attackRadius(16.0F);
   }

   public SpittingRangedMonsterAttack<E> attackInterval(Function<E, Integer> supplier) {
      this.attackIntervalSupplier = supplier;
      return this;
   }

   public SpittingRangedMonsterAttack<E> attackRadius(float radius) {
      this.attackRadius = radius * radius;
      return this;
   }

   public SpittingRangedMonsterAttack<E> requireInSight(boolean inSight) {
      this.inSightRequired = inSight;
      return this;
   }

   public SpittingRangedMonsterAttack<E> performAttack(BiConsumer<E, LivingEntity> callback) {
      this.performAttackSupplier = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      this.target = BrainUtils.getTargetOfEntity(entity);
      return this.inSightRequired && !BrainUtils.canSee(entity, this.target) ? false : entity.distanceToSqr(this.target) <= this.attackRadius;
   }

   protected void start(E entity) {
      entity.swing(InteractionHand.MAIN_HAND);
      BehaviorUtils.lookAtEntity(entity, this.target);
   }

   protected void stop(E entity) {
      this.target = null;
   }

   protected void doDelayedAction(E entity) {
      if (this.target != null) {
         if (BrainUtils.canSee(entity, this.target) && !(entity.distanceToSqr(this.target) > this.attackRadius)) {
            this.performAttackSupplier.accept(entity, this.target);
            BrainUtils.setForgettableMemory(
               entity, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, this.attackIntervalSupplier.apply(entity)
            );
         }
      }
   }
}
