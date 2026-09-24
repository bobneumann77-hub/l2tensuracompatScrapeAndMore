package io.github.manasmods.tensura.client.entity.multipart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.multipart.TempestSerpentBody;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TempestSerpentBodyRenderer extends GeoEntityRenderer<TempestSerpentBody> {
   public TempestSerpentBodyRenderer(Context renderManager) {
      super(renderManager, new TempestSerpentBodyModel());
   }

   protected float getShadowRadius(TempestSerpentBody entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      TempestSerpentBody animatable,
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
      float scale = 1.5F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }

   protected void applyRotations(TempestSerpentBody entity, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks, float nativeScale) {
      float newYaw = entity.yHeadRot;
      Pose pose = entity.getPose();
      if (pose != Pose.SLEEPING) {
         stack.mulPose(Axis.YP.rotationDegrees(180.0F - newYaw));
         stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
      }

      if (entity.deathTime > 0) {
         float f = (entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         stack.mulPose(Axis.ZP.rotationDegrees(f * this.getDeathMaxRotation(entity)));
      } else if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
         stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
         stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      }
   }
}
