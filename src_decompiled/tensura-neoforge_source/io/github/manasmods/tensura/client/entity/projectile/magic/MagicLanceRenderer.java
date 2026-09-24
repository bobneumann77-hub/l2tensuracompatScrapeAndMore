package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class MagicLanceRenderer<T extends TensuraFlyingProjectile & GeoEntity> extends GeoEntityRenderer<T> {
   public MagicLanceRenderer(Context renderManager) {
      super(renderManager, new MagicLanceModel());
   }

   protected float getShadowRadius(T entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(T entity, BlockPos blockPos) {
      return 10;
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isInFirstViewDistance(animatable, 3.0F + animatable.getSize())) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.1F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
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
      float scale = animatable.getVisualSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
