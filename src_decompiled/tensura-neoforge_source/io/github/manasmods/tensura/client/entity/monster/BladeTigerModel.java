package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.BladeTigerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BladeTigerModel extends DefaultedEntityGeoModel<BladeTigerEntity> {
   public BladeTigerModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "blade_tiger"), true);
   }

   public ResourceLocation getTextureResource(BladeTigerEntity entity) {
      if (this.isOrange(entity)) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/blade_tiger/blade_tiger_orange.png");
      } else {
         return entity.isWhite()
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/blade_tiger/blade_tiger_white.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/blade_tiger/blade_tiger.png");
      }
   }

   public void setCustomAnimations(BladeTigerEntity animatable, long instanceId, AnimationState<BladeTigerEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("Chests");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   private boolean isOrange(BladeTigerEntity entity) {
      if (!entity.hasCustomName()) {
         return false;
      } else if ("Tigger".equalsIgnoreCase(entity.getName().getString())) {
         return true;
      } else {
         return "Vitaly".equalsIgnoreCase(entity.getName().getString()) ? true : "Tigress".equalsIgnoreCase(entity.getName().getString());
      }
   }
}
