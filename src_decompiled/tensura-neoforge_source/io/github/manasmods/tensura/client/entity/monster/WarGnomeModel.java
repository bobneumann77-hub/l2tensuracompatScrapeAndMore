package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;

public class WarGnomeModel<E extends LivingEntity & GeoEntity> extends TensuraEntityGeoModel<E> {
   public WarGnomeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "war_gnome"), "Head");
   }

   public ResourceLocation getTextureResource(E instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/war_gnome/war_gnome.png");
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(E war_gnome, long instanceId, AnimationState<E> animationState) {
      if (war_gnome.onGround() || !animationState.isMoving()) {
         super.setCustomAnimations(war_gnome, instanceId, animationState);
      }
   }
}
