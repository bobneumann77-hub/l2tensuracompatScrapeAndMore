package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.entity.monster.OrcLordEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class OrcLordModel extends TensuraEntityGeoModel<OrcEntity> {
   public OrcLordModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "orc_lord"), "RotatingHead");
   }

   public ResourceLocation getTextureResource(OrcEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_lord.png");
   }

   public RenderType getRenderType(OrcEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public void setCustomAnimations(OrcEntity animatable, long instanceId, AnimationState<OrcEntity> animationState) {
      if (!animatable.isSleeping()) {
         if (!animationState.isCurrentAnimation(OrcLordEntity.CRUSH)) {
            if (!animationState.isCurrentAnimation(OrcLordEntity.EAT)) {
               if (!animationState.isCurrentAnimation(OrcLordEntity.EAT_ITEM)) {
                  super.setCustomAnimations(animatable, instanceId, animationState);
               }
            }
         }
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("Hair");
   }
}
