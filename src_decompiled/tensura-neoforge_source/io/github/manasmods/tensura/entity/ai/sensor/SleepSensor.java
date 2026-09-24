package io.github.manasmods.tensura.entity.ai.sensor;

import io.github.manasmods.tensura.registry.entity.ai.TensuraMemoryModules;
import io.github.manasmods.tensura.registry.entity.ai.TensuraSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SleepSensor<E extends LivingEntity> extends ExtendedSensor<E> {
   private static final List<MemoryModuleType<?>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
      new MemoryModuleType[]{(MemoryModuleType)TensuraMemoryModules.SLEEPING.get()}
   );

   public List<MemoryModuleType<?>> memoriesUsed() {
      return MEMORY_REQUIREMENTS;
   }

   public SensorType<? extends ExtendedSensor<?>> type() {
      return (SensorType<? extends ExtendedSensor<?>>)TensuraSensors.SLEEP.get();
   }

   protected void doTick(ServerLevel level, E entity) {
      if (entity.hasPose(Pose.SLEEPING)) {
         BrainUtils.setMemory(entity, (MemoryModuleType)TensuraMemoryModules.SLEEPING.get(), true);
      } else {
         BrainUtils.clearMemory(entity, (MemoryModuleType)TensuraMemoryModules.SLEEPING.get());
      }
   }
}
