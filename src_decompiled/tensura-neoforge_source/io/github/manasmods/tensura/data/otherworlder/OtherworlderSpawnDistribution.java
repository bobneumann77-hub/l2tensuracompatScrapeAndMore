package io.github.manasmods.tensura.data.otherworlder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record OtherworlderSpawnDistribution(ResourceLocation entity, double chance) {
   public static final Codec<OtherworlderSpawnDistribution> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(OtherworlderSpawnDistribution::entity),
            Codec.DOUBLE.fieldOf("chance").forGetter(OtherworlderSpawnDistribution::chance)
         )
         .apply(instance, OtherworlderSpawnDistribution::new)
   );

   public static OtherworlderSpawnDistribution getDefault(ResourceLocation entity, double chance) {
      return new OtherworlderSpawnDistribution(entity, chance);
   }
}
