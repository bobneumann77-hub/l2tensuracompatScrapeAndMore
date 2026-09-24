package io.github.manasmods.tensura.particle.option;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NumberParticleOptions(float value, int color, int shadowColor, float alpha, float scale, float gravity, int averageLife, int index)
   implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, NumberParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeFloat(option.value);
         buf.writeInt(option.color);
         buf.writeInt(option.shadowColor);
         buf.writeFloat(option.alpha);
         buf.writeFloat(option.scale);
         buf.writeFloat(option.gravity);
         buf.writeInt(option.averageLife);
         buf.writeInt(option.index);
      },
      buf -> new NumberParticleOptions(
         buf.readFloat(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt()
      )
   );
   public static MapCodec<NumberParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("value").forGetter(options -> options.value),
            Codec.INT.fieldOf("color").forGetter(options -> options.color),
            Codec.INT.fieldOf("shadowColor").forGetter(options -> options.shadowColor),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale),
            Codec.FLOAT.fieldOf("gravity").forGetter(options -> options.gravity),
            Codec.INT.fieldOf("life").forGetter(options -> options.averageLife),
            Codec.INT.fieldOf("index").forGetter(options -> options.index)
         )
         .apply(object, NumberParticleOptions::new)
   );

   @NotNull
   public ParticleType<NumberParticleOptions> getType() {
      return (ParticleType<NumberParticleOptions>)TensuraParticleTypes.NUMBER.get();
   }
}
