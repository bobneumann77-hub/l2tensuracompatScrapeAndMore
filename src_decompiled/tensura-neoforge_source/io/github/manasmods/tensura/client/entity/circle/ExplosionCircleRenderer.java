package io.github.manasmods.tensura.client.entity.circle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.ExplosionCircle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExplosionCircleRenderer extends GeoEntityRenderer<ExplosionCircle> {
   public ExplosionCircleRenderer(Context renderManager) {
      super(renderManager, new ExplosionCircleModel());
   }

   protected float getShadowRadius(ExplosionCircle entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(ExplosionCircle blaze, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      ExplosionCircle animatable,
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
      float scale = animatable.getSize() / 5.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, 15728880, packedOverlay
      );
   }
}
