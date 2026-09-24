package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.particle.option.SimpleGustParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleGustParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private float originalAlpha = 1.0F;

   SimpleGustParticle(
      ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet, SimpleGustParticleOptions options
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
      this.alpha = options.alpha();
      this.originalAlpha = options.alpha();
      this.scale(options.scale());
      this.gravity = options.gravity();
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.0074F;
         }

         if (this.isCloseToScopingPlayer()) {
            this.alpha = 0.0F;
         } else {
            this.alpha = Mth.lerp(0.05F, this.alpha, this.originalAlpha);
         }
      }
   }

   protected void setAlpha(float f) {
      super.setAlpha(f);
      this.originalAlpha = f;
   }

   private boolean isCloseToScopingPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localPlayer = minecraft.player;
      return localPlayer != null
         && localPlayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 9.0
         && minecraft.options.getCameraType().isFirstPerson()
         && localPlayer.isScoping();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleGustParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleGustParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SimpleGustParticle bubbleParticle = new SimpleGustParticle(clientLevel, d, e, f, g, h, i, this.sprite, options);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
