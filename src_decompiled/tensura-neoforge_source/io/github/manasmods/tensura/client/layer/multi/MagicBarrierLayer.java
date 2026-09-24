package io.github.manasmods.tensura.client.layer.multi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.layer.template.MultiRenderLayer;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
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

public class MagicBarrierLayer<T extends Entity, M extends EntityModel<T>> extends MultiRenderLayer<T, M> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/full_color.png");

   public MagicBarrierLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public <E extends Entity & GeoEntity> MagicBarrierLayer(GeoRenderer<E> entityRenderer) {
      super(entityRenderer);
   }

   private RenderType getRenderType(float f) {
      return RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
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
      if (animatable instanceof LivingEntity entity) {
         Color color = this.getColor(entity);
         if (color != null) {
            float f = animatable.tickCount + partialTick;
            poseStack.pushPose();
            poseStack.scale(1.0F, 1.0F, 1.0F);
            this.getGeoRenderer()
               .reRender(
                  bakedModel,
                  poseStack,
                  bufferSource,
                  (GeoAnimatable)animatable,
                  this.getRenderType(f),
                  bufferSource.getBuffer(this.getRenderType(f)),
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color.argbInt()
               );
            poseStack.popPose();
         }
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
      if (entity instanceof LivingEntity living) {
         Color color = this.getColor(living);
         if (color != null) {
            poseStack.pushPose();
            poseStack.scale(1.0F, 1.0F, 1.0F);
            this.getParentModel()
               .renderToBuffer(
                  poseStack,
                  multiBufferSource.getBuffer(this.getRenderType(entity.tickCount + partialTicks)),
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color.argbInt()
               );
            poseStack.popPose();
         }
      }
   }

   private Color getColor(LivingEntity entity) {
      if (entity.getAttributeValue(TensuraAttributes.MAGIC_BARRIER) > 0.0) {
         return entity.getAttributeValue(TensuraAttributes.PHYSICAL_BARRIER) > 0.0 ? Color.ofARGB(50, 68, 126, 77) : Color.ofARGB(50, 94, 126, 43);
      } else {
         return entity.getAttributeValue(TensuraAttributes.PHYSICAL_BARRIER) > 0.0 ? Color.ofARGB(50, 43, 126, 112) : null;
      }
   }
}
