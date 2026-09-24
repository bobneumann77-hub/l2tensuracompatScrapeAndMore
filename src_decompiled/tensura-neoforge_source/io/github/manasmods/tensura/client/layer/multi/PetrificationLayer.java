package io.github.manasmods.tensura.client.layer.multi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.layer.template.MultiRenderLayer;
import io.github.manasmods.tensura.effect.PetrificationEffect;
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

public class PetrificationLayer<T extends Entity, M extends EntityModel<T>> extends MultiRenderLayer<T, M> {
   private static final ResourceLocation STONE_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/block/stone.png");

   public PetrificationLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public <E extends Entity & GeoEntity> PetrificationLayer(GeoRenderer<E> entityRenderer) {
      super(entityRenderer);
   }

   private RenderType getRenderType() {
      return RenderType.entityTranslucent(STONE_TEXTURE);
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
         int petrification = PetrificationEffect.getPetrificationLevel(entity);
         if (petrification > 0) {
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
                  Color.ofARGB(Math.min(255, 85 * petrification), 255, 255, 255).argbInt()
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
         int petrification = PetrificationEffect.getPetrificationLevel(living);
         if (petrification > 0) {
            poseStack.pushPose();
            poseStack.scale(1.0F, 1.0F, 1.0F);
            this.getParentModel()
               .renderToBuffer(
                  poseStack,
                  multiBufferSource.getBuffer(this.getRenderType()),
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  Color.ofARGB(Math.min(255, 85 * petrification), 255, 255, 255).argbInt()
               );
            poseStack.popPose();
         }
      }
   }
}
