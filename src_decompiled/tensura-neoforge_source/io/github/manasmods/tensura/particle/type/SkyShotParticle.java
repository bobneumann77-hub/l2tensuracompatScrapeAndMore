package io.github.manasmods.tensura.particle.type;

import lombok.Generated;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public class SkyShotParticle extends TextureSheetParticle {
   protected double skyY;
   protected ParticleOptions skyParticle = null;
   protected SoundEvent skySound = null;
   protected float soundVolume = 10.0F;
   protected boolean isGlowing;

   public SkyShotParticle(ClientLevel clientLevel, double d, double e, double f, double skyY) {
      super(clientLevel, d, e, f);
      this.hasPhysics = false;
      this.gravity = -0.5F;
      this.skyY = skyY;
   }

   public SkyShotParticle(ClientLevel clientLevel, double d, double e, double f, double skyY, ParticleOptions particleOptions) {
      this(clientLevel, d, e, f, skyY);
      this.skyParticle = particleOptions;
   }

   public SkyShotParticle(ClientLevel clientLevel, double d, double e, double f, double skyY, ParticleOptions particleOptions, SoundEvent event) {
      this(clientLevel, d, e, f, skyY, particleOptions);
      this.skySound = event;
   }

   public SkyShotParticle(ClientLevel clientLevel, double d, double e, double f, double skyY, ParticleOptions particleOptions, SoundEvent event, float volume) {
      this(clientLevel, d, e, f, skyY, particleOptions, event);
      this.soundVolume = volume;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float f) {
      return this.isGlowing ? 240 : super.getLightColor(f);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (!this.removed) {
         this.yd = this.yd - this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if (!this.removed) {
            this.xd *= 0.98;
            this.yd *= 0.98;
            this.zd *= 0.98;
         }
      }
   }

   protected void postMoveUpdate() {
      if (this.y >= this.skyY && !this.removed) {
         this.remove();
         if (this.skyParticle != null) {
            this.level.addParticle(this.skyParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
         }

         if (this.skySound != null) {
            this.level.playLocalSound(this.x, this.y, this.z, this.skySound, SoundSource.WEATHER, this.soundVolume, 1.0F, false);
         }
      }
   }

   @Generated
   public void setGlowing(boolean isGlowing) {
      this.isGlowing = isGlowing;
   }
}
