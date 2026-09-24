package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.particle.option.SimpleBubbleParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SimpleBubbleParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   SimpleBubbleParticle(
      ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet, SimpleBubbleParticleOptions options
   ) {
      super(clientLevel, d, e, f);
      this.friction = 0.92F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd = g;
      this.yd = h;
      this.zd = i;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= 1.0F;
      this.lifetime = options.life() + (int)(Math.random() * 5.0);
      float random = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = options.red() * random;
      this.gCol = options.green() * random;
      this.bCol = options.blue() * random;
      this.scale(options.scale());
      this.gravity = options.gravity();
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - this.lifetime / 2) / this.lifetime);
         }

         if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.0074F;
         }
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleBubbleParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleBubbleParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SimpleBubbleParticle bubbleParticle = new SimpleBubbleParticle(clientLevel, d, e, f, g, h, i, this.sprite, options);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
