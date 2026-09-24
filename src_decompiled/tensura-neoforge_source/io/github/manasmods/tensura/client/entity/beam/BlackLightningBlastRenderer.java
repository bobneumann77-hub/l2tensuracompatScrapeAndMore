package io.github.manasmods.tensura.client.entity.beam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraRenderTypes;
import io.github.manasmods.tensura.entity.magic.beam.BlackLightningBlastProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackLightningBlastRenderer extends GeoEntityRenderer<BlackLightningBlastProjectile> {
   public BlackLightningBlastRenderer(Context renderManager) {
      super(renderManager, new BlackLightningBlastModel());
   }

   protected float getShadowRadius(BlackLightningBlastProjectile entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(BlackLightningBlastProjectile blaze, BlockPos blockPos) {
      return 15;
   }

   public RenderType getRenderType(
      BlackLightningBlastProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick
   ) {
      return TensuraRenderTypes.getUnlitTranslucent(texture);
   }

   public void preRender(
      PoseStack poseStack,
      BlackLightningBlastProjectile animatable,
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
      float scale = 0.1F * animatable.getSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
