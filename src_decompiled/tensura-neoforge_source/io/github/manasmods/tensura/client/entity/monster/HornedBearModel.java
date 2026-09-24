package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.HornedBearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HornedBearModel extends DefaultedEntityGeoModel<HornedBearEntity> {
   public HornedBearModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horned_bear"), true);
   }

   public ResourceLocation getTextureResource(HornedBearEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/bear/horned_bear.png");
   }

   public void setCustomAnimations(HornedBearEntity bear, long instanceId, AnimationState<HornedBearEntity> animationState) {
      GeoBone horn = this.getAnimationProcessor().getBone("horn");
      if (bear.isBaby() != horn.isHidden()) {
         horn.setHidden(bear.isBaby());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (bear.isChested() == chest.isHidden()) {
         chest.setHidden(!bear.isChested());
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (bear.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!bear.isSaddled());
      }

      if (!bear.isSleeping() && !bear.isInSittingPose() && bear.isAlive()) {
         super.setCustomAnimations(bear, instanceId, animationState);
      }
   }
}
