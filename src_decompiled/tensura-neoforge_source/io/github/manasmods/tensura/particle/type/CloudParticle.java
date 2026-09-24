package io.github.manasmods.tensura.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.particle.option.CloudParticleOptions;
import java.util.function.Consumer;
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

public class CloudParticle extends TextureSheetParticle {
   protected float originalAlpha;
   protected float minAlpha;

   public CloudParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, CloudParticleOptions options) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      float random = 0.3F;
      this.xd = motionX + (Math.random() * 2.0 - 1.0) * random;
      this.yd = motionY + (Math.random() * 2.0 - 1.0) * random;
      this.zd = motionZ + (Math.random() * 2.0 - 1.0) * random;
      double magnitude = (Math.random() + Math.random() + 1.0) * random * 0.3F;
      double length = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / length * magnitude * random;
      this.yd = this.yd / length * magnitude * random + random * 0.25F;
      this.zd = this.zd / length * magnitude * random;
      float brightness = this.random.nextFloat() * 0.14F + 0.85F;
      this.rCol = options.red() * brightness;
      this.gCol = options.green() * brightness;
      this.bCol = options.blue() * brightness;
      this.originalAlpha = options.alpha();
      this.minAlpha = options.minAlpha();
      this.quadSize = 1.5F * options.scale();
      this.lifetime = options.averageLife() + level.getRandom().nextInt(-20, 20);
      this.gravity = options.gravity();
      this.friction = 1.0F;
   }

   public float getQuadSize(float partialTicks) {
      float fadeIn = Mth.clamp((this.age + partialTicks) / this.lifetime * 0.75F, 0.0F, 1.0F);
      float lifeProgress = Mth.clamp(this.age / 5.0F, 0.0F, 1.0F);
      return this.quadSize * (1.0F + fadeIn) * lifeProgress;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd = this.yd - 0.04 * this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.yd *= 0.85F;
         this.xd *= 0.94F;
         this.zd *= 0.94F;
      }
   }

   public void render(VertexConsumer buffer, Camera camera, float ticks) {
      this.alpha = Math.max(this.originalAlpha - Mth.clamp((this.age + ticks - 20.0F) / this.lifetime, 0.2F, 0.7F), this.minAlpha);
      this.drawLayer(buffer, camera, ticks, quaternionf -> {
         quaternionf.mul(Axis.YP.rotation(0.0F));
         quaternionf.mul(Axis.XP.rotation((float) (-Math.PI / 2)));
      });
      this.drawLayer(buffer, camera, ticks, quaternionf -> {
         quaternionf.mul(Axis.YP.rotation((float) -Math.PI));
         quaternionf.mul(Axis.XP.rotation((float) (Math.PI / 2)));
      });
   }

   private void drawLayer(VertexConsumer consumer, Camera camera, float partialTick, Consumer<Quaternionf> transform) {
      Vec3 camPos = camera.getPosition();
      float dx = (float)(Mth.lerp(partialTick, this.xo, this.x) - camPos.x);
      float dy = (float)(Mth.lerp(partialTick, this.yo, this.y) - camPos.y);
      float dz = (float)(Mth.lerp(partialTick, this.zo, this.z) - camPos.z);
      Vector3f axis = new Vector3f(0.5F, 0.5F, 0.5F).normalize();
      Quaternionf quaternion = new Quaternionf().setAngleAxis(0.0F, axis.x(), axis.y(), axis.z());
      transform.accept(quaternion);
      Vector3f[] corners = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float size = this.getQuadSize(partialTick);

      for (Vector3f corner : corners) {
         corner.rotate(quaternion);
         corner.mul(size);
         corner.add(dx, dy, dz);
      }

      int light = this.getLightColor(partialTick);
      this.addVertex(consumer, corners[0], this.getU1(), this.getV1(), light);
      this.addVertex(consumer, corners[1], this.getU1(), this.getV0(), light);
      this.addVertex(consumer, corners[2], this.getU0(), this.getV0(), light);
      this.addVertex(consumer, corners[3], this.getU0(), this.getV1(), light);
   }

   private void addVertex(VertexConsumer consumer, Vector3f pos, float u, float v, int light) {
      consumer.addVertex(
            pos.x() + 0.2F * Mth.sin((float)(this.age + this.x) * 0.01F),
            pos.y() + 0.08F + this.alpha * 0.125F,
            pos.z() + Mth.sin((float)(this.age + this.z) * 0.01F) * 0.2F
         )
         .setUv(u, v)
         .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
         .setLight(light);
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<CloudParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         CloudParticleOptions options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         CloudParticle particle = new CloudParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, options);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }
}
