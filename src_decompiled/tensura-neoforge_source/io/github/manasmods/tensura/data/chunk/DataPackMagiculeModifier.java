package io.github.manasmods.tensura.data.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DataPackMagiculeModifier(DataPackMagiculeModifier.Mode mode, double value) {
   public static final Codec<DataPackMagiculeModifier> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("mode").xmap(DataPackMagiculeModifier.Mode::valueOf, Enum::name).forGetter(DataPackMagiculeModifier::mode),
            Codec.DOUBLE.fieldOf("value").forGetter(DataPackMagiculeModifier::value)
         )
         .apply(instance, DataPackMagiculeModifier::new)
   );

   public enum Mode {
      ADD,
      MULTIPLY;
   }
}
