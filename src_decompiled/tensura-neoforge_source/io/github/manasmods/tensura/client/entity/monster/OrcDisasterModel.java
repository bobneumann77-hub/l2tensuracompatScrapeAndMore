package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.OrcDisasterEntity;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class OrcDisasterModel extends TensuraEntityGeoModel<OrcEntity> {
   public OrcDisasterModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "orc_disaster"), "Head");
   }

   public ResourceLocation getTextureResource(OrcEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_disaster.png");
   }

   public RenderType getRenderType(OrcEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public void setCustomAnimations(OrcEntity animatable, long instanceId, AnimationState<OrcEntity> animationState) {
      if (!animatable.isSleeping()) {
         if (!animationState.isCurrentAnimation(OrcDisasterEntity.EAT)) {
            if (!animationState.isCurrentAnimation(OrcDisasterEntity.CRUSH)) {
               if (!animationState.isCurrentAnimation(OrcDisasterEntity.CRY)) {
                  super.setCustomAnimations(animatable, instanceId, animationState);
               }
            }
         }
      }
   }
}
