package io.github.manasmods.tensura.client.entity.barrier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.barrier.DisintegrationEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DisintegrationRenderer extends GeoEntityRenderer<DisintegrationEntity> {
   public DisintegrationRenderer(Context renderManager) {
      super(renderManager, new DisintegrationModel());
   }

   protected float getShadowRadius(DisintegrationEntity entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(DisintegrationEntity blaze, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      DisintegrationEntity animatable,
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
      float scale = animatable.getSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
