package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.WingedCatEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class WingedCatModel extends TensuraEntityGeoModel<WingedCatEntity> {
   public WingedCatModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "winged_cat"), "RotatingHead");
   }

   public ResourceLocation getTextureResource(WingedCatEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/winged_cat/winged_cat.png");
   }

   public RenderType getRenderType(WingedCatEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(WingedCatEntity animatable, long instanceId, AnimationState<WingedCatEntity> animationState) {
      if (!animatable.isSleeping()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }
}
