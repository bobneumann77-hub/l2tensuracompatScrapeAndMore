package io.github.manasmods.tensura.registry.data;

import io.github.manasmods.tensura.data.chunk.BiomeMagiculeModifier;
import io.github.manasmods.tensura.data.chunk.LevelMagiculeModifier;
import io.github.manasmods.tensura.data.disolving.ItemDissolving;
import io.github.manasmods.tensura.data.existence.EntityExistenceData;
import io.github.manasmods.tensura.data.existence.gear.GearExistenceData;
import io.github.manasmods.tensura.data.otherworlder.OtherworlderSpawnDistribution;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.data.slotting.SlottingCombination;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TensuraCustomData {
   public static final ResourceKey<Registry<EntityExistenceData>> ENTITY_EXISTENCE = ResourceKey.createRegistryKey(ResourceLocation.parse("entity_existence"));
   public static final ResourceKey<Registry<GearExistenceData>> GEAR_EXISTENCE = ResourceKey.createRegistryKey(ResourceLocation.parse("gear_existence"));
   public static final ResourceKey<Registry<BiomeMagiculeModifier>> BIOME_MAGICULE = ResourceKey.createRegistryKey(ResourceLocation.parse("biome_magicule"));
   public static final ResourceKey<Registry<KilnMoltenMaterial>> KILN_MOLTEN = ResourceKey.createRegistryKey(ResourceLocation.parse("kiln_molten"));
   public static final ResourceKey<Registry<LevelMagiculeModifier>> LEVEL_MAGICULE = ResourceKey.createRegistryKey(ResourceLocation.parse("level_magicule"));
   public static final ResourceKey<Registry<ItemDissolving>> ITEM_DISSOLVING = ResourceKey.createRegistryKey(ResourceLocation.parse("item_dissolving"));
   public static final ResourceKey<Registry<SlottingCombination>> SLOTTING = ResourceKey.createRegistryKey(ResourceLocation.parse("slotting"));
   public static final ResourceKey<Registry<OtherworlderSpawnDistribution>> OTHERWORLDER_SPAWN_DISTRIBUTION = ResourceKey.createRegistryKey(
      ResourceLocation.parse("otherworlder_spawn_distribution")
   );
}
