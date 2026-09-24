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

public record SimpleAuraParticleOptions(float red, float green, float blue, float alpha, float scale, float gravity, int life) implements ParticleOptions {
   public static StreamCodec<? super ByteBuf, SimpleAuraParticleOptions> STREAM_CODEC = StreamCodec.of((buf, option) -> {
      buf.writeFloat(option.red);
      buf.writeFloat(option.green);
      buf.writeFloat(option.blue);
      buf.writeFloat(option.alpha);
      buf.writeFloat(option.scale);
      buf.writeFloat(option.gravity);
      buf.writeInt(option.life);
   }, buf -> new SimpleAuraParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt()));
   public static MapCodec<SimpleAuraParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
            Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
            Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(options -> options.alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale),
            Codec.FLOAT.fieldOf("gravity").forGetter(options -> options.gravity),
            Codec.INT.fieldOf("life").forGetter(options -> options.life)
         )
         .apply(object, SimpleAuraParticleOptions::new)
   );

   public SimpleAuraParticleOptions(float red, float green, float blue, float alpha, float scale, float gravity) {
      this(red, green, blue, alpha, scale, gravity, 10);
   }

   @NotNull
   public ParticleType<SimpleAuraParticleOptions> getType() {
      return (ParticleType<SimpleAuraParticleOptions>)TensuraParticleTypes.SIMPLE_AURA.get();
   }
}
