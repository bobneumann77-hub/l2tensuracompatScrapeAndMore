package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;

public class SylphideModel<E extends LivingEntity & GeoEntity> extends TensuraEntityGeoModel<E> {
   public SylphideModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "sylphide"), "Head");
   }

   public ResourceLocation getTextureResource(E instance) {
      return instance.hasCustomName() && "Misa".equalsIgnoreCase(instance.getName().getString())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/sylphide/sylphide_goth.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/sylphide/sylphide.png");
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(E sylphide, long instanceId, AnimationState<E> animationState) {
      if (sylphide.onGround() || !animationState.isMoving()) {
         super.setCustomAnimations(sylphide, instanceId, animationState);
      }
   }
}
