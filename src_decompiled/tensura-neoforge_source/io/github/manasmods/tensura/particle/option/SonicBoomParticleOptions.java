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

public record SonicBoomParticleOptions(float red, float green, float blue, float alpha, float scale) implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, SonicBoomParticleOptions> STREAM_CODEC = StreamCodec.of((buf, option) -> {
      buf.writeFloat(option.red);
      buf.writeFloat(option.green);
      buf.writeFloat(option.blue);
      buf.writeFloat(option.alpha);
      buf.writeFloat(option.scale);
   }, buf -> new SonicBoomParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));
   public static MapCodec<SonicBoomParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
            Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
            Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale)
         )
         .apply(object, SonicBoomParticleOptions::new)
   );

   @NotNull
   public ParticleType<SonicBoomParticleOptions> getType() {
      return (ParticleType<SonicBoomParticleOptions>)TensuraParticleTypes.SONIC_BOOM.get();
   }
}
