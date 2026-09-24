package io.github.manasmods.tensura.world.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class TensuraNoiseGeneratorSettings {
   public static final ResourceKey<NoiseGeneratorSettings> HELL_NOISE = create("hell");

   public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {
      context.register(HELL_NOISE, Hell.GenerationNoises.hell(context));
   }

   public static ResourceKey<NoiseGeneratorSettings> create(String name) {
      return ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
