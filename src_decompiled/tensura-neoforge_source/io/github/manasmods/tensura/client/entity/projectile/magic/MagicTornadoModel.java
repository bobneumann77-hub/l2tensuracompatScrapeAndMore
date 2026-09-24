package io.github.manasmods.tensura.client.entity.projectile.magic;

import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicTornadoModel<T extends TensuraFlyingProjectile & GeoEntity> extends DefaultedEntityGeoModel<T> {
   public MagicTornadoModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_tornado"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      ResourceLocation texture = instance.getTexture();
      return texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/misc/wind_tornado.png") : texture;
   }
}
