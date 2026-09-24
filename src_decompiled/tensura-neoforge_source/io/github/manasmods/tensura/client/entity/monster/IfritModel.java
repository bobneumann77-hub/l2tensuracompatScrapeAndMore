package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;

public class IfritModel<E extends LivingEntity & GeoEntity> extends TensuraEntityGeoModel<E> {
   public IfritModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "ifrit"), "Head");
   }

   public ResourceLocation getTextureResource(E instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/ifrit/ifrit.png");
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(E ifrit, long instanceId, AnimationState<E> animationState) {
      if (ifrit.onGround() || !animationState.isMoving()) {
         super.setCustomAnimations(ifrit, instanceId, animationState);
      }
   }
}
