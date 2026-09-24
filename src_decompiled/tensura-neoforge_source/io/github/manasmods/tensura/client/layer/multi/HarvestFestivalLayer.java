package io.github.manasmods.tensura.client.layer.multi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.layer.template.MultiRenderLayer;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
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

public class HarvestFestivalLayer<T extends Entity, M extends EntityModel<T>> extends MultiRenderLayer<T, M> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/harvest_festival.png");

   public HarvestFestivalLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public <E extends Entity & GeoEntity> HarvestFestivalLayer(GeoRenderer<E> entityRenderer) {
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
      if (this.isInHarvestFestival(animatable)) {
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
               Color.ofARGB(175, 255, 255, 255).argbInt()
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
      if (this.isInHarvestFestival(entity)) {
         float f = entity.tickCount + partialTicks;
         poseStack.pushPose();
         poseStack.scale(1.0F, 1.0F, 1.0F);
         this.getParentModel()
            .renderToBuffer(
               poseStack,
               multiBufferSource.getBuffer(this.getRenderType(f)),
               packedLight,
               OverlayTexture.NO_OVERLAY,
               Color.ofARGB(175, 255, 255, 255).argbInt()
            );
         poseStack.popPose();
      }
   }

   private boolean isInHarvestFestival(Entity entity) {
      if (entity instanceof LivingEntity living) {
         IExistence existence = TensuraStorages.getExistenceFrom(living);
         if (existence.getHarvestTick() <= 0 || existence.getHarvestTick() > 1200) {
            return false;
         } else {
            return existence.isDemonLordSeed() && !existence.isTrueDemonLord() && !existence.isTrueHero()
               ? !RaceUtils.shouldNamingStopAwakening(living, existence)
               : false;
         }
      } else {
         return false;
      }
   }
}
