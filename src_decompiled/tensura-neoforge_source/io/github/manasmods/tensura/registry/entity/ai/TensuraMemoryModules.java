package io.github.manasmods.tensura.registry.entity.ai;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class TensuraMemoryModules {
   private static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create("tensura", Registries.MEMORY_MODULE_TYPE);
   public static RegistrySupplier<MemoryModuleType<Boolean>> SLEEPING = MEMORY_MODULES.register("sleeping", () -> new MemoryModuleType(Optional.of(Codec.BOOL)));
   public static RegistrySupplier<MemoryModuleType<BlockPos>> NEAREST_TREE = MEMORY_MODULES.register(
      "nearest_tree", () -> new MemoryModuleType(Optional.empty())
   );
   public static RegistrySupplier<MemoryModuleType<Set<BlockPos>>> BUILDING_LOGS = MEMORY_MODULES.register(
      "building_logs", () -> new MemoryModuleType(Optional.empty())
   );
   public static RegistrySupplier<MemoryModuleType<List<BlockPos>>> LAST_TREES_CHOPPED = MEMORY_MODULES.register(
      "last_trees_chopped", () -> new MemoryModuleType(Optional.empty())
   );

   public static void init() {
      MEMORY_MODULES.register();
   }
}
