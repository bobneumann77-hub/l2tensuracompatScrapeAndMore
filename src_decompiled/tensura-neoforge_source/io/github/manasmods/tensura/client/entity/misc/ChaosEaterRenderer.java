package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.magic.ChaosEaterProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChaosEaterRenderer extends GeoEntityRenderer<ChaosEaterProjectile> {
   public ChaosEaterRenderer(Context renderManager) {
      super(renderManager, new ChaosEaterModel());
   }

   protected float getShadowRadius(ChaosEaterProjectile entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(ChaosEaterProjectile blaze, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      ChaosEaterProjectile entity,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = entity.getSize();
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, entity, model, isReRender, partialTick, 15728880, packedOverlay);
   }

   public void renderFinal(
      PoseStack poseStack,
      ChaosEaterProjectile entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      super.renderFinal(poseStack, entity, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);
      if (entity.getOwner() != null && bufferSource != null) {
         renderRay(entity, entity.getOwner(), partialTick, poseStack, bufferSource, 15728880, 0.3F, 84, 31, 27, 255);
         renderRay(entity, entity.getOwner(), partialTick, poseStack, bufferSource, 15728880, 0.4F, 184, 26, 34, 30);
      }
   }

   public static void renderRay(
      ChaosEaterProjectile eater,
      Entity owner,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      float size,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      Vec3 offset = owner.getEyePosition().add(eater.getStartOffset());
      double x = offset.x();
      double y = offset.y();
      double z = offset.z();
      float xDist = (float)(x - Mth.lerp(partialTick, eater.xo, eater.getX()));
      float yDist = (float)(y - Mth.lerp(partialTick, eater.yo, eater.getY()));
      float zDist = (float)(z - Mth.lerp(partialTick, eater.zo, eater.getZ()));
      float f = Mth.sqrt(xDist * xDist + zDist * zDist);
      float f1 = Mth.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(zDist, xDist)) - (float) (Math.PI / 2)));
      poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(f, yDist)) - (float) (Math.PI / 2)));
      VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BeaconRenderer.BEAM_LOCATION));
      float f2 = 0.0F - (eater.getAge() + partialTick) * 0.01F;
      float f3 = f1 / 32.0F - (eater.getAge() + partialTick) * 0.01F;
      int i = 8;
      float f4 = 0.0F;
      float f5 = 0.25F;
      float f6 = 0.0F;
      Pose pose = poseStack.last();
      Matrix4f matrix4f = pose.pose();

      for (int j = 1; j <= i; j++) {
         float f7 = Mth.sin(j * (float) (Math.PI * 2) / i) * size;
         float f8 = Mth.cos(j * (float) (Math.PI * 2) / i) * size;
         float f9 = (float)j / i;
         vertexConsumer.addVertex(matrix4f, f4, f5, 0.0F)
            .setColor(red, green, blue, alpha)
            .setUv(f6, f2)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(packedLight & 65535, packedLight >> 16 & 65535)
            .setNormal(pose, 0.0F, -1.0F, 0.0F);
         vertexConsumer.addVertex(matrix4f, f4, f5, f1)
            .setColor(red, green, blue, alpha)
            .setUv(f6, f3)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(packedLight & 65535, packedLight >> 16 & 65535)
            .setNormal(pose, 0.0F, -1.0F, 0.0F);
         vertexConsumer.addVertex(matrix4f, f7, f8, f1)
            .setColor(red, green, blue, alpha)
            .setUv(f9, f3)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(packedLight & 65535, packedLight >> 16 & 65535)
            .setNormal(pose, 0.0F, -1.0F, 0.0F);
         vertexConsumer.addVertex(matrix4f, f7, f8, 0.0F)
            .setColor(red, green, blue, alpha)
            .setUv(f9, f2)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(packedLight & 65535, packedLight >> 16 & 65535)
            .setNormal(pose, 0.0F, -1.0F, 0.0F);
         f4 = f7;
         f5 = f8;
         f6 = f9;
      }

      poseStack.popPose();
   }
}
