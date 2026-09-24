package io.github.manasmods.tensura.registry.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.world.features.FloatingDebrisFeature;
import io.github.manasmods.tensura.world.features.HellBlockBlobFeature;
import io.github.manasmods.tensura.world.features.RockySpikeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class TensuraFeatures {
   private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create("tensura", Registries.FEATURE);
   public static final RegistrySupplier<RockySpikeFeature> ROCKY_SPIKE = FEATURES.register("rocky_spike", RockySpikeFeature::new);
   public static final RegistrySupplier<HellBlockBlobFeature> HELL_BLOCK_BLOB = FEATURES.register("hell_block_blob", HellBlockBlobFeature::new);
   public static final RegistrySupplier<FloatingDebrisFeature> FLOATING_DEBRIS = FEATURES.register("floating_debris", FloatingDebrisFeature::new);

   public static void init() {
      FEATURES.register();
   }
}
