package io.github.manasmods.tensura.world.biome.hell;

import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeGenerationSettings.PlainBuilder;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class UnderworldSpikesBiome {
   public static Biome create(BootstrapContext<?> context) {
      PlainBuilder generationSettings = new PlainBuilder()
         .addCarver(Carving.AIR, context.lookup(Registries.CONFIGURED_CARVER).getOrThrow(Carvers.CANYON))
         .addFeature(Decoration.LOCAL_MODIFICATIONS, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.HELL_BLOCK_BLOB))
         .addFeature(Decoration.LOCAL_MODIFICATIONS, context.lookup(Registries.PLACED_FEATURE).getOrThrow(TensuraPlacedFeatures.ROCK_SPIKE));
      Builder spawnSettings = new Builder()
         .addSpawn(MobCategory.MONSTER, new SpawnerData((EntityType)MonsterEntityTypes.HOUND_DOG.get(), 70, 1, 1))
         .addMobCharge((EntityType)MonsterEntityTypes.HOUND_DOG.get(), 0.7, 0.7)
         .addSpawn(MobCategory.MONSTER, new SpawnerData((EntityType)MonsterEntityTypes.LESSER_DAEMON.get(), 40, 1, 1))
         .addMobCharge((EntityType)MonsterEntityTypes.LESSER_DAEMON.get(), 4.0, 4.0)
         .addSpawn(MobCategory.MONSTER, new SpawnerData((EntityType)MonsterEntityTypes.GREATER_DAEMON.get(), 5, 1, 1))
         .addMobCharge((EntityType)MonsterEntityTypes.GREATER_DAEMON.get(), 4.0, 4.0)
         .creatureGenerationProbability(1.0E-4F);
      BiomeSpecialEffects effects = new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
         .fogColor(1586208)
         .waterColor(4159204)
         .waterFogColor(4159204)
         .skyColor(0)
         .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
         .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0))
         .ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111))
         .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BASALT_DELTAS))
         .build();
      return new BiomeBuilder()
         .hasPrecipitation(false)
         .generationSettings(generationSettings.build())
         .mobSpawnSettings(spawnSettings.build())
         .downfall(0.0F)
         .temperature(0.6F)
         .temperatureAdjustment(TemperatureModifier.FROZEN)
         .specialEffects(effects)
         .build();
   }
}
