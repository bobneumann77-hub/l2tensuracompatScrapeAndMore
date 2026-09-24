package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.entity.magic.barrier.FlareCircleEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FlareCircleModel extends DefaultedEntityGeoModel<FlareCircleEntity> {
   public FlareCircleModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "flare_circle"), false);
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/flare_circle.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(FlareCircleEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(FlareCircleEntity animatable, long instanceId, AnimationState<FlareCircleEntity> animationState) {
      GeoBone flare = this.getAnimationProcessor().getBone("flare_root");
      boolean battlewill = animatable.getSkill() == null || !(animatable.getSkill().getSkill() instanceof Battlewill);
      if (battlewill == flare.isHidden()) {
         flare.setHidden(!battlewill);
      }

      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
