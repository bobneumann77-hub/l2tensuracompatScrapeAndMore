package io.github.manasmods.tensura.client.layer.template;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HandCoatGeo implements GeoAnimatable {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public double getTick(Object object) {
      return 0.0;
   }
}
