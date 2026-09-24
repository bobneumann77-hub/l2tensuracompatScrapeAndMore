package io.github.manasmods.tensura.client.entity.beam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import io.github.manasmods.tensura.util.client.ClientHelper;
import java.awt.Color;
import java.util.Arrays;
import java.util.Map.Entry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class BeamProjectileRenderer extends EntityRenderer<BeamProjectile> {
   public BeamProjectileRenderer(Context pContext) {
      super(pContext);
   }

   protected int getBlockLightLevel(BeamProjectile entity, BlockPos blockPos) {
      return 15;
   }

   @NotNull
   public ResourceLocation getTextureLocation(BeamProjectile instance) {
      ResourceLocation[] resourceLocations = instance.getTextureLocation();
      return resourceLocations == null
         ? BeaconRenderer.BEAM_LOCATION
         : Arrays.stream(resourceLocations).toList().get(instance.tickCount % resourceLocations.length);
   }

   public void render(
      BeamProjectile beam, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight
   ) {
      if (beam.tickCount > 2) {
         double collidePosX = Mth.lerp(pPartialTick, beam.prevCollidePosX, beam.getCollideX());
         double collidePosY = Mth.lerp(pPartialTick, beam.prevCollidePosY, beam.getCollideY());
         double collidePosZ = Mth.lerp(pPartialTick, beam.prevCollidePosZ, beam.getCollideZ());
         Vec3 collisionPos = new Vec3(collidePosX, collidePosY, collidePosZ);
         double posX = Mth.lerp(pPartialTick, beam.xo, beam.getX());
         double posY = Mth.lerp(pPartialTick, beam.yo, beam.getY());
         double posZ = Mth.lerp(pPartialTick, beam.zo, beam.getZ());
         Vec3 startPos = new Vec3(posX, posY, posZ);
         pPoseStack.pushPose();
         pPoseStack.translate(0.0F, beam.getBbHeight() / 2.0F, 0.0F);
         beam.startParticles(startPos);
         Vec3 offSetToTarget = collisionPos.subtract(startPos);

         for (int i = 1; i < Mth.floor(offSetToTarget.length()) - 1; i++) {
            beam.rayParticles(startPos.add(offSetToTarget.normalize().scale(i)), i);
         }

         this.renderRays(beam, startPos, collisionPos, pPartialTick, pPoseStack, pBuffer, pPackedLight);
         beam.hitParticles(collidePosX, collidePosY, collidePosZ);
         pPoseStack.popPose();
      }
   }

   public void renderRays(
      BeamProjectile beam,
      Vec3 startPos,
      Vec3 collidePos,
      float pPartialTick,
      @NotNull PoseStack pPoseStack,
      @NotNull MultiBufferSource pBuffer,
      int pPackedLight
   ) {
      for (Entry<Color, Float> entry : beam.beamColorAndSize.entrySet()) {
         int alpha = entry.getKey().getAlpha();
         if (ClientHelper.isInFirstViewDistance(beam, 2.0)) {
            alpha /= 5;
         }

         this.renderRay(
            beam,
            startPos,
            collidePos,
            pPartialTick,
            pPoseStack,
            pBuffer,
            pPackedLight,
            entry.getValue() * beam.getVisualSize(),
            entry.getKey().getRed(),
            entry.getKey().getGreen(),
            entry.getKey().getBlue(),
            alpha
         );
      }
   }

   public void renderRay(
      BeamProjectile beam,
      Vec3 start,
      Vec3 end,
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
      float gloveHalfWidth = beam.getBbWidth() / 2.0F;
      float xDist = (float)(end.x() - start.x());
      float yDist = (float)(end.y() - start.y() - gloveHalfWidth);
      float zDist = (float)(end.z() - start.z());
      float f = Mth.sqrt(xDist * xDist + zDist * zDist);
      float f1 = Mth.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
      poseStack.pushPose();
      poseStack.translate(0.0F, gloveHalfWidth, 0.0F);
      poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(zDist, xDist)) - (float) (Math.PI / 2)));
      poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(f, yDist)) - (float) (Math.PI / 2)));
      VertexConsumer vertexConsumer = bufferSource.getBuffer(ClientHelper.getFirstViewRenderType(beam, 2.0, this.getTextureLocation(beam)));
      float f2 = 0.0F - (beam.tickCount + partialTick) * 0.01F;
      float f3 = f1 / 32.0F - (beam.tickCount + partialTick) * 0.01F;
      int k = beam.tickCount % 16;
      if (k > 8) {
         k = 8 + (k - 8) * -1;
      }

      int i = 8 + k;
      float f4 = 0.0F;
      float f5 = 0.25F * size;
      float f6 = 0.0F;
      Pose pose = poseStack.last();
      Matrix4f matrix4f = pose.pose();

      for (int j = 0; j <= i; j++) {
         float f7 = Mth.sin(j * (float) (Math.PI * 2) / i) * size;
         float f8 = Mth.cos(j * (float) (Math.PI * 2) / i) * size;
         float f9 = (float)j / i * size;
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
