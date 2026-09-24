package io.github.manasmods.tensura.particle.type;

import lombok.Generated;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class TensuraDripParticle extends TextureSheetParticle {
   protected boolean isGlowing;
   private final Fluid type;

   public TensuraDripParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid) {
      super(clientLevel, d, e, f);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = fluid;
   }

   protected Fluid getType() {
      return this.type;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float f) {
      return this.isGlowing ? 240 : super.getLightColor(f);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.preMoveUpdate();
      if (!this.removed) {
         this.yd = this.yd - this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if (!this.removed) {
            this.xd *= 0.98F;
            this.yd *= 0.98F;
            this.zd *= 0.98F;
            if (this.type != Fluids.EMPTY) {
               BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
               FluidState fluidState = this.level.getFluidState(blockPos);
               if (fluidState.getType() == this.type && this.y < blockPos.getY() + fluidState.getHeight(this.level, blockPos)) {
                  this.remove();
               }
            }
         }
      }
   }

   protected void preMoveUpdate() {
      if (this.lifetime-- <= 0) {
         this.remove();
      }
   }

   protected void postMoveUpdate() {
   }

   @Generated
   public void setGlowing(boolean isGlowing) {
      this.isGlowing = isGlowing;
   }

   public static class CoolingDripHangParticle extends TensuraDripParticle.DripHangParticle {
      public CoolingDripHangParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, ParticleOptions particleOptions) {
         super(clientLevel, d, e, f, fluid, particleOptions);
      }

      @Override
      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (40 - this.lifetime + 16);
         this.bCol = 4.0F / (40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   private static class DripHangParticle extends TensuraDripParticle {
      private final ParticleOptions fallingParticle;

      public DripHangParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, ParticleOptions particleOptions) {
         super(clientLevel, d, e, f, fluid);
         this.fallingParticle = particleOptions;
         this.gravity *= 0.02F;
         this.lifetime = 40;
      }

      @Override
      protected void preMoveUpdate() {
         if (this.lifetime-- <= 0) {
            this.remove();
            this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }
      }

      @Override
      protected void postMoveUpdate() {
         this.xd *= 0.02;
         this.yd *= 0.02;
         this.zd *= 0.02;
      }
   }

   public static class DripLandParticle extends TensuraDripParticle {
      public DripLandParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid) {
         super(clientLevel, d, e, f, fluid);
         this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      }
   }

   public static class DripstoneFallAndLandParticle extends TensuraDripParticle.FallAndLandParticle {
      public DripstoneFallAndLandParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, ParticleOptions particleOptions) {
         super(clientLevel, d, e, f, fluid, particleOptions);
      }

      @Override
      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            SoundEvent soundEvent = this.getType() == Fluids.LAVA ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
            float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, soundEvent, SoundSource.BLOCKS, f, 1.0F, false);
         }
      }
   }

   public static class FallAndLandParticle extends TensuraDripParticle.FallingParticle {
      protected final ParticleOptions landParticle;

      public FallAndLandParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, ParticleOptions particleOptions) {
         super(clientLevel, d, e, f, fluid);
         this.landParticle = particleOptions;
      }

      @Override
      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
         }
      }
   }

   public static class FallingParticle extends TensuraDripParticle {
      public FallingParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid) {
         this(clientLevel, d, e, f, fluid, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
      }

      public FallingParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, int i) {
         super(clientLevel, d, e, f, fluid);
         this.lifetime = i;
      }

      @Override
      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
         }
      }
   }

   public static class HoneyFallAndLandParticle extends TensuraDripParticle.FallAndLandParticle {
      public HoneyFallAndLandParticle(ClientLevel clientLevel, double d, double e, double f, Fluid fluid, ParticleOptions particleOptions) {
         super(clientLevel, d, e, f, fluid, particleOptions);
      }

      @Override
      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, f, 1.0F, false);
         }
      }
   }
}
