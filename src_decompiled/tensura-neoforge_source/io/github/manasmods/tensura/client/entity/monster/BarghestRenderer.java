package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.entity.layer.BarghestFlameLayer;
import io.github.manasmods.tensura.entity.monster.BarghestEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BarghestRenderer extends GeoEntityRenderer<BarghestEntity> {
   public BarghestRenderer(Context renderManager) {
      super(renderManager, new BarghestModel());
      this.addRenderLayer(new BarghestFlameLayer(this));
   }

   protected float getShadowRadius(BarghestEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      BarghestEntity animatable,
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
      float scale = animatable.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
