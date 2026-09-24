package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.entity.magic.field.HellFlare;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HellFlareModel extends DefaultedEntityGeoModel<HellFlare> {
   public HellFlareModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hell_flare"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(HellFlare instance) {
      return instance.getType().equals(MiscEntityTypes.HELL_FLARE_LIMITED.get())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/field/hell_flare/hell_flare_limited.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/field/hell_flare/hell_flare.png");
   }

   public ResourceLocation getModelResource(HellFlare instance) {
      return instance.getType().equals(MiscEntityTypes.HELL_FLARE_LIMITED.get())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/misc/hell_flare_limited.geo.json")
         : super.getModelResource(instance);
   }

   public ResourceLocation getAnimationResource(HellFlare instance) {
      return instance.getType().equals(MiscEntityTypes.HELL_FLARE_LIMITED.get())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/misc/hell_flare_limited.animation.json")
         : super.getAnimationResource(instance);
   }

   public RenderType getRenderType(HellFlare animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
