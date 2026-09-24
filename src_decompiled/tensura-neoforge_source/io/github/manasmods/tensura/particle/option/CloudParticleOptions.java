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

public record CloudParticleOptions(float red, float green, float blue, float alpha, float minAlpha, float scale, float gravity, int averageLife)
   implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, CloudParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeFloat(option.red);
         buf.writeFloat(option.green);
         buf.writeFloat(option.blue);
         buf.writeFloat(option.alpha);
         buf.writeFloat(option.minAlpha);
         buf.writeFloat(option.scale);
         buf.writeFloat(option.gravity);
         buf.writeInt(option.averageLife);
      },
      buf -> new CloudParticleOptions(
         buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt()
      )
   );
   public static MapCodec<CloudParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
            Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
            Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("minAlpha").forGetter(options -> options.minAlpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale),
            Codec.FLOAT.fieldOf("gravity").forGetter(options -> options.gravity),
            Codec.INT.fieldOf("life").forGetter(options -> options.averageLife)
         )
         .apply(object, CloudParticleOptions::new)
   );

   @NotNull
   public ParticleType<CloudParticleOptions> getType() {
      return (ParticleType<CloudParticleOptions>)TensuraParticleTypes.CLOUD.get();
   }
}
