package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.misc.HazyBlossomEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HazyBlossomModel extends DefaultedEntityGeoModel<HazyBlossomEntity> {
   public HazyBlossomModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hazy_blossom"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getModelResource(HazyBlossomEntity instance) {
      return instance.getPetals() >= 8
         ? ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/misc/hazy_double_blossom.geo.json")
         : super.getModelResource(instance);
   }

   public ResourceLocation getAnimationResource(HazyBlossomEntity instance) {
      return instance.getPetals() >= 8
         ? ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/misc/hazy_double_blossom.animation.json")
         : super.getAnimationResource(instance);
   }

   public RenderType getRenderType(HazyBlossomEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(HazyBlossomEntity animatable, long instanceId, AnimationState<HazyBlossomEntity> animationState) {
      int max = animatable.getPetals() >= 8 ? 8 : 5;

      for (int i = 1; i <= max; i++) {
         GeoBone orb = this.getAnimationProcessor().getBone("petal_" + i);
         if (orb.isHidden() == animatable.getPetals() >= i) {
            orb.setHidden(animatable.getPetals() < i);
         }
      }

      GeoBone all = this.getAnimationProcessor().getBone("All");
      if (all != null) {
         if (all.isHidden() != animatable.tickCount < 10) {
            all.setHidden(animatable.tickCount < 10);
         }

         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         all.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
