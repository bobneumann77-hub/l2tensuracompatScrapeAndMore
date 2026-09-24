package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicShieldRenderer<E extends TensuraProjectile & GeoEntity> extends GeoEntityRenderer<E> {
   public MagicShieldRenderer(Context renderManager, ResourceLocation texture) {
      super(renderManager, new MagicShieldModel(texture));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/shield/aura_shield.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/shield/magic_shield.png"));
   }

   protected float getShadowRadius(E entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(E entity, BlockPos blockPos) {
      return 15;
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
      float size = animatable.getSize() / 3.0F;
      this.scaleModelForRender(
         this.scaleWidth * size, this.scaleHeight * size, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }

   protected void applyRotations(E animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
      switch (animatable.getNearestViewDirection()) {
         case UP:
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0F - rotationYaw));
            break;
         case DOWN:
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F - rotationYaw));
            break;
         case EAST:
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - rotationYaw));
            break;
         case WEST:
            poseStack.mulPose(Axis.YN.rotationDegrees(90.0F - rotationYaw));
            break;
         case NORTH:
            poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - rotationYaw));
      }
   }
}
