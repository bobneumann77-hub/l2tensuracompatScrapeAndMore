package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.OneEyedOwlEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OneEyedOwlRenderer extends GeoEntityRenderer<OneEyedOwlEntity> {
   public OneEyedOwlRenderer(Context renderManager) {
      super(renderManager, new OneEyedOwlModel());
   }

   protected float getShadowRadius(OneEyedOwlEntity entity) {
      return entity.isBaby() ? 0.1F : 0.2F;
   }

   public void preRender(
      PoseStack poseStack,
      OneEyedOwlEntity owl,
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
      float scale = owl.isBaby() ? 0.25F : 0.5F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, owl, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
