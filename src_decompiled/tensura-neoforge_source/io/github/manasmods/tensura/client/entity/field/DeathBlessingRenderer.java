package io.github.manasmods.tensura.client.entity.field;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.field.DeathBlessingField;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class DeathBlessingRenderer extends GeoEntityRenderer<DeathBlessingField> {
   public DeathBlessingRenderer(Context renderManager) {
      super(renderManager, new DeathBlessingModel());
   }

   protected float getShadowRadius(DeathBlessingField entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(DeathBlessingField deathBlessing, BlockPos blockPos) {
      return 15;
   }

   public Color getRenderColor(DeathBlessingField animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      return Color.ofARGB(Mth.ceil(color.getAlpha() * animatable.getVisualSize()), color.getRed(), color.getGreen(), color.getBlue());
   }

   public void preRender(
      PoseStack poseStack,
      DeathBlessingField animatable,
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
      float scale = animatable.getSize() / 1.5F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
