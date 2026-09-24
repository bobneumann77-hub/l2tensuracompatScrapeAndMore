package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class JailSphereModel<E extends TensuraProjectile & GeoEntity> extends DefaultedEntityGeoModel<E> {
   private final ResourceLocation texture;

   public JailSphereModel(ResourceLocation texture) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "jail_sphere"), false);
      this.texture = texture;
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(E instance) {
      return this.texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/air_jail.png") : this.texture;
   }
}
