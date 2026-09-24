package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.SpearToroEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SpearToroModel extends DefaultedEntityGeoModel<SpearToroEntity> {
   public SpearToroModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "spear_toro"), true);
   }

   public ResourceLocation getTextureResource(SpearToroEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/spear_toro/spear_toro.png");
   }

   public RenderType getRenderType(SpearToroEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(SpearToroEntity fish, long instanceId, AnimationState<SpearToroEntity> animationState) {
      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (fish.isChested() == chest.isHidden()) {
         chest.setHidden(!fish.isChested());
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (fish.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!fish.isSaddled());
      }

      if (fish.isSaddled()) {
         GeoBone saddleHandle = this.getAnimationProcessor().getBone("SaddleStrings");
         if (fish.isVehicle() == saddleHandle.isHidden()) {
            saddleHandle.setHidden(!fish.isVehicle());
         }
      }
   }
}
