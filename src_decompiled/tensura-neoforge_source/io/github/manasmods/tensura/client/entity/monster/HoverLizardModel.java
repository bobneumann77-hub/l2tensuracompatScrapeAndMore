package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.HoverLizardEntity;
import io.github.manasmods.tensura.entity.variant.HoverLizardVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HoverLizardModel extends DefaultedEntityGeoModel<HoverLizardEntity> {
   public HoverLizardModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hover_lizard"), true);
   }

   public ResourceLocation getTextureResource(HoverLizardEntity instance) {
      return HoverLizardVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   public void setCustomAnimations(HoverLizardEntity animatable, long instanceId, AnimationState<HoverLizardEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      GeoBone headSaddle = this.getAnimationProcessor().getBone("headSaddle");
      if (animatable.isSaddled() == headSaddle.isHidden()) {
         headSaddle.setHidden(!animatable.isSaddled());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("Bag");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }
}
