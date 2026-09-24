package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlyingOrbRenderer<T extends TensuraFlyingProjectile & GeoEntity> extends GeoEntityRenderer<T> {
   public FlyingOrbRenderer(Context renderManager) {
      super(renderManager, new FlyingOrbModel());
   }

   protected float getShadowRadius(T entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(T entity, BlockPos blockPos) {
      return 15;
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
