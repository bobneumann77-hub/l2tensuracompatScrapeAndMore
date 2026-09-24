package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.HellMothEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class HellMothRenderer extends GeoEntityRenderer<HellMothEntity> {
   public HellMothRenderer(Context renderManager) {
      super(renderManager, new HellMothModel());
   }

   protected float getShadowRadius(HellMothEntity entity) {
      return entity.getAgeScale();
   }

   public void preRender(
      PoseStack poseStack,
      HellMothEntity animatable,
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
      float scale = animatable.getAgeScale();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }

   public Color getRenderColor(HellMothEntity animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (animatable.isWet) {
         color = Color.ofARGB(color.getAlpha(), color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);
      }

      return color;
   }
}
