package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.CattledeerEntity;
import io.github.manasmods.tensura.entity.variant.CattledeerVariant;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class CattledeerModel extends TensuraEntityGeoModel<CattledeerEntity> {
   public CattledeerModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "cattledeer"), "head");
   }

   public ResourceLocation getTextureResource(CattledeerEntity instance) {
      return this.isCow(instance)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/cattledeer/cowdeer.png")
         : CattledeerVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   public void setCustomAnimations(CattledeerEntity animatable, long instanceId, AnimationState<CattledeerEntity> animationState) {
      GeoBone backfire = this.getAnimationProcessor().getBone("Horns");
      if (animatable.isBaby() != backfire.isHidden()) {
         backfire.setHidden(animatable.isBaby());
      }

      if (!animatable.isSleeping()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   private boolean isCow(CattledeerEntity animatable) {
      return animatable.hasCustomName()
         && ("cow".equalsIgnoreCase(animatable.getName().getString()) || "cowdeer".equalsIgnoreCase(animatable.getName().getString()));
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("Horns");
   }
}
