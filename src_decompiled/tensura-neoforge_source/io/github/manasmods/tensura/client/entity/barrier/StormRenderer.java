package io.github.manasmods.tensura.client.entity.barrier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class StormRenderer<E extends TensuraProjectile & GeoEntity> extends GeoEntityRenderer<E> {
   public StormRenderer(Context renderManager, ResourceLocation location) {
      super(renderManager, new StormModel(location));
   }

   protected float getShadowRadius(E entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(E fire, BlockPos blockPos) {
      return 15;
   }

   public Color getRenderColor(E animatable, float partialTick, int packedLight) {
      float opacity = Math.min(animatable.getVisualSize() / animatable.getSize(), 1.0F);
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      return opacity >= 1.0F ? color : Color.ofARGB(Mth.ceil(color.getAlpha() * opacity), color.getRed(), color.getGreen(), color.getBlue());
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
      float scale = 0.25F * animatable.getSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
