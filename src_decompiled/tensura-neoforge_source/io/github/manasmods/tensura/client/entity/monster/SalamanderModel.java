package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.SalamanderEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SalamanderModel extends TensuraEntityGeoModel<SalamanderEntity> {
   public SalamanderModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "salamander"), "Head");
   }

   public ResourceLocation getTextureResource(SalamanderEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/salamander/salamander.png");
   }

   public RenderType getRenderType(SalamanderEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
