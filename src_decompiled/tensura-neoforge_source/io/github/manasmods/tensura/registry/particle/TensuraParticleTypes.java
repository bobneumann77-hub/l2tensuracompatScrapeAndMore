package io.github.manasmods.tensura.registry.particle;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.particle.TensuraParticleType;
import io.github.manasmods.tensura.particle.option.CloudParticleOptions;
import io.github.manasmods.tensura.particle.option.NumberParticleOptions;
import io.github.manasmods.tensura.particle.option.ShockWaveParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleBubbleParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleEffectParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleGustParticleOptions;
import io.github.manasmods.tensura.particle.option.SonicBoomParticleOptions;
import io.github.manasmods.tensura.particle.option.SpiritParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class TensuraParticleTypes {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create("tensura", Registries.PARTICLE_TYPE);
   public static final RegistrySupplier<TensuraParticleType> LIGHTNING_SPARK = PARTICLE_TYPES.register("lightning_spark", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> BLACK_LIGHTNING_SPARK = PARTICLE_TYPES.register(
      "black_lightning_spark", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> DARK_PURPLE_LIGHTNING_SPARK = PARTICLE_TYPES.register(
      "dark_purple_lightning_spark", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> DARK_RED_LIGHTNING_SPARK = PARTICLE_TYPES.register(
      "dark_red_lightning_spark", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> PURPLE_LIGHTNING_SPARK = PARTICLE_TYPES.register(
      "purple_lightning_spark", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> YELLOW_LIGHTNING_SPARK = PARTICLE_TYPES.register(
      "yellow_lightning_spark", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> BLACK_LIGHTNING_EFFECT = PARTICLE_TYPES.register(
      "black_lightning_effect", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> LIGHTNING_EFFECT = PARTICLE_TYPES.register(
      "lightning_effect", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> WEATHER_MANIPULATION = PARTICLE_TYPES.register(
      "weather_manipulation", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> FALLING_ACID = PARTICLE_TYPES.register("falling_acid", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> LANDING_ACID = PARTICLE_TYPES.register("landing_acid", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> FALLING_HEAL_DROP = PARTICLE_TYPES.register(
      "falling_heal_drop", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> LANDING_HEAL_DROP = PARTICLE_TYPES.register(
      "landing_heal_drop", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> BATS_MODE = PARTICLE_TYPES.register("bats_mode", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> BLOSSOM = PARTICLE_TYPES.register("blossom", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> SOLAR_FLASH = PARTICLE_TYPES.register("solar_flash", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> SOUL = PARTICLE_TYPES.register("soul", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> SNOWFLAKE = PARTICLE_TYPES.register("snowflake", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> SNOWFLAKE_EFFECT = PARTICLE_TYPES.register(
      "snowflake_effect", () -> new TensuraParticleType(false)
   );
   public static final RegistrySupplier<TensuraParticleType> BLACK_FIRE = PARTICLE_TYPES.register("black_fire", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> ILLUSION_FIRE = PARTICLE_TYPES.register("illusion_fire", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> RED_FIRE = PARTICLE_TYPES.register("red_fire", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> PLASMA_FIRE = PARTICLE_TYPES.register("plasma_fire", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<TensuraParticleType> HEAT_EFFECT = PARTICLE_TYPES.register("heat_effect", () -> new TensuraParticleType(false));
   public static final RegistrySupplier<ParticleType<NumberParticleOptions>> NUMBER = PARTICLE_TYPES.register(
      "number", () -> new ParticleType<NumberParticleOptions>(true) {
         @NotNull
         public MapCodec<NumberParticleOptions> codec() {
            return NumberParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, NumberParticleOptions> streamCodec() {
            return NumberParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SimpleAuraParticleOptions>> SIMPLE_AURA = PARTICLE_TYPES.register(
      "simple_aura", () -> new ParticleType<SimpleAuraParticleOptions>(true) {
         @NotNull
         public MapCodec<SimpleAuraParticleOptions> codec() {
            return SimpleAuraParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SimpleAuraParticleOptions> streamCodec() {
            return SimpleAuraParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SimpleBubbleParticleOptions>> SIMPLE_BUBBLE = PARTICLE_TYPES.register(
      "simple_bubble", () -> new ParticleType<SimpleBubbleParticleOptions>(true) {
         @NotNull
         public MapCodec<SimpleBubbleParticleOptions> codec() {
            return SimpleBubbleParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SimpleBubbleParticleOptions> streamCodec() {
            return SimpleBubbleParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SimpleEffectParticleOptions>> SIMPLE_EFFECT = PARTICLE_TYPES.register(
      "simple_effect", () -> new ParticleType<SimpleEffectParticleOptions>(true) {
         @NotNull
         public MapCodec<SimpleEffectParticleOptions> codec() {
            return SimpleEffectParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SimpleEffectParticleOptions> streamCodec() {
            return SimpleEffectParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SimpleGustParticleOptions>> SIMPLE_GUST = PARTICLE_TYPES.register(
      "simple_gust", () -> new ParticleType<SimpleGustParticleOptions>(true) {
         @NotNull
         public MapCodec<SimpleGustParticleOptions> codec() {
            return SimpleGustParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SimpleGustParticleOptions> streamCodec() {
            return SimpleGustParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<CloudParticleOptions>> CLOUD = PARTICLE_TYPES.register(
      "cloud", () -> new ParticleType<CloudParticleOptions>(true) {
         @NotNull
         public MapCodec<CloudParticleOptions> codec() {
            return CloudParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, CloudParticleOptions> streamCodec() {
            return CloudParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SpiritParticleOptions>> SPIRIT = PARTICLE_TYPES.register(
      "spirit", () -> new ParticleType<SpiritParticleOptions>(true) {
         @NotNull
         public MapCodec<SpiritParticleOptions> codec() {
            return SpiritParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SpiritParticleOptions> streamCodec() {
            return SpiritParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<SonicBoomParticleOptions>> SONIC_BOOM = PARTICLE_TYPES.register(
      "sonic_boom", () -> new ParticleType<SonicBoomParticleOptions>(true) {
         @NotNull
         public MapCodec<SonicBoomParticleOptions> codec() {
            return SonicBoomParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, SonicBoomParticleOptions> streamCodec() {
            return SonicBoomParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<ShockWaveParticleOptions>> SHOCK_WAVE = PARTICLE_TYPES.register(
      "shock_wave", () -> new ParticleType<ShockWaveParticleOptions>(true) {
         @NotNull
         public MapCodec<ShockWaveParticleOptions> codec() {
            return ShockWaveParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, ShockWaveParticleOptions> streamCodec() {
            return ShockWaveParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final RegistrySupplier<ParticleType<ShockWaveParticleOptions>> REVERSE_SHOCK_WAVE = PARTICLE_TYPES.register(
      "reverse_shock_wave", () -> new ParticleType<ShockWaveParticleOptions>(true) {
         @NotNull
         public MapCodec<ShockWaveParticleOptions> codec() {
            return ShockWaveParticleOptions.MAP_CODEC;
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, ShockWaveParticleOptions> streamCodec() {
            return ShockWaveParticleOptions.STREAM_CODEC;
         }
      }
   );

   public static void init() {
      PARTICLE_TYPES.register();
   }
}
