package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class ElementalColossusModel extends TensuraEntityGeoModel<ElementalColossusEntity> {
   public ElementalColossusModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "elemental_colossus"), "Head");
   }

   public ResourceLocation getTextureResource(ElementalColossusEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/elemental_colossus/elemental_colossus.png");
   }

   public RenderType getRenderType(ElementalColossusEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(ElementalColossusEntity animatable, long instanceId, AnimationState<ElementalColossusEntity> animationState) {
      if (!animatable.isSleeping()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }
}
