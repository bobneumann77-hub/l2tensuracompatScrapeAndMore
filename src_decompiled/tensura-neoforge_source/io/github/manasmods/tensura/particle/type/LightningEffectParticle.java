package io.github.manasmods.tensura.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightningEffectParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   LightningEffectParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
      super(clientLevel, d, e, f);
      this.xd = g;
      this.yd = h;
      this.zd = i;
      this.quadSize *= 1.0F;
      this.scale(3.0F);
      this.gravity = 0.0F;
      this.lifetime = 5 + (int)(Math.random() * 15.0);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSprite(this.sprites.get(this.level.random));
         if (this.isCloseToPlayer()) {
            this.alpha = 0.0F;
         } else {
            this.alpha = Mth.lerp(0.05F, this.alpha, 1.0F);
         }
      }
   }

   private boolean isCloseToPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localPlayer = minecraft.player;
      return localPlayer != null
         && localPlayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 1.0
         && minecraft.options.getCameraType().isFirstPerson();
   }

   public int getLightColor(float pPartialTick) {
      return 240;
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
         LightningEffectParticle bubbleParticle = new LightningEffectParticle(clientLevel, d, e, f, g, h, i, this.sprite);
         bubbleParticle.pickSprite(this.sprite);
         return bubbleParticle;
      }
   }
}
