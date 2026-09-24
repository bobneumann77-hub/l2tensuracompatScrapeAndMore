package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.BasiliskEntity;
import io.github.manasmods.tensura.entity.variant.BasiliskVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class BasiliskModel extends TensuraEntityGeoModel<BasiliskEntity> {
   public BasiliskModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "basilisk"), "RotatingHead");
   }

   public ResourceLocation getTextureResource(BasiliskEntity instance) {
      return this.isChocobo(instance)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/basilisk/basilisk_chocobo.png")
         : BasiliskVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   public void setCustomAnimations(BasiliskEntity animatable, long instanceId, AnimationState<BasiliskEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   private boolean isChocobo(BasiliskEntity entity) {
      return !entity.hasCustomName() ? false : "Chocobo".equalsIgnoreCase(entity.getName().getString());
   }
}
