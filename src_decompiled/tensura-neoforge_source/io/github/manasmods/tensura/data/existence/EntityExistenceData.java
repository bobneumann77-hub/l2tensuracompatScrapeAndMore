package io.github.manasmods.tensura.data.existence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record EntityExistenceData(
   ResourceLocation entity,
   int spiritualHP,
   int minMagicule,
   int maxMagicule,
   int minAura,
   int maxAura,
   Optional<List<ResourceLocation>> abilities,
   Optional<ResourceLocation> evolution,
   Optional<Map<ResourceLocation, Double>> abilitiesRandom
) {
   public static final Codec<EntityExistenceData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(EntityExistenceData::entity),
            Codec.INT.fieldOf("spiritualHealth").forGetter(EntityExistenceData::spiritualHP),
            Codec.INT.fieldOf("min_magicule").forGetter(EntityExistenceData::minMagicule),
            Codec.INT.fieldOf("max_magicule").forGetter(EntityExistenceData::maxMagicule),
            Codec.INT.optionalFieldOf("min_aura", 50).forGetter(EntityExistenceData::minAura),
            Codec.INT.optionalFieldOf("max_aura", 50).forGetter(EntityExistenceData::maxAura),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("abilities").forGetter(EntityExistenceData::abilities),
            ResourceLocation.CODEC.optionalFieldOf("evolution").forGetter(EntityExistenceData::evolution),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE).optionalFieldOf("abilities_random").forGetter(EntityExistenceData::abilitiesRandom)
         )
         .apply(instance, EntityExistenceData::new)
   );

   public static EntityExistenceData getDefault(ResourceLocation entity, int minMagicule, int maxMagicule) {
      return new EntityExistenceData(entity, 40, minMagicule, maxMagicule, 50, 50, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static EntityExistenceData getDefault(ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule) {
      return new EntityExistenceData(entity, spiritualHP, minMagicule, maxMagicule, 50, 50, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static EntityExistenceData getDefault(ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, List<ResourceLocation> abilities) {
      return new EntityExistenceData(entity, spiritualHP, minMagicule, maxMagicule, 50, 50, Optional.ofNullable(abilities), Optional.empty(), Optional.empty());
   }

   public static EntityExistenceData getDefault(ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, ResourceLocation evolution) {
      return new EntityExistenceData(entity, spiritualHP, minMagicule, maxMagicule, 50, 50, Optional.empty(), Optional.ofNullable(evolution), Optional.empty());
   }

   public static EntityExistenceData getDefault(
      ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, List<ResourceLocation> abilities, ResourceLocation evolution
   ) {
      return new EntityExistenceData(
         entity, spiritualHP, minMagicule, maxMagicule, 50, 50, Optional.ofNullable(abilities), Optional.ofNullable(evolution), Optional.empty()
      );
   }

   public static EntityExistenceData getDefault(ResourceLocation entity, int minMagicule, int maxMagicule, int minAura, int maxAura) {
      return new EntityExistenceData(entity, 40, minMagicule, maxMagicule, minAura, maxAura, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static EntityExistenceData getDefault(ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, int minAura, int maxAura) {
      return new EntityExistenceData(entity, spiritualHP, minMagicule, maxMagicule, minAura, maxAura, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static EntityExistenceData getDefault(
      ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, int minAura, int maxAura, List<ResourceLocation> abilities
   ) {
      return new EntityExistenceData(
         entity, spiritualHP, minMagicule, maxMagicule, minAura, maxAura, Optional.ofNullable(abilities), Optional.empty(), Optional.empty()
      );
   }

   public static EntityExistenceData getDefault(
      ResourceLocation entity, int spiritualHP, int minMagicule, int maxMagicule, int minAura, int maxAura, ResourceLocation evolution
   ) {
      return new EntityExistenceData(
         entity, spiritualHP, minMagicule, maxMagicule, minAura, maxAura, Optional.empty(), Optional.ofNullable(evolution), Optional.empty()
      );
   }

   public static EntityExistenceData getDefault(
      ResourceLocation entity,
      int spiritualHP,
      int minMagicule,
      int maxMagicule,
      int minAura,
      int maxAura,
      List<ResourceLocation> abilities,
      ResourceLocation evolution
   ) {
      return new EntityExistenceData(
         entity, spiritualHP, minMagicule, maxMagicule, minAura, maxAura, Optional.ofNullable(abilities), Optional.ofNullable(evolution), Optional.empty()
      );
   }

   public static EntityExistenceData getDefault(
      ResourceLocation entity,
      int spiritualHP,
      int minMagicule,
      int maxMagicule,
      int minAura,
      int maxAura,
      List<ResourceLocation> abilities,
      ResourceLocation evolution,
      Map<ResourceLocation, Double> randomAbilities
   ) {
      return new EntityExistenceData(
         entity,
         spiritualHP,
         minMagicule,
         maxMagicule,
         minAura,
         maxAura,
         Optional.ofNullable(abilities),
         Optional.ofNullable(evolution),
         Optional.ofNullable(randomAbilities)
      );
   }
}
