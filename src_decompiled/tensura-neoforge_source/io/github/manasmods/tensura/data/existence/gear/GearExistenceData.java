package io.github.manasmods.tensura.data.existence.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public record GearExistenceData(
   ResourceLocation gear,
   int minEP,
   int maxEP,
   double epGain,
   Optional<ResourceLocation> evolution,
   Optional<Map<Holder<Enchantment>, Integer>> engravings,
   Optional<List<UniqueGearEvolutionData>> uniqueEvolution
) {
   public static final Codec<GearExistenceData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(GearExistenceData::gear),
            Codec.INT.fieldOf("minEP").forGetter(GearExistenceData::minEP),
            Codec.INT.optionalFieldOf("maxEP", 2000000).forGetter(GearExistenceData::maxEP),
            Codec.DOUBLE.optionalFieldOf("epGain", 0.03).forGetter(GearExistenceData::epGain),
            ResourceLocation.CODEC.optionalFieldOf("evolution").forGetter(GearExistenceData::evolution),
            Codec.unboundedMap(Enchantment.CODEC, Codec.INT).optionalFieldOf("engravings").forGetter(GearExistenceData::engravings),
            Codec.list(UniqueGearEvolutionData.CODEC).optionalFieldOf("uniqueEvolutions").forGetter(GearExistenceData::uniqueEvolution)
         )
         .apply(instance, GearExistenceData::new)
   );

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, int maxEP, double epGain, ResourceLocation evolution) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.of(evolution), Optional.empty(), Optional.empty());
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, int maxEP, double epGain) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, double epGain) {
      return new GearExistenceData(entity, minEP, 2000000, epGain, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static GearExistenceData getDefault(
      ResourceLocation entity, int minEP, int maxEP, double epGain, ResourceLocation evolution, Map<Holder<Enchantment>, Integer> engravings
   ) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.of(evolution), Optional.of(engravings), Optional.empty());
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, int maxEP, double epGain, Map<Holder<Enchantment>, Integer> engravings) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.empty(), Optional.of(engravings), Optional.empty());
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, double epGain, Map<Holder<Enchantment>, Integer> engravings) {
      return new GearExistenceData(entity, minEP, 2000000, epGain, Optional.empty(), Optional.of(engravings), Optional.empty());
   }

   public static GearExistenceData getDefault(ResourceLocation entity, Map<Holder<Enchantment>, Integer> engravings) {
      return new GearExistenceData(entity, 0, 0, 0.0, Optional.empty(), Optional.of(engravings), Optional.empty());
   }

   public static GearExistenceData getDefault(
      ResourceLocation entity, int minEP, int maxEP, double epGain, Map<Holder<Enchantment>, Integer> engravings, List<UniqueGearEvolutionData> uniqueEvolution
   ) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.empty(), Optional.of(engravings), Optional.of(uniqueEvolution));
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, int maxEP, double epGain, List<UniqueGearEvolutionData> uniqueEvolution) {
      return new GearExistenceData(entity, minEP, maxEP, epGain, Optional.empty(), Optional.empty(), Optional.of(uniqueEvolution));
   }

   public static GearExistenceData getDefault(
      ResourceLocation entity, int minEP, double epGain, Map<Holder<Enchantment>, Integer> engravings, List<UniqueGearEvolutionData> uniqueEvolution
   ) {
      return new GearExistenceData(entity, minEP, 2000000, epGain, Optional.empty(), Optional.of(engravings), Optional.of(uniqueEvolution));
   }

   public static GearExistenceData getDefault(ResourceLocation entity, int minEP, double epGain, List<UniqueGearEvolutionData> uniqueEvolution) {
      return new GearExistenceData(entity, minEP, 2000000, epGain, Optional.empty(), Optional.empty(), Optional.of(uniqueEvolution));
   }
}
