package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class GluttonyMistRenderer extends GeoEntityRenderer<PredatorMistProjectile> {
   public GluttonyMistRenderer(Context renderManager, ResourceLocation customSkin) {
      super(renderManager, new GluttonyMistModel(customSkin));
   }

   public GluttonyMistRenderer(Context renderManager) {
      super(renderManager, new GluttonyMistModel(null));
   }

   protected float getShadowRadius(PredatorMistProjectile entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(PredatorMistProjectile blaze, BlockPos blockPos) {
      return 15;
   }

   public Color getRenderColor(PredatorMistProjectile animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isInFirstViewDistance(animatable, 2.0)) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.1F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   public void preRender(
      PoseStack poseStack,
      PredatorMistProjectile animatable,
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
      float scale = animatable.getAttackingRange() / 25.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
