package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackLightningRenderer extends GeoEntityRenderer<BlackLightningBolt> {
   public BlackLightningRenderer(Context renderManager) {
      super(renderManager, new BlackLightningModel());
   }

   protected float getShadowRadius(BlackLightningBolt entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(BlackLightningBolt bolt, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      BlackLightningBolt animatable,
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
      float scale = animatable.getAdditionalVisual() / 10.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale,
         this.scaleHeight * Math.min(1.0F, scale * 2.0F),
         poseStack,
         animatable,
         model,
         isReRender,
         partialTick,
         15728880,
         packedOverlay
      );
   }
}
