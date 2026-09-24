package io.github.manasmods.tensura.particle.type;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowFlakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   SnowFlakeParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.friction = 0.9F;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize *= 0.65F;
      this.scale(2.0F);
      this.gravity = 0.001F;
      this.lifetime = 20 + (int)(Math.random() * 80.0);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd = this.xd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
         this.zd = this.zd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
         this.yd = this.yd - this.gravity;
         this.xd = this.xd * this.friction;
         this.zd = this.zd * this.friction;
         this.yd = this.yd * this.friction;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }

         if (this.age > this.lifetime / 2 && this.gravity < 0.225F) {
            this.gravity += 0.001F;
         }
      } else {
         this.remove();
      }

      this.setSpriteFromAge(this.sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SnowFlakeParticle bubbleParticle = new SnowFlakeParticle(clientLevel, d, e, f, this.sprite, g, h, i);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
