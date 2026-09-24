package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.KnightSpiderEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KnightSpiderRenderer extends GeoEntityRenderer<KnightSpiderEntity> {
   public KnightSpiderRenderer(Context renderManager) {
      super(renderManager, new KnightSpiderModel());
   }

   protected float getShadowRadius(KnightSpiderEntity entity) {
      return entity.isBaby() ? 1.0F : 2.0F;
   }

   protected float getDeathMaxRotation(KnightSpiderEntity animatable) {
      return 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      KnightSpiderEntity animatable,
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
