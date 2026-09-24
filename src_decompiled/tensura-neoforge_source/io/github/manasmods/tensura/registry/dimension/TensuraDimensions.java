package io.github.manasmods.tensura.registry.dimension;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import io.github.manasmods.tensura.world.dimension.TensuraNoiseGeneratorSettings;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.DimensionType.MonsterSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class TensuraDimensions {
   public static final ResourceLocation HELL_LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "hell");
   public static final ResourceKey<Level> HELL = ResourceKey.create(Registries.DIMENSION, HELL_LOCATION);
   public static final ResourceKey<LevelStem> HELL_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM, HELL_LOCATION);
   public static final ResourceKey<DimensionType> HELL_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, HELL_LOCATION);
   public static final ResourceKey<Level> LABYRINTH = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth"));
   public static final ResourceKey<LevelStem> LABYRINTH_LEVEL_STEM = ResourceKey.create(
      Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth")
   );
   public static final ResourceKey<DimensionType> LABYRINTH_TYPE = ResourceKey.create(
      Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath("tensura", "labyrinth")
   );
   public static final ResourceKey<Level> BOSS_AREA = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("tensura", "boss_area"));
   public static final ResourceKey<LevelStem> BOSS_AREA_LEVEL_STEM = ResourceKey.create(
      Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("tensura", "boss_area")
   );
   public static final ResourceKey<DimensionType> BOSS_AREA_TYPE = ResourceKey.create(
      Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath("tensura", "boss_area")
   );

   public static void bootstrapLevelStem(BootstrapContext<LevelStem> context) {
      HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
      HolderGetter<DimensionType> dimensionTypeHolderGetter = context.lookup(Registries.DIMENSION_TYPE);
      HolderGetter<NoiseGeneratorSettings> noiseSettingsHolderGetter = context.lookup(Registries.NOISE_SETTINGS);
      ParameterList<Holder<Biome>> hellBiomes = new ParameterList(
         List.of(
            Pair.of(Climate.parameters(0.5F, 0.2F, 0.3F, 0.1F, 0.1F, 0.0F, 0.0F), (Holder)biomeHolderGetter.get(TensuraBiomes.UNDERWORLD_BARRENS).get()),
            Pair.of(Climate.parameters(1.0F, 0.0F, 0.3F, 0.2F, 0.1F, 0.0F, 0.0F), (Holder)biomeHolderGetter.get(TensuraBiomes.UNDERWORLD_RED_SANDS).get()),
            Pair.of(Climate.parameters(0.1F, 0.0F, 0.2F, 0.3F, 0.0F, 0.0F, 0.0F), (Holder)biomeHolderGetter.get(TensuraBiomes.UNDERWORLD_SANDS).get()),
            Pair.of(Climate.parameters(0.4F, 0.1F, 0.2F, 0.2F, 0.4F, 0.0F, 0.0F), (Holder)biomeHolderGetter.get(TensuraBiomes.UNDERWORLD_SPIKES).get())
         )
      );
      NoiseBasedChunkGenerator hellChunk = new NoiseBasedChunkGenerator(
         MultiNoiseBiomeSource.createFromList(hellBiomes), noiseSettingsHolderGetter.getOrThrow(TensuraNoiseGeneratorSettings.HELL_NOISE)
      );
      LevelStem hellStem = new LevelStem(dimensionTypeHolderGetter.getOrThrow(HELL_TYPE), hellChunk);
      context.register(HELL_LEVEL_STEM, hellStem);
      FlatLevelGeneratorSettings voidSettings = new FlatLevelGeneratorSettings(Optional.empty(), biomeHolderGetter.getOrThrow(Biomes.THE_VOID), List.of());
      LevelStem labyrinthStem = new LevelStem(dimensionTypeHolderGetter.getOrThrow(LABYRINTH_TYPE), new FlatLevelSource(voidSettings));
      context.register(LABYRINTH_LEVEL_STEM, labyrinthStem);
      LevelStem bossStem = new LevelStem(dimensionTypeHolderGetter.getOrThrow(BOSS_AREA_TYPE), new FlatLevelSource(voidSettings));
      context.register(BOSS_AREA_LEVEL_STEM, bossStem);
   }

   public static void bootstrapDimensionType(BootstrapContext<DimensionType> context) {
      createDimensionType(
         6000,
         320,
         320,
         -64,
         1,
         1.0F,
         true,
         false,
         false,
         true,
         true,
         false,
         BlockTags.INFINIBURN_END,
         HELL_LOCATION,
         new MonsterSettings(true, false, ConstantInt.of(15), 15),
         context,
         HELL_TYPE
      );
      createDimensionType(
         3000,
         320,
         320,
         -64,
         1,
         1.0F,
         true,
         false,
         false,
         false,
         true,
         false,
         BlockTags.INFINIBURN_END,
         BuiltinDimensionTypes.END_EFFECTS,
         new MonsterSettings(true, false, ConstantInt.of(0), 0),
         context,
         LABYRINTH_TYPE
      );
      createDimensionType(
         3000,
         320,
         320,
         -64,
         1,
         0.0F,
         true,
         false,
         false,
         false,
         true,
         false,
         BlockTags.INFINIBURN_END,
         BuiltinDimensionTypes.OVERWORLD_EFFECTS,
         new MonsterSettings(true, false, ConstantInt.of(0), 0),
         context,
         BOSS_AREA_TYPE
      );
   }

   private static void createDimensionType(
      int fixedTime,
      int height,
      int maxY,
      int minY,
      int coordinateScale,
      float ambientLight,
      boolean hasSkylight,
      boolean hasCeiling,
      boolean bedWorks,
      boolean anchorWorks,
      boolean isNatural,
      boolean isUltrawarm,
      TagKey<Block> infiniburn,
      ResourceLocation specialEffects,
      MonsterSettings monsterSettings,
      BootstrapContext<DimensionType> context,
      ResourceKey<DimensionType> resourceKey
   ) {
      DimensionType type = new DimensionType(
         OptionalLong.of(fixedTime),
         hasSkylight,
         hasCeiling,
         isUltrawarm,
         isNatural,
         coordinateScale,
         bedWorks,
         anchorWorks,
         minY,
         maxY,
         height,
         infiniburn,
         specialEffects,
         ambientLight,
         monsterSettings
      );
      context.register(resourceKey, type);
   }
}
