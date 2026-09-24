package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GiantCodEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GiantCodModel extends TensuraEntityGeoModel<GiantCodEntity> {
   public GiantCodModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "giant_cod"), "Head");
   }

   public ResourceLocation getTextureResource(GiantCodEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/giant_cod/giant_cod.png");
   }

   public ResourceLocation getAnimationResource(GiantCodEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/giant_fish.animation.json");
   }

   public RenderType getRenderType(GiantCodEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
