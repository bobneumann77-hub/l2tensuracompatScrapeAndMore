package io.github.manasmods.tensura.client.entity.circle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class MagicCircleRenderer extends GeoEntityRenderer<MagicCircle> {
   public MagicCircleRenderer(Context renderManager) {
      super(renderManager, new MagicCircleModel());
   }

   protected float getShadowRadius(MagicCircle entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(MagicCircle blaze, BlockPos blockPos) {
      return 15;
   }

   public Color getRenderColor(MagicCircle animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isInFirstViewDistance(animatable, 3.0)) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.1F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   public void preRender(
      PoseStack poseStack,
      MagicCircle animatable,
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
