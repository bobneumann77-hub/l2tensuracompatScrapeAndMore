package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.HellCaterpillarEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HellCaterpillarRenderer extends GeoEntityRenderer<HellCaterpillarEntity> {
   public HellCaterpillarRenderer(Context renderManager) {
      super(renderManager, new HellCaterpillarModel());
   }

   protected float getShadowRadius(HellCaterpillarEntity entity) {
      return entity.getAgeScale() * 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      HellCaterpillarEntity animatable,
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
      float scale = animatable.getAgeScale();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }

   private Direction rotate(Direction face) {
      return face.getAxis() == Axis.Y ? Direction.UP : face;
   }

   private void rotateForAngle(PoseStack matrixStackIn, Direction rotate, float f) {
      if (rotate.getAxis() != Axis.Y) {
         matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F * f));
      }

      switch (rotate) {
         case DOWN:
         case NORTH:
            matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F * f));
         case UP:
         case SOUTH:
         default:
            break;
         case WEST:
            matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F * f));
            break;
         case EAST:
            matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F * f));
      }
   }

   protected void applyRotations(HellCaterpillarEntity entity, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks, float nativeScale) {
      if (entity.isPassenger()) {
         super.applyRotations(entity, stack, ageInTicks, rotationYaw, partialTicks, nativeScale);
      } else {
         if (this.isShaking((HellCaterpillarEntity)this.animatable)) {
            rotationYaw += (float)(Math.cos(((HellCaterpillarEntity)this.animatable).tickCount * 3.25) * Math.PI * 0.4);
         }

         float trans = entity.isBaby() ? 0.26F : 0.52F;
         Pose pose = entity.getPose();
         if (pose != Pose.SLEEPING && !entity.isCocooning() && !entity.isCocooned()) {
            float progress = (entity.prevAttachChangeProgress + (entity.attachChangeProgress - entity.prevAttachChangeProgress) * partialTicks) * 0.2F;
            float yawMul = 0.0F;
            if (entity.prevAttachDir == entity.getAttachmentFacing() && entity.getAttachmentFacing().getAxis() == Axis.Y) {
               yawMul = 1.0F;
            }

            stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yawMul * rotationYaw));
            stack.translate(0.0, trans, 0.0);
            float prevProgress = 1.0F - progress;
            this.rotateForAngle(stack, this.rotate(entity.prevAttachDir), prevProgress);
            this.rotateForAngle(stack, this.rotate(entity.getAttachmentFacing()), progress);
            if (entity.getAttachmentFacing() != Direction.DOWN) {
               stack.translate(0.0, trans, 0.0);
               if (entity.getDeltaMovement().y <= -0.001F) {
                  stack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(180.0F * progress));
               }

               stack.translate(0.0, -trans, 0.0);
            }

            stack.translate(0.0, -trans, 0.0);
         }

         if (entity.deathTime > 0) {
            float f = (entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
               f = 1.0F;
            }

            stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(f * 90.0F));
         } else if (pose == Pose.SLEEPING || !entity.isCocooning() && !entity.isCocooned()) {
            if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
               stack.translate(0.0, (entity.getBbHeight() + 0.1F) / nativeScale, 0.0);
               stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
            }
         } else {
            stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
            stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
         }
      }
   }
}
