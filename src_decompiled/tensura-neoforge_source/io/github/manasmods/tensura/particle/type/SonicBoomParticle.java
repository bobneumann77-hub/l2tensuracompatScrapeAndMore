package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.particle.option.SonicBoomParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SonicBoomParticle extends HugeExplosionParticle {
   protected SonicBoomParticle(
      ClientLevel pLevel, double pX, double pY, double pZ, double pQuadSizeMultiplier, SpriteSet pSprites, SonicBoomParticleOptions options
   ) {
      super(pLevel, pX, pY, pZ, pQuadSizeMultiplier, pSprites);
      this.lifetime = 16;
      this.quadSize = 1.5F;
      this.scale(options.scale());
      this.setSpriteFromAge(pSprites);
      this.rCol = options.red();
      this.gCol = options.green();
      this.bCol = options.blue();
      this.alpha = options.alpha();
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SonicBoomParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         @NotNull SonicBoomParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i
      ) {
         SonicBoomParticle bubbleParticle = new SonicBoomParticle(clientLevel, d, e, f, g, this.sprite, options);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
