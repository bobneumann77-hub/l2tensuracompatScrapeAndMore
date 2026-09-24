package io.github.manasmods.tensura.registry.item.misc;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.data.existence.gear.UniqueGearEvolutionData;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TensuraDataComponents {
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create("tensura", Registries.DATA_COMPONENT_TYPE);
   public static final RegistrySupplier<DataComponentType<Boolean>> DUMMY_ITEM = DATA_COMPONENTS.register(
      "dummy_item", () -> DataComponentType.builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );
   public static final RegistrySupplier<DataComponentType<Integer>> ENCHANT_COUNTER = DATA_COMPONENTS.register(
      "enchant_counter", () -> DataComponentType.builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build()
   );
   public static final RegistrySupplier<DataComponentType<Boolean>> ALTERNATIVE_MODE = DATA_COMPONENTS.register(
      "alternative_mode", () -> DataComponentType.builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );
   public static final RegistrySupplier<DataComponentType<Boolean>> SECONDARY_MODE = DATA_COMPONENTS.register(
      "secondary_mode", () -> DataComponentType.builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );
   public static final RegistrySupplier<DataComponentType<Boolean>> MISC_SWITCH = DATA_COMPONENTS.register(
      "misc_switch", () -> DataComponentType.builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );
   public static final RegistrySupplier<DataComponentType<String>> OWNER = DATA_COMPONENTS.register(
      "owner", () -> DataComponentType.builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
   );
   public static final RegistrySupplier<DataComponentType<Integer>> MODE = DATA_COMPONENTS.register(
      "mode", () -> DataComponentType.builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build()
   );
   public static final RegistrySupplier<DataComponentType<ResourceLocation>> SKILL = DATA_COMPONENTS.register(
      "skill", () -> DataComponentType.builder().persistent(ResourceLocation.CODEC).build()
   );
   public static final RegistrySupplier<DataComponentType<ResourceLocation>> SECONDARY_SKILL = DATA_COMPONENTS.register(
      "secondary_skill", () -> DataComponentType.builder().persistent(ResourceLocation.CODEC).build()
   );
   public static final RegistrySupplier<DataComponentType<List<ResourceLocation>>> SKILL_LIST = DATA_COMPONENTS.register(
      "skill_list", () -> DataComponentType.builder().persistent(ResourceLocation.CODEC.listOf()).build()
   );
   public static final RegistrySupplier<DataComponentType<Integer>> RANDOM_ENGRAVING_LEVEL = DATA_COMPONENTS.register(
      "random_engraving_level", () -> DataComponentType.builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build()
   );
   public static final RegistrySupplier<DataComponentType<Float>> TSUKUMOGAMI_INACTIVE = DATA_COMPONENTS.register(
      "tsukumogami_inactive", () -> DataComponentType.builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build()
   );
   public static final RegistrySupplier<DataComponentType<Double>> EP = DATA_COMPONENTS.register(
      "existence_point", () -> DataComponentType.builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build()
   );
   public static final RegistrySupplier<DataComponentType<Double>> EP_GAIN = DATA_COMPONENTS.register(
      "existence_gain", () -> DataComponentType.builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build()
   );
   public static final RegistrySupplier<DataComponentType<Double>> EP_DURABILITY = DATA_COMPONENTS.register(
      "existence_durability", () -> DataComponentType.builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build()
   );
   public static final RegistrySupplier<DataComponentType<Double>> MAX_EP = DATA_COMPONENTS.register(
      "max_existence_point", () -> DataComponentType.builder().persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE).build()
   );
   public static final RegistrySupplier<DataComponentType<ResourceLocation>> EVOLUTION = DATA_COMPONENTS.register(
      "evolution", () -> DataComponentType.builder().persistent(ResourceLocation.CODEC).build()
   );
   public static final RegistrySupplier<DataComponentType<List<UniqueGearEvolutionData>>> UNIQUE_EVOLUTIONS = DATA_COMPONENTS.register(
      "unique_evolutions",
      () -> DataComponentType.builder()
         .persistent(UniqueGearEvolutionData.CODEC.listOf())
         .networkSynchronized(UniqueGearEvolutionData.STREAM_CODEC.apply(ByteBufCodecs.list()))
         .build()
   );
   public static final RegistrySupplier<DataComponentType<ItemAttributeModifiers>> ONE_HANDED_MODIFIERS = DATA_COMPONENTS.register(
      "one_handed_modifiers", () -> DataComponentType.builder().persistent(ItemAttributeModifiers.CODEC).build()
   );
   public static final RegistrySupplier<DataComponentType<ItemAttributeModifiers>> TWO_HANDED_MODIFIERS = DATA_COMPONENTS.register(
      "two_handed_modifiers", () -> DataComponentType.builder().persistent(ItemAttributeModifiers.CODEC).build()
   );

   public static void init() {
      DATA_COMPONENTS.register();
   }
}
