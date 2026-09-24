package io.github.manasmods.tensura.world.biome.overworld;

import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class DesertOfDeathBiome {
   public static Biome create(BootstrapContext<?> context) {
      Builder generationSettings = new Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
      BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);
      BiomeDefaultFeatures.addDefaultCrystalFormations(generationSettings);
      BiomeDefaultFeatures.addDefaultMonsterRoom(generationSettings);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(generationSettings);
      generationSettings.addFeature(Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA);
      BiomeDefaultFeatures.addDefaultOres(generationSettings);
      BiomeDefaultFeatures.addExtraGold(generationSettings);
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, TensuraPlacedFeatures.PALM_TREE_RARE);
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_2);
      BiomeSpecialEffects effects = new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
         .skyColor(11964263)
         .fogColor(11964263)
         .waterColor(4445678)
         .waterFogColor(14468791)
         .grassColorOverride(9470285)
         .foliageColorOverride(10387789)
         .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
         .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT))
         .ambientParticle(new AmbientParticleSettings(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()), 0.1F))
         .build();
      return new BiomeBuilder()
         .hasPrecipitation(false)
         .generationSettings(generationSettings.build())
         .temperature(2.0F)
         .downfall(0.0F)
         .temperatureAdjustment(TemperatureModifier.NONE)
         .specialEffects(effects)
         .mobSpawnSettings(new net.minecraft.world.level.biome.MobSpawnSettings.Builder().build())
         .build();
   }
}
