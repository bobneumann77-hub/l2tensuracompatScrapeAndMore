package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;

public class AkashModel<E extends LivingEntity & GeoEntity> extends TensuraEntityGeoModel<E> {
   public AkashModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "akash"));
   }

   public ResourceLocation getTextureResource(E instance) {
      return this.isSanta(instance)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/akash/santa.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/akash/akash.png");
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(E akash, long instanceId, AnimationState<E> animationState) {
      if (akash.onGround() || !animationState.isMoving()) {
         super.setCustomAnimations(akash, instanceId, animationState);
      }
   }

   private boolean isSanta(E instance) {
      if (ClientHelper.XMAS) {
         return true;
      } else if (!instance.hasCustomName()) {
         return false;
      } else {
         return "Santa Claus".equalsIgnoreCase(instance.getName().getString()) ? true : "Santa".equalsIgnoreCase(instance.getName().getString());
      }
   }
}
