package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.HornedRabbitEntity;
import io.github.manasmods.tensura.entity.variant.HornedRabbitVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class HornedRabbitModel extends TensuraEntityGeoModel<HornedRabbitEntity> {
   public HornedRabbitModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horned_rabbit"), "Head");
   }

   public ResourceLocation getTextureResource(HornedRabbitEntity instance) {
      return HornedRabbitVariant.getLocation(instance.getVariant());
   }

   public void setCustomAnimations(HornedRabbitEntity rabbit, long instanceId, AnimationState<HornedRabbitEntity> animationState) {
      GeoBone horn = this.getAnimationProcessor().getBone("Horn");
      if (rabbit.isBaby() != horn.isHidden()) {
         horn.setHidden(rabbit.isBaby());
      }

      if (!rabbit.isSleeping() && !rabbit.isInSittingPose() && rabbit.isAlive()) {
         super.setCustomAnimations(rabbit, instanceId, animationState);
      }
   }
}
