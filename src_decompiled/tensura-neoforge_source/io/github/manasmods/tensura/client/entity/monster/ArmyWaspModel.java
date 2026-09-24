package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.ArmyWaspEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class ArmyWaspModel extends TensuraEntityGeoModel<ArmyWaspEntity> {
   public ArmyWaspModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "army_wasp"), "Head");
   }

   public ResourceLocation getTextureResource(ArmyWaspEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/insect/army_wasp.png");
   }

   public void setCustomAnimations(ArmyWaspEntity entity, long instanceId, AnimationState<ArmyWaspEntity> animationState) {
      if (!entity.isFlying()) {
         super.setCustomAnimations(entity, instanceId, animationState);
      }
   }
}
