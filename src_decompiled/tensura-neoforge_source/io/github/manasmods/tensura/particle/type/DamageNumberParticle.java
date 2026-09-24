package io.github.manasmods.tensura.particle.type;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.particle.option.NumberParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class DamageNumberParticle extends Particle {
   private final Font fontRenderer = Minecraft.getInstance().font;
   private final Component text;
   private final int color;
   private final int shadowColor;
   private final int index;
   private final float size;

   public DamageNumberParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, NumberParticleOptions options) {
      super(clientLevel, d, e, f);
      this.friction = 0.92F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.x = d;
      this.y = e;
      this.z = f;
      this.xd = g;
      this.yd = h;
      this.zd = i;
      this.setSize(1.0F, 1.0F);
      this.size = options.scale();
      this.lifetime = options.averageLife();
      this.gravity = options.gravity();
      this.friction = 1.0F;
      this.index = options.index();
      this.color = TensuraColors.getARGB(options.color(), options.alpha());
      this.shadowColor = TensuraColors.getARGB(options.shadowColor(), options.alpha());
      this.text = Component.literal(
         options.value() < 0.0F ? "+" + SkillUtils.ROUND_DOUBLE.format(options.value() * -1.0F) : SkillUtils.ROUND_DOUBLE.format(options.value())
      );
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

   public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
      Vec3 cameraPos = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x);
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y);
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z);
      float lifeT = this.lifetime <= 0 ? 1.0F : (this.age + partialTicks) / this.lifetime;
      float alphaMul = Math.clamp(1.0F - Mth.clamp(lifeT, 0.0F, 1.0F), 0.1F, 1.0F);
      int argb = ARGB32.color((int)(ARGB32.alpha(this.color) * alphaMul), ARGB32.red(this.color), ARGB32.green(this.color), ARGB32.blue(this.color));
      int darkened = ARGB32.color(
         (int)(ARGB32.alpha(this.shadowColor) * alphaMul), ARGB32.red(this.shadowColor), ARGB32.green(this.shadowColor), ARGB32.blue(this.shadowColor)
      );
      PoseStack pose = new PoseStack();
      pose.pushPose();
      pose.translate(x, y, z);
      pose.mulPose(camera.rotation());
      int k = (this.index + 1) / 2;
      int slot = 0;
      if (this.index != 0) {
         int sign = this.index % 2 == 1 ? -1 : 1;
         slot = sign * k;
      }

      float distance = this.size * (float)new Vec3(x, y, z).length();
      float xOff = k == 0 ? slot * 0.4F : slot / (float)Math.sqrt(k) * 0.4F;
      float yOff = k * -0.3F;
      pose.translate(xOff * distance / 4.0F, yOff * distance / 4.0F, 0.0);
      float scale = 0.006F * distance;
      pose.scale(scale, -scale, scale);
      float textX = this.fontRenderer.width(this.text) / -2.0F;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().buffer);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      pose.pushPose();
      pose.translate(1.0, 1.0, 0.0);
      this.fontRenderer.drawInBatch(this.text, textX, 0.0F, argb, false, pose.last().pose(), buffer, DisplayMode.NORMAL, 0, 15728880);
      pose.popPose();
      this.fontRenderer.drawInBatch(this.text, textX, 0.0F, darkened, false, pose.last().pose(), buffer, DisplayMode.NORMAL, 0, 15728880);
      buffer.endBatch();
      RenderSystem.disableBlend();
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      pose.popPose();
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<NumberParticleOptions> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(NumberParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
         return new DamageNumberParticle(clientLevel, d, e, f, g, h, i, options);
      }
   }
}
