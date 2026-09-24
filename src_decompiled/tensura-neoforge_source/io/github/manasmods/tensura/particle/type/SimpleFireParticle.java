package io.github.manasmods.tensura.particle.type;

import io.github.manasmods.tensura.ability.SkillUtils;
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
public class SimpleFireParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final boolean illusion;

   SimpleFireParticle(
      ClientLevel level,
      double xCoord,
      double yCoord,
      double zCoord,
      SpriteSet spriteSet,
      double xd,
      double yd,
      double zd,
      int lifeAve,
      float scale,
      float gravity,
      boolean illusion
   ) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.lifetime = lifeAve + (int)(Math.random() * 10.0);
      this.gravity = gravity;
      this.illusion = illusion;
      this.scale(scale + scale * this.random.nextFloat());
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public SimpleFireParticle(
      ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd, boolean illusion
   ) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.lifetime = 15 + (int)(Math.random() * 10.0);
      this.gravity = -0.1F;
      this.illusion = illusion;
      this.scale(this.random.nextFloat() * 1.75F + 1.0F);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public int getLightColor(float pPartialTick) {
      int i = super.getLightColor(pPartialTick);
      int k = i >> 16 & 0xFF;
      return 240 | k << 16;
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         if (this.isCloseToPlayer()) {
            this.alpha = 0.0F;
         } else {
            this.alpha = Mth.lerp(0.05F, this.alpha, 1.0F);
         }
      }
   }

   public void setSpriteFromAge(SpriteSet spriteSet) {
      if (!this.removed) {
         if (this.illusion) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && SkillUtils.canSeeIllusion(localPlayer)) {
               int age = this.age * 7 / 15 + 8 * this.lifetime / 15;
               this.setSprite(spriteSet.get(age, this.lifetime));
            } else {
               this.setSprite(spriteSet.get(this.age * 7 / 15, this.lifetime));
            }
         } else {
            this.setSprite(spriteSet.get(this.age, this.lifetime));
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

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FireProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public FireProvider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SimpleFireParticle particle = new SimpleFireParticle(clientLevel, d, e, f, this.sprite, g, h, i, false);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HazyBlossomProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HazyBlossomProvider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         SimpleFireParticle particle = new SimpleFireParticle(clientLevel, d, e, f, this.sprite, g, h, i, 70, 0.5F, 0.03F, false);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class IllusionFireProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public IllusionFireProvider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         return new SimpleFireParticle(clientLevel, d, e, f, this.sprite, g, h, i, true);
      }
   }
}
