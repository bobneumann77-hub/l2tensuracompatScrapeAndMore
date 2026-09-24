package io.github.manasmods.tensura.neoforge;

import com.mojang.serialization.Codec;
import dev.architectury.platform.Platform;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.data.chunk.BiomeMagiculeModifier;
import io.github.manasmods.tensura.data.chunk.LevelMagiculeModifier;
import io.github.manasmods.tensura.data.disolving.ItemDissolving;
import io.github.manasmods.tensura.data.existence.EntityExistenceData;
import io.github.manasmods.tensura.data.existence.gear.GearExistenceData;
import io.github.manasmods.tensura.data.otherworlder.OtherworlderSpawnDistribution;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.data.slotting.SlottingCombination;
import io.github.manasmods.tensura.handler.TensuraHandlers;
import io.github.manasmods.tensura.neoforge.data.TensuraLanguageProvider;
import io.github.manasmods.tensura.neoforge.data.TensuraParticleDescriptionProvider;
import io.github.manasmods.tensura.neoforge.data.TensuraRecipeProvider;
import io.github.manasmods.tensura.neoforge.data.TensuraRegistryProvider;
import io.github.manasmods.tensura.neoforge.data.TensuraSoundProvider;
import io.github.manasmods.tensura.neoforge.data.advancement.TensuraAdvancementProvider;
import io.github.manasmods.tensura.neoforge.data.loot.TensuraLootProvider;
import io.github.manasmods.tensura.neoforge.data.model.TensuraBlockStateProvider;
import io.github.manasmods.tensura.neoforge.data.model.TensuraItemModelProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraAttributeTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraBannerPatternTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraBiomeTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraBlockTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraDamageTypeTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraEnchantmentTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraEntityTypeTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraFluidTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraItemTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraMobEffectTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraPaintingVariantTagsProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraPoiTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraPotionTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraRaceTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraSkillTagProvider;
import io.github.manasmods.tensura.neoforge.data.tag.TensuraStructureTagProvider;
import io.github.manasmods.tensura.neoforge.mixin.AxeItemAccessor;
import io.github.manasmods.tensura.neoforge.registry.TensuraArgumentTypes;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

@Mod("tensura")
public final class TensuraNeoForge {
   public TensuraNeoForge(IEventBus bus) {
      Tensura.init();
      TensuraAttributeRegisterImpl.ATTRIBUTES.register(bus);
      TensuraArgumentTypes.init();
      bus.addListener(this::gatherData);
      bus.addListener(this::commonSetup);
      bus.addListener(this::onCompleteLoad);
      this.registerCustomRegistries(bus);
      if (Platform.getEnv() == Dist.CLIENT) {
         bus.addListener(TensuraNeoForge::renderers);
      }
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(Tensura::initTerraBlender);
   }

   private void onCompleteLoad(FMLLoadCompleteEvent event) {
      TensuraHandlers.onAfterRegistration();
      getStrippables().put((Block)TensuraBlocks.PALM_LOG.get(), (Block)TensuraBlocks.STRIPPED_PALM_LOG.get());
      getStrippables().put((Block)TensuraBlocks.PALM_WOOD.get(), (Block)TensuraBlocks.STRIPPED_PALM_WOOD.get());
   }

   static void renderers(RegisterRenderers event) {
      TensuraClient.registerBlockEntityRenderers();
   }

   public void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = generator.getPackOutput();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      ExistingFileHelper helper = event.getExistingFileHelper();
      generator.addProvider(event.includeClient(), new TensuraBlockStateProvider(output, helper));
      generator.addProvider(event.includeClient(), new TensuraItemModelProvider(output, helper));
      generator.addProvider(event.includeClient(), new TensuraLanguageProvider(output));
      generator.addProvider(event.includeClient(), new TensuraParticleDescriptionProvider(output, helper));
      generator.addProvider(event.includeClient(), new TensuraSoundProvider(output, helper));
      TensuraBlockTagProvider blockTagsProvider = new TensuraBlockTagProvider(output, lookupProvider, helper);
      generator.addProvider(event.includeServer(), blockTagsProvider);
      generator.addProvider(event.includeServer(), new TensuraItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), helper));
      generator.addProvider(event.includeServer(), new TensuraEntityTypeTagProvider(output, lookupProvider, helper));
      generator.addProvider(event.includeServer(), new TensuraFluidTagProvider(output, lookupProvider, helper));
      generator.addProvider(event.includeServer(), new TensuraPoiTagProvider(output, lookupProvider, helper));
      DatapackBuiltinEntriesProvider registryProvider = new TensuraRegistryProvider(output, lookupProvider);
      CompletableFuture<Provider> lookup = registryProvider.getRegistryProvider();
      generator.addProvider(event.includeServer(), registryProvider);
      generator.addProvider(event.includeServer(), new TensuraAttributeTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraDamageTypeTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraBiomeTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraStructureTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraBannerPatternTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraEnchantmentTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraMobEffectTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraPotionTagProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraPaintingVariantTagsProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraRaceTagProvider(output, lookup));
      generator.addProvider(event.includeServer(), new TensuraSkillTagProvider(output, lookup));
      generator.addProvider(event.includeServer(), new TensuraAdvancementProvider(output, lookup, helper));
      generator.addProvider(event.includeServer(), new TensuraLootProvider(output, lookup));
      generator.addProvider(event.includeServer(), new TensuraRecipeProvider(output, lookup));
   }

   private void registerCustomRegistries(IEventBus bus) {
      this.registerCustomDataRegistry(bus, TensuraCustomData.ENTITY_EXISTENCE, EntityExistenceData.CODEC, EntityExistenceData.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.GEAR_EXISTENCE, GearExistenceData.CODEC, GearExistenceData.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.BIOME_MAGICULE, BiomeMagiculeModifier.CODEC, BiomeMagiculeModifier.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.KILN_MOLTEN, KilnMoltenMaterial.CODEC, KilnMoltenMaterial.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.LEVEL_MAGICULE, LevelMagiculeModifier.CODEC, LevelMagiculeModifier.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.ITEM_DISSOLVING, ItemDissolving.CODEC, ItemDissolving.CODEC);
      this.registerCustomDataRegistry(bus, TensuraCustomData.SLOTTING, SlottingCombination.CODEC, SlottingCombination.CODEC);
      this.registerCustomDataRegistry(
         bus, TensuraCustomData.OTHERWORLDER_SPAWN_DISTRIBUTION, OtherworlderSpawnDistribution.CODEC, OtherworlderSpawnDistribution.CODEC
      );
   }

   public <T> void registerCustomDataRegistry(IEventBus bus, ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
      Optional<? extends ModContainer> optional = ModList.get().getModContainerById(key.location().getNamespace());
      if (!optional.isEmpty()) {
         if (networkCodec != null) {
            bus.addListener(NewRegistry.class, event -> event.dataPackRegistry(key, codec, networkCodec));
         } else {
            bus.addListener(NewRegistry.class, event -> event.dataPackRegistry(key, codec));
         }
      }
   }

   private static Map<Block, Block> getStrippables() {
      return getAsMutableMap(AxeItemAccessor::getStrippedBlocks, AxeItemAccessor::setStrippedBlocks);
   }

   public static <K, V> Map<K, V> getAsMutableMap(Supplier<Map<K, V>> getter, Consumer<Map<K, V>> setter) {
      Map<K, V> map = getter.get();
      if (!(map instanceof HashMap)) {
         setter.accept(map = new HashMap<>(map));
      }

      return map;
   }
}
