package io.github.manasmods.tensura.particle;

import io.github.manasmods.tensura.particle.type.BatsParticle;
import io.github.manasmods.tensura.particle.type.CloudParticle;
import io.github.manasmods.tensura.particle.type.DamageNumberParticle;
import io.github.manasmods.tensura.particle.type.LightningEffectParticle;
import io.github.manasmods.tensura.particle.type.LightningSparkParticle;
import io.github.manasmods.tensura.particle.type.ShockwaveParticle;
import io.github.manasmods.tensura.particle.type.SimpleAuraParticle;
import io.github.manasmods.tensura.particle.type.SimpleBubbleParticle;
import io.github.manasmods.tensura.particle.type.SimpleEffectParticle;
import io.github.manasmods.tensura.particle.type.SimpleFireParticle;
import io.github.manasmods.tensura.particle.type.SimpleGustParticle;
import io.github.manasmods.tensura.particle.type.SkyShotParticle;
import io.github.manasmods.tensura.particle.type.SnowFlakeParticle;
import io.github.manasmods.tensura.particle.type.SonicBoomParticle;
import io.github.manasmods.tensura.particle.type.SpiritParticle;
import io.github.manasmods.tensura.particle.type.TensuraDripParticle;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.FireworkParticles.FlashProvider;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.client.particle.ParticleProvider.Sprite;
import net.minecraft.client.particle.SoulParticle.EmissiveProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluids;

public class TensuraParticleRegister {
   public static void registerParticleProviders(TensuraParticleRegister.ParticleProviderRegisterStrategy strategy) {
      strategy.register((ParticleType)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.BLACK_LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.BLACK_LIGHTNING_EFFECT.get(), LightningEffectParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.LIGHTNING_EFFECT.get(), LightningEffectParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(), LightningEffectParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.NUMBER.get(), DamageNumberParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.BATS_MODE.get(), BatsParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.BLOSSOM.get(), SimpleFireParticle.HazyBlossomProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SOLAR_FLASH.get(), FlashProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SOUL.get(), EmissiveProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SNOWFLAKE.get(), SnowFlakeParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SNOWFLAKE_EFFECT.get(), LightningEffectParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.BLACK_FIRE.get(), SimpleFireParticle.FireProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.ILLUSION_FIRE.get(), SimpleFireParticle.IllusionFireProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.RED_FIRE.get(), SimpleFireParticle.FireProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.PLASMA_FIRE.get(), SimpleFireParticle.FireProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.HEAT_EFFECT.get(), SimpleFireParticle.FireProvider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SIMPLE_AURA.get(), SimpleAuraParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SIMPLE_BUBBLE.get(), SimpleBubbleParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SIMPLE_EFFECT.get(), SimpleEffectParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SIMPLE_GUST.get(), SimpleGustParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.CLOUD.get(), CloudParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SONIC_BOOM.get(), SonicBoomParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SHOCK_WAVE.get(), ShockwaveParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.REVERSE_SHOCK_WAVE.get(), ShockwaveParticle.Provider::new);
      strategy.register((ParticleType)TensuraParticleTypes.SPIRIT.get(), SpiritParticle.Provider::new);
      registerSpecialParticleProvider(
         strategy,
         (ParticleType)TensuraParticleTypes.WEATHER_MANIPULATION.get(),
         (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            SkyShotParticle particle = new SkyShotParticle(
               clientLevel, d, e, f, 220.0, TensuraParticleUtils.getColorlessWave(0.9F, 40.0F), SoundEvents.BEACON_ACTIVATE, 12.0F
            );
            particle.setGlowing(true);
            return particle;
         }
      );
      registerSpecialParticleProvider(
         strategy,
         (ParticleType)TensuraParticleTypes.FALLING_ACID.get(),
         (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            TensuraDripParticle dripParticle = new TensuraDripParticle.FallAndLandParticle(
               clientLevel, d, e, f, Fluids.EMPTY, (ParticleOptions)TensuraParticleTypes.LANDING_ACID.get()
            );
            dripParticle.setColor(0.42F, 0.87F, 0.23F);
            dripParticle.setGlowing(true);
            return dripParticle;
         }
      );
      registerSpecialParticleProvider(strategy, (ParticleType)TensuraParticleTypes.LANDING_ACID.get(), (particleOptions, clientLevel, d, e, f, g, h, i) -> {
         TensuraDripParticle dripParticle = new TensuraDripParticle.DripLandParticle(clientLevel, d, e, f, Fluids.EMPTY);
         dripParticle.setGlowing(true);
         dripParticle.setColor(0.42F, 0.87F, 0.23F);
         dripParticle.setLifetime((int)(8.0 / (Math.random() * 0.8 + 0.2)));
         return dripParticle;
      });
      registerSpecialParticleProvider(
         strategy,
         (ParticleType)TensuraParticleTypes.FALLING_HEAL_DROP.get(),
         (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            TensuraDripParticle dripParticle = new TensuraDripParticle.FallAndLandParticle(
               clientLevel, d, e, f, Fluids.EMPTY, (ParticleOptions)TensuraParticleTypes.LANDING_HEAL_DROP.get()
            );
            dripParticle.setColor(0.56F, 0.93F, 0.56F);
            dripParticle.setGlowing(true);
            return dripParticle;
         }
      );
      registerSpecialParticleProvider(
         strategy, (ParticleType)TensuraParticleTypes.LANDING_HEAL_DROP.get(), (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            TensuraDripParticle dripParticle = new TensuraDripParticle.DripLandParticle(clientLevel, d, e, f, Fluids.EMPTY);
            dripParticle.setGlowing(true);
            dripParticle.setColor(0.56F, 0.93F, 0.56F);
            dripParticle.setLifetime((int)(4.0 / (Math.random() * 0.8 + 0.2)));
            return dripParticle;
         }
      );
   }

   public static <T extends ParticleOptions> void registerSpecialParticleProvider(
      TensuraParticleRegister.ParticleProviderRegisterStrategy strategy, ParticleType<T> particle, Sprite<T> sprite
   ) {
      strategy.register(particle, spriteSet -> (particleOptions, clientLevel, d, e, f, g, h, i) -> {
         TextureSheetParticle textureSheetParticle = sprite.createParticle(particleOptions, clientLevel, d, e, f, g, h, i);
         if (textureSheetParticle != null) {
            textureSheetParticle.pickSprite(spriteSet);
         }

         return textureSheetParticle;
      });
   }

   public interface ParticleProviderRegisterStrategy {
      <T extends ParticleOptions> void register(ParticleType<T> var1, SpriteParticleRegistration<T> var2);
   }
}
