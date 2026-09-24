package io.github.manasmods.tensura.client.entity.multipart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.multipart.EvilCentipedeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EvilCentipedeRenderer extends GeoEntityRenderer<EvilCentipedeEntity> {
   public EvilCentipedeRenderer(Context renderManager) {
      super(renderManager, new EvilCentipedeModel());
   }

   protected float getShadowRadius(EvilCentipedeEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   protected float getDeathMaxRotation(EvilCentipedeEntity animatable) {
      return 180.0F;
   }

   public void preRender(
      PoseStack poseStack,
      EvilCentipedeEntity animatable,
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
}
