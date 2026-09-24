package io.github.manasmods.tensura.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record KilnMoltenMaterial(ResourceLocation type, boolean magic, int red, int green, int blue, int alpha) {
   public static final Codec<KilnMoltenMaterial> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(KilnMoltenMaterial::type),
            Codec.BOOL.optionalFieldOf("magic", false).forGetter(KilnMoltenMaterial::magic),
            Codec.INT.optionalFieldOf("red", 0).forGetter(KilnMoltenMaterial::red),
            Codec.INT.optionalFieldOf("green", 0).forGetter(KilnMoltenMaterial::green),
            Codec.INT.optionalFieldOf("blue", 0).forGetter(KilnMoltenMaterial::blue),
            Codec.INT.optionalFieldOf("alpha", 255).forGetter(KilnMoltenMaterial::alpha)
         )
         .apply(instance, KilnMoltenMaterial::new)
   );

   public static KilnMoltenMaterial getDefault(ResourceLocation type, boolean magic, int red, int green, int blue, int alpha) {
      return new KilnMoltenMaterial(type, magic, red, green, blue, alpha);
   }
}
