package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WarpPortalRenderer extends GeoEntityRenderer<WarpPortalEntity> {
   public WarpPortalRenderer(Context renderManager) {
      super(renderManager, new WarpPortalModel());
   }

   protected float getShadowRadius(WarpPortalEntity entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(WarpPortalEntity portal, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      WarpPortalEntity animatable,
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
      float scale = animatable.getSize() / 3.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, 15728880, packedOverlay
      );
   }

   protected void applyRotations(WarpPortalEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
      if (animatable.getFacingDirection().getAxis() == Axis.Y) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F - rotationYaw));
      } else if (animatable.getFacingDirection().getAxis() == Axis.X) {
         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F - rotationYaw));
      }
   }
}
