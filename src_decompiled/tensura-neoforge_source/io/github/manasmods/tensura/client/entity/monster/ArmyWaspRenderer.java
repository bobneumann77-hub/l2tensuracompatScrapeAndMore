package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.ArmyWaspEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArmyWaspRenderer extends GeoEntityRenderer<ArmyWaspEntity> {
   public ArmyWaspRenderer(Context renderManager) {
      super(renderManager, new ArmyWaspModel());
   }

   protected float getShadowRadius(ArmyWaspEntity entity) {
      return entity.isBaby() ? 0.35F : 0.75F;
   }

   protected float getDeathMaxRotation(ArmyWaspEntity animatable) {
      return 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      ArmyWaspEntity wasp,
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
      float scale = wasp.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, wasp, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
