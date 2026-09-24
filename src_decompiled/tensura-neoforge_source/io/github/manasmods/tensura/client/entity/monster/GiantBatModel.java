package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GiantBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class GiantBatModel extends TensuraEntityGeoModel<GiantBatEntity> {
   public GiantBatModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "giant_bat"));
   }

   public ResourceLocation getTextureResource(GiantBatEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/giant_bat/giant_bat.png");
   }

   public void setCustomAnimations(GiantBatEntity animatable, long instanceId, AnimationState<GiantBatEntity> animationState) {
      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }
   }
}
