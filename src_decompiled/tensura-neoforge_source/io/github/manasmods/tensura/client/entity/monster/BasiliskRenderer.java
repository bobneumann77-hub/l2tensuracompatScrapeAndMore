package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.BasiliskEntity;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BasiliskRenderer extends GeoEntityRenderer<BasiliskEntity> {
   public BasiliskRenderer(Context renderManager) {
      super(renderManager, new BasiliskModel());
   }

   protected float getShadowRadius(BasiliskEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public RenderType getRenderType(BasiliskEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return ClientHelper.getRiderFirstViewRenderType(super.getRenderType(animatable, texture, bufferSource, partialTick), animatable, 2.0, texture);
   }

   public Color getRenderColor(BasiliskEntity animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isRiderFirstViewDistance(animatable, 2.0)) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.5F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   public void preRender(
      PoseStack poseStack,
      BasiliskEntity animatable,
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
