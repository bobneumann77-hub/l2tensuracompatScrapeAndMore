package io.github.manasmods.tensura.client.entity.field;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.field.HellFlare;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HellFlareRenderer extends GeoEntityRenderer<HellFlare> {
   public HellFlareRenderer(Context renderManager) {
      super(renderManager, new HellFlareModel());
   }

   protected float getShadowRadius(HellFlare entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(HellFlare flare, BlockPos blockPos) {
      return 15;
   }

   public float getVisualSize(HellFlare animatable) {
      return animatable.getType().equals(MiscEntityTypes.HELL_FLARE_LIMITED.get()) ? 0.66F * animatable.getVisualSize() : 0.11F * animatable.getVisualSize();
   }

   public void preRender(
      PoseStack poseStack,
      HellFlare animatable,
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
      float scale = this.getVisualSize(animatable);
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
