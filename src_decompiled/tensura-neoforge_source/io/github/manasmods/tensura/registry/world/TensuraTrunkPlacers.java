package io.github.manasmods.tensura.registry.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.world.tree.trunk.PalmTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class TensuraTrunkPlacers {
   private static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER = DeferredRegister.create("tensura", Registries.TRUNK_PLACER_TYPE);
   public static final RegistrySupplier<TrunkPlacerType<PalmTrunkPlacer>> PALM_TRUNK_PLACER = TRUNK_PLACER.register(
      "palm_trunk_placer", () -> new TrunkPlacerType(PalmTrunkPlacer.CODEC)
   );

   public static void init() {
      TRUNK_PLACER.register();
   }
}
