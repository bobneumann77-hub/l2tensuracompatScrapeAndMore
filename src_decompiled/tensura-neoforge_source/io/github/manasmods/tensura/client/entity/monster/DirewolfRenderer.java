package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.DirewolfEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DirewolfRenderer extends GeoEntityRenderer<DirewolfEntity> {
   public DirewolfRenderer(Context renderManager) {
      super(renderManager, new DirewolfModel());
   }

   protected float getShadowRadius(DirewolfEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      DirewolfEntity wolf,
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
      float scale = wolf.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, wolf, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
