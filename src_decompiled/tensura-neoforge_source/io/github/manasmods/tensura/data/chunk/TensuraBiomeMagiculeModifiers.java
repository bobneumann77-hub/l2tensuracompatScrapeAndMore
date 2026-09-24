package io.github.manasmods.tensura.data.chunk;

import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;

public class TensuraBiomeMagiculeModifiers {
   public static void bootstrap(BootstrapContext<BiomeMagiculeModifier> context) {
      registerDefault(context, Biomes.DARK_FOREST.location(), 1500.0, 10.0);
      registerDefault(context, Biomes.DEEP_DARK.location(), 2000.0, 20.0);
      registerDefault(context, Biomes.BASALT_DELTAS.location(), 2500.0, 30.0);
      registerDefault(context, Biomes.CRIMSON_FOREST.location(), 4500.0, 50.0);
      registerDefault(context, Biomes.NETHER_WASTES.location(), 1500.0, 20.0);
      registerDefault(context, Biomes.WARPED_FOREST.location(), 3500.0, 40.0);
      registerDefault(context, Biomes.END_BARRENS.location(), 4500.0, 25.0);
      registerDefault(context, Biomes.END_MIDLANDS.location(), 4500.0, 25.0);
      registerDefault(context, Biomes.END_HIGHLANDS.location(), 4500.0, 25.0);
      registerDefault(context, TensuraBiomes.ANCIENT_FOREST.location(), 19500.0, 100.0);
      registerDefault(context, TensuraBiomes.BARREN_LAND.location(), 45500.0, 400.0);
      registerDefault(context, TensuraBiomes.DESERT_OF_DEATH.location(), 29500.0, 200.0);
      registerDefault(context, TensuraBiomes.MIASMIC_PLAINS.location(), 19500.0, 100.0);
      registerDefault(context, TensuraBiomes.UNDERWORLD_BARRENS.location(), 3500.0, 50.0);
      registerDefault(context, TensuraBiomes.UNDERWORLD_RED_SANDS.location(), 1500.0, 50.0);
      registerDefault(context, TensuraBiomes.UNDERWORLD_SANDS.location(), 500.0, 50.0);
      registerDefault(context, TensuraBiomes.UNDERWORLD_SPIKES.location(), 2500.0, 50.0);
   }

   private static void registerDefault(BootstrapContext<BiomeMagiculeModifier> context, ResourceLocation biomeId, double base, double regen) {
      register(
         context,
         new BiomeMagiculeModifier(
            biomeId,
            1,
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.ADD, base)),
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.ADD, regen))
         )
      );
   }

   public static void register(BootstrapContext<BiomeMagiculeModifier> context, BiomeMagiculeModifier data) {
      ResourceKey<BiomeMagiculeModifier> key = ResourceKey.create(TensuraCustomData.BIOME_MAGICULE, data.biomeId());
      context.register(key, data);
   }
}
