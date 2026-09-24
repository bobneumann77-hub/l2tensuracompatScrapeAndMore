package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.ArmorsaurusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class ArmorsaurusModel extends TensuraEntityGeoModel<ArmorsaurusEntity> {
   public ArmorsaurusModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "armorsaurus"), "Head");
   }

   public ResourceLocation getTextureResource(ArmorsaurusEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/armorsaurus/armorsaurus.png");
   }

   public void setCustomAnimations(ArmorsaurusEntity animatable, long instanceId, AnimationState<ArmorsaurusEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      boolean chested = animatable.isChested();
      GeoBone chest = this.getAnimationProcessor().getBone("Chests");
      if (chested == chest.isHidden()) {
         chest.setHidden(!chested);
      }

      GeoBone chestSpike = this.getAnimationProcessor().getBone("ChestSpike");
      if (chested != chestSpike.isHidden()) {
         chestSpike.setHidden(chested);
      }

      if (!animatable.isSleeping() && animatable.isAlive() && !animatable.isInSittingPose()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }
}
