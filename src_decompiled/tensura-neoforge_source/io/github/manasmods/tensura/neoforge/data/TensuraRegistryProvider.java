package io.github.manasmods.tensura.neoforge.data;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.chunk.TensuraBiomeMagiculeModifiers;
import io.github.manasmods.tensura.data.chunk.TensuraLevelMagiculeModifiers;
import io.github.manasmods.tensura.data.disolving.TensuraItemDissolving;
import io.github.manasmods.tensura.data.existence.TensuraEntityExistenceData;
import io.github.manasmods.tensura.data.existence.gear.TensuraGearExistenceData;
import io.github.manasmods.tensura.data.otherworlder.TensuraOtherworlderSpawnDistribution;
import io.github.manasmods.tensura.data.recipe.TensuraKilnMoltenMaterials;
import io.github.manasmods.tensura.data.slotting.TensuraSlottingCombinations;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.registry.block.TensuraPaintingVariants;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.dimension.TensuraNoises;
import io.github.manasmods.tensura.registry.item.misc.TensuraBannerPatterns;
import io.github.manasmods.tensura.registry.item.misc.TensuraTrimMaterials;
import io.github.manasmods.tensura.registry.sound.TensuraJukeboxSongs;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import io.github.manasmods.tensura.registry.world.TensuraConfiguredFeatures;
import io.github.manasmods.tensura.registry.world.TensuraPlacedFeatures;
import io.github.manasmods.tensura.registry.world.TensuraStructureSets;
import io.github.manasmods.tensura.registry.world.TensuraStructures;
import io.github.manasmods.tensura.registry.world.TensuraTemplatePools;
import io.github.manasmods.tensura.world.dimension.TensuraNoiseGeneratorSettings;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class TensuraRegistryProvider extends DatapackBuiltinEntriesProvider {
   public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.BANNER_PATTERN, TensuraBannerPatterns::bootstrap)
      .add(Registries.BIOME, TensuraBiomes::bootstrap)
      .add(Registries.CONFIGURED_FEATURE, TensuraConfiguredFeatures::bootstrap)
      .add(Registries.DAMAGE_TYPE, TensuraDamageTypes::bootstrap)
      .add(Registries.DIMENSION_TYPE, TensuraDimensions::bootstrapDimensionType)
      .add(Registries.ENCHANTMENT, TensuraEnchantments::bootstrap)
      .add(Registries.JUKEBOX_SONG, TensuraJukeboxSongs::bootstrap)
      .add(Registries.LEVEL_STEM, TensuraDimensions::bootstrapLevelStem)
      .add(Registries.NOISE, TensuraNoises::bootstrap)
      .add(Registries.NOISE_SETTINGS, TensuraNoiseGeneratorSettings::bootstrap)
      .add(Registries.PAINTING_VARIANT, TensuraPaintingVariants::bootstrap)
      .add(Registries.PLACED_FEATURE, TensuraPlacedFeatures::bootstrap)
      .add(Registries.STRUCTURE, TensuraStructures::bootstrap)
      .add(Registries.STRUCTURE_SET, TensuraStructureSets::bootstrap)
      .add(Registries.TEMPLATE_POOL, TensuraTemplatePools::bootstrap)
      .add(Registries.TRIM_MATERIAL, TensuraTrimMaterials::bootstrap)
      .add(Keys.BIOME_MODIFIERS, TensuraBiomeModifiers::bootstrap)
      .add(TensuraCustomData.BIOME_MAGICULE, TensuraBiomeMagiculeModifiers::bootstrap)
      .add(TensuraCustomData.ENTITY_EXISTENCE, TensuraEntityExistenceData::bootstrap)
      .add(TensuraCustomData.GEAR_EXISTENCE, TensuraGearExistenceData::bootstrap)
      .add(TensuraCustomData.ITEM_DISSOLVING, TensuraItemDissolving::bootstrap)
      .add(TensuraCustomData.KILN_MOLTEN, TensuraKilnMoltenMaterials::bootstrap)
      .add(TensuraCustomData.LEVEL_MAGICULE, TensuraLevelMagiculeModifiers::bootstrap)
      .add(TensuraCustomData.SLOTTING, TensuraSlottingCombinations::bootstrap)
      .add(TensuraCustomData.OTHERWORLDER_SPAWN_DISTRIBUTION, TensuraOtherworlderSpawnDistribution::bootstrap);

   public TensuraRegistryProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries, BUILDER, Set.of("tensura", "minecraft"));
   }
}
