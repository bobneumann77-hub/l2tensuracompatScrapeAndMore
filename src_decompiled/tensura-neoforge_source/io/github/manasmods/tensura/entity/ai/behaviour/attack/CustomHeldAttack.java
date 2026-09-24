package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.ConditionlessHeldAttack;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.function.TriFunction;

public class CustomHeldAttack<E extends LivingEntity> extends ConditionlessHeldAttack<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory((MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get());
   protected TriFunction<E, LivingEntity, Integer, Boolean> tickConsumer = (entity, target, tick) -> target != null && target.isAlive();
   protected Function<E, Integer> attackIntervalSupplier = entity -> 20;
   protected Function<E, Float> maxAttackRadius = entity -> 20.0F;
   protected float minAttackRadius;
   protected boolean insightRequired = true;

   public CustomHeldAttack() {
      this.requiresTarget();
      this.maxAttackRadius(10.0F);
      this.minAttackRadius(5.0F);
   }

   public CustomHeldAttack<E> maxAttackRadius(Function<E, Float> radius) {
      this.maxAttackRadius = radius;
      return this;
   }

   public CustomHeldAttack<E> maxAttackRadius(float radius) {
      return this.maxAttackRadius(entity -> radius * radius);
   }

   public CustomHeldAttack<E> minAttackRadius(float radius) {
      this.minAttackRadius = radius * radius;
      return this;
   }

   public CustomHeldAttack<E> requireInSight(boolean inSight) {
      this.insightRequired = inSight;
      return this;
   }

   public CustomHeldAttack<E> onTick(TriFunction<E, LivingEntity, Integer, Boolean> tickConsumer) {
      this.tickConsumer = tickConsumer;
      return this;
   }

   public CustomHeldAttack<E> attackInterval(Function<E, Integer> supplier) {
      this.attackIntervalSupplier = supplier;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      this.target = BrainUtils.getTargetOfEntity(entity);
      if (this.requireTarget && this.target == null) {
         return false;
      } else {
         return this.insightRequired && !BrainUtils.canSee(entity, this.target)
            ? false
            : entity.distanceToSqr(this.target) >= this.minAttackRadius && entity.distanceToSqr(this.target) <= this.maxAttackRadius.apply(entity).floatValue();
      }
   }

   protected void tick(ServerLevel level, E owner, long gameTime) {
      this.tick(owner);
      if (!(Boolean)this.tickConsumer.apply(owner, this.target, this.runningTime)) {
         this.doStop(level, owner, gameTime);
         BrainUtils.setForgettableMemory(owner, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, this.attackIntervalSupplier.apply(owner));
      }

      this.runningTime++;
   }
}
