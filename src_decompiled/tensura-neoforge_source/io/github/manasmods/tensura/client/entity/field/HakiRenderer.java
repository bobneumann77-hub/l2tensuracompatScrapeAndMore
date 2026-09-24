package io.github.manasmods.tensura.client.entity.field;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class HakiRenderer extends GeoEntityRenderer<HakiField> {
   public HakiRenderer(Context renderManager) {
      super(renderManager, new HakiModel());
   }

   protected float getShadowRadius(HakiField entity) {
      return 0.0F;
   }

   public Color getRenderColor(HakiField animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isInFirstViewDistance(animatable, 1.0)) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.5F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   protected int getBlockLightLevel(HakiField haki, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      HakiField animatable,
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
      float scale = animatable.getEffectSizeMultiplier() * animatable.getVisualSize() / animatable.getSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
