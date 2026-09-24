package io.github.manasmods.tensura.client.entity.layer;

import io.github.manasmods.tensura.entity.monster.PegasusEntity;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PegasusArmorModel extends DefaultedEntityGeoModel<PegasusEntity> {
   public PegasusArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horse"), false);
   }

   public ResourceLocation getModelResource(PegasusEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "geo/layer/pegasus_armor.geo.json");
   }

   public void setCustomAnimations(PegasusEntity animatable, long instanceId, AnimationState<PegasusEntity> animationState) {
      GeoBone horn = this.getAnimationProcessor().getBone("Horn");
      if (horn != null && animatable.getType().equals(MonsterEntityTypes.PEGACORN.get()) == horn.isHidden()) {
         horn.setHidden(!animatable.getType().equals(MonsterEntityTypes.PEGACORN.get()));
      }

      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
