package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.PegasusEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PegasusArmorLayer extends GeoRenderLayer<PegasusEntity> {
   protected final GeoModel<PegasusEntity> model = new PegasusArmorModel();

   public PegasusArmorLayer(GeoRenderer<PegasusEntity> renderer) {
      super(renderer);
   }

   public GeoModel<PegasusEntity> getGeoModel() {
      return this.model;
   }

   public void render(
      PoseStack poseStack,
      PegasusEntity pegasus,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      ItemStack itemStack = pegasus.getBodyArmorItem();
      if (itemStack.getItem() instanceof AnimalArmorItem armorItem && armorItem.getBodyType() == BodyType.EQUESTRIAN) {
         int color = -1;
         if (itemStack.is(ItemTags.DYEABLE)) {
            color = ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536));
         }

         poseStack.pushPose();
         poseStack.scale(1.0015F, 1.0015F, 1.0015F);
         this.applyModelAnimation(pegasus, partialTick);
         BakedGeoModel bakedGeoModel = this.getDefaultBakedModel(pegasus);
         RenderType type = RenderType.entityTranslucent(armorItem.getTexture());
         this.getRenderer()
            .reRender(
               bakedGeoModel, poseStack, bufferSource, pegasus, type, bufferSource.getBuffer(type), partialTick, packedLight, OverlayTexture.NO_OVERLAY, color
            );
         poseStack.popPose();
      }
   }

   public void applyModelAnimation(PegasusEntity pegasus, float partialTick) {
      boolean shouldSit = pegasus.isPassenger() && pegasus.getVehicle() != null;
      float lerpBodyRot = Mth.rotLerp(partialTick, pegasus.yBodyRotO, pegasus.yBodyRot);
      float lerpHeadRot = Mth.rotLerp(partialTick, pegasus.yHeadRotO, pegasus.yHeadRot);
      float netHeadYaw = lerpHeadRot - lerpBodyRot;
      if (shouldSit && pegasus.getVehicle() instanceof LivingEntity livingentity) {
         lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
         netHeadYaw = lerpHeadRot - lerpBodyRot;
         float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85.0F, 85.0F);
         lerpBodyRot = lerpHeadRot - clampedHeadYaw;
         if (clampedHeadYaw * clampedHeadYaw > 2500.0F) {
            lerpBodyRot += clampedHeadYaw * 0.2F;
         }

         netHeadYaw = lerpHeadRot - lerpBodyRot;
      }

      float limbSwingAmount = 0.0F;
      float limbSwing = 0.0F;
      if (!shouldSit && pegasus.isAlive()) {
         limbSwingAmount = pegasus.walkAnimation.speed(partialTick);
         limbSwing = pegasus.walkAnimation.position(partialTick);
         if (pegasus.isBaby()) {
            limbSwing *= 3.0F;
         }

         if (limbSwingAmount > 1.0F) {
            limbSwingAmount = 1.0F;
         }
      }

      float headPitch = Mth.lerp(partialTick, pegasus.xRotO, pegasus.getXRot());
      float motionThreshold = this.getRenderer().getMotionAnimThreshold(pegasus);
      Vec3 velocity = pegasus.getDeltaMovement();
      float avgVelocity = (float)((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2.0);
      AnimationState<PegasusEntity> animationState = new AnimationState(
         pegasus, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0.0F
      );
      long instanceId = pegasus.getId() + pegasus.getRandom().nextInt(1000);
      GeoModel<PegasusEntity> currentModel = this.getGeoModel();
      animationState.setData(DataTickets.TICK, pegasus.getTick(pegasus));
      animationState.setData(DataTickets.ENTITY, pegasus);
      animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, pegasus.isBaby(), -netHeadYaw, -headPitch));
      currentModel.addAdditionalStateData(pegasus, instanceId, animationState::setData);
      currentModel.handleAnimations(pegasus, instanceId, animationState, partialTick);
   }
}
