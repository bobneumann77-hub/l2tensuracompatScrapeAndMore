package io.github.manasmods.tensura.world.biome.overworld;

import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class MiasmicPlainsBiome {
   public static Biome create(BootstrapContext<?> context) {
      Builder generationSettings = new Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
      generationSettings.addCarver(Carving.AIR, Carvers.CAVE);
      generationSettings.addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
      generationSettings.addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
      BiomeDefaultFeatures.addDefaultCrystalFormations(generationSettings);
      BiomeDefaultFeatures.addDefaultMonsterRoom(generationSettings);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(generationSettings);
      BiomeDefaultFeatures.addDefaultOres(generationSettings);
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, TensuraPlacedFeatures.DEAD_OAK);
      BiomeDefaultFeatures.addDefaultGrass(generationSettings);
      generationSettings.addFeature(Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_2);
      generationSettings.addFeature(
         Decoration.VEGETAL_DECORATION, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.HIPOKUTE_GRASS_UNCOMMON)
      );
      BiomeSpecialEffects effects = new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
         .fogColor(1781018)
         .skyColor(1781018)
         .waterColor(5731426)
         .waterFogColor(1781018)
         .grassColorOverride(3956557)
         .grassColorOverride(3956557)
         .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
         .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
         .build();
      net.minecraft.world.level.biome.MobSpawnSettings.Builder mobSpawning = new net.minecraft.world.level.biome.MobSpawnSettings.Builder();
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData((EntityType)MonsterEntityTypes.HOUND_DOG.get(), 10, 1, 2));
      mobSpawning.addMobCharge((EntityType)MonsterEntityTypes.HOUND_DOG.get(), 0.7, 0.7);
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE, 65, 2, 4));
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.DROWNED, 45, 2, 4));
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SKELETON, 70, 2, 4));
      mobSpawning.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.BOGGED, 50, 2, 4));
      mobSpawning.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.ZOMBIE_HORSE, 1, 1, 2));
      mobSpawning.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.SKELETON_HORSE, 1, 1, 2));
      return new BiomeBuilder()
         .hasPrecipitation(true)
         .generationSettings(generationSettings.build())
         .downfall(0.8F)
         .temperature(0.4F)
         .temperatureAdjustment(TemperatureModifier.NONE)
         .specialEffects(effects)
         .mobSpawnSettings(mobSpawning.build())
         .build();
   }
}
