package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.monster.CharybdisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CharybdisRenderer extends GeoEntityRenderer<CharybdisEntity> {
   public CharybdisRenderer(Context renderManager) {
      super(renderManager, new CharybdisModel());
   }

   protected float getShadowRadius(CharybdisEntity entity) {
      return entity.isBaby() ? 2.5F : 5.0F;
   }

   protected float getDeathMaxRotation(CharybdisEntity animatable) {
      return 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      CharybdisEntity entity,
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
      float scale = entity.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, entity, model, isReRender, partialTick, packedLight, packedOverlay);
      if (!entity.getBeamTarget().equals(BlockPos.ZERO)) {
         renderBeam(entity, partialTick, poseStack, bufferSource, 15728880, 2.5F * CharybdisEntity.MAX_SIZE, 189, 75, 75, 255);
         renderBeam(entity, partialTick, poseStack, bufferSource, 15728880, 4.0F * CharybdisEntity.MAX_SIZE, 62, 1, 61, 155);
      }
   }

   public static void renderBeam(
      CharybdisEntity charybdis,
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
      Vec3 offset = charybdis.getBeamStartOffset();
      double x = charybdis.getX() + offset.x();
      double y = charybdis.getY() + offset.y();
      double z = charybdis.getZ() + offset.z();
      float xDist = (float)(x - charybdis.getBeamTarget().getX()) * -1.0F;
      float yDist = (float)(y - charybdis.getBeamTarget().getY()) * -1.0F;
      float zDist = (float)(z - charybdis.getBeamTarget().getZ()) * -1.0F;
      float f = Mth.sqrt(xDist * xDist + zDist * zDist);
      float f1 = Mth.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
      poseStack.pushPose();
      poseStack.translate(offset.x(), offset.y(), offset.z());
      poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(zDist, xDist)) - (float) (Math.PI / 2)));
      poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(f, yDist)) - (float) (Math.PI / 2)));
      VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BeaconRenderer.BEAM_LOCATION));
      float f2 = 0.0F - (charybdis.tickCount + partialTick) * 0.01F;
      float f3 = f1 / 32.0F - (charybdis.tickCount + partialTick) * 0.01F;
      int i = 10;
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
