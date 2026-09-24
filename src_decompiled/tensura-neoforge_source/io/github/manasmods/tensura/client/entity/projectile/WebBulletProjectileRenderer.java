package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class WebBulletProjectileRenderer extends EntityRenderer<WebBulletProjectile> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
   private final LlamaSpitModel<WebBulletProjectile> model;

   public WebBulletProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.model = new LlamaSpitModel(renderManager.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void render(WebBulletProjectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      if (!pEntity.isSlinger()) {
         pMatrixStack.pushPose();
         pMatrixStack.translate(0.0, 0.15F, 0.0);
         pMatrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
         pMatrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
         this.model.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
         VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
         this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
         pMatrixStack.popPose();
      }

      if (pEntity.isSlinger() && pEntity.getOwner() != null) {
         this.renderLeash(pEntity, pPartialTicks, pMatrixStack, pBuffer, pEntity.getOwner());
      }

      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   public ResourceLocation getTextureLocation(WebBulletProjectile pEntity) {
      return LLAMA_SPIT_LOCATION;
   }

   private <E extends Entity> void renderLeash(
      WebBulletProjectile bullet, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, E pLeashHolder
   ) {
      pMatrixStack.pushPose();
      Vec3 vec3 = pLeashHolder.getRopeHoldPosition(pPartialTicks);
      double d0 = Mth.lerp(pPartialTicks, bullet.yRotO, bullet.getYRot()) * (float) (Math.PI / 180.0) + (Math.PI / 2);
      Vec3 bulletOffset = new Vec3(0.0, bullet.getEyeHeight(), bullet.getBbWidth() * 0.4F);
      double d1 = Math.cos(d0) * bulletOffset.z + Math.sin(d0) * bulletOffset.x;
      double d2 = Math.sin(d0) * bulletOffset.z - Math.cos(d0) * bulletOffset.x;
      double d3 = Mth.lerp(pPartialTicks, bullet.xo, bullet.getX()) + d1;
      double d4 = Mth.lerp(pPartialTicks, bullet.yo, bullet.getY()) + bulletOffset.y;
      double d5 = Mth.lerp(pPartialTicks, bullet.zo, bullet.getZ()) + d2;
      pMatrixStack.translate(d1, bulletOffset.y, d2);
      float x = (float)(vec3.x - d3);
      float y = (float)(vec3.y - d4);
      float z = (float)(vec3.z - d5);
      Vec3 bulletEyePos = bullet.getEyePosition(pPartialTicks);
      BlockPos bulletPos = new BlockPos((int)bulletEyePos.x(), (int)bulletEyePos.y(), (int)bulletEyePos.z());
      int i = this.getBlockLightLevel(bullet, bulletPos);
      int k = bullet.level().getBrightness(LightLayer.SKY, bulletPos);
      Vec3 leashHolderPos = pLeashHolder.getEyePosition(pPartialTicks);
      BlockPos ownerPos = new BlockPos((int)leashHolderPos.x(), (int)leashHolderPos.y(), (int)leashHolderPos.z());
      int j = this.getBlockLightLevels(pLeashHolder, ownerPos);
      int l = bullet.level().getBrightness(LightLayer.SKY, ownerPos);
      float offset = (float)(Mth.fastInvSqrt(x * x + z * z) * 0.025F / 2.0);
      float zOff = z * offset;
      float xOff = x * offset;
      Matrix4f matrix4f = pMatrixStack.last().pose();
      VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());

      for (int segment = 0; segment <= 60; segment++) {
         addVertexPair(vertexconsumer, matrix4f, x, y, z, i, j, k, l, 0.025F, zOff, xOff, segment, false);
      }

      for (int segment = 60; segment >= 0; segment--) {
         addVertexPair(vertexconsumer, matrix4f, x, y, z, i, j, k, l, 0.0F, zOff, xOff, segment, true);
      }

      pMatrixStack.popPose();
   }

   protected int getBlockLightLevels(Entity pEntity, BlockPos pPos) {
      return pEntity.isOnFire() ? 15 : pEntity.level().getBrightness(LightLayer.BLOCK, pPos);
   }

   private static void addVertexPair(
      VertexConsumer consumer,
      Matrix4f matrix4f,
      float x,
      float y,
      float z,
      int bulletLight,
      int holderLight,
      int bulletBright,
      int holderBright,
      float yOff,
      float xOff,
      float zOff,
      int segment,
      boolean darken
   ) {
      float f = segment / 60.0F;
      int i = (int)Mth.lerp(f, bulletLight, holderLight);
      int j = (int)Mth.lerp(f, bulletBright, holderBright);
      int k = LightTexture.pack(i, j);
      float f1 = segment % 2 == (darken ? 1 : 0) ? 0.7F : 1.0F;
      float f5 = x * f;
      float f6 = y > 0.0F ? y * f * f : y - y * (1.0F - f) * (1.0F - f);
      float f7 = z * f;
      consumer.addVertex(matrix4f, f5 - xOff, f6 + yOff, f7 + zOff).setColor(f1, f1, f1, 1.0F).setUv2(k & 65535, k >> 16 & 65535);
      consumer.addVertex(matrix4f, f5 + xOff, f6 + 0.025F - yOff, f7 - zOff).setColor(f1, f1, f1, 1.0F).setUv2(k & 65535, k >> 16 & 65535);
   }
}
