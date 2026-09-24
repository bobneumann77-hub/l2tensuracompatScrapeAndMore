package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.misc.HazyBlossomEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HazyBlossomRenderer extends GeoEntityRenderer<HazyBlossomEntity> {
   public HazyBlossomRenderer(Context renderManager) {
      super(renderManager, new HazyBlossomModel());
   }

   protected float getShadowRadius(HazyBlossomEntity entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(HazyBlossomEntity entity, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      HazyBlossomEntity animatable,
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
}
