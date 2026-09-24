package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.FeatheredSerpentEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FeatheredSerpentModel extends DefaultedEntityGeoModel<FeatheredSerpentEntity> {
   public FeatheredSerpentModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "feathered_serpent"), true);
   }

   public ResourceLocation getTextureResource(FeatheredSerpentEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/feathered_serpent/feathered_serpent.png");
   }

   public RenderType getRenderType(FeatheredSerpentEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(FeatheredSerpentEntity animatable, long instanceId, AnimationState<FeatheredSerpentEntity> animationState) {
      if (!animatable.isSleeping()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }
}
