package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

public class WakeUp<E extends LivingEntity> extends ExtendedBehaviour<E> {
   protected Consumer<E> action = entity -> {};

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return List.of();
   }

   public WakeUp<E> onWakeUp(Consumer<E> consumer) {
      this.action = consumer;
      return this;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!entity.getBrain().isActive(Activity.REST) && entity.isSleeping() && !SleepEffect.isForcedSleeping(entity)) {
         entity.stopSleeping();
         this.action.accept(entity);
         return true;
      } else {
         return false;
      }
   }
}
