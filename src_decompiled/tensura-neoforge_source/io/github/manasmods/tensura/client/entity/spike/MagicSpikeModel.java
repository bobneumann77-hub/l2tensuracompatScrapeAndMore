package io.github.manasmods.tensura.client.entity.spike;

import io.github.manasmods.tensura.entity.magic.spike.MagicSpikeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicSpikeModel<T extends MagicSpikeEntity> extends DefaultedEntityGeoModel<T> {
   public MagicSpikeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "spike"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      ResourceLocation texture = instance.getTexture();
      return texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png") : texture;
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      GeoBone spike = this.getAnimationProcessor().getBone("spike");
      if (spike != null) {
         spike.setRotX(animatable.getYaw() * (float) (Math.PI / 180.0));
         spike.setRotY(-animatable.getPitch() * (float) (Math.PI / 180.0));
      }
   }
}
