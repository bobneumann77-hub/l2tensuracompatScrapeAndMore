package io.github.manasmods.tensura.client.entity.barrier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BarrierCubeRenderer<E extends TensuraProjectile & GeoEntity> extends GeoEntityRenderer<E> {
   public BarrierCubeRenderer(Context renderManager, ResourceLocation texture, ResourceLocation[] startTexture, int startSpeed) {
      super(renderManager, new BarrierCubeModel(texture, startTexture, startSpeed));
   }

   public BarrierCubeRenderer(Context renderManager, ResourceLocation texture, ResourceLocation[] startTexture) {
      super(renderManager, new BarrierCubeModel(texture, startTexture));
   }

   public BarrierCubeRenderer(Context renderManager, String texture, ResourceLocation[] startTexture, int startSpeed) {
      super(renderManager, new BarrierCubeModel(ResourceLocation.fromNamespaceAndPath("tensura", texture), startTexture, startSpeed));
   }

   public BarrierCubeRenderer(Context renderManager, String texture, ResourceLocation[] startTexture) {
      super(renderManager, new BarrierCubeModel(ResourceLocation.fromNamespaceAndPath("tensura", texture), startTexture));
   }

   public BarrierCubeRenderer(Context renderManager, ResourceLocation texture, int startSpeed) {
      super(renderManager, new BarrierCubeModel(texture, null, startSpeed));
   }

   public BarrierCubeRenderer(Context renderManager, ResourceLocation texture) {
      super(renderManager, new BarrierCubeModel(texture, null));
   }

   protected float getShadowRadius(E entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(E blaze, BlockPos blockPos) {
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
      float scale = animatable.getVisualSize() / 1.5F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
