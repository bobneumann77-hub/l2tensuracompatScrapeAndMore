package io.github.manasmods.tensura.registry.world;

import io.github.manasmods.tensura.world.biome.hell.UnderworldBarrensBiome;
import io.github.manasmods.tensura.world.biome.hell.UnderworldRedSandsBiome;
import io.github.manasmods.tensura.world.biome.hell.UnderworldSandsBiome;
import io.github.manasmods.tensura.world.biome.hell.UnderworldSpikesBiome;
import io.github.manasmods.tensura.world.biome.overworld.AncientForestBiome;
import io.github.manasmods.tensura.world.biome.overworld.BarrenLandBiome;
import io.github.manasmods.tensura.world.biome.overworld.DesertOfDeathBiome;
import io.github.manasmods.tensura.world.biome.overworld.MiasmicPlainsBiome;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class TensuraBiomes {
   public static final ResourceKey<Biome> ANCIENT_FOREST = create("ancient_forest");
   public static final ResourceKey<Biome> BARREN_LAND = create("barren_land");
   public static final ResourceKey<Biome> DESERT_OF_DEATH = create("desert_of_death");
   public static final ResourceKey<Biome> MIASMIC_PLAINS = create("miasmic_plains");
   public static final ResourceKey<Biome> UNDERWORLD_BARRENS = create("underworld_barrens");
   public static final ResourceKey<Biome> UNDERWORLD_RED_SANDS = create("underworld_red_sands");
   public static final ResourceKey<Biome> UNDERWORLD_SANDS = create("underworld_sands");
   public static final ResourceKey<Biome> UNDERWORLD_SPIKES = create("underworld_spikes");

   public static void bootstrap(BootstrapContext<Biome> context) {
      context.register(ANCIENT_FOREST, AncientForestBiome.create(context));
      context.register(BARREN_LAND, BarrenLandBiome.create(context));
      context.register(DESERT_OF_DEATH, DesertOfDeathBiome.create(context));
      context.register(MIASMIC_PLAINS, MiasmicPlainsBiome.create(context));
      context.register(UNDERWORLD_BARRENS, UnderworldBarrensBiome.create(context));
      context.register(UNDERWORLD_RED_SANDS, UnderworldRedSandsBiome.create(context));
      context.register(UNDERWORLD_SANDS, UnderworldSandsBiome.create(context));
      context.register(UNDERWORLD_SPIKES, UnderworldSpikesBiome.create(context));
   }

   public static ResourceKey<Biome> create(String name) {
      return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
