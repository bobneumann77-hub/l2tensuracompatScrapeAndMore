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

public class TensuraOverworldDesertRegion extends Region {
   public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "overworld_desert");

   public TensuraOverworldDesertRegion(int weight) {
      super(LOCATION, RegionType.OVERWORLD, weight);
   }

   public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
      this.addModifiedVanillaOverworldBiomes(mapper, builder -> builder.replaceBiome(Biomes.DESERT, TensuraBiomes.DESERT_OF_DEATH));
   }
}
