package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.UnicornEntity;
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

public class UnicornArmorLayer extends GeoRenderLayer<UnicornEntity> {
   protected final GeoModel<UnicornEntity> model = new UnicornArmorModel();

   public UnicornArmorLayer(GeoRenderer<UnicornEntity> renderer) {
      super(renderer);
   }

   public GeoModel<UnicornEntity> getGeoModel() {
      return this.model;
   }

   public void render(
      PoseStack poseStack,
      UnicornEntity unicorn,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      ItemStack itemStack = unicorn.getBodyArmorItem();
      if (itemStack.getItem() instanceof AnimalArmorItem armorItem && armorItem.getBodyType() == BodyType.EQUESTRIAN) {
         int color = -1;
         if (itemStack.is(ItemTags.DYEABLE)) {
            color = ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536));
         }

         poseStack.pushPose();
         poseStack.scale(1.002F, 1.002F, 1.002F);
         this.applyModelAnimation(unicorn, partialTick);
         BakedGeoModel bakedGeoModel = this.getDefaultBakedModel(unicorn);
         RenderType type = RenderType.entityTranslucent(armorItem.getTexture());
         this.getRenderer()
            .reRender(
               bakedGeoModel, poseStack, bufferSource, unicorn, type, bufferSource.getBuffer(type), partialTick, packedLight, OverlayTexture.NO_OVERLAY, color
            );
         poseStack.popPose();
      }
   }

   public void applyModelAnimation(UnicornEntity unicorn, float partialTick) {
      boolean shouldSit = unicorn.isPassenger() && unicorn.getVehicle() != null;
      float lerpBodyRot = Mth.rotLerp(partialTick, unicorn.yBodyRotO, unicorn.yBodyRot);
      float lerpHeadRot = Mth.rotLerp(partialTick, unicorn.yHeadRotO, unicorn.yHeadRot);
      float netHeadYaw = lerpHeadRot - lerpBodyRot;
      if (shouldSit && unicorn.getVehicle() instanceof LivingEntity livingentity) {
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
      if (!shouldSit && unicorn.isAlive()) {
         limbSwingAmount = unicorn.walkAnimation.speed(partialTick);
         limbSwing = unicorn.walkAnimation.position(partialTick);
         if (unicorn.isBaby()) {
            limbSwing *= 3.0F;
         }

         if (limbSwingAmount > 1.0F) {
            limbSwingAmount = 1.0F;
         }
      }

      float headPitch = Mth.lerp(partialTick, unicorn.xRotO, unicorn.getXRot());
      float motionThreshold = this.getRenderer().getMotionAnimThreshold(unicorn);
      Vec3 velocity = unicorn.getDeltaMovement();
      float avgVelocity = (float)((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2.0);
      AnimationState<UnicornEntity> animationState = new AnimationState(
         unicorn, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0.0F
      );
      long instanceId = unicorn.getId() + unicorn.getRandom().nextInt(1000);
      GeoModel<UnicornEntity> currentModel = this.getGeoModel();
      animationState.setData(DataTickets.TICK, unicorn.getTick(unicorn));
      animationState.setData(DataTickets.ENTITY, unicorn);
      animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, unicorn.isBaby(), -netHeadYaw, -headPitch));
      currentModel.addAdditionalStateData(unicorn, instanceId, animationState::setData);
      currentModel.handleAnimations(unicorn, instanceId, animationState, partialTick);
   }
}
