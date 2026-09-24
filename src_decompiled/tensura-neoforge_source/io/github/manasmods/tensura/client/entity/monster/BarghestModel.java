package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.BarghestEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class BarghestModel extends TensuraEntityGeoModel<BarghestEntity> {
   public BarghestModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "barghest"), "head");
   }

   public ResourceLocation getTextureResource(BarghestEntity instance) {
      return instance.isNether()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_nether.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest.png");
   }

   public RenderType getRenderType(BarghestEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(BarghestEntity animatable, long instanceId, AnimationState<BarghestEntity> animationState) {
      GeoBone backfire = this.getAnimationProcessor().getBone("backfire");
      if (animatable.isBaby() != backfire.isHidden()) {
         backfire.setHidden(animatable.isBaby());
      }

      if (!animatable.isSleeping()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("backfire", "tail");
   }
}
