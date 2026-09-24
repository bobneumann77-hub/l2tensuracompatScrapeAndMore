package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GiantSalmonEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GiantSalmonModel extends TensuraEntityGeoModel<GiantSalmonEntity> {
   public GiantSalmonModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "giant_salmon"), "Head");
   }

   public ResourceLocation getTextureResource(GiantSalmonEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/giant_salmon/giant_salmon.png");
   }

   public ResourceLocation getAnimationResource(GiantSalmonEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/giant_fish.animation.json");
   }

   public RenderType getRenderType(GiantSalmonEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
