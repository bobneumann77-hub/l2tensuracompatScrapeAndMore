package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BlackLightningModel extends DefaultedEntityGeoModel<BlackLightningBolt> {
   public BlackLightningModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "black_lightning"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(BlackLightningBolt animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(BlackLightningBolt animatable, long instanceId, AnimationState<BlackLightningBolt> animationState) {
      GeoBone backfire = this.getAnimationProcessor().getBone("after_shock_root");
      boolean shouldHide = animatable.getAdditionalVisual() < 5;
      if (backfire.isHidden() != shouldHide) {
         backfire.setHidden(shouldHide);
      }

      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
