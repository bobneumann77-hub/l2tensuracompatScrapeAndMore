package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.HoundDogEntity;
import io.github.manasmods.tensura.entity.variant.HoundDogVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HoundDogModel extends DefaultedEntityGeoModel<HoundDogEntity> {
   public HoundDogModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hound_dog"), true);
   }

   public ResourceLocation getTextureResource(HoundDogEntity instance) {
      return HoundDogVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   public void setCustomAnimations(HoundDogEntity animatable, long instanceId, AnimationState<HoundDogEntity> animationState) {
      if (!animatable.isSleeping() && animatable.isAlive()) {
         GeoBone head = this.getAnimationProcessor().getBone("Head");
         if (head != null) {
            EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * (float) (Math.PI / 180.0));
            head.setRotY(entityData.netHeadYaw() * (float) (Math.PI / 180.0));
         }
      }
   }
}
