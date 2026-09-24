package io.github.manasmods.tensura.world.biome;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import terrablender.api.Region;
import terrablender.api.RegionType;

public class TensuraOverworldRegion extends Region {
   public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "overworld");

   public TensuraOverworldRegion(int weight) {
      super(LOCATION, RegionType.OVERWORLD, weight);
   }

   public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
      this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
         builder.replaceBiome(Biomes.PLAINS, TensuraBiomes.ANCIENT_FOREST);
         builder.replaceBiome(Biomes.DESERT, TensuraBiomes.BARREN_LAND);
         builder.replaceBiome(Biomes.SWAMP, TensuraBiomes.MIASMIC_PLAINS);
      });
   }
}
