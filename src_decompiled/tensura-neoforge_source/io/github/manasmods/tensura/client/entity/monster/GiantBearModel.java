package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GiantBearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class GiantBearModel extends TensuraEntityGeoModel<GiantBearEntity> {
   public GiantBearModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "giant_bear"), "Head");
   }

   public ResourceLocation getTextureResource(GiantBearEntity instance) {
      return this.isPooh(instance)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/bear/giant_pooh.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/bear/giant_bear.png");
   }

   public void setCustomAnimations(GiantBearEntity bear, long instanceId, AnimationState<GiantBearEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Head Fur");
      if (!bear.isBaby() == saddle.isHidden()) {
         saddle.setHidden(bear.isBaby());
      }

      if (!bear.isAngry() && !bear.isSprinting() && bear.isAlive()) {
         super.setCustomAnimations(bear, instanceId, animationState);
      }
   }

   private boolean isPooh(GiantBearEntity entity) {
      if (!entity.hasCustomName()) {
         return false;
      } else if ("Winnie".equalsIgnoreCase(entity.getName().getString())) {
         return true;
      } else {
         return "Pooh".equalsIgnoreCase(entity.getName().getString()) ? true : "Winnie the Pooh".equalsIgnoreCase(entity.getName().getString());
      }
   }
}
