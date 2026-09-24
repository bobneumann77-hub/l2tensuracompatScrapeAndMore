package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.particle.option.SpiritParticleOptions;
import lombok.Generated;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SpiritParticle extends TextureSheetParticle {
   protected float originalAlpha;
   protected final Vec3 center;
   protected final double radius;
   protected final double orbitSpeed;
   protected double angle;
   protected boolean isGlowing = true;

   public SpiritParticle(ClientLevel clientLevel, double d, double e, double f, SpriteSet spriteSet, SpiritParticleOptions options) {
      super(clientLevel, d, e, f);
      this.alpha = options.alpha();
      this.originalAlpha = options.alpha();
      float brightness = this.random.nextFloat() * 0.2F + 0.9F;
      this.rCol = options.red() * brightness;
      this.gCol = options.green() * brightness;
      this.bCol = options.blue() * brightness;
      this.setSize(options.scale(), options.scale());
      this.center = new Vec3(options.x(), options.y(), options.z());
      this.radius = options.radius();
      this.lifetime = options.averageLife() + this.level.getRandom().nextInt(-20, 20);
      double dx = d - options.x();
      double dz = f - options.z();
      this.angle = dx == 0.0 && dz == 0.0 ? this.random.nextDouble() * Math.PI * 2.0 : Math.atan2(dz, dx);
      this.orbitSpeed = (0.03 + this.random.nextDouble() * 0.02) * (this.random.nextBoolean() ? 1 : -1);
      this.x = this.center.x + this.radius * Math.cos(this.angle);
      this.z = this.center.z + this.radius * Math.sin(this.angle);
      this.y = this.center.y + Math.sin(this.angle * 2.0) * 0.2;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.pickSprite(spriteSet);
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
      this.age++;
      if (this.age >= this.lifetime) {
         this.remove();
      }

      if (!this.removed) {
         this.angle = this.angle + this.orbitSpeed;
         this.x = this.center.x + this.radius * Math.cos(this.angle);
         this.z = this.center.z + this.radius * Math.sin(this.angle);
         this.y = this.center.y + Math.sin(this.angle * 2.0) * 0.2;
         if (this.age > this.lifetime / 2) {
            this.setAlpha(this.originalAlpha * (1.0F - (this.age - this.lifetime / 2.0F) / (this.lifetime / 2.0F)));
         }
      }
   }

   @Generated
   public void setGlowing(boolean isGlowing) {
      this.isGlowing = isGlowing;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SpiritParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SpiritParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         return new SpiritParticle(clientLevel, d, e, f, this.sprite, options);
      }
   }
}
