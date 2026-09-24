package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicHandsModel<E extends TensuraProjectile & GeoEntity> extends DefaultedEntityGeoModel<E> {
   private final ResourceLocation texture;

   public MagicHandsModel(ResourceLocation texture) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "shadow_bind"), false);
      this.texture = texture;
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(E instance) {
      return this.texture;
   }
}
