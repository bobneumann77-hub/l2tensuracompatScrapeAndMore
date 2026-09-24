package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HellfireModel extends DefaultedEntityGeoModel<Hellfire> {
   public HellfireModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hell_flare"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(Hellfire instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/field/hell_flare/hellfire.png");
   }

   public RenderType getRenderType(Hellfire animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(Hellfire animatable, long instanceId, AnimationState<Hellfire> animationState) {
      GeoBone backfire = this.getAnimationProcessor().getBone("smoke_sphere_2");
      backfire.setHidden(true);
      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
