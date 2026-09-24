package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.misc.MadOrbsEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MadOrbsRenderer extends GeoEntityRenderer<MadOrbsEntity> {
   public MadOrbsRenderer(Context renderManager) {
      super(renderManager, new MadOrbsModel());
   }

   protected float getShadowRadius(MadOrbsEntity entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(MadOrbsEntity entity, BlockPos blockPos) {
      return 15;
   }

   public void preRender(
      PoseStack poseStack,
      MadOrbsEntity animatable,
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
