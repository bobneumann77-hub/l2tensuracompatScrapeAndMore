package io.github.manasmods.tensura.neoforge.data;

import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class TensuraParticleDescriptionProvider extends ParticleDescriptionProvider {
   public TensuraParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
      super(output, fileHelper);
   }

   protected void addDescriptions() {
      this.spriteSet((ParticleType)TensuraParticleTypes.LIGHTNING_SPARK.get(), this.getModLocation("lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.BLACK_LIGHTNING_SPARK.get(), this.getModLocation("black_lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get(), this.getModLocation("dark_red_lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get(), this.getModLocation("dark_purple_lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(), this.getModLocation("purple_lightning_spark"), 6, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), this.getModLocation("yellow_lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.LIGHTNING_EFFECT.get(), this.getModLocation("lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.BLACK_LIGHTNING_EFFECT.get(), this.getModLocation("black_lightning_spark"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SPIRIT.get(), this.getMinecraftLocation("flash"), new ResourceLocation[0]);
      this.spriteSet((ParticleType)TensuraParticleTypes.WEATHER_MANIPULATION.get(), this.getMinecraftLocation("flash"), new ResourceLocation[0]);
      this.spriteSet((ParticleType)TensuraParticleTypes.BATS_MODE.get(), this.getModLocation("bats_particle"), 2, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.BLOSSOM.get(), this.getModLocation("blossom"), new ResourceLocation[0]);
      this.spriteSet((ParticleType)TensuraParticleTypes.SOLAR_FLASH.get(), this.getModLocation("solar_flash"), 1, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SOUL.get(), this.getModLocation("trsoul"), 11, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SNOWFLAKE.get(), this.getModLocation("snowflake"), 2, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SNOWFLAKE_EFFECT.get(), this.getModLocation("snowflake"), 2, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.BLACK_FIRE.get(), this.getModLocation("black_fire_particle"), 8, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.RED_FIRE.get(), this.getModLocation("fire_particle"), 8, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.PLASMA_FIRE.get(), this.getModLocation("plasma_fire_particle"), 8, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.HEAT_EFFECT.get(), this.getModLocation("heat_particle"), 6, false);
      this.spriteSet(
         (ParticleType)TensuraParticleTypes.ILLUSION_FIRE.get(),
         this.getModLocation("fire_particle_0"),
         new ResourceLocation[]{
            this.getModLocation("fire_particle_1"),
            this.getModLocation("fire_particle_2"),
            this.getModLocation("fire_particle_3"),
            this.getModLocation("fire_particle_4"),
            this.getModLocation("fire_particle_5"),
            this.getModLocation("fire_particle_6"),
            this.getModLocation("fire_particle_7"),
            this.getModLocation("blue_fire_particle_0"),
            this.getModLocation("blue_fire_particle_1"),
            this.getModLocation("blue_fire_particle_2"),
            this.getModLocation("blue_fire_particle_3"),
            this.getModLocation("blue_fire_particle_4"),
            this.getModLocation("blue_fire_particle_5"),
            this.getModLocation("blue_fire_particle_6"),
            this.getModLocation("blue_fire_particle_7")
         }
      );
      this.spriteSet((ParticleType)TensuraParticleTypes.SIMPLE_AURA.get(), this.getModLocation("aura"), 6, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SIMPLE_BUBBLE.get(), this.getModLocation("bubble_pop"), 6, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SIMPLE_EFFECT.get(), this.getMinecraftLocation("generic"), 8, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SIMPLE_GUST.get(), this.getMinecraftLocation("small_gust"), 7, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.CLOUD.get(), this.getMinecraftLocation("big_smoke"), 9, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SONIC_BOOM.get(), this.getModLocation("sonic_boom"), 16, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.SHOCK_WAVE.get(), this.getModLocation("shock_wave"), 9, false);
      this.spriteSet((ParticleType)TensuraParticleTypes.REVERSE_SHOCK_WAVE.get(), this.getModLocation("shock_wave"), 6, true);
      this.sprite((ParticleType)TensuraParticleTypes.FALLING_ACID.get(), this.getMinecraftLocation("drip_fall"));
      this.sprite((ParticleType)TensuraParticleTypes.LANDING_ACID.get(), this.getMinecraftLocation("drip_land"));
      this.sprite((ParticleType)TensuraParticleTypes.FALLING_HEAL_DROP.get(), this.getMinecraftLocation("drip_fall"));
      this.sprite((ParticleType)TensuraParticleTypes.LANDING_HEAL_DROP.get(), this.getMinecraftLocation("drip_land"));
   }

   private ResourceLocation getModLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("tensura", name);
   }

   private ResourceLocation getMinecraftLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("minecraft", name);
   }
}
