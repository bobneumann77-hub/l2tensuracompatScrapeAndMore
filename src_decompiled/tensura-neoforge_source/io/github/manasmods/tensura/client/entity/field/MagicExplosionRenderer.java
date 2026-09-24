package io.github.manasmods.tensura.client.entity.field;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicExplosionRenderer extends GeoEntityRenderer<MagicExplosion> {
   public MagicExplosionRenderer(Context renderManager) {
      super(renderManager, new MagicExplosionModel());
   }

   protected float getShadowRadius(MagicExplosion entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(MagicExplosion flare, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      MagicExplosion animatable,
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
      float scale = animatable.getSize() / 200.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
