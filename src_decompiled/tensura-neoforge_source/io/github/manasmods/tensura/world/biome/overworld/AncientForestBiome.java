package io.github.manasmods.tensura.world.biome.overworld;

import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class AncientForestBiome {
   public static Biome create(BootstrapContext<?> context) {
      Builder generationSettings = new Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
      generationSettings.addCarver(Carving.AIR, context.lookup(Registries.CONFIGURED_CARVER).getOrThrow(Carvers.CAVE));
      generationSettings.addCarver(Carving.AIR, context.lookup(Registries.CONFIGURED_CARVER).getOrThrow(Carvers.CAVE_EXTRA_UNDERGROUND));
      BiomeDefaultFeatures.addDefaultCrystalFormations(generationSettings);
      BiomeDefaultFeatures.addDefaultMonsterRoom(generationSettings);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(generationSettings);
      BiomeDefaultFeatures.addDefaultOres(generationSettings);
      BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
      BiomeDefaultFeatures.addForestFlowers(generationSettings);
      BiomeDefaultFeatures.addDefaultFlowers(generationSettings);
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.HIPOKUTE_GRASS_COMMON)
      );
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.BAFFLEDIL_COMMON));
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(VegetationPlacements.PATCH_GRASS_TAIGA));
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.COMMON_FERN_PATCHES)
      );
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.COMMON_TALL_GRASS_PATCHES)
      );
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.COMMON_BROWN_MUSHROOM_PATCHES)
      );
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.COMMON_RED_MUSHROOM_PATCHES)
      );
      BiomeDefaultFeatures.addMossyStoneBlock(generationSettings);
      BiomeSpecialEffects effects = new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
         .fogColor(12638463)
         .waterColor(4159204)
         .waterFogColor(329011)
         .skyColor(7972607)
         .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
         .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE))
         .build();
      return new BiomeBuilder()
         .hasPrecipitation(true)
         .generationSettings(generationSettings.build())
         .downfall(0.8F)
         .temperature(0.7F)
         .temperatureAdjustment(TemperatureModifier.NONE)
         .specialEffects(effects)
         .mobSpawnSettings(new net.minecraft.world.level.biome.MobSpawnSettings.Builder().build())
         .build();
   }
}
