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

public record SpiritParticleOptions(double x, double y, double z, double radius, float red, float green, float blue, float alpha, float scale, int averageLife)
   implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, SpiritParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeDouble(option.x);
         buf.writeDouble(option.y);
         buf.writeDouble(option.z);
         buf.writeDouble(option.radius);
         buf.writeFloat(option.red);
         buf.writeFloat(option.green);
         buf.writeFloat(option.blue);
         buf.writeFloat(option.alpha);
         buf.writeFloat(option.scale);
         buf.writeInt(option.averageLife);
      },
      buf -> new SpiritParticleOptions(
         buf.readDouble(),
         buf.readDouble(),
         buf.readDouble(),
         buf.readDouble(),
         buf.readFloat(),
         buf.readFloat(),
         buf.readFloat(),
         buf.readFloat(),
         buf.readFloat(),
         buf.readInt()
      )
   );
   public static MapCodec<SpiritParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.DOUBLE.fieldOf("x").forGetter(options -> options.x),
            Codec.DOUBLE.fieldOf("y").forGetter(options -> options.y),
            Codec.DOUBLE.fieldOf("z").forGetter(options -> options.z),
            Codec.DOUBLE.fieldOf("radius").forGetter(options -> options.radius),
            Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
            Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
            Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale),
            Codec.INT.fieldOf("life").forGetter(options -> options.averageLife)
         )
         .apply(object, SpiritParticleOptions::new)
   );

   @NotNull
   public ParticleType<SpiritParticleOptions> getType() {
      return (ParticleType<SpiritParticleOptions>)TensuraParticleTypes.SPIRIT.get();
   }
}
