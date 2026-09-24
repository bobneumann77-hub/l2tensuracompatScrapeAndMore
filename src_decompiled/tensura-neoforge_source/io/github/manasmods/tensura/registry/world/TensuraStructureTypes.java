package io.github.manasmods.tensura.registry.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.world.structure.JigsawMinHeightStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class TensuraStructureTypes {
   public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create("tensura", Registries.STRUCTURE_TYPE);
   public static final RegistrySupplier<StructureType<JigsawMinHeightStructure>> JIGSAW_MIN_HEIGHT = STRUCTURE_TYPES.register(
      "jigsaw_min_height", () -> () -> JigsawMinHeightStructure.CODEC
   );

   public static void init() {
      STRUCTURE_TYPES.register();
   }
}
