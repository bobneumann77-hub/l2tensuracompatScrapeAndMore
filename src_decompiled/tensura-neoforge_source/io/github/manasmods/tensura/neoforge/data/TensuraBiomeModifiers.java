package io.github.manasmods.tensura.neoforge.data;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import io.github.manasmods.tensura.world.TensuraBiomeModification;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class TensuraBiomeModifiers {
   public static final ResourceKey<BiomeModifier> PALM_TREE = registerKey("palm_tree");
   public static final ResourceKey<BiomeModifier> ORE_SILVER_SMALL = registerKey("ore_silver_small");
   public static final ResourceKey<BiomeModifier> ORE_SILVER_LARGE = registerKey("ore_silver_large");
   public static final ResourceKey<BiomeModifier> ORE_SILVER_BURIED = registerKey("ore_silver_buried");
   public static final ResourceKey<BiomeModifier> ORE_MAGIC = registerKey("ore_magic");
   public static final ResourceKey<BiomeModifier> ORE_MAGIC_BURIED = registerKey("ore_magic_buried");
   public static final ResourceKey<BiomeModifier> HIPOKUTE_GRASS = registerKey("hipokute_grass");
   public static final ResourceKey<BiomeModifier> BAFFLEDIL = registerKey("baffledil");

   public static void bootstrap(BootstrapContext<BiomeModifier> context) {
      addSpawns(context);
      addFeatures(context);
   }

   private static void addSpawns(BootstrapContext<BiomeModifier> context) {
      TensuraBiomeModification.initMobSpawning();

      for (TensuraBiomeModification.MobSpawningTemplate template : TensuraBiomeModification.MOB_SPAWNING) {
         registerSpawn(
            context,
            template.getBiome(),
            template.getType(),
            template.getWeight(),
            template.getMinPack(),
            template.getMaxPack(),
            template.getBudget(),
            template.getCharge()
         );
      }
   }

   private static void addFeatures(BootstrapContext<BiomeModifier> context) {
      HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      context.register(
         PALM_TREE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(TensuraBiomeTags.HAS_PALM),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.PALM_TREE)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ORE_SILVER_SMALL,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.ORE_SILVER_SMALL)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ORE_SILVER_LARGE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.ORE_SILVER_LARGE)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ORE_SILVER_BURIED,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.ORE_SILVER_BURIED)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ORE_MAGIC,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.ORE_MAGIC)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ORE_MAGIC_BURIED,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.ORE_MAGIC_BURIED)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         HIPOKUTE_GRASS,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.HIPOKUTE_GRASS)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         BAFFLEDIL,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_FOREST),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(TensuraPlacedFeatures.BAFFLEDIL)}),
            Decoration.VEGETAL_DECORATION
         )
      );
   }

   private static ResourceKey<BiomeModifier> registerKey(String name) {
      return ResourceKey.create(Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   private static void registerSpawn(
      BootstrapContext<BiomeModifier> context, TagKey<Biome> biome, EntityType<?> type, int weight, int min, int max, double budget, double charge
   ) {
      HolderSet<Biome> holderSet = context.lookup(Registries.BIOME).getOrThrow(biome);
      registerSpawn(context, holderSet, type, weight, min, max);
      registerSpawnCost(context, holderSet, type, budget, charge);
   }

   private static void registerSpawn(BootstrapContext<BiomeModifier> context, HolderSet<Biome> biome, EntityType<?> type, int weight, int min, int max) {
      context.register(
         registerKey(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath() + "_spawn"),
         AddSpawnsBiomeModifier.singleSpawn(biome, new SpawnerData(type, weight, min, max))
      );
   }

   private static void registerSpawnCost(BootstrapContext<BiomeModifier> context, HolderSet<Biome> biome, EntityType<?> type, double budget, double charge) {
      Reference<EntityType<?>> holder = context.lookup(Registries.ENTITY_TYPE)
         .getOrThrow((ResourceKey)BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).get());
      context.register(
         registerKey(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath() + "_spawn_cost"),
         new AddSpawnCostsBiomeModifier(biome, HolderSet.direct(new Holder[]{holder}), new MobSpawnCost(budget, charge))
      );
   }
}
