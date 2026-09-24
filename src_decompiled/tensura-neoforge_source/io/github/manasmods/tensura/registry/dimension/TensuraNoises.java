package io.github.manasmods.tensura.registry.dimension;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class TensuraNoises {
   public static final ResourceKey<NoiseParameters> HELL = create("hell");

   public static void bootstrap(BootstrapContext<NoiseParameters> context) {
      context.register(HELL, new NoiseParameters(-7, DoubleList.of(new double[]{4.0, 10.0, 10.0, 1.0})));
   }

   public static ResourceKey<NoiseParameters> create(String name) {
      return ResourceKey.create(Registries.NOISE, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
