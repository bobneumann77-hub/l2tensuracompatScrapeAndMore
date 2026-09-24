package io.github.manasmods.tensura.client.layer.multi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.layer.template.MultiRenderLayer;
import io.github.manasmods.tensura.effect.WebbedEffect;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.Color;

public class WebbedLayer<T extends Entity, M extends EntityModel<T>> extends MultiRenderLayer<T, M> {
   private static final ResourceLocation WEBBED_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/block/web.png");

   public WebbedLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public <E extends Entity & GeoEntity> WebbedLayer(GeoRenderer<E> entityRenderer) {
      super(entityRenderer);
   }

   private RenderType getRenderType() {
      return RenderType.entityTranslucent(WEBBED_TEXTURE);
   }

   @Override
   public <E extends Entity & GeoEntity> void renderGeo(
      PoseStack poseStack,
      E animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (this.isWebbed(animatable)) {
         poseStack.pushPose();
         poseStack.scale(1.0F, 1.0F, 1.0F);
         this.getGeoRenderer()
            .reRender(
               bakedModel,
               poseStack,
               bufferSource,
               (GeoAnimatable)animatable,
               this.getRenderType(),
               bufferSource.getBuffer(this.getRenderType()),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               Color.WHITE.argbInt()
            );
         poseStack.popPose();
      }
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int packedLight,
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (this.isWebbed(entity)) {
         poseStack.pushPose();
         poseStack.scale(1.0F, 1.0F, 1.0F);
         this.getParentModel().renderToBuffer(poseStack, multiBufferSource.getBuffer(this.getRenderType()), packedLight, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
      }
   }

   private boolean isWebbed(Entity entity) {
      return entity instanceof LivingEntity living ? WebbedEffect.isFullyWebbed(living) : false;
   }
}
