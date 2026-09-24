package io.github.manasmods.tensura.client.entity.field;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicHandsRenderer<E extends TensuraProjectile & GeoEntity> extends GeoEntityRenderer<E> {
   public MagicHandsRenderer(Context renderManager, ResourceLocation texture) {
      super(renderManager, new MagicHandsModel(texture));
   }

   protected float getShadowRadius(E entity) {
      return 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      E animatable,
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
      this.scaleModelForRender(
         this.scaleWidth * animatable.getSize(),
         this.scaleHeight * animatable.getVisualSize(),
         poseStack,
         animatable,
         model,
         isReRender,
         partialTick,
         packedLight,
         packedOverlay
      );
   }
}
