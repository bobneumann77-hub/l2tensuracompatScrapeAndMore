package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.UnicornEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class UnicornModel extends DefaultedEntityGeoModel<UnicornEntity> {
   public UnicornModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horse"), false);
   }

   public ResourceLocation getModelResource(UnicornEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/unicorn.geo.json");
   }

   public ResourceLocation getTextureResource(UnicornEntity instance) {
      if (this.isUnicorn(instance)) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horse/rainicorn.png");
      } else {
         return instance.isBlack()
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horse/unicorn_black.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horse/unicorn.png");
      }
   }

   public void setCustomAnimations(UnicornEntity animatable, long instanceId, AnimationState<UnicornEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("chest");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   private boolean isUnicorn(UnicornEntity entity) {
      return !entity.hasCustomName() ? false : "Rainicorn".equalsIgnoreCase(entity.getName().getString());
   }
}
