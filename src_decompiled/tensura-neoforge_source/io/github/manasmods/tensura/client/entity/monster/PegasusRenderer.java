package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.entity.layer.PegasusArmorLayer;
import io.github.manasmods.tensura.entity.monster.PegasusEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PegasusRenderer extends GeoEntityRenderer<PegasusEntity> {
   public PegasusRenderer(Context renderManager) {
      super(renderManager, new PegasusModel());
      this.addRenderLayer(new PegasusArmorLayer(this));
   }

   protected float getDeathMaxRotation(PegasusEntity animatable) {
      return 0.0F;
   }

   protected float getShadowRadius(PegasusEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      PegasusEntity animatable,
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
