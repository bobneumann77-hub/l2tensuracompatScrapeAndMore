package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StormModel<E extends TensuraProjectile & GeoEntity> extends DefaultedEntityGeoModel<E> {
   private final ResourceLocation location;

   public StormModel(ResourceLocation location) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "storm"), false);
      this.location = location;
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(E instance) {
      return this.location;
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
