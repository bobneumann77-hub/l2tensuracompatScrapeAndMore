package io.github.manasmods.tensura.registry.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.world.tree.leaves.PalmFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class TensuraFoliagePlacers {
   private static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER = DeferredRegister.create("tensura", Registries.FOLIAGE_PLACER_TYPE);
   public static final RegistrySupplier<FoliagePlacerType<PalmFoliagePlacer>> PALM_FOLIAGE = FOLIAGE_PLACER.register(
      "palm_foliage_placer", () -> new FoliagePlacerType(PalmFoliagePlacer.CODEC)
   );

   public static void init() {
      FOLIAGE_PLACER.register();
   }
}
