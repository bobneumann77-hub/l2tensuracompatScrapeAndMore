package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
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
public class SimpleAuraParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private float originalAlpha;

   SimpleAuraParticle(
      ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet, SimpleAuraParticleOptions options
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
      this.originalAlpha = options.alpha();
      this.setSpriteFromAge(spriteSet);
      if (this.isCloseToPlayer()) {
         this.setAlpha(0.0F);
      }
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         if (this.age % 2 == 0) {
            this.setSpriteFromAge(this.sprites);
         }

         if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - this.lifetime / 2) / this.lifetime);
         }

         if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.0074F;
         }

         if (this.isCloseToPlayer()) {
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

   private boolean isCloseToPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localPlayer = minecraft.player;
      return localPlayer != null
         && localPlayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 1.0
         && minecraft.options.getCameraType().isFirstPerson();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleAuraParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleAuraParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SimpleAuraParticle bubbleParticle = new SimpleAuraParticle(clientLevel, d, e, f, g, h, i, this.sprite, options);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
