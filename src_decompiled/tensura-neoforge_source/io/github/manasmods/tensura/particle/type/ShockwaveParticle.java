package io.github.manasmods.tensura.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.particle.option.ShockWaveParticleOptions;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends TextureSheetParticle {
   private final float scale;
   private final boolean reversed;
   private final SpriteSet spriteSet;

   ShockwaveParticle(
      ClientLevel level, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ, ShockWaveParticleOptions options
   ) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      this.xd = velX;
      this.yd = velY;
      this.zd = velZ;
      float brightness = this.random.nextFloat() * 0.15F + 0.85F;
      this.rCol = options.red() * brightness;
      this.gCol = options.green() * brightness;
      this.bCol = options.blue() * brightness;
      this.alpha = options.alpha();
      this.scale = options.scale();
      this.quadSize = 1.0F;
      this.lifetime = 10;
      this.friction = 1.0F;
      this.spriteSet = sprites;
      this.setSpriteFromAge(this.spriteSet);
      this.gravity = options.gravity();
      this.reversed = options.reversed();
   }

   public float getQuadSize(float partialTicks) {
      float progress = (partialTicks + this.age) / this.lifetime;
      float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
      if (this.reversed) {
         eased *= -1.0F;
      }

      return this.quadSize * Mth.lerp(eased, this.scale * 0.75F, this.scale);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (++this.age >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.spriteSet);
         this.yd = this.yd - 0.04 * this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.yd *= 0.85F;
         this.xd *= 0.94F;
         this.zd *= 0.94F;
      }
   }

   public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
      this.alpha = 1.0F - Mth.clamp((this.age + partialTicks) / this.lifetime, 0.0F, 1.0F);
      this.renderRotated(buffer, camera, partialTicks, quaternion -> {
         quaternion.mul(Axis.YP.rotation(0.0F));
         quaternion.mul(Axis.XP.rotation((float) (-Math.PI / 2)));
      });
      this.renderRotated(buffer, camera, partialTicks, quaternion -> {
         quaternion.mul(Axis.YP.rotation((float) -Math.PI));
         quaternion.mul(Axis.XP.rotation((float) (Math.PI / 2)));
      });
   }

   private void renderRotated(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> applyRotation) {
      Vec3 camPos = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());
      Vector3f axis = (Vector3f)Util.make(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
      Quaternionf rotation = new Quaternionf().setAngleAxis(0.0F, axis.x(), axis.y(), axis.z());
      applyRotation.accept(rotation);
      rotation.transform(new Vector3f(-1.0F, -1.0F, 0.0F));
      Vector3f[] corners = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float scale = this.getQuadSize(partialTicks);

      for (Vector3f corner : corners) {
         corner.rotate(rotation);
         corner.mul(scale);
         corner.add(x, y, z);
      }

      int light = this.getLightColor(partialTicks);
      this.makeVertex(consumer, corners[0], this.getU1(), this.getV1(), light);
      this.makeVertex(consumer, corners[1], this.getU1(), this.getV0(), light);
      this.makeVertex(consumer, corners[2], this.getU0(), this.getV0(), light);
      this.makeVertex(consumer, corners[3], this.getU0(), this.getV1(), light);
   }

   private void makeVertex(VertexConsumer consumer, Vector3f pos, float u, float v, int light) {
      consumer.addVertex(pos.x(), pos.y() + 0.08F, pos.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
   }

   protected int getLightColor(float partialTick) {
      return 15728880;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<ShockWaveParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         @NotNull ShockWaveParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double speedX, double speedY, double speedZ
      ) {
         return new ShockwaveParticle(level, x, y, z, this.sprites, speedX, speedY, speedZ, options);
      }
   }
}
