package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ConditionlessAction<E extends LivingEntity> extends DelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory(MemoryModuleType.ATE_RECENTLY);
   protected Function<E, Integer> actionIntervalSupplier = entity -> 20;
   protected Consumer<E> attack = entity -> {};

   public ConditionlessAction(int delayTicks) {
      super(delayTicks);
   }

   public ConditionlessAction<E> actionInterval(Function<E, Integer> supplier) {
      this.actionIntervalSupplier = supplier;
      return this;
   }

   public ConditionlessAction<E> attack(Consumer<E> consumer) {
      this.attack = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected void start(E entity) {
      entity.swing(InteractionHand.MAIN_HAND);
   }

   protected void doDelayedAction(E entity) {
      this.attack.accept(entity);
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATE_RECENTLY, true, this.actionIntervalSupplier.apply(entity));
   }
}
