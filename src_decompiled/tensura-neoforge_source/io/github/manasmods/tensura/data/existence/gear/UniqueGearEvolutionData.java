package io.github.manasmods.tensura.data.existence.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record UniqueGearEvolutionData(int EP, List<UniqueGearEvolutionData.Entry> attributes) {
   public static final Codec<UniqueGearEvolutionData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("EP").forGetter(UniqueGearEvolutionData::EP),
            Codec.list(UniqueGearEvolutionData.Entry.CODEC).optionalFieldOf("attributes", List.of()).forGetter(UniqueGearEvolutionData::attributes)
         )
         .apply(instance, UniqueGearEvolutionData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, UniqueGearEvolutionData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      UniqueGearEvolutionData::EP,
      UniqueGearEvolutionData.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
      UniqueGearEvolutionData::attributes,
      UniqueGearEvolutionData::new
   );

   public static UniqueGearEvolutionData getDefault(int EP, List<UniqueGearEvolutionData.Entry> attributes) {
      return new UniqueGearEvolutionData(EP, attributes);
   }

   public record Entry(Holder<Attribute> attribute, double amount, EquipmentSlotGroup slot) {
      public static final Codec<UniqueGearEvolutionData.Entry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Attribute.CODEC.fieldOf("type").forGetter(UniqueGearEvolutionData.Entry::attribute),
               Codec.DOUBLE.fieldOf("amount").forGetter(UniqueGearEvolutionData.Entry::amount),
               EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(UniqueGearEvolutionData.Entry::slot)
            )
            .apply(instance, UniqueGearEvolutionData.Entry::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, UniqueGearEvolutionData.Entry> STREAM_CODEC = StreamCodec.composite(
         Attribute.STREAM_CODEC,
         UniqueGearEvolutionData.Entry::attribute,
         ByteBufCodecs.DOUBLE,
         UniqueGearEvolutionData.Entry::amount,
         EquipmentSlotGroup.STREAM_CODEC,
         UniqueGearEvolutionData.Entry::slot,
         UniqueGearEvolutionData.Entry::new
      );
   }
}
