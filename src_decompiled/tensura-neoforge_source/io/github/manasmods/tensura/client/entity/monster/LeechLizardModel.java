package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.LeechLizardEntity;
import io.github.manasmods.tensura.entity.variant.LeechLizardVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LeechLizardModel extends DefaultedEntityGeoModel<LeechLizardEntity> {
   public LeechLizardModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "leech_lizard"), true);
   }

   public ResourceLocation getTextureResource(LeechLizardEntity instance) {
      return this.isBlue(instance)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/leech_lizard/leech_lizard_blue.png")
         : LeechLizardVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   public void setCustomAnimations(LeechLizardEntity animatable, long instanceId, AnimationState<LeechLizardEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   private boolean isBlue(LeechLizardEntity animatable) {
      return animatable.hasCustomName() && "blue".equalsIgnoreCase(animatable.getName().getString());
   }
}
