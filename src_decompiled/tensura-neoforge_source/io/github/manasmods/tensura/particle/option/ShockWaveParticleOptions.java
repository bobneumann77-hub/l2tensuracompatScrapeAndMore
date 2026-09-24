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

public record ShockWaveParticleOptions(float red, float green, float blue, float alpha, float scale, float gravity, boolean reversed) implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, ShockWaveParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeFloat(option.red);
         buf.writeFloat(option.green);
         buf.writeFloat(option.blue);
         buf.writeFloat(option.alpha);
         buf.writeFloat(option.scale);
         buf.writeFloat(option.gravity);
         buf.writeBoolean(option.reversed);
      },
      buf -> new ShockWaveParticleOptions(
         buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean()
      )
   );
   public static MapCodec<ShockWaveParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
            Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
            Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale),
            Codec.FLOAT.fieldOf("gravity").forGetter(options -> options.gravity),
            Codec.BOOL.fieldOf("reversed").forGetter(options -> options.reversed)
         )
         .apply(object, ShockWaveParticleOptions::new)
   );

   @NotNull
   public ParticleType<ShockWaveParticleOptions> getType() {
      return this.reversed ? (ParticleType)TensuraParticleTypes.REVERSE_SHOCK_WAVE.get() : (ParticleType)TensuraParticleTypes.SHOCK_WAVE.get();
   }
}
