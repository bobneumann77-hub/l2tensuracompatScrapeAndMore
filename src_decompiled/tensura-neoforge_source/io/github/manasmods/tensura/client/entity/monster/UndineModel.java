package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;

public class UndineModel<E extends LivingEntity & GeoEntity> extends TensuraEntityGeoModel<E> {
   public UndineModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "undine"), "Head");
   }

   public ResourceLocation getTextureResource(E instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/undine/undine.png");
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(E undine, long instanceId, AnimationState<E> animationState) {
      if (undine.onGround() || !animationState.isMoving()) {
         super.setCustomAnimations(undine, instanceId, animationState);
      }
   }
}
