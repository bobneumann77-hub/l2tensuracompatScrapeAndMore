package io.github.manasmods.tensura.client.entity.barrier;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.github.manasmods.tensura.entity.magic.barrier.MegiddoBubbleEntity;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MegiddoBubbleRenderer<E extends MegiddoBubbleEntity> extends GeoEntityRenderer<E> {
   private static final RenderType TRANSPARENT_LEASH = RenderType.create(
      "transparent_leash",
      DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
      Mode.TRIANGLE_STRIP,
      256,
      false,
      false,
      CompositeState.builder()
         .setShaderState(RenderStateShard.RENDERTYPE_LEASH_SHADER)
         .setTextureState(RenderStateShard.NO_TEXTURE)
         .setCullState(RenderStateShard.NO_CULL)
         .setLightmapState(RenderStateShard.LIGHTMAP)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false)
   );

   public MegiddoBubbleRenderer(Context renderManager) {
      super(renderManager, new MegiddoBubbleModel());
   }

   protected float getShadowRadius(E entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(E blaze, BlockPos blockPos) {
      return 10;
   }

   public void preRender(
      PoseStack poseStack,
      E animatable,
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
      float scale = animatable.getVisualSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }

   public void renderFinal(
      PoseStack poseStack,
      E entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTicks,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      super.renderFinal(poseStack, entity, model, bufferSource, buffer, partialTicks, packedLight, packedOverlay, colour);
      if (!entity.getTargetPos().equals(Vec3.ZERO)) {
         this.renderBeam(entity, partialTicks, poseStack, bufferSource, 0.025F, 1.0F);
         this.renderBeam(entity, partialTicks, poseStack, bufferSource, 0.1F, 0.15F);
      }
   }

   private void renderBeam(MegiddoBubbleEntity bubble, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, float size, float alpha) {
      pMatrixStack.pushPose();
      Vec3 targetPos = bubble.getTargetPos();
      double d0 = Mth.lerp(pPartialTicks, bubble.yRotO, bubble.getYRot()) * (float) (Math.PI / 180.0) + (Math.PI / 2);
      Vec3 startBeam = bubble.getStartBeamOffset().subtract(bubble.position());
      double d1 = Math.cos(d0) * startBeam.z + Math.sin(d0) * startBeam.x;
      double d2 = Math.sin(d0) * startBeam.z - Math.cos(d0) * startBeam.x;
      double d3 = Mth.lerp(pPartialTicks, bubble.xo, bubble.getX()) + d1;
      double d4 = Mth.lerp(pPartialTicks, bubble.yo, bubble.getY()) + startBeam.y;
      double d5 = Mth.lerp(pPartialTicks, bubble.zo, bubble.getZ()) + d2;
      pMatrixStack.translate(d1, startBeam.y, d2);
      float x = (float)(targetPos.x - d3);
      float y = (float)(targetPos.y - d4);
      float z = (float)(targetPos.z - d5);
      int k = bubble.level().getBrightness(LightLayer.SKY, ObjectSelectionHelper.getBlockPos(startBeam));
      int l = bubble.level().getBrightness(LightLayer.SKY, ObjectSelectionHelper.getBlockPos(targetPos));
      float offset = (float)(Mth.fastInvSqrt(x * x + z * z) * size);
      float zOff = z * offset;
      float xOff = x * offset;
      Matrix4f matrix4f = pMatrixStack.last().pose();
      VertexConsumer vertexconsumer = pBuffer.getBuffer(TRANSPARENT_LEASH);

      for (int segment = 0; segment <= 10; segment++) {
         addVertexPair(vertexconsumer, matrix4f, x, y, z, 15, 15, k, l, 0.025F, zOff, xOff, segment, alpha);
      }

      for (int segment = 10; segment >= 0; segment--) {
         addVertexPair(vertexconsumer, matrix4f, x, y, z, 5, 15, k, l, 0.0F, zOff, xOff, segment, alpha);
      }

      pMatrixStack.popPose();
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
      float alpha
   ) {
      float f = segment / 10.0F;
      int i = (int)Mth.lerp(f, bulletLight, holderLight);
      int j = (int)Mth.lerp(f, bulletBright, holderBright);
      int k = LightTexture.pack(i, j);
      float f1 = 1.0F;
      float f5 = x * f;
      float f6 = y * f;
      float f7 = z * f;
      consumer.addVertex(matrix4f, f5 - xOff, f6 + yOff, f7 + zOff).setColor(f1, f1, f1, alpha).setUv2(k & 65535, k >> 16 & 65535);
      consumer.addVertex(matrix4f, f5 + xOff, f6 + 0.025F - yOff, f7 - zOff).setColor(f1, f1, f1, alpha).setUv2(k & 65535, k >> 16 & 65535);
   }
}
