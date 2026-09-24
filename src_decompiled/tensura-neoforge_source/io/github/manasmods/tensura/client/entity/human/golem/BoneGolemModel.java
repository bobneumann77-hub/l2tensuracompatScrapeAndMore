package io.github.manasmods.tensura.client.entity.human.golem;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.human.golem.BoneGolemEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class BoneGolemModel<E extends BoneGolemEntity> extends TensuraEntityGeoModel<E> {
   public BoneGolemModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "bone_golem"));
   }

   public ResourceLocation getTextureResource(E instance) {
      return instance.getVariant().getTextureLocation();
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
