package io.github.manasmods.tensura.client.entity.beam;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicBeamModel<T extends TensuraProjectile & GeoEntity> extends DefaultedEntityGeoModel<T> {
   private final ResourceLocation texture;

   public MagicBeamModel(ResourceLocation texture) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_beam"), false);
      this.texture = texture;
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/beam/summon_daemon.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      return this.texture;
   }
}
