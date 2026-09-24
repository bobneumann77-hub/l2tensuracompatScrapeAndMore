package io.github.manasmods.tensura.registry.entity.ai;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyTreeSensor;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyWantedItemSensor;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class TensuraSensors {
   private static final DeferredRegister<SensorType<?>> SENSORS = DeferredRegister.create("tensura", Registries.SENSOR_TYPE);
   public static RegistrySupplier<SensorType<SleepSensor<?>>> SLEEP = SENSORS.register("sleep", () -> new SensorType(SleepSensor::new));
   public static RegistrySupplier<SensorType<NearbyTreeSensor<?>>> NEARBY_TREE = SENSORS.register("nearby_tree", () -> new SensorType(NearbyTreeSensor::new));
   public static RegistrySupplier<SensorType<NearbyWantedItemSensor<?>>> NEARBY_WANTED_ITEM = SENSORS.register(
      "nearby_wanted_item", () -> new SensorType(NearbyWantedItemSensor::new)
   );

   public static void init() {
      SENSORS.register();
   }
}
