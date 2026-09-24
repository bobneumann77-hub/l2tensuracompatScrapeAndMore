package io.github.manasmods.tensura.data.slotting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SlottingStatusEffect(ResourceLocation id, int level, int ticks, float range) {
   public static final Codec<SlottingStatusEffect> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("effectID").forGetter(SlottingStatusEffect::id),
            Codec.INT.optionalFieldOf("level", 0).forGetter(SlottingStatusEffect::level),
            Codec.INT.optionalFieldOf("ticks", 0).forGetter(SlottingStatusEffect::ticks),
            Codec.FLOAT.optionalFieldOf("range", 0.0F).forGetter(SlottingStatusEffect::range)
         )
         .apply(instance, SlottingStatusEffect::new)
   );
}
